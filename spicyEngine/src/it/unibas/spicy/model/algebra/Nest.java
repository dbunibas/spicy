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
 
package it.unibas.spicy.model.algebra;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.values.INullValue;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.TGDGeneratorsMap;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Nest extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(Nest.class);
    
    private MappingTask mappingTask;
    private FORule tgd;
    private IDataSourceProxy target;
    private TGDGeneratorsMap generators;
    private Map<String, INode> nodeCache;

    public Nest(FORule tgd, IDataSourceProxy target, TGDGeneratorsMap generators, MappingTask mappingTask) {
        this.tgd = tgd;
        this.target = target;
        this.generators = generators;
        this.mappingTask = mappingTask;
    }

    public IDataSourceProxy getTarget() {
        return target;
    }

    public TGDGeneratorsMap getGenerators() {
        return generators;
    }

    public String getName() {
        return "nest";
    }

    public Map<String, INode> getNodeCache() {
        return nodeCache;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitNest(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (logger.isDebugEnabled()) logger.debug("Starting nest for TGD: " + tgd);
        if (logger.isDebugEnabled()) logger.debug("On data source: " + dataSource.toLongString());
        nodeCache = new HashMap<String, INode>();
        IDataSourceProxy child = children.get(0).execute(dataSource);
        IDataSourceProxy nestResult = new NestOperator().nest(child, tgd, target, generators, nodeCache, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("Final result: " + nestResult.toLongString());
        return nestResult;
    }
}

class NestOperator {

    private static Log logger = LogFactory.getLog(NestOperator.class);

    private EvaluateExpression evaluator = new EvaluateExpression();

    IDataSourceProxy nest(IDataSourceProxy sourceView, FORule tgd, IDataSourceProxy target, TGDGeneratorsMap generators, Map<String, INode> nodeCache, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Starting nest operator: " + sourceView.toInstanceString());
        if (logger.isTraceEnabled()) logger.debug("Generators: " + generators);
        INode targetSchemaClone = target.getIntermediateSchema().clone();
        IDataSourceProxy result = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, targetSchemaClone));
        for (int i = 0; i < sourceView.getInstances().size(); i++) {
            INode sourceViewInstance = sourceView.getInstances().get(i);
            if (isEmpty(sourceViewInstance)) {
                continue;
            }
            for (INode sourceViewTuple : sourceViewInstance.getChildren()) {
                if (logger.isTraceEnabled()) logger.trace("Analyzing source tuple: " + sourceViewTuple);
                for (SetAlias targetVariable : tgd.getTargetView().getVariables()) {
                    NestSourceViewTupleVisitor visitor = 
                            new NestSourceViewTupleVisitor(tgd, sourceViewTuple, i, nodeCache, generators.getGeneratorsForVariable(targetVariable), result, mappingTask);
                    targetSchemaClone.accept(visitor);
                }
                if (logger.isTraceEnabled()) logger.trace("Current result of nest: " + result.toInstanceString());
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Final result of nest: " + result.toInstanceString());
        return result;
    }

    private boolean isEmpty(INode sourceViewInstance) {
        return (sourceViewInstance.getChildren().isEmpty());
    }

}

class NestSourceViewTupleVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(NestSourceViewTupleVisitor.class);
    private FORule tgd;
    private INode sourceTuple;
    private int instanceId;
    private MappingTask mappingTask;
    private Map<String, INode> nodeCache;
    private Map<PathExpression, IValueGenerator> generators;
    private IDataSourceProxy nestResult;
    private INode resultingInstance;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    NestSourceViewTupleVisitor(FORule tgd, INode sourceTuple, int instanceId, Map<String, INode> nodeCache,
            Map<PathExpression, IValueGenerator> generators, IDataSourceProxy nestResult, MappingTask mappingTask) {
        this.tgd = tgd;
        this.sourceTuple = sourceTuple;
        this.instanceId = instanceId;
        this.nodeCache = nodeCache;
        this.generators = generators;
        this.nestResult = nestResult;
        this.mappingTask = mappingTask;
    }

    public void visitSetNode(SetNode node) {
        visitIntermediateNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitIntermediateNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitIntermediateNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitIntermediateNode(node);
    }

    private void visitIntermediateNode(INode node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting intermediate node: " + node.getLabel());
        Object value = generateValue(node);
        if (value instanceof INullValue) {
            if (logger.isDebugEnabled()) logger.debug("Node has null generator, returning...");
            return;
        }
        String key = generateKey(node);
        if (logger.isTraceEnabled()) logger.trace("Node key: " + key);
        INode nodeInstance = nodeCache.get(key);
        if (nodeInstance == null) {
            OID oid = (OID) value;
            String instanceNodeLabel = generateInstanceNodeLabel(node);
            nodeInstance = SpicyEngineUtility.createNode(node.getClass().getSimpleName(), instanceNodeLabel, oid);
            nodeCache.put(key, nodeInstance);
            if (logger.isTraceEnabled()) logger.trace("Node not found in cache. Creating node");
            if (logger.isTraceEnabled()) logger.trace("OID: " + oid);
            if (logger.isDebugEnabled()) logger.debug("Created new intermediate node: " + nodeInstance);
            if (node.isRoot()) {
                if (logger.isDebugEnabled()) logger.debug("Node is root");
                nodeInstance.setRoot(true);
                nodeInstance.addAnnotation(SpicyEngineConstants.SOURCE_INSTANCE_POSITION, instanceId);
                nestResult.addInstance(nodeInstance);
            } else {
                INode fatherInstance = findFatherInstance(node);
                fatherInstance.addChild(nodeInstance);
                if (logger.isDebugEnabled()) logger.debug("Father instance: " + fatherInstance.getLabel());
            }
        }
        if (nodeInstance instanceof TupleNode) {
            addProvenance((TupleNode) nodeInstance);
        }
        if (node.isRoot()) {
            resultingInstance = nodeInstance;
        }
        visitChildren(node);
    }

    private void addProvenance(TupleNode tupleNode) {
        tupleNode.addProvenance(tgd.getId());
    }

    private void visitChildren(final INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        String key = generateKey(node);
        if (logger.isDebugEnabled()) logger.debug("Visiting attribute: " + node.getLabel());
        if (logger.isTraceEnabled()) logger.trace("Node key: " + key);
        INode nodeInstance = nodeCache.get(key);
        if (nodeInstance == null) {
            OID oid = (OID) generateValue(node);
            if (logger.isTraceEnabled()) logger.trace("Node not found in cache. Creating node");
            if (logger.isTraceEnabled()) logger.trace("Node oid: " + oid);
            nodeInstance = SpicyEngineUtility.createNode(node.getClass().getSimpleName(), node.getLabel(), oid);
            nodeCache.put(key, nodeInstance);
            INode fatherInstance = findFatherInstance(node);
            fatherInstance.addChild(nodeInstance);
            INode leafNode = node.getChild(0);
//            IValueGenerator leafGenerator = getGenerator(leafNode);
            Object value = generateValue(leafNode);
            INode leafNodeInstance = SpicyEngineUtility.createNode(leafNode.getClass().getSimpleName(), leafNode.getLabel(), value);
            nodeInstance.addChild(leafNodeInstance);
            if (logger.isDebugEnabled()) logger.debug("Created new attribute node: " + nodeInstance);
            if (logger.isDebugEnabled()) logger.debug("Father instance: " + fatherInstance.getLabel());
            if (logger.isDebugEnabled()) logger.debug("Current instance: " + this.resultingInstance);
        }
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private String generateInstanceNodeLabel(INode schemaNode) {
        String label = schemaNode.getLabel();
        // codice introdotto per i cloni
//        if (schemaNode instanceof SetNode) {
//            if (((SetNode)schemaNode).getClones().size() != 0) {
//                label = DuplicateSet.getCloneLabel(0, (SetNode)schemaNode);
//            }
//        }
        return label;
    }

    private Object generateValue(INode node) {
        IValueGenerator nodeGenerator = getGenerator(node);
        return nodeGenerator.generateValue((TupleNode)sourceTuple, mappingTask);
    }

    private IValueGenerator getGenerator(INode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        return generators.get(nodePath);
    }

    private String generateKey(INode node) {
        if (node.getFather() == null) {
            return instanceId + SpicyEngineConstants.ALGEBRA_SEPARATOR + generateValue(node);
        }
        return generateKey(node.getFather()) + SpicyEngineConstants.ALGEBRA_SEPARATOR + generateValue(node);
    }

    private INode findFatherInstance(INode node) {
        INode fatherNode = node.getFather();
        if (fatherNode == null) {
            return null;
        }
        String fatherKey = generateKey(fatherNode);
        INode fatherInstance = nodeCache.get(fatherKey);
        return fatherInstance;
    }

    public Object getResult() {
        return null;
    }
}
