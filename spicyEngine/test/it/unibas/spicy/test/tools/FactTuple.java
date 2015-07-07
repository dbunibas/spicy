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

import java.util.ArrayList;
import java.util.List;

public class FactTuple implements IExpectedTuple {

    private String relationName;
    private String oid;
    private String fatherOid;
    private List<FactValue> values = new ArrayList<FactValue>();

    public FactTuple(String relationName) {
        this.relationName = relationName;
    }

    public void addValue(IExpectedValue value) {
        this.values.add((FactValue)value);
    }

    public String getFatherOid() {
        return fatherOid;
    }

    public String getOid() {
        return oid;
    }

    public String getRelationName() {
        return relationName;
    }

    public List<IExpectedValue> getValues() {
        return new ArrayList<IExpectedValue>(values);
    }

    public void setFatherOid(String fatherOid) {
        this.fatherOid = fatherOid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean containsNullOrSkolem() {
        for (FactValue factValue : values) {
            if (factValue.isNullOrSkolem()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> containsSameSkolem(IExpectedValue otherValue) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i++) {
            IExpectedValue factValue = values.get(i);
            if (factValue.equalsSkolem(otherValue)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(relationName).append("(");
        for (int i = 0; i < values.size(); i++) {
            FactValue factValue = values.get(i);
            result.append(factValue);
            if (i != values.size() - 1) result.append(",");
        }
        result.append(")");
        return result.toString();
    }

}
