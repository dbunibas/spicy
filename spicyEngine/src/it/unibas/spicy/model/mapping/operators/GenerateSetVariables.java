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
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateSetVariables {
    
    public Map<SetNode, SetAlias> generateVariables(IDataSourceProxy dataSource) {
        FindSetVariablesVisitor visitor = new FindSetVariablesVisitor();
        dataSource.getIntermediateSchema().accept(visitor);
        return visitor.getResult();
    }

    public List<PathExpression> findSetAbsolutePaths(IDataSourceProxy dataSource) {
        FindAbsoluteSetPathsVisitor visitor = new FindAbsoluteSetPathsVisitor();
        dataSource.getIntermediateSchema().accept(visitor);
        return visitor.getResult();
    }

}

class FindSetVariablesVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindSetVariablesVisitor.class);
    private Stack<SetAlias> variableStack = new Stack<SetAlias>();
    private Stack<INode> nodeStack = new Stack<INode>();
    private Map<SetNode, SetAlias> setVariables = new HashMap<SetNode, SetAlias>();

    public void visitSetNode(SetNode setNode) {
        if (logger.isDebugEnabled()) logger.debug("Visiting set node: " + setNode.getLabel());
        nodeStack.push(setNode);
        List<INode> steps = this.getNodeListFromStack();
        if (variableStack.isEmpty()) {
            VariablePathExpression rootSetPath = new VariablePathExpression(generateLabels(steps));
            SetAlias rootVariable = new SetAlias(rootSetPath);
            variableStack.push(rootVariable);
            setVariables.put(setNode, rootVariable);
        } else {
            SetAlias lastVariable = variableStack.peek();
            VariablePathExpression setPath = new VariablePathExpression(lastVariable, generateLabels(steps));
            SetAlias newVariable = new SetAlias(setPath);
            variableStack.push(newVariable);
            setVariables.put(setNode, newVariable);
        }
        for (INode child : setNode.getChildren()) {
            child.accept(this);
        }
        nodeStack.pop();
        variableStack.pop();
    }

    private List<String> generateLabels(List<INode> nodes) {
        List<String> result = new ArrayList<String>();
        for (INode node : nodes) {
            result.add(node.getLabel());
        }
        return result;
    }

    public void visitTupleNode(TupleNode node) {
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitChildren(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitChildren(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitChildren(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitChildren(INode node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (logger.isDebugEnabled()) logger.debug("Node stack: " + getNodeStackString());
        if (logger.isDebugEnabled()) logger.debug("Variable stack: " + getVariableStackString());
        this.nodeStack.push(node);
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
        this.nodeStack.pop();
    }

    private List<INode> getNodeListFromStack() {
        List<INode> nodeList = new ArrayList<INode>();
        for (int i = nodeStack.size() - 1; i >= 0; i--) {
            INode step = nodeStack.get(i);
            if (i < nodeStack.size() - 1 && step instanceof SetNode) {
                break;
            }
            nodeList.add(0, step);
        }
        return nodeList;
    }

    public Map<SetNode, SetAlias> getResult() {
        return this.setVariables;
    }

    private String getNodeStackString() {
        String result = "";
        for (INode step : nodeStack) {
            result += step.getLabel() + ", ";
        }
        return result;
    }

    private String getVariableStackString() {
        String result = "";
        for (SetAlias variable : variableStack) {
            result += variable + ", ";
        }
        return result;
    }
}


class FindAbsoluteSetPathsVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindAbsoluteSetPathsVisitor.class);
    private List<PathExpression> result = new ArrayList<PathExpression>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public void visitSetNode(SetNode setNode) {
        if (logger.isDebugEnabled()) logger.debug("Visiting set node: " + setNode.getLabel());
        result.add(pathGenerator.generatePathFromRoot(setNode));
        visitChildren(setNode);
    }

    public void visitTupleNode(TupleNode node) {
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitChildren(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitChildren(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitChildren(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public List<PathExpression> getResult() {
        return this.result;
    }
}
