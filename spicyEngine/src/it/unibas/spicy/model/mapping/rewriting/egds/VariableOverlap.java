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
 
package it.unibas.spicy.model.mapping.rewriting.egds;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.TGDRenaming;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import java.util.ArrayList;
import java.util.List;

public class VariableOverlap {

    private SetAlias variable1;
    private SetAlias variable2;
    private FORule tgd1;
    private FORule tgd2;
    private boolean singleTgd;
    private VariableFunctionalDependency functionalDependency;
    private TGDRenaming firstRenamedTgd;
    private TGDRenaming secondRenamedTgd;
    private List<FORule> overlapTGDs = new ArrayList<FORule>();

    public VariableOverlap(SetAlias variable1, SetAlias variable2, FORule tgd1, FORule tgd2, VariableFunctionalDependency functionalDependency) {
        this.variable1 = variable1;
        this.variable2 = variable2;
        this.functionalDependency = functionalDependency;
        this.tgd1 = tgd1;
        this.tgd2 = tgd2;
    }

    public VariableFunctionalDependency getFunctionalDependency() {
        return functionalDependency;
    }

    public FORule getTgd1() {
        return tgd1;
    }

    public FORule getTgd2() {
        return tgd2;
    }

    public boolean isSingleTgd() {
        return singleTgd;
    }

    public void setSingleTgd(boolean singleTgd) {
        this.singleTgd = singleTgd;
    }

    public SetAlias getVariable1() {
        return variable1;
    }

    public SetAlias getVariable2() {
        return variable2;
    }

    public boolean involves(FORule tgd) {
        return tgd1.equals(tgd) || tgd2.equals(tgd);
    }

    public TGDRenaming getFirstRenamedTgd() {
        return firstRenamedTgd;
    }

    public TGDRenaming getSecondRenamedTgd() {
        return secondRenamedTgd;
    }

    public void setFirstRenamedTgd(TGDRenaming firstRenamedTgd) {
        this.firstRenamedTgd = firstRenamedTgd;
    }

    public void setSecondRenamedTgd(TGDRenaming secondRenamedTgd) {
        this.secondRenamedTgd = secondRenamedTgd;
    }

    public List<FORule> getOverlapTGDs() {
        return overlapTGDs;
    }

    public void addOverlapTGD(FORule overlapTGD) {
        this.overlapTGDs.add(overlapTGD);
    }

    public boolean equals(Object o) {
        if (!(o instanceof VariableOverlap)) {
            return false;
        }
        VariableOverlap otherOverlap = (VariableOverlap) o;
        boolean sameVariable1 = variable1.getId() == otherOverlap.variable1.getId();
        boolean sameVariable2 = variable2.getId() == otherOverlap.variable2.getId();
        boolean oppositeVariable1 = variable1.getId() == otherOverlap.variable2.getId();
        boolean oppositeVariable2 = variable2.getId() == otherOverlap.variable1.getId();
        boolean sameTgds = (tgd1.equals(otherOverlap.tgd1) && tgd2.equals(otherOverlap.tgd2)) ||
                (tgd1.equals(otherOverlap.tgd2) && tgd2.equals(otherOverlap.tgd1));
        boolean sameKey = functionalDependency.equals(otherOverlap.functionalDependency);
        return ((sameVariable1 && sameVariable2) || (oppositeVariable1 && oppositeVariable2)) && sameTgds && sameKey;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("-------------------  TGD Overlap  -------------------\n");
        result.append(indent).append(variable1).append(" - ").append(variable2);
        result.append(indent).append(functionalDependency).append("\n");
        result.append(indent).append("---First tgd :").append(tgd1.toShortString()).append("\n");
        result.append(indent).append("---Second tgd :").append(tgd2.toShortString()).append("\n");
        if (!this.overlapTGDs.isEmpty()) {
            result.append(indent).append("---Overlap tgds :\n");
            for (FORule overlapTGD : overlapTGDs) {
                result.append(indent).append(overlapTGD.toShortString()).append("\n");
            }
        }
        result.append(indent).append("-----------------------------------------------------");
        return result.toString();
    }

    public String toString(MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        result.append("-------------------  TGD Overlap  -------------------\n");
        result.append(variable1).append(" - ").append(variable2);
        result.append(functionalDependency).append("\n");
        result.append("---First tgd :\n").append(tgd1.toLogicalString(mappingTask)).append("\n");
        result.append("---Second tgd :\n").append(tgd2.toLogicalString(mappingTask)).append("\n");
        if (!this.overlapTGDs.isEmpty()) {
            result.append("---Overlap tgds :\n");
            for (FORule overlapTGD : overlapTGDs) {
                result.append(overlapTGD.toLogicalString(mappingTask)).append("\n");
            }
        }
        result.append("-----------------------------------------------------");
        return result.toString();
    }
}
