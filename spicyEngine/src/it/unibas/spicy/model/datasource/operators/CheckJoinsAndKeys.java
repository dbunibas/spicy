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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckJoinsAndKeys {

    private static Log logger = LogFactory.getLog(CheckJoinsAndKeys.class);

    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();

    public boolean checkIfIsKey(INode node, DataSource dataSource) {
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
        return checkIfIsKey(pathExpression, dataSource);
    }

    public boolean checkIfIsKey(PathExpression nodePath, DataSource dataSource) {
        List<KeyConstraint> listKeyConstraints = dataSource.getKeyConstraints();
        for (KeyConstraint keyConstraint : listKeyConstraints) {
            if (keyConstraint.getKeyPaths().contains(nodePath)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfIsForeignKey(INode node, DataSource dataSource) {
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
        return checkIfIsForeignKey(pathExpression, dataSource);
    }

    public boolean checkIfIsForeignKey(PathExpression nodePath, DataSource dataSource) {
        List<ForeignKeyConstraint> listForeignKeyConstraints = dataSource.getForeignKeyConstraints();
        for (ForeignKeyConstraint foreignKeyConstraint : listForeignKeyConstraints) {
            if (foreignKeyConstraint.getForeignKeyPaths().contains(nodePath)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTypes(ForeignKeyConstraint foreignKeyConstraint, IDataSourceProxy dataSource) {
        for (int i = 0; i < foreignKeyConstraint.getForeignKeyPaths().size(); i++) {
            PathExpression keyPath = foreignKeyConstraint.getKeyConstraint().getKeyPaths().get(i);
            INode keyNode = nodeFinder.findNodeInSchema(keyPath, dataSource);
            String keyType = keyNode.getChild(0).getLabel();
            PathExpression foreignKeyPath = foreignKeyConstraint.getForeignKeyPaths().get(i);
            INode foreignKeyNode = nodeFinder.findNodeInSchema(foreignKeyPath, dataSource);
            String foreignKeyType = foreignKeyNode.getChild(0).getLabel();
            if (!(keyType.equals(foreignKeyType))) {
                logger.error("Found mismatching types in foreign key constraint. Key path= " + keyPath + "- key type = " + keyType + " - Foreign key path=" + foreignKeyPath + " - foreign key type =" + foreignKeyType);
                return false;
            }
        }
        return true;
    }

    public JoinCondition checkIfIsToPath(VariablePathExpression variablePath, IDataSourceProxy dataSource) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        return checkIfIsToPath(absolutePath, dataSource);
    }

    public JoinCondition checkIfIsFromPath(VariablePathExpression variablePath, IDataSourceProxy dataSource) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        return checkIfIsFromPath(absolutePath, dataSource);
    }

    public JoinCondition checkIfIsToPath(PathExpression absolutePath, IDataSourceProxy dataSource) {
        List<JoinCondition> joinConditions = dataSource.getJoinConditions();
        for (JoinCondition joinCondition : joinConditions) {
            if (joinCondition.getToPaths().contains(absolutePath)) {
                return joinCondition;
            }
        }
        return null;
    }

    public JoinCondition checkIfIsFromPath(PathExpression absolutePath, IDataSourceProxy dataSource) {
        List<JoinCondition> joinConditions = dataSource.getJoinConditions();
        for (JoinCondition joinCondition : joinConditions) {
            if (joinCondition.getFromPaths().contains(absolutePath)) {
                return joinCondition;
            }
        }
        return null;
    }

}
