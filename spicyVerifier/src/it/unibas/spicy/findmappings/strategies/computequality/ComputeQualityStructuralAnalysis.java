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
 
package it.unibas.spicy.findmappings.strategies.computequality;

import com.google.inject.Inject;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.structuralanalysis.strategies.IPerformStructuralAnalysis;
import java.util.Comparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComputeQualityStructuralAnalysis implements IComputeQuality, Comparator<AnnotatedMappingTask> {

    private Log logger = LogFactory.getLog(this.getClass());
    
    @Inject()
    private IPerformStructuralAnalysis analyzer;
        
    public ComputeQualityStructuralAnalysis() {}
    
    public ComputeQualityStructuralAnalysis(IPerformStructuralAnalysis analyzer) {
        this.analyzer = analyzer;
    }
    
    public void computeQuality(AnnotatedMappingTask annotatedMappingTask) {
        if (annotatedMappingTask.getQualityMeasure() != null) {
            logger.info("Mapping task already solved. Skipping...");
            return;
        }
        analyzer.runAnalisys(annotatedMappingTask);
    //    analyzer.clear();
    }
       
    public int compare(AnnotatedMappingTask mt1, AnnotatedMappingTask mt2) {
        double bestSimilarity1 = (Double)mt1.getQualityMeasure();
        double bestSimilarity2 = (Double)mt2.getQualityMeasure();
        double difference = bestSimilarity1 - bestSimilarity2;
        if (difference < 0.0) {
            return 1;
        } else if (difference > 0.0) {
            return -1;
        }
        return 0;
    }
}
