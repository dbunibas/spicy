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
 
package it.unibas.spicy.test.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FactValue implements IExpectedValue {

    private static Log logger = LogFactory.getLog(FactValue.class);

    public final String NULL = "NULL";
    public final String SKOLEM = "N_";

    private String value;

    public FactValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isUniversal() {
        return !isNullOrSkolem();
    }

    public boolean isNull() {
        return value.equalsIgnoreCase(NULL);
    }

    public boolean isSkolem() {
        return value.startsWith(SKOLEM);
    }

    public boolean isNullOrSkolem() {
        return isNull() || isSkolem();

    }

    public boolean equals(Object obj) {
        String valueString = obj.toString();
        if (valueString.charAt(0) == '\"') {
            valueString = valueString.substring(1);
        }
        if (valueString.charAt(valueString.length() - 1) == '\"') {
            valueString = valueString.substring(0, valueString.length() - 1);
        }
        return this.getValue().equals(valueString);
    }

    public boolean equalsSkolem(IExpectedValue otherValue) {
        return this.isSkolem() && otherValue.isNullOrSkolem() && this.value.equals(otherValue.getValue());
    }

    public String toString() {
        return value;
    }

}
