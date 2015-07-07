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
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.CheckPathContainment;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateSet {

    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private CheckPathContainment containmentChecker = new CheckPathContainment();
    private FindNode nodeFinder = new FindNode();

    public PathExpression duplicateSet(PathExpression originalPath, IDataSourceProxy dataSource) {
        return duplicateSet(originalPath, dataSource, new ArrayList<ForeignKeyConstraint>());
    }

    public PathExpression duplicateSet(PathExpression originalPath, IDataSourceProxy dataSource, List<ForeignKeyConstraint> foreignKeysToExclude) {
        SetCloneNode clone = duplicateSetInSchema(originalPath, dataSource, foreignKeysToExclude);
        cloneInstances(originalPath, clone, dataSource);
        return pathGenerator.generatePathFromRoot(clone);
    }

    public SetCloneNode duplicateSetInSchema(PathExpression originalPath, IDataSourceProxy dataSource) {
        return duplicateSetInSchema(originalPath, dataSource, new ArrayList<ForeignKeyConstraint>());
    }

    public SetCloneNode duplicateSetInSchema(PathExpression originalPath, IDataSourceProxy dataSource, List<ForeignKeyConstraint> foreignKeysToExclude) {
        INode lastNode = originalPath.getLastNode(dataSource.getIntermediateSchema());
        if (!(lastNode instanceof SetNode)) {
            throw new IllegalArgumentException("Incorrect duplication. Only sets can be duplicated: " + lastNode.getLabel());
        }
        if (lastNode.isRoot()) {
            throw new IllegalArgumentException("Incorrect duplication. The tree root cannot be duplicated: " + lastNode.getLabel());
        }
//        if (lastNode instanceof SetCloneNode) {
//            throw new IllegalArgumentException("Unable to duplicate set. The set is already a duplicate: " + lastNode.getLabel());
//        }
        SetNode setNode = (SetNode) lastNode;
        SetCloneNode clone = cloneSchema(setNode, originalPath, dataSource);
        generateConstraintsForClone(setNode, clone, dataSource, foreignKeysToExclude);
        PathExpression clonePath = pathGenerator.generatePathFromRoot(clone);
        Duplication duplication = new Duplication(originalPath, clonePath);
        dataSource.getDuplications().add(duplication);
        return clone;
    }

    private SetCloneNode cloneSchema(SetNode setNode, PathExpression duplication, IDataSourceProxy dataSource) {
        setNode.setCloned(true);
        SetNode setNodeClone = (SetNode) setNode.clone();
        int counter = generateCloneCounter(duplication, dataSource);
        SetCloneNode clone = new SetCloneNode(counter, duplication);
        for (INode child : setNodeClone.getChildren()) {
            clone.addChild(child);
        }
        setNode.getFather().addChild(clone);
        return clone;
    }

    public int generateCloneCounter(PathExpression nodePath, IDataSourceProxy dataSource) {
        int clones = 0;
        for (Duplication duplication : dataSource.getDuplications()) {
            if (duplication.getOriginalPath().equals(nodePath)) {
                clones++;
            }
        }
        return clones + 1;
    }

    private void generateConstraintsForClone(SetNode setNode, SetCloneNode clone, IDataSourceProxy dataSource, List<ForeignKeyConstraint> foreignKeysToExclude) {
        Map<KeyConstraint, KeyConstraint> keyConstraintMap = new HashMap<KeyConstraint, KeyConstraint>();
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            if (containmentChecker.containsNode(keyConstraint.getKeyPaths(), setNode, dataSource.getIntermediateSchema())) {
                KeyConstraint newKeyConstraint = new KeyConstraint(generateNewPathList(setNode, clone, keyConstraint.getKeyPaths(), dataSource), keyConstraint.isPrimaryKey());
                keyConstraintMap.put(keyConstraint, newKeyConstraint);
            }
        }
        dataSource.getKeyConstraints().addAll(keyConstraintMap.values());
        Map<ForeignKeyConstraint, ForeignKeyConstraint> foreignKeyConstraintMap = new HashMap<ForeignKeyConstraint, ForeignKeyConstraint>();
        for (ForeignKeyConstraint foreignKeyConstraint : dataSource.getForeignKeyConstraints()) {
            if (!foreignKeysToExclude.contains(foreignKeyConstraint) &&
                    containmentChecker.containsNode(foreignKeyConstraint.getForeignKeyPaths(), setNode, dataSource.getIntermediateSchema())) {
                KeyConstraint keyConstraint = keyConstraintMap.get(foreignKeyConstraint.getKeyConstraint());
                if (keyConstraint == null) {
                    keyConstraint = foreignKeyConstraint.getKeyConstraint();
                }
                ForeignKeyConstraint newForeignKeyConstraint = new ForeignKeyConstraint(keyConstraint, generateNewPathList(setNode, clone, foreignKeyConstraint.getForeignKeyPaths(), dataSource));
                foreignKeyConstraintMap.put(foreignKeyConstraint, newForeignKeyConstraint);
            }
        }
        dataSource.getForeignKeyConstraints().addAll(foreignKeyConstraintMap.values());
    }

    private List<PathExpression> generateNewPathList(SetNode setNode, SetCloneNode clone, List<PathExpression> oltPathList, IDataSourceProxy dataSource) {
        List<PathExpression> newPaths = new ArrayList<PathExpression>();
        for (PathExpression oldPath : oltPathList) {
            newPaths.add(generatePathForCloneSet(setNode, clone, oldPath, dataSource));
        }
        return newPaths;
    }

    private PathExpression generatePathForCloneSet(INode originalNode, INode clone, PathExpression originalPath, IDataSourceProxy dataSource) {
        String newPathString = toStringWithReplacement(originalNode, clone, originalPath);
        PathExpression newPath = pathGenerator.generatePathFromString(newPathString);
        return newPath;
    }

    private String toStringWithReplacement(INode originalNode, INode clone, PathExpression originalPath) {
        String result = "";
        for (String nodeLabel : originalPath.getPathSteps()) {
            if (!result.equals("")) {
                result += ".";
            }
            if (!(nodeLabel.equals(originalNode.getLabel()))) {
                result += nodeLabel;
            } else {
                result += clone.getLabel();
            }
        }
        return result;
    }

    private void cloneInstances(PathExpression originalPath, SetCloneNode clone, IDataSourceProxy dataSource) {
        for (INode instance : dataSource.getInstances()) {
            generateInstanceClone(originalPath, instance, clone);
        }
    }

    public INode generateInstanceClone(List<Duplication> duplications, INode instance, INode schemaRoot) {
        INode clone = instance.clone();
        for (Duplication duplication : duplications) {
            generateInstanceClone(duplication.getOriginalPath(), clone, (SetCloneNode) duplication.getClonePath().getLastNode(schemaRoot));
        }
        return clone;
    }

    private void generateInstanceClone(PathExpression pathToClone, INode instance, SetCloneNode cloneInSchema) {
        List<INode> instanceNodes = nodeFinder.findNodesInInstance(pathToClone, instance);
        for (INode instanceNode : instanceNodes) {
            ((SetNode) instanceNode).setCloned(true);
            SetNode instanceNodeClone = (SetNode) instanceNode.clone();
            SetCloneNode clone = new SetCloneNode(cloneInSchema.getLabel(), new IntegerOIDGenerator().getNextOID());
            for (INode child : instanceNodeClone.getChildren()) {
                clone.addChild(child);
            }
            instanceNode.getFather().addChild(clone);
        }
    }
}
