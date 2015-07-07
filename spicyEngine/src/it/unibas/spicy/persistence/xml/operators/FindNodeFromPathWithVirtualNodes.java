/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Alessandro Pappalardo - pappalardo.alessandro@gmail.com
    Gianvito Summa - gianvito.summa@gmail.com

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
 
package it.unibas.spicy.persistence.xml.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FindNodeFromPathWithVirtualNodes {

    INode findNodeInSchema(INode startingNode, List<String> pathSteps) {
        FindNodeWithVirtualNodesVisitor visitor = new FindNodeWithVirtualNodesVisitor(pathSteps);
        startingNode.accept(visitor);
        return visitor.getResult();
    }
}

class FindNodeWithVirtualNodesVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindNodeWithVirtualNodesVisitor.class);
    private List<String> pathSteps;
    private INode result;

    @SuppressWarnings("unchecked")
    FindNodeWithVirtualNodesVisitor(List<String> pathSteps) {
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
        if (result != null) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel() + " with path steps: " + pathSteps);
        if (node.isVirtual()) {
            if (logger.isDebugEnabled()) logger.debug("Node is virtual. Visiting children");
            visitChildren(node);
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (!node.getLabel().equals(firstStepLabel)) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Match found, removing step " + pathSteps.get(0));
        pathSteps.remove(0);
        if (pathSteps.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Steps empty, node found");
            this.result = node;
            return;
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public INode getResult() {
        return this.result;
    }
}
