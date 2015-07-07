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

import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class FormulaPosition {

    private FormulaAtom atom;
    private VariablePathExpression pathExpression;
    private VariableCorrespondence correspondence;
    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();

    public FormulaPosition(FormulaAtom atom, VariablePathExpression pathExpression) {
        this.atom = atom;
        this.pathExpression = pathExpression;
    }

    public FormulaAtom getAtom() {
        return atom;
    }

    public VariablePathExpression getPathExpression() {
        return pathExpression;
    }

    public VariableCorrespondence getCorrespondence() {
        return correspondence;
    }

    public void setCorrespondence(VariableCorrespondence correspondence) {
        this.correspondence = correspondence;
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public boolean isUniversal() {
        return correspondence != null;
    }

    public boolean isSkolem() {
        return !isUniversal() && !joinConditions.isEmpty();
    }

    public boolean isNull() {
        return correspondence == null && joinConditions.isEmpty();
    }

    public boolean isSkolemOrNull() {
        return isSkolem() || isNull();
    }

    public String toString() {
        String result = pathExpression.toString();
        if (isUniversal()) {
            result +=  "[universal]";
        }
        if (isSkolem()) {
            result +=  "[skolem]";
        }
        if (isNull()) {
            result +=  "[null]";
        }
        return result;
    }

}
