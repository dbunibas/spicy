/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.IIdentifiable;
import it.unibas.spicy.model.mapping.QueryRenaming;
import it.unibas.spicy.model.mapping.SourceEqualities;
import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class RenameSetAliasesUtility {

    private static Log logger = LogFactory.getLog(RenameSetAliasesUtility.class);

    public List<VariableCorrespondence> renameTargetSetAliasInCorrespondences(List<VariableCorrespondence> correspondences, QueryRenaming sourceViewRenaming, QueryRenaming targetViewRenaming) {
        List<VariableCorrespondence> result = renameSourceSetAliasesInCorrespondences(correspondences, sourceViewRenaming.getRenamings());
        for (VariableCorrespondence correspondence : result) {
            VariablePathExpression newTargetPath = correctPath(correspondence.getTargetPath(), targetViewRenaming.getRenamings());
            correspondence.setTargetPath(newTargetPath);
        }
        return result;
    }

    public List<VariableCorrespondence> renameSourceSetAliasesInCorrespondences(List<VariableCorrespondence> coveredCorrespondences, Map<Integer, SetAlias> renamings) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (VariableCorrespondence correspondence : coveredCorrespondences) {
            if (correspondence.isConstant()) {
                continue;
            }
            List<VariablePathExpression> newSourcePaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression sourcePath : correspondence.getSourcePaths()) {
                newSourcePaths.add(correctPath(sourcePath, renamings));
            }
            Expression newTransformationFunction = replaceVariableInExpression(correspondence.getTransformationFunction(), renamings);
            VariableCorrespondence newCorrespondence = new VariableCorrespondence(newSourcePaths, null, correspondence.getTargetPath(), newTransformationFunction);
            result.add(newCorrespondence);
        }
        return result;
    }

    //// joinConditions
    public VariableJoinCondition generateNewJoin(VariableJoinCondition joinCondition, Map<Integer, SetAlias> variableRenamings) {
        List<VariablePathExpression> newFromPaths = correctPaths(joinCondition.getFromPaths(), variableRenamings);
        List<VariablePathExpression> newToPaths = correctPaths(joinCondition.getToPaths(), variableRenamings);
        VariableJoinCondition newJoinCondition = new VariableJoinCondition(newFromPaths, newToPaths,
                joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return newJoinCondition;
    }

    //// pathExpression
    public List<VariablePathExpression> correctPaths(List<VariablePathExpression> oldPaths, Map<Integer, SetAlias> variableRenamings) {
        List<VariablePathExpression> newPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression oldPath : oldPaths) {
            VariablePathExpression newPath = correctPath(oldPath, variableRenamings);
            newPaths.add(newPath);
        }
        return newPaths;
    }

    public VariablePathExpression correctPath(VariablePathExpression oldPath, Map<Integer, SetAlias> variableRenamings) {
        SetAlias newVariable = variableRenamings.get(oldPath.getStartingVariable().getId());
        if (newVariable == null) {
            return oldPath;
        }
        VariablePathExpression newPath = new VariablePathExpression(newVariable, oldPath.getPathSteps());
        return newPath;
    }

    // selection conditions
    public VariableSelectionCondition generateNewSelection(VariableSelectionCondition selectionCondition, Map<Integer, SetAlias> variableRenamings) {
        if (logger.isTraceEnabled()) logger.debug("Changing selection condition: " + selectionCondition);
        Expression newExpression = replaceVariableInExpression(selectionCondition.getCondition(), variableRenamings);
        return new VariableSelectionCondition(newExpression);
    }

    private List<VariablePathExpression> changeSetPaths(List<VariablePathExpression> oldSetPaths, Map<Integer, SetAlias> variableRenamings) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression oldSetPath : oldSetPaths) {
            SetAlias newVariable = variableRenamings.get(oldSetPath.getStartingVariable().getId());
            if (newVariable != null) {
                result.add(new VariablePathExpression(oldSetPath, newVariable));
            } else {
                result.add(oldSetPath);
            }
        }
        return result;
    }

    private Expression replaceVariableInExpression(Expression oldExpression, Map<Integer, SetAlias> variableRenamings) {
        if (logger.isDebugEnabled()) logger.debug("Replacing paths in expression: " + oldExpression);
        Expression expression = oldExpression.clone();
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            VariablePathExpression oldDescription = (VariablePathExpression) variable.getDescription();
            SetAlias newVariable = variableRenamings.get(oldDescription.getStartingVariable().getId());
            if (newVariable != null) {
                VariablePathExpression newDescription = new VariablePathExpression(oldDescription, newVariable);
                variable.setDescription(newDescription);
                variable.setOriginalDescription(newDescription.getAbsolutePath());
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting expression: " + expression);
        return expression;
    }

    public VariableSelectionCondition generateNewSelection(VariableSelectionCondition selectionCondition, SetAlias newVariable) {
        assert (selectionCondition.getSetVariables().size() == 1) : "Method is applicable only to single selection conditions: " + selectionCondition;
        Map<Integer, SetAlias> renaming = new HashMap<Integer, SetAlias>();
        Integer id = selectionCondition.getSetVariables().get(0).getId();
        renaming.put(id, newVariable);
        return generateNewSelection(selectionCondition, renaming);
    }

    public String generateId(List<IIdentifiable> stack) {
        StringBuilder result = new StringBuilder();
//        result.append("#");
        for (int i = stack.size() - 1; i >= 0; i--) {
            IIdentifiable identifiable = stack.get(i);
            result.append(generateIdForView(identifiable));
        }
        return result.toString();
    }

    public String generateIdForView(IIdentifiable view) {
        return view.getId() + "#";
    }

    public QueryRenaming getRenamingForChild(IIdentifiable query, Map<String, QueryRenaming> renamings, List<IIdentifiable> stack) {
        stack.add(0, query);
        QueryRenaming renaming = renamings.get(generateId(stack));
        if (renaming == null) {
            throw new IllegalArgumentException("Unable to find renaming for id " + generateId(stack) + "\nRenamings: " + SpicyEngineUtility.printMap(renamings));
        }
        stack.remove(0);
        return renaming;
    }

    public Map<Integer, SetAlias> generateAllRenamings(List<QueryRenaming> renamings) {
        Map<Integer, SetAlias> result = new HashMap<Integer, SetAlias>();
        for (QueryRenaming renaming : renamings) {
            result.putAll(renaming.getRenamings());
        }
        return result;
    }

    TargetEqualities renameRightCorrespondencesInEqualities(TargetEqualities targetEqualities, QueryRenaming renamings) {
        TargetEqualities result = targetEqualities.clone();
        List<VariableCorrespondence> newCorrespondences = renameSourceSetAliasesInCorrespondences(targetEqualities.getRightCorrespondences(), renamings.getRenamings());
        result.setRightCorrespondences(newCorrespondences);
        return result;
    }

    TargetEqualities renameLeftCorrespondencesInEqualities(TargetEqualities targetEqualities, QueryRenaming renamings) {
        TargetEqualities result = targetEqualities.clone();
        List<VariableCorrespondence> newCorrespondences = renameSourceSetAliasesInCorrespondences(targetEqualities.getLeftCorrespondences(), renamings.getRenamings());
        result.setLeftCorrespondences(newCorrespondences);
        return result;
    }

    TargetEqualities renamePathsInIntersectionEqualities(TargetEqualities intersectionEqualities, List<QueryRenaming> renamings, QueryRenaming intersectionRenaming) {
        Map<Integer, SetAlias> allRenamings = generateAllRenamings(renamings);
        TargetEqualities result = intersectionEqualities.clone();
        List<VariableCorrespondence> newLeftCorrespondences = renameSourceSetAliasesInCorrespondences(intersectionEqualities.getLeftCorrespondences(), allRenamings);
        result.setLeftCorrespondences(newLeftCorrespondences);
        List<VariableCorrespondence> newRightCorrespondences = renameSourceSetAliasesInCorrespondences(intersectionEqualities.getRightCorrespondences(), intersectionRenaming.getRenamings());
        result.setRightCorrespondences(newRightCorrespondences);
        return result;

    }

    SourceEqualities renameRightPathsInEqualities(SourceEqualities sourceEqualities, Map<Integer, SetAlias> renamings) {
        SourceEqualities result = sourceEqualities.clone();
        List<VariablePathExpression> newPaths = correctPaths(sourceEqualities.getRightPaths(), renamings);
        result.setRightPaths(newPaths);
        return result;
    }

    SourceEqualities changeLeftPathsInEqualities(SourceEqualities sourceEqualities, Map<Integer, SetAlias> renamings) {
        SourceEqualities result = sourceEqualities.clone();
        List<VariablePathExpression> newPaths = correctPaths(sourceEqualities.getLeftPaths(), renamings);
        result.setLeftPaths(newPaths);
        return result;
    }
}
