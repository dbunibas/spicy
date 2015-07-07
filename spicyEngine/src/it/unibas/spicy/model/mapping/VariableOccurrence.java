/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Donatello Santoro - donatello.santoro@gmail.com

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
import java.util.Collections;
import java.util.List;

public class VariableOccurrence {

    private VariablePathExpression path;
    private boolean added;

    public static List<VariablePathExpression> getOriginalPaths(List<VariableOccurrence> occurrences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableOccurrence occurrence : occurrences) {
            if (!occurrence.isAdded()) {
                result.add(occurrence.getPath());
            }
        }
        return result;
    }

    public VariableOccurrence(VariablePathExpression path, boolean added) {
        this.path = path;
        this.added = added;
    }

    public VariableOccurrence(VariableOccurrence occurrence, boolean added) {
        this.path = occurrence.path;
        this.added = added;
    }

    public boolean isAdded() {
        return added;
    }

    public VariablePathExpression getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public String toString() {
        return this.path + "(" + (added ? "added" : "") + ")";
    }
}
