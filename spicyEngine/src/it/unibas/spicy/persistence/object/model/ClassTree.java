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

import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassTree {

    private IClassNode root;
    private Map<String, IClassNode> elements = new HashMap<String, IClassNode>();
    private List<ReferenceProperty> references = new ArrayList<ReferenceProperty>();

    public ClassTree() {
        root = new ClassNode(Constants.OBJECT_CLASS, true);
        root.setCorrespondingClass(Object.class);
        elements.put(Constants.OBJECT_CLASS, root);
    }

    public IClassNode getRoot() {
        return root;
    }

    public Map<String, IClassNode> getElements() {
        return elements;
    }

    public IClassNode put(String className, IClassNode value) {
        return elements.put(ClassUtility.encodeClassName(className), value);
    }

    public IClassNode get(String className) {
        return elements.get(ClassUtility.encodeClassName(className));
    }

    
    
    public List<ReferenceProperty> getReferences() {
        return references;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
