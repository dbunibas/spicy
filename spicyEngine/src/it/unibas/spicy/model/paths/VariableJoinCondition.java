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
 
package it.unibas.spicy.model.paths;

import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class VariableJoinCondition implements Cloneable, Comparable<VariableJoinCondition> {
    
    private List<VariablePathExpression> fromPaths;
    private List<VariablePathExpression> toPaths;
    private boolean monodirectional;
    private boolean mandatory;
 
    public VariableJoinCondition(VariablePathExpression fromPath, VariablePathExpression toPath,
            boolean monodirectional, boolean mandatory) {
        this.fromPaths = new ArrayList<VariablePathExpression>();
        this.fromPaths.add(fromPath);
        this.toPaths = new ArrayList<VariablePathExpression>();
        this.toPaths.add(toPath);
        this.monodirectional = monodirectional;
        this.mandatory = mandatory;
    }

    public VariableJoinCondition(List<VariablePathExpression> fromPaths, List<VariablePathExpression> toPaths,
            boolean monodirectional, boolean mandatory) {
        assert(fromPaths.size() >= 1 && toPaths.size() >= 1) : "Path lists cannot be empty: " + fromPaths + " - " + toPaths;
        assert(fromPaths.size() == toPaths.size()) : "Path lists must have equal sizes: " + fromPaths + " - " + toPaths;
        this.fromPaths = fromPaths;
        this.toPaths = toPaths;
        this.monodirectional = monodirectional;
        this.mandatory = mandatory;
    }

    public VariableJoinCondition(VariableJoinCondition originalJoin, SetAlias newVariable) {
        assert(newVariable.equals(originalJoin.getFromVariable()) || newVariable.equals(originalJoin.getToVariable())) : "Variable not present in join: " + newVariable + " - " + originalJoin;
        if (newVariable.equals(originalJoin.getFromVariable())) {
            this.toPaths = originalJoin.toPaths;
            this.fromPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression originalFromPath : originalJoin.fromPaths) {
                this.fromPaths.add(new VariablePathExpression(originalFromPath, newVariable));
            }
        } else {
            this.fromPaths = originalJoin.fromPaths;
            this.toPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression originalToPath : originalJoin.toPaths) {
                this.toPaths.add(new VariablePathExpression(originalToPath, newVariable));
            }
        }
        this.monodirectional = originalJoin.monodirectional;
        this.mandatory = originalJoin.mandatory;
    }

    public boolean isSimple() {
        return this.fromPaths.size() == 1;
    }

    public int size() {
        return this.fromPaths.size();
    }

    public VariablePathExpression getFirstFromPath() {
        return fromPaths.get(0);
    }

    public VariablePathExpression getFirstToPath() {
        return toPaths.get(0);
    }

    public List<VariablePathExpression> getFromPaths() {
        return fromPaths;
    }

    public List<VariablePathExpression> getToPaths() {
        return toPaths;
    }

    public SetAlias getFromVariable() {
        return getFirstFromPath().getStartingVariable();
    }

    public SetAlias getToVariable() {
        return getFirstToPath().getStartingVariable();
    }

    public boolean isMonodirectional() {
        return monodirectional;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public VariableJoinCondition clone() {
        try {
            VariableJoinCondition clone = (VariableJoinCondition) super.clone();
            clone.fromPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression fromPath : fromPaths) {
                clone.fromPaths.add(fromPath.clone());
            }
            clone.toPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression toPath : toPaths) {
                clone.toPaths.add(toPath.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VariableJoinCondition)) {
            return false;
        }
        VariableJoinCondition condition = (VariableJoinCondition)obj;
        boolean equalPaths = (SpicyEngineUtility.equalLists(this.fromPaths, condition.fromPaths) &&
                SpicyEngineUtility.equalLists(this.toPaths, condition.toPaths));
        boolean bothMandatory = (this.mandatory == condition.mandatory);
        boolean bothMonodirectional = (this.monodirectional == condition.monodirectional);
        return equalPaths && bothMandatory && bothMonodirectional;
    }

    public boolean equalsUpToClones(VariableJoinCondition obj) {
        VariableJoinCondition condition = (VariableJoinCondition)obj;
        boolean equalPaths = (SpicyEngineUtility.equalListsUpToClones(this.fromPaths, condition.fromPaths) &&
                SpicyEngineUtility.equalListsUpToClones(this.toPaths, condition.toPaths));
        boolean bothMandatory = (this.mandatory == condition.mandatory);
        boolean bothMonodirectional = (this.monodirectional == condition.monodirectional);
        return equalPaths && bothMandatory && bothMonodirectional;
    }

    public boolean hasEqualPaths(VariableJoinCondition joinCondition) {
        return (SpicyEngineUtility.equalLists(this.fromPaths, joinCondition.fromPaths) &&
                SpicyEngineUtility.equalLists(this.toPaths, joinCondition.toPaths));
    }

    public boolean hasInversePaths(VariableJoinCondition joinCondition) {
        return (SpicyEngineUtility.equalLists(this.fromPaths, joinCondition.toPaths) &&
                SpicyEngineUtility.equalLists(this.toPaths, joinCondition.fromPaths));
    }

    public boolean isInverseUpToClones(VariableJoinCondition joinCondition) {
        VariableJoinCondition condition = (VariableJoinCondition)joinCondition;
        return (SpicyEngineUtility.equalListsUpToClones(this.fromPaths, condition.toPaths) &&
                SpicyEngineUtility.equalListsUpToClones(this.toPaths, condition.fromPaths));
    }

    public boolean equalsOrIsInverseUpToClones(VariableJoinCondition joinCondition) {
        return equalsUpToClones(joinCondition) || isInverseUpToClones(joinCondition);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.fromPaths).append(" => ").append(this.toPaths);
        result.append(" (bidirectional = ").append(!monodirectional).append(")");
        result.append(" (mandatory = ").append(mandatory).append(")");
        return result.toString();
    }
    
    public String toStringWithSet() {
        StringBuilder result = new StringBuilder();
        for (VariablePathExpression fromPath : fromPaths) {
            result.append(fromPath.toStringWithSet()).append(" ");
        }
        result.append(" => ");
        for (VariablePathExpression toPath : toPaths) {
            result.append(toPath.toStringWithSet()).append(" ");
        }
        result.append(" (bidirectional = ").append(!monodirectional).append(")");
        result.append(" (mandatory = ").append(mandatory).append(")");
        return result.toString();
    }

    public int compareTo(VariableJoinCondition o) {
        return this.getAbsoluteJoinCondition().toString().compareTo(o.getAbsoluteJoinCondition().toString());

    }

    public JoinCondition getAbsoluteJoinCondition() {
        List<PathExpression> absoluteFromPaths = new ArrayList<PathExpression>();
        List<PathExpression> absoluteToPaths = new ArrayList<PathExpression>();
        for (VariablePathExpression fromPath : fromPaths) {
            absoluteFromPaths.add(fromPath.getAbsolutePath());
        }
        for (VariablePathExpression toPath : toPaths) {
            absoluteToPaths.add(toPath.getAbsolutePath());
        }
        return new JoinCondition(absoluteFromPaths, absoluteToPaths);
    }

}
