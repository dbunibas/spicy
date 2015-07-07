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
 
package it.unibas.spicy.persistence.object.operators;

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
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.persistence.Types;
import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.Constants;
import it.unibas.spicy.persistence.object.IllegalModelException;
import it.unibas.spicy.persistence.object.model.ClassTree;
import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.ReferenceProperty;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateObjectInstance {

    private static Log logger = LogFactory.getLog(GenerateObjectInstance.class);
    
    private static IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
    private Map<Integer, INode> objectMap = new HashMap<Integer, INode>();

    public void addInstance(IDataSourceProxy dataSource, List<Object> objects) {
        INode rootInstanceNode = generateRootInstanceNode(dataSource);
        rootInstanceNode.setRoot(true);
        for (Object o : objects) {
            generateInstanceForObject(dataSource, rootInstanceNode, o);
        }
        for (Object o : objects) {
            generateReferencesForObject(dataSource, rootInstanceNode, o);
        }
        if (logger.isDebugEnabled()) logger.debug(rootInstanceNode);
        dataSource.addInstanceWithCheck(rootInstanceNode);
    }

    private void generateInstanceForObject(IDataSourceProxy dataSource, INode rootInstanceNode, Object object) {
        String objectClassName = object.getClass().getCanonicalName();
        ClassTree classTree = (ClassTree) dataSource.getIntermediateSchema().getAnnotation(Constants.CLASS_TREE);
        IClassNode classNode = classTree.get(objectClassName);
        SetNode setNodeInSchema = findFatherSetInSchema(classNode);
        INode setNodeInInstance = getSetNodeInInstance(rootInstanceNode, setNodeInSchema);
        GenerateObjectInstanceVisitor visitor = new GenerateObjectInstanceVisitor(object, objectMap);
        setNodeInSchema.accept(visitor);
        INode tupleInInstance = visitor.getResult();
        setNodeInInstance.addChild(tupleInInstance);
    }

    private SetNode findFatherSetInSchema(IClassNode classNode) {
        INode node = classNode.getSchemaNode();
        while (!(node instanceof SetNode)) {
            node = node.getFather();
        }
        return (SetNode) node;
    }

    private INode generateRootInstanceNode(IDataSourceProxy dataSource) {
        INode tupleNode = new TupleNode(dataSource.getIntermediateSchema().getLabel(), getOID());
        return tupleNode;
    }

    public static OID getOID() {
        return oidGenerator.getNextOID();
    }

    private INode getSetNodeInInstance(INode rootInstanceNode, INode setNodeInSchema) {
        INode setNodeInInstance = rootInstanceNode.searchChild(setNodeInSchema.getLabel());
        if (setNodeInInstance == null) {
            setNodeInInstance = new SetNode(setNodeInSchema.getLabel(), getOID());
            rootInstanceNode.addChild(setNodeInInstance);
        }
        return setNodeInInstance;
    }

    private void generateReferencesForObject(IDataSourceProxy dataSource, INode rootInstanceNode, Object object) {
        String objectClassName = object.getClass().getCanonicalName();
        if (logger.isDebugEnabled()) logger.debug("Analyzing object: " + object);
        ClassTree classTree = (ClassTree) dataSource.getIntermediateSchema().getAnnotation(Constants.CLASS_TREE);
        IClassNode classNode = classTree.get(objectClassName);
        List<ReferenceProperty> references = classNode.getReferenceProperties();
        for (ReferenceProperty reference : references) {
            String associationSetNodeName = reference.getSchemaNode().getLabel();
            INode setNodeInSchema = dataSource.getIntermediateSchema().searchChild(associationSetNodeName);
            INode setNodeInInstance = getSetNodeInInstance(rootInstanceNode, setNodeInSchema);
            if (logger.isDebugEnabled()) logger.debug("Analyzing reference: " + reference.getName());
            if (ClassUtility.isAReferenceCollection(reference.getField())) {
                generateTupleNodesFromCollection(reference, object, setNodeInInstance);
            } else {
                generateTupleNodeFromReference(reference, object, setNodeInInstance);
            }
        }
    }

    private void generateTupleNodesFromCollection(ReferenceProperty reference, Object object, INode setNodeInInstance) {
        try {
            Object ref = ClassUtility.invokeGetMethod(ClassUtility.generateGetMethodName(reference.getField().getName()), object);
            if (ref == null) {
                if (logger.isDebugEnabled()) logger.debug("Found a null collection");
                return;
            }
            Collection collection = (Collection) ref;
            if (logger.isDebugEnabled()) logger.debug("collection size: " + collection.size());
            for (Object element : collection) {
                INode tupleNodeInInstance = generateTupleNode(reference, object, element);
                setNodeInInstance.addChild(tupleNodeInInstance);
            }
        } catch (Exception ex) {
            logger.error(ex);
            throw new IllegalModelException(ex.getMessage());
        }
    }

    private INode generateTupleNode(ReferenceProperty reference, Object refSource, Object refTarget) {
        INode tupleNodeInSchema = reference.getSchemaNode().getChild(0);
        INode tupleNodeInInstance = new TupleNode(tupleNodeInSchema.getLabel(), getOID());
        INode sourceAttribute = generateAttributeNode(reference.getSource(), refSource, tupleNodeInSchema);
        INode targetAttribute = generateAttributeNode(reference.getTarget(), refTarget, tupleNodeInSchema);
        tupleNodeInInstance.addChild(sourceAttribute);
        tupleNodeInInstance.addChild(targetAttribute);
        return tupleNodeInInstance;
    }

    private INode generateAttributeNode(IClassNode classNode, Object object, INode tupleNodeInSchema) {
        try {
            String attributeNodeName = Constants.REF + ClassUtility.extractSimpleClassName(classNode.getName());
            INode attributeNodeInSchema = tupleNodeInSchema.searchChild(attributeNodeName);
            INode attributeNodeInInstance = new AttributeNode(attributeNodeInSchema.getLabel(), getOID());
            INode leafNodeInSchema = attributeNodeInSchema.getChild(0);
            String getIdMethodName = ClassUtility.generateGetMethodName(ClassUtility.getIdNode(classNode).getName());
            Object value = ClassUtility.invokeGetMethod(getIdMethodName, object);
            String type = leafNodeInSchema.getLabel();
            INode leafNodeInInstance = new LeafNode(type, Types.getTypedValue(type, value));
            attributeNodeInInstance.addChild(leafNodeInInstance);
            return attributeNodeInInstance;
        } catch (Exception ex) {
            logger.error(ex);
            throw new IllegalModelException(ex.getMessage());
        }
    }

    private void generateTupleNodeFromReference(ReferenceProperty reference, Object object, INode setNodeInInstance) {
        try {
            Object ref = ClassUtility.invokeGetMethod(ClassUtility.generateGetMethodName(reference.getField().getName()), object);
            INode tupleNodeInInstance = generateTupleNode(reference, object, ref);
            setNodeInInstance.addChild(tupleNodeInInstance);
        } catch (Exception ex) {
            logger.error(ex);
            throw new IllegalModelException(ex.getMessage());
        }
    }
}

class GenerateObjectInstanceVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateObjectInstanceVisitor.class);

    private Object object;
    private INode rootInstanceNode;
    private INode currentNodeInInstance;
    private Map<Integer, INode> objectMap;

    GenerateObjectInstanceVisitor(Object object, Map<Integer, INode> objectMap) {
        this.object = object;
        this.objectMap = objectMap;
    }

    public void visitSetNode(SetNode setNodeInSchema) {
        INode tupleInSchema = setNodeInSchema.getChild(0);
        this.rootInstanceNode = new TupleNode(tupleInSchema.getLabel(), GenerateObjectInstance.getOID());
        putObject(object, rootInstanceNode);
        this.currentNodeInInstance = rootInstanceNode;
        for (INode child : tupleInSchema.getChildren()) {
            child.accept(this);
        }
    }

    public void visitTupleNode(TupleNode tupleNodeInSchema) {
        if (!objectHasType(tupleNodeInSchema, object)) {
            return;
        }
        INode oldCurrentNodeInInstance = currentNodeInInstance;
        INode newTupleInInstance = new TupleNode(tupleNodeInSchema.getLabel(), GenerateObjectInstance.getOID());
        currentNodeInInstance.addChild(newTupleInInstance);
        putObject(object, newTupleInInstance);
        currentNodeInInstance = newTupleInInstance;
        for (INode child : tupleNodeInSchema.getChildren()) {
            child.accept(this);
        }
        currentNodeInInstance = oldCurrentNodeInInstance;
    }

    private void putObject(Object object, INode tupleNode) {
        if (objectMap.get(object.hashCode()) == null) {
            if (logger.isDebugEnabled()) logger.debug("Adding object in map: " + object);
            objectMap.put(object.hashCode(), tupleNode);
        }
    }

    private boolean objectHasType(TupleNode tupleNodeInSchema, Object object) {
        try {
            Class tupleClass = Class.forName(ClassUtility.decodeClassName(tupleNodeInSchema.getLabel()));
            if (tupleClass.isInstance(object)) {
                return true;
            }
        } catch (Exception ex) {
            logger.error(ex);
            return false;
        }
        return false;
    }

    public void visitAttributeNode(AttributeNode attributeNodeInSchema) {
        INode newAttributeNodeInInstance = generateAttributeNode(attributeNodeInSchema);
        currentNodeInInstance.addChild(newAttributeNodeInInstance);
    }

    private INode generateAttributeNode(AttributeNode attributeNodeInSchema) {
        AttributeNode attributeNodeInInstance = new AttributeNode(attributeNodeInSchema.getLabel(), GenerateObjectInstance.getOID());
        INode leafNodeInSchema = attributeNodeInSchema.getChild(0);
        Object nodeValue = ClassUtility.invokeGetMethod(ClassUtility.generateGetMethodName(attributeNodeInSchema.getLabel()), object);
        LeafNode leafNodeInInstance = new LeafNode(leafNodeInSchema.getLabel(), nodeValue);
        attributeNodeInInstance.addChild(leafNodeInInstance);
        return attributeNodeInInstance;
    }

    public void visitSequenceNode(SequenceNode node) {
        return;
    }

    public void visitUnionNode(UnionNode node) {
        return;
    }

    public void visitMetadataNode(MetadataNode node) {
        return;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public INode getResult() {
        return rootInstanceNode;
    }
}

