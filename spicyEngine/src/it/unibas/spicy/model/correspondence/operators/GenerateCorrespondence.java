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
 
package it.unibas.spicy.model.correspondence.operators;

import it.unibas.spicy.model.correspondence.ConstantValue;
import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.exceptions.PathSyntaxException;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class GenerateCorrespondence {

    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public ValueCorrespondence generateCorrespondence(String sourcePathString, String targetPathString,
            IDataSourceProxy source, IDataSourceProxy target) {
        PathExpression sourcePath = pathGenerator.generatePathFromString(sourcePathString);
        checkAttributePath(sourcePath, source.getIntermediateSchema());
        PathExpression targetPath = pathGenerator.generatePathFromString(targetPathString);
        checkAttributePath(targetPath, target.getIntermediateSchema());
        return new ValueCorrespondence(sourcePath, targetPath);
    }

    public ValueCorrespondence generateCorrespondenceWithSourceValue(String sourceValue, String targetPathString,
            IDataSourceProxy source, IDataSourceProxy target) {
        PathExpression targetPath = pathGenerator.generatePathFromString(targetPathString);
        checkAttributePath(targetPath, target.getIntermediateSchema());
        return new ValueCorrespondence(new ConstantValue(sourceValue), targetPath);
    }

    public ValueCorrespondence generateCorrespondence(String sourcePathString, String targetPathString,
            IDataSourceProxy source, IDataSourceProxy target, double confidence) {
        ValueCorrespondence correspondence = generateCorrespondence(sourcePathString, targetPathString, source, target);
        correspondence.setConfidence(confidence);
        return correspondence;
    }

    public List<ValueCorrespondence> generateNewCorrespondences(List<ValueCorrespondence> correspondences, DataSource newSource, DataSource newTarget) {
        List<ValueCorrespondence> result = new ArrayList<ValueCorrespondence>();
        for (ValueCorrespondence correspondence : correspondences) {
            result.add(generateNewCorrespondence(correspondence, newSource, newTarget));
        }
        return result;
    }
    
    public ValueCorrespondence generateNewCorrespondence(ValueCorrespondence correspondence, DataSource newSource, DataSource newTarget) {
        List<PathExpression> newSourcePaths = null;
        if (correspondence.getSourcePaths() != null) {
            newSourcePaths = new ArrayList<PathExpression>();
            for (PathExpression sourcePath : correspondence.getSourcePaths()) {
                PathExpression newSourcePath = pathGenerator.generatePathFromString(sourcePath.toString());
                newSourcePaths.add(newSourcePath);
            }
        }
        ISourceValue newSourceValue = null;
        if (correspondence.getSourceValue() != null) {
            newSourceValue = (ISourceValue) correspondence.getSourceValue().clone();
        }
        PathExpression newTargetPath = pathGenerator.generatePathFromString(correspondence.getTargetPath().toString());
        Expression newTransformationFunction = new Expression(correspondence.getTransformationFunction().toString());
        ValueCorrespondence newCorrespondence = new ValueCorrespondence(newSourcePaths, newSourceValue, newTargetPath, newTransformationFunction, correspondence.getConfidence());
        return newCorrespondence;
    }

    private void checkAttributePath(PathExpression path, INode root) {
        if (!(path.getLastNode(root) instanceof AttributeNode)) {
            throw new PathSyntaxException("Path does not reach an attribute: " + path);
        }
    }

    public VariableCorrespondence checkCorrespondences(FORule tgd, VariableCorrespondence newCorrespondence) {
        if (!SpicyEngineUtility.containsVariableWithSameId(tgd.getTargetView().getGenerators(), newCorrespondence.getTargetPath().getStartingVariable())) {
            throw new IllegalMappingTaskException("Target path for correspondence: " + newCorrespondence + "\ndoes not belong to target view:\n" + tgd);
        }
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(newCorrespondence.getTargetPath())) {
                return correspondence;
            }
        }
        return null;
    }
}


