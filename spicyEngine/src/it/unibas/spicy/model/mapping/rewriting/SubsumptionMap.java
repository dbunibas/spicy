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

import it.unibas.spicy.model.mapping.*;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SubsumptionMap {

    private MappingTask mappingTask;
    private Map<FORule, List<Subsumption>> fathers;

    public SubsumptionMap(MappingTask mappingTask, Map<FORule, List<Subsumption>> fathers) {
        this.mappingTask = mappingTask;
        this.fathers = fathers;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

//    public List<TGD> getTgds() {
//        List<TGD> tgds = new ArrayList<TGD>(fathers.keySet());
//        Collections.sort(tgds, new TGDComparator(this));
//        return tgds;
//    }

    public List<FORule> getRules() {
        List<FORule> rules = new ArrayList<FORule>(fathers.keySet());
        Collections.sort(rules);
        return rules;
    }

    public List<Subsumption> getFathers(FORule tgd) {
        return fathers.get(tgd);
    }

    public int getSize() {
        int counter = 0;
        for (FORule tgd : fathers.keySet()) {
            counter += getFathers(tgd).size();
        }
        return counter;
    }

    public String toString() {
        List<FORule> tgds = new ArrayList<FORule>(fathers.keySet());
        Collections.sort(tgds, new TGDComparator(this));
        StringBuilder result = new StringBuilder("-------------- ST SUBSUMPTIONS (size: " + tgds.size() + ", subsumptions: " + getSize() + ") ---------------------\n");
        for (FORule tgd : tgds) {
            result.append("\n*************************** TGD ******************************* \n");
            result.append(tgd);
            result.append(SpicyEngineConstants.INDENT).append("--------------------------- Fathers ---------------------------\n");
            if (fathers.get(tgd).size() != 0) {
                for (Subsumption subsumption : fathers.get(tgd)) {
                    result.append(subsumption.toString(SpicyEngineConstants.INDENT)).append("\n");
                }
            } else {
                result.append(SpicyEngineConstants.INDENT).append("(no father)\n");
            }
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }
}

class TGDComparator implements Comparator<FORule> {

    private SubsumptionMap tgdDag;

    public TGDComparator(SubsumptionMap tgdDag) {
        this.tgdDag = tgdDag;
    }

    public int compare(FORule tgd1, FORule tgd2) {
        int numberOfFathers1 = tgdDag.getFathers(tgd1).size();
        int numberOfFathers2 = tgdDag.getFathers(tgd2).size();
        return numberOfFathers1 - numberOfFathers2;
    }
}