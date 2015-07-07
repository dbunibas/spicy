/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
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
package it.unibas.spicy.model.mapping.rewriting.sourcenulls;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.proxies.ChainingDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.MergeDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FindNullablePathsInSource {

    private static Log logger = LogFactory.getLog(FindNullablePathsInSource.class);
    private FindNode nodeFinder = new FindNode();

    public PathClassification classifyPaths(IDataSourceProxy sourceProxy) {
        if (sourceProxy instanceof ConstantDataSourceProxy) {
            return classifyPathsInDataSource(sourceProxy);
        }
        if (sourceProxy instanceof ChainingDataSourceProxy) {
            return classifyPathsInChainingSource(sourceProxy);
        }
        if (sourceProxy instanceof MergeDataSourceProxy) {
            return classifyPathsInMergeProxy(sourceProxy);
        }
        return null;
    }

    
    //////////////////////////   CONSTANT DATA SOURCE  //////////////////////////////////
    
    @SuppressWarnings("unchecked")
    private PathClassification classifyPathsInDataSource(IDataSourceProxy sourceProxy) {
        List<PathExpression> nullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> notNullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> allAttributes = findAllAttributes(sourceProxy);
        for (PathExpression pathExpression : allAttributes) {
            INode attributeNode = nodeFinder.findNodeInSchema(pathExpression, sourceProxy);
            if (attributeNode.isNotNull()) {
                notNullablePaths.add(pathExpression);
            } else {
                nullablePaths.add(pathExpression);
            }
        }
        return new PathClassification(sourceProxy, nullablePaths, notNullablePaths, Collections.EMPTY_LIST);
    }

    //////////////////////////   CHAINING DATA SOURCE  //////////////////////////////////

    @SuppressWarnings("unchecked")
    private PathClassification classifyPathsInChainingSource(IDataSourceProxy sourceProxy) {
        ChainingDataSourceProxy chainingProxy = (ChainingDataSourceProxy) sourceProxy;
        MappingTask mappingTask = chainingProxy.getMappingTask();
        IDataSourceProxy previousSourceProxy = mappingTask.getSourceProxy();
        List<PathExpression> nullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> notNullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> alwaysNullPaths = new ArrayList<PathExpression>();
        List<PathExpression> allAttributes = findAllAttributes(sourceProxy);
        for (PathExpression pathExpression : allAttributes) {
            List<VariableCorrespondence> correspondencesForPath = findCorrespondencesForPath(pathExpression, mappingTask);
            if (correspondencesForPath.isEmpty()) {
                alwaysNullPaths.add(pathExpression);                
            } else {
                if (allSourcePathsAreNotNull(correspondencesForPath, previousSourceProxy)) {
                    notNullablePaths.add(pathExpression);
                } else {
                    nullablePaths.add(pathExpression);
                }
            }

        }
        return new PathClassification(sourceProxy, nullablePaths, notNullablePaths, Collections.EMPTY_LIST);
    }

    //////////////////////////   MERGE DATA SOURCE  //////////////////////////////////
 
    private PathClassification classifyPathsInMergeProxy(IDataSourceProxy sourceProxy) {
        MergeDataSourceProxy mergeProxy = (MergeDataSourceProxy)sourceProxy;
        List<PathExpression> nullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> notNullablePaths = new ArrayList<PathExpression>();
        List<PathExpression> alwaysNullPaths = new ArrayList<PathExpression>();
        for (int i = 0; i < mergeProxy.getProxies().size(); i++) {
            IDataSourceProxy componentProxy = mergeProxy.getProxies().get(i);
            boolean relational = !componentProxy.getMappingData().isNested();
            PathClassification classificationForComponent = this.classifyPaths(componentProxy);
            nullablePaths.addAll(modifyPaths(classificationForComponent.getNullablePaths(), i, relational));
            notNullablePaths.addAll(modifyPaths(classificationForComponent.getNotNullPaths(), i, relational));
            alwaysNullPaths.addAll(modifyPaths(classificationForComponent.getAlwaysNullPaths(), i, relational));
        }
        return new PathClassification(sourceProxy, nullablePaths, notNullablePaths, alwaysNullPaths);
    }

    private List<PathExpression> findAllAttributes(IDataSourceProxy sourceProxy) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        List<SetAlias> setAliases = sourceProxy.getMappingData().getVariables();
        for (SetAlias alias : setAliases) {
            List<VariablePathExpression> setAttributes = alias.getAttributes(sourceProxy.getIntermediateSchema());
            for (VariablePathExpression attributePath : setAttributes) {
                PathExpression absoluteAttributePathWithoutClones = SpicyEngineUtility.removeClonesFromAbsolutePath(attributePath.getAbsolutePath());
                if (!result.contains(absoluteAttributePathWithoutClones)) {
                    result.add(absoluteAttributePathWithoutClones);
                }
            }
        }
        return result;
    }

    private List<VariableCorrespondence> findCorrespondencesForPath(PathExpression pathExpression, MappingTask mappingTask) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (FORule rule : mappingTask.getMappingData().getRewrittenRules()) {
            for (VariableCorrespondence correspondence : rule.getCoveredCorrespondences()) {
                if (correspondence.getTargetPath().getAbsolutePath().equalsUpToClones(pathExpression)) {
                    result.add(correspondence);
                }
            }
        }
        return result;
    }

    private boolean allSourcePathsAreNotNull(List<VariableCorrespondence> correspondencesForPath, IDataSourceProxy previousSourceProxy) {
        PathClassification previousClassification = this.classifyPaths(previousSourceProxy);
        for (VariableCorrespondence correspondence : correspondencesForPath) {
            if (correspondence.isConstant()) {
                continue;
            }
            if (!allSourcePathsAreNotNull(correspondence.getSourcePaths(), previousClassification.getNotNullPaths())) {
                return false;
            }
        }
        return true;
    }

    private boolean allSourcePathsAreNotNull(List<VariablePathExpression> sourcePaths, List<PathExpression> notNullPaths) {
        for (VariablePathExpression variablePathExpression : sourcePaths) {
            PathExpression absolutePathWithoutClones = SpicyEngineUtility.removeClonesFromAbsolutePath(variablePathExpression.getAbsolutePath());
            if (!notNullPaths.contains(absolutePathWithoutClones)) {
                return false;
            }
        }
        return true;
    }

    private List<PathExpression> modifyPaths(List<PathExpression> pathSet, int i, boolean relational) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        for (PathExpression pathExpression : pathSet) {
            PathExpression newPath = pathExpression.clone();
            SpicyEngineUtility.changePathExpressionForMerge(newPath, i, relational);
            result.add(newPath);
        }
        return result;
    }
}

class PathClassification {

    private IDataSourceProxy sourceProxy;
    private List<PathExpression> nullablePaths;
    private List<PathExpression> notNullPaths;
    private List<PathExpression> alwaysNullPaths;

    public PathClassification(IDataSourceProxy sourceProxy, List<PathExpression> nullablePaths, List<PathExpression> notNullPaths, List<PathExpression> alwaysNullPaths) {
        this.sourceProxy = sourceProxy;
        this.nullablePaths = nullablePaths;
        this.notNullPaths = notNullPaths;
        this.alwaysNullPaths = alwaysNullPaths;
    }

    public List<PathExpression> getAlwaysNullPaths() {
        return alwaysNullPaths;
    }

    public List<PathExpression> getNotNullPaths() {
        return notNullPaths;
    }

    public List<PathExpression> getNullablePaths() {
        return nullablePaths;
    }

    public IDataSourceProxy getSourceProxy() {
        return sourceProxy;
    }
}
