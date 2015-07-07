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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.exceptions.IllegalInstanceException;
import it.unibas.spicy.model.exceptions.NodeNotFoundException;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckInstance {
    
    public void checkInstance(DataSource dataSource, INode instanceRoot) {
        CheckInstanceVisitor visitor = new CheckInstanceVisitor(dataSource);
        instanceRoot.accept(visitor);
    }
    
}

class CheckInstanceVisitor implements INodeVisitor {
    
    private static Log logger = LogFactory.getLog(CheckInstanceVisitor.class);
    
    private DataSource dataSource;
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();
    
    CheckInstanceVisitor(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void visitSetNode(SetNode node) {
        visitNode(node);
    }
    
    public void visitTupleNode(TupleNode node) {
        visitRecordNode(node);
    }
    
    public void visitSequenceNode(SequenceNode node) {
        visitRecordNode(node);
    }
    
    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }
    
    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }
    
    public void visitLeafNode(LeafNode node) {
    }
    
    private void visitNode(INode node) {
        PathExpression nodePathExpression = pathGenerator.generatePathFromRoot(node);
        try {
            nodeFinder.findNodeInSchema(nodePathExpression, this.dataSource.getSchema());
        } catch (NodeNotFoundException nfe) {
            logger.error(nfe);
            throw new IllegalInstanceException("Node does not have a matching schema node: " + node.getLabel());
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    private void visitRecordNode(INode node) {
        PathExpression nodePathExpression = pathGenerator.generatePathFromRoot(node);
        try {
            INode schemaNode = nodeFinder.findNodeInSchema(nodePathExpression, this.dataSource.getSchema());
            if (!checkRecordChildren(schemaNode, node)) {
                throw new IllegalInstanceException("Node children do not match schema: \n" + node.toStringWithOids() + " Current schema node: " + schemaNode.getLabel() + " in\n" + dataSource.getSchema());
            }
        } catch (NodeNotFoundException nfe) {
            logger.error(nfe);
            throw new IllegalInstanceException("Node does not have a matching schema node: " + node.getLabel() + " in " + dataSource.getSchema());
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    private boolean checkRecordChildren(INode schemaNode, INode node) {
        int requiredChilds = countRequiredChilds(schemaNode);
        if (requiredChilds > node.getChildren().size()) {
            return false;
        }
        return true;
    }

    private int countRequiredChilds(INode schemaNode) {
        int counter = 0;
        for (INode child : schemaNode.getChildren()) {
            if (child.isRequired() && !child.isVirtual()) {
                counter++;
            }
        }
        return counter;
    }

    public Object getResult() {
        return null;
    }

    
}
