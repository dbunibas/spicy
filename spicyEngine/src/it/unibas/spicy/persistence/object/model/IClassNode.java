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
import it.unibas.spicy.persistence.object.operators.IClassNodeVisitor;
import it.unibas.spicy.persistence.xml.model.*;
import java.util.List;

public interface IClassNode {

    String getName();

    void setName(String name);

    Class getCorrespondingClass();

    void setCorrespondingClass(Class correspondingClass);

    IClassNode getFather();

    void setFather(IClassNode father);

    PersistentProperty getId();

    void setId(PersistentProperty id);

    void addPersistentProperty(PersistentProperty persistentProperty);

    public List<PersistentProperty> getPersistentProperties();

    List<ReferenceProperty> getReferenceProperties();

    void addReferenceProperty(ReferenceProperty referenceProperty);

    ReferenceProperty findReferencePropertyByName(String name);

    List<IClassNode> getChildren();

    void addChild(IClassNode child);

    boolean isRoot();

    void setRoot(boolean isRoot);

    public INode getSchemaNode();

    public void setSchemaNode(INode schemaNode);

    void accept(IClassNodeVisitor visitor);

    @Override
    String toString();
}
