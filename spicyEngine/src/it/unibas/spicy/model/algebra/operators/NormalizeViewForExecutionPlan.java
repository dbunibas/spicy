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
package it.unibas.spicy.model.algebra.operators;

import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NormalizeViewForExecutionPlan {

    private static Log logger = LogFactory.getLog(NormalizeViewForExecutionPlan.class);

    public SimpleConjunctiveQuery normalizeView(SimpleConjunctiveQuery view) {
        if (logger.isDebugEnabled()) logger.debug("Normalizing view: " + view);
        SimpleConjunctiveQuery result = new SimpleConjunctiveQuery();
        List<SetAlias> variablesToAdd = new ArrayList<SetAlias>(view.getVariables());
        SetAlias firstVariable = variablesToAdd.remove(0);
        result.addVariable(firstVariable);
        List<VariableJoinCondition> allJoinsToAdd = new ArrayList<VariableJoinCondition>(view.getAllJoinConditions());
        while (!allJoinsToAdd.isEmpty()) {
            if (logger.isDebugEnabled()) logger.trace("Current view: " + result);
            if (logger.isDebugEnabled()) logger.trace("Variables to add: " + variablesToAdd);
            VariableJoinCondition joinToAdd = findAndRemoveFirstJoinOnAddedVariables(allJoinsToAdd, variablesToAdd, result);
            if (logger.isDebugEnabled()) logger.trace("Join to add: " + joinToAdd);
            SetAlias variableToAdd = findAndRemoveVariableToAdd(joinToAdd, variablesToAdd);
            if (logger.isDebugEnabled()) logger.trace("New variable to add: " + joinToAdd);
            if (variableToAdd == null) {
                if (logger.isDebugEnabled()) logger.trace("Join is cyclic...");
                result.addCyclicJoinCondition(joinToAdd);
            } else {
                result.addJoinCondition(joinToAdd);
                result.addVariable(variableToAdd);
            }
        }
        result.addSelectionConditions(view.getSelectionConditions());
        return result;
    }

    private VariableJoinCondition findAndRemoveFirstJoinOnAddedVariables(List<VariableJoinCondition> allJoinsToAdd, List<SetAlias> variablesToAdd, SimpleConjunctiveQuery result) {
        for (int i = 0; i < allJoinsToAdd.size(); i++) {
            VariableJoinCondition joinCondition = allJoinsToAdd.get(i);
            if (containsVariableWithSameId(joinCondition.getFromVariable(), variablesToAdd) != -1
                    && containsVariableWithSameId(joinCondition.getToVariable(), variablesToAdd) != -1) {
                continue;
            }
            allJoinsToAdd.remove(i);
            return joinCondition;
        }
        throw new IllegalArgumentException("No join condition on added variables. Join conditions: " + allJoinsToAdd + "\nVariables to add: " + variablesToAdd + "\nCurrent result: " + result);
    }

    private SetAlias findAndRemoveVariableToAdd(VariableJoinCondition joinToAdd, List<SetAlias> variablesToAdd) {
        int posOfFromVariable = containsVariableWithSameId(joinToAdd.getFromVariable(), variablesToAdd);
        if (posOfFromVariable != -1) {
            return variablesToAdd.remove(posOfFromVariable);
        }
        int posOfToVariable = containsVariableWithSameId(joinToAdd.getToVariable(), variablesToAdd);
        if (posOfToVariable != -1) {
            return variablesToAdd.remove(posOfToVariable);
        }
        return null;
    }

    private int containsVariableWithSameId(SetAlias variable, List<SetAlias> variables) {
        for (int i = 0; i < variables.size(); i++) {
            SetAlias otherVariable = variables.get(i);
            for (SetAlias generator : otherVariable.getGenerators()) {
                if (generator.getId() == variable.getId()) {
                    return i;
                }
            }
        }
        return -1;
    }
}
