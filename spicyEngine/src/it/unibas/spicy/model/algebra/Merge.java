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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Merge extends IntermediateOperator {

    public String getName() {
        return "merge";
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitMerge(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (children.isEmpty()) {
            return null;
        }
        List<IDataSourceProxy> tgdResults = new ArrayList<IDataSourceProxy>();
        for (IAlgebraOperator child : children) {
            tgdResults.add(child.execute(dataSource));
        }
        IDataSourceProxy finalResult = new MergeOperator().merge(children, tgdResults);
        return finalResult;
    }
}

class MergeOperator {

    private static Log logger = LogFactory.getLog(MergeOperator.class);

    IDataSourceProxy merge(List<IAlgebraOperator> nestOperators, List<IDataSourceProxy> tgdResults) {
        Nest firstNest = (Nest) nestOperators.get(0);
        IDataSourceProxy firstResult = tgdResults.get(0);
        DataSource resultSource = new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, firstResult.getIntermediateSchema().clone());
        IDataSourceProxy result = new ConstantDataSourceProxy(resultSource);
        addInstancesToResult(result, firstResult);
        if (logger.isDebugEnabled()) logger.debug("Initial instances: \n" + result.toInstanceString());
        Map<String, INode> nodeCache = firstNest.getNodeCache();
        if (logger.isDebugEnabled()) logger.trace("Initial node cache: \n" + SpicyEngineUtility.printMap(nodeCache));
        for (int i = 1; i < nestOperators.size(); i++) {
            Nest ithNest = (Nest) nestOperators.get(i);
            Map<String, INode> ithNodeCache = ithNest.getNodeCache();
            mergeNodes(nodeCache, ithNodeCache, result);
        }
        if (logger.isDebugEnabled()) logger.debug("Final result of merge: \n" + result.toInstanceString());
        return result;
    }

    private void addInstancesToResult(IDataSourceProxy result, IDataSourceProxy nestResult) {
        for (INode instance : nestResult.getInstances()) {
            result.addInstance(instance);
        }
    }

    private void mergeNodes(Map<String, INode> nodeCache, Map<String, INode> newNodeCache, IDataSourceProxy result) {
        if (logger.isDebugEnabled()) logger.trace("Merging node cache: \n" + SpicyEngineUtility.printMap(newNodeCache));
        List<String> newKeys = new ArrayList<String>(newNodeCache.keySet());
        Collections.sort(newKeys);
        for (String newNodeKey : newKeys) {
            if (logger.isDebugEnabled()) logger.trace("Inspecting node key: " + newNodeKey);
            INode newNode = newNodeCache.get(newNodeKey);
            if (logger.isDebugEnabled()) logger.trace("Inspecting node: " + newNode);
            if (!nodeCache.keySet().contains(newNodeKey)) {
                if (logger.isDebugEnabled()) logger.trace("This is a new node, adding...");
                nodeCache.put(newNodeKey, newNode);
                String instanceId = findInstanceId(newNodeKey);
                addChildrenToCache(newNode, nodeCache, instanceId);
                if (newNode.isRoot()) {
                    result.addInstance(newNode);
                    if (logger.isDebugEnabled()) logger.trace("New node is a new root");
                } else {
                    String fatherKey = generateFatherKey(newNodeKey);
                    INode father = nodeCache.get(fatherKey);
                    if (logger.isDebugEnabled()) logger.trace("Father node key: " + fatherKey + " - Father node: " + father);
                    father.addChild(newNode);
                }
            } else {
                if (logger.isDebugEnabled()) logger.trace("This is an existing node");
                INode existingNode = nodeCache.get(newNodeKey);
                if (newNode instanceof TupleNode) {
                    fixProvenance(newNode, existingNode);
                }
//                if (existingNode instanceof AttributeNode) {
//                    fixImpliedValues(existingNode, newNode);
//                }
            }
        }
    }

    private String findInstanceId(String newNodeKey) {
        return newNodeKey.substring(0, newNodeKey.indexOf(SpicyEngineConstants.ALGEBRA_SEPARATOR));
    }

    private void addChildrenToCache(INode newNode, Map<String, INode> nodeCache, String instanceId) {
        if (newNode instanceof AttributeNode) {
            return;
        }
        for (INode child : newNode.getChildren()) {
            String childKey = generateKey(child, instanceId);
            nodeCache.put(childKey, child);
            addChildrenToCache(child, nodeCache, instanceId);
        }
    }

    private String generateKey(INode node, String instanceId) {
        if (node.getFather() == null) {
            return instanceId + SpicyEngineConstants.ALGEBRA_SEPARATOR + node.getValue();
        }
        return generateKey(node.getFather(), instanceId) + SpicyEngineConstants.ALGEBRA_SEPARATOR + node.getValue();
    }

    private String generateFatherKey(String key) {
        return key.substring(0, key.lastIndexOf(SpicyEngineConstants.ALGEBRA_SEPARATOR));
    }

    private void fixProvenance(INode newNode, INode existingNode) {
        TupleNode newTupleNode = (TupleNode) newNode;
        TupleNode existingTupleNode = (TupleNode) existingNode;
        existingTupleNode.addProvenanceList(newTupleNode.getProvenance());
    }

}