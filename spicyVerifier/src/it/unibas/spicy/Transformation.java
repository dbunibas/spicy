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
 
package it.unibas.spicy;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.FORule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transformation { //implements Comparable<Transformation> {
    
    private int id;
    private List<FORule> mappings = new ArrayList<FORule>();
    private List<INode> translatedInstances;
    private Map<String, Object> annotations = new HashMap<String, Object>();

    public Transformation(List<FORule> mappings, int id) {
        this.mappings = mappings;
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public List<FORule> getMappings() {
        return mappings;
    }

    public List<INode> getTranslatedInstances() {
        return translatedInstances;
    }

    public void setTranslatedInstances(List<INode> translatedInstances) {
        this.translatedInstances = translatedInstances;
    }

//    public int getAliasesInMappings() {
//        int aliasCount = 0;
//        for (TGD mapping : mappings) {
//            aliasCount += mapping.getAliasesInCorrespondences();
//        }
//        return aliasCount;
//    }

    public void addAnnotation(String key, Object value) {
        this.annotations.put(key, value);
    }
    
    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }

//    public int compareTo(Transformation otherTransformation) {
//        return (this.getAliasesInMappings() - otherTransformation.getAliasesInMappings());
//    }
        
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("=============================== TRANSFORMATION n. ").append(id).append(" ================================\n");
        for (int i = 0; i < mappings.size(); i++) {
            result.append(mappings.get(i));
            if (i != mappings.size() - 1) {
                result.append("\nDEEP UNION\n\n");
            }
        }
        result.append("=========================================================================================\n");
        return result.toString();
    }
        
}
