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
package it.unibas.spicy.model.expressions;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.IExpressionVisitor;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class Expression implements Cloneable {

    private static Log logger = LogFactory.getLog(Expression.class);
    private JEP jepExpression;

    public Expression(String expression) throws ExpressionSyntaxException {
        jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression(expression);
        if (jepExpression.hasError()) {
            throw new ExpressionSyntaxException(jepExpression.getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Created expression: " + jepExpression);
    }

    public JEP getJepExpression() {
        return jepExpression;
    }

    public void accept(IExpressionVisitor visitor) {
        if (this.jepExpression != null) {
            jepExpression.getTopNode().accept(visitor);
        }
    }

    public List<VariablePathExpression> getAttributePaths() {
        List<VariablePathExpression> attributePaths = new ArrayList<VariablePathExpression>();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable jepVariable : symbolTable.getVariables()) {
            if (!attributePaths.contains(jepVariable.getDescription())) {
                attributePaths.add((VariablePathExpression) jepVariable.getDescription());
            }
        }
        return attributePaths;
    }

    public Expression clone() {
        Expression clone = null;
        try {
            clone = (Expression) super.clone();
            clone.jepExpression = (JEP) this.jepExpression.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    public String toString() {
        return this.jepExpression.toString();
    }

    public String toSaveString() {
        return this.jepExpression.toStringWithDollars();
    }

    public String toStringWithAbsolutePaths() {
        return this.jepExpression.toStringWithAbsolutePaths();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }

    public boolean equalsUpToVariableIds(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }
    private static Expression trueExpression = new Expression("true");

    public static Expression getTrueExpression() {
        return trueExpression;
    }
}
