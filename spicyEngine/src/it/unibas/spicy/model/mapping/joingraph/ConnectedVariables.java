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

import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.ArrayList;
import java.util.List;

public class ConnectedVariables {

    private List<SetAlias> variables = new ArrayList<SetAlias>();
    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();

    public ConnectedVariables(SetAlias variable) {
        this.variables.add(variable);
    }

    public List<SetAlias> getVariables() {
        return variables;
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addVariable(SetAlias variable) {
        this.variables.add(variable);
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("VARIABLES: [");
        for (SetAlias variable : variables) {
            result.append(variable.toShortString()).append(" ");
        }
        result.append("] - ");
        result.append("JOINS: [");
        for (VariableJoinCondition joinCondition : joinConditions) {
            result.append(joinCondition.toString()).append(" ");
        }
        result.append("]");
        return result.toString();
    }
}
