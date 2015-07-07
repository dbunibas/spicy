/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com
Donatello Santoro - donatello.santoro@gmail.com

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

public class NodeToSaveString {

    public String toString(INode node) {
        NodeToSaveStringVisitor printVisitor = new NodeToSaveStringVisitor();
        node.accept(printVisitor);
        return (String) printVisitor.getResult();
    }
}

class NodeToSaveStringVisitor implements INodeVisitor {

    private StringBuilder treeDescription = new StringBuilder();

    public String getResult() {
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        if (hasChildAttributes(node)) {
            String nodeLabel = node.getFather().getLabel();
            if (nodeLabel.matches(".+_\\d+_\\z")) {
                return;
            }
            treeDescription.append(node.getFather().getLabel());
//            treeDescription.append(node.getLabel());
            treeDescription.append("(");
            for (int i = 0; i < node.getChildren().size(); i++) {
                INode child = node.getChildren().get(i);
                if (child instanceof AttributeNode) {
                    treeDescription.append(child.getLabel()).append(": ").append("\"").append(child.getChild(0).getValue()).append("\"");
                }
                if (i != node.getChildren().size() - 1) {
                    treeDescription.append(",   ");
                }
            }
            treeDescription.append(")");
            treeDescription.append("\n");
        }
        for (INode child : node.getChildren()) {
            if (!(child instanceof AttributeNode)) {
                child.accept(this);
            }
        }
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
            for (INode child : listOfChildren) {
                child.accept(this);
            }
        }
    }

    private void visitInstanceNode(INode node) {
        return;
    }
}
