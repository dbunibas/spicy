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
 
package it.unibas.spicy.model.datasource.operators;

import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckNewJoin {

    private static Log logger = LogFactory.getLog(CheckNewJoin.class);

    //////////////////////    FOREIGN KEY   ////////////////////////////

    public Boolean addJoinConditionForForeignKey(ForeignKeyConstraint foreignKeyConstraint, IDataSourceProxy dataSourceProvider) {
        checkPaths(foreignKeyConstraint, dataSourceProvider);
        checkNullablePaths(foreignKeyConstraint.getForeignKeyPaths(), dataSourceProvider.getIntermediateSchema());
        JoinCondition joinCondition = generateJoinConditionFromForeignKey(foreignKeyConstraint, dataSourceProvider);
        return addJoinCondition(joinCondition, dataSourceProvider);
    }

    private void checkPaths(ForeignKeyConstraint foreignKeyConstraint, IDataSourceProxy dataSource)  {
        PathExpression foreignKeySetPath = generateSetPath(foreignKeyConstraint.getForeignKeyPaths().get(0), dataSource);
        PathExpression keySetPath = generateSetPath(foreignKeyConstraint.getKeyConstraint().getKeyPaths().get(0), dataSource);
        if (foreignKeySetPath.equals(keySetPath)) {
            throw new IllegalMappingTaskException("Foreign key and key sets coincide in constraint: " + foreignKeyConstraint);
        }
        
    }

    private boolean checkNullablePaths(List<PathExpression> paths, INode root) {
        boolean notNull = paths.get(0).getLastNode(root).isNotNull();
        for (PathExpression path : paths) {
            if (path.getLastNode(root).isNotNull() != notNull) {
                throw new IllegalMappingTaskException("Paths in foreign key/join path muast be either all nullable or all required: " + paths);
            }
        }
        return !notNull;
    }

    private JoinCondition generateJoinConditionFromForeignKey(ForeignKeyConstraint foreignKeyConstraint, IDataSourceProxy dataSource) {
        boolean nullable = checkNullablePaths(foreignKeyConstraint.getForeignKeyPaths(), dataSource.getIntermediateSchema());
        JoinCondition joinCondition = new JoinCondition(foreignKeyConstraint.getForeignKeyPaths(),
                foreignKeyConstraint.getKeyConstraint().getKeyPaths(), true, !nullable);
        return joinCondition;
    }

    private PathExpression generateSetPath(PathExpression attributePath, IDataSourceProxy dataSource) {
        GeneratePathExpression pathGenerator = new GeneratePathExpression();
        return pathGenerator.generateSetPathForAttribute(attributePath, dataSource);
    }

    ////////////////////    ORDINARY JOIN   ////////////////////////////

    public Boolean addJoinCondition(JoinCondition newJoin, IDataSourceProxy dataSource) {
        boolean joinExists = exists(newJoin, dataSource);
        if (joinExists) {
            return Boolean.FALSE;
        }
        dataSource.getJoinConditions().add(newJoin);
        return Boolean.TRUE;
    }

    private boolean exists(JoinCondition newJoin, IDataSourceProxy dataSource) {
        for (JoinCondition joinCondition : dataSource.getJoinConditions()) {
            if (joinCondition.equals(newJoin) || joinCondition.isInverse(newJoin)) {
                return true;
            }
        }
        return false;
    }


}
