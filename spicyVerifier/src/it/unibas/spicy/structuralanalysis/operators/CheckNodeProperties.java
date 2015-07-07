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
 
package it.unibas.spicy.structuralanalysis.operators;

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.values.INullValue;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.sampling.SampleValue;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckNodeProperties {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    
    public boolean isPrimaryKey(INode node, DataSource dataSource) {
        PathExpression pathToNode = pathGenerator.generatePathFromRoot(node);
        return isPrimaryKey(pathToNode, dataSource);
    }
    
    public boolean isPrimaryKey(PathExpression pathToNode, DataSource dataSource) {
        List<KeyConstraint> constraints = dataSource.getKeyConstraints();
        for (KeyConstraint keyConstraint : constraints) {
            if (keyConstraint.getKeyPaths().contains(pathToNode)) {
                if (logger.isDebugEnabled()) logger.debug("Attribute is primary key: " + pathToNode);
                return true;
            }
        }
        return false;
    }
    
    public boolean isForeignKey(INode node, DataSource dataSource) {
        PathExpression pathToNode = pathGenerator.generatePathFromRoot(node);
        return isForeignKey(pathToNode, dataSource);
    }

    public boolean isForeignKey(PathExpression pathToNode, DataSource dataSource) {
        List<ForeignKeyConstraint> constraints = dataSource.getForeignKeyConstraints();
        for (ForeignKeyConstraint foreignKeyConstraint : constraints) {
            if (foreignKeyConstraint.getForeignKeyPaths().contains(pathToNode)) {
                if (logger.isDebugEnabled()) logger.debug("Attribute is foreign key: " + pathToNode);
                return true;
            }
        }
        return false;
    }
    
    public boolean isTargetForCorrespondence(INode node, List<ValueCorrespondence> correspondences) {
        PathExpression pathToNode = pathGenerator.generatePathFromRoot(node);
        return isTargetForCorrespondence(pathToNode, correspondences);
    }

    public boolean isTargetForCorrespondence(PathExpression pathToNode, List<ValueCorrespondence> correspondences) {
        for (ValueCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equals(pathToNode)) {
                if (logger.isDebugEnabled()) logger.debug("Attribute is generated by a value correspondence");
                return true;
            }
        }
        return false;
    }
    
    public boolean isUndersampled(INode node, int minSampleSize) {
        int numberOfSamples = getNumberOfNonNullSamples(node);
        if (logger.isDebugEnabled()) logger.debug("Number of samples = " + numberOfSamples + " - minSamples = " + minSampleSize);
        boolean result = numberOfSamples < minSampleSize;
        if (logger.isDebugEnabled()) logger.debug("Check samples: #ofNonNullSamples=" + numberOfSamples + " minSampleSize=" + minSampleSize + " - undersampled=" + result);
        return result;
    }
    
    public int getSampleSize(INode node) {
        List<SampleValue> samples = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        assert(samples != null) : "Missing samples in attribute: " + node.getLabel();
        return samples.size();
    }

    public int getNumberOfNonNullSamples(INode node) {
        List<SampleValue> samples = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        assert(samples != null) : "Missing samples in attribute: " + node.getLabel();
        int counter = 0;
        for (SampleValue sampleValue : samples) {
            if (!(sampleValue.getValue() instanceof INullValue)) {
                counter++;
            }
        }
        return counter;
    }

    public int getNumberOfNulls(INode node) {
        List<SampleValue> samples = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        int counter = 0;
        for (SampleValue sampleValue : samples) {
            if ((sampleValue.getValue() instanceof INullValue)) {
                counter++;
            }
        }
        return counter;
    }

    public int getNumberOfGeneratedNulls(INode node) {
        List<SampleValue> samples = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        int counter = 0;
        for (SampleValue sampleValue : samples) {
            if ((sampleValue.getValue() instanceof INullValue) && ((INullValue)sampleValue.getValue()).isGenerated()) {
                counter++;
            }
        }
        return counter;
    }
}
