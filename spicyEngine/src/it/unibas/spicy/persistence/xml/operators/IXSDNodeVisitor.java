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
import it.unibas.spicy.persistence.xml.model.PCDATA;
import it.unibas.spicy.persistence.xml.model.SimpleType;
import it.unibas.spicy.persistence.xml.model.TypeCompositor;

public interface IXSDNodeVisitor {
        
    void visitSimpleType(SimpleType node);
    
    void visitElementDeclaration(ElementDeclaration node);
    
    void visitTypeCompositor(TypeCompositor node);
    
    void visitAttributeDeclaration(AttributeDeclaration node);
    
    void visitPCDATA(PCDATA node);
    
    Object getResult();
    
}
