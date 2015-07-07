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

import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import java.util.Stack;

public class VariableToString {

    public String toString(SetAlias variable) {
        VariableToStringVisitor visitor = new VariableToStringVisitor(true);
        variable.accept(visitor);
        return visitor.getResult();
    }

    public String toStringNoConditions(SetAlias variable) {
        VariableToStringVisitor visitor = new VariableToStringVisitor(false);
        variable.accept(visitor);
        return visitor.getResult();
    }
}

class VariableToStringVisitor implements IVariablePathVisitor {

    private boolean withConditions;
    private StringBuilder pathDescription = new StringBuilder();
    private Stack<StringBuilder> stack = new Stack<StringBuilder>();

    public VariableToStringVisitor(boolean withConditions) {
        this.withConditions = withConditions;
    }

    public void visitVariable(SetAlias v) {
        StringBuilder variableDescription = new StringBuilder();
        variableDescription.append(v.toShortString()).append(" in ").append(v.getBindingPathExpression().toString());
        if (withConditions && !v.getSelectionConditions().isEmpty()) {
            variableDescription.append(conditionsString(v));
        }
//        variableDescription.append(provenanceString(v));
        stack.push(variableDescription);
        v.getBindingPathExpression().accept(this);
    }

    private String conditionsString(SetAlias v) {
        StringBuilder result = new StringBuilder(" (where ");
        for (int i = 0; i < v.getSelectionConditions().size(); i++) {
            VariableSelectionCondition condition = v.getSelectionConditions().get(i);
            result.append(condition.getCondition());
            if (i != v.getSelectionConditions().size() - 1) {
                result.append(" and ");
            }
        }
        result.append(")");
        return result.toString();
    }

    private String provenanceString(SetAlias v) {
        if (v.getProvenanceCondition() == null) {
            return "";
        }
        StringBuilder result = new StringBuilder("");
        result.append(" [").append(v.getProvenanceCondition().getProvenance()).append("]");
        return result.toString();
    }

    public void visitPathExpression(VariablePathExpression pathExpression) {
        SetAlias startingVariable = pathExpression.getStartingVariable();
        if (startingVariable == null) {
            generateResult();
        } else {
            startingVariable.accept(this);
        }
    }

    private void generateResult() {
        while (!stack.isEmpty()) {
            if (!(pathDescription.length() == 0)) {
                pathDescription.append(", ");
            }
            pathDescription.append(stack.pop());
        }
    }

    public String getResult() {
        return pathDescription.toString();
    }
}
