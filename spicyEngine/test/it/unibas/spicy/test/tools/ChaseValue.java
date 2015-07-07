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

public class ChaseValue implements IExpectedValue {

    String value;
    String id;
    String type;

    public ChaseValue(String value, String id, String type) {
        this.value = value;
        this.id = id;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public boolean isUniversal() {
        return (!value.equals("NULL"));
    }

    public boolean isNullOrSkolem() {
        return value.equals("NULL");
    }

    public boolean equalsSkolem(IExpectedValue otherValue) {
        ChaseValue otherViewValue = (ChaseValue)otherValue;
        return (this.id.equals(otherViewValue.id) && this.type.equals(otherViewValue.type));
    }

    public boolean equals(Object obj) {
        return this.getValue().equals(obj.toString());
    }

    public String toLongString() {
        return value + "[" + id + "][" + type + "]";
    }

    public String toString() {
        if (isUniversal()) {
            return value;
        }
        return "[" + type + "]";
    }


}
