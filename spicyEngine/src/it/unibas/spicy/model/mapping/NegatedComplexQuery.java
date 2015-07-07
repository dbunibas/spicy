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
 
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.mapping.operators.IQueryVisitor;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;

public class NegatedComplexQuery implements IIdentifiable, Comparable<NegatedComplexQuery> {

    private ComplexQueryWithNegations complexQuery;
    private SourceEqualities sourceEqualities;
    private TargetEqualities targetEqualities;
    private List<VariableJoinCondition> additionalCyclicJoins = new ArrayList<VariableJoinCondition>();
    private List<VariableCorrespondence> correspondencesForJoin = new ArrayList<VariableCorrespondence>();
    private String provenance;

    public String getId() {
        return "NCQ_" + Math.abs(this.toString().hashCode());
    }

    public boolean isTargetDifference() {
        return targetEqualities != null;
    }

    public NegatedComplexQuery(ComplexQueryWithNegations complexQuery, TargetEqualities equalities) {
        this.complexQuery = complexQuery;
        this.targetEqualities = equalities;
    }

    public NegatedComplexQuery(ComplexQueryWithNegations complexQuery, SourceEqualities equalities) {
        this.complexQuery = complexQuery;
        this.sourceEqualities = equalities;
    }

    public ComplexQueryWithNegations getComplexQuery() {
        return complexQuery;
    }

    public TargetEqualities getTargetEqualities() {
        return targetEqualities;
    }

    public void setTargetEqualities(TargetEqualities targetEqualities) {
        this.targetEqualities = targetEqualities;
    }

    public SourceEqualities getSourceEqualities() {
        return sourceEqualities;
    }

    public void setSourceEqualities(SourceEqualities sourceEqualities) {
        this.sourceEqualities = sourceEqualities;
    }

    public List<VariableJoinCondition> getAdditionalCyclicJoins() {
        return additionalCyclicJoins;
    }

    public void setAdditionalCyclicJoins(List<VariableJoinCondition> additionalCyclicJoins) {
        this.additionalCyclicJoins = additionalCyclicJoins;
    }

    public List<VariableCorrespondence> getCorrespondencesForJoin() {
        return correspondencesForJoin;
    }

    public void setCorrespondencesForJoin(List<VariableCorrespondence> correspondencesForJoin) {
        this.correspondencesForJoin = correspondencesForJoin;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("and not exist ");
        if (provenance != null) {
            result.append("(").append(provenance).append(")");
        }
        result.append("\n");
//        result.append(indent).append(complexQuery.getProvenance()).append("\n");
        result.append(complexQuery.toString(indent + SpicyEngineConstants.INDENT));
        if (additionalCyclicJoins != null && !additionalCyclicJoins.isEmpty()) {
            result.append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("additional joins: ").append(additionalCyclicJoins).append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("correspondences: ").append(correspondencesForJoin);
        }
        result.append("\n");
        result.append(indent).append(SpicyEngineConstants.INDENT).append("with equalities:").append("\n");
        if(targetEqualities != null) {
            result.append(targetEqualities.toString(indent + SpicyEngineConstants.INDENT));
        }else {
            result.append(sourceEqualities.toString(indent + SpicyEngineConstants.INDENT));
        }
        return result.toString();
    }

    public void accept(IQueryVisitor visitor) {
        visitor.visitNegatedComplexQuery(this);
    }

    public int compareTo(NegatedComplexQuery o) {
        return this.getId().compareTo(o.getId());
    }
}
