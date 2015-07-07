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

public class ChaseTuple implements IExpectedTuple {

    private String relationName;
    private String oid;
    private String fatherOid;
    private List<ChaseValue> values = new ArrayList<ChaseValue>();

    public ChaseTuple(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getFatherOid() {
        return fatherOid;
    }

    public void setFatherOid(String fatherOid) {
        this.fatherOid = fatherOid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void addValue(IExpectedValue value) {
        values.add((ChaseValue)value);
    }

    public List<IExpectedValue> getValues() {
        return new ArrayList<IExpectedValue>(values);
    }

    public boolean containsNullOrSkolem() {
        for (ChaseValue chaseValue : values) {
            if (chaseValue.isNullOrSkolem()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> containsSameSkolem(IExpectedValue otherValue) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i++) {
            IExpectedValue chaseValue = values.get(i);
            if (chaseValue.equalsSkolem(otherValue)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[").append(relationName).append("]");
        result.append("(");
        for (int i = 0; i < values.size(); i++) {
            ChaseValue tupleValue = values.get(i);
            result.append(tupleValue);
            if (i != values.size() - 1) result.append(",");
        }
        result.append(")");
        return result.toString();
    }
}
