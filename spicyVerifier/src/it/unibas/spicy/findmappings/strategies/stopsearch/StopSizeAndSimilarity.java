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
 
package it.unibas.spicy.findmappings.strategies.stopsearch;

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.model.mapping.MappingTask;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StopSizeAndSimilarity implements IStopSearchStrategy {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private static final double FINAL_SIMILARITY = 0.85;
    private static final double SIZE_FACTOR = 0.1;
    
    private double lastQualityValue = 0;
    private List<AnnotatedMappingTask> previousCandidates;
    
    public boolean stopSearch(IFindBestMappingsStrategy mappingFinder) {
        double requiredSimilarity = (Double)mappingFinder.getStatusValue(SpicyConstants.CURRENT_SIMILARITY);
        if (requiredSimilarity < FINAL_SIMILARITY) {
            if (logger.isDebugEnabled()) logger.debug("Required similarity too low, stopping...");
            return true;
        }
        List<AnnotatedMappingTask> candidateMappingTasks = (List<AnnotatedMappingTask>)mappingFinder.getStatusValue(SpicyConstants.CURRENT_CANDIDATES);
        if (candidateMappingTasks.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("No candidates, go on searching...");
            return false;
        }
        double quality = computeQualityMeasure(candidateMappingTasks);
        boolean decreasing = (quality < lastQualityValue);
        if (logger.isTraceEnabled()) logger.trace("Mapping quality: " + quality + " - decreasing: " + decreasing);
        if (!decreasing) {
            lastQualityValue = quality;
            previousCandidates = candidateMappingTasks;
        }
        return decreasing;
    }
            
    private double computeQualityMeasure(List<AnnotatedMappingTask> candidateMappingTasks) {
        AnnotatedMappingTask annotatedBestMappingTask = candidateMappingTasks.get(0);
        MappingTask bestMappingTask = annotatedBestMappingTask.getMappingTask();
        if (logger.isTraceEnabled()) logger.trace("Best mapping task: " + bestMappingTask.toString());
        int size = bestMappingTask.getValueCorrespondences().size();
        double bestSimilarity = (Double)annotatedBestMappingTask.getQualityMeasure();
        if (logger.isDebugEnabled()) logger.debug("Best similarity for candidates: " + bestSimilarity);
        double quality = (size * SIZE_FACTOR) + bestSimilarity;        
        return quality;
    }
}
