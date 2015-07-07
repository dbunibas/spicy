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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.Constants;
import it.unibas.spicy.persistence.object.model.ClassTree;
import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.PersistentProperty;
import it.unibas.spicy.persistence.object.model.ReferenceProperty;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateObjectDataSource {

    private static Log logger = LogFactory.getLog(GenerateObjectDataSource.class);
    
    private GenerateClassModelTree classModelTreeGenerator = new GenerateClassModelTree();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public IDataSourceProxy generateDataSource(String rootName, List<Class> classes) {
        ClassTree classTree = classModelTreeGenerator.generateClassModelTree(classes);
        if (logger.isDebugEnabled()) {
            logger.debug("Class Tree: \n" + classTree);
        }
        GenerateDataSourceVisitor visitor = new GenerateDataSourceVisitor(classTree, rootName);
        visitor.visitClassNode(classTree.getRoot());
        DataSource dataSource = visitor.getResult();
        IDataSourceProxy dataSourceProvider = new ConstantDataSourceProxy(dataSource);
        generateReferences(classTree, dataSourceProvider);
        return dataSourceProvider;
    }

    private void generateReferences(ClassTree classTree, IDataSourceProxy dataSource) {
        for (IClassNode classNode : classTree.getElements().values()) {
            generateReferencesForClass(classNode, dataSource);
        }
    }

    private void generateReferencesForClass(IClassNode classNode, IDataSourceProxy dataSource) {
        for (ReferenceProperty reference : classNode.getReferenceProperties()) {
            if (reference.isTranslated()) {
                continue;
            }
            String nodeName = reference.getAssociationTupleNodeName();
            INode setNode = new SetNode(reference.getAssociationSetNodeName());
            setNode.setVirtual(true);
            setNode.addAnnotation(Constants.SET_TYPE, Constants.ASSOCIATION);
            setNode.addAnnotation(Constants.REFERENCE_PROPERTY, reference);
            reference.setSchemaNode(setNode);
            INode tupleNode = new TupleNode(nodeName);
            setNode.addChild(tupleNode);
            dataSource.getIntermediateSchema().addChild(setNode);
            generateAssociationAttributes(tupleNode, reference, dataSource);
        }
    }

    private void generateAssociationAttributes(INode tupleNode, ReferenceProperty reference, IDataSourceProxy dataSource) {
        String refSourceName = Constants.REF + ClassUtility.extractSimpleClassName(reference.getSource().getName());
        if (logger.isDebugEnabled()) logger.debug("Node refSource: " + refSourceName);
        INode refSource = new AttributeNode(refSourceName);
        PersistentProperty sourceId = ClassUtility.getIdNode(reference.getSource());
        INode leafSourceNode = new LeafNode(sourceId.getSchemaNode().getChild(0).getLabel());
        refSource.addChild(leafSourceNode);
        String refTargetName = Constants.REF + ClassUtility.extractSimpleClassName(reference.getTarget().getName());
        if (logger.isDebugEnabled()) logger.debug("Node refTarget: " + refTargetName);
        INode refTarget = new AttributeNode(refTargetName);
        PersistentProperty targetId = ClassUtility.getIdNode(reference.getTarget());
        INode leafTargetNode = new LeafNode(targetId.getSchemaNode().getChild(0).getLabel());
        refTarget.addChild(leafTargetNode);
        tupleNode.addChild(refSource);
        tupleNode.addChild(refTarget);
        dataSource.addForeignKeyConstraint(generateForeignKeyConstraint(sourceId.getKeyConstraint(), refSource));
        dataSource.addForeignKeyConstraint(generateForeignKeyConstraint(targetId.getKeyConstraint(), refTarget));
        reference.setTranslated(true);
        if (reference.getInverse() != null) {
            reference.getInverse().setTranslated(true);
            reference.getInverse().setSchemaNode(tupleNode.getFather());
        }
        addKeyConstraints(reference, refSource, refTarget, dataSource);
    }

    
    private ForeignKeyConstraint generateForeignKeyConstraint(KeyConstraint keyConstraint, INode referenceNode) {
        PathExpression referencePath = pathGenerator.generatePathFromRoot(referenceNode);
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint(keyConstraint, referencePath);
        return foreignKey;
    }

    private void addKeyConstraints(ReferenceProperty reference, INode refSource, INode refTarget, IDataSourceProxy dataSource) {
        PathExpression refSourcePath = pathGenerator.generatePathFromRoot(refSource);
        PathExpression refTargetPath = pathGenerator.generatePathFromRoot(refTarget);
        if (reference.getCardinality().equals(Constants.ONE)) {
            KeyConstraint refSourceKeyConstraint = new KeyConstraint(refSourcePath);
            dataSource.addKeyConstraint(refSourceKeyConstraint);
            if (reference.getInverse() != null && reference.getInverse().getCardinality().equals(Constants.ONE)) {
                KeyConstraint refTargetKeyConstraint = new KeyConstraint(refTargetPath);
                dataSource.addKeyConstraint(refTargetKeyConstraint);
            }
        } else if (reference.getCardinality().equals(Constants.MANY)) {
            if (reference.getInverse() != null && reference.getInverse().getCardinality().equals(Constants.ONE)) {
                KeyConstraint refTargetKeyConstraint = new KeyConstraint(refTargetPath);
                dataSource.addKeyConstraint(refTargetKeyConstraint);
            } else {
                List<PathExpression> keyPaths = new ArrayList<PathExpression>();
                keyPaths.add(refSourcePath);
                keyPaths.add(refTargetPath);
                KeyConstraint refSourceTargetKeyConstraint = new KeyConstraint(keyPaths);
                dataSource.addKeyConstraint(refSourceTargetKeyConstraint);
            }
        }
    }
}

class GenerateDataSourceVisitor implements IClassNodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateDataSourceVisitor.class);
    
    private ClassTree classTree;
    private String rootName;
    private DataSource dataSource;
    private INode fatherNode;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public GenerateDataSourceVisitor(ClassTree classTree, String rootName) {
        this.classTree = classTree;
        this.rootName = rootName;
    }

    public void visitClassNode(IClassNode node) {
        INode oldFatherNode = fatherNode;
        if (node.isRoot()) {
            visitRoot(node);
        } else {
            visitClass(node);
        }
        for (IClassNode currentModelNode : node.getChildren()) {
            currentModelNode.accept(this);
        }
        fatherNode = oldFatherNode;
    }

    private void visitRoot(IClassNode node) {
        INode rootNode = new TupleNode(rootName);
        rootNode.setRoot(true);
        rootNode.addAnnotation(Constants.CLASS_TREE, classTree);
        fatherNode = rootNode;
        dataSource = new DataSource(SpicyEngineConstants.TYPE_OBJECT, rootNode);
        //TODO: add annotations for objects
    }

    private void visitClass(IClassNode classNode) {
        if (classNode.getFather().equals(classTree.getRoot())) {
            INode setNode = new SetNode(classNode.getName() + Constants.SET);
            setNode.addAnnotation(Constants.SET_TYPE, Constants.HIERARCHY);
            fatherNode.addChild(setNode);
            fatherNode = setNode;
        }
        INode tupleNode = new TupleNode(classNode.getName());
        //tupleNode.setRequired(false);
        tupleNode.addAnnotation(Constants.CLASS_NODE, classNode);
        classNode.setSchemaNode(tupleNode);
        fatherNode.addChild(tupleNode);
        fatherNode = tupleNode;
        generateAttributes(tupleNode, classNode);
    }

    private void generateAttributes(INode tupleNode, IClassNode classNode) {
        if (classNode.getId() != null) {
            generateId(tupleNode, classNode);
        }
        for (PersistentProperty property : classNode.getPersistentProperties()) {
            generateAttribute(tupleNode, property);
        }
    }

    private void generateId(INode tupleNode, IClassNode classNode) {
        AttributeNode keyNode = generateAttribute(tupleNode, classNode.getId());
        //keyNode.setRequired(false);
        classNode.getId().setSchemaNode(keyNode);
        PathExpression keyPath = pathGenerator.generatePathFromRoot(keyNode);
        KeyConstraint keyConstraint = new KeyConstraint(keyPath, true);
        classNode.getId().setKeyConstraint(keyConstraint);
        dataSource.addKeyConstraint(keyConstraint);
    }

    private AttributeNode generateAttribute(INode tupleNode, PersistentProperty property) {
        AttributeNode attributeNode = new AttributeNode(property.getName());
        //attributeNode.setRequired(false);
        property.setSchemaNode(attributeNode);
        tupleNode.addChild(attributeNode);
        INode leafNode = new LeafNode(property.getType());
        attributeNode.addChild(leafNode);
        return attributeNode;
    }

    public DataSource getResult() {
        return dataSource;
    }
}
