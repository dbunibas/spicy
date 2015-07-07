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
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import java.util.List;

public class LeafNode extends AbstractNode {
    
    public LeafNode(String type) {
        super(type);
    }
    
    public LeafNode(String label, Object value) {
        super(label, value);
    }
    
    public void addChild(INode node) {
        throw new UnsupportedOperationException("It is not possible to add children to leaf nodes");
    }
    
    public INode getChild(int pos) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public INode getChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }
    
    public INode searchChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public INode getChildStartingWith(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public void removeChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public void accept(INodeVisitor visitor) {
        visitor.visitLeafNode(this);
    }

    public List<INode> getChildren() {
        return null;
    }

}
