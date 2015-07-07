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

import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.algebra.operators.GenerateAlgebraTree;
import it.unibas.spicy.model.mapping.operators.IQueryVisitor;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplexConjunctiveQuery implements Cloneable, IIdentifiable {

    // join part
    private List<SimpleConjunctiveQuery> conjunctions = new ArrayList<SimpleConjunctiveQuery>();
    private List<List<VariableCorrespondence>> correspondencesForConjunctions = new ArrayList<List<VariableCorrespondence>>();
    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();
    private List<VariableJoinCondition> cyclicJoinConditions = new ArrayList<VariableJoinCondition>();
    // intesection part (optional)
    private SimpleConjunctiveQuery conjunctionForIntersection;
    private TargetEqualities intersectionEqualities;
    private String provenance;
    // generated
    private IAlgebraOperator algebraTreeRoot;

    public ComplexConjunctiveQuery() {
    }

    public ComplexConjunctiveQuery(SimpleConjunctiveQuery query) {
        this.conjunctions.add(query);
    }

    public boolean isSimple() {
        return conjunctions.size() == 1 && joinConditions.isEmpty();
    }

    public boolean hasIntersection() {
        return this.conjunctionForIntersection != null;
    }

    public List<SetAlias> getVariables() {
        List<SetAlias> result = new ArrayList<SetAlias>();
        for (SimpleConjunctiveQuery conjunct : conjunctions) {
            result.addAll(conjunct.getVariables());
        }
        return result;
    }

    public List<SetAlias> getGenerators() {
        List<SetAlias> result = new ArrayList<SetAlias>();
        for (SimpleConjunctiveQuery conjunct : conjunctions) {
            result.addAll(conjunct.getGenerators());
        }
        return result;
    }

    public SimpleConjunctiveQuery getFirstConjunct() {
        assert (!conjunctions.isEmpty()) : "Complex conjunctive query is empty";
        return conjunctions.get(0);
    }

    public List<SimpleConjunctiveQuery> getConjunctions() {
        return conjunctions;
    }

    public List<List<VariableCorrespondence>> getCorrespondencesForConjunctions() {
        return correspondencesForConjunctions;
    }

    public List<VariableCorrespondence> getAllCorrespondences() {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (List<VariableCorrespondence> correspondencesForConjunct : correspondencesForConjunctions) {
            result.addAll(correspondencesForConjunct);
        }
        return result;
    }

    public List<VariableSelectionCondition> getAllSelections() {
        List<VariableSelectionCondition> result = new ArrayList<VariableSelectionCondition>();
        for (SimpleConjunctiveQuery conjunct : conjunctions) {
            result.addAll(conjunct.getSelectionConditions());
            for (SetAlias variable : conjunct.getGenerators()) {
                result.addAll(variable.getSelectionConditions());
            }
        }
        return result;
    }

    public List<VariableJoinCondition> getCyclicJoinConditions() {
        return cyclicJoinConditions;
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public List<VariableJoinCondition> getAllJoins() {
        List<VariableJoinCondition> allJoins = new ArrayList<VariableJoinCondition>();
        allJoins.addAll(this.joinConditions);
        allJoins.addAll(this.cyclicJoinConditions);
        return allJoins;
    }

    public void addConjunction(SimpleConjunctiveQuery e) {
        conjunctions.add(e);
    }

    public void addCorrespondencesForConjuntion(List<VariableCorrespondence> e) {
        correspondencesForConjunctions.add(e);
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public void addCyclicJoinCondition(VariableJoinCondition joinCondition) {
        this.cyclicJoinConditions.add(joinCondition);
    }

    public void setCyclicJoinConditions(List<VariableJoinCondition> cyclicJoinConditions) {
        this.cyclicJoinConditions = cyclicJoinConditions;
    }

    public void setJoinConditions(List<VariableJoinCondition> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public SimpleConjunctiveQuery getConjunctionForIntersection() {
        return conjunctionForIntersection;
    }

    public void setConjunctionForIntersection(SimpleConjunctiveQuery conjunctionForIntersection) {
        this.conjunctionForIntersection = conjunctionForIntersection;
    }

    public TargetEqualities getIntersectionEqualities() {
        return intersectionEqualities;
    }

    public void setIntersectionEqualities(TargetEqualities intersectionEqualities) {
        this.intersectionEqualities = intersectionEqualities;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public IAlgebraOperator getAlgebraTree() {
        if (algebraTreeRoot == null) {
            algebraTreeRoot = new GenerateAlgebraTree().generateTreeForComplexConjunctiveQuery(this);
        }
        return algebraTreeRoot;
    }

    public String getId() {
        return "CCQ_" + Math.abs(this.toString().hashCode());
    }

    public String toString() {
        return toString("");
    }

    public boolean equals(Object o) {
        if (!(o instanceof ComplexConjunctiveQuery)) {
            return false;
        }
        ComplexConjunctiveQuery otherQuery = (ComplexConjunctiveQuery) o;
        if (this.hasIntersection() != otherQuery.hasIntersection()) {
            return false;
        }
        boolean equalConjunctions = this.conjunctions.equals(otherQuery.conjunctions);
        boolean equalCorrespondences = this.correspondencesForConjunctions.equals(otherQuery.correspondencesForConjunctions);
        boolean equalJoins = this.joinConditions.equals(otherQuery.joinConditions);
        boolean equalIntersection = true;
        if (this.hasIntersection() && otherQuery.hasIntersection()) {
            equalIntersection = equalIntersection && this.conjunctionForIntersection.equals(otherQuery.conjunctionForIntersection);
            equalIntersection = equalIntersection && this.intersectionEqualities.equals(otherQuery.intersectionEqualities);
        }
        return equalConjunctions && equalCorrespondences && equalJoins && equalIntersection;
    }

    @Override
    public ComplexConjunctiveQuery clone() {
        try {
            ComplexConjunctiveQuery clone = (ComplexConjunctiveQuery) super.clone();
            clone.conjunctions = new ArrayList<SimpleConjunctiveQuery>();
            for (SimpleConjunctiveQuery conjunctiveQuery : conjunctions) {
                clone.conjunctions.add(conjunctiveQuery.clone());
            }
            clone.correspondencesForConjunctions = new ArrayList<List<VariableCorrespondence>>(correspondencesForConjunctions);
            clone.joinConditions = new ArrayList<VariableJoinCondition>(joinConditions);
            clone.cyclicJoinConditions = new ArrayList<VariableJoinCondition>(cyclicJoinConditions);
            if (this.hasIntersection()) {
                clone.conjunctionForIntersection = this.conjunctionForIntersection.clone();
                clone.intersectionEqualities = this.intersectionEqualities.clone();
            }
            clone.algebraTreeRoot = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ComplexQueryWithNegations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        if (isSimple()) {
            result.append(indent).append("simple view:");
            if (provenance != null) {
                result.append(" (").append(provenance).append(")");
            }
            result.append("\n");
            result.append(conjunctions.get(0).toTGDSTring(indent + SpicyEngineConstants.INDENT));
        } else {
            result.append(indent).append("complex view:");
            if (provenance != null) {
                result.append(" (").append(provenance).append(")");
            }
            result.append("\n");
            for (int i = 0; i < conjunctions.size(); i++) {
                result.append(conjunctions.get(i).toTGDSTring(indent + SpicyEngineConstants.INDENT)).append("\n");
                if (i != conjunctions.size() - 1) {
                    result.append(indent).append(SpicyEngineConstants.INDENT).append("joined with\n");
                }
            }
            result.append(indent).append(SpicyEngineConstants.INDENT).append("join conditions: \n");
            for (int i = 0; i < joinConditions.size(); i++) {
                result.append(indent).append(SpicyEngineConstants.INDENT).append(joinConditions.get(i).toString());
                if (i != joinConditions.size() - 1) {
                    result.append(indent).append(SpicyEngineConstants.INDENT).append("\n");
                }
            }
            result.append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("correspondences: \n");
            for (int i = 0; i < correspondencesForConjunctions.size(); i++) {
                result.append(indent).append(SpicyEngineConstants.INDENT).append(correspondencesForConjunctions.get(i).toString());
                if (i != correspondencesForConjunctions.size() - 1) {
                    result.append(indent).append(SpicyEngineConstants.INDENT).append("\n");
                }
            }
        }
        if (!cyclicJoinConditions.isEmpty()) {
            result.append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("cyclic join conditions: \n");
            for (int i = 0; i < cyclicJoinConditions.size(); i++) {
                result.append(indent).append(SpicyEngineConstants.INDENT).append(cyclicJoinConditions.get(i).toString());
                if (i != cyclicJoinConditions.size() - 1) {
                    result.append(indent).append("\n");
                }
            }
        }
        if (hasIntersection()) {
            result.append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("intersect with\n");
            result.append(conjunctionForIntersection.toString(indent + SpicyEngineConstants.INDENT)).append("\n");
            result.append(indent).append(SpicyEngineConstants.INDENT).append("with equalities\n");
            result.append(intersectionEqualities.toString(indent + SpicyEngineConstants.INDENT));
        }
        return result.toString();
    }

    public void accept(IQueryVisitor visitor) {
        visitor.visitComplexConjunctiveQuery(this);
    }
}
