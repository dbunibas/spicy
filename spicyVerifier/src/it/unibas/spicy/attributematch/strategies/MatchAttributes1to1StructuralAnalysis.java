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
 
package it.unibas.spicy.attributematch.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.attributematch.operators.FindAttributes;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.operators.CheckNodeProperties;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatchAttributes1to1StructuralAnalysis implements IMatchAttributes {

    private Log logger = LogFactory.getLog(this.getClass());
    @Inject
    private SampleInstances sampler;
    @Inject
    @Named(Application.CIRCUIT_BUILDER_FOR_MATCH)
    private IBuildCircuitStrategy circuitBuilder;
    @Inject
    private CompareFeatures comparator;
    @Inject()
    @Named(Application.SIMILARITY_THRESHOLD_FOR_MATCHER_KEY)
    private double similarityThreshold;
    @Inject()
    @Named(Application.MIN_SAMPLE_SIZE_KEY)
    private int minSampleSize;
    private FindAttributes attributeFinder = new FindAttributes();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();
    private CheckNodeProperties nodeChecker = new CheckNodeProperties();
    private Map<INode, INode> virtualTupleCache = new HashMap<INode, INode>();

    public MatchAttributes1to1StructuralAnalysis() {
    }

    public MatchAttributes1to1StructuralAnalysis(SampleInstances sampler, IBuildCircuitStrategy circuitBuilder, CompareFeatures comparator,
            double similarityThreshold, int minSampleSize) {
        this.sampler = sampler;
        this.circuitBuilder = circuitBuilder;
        this.comparator = comparator;
        this.similarityThreshold = similarityThreshold;
        this.minSampleSize = minSampleSize;
    }

    public void findMatches(MappingTask mappingTask) {
        AttributeCorrespondences candidateCorrespondences = findMatches(mappingTask.getSourceProxy().getDataSource(), mappingTask.getTargetProxy().getDataSource());
        mappingTask.setCandidateCorrespondences(candidateCorrespondences.getCorrespondences());
    }

    public AttributeCorrespondences findMatches(DataSource source, DataSource target) {
        AttributeCorrespondences correspondences = new AttributeCorrespondences(source, target);
        INode sourceSchemaClone = source.getSchema().clone();
        INode targetSchemaClone = target.getSchema().clone();
        sampler.sample(sourceSchemaClone, source.getInstances());
        sampler.sample(targetSchemaClone, target.getInstances());
        List<INode> sourceAttributes = attributeFinder.findAttributes(sourceSchemaClone);
        List<INode> targetAttributes = attributeFinder.findAttributes(targetSchemaClone);
        for (INode sourceAttribute : sourceAttributes) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing source node: " + sourceAttribute.getLabel());
            if (isToExclude(sourceAttribute, source)) {
                if (logger.isDebugEnabled()) logger.debug("Node excluded: " + sourceAttribute.getLabel());            
                continue;
            }
            INode sourceVirtualTuple = getVirtualTuple(sourceAttribute);
            PathExpression sourcePath = pathGenerator.generatePathFromRoot(sourceAttribute);
            if (logger.isDebugEnabled()) logger.debug("Attribute path: " + sourcePath);
            for (INode targetAttribute : targetAttributes) {
                if (logger.isDebugEnabled()) logger.debug("Analyzing target node: " + targetAttribute.getLabel());
                if (isToExclude(targetAttribute, target)) {
                    if (logger.isDebugEnabled()) logger.debug("Node excluded: " + targetAttribute.getLabel());            
                    continue;
                }
                INode targetVirtualTuple = getVirtualTuple(targetAttribute);
                PathExpression targetPath = pathGenerator.generatePathFromRoot(targetAttribute);
                if (logger.isDebugEnabled()) logger.debug("Attribute path: " + targetPath);
                double similarity = runStructuralAnalysis(sourceVirtualTuple, targetVirtualTuple);
                if (similarity > similarityThreshold) {
                    addCorrespondence(source, target, sourcePath, targetPath, similarity, correspondences);
                }
            }
        }
        return correspondences;
    }

    private boolean isToExclude(INode node, DataSource dataSource) {
        if (node.isExcluded()) {
            return true;
        }
        boolean underSampled = nodeChecker.isUndersampled(node, minSampleSize);
        if (underSampled) logger.info("Notice: node " + node.getLabel() + " is undersampled...");

        return node.isExcluded() || underSampled ||
                nodeChecker.isPrimaryKey(node, dataSource) || nodeChecker.isForeignKey(node, dataSource);

        // use the following code if you don't want to exclude primary keys and foreign keys
//        return node.isExcluded() || underSampled;
    }

    private INode getVirtualTuple(INode node) {
        INode virtualTuple = this.virtualTupleCache.get(node);
        if (virtualTuple == null) {
            virtualTuple = new TupleNode("virtual");
            virtualTuple.setRoot(true);
            virtualTuple.addChild(node.clone());
            virtualTupleCache.put(node, virtualTuple);
        }
        return virtualTuple;
    }

    private double runStructuralAnalysis(INode sourceSchema, INode targetSchema) {
        SimilarityCheck similarityCheck = new SimilarityCheck(sourceSchema, targetSchema);
        buildCircuits(similarityCheck);
        comparator.compare(similarityCheck, Application.getInstance().getComparatorChainForMatch());
        return similarityCheck.getQualityMeasure();
    }

    public void buildCircuits(SimilarityCheck similarityCheck) {
        INode firstSchema = similarityCheck.getFirstSchemaWithSamples();
        Circuit firstCircuit = circuitBuilder.buildAndSolveCircuit(firstSchema, similarityCheck, false);
        similarityCheck.addAnnotation(SpicyConstants.FIRST_CIRCUIT, firstCircuit);
        INode secondSchema = similarityCheck.getSecondSchemaWithSamples();
        Circuit secondCircuit = circuitBuilder.buildAndSolveCircuit(secondSchema, similarityCheck, false);
        similarityCheck.addAnnotation(SpicyConstants.SECOND_CIRCUIT, secondCircuit);
    }

    private void addCorrespondence(DataSource source, DataSource target,
            PathExpression sourcePath, PathExpression targetPath, double similarity, AttributeCorrespondences correspondences) {
        INode nodeInSource = nodeFinder.findNodeInSchema(sourcePath, new ConstantDataSourceProxy(source));
        PathExpression pathInSource = pathGenerator.generatePathFromRoot(nodeInSource);
        INode nodeInTarget = nodeFinder.findNodeInSchema(targetPath, new ConstantDataSourceProxy(target));
        PathExpression pathInTarget = pathGenerator.generatePathFromRoot(nodeInTarget);
        correspondences.addCorrespondence(pathInSource, pathInTarget, similarity);
    }

    public void clearCache() {
        this.virtualTupleCache.clear();
    }
}
