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
package it.unibas.spicy.model.paths;

import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;

public class VariableCorrespondence implements Cloneable {

    private static Log logger = LogFactory.getLog(VariableCorrespondence.class);
    private List<VariablePathExpression> sourcePaths;
    private ISourceValue sourceValue;
    private VariablePathExpression targetPath;
    private Expression transformationFunction;

    public VariableCorrespondence(List<VariablePathExpression> sourcePaths, ISourceValue sourceValue,
            VariablePathExpression targetPath, Expression transformationFunction) {
        this.sourcePaths = sourcePaths;
        this.sourceValue = sourceValue;
        this.targetPath = targetPath;
        this.transformationFunction = transformationFunction;
    }

    public VariableCorrespondence(VariablePathExpression sourcePath, VariablePathExpression targetPath) {
        this(generateList(sourcePath), null, targetPath, generateTranformationFunction(sourcePath));
    }

    public VariableCorrespondence(VariableCorrespondence oldCorrespondence, VariablePathExpression targetPath) {
        this.sourcePaths = oldCorrespondence.sourcePaths;
        this.sourceValue = oldCorrespondence.sourceValue;
        this.targetPath = targetPath;
        this.transformationFunction = oldCorrespondence.transformationFunction;
    }

    public boolean isConstant() {
        return this.sourceValue != null;
    }

    public boolean isValueCopy() {
        return (this.sourcePaths != null && this.sourcePaths.size() == 1 && this.transformationFunction.toString().equals(sourcePaths.get(0).toString()));
    }

    public List<VariablePathExpression> getSourcePaths() {
        return (List<VariablePathExpression>) sourcePaths;
    }

    public void setSourcePaths(List<VariablePathExpression> sourcePaths) {
        this.sourcePaths = sourcePaths;
    }

    public VariablePathExpression getFirstSourcePath() {
        return sourcePaths.get(0);
    }

    public VariablePathExpression getTargetPath() {
        return (VariablePathExpression) targetPath;
    }

    public void setTargetPath(VariablePathExpression targetPath) {
        this.targetPath = targetPath;
    }

    public Expression getTransformationFunction() {
        return transformationFunction;
    }

    public ISourceValue getSourceValue() {
        return sourceValue;
    }

    public VariableCorrespondence clone() {
        try {
            VariableCorrespondence clone = (VariableCorrespondence) super.clone();
            if (sourcePaths != null) {
                clone.sourcePaths = new ArrayList<VariablePathExpression>();
                for (VariablePathExpression sourcePath : sourcePaths) {
                    clone.sourcePaths.add(sourcePath.clone());
                }
            }
            if (sourceValue != null) {
                clone.sourceValue = sourceValue.clone();
            }
            clone.targetPath = targetPath.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VariableCorrespondence)) {
            return false;
        }
        VariableCorrespondence correspondence = (VariableCorrespondence) obj;
        boolean equalTarget = this.targetPath.equals(correspondence.targetPath);
        boolean equalSource = false;
        if (this.sourcePaths != null && correspondence.getSourcePaths() != null) {
            equalSource = SpicyEngineUtility.equalLists(this.sourcePaths, correspondence.getSourcePaths());
        } else if (this.sourceValue != null && correspondence.getSourceValue() != null) {
            equalSource = this.sourceValue.equals(correspondence.sourceValue);
        }
        boolean equalExpression = true;
        if (!correspondence.isValueCopy()) {
            equalExpression = this.transformationFunction.equals(correspondence.getTransformationFunction());
        }
        return equalTarget && equalSource && equalExpression;
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        if (this.sourcePaths != null && this.sourcePaths.size() == 1) {
            result.append(this.sourcePaths.get(0).toStringWithSet());
        } else {
            result.append(this.transformationFunction);
        }
        result.append(" --> ").append(this.targetPath.toStringWithSet());
        result.append("]");
        return result.toString();
    }

    protected static List<VariablePathExpression> generateList(VariablePathExpression path) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        result.add(path);
        return result;
    }

    protected static Expression generateTranformationFunction(VariablePathExpression sourcePath) {
        Expression expression = new Expression(sourcePath.toString());
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (org.nfunk.jep.Variable variable : symbolTable.getVariables()) {
            variable.setDescription(sourcePath);
            variable.setOriginalDescription(sourcePath.getAbsolutePath());
        }
        return expression;
    }
}
