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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
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
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.DAOXmlUtility;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExportXSD {

    private static Log logger = LogFactory.getLog(ExportXSD.class);
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
    public static final String PREFIX = "xs:";
    private Map<KeyConstraint, String> mapOfKeyConstraints = new HashMap<KeyConstraint, String>();
    
    private DAOXmlUtility daoXmlUtility = new DAOXmlUtility();

    public void exportXSDSchema(IDataSourceProxy dataSource, String directoryPath) throws Exception {
        checkDirectory(directoryPath);
        String filename = "";
        if (directoryPath.charAt(directoryPath.length() - 1) == '\\') {
            filename = directoryPath + dataSource.getIntermediateSchema().getLabel() + ".xsd";
        } else {
            filename = directoryPath + "\\" + dataSource.getIntermediateSchema().getLabel() + ".xsd";
        }
        processDataSource(dataSource, filename);
        dataSource.addAnnotation(SpicyEngineConstants.XML_SCHEMA_FILE, filename);
    }

    private void checkDirectory(String directoryPath) throws DAOException {
        boolean exists;
        boolean isCreated;
        try {
            File directory = new File(directoryPath);
            exists = directory.exists();
            if (!exists) {
                directory.mkdirs();
            }
        } catch (Exception ex) {
            logger.error(ex);
            throw new DAOException(" -- Unable to find or unable to create direcotry: " + directoryPath);
        }
    }

    private void processDataSource(IDataSourceProxy dataSource, String filename) throws DAOException {
        if (dataSource == null || dataSource.getSchema() == null) {
            throw new IllegalArgumentException("Unable to extract xsd from this datasource: datasource and root node must be != null");
        }
        Document document = daoXmlUtility.buildNewDOM();
        processRoot(dataSource.getSchema(), document);
        processChildren(dataSource.getSchema(), document);
        processConstraints(dataSource, document);
        logger.debug("filename schema = " + filename);
        daoXmlUtility.saveDOM(document, filename);
    }

    private void processRoot(INode root, Document document) {
        Element schema = document.createElementNS(XSD_NS, PREFIX + "schema");
        document.appendChild(schema);
        Attr elementFormDefault = document.createAttribute("elementFormDefault");
        elementFormDefault.setValue("qualified");
        schema.setAttributeNode(elementFormDefault);
    }

    private void processChildren(INode node, Document document) {
        ProcessSchemaNodeVisitor visitor = new ProcessSchemaNodeVisitor(document);
        node.accept(visitor);
    }

    private void processConstraints(IDataSourceProxy dataSource, Document document) {
        Element rootDocument = document.getDocumentElement();
        Node child = rootDocument.getFirstChild();
        if (child == null) {
            throw new IllegalArgumentException(" Unable to find right child root node for: " + dataSource.getIntermediateSchema().getLabel());
        }
        processKeyConstraints(dataSource, child, document);
        processForeignKeyConstraints(dataSource, child, document);
    }

    private void processKeyConstraints(IDataSourceProxy dataSource, Node node, Document document) {
        int i = 0;
        String name;
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            Element keyOrUniqueTag;
            i++;
            if (keyConstraint.isPrimaryKey()) {
                name = "key" + i;
                keyOrUniqueTag = document.createElement(PREFIX + "key");
            } else {
                name = "unique" + i;
                keyOrUniqueTag = document.createElement(PREFIX + "unique");
            }
            mapOfKeyConstraints.put(keyConstraint, name);
            keyOrUniqueTag.setAttribute("name", name);

            Element selectorTag = document.createElement(PREFIX + "selector");
            String rootName = dataSource.getIntermediateSchema().getLabel();
            if (logger.isDebugEnabled()) logger.debug("----->  rootName = " + rootName);
            if (keyConstraint.getKeyPaths().get(0).getLastNode(dataSource.getIntermediateSchema()).getFather().getLabel().equalsIgnoreCase(rootName)) {
                selectorTag.setAttribute("xpath", "./.");
            } else {
                selectorTag.setAttribute("xpath", ".//" + keyConstraint.getKeyPaths().get(0).getLastNode(dataSource.getIntermediateSchema()).getFather().getLabel());
            }
            keyOrUniqueTag.appendChild(selectorTag);

            for (PathExpression pathExpression : keyConstraint.getKeyPaths()) {
                Element fieldTag = document.createElement(PREFIX + "field");
                keyOrUniqueTag.appendChild(fieldTag);
                String xpath = "";
                if (pathExpression.getLastNode(dataSource.getIntermediateSchema()) instanceof MetadataNode) {
                    xpath = "@";
                }
                xpath += pathExpression.getLastNode(dataSource.getIntermediateSchema()).getLabel();
                fieldTag.setAttribute("xpath", xpath);
            }
            node.appendChild(keyOrUniqueTag);
        }
    }

    private void processForeignKeyConstraints(IDataSourceProxy dataSource, Node node, Document document) {
        int i = 0;
        String name;
        for (ForeignKeyConstraint fkConstraint : dataSource.getForeignKeyConstraints()) {
            Element keyRefTag;
            i++;
            name = "keyRef" + i;
            keyRefTag = document.createElement(PREFIX + "keyref");
            keyRefTag.setAttribute("name", name);
            if (fkConstraint.getKeyConstraint() == null || mapOfKeyConstraints.get(fkConstraint.getKeyConstraint()) == null) {
                throw new IllegalArgumentException(" --- Unable to locate referred key in a foreignKey constraint");
            }

            keyRefTag.setAttribute("refer", mapOfKeyConstraints.get(fkConstraint.getKeyConstraint()));
            Element selectorTag = document.createElement(PREFIX + "selector");
            selectorTag.setAttribute("xpath", ".//" + fkConstraint.getForeignKeyPaths().get(0).getLastNode(dataSource.getIntermediateSchema()).getFather().getLabel());
            keyRefTag.appendChild(selectorTag);

            for (PathExpression pathExpression : fkConstraint.getForeignKeyPaths()) {
                Element fieldTag = document.createElement(PREFIX + "field");
                keyRefTag.appendChild(fieldTag);
                String xpath = "";
                if (pathExpression.getLastNode(dataSource.getIntermediateSchema()) instanceof MetadataNode) {
                    xpath = "@";
                }
                xpath += pathExpression.getLastNode(dataSource.getIntermediateSchema()).getLabel();
                fieldTag.setAttribute("xpath", xpath);
            }
            node.appendChild(keyRefTag);
        }
    }
}

////////////////////   PROCESS SCHEMA NODE
class ProcessSchemaNodeVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(ProcessSchemaNodeVisitor.class);
    private Stack<Element> stackOfElements = new Stack<Element>();
    private Document document;

    public ProcessSchemaNodeVisitor(Document document) {
        this.document = document;
        this.stackOfElements.push(document.getDocumentElement());
    }

    private void visitGenericNode(INode node) {
        if (logger.isDebugEnabled()) logger.debug(" --- invocation in : " + node.getLabel());
        visitChildren(node);
        if (logger.isDebugEnabled()) logger.debug(" --- removed : " + stackOfElements.peek().getNodeName());
        stackOfElements.pop();
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void checkOccurs(INode node, Element elementTag) {
        if (node.isRoot()) {
            return;
        }

        if (node.isRequired()) {
            elementTag.setAttribute("minOccurs", "1");
        } else if (!node.isRequired()) {
            elementTag.setAttribute("minOccurs", "0");
        }
        if (node.isNotNull()) {
            elementTag.setAttribute("nillable", "false");
        } else {
            elementTag.setAttribute("nillable", "true");
        }
    }

    private Element createAndAppendElement(INode node) {
        String name = node.getLabel();
        Element elementTag = document.createElement(ExportXSD.PREFIX + "element");
        elementTag.setAttribute("name", name);
        checkOccurs(node, elementTag);
        stackOfElements.peek().appendChild(elementTag);
        verifyMaxOccurs(elementTag, node);
        return elementTag;
    }

    private void verifyMaxOccurs(Element element, INode node) {
        if (node.getFather() instanceof SetNode) {
            element.setAttribute("maxOccurs", "unbounded");
            if (logger.isDebugEnabled()) logger.debug(" Attribute MaxOccours for element: " + element.getNodeName() + "- Node analyzed : " + node.getLabel());
            if (logger.isDebugEnabled()) logger.debug(" maxOccurs = " + element.getAttribute("maxOccurs"));
        }
    }

    private Element createComplexType(Element fatherElement) {
        Element complexType = document.createElement(ExportXSD.PREFIX + "complexType");
        fatherElement.appendChild(complexType);
        return complexType;
    }

    private void createSequenceTag(INode node) {
        if (!node.isVirtual()) {
            Element elementTag = createAndAppendElement(node);
            Element sequenceTag = document.createElement(ExportXSD.PREFIX + "sequence");
            Element complexType = createComplexType(elementTag);
            complexType.appendChild(sequenceTag);
            stackOfElements.push(sequenceTag);
            //if(logger.isDebugEnabled()) logger.debug(" sequence Tag for element: " + node.getLabel() + " is Virtual = " + node.isVirtual());
            visitGenericNode(node);
        } else {
            if (logger.isDebugEnabled()) logger.debug(" -- Element virtual: " + node.getLabel());
            Element peekElement = stackOfElements.peek();
            verifyMaxOccurs(peekElement, node);
            visitChildren(node);
        }
    }

    public void visitSetNode(SetNode node) {
        createSequenceTag(node);
    }

    public void visitTupleNode(TupleNode node) {
        createSequenceTag(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        createSequenceTag(node);
    }

    public void visitUnionNode(UnionNode node) {
        if (!node.isVirtual()) {
            Element elementTag = createAndAppendElement(node);
            Element choiceTag = document.createElement(ExportXSD.PREFIX + "choice");
            Element complexType = createComplexType(elementTag);
            complexType.appendChild(choiceTag);
            stackOfElements.push(choiceTag);
            if (logger.isDebugEnabled()) logger.debug(" choice: " + node.getLabel());
            visitGenericNode(node);
        } else {
            Element peekElement = stackOfElements.peek();
            verifyMaxOccurs(peekElement, node);
            visitChildren(node);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        if (!node.isVirtual()) {
            Element elementTag = document.createElement(ExportXSD.PREFIX + "element");
            checkLeafNode(node, elementTag);
            stackOfElements.peek().appendChild(elementTag);
            checkOccurs(node, elementTag);
            if (logger.isDebugEnabled()) logger.debug(" attribute of: " + node.getLabel());
        } else {
            Element fatherElement = this.stackOfElements.peek();
            Element grandFatherElement = (Element) fatherElement.getParentNode();
            grandFatherElement.setAttribute("mixed", "true");
        }
    }

    public void visitMetadataNode(MetadataNode node) {
        if (logger.isDebugEnabled()) logger.debug(" metadata: " + node.getLabel());
        Element attributeTag = document.createElement(ExportXSD.PREFIX + "attribute");
        checkLeafNode(node, attributeTag);
        Element peekElement = stackOfElements.peek();
        Node parentPeekElement = peekElement.getParentNode();
        parentPeekElement.appendChild(attributeTag);
        if (logger.isDebugEnabled()) logger.debug(" ElementTag Father of the Metadata Node: \n" + parentPeekElement.getNodeName());
    }

    private void checkLeafNode(INode node, Element element) {
        element.setAttribute("name", node.getLabel());
        INode leafNode = node.getChild(0);
        if (leafNode != null) {
            element.setAttribute("type", ExportXSD.PREFIX + leafNode.getLabel());
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public Element getResult() {
        if (stackOfElements.size() != 1) {
            throw new IllegalArgumentException("- Exception in ProcessNode: stackOfElements.size() must be == 1");
        }
        if (logger.isDebugEnabled()) logger.debug(" --- Root Element: " + stackOfElements.peek().getNodeName());
        return stackOfElements.pop();
    }
}
