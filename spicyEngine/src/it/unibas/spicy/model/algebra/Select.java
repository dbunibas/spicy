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
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Select extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(Select.class);

    private List<VariableSelectionCondition> conditions;

    public Select(VariableSelectionCondition condition) {
        this.conditions = new ArrayList<VariableSelectionCondition>();
        this.conditions.add(condition);
    }

    public Select(List<VariableSelectionCondition> conditions) {
        this.conditions = conditions;
    }

    public String getName() {
        return "select";
    }

    public List<VariableSelectionCondition> getConditions() {
        return conditions;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelect(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Select " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Starting selection " + printIds());
        if (logger.isDebugEnabled()) logger.debug("with condition :" + conditions);
        if (logger.isDebugEnabled()) logger.debug("of data source :" + child.toInstanceString());
        result = new SelectOperator().selectTuples(child, conditions);
        if (logger.isDebugEnabled()) logger.debug("Final result of select:\n" + result.toInstanceString());
        return result;
    }
}

class SelectOperator {

    private static Log logger = LogFactory.getLog(SelectOperator.class);

    private EvaluateExpression expressionEvaluator = new EvaluateExpression();

    IDataSourceProxy selectTuples(IDataSourceProxy dataSource, List<VariableSelectionCondition> conditions) {
        if (logger.isDebugEnabled()) logger.trace("Evaluating select with: " + conditions + " on data source\n" + dataSource.toInstanceString());
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(dataSource);
        for (INode instance : clone.getInstances()) {
            removeTuples(instance, conditions);
        }
        if (logger.isDebugEnabled()) logger.trace("Final result of select: " + clone.toInstanceString());
        return clone;
    }

    private void removeTuples(INode instance, List<VariableSelectionCondition> conditions) {
        for (Iterator<INode> tupleIt = instance.getChildren().iterator(); tupleIt.hasNext();) {
            if (conditionsAreFalse(tupleIt.next(), conditions)) {
                tupleIt.remove();
            }
        }
    }

    private boolean conditionsAreFalse(INode tuple, List<VariableSelectionCondition> conditions) {
        if (logger.isDebugEnabled()) logger.trace("Evaluating conditions: " + conditions + " on tuple\n" + tuple);
        for (VariableSelectionCondition condition : conditions) {
            Expression filter = condition.getCondition();
            if (expressionEvaluator.evaluateCondition(filter, tuple) != SpicyEngineConstants.TRUE) {
                if (logger.isDebugEnabled()) logger.trace("Condition is false");
                return true;
            }
        }
        return false;
    }
}