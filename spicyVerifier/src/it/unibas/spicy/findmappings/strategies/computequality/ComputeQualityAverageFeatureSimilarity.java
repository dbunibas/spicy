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
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComputeQualityAverageFeatureSimilarity implements IComputeQuality, Comparator<AnnotatedMappingTask> {
    
    private Log logger = LogFactory.getLog(this.getClass());

    @Inject()
    private SampleInstances sampler;
    @Inject() 
    private CompareFeatures comparator;
    @Inject() @Named(Application.CIRCUIT_BUILDER_FOR_MATCH)
    private IBuildCircuitStrategy circuitBuilder;
        
//    private GenerateTransformationsAndTranslate mappingTaskSolver = new GenerateTransformationsAndTranslate();
    private FindNode nodeFinder = new FindNode();
    
    private INode targetSchemaWithSamples;
    
    // requires refactoring; quick hack to obtain feature-based similarity between
    // canonical and translated instance; the code uses circuits but the actual features be controlled by
    // changing the chain of comparators
    public void computeQuality(AnnotatedMappingTask annotatedMappingTask) {
//        mappingTaskSolver.solve(mappingTask);
//        logger.warn("********************ANNOTATED MAPPING TASK: " + annotatedMappingTask);
        MappingTask mappingTask = annotatedMappingTask.getMappingTask();
//        logger.warn("********************MAPPING TASK: " + mappingTask);
        DataSource target = mappingTask.getTargetProxy().getDataSource();
        initClone(target);
        if (target.getInstances().isEmpty()) {
            throw new IllegalArgumentException("Target does not have instances: " + mappingTask);
        }
        List<Transformation> transformations = annotatedMappingTask.getTransformations();
        assert(transformations != null) : "Missing trasformations in mapping task: ";// + mappingTask;
        //TODO: only the first transformation is checked
        Transformation transformation = transformations.get(0);
        INode translatedSchemaWithSamples = sampleTranslatedInstances(transformation, target);
        double similarity = calculateSimilarity(targetSchemaWithSamples, translatedSchemaWithSamples, mappingTask);
        annotatedMappingTask.setQualityMeasure(similarity);
    }
    
    private void initClone(DataSource target) {
        // targetSchemaClone is a sampled clone of target;
        // target will not be sampled
        if (this.targetSchemaWithSamples == null) {
            this.targetSchemaWithSamples = target.getSchema().clone();
            sampler.sample(targetSchemaWithSamples, target.getInstances());
        }
    }
    
    private INode sampleTranslatedInstances(Transformation transformation, DataSource target) {
        List<INode> translatedInstances = transformation.getTranslatedInstances();
        assert(translatedInstances != null) : "Missing translated instances in transformation: " + transformation;
        INode secondTargetSchemaClone = target.getSchema().clone();
        sampler.sample(secondTargetSchemaClone, translatedInstances);
        return secondTargetSchemaClone;
    }
    
    private double calculateSimilarity(INode targetSchemaWithSamples, INode translatedSchemaWithSamples, MappingTask mappingTask) {
        List<ValueCorrespondence> correspondences = mappingTask.getValueCorrespondences();
        int numberOfAttributes = correspondences.size();
        double totalSimilarity = 0;
        for (ValueCorrespondence correspondence : correspondences) {
            PathExpression targetPath = correspondence.getTargetPath();
            INode nodeInTarget = nodeFinder.findNodeInSchema(targetPath, targetSchemaWithSamples);
            INode nodeInTranslation = nodeFinder.findNodeInSchema(targetPath, translatedSchemaWithSamples);
            INode targetVirtualTuple = getVirtualTuple(nodeInTarget);
            INode translationVirtualTuple = getVirtualTuple(nodeInTranslation);
            SimilarityCheck similarityCheck = new SimilarityCheck(targetVirtualTuple, translationVirtualTuple);
            Circuit firstCircuit = circuitBuilder.buildAndSolveCircuit(targetVirtualTuple, similarityCheck, false);
            similarityCheck.addAnnotation(SpicyConstants.FIRST_CIRCUIT, firstCircuit);
            Circuit secondCircuit = circuitBuilder.buildAndSolveCircuit(translationVirtualTuple, similarityCheck, false);
            similarityCheck.addAnnotation(SpicyConstants.SECOND_CIRCUIT, secondCircuit);
            comparator.compare(similarityCheck, Application.getInstance().getComparatorChain());
            if (logger.isDebugEnabled()) logger.debug("Checking attribute features for attribute: " + targetPath + "\n"+ similarityCheck);
            double attributeSimilarity = similarityCheck.getQualityMeasure();
            totalSimilarity += attributeSimilarity;
        }
        return totalSimilarity / numberOfAttributes;
    }
    
    private INode getVirtualTuple(INode node) {
        INode virtualTuple = new TupleNode("virtual");
        virtualTuple.setRoot(true);
        virtualTuple.addChild(node.clone());
        return virtualTuple;
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
