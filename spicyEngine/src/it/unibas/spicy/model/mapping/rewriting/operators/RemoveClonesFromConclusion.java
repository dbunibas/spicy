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
 
package it.unibas.spicy.model.mapping.rewriting.operators;

import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class RemoveClonesFromConclusion {

    public List<FORule> cloneAndRemoveClonesFromConclusions(List<FORule> rules) {
        List<FORule> result = new ArrayList<FORule>();
        for (FORule rule : rules) {
            FORule clone = rule.clone();
            removeClonesFromRuleConclusion(clone);
            if (!result.contains(clone)) {
                result.add(clone);
            }
        }
        return result;
    }

    public void removeClonesFromRuleConclusion(List<FORule> rules) {
        for (FORule rule : rules) {
            removeClonesFromRuleConclusion(rule);
        }
    }

    public void removeClonesFromRuleConclusion(FORule rule) {
        List<SetAlias> newVariables = new ArrayList<SetAlias>();
        for (SetAlias variable : rule.getTargetView().getVariables()) {
            newVariables.add(removeClones(variable));
        }
        List<VariableJoinCondition> newJoinConditions = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition joinCondition : rule.getTargetView().getJoinConditions()) {
            newJoinConditions.add(removeClones(joinCondition));
        }
        List<VariableJoinCondition> newCyclicJoinConditions = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition joinCondition : rule.getTargetView().getCyclicJoinConditions()) {
            newCyclicJoinConditions.add(removeClones(joinCondition));
        }
        SimpleConjunctiveQuery newTargetQuery = new SimpleConjunctiveQuery(newVariables, newJoinConditions, newCyclicJoinConditions);
        rule.setTargetView(newTargetQuery);
        List<VariableCorrespondence> newCorrespondences = new ArrayList<VariableCorrespondence>();
        for (VariableCorrespondence correspondence : rule.getCoveredCorrespondences()) {
            newCorrespondences.add(removeClones(correspondence));
        }
        rule.setCoveredCorrespondences(newCorrespondences);
    }

    private SetAlias removeClones(SetAlias variable) {
        SetAlias clone = variable.clone();
        SetAlias currentVariable = clone;
        while (currentVariable != null) {
            VariablePathExpression newPath = cleanBindingPathExpression(currentVariable.getBindingPathExpression());
            currentVariable.setBindingPathExpression(newPath);
            currentVariable = currentVariable.getBindingPathExpression().getStartingVariable();
        }
        return clone;
    }

    private VariablePathExpression cleanBindingPathExpression(VariablePathExpression pathExpression) {
        List<String> cleanSteps = cleanPathSteps(pathExpression.getPathSteps());
        if (pathExpression.getStartingVariable() == null) {
            return new VariablePathExpression(cleanSteps);
        }
        return new VariablePathExpression(pathExpression.getStartingVariable(), cleanSteps);
    }

    private List<String> cleanPathSteps(List<String> pathSteps) {
        List<String> cleanSteps = new ArrayList<String>();
        for (String step : pathSteps) {
            cleanSteps.add(SetCloneNode.removeCloneLabel(step));
        }
        return cleanSteps;
    }

    private VariableCorrespondence removeClones(VariableCorrespondence correspondence) {
        VariableCorrespondence newCorrespondence = correspondence.clone();
        newCorrespondence.setTargetPath(removeClones(correspondence.getTargetPath()));
        return newCorrespondence;
    }

    private VariableJoinCondition removeClones(VariableJoinCondition joinCondition) {
        List<VariablePathExpression> newFromPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
            newFromPaths.add(removeClones(fromPath));
        }
        List<VariablePathExpression> newToPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression toPath : joinCondition.getToPaths()) {
            newToPaths.add(removeClones(toPath));
        }
        VariableJoinCondition newJoin = new VariableJoinCondition(newFromPaths, newToPaths, joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return newJoin;
    }

    private VariablePathExpression removeClones(VariablePathExpression pathExpression) {
        SetAlias newVariable = removeClones(pathExpression.getStartingVariable());
        List<String> newPathSteps = cleanPathSteps(pathExpression.getPathSteps());
        VariablePathExpression newPath = new VariablePathExpression(newVariable, newPathSteps);
        return newPath;
    }
}
