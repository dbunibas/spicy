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
import java.util.Enumeration;
import java.util.List;

public class GenericCombinationsGenerator<T> implements Enumeration {
    
    protected List<T> inputCollections;
    protected int inputCollectionSize, dimension;
    protected int[] array;
    protected boolean hasMore = true;

    public GenericCombinationsGenerator(List<T> inputCollections, int dimension) {
        this.inputCollections = inputCollections;
        this.inputCollectionSize = inputCollections.size();
        this.dimension = dimension;
        assert(0 <= dimension && dimension <= inputCollectionSize) : "Unable to generate combinations of size: " + dimension + " of " + inputCollectionSize + " elements";
        array = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            array[i] = i;
        }
    }
    
    public boolean hasMoreElements() {
        return hasMore;
    }

    protected void moveIndex() {
        int i = rightmostIndexBelowMax();
        if (i >= 0) {
            array[i] = array[i] + 1;
            for (int j = i + 1; j < dimension; j++) {
                array[j] = array[j-1] + 1;
            }
        } else {
            hasMore = false;
        }
    }

    public List<T> nextElement() {
        if (!hasMore) {
            return null;
        }
        List<T> combination = new ArrayList<T>();
        for (int i = 0; i < dimension; i++) {
            combination.add(i, this.inputCollections.get(array[i]));
        }
        moveIndex();
        return combination;
    }

    protected int rightmostIndexBelowMax() {       
        for (int i = dimension - 1; i >= 0; i--) {
            if (array[i] < inputCollectionSize - dimension + i) {
                return i;
            }
        }
        return -1;
    }
}
