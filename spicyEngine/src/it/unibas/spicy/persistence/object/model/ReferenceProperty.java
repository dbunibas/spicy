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
import it.unibas.spicy.persistence.object.Constants;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReferenceProperty {
    
    private static Map<String, Integer> labelCache = new HashMap<String, Integer>();

    private String name;
    private IClassNode source;
    private IClassNode target;
    private Field field;
    private String cardinality;
    private ReferenceProperty inverse;
    private boolean translated;
    private INode schemaNode;

    public ReferenceProperty(String name, Field field, IClassNode source, IClassNode target, String cardinality) {
        this.name = name;
        this.field = field;
        this.source = source;
        this.target = target;
        this.cardinality = cardinality;
    }

    public void setInverse(ReferenceProperty inverse) {
        this.inverse = inverse;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public ReferenceProperty getInverse() {
        return inverse;
    }

    public boolean isTranslated() {
        return translated;
    }

    public IClassNode getSource() {
        return source;
    }

    public IClassNode getTarget() {
        return target;
    }

    public String getCardinality() {
        return cardinality;
    }

    public INode getSchemaNode() {
        return schemaNode;
    }

    public void setSchemaNode(INode schemaNode) {
        this.schemaNode = schemaNode;
    }

    public String getAssociationTupleNodeName() {
        String sourceName = source.getName();
        sourceName = ClassUtility.extractSimpleClassName(sourceName);
        sourceName += ClassUtility.convertFirstCapitalLetter(field.getName());
        String targetName = target.getName();
        targetName = ClassUtility.extractSimpleClassName(targetName);
        if (inverse != null) {
            targetName += ClassUtility.convertFirstCapitalLetter(inverse.getField().getName());
        }
        String label = sourceName + targetName;
        Integer lastCounter = labelCache.get(label);
        if (lastCounter == null) {
            lastCounter = 0;
        } else {
            lastCounter++;
        }
        labelCache.put(label, lastCounter);
        if (lastCounter == 0) {
            return label;
        }
        return label + lastCounter;
    }
    
    public String getAssociationSetNodeName() {
        return getAssociationTupleNodeName() + Constants.SET;
    }

    
    public String toString() {
        return name + " - Source: " + source.getName() + " - Target: " + target.getName() + " - cardinality: " + cardinality;
    }

    public String toShortString() {
        return name;
    }
}
