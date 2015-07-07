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

import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class VariableFunctionalDependency implements Cloneable {

    protected List<VariablePathExpression> leftPaths;
    protected List<VariablePathExpression> rightPaths;
    private boolean key;
    private boolean primaryKey;

    public VariableFunctionalDependency(List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        assert (leftPaths.size() >= 1 && rightPaths.size() >= 1) : "Path lists cannot be empty: " + leftPaths + " - " + rightPaths;
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
    }

    public VariableFunctionalDependency(List<VariablePathExpression> leftPaths, VariablePathExpression rightPath) {
        assert (leftPaths.size() >= 1) : "Path lists cannot be empty: " + leftPaths;
        List<VariablePathExpression> singleton = new ArrayList<VariablePathExpression>();
        singleton.add(rightPath);
        this.leftPaths = leftPaths;
        this.rightPaths = singleton;
    }

    public VariableFunctionalDependency(VariableFunctionalDependency dependency, SetAlias newVariable) {
        this.leftPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression leftPath : dependency.getLeftPaths()) {
            this.leftPaths.add(new VariablePathExpression(leftPath, newVariable));
        }
        this.rightPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression rightPath : dependency.getRightPaths()) {
            this.rightPaths.add(new VariablePathExpression(rightPath, newVariable));
        }
        this.key = dependency.key;
        this.primaryKey = dependency.primaryKey;
    }

    public List<VariablePathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<VariablePathExpression> getRightPaths() {
        return rightPaths;
    }

    public SetAlias getVariable() {
        VariablePathExpression firstPath = leftPaths.get(0);
        if (firstPath == null) {
            return null;
        }
        return leftPaths.get(0).getStartingVariable();
    }

    public boolean isKey() {
        return key;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public VariableFunctionalDependency clone() {
        try {
            VariableFunctionalDependency clone = (VariableFunctionalDependency) super.clone();
            clone.leftPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression leftPath : leftPaths) {
                clone.leftPaths.add(leftPath.clone());
            }
            clone.rightPaths = new ArrayList<VariablePathExpression>();
            for (VariablePathExpression rightPath : rightPaths) {
                clone.rightPaths.add(rightPath.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VariableFunctionalDependency)) {
            return false;
        }
        VariableFunctionalDependency dependency = (VariableFunctionalDependency) obj;
        boolean equalPaths = (SpicyEngineUtility.equalLists(this.leftPaths, dependency.leftPaths) &&
                SpicyEngineUtility.equalLists(this.rightPaths, dependency.rightPaths));
        boolean bothKeys = (this.primaryKey == dependency.primaryKey);
        boolean bothPrimaryKeys = (this.key == dependency.key);
        return equalPaths && bothKeys && bothPrimaryKeys;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.leftPaths).append(" => ").append(this.rightPaths);
        if (this.primaryKey) {
            result.append(" (primary key)");
        } else if (this.key) {
            result.append(" (key)");
        }
        return result.toString();
    }

    public String toSaveString() {
        StringBuilder result = new StringBuilder();
        String firstStep = leftPaths.get(0).getFirstStep();
        if(firstStep.endsWith("Tuple")){
            firstStep = firstStep.substring(0,firstStep.length() - "Tuple".length());
        }
        result.append(firstStep).append(": ");
        for (int i = 0; i < leftPaths.size(); i++) {
            VariablePathExpression variablePathExpression = leftPaths.get(i);
            result.append(variablePathExpression.getLastStep());
            if(i != leftPaths.size() - 1){
                result.append(", ");
            }
        }
        result.append(" -> ");
        for (int i = 0; i < rightPaths.size(); i++) {
            VariablePathExpression variablePathExpression = rightPaths.get(i);
            result.append(variablePathExpression.getLastStep());
            if(i != rightPaths.size() - 1){
                result.append(", ");
            }
        }
        if (this.primaryKey) {
            result.append(" [pk]");
        } else if (this.key) {
            result.append(" [key]");
        }
        return result.toString();
    }
}
