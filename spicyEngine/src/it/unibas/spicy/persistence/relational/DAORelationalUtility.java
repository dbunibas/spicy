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
 
package it.unibas.spicy.persistence.relational;

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.utility.SpicyEngineUtility;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class DAORelationalUtility {
    
    private static Map<String, INode> nodeMap = new HashMap<String, INode>();
    private static Map<String, INode> instanceNodeMap = new HashMap<String, INode>();
    private static IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
    private static GeneratePathExpression pathGenerator = new GeneratePathExpression();
    
    private static Log logger = LogFactory.getLog(DAORelationalUtility.class);
    
    static OID getOID() {
        return oidGenerator.getNextOID();
    }
    
    // SCHEMA AND INSTANCES
    
    static SetNode generateSetNode(String name, TupleNode tupleNode) {
        assert(tupleNode == null || tupleNode.isSchemaNode()) : "Node must be a schema node: " + tupleNode.getLabel();
        SetNode node = new SetNode(name);
        addNode(node);
        if (tupleNode != null) {
            tupleNode.addChild(node);
        } else {
            node.setRoot(true);
        }
        return node;
    }
    
    static SetNode generateSetNodeInstance(String name, TupleNode tupleNode) {
        assert(tupleNode == null || !(tupleNode.isSchemaNode())) : "Node must be an instance node: " + tupleNode.getLabel();
        assert(getNode(name) instanceof SetNode) : "Wrong type: " + name;
        SetNode node = new SetNode(name, getOID());
        addInstanceNode(node);
        if (tupleNode != null) {
            tupleNode.addChild(node);
        } else {
            node.setRoot(true);
        }
        return node;
    }
    
    static TupleNode generateTupleNode(String name, SetNode setNode) {
        assert(setNode == null || setNode.isSchemaNode()) : "Node must be a schema node: " + setNode.getLabel();
        TupleNode node = new TupleNode(name);
        addNode(node);
        if (setNode != null) {
            setNode.addChild(node);
        } else {
            node.setRoot(true);
        }
        return node;
    }
    
    static TupleNode generateTupleNodeInstance(String name, SetNode setNode) {
        assert(setNode == null || !(setNode.isSchemaNode())) : "Node must be an instance node: " + setNode.getLabel();
        assert(getNode(name) instanceof TupleNode) : "Wrong type: " + name;
        TupleNode node = new TupleNode(name, getOID());
        addInstanceNode(node);
        if (setNode != null) {
            setNode.addChild(node);
        } else {
            node.setRoot(true);
        }
        return node;
    }
    
    static TupleNode generateNestedTupleNode(String name, TupleNode tupleNode) {
        assert(tupleNode.isSchemaNode()) : "Node must be a schema node: " + tupleNode.getLabel();
        TupleNode node = new TupleNode(name);
        addNode(node);
        tupleNode.addChild(node);
        return node;
    }
    
    static TupleNode generateNestedTupleNodeInstance(String name, TupleNode tupleNode) {
        assert(!(tupleNode.isSchemaNode())) : "Node must be an instance node: " + tupleNode.getLabel();
        assert(getNode(name) instanceof TupleNode) : "Wrong type: " + name;
        TupleNode node = new TupleNode(name, getOID());
        addInstanceNode(node);
        tupleNode.addChild(node);
        return node;
    }
    
    static AttributeNode generateAttribute(String name, TupleNode tupleNode) {
        assert(tupleNode.isSchemaNode()) : "Node must be a schema node: " + tupleNode.getLabel();
        AttributeNode node = new AttributeNode(name);
        addNode(node);
        node.addChild(new LeafNode("string"));
        tupleNode.addChild(node);
        return node;
    }
    
    static AttributeNode generateAttributeInstance(String name, Object value, TupleNode tupleNode) {
        assert(!(tupleNode.isSchemaNode())) : "Node must be an instance node: " + tupleNode.getLabel();
        assert(getNode(name) instanceof AttributeNode) : "Wrong type: " + name;
        AttributeNode node = new AttributeNode(name, getOID());
        addInstanceNode(node);
        LeafNode leaf = new LeafNode("string", value);
        node.addChild(leaf);
        tupleNode.addChild(node);
        return node;
    }
    
    // CONSTRAINTS
    
    static void generateConstraints(DataSource dataSource) {}
    
    static void generateConstraint(String foreignKey, String primaryKey, IDataSourceProxy dataSource) {
        PathExpression primaryKeyPath = generatePath(primaryKey);
        KeyConstraint keyConstraint = getKeyConstraint(dataSource, primaryKeyPath);
        if (keyConstraint == null) {
            keyConstraint = new KeyConstraint(primaryKeyPath, true);
            dataSource.addKeyConstraint(keyConstraint);
        }
        PathExpression foreignKeyPath = generatePath(foreignKey);
        ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint(keyConstraint, foreignKeyPath);
        dataSource.addForeignKeyConstraint(foreignKeyConstraint);
    }
    
    public static void generateConstraint(Object[] foreignKeys, Object[] primaryKeys, IDataSourceProxy dataSource) {
        List<PathExpression> primaryKeyPaths = new ArrayList<PathExpression>();
        for (Object primaryKey : primaryKeys) {
            primaryKeyPaths.add(generatePath((String) primaryKey));
        }
        KeyConstraint keyConstraint = getKeyConstraint(dataSource, primaryKeyPaths);
        if (keyConstraint == null) {
            keyConstraint = new KeyConstraint(primaryKeyPaths, true);
            dataSource.addKeyConstraint(keyConstraint);
        }
        List<PathExpression> foreignKeyPaths = new ArrayList<PathExpression>();
        for (Object foreignKey : foreignKeys) {
            foreignKeyPaths.add(generatePath((String) foreignKey));
        }
        ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint(keyConstraint, foreignKeyPaths);
        dataSource.addForeignKeyConstraint(foreignKeyConstraint);
    }
    
    public static KeyConstraint getKeyConstraint(IDataSourceProxy dataSource, PathExpression pathExpression) {
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            if (keyConstraint.getKeyPaths().size() == 1 &&
                    keyConstraint.getKeyPaths().get(0).equals(pathExpression)) {
                return keyConstraint;
            }
        }
        return null;
    }
    
    public static KeyConstraint getKeyConstraint(IDataSourceProxy dataSource, List<PathExpression> pathExpressions) {
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            if (SpicyEngineUtility.equalLists(keyConstraint.getKeyPaths(), pathExpressions)) {
                return keyConstraint;
            }
        }
        return null;
    }
    
    public static PathExpression generatePath(String nodeLabel) {
        INode node = getNode(nodeLabel);
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
        assert(pathExpression != null) : "Cannot find attribute " + nodeLabel;
        return pathExpression;
    }
    
    public static void addNode(INode node) {
        nodeMap.put(node.getLabel(), node);
    }
    
    public static void addNode(String key, INode node) {
        if (logger.isDebugEnabled()) logger.debug("Adding to map node: " + node.getLabel() + " with key: " + key);
        nodeMap.put(key, node);        
    }
    
    public static INode getNode(String label) {
        INode node = nodeMap.get(label);
        assert(node != null) : "Node not found: " + label;
        return node;
    }
    
    public static void addInstanceNode(INode node) {
        instanceNodeMap.put(node.getValue() + "-" + node.getLabel(), node);
    }
    
    public static INode getInstanceNode(Object value, String label) {
        INode nodeInstance = instanceNodeMap.get(value + "-" + label);
        assert(nodeInstance != null) : "Node not found: " + label;
        return nodeInstance;
    }
}
