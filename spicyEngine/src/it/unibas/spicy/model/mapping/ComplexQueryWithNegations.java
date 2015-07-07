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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplexQueryWithNegations implements Cloneable, IIdentifiable {

    private ComplexConjunctiveQuery complexQuery;
    private List<NegatedComplexQuery> negatedComplexQueries = new ArrayList<NegatedComplexQuery>();
    private IAlgebraOperator algebraTreeRoot;
    private String provenance;

    public ComplexQueryWithNegations(ComplexConjunctiveQuery complexQuery) {
        this.complexQuery = complexQuery;
    }

    public String getId() {
        return "CQWN_" + Math.abs(this.toString().hashCode());
    }

    public ComplexConjunctiveQuery getComplexQuery() {
        return complexQuery;
    }

    public void setComplexQuery(ComplexConjunctiveQuery complexQuery) {
        this.complexQuery = complexQuery;
    }

    public List<NegatedComplexQuery> getNegatedComplexQueries() {
        return negatedComplexQueries;
    }

    public void setNegatedComplexQueries(List<NegatedComplexQuery> negatedComplexQueries) {
        this.negatedComplexQueries = negatedComplexQueries;
    }

    public void addNegatedComplexQuery(NegatedComplexQuery negatedView) {
        this.negatedComplexQueries.add(negatedView);
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public List<SetAlias> getVariables() {
        return this.complexQuery.getVariables();
    }

    public List<SetAlias> getGenerators() {
        return this.complexQuery.getGenerators();
    }

    public IAlgebraOperator getAlgebraTree() {
        if (algebraTreeRoot == null) {
            algebraTreeRoot = new GenerateAlgebraTree().generateTreeForComplexQueryWithNegations(this);
        }
        return algebraTreeRoot;
    }

    public String toString() {
        return toString("");
    }

    public boolean equals(Object o) {
        if (!(o instanceof ComplexQueryWithNegations)) return false;
        ComplexQueryWithNegations otherQuery = (ComplexQueryWithNegations) o;
        return this.complexQuery.equals(otherQuery.complexQuery)
                && this.negatedComplexQueries.equals(otherQuery.negatedComplexQueries);
    }

    @Override
    public ComplexQueryWithNegations clone() {
        try {
            ComplexQueryWithNegations clone = (ComplexQueryWithNegations) super.clone();
            clone.complexQuery = this.complexQuery.clone();
            clone.negatedComplexQueries = new ArrayList<NegatedComplexQuery>(this.negatedComplexQueries);
            clone.algebraTreeRoot = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ComplexQueryWithNegations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ComplexQueryWithNegations cloneAndKeepComplexQuery() {
        try {
            ComplexQueryWithNegations clone = (ComplexQueryWithNegations) super.clone();
            clone.negatedComplexQueries = new ArrayList<NegatedComplexQuery>(this.negatedComplexQueries);
            clone.algebraTreeRoot = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ComplexQueryWithNegations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
//        result.append(indent).append(provenance).append("\n");
        result.append(complexQuery.toString(indent));
        if (!negatedComplexQueries.isEmpty()) {
            result.append("\n");
            for (int i = 0; i < negatedComplexQueries.size(); i++) {
                NegatedComplexQuery negatedQuery = negatedComplexQueries.get(i);
                result.append(negatedQuery.toString(indent));
                if (i != negatedComplexQueries.size() - 1) {
                    result.append("\n");
                }
            }
        }
        return result.toString();
    }

    public void accept(IQueryVisitor visitor) {
        visitor.visitComplexQueryWithNegation(this);
    }

}
