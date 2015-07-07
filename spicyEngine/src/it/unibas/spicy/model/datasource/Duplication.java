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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Duplication implements Cloneable {

    private static Log logger = LogFactory.getLog(Duplication.class);

    private PathExpression originalPath;
    private PathExpression clonePath;

    public Duplication(PathExpression originalPath, PathExpression clonePath) {
        this.originalPath = originalPath;
        this.clonePath = clonePath;
    }

    public PathExpression getClonePath() {
        return clonePath;
    }

    public PathExpression getOriginalPath() {
        return originalPath;
    }

    public Duplication clone() {
        Duplication clone = null;
        try {
            clone = (Duplication) super.clone();
            clone.originalPath = this.originalPath.clone();
            clone.clonePath = this.clonePath.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Duplicate ").append(originalPath).append(" into ").append(clonePath);
        return result.toString();
    }

}