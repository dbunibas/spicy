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

import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;

public class ForeignKeyConstraint {

    private KeyConstraint keyConstraint;
    private List<PathExpression> foreignKeyPaths;
        
    public ForeignKeyConstraint(KeyConstraint keyConstraint, List<PathExpression> foreignKeyPaths) {
        if (keyConstraint.getKeyPaths().size() != foreignKeyPaths.size()) {
            throw new IllegalArgumentException("Foreign key number does not match primary key number: " + keyConstraint + " - " + foreignKeyPaths);
        }
        this.keyConstraint = keyConstraint;
        this.foreignKeyPaths = foreignKeyPaths;
    }
    
    public ForeignKeyConstraint(KeyConstraint keyConstraint, PathExpression foreignKeyPath) {
        if (keyConstraint.getKeyPaths().size() != 1) {
            throw new IllegalArgumentException("Foreign key number does not match primary key number: " + keyConstraint + " - " + foreignKeyPaths);
        }
        this.keyConstraint = keyConstraint;
        this.foreignKeyPaths = new ArrayList<PathExpression>();
        this.foreignKeyPaths.add(foreignKeyPath);
    }

    public List<PathExpression> getForeignKeyPaths() {
        return foreignKeyPaths;
    }
    
    public KeyConstraint getKeyConstraint() {
        return keyConstraint;
    }

    public String toString() {
        return this.foreignKeyPaths + " references " + this.keyConstraint.getKeyPaths();
    }

}
