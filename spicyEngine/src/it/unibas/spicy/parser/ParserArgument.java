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
 
package it.unibas.spicy.parser;

import it.unibas.spicy.model.paths.PathExpression;

public class ParserArgument implements Cloneable {

    private String variable;
    private Object value;
    private PathExpression setPath;
    private PathExpression attributePath;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public Object getValue() {
        return value;
    }

    public String getVariable() {
        return variable;
    }

    public void setAttributePath(PathExpression attributePath) {
        this.attributePath = attributePath;
    }

    public PathExpression getAttributePath() {
        return attributePath;
    }

    public PathExpression getSetPath() {
        return setPath;
    }

    public void setSetPath(PathExpression setPath) {
        this.setPath = setPath;
    }

    public boolean isVariable() {
        return this.variable != null;
    }

    public ParserArgument clone() {
        try {
            return (ParserArgument) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        String printValue = "";
        if (this.variable != null) {
            printValue = "$" + this.variable;
        } else {
            printValue = this.value + "";
        }
        return printValue;
    }

}

