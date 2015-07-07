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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.exceptions.IllegalDataSourceException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MergeDataSourceProxy extends AbstractDataSourceProxy {

    private MergeDataSourcesOperator merger = new MergeDataSourcesOperator();
    private List<IDataSourceProxy> proxies = new ArrayList<IDataSourceProxy>();
    private DataSource dataSource;

    public MergeDataSourceProxy(List<IDataSourceProxy> proxies) {
        this.proxies = proxies;
    }

    public List<IDataSourceProxy> getProxies() {
        return proxies;
    }

    public DataSource getDataSource() {
        boolean wasToBeSaved = this.isToBeSaved();
        if (this.dataSource == null || this.isModified()) {
            this.dataSource = merger.mergeDataSources(proxies, areRelational(proxies));
            this.modified = false;
            this.setToBeSaved(wasToBeSaved);
        }
        return this.dataSource;
    }

    private boolean areRelational(List<IDataSourceProxy> dataSourcesProxies) {
        for (IDataSourceProxy proxy : dataSourcesProxies) {
            if (proxy.getMappingData().isNested()) {
                return false;
            }
        }
        return true;
    }

    public String getProviderType() {
        return SpicyEngineConstants.PROVIDER_TYPE_MERGE;
    }

    public boolean isModified() {
        if (super.modified) {
            return true;
        }
        for (IDataSourceProxy proxy : proxies) {
            if (proxy.isModified()) {
                return true;
            }
        }
        return false;
    }

    public List<JoinCondition> getJoinConditions() {
        List<JoinCondition> result = new ArrayList<JoinCondition>(this.joinConditions);
        result.addAll(merger.mergeJoinConditions(proxies, areRelational(proxies)));
        return result;
    }

    public List<SelectionCondition> getSelectionConditions() {
        List<SelectionCondition> result = new ArrayList<SelectionCondition>(this.selectionConditions);
        result.addAll(merger.mergeSelectionConditions(proxies, areRelational(proxies)));
        return result;
    }

    public List<FunctionalDependency> getFunctionalDependencies() {
        List<FunctionalDependency> result = new ArrayList<FunctionalDependency>(this.functionalDependencies);
        result.addAll(merger.mergeFunctionalDependencies(proxies, areRelational(proxies)));
        return result;
    }

    public List<Duplication> getDuplications() {
        List<Duplication> result = new ArrayList<Duplication>(this.duplications);
        result.addAll(merger.mergeDuplications(proxies, areRelational(proxies)));
        return result;
    }

    public List<PathExpression> getExclusions() {
        List<PathExpression> result = new ArrayList<PathExpression>(this.exclusions);
//        result.addAll(merger.mergeExclusions(proxies, areRelational(proxies)));
        return result;
    }

    public List<PathExpression> getInclusions() {
        List<PathExpression> result = new ArrayList<PathExpression>(this.inclusions);
//        result.addAll(merger.mergeInclusions(proxies, areRelational(proxies)));
        return result;
    }
}

class MergeDataSourcesOperator {

    private static Log logger = LogFactory.getLog(MergeDataSourceProxy.class);
    private IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();

    public DataSource mergeDataSources(List<IDataSourceProxy> dataSourcesProxies, boolean relational) {
        INode root = new TupleNode(SpicyEngineConstants.MERGE_ROOT_LABEL);
        root.setRoot(true);
        DataSource result = new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, root);
        for (int i = 0; i < dataSourcesProxies.size(); i++) {
            IDataSourceProxy proxy = dataSourcesProxies.get(i);
            addSetsToDataSource(proxy, i, result, relational);
            addConstraintsToDataSource(proxy, i, result, relational);
        }
        INode rootIstance = new TupleNode(SpicyEngineConstants.MERGE_ROOT_LABEL, oidGenerator.getNextOID());
        rootIstance.setRoot(true);
        result.addInstance(rootIstance);
        for (int i = 0; i < dataSourcesProxies.size(); i++) {
            IDataSourceProxy proxy = dataSourcesProxies.get(i);
            addInstancesToDataSource(proxy, i, result, relational);
        }
        return result;
    }

    private void addSetsToDataSource(IDataSourceProxy proxy, int i, DataSource dataSource, boolean relational) {
        INode schema = proxy.getDataSource().getSchema();
        if(logger.isDebugEnabled())logger.debug("Original schema: " + schema);
        INode schemaClone = schema.clone();
        if (relational) {
            for (INode setNode : schemaClone.getChildren()) {
                setNode.setLabel(SpicyEngineUtility.generateNewLabelForMerge(setNode.getLabel(), i));
                dataSource.getSchema().addChild(setNode);
            }
        } else {
            schemaClone.setRoot(false);
            schemaClone.setLabel(SpicyEngineUtility.generateNewLabelForMerge(schemaClone.getLabel(), i));
            dataSource.getSchema().addChild(schemaClone);
        }
    }

    private void addConstraintsToDataSource(IDataSourceProxy proxy, int i, DataSource result, boolean relational) {
        Map<KeyConstraint, KeyConstraint> keyMap = new HashMap<KeyConstraint, KeyConstraint>();
        for (KeyConstraint key : proxy.getDataSource().getKeyConstraints()) {
            KeyConstraint newKey = generateNewKeyConstraint(key, i, relational);
            result.addKeyConstraint(newKey);
            keyMap.put(key, newKey);
        }
        for (ForeignKeyConstraint foreignKey : proxy.getDataSource().getForeignKeyConstraints()) {
            ForeignKeyConstraint newFK = generateNewForeignKeyConstraint(foreignKey, i, keyMap, relational);
            result.addForeignKeyConstraint(newFK);
        }
    }

    private KeyConstraint generateNewKeyConstraint(KeyConstraint key, int i, boolean relational) {
        List<PathExpression> newPaths = new ArrayList<PathExpression>();
        for (PathExpression path : key.getKeyPaths()) {
            PathExpression newPath = path.clone();
            SpicyEngineUtility.changePathExpressionForMerge(newPath, i, relational);
            newPaths.add(newPath);
        }
        KeyConstraint newKey = new KeyConstraint(newPaths);
        return newKey;
    }

    private ForeignKeyConstraint generateNewForeignKeyConstraint(ForeignKeyConstraint foreignKey, int i, Map<KeyConstraint, KeyConstraint> keyMap,
            boolean relational) {
        List<PathExpression> newPaths = new ArrayList<PathExpression>();
        for (PathExpression path : foreignKey.getForeignKeyPaths()) {
            PathExpression newPath = path.clone();
            SpicyEngineUtility.changePathExpressionForMerge(newPath, i, relational);
            newPaths.add(newPath);
        }
        KeyConstraint newKey = keyMap.get(foreignKey.getKeyConstraint());
        ForeignKeyConstraint newFK = new ForeignKeyConstraint(newKey, newPaths);
        return newFK;
    }

    private void addInstancesToDataSource(IDataSourceProxy proxy, int i, DataSource result, boolean relational) {
        if (proxy.getInstances().isEmpty()) {
            throw new IllegalDataSourceException("Data Source has no instances: " + proxy);
        }
        INode firstInstance = proxy.getInstances().get(0);
        INode newInstance = firstInstance.clone();
        if (relational) {
            for (INode setNode : newInstance.getChildren()) {
                setNode.setLabel(SpicyEngineUtility.generateNewLabelForMerge(setNode.getLabel(), i));
                result.getInstances().get(0).addChild(setNode);
            }
        } else {
            newInstance.setRoot(false);
            newInstance.setLabel(SpicyEngineUtility.generateNewLabelForMerge(newInstance.getLabel(), i));
            result.getInstances().get(0).addChild(newInstance);
        }
    }

    public List<JoinCondition> mergeJoinConditions(List<IDataSourceProxy> proxies, boolean relational) {
        List<JoinCondition> result = new ArrayList<JoinCondition>();
        for (int i = 0; i < proxies.size(); i++) {
            IDataSourceProxy proxy = proxies.get(i);
            result.addAll(changeJoins(proxy, i, relational));
        }
        return result;
    }

    private List<JoinCondition> changeJoins(IDataSourceProxy proxy, int i, boolean relational) {
        List<JoinCondition> result = new ArrayList<JoinCondition>();
        for (JoinCondition joinCondition : proxy.getJoinConditions()) {
            JoinCondition clone = joinCondition.clone();
            for (PathExpression fromPath : clone.getFromPaths()) {
                SpicyEngineUtility.changePathExpressionForMerge(fromPath, i, relational);
            }
            for (PathExpression toPath : clone.getToPaths()) {
                SpicyEngineUtility.changePathExpressionForMerge(toPath, i, relational);
            }
            result.add(clone);
        }
        return result;
    }

    public List<SelectionCondition> mergeSelectionConditions(List<IDataSourceProxy> proxies, boolean relational) {
        List<SelectionCondition> result = new ArrayList<SelectionCondition>();
        for (int i = 0; i < proxies.size(); i++) {
            IDataSourceProxy proxy = proxies.get(i);
            result.addAll(changeSelectionConditions(proxy, i, relational));
        }
        return result;
    }

    private List<SelectionCondition> changeSelectionConditions(IDataSourceProxy proxy, int i, boolean relational) {
        List<SelectionCondition> result = new ArrayList<SelectionCondition>();
        for (SelectionCondition selectionCondition : proxy.getSelectionConditions()) {
            SelectionCondition clone = selectionCondition.clone();
            for (PathExpression setPath : clone.getSetPaths()) {
                SpicyEngineUtility.changePathExpressionForMerge(setPath, i, relational);
            }
            result.add(clone);
        }
        return result;
    }

    public List<FunctionalDependency> mergeFunctionalDependencies(List<IDataSourceProxy> proxies, boolean relational) {
        List<FunctionalDependency> result = new ArrayList<FunctionalDependency>();
        for (int i = 0; i < proxies.size(); i++) {
            IDataSourceProxy proxy = proxies.get(i);
            result.addAll(changeFunctionalDependency(proxy, i, relational));
        }
        return result;
    }

    private List<FunctionalDependency> changeFunctionalDependency(IDataSourceProxy proxy, int i, boolean relational) {
        List<FunctionalDependency> result = new ArrayList<FunctionalDependency>();
        for (FunctionalDependency functionalDependency : proxy.getFunctionalDependencies()) {
            FunctionalDependency clone = functionalDependency.clone();
            for (PathExpression path : clone.getLeftPaths()) {
                SpicyEngineUtility.changePathExpressionForMerge(path, i, relational);
            }
            for (PathExpression path : clone.getRightPaths()) {
                SpicyEngineUtility.changePathExpressionForMerge(path, i, relational);
            }
            result.add(clone);
        }
        return result;
    }

    public List<Duplication> mergeDuplications(List<IDataSourceProxy> proxies, boolean relational) {
        List<Duplication> result = new ArrayList<Duplication>();
        for (int i = 0; i < proxies.size(); i++) {
            IDataSourceProxy proxy = proxies.get(i);
            result.addAll(changeDuplications(proxy, i, relational));
        }
        return result;
    }

    private List<Duplication> changeDuplications(IDataSourceProxy proxy, int i, boolean relational) {
        List<Duplication> result = new ArrayList<Duplication>();
        for (Duplication duplication : proxy.getDuplications()) {
            Duplication clone = duplication.clone();
            SpicyEngineUtility.changePathExpressionForMerge(clone.getOriginalPath(), i, relational);
            SpicyEngineUtility.changePathExpressionForMerge(clone.getClonePath(), i, relational);
            result.add(clone);
        }
        return result;
    }
}
