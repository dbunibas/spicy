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
 
package it.unibas.spicy.model.paths.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.List;

public class CheckPathContainment {
    
    public boolean isPrefixOf(PathExpression firstPath, PathExpression secondPath) {
        if (firstPath.getPathSteps().size() > secondPath.getPathSteps().size()) {
            return false;
        }
        for (int i = 0; i < firstPath.getPathSteps().size(); i++) {
            if (!firstPath.getPathSteps().get(i).equals(secondPath.getPathSteps().get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean correctExclusion(PathExpression exclusionPath, List<PathExpression> inclusions) {
        for (PathExpression inclusionPath : inclusions) {
            if (isPrefixOf(inclusionPath, exclusionPath)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNode(PathExpression pathExpression, INode node, INode root) {
        for (INode pathStep : pathExpression.getPathNodes(root)) {
            if (pathStep.equals(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNode(List<PathExpression> pathList, INode node, INode root) {
        for (PathExpression pathExpression : pathList) {
            if (containsNode(pathExpression, node, root)) {
                return true;
            }
        }
        return false;
    }

}
