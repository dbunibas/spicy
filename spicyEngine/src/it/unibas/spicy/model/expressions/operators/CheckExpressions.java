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
package it.unibas.spicy.model.expressions.operators;

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.correspondence.ConstantValue;
import it.unibas.spicy.model.correspondence.DateFunction;
import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.correspondence.NewIdFunction;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class CheckExpressions {

    private static Log logger = LogFactory.getLog(CheckExpressions.class);
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    // SELECTION CONDITIONS
    public void checkSelectionCondition(List<PathExpression> setPaths, Expression condition, INode root) throws ExpressionSyntaxException {
        if (condition == null) {
            throw new ExpressionSyntaxException("Transformation function cannot be null. Set path: " + setPaths);
        }
        StringBuilder errorMessage = new StringBuilder();
        JEP jepExpression = condition.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            String variableName = variable.getName();
            PathExpression variablePath = searchPathInSet(setPaths, variableName, root);
            if (variablePath == null) {
                errorMessage.append("Unable to find path for variable ").append(variableName).append("\n");
            } else {
                variable.setDescription(variablePath);
                variable.setOriginalDescription(variablePath);
            }
        }
        if (errorMessage.length() != 0) {
            throw new ExpressionSyntaxException(errorMessage.toString());
        }
    }

    private PathExpression searchPathInSet(List<PathExpression> setPaths, String variableName, INode root) {
        for (PathExpression setPath : setPaths) {
            List<PathExpression> childPaths = pathGenerator.generateFirstLevelChildrenAbsolutePaths((SetNode) (setPath.getLastNode(root)));
            for (PathExpression childPath : childPaths) {
                if (childPath.toString().endsWith(variableName)) {
                    return childPath;
                }
            }
        }
        return null;
    }

    public void checkExpression(List<PathExpression> sourcePaths, Expression expression) throws ExpressionSyntaxException {
        if (expression == null) {
            throw new ExpressionSyntaxException("Transformation function cannot be null. Source paths: " + sourcePaths);
        }
        StringBuilder errorMessage = new StringBuilder();
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            String variableName = variable.getName();
            PathExpression variablePath = searchPath(sourcePaths, variableName);
            if (variablePath == null) {
                errorMessage.append("Unable to find path for variable ").append(variableName).append("\n");
            } else {
                variable.setDescription(variablePath);
                variable.setOriginalDescription(variablePath);
            }
        }
        if (errorMessage.length() != 0) {
            throw new ExpressionSyntaxException(errorMessage.toString());
        }
    }

    private PathExpression searchPath(List<PathExpression> sourcePaths, String variableName) {
        for (PathExpression pathExpression : sourcePaths) {
            if (pathExpression.toString().endsWith(variableName)) {
                return pathExpression;
            }
        }
        return null;
    }

    public void checkTransformationFunction(ISourceValue sourceValue, Expression transformationFunction) throws ExpressionSyntaxException {
        if (!transformationFunction.equals(checkSourceValueAndGenerateExpression(sourceValue))) {
            throw new ExpressionSyntaxException("Transformation function does not match source value: " + sourceValue + " - " + transformationFunction);
        }
    }

    public Expression checkSourceValueAndGenerateExpression(ISourceValue sourceValue) throws ExpressionSyntaxException {
        if (sourceValue == null) {
            throw new ExpressionSyntaxException("Source value cannot be null");
        }
        String sourceValueString = sourceValue.toString();
        if (sourceValueString.equals(SpicyEngineConstants.SOURCEVALUE_DATE_FUNCTION)) {
            if (!(sourceValue instanceof DateFunction)) {
                throw new ExpressionSyntaxException("Source value of the wrong type (must be a DateFunction): " + sourceValue);
            }
        } else if (sourceValueString.equals(SpicyEngineConstants.SOURCEVALUE_NEWID_FUNCTION)) {
            if (!(sourceValue instanceof NewIdFunction)) {
                throw new ExpressionSyntaxException("Source value of the wrong type (must be a NewIdFunction): " + sourceValue);
            }
        } else { //constant
            if (!(sourceValue instanceof ConstantValue)) {
                throw new ExpressionSyntaxException("Source value of the wrong type (must be a ConstantValue): " + sourceValue);
            }
            if (!(sourceValueString.startsWith("\"")) || !(sourceValueString.endsWith("\""))) {
                try {
                    Double.parseDouble(sourceValueString);
                } catch (NumberFormatException numberFormatException) {
                    throw new ExpressionSyntaxException("String values must be enclosed in quotes: " + sourceValue);
                }
            }
        }
        return new Expression(sourceValueString);
    }

    // FILTERS
    public Expression checkFilter(IDataSourceProxy dataSource, Expression filter) throws ExpressionSyntaxException {
        if (logger.isDebugEnabled()) logger.debug("Checking filter: " + filter);
        Expression contextualizedExpression = findPaths(dataSource, filter);
        if (contextualizedExpression == null) {
            throw new ExpressionSyntaxException("Unable to find a logical relation that covers filter: " + filter);
        }
        if (logger.isDebugEnabled()) logger.debug("Returning new filter: " + contextualizedExpression);
        return contextualizedExpression;
    }

    private Expression findPaths(IDataSourceProxy dataSource, Expression filter) {
        for (SimpleConjunctiveQuery view : dataSource.getMappingData().getViews()) {
            Expression clone = filter.clone();
            if (containsAllVariables(view, clone, dataSource.getIntermediateSchema())) {
                return clone;
            }
        }
        return null;
    }

    private boolean containsAllVariables(SimpleConjunctiveQuery view, Expression filter, INode root) {
        JEP jepExpression = filter.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (org.nfunk.jep.Variable jepVariable : symbolTable.getVariables()) {
            String variableName = jepVariable.getName();
            PathExpression attributePath = searchPathInView(view, variableName, root);
            if (attributePath == null) {
                return false;
            } else {
                jepVariable.setDescription(attributePath);
                jepVariable.setOriginalDescription(attributePath);
            }
        }
        return true;
    }

    private PathExpression searchPathInView(SimpleConjunctiveQuery view, String jepVariableName, INode root) {
        if (logger.isDebugEnabled()) logger.debug("Variable: " + jepVariableName);
        for (VariablePathExpression relativePathExpression : view.getAttributePaths(root)) {
            PathExpression absolutePath = relativePathExpression.getAbsolutePath();
            if (logger.isDebugEnabled()) logger.debug("Path expression: " + absolutePath);
            if (absolutePath.toString().endsWith(jepVariableName)) {
                if (logger.isDebugEnabled()) logger.debug("OK");
                return relativePathExpression;
            }
        }
        return null;
    }
    
    
}
