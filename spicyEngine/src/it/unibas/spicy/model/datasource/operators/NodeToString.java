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
import it.unibas.spicy.model.datasource.values.OID;
import java.util.List;

public class NodeToString {

    public String toString(INode node, boolean printOids, boolean printAnnotations) {
        NodeToStringVisitor printVisitor = new NodeToStringVisitor(printOids, printAnnotations);
        node.accept(printVisitor);
        return (String) printVisitor.getResult();
    }
}

class NodeToStringVisitor implements INodeVisitor {

    private static final String HEADER_SCHEMA = "----------- Schema ------------------\n";
    private static final String HEADER_INSTANCE = "----------- Instance ------------------\n";
    private static final String FOOTER = "------------------------------------------\n";
    private int MAXCHARS = 10000000;
    private int indentLevel = 0;
    private StringBuilder treeDescription = new StringBuilder();
    private boolean printOids = false;
    private boolean printAnnotations = false;

    public NodeToStringVisitor(boolean printOids, boolean printAnnotations) {
        this.printOids = printOids;
        this.printAnnotations = printAnnotations;
    }

    public String getResult() {
        if (treeDescription.length() > MAXCHARS) {
            treeDescription.append("....");
        }
        treeDescription.append(FOOTER);
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
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
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isSchemaNode()) {
            visitSchemaNode(node);
        } else {
            visitInstanceNode(node);
        }
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (INode child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private void visitSchemaNode(INode node) {
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isRoot()) {
            treeDescription.append(HEADER_SCHEMA);
        }
        treeDescription.append(this.indentString());
        treeDescription.append(typeNodeDescription(node));
        if (node instanceof AttributeNode) {
            treeDescription.append(" (");
            treeDescription.append(node.getChild(0).getLabel());
            treeDescription.append(")");
        }
        treeDescription.append("\n");
        if (this.printAnnotations) {
            treeDescription.append(annotationsString(node));
        }
    }

    private void visitInstanceNode(INode node) {
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isRoot()) {
            treeDescription.append(HEADER_INSTANCE);
        }
        treeDescription.append(this.indentString());
        treeDescription.append(instanceNodeDescription(node));
        if (this.printOids) {
            treeDescription.append(" - ");
            Object value = node.getValue();
            if (value instanceof OID) {
                OID oid = (OID) value;
                if (oid.getSkolemString() != null) {
                    treeDescription.append(oid.getSkolemString());
                }
            }
        }
        if (node instanceof SetNode) {
            treeDescription.append(" - Tuples: ").append(node.getChildren().size());
        }
        if (node instanceof TupleNode) {
            TupleNode tupleNode = (TupleNode) node;
            if (!tupleNode.getProvenance().isEmpty()) {
                treeDescription.append(" - Provenance: ");
                treeDescription.append(tupleNode.getProvenance());
            }
        }
        if (node instanceof AttributeNode) {
            treeDescription.append(" (");
            treeDescription.append(node.getChild(0).getValue());
            treeDescription.append(")");
        }
        treeDescription.append("\n");
        if (this.printAnnotations) {
            treeDescription.append(annotationsString(node));
        }
    }

    private StringBuilder typeNodeDescription(INode node) {
        StringBuilder result = new StringBuilder();
        result.append(node.getLabel());
        result.append(" : ");
        result.append(node.getClass().getSimpleName());
        if (node.isExcluded()) {
            result.append(" [EXCLUDED]");
        }
        if (node.isVirtual()) {
            result.append(" [virtual]");
        }
        if (node.isRequired()) {
            result.append(" [required]");
        }
        if (node.isNotNull()) {
            result.append(" [not nullable]");
        }
        return result;
    }

    private StringBuilder annotationsString(INode node) {
        StringBuilder result = new StringBuilder();
        if (node.getAnnotations() != null && !node.getAnnotations().isEmpty()) {
            result.append(indentString());
            result.append("--------ANNOTATIONS\n");
            for (String key : node.getAnnotations().keySet()) {
                result.append(indentString());
                result.append("        ");
                result.append(key);
                result.append(": ");
                result.append(node.getAnnotation(key));
                result.append("\n");
            }
        }
        return result;
    }

    private StringBuilder instanceNodeDescription(INode node) {
        StringBuilder result = new StringBuilder();
        result.append(node.getLabel());
        result.append(" (");
        result.append(node.getValue());
        result.append(") : ");
        result.append(node.getClass().getSimpleName());
        return result;
    }

    private StringBuilder indentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            result.append("    ");
        }
        return result;
    }
}

