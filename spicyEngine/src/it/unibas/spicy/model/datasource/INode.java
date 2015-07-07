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
 
package it.unibas.spicy.model.datasource;

import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import java.util.List;
import java.util.Map;

public interface INode extends Cloneable {
    
    List<INode> getChildren();
    
    INode getFather();
    
    void setFather(INode father);
    
    void addChild(INode node);
    
    INode getChild(int pos);
    
    INode getChild(String name);
    
    INode searchChild(String name);

    INode getChildStartingWith(String name);

    void removeChild(String name);
    
    String getLabel();
    
    Object getValue();

    boolean isRoot();

    boolean isVirtual();

    boolean isSchemaNode();
    
    boolean isRequired();
    
    boolean isNotNull();
    
    boolean isExcluded();

    void setRoot(boolean root);

    void setVirtual(boolean virtual);
   
    void setRequired(boolean required);
                    
    void setNotNull(boolean notNull);
    
    void setExcluded(boolean excluded);

    void setLabel(String label);

    void setValue(Object value);

    void addAnnotation(String name, Object value);

    Object getAnnotation(String name);
    
    Object removeAnnotation(String name);

    Map<String, Object> getAnnotations();

    void accept(INodeVisitor visitor);
    
    INode clone();

    String toString();

    String toShortString();

    String toSaveString();

    String toStringWithOids();

    String toStringWithAnnotations();
}
