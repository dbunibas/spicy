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
 
package it.unibas.spicy.model.algebra.query.operators.xquery;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class XQFinalNest {

    public String toString(MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        GenerateNestXQueryVisitor visitor = new GenerateNestXQueryVisitor(mappingTask);
        mappingTask.getTargetProxy().getIntermediateSchema().accept(visitor);
        result.append(visitor.getResult());
        return result.toString();
    }
}

class GenerateNestXQueryVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateNestXQueryVisitor.class);
    
    private int indentLevel = 0;
    private StringBuilder xQuery = new StringBuilder("\n");
    private MappingTask mappingTask;

    public GenerateNestXQueryVisitor(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public String getResult() {
        return xQuery.toString();
    }

    public void visitSetNode(SetNode setNode) {
        if (isEmpty(setNode)) {
            if (!setNode.isVirtual()) {
                xQuery.append(indentString());
                xQuery.append(generateElement(setNode)).append("{").append("}").append("\n");
            }
            return;
        }
        String indent = indentString().toString();
        if (!setNode.isVirtual()) {
            xQuery.append(indentString());
            xQuery.append(generateElement(setNode)).append(open()).append("\n");
            indentLevel++;
            indent = indentString().toString();
        }
        String forEachBlock = generateForEachBlock(setNode, indent);
        if (forEachBlock != null) {
            xQuery.append(forEachBlock);
            visitChildren(setNode);
            xQuery.append(indentString());
            xQuery.append(")");
        }
        if (!setNode.isVirtual()) {
            indentLevel--;
            xQuery.append("\n").append(indentString());
            xQuery.append(close());
        }
    }

    private boolean isEmpty(SetNode setNode) {
        VariablePathExpression relativePath = new GeneratePathExpression().generateRelativePath(setNode, mappingTask.getTargetProxy());
        SetAlias setVariable = relativePath.getStartingVariable();
        String setNodeVariableName = XQNames.finalXQueryNameSTExchange(setVariable);
        return (!GenerateXQuery.materializedViews.containsValue(setNodeVariableName));
    }

    private String generateForEachBlock(SetNode setNode, String indent) {
        VariablePathExpression relativePath = new GeneratePathExpression().generateRelativePath(setNode, mappingTask.getTargetProxy());
        SetAlias setVariable = relativePath.getStartingVariable();
        StringBuilder result = new StringBuilder();
        String setNodeVariableName;
//        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
//            setNodeVariableName = XQNames.finalXQueryNameTExchange(setVariable);
//        } else {
            setNodeVariableName = XQNames.finalXQueryNameSTExchange(setVariable);
//        }
        result.append(indent).append("for ").append(setVariable.toShortStringWithDollar()).append(" in ");
        result.append(setNodeVariableName);
        result.append("/").append("tuple");
        SetAlias fatherVariable = setVariable.getBindingPathExpression().getStartingVariable();
        if (fatherVariable != null) {
            result.append("\n");
            result.append(indent).append("where ").append(setVariable.toShortStringWithDollar()).append("/").append(XQUtility.SET_ID).append("/text()");
            result.append(" = ");
            result.append(fatherVariable.toShortStringWithDollar()).append("/").append(setVariable.getBindingPathExpression().getLastStep()).append("/text()");
        }
        result.append("\n");
        result.append(indent).append("return (\n");
        return result.toString();
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

    private void visitIntermediateNode(INode node) {
        if (!node.isVirtual()) {
            xQuery.append(indentString());
            xQuery.append(generateElement(node)).append(open()).append("\n");
        }
        visitChildren(node);
        if (!node.isVirtual()) {
            xQuery.append("\n");
            xQuery.append(indentString());
            xQuery.append(close()).append("\n");
        }
    }

    private void visitChildren(INode node) {
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            for (int i = 0; i < listOfChildren.size(); i++) {
                INode child = listOfChildren.get(i);
                if (!child.isVirtual() || (!(child instanceof TupleNode))) {
                    this.indentLevel++;
                }
                child.accept(this);
                if (!child.isVirtual() || (!(child instanceof TupleNode))) {
                    this.indentLevel--;
                }
                if (i != listOfChildren.size() - 1) {
                    INode nextChild = listOfChildren.get(i + 1);
                    if (requiresComma(nextChild))
                        xQuery.append(",\n");
                }
            }
        }
    }

    public boolean requiresComma(INode nextChild) {
        if (!nextChild.isVirtual()) {
            return true;
        }
        if (nextChild instanceof SetNode && !(isEmpty((SetNode) nextChild))) {
            return true;
        }
        return false;
    }

    public void visitAttributeNode(AttributeNode attributeNode) {
        xQuery.append(indentString());
        xQuery.append(generateElement(attributeNode));
        VariablePathExpression relativePath = new GeneratePathExpression().generateRelativePath(attributeNode, mappingTask.getTargetProxy());
        SetAlias setVariable = relativePath.getStartingVariable();
        xQuery.append("{").append(setVariable.toShortStringWithDollar()).append("/").append(XQNames.xQueryNameForPath(relativePath)).append("/text()}");
    }

    public void visitMetadataNode(MetadataNode node) {
        xQuery.append(indentString());
        xQuery.append(generateAttribute(node));
        VariablePathExpression relativePath = new GeneratePathExpression().generateRelativePath(node, mappingTask.getTargetProxy());
        SetAlias setVariable = relativePath.getStartingVariable();
        xQuery.append("{").append(setVariable.toShortStringWithDollar()).append("/").append(XQNames.xQueryNameForPath(relativePath)).append("/text()}");
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private String generateElement(INode node) {
        return "element " + node.getLabel() + " ";
    }

    private String generateAttribute(INode node) {
        return "attribute " + node.getLabel() + " ";
    }

    private String open() {
        return "{";
    }

    private String close() {
        return "}";
    }
    private final static String INDENT_STRING = "    ";

    private StringBuilder indentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            result.append(INDENT_STRING);
        }
        return result;
    }
}

