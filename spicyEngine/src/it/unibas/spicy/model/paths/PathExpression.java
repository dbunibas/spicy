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
 
package it.unibas.spicy.model.paths;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PathExpression implements Comparable<PathExpression>, Cloneable {

    private static Log logger = LogFactory.getLog(PathExpression.class);
    
    protected List<String> pathSteps = new ArrayList<String>();

    public PathExpression(List<String> pathSteps) {
        this.pathSteps = pathSteps;
    }

    public PathExpression(PathExpression pathExpression) {
        this.pathSteps.addAll(pathExpression.getPathSteps());
    }

    public VariablePathExpression getRelativePath(IDataSourceProxy dataSource) {
        return new GeneratePathExpression().generateRelativePath(this, dataSource);
    }

    public List<String> getPathSteps() {
        return this.pathSteps;
    }

    public String getFirstStep() {
        if (pathSteps.isEmpty()) {
            return null;
        }
        return pathSteps.get(0);
    }

    public String getLastStep() {
        if (pathSteps.isEmpty()) {
            return null;
        }
        return pathSteps.get(pathSteps.size() - 1);
    }

    public List<INode> getPathNodes(INode root) {
        return new GeneratePathExpression().generatePathStepNodes(pathSteps, root);
    }

    public List<PathExpression> getAttributePaths(INode root) {
        return new GeneratePathExpression().generateFirstLevelAttributeAbsolutePaths(this, root);
    }

    public int getLevel() {
        return this.pathSteps.size();
    }

    public INode getLastNode(INode root) {
        if (this.pathSteps.isEmpty()) {
            return null;
        }
        List<INode> pathNodes = new GeneratePathExpression().generatePathStepNodes(pathSteps, root);
        return pathNodes.get(pathNodes.size() - 1);
    }

    public int compareTo(PathExpression pathExpression) {
        return this.toString().compareTo(pathExpression.toString());
    }

    public PathExpression clone() {
        PathExpression clone = null;
        try {
            clone = (PathExpression) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
        clone.pathSteps = new ArrayList<String>(this.pathSteps);
        return clone;
    }

    public boolean equals(Object object) {
        if (logger.isTraceEnabled()) logger.trace("Comparing: " + this + " to " + object);
        if (!(object instanceof PathExpression)) {
            return false;
        }
        PathExpression otherPathExpression = (PathExpression) object;
        if (this.getPathSteps().size() != otherPathExpression.getPathSteps().size()) {
            return false;
        }
        for (int i = 0; i < this.getPathSteps().size(); i++) {
            if (!this.getPathSteps().get(i).equals(otherPathExpression.getPathSteps().get(i))) {
                if (logger.isTraceEnabled()) logger.trace("Found a different step: " + this.pathSteps.get(i) + " - Returning false");
                return false;
            }
        }
        if (logger.isTraceEnabled()) logger.trace("Paths are equal");
        return true;
    }

    public boolean equalsUpToClones(PathExpression otherPath) {
        return SpicyEngineUtility.removeClonesFromAbsolutePath(this).equals(SpicyEngineUtility.removeClonesFromAbsolutePath(otherPath));
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        String result = "";
        for (String nodeLabel : this.pathSteps) {
            if (!result.equals("")) {
                result += ".";
            }
            result += nodeLabel;
        }
        return result;
    }

}
