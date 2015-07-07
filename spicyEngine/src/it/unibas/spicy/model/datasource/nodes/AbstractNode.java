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
 
package it.unibas.spicy.model.datasource.nodes;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.NodeToCompactString;
import it.unibas.spicy.model.datasource.operators.NodeToSaveString;
import it.unibas.spicy.model.datasource.operators.NodeToString;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class AbstractNode implements INode {

    private static Log logger = LogFactory.getLog(AbstractNode.class);

    private String label;
    private Object value;
    private boolean root;
    private boolean virtual;
    private boolean required;
    private boolean notNull;
    private boolean excluded;
    private INode father;
    private NodeToString nodeToString = new NodeToString();
    private NodeToCompactString nodeToCompactString = new NodeToCompactString();
    private NodeToSaveString nodeToSaveString = new NodeToSaveString();
    private Map<String, Object> annotations;

    public AbstractNode(String label) {
        this.label = label;
    }

    public AbstractNode(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isSchemaNode() {
        return this.value == null;
    }

    public INode getFather() {
        return this.father;
    }

    public void setFather(INode father) {
        this.father = father;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public void addAnnotation(String name, Object value) {
        if (this.annotations == null) {
            this.annotations =  new HashMap<String, Object>();
        }
        this.annotations.put(name, value);
    }

    public Object getAnnotation(String name) {
        if (this.annotations == null) {
            return null;
        }
        return this.annotations.get(name);
    }

    public Object removeAnnotation(String name) {
        if (this.annotations == null) {
            return null;
        }
        return this.annotations.remove(name);
    }

    public Map<String, Object> getAnnotations() {
        return this.annotations;
    }

    public String toString() {
        return nodeToString.toString(this, false, false);
    }

    public String toShortString() {
        return nodeToCompactString.toString(this);
    }

    public String toSaveString() {
        return nodeToSaveString.toString(this);
    }

    public String toStringWithOids() {
        return nodeToString.toString(this, true, false);
    }

    public String toStringWithAnnotations() {
        return nodeToString.toString(this, false, true);
    }

    @SuppressWarnings("unchecked")
    public INode clone() {
        AbstractNode clone = null;
        try {
            clone = (AbstractNode) super.clone();
            clone.father = null;
            if (this.annotations != null) {
                clone.annotations = (Map<String, Object>) ((HashMap<String, Object>)this.annotations).clone();
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }
}
