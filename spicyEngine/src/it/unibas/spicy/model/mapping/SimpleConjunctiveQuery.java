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
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.operators.IQueryVisitor;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.model.paths.operators.FindPathsInVariable;
import it.unibas.spicy.model.paths.operators.FindVariableGenerators;
import it.unibas.spicy.model.paths.operators.VariableToString;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleConjunctiveQuery implements Cloneable, Comparable<SimpleConjunctiveQuery>, IIdentifiable {

    private static Log logger = LogFactory.getLog(SimpleConjunctiveQuery.class);
    private List<SetAlias> variables = new ArrayList<SetAlias>();
    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();
    private List<VariableJoinCondition> cyclicJoinConditions = new ArrayList<VariableJoinCondition>();
    private List<VariableSelectionCondition> selectionConditions = new ArrayList<VariableSelectionCondition>();
    private String provenance;
    // generated
    private IAlgebraOperator algebraTreeRoot;

    public SimpleConjunctiveQuery() {
    }

    public SimpleConjunctiveQuery(SetAlias variable) {
        this.variables.add(variable);
    }

    public SimpleConjunctiveQuery(List<SetAlias> variables, List<VariableJoinCondition> joinConditions) {
        this.variables = variables;
        this.joinConditions = joinConditions;
    }

    public SimpleConjunctiveQuery(List<SetAlias> variables, List<VariableJoinCondition> joinConditions, List<VariableJoinCondition> cyclicJoinConditions) {
        this.variables = variables;
        this.joinConditions = joinConditions;
        this.cyclicJoinConditions = cyclicJoinConditions;
    }
    
    public String getId() {
        return "SCQ_" + Math.abs(this.toString().hashCode());
    }

    public void addVariable(SetAlias variable) {
        this.variables.add(variable);
    }

    public void addVariables(List<SetAlias> variables) {
        variables.addAll(variables);
    }

    public List<SetAlias> getVariables() {
        return variables;
    }

    public boolean containsVariableInGenerators(SetAlias variable) {
        return this.getGenerators().contains(variable);
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public void addJoinConditions(List<VariableJoinCondition> joinConditions) {
        this.joinConditions.addAll(joinConditions);
    }

    public List<VariableJoinCondition> getCyclicJoinConditions() {
        return cyclicJoinConditions;
    }

    public void addCyclicJoinCondition(VariableJoinCondition cyclicJoinCondition) {
        this.cyclicJoinConditions.add(cyclicJoinCondition);
    }

    public void addCyclicJoinConditions(List<VariableJoinCondition> cyclicJoinConditions) {
        this.cyclicJoinConditions.addAll(cyclicJoinConditions);
    }

    public List<VariableJoinCondition> getAllJoinConditions() {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        result.addAll(joinConditions);
        result.addAll(cyclicJoinConditions);
        return result;
    }

    public boolean hasJoinConditions() {
        return !joinConditions.isEmpty() || !cyclicJoinConditions.isEmpty();
    }

    public void addSelectionCondition(VariableSelectionCondition selectionCondition) {
        this.selectionConditions.add(selectionCondition);
    }

    public void addSelectionConditions(List<VariableSelectionCondition> selectionConditions) {
        this.selectionConditions.addAll(selectionConditions);
    }

    public List<VariableSelectionCondition> getSelectionConditions() {
        return selectionConditions;
    }

    public List<SetAlias> getGenerators() {
        return new FindVariableGenerators().findViewGenerators(this);
    }

    public List<VariablePathExpression> getAttributePaths(INode root) {
        return new FindPathsInVariable().findAttributePaths(this, root);
    }

    public IAlgebraOperator getAlgebraTree() {
        if (algebraTreeRoot == null) {
            algebraTreeRoot = new GenerateAlgebraTree().generateTreeForSimpleConjunctiveQuery(this);
        }
        return algebraTreeRoot;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public SimpleConjunctiveQuery clone() {
        try {
            SimpleConjunctiveQuery clone = (SimpleConjunctiveQuery) super.clone();
            List<SetAlias> variableClones = new ArrayList<SetAlias>();
            for (SetAlias setVariable : variables) {
                variableClones.add(setVariable.clone());
            }
            clone.variables = variableClones;
            clone.joinConditions = new ArrayList<VariableJoinCondition>();
            for (VariableJoinCondition joinCondition : joinConditions) {
                clone.joinConditions.add(joinCondition.clone());
            }
            clone.cyclicJoinConditions = new ArrayList<VariableJoinCondition>();
            for (VariableJoinCondition joinCondition : cyclicJoinConditions) {
                clone.cyclicJoinConditions.add(joinCondition.clone());
            }
            clone.selectionConditions = new ArrayList<VariableSelectionCondition>();
            for (VariableSelectionCondition selectionCondition : selectionConditions) {
                clone.selectionConditions.add(selectionCondition.clone());
            }
            clone.algebraTreeRoot = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    public SimpleConjunctiveQuery cloneWithoutAlgebraTree() {
        try {
            SimpleConjunctiveQuery clone = (SimpleConjunctiveQuery) super.clone();
            clone.variables = new ArrayList<SetAlias>();
            for (SetAlias variable : variables) {
                clone.variables.add(variable.cloneWithoutAlgebraTree());
            }
            clone.joinConditions = new ArrayList<VariableJoinCondition>(this.joinConditions);
            clone.cyclicJoinConditions = new ArrayList<VariableJoinCondition>(this.cyclicJoinConditions);
            clone.selectionConditions = new ArrayList<VariableSelectionCondition>(this.selectionConditions);
            clone.algebraTreeRoot = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    public int compareTo(SimpleConjunctiveQuery otherView) {
        return (this.getGenerators().size() - otherView.getGenerators().size());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleConjunctiveQuery)) return false;
        SimpleConjunctiveQuery view = (SimpleConjunctiveQuery) obj;
//        boolean result = (SpicyEngineUtility.equalLists(this.variables, view.variables)
//                && SpicyEngineUtility.equalLists(this.joinConditions, view.joinConditions)
//                && SpicyEngineUtility.equalLists(this.cyclicJoinConditions, view.cyclicJoinConditions));
        boolean result = this.variables.equals(view.variables)
                && this.joinConditions.equals(view.joinConditions)
                && this.cyclicJoinConditions.equals(view.cyclicJoinConditions)
                && this.selectionConditions.equals(view.selectionConditions);
        if (logger.isDebugEnabled()) logger.debug("Checking equality of view: \n" + this + "\nagainst view:\n" + view + "\nResult: " + result);
        return result;
    }

    public String toVariableString() {
        StringBuilder result = new StringBuilder();
        for (SetAlias variable : variables) {
            result.append(variable.toShortString());
        }
        return result.toString();
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(variableString(indent));
        if (!this.selectionConditions.isEmpty()) {
            result.append("\n");
            result.append(indent).append("Selection conditions: \n");
            for (VariableSelectionCondition selectionCondition : this.selectionConditions) {
                result.append(indent).append(selectionCondition).append("\n");
            }
        }
        if (!this.joinConditions.isEmpty()) {
            result.append("\n");
            result.append(indent).append("Join conditions: \n");
            for (VariableJoinCondition joinCondition : this.joinConditions) {
                result.append(indent).append(joinCondition).append("\n");
            }
        }
        if (!this.cyclicJoinConditions.isEmpty()) {
            result.append(indent).append("Cyclic join conditions: \n");
            for (VariableJoinCondition cyclicJoinCondition : this.cyclicJoinConditions) {
                result.append(indent).append(cyclicJoinCondition).append("\n");
            }
        }
        return result.toString();
    }

    public String variableString(String indent) {
        VariableToString printer = new VariableToString();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < variables.size(); i++) {
            result.append(indent).append(printer.toString(variables.get(i)));
            if (i != variables.size() - 1) result.append(",\n");
        }
        return result.toString();
    }

    public String toTGDSTring(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(this.variableString(indent));
        if (!this.joinConditions.isEmpty() || !this.selectionConditions.isEmpty()) {
            result.append("\n");
            result.append(indent).append("where     ");
        }
        if (!this.selectionConditions.isEmpty()) {
            result.append("\n");
            for (int i = 0; i < selectionConditions.size(); i++) {
                VariableSelectionCondition selectionCondition = selectionConditions.get(i);
                result.append(indent).append(selectionCondition);
                if (i != selectionConditions.size() - 1) {
                    result.append("\n");
                }
            }
        }
        if (!this.joinConditions.isEmpty()) {
            result.append("\n");
            for (int i = 0; i < joinConditions.size(); i++) {
                VariableJoinCondition joinCondition = joinConditions.get(i);
                result.append(indent).append(joinCondition);
                if (i != joinConditions.size() - 1) {
                    result.append("\n");
                }
            }
        }
        if (!this.cyclicJoinConditions.isEmpty()) {
            result.append("\n");
            result.append(indent).append("and     \n");
            for (int i = 0; i < cyclicJoinConditions.size(); i++) {
                VariableJoinCondition joinCondition = cyclicJoinConditions.get(i);
                result.append(indent).append(joinCondition).append(" (cyclic)");
                if (i != cyclicJoinConditions.size() - 1) {
                    result.append("\n");
                }
            }
        }
        return result.toString();
    }

    public void accept(IQueryVisitor visitor) {
        visitor.visitSimpleConjunctiveQuery(this);
    }
}
