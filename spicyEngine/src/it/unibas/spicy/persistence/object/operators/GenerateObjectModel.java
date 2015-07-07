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
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.Constants;
import it.unibas.spicy.persistence.object.IllegalModelException;
import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.ObjectModel;
import it.unibas.spicy.persistence.object.model.ReferenceProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateObjectModel {

    public ObjectModel generate(IDataSourceProxy dataSource) {
        INode instanceRoot = dataSource.getInstances().get(0);
        GenerateObjectModelVisitor visitor = new GenerateObjectModelVisitor(dataSource);
        instanceRoot.accept(visitor);
        ObjectModel objectModel = visitor.getResult();
        Map<String, Map<Object, Object>> objectMap = visitor.getObjectMap();
        GenerateReferencesVisitor visitorReferences = new GenerateReferencesVisitor(dataSource, objectModel, objectMap);
        instanceRoot.accept(visitorReferences);
        objectModel = visitorReferences.getResult();
        return objectModel;
    }
}

class GenerateObjectModelVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateObjectModelVisitor.class);
    private IDataSourceProxy dataSource;
    private ObjectModel objectModel;
    private Map<String, Object> tupleMap = new HashMap<String, Object>();
    private Map<String, Map<Object, Object>> objectMap = new HashMap<String, Map<Object, Object>>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();

    public GenerateObjectModelVisitor(IDataSourceProxy dataSource) {
        this.dataSource = dataSource;
        this.objectModel = new ObjectModel(dataSource.getIntermediateSchema().getLabel(), new ArrayList<Object>());
    }

    public ObjectModel getResult() {
        return objectModel;
    }

    public Map<String, Map<Object, Object>> getObjectMap() {
        return objectMap;
    }

    public void visitTupleNode(TupleNode node) {
        visitChildren(node);
        if (isLeafTuple(node)) {
            generateObject(node);
        }
    }

    public void visitSetNode(SetNode node) {
        INode nodeInSchema = findNodeInSchema(node);
        if (logger.isDebugEnabled()) logger.debug("Node in schema: " + nodeInSchema.getLabel());
        if (nodeInSchema.getAnnotation(Constants.SET_TYPE) != null && nodeInSchema.getAnnotation(Constants.SET_TYPE).equals(Constants.HIERARCHY)) {
            if (logger.isDebugEnabled()) logger.debug("Found an HIERARCHY annotation: " + nodeInSchema.getAnnotation(Constants.SET_TYPE));
            visitChildren(node);
        }
    }

    private IClassNode findClassNodeInSchema(INode node) {
        INode nodeInSchema = findNodeInSchema(node);
        IClassNode classNode = (IClassNode) nodeInSchema.getAnnotation(Constants.CLASS_NODE);
        return classNode;
    }

    private INode findNodeInSchema(INode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        INode schemaRoot = dataSource.getIntermediateSchema();
        INode nodeInSchema = nodeFinder.findNodeInSchema(nodePath, schemaRoot);
        return nodeInSchema;
    }

    private void generateObject(INode tupleNode) {
        try {
            String className = ClassUtility.decodeClassName(tupleNode.getLabel());
            Object newObject = Class.forName(className).newInstance();
            if (logger.isDebugEnabled()) logger.debug("Creating a new instance for class: " + newObject);
            for (String propertyKey : tupleMap.keySet()) {
                Object propertyValue = tupleMap.get(propertyKey);
                if (logger.isDebugEnabled()) logger.debug("PropertyKey: " + propertyKey + " - propertyValue: " + propertyValue);
                ClassUtility.invokeSetMethod(ClassUtility.generateSetMethodName(propertyKey), newObject, propertyValue);
            }
            objectModel.add(newObject);
            tupleMap.clear();
            IClassNode classNode = findClassNodeInSchema(tupleNode);
            String idName = ClassUtility.getIdNode(classNode).getName();
            Object idValue = ClassUtility.invokeGetMethod(ClassUtility.generateGetMethodName(idName), newObject);
            addId(className, idValue, newObject);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void addId(String className, Object idValue, Object newObject) {
        if (logger.isDebugEnabled()) logger.debug("Adding a new object in map: " + newObject);
        if (logger.isDebugEnabled()) logger.debug("...with classname: " + className);
        if (logger.isDebugEnabled()) logger.debug("...with idValue: " + idValue);
        Map<Object, Object> idMap = objectMap.get(className);
        if (idMap == null) {
            idMap = new HashMap<Object, Object>();
            objectMap.put(className, idMap);
        }
        idMap.put(idValue, newObject);
    }

    private boolean isLeafTuple(TupleNode node) {
        if (node.isRoot()) {
            return false;
        }
        for (INode child : node.getChildren()) {
            if (child instanceof TupleNode) {
                return false;
            }
        }
        return true;
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        if (logger.isDebugEnabled()) logger.debug("adding tuple in map: " + node.getLabel() + " - value: " + node.getChild(0).getValue());
        if (logger.isDebugEnabled()) logger.debug("attributeNode: " + node);
        if (logger.isDebugEnabled()) logger.debug("leafNode: " + node.getChild(0));
        tupleMap.put(node.getLabel(), node.getChild(0).getValue());
    }

    public void visitSequenceNode(SequenceNode node) {
    }

    public void visitUnionNode(UnionNode node) {
    }

    public void visitMetadataNode(MetadataNode node) {
    }

    public void visitLeafNode(LeafNode node) {
    }
}

class GenerateReferencesVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateReferencesVisitor.class);
    private IDataSourceProxy dataSource;
    private ObjectModel objectModel;
    private Map<String, Map<Object, Object>> objectMap;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();

    public GenerateReferencesVisitor(IDataSourceProxy dataSource, ObjectModel objectModel, Map<String, Map<Object, Object>> objectMap) {
        this.dataSource = dataSource;
        this.objectModel = objectModel;
        this.objectMap = objectMap;
    }

    public void visitSetNode(SetNode node) {
        INode nodeInSchema = findNodeInSchema(node);
        if (nodeInSchema.getAnnotation(Constants.SET_TYPE) != null && nodeInSchema.getAnnotation(Constants.SET_TYPE).equals(Constants.ASSOCIATION)) {
            if (logger.isDebugEnabled()) logger.debug("Found an ASSOCIATION annotation");
            visitChildren(node);
        }
    }

    public void visitTupleNode(TupleNode node) {
        if (node.isRoot()) {
            visitChildren(node);
        } else {
            INode setNode = node.getFather();
            INode setNodeInSchema = findNodeInSchema(setNode);
            ReferenceProperty referenceProperty = (ReferenceProperty) setNodeInSchema.getAnnotation(Constants.REFERENCE_PROPERTY);
            generateAssociation(referenceProperty, node);
            if (referenceProperty.getInverse() != null) {
                generateAssociation(referenceProperty.getInverse(), node);
            }
        }
    }

    private void generateAssociation(ReferenceProperty referenceProperty, TupleNode tupleNode) {
        Object sourceObject = findObject(tupleNode, referenceProperty.getSource());
        Object targetObject = findObject(tupleNode, referenceProperty.getTarget());
        String fieldName = referenceProperty.getField().getName();
        String cardinality = referenceProperty.getCardinality();
        if (cardinality.equals(Constants.ONE)) {
            generateReference(sourceObject, targetObject, fieldName);
        } else if (cardinality.equals(Constants.MANY)) {
            generateReferenceCollection(sourceObject, targetObject, fieldName);
        }
    }

    private void generateReference(Object sourceObject, Object targetObject, String fieldName) {
        ClassUtility.invokeSetMethod(ClassUtility.generateSetMethodName(fieldName), sourceObject, targetObject);
    }

    @SuppressWarnings("unchecked")
    private void generateReferenceCollection(Object sourceObject, Object targetObject, String fieldName) {
        Collection collection = (Collection) ClassUtility.invokeGetMethod(ClassUtility.generateGetMethodName(fieldName), sourceObject);
        if (collection == null) {
            try {
                collection = (Collection) Class.forName(Constants.ARRAYLIST).newInstance();
                ClassUtility.invokeSetMethod(ClassUtility.generateSetMethodName(fieldName), sourceObject, collection);
            } catch (Exception ex) {
                logger.error(ex);
                throw new IllegalModelException(ex.getMessage());
            }
        }
        ((Collection) collection).add(targetObject);
    }

    private Object findObject(INode tupleNode, IClassNode classNode) {
        String refName = Constants.REF + classNode.getCorrespondingClass().getSimpleName();
        INode attributeNode = tupleNode.searchChild(refName);
        Object value = attributeNode.getChild(0).getValue();
        Object object = findObjectInMap(ClassUtility.decodeClassName(classNode.getName()), value);
        return object;
    }

    private Object findObjectInMap(String className, Object id) {
        if (logger.isDebugEnabled()) logger.debug("Seeking class: " + className + " with id " + id);
        if (logger.isDebugEnabled()) logger.debug("ObjectMap size: " + objectMap.size());
        Map<Object, Object> idMap = objectMap.get(className);
        if (logger.isDebugEnabled()) logger.debug("IdMap: " + idMap);
        return idMap.get(id);
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }

    }

    private INode findNodeInSchema(INode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        INode schemaRoot = dataSource.getIntermediateSchema();
        INode nodeInSchema = nodeFinder.findNodeInSchema(nodePath, schemaRoot);
        return nodeInSchema;
    }

    public void visitSequenceNode(SequenceNode node) {
    }

    public void visitUnionNode(UnionNode node) {
    }

    public void visitAttributeNode(AttributeNode node) {
    }

    public void visitMetadataNode(MetadataNode node) {
    }

    public void visitLeafNode(LeafNode node) {
    }

    public ObjectModel getResult() {
        return objectModel;
    }
}

