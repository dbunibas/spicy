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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VariableProvenanceCondition implements Cloneable {

    private static Log logger = LogFactory.getLog(VariableProvenanceCondition.class);

    private PathExpression setPath;
    private String provenance;

    public VariableProvenanceCondition(PathExpression setPath, String provenance) {
        this.setPath = setPath;
        this.provenance = provenance;
    }

    public PathExpression getSetPath() {
        return setPath;
    }

    public String getProvenance() {
        return provenance;
    }

    public VariableProvenanceCondition clone() {
        try {
            VariableProvenanceCondition clone = (VariableProvenanceCondition) super.clone();
            clone.setPath = setPath.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VariableProvenanceCondition)) {
            return false;
        }
        VariableProvenanceCondition otherCondition = (VariableProvenanceCondition)obj;
        return setPath.equals(otherCondition.setPath) && provenance.equals(otherCondition.provenance);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Provenance of ").append(setPath).append(" = ").append(provenance);
        return result.toString();
    }
}
