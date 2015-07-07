/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Donatello Santoro - donatello.santoro@gmail.com

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

import it.unibas.spicy.model.mapping.joingraph.JoinEdge;
import it.unibas.spicy.model.mapping.joingraph.JoinGraph;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class CheckCyclesInViews {

    private static Log logger = LogFactory.getLog(CheckCyclesInViews.class);

    boolean isCyclic(VariableJoinCondition joinCondition, SimpleConjunctiveQuery view) {
        if (logger.isDebugEnabled()) logger.debug("************* Checking join condition: " + joinCondition + " in\n" + view);
        JoinGraph joinGraph = initializeJoinGraph(view);
        if (logger.isDebugEnabled()) logger.debug("************* Join graph: " + joinGraph);
        boolean result = visitGraph(joinCondition, joinGraph, view);
        if (logger.isDebugEnabled()) logger.debug("************* Result: " + result);
        return result;
    }

    ////////////////    JOIN GRAPH CONSTRUCTION    ////////////////////
    JoinGraph initializeJoinGraph(SimpleConjunctiveQuery view) {
        JoinGraph joinGraph = new JoinGraph();
        for (SetAlias variable : view.getGenerators()) {
            joinGraph.addNode(variable);
        }
        for (VariableJoinCondition joinCondition : view.getJoinConditions()) {
            addJoinConditionToGraph(joinCondition, joinGraph, view);
        }
        return joinGraph;
    }

    private void addJoinConditionToGraph(VariableJoinCondition joinCondition, JoinGraph joinGraph, SimpleConjunctiveQuery view) {
        SetAlias fromVariable = findVariable(joinCondition.getFromVariable(), view);
        SetAlias toVariable = findVariable(joinCondition.getToVariable(), view);
        joinGraph.addSuccessor(fromVariable, toVariable, joinCondition);
        if (!joinCondition.isMonodirectional()) {
            joinGraph.addSuccessor(toVariable, fromVariable, joinCondition);
        }
    }

    private SetAlias findVariable(SetAlias joinVariable, SimpleConjunctiveQuery view) {
        for (SetAlias viewVariable : view.getVariables()) {
            if (viewVariable.getGenerators().contains(joinVariable)) {
                return viewVariable;
            }
        }
        throw new IllegalArgumentException("Unable to find variable " + joinVariable + " in view " + view);
    }

    ////////////////    JOIN GRAPH VISIT    ////////////////////
    private boolean visitGraph(VariableJoinCondition newJoinCondition, JoinGraph joinGraph, SimpleConjunctiveQuery view) {
        SetAlias fromVariable = findVariable(newJoinCondition.getFromVariable(), view);
        SetAlias toVariable = findVariable(newJoinCondition.getToVariable(), view);
        if (fromVariable.equals(toVariable)) {
            return true;
        }
        if (logger.isDebugEnabled()) logger.debug("Starting join graph visit to find path between: " + fromVariable + " and " + toVariable);
        if (searchPathBetweenVariables(fromVariable, toVariable, joinGraph) ||
                searchPathBetweenVariables(toVariable, fromVariable, joinGraph)) {
            return true;
        }
        return false;
    }

    private boolean searchPathBetweenVariables(SetAlias startingVariable, SetAlias endingVariable, JoinGraph joinGraph) {
        List<SetAlias> visitedVariables = new ArrayList<SetAlias>();
        List<JoinEdge> visitedEdges = new ArrayList<JoinEdge>();
        visitForPath(startingVariable, joinGraph, visitedVariables, visitedEdges);
        if (logger.isDebugEnabled()) logger.debug("End of visit; visited variables: " + visitedVariables);
        if (visitedVariables.contains(endingVariable)) {
            return true;
        }
        return false;
    }

    private void visitForPath(SetAlias variable, JoinGraph joinGraph, List<SetAlias> visitedVariables, List<JoinEdge> visitedEdges) {
        visitedVariables.add(variable);
        if (logger.isDebugEnabled()) logger.debug("Visiting graph searching for path from set: " + variable);
        List<JoinEdge> edges = joinGraph.getSuccessors().get(variable);
        for (JoinEdge edge : edges) {
            if (!visitedEdges.contains(edge)) {
                SetAlias successor = edge.getDestinationVariable();
                if (!visitedVariables.contains(successor)) {
                    visitedEdges.add(edge);
                    visitForPath(successor, joinGraph, visitedVariables, visitedEdges);
                }
            }
        }
    }
}
