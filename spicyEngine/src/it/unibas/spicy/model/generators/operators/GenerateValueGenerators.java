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
 
package it.unibas.spicy.model.generators.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.generators.FunctionGenerator;
import it.unibas.spicy.model.generators.GeneratorWithPath;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullValueGenerator;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.generators.TGDGeneratorsMap;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GenerateValueGenerators {

    private static Log logger = LogFactory.getLog(GenerateValueGenerators.class);

    private GenerateSkolemGenerators skolemGeneratorFinder = new GenerateSkolemGenerators();

    public TGDGeneratorsMap generateValueGenerators(FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("*****************Generating generators for TGD:\n" + tgd);
        Map<String, IValueGenerator> attributeGenerators = findAttributeGenerators(tgd, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("Attribute generators: " + SpicyEngineUtility.printMap(attributeGenerators));
        TGDGeneratorsMap generatorsMap = new TGDGeneratorsMap();
        for (SetAlias targetVariable : tgd.getTargetView().getVariables()) {
            PropagateGeneratorsNodeVisitor visitor = new PropagateGeneratorsNodeVisitor(tgd, targetVariable, attributeGenerators, mappingTask.getTargetProxy());
            mappingTask.getTargetProxy().getIntermediateSchema().accept(visitor);
            Map<PathExpression, IValueGenerator> variableGenerators = visitor.getResult();
            generatorsMap.addGeneratorsForVariable(targetVariable, variableGenerators);
        }
        if (logger.isDebugEnabled()) logger.debug("Generators: " + generatorsMap);
        initializeSkolemStrings(generatorsMap, mappingTask);
        return generatorsMap;
    }

    private Map<String, IValueGenerator> findAttributeGenerators(FORule tgd, MappingTask mappingTask) {
        Map<String, IValueGenerator> attributeGenerators = new HashMap<String, IValueGenerator>();
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            VariablePathExpression targetPath = correspondence.getTargetPath();
            FunctionGenerator generator = new FunctionGenerator(correspondence.getTransformationFunction());
            attributeGenerators.put(targetPath.toString(), generator);
        }
        if (logger.isDebugEnabled()) logger.debug("Function generators: " + SpicyEngineUtility.printMap(attributeGenerators));
        List<GeneratorWithPath> functionGeneratorsForSkolemFunctions = findGeneratorsForVariablesInJoin(tgd, attributeGenerators);
        if (logger.isDebugEnabled()) logger.debug("Generators for variables in join: " + SpicyEngineUtility.printCollection(functionGeneratorsForSkolemFunctions));
        skolemGeneratorFinder.findGeneratorsForSkolems(tgd, mappingTask, attributeGenerators, functionGeneratorsForSkolemFunctions);
        return attributeGenerators;
    }

    // NOTE: this method is useless if TGDs are normalized
    private List<GeneratorWithPath> findGeneratorsForVariablesInJoin(FORule tgd, Map<String, IValueGenerator> generators) {
        List<GeneratorWithPath> result = new ArrayList<GeneratorWithPath>();
        List<SetAlias> joinVariables = findJoinVariables(tgd);
        if (logger.isDebugEnabled()) logger.debug("Join variables: " + joinVariables);
        for (SetAlias variable : joinVariables) {
            result.addAll(findGeneratorsForVariable(tgd, generators, variable));
        }
        return result;
    }

    private List<SetAlias> findJoinVariables(FORule tgd) {
        List<SetAlias> result = new ArrayList<SetAlias>();
        if (tgd.getTargetView().getVariables().size() == 1) {
            result.add(tgd.getTargetView().getVariables().get(0));
            return result;
        }
        for (VariableJoinCondition joinCondition : tgd.getTargetView().getAllJoinConditions()) {
            if (!containsVariableWithSameId(result, joinCondition.getFromVariable())) {
                result.add(joinCondition.getFromVariable());
            }
            if (!containsVariableWithSameId(result, joinCondition.getToVariable())) {
                result.add(joinCondition.getToVariable());
            }
        }
        return result;
    }

    private boolean containsVariableWithSameId(List<SetAlias> list, SetAlias variable) {
        for (SetAlias listVariable : list) {
            if (listVariable.getId() == variable.getId()) {
                return true;
            }
        }
        return false;
    }


    private List<GeneratorWithPath> findGeneratorsForVariable(FORule tgd, Map<String, IValueGenerator> generators, SetAlias variable) {
        //TODO:*********  variable or generators ?
        List<GeneratorWithPath> result = new ArrayList<GeneratorWithPath>();
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().getStartingVariable().getId() == variable.getId()) {
                VariablePathExpression targetPath = correspondence.getTargetPath();
                IValueGenerator generator = generators.get(targetPath.toString());
                GeneratorWithPath generatorWithPath = new GeneratorWithPath(targetPath, generator);
                result.add(generatorWithPath);
            }
        }
        return result;
    }

    private void initializeSkolemStrings(TGDGeneratorsMap generatorsMap, MappingTask mappingTask) {
        for (Map<PathExpression, IValueGenerator> map : generatorsMap.getMaps()) {
            for (IValueGenerator generator : map.values()) {
                if (generator instanceof SkolemFunctionGenerator) {
                    SkolemFunctionGenerator skolemGenerator = (SkolemFunctionGenerator)generator;
                    skolemGenerator.getSkolemFunction(mappingTask);
                }
            }
        }
    }


//    private boolean conflict(IValueGenerator keyGenerator, IValueGenerator foreignKeyGenerator) {
//        if (keyGenerator instanceof FunctionGenerator && foreignKeyGenerator instanceof FunctionGenerator) {
//            return false;
//        }
//        if (keyGenerator instanceof SkolemFunctionGenerator && foreignKeyGenerator instanceof SkolemFunctionGenerator) {
//            return false;
//        }
//        return true;
//    }
}

class PropagateGeneratorsNodeVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(PropagateGeneratorsNodeVisitor.class);
    private FORule tgd;
    private SetAlias generatingVariable;
    private Map<String, IValueGenerator> attributeGenerators;
    private IDataSourceProxy target;
    private Map<PathExpression, IValueGenerator> result = new HashMap<PathExpression, IValueGenerator>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public PropagateGeneratorsNodeVisitor(FORule tgd, SetAlias generatingVariable, Map<String, IValueGenerator> attributeGenerators, IDataSourceProxy target) {
        this.tgd = tgd;
        this.generatingVariable = generatingVariable;
        this.attributeGenerators = attributeGenerators;
        this.target = target;
    }

    public void visitSetNode(SetNode node) {
        visitChildren(node);
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        if (node.isRoot()) {
            result.put(nodePath, new SkolemFunctionGenerator(nodePath.toString(), false));
        }
    }

    public void visitTupleNode(TupleNode node) {
        visitChildren(node);
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        // node not to be generated: null value generators
        if (!isNodeToGenerate(node)) {
            result.put(nodePath, NullValueGenerator.getInstance());
            for (INode child : node.getChildren()) {
                PathExpression childPath = pathGenerator.generatePathFromRoot(child);
                result.put(childPath, NullValueGenerator.getInstance());
            }
            return;
        }
        // node to be generated: appropriate generators
        List<GeneratorWithPath> leafGenerators = findLeafGenerators(node);
        IValueGenerator generator = null;
        if (node.isRoot()) {
            generator = new SkolemFunctionGenerator(nodePath.toString(), false);
        } else {
            generator = new SkolemFunctionGenerator(nodePath.toString(), false, tgd, leafGenerators);
        }
        result.put(nodePath, generator);
        for (INode child : node.getChildren()) {
            PathExpression childPath = pathGenerator.generatePathFromRoot(child);
            IValueGenerator childGenerator = new SkolemFunctionGenerator(childPath.toString(), false, tgd, leafGenerators);
            result.put(childPath, childGenerator);
            if (child instanceof AttributeNode) {
                GeneratorWithPath leafGenerator = getLeafGenerator(child);
                INode leafNode = child.getChild(0);
                PathExpression leafPath = pathGenerator.generatePathFromRoot(leafNode);
                result.put(leafPath, leafGenerator.getGenerator());
            }
        }
    }

    private List<GeneratorWithPath> findLeafGenerators(TupleNode node) {
        List<GeneratorWithPath> leafGenerators = new ArrayList<GeneratorWithPath>();
        for (INode child : node.getChildren()) {
            if (child instanceof AttributeNode) {
                GeneratorWithPath leafGeneratorWithPath = getLeafGenerator(child);
                if (leafGeneratorWithPath.getGenerator() instanceof NullValueGenerator) {
                    continue;
                }
                leafGenerators.add(leafGeneratorWithPath);
            }
        }
        return leafGenerators;
    }

    private GeneratorWithPath getLeafGenerator(INode attributeNode) {
        PathExpression nodeAbsolutePath = pathGenerator.generatePathFromRoot(attributeNode);
        VariablePathExpression nodePath = pathGenerator.generateContextualPathForNode(generatingVariable, nodeAbsolutePath, target.getIntermediateSchema());
        IValueGenerator leafGenerator = attributeGenerators.get(nodePath.toString());
        if (leafGenerator == null) {
            leafGenerator = NullValueGenerator.getInstance();
        }
        GeneratorWithPath generatorWithPath = new GeneratorWithPath(nodePath, leafGenerator);
        return generatorWithPath;
    }

    public void visitSequenceNode(SequenceNode node) {
        visitTupleNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitChildren(node);
    }

    public boolean isNodeToGenerate(INode node) {
        if (node.isRoot()) {
            return true;
        }
        VariablePathExpression relativePath = pathGenerator.generateRelativePath(node, target);
        if (relativePath != null) {
            SetAlias nodeVariable = relativePath.getStartingVariable();
            for (SetAlias generator : generatingVariable.getGenerators()) {
                if (nodeVariable.equalsUpToProvenance(generator)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        return;
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public Map<PathExpression, IValueGenerator> getResult() {
        return result;
    }
}

