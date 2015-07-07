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

public class FunctionalDependency implements Cloneable {

    private static Log logger = LogFactory.getLog(FunctionalDependency.class);

    private List<PathExpression> leftPaths = new ArrayList<PathExpression>();
    private List<PathExpression> rightPaths = new ArrayList<PathExpression>();

    public FunctionalDependency(List<PathExpression> leftPaths, List<PathExpression> rightPaths) {
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
    }

    public List<PathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<PathExpression> getRightPaths() {
        return rightPaths;
    }

    public boolean equals(Object object) {
        if (!(object instanceof FunctionalDependency)) {
            return false;
        }
        FunctionalDependency dependency = (FunctionalDependency) object;
        return (this.leftPaths.equals(dependency.leftPaths) && this.rightPaths.equals(dependency.rightPaths));
    }

    public FunctionalDependency clone() {
        FunctionalDependency clone = null;
        try {
            clone = (FunctionalDependency) super.clone();
            clone.leftPaths = new ArrayList<PathExpression>();
            for (PathExpression leftPath : leftPaths) {
                clone.leftPaths.add(leftPath.clone());
            }
            clone.rightPaths = new ArrayList<PathExpression>();
            for (PathExpression rightPath : rightPaths) {
                clone.rightPaths.add(rightPath.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(leftPaths).append(" --> ").append(rightPaths);
        return result.toString();
    }

}
