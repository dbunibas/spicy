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

public class JoinEdge {

    private SetAlias sourceVariable;
    private SetAlias destinationVariable;
    private VariableJoinCondition joinCondition;

    public JoinEdge(SetAlias sourceVariable, SetAlias destinationVariable, VariableJoinCondition joinCondition) {
        this.sourceVariable = sourceVariable;
        this.destinationVariable = destinationVariable;
        this.joinCondition = joinCondition;
    }

    public SetAlias getDestinationVariable() {
        return destinationVariable;
    }

    public VariableJoinCondition getJoinCondition() {
        return joinCondition;
    }

    public SetAlias getSourceVariable() {
        return sourceVariable;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JoinEdge)) {
            return false;
        }
        JoinEdge otherEdge = (JoinEdge)obj;
        return sourceVariable.equals(otherEdge.sourceVariable) && 
                destinationVariable.equals(otherEdge.destinationVariable) &&
                joinCondition.equals(otherEdge.joinCondition);
    }

    public String toString() {
        return "Join edge: " + sourceVariable + " -> " + destinationVariable + "(join condition: " + joinCondition + ")";
    }

}