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
 
package it.unibas.spicy.structuralanalysis.circuits.operators;

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.structuralanalysis.operators.CheckNodeProperties;
import it.unibas.spicy.structuralanalysis.sampling.Distribution;
import it.unibas.spicy.structuralanalysis.sampling.SampleValue;
import java.util.List;

public class CalculateAttributeFeatures {

    private double totalStress;
    private double totalStatisticStress;
    private double totalConsistency;
    private double totalStatisticConsistency;
    private double totalDensity;
    
    private CheckNodeProperties nodeChecker = new CheckNodeProperties();
    
    public double getNodeConsistency(INode node) {
        Double consistency = (Double)node.getAnnotation(SpicyConstants.CONSISTENCY);
        assert(consistency != null) : "Consistency missing in attribute: " + node.getLabel();
        totalConsistency += consistency;
        return consistency;
    }
    
    public double getNodeStress(INode node) {
        Double stress = (Double)node.getAnnotation(SpicyConstants.STRESS);
        assert(stress != null) : "Stress missing in attribute: " + node.getLabel();
        totalStress += stress;
        return stress;
    }
    
    public double getNodeConsistencyIC(INode node) {
        Distribution categoryDistribution = (Distribution)node.getAnnotation(SpicyConstants.CHAR_CATEGORY_DISTRIBUTION);
        assert(categoryDistribution != null) : "Char category distribution missing in attribute: " + node.getLabel();
        double consistencyIC = categoryDistribution.getSimpsonConcentrationIndex();
        totalStatisticConsistency += consistencyIC;
        return consistencyIC;
    }
    
    public double getNodeStressIC(INode node) {
        Distribution lengthDistribution = (Distribution)node.getAnnotation(SpicyConstants.LENGHT_DISTRIBUTION);
        Double stress = (Double)node.getAnnotation(SpicyConstants.STRESS);
        assert(lengthDistribution != null) :  "Length distribution missing in attribute: " + node.getLabel();
        double stressIC = lengthDistribution.getSimpsonConcentrationIndex();
        totalStatisticStress += stressIC;
        return stressIC;
    }

    public double getNodeDensity(INode node) {
        Integer sampleSize = nodeChecker.getSampleSize(node);
        Integer nonNullValues = nodeChecker.getNumberOfNonNullSamples(node);
        double density = nonNullValues / (double)sampleSize;
        totalDensity += density;
        return density;
    }

    public double getTotalStress() {
        return totalStress;
    }

    public double getTotalStatisticStress() {
        return totalStatisticStress;
    }

    public double getTotalConsistency() {
        return totalConsistency;
    }

    public double getTotalStatisticConsistency() {
        return totalStatisticConsistency;
    }

    public double getTotalDensity() {
        return totalDensity;
    }
}
