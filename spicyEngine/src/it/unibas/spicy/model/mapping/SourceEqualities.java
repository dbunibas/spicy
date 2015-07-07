/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceEqualities implements Cloneable {

    private List<VariablePathExpression> leftPaths = new ArrayList<VariablePathExpression>();
    private List<VariablePathExpression> rightPaths = new ArrayList<VariablePathExpression>();

    public SourceEqualities() {}

    public SourceEqualities(List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        assert(leftPaths.size() == rightPaths.size()) : "Equality paths have different sizes: " + leftPaths + "\n" + rightPaths;
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
    }

    public void addLeftPath(VariablePathExpression leftPath) {
        this.leftPaths.add(leftPath);
    }

    public void addRightPath(VariablePathExpression rightPath) {
        this.rightPaths.add(rightPath);
    }

    public void addPaths(VariablePathExpression leftPath, VariablePathExpression rightPath) {
        this.leftPaths.add(leftPath);
        this.rightPaths.add(rightPath);
    }

    public List<VariablePathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<VariablePathExpression> getRightPaths() {
        return rightPaths;
    }

    public void setLeftPaths(List<VariablePathExpression> leftPaths) {
        this.leftPaths = leftPaths;
    }

    public void setRightPaths(List<VariablePathExpression> rightPaths) {
        this.rightPaths = rightPaths;
    }

    public int size() {
        return this.leftPaths.size();
    }

    @Override
    public SourceEqualities clone()  {
        try {
            SourceEqualities clone = (SourceEqualities) super.clone();
            clone.leftPaths = new ArrayList<VariablePathExpression>(leftPaths);
            clone.rightPaths = new ArrayList<VariablePathExpression>(rightPaths);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        if (leftPaths.size() == rightPaths.size()) {
            result.append(indent).append("[");
            for (int i = 0; i < leftPaths.size(); i++) {
                result.append("  ").append(leftPaths.get(i).toString()).append("=").append(rightPaths.get(i).toString()).append("  ");
            }
            result.append("]");
        } else {
            result.append(indent).append(leftPaths).append("\n");
            result.append(indent).append(rightPaths);
        }
        return result.toString();
    }
}
