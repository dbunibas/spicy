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
package it.unibas.spicy.model.algebra.query.operators.xquery;

import it.unibas.spicy.model.generators.AppendSkolemPart;
import it.unibas.spicy.model.generators.GeneratorWithPath;
import it.unibas.spicy.model.generators.ISkolemPart;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullSkolemPart;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.generators.StringSkolemPart;
import it.unibas.spicy.model.mapping.FORule;
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

public class XQSkolemHandler {

    private static Log logger = LogFactory.getLog(XQSkolemHandler.class);
    /////// CONSTANTS
    private static Map<String, String> attributesMap = new HashMap<String, String>();
    private static Map<String, String> setMap = new HashMap<String, String>();
    private static int setCounter = 0;
    private static int attributeCounter = 0;
    private final static boolean COMPACT = true;
    private final static String SIMPLE_COMMA = ",";
//    private final static String CONCATENATED_COMMA = "||\'-\'||";
    private final static String CONCATENATED_COMMA = ",";

    public ISkolemPart skolemString(SkolemFunctionGenerator generator, MappingTask mappingTask, boolean useSorting) {
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
//            return skolemString(generator, false);
        }
    }

    private ISkolemPart generateSkolemFunctionForIntermediateNode(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("\"SK_");
        functionName.append(SpicyEngineUtility.removeRootLabel(generator.getName()));
        return generateAppendWithFunctionName(functionName.toString(), generator, mappingTask);
    }
    /////////////////////////////////   LOCAL NODE   /////////////////////////////////////////////////

    private ISkolemPart generateLocalSkolemFunction(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("\"SK_");
        functionName.append("TGD_").append(compactHashCode(generator.getTgd().hashCode())).append("_");
        functionName.append("N_").append(compactHashCode(generator.getJoinConditions().toString().hashCode()));
        if (generator.getPosition() != null) {
            functionName.append("_P=").append(generator.getPosition());
        }
        if (mappingTask.getConfig().useSortInSkolems()) {
            functionName.append("\",");
        }
        return generateAppendWithFunctionName(functionName.toString(), generator, mappingTask);
    }

    /////////////////////////////////   KEY NODE   /////////////////////////////////////////////////
    private ISkolemPart generateSkolemFunctionForKey(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringBuilder functionName = new StringBuilder();
        functionName.append("\"SK_");
        functionName.append("KEY_").append(compactHashCode(generator.getFunctionalDependencies().get(0).hashCode()));
        if (generator.getPosition() != null) {
            functionName.append("_P=").append(generator.getPosition());
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
        return "\"SK_EGDs" + compactHashCode(result.toString().hashCode());
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
        ISkolemPart result = new AppendSkolemPart(false, true);
        result.addChild(new StringSkolemPart("\""));
        result.addChild(new StringSkolemPart("SK{"));
        result.addChild(new StringSkolemPart("T=\","));
        result.addChild(partForTuples);
        result.addChild(new StringSkolemPart(",\"J=\","));
        result.addChild(partForJoins);
        if (generator.getTgd().getTargetView().getAllJoinConditions().size() > 1) {
            result.addChild(new StringSkolemPart(",\"V=\","));
            result.addChild(partForVariable);
        }
        if (generator.getPosition() != null) {
            result.addChild(new StringSkolemPart(",\"P=" + generator.getPosition() + "\""));
        }
        result.addChild(new StringSkolemPart(",\"}"));
        result.addChild(new StringSkolemPart("\""));
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
            partsByAlias.put(variable, generatePartForAlias(variable, generatorGroup, generator.getTgd(), mappingTask));
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

    private ISkolemPart generatePartForAlias(SetAlias variable, List<GeneratorWithPath> subGenerators, FORule rule, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Generating part for set alias: " + variable + "\nGenerators: " + subGenerators);
        ISkolemPart generatorsAppend = new AppendSkolemPart(false, true, "[", "]\"", "-");
        for (GeneratorWithPath subGeneratorWithPath : subGenerators) {
            PathExpression absolutePath = subGeneratorWithPath.getTargetPath().getAbsolutePath();
            ISkolemPart appendForSubGenerator = new AppendSkolemPart(false, true, "", ",\"", ":\",");

            String attributePath = findPathInAttributeMap(absolutePath);
            appendForSubGenerator.addChild(new StringSkolemPart(attributePath));
            String attributeValue = generateAttributeValue(subGeneratorWithPath, rule, mappingTask);
            appendForSubGenerator.addChild(new StringSkolemPart(attributeValue));
            generatorsAppend.addChild(appendForSubGenerator);
        }
        if (logger.isDebugEnabled()) logger.debug("Final result: " + generatorsAppend);
        return generatorsAppend;
    }

    private String findPathInAttributeMap(PathExpression targetPath) {
        String absolutePath = SpicyEngineUtility.removeRootLabel(targetPath.toString());
        if (logger.isDebugEnabled()) logger.debug("Searching targetPath " + absolutePath + " in Map: " + attributesMap);
        String value = attributesMap.get(absolutePath);
        if (value != null) {
            if (logger.isDebugEnabled()) logger.debug("targetPath found");
            return value;
        }
        String setName = removeAttributeNameFromPath(absolutePath);
        if (logger.isDebugEnabled()) logger.debug("SetName: " + setName);
        String setInMap = setMap.get(setName);
        if (logger.isDebugEnabled()) logger.debug("Set in Map: " + setInMap);
        String attributeName = "";
        if (setInMap != null) {
            attributeName = setInMap;
        } else {
            int setValue = setCounter++;
            attributeName = setValue + "";
            setMap.put(setName, setValue + "");
        }
        attributeName += "." + attributeCounter++;
        attributesMap.put(absolutePath, attributeName);
        return attributeName;
    }

    private String removeAttributeNameFromPath(String targetPath) {
        return targetPath.substring(0, targetPath.lastIndexOf("."));
    }

    private String generateAttributeValue(GeneratorWithPath subGeneratorWithPath, FORule rule, MappingTask mappingTask) {
        VariablePathExpression targetPath = subGeneratorWithPath.getTargetPath();
        String attributeNameInVariable = "";
//        if (GenerateSQL.hasDifferences(rule)) {
        VariablePathExpression sourcePath = XQUtility.findSourcePath(rule.getCoveredCorrespondences(), targetPath);
        attributeNameInVariable = XQNames.attributeNameInVariable(sourcePath);
//        } else {
//            VariablePathExpression sourcePath = GenerateSQL.findSourcePath(rule.getCoveredCorrespondences(), targetPath);
//            attributeNameInVariable = GenerateSQL.attributeNameWithVariable(sourcePath);
//        }
        return mappingTask.getDBMSHandler().coalesceFunctionWithNull(attributeNameInVariable);
    }

    private void generatePartsForJoins(List<VariableJoinCondition> joinConditions, Map<VariableJoinCondition, ISkolemPart> partsForJoins, Map<SetAlias, ISkolemPart> partsByAlias, MappingTask mappingTask) {
        for (VariableJoinCondition joinCondition : joinConditions) {
            SetAlias fromVariable = joinCondition.getFromVariable();
            SetAlias toVariable = joinCondition.getToVariable();
            ISkolemPart fromGroupPart = findPartForAlias(fromVariable, partsByAlias);
            ISkolemPart fromPathsPart = generatePartForPaths(joinCondition.getFromPaths());
            ISkolemPart toGroupPart = findPartForAlias(toVariable, partsByAlias);
            ISkolemPart toPathsPart = generatePartForPaths(joinCondition.getToPaths());
            ISkolemPart appendFrom = new AppendSkolemPart(false, true, "", "", ",\".");
            appendFrom.addChild(fromGroupPart);
            appendFrom.addChild(fromPathsPart);
            ISkolemPart appendTo = new AppendSkolemPart(false, true, "", "", ",\".");
            appendTo.addChild(toGroupPart);
            appendTo.addChild(toPathsPart);
            ISkolemPart root = null;
            if (mappingTask.getConfig().useSortInSkolems()) {
                root = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), true, "\"", "\"", "\"" + getCommaString(mappingTask.getConfig().useSortInSkolems()) + "\"");
            } else {
                root = new AppendSkolemPart(false, true, "[\",\"", "\"", "\"" + getCommaString(mappingTask.getConfig().useSortInSkolems()) + "\"");
            }
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
            aliasPart = new StringSkolemPart("[]\"");
        }
        return aliasPart;
    }

    private ISkolemPart generatePartForPaths(List<VariablePathExpression> paths) {
        ISkolemPart append = new AppendSkolemPart(false, true, "", "", "-", "-");
        List<String> pathStrings = new ArrayList<String>();
        for (VariablePathExpression path : paths) {
            pathStrings.add(findPathInAttributeMap(path));
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
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), true, "\"", "", getCommaString(mappingTask.getConfig().useSortInSkolems()) + "\"");
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
        ISkolemPart append = null;
        if (mappingTask.getConfig().useSortInSkolems()) {
            append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), true, "", "", SIMPLE_COMMA);
        } else {
            append = new AppendSkolemPart(false, true, "\"", "", CONCATENATED_COMMA + "\"");
        }
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
        ISkolemPart append = null;
        if (mappingTask.getConfig().useSortInSkolems()) {
            append = new AppendSkolemPart(true, true, "", "", "" + CONCATENATED_COMMA);
        } else {
            append = new AppendSkolemPart(false, true, "\"", "", CONCATENATED_COMMA + "\"");
        }
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

    private String getCommaString(boolean sort) {
        if (sort) {
            return SIMPLE_COMMA;
        }
        return CONCATENATED_COMMA;
    }

    private ISkolemPart generateAppendWithFunctionName(String functionName, SkolemFunctionGenerator generator, MappingTask mappingTask) {
        StringSkolemPart functionString = new StringSkolemPart(functionName);
        ISkolemPart appendForSubGenerators = generateAppendsForSubGenerator(generator, mappingTask);
        ISkolemPart append = new AppendSkolemPart(false, false);
        append.addChild(functionString);
        append.addChild(appendForSubGenerators);
        return append;
    }

    private ISkolemPart generateAppendsForSubGenerator(SkolemFunctionGenerator generator, MappingTask mappingTask) {
        ISkolemPart append = new AppendSkolemPart(mappingTask.getConfig().useSortInSkolems(), "(", "", ",\"");
        for (GeneratorWithPath subGeneratorWithPath : generator.getSubGenerators()) {
            VariablePathExpression subGeneratorPath = subGeneratorWithPath.getTargetPath();
            IValueGenerator subGenerator = subGeneratorWithPath.getGenerator();
            ISkolemPart appendForGenerator = new AppendSkolemPart(false, false, "", "", ":\",");
//            StringSkolemPart stringPart = new StringSkolemPart(SpicyEngineUtility.removeRootLabel(subGeneratorPath.getAbsolutePath()));
//            String generatorPartString = subGenerator.toString();
            
            String attributeName = findPathInAttributeMap(subGeneratorPath);
            StringSkolemPart stringPart = new StringSkolemPart(attributeName);
            String attributeNameInVariable = XQNames.attributeNameInVariable(subGeneratorPath);
            StringSkolemPart generatorPart = new StringSkolemPart(attributeNameInVariable);
            
            
//            if (generatorPartString.startsWith("[")) {
//                generatorPartString = generatorPartString.substring(1, generatorPartString.length() - 1);
//            } else {
//                generatorPartString = "\"" + generatorPartString + "\"";
//            }
//            SubGeneratorSkolemPart generatorPart = new SubGeneratorSkolemPart(subGenerator);
//            StringSkolemPart generatorPart = new StringSkolemPart(generatorPartString);
            appendForGenerator.addChild(stringPart);
            appendForGenerator.addChild(generatorPart);
            append.addChild(appendForGenerator);
        }
        return append;
    }
//    private ISkolemPart generateAppendsForSubGenerator(SkolemFunctionGenerator generator, MappingTask mappingTask) {
//        boolean sort = mappingTask.getConfig().useSortInSkolems();
//        ISkolemPart append = null;
//        if (sort) {
//            append = new AppendSkolemPart(sort, true, "\"(", ")\"", ",");
//        } else {
//            append = new AppendSkolemPart(false, true, "(", ")\"", ",");
//        }
//        for (GeneratorWithPath subGeneratorWithPath : generator.getSubGenerators()) {
//            VariablePathExpression subGeneratorPath = subGeneratorWithPath.getTargetPath();
//            FORule tgd = generator.getTgd();
//            String attributeNameInVariable = "";
////            if (GenerateSQL.hasDifferences(tgd)) {
//            VariablePathExpression sourcePath = XQUtility.findSourcePath(tgd.getCoveredCorrespondences(), subGeneratorPath);
//            attributeNameInVariable = XQNames.attributeNameInVariable(sourcePath);
////            } else {
////                VariablePathExpression sourcePath = GenerateSQL.findSourcePath(tgd.getCoveredCorrespondences(), subGeneratorPath);
////                attributeNameInVariable = GenerateSQL.attributeNameWithVariable(sourcePath);
////            }
//            String attributeName = findPathInAttributeMap(subGeneratorPath);
//            ISkolemPart appendForGenerator = new AppendSkolemPart(false, true, "", "", ":\",");
//            StringSkolemPart stringPart = new StringSkolemPart(attributeName);
//            StringSkolemPart generatorPart = new StringSkolemPart(attributeNameInVariable + ",\"");
//            appendForGenerator.addChild(stringPart);
//            appendForGenerator.addChild(generatorPart);
//            append.addChild(appendForGenerator);
//        }
//        return append;
//    }
//    public String skolemStringForLeaf(SkolemFunctionGenerator skolemFunction) {
//    return skolemString(skolemFunction, true);
//    }
//    public String skolemStringForIntermediateNode(SkolemFunctionGenerator skolemFunction) {
//        return skolemString(skolemFunction, false);
//    }

//    private String skolemString(SkolemFunctionGenerator skolemFunction, boolean leafGenerator) {
//        Map<SetAlias, List<GeneratorWithPath>> generatorGroups = findGeneratorGroups(skolemFunction);
//        Map<List<GeneratorWithPath>, String> groupStrings = new HashMap<List<GeneratorWithPath>, String>();
//        generateGroupStrings(generatorGroups, groupStrings, skolemFunction.getTgd());
//        String tupleString = generateTupleString(groupStrings);
//        String joinString = "";
//        String skolemString = "";
//        List<VariableJoinCondition> tgdJoinConditions = skolemFunction.getTgd().getTargetView().getJoinConditions();
//        if (tgdJoinConditions.size() != 0) {
//            Map<VariableJoinCondition, String> joinStrings = new HashMap<VariableJoinCondition, String>();
//            generateJoinStrings(tgdJoinConditions, joinStrings);
//            joinString = generateJoinString(joinStrings);
//            if (skolemFunction.getJoinConditions().size() != 0) {
//                skolemString = generateSkolemString(skolemFunction, joinStrings);
//            }
//        }
//        StringBuilder result = new StringBuilder();
//        //        result.append("\'").append(removeRootLabel(skolemFunction.getName())).append("{");
//        result.append("\"SK").append("{");
//        result.append("T=").append(tupleString);
//        if (leafGenerator) {
//            result.append("J=").append(joinString);
//            result.append("V=").append(skolemString);
//        }
//        result.append("}\"");
//        return result.toString();
//    }
//
//    private Map<SetAlias, List<GeneratorWithPath>> findGeneratorGroups(SkolemFunctionGenerator skolemFunction) {
//        Map<SetAlias, List<GeneratorWithPath>> groups = new HashMap<SetAlias, List<GeneratorWithPath>>();
//        for (GeneratorWithPath subGenerator : skolemFunction.getSubGenerators()) {
//            SetAlias generatorVariable = subGenerator.getTargetPath().getStartingVariable();
//            List<GeneratorWithPath> variableGroup = groups.get(generatorVariable);
//            if (variableGroup == null) {
//                variableGroup = new ArrayList<GeneratorWithPath>();
//                groups.put(generatorVariable, variableGroup);
//            }
//            variableGroup.add(subGenerator);
//        }
//        return groups;
//    }
//
//    ///////////////////////////////////////////////////////////
//    //////////////////////////// step 1.a: generate group strings
//    private void generateGroupStrings(Map<SetAlias, List<GeneratorWithPath>> generatorGroups, Map<List<GeneratorWithPath>, String> groupStringMap, FORule rule) {
//        for (SetAlias variable : generatorGroups.keySet()) {
//            List<GeneratorWithPath> generatorGroup = generatorGroups.get(variable);
//            groupStringMap.put(generatorGroup, generateStringForGroup(generatorGroup, rule));
//        }
//    }
//
//    private String generateStringForGroup(List<GeneratorWithPath> subGenerators, FORule rule) {
//        List<String> values = new ArrayList<String>();
//        for (GeneratorWithPath subGeneratorWithPath : subGenerators) {
//            VariablePathExpression targetPath = subGeneratorWithPath.getTargetPath();
////            VariablePathExpression sourcePath = XQUtility.findSourcePath(rule.getCoveredCorrespondences(), targetPath);
////            if (sourcePath == null) {
////                continue;
////            }
//            String attributeNameInVariable = XQNames.attributeNameInVariable(targetPath);
//            String attributeName = "";
//            if (COMPACT) {
//                attributeName = findPathInAttributeMap(targetPath);
//            } else {
//                attributeName = XQUtility.removeRootLabel(targetPath.getAbsolutePath().toString());
//            }
//            values.add(attributeName + ":\"," + attributeNameInVariable + ",\"");
//            //            values.add(removeRootLabel(targetPath.getAbsolutePath().toString()) + ":\'||" + mappingTask.getDBMSHandler().coalesceFunctionWithNull(attributeNameInVariable));
//        }
//        Collections.sort(values);
//        StringBuilder result = new StringBuilder();
//        //        result.append(removeRootLabel(variable.getAbsoluteBindingPathExpression().toString())).append("[");
//        result.append("[");
//        int numberOfValues = values.size();
//        for (int i = 0; i < numberOfValues; i++) {
//            result.append(values.get(i));
//            if (i < numberOfValues - 1) {
//                result.append(",");
//                //                result.append(CONCATENATED_COMMA);
//                //                result.append("\'");
//            }
//        }
//        result.append("]");
//        return result.toString();
//    }
//
//    private String findPathInAttributeMap(VariablePathExpression targetPath) {
//        String absolutePath = targetPath.getAbsolutePath().toString();
//        if (logger.isDebugEnabled()) logger.debug("Searching targetPath " + absolutePath + " in Map: " + attributesMap);
//        String value = attributesMap.get(absolutePath);
//        if (value != null) {
//            if (logger.isDebugEnabled()) logger.debug("targetPath found");
//            return value;
//        }
//        String setName = removeAttributeNameFromPath(absolutePath);
//        if (logger.isDebugEnabled()) logger.debug("SetName: " + setName);
//        String setInMap = setMap.get(setName);
//        if (logger.isDebugEnabled()) logger.debug("Set in Map: " + setInMap);
//        String attributeName = "";
//        if (setInMap != null) {
//            attributeName = setInMap;
//        } else {
//            int setValue = setCounter++;
//            attributeName = setValue + "";
//            setMap.put(setName, setValue + "");
//        }
//        attributeName += "." + attributeCounter++;
//        attributesMap.put(absolutePath, attributeName);
//        return attributeName;
//    }
//
////    private String removeAttributeNameFromPath(String targetPath) {
////        return targetPath.substring(0, targetPath.lastIndexOf("."));
////    }
//
//    //////////////////////////// first part: tuple string
//    private String generateTupleString(Map<List<GeneratorWithPath>, String> groupStringMap) {
//        StringBuilder result = new StringBuilder();
//        List<String> groupStrings = new ArrayList<String>(groupStringMap.values());
//        Collections.sort(groupStrings);
//        int numberOfValues = groupStrings.size();
//        //        result.append("\'");
//        for (int i = 0; i < numberOfValues; i++) {
//            result.append(groupStrings.get(i));
//            if (i < numberOfValues - 1) {
//                result.append(",");
//                //                result.append(CONCATENATED_COMMA);
//                //                result.append("\'");
//            }
//        }
//        return result.toString();
//    }
//
//    //////////////////////////// step 1.b: generate join strings
//    private void generateJoinStrings(List<VariableJoinCondition> joinConditions, Map<VariableJoinCondition, String> joinStrings) {
//        for (VariableJoinCondition variableJoinCondition : joinConditions) {
//            joinStrings.put(variableJoinCondition, generateStringForJoinCondition(variableJoinCondition));
//        }
//    }
//
//    private String generateStringForJoinCondition(VariableJoinCondition variableJoinCondition) {
//        StringBuilder result = new StringBuilder();
//        String fromPathsString = generatePathStrings(variableJoinCondition.getFromPaths());
//        String toPathsString = generatePathStrings(variableJoinCondition.getToPaths());
//        //        result.append("[\'||");
//        result.append("[").append(fromPathsString).append("-").append(toPathsString).append("]\'");
//        return result.toString();
//    }
//
//    private String generatePathStrings(List<VariablePathExpression> paths) {
//        StringBuilder result = new StringBuilder();
//        List<String> pathStrings = new ArrayList<String>();
//        for (VariablePathExpression path : paths) {
//            if (COMPACT) {
//                //                pathStrings.add(removeRootLabel(path.getAbsolutePath().toString()));
//                pathStrings.add(findPathInAttributeMap(path));
//            } else {
//                pathStrings.add(XQUtility.removeRootLabel(path.getAbsolutePath().toString()));
//            }
//        }
//        Collections.sort(pathStrings);
//        int numberOfValues = pathStrings.size();
//        //        result.append("cast(sort(array[\'");
//        for (int i = 0; i < numberOfValues; i++) {
//            result.append(pathStrings.get(i));
//            if (i < numberOfValues - 1) {
//                //                result.append("||\',\'||\'");
//                result.append("-");
//            }
//        }
//        //        result.append("]) as text)");
//        return result.toString();
//    }
//
//    //////////////////////////// second part: joins string
//    private String generateJoinString(Map<VariableJoinCondition, String> joinStrings) {
//        StringBuilder result = new StringBuilder();
//        List<String> groupStrings = new ArrayList<String>(joinStrings.values());
//        Collections.sort(groupStrings);
//        int numberOfValues = groupStrings.size();
//        //        result.append("\'");
//        for (int i = 0; i < numberOfValues; i++) {
//            result.append(groupStrings.get(i));
//            if (i < numberOfValues - 1) {
//                result.append(",");
//                //                result.append(CONCATENATED_COMMA);
//                //                result.append("\'");
//            }
//        }
//        return result.toString();
//    }
//
//    //////////////////////////// third part: skolem string
//    private String generateSkolemString(SkolemFunctionGenerator skolemFunction, Map<VariableJoinCondition, String> joinStrings) {
//        StringBuilder result = new StringBuilder();
//        List<String> groupStrings = new ArrayList<String>(joinStrings.values());
//        Collections.sort(groupStrings);
//
//        List<String> joinStringsForSkolem = new ArrayList<String>();
//        for (VariableJoinCondition variableJoinCondition : skolemFunction.getJoinConditions()) {
//            String joinString = generateStringForJoinCondition(variableJoinCondition);
//            int index = groupStrings.indexOf(joinString);
//            joinStringsForSkolem.add("" + index);
//        }
//        //        Collections.sort(joinStringsForSkolem);
//
//        int numberOfValues = joinStringsForSkolem.size();
//        for (int i = 0; i < numberOfValues; i++) {
//            result.append(joinStringsForSkolem.get(i));
//            if (i < numberOfValues - 1) {
//                result.append(",");
//            }
//        }
//        return result.toString();
//    }
}
