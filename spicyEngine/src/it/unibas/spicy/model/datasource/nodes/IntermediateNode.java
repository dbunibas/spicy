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
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import java.util.ArrayList;
import java.util.List;

abstract class IntermediateNode extends AbstractNode {

    private List<INode> listOfChildren = new ArrayList<INode>(1);

    public IntermediateNode(String label) {
        super(label);
    }

    public IntermediateNode(String label, Object value) {
        super(label, value);
    }

    public void addChild(INode node) {
        assert (node != null) : "Child cannot be null";
        this.listOfChildren.add(node);
        node.setFather(this);
    }

    public INode getChild(int pos) {
        assert (0 <= pos && pos < listOfChildren.size()) : "Child does not exist: " + pos;
        return this.listOfChildren.get(pos);
    }

    public INode getChild(String name) {
        INode result = null;
        for (INode child : this.listOfChildren) {
            if (child.getLabel().equals(name)) {
                result = child;
                break;
            }
        }
        return result;
    }

    public INode searchChild(String name) {
        INode result = null;
        for (INode child : this.listOfChildren) {
            if (child.getLabel().equals(name)) {
                result = child;
                break;
            }
        }
        return result;
    }

    public INode getChildStartingWith(String name) {
        INode result = null;
        for (INode child : this.listOfChildren) {
            if (child.getLabel().startsWith(name)) {
                result = child;
                break;
            }
        }
        return result;
    }

    public void removeChild(String name) {
        for (INode child : this.listOfChildren) {
            if (child.getLabel().equals(name)) {
                child.setFather(null);
                this.listOfChildren.remove(child);
                return;
            }
        }
    }

    public List<INode> getChildren() {
        return listOfChildren;
    }

    public INode clone() {
        IntermediateNode clone = (IntermediateNode) super.clone();
        if (this.getValue() != null) {
            clone.setValue(new IntegerOIDGenerator().getNextOID());
        }
        clone.listOfChildren = new ArrayList<INode>();
        for (INode child : listOfChildren) {
            clone.addChild((INode) child.clone());
        }
        return clone;
    }
}
