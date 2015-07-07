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
 
package it.unibas.spicy.model.mapping.rewriting;

import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;

public class ExpansionFormulaPosition {

    private VariablePathExpression pathExpression;
    private boolean universal;
    private boolean skolem;

    public ExpansionFormulaPosition(VariablePathExpression pathExpression) {
        this.pathExpression = pathExpression;
    }

    public ExpansionFormulaPosition(ExpansionFormulaPosition originalPosition, SetAlias newVariable) {
        this.pathExpression = new VariablePathExpression(originalPosition.getPathExpression(), newVariable);
        this.universal = originalPosition.universal;
        this.skolem = originalPosition.skolem;
    }

    public VariablePathExpression getPathExpression() {
        return pathExpression;
    }

    public boolean isSkolem() {
        return skolem;
    }

    public void setSkolem(boolean skolem) {
        this.skolem = skolem;
    }

    public boolean isUniversal() {
        return universal;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

    public boolean isNull() {
        return !universal && !skolem;
    }

    public boolean isSkolemOrNull() {
        return isSkolem() || isNull();
    }

    public String toString() {
        String result = pathExpression.toString();
        if (isUniversal()) {
            result +=  "[universal]";
        }
        if (isSkolem()) {
            result +=  "[skolem]";
        }
        if (isNull()) {
            result +=  "[null]";
        }
        return result;
    }

}
