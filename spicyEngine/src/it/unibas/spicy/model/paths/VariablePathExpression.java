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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.model.paths.operators.IVariablePathVisitor;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VariablePathExpression extends PathExpression {

    private static Log logger = LogFactory.getLog(VariablePathExpression.class);

    protected SetAlias startingVariable;

    public VariablePathExpression(List<String> pathSteps) {
        super(pathSteps);
    }

    public VariablePathExpression(PathExpression pathExpression) {
        super(pathExpression.getPathSteps());
    }

    public VariablePathExpression(SetAlias startingVariable, List<String> pathSteps) {
        super(pathSteps);
        assert (startingVariable.getBindingPathExpression() != null) : "Starting variable must be bound: " + startingVariable;
        this.startingVariable = startingVariable;
    }

    public VariablePathExpression(VariablePathExpression originalPath, SetAlias newVariable) {
        this(newVariable, originalPath.getPathSteps());
        assert(newVariable.equalsOrIsClone(originalPath.getStartingVariable())) : "Variables do not match. New variable: " + newVariable + " - Original variable: " + originalPath.getStartingVariable();
    }

    public SetAlias getStartingVariable() {
        return this.startingVariable;
    }

    public INode getLastNode(INode root) {
        List<INode> pathNodes = new GeneratePathExpression().generatePathStepNodes(getAbsolutePath().getPathSteps(), root);
        return pathNodes.get(pathNodes.size() - 1);
    }

    public PathExpression getAbsolutePath() {
        return new GeneratePathExpression().generateAbsolutePath(this);
    }

    public void accept(IVariablePathVisitor pathVisitor) {
        pathVisitor.visitPathExpression(this);
    }

    public VariablePathExpression clone() {
        VariablePathExpression clone = (VariablePathExpression) super.clone();
        if (startingVariable != null) {
            clone.startingVariable = startingVariable.clone();
        }
        return clone;
    }

    public boolean equals(Object object) {
        if (!(object instanceof VariablePathExpression)) {
            return false;
        }
        VariablePathExpression otherPathExpression = (VariablePathExpression) object;
        return this.getAbsolutePath().equals(otherPathExpression.getAbsolutePath());
    }

    public boolean equalsUpToClones(VariablePathExpression otherPathExpression) {
        return this.getAbsolutePath().equalsUpToClones(otherPathExpression.getAbsolutePath());
    }

    public boolean equalsUpToClonesAndHasSameVariableId(VariablePathExpression otherPathExpression) {
        return this.getStartingVariable().getId() == otherPathExpression.getStartingVariable().getId() &&
                this.getAbsolutePath().equalsUpToClones(otherPathExpression.getAbsolutePath());
    }

    public boolean equalsAndHasSameVariableId(VariablePathExpression otherPathExpression) {
        return this.getStartingVariable().getId() == otherPathExpression.getStartingVariable().getId() &&
                this.getAbsolutePath().equals(otherPathExpression.getAbsolutePath());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (this.startingVariable != null) {
            result.append(this.startingVariable.toShortString());
        }
        for (String nodeLabel : this.pathSteps) {
            if (!result.toString().equals("")) {
                result.append(".");
            }
            result.append(nodeLabel);
        }
        return result.toString();
    }

    public String toStringWithSet() {
        StringBuilder result = new StringBuilder();
        if (this.startingVariable != null) {
            result.append(this.startingVariable.toShortString());
            result.append("[" + this.startingVariable.getBindingPathExpression().getLastStep()).append("]");
        }
        for (String nodeLabel : this.pathSteps) {
            if (!result.toString().equals("")) {
                result.append(".");
            }
            result.append(nodeLabel);
        }
        return result.toString();
    }
}
