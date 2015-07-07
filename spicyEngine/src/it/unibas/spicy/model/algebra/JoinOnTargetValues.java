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
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JoinOnTargetValues extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(JoinOnTargetValues.class);

    private VariableJoinCondition joinCondition;
    private List<VariableCorrespondence> leftCorrespondences;
    private List<VariableCorrespondence> rightCorrespondences;

    public JoinOnTargetValues(VariableJoinCondition joinCondition, List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        this.joinCondition = joinCondition;
        this.leftCorrespondences = leftCorrespondences;
        this.rightCorrespondences = rightCorrespondences;
    }

    public String getName() {
        return "join-on-target-values" + " - " + this.id;
    }

    public VariableJoinCondition getJoinCondition() {
        return joinCondition;
    }

    public List<VariableCorrespondence> getLeftCorrespondences() {
        return leftCorrespondences;
    }

    public List<VariableCorrespondence> getRightCorrespondences() {
        return rightCorrespondences;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitJoinOnTargetValues(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Join on target values " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy leftChild = children.get(0).execute(dataSource);
        IDataSourceProxy rightChild = children.get(1).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Calculating join on target values" + printIds());
        if (logger.isDebugEnabled()) logger.debug("between:\n" + leftChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("and:\n" + rightChild.toInstanceString());
        if (logger.isDebugEnabled()) logger.debug("with :\n" + joinCondition);
        if (logger.isDebugEnabled()) logger.debug("left correspondences :\n" + leftCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("right correspondences :\n" + rightCorrespondences);
        IJoinOnTargetValuesOperator joinOperator = new NestedLoopJoinOnTargetValuesOperator();
        result = joinOperator.join(leftChild, rightChild, leftCorrespondences, rightCorrespondences, joinCondition);
        if (logger.isDebugEnabled()) logger.debug("Final result of join on target values: " + result.toInstanceString());
        return result;
    }
}
