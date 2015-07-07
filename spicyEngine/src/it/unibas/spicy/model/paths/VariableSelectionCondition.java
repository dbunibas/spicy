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
 
package it.unibas.spicy.model.paths;

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VariableSelectionCondition implements Cloneable {

    private static Log logger = LogFactory.getLog(VariableSelectionCondition.class);
 
    protected Expression condition;
 
    public VariableSelectionCondition() {}

    public VariableSelectionCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public List<SetAlias> getSetVariables() {
        return new ContextualizePaths().findAliasesInVariableExpression(condition);
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof VariableSelectionCondition)) {
            return false;
        }
        VariableSelectionCondition other = (VariableSelectionCondition) object;
        boolean result = this.condition.equals(other.getCondition());
        return result;
    }

    public boolean equalsUpToVariableIds(Object object) {
        if (!(object instanceof VariableSelectionCondition)) {
            return false;
        }
        VariableSelectionCondition other = (VariableSelectionCondition) object;
        boolean result = this.condition.equalsUpToVariableIds(other.getCondition());
        return result;
    }

    public VariableSelectionCondition clone() {
        try {
            VariableSelectionCondition clone = (VariableSelectionCondition) super.clone();
            clone.condition = condition.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(condition);
        return result.toString();
    }

}
