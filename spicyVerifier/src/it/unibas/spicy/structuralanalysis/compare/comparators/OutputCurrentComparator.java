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
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OutputCurrentComparator implements IFeatureComparator {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private double weight;

    public OutputCurrentComparator() {
    }
    
    public OutputCurrentComparator(Double weight) {
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
            logger.warn("Currents are empty. Similarity = 0");
            double similarity = 0.0;
            similarityCheck.addFeature(SpicyConstants.OTHER_CURRENTS, similarity,getWeight());
            return;
        }
        double firstOutputCurrent = getOutputCurrent(firstCircuit);
        double secondOutputCurrent = getOutputCurrent(secondCircuit);
        double percentualDifference = SpicyUtility.ratio(firstOutputCurrent, secondOutputCurrent);
        double similarity = 1.0 - percentualDifference;
        if (logger.isDebugEnabled()) logger.debug("Comparing output currents: firstCurrent=" + firstOutputCurrent + " - secondCurrent=" + secondOutputCurrent + " - similarity=" + similarity);
        similarityCheck.addFeature(SpicyConstants.OUTPUT_CURRENT, similarity,getWeight());
    }
     
    private Double getOutputCurrent(Circuit circuit) {
        double[] currents = circuit.getCurrents();
        return currents[currents.length - 1];
//        logger.warn("Circuit: " + circuit.toString());
//        Map<String, Double> currentMap = circuit.getCurrentMap();
//        Double outputCurrent = currentMap.get("NULL-LOAD");
//        logger.warn("Output current: " + outputCurrent);
//        return outputCurrent;
    }
    
    public String toString() {
        return "Output current comparator - WEIGHT= " + getWeight();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
