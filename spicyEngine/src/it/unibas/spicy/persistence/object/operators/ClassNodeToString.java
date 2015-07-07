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

import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.PersistentProperty;
import it.unibas.spicy.persistence.object.model.ReferenceProperty;
import it.unibas.spicy.persistence.xml.operators.*;
import java.util.List;

public class ClassNodeToString {

    public String toString(IClassNode node) {
        ClassNodeToStringVisitor printVisitor = new ClassNodeToStringVisitor();
        node.accept(printVisitor);
        return (String) printVisitor.getResult();
    }
}

class ClassNodeToStringVisitor implements IClassNodeVisitor {

    private int indentLevel = 0;
    private String treeDescription = "";

    public String getResult() {
        return treeDescription;
    }

    public void visitClassNode(IClassNode node) {
        visitGenericNode(node);
    }

    private void visitGenericNode(IClassNode node) {
        treeDescription += this.indentString();
        treeDescription += node.getName();
        treeDescription += "\n";
        if (node.getId() != null) {
            this.indentLevel++;
            treeDescription += indentString();
            treeDescription += node.getId() + "\n";
            this.indentLevel--;
        }
        List<PersistentProperty> properties = node.getPersistentProperties();
        if (properties != null) {
            this.indentLevel++;
            for (PersistentProperty property : properties) {
                treeDescription += indentString();
                treeDescription += property.toString() + "\n";
            }
            this.indentLevel--;
        }
        List<ReferenceProperty> references = node.getReferenceProperties();
        if (references != null) {
            this.indentLevel++;
            for (ReferenceProperty reference : references) {
                treeDescription += indentString();
                treeDescription += reference.toString() + "\n";
            }
            this.indentLevel--;
        }

        List<IClassNode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (IClassNode child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private String indentString() {
        String result = "";
        for (int i = 0; i < this.indentLevel; i++) {
            result += "    ";
        }
        return result;
    }
}

