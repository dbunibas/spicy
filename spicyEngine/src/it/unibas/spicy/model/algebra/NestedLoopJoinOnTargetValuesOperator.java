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
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class NestedLoopJoinOnTargetValuesOperator implements IJoinOnTargetValuesOperator {

    private static Log logger = LogFactory.getLog(NestedLoopJoinOnTargetValuesOperator.class);
    private EvaluateExpression evaluator = new EvaluateExpression();
    private IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();

    public IDataSourceProxy join(IDataSourceProxy firstDataSource, IDataSourceProxy secondDataSource,
            List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences,
            VariableJoinCondition joinCondition) {
        if (logger.isDebugEnabled()) logger.trace("Joining dataSources on target values: " + firstDataSource.toInstanceString() + "\n" + secondDataSource.toInstanceString());
        if (logger.isDebugEnabled()) logger.trace("Correspondences: \n" + leftCorrespondences + "\n" + rightCorrespondences);
        if (logger.isDebugEnabled()) logger.trace("Join Condition: " + joinCondition);
        INode resultSchema = generateSchema(firstDataSource, secondDataSource);
        if (logger.isDebugEnabled()) logger.trace("Resulting schema: \n" + resultSchema);
        IDataSourceProxy result = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, resultSchema));
        assert (firstDataSource.getInstances().size() == secondDataSource.getInstances().size()) : "Data sources must have equal number of instances";
        if (firstDataSource.getInstances().size() > 0 && secondDataSource.getInstances().size() > 0) {
            for (int i = 0; i < firstDataSource.getInstances().size(); i++) {
                INode firstInstance = firstDataSource.getInstances().get(i);
                INode secondInstance = secondDataSource.getInstances().get(i);
                INode joinedInstance = joinInstances(firstInstance, secondInstance, leftCorrespondences, rightCorrespondences, joinCondition);
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
            if (contains(firstTupleNode, attributeNode)) {
                throw new IllegalArgumentException("Attribute already present in join result: " + attributeNode + "\n" + firstTupleNode);
            }
            addAttribute(rootNode, attributeNode);
        }
        return rootNode;
    }

    private void addAttribute(INode rootNode, INode attributeNode) {
        INode tupleNode = rootNode.getChild(0);
        tupleNode.addChild((INode) attributeNode.clone());
    }

    private INode joinInstances(INode firstInstanceRoot, INode secondInstanceRoot,
            List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences,
            VariableJoinCondition joinCondition) {
        if (logger.isTraceEnabled()) logger.trace("Joining instances: \n" + firstInstanceRoot + "\n" + secondInstanceRoot + "\n" + joinCondition);
        INode result = new SetNode(generateSetLabel(firstInstanceRoot.getLabel(), secondInstanceRoot.getLabel()), oidGenerator.getNextOID());
        result.setRoot(true);
        for (INode firstTuple : firstInstanceRoot.getChildren()) {
            for (INode secondTuple : secondInstanceRoot.getChildren()) {
                if (logger.isDebugEnabled()) logger.trace("Joining tuples: \n" + firstTuple + "\n" + secondTuple + "\n" + joinCondition);
                String leftTupleSummary = generateSummary(generateLeftTupleValues(firstTuple, leftCorrespondences, joinCondition));
                String rightTupleSummary = generateSummary(generateRightTupleValues(secondTuple, rightCorrespondences, joinCondition));
                if (leftTupleSummary != null && rightTupleSummary != null && leftTupleSummary.equals(rightTupleSummary)) {
                    if (logger.isDebugEnabled()) logger.trace("References match, joining tuples");
                    result.addChild(joinTuples(firstTuple, secondTuple));
                }
            }
        }
        return result;
    }

    private List<String> generateLeftTupleValues(INode tuple, List<VariableCorrespondence> correspondences, VariableJoinCondition joinCondition) {
        List<String> result = new ArrayList<String>();
        for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
            VariableCorrespondence fromPathCorrespondence = findCorrespondenceForPath(fromPath, correspondences);
            if (fromPathCorrespondence != null) {
                String targetValue = evaluator.evaluateFunction(fromPathCorrespondence.getTransformationFunction(), tuple).toString();
                result.add(targetValue);
            }
        }
        if (result.isEmpty() && !joinCondition.isMonodirectional()) {
            for (VariablePathExpression toPath : joinCondition.getToPaths()) {
                VariableCorrespondence toPathCorrespondence = findCorrespondenceForPath(toPath, correspondences);
                if (toPathCorrespondence != null) {
                    String targetValue = evaluator.evaluateFunction(toPathCorrespondence.getTransformationFunction(), tuple).toString();
                    result.add(targetValue);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Unable to find left attributes for join " + joinCondition + "\nin tuple " + tuple + "\nwith correspondences: " + correspondences);
        }
        return result;
    }

    private List<String> generateRightTupleValues(INode tuple, List<VariableCorrespondence> correspondences, VariableJoinCondition joinCondition) {
        List<String> result = new ArrayList<String>();
        for (VariablePathExpression toPath : joinCondition.getToPaths()) {
            VariableCorrespondence toPathCorrespondence = findCorrespondenceForPath(toPath, correspondences);
            if (toPathCorrespondence != null) {
                String targetValue = evaluator.evaluateFunction(toPathCorrespondence.getTransformationFunction(), tuple).toString();
                result.add(targetValue);
            }
        }
        if (result.isEmpty() && !joinCondition.isMonodirectional()) {
            for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
                VariableCorrespondence fromPathCorrespondence = findCorrespondenceForPath(fromPath, correspondences);
                if (fromPathCorrespondence != null) {
                    String targetValue = evaluator.evaluateFunction(fromPathCorrespondence.getTransformationFunction(), tuple).toString();
                    result.add(targetValue);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Unable to find right attributes for join " + joinCondition + "\nin tuple " + tuple + "\nwith correspondences: " + correspondences);
        }
        return result;
    }

    private String generateSummary(List<String> values) {
        StringBuilder result = new StringBuilder("#");
        for (String value : values) {
            if (!value.equals("NULL")) {
                result.append(value).append("#");
            }
        }
        if (result.toString().equals("#")) {
            return null;
        }
        return result.toString();
    }

    private INode joinTuples(INode firstTuple, INode secondTuple) {
        INode joinedTuple = firstTuple.clone();
        joinedTuple.setLabel(generateTupleLabel(firstTuple.getLabel(), secondTuple.getLabel()));
        for (INode attributeNode : secondTuple.getChildren()) {
            if (contains(joinedTuple, attributeNode)) {
                throw new IllegalArgumentException("Attribute already present in join result: " + attributeNode + "\n" + joinedTuple);
            }
            joinedTuple.addChild(attributeNode.clone());
        }
        if (logger.isDebugEnabled()) logger.trace("Joined tuple: \n" + joinedTuple);
        return joinedTuple;
    }

    private boolean contains(INode tuple, INode attribute) {
        return (findNode(tuple, attribute.getLabel()) != null);
    }

    private INode findNode(INode tuple, String nodeLabel) {
        for (INode child : tuple.getChildren()) {
            if (child.getLabel().equals(nodeLabel)) {
                return child;
            }
        }
        return null;
    }

    private String generateSetLabel(String label1, String label2) {
        return label1 + "+" + label2;
    }

    private String generateTupleLabel(String label1, String label2) {
        return label1 + "+" + label2;
    }

    private VariableCorrespondence findCorrespondenceForPath(VariablePathExpression pathExpression, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(pathExpression)) {
                return correspondence;
            }
        }
        return null;
    }
}
