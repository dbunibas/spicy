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
 
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.paths.VariableCorrespondence;
import java.util.ArrayList;
import java.util.List;

public class TargetEqualities implements Cloneable {

    private List<VariableCorrespondence> leftCorrespondences;
    private List<VariableCorrespondence> rightCorrespondences;

    public TargetEqualities() {}

    public TargetEqualities(List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        assert(leftCorrespondences.size() == rightCorrespondences.size()) : "Equality correspondences have different sizes: " + leftCorrespondences + "\n" + rightCorrespondences;
        this.leftCorrespondences = leftCorrespondences;
        this.rightCorrespondences = rightCorrespondences;
    }

    public List<VariableCorrespondence> getLeftCorrespondences() {
        return leftCorrespondences;
    }

    public List<VariableCorrespondence> getRightCorrespondences() {
        return rightCorrespondences;
    }

    public void setLeftCorrespondences(List<VariableCorrespondence> leftCorrespondences) {
        this.leftCorrespondences = leftCorrespondences;
    }

    public void setRightCorrespondences(List<VariableCorrespondence> rightCorrespondences) {
        this.rightCorrespondences = rightCorrespondences;
    }

    public void addCorrespondences(VariableCorrespondence leftCorrespondence, VariableCorrespondence rightCorrespondence) {
        this.leftCorrespondences.add(leftCorrespondence);
        this.rightCorrespondences.add(rightCorrespondence);
    }

    public int size() {
        return this.leftCorrespondences.size();
    }

    @Override
    public TargetEqualities clone() {
        try {
            TargetEqualities clone = (TargetEqualities) super.clone();
            clone.leftCorrespondences = new ArrayList<VariableCorrespondence>(this.leftCorrespondences);
            clone.rightCorrespondences = new ArrayList<VariableCorrespondence>(this.rightCorrespondences);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append(leftCorrespondences).append("\n");
        result.append(indent).append(rightCorrespondences);
        return result.toString();
    }
}
