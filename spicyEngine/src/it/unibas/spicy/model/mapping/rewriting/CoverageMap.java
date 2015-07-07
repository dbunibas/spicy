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
import java.util.List;
import java.util.Map;

public class CoverageMap {

    private MappingTask mappingTask;
    private Map<FORule, List<Coverage>> coverageMap;

    public CoverageMap(MappingTask mappingTask, Map<FORule, List<Coverage>> coverageMap) {
        this.mappingTask = mappingTask;
        this.coverageMap = coverageMap;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public List<FORule> getTgds() {
        List<FORule> tgds = new ArrayList<FORule>(coverageMap.keySet());
        Collections.sort(tgds);
        return tgds;
    }

    public int getNumberOfCoverages() {
        int counter = 0;
        for (FORule tgd : getTgds()) {
            counter += getCoverage(tgd).size();
        }
        return counter;
    }

    public List<Coverage> getCoverage(FORule tgd) {
        return coverageMap.get(tgd);
    }

    public String toString() {
        List<FORule> tgds = new ArrayList<FORule>(coverageMap.keySet());
        Collections.sort(tgds);
        StringBuilder result = new StringBuilder("-------------- COVERAGE MAP (tgds: " + tgds.size() + ", coverages: " + getNumberOfCoverages() + ") ---------------------\n");
        for (FORule tgd : tgds) {
            result.append("\n*************************** TGD ******************************* \n");
            result.append(tgd);
            result.append(SpicyEngineConstants.INDENT).append("--------------------------- Coverage ---------------------------\n");
            if (coverageMap.get(tgd).size() != 0) {
                for (Coverage coverage : coverageMap.get(tgd)) {
                    result.append(coverage.toString(SpicyEngineConstants.INDENT));
                }
            } else {
                result.append(SpicyEngineConstants.INDENT).append("(no coverage)\n");
            }
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }
}
