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

public class ParserBuiltinOperator implements Cloneable {

    private String operator;
    private ParserArgument firstArgument;
    private ParserArgument secondArgument;

    public ParserArgument getFirstArgument() {
        return firstArgument;
    }

    public void setFirstArgument(ParserArgument firstArgument) {
        this.firstArgument = firstArgument;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public ParserArgument getSecondArgument() {
        return secondArgument;
    }

    public void setSecondArgument(ParserArgument secondArgument) {
        this.secondArgument = secondArgument;
    }

    public ParserBuiltinOperator clone() {
        try {
            ParserBuiltinOperator clone = (ParserBuiltinOperator) super.clone();
            clone.firstArgument = this.firstArgument.clone();
            clone.secondArgument = this.secondArgument.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(firstArgument);
        result.append(" ").append(operator).append(" ");
        result.append(secondArgument);
        return result.toString();
    }

    public String toExpressionString() {
        StringBuilder result = new StringBuilder();
        if (firstArgument.isVariable()) {
            result.append(firstArgument.getAttributePath());
        } else {
            result.append(firstArgument);
        }
        result.append(" ").append(operator).append(" ");
        if (secondArgument.isVariable()) {
            result.append(secondArgument.getAttributePath());
        } else {
            result.append(secondArgument);
        }
        return result.toString();
    }
}
