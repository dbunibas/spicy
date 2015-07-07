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
 
package it.unibas.spicy.model.datasource.nodes;

import it.unibas.spicy.model.paths.PathExpression;

public class SetCloneNode extends SetNode {

    private static final char DUPLICATION_CHAR = '_';

    public static String getCloneLabel(int counter, PathExpression originalNodePath) {
        return originalNodePath.getLastStep() + DUPLICATION_CHAR + counter + DUPLICATION_CHAR;
    }

    public static String removeCloneLabel(String nodeLabel) {
        if (nodeLabel.matches(".+_\\d+_\\z")) {
            String newLabel = nodeLabel.substring(0, nodeLabel.lastIndexOf(DUPLICATION_CHAR));
            return newLabel.substring(0, newLabel.lastIndexOf(DUPLICATION_CHAR));
        }
        return nodeLabel;
    }
    
    private PathExpression originalNodePath;

    public SetCloneNode(int counter, PathExpression originalNodePath) {
        super(getCloneLabel(counter, originalNodePath));
        this.originalNodePath = originalNodePath;
    }

    public SetCloneNode(String label, Object value) {
        super(label, value);
    }

    public PathExpression getOriginalNodePath() {
        return originalNodePath;
    }
}
