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
 
package it.unibas.spicy.structuralanalysis.circuits.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.structuralanalysis.operators.CheckNodeProperties;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.EmptyCircuitException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindNodesUndersampledConstraints implements IFindNodesToExclude {
    
    @Inject() @Named(Application.MIN_SAMPLE_SIZE_KEY)
    private int minSampleSize;
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public FindNodesUndersampledConstraints() {}
    
    public FindNodesUndersampledConstraints(int minSampleSize) {
        this.minSampleSize = minSampleSize;
    }
    
    public List<PathExpression> findCommonAttributesToExclude(SimilarityCheck similarityCheck) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        INode firstSchemaWithSamples = similarityCheck.getFirstSchemaWithSamples();
        add(result, findAttributesToExclude(firstSchemaWithSamples, similarityCheck));
        INode secondSchemaWithSamples = similarityCheck.getSecondSchemaWithSamples();
        add(result, findAttributesToExclude(secondSchemaWithSamples, similarityCheck));
        return result;
    }
    
    public List<PathExpression> findAttributesToExclude(INode schemaWithSamples, SimilarityCheck similarityCheck) {
        MappingTask mappingTask = (MappingTask)similarityCheck.getAnnotation(SpicyConstants.MAPPING_TASK);
        //TODO: check
        if (mappingTask == null) {
            return Collections.EMPTY_LIST;
        }
        DataSource target = mappingTask.getTargetProxy().getDataSource();
        FindUndersampledConstraintsNodesVisitor visitor = new FindUndersampledConstraintsNodesVisitor(target, minSampleSize);
        schemaWithSamples.accept(visitor);
        List<PathExpression> exclusionList = visitor.getResult();
        checkEmptyCircuits(schemaWithSamples, exclusionList);
        return exclusionList;
    }
    
    private void add(List<PathExpression> result, List<PathExpression> list) {
        for (PathExpression pathExpression : list) {
            if (!result.contains(pathExpression)) {
                result.add(pathExpression);
            }
        }
    }
    
    private void checkEmptyCircuits(INode schemaRoot, List<PathExpression> exclusionList) {
        PathExpression rootPath = pathGenerator.generatePathFromRoot(schemaRoot);
        if (exclusionList.contains(rootPath)) {
            throw new EmptyCircuitException("FindNodesUndersampledConstraints - MinSampleSize = " + minSampleSize + " - Cirtuit is empty: " + schemaRoot + " - Exclusion list: " + exclusionList);
        }
    }
    
}

class FindUndersampledConstraintsNodesVisitor implements INodeVisitor {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private DataSource dataSource;
    private int minSampleSize;
    
    private List<PathExpression> result = new ArrayList<PathExpression>();
    
    private GeneratePathExpression pathFinder = new GeneratePathExpression();
    private CheckNodeProperties nodeChecker = new CheckNodeProperties();
    
    public FindUndersampledConstraintsNodesVisitor(DataSource dataSource, int minSampleSize) {
        this.dataSource = dataSource;
        this.minSampleSize = minSampleSize;
    }
    
    public void visitSetNode(SetNode node) {
        visitNode(node);
    }
    
    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }
    
    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }
    
    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }
    
    private void visitNode(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
        if (allChildrenRemoved(node)) {
            PathExpression pathToNode = pathFinder.generatePathFromRoot(node);
            result.add(pathToNode);
        }
    }
    
    private boolean allChildrenRemoved(INode node) {
        for (INode child : node.getChildren()) {
            PathExpression pathToChild = pathFinder.generatePathFromRoot(child);
            if (!result.contains(pathToChild)) {
                return false;
            }
        }
        return true;
    }
    
    public void visitAttributeNode(AttributeNode node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting attribute: " + node.getLabel());
        PathExpression pathToNode = pathFinder.generatePathFromRoot(node);
        boolean toExclude = false;
        toExclude = isToExclude(node, dataSource);
        if (toExclude) {
            result.add(pathToNode);
        }
    }
    
    private boolean isToExclude(INode node, DataSource dataSource) {
        return node.isExcluded() || nodeChecker.isUndersampled(node, minSampleSize) ||
                nodeChecker.isPrimaryKey(node, dataSource) || nodeChecker.isForeignKey(node, dataSource);
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }
    
    public void visitLeafNode(LeafNode leafNode) {
        return;
    }
    
    public List<PathExpression> getResult() {
        return result;
    }
    
    
}