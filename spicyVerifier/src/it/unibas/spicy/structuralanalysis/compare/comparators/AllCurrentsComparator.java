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
 
package it.unibas.spicy.structuralanalysis.compare.comparators;

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.utility.SpicyUtility;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AllCurrentsComparator implements IFeatureComparator {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private double weight;

    public AllCurrentsComparator() {
    }
    
    public AllCurrentsComparator(Double weight) {
        this.weight = weight;
    }
        
    public void compareFeature(SimilarityCheck similarityCheck) {
        Circuit firstCircuit = (Circuit)similarityCheck.getAnnotation(SpicyConstants.FIRST_CIRCUIT);
        assert(firstCircuit != null) : "Missing circuit in similarity check: " + similarityCheck;
        Circuit secondCircuit = (Circuit)similarityCheck.getAnnotation(SpicyConstants.SECOND_CIRCUIT);
        assert(secondCircuit != null) : "Missing circuit in similarity check: " + similarityCheck;
        double[] firstCurrents = firstCircuit.getCurrents();
        double[] secondCurrents = secondCircuit.getCurrents();
        if (firstCurrents.length == 0 || secondCurrents.length == 0) {
            double similarity = 0.0;
            similarityCheck.addFeature(SpicyConstants.OTHER_CURRENTS, similarity,getWeight());
            return;
        }
        Map<String, Double> currentMap1 = null;
        Map<String, Double> currentMap2 = null;
        if (firstCircuit.getCurrentMap().size() <= secondCircuit.getCurrentMap().size()) {
            currentMap1 = firstCircuit.getCurrentMap();
            currentMap2 = secondCircuit.getCurrentMap();
        } else {
            currentMap1 = secondCircuit.getCurrentMap();
            currentMap2 = firstCircuit.getCurrentMap();
        }
        List<Double> differences = new ArrayList<Double>();
        for (String key : currentMap1.keySet()) {
            Double firstCurrent = currentMap1.get(key);
            Double secondCurrent = currentMap2.get(key);
            if (firstCurrent != null && secondCurrent != null) {
                differences.add(SpicyUtility.ratio(firstCurrent, secondCurrent));
            }
        }
        double sumOfDifferences = sumDifferences(differences);
        double meanDifference = sumOfDifferences / differences.size();
        double similarity = 1.0 - meanDifference;
        if (logger.isDebugEnabled()) logger.debug("Comparing currents: firstCurrents=" + SpicyUtility.printDoubleArray(firstCurrents) + " - secondCurrents=" + SpicyUtility.printDoubleArray(secondCurrents) + " - similarity=" + similarity);
        similarityCheck.addFeature(SpicyConstants.ALL_CURRENTS, similarity,getWeight());
    }
        
    private double sumDifferences(List<Double> differences) {
        double sum = 0;
        for (int i = 0; i < differences.size(); i++) {
            sum += differences.get(i);
        }
        return sum;
    }

    public String toString() {
        return "All currents comparator - WEIGHT= " + getWeight();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
