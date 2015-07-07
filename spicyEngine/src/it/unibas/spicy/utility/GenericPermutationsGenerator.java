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

public class GenericPermutationsGenerator<T> implements Enumeration {
    
    private List<T> list;
    private int n, m;
    private int[] index;
    private boolean hasMore = true;
    
    public GenericPermutationsGenerator(List<T> list) {
        this(list, list.size());
    }
    
    public GenericPermutationsGenerator(List<T> list, int m) {
        this.list = list;
        this.n = list.size();
        this.m = m;
        index = new int[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        reverseAfter(m - 1);
    }
    
    public boolean hasMoreElements() {
        return hasMore;
    }
    
    private void moveIndex() {
        int i = rightmostDip();
        if (i < 0) {
            hasMore = false;
            return;
        }
        int leastToRightIndex = i + 1;
        for (int j = i + 2; j < n; j++) {
            if (index[j] < index[leastToRightIndex] &&  index[j] > index[i])
                leastToRightIndex = j;
        }
        int t = index[i];
        index[i] = index[leastToRightIndex];
        index[leastToRightIndex] = t;
        
        if (m - 1 > i) {
            reverseAfter(i);
            reverseAfter(m - 1);
        }
        
    }

    public List<T> nextElement() {
        if (!hasMore)
            return null;
        List<T> out = new ArrayList<T>();
        for (int i = 0; i < m; i++) {
            out.add(list.get(index[i]));
        }
        moveIndex();
        return out;
    }

    private void reverseAfter(int i) {
        int start = i + 1;
        int end = n - 1;
        while (start < end) {
            int t = index[start];
            index[start] = index[end];
            index[end] = t;
            start++;
            end--;
        }
    }

    private int rightmostDip() {
        for (int i = n - 2; i >= 0; i--) {
            if (index[i] < index[i+1])
                return i;
        }
        return -1;
    }
}