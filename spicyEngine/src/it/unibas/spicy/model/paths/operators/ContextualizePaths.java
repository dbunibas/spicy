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
 
package it.unibas.spicy.model.paths.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.expressions.operators.CheckExpressions;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class ContextualizePaths {

    private static Log logger = LogFactory.getLog(ContextualizePaths.class);
    
    private CheckExpressions filterChecker = new CheckExpressions();

    // FUNCTIONAL DEPENDENCIES
    public List<VariableFunctionalDependency> contextualizeFunctionalDependencies(IDataSourceProxy dataSource) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            result.add(createDependencyFromKey(keyConstraint, dataSource));
        }
        for (FunctionalDependency dependency : dataSource.getFunctionalDependencies()) {
            result.add(contextualizeFunctionalDependency(dependency, dataSource));
        }
        return result;
    }

    public VariableFunctionalDependency contextualizeFunctionalDependency(FunctionalDependency functionalDependency, IDataSourceProxy dataSource) {
        List<VariablePathExpression> leftVariablePaths = contextualizePaths(functionalDependency.getLeftPaths(), dataSource);
        List<VariablePathExpression> rightVariablePaths = contextualizePaths(functionalDependency.getRightPaths(), dataSource);
        VariableFunctionalDependency variableDependency = new VariableFunctionalDependency(leftVariablePaths, rightVariablePaths);
        return variableDependency;
    }

    //  JOIN CONDITIONS
    public VariableJoinCondition contextualizeJoinCondition(JoinCondition joinCondition, IDataSourceProxy dataSource) {
        List<PathExpression> toPaths = joinCondition.getToPaths();
        List<PathExpression> fromPaths = joinCondition.getFromPaths();
        List<VariablePathExpression> variableToPaths = contextualizePaths(toPaths, dataSource);
        List<VariablePathExpression> variableFromPaths = contextualizePaths(fromPaths, dataSource);
        VariableJoinCondition result = new VariableJoinCondition(variableFromPaths, variableToPaths,
                joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return result;
    }

    private List<VariablePathExpression> contextualizePaths(List<PathExpression> pathExpressions, IDataSourceProxy dataSource) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (PathExpression pathExpression : pathExpressions) {
            result.add(pathExpression.getRelativePath(dataSource));
        }
        return result;
    }

    private VariableFunctionalDependency createDependencyFromKey(KeyConstraint keyConstraint, IDataSourceProxy dataSource) {
        SetAlias keyVariable = findKeyVariable(keyConstraint, dataSource);
        List<VariablePathExpression> leftPaths = contextualizePaths(keyConstraint.getKeyPaths(), dataSource);
        List<VariablePathExpression> rightPaths = keyVariable.getFirstLevelAttributes(dataSource.getIntermediateSchema());
        VariableFunctionalDependency variableDependency = new VariableFunctionalDependency(leftPaths, rightPaths);
        variableDependency.setKey(true);
        variableDependency.setPrimaryKey(keyConstraint.isPrimaryKey());
        return variableDependency;
    }

    private SetAlias findKeyVariable(KeyConstraint keyConstraint, IDataSourceProxy dataSource) {
        PathExpression firstKeyPath = keyConstraint.getKeyPaths().get(0);
        VariablePathExpression firstRelativePath = firstKeyPath.getRelativePath(dataSource);
        return firstRelativePath.getStartingVariable();
    }

    // SELECTION CONDITION
    public List<VariableSelectionCondition> contextualizeSelectionConditions(IDataSourceProxy dataSource) {
        List<VariableSelectionCondition> result = new ArrayList<VariableSelectionCondition>();
        for (SelectionCondition selectionCondition : dataSource.getSelectionConditions()) {
            result.add(contextualizeSelectionCondition(selectionCondition, dataSource));
        }
        return result;
    }

    public VariableSelectionCondition contextualizeSelectionCondition(SelectionCondition selectionCondition, IDataSourceProxy dataSource) {
        Expression newExpression = selectionCondition.getCondition().clone();
        replacePathsInExpression(newExpression, dataSource);
        return new VariableSelectionCondition(newExpression);
    }

    public List<SetAlias> findAliasesInVariableExpression(Expression expression) {
        List<SetAlias> result = new ArrayList<SetAlias>();
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            VariablePathExpression variablePath = (VariablePathExpression) variable.getDescription();
            SetAlias setVariable = variablePath.getStartingVariable();
            if (!result.contains(setVariable)) {
                result.add(setVariable);
            }
        }
        return result;
    }

    // CORRESPONDENCE
    public List<VariableCorrespondence> contextualizeCorrespondences(MappingTask mappingTask) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (ValueCorrespondence correspondence : mappingTask.getValueCorrespondences()) {
            result.add(contextualizeCorrespondence(mappingTask.getSourceProxy(), mappingTask.getTargetProxy(), correspondence));
        }
        return result;
    }

    public VariableCorrespondence contextualizeCorrespondence(IDataSourceProxy source, IDataSourceProxy target, ValueCorrespondence correspondence) {
        if (logger.isDebugEnabled()) logger.debug("\nContextualizing: " + correspondence);
        // target path
        VariablePathExpression targetRelativePath = generateRelativePath(correspondence.getTargetPath(), target);
        // source paths
        List<VariablePathExpression> sourceRelativePaths = null;
        ISourceValue newSourceValue = null;
        if (correspondence.getSourcePaths() != null) {
            sourceRelativePaths = new ArrayList<VariablePathExpression>();
            for (PathExpression sourceAbsolutePath : correspondence.getSourcePaths()) {
                VariablePathExpression sourceRelativePath = generateRelativePath(sourceAbsolutePath, source);
                sourceRelativePaths.add(sourceRelativePath);
            }
        } else {
            newSourceValue = (ISourceValue) correspondence.getSourceValue().clone();
        }
        // expressions
        Expression newTransformationFunction = correspondence.getTransformationFunction().clone();
        replacePathsInExpression(newTransformationFunction, source);
        // creation of the new correspondence
        VariableCorrespondence newCorrespondence = new VariableCorrespondence(sourceRelativePaths, newSourceValue, targetRelativePath, newTransformationFunction);
        if (logger.isDebugEnabled()) logger.debug("\nContextualized correspondence: " + newCorrespondence);
        return newCorrespondence;
    }

    private VariablePathExpression generateRelativePath(PathExpression absolutePath, IDataSourceProxy dataSource) {
        VariablePathExpression relativePath = absolutePath.getRelativePath(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Found relative path: " + relativePath);
        if (relativePath == null) {
            throw new IllegalArgumentException("Unable to contextualize correspondence with respect to source: " + absolutePath + " \n " + dataSource);
        }
        return relativePath;
    }

    private void replacePathsInExpression(Expression expression, IDataSourceProxy dataSource) {
        if (logger.isDebugEnabled()) logger.debug("Replacing paths in expression: " + expression);
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            PathExpression variableAbsolutePath = (PathExpression) variable.getDescription();
            VariablePathExpression variableRelativePath = generateRelativePath(variableAbsolutePath, dataSource);
            variable.setDescription(variableRelativePath);
            variable.setOriginalDescription(variableAbsolutePath);
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting expression: " + expression);
    }
    
}

