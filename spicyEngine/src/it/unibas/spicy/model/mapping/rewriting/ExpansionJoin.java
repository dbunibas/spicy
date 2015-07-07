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
 
package it.unibas.spicy.model.mapping.rewriting;

import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpansionJoin implements Cloneable {

    private VariableJoinCondition joinCondition;
    private ExpansionElement fromElement;
    private ExpansionElement toElement;
    private boolean selfJoin;
    private boolean joinOnSkolems;

    public ExpansionJoin(VariableJoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public ExpansionJoin(ExpansionJoin oldExpansionJoin, VariableJoinCondition newJoinCondition) {
        this.joinCondition = newJoinCondition;
        this.selfJoin = oldExpansionJoin.selfJoin;
        this.joinOnSkolems = oldExpansionJoin.joinOnSkolems;
    }

    public VariableJoinCondition getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(VariableJoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public boolean isJoinOnSkolems() {
        return joinOnSkolems;
    }

    public boolean isJoinOnUniversal() {
        return !joinOnSkolems;
    }

    public void setJoinOnSkolems(boolean joinOnSkolems) {
        this.joinOnSkolems = joinOnSkolems;
    }

    public boolean isSelfJoin() {
        return selfJoin;
    }

    public void setSelfJoin(boolean selfJoin) {
        this.selfJoin = selfJoin;
    }

    public ExpansionElement getFromElement() {
        return fromElement;
    }

    public void setFromElement(ExpansionElement fromElement) {
        this.fromElement = fromElement;
    }

    public ExpansionElement getToElement() {
        return toElement;
    }

    public void setToElement(ExpansionElement toElement) {
        this.toElement = toElement;
    }

    public ExpansionJoin clone() {
        try {
            ExpansionJoin clone = (ExpansionJoin) super.clone();
            clone.joinCondition = this.joinCondition.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ExpansionJoin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String toString() {
        StringBuilder result =  new StringBuilder(joinCondition.toString());
        if (selfJoin) {
            result.append(" - [selfjoin]");
        }
        if (joinOnSkolems) {
            result.append(" - [onSkolems]");
        }
        return result.toString();
    }

    public String toLongString() {
        StringBuilder result =  new StringBuilder(joinCondition.toString());
        result.append("from element").append(fromElement).append("\n");
        result.append("to element").append(toElement).append("\n");
        if (selfJoin) {
            result.append(" - [selfjoin]");
        }
        if (joinOnSkolems) {
            result.append(" - [onSkolems]");
        }
        return result.toString();
    }
}
