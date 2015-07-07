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

import it.unibas.spicy.model.generators.AppendSkolemPart;
import it.unibas.spicy.model.generators.GeneratorWithPath;
import it.unibas.spicy.model.generators.ISkolemPart;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullSkolemPart;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.generators.StringSkolemPart;
import it.unibas.spicy.model.generators.SubGeneratorSkolemPart;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateSkolemFunctions {

    private static Log logger = LogFactory.getLog(GenerateSkolemFunctions.class);

    public ISkolemPart generateSkolemFunction(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        if (generator.isLeafGenerator()) {
            if (generator.getType() == SkolemFunctionGenerator.STANDARD) {
                if (mappingTask.getConfig().useLocalSkolem()) {
                    return generateLocalSkolemFunction(generator, mappingTask);
                } else {
                    return generateHyperGraphSkolemFunction(generator, mappingTask);
                }
            } else if (generator.getType() == SkolemFunctionGenerator.KEY) {
                return generateSkolemFunctionForKey(generator, mappingTask);
            } else if (generator.getType() == SkolemFunctionGenerator.EGD_BASED) {
                return generateEGDSkolemFunction(generator, mappingTask);
            }
            throw new IllegalArgumentException("Incorrect type for leaf generator: " + generator + " - Type: " + generator.getType());
        } else {
            return generateSkolemFunctionForIntermediateNode(generator, mappingTask);
        }

    }

    /////////////////////////////////   INTERMEDIATE NODE   /////////////////////////////////////////////////
    private ISkolemPart generateSkolemFunctionForIntermediateNode(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("SK_");
        functionName.append(SpicyEngineUtility.removeRootLabel(generator.getName()));
        return generateAppendWithFunctionName(functionName.toString(), generator, mappingTask);
    }

    /////////////////////////////////   LOCAL NODE   /////////////////////////////////////////////////
    private ISkolemPart generateLocalSkolemFunction(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("SK_");
        functionName.append("TGD").append(compactHashCode(generator.getTgd().hashCode())).append("_");
        functionName.append("N").append(compactHashCode(generator.getJoinConditions().toString().hashCode()));
        if (generator.getPosition() != null) {
            functionName.append("_Pos=").append(generator.getPosition());
        }
        return generateAppendWithFunctionName(functionName.toString(), generator, mappingTask);
    }

    /////////////////////////////////   KEY NODE   /////////////////////////////////////////////////
    private ISkolemPart generateSkolemFunctionForKey(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("SK_");
//        functionName.append("TGD_").append(compactHashCode(generator.getTgd().hashCode())).append("_");
        functionName.append("KEY_").append(compactHashCode(generator.getFunctionalDependencies().get(0).hashCode()));
        if (generator.getPosition() != null) {
            functionName.append("_Pos=").append(generator.getPosition());
        }
        return generateAppendWithFunctionName(functionName.toString(), generator, mappingTask);
    }

    /////////////////////////////////   EGD NODE   /////////////////////////////////////////////////
    private ISkolemPart generateEGDSkolemFunction(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        String functionName = generateEGDFunctionName(generator, mappingTask);
        return generateAppendWithFunctionName(functionName, generator, mappingTask);
    }

    private String generateEGDFunctionName(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        List<String> fds = new ArrayList<String>();
        for (VariableFunctionalDependency functionalDependency : generator.getFunctionalDependencies()) {
            fds.add("-" + generateId(functionalDependency));
        }
        Collections.sort(fds);
        for (int i = 0; i < fds.size(); i++) {
            result.append(fds.get(i));
        }
        return "SK_EGDs" + compactHashCode(result.toString().hashCode());
    }

    private String generateId(VariableFunctionalDependency dependency) {
        StringBuilder result = new StringBuilder();
        for (VariablePathExpression leftPath : dependency.getLeftPaths()) {
            result.append(leftPath.getAbsolutePath());
        }
        result.append(" => ");
        for (VariablePathExpression rightPath : dependency.getRightPaths()) {
            result.append(rightPath.getAbsolutePath());
        }
        return "" + compactHashCode(result.toString().hashCode());
    }


    /////////////////////////////////   HYPERGRAPH NODE   /////////////////////////////////////////////////
    private ISkolemPart generateHyperGraphSkolemFunction(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        // initialize data structures
        Map<SetAlias, List<GeneratorWithPath>> generatorsByAlias = new HashMap<SetAlias, List<GeneratorWithPath>>();
        Map<SetAlias, ISkolemPart> partsByAlias = new HashMap<SetAlias, ISkolemPart>();
        Map<VariableJoinCondition, ISkolemPart> partsForJoins = new HashMap<VariableJoinCondition, ISkolemPart>();
        initializeDataStructures(generator, generatorsByAlias, partsByAlias, partsForJoins, mappingTask);
        // generate tuple strings
        ISkolemPart partForTuples = generatePartForTuples(partsByAlias, mappingTask);
        ISkolemPart partForJoins = generatePartForJoins(generator, partsForJoins, mappingTask);
        ISkolemPart partForVariable = generatePartForVariable(generator, partsForJoins, mappingTask);
        ISkolemPart result = new AppendSkolemPart();
        result.addChild(new StringSkolemPart("SK("));
        result.addChild(new StringSkolemPart("Tuples="));
        result.addChild(partForTuples);
        result.addChild(new StringSkolemPart("**Joins="));
        result.addChild(partForJoins);
        if (generator.getTgd().getTargetView().getAllJoinConditions().size() > 1) {
            result.addChild(new StringSkolemPart("**Var="));
            result.addChild(partForVariable);
        }
        if (generator.getPosition() != null) {
            result.addChild(new StringSkolemPart("**Pos=" + generator.getPosition()));
        }
        result.addChild(new StringSkolemPart(")"));
        return result;
    }

    //////////////////////////// step 1.a: initialize data structures
    private void initializeDataStructures(SkolemFunctionGenerator generator,
            Map<SetAlias, List<GeneratorWithPath>> generatorsByAlias,
            Map<SetAlias, ISkolemPart> partsByAlias,
            Map<VariableJoinCondition, ISkolemPart> partsForJoin,
            MappingTask mappingTask) {
        generatorsByAlias = groupGeneratorsByAlias(generator);
        for (SetAlias variable : generatorsByAlias.keySet()) {
            List<GeneratorWithPath> generatorGroup = generatorsByAlias.get(variable);
            partsByAlias.put(variable, generatePartForAlias(variable, generatorGroup, mappingTask));
        }
        generatePartsForJoins(generator.getTgd().getTargetView().getAllJoinConditions(), partsForJoin, partsByAlias, mappingTask);
    }

    private Map<SetAlias, List<GeneratorWithPath>> groupGeneratorsByAlias(SkolemFunctionGenerator skolemFunction) {
        Map<SetAlias, List<GeneratorWithPath>> groups = new HashMap<SetAlias, List<GeneratorWithPath>>();
        for (GeneratorWithPath subGenerator : skolemFunction.getSubGenerators()) {
            SetAlias generatorVariable = subGenerator.getTargetPath().getStartingVariable();
            List<GeneratorWithPath> variableGroup = groups.get(generatorVariable);
            if (variableGroup == null) {
                variableGroup = new ArrayList<GeneratorWithPath>();
                groups.put(generatorVariable, variableGroup);
            }
            variableGroup.add(subGenerator);
        }
        return groups;
    }

    private ISkolemPart generatePartForAlias(SetAlias variable, List<GeneratorWithPath> subGenerators, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Generating part for set alias: " + variable + "\nGenerators: " + subGenerators);
        ISkolemPart generatorsAppend = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "[", "]", ", ");
        for (GeneratorWithPath subGeneratorWithPath : subGenerators) {
            IValueGenerator subGenerator = subGeneratorWithPath.getGenerator();
            PathExpression absolutePath = subGeneratorWithPath.getTargetPath().getAbsolutePath();
            ISkolemPart appendForSubGenerator = new AppendSkolemPart(false, "", "", ": ");
            appendForSubGenerator.addChild(new StringSkolemPart(SpicyEngineUtility.removeRootLabel(absolutePath)));
            appendForSubGenerator.addChild(new SubGeneratorSkolemPart(subGenerator));
            generatorsAppend.addChild(appendForSubGenerator);
        }
        if (logger.isDebugEnabled()) logger.debug("Final result: " + generatorsAppend);
        return generatorsAppend;
    }

    private void generatePartsForJoins(List<VariableJoinCondition> joinConditions, Map<VariableJoinCondition, ISkolemPart> partsForJoins, Map<SetAlias, ISkolemPart> partsByAlias, MappingTask mappingTask) {
        for (VariableJoinCondition joinCondition : joinConditions) {
            SetAlias fromVariable = joinCondition.getFromVariable();
            SetAlias toVariable = joinCondition.getToVariable();
            ISkolemPart fromGroupPart = findPartForAlias(fromVariable, partsByAlias);
            ISkolemPart fromPathsPart = generatePartForPaths(joinCondition.getFromPaths());
            ISkolemPart toGroupPart = findPartForAlias(toVariable, partsByAlias);
            ISkolemPart toPathsPart = generatePartForPaths(joinCondition.getToPaths());
            ISkolemPart appendFrom = new AppendSkolemPart(false, "", "", ".");
            appendFrom.addChild(fromGroupPart);
            appendFrom.addChild(fromPathsPart);
            ISkolemPart appendTo = new AppendSkolemPart(false, "", "", ".");
            appendTo.addChild(toGroupPart);
            appendTo.addChild(toPathsPart);
            ISkolemPart root = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "[", "]", "=");
            if (joinCondition.getFromPaths().toString().compareTo(joinCondition.getToPaths().toString()) >= 0) {
                root.addChild(appendFrom);
                root.addChild(appendTo);
            } else {
                root.addChild(appendTo);
                root.addChild(appendFrom);
            }
            partsForJoins.put(joinCondition, root);
        }
    }

    private ISkolemPart findPartForAlias(SetAlias variable, Map<SetAlias, ISkolemPart> partsByAlias) {
        ISkolemPart aliasPart = partsByAlias.get(variable);
        if (aliasPart == null) {
//            aliasPart = new StringSkolemPart(SpicyEngineUtility.removeRootLabel(variable.getAbsoluteBindingPathExpression()) + "[]");
            aliasPart = new StringSkolemPart("[]");
        }
        return aliasPart;
    }

    private ISkolemPart generatePartForPaths(List<VariablePathExpression> paths) {
        ISkolemPart append = new AppendSkolemPart(false, "", "", "-", "-");
        List<String> pathStrings = new ArrayList<String>();
        for (VariablePathExpression path : paths) {
            pathStrings.add(SpicyEngineUtility.removeRootLabel(path.getAbsolutePath()).toString());
        }
        Collections.sort(pathStrings);
        for (String pathString : pathStrings) {
            append.addChild(new StringSkolemPart(pathString));
        }
        return append;
    }

    //////////////////////////// first part: tuple part
    private ISkolemPart generatePartForTuples(Map<SetAlias, ISkolemPart> partsByAlias, MappingTask mappingTask) {
        List<SetAlias> variables = new ArrayList<SetAlias>(partsByAlias.keySet());
        Collections.sort(variables);
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "", "", ", ", ", ");
        for (SetAlias setVariable : variables) {
            append.addChild(partsByAlias.get(setVariable));
        }
        return append;
    }

    //////////////////////////// second part: joins part
    private ISkolemPart generatePartForJoins(SkolemFunctionGenerator generator, Map<VariableJoinCondition, ISkolemPart> partsForJoins, MappingTask mappingTask) {
        List<VariableJoinCondition> joinConditions = generator.getTgd().getTargetView().getAllJoinConditions();
        if (joinConditions.isEmpty()) {
            return NullSkolemPart.getInstance();
        }
        List<VariableJoinCondition> sortedConditions = new ArrayList<VariableJoinCondition>(joinConditions);
        Collections.sort(sortedConditions);
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "", "", ", ");
        for (VariableJoinCondition variableJoinCondition : sortedConditions) {
            ISkolemPart joinPart = partsForJoins.get(variableJoinCondition);
            append.addChild(joinPart);
        }
        return append;
    }

    //////////////////////////// third part: variable part
    private ISkolemPart generatePartForVariable(SkolemFunctionGenerator generator, Map<VariableJoinCondition, ISkolemPart> partsForJoins, MappingTask mappingTask) {
        if (generator.getJoinConditions().isEmpty()) {
            return NullSkolemPart.getInstance();
        }
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "", "", ", ");
        for (VariableJoinCondition variableJoinCondition : generator.getJoinConditions()) {
            append.addChild(partsForJoins.get(variableJoinCondition));
        }
        return append;
    }

    /////////////////////////////////   ALTRI METODI   /////////////////////////////////////////////////
    private int compactHashCode(int hashCode) {
//        return Math.abs((hashCode + "").hashCode());
        return Math.abs(hashCode);
    }

    private ISkolemPart generateAppendWithFunctionName(String functionName, SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringSkolemPart functionString = new StringSkolemPart(functionName);
        ISkolemPart appendForSubGenerators = generateAppendsForSubGenerator(generator, mappingTask);
        ISkolemPart append = new AppendSkolemPart();
        append.addChild(functionString);
        append.addChild(appendForSubGenerators);
        return append;
    }

    private ISkolemPart generateAppendsForSubGenerator(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "(", ")", ", ");
        for (GeneratorWithPath subGeneratorWithPath : generator.getSubGenerators()) {
            VariablePathExpression subGeneratorPath = subGeneratorWithPath.getTargetPath();
            IValueGenerator subGenerator = subGeneratorWithPath.getGenerator();
            ISkolemPart appendForGenerator = new AppendSkolemPart(false, "", "", ": ");
            StringSkolemPart stringPart = new StringSkolemPart(SpicyEngineUtility.removeRootLabel(subGeneratorPath.getAbsolutePath()));
            SubGeneratorSkolemPart generatorPart = new SubGeneratorSkolemPart(subGenerator);
            appendForGenerator.addChild(stringPart);
            appendForGenerator.addChild(generatorPart);
            append.addChild(appendForGenerator);
        }
        return append;
    }
}
