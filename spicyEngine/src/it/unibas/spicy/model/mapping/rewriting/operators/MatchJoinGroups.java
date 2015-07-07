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
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MatchJoinGroups {

    private static Log logger = LogFactory.getLog(MatchJoinGroups.class);

    // this component assumes that join variables have been normalized, i.e., both the join and the join group
    // use exactly the same variables
    boolean checkJoinConditionContainment(VariableJoinCondition joinCondition, JoinGroup fatherJoinGroup) {
        JoinGroup singleton = new JoinGroup(joinCondition);
        return checkJoinGroupContainment(singleton, fatherJoinGroup);
    }

    boolean checkJoinGroupContainment(JoinGroup joinGroup, JoinGroup fatherJoinGroup) {
        // assumes that paths in joinGroup must have identical variable ids to those in fatherJoinGroup
        if (logger.isDebugEnabled()) logger.debug("Checking containment of join group:\n" + joinGroup + "by father join group:\n" + fatherJoinGroup);
        boolean result = checkPathContainment(joinGroup, fatherJoinGroup);
        if (logger.isDebugEnabled()) logger.debug("Result of check: " + result);
        return result;
    }

    ////////////////////////   CHECKING PATHS

    private boolean checkPathContainment(JoinGroup joinGroup, JoinGroup fatherJoinGroup) {
        List<List<VariablePathExpression>> joinPaths = getAttributePaths(joinGroup);
        List<List<VariablePathExpression>> fatherJoinPaths = getAttributePaths(fatherJoinGroup);
        if (logger.isDebugEnabled()) logger.debug("Checking paths:\n" + joinPaths + "by father join paths:\n" + fatherJoinPaths);
        for (List<VariablePathExpression> paths : joinPaths) {
            if (!containsPaths(fatherJoinPaths, paths)) {
                if (logger.isDebugEnabled()) logger.debug("Father does not contain paths:\n" + paths);
                return false;
            }
        }
        return true;
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
            if (path.equalsAndHasSameVariableId(fatherPath)) {
//            if (path.equals(fatherPath)) {
                it.remove();
                return true;
            }
        }
        return false;
    }


    ///////////////////////     OPERATIONS ON JOIN GROUPS

    List<List<VariablePathExpression>> getAttributePaths(JoinGroup joinGroup) {
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


}
