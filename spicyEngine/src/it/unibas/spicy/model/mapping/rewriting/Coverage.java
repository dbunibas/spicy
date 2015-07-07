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
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class Coverage implements Cloneable {

    private List<CoverageAtom> atomCoverages;
    private List<VariableJoinCondition> targetJoinConditions;
    private List<VariableJoinCondition> targetCyclicJoinConditions;

    public Coverage(List<CoverageAtom> atomCoverages, List<VariableJoinCondition> joinConditions, List<VariableJoinCondition> cyclicJoinConditions) {
        this.atomCoverages = atomCoverages;
        this.targetJoinConditions = joinConditions;
        this.targetCyclicJoinConditions = cyclicJoinConditions;
    }

    public List<CoverageAtom> getCoveringAtoms() {
        return atomCoverages;
    }

    public void setAtomCoverages(List<CoverageAtom> atoms) {
        this.atomCoverages = atoms;
    }

    public List<VariableJoinCondition> getTargetJoinConditions() {
        return targetJoinConditions;
    }

    public List<VariableJoinCondition> getTargetCyclicJoinConditions() {
        return targetCyclicJoinConditions;
    }

    @Override
    public Coverage clone() {
        try {
            Coverage clone = (Coverage) super.clone();
            clone.atomCoverages = new ArrayList<CoverageAtom>(atomCoverages);
            clone.targetJoinConditions = new ArrayList<VariableJoinCondition>(targetJoinConditions);
            clone.targetCyclicJoinConditions = new ArrayList<VariableJoinCondition>(targetCyclicJoinConditions);
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("--------------Coverage----------------\n");
        for (CoverageAtom atomCoverage : atomCoverages) {
            result.append(atomCoverage.toString(indent));
        }
        result.append(indent).append("--- joins: \n");
        result.append(SpicyEngineUtility.printCollection(targetJoinConditions, indent));
        result.append(indent).append("--------------------------------------\n");
        if (targetCyclicJoinConditions.size() > 0) {
            result.append(indent).append("--- cyclic joins: \n");
            result.append(SpicyEngineUtility.printCollection(targetJoinConditions, indent));
            result.append(indent).append("--------------------------------------\n");
        }
        return result.toString();
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        for (CoverageAtom atomCoverage : atomCoverages) {
            result.append(atomCoverage.toShortString()).append("+");
        }
        return result.toString();
    }
}