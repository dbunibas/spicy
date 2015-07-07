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

import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class ExpansionElement implements Cloneable {

    private ExpansionAtom coveringAtom;
    private List<ExpansionAtom> originalAtoms = new ArrayList<ExpansionAtom>();
    // the following sets of paths are redundant, since they are replicated in the overall expansion
    private List<VariablePathExpression> originalUniversalPaths;
    private List<VariablePathExpression> matchingUniversalPaths;

    public ExpansionElement(ExpansionAtom originalAtom, ExpansionAtom coveringAtom) {
        this.originalAtoms.add(originalAtom);
        this.coveringAtom = coveringAtom;
    }

    public ExpansionElement(ExpansionElement originalCoverage, SetAlias newVariable) {
        this.originalAtoms = originalCoverage.originalAtoms;
        this.originalUniversalPaths = new ArrayList<VariablePathExpression>(originalCoverage.originalUniversalPaths);
        this.coveringAtom = new ExpansionAtom(originalCoverage.getCoveringAtom(), newVariable);
        this.matchingUniversalPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression originalMatchingPath : originalCoverage.matchingUniversalPaths) {
            this.matchingUniversalPaths.add(new VariablePathExpression(originalMatchingPath, newVariable));
        }
    }

    public ExpansionAtom getCoveringAtom() {
        return coveringAtom;
    }

    public ExpansionAtom getFirstOriginalAtom() {
        return originalAtoms.get(0);
    }

    public List<ExpansionAtom> getOriginalAtoms() {
        return originalAtoms;
    }

    public void addOriginalAtom(ExpansionAtom originalAtom) {
        this.originalAtoms.add(originalAtom);
    }

    public List<VariablePathExpression> getMatchingUniversalPaths() {
        return matchingUniversalPaths;
    }

    public void setMatchingUniversalPaths(List<VariablePathExpression> matchingPaths) {
        this.matchingUniversalPaths = matchingPaths;
    }

    public List<VariablePathExpression> getOriginalUniversalPaths() {
        return originalUniversalPaths;
    }

    public void setOriginalUniversalPaths(List<VariablePathExpression> originalPaths) {
        this.originalUniversalPaths = originalPaths;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExpansionElement)) {
            return false;
        }
        ExpansionElement otherAtom = (ExpansionElement)obj;
        if (!this.coveringAtom.equals(otherAtom.coveringAtom)) {
            return false;
        }
        if (this.originalAtoms.size() != otherAtom.originalAtoms.size()) {
            return false;
        }
        for (int i = 0; i < originalAtoms.size(); i++) {
            ExpansionAtom formulaAtom = originalAtoms.get(i);
            ExpansionAtom otherFormulaAtom = otherAtom.originalAtoms.get(i);
            if (!formulaAtom.getVariable().hasSameId(otherFormulaAtom.getVariable())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ExpansionElement clone() {
        try {
            ExpansionElement clone = (ExpansionElement) super.clone();
            clone.originalAtoms = new ArrayList<ExpansionAtom>(originalAtoms);
            clone.originalUniversalPaths = new ArrayList<VariablePathExpression>(originalUniversalPaths);
            clone.matchingUniversalPaths = new ArrayList<VariablePathExpression>(matchingUniversalPaths);
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("----------------Atom coverage--------------\n");
        result.append(indent).append("Covering atom: ").append(coveringAtom).append("\n");
        if (originalAtoms.size() == 1) {
            result.append(indent).append("---original atom: ").append(originalAtoms.get(0)).append("\n");
        } else {
            result.append(indent).append("---original atoms: ").append("\n");
            for (int i = 0; i < originalAtoms.size(); i++) {
                result.append(indent).append(originalAtoms.get(i)).append("\n");
            }
        }
        result.append(indent).append("---original universal paths:").append(originalUniversalPaths).append("\n");
        result.append(indent).append("---covering universal paths:").append(matchingUniversalPaths).append("\n");
        result.append(indent).append("--------------------------------------------\n");
        return result.toString();
    }

    public String toShortString() {
        return toShortString("");
    }

    public String toShortString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append(coveringAtom.toShortString());
        result.append("[");
        for (ExpansionAtom originalAtom : originalAtoms) {
            result.append(originalAtom.toShortString()).append("-");
        }
        result.append("]");
        result.append("---covering universal paths:").append(matchingUniversalPaths);
        return result.toString();
    }
}

