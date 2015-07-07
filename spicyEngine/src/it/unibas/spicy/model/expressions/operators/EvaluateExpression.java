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
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.expressions.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class EvaluateExpression {

    private static Log logger = LogFactory.getLog(EvaluateExpression.class);

    public Object evaluateFunction(Expression expression, INode tuple) throws ExpressionSyntaxException {
        if (logger.isDebugEnabled()) logger.debug("Evaluating function: " + expression + " on tuple " + tuple);
        setVariableValues(expression, tuple);
        Object value = expression.getJepExpression().getValueAsObject();
        if (expression.getJepExpression().hasError()) {
            throw new ExpressionSyntaxException(expression.getJepExpression().getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of function: " + value);
        return value;
    }

    public Double evaluateCondition(Expression expression, INode tuple) throws ExpressionSyntaxException {
        if (logger.isDebugEnabled()) logger.debug("Evaluating condition: " + expression + " on tuple " + tuple);
        if (expression.toString().equals("true")) {
            return SpicyEngineConstants.TRUE;
        }
        setVariableValues(expression, tuple);
        Object value = expression.getJepExpression().getValueAsObject();
        if (expression.getJepExpression().hasError()) {
            throw new ExpressionSyntaxException(expression.getJepExpression().getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of condition: " + value);
        try {
            Double result = Double.parseDouble(value.toString());
            return result;
        } catch (NumberFormatException numberFormatException) {
            logger.error(numberFormatException);
            throw new ExpressionSyntaxException(numberFormatException.getMessage());
        }
    }

    private void setVariableValues(Expression expression, INode tuple) {
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable jepVariable : symbolTable.getVariables()) {
            String variablePathString = jepVariable.getDescription().toString();
            Object variableValue = findAttributeValue(tuple, variablePathString);
            assert (variableValue != null) : "Value of variable: " + jepVariable + " is null in tuple " + tuple;
            if (logger.isTraceEnabled()) logger.trace("Setting var value: " + jepVariable.getDescription() + " = " + variableValue);
            jepExpression.setVarValue(jepVariable.getName(), variableValue);
        }
    }

    private Object findAttributeValue(INode tuple, String attributeLabel) {
        if (logger.isTraceEnabled()) logger.trace("Searching variable with description: " + attributeLabel + " in tuple " + tuple);
        INode attribute = tuple.getChild(attributeLabel);
        if (attribute == null) {
            throw new IllegalArgumentException("Unable to find attribute: " + attributeLabel + " in tuple " + tuple);
        }
        INode leaf = attribute.getChild(0);
        return leaf.getValue();
    }
}
