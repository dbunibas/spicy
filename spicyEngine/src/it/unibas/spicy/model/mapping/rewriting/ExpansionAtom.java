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

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.SetAlias;
import java.util.ArrayList;
import java.util.List;

public class ExpansionAtom {

    private SetAlias variable;
    private FORule tgd;
    private List<ExpansionFormulaPosition> positions;

    public ExpansionAtom(SetAlias variable, FORule tgd) {
        this.variable = variable;
        this.tgd = tgd;
    }

    public ExpansionAtom(ExpansionAtom originalAtom, SetAlias newVariable) {
        this.variable = newVariable;
        this.tgd = originalAtom.getTgd();
        this.positions = new ArrayList<ExpansionFormulaPosition>();
        for (ExpansionFormulaPosition originalPosition : originalAtom.getPositions()) {
            this.positions.add(new ExpansionFormulaPosition(originalPosition, newVariable));
        }
    }

    public void setPositions(List<ExpansionFormulaPosition> positions) {
        this.positions = positions;
    }

    public List<ExpansionFormulaPosition> getPositions() {
        return positions;
    }

    public FORule getTgd() {
        return tgd;
    }

    public SetAlias getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return variable.getAbsoluteBindingPathExpression().toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExpansionAtom)) {
            return false;
        }
        ExpansionAtom otherAtom = (ExpansionAtom)obj;
        return this.getVariable().equals(otherAtom.getVariable());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Atom: ").append(variable.toString());
        result.append(" - tgd: ").append(tgd.getId());
        result.append("-- positions: ").append(positions);
        return result.toString();
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append(variable.toString());
        return result.toString();
    }

}
