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

import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class FindJoinGroups {

    public List<JoinGroup> findJoinGroups(List<VariableJoinCondition> joinConditions) {
        List<JoinGroup> result = new ArrayList<JoinGroup>();
        for (VariableJoinCondition joinCondition : joinConditions) {
            JoinGroup joinGroupForCondition = findJoinGroup(joinCondition, result);
            if (joinGroupForCondition == null) {
                JoinGroup newJoinGroup = new JoinGroup(joinCondition);
                result.add(newJoinGroup);
            } else {
                joinGroupForCondition.addJoinCondition(joinCondition);
            }
        }
        return result;
    }

    private JoinGroup findJoinGroup(VariableJoinCondition variableJoinCondition, List<JoinGroup> result) {
        for (JoinGroup joinGroup : result) {
            if (generateSameVariable(variableJoinCondition, joinGroup)) {
                return joinGroup;
            }
        }
        return null;
    }

    private boolean generateSameVariable(VariableJoinCondition joinCondition, JoinGroup joinGroup) {
        for (VariableJoinCondition joinConditionInGroup : joinGroup.getJoinConditions()) {
            if ((joinCondition.getFromVariable().getId() == joinConditionInGroup.getFromVariable().getId()) &&
                    samePaths(joinCondition.getFromPaths(), joinConditionInGroup.getFromPaths())) {
                return true;
            }
            if ((joinCondition.getToVariable().getId() == joinConditionInGroup.getToVariable().getId()) &&
                    samePaths(joinCondition.getToPaths(), joinConditionInGroup.getToPaths())) {
                return true;
            }
            if ((joinCondition.getFromVariable().getId() == joinConditionInGroup.getToVariable().getId()) &&
                    samePaths(joinCondition.getFromPaths(), joinConditionInGroup.getToPaths())) {
                return true;
            }
            if ((joinCondition.getToVariable().getId() == joinConditionInGroup.getFromVariable().getId()) &&
                    samePaths(joinCondition.getToPaths(), joinConditionInGroup.getFromPaths())) {
                return true;
            }
        }
        return false;
    }

    private boolean samePaths(List<VariablePathExpression> pathList, List<VariablePathExpression> otherPathList) {
        return SpicyEngineUtility.equalLists(pathList, otherPathList);
    }
}
