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
package it.unibas.spicy.model.generators;

import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.expressions.operators.EvaluateExpression;
import it.unibas.spicy.model.mapping.operators.TGDToLogicalStringUtility;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionGenerator extends AbstractGenerator {

    protected Expression function;
    private Map<INode, Object> valueMap = new HashMap<INode, Object>();

    public FunctionGenerator(Expression function) {
        this.function = function.clone();
    }

    public Expression getFunction() {
        return function;
    }

    @SuppressWarnings("unchecked")
    public List<GeneratorWithPath> getSubGenerators() {
        return Collections.EMPTY_LIST;
    }

    public Object generateValue(TupleNode sourceTuple, MappingTask mappingTask) {
        EvaluateExpression evaluator = new EvaluateExpression();
        if (this.valueMap.get(sourceTuple) == null) {
            Object value = null;
            value = evaluator.evaluateFunction(function, sourceTuple);
            valueMap.put(sourceTuple, value);
        }
        return valueMap.get(sourceTuple);
    }

    public void setVariableDescriptions(List<FormulaVariable> formulaVariables) {
        if (this.variableDescriptionsSet) {
            return;
        }
        new TGDToLogicalStringUtility().setVariableDescriptionsForSkolems(this.function, formulaVariables);
        this.variableDescriptionsSet = true;
    }

    public IValueGenerator clone() {
        FunctionGenerator clone = (FunctionGenerator) super.clone();
        clone.function = this.function.clone();
        return clone;
    }

    public String toString() {
        String result = "";
        result += "[" + this.function + "]";
        return result;
    }
}
