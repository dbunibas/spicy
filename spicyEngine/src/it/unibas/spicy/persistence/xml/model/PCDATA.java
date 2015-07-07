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
import it.unibas.spicy.persistence.xml.operators.IXSDNodeVisitor;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PCDATA implements IXSDNode {
    
    private static Log logger = LogFactory.getLog(PCDATA.class);
    
    private IXSDNode father;
    private INode correspondingSchemaNode;
    private boolean visited;
    
    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitPCDATA(this);
    }

    @SuppressWarnings("unchecked")
    public List<IXSDNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

    public void addChild(IXSDNode child) {
        throw new UnsupportedOperationException("PCDATA cannot have children");
    }

    public String getLabel() {
        return "PCDATA";
    }

    public String getDescription() {
        return "PCDATA";
    }

    public boolean isNested() {
        return false;
    }

    public void setNested(boolean nested) {
        throw new UnsupportedOperationException("PCDATA cannot be nested");
    }

    public IXSDNode getFather() {
        return father;
    }

    public void setFather(IXSDNode father) {
        this.father = father;
    }

    public int getMinCardinality() {
        return 0;
    }

    public void setMinCardinality(int minCardinality) {
        throw new UnsupportedOperationException("PCDATA do not have cardinality");
    }

    public int getMaxCardinality() {
        return 0;
    }

    public void setMaxCardinality(int maxCardinality) {
        throw new UnsupportedOperationException("PCDATA do not have cardinality");
    }

    public boolean isNullable() {
        return false;
    }

    public void setNullable(boolean nullable) {
        throw new UnsupportedOperationException("PCDATA cannot be nullable");
    }

    public boolean isMixedContent() {
        return false;
    }

    public void setMixedContent(boolean mixedContent) {
        throw new UnsupportedOperationException("PCDATA cannot have mixed content");
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

    public PCDATA clone() {
        try {
            return (PCDATA)super.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }
}

