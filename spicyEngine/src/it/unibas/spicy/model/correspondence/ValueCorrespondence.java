/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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
 
package it.unibas.spicy.model.correspondence;

import it.unibas.spicy.model.expressions.operators.CheckExpressions;
import it.unibas.spicy.utility.SpicyEngineUtility;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValueCorrespondence implements Comparable<ValueCorrespondence> {

    private static Log logger = LogFactory.getLog(ValueCorrespondence.class);
    
    private List<PathExpression> sourcePaths = new ArrayList<PathExpression>();
    private ISourceValue sourceValue;
    private PathExpression targetPath;
    private Expression transformationFunction;
    private double confidence;

    public ValueCorrespondence(PathExpression sourcePath, PathExpression targetPath) {
        this(generateList(sourcePath), null, targetPath, generateExpression(sourcePath), 1.0);
    }

    public ValueCorrespondence(PathExpression sourcePath, PathExpression targetPath, double confidence) {
        this(generateList(sourcePath), null, targetPath, generateExpression(sourcePath), confidence);
    }
    
    public ValueCorrespondence(List<PathExpression> sourcePaths, PathExpression targetPath, Expression transformationFunction) {
        this(sourcePaths, null, targetPath, transformationFunction, 1.0);
    }

    public ValueCorrespondence(List<PathExpression> sourcePaths, PathExpression targetPath, Expression transformationFunction, double confidence) {
        this(sourcePaths, null, targetPath, transformationFunction, confidence);
    }

    public ValueCorrespondence(ISourceValue sourceValue, PathExpression targetPath) {
        this(null, sourceValue, targetPath, generateExpression(sourceValue), 1.0);
    }

    public ValueCorrespondence(ISourceValue sourceValue, PathExpression targetPath, double confidence) {
        this(null, sourceValue, targetPath, generateExpression(sourceValue), confidence);
    }

    public ValueCorrespondence(ValueCorrespondence correspondence) {
        this(correspondence.getSourcePaths(), correspondence.getSourceValue(), correspondence.getTargetPath(), correspondence.getTransformationFunction(), correspondence.getConfidence());
    }

    public ValueCorrespondence(List<PathExpression> sourcePaths, ISourceValue sourceValue, PathExpression targetPath, Expression transformationFunction, double confidence) {
        assert ((sourcePaths != null && sourceValue == null) || (sourcePaths == null && sourceValue != null)) : "Either source paths or source value are allowed: " + sourcePaths + " - " + sourceValue;
        this.sourcePaths = sourcePaths;
        this.sourceValue = sourceValue;
        this.targetPath = targetPath;
        checkTransformationFunction(sourcePaths, sourceValue, transformationFunction);
        this.transformationFunction = transformationFunction;
        this.confidence = confidence;
    }

    public boolean isValueCopy() {
        return (this.sourcePaths != null && this.sourcePaths.size() == 1 && this.transformationFunction.toString().equals(sourcePaths.get(0).toString()));
    }

    public List<PathExpression> getSourcePaths() {
        return sourcePaths;
    }

    public PathExpression getTargetPath() {
        return targetPath;
    }

    public Expression getTransformationFunction() {
        return transformationFunction;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public ISourceValue getSourceValue() {
        return sourceValue;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean hasEqualPaths(ValueCorrespondence c2) {
        return (SpicyEngineUtility.equalLists(this.getSourcePaths(), c2.getSourcePaths()) &&
                this.getTargetPath().equals(c2.getTargetPath()));
    }

    public boolean hasEqualTargetPath(ValueCorrespondence c2) {
        return (this.getTargetPath().equals(c2.getTargetPath()));
    }

    public boolean hasEqualSourcePaths(ValueCorrespondence c2) {
        return (SpicyEngineUtility.equalLists(this.getSourcePaths(), c2.getSourcePaths()));
    }

    public int compareTo(ValueCorrespondence other) {
        double difference = this.confidence - other.confidence;
        if (difference < 0.0) {
            return 1;
        } else if (difference > 0.0) {
            return -1;
        }
        return 0;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public String toString() {
        StringBuilder result = new StringBuilder("[");
        result.append(this.transformationFunction).append(" --> ").append(this.targetPath);
        if (confidence != 1.0) {
            result.append(" (Confidence: ").append(confidence).append(")");
        }
        result.append("]");
        return result.toString();
    }

    private static List<PathExpression> generateList(PathExpression pathExpression) {
        List<PathExpression> list = new ArrayList<PathExpression>();
        list.add(pathExpression);
        return list;
    }

    private static Expression generateExpression(PathExpression sourcePath) {
        return new Expression(sourcePath.toString());
    }

    private static Expression generateExpression(ISourceValue sourceValue) {
        CheckExpressions checker = new CheckExpressions();
        return checker.checkSourceValueAndGenerateExpression(sourceValue);
    }

    private void checkTransformationFunction(List<PathExpression> sourcePaths, ISourceValue sourceValue, Expression transformationFunction) {
        CheckExpressions functionChecker = new CheckExpressions();
        if (sourcePaths != null) {
            functionChecker.checkExpression(sourcePaths, transformationFunction);
        } else {
            functionChecker.checkTransformationFunction(sourceValue, transformationFunction);
        }
    }
}
