/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
 
package it.unibas.spicygui.controllo.datasource.operators;

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
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import it.unibas.spicygui.vista.treepm.TreeRenderer;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateInstanceTree {

    public JTree buildInstanceTree(INode instanceRoot, IDataSourceProxy dataSource) {
        InstanceTreeVisitor visitor = new InstanceTreeVisitor(dataSource);
        instanceRoot.accept(visitor);
        JTree instanceTree = new JTree(visitor.getResult());
        TreeRenderer rendererTree = new TreeRenderer();
        instanceTree.setCellRenderer(rendererTree);
        return instanceTree;
    }
}

class InstanceTreeVisitor implements INodeVisitor {

    private Log logger = LogFactory.getLog(InstanceTreeVisitor.class);
    private DefaultMutableTreeNode treeRoot;
    private DefaultMutableTreeNode currentTreeNode;
    private IDataSourceProxy dataSource;

    public InstanceTreeVisitor(IDataSourceProxy dataSource) {
        this.dataSource = dataSource;
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

    private void visitNode(INode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new TreeNodeAdapter(node, dataSource.getType()));
        if (logger.isDebugEnabled()) logger.debug("Creato nuovo nodo: " + treeNode);
        if (treeRoot == null) {
            treeRoot = treeNode;
        } else {
            currentTreeNode.add(treeNode);
        }
        currentTreeNode = treeNode;
        for (INode child : node.getChildren()) {
            child.accept(this);
            currentTreeNode = treeNode;
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new TreeNodeAdapter(node, dataSource.getType()));
        if (logger.isDebugEnabled()) logger.debug("Creato nuovo nodo: " + treeNode);
        currentTreeNode.add(treeNode);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public MutableTreeNode getResult() {
        return this.treeRoot;
    }
}
