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

import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MatchJoinGroupsForExpansions {

    private static Log logger = LogFactory.getLog(MatchJoinGroupsForExpansions.class);

    boolean checkJoinGroups(JoinGroup joinGroup, JoinGroup fatherJoinGroup,
            List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        if (logger.isDebugEnabled()) logger.debug("Checking containment of join group:\n" + joinGroup + "by father join group:\n" + fatherJoinGroup);
        if (logger.isDebugEnabled()) logger.debug("Expansion atoms:\n" + expansionAtoms + "\nFather atoms:\n" + fatherAtoms);
        Map<SetAlias, SetAlias> coveringVariablesMap = findJoinGroupCoveringVariables(joinGroup, expansionAtoms, fatherAtoms);
        List<SetAlias> fatherGroupVariables = getVariables(fatherJoinGroup);
        boolean result = fatherJoinGroupHasRightVariables(joinGroup, fatherJoinGroup, coveringVariablesMap, fatherGroupVariables) &&
                fatherJoinGroupHasRightPaths(joinGroup, fatherJoinGroup, coveringVariablesMap);
        if (logger.isDebugEnabled()) logger.debug("Result of check: " + result);
        return result;
    }

    ////////////////////////   CHECKING VARIABLES
    private boolean fatherJoinGroupHasRightVariables(JoinGroup joinGroup, JoinGroup fatherJoinGroup,
            Map<SetAlias, SetAlias> coveringVariablesMap, List<SetAlias> fatherGroupVariables) {
        List<SetAlias> coveringVariablesForGroup = new ArrayList<SetAlias>(coveringVariablesMap.values());
        boolean result = containsAllVariables(fatherGroupVariables, coveringVariablesForGroup);
        if (logger.isDebugEnabled()) logger.debug("Looking for variables: " + coveringVariablesForGroup);
        if (logger.isDebugEnabled()) logger.debug("Check for variable containment: " + result);
        return result;
    }

    private Map<SetAlias, SetAlias> findJoinGroupCoveringVariables(JoinGroup joinGroup, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        Map<SetAlias, SetAlias> coverageVariables = new HashMap<SetAlias, SetAlias>();
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            SetAlias fromVariable = joinCondition.getFromVariable();
            if (coverageVariables.get(fromVariable) == null) {
                Integer fromAtomPosition = findAtomPositionFromVariable(fromVariable, expansionAtoms);
                SetAlias coveringFromVariable = fatherAtoms.get(fromAtomPosition).getCoveringAtom().getVariable();
                coverageVariables.put(fromVariable, coveringFromVariable);
            }
            SetAlias toVariable = joinCondition.getToVariable();
            if (coverageVariables.get(toVariable) == null) {
                Integer toAtomPosition = findAtomPositionFromVariable(toVariable, expansionAtoms);
                SetAlias coveringToVariable = fatherAtoms.get(toAtomPosition).getCoveringAtom().getVariable();
                coverageVariables.put(toVariable, coveringToVariable);
            }
        }
        return coverageVariables;
    }

    private Integer findAtomPositionFromVariable(SetAlias joinVariable, List<ExpansionElement> coveringAtoms) {
        for (int i = 0; i < coveringAtoms.size(); i++) {
            ExpansionElement coveringAtom = coveringAtoms.get(i);
            if (coveringAtom.getCoveringAtom().getVariable().getId() == joinVariable.getId()) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find variable " + joinVariable + " in " + coveringAtoms);
    }

    private boolean containsAllVariables(List<SetAlias> joinGroupVariables, List<SetAlias> coveringVariables) {
        List<SetAlias> joinGroupVariableClone = new ArrayList<SetAlias>(joinGroupVariables);
        for (SetAlias variable : coveringVariables) {
            if (!checkContainmentAndRemove(variable, joinGroupVariableClone)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkContainmentAndRemove(SetAlias variable, List<SetAlias> joinGroupVariableClone) {
        for (Iterator<SetAlias> it = joinGroupVariableClone.iterator(); it.hasNext();) {
            SetAlias variableInGroup = it.next();
            if (variableInGroup.getId() == variable.getId()) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    ////////////////////////   CHECKING PATHS
    private boolean fatherJoinGroupHasRightPaths(JoinGroup joinGroup, JoinGroup fatherJoinGroup, Map<SetAlias, SetAlias> coveringVariablesMap) {
        List<List<VariablePathExpression>> joinPaths = getAttributePaths(joinGroup);
        List<List<VariablePathExpression>> fatherJoinPaths = getAttributePaths(fatherJoinGroup);
        if (logger.isDebugEnabled()) logger.debug("Checking paths:\n" + joinPaths + "by father join paths:\n" + fatherJoinPaths);
        for (List<VariablePathExpression> paths : joinPaths) {
            if (!containsPathsWithCorrectVariable(fatherJoinPaths, paths, coveringVariablesMap)) {
                if (logger.isDebugEnabled()) logger.debug("Father does not contain paths:\n" + paths);
                return false;
            }
        }
        return true;
    }

    private boolean containsPathsWithCorrectVariable(List<List<VariablePathExpression>> listOfFatherPaths, List<VariablePathExpression> paths, Map<SetAlias, SetAlias> coveringVariablesMap) {
        for (List<VariablePathExpression> fatherPath : listOfFatherPaths) {
            if (equalLists(fatherPath, paths, coveringVariablesMap)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalLists(List<VariablePathExpression> fatherPaths, List<VariablePathExpression> paths, Map<SetAlias, SetAlias> coveringVariablesMap) {
        List<VariablePathExpression> pathsClone = new ArrayList<VariablePathExpression>(paths);
        for (VariablePathExpression fatherPath : fatherPaths) {
            if (!findEqualPathsAndRemove(pathsClone, fatherPath, coveringVariablesMap)) {
                return false;
            }
        }
        if (!pathsClone.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean findEqualPathsAndRemove(List<VariablePathExpression> pathsClone, VariablePathExpression fatherPath, Map<SetAlias, SetAlias> coveringVariablesMap) {
        for (Iterator<VariablePathExpression> it = pathsClone.iterator(); it.hasNext();) {
            VariablePathExpression path = it.next();
            SetAlias coveringVariable = coveringVariablesMap.get(path.getStartingVariable());
            if ((coveringVariable.getId() == fatherPath.getStartingVariable().getId()) &&
                    path.equalsUpToClones(fatherPath)) {
                it.remove();
                return true;
            }
        }
        return false;
    }


    ///////////////////////     OPERATIONS ON JOIN GROUPS
    private List<SetAlias> getVariables(JoinGroup joinGroup) {
        List<SetAlias> result = new ArrayList<SetAlias>();
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            if (!SpicyEngineUtility.containsVariableWithSameId(result, joinCondition.getFromVariable())) {
                result.add(joinCondition.getFromVariable());
            }
            if (!SpicyEngineUtility.containsVariableWithSameId(result, joinCondition.getToVariable())) {
                result.add(joinCondition.getToVariable());
            }
        }
        return result;
    }

    public List<List<VariablePathExpression>> getAttributePaths(JoinGroup joinGroup) {
        List<List<VariablePathExpression>> result = new ArrayList<List<VariablePathExpression>>();
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            if (!containsPaths(result, joinCondition.getFromPaths())) {
                result.add(joinCondition.getFromPaths());
            }
            if (!containsPaths(result, joinCondition.getToPaths())) {
                result.add(joinCondition.getToPaths());
            }
        }
        return result;
    }

    private boolean containsPaths(List<List<VariablePathExpression>> listOfFatherPaths, List<VariablePathExpression> paths) {
        for (List<VariablePathExpression> fatherPath : listOfFatherPaths) {
            if (equalLists(fatherPath, paths)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalLists(List<VariablePathExpression> fatherPaths, List<VariablePathExpression> paths) {
        List<VariablePathExpression> pathsClone = new ArrayList<VariablePathExpression>(paths);
        for (VariablePathExpression fatherPath : fatherPaths) {
            if (!findEqualPathsAndRemove(pathsClone, fatherPath)) {
                return false;
            }
        }
        if (!pathsClone.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean findEqualPathsAndRemove(List<VariablePathExpression> pathsClone, VariablePathExpression fatherPath) {
        for (Iterator<VariablePathExpression> it = pathsClone.iterator(); it.hasNext();) {
            VariablePathExpression path = it.next();
            if ((path.getStartingVariable().getId() == fatherPath.getStartingVariable().getId()) &&
                    path.equalsUpToClones(fatherPath)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}
