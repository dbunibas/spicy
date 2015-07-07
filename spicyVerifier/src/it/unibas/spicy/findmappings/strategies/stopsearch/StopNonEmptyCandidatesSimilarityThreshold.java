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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.findmappings.strategies.*;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StopNonEmptyCandidatesSimilarityThreshold implements IStopSearchStrategy {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    @Inject() @Named(Application.SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS_KEY)
    private double similarityThreshold;
    @Inject() @Named(Application.QUALITY_THRESHOLD_KEY)
    private double qualityThreshold;
    
    public boolean stopSearch(IFindBestMappingsStrategy mappingFinder) {
        double requiredSimilarity = (Double)mappingFinder.getStatusValue(SpicyConstants.CURRENT_SIMILARITY);
        if (requiredSimilarity < similarityThreshold) {
            if (logger.isDebugEnabled()) logger.debug("Attribute similarity too low, stopping...");
            return true;
        }
        List<AnnotatedMappingTask> currentResult = (List<AnnotatedMappingTask>)mappingFinder.getStatusValue(SpicyConstants.CURRENT_RESULT);
        if (currentResult.size() > 0) {
            AnnotatedMappingTask bestAnnotatedMappingTask = currentResult.get(0);
            double quality = bestAnnotatedMappingTask.getQualityMeasure();
            logger.info("Best quality: " + quality);
            logger.info("Best Result: " + bestAnnotatedMappingTask);
            if (quality > qualityThreshold) {   
                return true;
            }
        }
        return false;
    }
    
}
