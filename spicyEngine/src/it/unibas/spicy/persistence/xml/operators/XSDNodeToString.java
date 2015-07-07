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
 
package it.unibas.spicy.persistence.xml.operators;

import it.unibas.spicy.persistence.xml.model.AttributeDeclaration;
import it.unibas.spicy.persistence.xml.model.ElementDeclaration;
import it.unibas.spicy.persistence.xml.model.IXSDNode;
import it.unibas.spicy.persistence.xml.model.PCDATA;
import it.unibas.spicy.persistence.xml.model.SimpleType;
import it.unibas.spicy.persistence.xml.model.TypeCompositor;
import java.util.List;

public class XSDNodeToString {
    
    public String toString(IXSDNode node) {
        XSDNodeToStringVisitor printVisitor = new XSDNodeToStringVisitor();
        node.accept(printVisitor);
        return (String)printVisitor.getResult();
    }
}

class XSDNodeToStringVisitor implements IXSDNodeVisitor {
    
    private int indentLevel = 0;
    private String treeDescription = "";
    
    public String getResult() {
        return treeDescription;
    }
    
    public void visitSimpleType(SimpleType node) {
        visitGenericNode(node);
    }
    
    public void visitElementDeclaration(ElementDeclaration node) {
        visitGenericNode(node);
    }
    
    public void visitTypeCompositor(TypeCompositor node) {
        visitGenericNode(node);
    }
    
    public void visitAttributeDeclaration(AttributeDeclaration node) {
        visitGenericNode(node);
    }
    
    public void visitPCDATA(PCDATA node) {
        visitGenericNode(node);
    }

    private void visitGenericNode(IXSDNode node) {
        treeDescription += this.indentString();
        treeDescription += node.getDescription();
        treeDescription += "\n";
        List<IXSDNode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (IXSDNode child : listOfChildren) {
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

