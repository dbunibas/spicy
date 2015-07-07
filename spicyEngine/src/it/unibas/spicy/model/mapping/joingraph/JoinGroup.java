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
 
package it.unibas.spicy.model.mapping.joingraph;

import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class JoinGroup implements Cloneable {

    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();

    public JoinGroup(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public boolean equals(Object o) {
        if (!(o instanceof JoinGroup)) {
            return false;
        }
        JoinGroup other = (JoinGroup) o;
        return SpicyEngineUtility.equalLists(this.joinConditions, other.joinConditions);
    }

    public JoinGroup clone() {
        try {
            JoinGroup clone = (JoinGroup) super.clone();
            clone.joinConditions = new ArrayList<VariableJoinCondition>();
            for (VariableJoinCondition variableJoinCondition : joinConditions) {
                clone.joinConditions.add(variableJoinCondition.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder("-------------Join Group------------\n");
        for (VariableJoinCondition variableJoinCondition : joinConditions) {
            result.append(variableJoinCondition).append("\n");
        }
        result.append("-------------------------\n");
        return result.toString();
    }

}
