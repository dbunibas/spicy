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

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.operators.FindFormulaVariablesUtility;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaVariable implements Cloneable, Comparable<FormulaVariable> {

    private static int counter = 0;
    private String id;
    private boolean existential;
    private List<VariableOccurrence> sourceOccurrences = new ArrayList<VariableOccurrence>();
    private List<VariableOccurrence> targetOccurrences = new ArrayList<VariableOccurrence>();
    private Map<VariablePathExpression, Expression> transformationFunctions = new HashMap<VariablePathExpression, Expression>();

    public FormulaVariable(boolean existential) {
        this.existential = existential;
        setId();
    }

    public FormulaVariable(VariablePathExpression occurrence, boolean existential) {
        this(existential);
        if (!existential) {
            this.sourceOccurrences.add(new VariableOccurrence(occurrence, false));
        } else {
            this.targetOccurrences.add(new VariableOccurrence(occurrence, false));
        }
    }

    private void setId() {
        if (existential) {
            id = "Y" + counter++;
        } else {
            id = "x" + counter++;
        }
    }

    public String getId() {
        return this.id;
    }

    public boolean isExistential() {
        return existential;
    }

    public boolean isPureNull() {
        return (isExistential() && targetOccurrences.size() == 1);
    }

    public List<VariablePathExpression> getSourceOccurrencePaths() {
        return new FindFormulaVariablesUtility().getPaths(sourceOccurrences);
    }

    public List<VariablePathExpression> getOriginalSourceOccurrencePaths() {
        return VariableOccurrence.getOriginalPaths(sourceOccurrences);
    }

    public void addSourceOccurrencePath(VariablePathExpression pathExpression) {
        this.sourceOccurrences.add(new VariableOccurrence(pathExpression, false));
    }

    public List<VariableOccurrence> getSourceOccurrences() {
        return sourceOccurrences;
    }

    public void addSourceOccurrence(VariableOccurrence occurrence) {
        this.sourceOccurrences.add(occurrence);
    }

    public List<VariablePathExpression> getTargetOccurrencePaths() {
        return new FindFormulaVariablesUtility().getPaths(targetOccurrences);
    }

    public void addTargetOccurrencePath(VariablePathExpression pathExpression) {
        this.targetOccurrences.add(new VariableOccurrence(pathExpression, false));
    }

    public void addTransformationFunction(VariablePathExpression targetPath, Expression expression) {
        this.transformationFunctions.put(targetPath, expression);
    }

    public Expression getTransformationFunction(VariablePathExpression targetPath) {
        return this.transformationFunctions.get(targetPath);
    }

    public int compareTo(FormulaVariable o) {
        return this.id.compareTo(o.id);
    }

    public boolean hasSameId(FormulaVariable variable) {
        return this.id.equals(variable.id);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FormulaVariable)) {
            return false;
        }
        FormulaVariable otherVariable = (FormulaVariable) obj;
        boolean sameQuantifier = (this.existential == otherVariable.existential);
        boolean sameSourceOccurrences = SpicyEngineUtility.equalLists(this.sourceOccurrences, otherVariable.sourceOccurrences);
        boolean sameTargetOccurrences = SpicyEngineUtility.equalLists(this.targetOccurrences, otherVariable.targetOccurrences);
        return sameQuantifier && sameSourceOccurrences && sameTargetOccurrences;
    }

    public FormulaVariable clone() {
        try {
            FormulaVariable clone = (FormulaVariable) super.clone();
            clone.sourceOccurrences = new ArrayList<VariableOccurrence>(this.sourceOccurrences);
            clone.targetOccurrences = new ArrayList<VariableOccurrence>(this.targetOccurrences);
            clone.transformationFunctions = new HashMap<VariablePathExpression, Expression>(this.transformationFunctions);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public String toShortString() {
        return this.toString();
    }

    public String toString() {
        return this.id;
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(toString());
        if (!sourceOccurrences.isEmpty()) {
            result.append(" - Source Occ: ").append(sourceOccurrences);
        }
        if (!targetOccurrences.isEmpty()) {
            result.append(" - Target Occ: ").append(targetOccurrences);
        }
        return result.toString();
    }
}
