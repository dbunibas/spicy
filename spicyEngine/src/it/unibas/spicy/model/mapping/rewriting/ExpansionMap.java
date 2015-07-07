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
 
package it.unibas.spicy.model.mapping.rewriting;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExpansionMap {

    private MappingTask mappingTask;
    private Map<ExpansionBaseView, List<Expansion>> expansionMap;
    private List<Expansion> expansions;

    public ExpansionMap(MappingTask mappingTask, Map<ExpansionBaseView, List<Expansion>> expansions) {
        this.mappingTask = mappingTask;
        this.expansionMap = expansions;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public List<Expansion> getExpansions() {
        if (this.expansions == null) {
            expansions = new ArrayList<Expansion>();
            for (List<Expansion> viewExpansions : expansionMap.values()) {
                expansions.addAll(viewExpansions);
            }
        }
        return expansions;
    }

    public int getNumberOfExpansions() {
        return getExpansions().size();
    }

    public String toString(MappingTask mappingTask) {
        List<ExpansionBaseView> baseViews = new ArrayList<ExpansionBaseView>(expansionMap.keySet());
        Collections.sort(baseViews, new ViewComparator());
        StringBuilder result = new StringBuilder("-------------- EXPANSION MAP (size: " + getNumberOfExpansions() + ") ---------------------\n");
        for (ExpansionBaseView view : baseViews) {
            result.append("\n*************************** Base View ******************************* \n");
            result.append(view);
            result.append(SpicyEngineConstants.INDENT).append("--------------------------- Expansions ---------------------------\n");
            if (!expansionMap.get(view).isEmpty()) {
                for (Expansion expansion : expansionMap.get(view)) {
                    result.append(expansion.toString(SpicyEngineConstants.INDENT));
                }
            } else {
                result.append(SpicyEngineConstants.INDENT).append("(no expansion)\n");
            }
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }
}

class ViewComparator implements Comparator<ExpansionBaseView> {

    public int compare(ExpansionBaseView view1, ExpansionBaseView view2) {
        return view1.getStTgd().compareTo(view2.getStTgd());
    }
}