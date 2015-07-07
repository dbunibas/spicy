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
 
package it.unibas.spicy.persistence.object.model;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.operators.ClassNodeToString;
import it.unibas.spicy.persistence.object.operators.IClassNodeVisitor;
import java.util.ArrayList;
import java.util.List;

public class ClassNode implements IClassNode {

    private String name;
    private IClassNode father;
    private Class correspondingClass;
    private boolean isRoot;
    private List<IClassNode> children = new ArrayList<IClassNode>();
    private PersistentProperty id;
    private List<PersistentProperty> properties = new ArrayList<PersistentProperty>();
    private List<ReferenceProperty> references = new ArrayList<ReferenceProperty>();
    private INode schemaNode;

    public ClassNode(String name) {
        this.name = ClassUtility.encodeClassName(name);
        this.isRoot = false;
    }

    public ClassNode(String name, boolean isRoot) {
        this.name = ClassUtility.encodeClassName(name);
        this.isRoot = isRoot;
    }

    public void accept(IClassNodeVisitor visitor) {
        visitor.visitClassNode(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = ClassUtility.encodeClassName(name);
    }

    public Class getCorrespondingClass() {
        return correspondingClass;
    }

    public void setCorrespondingClass(Class correspondingClass) {
        this.correspondingClass = correspondingClass;
    }

    public IClassNode getFather() {
        return father;
    }

    public void setFather(IClassNode father) {
        this.father = father;
    }

    public List<IClassNode> getChildren() {
        return children;
    }

    public void addChild(IClassNode child) {
        if (children.contains(child)) {
            return;
        }
        children.add(child);
        child.setFather(this);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public PersistentProperty getId() {
        return id;
    }

    public void setId(PersistentProperty id) {
        this.id = id;
    }

    public List<PersistentProperty> getPersistentProperties() {
        return properties;
    }

    public void addPersistentProperty(PersistentProperty persistentProperty) {
        properties.add(persistentProperty);
    }

    public List<ReferenceProperty> getReferenceProperties() {
        return references;
    }

    public void addReferenceProperty(ReferenceProperty referenceProperty) {
        references.add(referenceProperty);
    }

    public ReferenceProperty findReferencePropertyByName(String name) {
        for (ReferenceProperty reference : references) {
            if (reference.getName().equals(name)) {
                return reference;
            }
        }
        return null;
    }

    public INode getSchemaNode() {
        return schemaNode;
    }

    public void setSchemaNode(INode schemaNode) {
        this.schemaNode = schemaNode;
    }

    public String toString() {
        return new ClassNodeToString().toString(this);
    }
}
