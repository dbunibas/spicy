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

public class JoinCondition implements Cloneable {

    private static Log logger = LogFactory.getLog(JoinCondition.class);

    private List<PathExpression> fromPaths = new ArrayList<PathExpression>();
    private List<PathExpression> toPaths = new ArrayList<PathExpression>();
    private boolean monodirectional;
    private boolean mandatory;

    public JoinCondition(List<PathExpression> fromPaths, List<PathExpression> toPaths, boolean foreignKey) {
        this(fromPaths, toPaths, foreignKey, false);
    }

    public JoinCondition(List<PathExpression> fromPaths, List<PathExpression> toPaths, boolean monodirectional, boolean mandatory) {
        assert (fromPaths.size() >= 1 && toPaths.size() >= 1) : "Path lists cannot be empty: " + fromPaths + " - " + toPaths;
        this.fromPaths = fromPaths;
        this.toPaths = toPaths;
        this.monodirectional = monodirectional;
        this.mandatory = mandatory;
    }

    public JoinCondition(List<PathExpression> fromPaths, List<PathExpression> toPaths) {
        this(fromPaths, toPaths, false);
    }

    public JoinCondition(PathExpression fromPath, PathExpression toPath) {
        this(generateList(fromPath), generateList(toPath), false);
    }

    public List<PathExpression> getFromPaths() {
        return fromPaths;
    }

    public List<PathExpression> getToPaths() {
        return toPaths;
    }

    public void addPaths(PathExpression fromPath, PathExpression toPath) {
        this.fromPaths.add(fromPath);
        this.toPaths.add(toPath);
    }

    public boolean isMonodirectional() {
        return monodirectional;
    }

    public void setMonodirectional(boolean monodirectional) {
        this.monodirectional = monodirectional;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean equals(Object object) {
        if (!(object instanceof JoinCondition)) {
            return false;
        }
        JoinCondition condition = (JoinCondition) object;
        return (this.fromPaths.equals(condition.fromPaths) && this.toPaths.equals(condition.toPaths));
    }

    public boolean isInverse(JoinCondition condition) {
        return (this.fromPaths.equals(condition.toPaths) && this.toPaths.equals(condition.fromPaths));
    }

    public JoinCondition clone() {
        JoinCondition clone = null;
        try {
            clone = (JoinCondition) super.clone();
            clone.fromPaths = new ArrayList<PathExpression>();
            for (PathExpression fromPath : fromPaths) {
                clone.fromPaths.add(fromPath.clone());
            }
            clone.toPaths = new ArrayList<PathExpression>();
            for (PathExpression toPath : toPaths) {
                clone.toPaths.add(toPath.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(fromPaths).append(" JOIN ").append(toPaths);
        result.append(" (bidirectional = ").append(!monodirectional).append(")");
        result.append(" (mandatory = ").append(mandatory).append(")");
        return result.toString();
    }

    private static List<PathExpression> generateList(PathExpression path) {
        List<PathExpression> list = new ArrayList<PathExpression>();
        list.add(path);
        return list;
    }
}
