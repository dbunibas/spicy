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
 
package it.unibas.spicy.model.mapping.rewriting.egds;

import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class AttributeGroup {

    private List<VariablePathExpression> attributePaths = new ArrayList<VariablePathExpression>();

    public List<VariablePathExpression> getAttributePaths() {
        return attributePaths;
    }

    public void addAttributePath(VariablePathExpression attributePath) {
        this.attributePaths.add(attributePath);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
            result.append("--------- Attribute Group:  ");
        if (attributePaths.size() == 1) {
            result.append(attributePaths.get(0));
        } else {
            result.append("\n");
            for (int i = 0; i < attributePaths.size(); i++) {
                VariablePathExpression attributePath = attributePaths.get(i);
                result.append(attributePath);
                if (i != attributePaths.size() - 1) {
                    result.append("\n");
                }
            }
        }
        return result.toString();
    }
}
