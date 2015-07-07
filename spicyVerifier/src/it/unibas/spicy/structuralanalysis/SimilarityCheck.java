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
 
package it.unibas.spicy.structuralanalysis;

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.compare.SimilarityFeature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarityCheck implements Comparable<SimilarityCheck> {
    
    private INode firstSchemaWithSamples;
    private INode secondSchemaWithSamples;
    private List<SimilarityFeature> features = new ArrayList<SimilarityFeature>();
    private Map<String, Object> annotations = new HashMap<String, Object>();
    private Double qualityMeasure;
    
    public SimilarityCheck(INode firstSchemaWithSamples, INode secondSchemaWithSamples) {
        this.firstSchemaWithSamples = firstSchemaWithSamples;
        this.secondSchemaWithSamples = secondSchemaWithSamples;
    }
    
    public INode getFirstSchemaWithSamples() {
        return firstSchemaWithSamples;
    }

    public INode getSecondSchemaWithSamples() {
        return secondSchemaWithSamples;
    }
    
    public void addFeature(String name, double value, double weight){
        SimilarityFeature feature = new SimilarityFeature(name, value, weight);
        this.features.add(feature);
    }
    
    public List<SimilarityFeature> getFeatures() {
        return features;
    }
    
    public void addAnnotation(String key, Object value) {
        this.annotations.put(key, value);
    }
    
    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }
    
    public double getQualityMeasure() {
        return qualityMeasure;
    }

    public void setQualityMeasure(double qualityMeasure) {
        this.qualityMeasure = qualityMeasure;
    }

    public int compareTo(SimilarityCheck sc) {
        double difference = this.qualityMeasure - sc.qualityMeasure;
        if (difference < 0.0) {
            return 1;
        } else if (difference > 0.0) {
            return -1;
        }
        return 0;
    }

    public String toString(){
        String result = "";
        if (!this.features.isEmpty()) {
            for(SimilarityFeature feature : features){
                result += feature + "\n";
            }
        }
        result += "OVERALL QUALITY: " + qualityMeasure + "\n";
        return result;
    }

    public String toStringWithCircuits(){
        String result = "---------SIMILARITY CHECK----------------\n";
        result += toString();
        Circuit firstCircuit = (Circuit)getAnnotation(SpicyConstants.FIRST_CIRCUIT);
        result += firstCircuit;
        Circuit secondCircuit = (Circuit)getAnnotation(SpicyConstants.SECOND_CIRCUIT);
        result += secondCircuit;
        return result;
    }

    public String toStringWithCircuitsAndSchemas(){
        String result = "---------SIMILARITY CHECK----------------\n";
        result += toString();
        Transformation transformation = (Transformation)getAnnotation(SpicyConstants.TRANSFORMATION);
        result += "TRANSFORMATION\n";
        result += transformation;
//        List<INode> translatedInstances = transformation.getTranslatedInstances();
//        result += "TRANSLATED INSTANCE\n";
//        result += translatedInstances.get(0);
        result += "FIRST SCHEMA\n";
        result += firstSchemaWithSamples.toStringWithAnnotations();
        result += "SECOND SCHEMA\n";
        result += secondSchemaWithSamples.toStringWithAnnotations();
        Circuit firstCircuit = (Circuit)getAnnotation(SpicyConstants.FIRST_CIRCUIT);
        result += firstCircuit;
        Circuit secondCircuit = (Circuit)getAnnotation(SpicyConstants.SECOND_CIRCUIT);
        result += secondCircuit;
        return result;
    }

    public String toLongString(){
        String result = "---------SIMILARITY CHECK----------------\n";
        result += toString();
        result += "FIRST SCHEMA\n";
        result += firstSchemaWithSamples.toStringWithAnnotations();
        result += "SECOND SCHEMA\n";
        result += secondSchemaWithSamples.toStringWithAnnotations();
        if (!this.annotations.isEmpty()) {
            result += "           ANNOTATIONS\n";
            for(String key : annotations.keySet()) {
                result += "Key: " + key + " - Value: " + annotations.get(key) + "\n";
            }
        }
        return result;
    }

}
