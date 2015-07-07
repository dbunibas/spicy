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
 
package it.unibas.spicy.findmappings.operators;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.correspondence.operators.GenerateCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import it.unibas.spicy.structuralanalysis.strategies.PerformStructuralAnalysis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RankTransformations {

    private static Log logger = LogFactory.getLog(RankTransformations.class);
    @Inject()
    private SampleInstances sampler;
    @Inject() @Named(Application.CIRCUIT_BUILDER_FOR_COMPARE)
    private IBuildCircuitStrategy circuitBuilder;
    @Inject()
    private CompareFeatures comparator;    

    private PerformStructuralAnalysis analyzer;
//    private PartiallyCloneDataSource cloner = new PartiallyCloneDataSource();
    private GenerateCorrespondence correspondenceGenerator = new GenerateCorrespondence();
    
    public RankTransformations() {}

    public RankTransformations(SampleInstances sampler, IBuildCircuitStrategy circuitBuilder, CompareFeatures comparator) {
        this.sampler = sampler;
        this.circuitBuilder = circuitBuilder;
        this.comparator = comparator;
    }
        
    public List<Transformation> rankTransformations(MappingTask mappingTask) {
        analyzer = new PerformStructuralAnalysis(sampler, circuitBuilder, comparator);
        DataSource source = mappingTask.getSourceProxy().getDataSource();
//        SchemaMapperUtility.removeNestAnnotation(source);
        DataSource target = mappingTask.getTargetProxy().getDataSource();
//        DataSource newTarget = cloner.partiallyClone(target);
        DataSource newTarget = target;
        List<ValueCorrespondence> newCorrespondences = correspondenceGenerator.generateNewCorrespondences(mappingTask.getValueCorrespondences(), source, newTarget);
        ExcludeNonGeneratedNodesVisitor excludeVisitor = new ExcludeNonGeneratedNodesVisitor(mappingTask.getValueCorrespondences());
        newTarget.getSchema().accept(excludeVisitor);
        newTarget.getSchema().removeAnnotation(SpicyConstants.SAMPLED);
        MappingTask newMappingTask = new MappingTask(source, newTarget, newCorrespondences);
        AnnotatedMappingTask annotatedMappingTask = new AnnotatedMappingTask(newMappingTask);
        analyzer.runAnalisys(annotatedMappingTask);
        List<Transformation> result = new ArrayList<Transformation>();
        List<SimilarityCheck> similarityChecks = (List<SimilarityCheck>) annotatedMappingTask.getSimilarityChecks();
        Collections.sort(similarityChecks);
        for (SimilarityCheck similarityCheck : similarityChecks) {
            Transformation transformation = (Transformation) similarityCheck.getAnnotation(SpicyConstants.TRANSFORMATION);
            transformation.addAnnotation(SpicyConstants.SIMILARITY_CHECK, similarityCheck);
            result.add(transformation);
        }
        return result;
    }
}

class ExcludeNonGeneratedNodesVisitor implements INodeVisitor {

    private List<ValueCorrespondence> correspondences;
    private GeneratePathExpression pathFinder = new GeneratePathExpression();
    
    public ExcludeNonGeneratedNodesVisitor(List<ValueCorrespondence> correspondences) {
        this.correspondences = correspondences;
    }
    
    public void visitSetNode(SetNode node) {
        visitIntermediateNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitIntermediateNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitIntermediateNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitIntermediateNode(node);
    }
    
    private void visitIntermediateNode(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
        if (allChildrenExcluded(node)) {
            node.setExcluded(true);
        }
    }
    
    private boolean allChildrenExcluded(INode node) {
        for (INode child : node.getChildren()) {
            if (!child.isExcluded()) {
                return false;
            }
        }
        return true;
    }

    public void visitAttributeNode(AttributeNode node) {
        PathExpression attributePath = pathFinder.generatePathFromRoot(node);
        if (!(isGenerated(attributePath))) {
            node.setExcluded(true);
        }
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    private boolean isGenerated(PathExpression attributePath) {
        for (ValueCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equals(attributePath)) {
                return true;
            }
        }
        return false;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public Object getResult() {
        return null;
    }
    
}