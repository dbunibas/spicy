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

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.MappingTask;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComputeQualityMatchHarmonicMean implements IComputeQuality, Comparator<AnnotatedMappingTask> {

    private Log logger = LogFactory.getLog(this.getClass());
    
//    private GenerateTransformationsAndTranslate mappingTaskSolver = new GenerateTransformationsAndTranslate();

    public void computeQuality(AnnotatedMappingTask annotatedMappingTask) {
//        mappingTaskSolver.solve(mappingTask);
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
        List<ValueCorrespondence> correspondences = mappingTask.getValueCorrespondences();
        double sumOfValues = 0.0;
        for (ValueCorrespondence correspondence : correspondences) {
            sumOfValues += (1.0 / correspondence.getConfidence());
        }
        double harmonicMean = correspondences.size() / sumOfValues;
        annotatedMappingTask.setQualityMeasure(harmonicMean);
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
