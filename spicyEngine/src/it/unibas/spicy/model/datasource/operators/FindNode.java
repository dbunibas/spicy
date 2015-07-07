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
import it.unibas.spicy.model.exceptions.NodeNotFoundException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.PathExpressionWithFilters;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindNode {

    private static GeneratePathExpression pathGenerator = new GeneratePathExpression();

    ////   FROM PATH
    public INode findNodeInSchema(PathExpression pathExpression, IDataSourceProxy dataSource) {
        return findNodeInSchema(pathExpression, dataSource.getIntermediateSchema());
    }

    public INode findNodeInSchema(PathExpression pathExpression, INode schema) {
        FindNodeFromAbsolutePathVisitor visitor = new FindNodeFromAbsolutePathVisitor(pathExpression);
        schema.accept(visitor);
        return visitor.getResult();
    }

    public INode findFirstNodeInInstance(PathExpression pathExpression, INode instance) {
        FindNodeFromAbsolutePathVisitor visitor = new FindNodeFromAbsolutePathVisitor(pathExpression, false);
        instance.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findNodesInInstance(PathExpression pathExpression, INode instance) {
        FindNodesFromAbsolutePathVisitor visitor = new FindNodesFromAbsolutePathVisitor(pathExpression);
        instance.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findNodesWithFilterInInstance(PathExpressionWithFilters pathExpression, INode instance) {
        FindNodesFromFilterPathVisitor visitor = new FindNodesFromFilterPathVisitor(pathExpression);
        instance.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findNodesInInstance(PathExpression pathExpression, INode instance, int limit) {
        FindNodesFromAbsolutePathVisitor visitor = new FindNodesFromAbsolutePathVisitor(pathExpression, limit);
        instance.accept(visitor);
        return visitor.getResult();
    }

    ////   FROM STRING
    public INode findNodeInSchema(String pathString, IDataSourceProxy dataSource) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findNodeInSchema(absolutePath, dataSource);
    }

    public INode findFirstNodeInInstance(String pathString, INode instance) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findFirstNodeInInstance(absolutePath, instance);
    }

    public List<INode> findNodesInInstance(String pathString, INode instance) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findNodesInInstance(absolutePath, instance);
    }

    ////   FROM NODE
    public INode findNodeInSchema(INode instanceNode, IDataSourceProxy dataSource) {
        assert (!(instanceNode.isSchemaNode())) : "Node must be an instance node: " + instanceNode;
        PathExpression absolutePath = pathGenerator.generatePathFromRoot(instanceNode);
        return findNodeInSchema(absolutePath, dataSource);
    }
}

class FindNodeFromAbsolutePathVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindNodeFromAbsolutePathVisitor.class);
    private PathExpression pathExpression;
    private boolean required = true;
    private List<String> pathSteps;
    private INode result;
    private boolean root = true;

    public FindNodeFromAbsolutePathVisitor(PathExpression pathExpression) {
        this(pathExpression, true);
    }

    @SuppressWarnings("unchecked")
    public FindNodeFromAbsolutePathVisitor(PathExpression pathExpression, boolean required) {
        this.pathExpression = pathExpression;
        this.required = required;
        List<String> pathSteps = pathExpression.getPathSteps();
        this.pathSteps = (List<String>) ((ArrayList<String>) pathSteps).clone();
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    private void visitNode(INode node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (pathSteps.isEmpty()) {
            this.result = node;
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (this.root) {
            if (node.getLabel().equals(firstStepLabel)) {
                this.root = false;
                pathSteps.remove(0);
                if (pathSteps.isEmpty()) {
                    this.result = node;
                    return;
                }
                firstStepLabel = pathSteps.get(0);
            }
        }
        for (INode child : node.getChildren()) {
            if (child.getLabel().equals(firstStepLabel)) {
                pathSteps.remove(0);
                child.accept(this);
            }
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public INode getResult() {
        if (this.result == null && this.required) {
            throw new NodeNotFoundException("Node not found: " + pathExpression);
        }
        return this.result;
    }
}

class FindNodesFromAbsolutePathVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindNodesFromAbsolutePathVisitor.class);
    private PathExpression pathExpression;
    private Integer limit;
    private List<String> pathSteps;
    private List<INode> result = new ArrayList<INode>();
    private boolean root = true;

    @SuppressWarnings("unchecked")
    public FindNodesFromAbsolutePathVisitor(PathExpression pathExpression) {
        this.pathExpression = pathExpression;
        this.pathSteps = (List<String>) ((ArrayList<String>) pathExpression.getPathSteps()).clone();
    }

    @SuppressWarnings("unchecked")
    public FindNodesFromAbsolutePathVisitor(PathExpression pathExpression, Integer limit) {
        this.pathExpression = pathExpression;
        this.limit = limit;
        this.pathSteps = (List<String>) ((ArrayList<String>) pathExpression.getPathSteps()).clone();
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    private void visitNode(INode node) {
        if (this.limit != null && this.result.size() >= limit) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (pathSteps.isEmpty()) {
            this.result.add(node);
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (this.root) {
            if (node.getLabel().equals(firstStepLabel)) {
                this.root = false;
                pathSteps.remove(0);
                if (pathSteps.isEmpty()) {
                    this.result.add(node);
                    return;
                }
                firstStepLabel = pathSteps.get(0);
            }
        }
        for (INode child : node.getChildren()) {
            if (child.getLabel().equals(firstStepLabel)) {
                String firstNode = pathSteps.get(0);
                pathSteps.remove(0);
                child.accept(this);
                pathSteps.add(0, firstNode);
            }
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<INode> getResult() {
        return this.result;
    }
}

class FindNodesFromFilterPathVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindNodesFromFilterPathVisitor.class);
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private PathExpressionWithFilters pathExpression;
    private List<String> pathSteps;
    private List<INode> result = new ArrayList<INode>();
    private boolean root = true;

    @SuppressWarnings("unchecked")
    public FindNodesFromFilterPathVisitor(PathExpressionWithFilters pathExpression) {
        this.pathExpression = pathExpression;
        this.pathSteps = (List<String>) ((ArrayList<String>) pathExpression.getPathSteps()).clone();
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    private void visitNode(INode node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (pathSteps.isEmpty()) {
            this.result.add(node);
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (this.root) {
            if (matchesFilter(node)) {
                this.root = false;
                pathSteps.remove(0);
                if (pathSteps.isEmpty()) {
                    this.result.add(node);
                    return;
                }
                firstStepLabel = pathSteps.get(0);
            }
        }
        for (INode child : node.getChildren()) {
            if (matchesFilter(child)) {
                String firstNode = pathSteps.get(0);
                pathSteps.remove(0);
                child.accept(this);
                pathSteps.add(0, firstNode);
            }
        }
    }

    private boolean matchesFilter(INode node) {
        String firstStepLabel = pathSteps.get(0);
        if (!node.getLabel().equals(firstStepLabel)) {
            return false;
        }
        if (node instanceof TupleNode) {
            for (INode child : node.getChildren()) {
                if (child instanceof AttributeNode) {
                    PathExpression attributePath = pathGenerator.generatePathFromRoot(child);
                    Object filterValue = pathExpression.getFilterValues().get(attributePath);
                    if (filterValue != null && !child.getChild(0).getValue().toString().equals(filterValue)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<INode> getResult() {
        return this.result;
    }
}

