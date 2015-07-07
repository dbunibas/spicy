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

import java.util.ArrayList;
import java.util.List;

public class ParserFD {

    private String set;
    private List<String> leftAttributes = new ArrayList<String>();
    private List<String> rightAttributes = new ArrayList<String>();
    private boolean primaryKey;
    private boolean key;

    public ParserFD(String set) {
        this.set = set;
    }

    public String getSet() {
        return set;
    }

    public List<String> getLeftAttributes() {
        return leftAttributes;
    }

    public List<String> getRightAttributes() {
        return rightAttributes;
    }

    public void setLeftAttributes(List<String> leftAttributes) {
        this.leftAttributes = leftAttributes;
    }

    public void setRightAttributes(List<String> rightAttributes) {
        this.rightAttributes = rightAttributes;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String toString() {
        String result = set + ":";
        for (int i = 0; i < leftAttributes.size(); i++) {
            result += leftAttributes.get(i);
            if (i != leftAttributes.size() - 1) {
                result += ", ";
            }
        }
        result += " --> ";
        for (int i = 0; i < rightAttributes.size(); i++) {
            result += rightAttributes.get(i);
            if (i != rightAttributes.size() - 1) {
                result += ", ";
            }
        }
        if (this.key) {
            result += " [key]";
        }
        if (this.key) {
            result += " [primary key]";
        }
        return result;
    }
}
