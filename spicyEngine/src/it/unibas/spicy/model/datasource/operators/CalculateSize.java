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

public class CalculateSize {

    public int getSchemaSize(DataSource dataSource) {
        return getNumberOfNodes(dataSource.getSchema());
    }

    public int getNumberOfNodes(INode node) {
        CalculateSizeVisitor visitor = new CalculateSizeVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }

    public int getNumberOfTuples(INode node) {
        CalculateTuplesVisitor visitor = new CalculateTuplesVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }
}

class CalculateSizeVisitor implements INodeVisitor {

    private int counter;

    public Integer getResult() {
        return counter;
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitGenericNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitGenericNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitGenericNode(INode node) {
        if (!node.isExcluded()) {
            counter++;
        }
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
}

class CalculateTuplesVisitor implements INodeVisitor {

    private int counter;

    public Integer getResult() {
        return counter;
    }

    public void visitSetNode(SetNode node) {
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitChildren(node);
    }

    public void visitTupleNode(TupleNode node) {
        if (!node.isExcluded()) {
            if (hasChildAttribute(node)) {
                counter++;
            }
        }
        visitChildren(node);
    }

    private boolean hasChildAttribute(INode node) {
        for (INode child : node.getChildren()) {
            if (child instanceof AttributeNode) {
                return true;
            }
        }
        return false;
    }

    public void visitSequenceNode(SequenceNode node) {
        visitTupleNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        return;
    }

    public void visitMetadataNode(MetadataNode node) {
        return;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
}
