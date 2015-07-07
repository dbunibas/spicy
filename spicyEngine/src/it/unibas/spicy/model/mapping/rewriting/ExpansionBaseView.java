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
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import java.util.List;

public class ExpansionBaseView implements Comparable<ExpansionBaseView> {

    private FORule stTgd;
    private SimpleConjunctiveQuery view;
    private List<ExpansionAtom> formulaAtoms;

    public ExpansionBaseView(FORule stTgd, SimpleConjunctiveQuery view) {
        this.stTgd = stTgd;
        this.view = view;
    }

    public FORule getStTgd() {
        return stTgd;
    }

    public SimpleConjunctiveQuery getView() {
        return view;
    }

    public List<ExpansionAtom> getFormulaAtoms() {
        return formulaAtoms;
    }

    public void setFormulaAtoms(List<ExpansionAtom> formulaAtoms) {
        this.formulaAtoms = formulaAtoms;
    }

    public int compareTo(ExpansionBaseView otherView) {
        return this.stTgd.getTargetView().getVariables().size() -
                otherView.stTgd.getTargetView().getVariables().size();
    }

    public String getId() {
        return this.view.toVariableString();
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Base View (STTGD: ").append(stTgd.getId()).append("):\n");
        result.append(view.toString(indent)).append("\n");
        return result.toString();
    }
}
