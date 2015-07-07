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
 
package it.unibas.spicy.structuralanalysis.compare.operators;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.Application;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.compare.SimilarityFeature;
import it.unibas.spicy.structuralanalysis.compare.comparators.IFeatureComparator;
import it.unibas.spicy.structuralanalysis.compare.strategies.*;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompareFeatures {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    @Inject()
    private IAggregateSimilarityFeatures aggregator;
    @Inject() @Named(Application.SIMILARITY_THRESHOLD_FOR_PRUNING_KEY)
    private double similarityThresholdForPruning;
    
    public CompareFeatures() {}
    
    public CompareFeatures(IAggregateSimilarityFeatures meanStrategy, Double similarityThreshold) {
        this.aggregator = meanStrategy;
        this.similarityThresholdForPruning = similarityThreshold;
    }
    
    public void compare(SimilarityCheck similarityCheck, List<IFeatureComparator> comparatorChain) {
        for (IFeatureComparator comparator : comparatorChain) {
            comparator.compareFeature(similarityCheck);
            // TODO: check pruning
            if (featureBelowThreshold(similarityCheck)) {
                similarityCheck.setQualityMeasure(0.0);
                return;
            }
        }
        double result = aggregator.calculateAggregateValue(similarityCheck.getFeatures());
        if (logger.isDebugEnabled()) logger.debug("Similarity check: " + similarityCheck);
        similarityCheck.setQualityMeasure(result);
    }
    
    private boolean featureBelowThreshold(SimilarityCheck similarityCheck) {
        int numberOfFeatures = similarityCheck.getFeatures().size();
        SimilarityFeature lastFeature = similarityCheck.getFeatures().get(numberOfFeatures - 1);
        boolean result = (lastFeature.getValue() < similarityThresholdForPruning);
        return result;
    }
    
}
