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

public class ExpansionPartialOrder {

    private MappingTask mappingTask;
    private Map<Expansion, List<ExpansionOrderedPair>> subsumptions;

    public ExpansionPartialOrder(MappingTask mappingTask, Map<Expansion, List<ExpansionOrderedPair>> subsumptions) {
        this.mappingTask = mappingTask;
        this.subsumptions = subsumptions;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public List<Expansion> getExpansions() {
        List<Expansion> tgds = new ArrayList<Expansion>(subsumptions.keySet());
        Collections.sort(tgds, new ExpansionComparator(this));
        return tgds;
    }

    public List<ExpansionOrderedPair> getOrderedPairsForExpansion(Expansion expansion) {
        return subsumptions.get(expansion);
    }

    public int getSize() {
        int counter = 0;
        for (Expansion expansion : subsumptions.keySet()) {
            counter += getOrderedPairsForExpansion(expansion).size();
        }
        return counter;
    }

    public String toString() {
        List<Expansion> expansions = new ArrayList<Expansion>(subsumptions.keySet());
        Collections.sort(expansions, new ExpansionComparator(this));
        StringBuilder result = new StringBuilder("-------------- Expansion Partial Order (expansions: " + expansions.size() + ", subsumptions: " + getSize() + ") ---------------------\n");
        for (Expansion expansion : expansions) {
            result.append(expansion);
            result.append(SpicyEngineConstants.INDENT).append("--------------------------- Ordered Pairs ---------------------------\n");
            if (subsumptions.get(expansion).size() != 0) {
                for (ExpansionOrderedPair subsumption : subsumptions.get(expansion)) {
                    result.append(subsumption.toString(SpicyEngineConstants.INDENT)).append("\n");
                }
            } else {
                result.append(SpicyEngineConstants.INDENT).append("(no ordered pairs)\n");
            }
            result.append("\n");
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }

}

class ExpansionComparator implements Comparator<Expansion> {

    private ExpansionPartialOrder subsumptionMap;

    public ExpansionComparator(ExpansionPartialOrder tgdDag) {
        this.subsumptionMap = tgdDag;
    }

    public int compare(Expansion expansion1, Expansion expansion2) {
        int numberOfFathers1 = subsumptionMap.getOrderedPairsForExpansion(expansion1).size();
        int numberOfFathers2 = subsumptionMap.getOrderedPairsForExpansion(expansion2).size();
        return numberOfFathers1 - numberOfFathers2;
    }
}