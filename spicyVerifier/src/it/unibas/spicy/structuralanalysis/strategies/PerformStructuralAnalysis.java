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
 
package it.unibas.spicy.structuralanalysis.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.EmptyCircuitException;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.compare.comparators.IFeatureComparator;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PerformStructuralAnalysis implements IPerformStructuralAnalysis {

    private Log logger = LogFactory.getLog(this.getClass());
//    private GenerateTransformationsAndTranslate mappingTaskSolver = new GenerateTransformationsAndTranslate();
    @Inject()
    private SampleInstances sampler;
    @Inject()
    @Named(Application.CIRCUIT_BUILDER_FOR_COMPARE)
    private IBuildCircuitStrategy circuitBuilder;
    private List<IFeatureComparator> comparatorChain;
    @Inject()
    private CompareFeatures comparator;
    private INode targetSchemaWithSamples;

    public PerformStructuralAnalysis() {
        this.comparatorChain = Application.getInstance().getComparatorChain();
    }

    public PerformStructuralAnalysis(SampleInstances sampler, IBuildCircuitStrategy circuitBuilder, CompareFeatures comparator) {
        this();
        this.sampler = sampler;
        this.circuitBuilder = circuitBuilder;
        this.comparator = comparator;
    }

    public void runAnalisys(AnnotatedMappingTask annotatedMappingTask) {
        //System.out.println("IPerformStructuralAnalysis - " + this.getClass().getSimpleName());
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
        initClone(mappingTask.getTargetProxy().getDataSource());
//        mappingTaskSolver.solve(mappingTask);
        List<SimilarityCheck> similarityChecks = buildSimilarityChecks(annotatedMappingTask);
        for (SimilarityCheck similarityCheck : similarityChecks) {
            try {
                generateCircuits(similarityCheck);
                comparator.compare(similarityCheck, comparatorChain);
            } catch (EmptyCircuitException ex) {
                logger.error(ex);
                similarityCheck.setQualityMeasure(0.0);
            }
        }
        annotatedMappingTask.setSimilarityChecks(similarityChecks);
        annotatedMappingTask.setQualityMeasure(getBestCompare(annotatedMappingTask));
    }

    private void initClone(DataSource target) {
        // targetSchemaClone is a sampled clone of target;
        // target will not be sampled
        if (this.targetSchemaWithSamples == null) {
            this.targetSchemaWithSamples = target.getSchema().clone();
            sampler.sample(targetSchemaWithSamples, target.getInstances());
        }
    }

    private List<SimilarityCheck> buildSimilarityChecks(AnnotatedMappingTask annotatedMappingTask) {
        List<SimilarityCheck> result = new ArrayList<SimilarityCheck>();
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
        DataSource target = mappingTask.getTargetProxy().getDataSource();
        if (target.getInstances().isEmpty()) {
            throw new IllegalArgumentException("Target does not have instances: " + mappingTask);
        }
        List<Transformation> transformations = (List<Transformation>) annotatedMappingTask.getTransformations();
        assert (transformations != null) : "Missing trasformations in mapping task: " + mappingTask;
        for (Transformation transformation : transformations) {
            List<INode> translatedInstances = transformation.getTranslatedInstances();
            assert (translatedInstances != null) : "Missing translated instances in transformation: " + transformation;
//            if (translatedInstances == null || areEmpty(translatedInstances)) {
//                continue;
//            }
            INode targetSchemaWithTranslatedSamples = target.getSchema().clone();
            sampler.sample(targetSchemaWithTranslatedSamples, translatedInstances);
            SimilarityCheck similarityCheck = new SimilarityCheck(targetSchemaWithSamples, targetSchemaWithTranslatedSamples);
            similarityCheck.addAnnotation(SpicyConstants.MAPPING_TASK, mappingTask);
            similarityCheck.addAnnotation(SpicyConstants.TRANSFORMATION, transformation);
            result.add(similarityCheck);
        }
        return result;
    }

    private boolean areEmpty(List<INode> translatedInstances) {
        for (INode instance : translatedInstances) {
            if (instance.getAnnotation(SpicyConstants.EMPTY_INSTANCE) == null) {
                return false;
            }
        }
        return true;
    }

    private void generateCircuits(SimilarityCheck similarityCheck) {
        INode firstSchema = similarityCheck.getFirstSchemaWithSamples();
        Circuit firstCircuit = circuitBuilder.buildAndSolveCircuit(firstSchema, similarityCheck, true);
        similarityCheck.addAnnotation(SpicyConstants.FIRST_CIRCUIT, firstCircuit);
        INode secondSchema = similarityCheck.getSecondSchemaWithSamples();
        Circuit secondCircuit = circuitBuilder.buildAndSolveCircuit(secondSchema, similarityCheck, true);
        //  System.out.println("PerformStructuralAnalysis - SECOND CIRCUIT - NUMBER OF ANNOTATIONS = " + secondCircuit.getAnnotations().size());     
        //  System.out.println("PerformStructuralAnalysis - SECOND CIRCUIT CLASS = " + secondCircuit.getClass().getSimpleName());  
        similarityCheck.addAnnotation(SpicyConstants.SECOND_CIRCUIT, secondCircuit);
    }

    private double getBestCompare(AnnotatedMappingTask annotatedMappingTask) {
        List<SimilarityCheck> similarityChecks = (List<SimilarityCheck>) annotatedMappingTask.getSimilarityChecks();
        if (similarityChecks.isEmpty()) {
            return 0.0;
        }
        double maxCompare = 0.0;
        for (SimilarityCheck sc : similarityChecks) {
            if (sc.getQualityMeasure() > maxCompare) {
                maxCompare = sc.getQualityMeasure();
            }
        }
        return maxCompare;
    }

    public void clear() {
        this.comparatorChain.clear();
        this.targetSchemaWithSamples = null;
    }
}
