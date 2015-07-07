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
 
package it.unibas.spicy.model.datasource.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VerifyFunctionalDependencies {

    private static Log logger = LogFactory.getLog(VerifyFunctionalDependencies.class);

    public String verifyFunctionalDependencies(IDataSourceProxy target, IDataSourceProxy solution) {
        if (logger.isDebugEnabled()) {
            logger.debug("*********** Checking functional dependencies on data source");
        }
        VerifyFunctionalDependenciesVisitor visitor = new VerifyFunctionalDependenciesVisitor(target.getMappingData().getDependencies());
        for (INode instanceRoot : solution.getInstances()) {
            instanceRoot.accept(visitor);
        }
        List<String> errors = visitor.getResult();
        StringBuilder builder = new StringBuilder();
        for (String string : errors) {
            builder.append(string).append("\n");
        }
        String result = builder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("*********** Result: " + result);
        }
        return result;
    }
}

class VerifyFunctionalDependenciesVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(VerifyFunctionalDependenciesVisitor.class);
    private List<VariableFunctionalDependency> functionalDependencies;
    private List<String> errors = new ArrayList<String>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public VerifyFunctionalDependenciesVisitor(List<VariableFunctionalDependency> functionalDependencies) {
        this.functionalDependencies = functionalDependencies;
    }

    public void visitSetNode(SetNode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        if (logger.isDebugEnabled()) {
            logger.debug("-- Checking set node: " + nodePath);
        }
        List<VariableFunctionalDependency> relevantDependencies = findDependenciesForSet(node);
        for (VariableFunctionalDependency functionalDependency : relevantDependencies) {
            if (logger.isDebugEnabled()) {
                logger.debug("-- Checking dependency: " + functionalDependency);
            }
            List<PathExpression> leftPaths = new ArrayList<PathExpression>();
            for (VariablePathExpression leftPath : functionalDependency.getLeftPaths()) {
                leftPaths.add(leftPath.getAbsolutePath());
            }
            List<PathExpression> dependencyPaths = new ArrayList<PathExpression>();
            dependencyPaths.addAll(leftPaths);
            for (VariablePathExpression rightPath : functionalDependency.getRightPaths()) {
                dependencyPaths.add(rightPath.getAbsolutePath());
            }
            SetNode leftPathProjection = calculateProjection(node, leftPaths, leftPaths);
            SetNode allPathsProjection = calculateProjection(node, dependencyPaths, leftPaths);
            if (logger.isDebugEnabled()) {
                logger.debug("-- Left path projection: " + leftPathProjection);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("-- All paths projection: " + allPathsProjection);
            }
            if (differentSizes(leftPathProjection, allPathsProjection)) {
                errors.add("Violation of functional dependency " + functionalDependency + " in set node " + nodePath);
            }
        }
        visitIntermediateNode(node);
    }

    private List<VariableFunctionalDependency> findDependenciesForSet(SetNode node) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (VariableFunctionalDependency functionalDependency : functionalDependencies) {
            PathExpression dependencySetPath = findSetPathForDependency(functionalDependency);
            PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
            if (logger.isTraceEnabled()) {
                logger.debug("--Considering dependency: " + functionalDependency);
            }
            if (logger.isTraceEnabled()) {
                logger.debug("--Set node path: " + nodePath);
            }
            if (logger.isTraceEnabled()) {
                logger.debug("--Dependency set path: " + dependencySetPath);
            }
            if (dependencySetPath.equals(nodePath)) {
                result.add(functionalDependency);
            }
        }
        return result;
    }

    private SetNode calculateProjection(SetNode node, List<PathExpression> projectionPaths, List<PathExpression> nonNullPaths) {
        SetNode clone = (SetNode) node.clone();
        clone.setFather(node.getFather());
        removeAttributes(clone, projectionPaths);
        removeDuplicates(clone, nonNullPaths);
        return clone;
    }

    private void removeAttributes(INode setNode, List<PathExpression> projectionPaths) {
        for (INode tuple : setNode.getChildren()) {
            for (Iterator<INode> it = tuple.getChildren().iterator(); it.hasNext();) {
                INode attributeNode = it.next();
                if (!(attributeNode instanceof AttributeNode)) {
                    it.remove();
                } else {
                    PathExpression attributePath = pathGenerator.generatePathFromRoot(attributeNode);
                    if (!projectionPaths.contains(attributePath)) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void removeDuplicates(SetNode setNode, List<PathExpression> nonNullPaths) {
        Set<String> tupleSummaries = new HashSet<String>();
        for (Iterator<INode> tupleIterator = setNode.getChildren().iterator(); tupleIterator.hasNext();) {
            INode tupleNode = tupleIterator.next();
            if (isNull(tupleNode, nonNullPaths)) {
                tupleIterator.remove();
            } else {
                String tupleSummary = generateSummary(tupleNode);
                if (tupleSummaries.contains(tupleSummary)) {
                    tupleIterator.remove();
                } else {
                    tupleSummaries.add(tupleSummary);
                }
            }
        }
    }

    private boolean isNull(INode tupleNode, List<PathExpression> nonNullPaths) {
        for (INode attributeNode : tupleNode.getChildren()) {
            PathExpression attributePath = pathGenerator.generatePathFromRoot(attributeNode);
            if (nonNullPaths.contains(attributePath)) {
                String value = attributeNode.getChild(0).getValue().toString();
                if (!value.equalsIgnoreCase("null")) {
                    return false;
                }
            }
        }
        return true;
    }

    private String generateSummary(INode tuple) {
        StringBuilder summary = new StringBuilder("#");
        for (INode attributeNode : tuple.getChildren()) {
            String value = attributeNode.getChild(0).getValue().toString();
            summary.append(value).append("#");
        }
        return summary.toString();
    }

    private boolean differentSizes(SetNode firstSet, SetNode secondSet) {
        return firstSet.getChildren().size() != secondSet.getChildren().size();
    }

    private PathExpression findSetPathForDependency(VariableFunctionalDependency functionalDependency) {
        return functionalDependency.getLeftPaths().get(0).getStartingVariable().getAbsoluteBindingPathExpression();
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

    public void visitAttributeNode(AttributeNode node) {
    }

    public void visitMetadataNode(MetadataNode node) {
    }

    public void visitLeafNode(LeafNode node) {
    }

    private void visitIntermediateNode(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public List<String> getResult() {
        return errors;
    }
}
