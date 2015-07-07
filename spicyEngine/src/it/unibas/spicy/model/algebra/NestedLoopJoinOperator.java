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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.values.INullValue;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class NestedLoopJoinOperator implements IJoinOperator {

    private static Log logger = LogFactory.getLog(NestedLoopJoinOperator.class);

    private IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();

    public IDataSourceProxy join(IDataSourceProxy firstDataSource, IDataSourceProxy secondDataSource, VariableJoinCondition joinCondition) {
        if (logger.isDebugEnabled()) logger.trace("Joining dataSources: " + firstDataSource.toInstanceString() + "\n" + secondDataSource.toInstanceString() + "\n" + joinCondition);
        INode resultSchema = generateSchema(firstDataSource, secondDataSource);
        if (logger.isDebugEnabled()) logger.trace("Resulting schema: \n" + resultSchema);
        IDataSourceProxy result = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, resultSchema));
        assert (firstDataSource.getInstances().size() == secondDataSource.getInstances().size()) : "Data sources must have equal number of instances";
        if (firstDataSource.getInstances().size() > 0 && secondDataSource.getInstances().size() > 0) {
            for (int i = 0; i < firstDataSource.getInstances().size(); i++) {
                INode firstInstance = firstDataSource.getInstances().get(i);
                INode secondInstance = secondDataSource.getInstances().get(i);
                INode joinedInstance = joinInstances(firstInstance, secondInstance, joinCondition);
                result.addInstance(joinedInstance);
            }
        } else {
            if (logger.isDebugEnabled()) logger.trace("No instances to join");
        }
        return result;
    }

    private INode generateSchema(IDataSourceProxy firstDataSource, IDataSourceProxy secondDataSource) {
        INode firstRootNode = firstDataSource.getIntermediateSchema();
        INode secondRootNode = secondDataSource.getIntermediateSchema();
        INode firstTupleNode = firstRootNode.getChild(0);
        INode secondTupleNode = secondRootNode.getChild(0);
        INode rootNode = (INode) firstRootNode.clone();
        for (INode attributeNode : secondTupleNode.getChildren()) {
            if (!contains(firstTupleNode, attributeNode)) {
                addAttribute(rootNode, attributeNode);
            }
        }
        return rootNode;
    }

    private void addAttribute(INode rootNode, INode attributeNode) {
        INode tupleNode = rootNode.getChild(0);
        tupleNode.addChild((INode) attributeNode.clone());
    }

    private INode joinInstances(INode firstInstanceRoot, INode secondInstanceRoot, VariableJoinCondition joinCondition) {
        if (logger.isTraceEnabled()) logger.trace("Joining instances: \n" + firstInstanceRoot + "\n" + secondInstanceRoot + "\n" + joinCondition);
        INode result = new SetNode(generateSetLabel(firstInstanceRoot.getLabel(), secondInstanceRoot.getLabel()), oidGenerator.getNextOID());
        result.setRoot(true);
        for (INode firstTuple : firstInstanceRoot.getChildren()) {
            for (INode secondTuple : secondInstanceRoot.getChildren()) {
                if (logger.isDebugEnabled()) logger.trace("Joining tuples: \n" + firstTuple + "\n" + secondTuple + "\n" + joinCondition);
                List<Object> firstTupleValues = findLeftTupleValues(firstTuple, joinCondition);
                if (logger.isDebugEnabled()) logger.trace("From values: " + firstTupleValues);
                List<Object> secondTupleValues = findRightTupleValues(secondTuple, joinCondition);
                if (logger.isDebugEnabled()) logger.trace("To values: " + secondTupleValues);
                if (equalListsForJoin(firstTupleValues, secondTupleValues)) {
                    if (logger.isDebugEnabled()) logger.trace("References match, joining tuples");
                    result.addChild(joinTuples(firstTuple, secondTuple));
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean equalListsForJoin(List list1, List list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return false;
        }
        return (list1.containsAll(list2) && list2.containsAll(list1));
    }

    private static boolean isEmpty(List list) {
        for (Object element : list) {
            if (element != null && !(element instanceof INullValue) && !(element.toString().equals("NULL"))) {
                return false;
            }
        }
        return true;
    }

    private List<Object> findLeftTupleValues(INode tuple, VariableJoinCondition joinCondition) {
        List<Object> result = new ArrayList<Object>();
        for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
            INode attributeNode = findNode(tuple, fromPath.toString());
            if (attributeNode != null) {
                result.add(attributeNode.getChild(0).getValue());
            }
        }
        if (result.isEmpty() && !joinCondition.isMonodirectional()) {
            for (VariablePathExpression toPath : joinCondition.getToPaths()) {
                INode attributeNode = findNode(tuple, toPath.toString());
                if (attributeNode != null) {
                    result.add(attributeNode.getChild(0).getValue());
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Unable to find left attributes for join " + joinCondition + "\nin tuple " + tuple);
        }
        return result;
    }

    private List<Object> findRightTupleValues(INode tuple, VariableJoinCondition joinCondition) {
        List<Object> result = new ArrayList<Object>();
        for (VariablePathExpression toPath : joinCondition.getToPaths()) {
            INode attributeNode = findNode(tuple, toPath.toString());
            if (attributeNode != null) {
                result.add(attributeNode.getChild(0).getValue());
            }
        }
        if (result.isEmpty() && !joinCondition.isMonodirectional()) {
            for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
                INode attributeNode = findNode(tuple, fromPath.toString());
                if (attributeNode != null) {
                    result.add(attributeNode.getChild(0).getValue());
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Unable to find right attributes for join " + joinCondition + "\nin tuple " + tuple);
        }
        return result;
    }

    private INode joinTuples(INode firstTuple, INode secondTuple) {
        INode joinedTuple = firstTuple.clone();
        joinedTuple.setLabel(generateTupleLabel(firstTuple.getLabel(), secondTuple.getLabel()));
        for (INode attributeNode : secondTuple.getChildren()) {
            if (!contains(joinedTuple, attributeNode)) {
                joinedTuple.addChild(attributeNode.clone());
            }
        }
        if (logger.isDebugEnabled()) logger.trace("Joined tuple: \n" + joinedTuple);
        return joinedTuple;
    }

    private INode findNode(INode tuple, String nodeLabel) {
        for (INode child : tuple.getChildren()) {
            if (child.getLabel().equals(nodeLabel)) {
                return child;
            }
        }
        return null;
    }

    private boolean contains(INode tuple, INode attribute) {
        return (findNode(tuple, attribute.getLabel()) != null);
    }

    private String generateSetLabel(String label1, String label2) {
        return label1 + "+" + label2;
    }

    private String generateTupleLabel(String label1, String label2) {
        return label1 + "+" + label2;
    }
}
