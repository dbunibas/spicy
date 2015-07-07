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
 
package it.unibas.spicy.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericListGenerator<T> {
    
    private static Log logger = LogFactory.getLog(GenericListGenerator.class);
    
    @SuppressWarnings("unchecked")
    public List<List<T>> generateListsOfElements(List<List<T>> listOfLists) {
        if (listOfLists.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return generate(listOfLists);
    }
    
    private int estimateResultSize(List<List<T>> listOfLists) {
        int result = 1;
        for (List<T> list : listOfLists) {
            result *= list.size();
        }
        return result;
    }
    
    private List<List<T>> generate(List<List<T>> listOfLists) {
        if (listOfLists.size() == 1) {
            if (logger.isTraceEnabled()) logger.trace("Base call with list of size = " + listOfLists.size());
            List<List<T>> result = new ArrayList<List<T>>();
            List<T> firstListOfElements = listOfLists.get(0);
            for (T element : firstListOfElements) {
                List<T> singletonListOfMappings = new ArrayList<T>();
                singletonListOfMappings.add(element);
                result.add(singletonListOfMappings);
            }
            if (logger.isTraceEnabled()) logger.trace("Result: " + result);
            return result;
        }
        if (logger.isDebugEnabled()) logger.debug("Recursive call with list of size = " + listOfLists.size());
        List<List<T>> combinationsOfRest = generateListsOfElements(rest(listOfLists));
        if (logger.isDebugEnabled()) logger.debug("Returning from recursive call = " + listOfLists.size());
        List<T> firstList = listOfLists.get(0);
        if (logger.isTraceEnabled()) logger.trace("First list of variables = " + firstList);
        List<List<T>> result = new ArrayList<List<T>>();
        for (T elem : firstList) {
            if (logger.isTraceEnabled()) logger.trace("Adding element: " + elem);
            int j=0;
            for (List<T> combination : combinationsOfRest) {
                if (logger.isTraceEnabled()) logger.trace("To combination: " + combination);
                List<T> newCombination = generateNewAlternative(elem, combination);
                if (logger.isTraceEnabled()) logger.trace("New combination: " + newCombination);
                result.add(newCombination);
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private List<List<T>> rest(List<List<T>> list) {
        if (list.size() == 0) {
            throw new IllegalArgumentException("Empty list does not have rest");
        }
        List<List<T>> clone = (List<List<T>>)((ArrayList<List<T>>)list).clone();
        clone.remove(0);
        return clone;
    }
    
    @SuppressWarnings("unchecked")
    private List<T> generateNewAlternative(T element, List<T> combination) {
        List<T> newCombination = (List<T>)((ArrayList<T>)combination).clone();
        newCombination.add(0, element);
        return newCombination;
    }
    
}
