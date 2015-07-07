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
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariableProvenanceCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectProvenance extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(SelectProvenance.class);
    private VariableProvenanceCondition condition;

    public SelectProvenance(VariableProvenanceCondition condition) {
        this.condition = condition;
    }

    public String getName() {
        return "select-provenance";
    }

    public VariableProvenanceCondition getCondition() {
        return condition;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelectProvenance(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Select provenance " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        result = new SelectProvenanceOperator().selectTuples(child, condition);
        return result;
    }
}

class SelectProvenanceOperator {

    private static Log logger = LogFactory.getLog(SelectProvenanceOperator.class);

    IDataSourceProxy selectTuples(IDataSourceProxy dataSource, VariableProvenanceCondition condition) {
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(dataSource);
        for (INode instance : clone.getInstances()) {
            removeTuples(instance, condition);
        }
        if (logger.isDebugEnabled()) logger.debug("Evaluating provenance: " + condition + " on data source\n" + dataSource.toInstanceString() + " - Final result: " + clone.toInstanceString());
        return clone;
    }

    private void removeTuples(INode instance, VariableProvenanceCondition condition) {
        for (Iterator<INode> tupleIt = instance.getChildren().iterator(); tupleIt.hasNext();) {
            if (wrongProvenance((TupleNode) tupleIt.next(), condition)) {
                tupleIt.remove();
            }
        }
    }

    private boolean wrongProvenance(TupleNode tuple, VariableProvenanceCondition condition) {
        boolean result = !(tuple.getProvenance().contains(condition.getProvenance()));
        if (logger.isDebugEnabled()) logger.debug("Evaluating provenance: " + condition + " on tuple\n" + tuple + " - Wrong provenance: " + result);
        return result;
    }
}