/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com

    This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool
    
    ++Spicy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    ++Spicy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.joingraph.ConnectedVariables;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NormalizeSTTGDs {

    private static Log logger = LogFactory.getLog(NormalizeSTTGDs.class);

    public List<FORule> normalizeSTTGDs(List<FORule> tgds) {
        Map<FORule, List<FORule>> normalization = findNomalizationOfSTTGDs(tgds);
        List<FORule> result = new ArrayList<FORule>();
        for (FORule tgd : tgds) {
            result.addAll(normalization.get(tgd));
        }
        return result;
    }

    public Map<FORule, List<FORule>> findNomalizationOfSTTGDs(List<FORule> tgds) {
        if (logger.isDebugEnabled()) logger.debug("Normalizing candidate tgds: " + tgds);
        Map<FORule, List<FORule>> normalization = new HashMap<FORule, List<FORule>>();
        for (FORule tgd : tgds) {
            normalization.put(tgd, normalizeTGD(tgd));
        }
        if (logger.isDebugEnabled()) logger.debug("Final result: " + normalization);
        return normalization;
    }

    public List<FORule> normalizeTGD(FORule tgd) {
        List<FORule> normalizedTgds = new ArrayList<FORule>();
        if (logger.isDebugEnabled()) logger.debug("Analyzing tgd: " + tgd);
        if (tgd.getTargetView().getVariables().size() == 1) {
            if (logger.isDebugEnabled()) logger.debug("Tgd has single target variable, adding...");
            normalizedTgds.add(tgd);
        } else {
            List<ConnectedVariables> connectedGroups = findConnectedVariables(tgd);
            if (logger.isDebugEnabled()) logger.debug("Connected variables: " + SpicyEngineUtility.printCollection(connectedGroups));
            if (connectedGroups.size() == 1) {
                if (logger.isDebugEnabled()) logger.debug("Target is connected, adding tgd to result...");
                normalizedTgds.add(tgd);
            } else {
                for (ConnectedVariables connectedVariables : connectedGroups) {
                    SimpleConjunctiveQuery newTargetView = new SimpleConjunctiveQuery(connectedVariables.getVariables(), connectedVariables.getJoinConditions());
                    // note: it is necessary to clone the source view, otherwise differences due to subsumptions for different tgds will add-up
                    FORule newTGD = new FORule(tgd.getComplexSourceQuery().clone(), newTargetView);
                    addCorrespondences(tgd, newTGD);
                    if (logger.isDebugEnabled()) logger.debug("Connected group: " + connectedVariables + "\nNormalized tgd: " + newTGD);
//                    if (!normalizedTgds.contains(newTGD)) {
                        normalizedTgds.add(newTGD);
//                    }
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting set of tgds: " + normalizedTgds);
        return normalizedTgds;
    }

    private void addCorrespondences(FORule tgd, FORule newTGD) {
        for (VariableCorrespondence variableCorrespondence : tgd.getCoveredCorrespondences()) {
            if (SpicyEngineUtility.containsVariableWithSameId(newTGD.getTargetView().getGenerators(), variableCorrespondence.getTargetPath().getStartingVariable())) {
                newTGD.addCoveredCorrespondence(variableCorrespondence);
            }
        }
    }

    ////////////////////////    VIEW NORMALIZATION
    public List<ConnectedVariables> findConnectedVariables(FORule tgd) {
        List<ConnectedVariables> result = new ArrayList<ConnectedVariables>();
        for (SetAlias variable : tgd.getTargetView().getVariables()) {
            ConnectedVariables connectedVariables = findConnectedVariables(result, tgd, variable);
            if (connectedVariables == null) {
                connectedVariables = new ConnectedVariables(variable);
                result.add(connectedVariables);
            }
        }
        return result;
    }

    private ConnectedVariables findConnectedVariables(List<ConnectedVariables> result, FORule tgd, SetAlias variable) {
        for (ConnectedVariables connectedVariables : result) {
            if (joinsOnSkolem(connectedVariables, variable, tgd)) {
                return connectedVariables;
            }
        }
        return null;
    }

    private boolean joinsOnSkolem(ConnectedVariables connectedVariables, SetAlias variable, FORule tgd) {
        // for normalization purposes it is sufficient to consider join conditions; cyclic joins do not add to reachability
        for (VariableJoinCondition joinCondition : tgd.getTargetView().getJoinConditions()) {
            if (joinsOnSkolem(joinCondition, tgd) && joins(connectedVariables, variable, joinCondition)) {
                if (!SpicyEngineUtility.containsVariableWithSameId(connectedVariables.getVariables(), variable)) {
                    connectedVariables.addVariable(variable);
                }
                if (!SpicyEngineUtility.containsJoinWithSameIds(connectedVariables.getJoinConditions(), joinCondition)) {
                    connectedVariables.addJoinCondition(joinCondition);
                }
                return true;
            }
        }
        return false;
    }

    private boolean joinsOnSkolem(VariableJoinCondition joinCondition, FORule tgd) {
        boolean fromPathsAreSkolem = checkSkolems(joinCondition.getFromPaths(), tgd);
        boolean toPathsAreSkolem = checkSkolems(joinCondition.getToPaths(), tgd);
//        if (fromPathsAreSkolem != toPathsAreSkolem) {
//            throw new IllegalMappingTaskException("There is a conflict for join condition " + joinCondition + " in tgd " + tgd);
//        }
        return fromPathsAreSkolem && toPathsAreSkolem;
    }

    private boolean checkSkolems(List<VariablePathExpression> paths, FORule tgd) {
        for (VariablePathExpression path : paths) {
            if (isUniversal(path, tgd.getCoveredCorrespondences())) {
                return false;
            }
        }
        return true;
    }

    private boolean isUniversal(VariablePathExpression path, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().getStartingVariable().hasSameId(path.getStartingVariable()) &&
                    correspondence.getTargetPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean joins(ConnectedVariables connectedVariables, SetAlias variable, VariableJoinCondition joinCondition) {
        SetAlias fromVariable = joinCondition.getFromVariable();
        SetAlias toVariable = joinCondition.getToVariable();
        List<SetAlias> connectedGenerators = findGenerators(connectedVariables);
        if (SpicyEngineUtility.containsVariableWithSameId(variable.getGenerators(), fromVariable)) {
            return SpicyEngineUtility.containsVariableWithSameId(connectedGenerators, toVariable);
        } else if (SpicyEngineUtility.containsVariableWithSameId(variable.getGenerators(), toVariable)) {
            return SpicyEngineUtility.containsVariableWithSameId(connectedGenerators, fromVariable);
        }
        return false;
    }

    private List<SetAlias> findGenerators(ConnectedVariables connectedVariables) {
        List<SetAlias> connectedGenerators = new ArrayList<SetAlias>();
        for (SetAlias variable : connectedVariables.getVariables()) {
            List<SetAlias> variableGenerators = variable.getGenerators();
            for (SetAlias variableGenerator : variableGenerators) {
                if (!SpicyEngineUtility.containsVariableWithSameId(connectedGenerators, variableGenerator)) {
                    connectedGenerators.add(variableGenerator);
                }
            }
        }
        return connectedGenerators;
    }


}
