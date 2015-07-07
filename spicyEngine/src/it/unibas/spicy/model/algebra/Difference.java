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
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Difference extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(Difference.class);

    protected List<VariablePathExpression> leftPaths;
    protected List<VariablePathExpression> rightPaths;

    public Difference(List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
    }

    public String getName() {
        return "difference-on-target-values";
    }

    public List<VariablePathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<VariablePathExpression> getRightPaths() {
        return rightPaths;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitDifference(this);
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
        if (logger.isDebugEnabled()) logger.debug("on:\n" + leftPaths + "\n" + rightPaths);
        IDataSourceProxy differenceResult = new DifferenceOnSourceValuesOperator().calculateDifference(leftChild, rightChild, leftPaths, rightPaths);
        if (logger.isDebugEnabled()) logger.debug("Final result of difference on target values:\n" + differenceResult.toInstanceString());
        result = differenceResult;
        return differenceResult;
    }
}

class DifferenceOnSourceValuesOperator {

    IDataSourceProxy calculateDifference(IDataSourceProxy leftOperand, IDataSourceProxy rightOperand, List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(leftOperand);
        for (int i = 0; i < clone.getInstances().size(); i++) {
            INode leftInstance = clone.getInstances().get(i);
            INode rightInstance = rightOperand.getInstances().get(i);
            removeTuples(leftInstance, rightInstance, leftPaths, rightPaths);
        }
        return clone;
    }

    private void removeTuples(INode leftInstance, INode rightInstance, List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        for (Iterator<INode> leftTupleIterator = leftInstance.getChildren().iterator(); leftTupleIterator.hasNext();) {
            String tupleSummary = generateSummary(leftTupleIterator.next(), leftPaths);
            if (existTupleWithEqualSummary(tupleSummary, rightInstance, rightPaths)) {
                leftTupleIterator.remove();
            }
        }
    }

    private String generateSummary(INode tuple, List<VariablePathExpression> paths) {
        StringBuilder summary = new StringBuilder("#");
        for (VariablePathExpression path : paths) {
            String value = SpicyEngineUtility.findAttributeValue(tuple, path.toString()).toString();
            summary.append(value).append("#");
        }
        return summary.toString();
    }

    private boolean existTupleWithEqualSummary(String leftTupleSummary, INode rightInstance, List<VariablePathExpression> rightPaths) {
        for (INode rightTuple : rightInstance.getChildren()) {
            if (generateSummary(rightTuple, rightPaths).equals(leftTupleSummary)) {
                return true;
            }
        }
        return false;
    }

}