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
import it.unibas.spicy.model.datasource.KeyConstraint;

public class PersistentProperty {

    private String name;
    private String type;
    private INode schemaNode;
    private KeyConstraint keyConstraint;

    public PersistentProperty(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public INode getSchemaNode() {
        return schemaNode;
    }

    public void setSchemaNode(INode schemaNode) {
        this.schemaNode = schemaNode;
    }

    public KeyConstraint getKeyConstraint() {
        return keyConstraint;
    }

    public void setKeyConstraint(KeyConstraint keyConstraint) {
        this.keyConstraint = keyConstraint;
    }

    public String toString() {
        return name + ": (" + type + ")";
    }
}
