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
 
package it.unibas.spicy.persistence.xml.model;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.persistence.xml.operators.XSDNodeToString;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Particle implements IXSDNode {
    
    public static final int UNBOUNDED = Integer.MAX_VALUE;
    
    private static Log logger = LogFactory.getLog(Particle.class);
    
    private String label;
    private int minCardinality;
    private int maxCardinality;
    private boolean nullable;
    private boolean nested;
    private boolean mixedContent;
    
    private List<IXSDNode> children = new ArrayList<IXSDNode>();
    private IXSDNode father;
    private INode correspondingSchemaNode;
    private boolean visited;
    
    public Particle(String label) {
        this.label = label;
    }
    
    public void addChild(IXSDNode child) {
        this.children.add(child);
        child.setFather(this);
    }
    
    public List<IXSDNode> getChildren() {
        return children;
    }
    
    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        String result = "";
        result += this.label + " : " + this.getClass().getSimpleName();
        result += "(minC=" + this.minCardinality + ", maxC=" + this.maxCardinality + ", nullable=" + nullable + ", mixed=" + this.mixedContent + 
                ", nested=" + this.nested + ") - iNode: " + correspondingSchemaNode + " - visited: " + visited;
        return result;
    }
    
    public int getMinCardinality() {
        return minCardinality;
    }
    
    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }
    
    public int getMaxCardinality() {
        return maxCardinality;
    }
    
    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }
    
    public boolean isNested() {
        return nested;
    }
    
    public void setNested(boolean nested) {
        this.nested = nested;
    }
    
    public IXSDNode getFather() {
        return father;
    }
    
    public void setFather(IXSDNode father) {
        this.father = father;
    }
    
    public boolean isMixedContent() {
        return mixedContent;
    }
    
    public void setMixedContent(boolean mixedContent) {
        this.mixedContent = mixedContent;
    }
    
    public INode getCorrespondingSchemaNode() {
        return correspondingSchemaNode;
    }

    public void setCorrespondingSchemaNode(INode correspondingSchemaNode) {
        this.correspondingSchemaNode = correspondingSchemaNode;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String toString() {
        return new XSDNodeToString().toString(this);
    }
    
    public Particle clone() {
        Particle clone = null;
        try {
            clone = (Particle)super.clone();
            clone.father = null;
            clone.children = new ArrayList<IXSDNode>();
            for (IXSDNode child : children) {
                clone.addChild((IXSDNode) child.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }
    
}
