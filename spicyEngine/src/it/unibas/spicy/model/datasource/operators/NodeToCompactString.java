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
import java.util.List;

public class NodeToCompactString {

    public String toString(INode node) {
        NodeToCompactStringVisitor printVisitor = new NodeToCompactStringVisitor();
        node.accept(printVisitor);
        String result = (String) printVisitor.getResult();
        CalculateSize sizeCalculator = new CalculateSize();
        if (node instanceof SetNode) {
            result += "\n" + "Total number of tuples: " + sizeCalculator.getNumberOfTuples(node);
        }
        return result;
    }
}

class NodeToCompactStringVisitor implements INodeVisitor {

    private int indentLevel = 0;
    private StringBuilder treeDescription = new StringBuilder();

    public String getResult() {
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        treeDescription.append(this.indentString());
        treeDescription.append(node.getLabel());
        if (hasChildAttributes(node)) {
            treeDescription.append(" [");
            for (int i = 0; i < node.getChildren().size(); i++) {
                INode child = node.getChildren().get(i);
                if (child instanceof AttributeNode) {
                    treeDescription.append(child.getLabel()).append(": ").append(child.getChild(0).getValue());
                }
                if (i != node.getChildren().size() - 1) {
                    treeDescription.append(",   ");
                }
            }
            treeDescription.append("]");
        }
        if (node.getProvenance().size() != 0) {
            treeDescription.append(" **Prov:");
            treeDescription.append(node.getProvenance());
        }
        treeDescription.append("\n");
        this.indentLevel++;
        for (INode child : node.getChildren()) {
            if (!(child instanceof AttributeNode)) {
                child.accept(this);
            }
        }
        this.indentLevel--;
    }

    private boolean hasChildAttributes(TupleNode node) {
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

    public void visitUnionNode(UnionNode node) {
        visitGenericNode(node);
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
        visitInstanceNode(node);
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (INode child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private void visitInstanceNode(INode node) {
        treeDescription.append(this.indentString());
        treeDescription.append(node.getLabel());
        if (node instanceof SetNode) {
            treeDescription.append(" - Tuples: " + node.getChildren().size());
        }
        treeDescription.append("\n");
    }

    private StringBuilder indentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            result.append("    ");
        }
        return result;
    }
}

