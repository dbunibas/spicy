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
 
package it.unibas.spicy;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedMappingTask {

    private MappingTask mappingTask;
    private List<SimilarityCheck> similarityChecks;
    private Double qualityMeasure;
    private List<Transformation> transformations;

    public AnnotatedMappingTask(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public Double getQualityMeasure() {
        return qualityMeasure;
    }

    public void setQualityMeasure(Double qualityMeasure) {
        this.qualityMeasure = qualityMeasure;
    }

    public List<SimilarityCheck> getSimilarityChecks() {
        return similarityChecks;
    }

    public void setSimilarityChecks(List<SimilarityCheck> similarityChecks) {
        this.similarityChecks = similarityChecks;
    }

    public List<Transformation> getTransformations() {
        Transformation transformation = new Transformation(this.mappingTask.getMappingData().getSTTgds(), 1);
        transformation.setTranslatedInstances(mappingTask.getMappingData().getSolution().getInstances());
        List<Transformation> result = new ArrayList<Transformation>();
        result.add(transformation);
        return result;
    }

    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("\n--------- Annotated Mapping Task ---------\n");
//        result.append("Quality Measure: " + this.qualityMeasure + "\n");
        result.append("Similarity Checks: \n" + this.similarityChecks + "\n");
        result.append("================ Correspondences ===================\n");
        for (ValueCorrespondence correspondence : this.mappingTask.getValueCorrespondences()) {
            result.append(correspondence).append("\n");
        }
        result.append("Translated Instances: \n");
        for (INode instance : this.getTransformations().get(0).getTranslatedInstances()) {
            result.append(instance.toShortString());
        }
//        result.append(this.mappingTask.toString());
        return result.toString();
    }

    public String toStringWithoutInstances() {
        StringBuffer result = new StringBuffer();
        result.append("\n--------- Annotated Mapping Task ---------\n");
//        result.append("Quality Measure: " + this.qualityMeasure + "\n");
        result.append("Similarity Checks: \n" + this.similarityChecks + "\n");
        result.append("================ Correspondences ===================\n");
        for (ValueCorrespondence correspondence : this.mappingTask.getValueCorrespondences()) {
            result.append(correspondence).append("\n");
        }
        return result.toString();
    }
}
