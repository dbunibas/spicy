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
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.DAOXmlUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportXMLInstances {

    private static Log logger = LogFactory.getLog(ExportXMLInstances.class);

    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String PREFIX = "xsi:";
    private DAOXmlUtility daoXmlUtility = new DAOXmlUtility();

    public void exportXMLinstances(List<INode> instances, String directoryPath, String suffix) throws Exception {
        if (logger.isDebugEnabled()) logger.debug(" -- Number of instances: " + instances.size());
        int i = 0;
        List<String> instanceFileNames = new ArrayList<String>();
        for (INode instanceNode : instances) {
            String filename = generateFileName(instanceNode, directoryPath, suffix, i++);
            processInstance(instanceNode, filename);
            instanceFileNames.add(filename);
        }
    }

    private String generateFileName(INode rootNode, String directoryPath, String suffix, int i) {
        String filename = "";
        if (directoryPath.endsWith(File.separator)) {
            filename = directoryPath + rootNode.getLabel() + suffix + i + ".xml";
        } else {
            filename = directoryPath + File.separator + rootNode.getLabel() + suffix + i + ".xml";
        }
        return filename;
    }

    private void processInstance(INode instance, String filename) throws DAOException {
        if (instance == null) {
            throw new IllegalArgumentException("Unable to extract instance with a null instance");
        }
        Document document = daoXmlUtility.buildNewDOM();
        processInstanceNode(instance, document);
        // adding other attributes to root
        processRoot(instance, document);
        daoXmlUtility.saveDOM(document, filename);
    }

    private void processRoot(INode node, Document document) {
        Element root = document.getDocumentElement();
        root.setAttributeNS(XSI_NS, "xsi:noNamespaceSchemaLocation", node.getLabel() + ".xsd");
    }

    private void processInstanceNode(INode instance, Document document) {
        ProcessInstanceNodeVisitor visitor = new ProcessInstanceNodeVisitor(document);
        instance.accept(visitor);
    }
}

////////////////////   PROCESS INSTANCE NODE
class ProcessInstanceNodeVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(ProcessInstanceNodeVisitor.class);
    private Stack<Element> stackOfElements = new Stack<Element>();
    private Document document;

    public ProcessInstanceNodeVisitor(Document document) {
        this.document = document;
    }

    private void visitGenericNode(INode node) {
        if (!node.isVirtual()) {
            if (logger.isDebugEnabled()) {
                logger.debug(" --- visiting: " + node.getLabel());
            }
            Element element = document.createElement(node.getLabel());
            if (stackOfElements.size() != 0) {
                stackOfElements.peek().appendChild(element);
            } else {
                document.appendChild(element);
            }
            stackOfElements.push(element);
            visitChildren(node);
            if (logger.isDebugEnabled()) {
                logger.debug(" --- removed: " + stackOfElements.peek().getNodeName());
            }
            stackOfElements.pop();
        } else {
            visitChildren(node);
        }

    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }

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
        if (!node.isVirtual()) {
            visitGenericNode(node);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("\nNODE " + node.getLabel() + " IS VIRTUAL !");
            }
            Element fatherElement = stackOfElements.peek();
            LeafNode leafOfAttributeNode = (LeafNode) node.getChild(0);
            fatherElement.appendChild(document.createTextNode(leafOfAttributeNode.getValue().toString()));
            if (logger.isDebugEnabled()) {
                logger.debug("leafOfAttributeNode value = " + leafOfAttributeNode.getValue());
            }
        }
    }

    public void visitMetadataNode(MetadataNode node) {
        Element peekElement = stackOfElements.peek();
        LeafNode leafOfMetadataNodeNode = (LeafNode) node.getChild(0);
        peekElement.setAttribute(node.getLabel(), leafOfMetadataNodeNode.getValue().toString());
    }

    public void visitLeafNode(LeafNode node) {
        if (node == null || node.getValue() == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(" -- leaf node: " + node.getValue());
        }
        Element peekElement = stackOfElements.peek();
        peekElement.setTextContent(node.getValue().toString());
    }

    public Object getResult() {
        return null;
    }
}
