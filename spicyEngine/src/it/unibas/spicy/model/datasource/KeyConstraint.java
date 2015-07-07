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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyConstraint implements Cloneable {

    private static Log logger = LogFactory.getLog(KeyConstraint.class);

    private List<PathExpression> keyPaths;
    private boolean primaryKey;
    
    public KeyConstraint(List<PathExpression> keyPaths) {
        this(keyPaths, false);
    }
    
    public KeyConstraint(PathExpression keyPath) {
        this(keyPath, false);
    }

    public KeyConstraint(List<PathExpression> keyPaths, boolean primaryKey) {
        if (keyPaths.size() < 1) {
            throw new IllegalArgumentException("Constraints cannot be empty: " + keyPaths);
        }
        this.keyPaths = keyPaths;
        this.primaryKey = primaryKey;
    }
    
    public KeyConstraint(PathExpression keyPath, boolean primaryKey) {
        this.keyPaths = new ArrayList<PathExpression>();
        this.keyPaths.add(keyPath);
        this.primaryKey = primaryKey;
    }

    public List<PathExpression> getKeyPaths() {
        return keyPaths;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public KeyConstraint clone() {
        KeyConstraint clone = null;
        try {
            clone = (KeyConstraint) super.clone();
            clone.keyPaths = new ArrayList<PathExpression>();
            for (PathExpression keyPath : keyPaths) {
                clone.keyPaths.add(keyPath.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    public String toString() {
        String result = "";
        if (this.primaryKey) {
            result += "Primary key: ";
        } else {
            result += "Key: ";
        }
        result += this.keyPaths;        
        return result;
    }
}
