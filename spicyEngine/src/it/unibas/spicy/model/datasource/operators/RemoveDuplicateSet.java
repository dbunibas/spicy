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

import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.CheckPathContainment;
import java.util.Iterator;
import java.util.List;

public class RemoveDuplicateSet {

    private CheckPathContainment containmentChecker = new CheckPathContainment();
    private FindNode nodeFinder = new FindNode();

    public void removeDuplicateSet(PathExpression clonePath, IDataSourceProxy dataSource) {
        Duplication duplication = findDuplication(clonePath, dataSource);
        if (duplication == null) {
            throw new IllegalArgumentException("Duplication does not exist in data source:  " + duplication);
        }
        SetNode setCloneNode = (SetNode) duplication.getClonePath().getLastNode(dataSource.getIntermediateSchema());
        cleanConstraints(setCloneNode, dataSource);
        cleanJoinConditions(setCloneNode, dataSource);
        cleanSelections(setCloneNode, dataSource);
        cleanInclusionsAndExclusions(setCloneNode, dataSource);
        dataSource.getDuplications().remove(duplication);
        cleanSchema(setCloneNode, dataSource);
        cleanInstance(duplication.getClonePath(), dataSource);
        if(!findOtherClonesForNode(duplication.getOriginalPath(), dataSource)) {
            SetNode originalNode = (SetNode)duplication.getOriginalPath().getLastNode(dataSource.getIntermediateSchema());
            originalNode.setCloned(false);
        }
    }


    private void cleanSchema(SetNode setNode, IDataSourceProxy dataSource) {
        INode setCloneFather = setNode.getFather();
        setCloneFather.removeChild(setNode.getLabel());
        setNode.setFather(null);
    }

    private void cleanInstance(PathExpression clonePath, IDataSourceProxy dataSource) {
        for (INode instance : dataSource.getInstances()) {
            List<INode> cloneInstances = nodeFinder.findNodesInInstance(clonePath, instance);
            for (INode cloneInstance : cloneInstances) {
                INode cloneFather = cloneInstance.getFather();
                cloneFather.getChildren().remove(cloneInstance);
                cloneInstance.setFather(null);
            }
        }
    }

    private void cleanInclusionsAndExclusions(SetNode setNode, IDataSourceProxy dataSource) {
        for (Iterator<PathExpression> it = dataSource.getInclusions().iterator(); it.hasNext();) {
            if (containmentChecker.containsNode(it.next(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
        for (Iterator<PathExpression> it = dataSource.getExclusions().iterator(); it.hasNext();) {
            if (containmentChecker.containsNode(it.next(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
    }

    private void cleanConstraints(SetNode setNode, IDataSourceProxy dataSource) {
        // foreign key constraints referring to a key for the node
        for (Iterator<ForeignKeyConstraint> it = dataSource.getForeignKeyConstraints().iterator(); it.hasNext();) {
            ForeignKeyConstraint foreignKeyConstraint = it.next();
            KeyConstraint keyConstraint = foreignKeyConstraint.getKeyConstraint();
            if (containmentChecker.containsNode(keyConstraint.getKeyPaths(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
        // key constraints for the node
        for (Iterator<KeyConstraint> it = dataSource.getKeyConstraints().iterator(); it.hasNext();) {
            KeyConstraint keyConstraint = it.next();
            if (containmentChecker.containsNode(keyConstraint.getKeyPaths(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
        // foreign key constraints for the node
        for (Iterator<ForeignKeyConstraint> it = dataSource.getForeignKeyConstraints().iterator(); it.hasNext();) {
            ForeignKeyConstraint foreignKeyConstraint = it.next();
            if (containmentChecker.containsNode(foreignKeyConstraint.getForeignKeyPaths(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
    }

    private void cleanJoinConditions(SetNode setNode, IDataSourceProxy dataSource) {
        for (Iterator<JoinCondition> it = dataSource.getJoinConditions().iterator(); it.hasNext();) {
            JoinCondition joinCondition = it.next();
            if (containmentChecker.containsNode(joinCondition.getFromPaths(), setNode, dataSource.getIntermediateSchema()) ||
                    containmentChecker.containsNode(joinCondition.getToPaths(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
    }

    private void cleanSelections(SetNode setNode, IDataSourceProxy dataSource) {
        for (Iterator<SelectionCondition> it = dataSource.getSelectionConditions().iterator(); it.hasNext();) {
            SelectionCondition selectionCondition = it.next();
            if (containmentChecker.containsNode(selectionCondition.getSetPaths(), setNode, dataSource.getIntermediateSchema())) {
                it.remove();
            }
        }
    }

    private Duplication findDuplication(PathExpression clonePath, IDataSourceProxy dataSource) {
        for (Duplication duplication : dataSource.getDuplications()) {
            if (duplication.getClonePath().equals(clonePath)) {
                return duplication;
            }
        }
        return null;
    }

    private boolean findOtherClonesForNode(PathExpression originalPath, IDataSourceProxy dataSource) {
        for (Duplication duplication : dataSource.getDuplications()) {
            if (duplication.getOriginalPath().equals(originalPath)) {
                return true;
            }
        }
        return false;
    }
}

