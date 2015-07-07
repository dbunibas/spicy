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
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.Types;
import it.unibas.spicy.persistence.xml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

public class LoadXMLFile {

    protected static Log logger = LogFactory.getLog(LoadXMLFile.class);
    private static IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
    private IDataSourceProxy dataSource;
    private boolean skipSetElement;
    private List<String> currentPathInDOM = new ArrayList<String>();
    private Map<String, INode> nodeMap = new HashMap<String, INode>();
    private INode instanceRoot;

    public INode loadInstance(IDataSourceProxy dataSource, String fileName) throws IllegalSchemaException, DAOException {
        return loadInstance(dataSource, false, fileName);
    }

    public INode loadInstance(IDataSourceProxy dataSource, boolean skipSetElement, String fileName) throws IllegalSchemaException, DAOException {
        this.dataSource = dataSource;
        this.skipSetElement = skipSetElement;
        if (logger.isDebugEnabled()) logger.debug("Data source schema: " + dataSource.getSchema());
        DAOXmlUtility daoUtility = new DAOXmlUtility();
        Document document = daoUtility.buildDOM(fileName);
        Element domRoot = document.getRootElement();
        analyzeElement(domRoot, null);
        return instanceRoot;
    }

    private INode generateInstanceNode(INode schemaNode) {
        INode instanceNode = null;
        if (schemaNode instanceof SetNode) {
            instanceNode = new SetNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof UnionNode) {
            instanceNode = new UnionNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof SequenceNode) {
            instanceNode = new SequenceNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof TupleNode) {
            instanceNode = new TupleNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof MetadataNode) {
            instanceNode = new MetadataNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof AttributeNode) {
            instanceNode = new AttributeNode(schemaNode.getLabel(), getOID());
        }
        instanceNode.setRoot(schemaNode.isRoot());
        instanceNode.setVirtual(schemaNode.isVirtual());
        if (logger.isDebugEnabled()) logger.debug("Generated instance node: " + instanceNode.getLabel());
        return instanceNode;
    }

    private void analyzeElement(Element element, INode fatherInInstance) throws DAOException {
        String elementLabel = element.getName();
        if (skipSetElement && isSetElement(element)) {
            List listOfChildren = element.getChildren();
            for (Iterator it = listOfChildren.iterator(); it.hasNext();) {
                Element child = (Element) it.next();
                analyzeElement(child, fatherInInstance);
            }
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Visiting element: " + elementLabel + " - Current path in DOM: " + currentPathInDOM + " - Father in instance: " + fatherInInstance);
        currentPathInDOM.add(elementLabel);
        INode nodeInSchema = findNodeInSchema(currentPathInDOM);
        assert (nodeInSchema != null) : "Element must be present in schema: " + elementLabel;
        INode instanceNode = generateInstanceNodes(nodeInSchema, fatherInInstance);
        if (nodeInSchema instanceof AttributeNode) {
            assignValue(nodeInSchema, instanceNode, element);
        }
        if (logger.isDebugEnabled()) logger.debug("Instance node: " + instanceNode.getLabel());
        if (logger.isTraceEnabled()) logger.trace("Current instance: " + instanceRoot);
        List listOfChildren = element.getChildren();
        List listOfAttributes = element.getAttributes();
        for (Iterator it = listOfAttributes.iterator(); it.hasNext();) {
            Attribute attribute = (Attribute) it.next();
            analyzeAttribute(attribute, instanceNode);
        }
        for (Iterator it = listOfChildren.iterator(); it.hasNext();) {
            Element child = (Element) it.next();
            analyzeElement(child, instanceNode);
        }
        checkPCDATA(element, nodeInSchema, instanceNode);
        currentPathInDOM.remove(currentPathInDOM.size() - 1);
    }

    private boolean isSetElement(Element element) {
        if (element == null) {
            return false;
        }
        String elementLabel = element.getName();
        if (!elementLabel.endsWith("Set")) {
            return false;
        }
        if (!element.getChildren().isEmpty()) {
            Element firstChild = (Element) element.getChildren().get(0);
            String firstChildLabel = firstChild.getName();
            if (!elementLabel.equals(firstChildLabel + "Set")) {
                return false;
            }
        }
        return true;
    }

    private void analyzeAttribute(Attribute attribute, INode fatherInInstance) throws DAOException {
        String attributeLabel = attribute.getName();
        if (logger.isDebugEnabled()) logger.debug("Visiting attribute: " + attributeLabel + " - Current path in DOM: " + currentPathInDOM + " - Father in instance: " + fatherInInstance);
        currentPathInDOM.add(attributeLabel);
        INode nodeInSchema = findNodeInSchema(currentPathInDOM);
        if (nodeInSchema != null) {
            INode metadataNode = generateInstanceNode(nodeInSchema);
            assignMetadataValue(attribute, nodeInSchema, metadataNode);
            fatherInInstance.addChild(metadataNode);
        }
        currentPathInDOM.remove(currentPathInDOM.size() - 1);
    }

    private INode findNodeInSchema(List<String> pathSteps) {
        if (logger.isTraceEnabled()) logger.debug("Searching schema node: " + pathSteps);
        //TODO: check efficiency of caching
        INode node = nodeMap.get(pathSteps.toString());
        if (node == null) {
            if (logger.isTraceEnabled()) logger.debug("Node not found in cache. Searching...");
            FindNodeFromPathWithVirtualNodes nodeFinder = new FindNodeFromPathWithVirtualNodes();
            node = nodeFinder.findNodeInSchema(dataSource.getIntermediateSchema(), pathSteps);
        }
        if (node != null) {
            if (logger.isTraceEnabled()) logger.debug("Result: " + node.getLabel());
            nodeMap.put(pathSteps.toString(), node);
        }
        return node;
    }

    private INode generateInstanceNodes(INode nodeInSchema, INode fatherInInstance) {
        if (logger.isDebugEnabled()) logger.debug("Generating new nodes for node: " + nodeInSchema.getLabel());
        List<String> pathSteps = generatePathStepsInSchema(nodeInSchema, fatherInInstance);
        if (logger.isDebugEnabled()) logger.debug("Path steps: " + pathSteps);
        if (fatherInInstance != null) {
            fatherInInstance = removeStepsForExistingNodes(nodeInSchema, fatherInInstance, pathSteps);
            if (logger.isDebugEnabled()) logger.debug("Path steps after removal: " + pathSteps);
        }
        List<INode> ancestorsInSchema = generateAncestors(nodeInSchema, pathSteps);
        INode currentFather = fatherInInstance;
        INode instanceNode = null;
        for (INode ancestorInSchema : ancestorsInSchema) {
            instanceNode = generateInstanceNode(ancestorInSchema);
            if (fatherInInstance == null) {
                this.instanceRoot = instanceNode;
            } else {
                currentFather.addChild(instanceNode);
            }
            currentFather = instanceNode;
        }
        return instanceNode;
    }

    private List<String> generatePathStepsInSchema(INode nodeInSchema, INode fatherInInstance) {
        List<String> pathSteps = new ArrayList<String>();
        INode node = nodeInSchema;
        while (node != null && (fatherInInstance == null || !node.getLabel().equals(fatherInInstance.getLabel()))) {
            pathSteps.add(0, node.getLabel());
            node = node.getFather();
        }
        return pathSteps;
    }

    private INode removeStepsForExistingNodes(INode nodeInSchema, INode fatherInInstance, List<String> pathSteps) {
        INode father = fatherInInstance;
        for (Iterator<String> it = pathSteps.iterator(); it.hasNext();) {
            String step = it.next();
            if (step.equals(nodeInSchema.getLabel())) {
                break;
            }
            INode existingNode = findChild(father, step);
            if (existingNode == null) {
                break;
            }
            if (isVirtualTupleForSingleAttribute(existingNode)) {
                break;
            }
            father = existingNode;
            it.remove();
        }
        return father;
    }

    private INode findChild(INode father, String label) {
        for (INode child : father.getChildren()) {
            if (child.getLabel().equals(label)) {
                return child;
            }
        }
        return null;
    }

    private boolean isVirtualTupleForSingleAttribute(INode existingNode) {
        if ((existingNode instanceof TupleNode) && existingNode.isVirtual()
                && (existingNode.getFather() instanceof SetNode) && existingNode.getChildren().size() == 1) {
            return true;
        }
        return false;
    }

    private List<INode> generateAncestors(INode nodeInSchema, List<String> pathSteps) {
        List<INode> result = new ArrayList<INode>();
        INode node = nodeInSchema;
        while (node != null && pathSteps.contains(node.getLabel())) {
            result.add(0, node);
            node = node.getFather();
        }
        return result;
    }

    private void assignValue(INode schemaNode, INode instanceNode, Element element) throws DAOException {
        String type = schemaNode.getChild(0).getLabel();
        String value = element.getValue();
        if (logger.isDebugEnabled()) logger.debug("Found an attribute node: " + instanceNode.getLabel() + " - Value: " + value);
        if (!value.trim().isEmpty()) {
            instanceNode.addChild(new LeafNode(type, Types.getTypedValue(type, value.trim())));
        } else {
            instanceNode.addChild(new LeafNode(schemaNode.getChild(0).getLabel(), NullValueFactory.getNullValue()));
        }
    }

    private void assignMetadataValue(final Attribute attribute, final INode nodeInSchema, final INode metadataNode) throws DAOException {
        String value = attribute.getValue();
        if (logger.isDebugEnabled()) logger.debug("Found a metadata node: " + metadataNode.getLabel() + " - Value: " + value);
        INode leafInSchema = nodeInSchema.getChild(0);
        String type = leafInSchema.getLabel();
        if (!value.trim().isEmpty()) {
            metadataNode.addChild(new LeafNode(type, Types.getTypedValue(type, value.trim())));
        } else {
            metadataNode.addChild(new LeafNode(type, NullValueFactory.getNullValue()));
        }
    }

    private void checkPCDATA(Element element, INode nodeInSchema, INode fatherInInstance) throws DAOException {
        if (nodeInSchema instanceof TupleNode && !(nodeInSchema instanceof SequenceNode)) {
            for (INode child : nodeInSchema.getChildren()) {
                if (child instanceof SequenceNode && child.getChildren().size() == 1) {
                    INode descendant = child.getChild(0);
                    if (descendant.isVirtual() && descendant.getLabel().contains(GenerateSchemaFromXSDTree.PCDATA_SUFFIX)) {
                        INode sequence = child;
                        INode PCDATA = descendant;
                        INode PCDATAInstance = generateInstanceNode(PCDATA);
                        assignValue(PCDATA, PCDATAInstance, element);
                        INode sequenceInInstance = generateInstanceNode(sequence);
                        sequenceInInstance.addChild(PCDATAInstance);
                        fatherInInstance.addChild(sequenceInInstance);
                    }
                }
            }
        } else if (nodeInSchema instanceof SequenceNode) {
            for (INode child : nodeInSchema.getChildren()) {
                if (child.isVirtual() && child.getLabel().contains(GenerateSchemaFromXSDTree.PCDATA_SUFFIX)) {
                    INode sequence = nodeInSchema;
                    INode PCDATA = child;
                    INode PCDATAInstance = generateInstanceNode(PCDATA);
                    assignValue(PCDATA, PCDATAInstance, element);
                    fatherInInstance.addChild(PCDATAInstance);
                }
            }
        }
    }

    private static OID getOID() {
        return oidGenerator.getNextOID();
    }
}
