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

import it.unibas.spicy.persistence.xml.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;

class LoadXsdConstraints {
    
    private static Log logger = LogFactory.getLog(LoadXsdConstraints.class);
        
    void checkElementConstraints(XSDSchema xsdSchema, XSElementDeclaration element){
        XSNamedMap constraints = element.getIdentityConstraints();
        for (int i = 0;  i < constraints.getLength();  i++) {
            XSObject constraint = constraints.item(i);
            if (logger.isDebugEnabled()) logger.debug(" *** " + constraint + " TYPE: " +constraint.getType());
            if (constraint instanceof UniqueOrKey) {
                processUniqueOrKeyConstraint(xsdSchema, (UniqueOrKey)constraint);
            }
            if (constraint instanceof KeyRef) {
                processForeignkeyConstraint(xsdSchema, (KeyRef)constraint);
            }
        }
    }
    
    private void processUniqueOrKeyConstraint(XSDSchema xsdSchema, UniqueOrKey constraint){
        StringList fields = constraint.getFieldStrs();
        String constraintName = constraint.getName();
        String fieldStr;
        String[] vectStrs;
        for(int i = 0; i < fields.getLength(); i++){
            fieldStr = fields.item(i);
            vectStrs = getVectorStrings(constraint.getElementName(),constraint.getSelectorStr(),fieldStr);
            if (constraint.getCategory() == UniqueOrKey.IC_KEY) {
                xsdSchema.addKeyXPathStr(constraintName, vectStrs);
            }
            if (constraint.getCategory() == UniqueOrKey.IC_UNIQUE) {
                xsdSchema.addUniqueXPathStr(constraintName, vectStrs);
            }
        }
    }
    
    private void processForeignkeyConstraint(XSDSchema xsdSchema, KeyRef constraint){
        StringList fields = constraint.getFieldStrs();
        // keyref's name: FK|PK
        String constraintName = constraint.getName() + "|" + constraint.getKey().getName();
        String fieldStr;
        String[] vectStrs;
        for(int i = 0; i < fields.getLength(); i++){
            fieldStr = fields.item(i);
            vectStrs = getVectorStrings(constraint.getElementName(),constraint.getSelectorStr(),fieldStr);
            xsdSchema.addForeignKeyXPathStr(constraintName, vectStrs);
        }
    }
    
    private String[] getVectorStrings(String elementStr, String selectorStr, String fieldStr) {
        String[] vectStrs = new String[3];
        vectStrs[0] = elementStr;
        vectStrs[1] = selectorStr;
        vectStrs[2] = fieldStr;
        return vectStrs;
    }
    
}