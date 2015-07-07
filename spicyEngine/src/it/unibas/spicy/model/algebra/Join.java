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
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Join extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(Join.class);

    private VariableJoinCondition joinCondition;

    public Join(VariableJoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public String getName() {
        return "join";
    }

    public VariableJoinCondition getJoinCondition() {
        return joinCondition;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitJoin(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Join " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy leftChild = children.get(0).execute(dataSource);
        IDataSourceProxy rightChild = children.get(1).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Calculating join " + printIds());
        if (logger.isDebugEnabled()) logger.debug("between:\n" + leftChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("and:\n" + rightChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("with :\n" + joinCondition);
        IJoinOperator joinOperator = new NestedLoopJoinOperator();
        result = joinOperator.join(leftChild, rightChild, joinCondition);
        if (logger.isDebugEnabled()) logger.debug("Final result of join:\n" + result.toInstanceString());
        return result;
    }
}
