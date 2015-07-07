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
 
package it.unibas.spicy.structuralanalysis.sampling.operators;

import com.google.inject.Inject;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateConsistencyStrategy;
import it.unibas.spicy.structuralanalysis.sampling.SampleValue;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateStressStrategy;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SampleInstances {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    public final static int MAX_SAMPLE_SIZE = 100;
    
    private IGenerateStressStrategy stressGenerator;
    private IGenerateConsistencyStrategy consistencyGenerator;
    
    @Inject()
    public SampleInstances(IGenerateStressStrategy stressGenerator, IGenerateConsistencyStrategy consistencyGenerator) {
        this.stressGenerator = stressGenerator;
        this.consistencyGenerator = consistencyGenerator;
    }
    
    public void sample(INode schema, List<INode> instances) {
        if (schema.getAnnotation(SpicyConstants.SAMPLED) != null) {
            if (logger.isDebugEnabled()) logger.debug("Schema already sampled, skipping...");
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Sampling schema...");
        SampleVisitor visitor = new SampleVisitor(instances, stressGenerator, consistencyGenerator);
        schema.accept(visitor);
    }
    
    public void sample(INode schema, INode instance) {
        List<INode> instances = new ArrayList<INode>();
        instances.add(instance);
        sample(schema, instances);
    }
    
    public void sample(DataSource dataSource) {
        sample(dataSource.getSchema(), dataSource.getInstances());
    }
}

class SampleVisitor implements INodeVisitor {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private List<INode> instances;
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();
    
    private IGenerateStressStrategy stressGenerator;
    private IGenerateConsistencyStrategy consistencyGenerator;
    
    public SampleVisitor(List<INode> instances, IGenerateStressStrategy stressGenerator, IGenerateConsistencyStrategy consistencyGenerator) {
        this.instances = instances;
        this.stressGenerator = stressGenerator;
        this.consistencyGenerator = consistencyGenerator;
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
    
    public void visitLeafNode(LeafNode node) {
        return;
    }
    
    private void visitNode(INode node) {
        node.addAnnotation(SpicyConstants.SAMPLED, Boolean.TRUE);
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }
    
    public void visitAttributeNode(AttributeNode node) {
        if (node.isExcluded()) {
            return;
        }
        List<SampleValue> values = sampleInstances(node);
        node.addAnnotation(SpicyConstants.SAMPLES, values);
        if (values.size() != 0) {
            this.stressGenerator.generateStress(node);
            this.consistencyGenerator.generateConsistency(node);
        }
    }
    
    private List<SampleValue> sampleInstances(AttributeNode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        List<SampleValue> values = new ArrayList<SampleValue>();
        for (INode instance : this.instances) {
            List<INode> attributeInstances = nodeFinder.findNodesInInstance(nodePath, instance, SampleInstances.MAX_SAMPLE_SIZE);
            for (INode attributeInstance : attributeInstances) {
                Object value = attributeInstance.getChild(0).getValue();
                values.add(new SampleValue(value));
            }
        }
        return values;
    }
    
    public Object getResult() {
        return null;
    }
    
    
}