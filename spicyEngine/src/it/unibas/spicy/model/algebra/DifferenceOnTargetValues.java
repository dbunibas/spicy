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
 
package it.unibas.spicy.model.algebra;

import it.unibas.spicy.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DifferenceOnTargetValues extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(DifferenceOnTargetValues.class);

    protected List<VariableCorrespondence> leftCorrespondences;
    protected List<VariableCorrespondence> rightCorrespondences;

    public DifferenceOnTargetValues(List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        this.leftCorrespondences = leftCorrespondences;
        this.rightCorrespondences = rightCorrespondences;
    }

    public String getName() {
        return "difference-on-target-values";
    }

    public List<VariableCorrespondence> getLeftCorrespondences() {
        return leftCorrespondences;
    }

    public List<VariableCorrespondence> getRightCorrespondences() {
        return rightCorrespondences;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitDifferenceOnTargetValues(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (result != null) {
            if (logger.isDebugEnabled()) logger.debug("Difference on target values " + printIds() + " - Returning cached result...\n" + result);
            return result;
        }
        IDataSourceProxy leftChild = children.get(0).execute(dataSource);
        IDataSourceProxy rightChild = children.get(1).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Calculating difference " + printIds());
        if (logger.isDebugEnabled()) logger.debug("between:\n" + leftChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("and:\n" + rightChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("on:\n" + leftCorrespondences + "\n" + rightCorrespondences);
        IDataSourceProxy differenceResult = new DifferenceOnTargetValuesOperator().calculateDifference(leftChild, rightChild, leftCorrespondences, rightCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("Final result of difference on target values:\n" + differenceResult.toInstanceString());
        result = differenceResult;
        return differenceResult;
    }
}

class DifferenceOnTargetValuesOperator {

    private EvaluateExpression evaluator = new EvaluateExpression();

    IDataSourceProxy calculateDifference(IDataSourceProxy leftOperand, IDataSourceProxy rightOperand, List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(leftOperand);
        for (int i = 0; i < clone.getInstances().size(); i++) {
            INode leftInstance = clone.getInstances().get(i);
            INode rightInstance = rightOperand.getInstances().get(i);
            removeTuples(leftInstance, rightInstance, leftCorrespondences, rightCorrespondences);
        }
        return clone;
    }

    private void removeTuples(INode leftInstance, INode rightInstance, List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        for (Iterator<INode> leftTupleIterator = leftInstance.getChildren().iterator(); leftTupleIterator.hasNext();) {
            String tupleSummary = applyCorrespondencesAndGenerateSummary(leftTupleIterator.next(), leftCorrespondences);
            if (existTupleWithEqualSummary(tupleSummary, rightInstance, rightCorrespondences)) {
                leftTupleIterator.remove();
            }
        }
    }

    private String applyCorrespondencesAndGenerateSummary(INode tuple, List<VariableCorrespondence> correspondences) {
        StringBuilder summary = new StringBuilder("#");
        for (VariableCorrespondence correspondence : correspondences) {
            String targetValue = evaluator.evaluateFunction(correspondence.getTransformationFunction(), tuple).toString();
            summary.append(targetValue).append("#");
        }
        return summary.toString();
    }

    private boolean existTupleWithEqualSummary(String leftTupleSummary, INode rightInstance, List<VariableCorrespondence> rightCorrespondences) {
        for (INode rightTuple : rightInstance.getChildren()) {
            String rightTupleSummary = applyCorrespondencesAndGenerateSummary(rightTuple, rightCorrespondences);
            if (rightTupleSummary.equals(leftTupleSummary)) {
                return true;
            }
        }
        return false;
    }

//    private String generateSummary(INode tuple, List<VariableCorrespondence> correspondences) {
//        StringBuilder summary = new StringBuilder("#");
//        for (VariableCorrespondence correspondence : correspondences) {
//            String targetValue = AlgebraUtility.findAttributeValue(tuple, correspondence.getTargetPath().toString()).toString();
//            summary.append(targetValue).append("#");
//        }
//        return summary.toString();
//    }

}