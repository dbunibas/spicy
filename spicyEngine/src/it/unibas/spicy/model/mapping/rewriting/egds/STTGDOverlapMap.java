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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.operators.NormalizeSTTGDs;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class STTGDOverlapMap {

    private MappingTask mappingTask;
    private Map<FORule, List<VariableOverlap>> overlapMap;
    private List<VariableOverlap> discardedOverlaps = new ArrayList<VariableOverlap>();

    public STTGDOverlapMap(MappingTask mappingTask, Map<FORule, List<VariableOverlap>> overlaps) {
        this.mappingTask = mappingTask;
        this.overlapMap = overlaps;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public List<FORule> getOverlapTgds() {
        List<FORule> tgds = new ArrayList<FORule>(overlapMap.keySet());
        Collections.sort(tgds, new OverlapTGDComparator(this));
        NormalizeSTTGDs normalizer = new NormalizeSTTGDs();
        List<FORule> normalizedTgds = normalizer.normalizeSTTGDs(tgds);
        return normalizedTgds;
    }

    public List<VariableOverlap> getDiscardedOverlaps() {
        return discardedOverlaps;
    }

    public Map<FORule, List<VariableOverlap>> getOverlapMap() {
        return overlapMap;
    }

    // returns all overlaps in which tgd is involved
    public List<VariableOverlap> getOverlaps(FORule tgd) {
        return overlapMap.get(tgd);
    }

    public int getNumberOfOverlaps() {
        int counter = 0;
        for (FORule tgd : overlapMap.keySet()) {
            counter += getOverlaps(tgd).size();
        }
        return counter;
    }

    public String toString() {
        List<FORule> tgds = new ArrayList<FORule>(overlapMap.keySet());
        Collections.sort(tgds, new OverlapTGDComparator(this));
        StringBuilder result = new StringBuilder("-------------- ST OVERLAPS (size: " + tgds.size() + ", overlaps: " + getNumberOfOverlaps() + ") ---------------------\n");
        for (FORule tgd : tgds) {
            result.append("\n*************************** TGD ******************************* \n");
            result.append(tgd);
            result.append(SpicyEngineConstants.INDENT).append("--------------------------- Overlaps ---------------------------\n");
            if (overlapMap.get(tgd).size() != 0) {
                for (VariableOverlap overlap : overlapMap.get(tgd)) {
                    result.append(overlap.toString(SpicyEngineConstants.INDENT)).append("\n");
                }
            } else {
                result.append(SpicyEngineConstants.INDENT).append("(no father)\n");
            }
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }
}

class OverlapTGDComparator implements Comparator<FORule> {

    private STTGDOverlapMap tgdDag;

    public OverlapTGDComparator(STTGDOverlapMap tgdDag) {
        this.tgdDag = tgdDag;
    }

    public int compare(FORule tgd1, FORule tgd2) {
        int overlaps1 = tgdDag.getOverlaps(tgd1).size();
        int overlaps2 = tgdDag.getOverlaps(tgd2).size();
        return overlaps1 - overlaps2;
    }
}