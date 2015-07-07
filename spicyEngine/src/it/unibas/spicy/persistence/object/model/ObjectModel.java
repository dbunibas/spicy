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

import java.util.ArrayList;
import java.util.List;

public class ObjectModel {

    private String schemaName;
    private List<String> classNames;
    private List<Object> objects;

    public ObjectModel(List<String> classNames, String schemaName) {
        this.schemaName = schemaName;
        this.classNames = classNames;
    }

    public ObjectModel(String schemaName, List<Object> objects) {
        this.schemaName = schemaName;
        this.objects = objects;
        this.classNames = new ArrayList<String>();
        for (Object object : objects) {
            String className = object.getClass().getName();
            if (classNames.contains(className)) {
                continue;
            }
            classNames.add(className);
        }
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public List<Object> getObjects() {
        return objects;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public boolean add(Object object) {
        return objects.add(object);
    }

    @Override
    public String toString() {
        String result = "";
        for (Object object : objects) {
            result += object + "\n";
        }
        return result;
    }
    
}
