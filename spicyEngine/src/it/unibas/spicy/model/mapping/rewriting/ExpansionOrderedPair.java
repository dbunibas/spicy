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

import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.List;

public class ExpansionOrderedPair {

    private Expansion leftHandSideExpansion;
    private Expansion rightHandSideExpansion;
    private List<VariablePathExpression> leftPaths;
    private List<VariablePathExpression> rightPaths;
    private List<ExpansionJoin> additionalCyclicJoins;
    private String type;

    public ExpansionOrderedPair(Expansion leftHandSideExpansion, Expansion rightHandSideExpansion, List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths, String type) {
        this.leftHandSideExpansion = leftHandSideExpansion;
        this.rightHandSideExpansion = rightHandSideExpansion;
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
        this.type = type;
    }

    public List<VariablePathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<VariablePathExpression> getRightPaths() {
        return rightPaths;
    }

    public Expansion getLeftHandSideExpansion() {
        return leftHandSideExpansion;
    }

    public Expansion getRightHandSideExpansion() {
        return rightHandSideExpansion;
    }

    public List<ExpansionJoin> getAdditionalCyclicJoins() {
        return additionalCyclicJoins;
    }

    public void setAdditionalCyclicJoins(List<ExpansionJoin> additionalCyclicJoins) {
        this.additionalCyclicJoins = additionalCyclicJoins;
    }

//    public boolean equals(Object o) {
//        if (!(o instanceof TTExpansionSubsumption)) {
//            return false;
//        }
//        TTExpansionSubsumption otherSubsumption = (TTExpansionSubsumption)o;
//        boolean sameExpansion = expansion.equals(otherSubsumption.expansion);
//        boolean sameLeftPaths = SpicyEngineUtility.equalLists(leftPaths, otherSubsumption.leftPaths);
//        boolean sameRightPaths = SpicyEngineUtility.equalLists(rightPaths, otherSubsumption.rightPaths);
//        return sameExpansion && sameLeftPaths && sameRightPaths;
//    }

    public String getId() {
        return rightHandSideExpansion.getId() + " " + type + " than " + leftHandSideExpansion.getId();
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("----Expansion Ordered Pair---- (").append(type).append(")\n");
        result.append(rightHandSideExpansion.toString(indent));
        result.append(indent).append("----with difference paths:\n");
        result.append(indent).append(leftPaths).append("\n");
        result.append(indent).append(rightPaths).append("\n");
        if (additionalCyclicJoins != null && additionalCyclicJoins.size() > 0) {
            result.append(indent).append("----and additional cyclic joins:\n");
            result.append(indent).append(additionalCyclicJoins).append("\n");
        }
        return result.toString();
    }
}