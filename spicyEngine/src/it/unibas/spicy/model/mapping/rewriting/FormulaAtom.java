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
import java.util.List;

public class FormulaAtom {

    private SetAlias variable;
    private FORule tgd;
    private List<FormulaPosition> positions;

    public FormulaAtom(SetAlias variable, FORule tgd) {
        this.variable = variable;
        this.tgd = tgd;
    }

    public void setPositions(List<FormulaPosition> positions) {
        this.positions = positions;
    }

    public List<FormulaPosition> getPositions() {
        return positions;
    }

    public FORule getTgd() {
        return tgd;
    }

    public SetAlias getVariable() {
        return variable;
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
