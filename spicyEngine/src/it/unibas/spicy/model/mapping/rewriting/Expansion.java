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

import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class Expansion implements Comparable<Expansion>, Cloneable {

    private ExpansionBaseView baseView;
    private List<ExpansionElement> expansionElements;
    private List<ExpansionJoin> joinConditions;
    private List<ExpansionJoin> cyclicJoinConditions;
    private List<VariablePathExpression> originalUniversalPaths;
    private List<VariablePathExpression> matchingUniversalPaths;
    private boolean base;
    // rewriting
    private ComplexQueryWithNegations sourceRewriting;
    private ComplexQueryWithNegations rew_c;
    private ComplexQueryWithNegations rew_i;

    public Expansion(ExpansionBaseView baseView, ExpansionElement expansionElement) {
        this.baseView = baseView;
        this.expansionElements = new ArrayList<ExpansionElement>();
        this.expansionElements.add(expansionElement);
        this.joinConditions = new ArrayList<ExpansionJoin>();
        this.cyclicJoinConditions = new ArrayList<ExpansionJoin>();
    }

    public Expansion(ExpansionBaseView baseView, List<ExpansionElement> coveringAtoms, List<ExpansionJoin> joinConditions, List<ExpansionJoin> cyclicJoinConditions) {
        this.baseView = baseView;
        this.expansionElements = coveringAtoms;
        this.joinConditions = joinConditions;
        this.cyclicJoinConditions = cyclicJoinConditions;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public ExpansionBaseView getBaseView() {
        return baseView;
    }

    public void setBaseView(ExpansionBaseView baseView) {
        this.baseView = baseView;
    }

    public List<ExpansionElement> getExpansionElements() {
        return expansionElements;
    }

    public void setCoveringAtoms(List<ExpansionElement> coveringAtoms) {
        this.expansionElements = coveringAtoms;
    }

    public List<ExpansionJoin> getJoinConditions() {
        return joinConditions;
    }

    public List<VariableJoinCondition> getUniversalJoinConditions() {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (ExpansionJoin expansionJoin : joinConditions) {
            if (expansionJoin.isJoinOnUniversal()) {
                result.add(expansionJoin.getJoinCondition());
            }
        }
        return result;
    }

    public List<ExpansionJoin> getCyclicJoinConditions() {
        return cyclicJoinConditions;
    }

    public List<VariableJoinCondition> getUniversalCyclicJoinConditions() {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (ExpansionJoin expansionJoin : cyclicJoinConditions) {
            if (expansionJoin.isJoinOnUniversal()) {
                result.add(expansionJoin.getJoinCondition());
            }
        }
        return result;
    }

    public List<VariableJoinCondition> getAllJoinConditions() {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (ExpansionJoin expansionJoin : joinConditions) {
            result.add(expansionJoin.getJoinCondition());
        }
        for (ExpansionJoin cyclicExpansionJoin : cyclicJoinConditions) {
            result.add(cyclicExpansionJoin.getJoinCondition());
        }
        return result;
    }

//    public List<ExpansionJoin> getAllExpansionJoins() {
//        List<ExpansionJoin> result = new ArrayList<ExpansionJoin>();
//        result.addAll(joinConditions);
//        result.addAll(cyclicJoinConditions);
//        return result;
//    }
    public List<VariableJoinCondition> getAllJoinConditionsOnUniversal() {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (ExpansionJoin expansionJoin : joinConditions) {
            if (expansionJoin.isJoinOnUniversal()) {
                result.add(expansionJoin.getJoinCondition());
            }
        }
        for (ExpansionJoin cyclicExpansionJoin : cyclicJoinConditions) {
            if (cyclicExpansionJoin.isJoinOnUniversal()) {
                result.add(cyclicExpansionJoin.getJoinCondition());
            }
        }
        return result;
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

    public int compareTo(Expansion o) {
        if (this.expansionElements.size() != o.expansionElements.size()) {
            return this.expansionElements.size() - o.expansionElements.size();
        }
        return this.toString().compareTo(o.toString());
    }

    public String getId() {
        if (base) {
            return "BASE_EXP_" + this.baseView.getId();
        }
        int hashCode = Math.abs(getVariablesIdWithProvenance().hashCode()) % 1000;
        return "EXP_" + this.getVariableIds() + "_" + hashCode + "_OF_" + this.baseView.getId();
    }

    public String getVariableIds() {
        StringBuilder result = new StringBuilder();
        for (ExpansionElement element : this.expansionElements) {
            result.append(element.getCoveringAtom().getVariable().toShortString());
        }
        return result.toString();
    }

    public String getVariablesIdWithProvenance() {
        StringBuilder result = new StringBuilder();
        for (ExpansionElement element : this.expansionElements) {
            result.append(element.getCoveringAtom().getVariable().toShortStringWithProvenance());
        }
        return result.toString();
    }

    public ComplexQueryWithNegations getRew_c() {
        return rew_c;
    }

    public void setRew_c(ComplexQueryWithNegations rew_c) {
        this.rew_c = rew_c;
    }

    public ComplexQueryWithNegations getRew_i() {
        return rew_i;
    }

    public void setRew_i(ComplexQueryWithNegations rew_i) {
        this.rew_i = rew_i;
    }

    public ComplexQueryWithNegations getSourceRewriting() {
        return sourceRewriting;
    }

    public void setSourceRewriting(ComplexQueryWithNegations sourceRewriting) {
        this.sourceRewriting = sourceRewriting;
    }

//    public boolean equals(Object o) {
//        if (!(o instanceof Expansion)) {
//            return false;
//        }
//        Expansion otherExpansion = (Expansion) o;
//        if (!this.baseView.equals(otherExpansion.baseView)) {
//            return false;
//        }
//        if (this.expansionElements.size() != otherExpansion.expansionElements.size()) {
//            return false;
//        }
//        if (this.joinConditions.size() != otherExpansion.joinConditions.size()) {
//            return false;
//        }
//        if (this.cyclicJoinConditions.size() != otherExpansion.cyclicJoinConditions.size()) {
//            return false;
//        }
//        for (int i = 0; i < expansionElements.size(); i++) {
//            ExpansionElement coveringAtom = expansionElements.get(i);
//            ExpansionElement otherCoveringAtom = otherExpansion.expansionElements.get(i);
//            if (!coveringAtom.equals(otherCoveringAtom)) {
//                return false;
//            }
//        }
//        return true;
//    }

//    public Expansion clone() {
//        try {
//            Expansion clone = (Expansion) super.clone();
//            clone.expansionElements = new ArrayList<ExpansionElement>();
//            for (ExpansionElement atom : expansionElements) {
//                clone.expansionElements.add(atom.clone());
//            }
//            clone.joinConditions = new ArrayList<ExpansionJoin>();
//            for (ExpansionJoin joinCondition : joinConditions) {
//                clone.joinConditions.add(joinCondition.clone());
//            }
//            clone.cyclicJoinConditions = new ArrayList<ExpansionJoin>();
//            for (ExpansionJoin cyclicJoinCondition : cyclicJoinConditions) {
//                clone.cyclicJoinConditions.add(cyclicJoinCondition.clone());
//            }
//            clone.originalUniversalPaths = new ArrayList<VariablePathExpression>();
//            for (VariablePathExpression universalPath : originalUniversalPaths) {
//                clone.originalUniversalPaths.add(universalPath.clone());
//            }
//            clone.matchingUniversalPaths = new ArrayList<VariablePathExpression>();
//            for (VariablePathExpression universalPath : matchingUniversalPaths) {
//                clone.matchingUniversalPaths.add(universalPath.clone());
//            }
//            return clone;
//        } catch (CloneNotSupportedException ex) {
//            return null;
//        }
//    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("-------------------View Expansion (").append(getId()).append(") -------------------\n");
        if (isBase()) {
            result.append(indent).append("BASE EXPANSION\n");
        }
        for (int i = 0; i < expansionElements.size(); i++) {
            ExpansionElement element = expansionElements.get(i);
            result.append(element.toShortString(indent));
            if (i != expansionElements.size() - 1) {
                result.append("\n");
            }
        }
        if (joinConditions.size() > 0) {
            result.append("\n");
            result.append(indent).append("--- joins: \n");
            result.append(SpicyEngineUtility.printCollection(joinConditions, indent));
        }
        if (cyclicJoinConditions.size() > 0) {
            result.append("\n");
            result.append(indent).append("--- cyclic joins: \n");
            result.append(SpicyEngineUtility.printCollection(cyclicJoinConditions, indent));
        }
        if (!isBase()) {
            result.append("\n");
            result.append(indent).append("--- intersection paths: \n");
            result.append(indent).append(originalUniversalPaths).append("\n");
            result.append(indent).append(matchingUniversalPaths).append("\n");
        } else {
            result.append("\n");
        }
        result.append(indent).append("----------------------------------------------------\n");
        return result.toString();
    }

}
