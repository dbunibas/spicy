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
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.structuralanalysis.*;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.EmptyCircuitException;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.compare.comparators.IFeatureComparator;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PerformLocalStructuralAnalysis implements IPerformStructuralAnalysis {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private static final double MIN_GLOBAL_COMPARE = 0.5;
    
//    private GenerateTransformationsAndTranslate mappingTaskSolver = new GenerateTransformationsAndTranslate();
    @Inject()
    private SampleInstances sampler;
    @Inject() @Named(Application.CIRCUIT_BUILDER_FOR_COMPARE)
    private IBuildCircuitStrategy circuitBuilderForCompare;
    private List<IFeatureComparator> comparatorChain;
    private List<IFeatureComparator> localComparatorChain;
    @Inject()
    private CompareFeatures comparator;
    
    private INode targetSchemaWithSamples;
    
    private FindNode nodeFinder = new FindNode();
    
    private Map<INode, INode> virtualTupleCache = new HashMap<INode, INode>();

    public PerformLocalStructuralAnalysis() {
        this.comparatorChain = Application.getInstance().getComparatorChain();
        this.localComparatorChain = Application.getInstance().getComparatorChainForMatch();
    }
    
    public PerformLocalStructuralAnalysis(SampleInstances sampler, IBuildCircuitStrategy circuitBuilder, CompareFeatures comparator) {
        this();
        this.sampler = sampler;
        this.circuitBuilderForCompare = circuitBuilder;
        this.comparator = comparator;
    }
    
    public void runAnalisys(AnnotatedMappingTask annotatedMappingTask) {
        //TODO: local analysis requires significant refactoring
        //System.out.println("IPerformStructuralAnalysis - " + this.getClass().getSimpleName());
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
        initClone(mappingTask.getTargetProxy().getDataSource());
//        mappingTaskSolver.solve(mappingTask);
        List<SimilarityCheck> similarityChecks = buildSimilarityChecks(annotatedMappingTask);
        for (SimilarityCheck similarityCheck : similarityChecks) {
            try {
                generateCircuits(similarityCheck);
                comparator.compare(similarityCheck, comparatorChain);
                double globalSimilarity = similarityCheck.getQualityMeasure();
                if (globalSimilarity < MIN_GLOBAL_COMPARE) {
                    continue;
                }
                double localSimilarity = runLocalCompare(similarityCheck, mappingTask.getValueCorrespondences());
                similarityCheck.setQualityMeasure((globalSimilarity + localSimilarity) / 2);
            } catch (EmptyCircuitException ex) {
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
        //System.out.println(this.getClass().getSimpleName() + " - buildSimilarityChecks");
        List<SimilarityCheck> result = new ArrayList<SimilarityCheck>();
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
        DataSource target = mappingTask.getTargetProxy().getDataSource();
        if (target.getInstances().isEmpty()) {
            throw new IllegalArgumentException("Target does not have instances: " + mappingTask);
        }
        List<Transformation> transformations = annotatedMappingTask.getTransformations();
        assert(transformations != null) : "Missing trasformations in mapping task: " + mappingTask;
        for (Transformation transformation : transformations) {
            List<INode> translatedInstances = transformation.getTranslatedInstances();
            if (translatedInstances == null) {
                continue;
            }
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
        Circuit firstCircuit = circuitBuilderForCompare.buildAndSolveCircuit(firstSchema, similarityCheck, true);
        similarityCheck.addAnnotation(SpicyConstants.FIRST_CIRCUIT, firstCircuit);
        INode secondSchema = similarityCheck.getSecondSchemaWithSamples();
        Circuit secondCircuit = circuitBuilderForCompare.buildAndSolveCircuit(secondSchema, similarityCheck, true);
        similarityCheck.addAnnotation(SpicyConstants.SECOND_CIRCUIT, secondCircuit);
    }
    
    private double runLocalCompare(SimilarityCheck similarityCheck, List<ValueCorrespondence> correspondences) {
        INode firstSchemaWithSamples = similarityCheck.getFirstSchemaWithSamples();
        INode secondSchemaWithSamples = similarityCheck.getSecondSchemaWithSamples();
        int numberOfAttributes = correspondences.size();
        double totalSimilarity = 0;
        for (ValueCorrespondence correspondence : correspondences) {
            PathExpression targetPath = correspondence.getTargetPath();
            INode nodeInTarget = nodeFinder.findNodeInSchema(targetPath, firstSchemaWithSamples);
            INode nodeInTranslation = nodeFinder.findNodeInSchema(targetPath, secondSchemaWithSamples);
            INode targetVirtualTuple = getVirtualTuple(nodeInTarget);
            INode translationVirtualTuple = getVirtualTuple(nodeInTranslation);
            SimilarityCheck localSimilarityCheck = new SimilarityCheck(targetVirtualTuple, translationVirtualTuple);
            try {
                Circuit firstCircuit = circuitBuilderForCompare.buildAndSolveCircuit(targetVirtualTuple, localSimilarityCheck, true);
                localSimilarityCheck.addAnnotation(SpicyConstants.FIRST_CIRCUIT, firstCircuit);
                Circuit secondCircuit = circuitBuilderForCompare.buildAndSolveCircuit(translationVirtualTuple, localSimilarityCheck, true);
                localSimilarityCheck.addAnnotation(SpicyConstants.SECOND_CIRCUIT, secondCircuit);
                comparator.compare(localSimilarityCheck, localComparatorChain);
                if (logger.isDebugEnabled()) logger.debug("Checking attribute features for attribute: " + targetPath + "\n"+ similarityCheck);
            } catch (EmptyCircuitException e) {
                localSimilarityCheck.setQualityMeasure(0.0);
            }
            double attributeSimilarity = localSimilarityCheck.getQualityMeasure();
            totalSimilarity += attributeSimilarity;
            similarityCheck.addFeature(SpicyConstants.LOCAL_SIMILARITY + " - " + targetPath, attributeSimilarity, 1.0);
        }
        return totalSimilarity / numberOfAttributes;
    }
    
    private INode getVirtualTuple(INode node) {
        INode virtualTuple = virtualTupleCache.get(node);
        if (virtualTuple == null) {
            virtualTuple = new TupleNode("virtual");
            virtualTuple.setRoot(true);
            virtualTuple.addChild(node.clone());
            virtualTupleCache.put(node, virtualTuple);
        }
        return virtualTuple;
    }

    private double getBestCompare(AnnotatedMappingTask annotatedMappingTask) {
        List<SimilarityCheck> similarityChecks = annotatedMappingTask.getSimilarityChecks();
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
 
    public void clear(){
        this.comparatorChain.clear();
        this.localComparatorChain.clear();
        this.targetSchemaWithSamples = null;
        this.virtualTupleCache.clear();
    }
}
