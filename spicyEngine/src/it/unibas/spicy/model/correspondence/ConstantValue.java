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
 
package it.unibas.spicy.model.correspondence;

public class ConstantValue extends AbstractSourceValue {

    private Object value;

    public ConstantValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ConstantValue)) {
            return false;
        }
        ConstantValue otherValue = (ConstantValue) o;
        return this.value.equals(otherValue.value);
    }

}
