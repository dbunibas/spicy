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
 
package it.unibas.spicy.structuralanalysis.sampling.strategies;

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.structuralanalysis.sampling.IntegerKeyDistribution;
import it.unibas.spicy.structuralanalysis.sampling.SampleValue;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateStandardLengthDistribution implements IGenerateLengthDistributionStrategy {
    
    private Log logger = LogFactory.getLog(this.getClass());
        
    public void generateLengthDistribution(AttributeNode node) {
        IntegerKeyDistribution lengthDistribution = new IntegerKeyDistribution();
        List<SampleValue> sampleValues = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        assert(sampleValues != null) : "Samples missing in attribute: " + node.getLabel();
        for (SampleValue sampleValue: sampleValues) {
            Integer key = new Integer(sampleValue.getLength());
            lengthDistribution.addValue(key, 1);
        }
        node.addAnnotation(SpicyConstants.LENGHT_DISTRIBUTION, lengthDistribution);    
    }
    
}
