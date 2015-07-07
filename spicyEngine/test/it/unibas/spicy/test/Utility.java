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
 
package it.unibas.spicy.test;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.values.INullValue;
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.PathExpressionWithFilters;
import it.unibas.spicy.model.paths.SetAlias;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utility {

    private static FindNode nodeFinder = new FindNode();

    // variables
    public static boolean checkVariable(String setLabel, Collection<SetAlias> variables, INode root) {
        return findVariable(setLabel, variables, root) != null;
    }

    public static  SetAlias findVariable(String setLabel, Collection<SetAlias> variables, INode root) {
        for (SetAlias variable : variables) {
            if (variable.getBindingPathExpression().getLastStep().equals(setLabel)) {
                return variable;
            }
        }
        return null;
    }

    // VIEWS
    public static  SimpleConjunctiveQuery retrieveViewFromVariables(List<String> expectedVariables, List<SimpleConjunctiveQuery> actualViews) {
        for (SimpleConjunctiveQuery view : actualViews) {
            if (checkVariablesInView(expectedVariables, view)) {
                return view;
            }
        }
        return null;
    }

    public static  boolean checkVariablesInView(List<String> expectedVariables, SimpleConjunctiveQuery currentView) {
        List<String> currentVariablesString = convertListOfVariables(currentView.getVariables());
        if (currentVariablesString.containsAll(expectedVariables)) {
            return true;
        }
        return false;
    }

    public static  List<String> convertListOfVariables(List<SetAlias> variables) {
        List<String> variablesString = new ArrayList<String>();
        for (SetAlias variable : variables) {
            variablesString.add(variable.getAbsoluteBindingPathExpression().toString());
        }
        return variablesString;
    }

    public static  List<INode> getNodesWithFilters(INode instanceNode, String tupleNode, String[][] filters) {
        PathExpressionWithFilters pathExpressionWithFilter = new PathExpressionWithFilters(tupleNode, filters);
        return nodeFinder.findNodesWithFilterInInstance(pathExpressionWithFilter, instanceNode);
    }

    public static  String[][] addFiltersToArray(String[][] filter1, String[][] filter2) {
        int filter1Size = filter1.length;
        int filter2Size = filter2.length;
        String[][] result = new String[filter1Size + filter2Size][2];
        for (int i = 0; i < filter1Size; i++) {
            String[] keys = filter1[i];
            result[i] = keys;
        }
        for (int i = 0; i < filter2Size; i++) {
            String[] keys = filter2[i];
            result[i + filter1Size] = keys;
        }
        return result;
    }

    public static int getDifferentGeneratedValues(String attributePath, List<INode> nodes) {
        List<String> differentValues = new ArrayList<String>();
        for (INode node : nodes) {
            String attributeValue = node.getChild(attributePath).getChild(0).getValue().toString();
            if (!differentValues.contains(attributeValue)) {
                differentValues.add(attributeValue);
            }
        }
        return differentValues.size();
    }

    public static INullValue nullValue() {
        return NullValueFactory.getNullValue();
    }
}
