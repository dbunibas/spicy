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

import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.paths.operators.IVariablePathVisitor;
import it.unibas.spicy.model.algebra.operators.GenerateAlgebraTree;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.mapping.operators.RenameSetAliasesUtility;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.model.paths.operators.FindPathsInVariable;
import it.unibas.spicy.model.paths.operators.FindVariableGenerators;
import it.unibas.spicy.model.paths.operators.VariableToString;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SetAlias implements Comparable<SetAlias>, Cloneable {

    private static Log logger = LogFactory.getLog(SetAlias.class);
    private static int counter = 0;

    public static int getNextId() {
        return counter++;
    }

    public static void resetId() {
        counter = 0;
    }

    public static String variableId(int id) {
        return "v" + id;
    }
    protected int id;
    protected VariablePathExpression bindingPathExpression;
    protected List<VariableSelectionCondition> selectionConditions = new ArrayList<VariableSelectionCondition>();
    protected VariableProvenanceCondition provenanceCondition;
    protected IAlgebraOperator algebraTreeRoot;

    public SetAlias(VariablePathExpression bindingPathExpression) {
        this.id = getNextId();
        this.bindingPathExpression = bindingPathExpression;
    }

    public VariablePathExpression getBindingPathExpression() {
        return bindingPathExpression;
    }

    public void setBindingPathExpression(VariablePathExpression bindingPathExpression) {
        this.bindingPathExpression = bindingPathExpression;
    }

    public PathExpression getAbsoluteBindingPathExpression() {
        PathExpression absolutePath = new GeneratePathExpression().generateAbsolutePath(bindingPathExpression);
        return absolutePath;
    }

    public SetAlias getBindingVariable() {
        return bindingPathExpression.getStartingVariable();
    }

    public int getId() {
        return this.id;
    }

    public SetNode getBindingNode(INode root) {
        return (SetNode) this.getBindingPathExpression().getLastNode(root);
    }

    public List<VariablePathExpression> getAttributes(INode root) {
        return new FindPathsInVariable().findAttributePaths(this, root);
    }

    public List<VariablePathExpression> getFirstLevelAttributes(INode root) {
        return new FindPathsInVariable().findFirstLevelAttributePaths(this, root);
    }

    public List<VariablePathExpression> getChildrenPaths(INode root) {
        return new FindPathsInVariable().findChidrenPaths(this, root);
    }

    public List<SetAlias> getGenerators() {
        return new FindVariableGenerators().findVariableGenerators(this);
    }

    public List<VariableSelectionCondition> getSelectionConditions() {
        return selectionConditions;
    }

    public void addSelectionCondition(VariableSelectionCondition selectionCondition) {
        this.selectionConditions.add(selectionCondition);
    }

    public VariableProvenanceCondition getProvenanceCondition() {
        return provenanceCondition;
    }

    public void setProvenanceCondition(VariableProvenanceCondition provenanceCondition) {
        this.provenanceCondition = provenanceCondition;
    }

    public IAlgebraOperator getAlgebraTree() {
//            return new GenerateAlgebraTree().generateTreeForVariable(this);
        if (algebraTreeRoot == null) {
            algebraTreeRoot = new GenerateAlgebraTree().generateTreeForVariable(this);
        }
        return algebraTreeRoot;
    }

    public void accept(IVariablePathVisitor pathVisitor) {
        pathVisitor.visitVariable(this);
    }

    public SetAlias clone() {
        SetAlias clone = null;
        try {
            clone = (SetAlias) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
        clone.bindingPathExpression = bindingPathExpression.clone();
        clone.selectionConditions = new ArrayList<VariableSelectionCondition>();
        for (VariableSelectionCondition selectionCondition : selectionConditions) {
            clone.selectionConditions.add(selectionCondition.clone());
        }
        if (provenanceCondition != null) {
            clone.provenanceCondition = provenanceCondition.clone();
        }
        return clone;
    }

    public SetAlias cloneWithoutAlgebraTree() {
        SetAlias clone = this.clone();
        clone.algebraTreeRoot = null;
        return clone;
    }

    public SetAlias cloneWithoutAlgebraTreeAndWithNewId() {
        SetAlias clone = this.clone();
        clone.algebraTreeRoot = null;
        clone.id = getNextId();
        clone.selectionConditions = new ArrayList<VariableSelectionCondition>();
        RenameSetAliasesUtility renamer = new RenameSetAliasesUtility();
        for (VariableSelectionCondition selectionCondition : this.selectionConditions) {
            clone.addSelectionCondition(renamer.generateNewSelection(selectionCondition, clone));
        }
        return clone;
    }

    public void changeId() {
        id = getNextId();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SetAlias)) {
            return false;
        }
        SetAlias otherVariable = (SetAlias) obj;
        if ((this.provenanceCondition != null && otherVariable.provenanceCondition == null)
                || (this.provenanceCondition == null && otherVariable.provenanceCondition != null)) {
            return false;
        }
        boolean equalProvenance = true;
        if (this.provenanceCondition != null) {
            equalProvenance = this.getProvenanceCondition().equals(otherVariable.getProvenanceCondition());
        }
        boolean equalSelections = SpicyEngineUtility.equalLists(this.selectionConditions, otherVariable.selectionConditions);
        boolean equalPath = this.getAbsoluteBindingPathExpression().equals(((SetAlias) obj).getAbsoluteBindingPathExpression());
        boolean result = equalProvenance && equalSelections && equalPath;
        if (logger.isDebugEnabled()) logger.debug("Testing equality of variables: " + this + " and " + obj + " - Result: " + result);
        return result;
    }

    public boolean equalsUpToProvenance(SetAlias otherVariable) {
        PathExpression thisPath = bindingPathExpression.getAbsolutePath();
        PathExpression otherPath = otherVariable.bindingPathExpression.getAbsolutePath();
        return thisPath.equals(otherPath);
    }

    public boolean isClone(SetAlias otherVariable) {
        PathExpression thisPathWithoutClones = SpicyEngineUtility.getAbsolutePathWithoutClones(bindingPathExpression);
        PathExpression otherPathWithoutClones = SpicyEngineUtility.getAbsolutePathWithoutClones(otherVariable.bindingPathExpression);
        return thisPathWithoutClones.equals(otherPathWithoutClones) && (this.id != otherVariable.id);
    }

    public boolean equalsOrIsClone(SetAlias otherVariable) {
        PathExpression thisPathWithoutClones = SpicyEngineUtility.getAbsolutePathWithoutClones(bindingPathExpression);
        PathExpression otherPathWithoutClones = SpicyEngineUtility.getAbsolutePathWithoutClones(otherVariable.bindingPathExpression);
        return thisPathWithoutClones.equals(otherPathWithoutClones);
    }

    public boolean hasSameId(SetAlias otherVariable) {
        return this.id == otherVariable.id;
    }

    public boolean equalsAndHasSameId(SetAlias otherVariable) {
        return this.equals(otherVariable) && this.hasSameId(otherVariable);
    }

    public int hashCode() {
        return this.id;
    }

    public int compareTo(SetAlias o) {
        if (this.id != o.id) {
            return (this.id - o.id);
        }
        return (this.provenanceCondition + "").compareTo(o.provenanceCondition + "");
    }

    public String toString() {
        VariableToString toStringOperator = new VariableToString();
        return "<" + toStringOperator.toString(this) + ">";
    }

    public String toStringNoConditions() {
        VariableToString toStringOperator = new VariableToString();
        return toStringOperator.toStringNoConditions(this);
    }

    public String toShortString() {
        return variableId(this.id);
    }

    public String toShortStringWithProvenance() {
        StringBuilder result = new StringBuilder();
        result.append(variableId(this.id));
        if (this.provenanceCondition != null) {
            result.append("p").append(provenanceCondition.getProvenance());
        }
        return result.toString();
    }

    public String toShortStringWithDollar() {
        return "$" + toShortString();
    }
}
