/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com
Donatello Santoro - donatello.santoro@gmail.com

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
package it.unibas.spicy.model.mapping.proxies;

import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.operators.CheckNewJoin;
import it.unibas.spicy.model.datasource.operators.DuplicateSet;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.operators.RemoveDuplicateSet;
import it.unibas.spicy.model.mapping.DataSourceMappingData;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.operators.GenerateSetVariables;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.CheckPathContainment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataSourceProxy implements IDataSourceProxy {

    // mapping artifacts
    protected List<FunctionalDependency> functionalDependencies = new ArrayList<FunctionalDependency>();
    protected List<Duplication> duplications = new ArrayList<Duplication>();
    protected List<JoinCondition> joinConditions = new ArrayList<JoinCondition>();
    protected List<SelectionCondition> selectionConditions = new ArrayList<SelectionCondition>();
    protected List<PathExpression> inclusions = new ArrayList<PathExpression>();
    protected List<PathExpression> exclusions = new ArrayList<PathExpression>();
    // results of the mapping process (these are not cloned)
    protected DataSourceMappingData mappingData = null;
    protected INode intermediateSchema;
    protected List<INode> intermediateInstances = new ArrayList<INode>();
    protected Map<String, Object> annotations = new HashMap<String, Object>();
    protected boolean modified;
    protected boolean toBeSaved;

    public DataSourceMappingData getMappingData() {
        if (this.mappingData == null || this.isModified()) {
            this.mappingData = new DataSourceMappingData(this);
            this.modified = false;
        }
        return this.mappingData;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        if (modified) {
            this.setToBeSaved(true);
        }
    }

    public boolean isToBeSaved() {
        return toBeSaved;
    }

    public boolean isNested() {
        return this.getMappingData().isNested();
    }

    public void setToBeSaved(boolean toBeSaved) {
        this.toBeSaved = toBeSaved;
    }

    public String getType() {
        return this.getDataSource().getType();
    }

    public void setType(String type) {
        this.getDataSource().setType(type);
    }

    public void addKeyConstraint(KeyConstraint keyConstraint) {
        this.getDataSource().addKeyConstraint(keyConstraint);
        setModified(true);
    }

    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint) {
        this.getDataSource().addForeignKeyConstraint(foreignKeyConstraint);
        setModified(true);
    }

    public List<KeyConstraint> getKeyConstraints() {
        return this.getDataSource().getKeyConstraints();
    }

    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        return this.getDataSource().getForeignKeyConstraints();
    }

    public void addInstance(INode instance) {
        this.getDataSource().addInstance(instance);
        setModified(true);
    }

    public void addInstanceWithCheck(INode instance) {
        this.getDataSource().addInstanceWithCheck(instance);
        setModified(true);
    }

    public List<INode> getInstances() {
        return this.getDataSource().getInstances();
    }

    public List<INode> getOriginalInstances() {
        return this.getDataSource().getOriginalInstances();
    }

    public void generateIntermediateSchema() {
        this.intermediateSchema = this.getDataSource().getSchema().clone();
    }

    public INode getSchema() {
        return this.getDataSource().getSchema();
    }

    public INode getIntermediateSchema() {
        if (this.intermediateSchema == null) {
            generateIntermediateSchema();
        }
        return this.intermediateSchema;
    }

    public Boolean addJoinForForeignKey(ForeignKeyConstraint foreignKeyConstraint) {
        Boolean result = new CheckNewJoin().addJoinConditionForForeignKey(foreignKeyConstraint, this);
        if (result.equals(Boolean.TRUE)) {
            setModified(true);
        }
        return result;
    }

    public Boolean addJoinCondition(JoinCondition joinCondition) {
        Boolean result = new CheckNewJoin().addJoinCondition(joinCondition, this);
        if (result.equals(Boolean.TRUE)) {
            setModified(true);
        }
        return result;
    }

    public void removeJoinCondition(JoinCondition joinCondition) {
        this.joinConditions.remove(joinCondition);
        setModified(true);
    }

    public void setForeignKeyForJoin(JoinCondition joinCondition, boolean foreignKey) {
        if (!this.joinConditions.contains(joinCondition)) {
            throw new IllegalArgumentException("Join condition does not exist in data source: " + joinCondition + "\n" + this);
        }
        joinCondition.setMonodirectional(foreignKey);
        setModified(true);
    }

    public void setMandatoryForJoin(JoinCondition joinCondition, boolean mandatory) {
        if (!this.joinConditions.contains(joinCondition)) {
            throw new IllegalArgumentException("Join condition does not exist in data source: " + joinCondition + "\n" + this);
        }
        joinCondition.setMandatory(mandatory);
        setModified(true);
    }

    public void addFunctionalDependency(FunctionalDependency functionalDependency) {
        this.functionalDependencies.add(functionalDependency);
        setModified(true);
    }

    public void removeFunctionalDependency(FunctionalDependency functionalDependency) {
        this.functionalDependencies.remove(functionalDependency);
        setModified(true);
    }

    public PathExpression addDuplication(PathExpression duplication) {
        DuplicateSet setDuplicator = new DuplicateSet();
        PathExpression newPath = setDuplicator.duplicateSet(duplication, this);
        setModified(true);
        return newPath;
    }

    public void removeDuplication(PathExpression duplication) {
        RemoveDuplicateSet remover = new RemoveDuplicateSet();
        remover.removeDuplicateSet(duplication, this);
        setModified(true);
    }

    public void addSelectionCondition(SelectionCondition selectionCondition) {
        selectionConditions.add(selectionCondition);
        setModified(true);
    }

    public void removeSelectionCondition(SelectionCondition selectionCondition) {
        selectionConditions.remove(selectionCondition);
        setModified(true);
    }

    public List<INode> getIntermediateInstances() {
        return this.intermediateInstances;
    }

    public void addIntermediateInstance(INode instance) {
        this.intermediateInstances.add(instance);
    }

    public void clearIntermediateInstances() {
        this.intermediateInstances.clear();
    }

    public void addInclusion(PathExpression inclusionPath) {
        FindNode nodeFinder = new FindNode();
        assert (nodeFinder.findNodeInSchema(inclusionPath, this) != null) : "Incorrect path: " + inclusionPath;
        this.inclusions.add(inclusionPath);
        setModified(true);
    }

    public void addExclusion(PathExpression exclusionPath) {
        FindNode nodeFinder = new FindNode();
        assert (nodeFinder.findNodeInSchema(exclusionPath, this) != null) : "Incorrect path: " + exclusionPath;
        CheckPathContainment pathContainmentChecker = new CheckPathContainment();
        assert (pathContainmentChecker.correctExclusion(exclusionPath, inclusions)) : "Exclusions must be relative to inclusions: " + exclusionPath + " - Inclusions: " + inclusions;
        this.exclusions.add(exclusionPath);
        setModified(true);
    }

    public void addAnnotation(String key, Object value) {
        this.annotations.put(key, value);
    }

    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }

    public Object removeAnnotation(String key) {
        return this.annotations.remove(key);
    }

    public Map<String, Object> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, Object> annotations) {
        this.annotations = annotations;
    }

    public List<PathExpression> getAbsoluteSetPaths() {
        return new GenerateSetVariables().findSetAbsolutePaths(this);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------------- DATA SOURCE  (").append(getType()).append(" - ").append(getProviderType()).append(") -----------------\n");
        result.append(this.getIntermediateSchema());
        result.append(constraintsString());
        result.append(duplicationsString());
        result.append(selectionString());
        result.append(joinConditionsString());
        result.append(inclusionsString());
        return result.toString();
    }

    private String constraintsString() {
        StringBuilder result = new StringBuilder();
        if (!getDataSource().getKeyConstraints().isEmpty()) {
            result.append("============= Key Constraints =================\n");
            for (KeyConstraint keyConstraint : getDataSource().getKeyConstraints()) {
                result.append(keyConstraint);
                result.append("\n");
            }
        }
        if (!getFunctionalDependencies().isEmpty()) {
            result.append("============= Functional Dependencies =================\n");
            for (FunctionalDependency functionalDependency : getFunctionalDependencies()) {
                result.append(functionalDependency);
                result.append("\n");
            }
        }
        if (!getForeignKeyConstraints().isEmpty()) {
            result.append("============= Foreign Key Constraints =================\n");
            for (ForeignKeyConstraint foreignKeyConstraint : getForeignKeyConstraints()) {
                result.append(foreignKeyConstraint);
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String joinConditionsString() {
        StringBuilder result = new StringBuilder();
        if (!getJoinConditions().isEmpty()) {
            result.append("============= Join Conditions =================\n");
            for (JoinCondition joinCondition : getJoinConditions()) {
                result.append(joinCondition);
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String duplicationsString() {
        StringBuilder result = new StringBuilder();
        if (!getDuplications().isEmpty()) {
            result.append("============= Duplications =================\n");
            for (Duplication duplication : getDuplications()) {
                result.append(duplication);
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String selectionString() {
        StringBuilder result = new StringBuilder();
        if (!getSelectionConditions().isEmpty()) {
            result.append("============= Selection Conditions =================\n");
            for (SelectionCondition selection : getSelectionConditions()) {
                result.append(selection);
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String inclusionsString() {
        StringBuilder result = new StringBuilder();
        if (!this.getInclusions().isEmpty()) {
            result.append("============= Inclusion list =================\n");
            for (PathExpression inclusionPath : getInclusions()) {
                result.append(inclusionPath);
                result.append("\n");
            }
        }
        if (!this.getExclusions().isEmpty()) {
            result.append("============= Exclusion list =================\n");
            for (PathExpression exclusionPath : getExclusions()) {
                result.append(exclusionPath);
                result.append("\n");
            }
        }
        return result.toString();
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(this.toString());
        result.append("============= Instances =================\n");
        for (INode instanceRoot : this.getInstances()) {
            result.append(instanceRoot);
            result.append("\n");
        }
        return result.toString();
    }

    public String toInstanceString() {
        StringBuilder result = new StringBuilder();
        result.append("============= Instances =================\n");
        for (INode instanceRoot : this.getInstances()) {
            result.append(instanceRoot.toShortString());
            result.append("\n");
        }
        return result.toString();
    }

    public String toIntermediateInstancesString() {
        StringBuilder result = new StringBuilder();
        if (getIntermediateInstances().isEmpty()) {
            result.append("============= No Intermediate Instances =================\n");
        } else {
            result.append("============= Intermediate Instances =================\n");
            for (INode instanceRoot : this.getIntermediateInstances()) {
                result.append(instanceRoot.toShortString());
                result.append("\n");
            }
        }
        return result.toString();
    }

    public String toLongStringWithOids() {
        StringBuilder result = new StringBuilder();
        result.append(this.toString());
        result.append("============= Instances =================\n");
        for (INode instanceRoot : this.getInstances()) {
            result.append(instanceRoot.toStringWithOids());
            result.append("\n");
        }
        return result.toString();
    }
}
