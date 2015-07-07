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

import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.HashMap;
import java.util.Map;

public class PathExpressionWithFilters extends PathExpression {

    private Map<PathExpression, Object> filterValues = new HashMap<PathExpression, Object>();

    public PathExpressionWithFilters(PathExpression pathExpression) {
        super(pathExpression);
    }

    public PathExpressionWithFilters(PathExpression pathExpression, Map<String, Object> stringFilterValues) {
        super(pathExpression);
        this.filterValues = transformPaths(stringFilterValues);
    }

    public PathExpressionWithFilters(String pathExpressionString, String[][] stringFilterValues) {
        super(new GeneratePathExpression().generatePathFromString(pathExpressionString));
        this.filterValues = transformPaths(stringFilterValues);
    }

    public Map<PathExpression, Object> getFilterValues() {
        return filterValues;
    }

    public Object addFilterValue(PathExpression key, Object value) {
        return filterValues.put(key, value);
    }

    public String toString() {
        String result = "";
        for (String nodeLabel : this.pathSteps) {
            if (!result.equals("")) {
                result += ".";
            }
            result += nodeLabel;
        }
        result += "\n";
        for (PathExpression filterPath : this.filterValues.keySet()) {
            result += filterPath + " = " + filterValues.get(filterPath);
        }
        return result;
    }

    private Map<PathExpression, Object> transformPaths(Map<String, Object> stringFilterValues) {
        Map<PathExpression, Object> result = new HashMap<PathExpression, Object>();
        for (String pathString : stringFilterValues.keySet()) {
            result.put(new GeneratePathExpression().generatePathFromString(pathString), stringFilterValues.get(pathString));
        }
        return result;
    }

    private Map<PathExpression, Object> transformPaths(String[][] stringFilterValues) {
        Map<PathExpression, Object> result = new HashMap<PathExpression, Object>();
        for (int i = 0; i < stringFilterValues.length; i++) {
            String pathString = stringFilterValues[i][0];
            String filterString = stringFilterValues[i][1];
            result.put(new GeneratePathExpression().generatePathFromString(pathString), filterString);
        }
        return result;
    }
}
