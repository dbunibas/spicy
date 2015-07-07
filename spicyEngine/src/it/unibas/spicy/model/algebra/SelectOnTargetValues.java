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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.persistence.Types;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectOnTargetValues extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(SelectOnTargetValues.class);

    private List<VariableSelectionCondition> conditions;
    private List<VariableCorrespondence> correspondences;

    public SelectOnTargetValues(VariableSelectionCondition condition, List<VariableCorrespondence> correspondences) {
        this.conditions = new ArrayList<VariableSelectionCondition>();
        this.conditions.add(condition);
        this.correspondences = correspondences;
    }

    public SelectOnTargetValues(List<VariableSelectionCondition> conditions, List<VariableCorrespondence> correspondences) {
        this.conditions = conditions;
        this.correspondences = correspondences;
    }

    public String getName() {
        return "select-on-target-values";
    }

    public List<VariableSelectionCondition> getConditions() {
        return conditions;
    }

    public List<VariableCorrespondence> getCorrespondences() {
        return correspondences;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelectOnTargetValues(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Select on target values " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Starting selection on target values" + printIds());
        if (logger.isDebugEnabled()) logger.debug("with condition :" + conditions);
        if (logger.isDebugEnabled()) logger.debug("and correspondences :" + correspondences);
        if (logger.isDebugEnabled()) logger.debug("of data source :" + child.toInstanceString());
        result = new SelectOnTargetValuesOperator().selectTuples(child, conditions, correspondences);
        if (logger.isDebugEnabled()) logger.debug("Final result of selection on target values:\n" + result.toInstanceString());
        return result;
    }
}

class SelectOnTargetValuesOperator {

    private static Log logger = LogFactory.getLog(SelectOperator.class);

    private IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
    private EvaluateExpression evaluator = new EvaluateExpression();

    IDataSourceProxy selectTuples(IDataSourceProxy dataSource, List<VariableSelectionCondition> conditions, List<VariableCorrespondence> correspondences) {
        if (logger.isDebugEnabled()) logger.trace("Evaluating select on target values with: " + conditions + " on data source\n" + dataSource.toInstanceString());
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(dataSource);
        for (INode instance : clone.getInstances()) {
            removeTuples(instance, conditions, correspondences);
        }
        if (logger.isDebugEnabled()) logger.trace("Final result of select: " + clone.toInstanceString());
        return clone;
    }

    private void removeTuples(INode instance, List<VariableSelectionCondition> conditions, List<VariableCorrespondence> correspondences) {
        for (Iterator<INode> tupleIt = instance.getChildren().iterator(); tupleIt.hasNext();) {
            INode tuple = tupleIt.next();
            if (conditionsAreFalse(tuple, conditions, correspondences)) {
                tupleIt.remove();
            }
        }
    }

    private boolean conditionsAreFalse(INode tuple, List<VariableSelectionCondition> conditions, List<VariableCorrespondence> correspondences) {
        if (logger.isDebugEnabled()) logger.trace("Evaluating conditions: " + conditions + " on tuple\n" + tuple);
        INode targetTuple = generateTuple(tuple, correspondences);
        for (VariableSelectionCondition condition : conditions) {
            Expression filter = condition.getCondition();
            if (evaluator.evaluateCondition(filter, targetTuple) != SpicyEngineConstants.TRUE) {
                if (logger.isDebugEnabled()) logger.trace("Condition is false");
                return true;
            }
        }
        return false;
    }

    private INode generateTuple(INode sourceTuple, List<VariableCorrespondence> correspondences) {
        TupleNode tupleNode = new TupleNode(SpicyEngineUtility.generateTupleNodeLabel(), oidGenerator.getNextOID());
        for (VariableCorrespondence correspondence : correspondences) {
            String targetValue = evaluator.evaluateFunction(correspondence.getTransformationFunction(), sourceTuple).toString();
            AttributeNode attributeNode = new AttributeNode(correspondence.getTargetPath().toString(), oidGenerator.getNextOID());
            LeafNode leafNode = new LeafNode(Types.ANY, targetValue);
            attributeNode.addChild(leafNode);
            tupleNode.addChild(attributeNode);
        }
        return tupleNode;
    }

}