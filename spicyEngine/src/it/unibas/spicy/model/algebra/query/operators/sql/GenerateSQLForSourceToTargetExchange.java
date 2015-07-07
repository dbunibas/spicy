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
package it.unibas.spicy.model.algebra.query.operators.sql;

import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.generators.FunctionGenerator;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullValueGenerator;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateSQLForSourceToTargetExchange {

    private static Log logger = LogFactory.getLog(GenerateSQLForSourceToTargetExchange.class);
    private String CREATE_TABLE_OR_VIEW = GenerateSQL.CREATE_VIEW;
    private final static String CREATE_TABLE = GenerateSQL.CREATE_TABLE;
    private final static String SKOLEM_TABLE_NAME = GenerateSQL.SKOLEM_TABLE_NAME;
    private final static String SKOLEM_TABLE_COLUMN_ID = GenerateSQL.SKOLEM_TABLE_COLUMN_ID;
    private final static String SKOLEM_TABLE_COLUMN_SKOLEM = GenerateSQL.SKOLEM_TABLE_COLUMN_SKOLEM;
    private final static String SKOLEM_VIEW_NAME = GenerateSQL.SKOLEM_VIEW_NAME;
    private final static String NULL_VALUE = GenerateSQL.NULL_VALUE;
    private static final String INDENT = GenerateSQL.INDENT;
    private static final String DOUBLE_INDENT = GenerateSQL.DOUBLE_INDENT;
    private List<String> allViewsToDelete = new ArrayList<String>();
    private List<String> allTablesToDelete = new ArrayList<String>();
    private Map<FORule, List<String>> functorsMap = new HashMap<FORule, List<String>>();

    private int chainingStep;
    
    public String generateSQL(MappingTask mappingTask, int chainingStep) {
        this.chainingStep = chainingStep;
        if (mappingTask.getConfig().useCreateTableInSTExchange()) {
            CREATE_TABLE_OR_VIEW = GenerateSQL.CREATE_TABLE;
        }
        StringBuilder result = new StringBuilder();
        generateSQLForDuplications(result, mappingTask);
        generateSQLForExchange(result, mappingTask);
        return result.toString();
    }

    //// DUPLICATIONS
    private void generateSQLForDuplications(StringBuilder result, MappingTask mappingTask) {
        if (mappingTask.getSourceProxy().getDuplications().size() > 0) {
            result.append("\n------------------------------ DUPLICATIONS -----------------------------------\n");
        }
        for (Duplication duplication : mappingTask.getSourceProxy().getDuplications()) {
            result.append(generateSQLViewForDuplication(duplication)).append("\n");
        }
    }

    private String generateSQLViewForDuplication(Duplication duplication) {
        StringBuilder result = new StringBuilder();
        String originalRelation = GenerateSQL.getSourceSchemaName(chainingStep) + "." + duplication.getOriginalPath().getLastStep();
        String cloneRelation = GenerateSQL.getSourceSchemaName(chainingStep) + "." + duplication.getClonePath().getLastStep();
        result.append(CREATE_TABLE_OR_VIEW).append(cloneRelation).append(" AS \n");
        result.append(INDENT).append("select distinct *\n");
        result.append(INDENT).append("from ").append(originalRelation);
        result.append(";\n");
        allViewsToDelete.add(cloneRelation);
        return result.toString();
    }
    /////////////////

    private void generateSQLForExchange(StringBuilder result, MappingTask mappingTask) {
        List<FORule> tgds = mappingTask.getMappingData().getRewrittenRules();

        for (FORule tgd : tgds) {
            result.append(analyzeSourceView(tgd.getComplexSourceQuery(), mappingTask));
        }
        result.append("\n------------------------------  TGDS  -----------------------------------\n");

        for (FORule tgd : tgds) {
            result.append(generateSQLViewForRule(tgd, mappingTask));
        }
        if (functorsMap.size() > 0 && mappingTask.getConfig().useSkolemTable()) {
            result.append("\n--------------------  SKOLEM STRINGS FOR S-T EXCHANGE  --------------------------------\n\n");
            result.append(generateSQLViewForSkolemString()).append("\n");
        }
        if (functorsMap.size() > 0 && !mappingTask.getConfig().useHashTextForSkolems() && mappingTask.getConfig().useSkolemTable()) {
            result.append("\n---------------------------  SKOLEM TABLE FOR S-T EXCHANGE -----------------------------------\n");
            result.append(GenerateSQL.generateInsertInSkolemTable());
        }
        result.append("\n-----------------------  RESULT OF EXCHANGE ---------------------------\n\n");
        result.append(generateFinalResult(mappingTask));
    }

    private String analyzeSourceView(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        if (GenerateSQL.materializedViews.containsKey(query.getId())) {
            return "";
        }
        result.append(generatePositiveQuery(query, mappingTask));
        if (!query.getNegatedComplexQueries().isEmpty()) {
            for (NegatedComplexQuery negatedComplexQuery : query.getNegatedComplexQueries()) {
                result.append(analyzeSourceView(negatedComplexQuery.getComplexQuery(), mappingTask));
            }
            result.append(generateDifferenceForNegation(query, mappingTask));
        }
        return result.toString();
    }

    //////// POSITIVE QUERY
    private String generatePositiveQuery(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        if (!GenerateSQL.materializedViews.containsKey(query.getId())) {
            String viewName = GenerateSQL.sqlNameForPositiveView(query);
            result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
            if (query.getComplexQuery().hasIntersection()) {
                result.append(GenerateSQL.generateProjectionWithIntersection(query.getComplexQuery(), mappingTask, chainingStep));
            } else {
                result.append(GenerateSQL.generateProjectionWithoutIntersection(query.getComplexQuery(), mappingTask, chainingStep));
            }
            result.append(";\n");
            allViewsToDelete.add(viewName);
            GenerateSQL.materializedViews.put(query.getId(), viewName);
        }
        return result.toString();
    }

    private String generateDifferenceForNegation(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String viewName = GenerateSQL.sqlNameForViewWithIntersection(query);
        result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
        result.append(GenerateSQL.generateProjectionFromPositiveQuery(query));
        for (NegatedComplexQuery negatedComplexQuery : query.getNegatedComplexQueries()) {
            result.append("EXCEPT\n");
            if (negatedComplexQuery.getComplexQuery().getNegatedComplexQueries().isEmpty()) {
                String positiveViewName = GenerateSQL.sqlNameForPositiveView(query);
                result.append(GenerateSQL.generateProjectionFromNegation(positiveViewName, negatedComplexQuery));
            } else {
                result.append(GenerateSQL.generateProjectionFromNegationOnTargetRenaming(query, negatedComplexQuery));
            }
        }
        GenerateSQL.materializedViews.put(query.getId(), viewName);
        result.append(";\n");
        return result.toString();
    }

    ///////////// FINAL FO-RULES
    private String generateSQLViewForRule(FORule rule, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        if (isComplexRewriting(mappingTask)) {
            if (GenerateSQL.hasDifferences(rule)) {
                result.append(generateDifferenceForFinalRule(rule));
            }
        } else {
            if (GenerateSQL.hasDifferences(rule)) {
                result.append(generateDifferenceForNegationNoSelfJoins(rule, rule.getComplexSourceQuery()));
            }
        }
        result.append("\n");
        if (GenerateSQL.hasDifferences(rule)) {
            result.append(generateTargetValueViewWithDifference(rule, mappingTask));
        } else {
            result.append(generateTargetValueViewWithoutDifference(rule, mappingTask));
        }
        return result.toString();
    }

    private boolean isComplexRewriting(MappingTask mappingTask) {
        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()
                || (mappingTask.getLoadedTgds() != null) && hasLoadedTgdsWithNegation(mappingTask)) {
            return true;
        }
        return false;
    }

    private boolean hasLoadedTgdsWithNegation(MappingTask mappingTask) {
        for (FORule rule : mappingTask.getLoadedTgds()) {
            ComplexQueryWithNegations sourceView = rule.getComplexSourceQuery();
            if (!sourceView.getNegatedComplexQueries().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String generateDifferenceForFinalRule(FORule rule) {
        String viewName = GenerateSQL.sqlNameForRule(rule);
        StringBuilder result = new StringBuilder();
        String positiveViewName = "";
        positiveViewName = GenerateSQL.sqlNameForViewWithIntersection(rule.getComplexSourceQuery());
        if (!rule.getComplexSourceQuery().getNegatedComplexQueries().isEmpty()) {
            result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
            result.append("select distinct *\n");
            result.append("from ").append(positiveViewName).append("\n");
            for (NegatedComplexQuery negatedComplexQuery : rule.getComplexSourceQuery().getNegatedComplexQueries()) {
                result.append("EXCEPT\n");
                result.append(GenerateSQL.generateProjectionFromFinalRuleNegation(positiveViewName, negatedComplexQuery));
            }
        }
        result.append(";\n");
        return result.toString();
    }

    private String generateDifferenceForNegationNoSelfJoins(FORule rule, ComplexQueryWithNegations query) {
        String viewName = GenerateSQL.sqlNameForRule(rule);
        StringBuilder result = new StringBuilder();
        if (!query.getNegatedComplexQueries().isEmpty()) {
            result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
            result.append(GenerateSQL.generateProjectionForTgd(query, rule.getCoveredCorrespondences(), chainingStep));

            for (NegatedComplexQuery negatedComplexQuery : query.getNegatedComplexQueries()) {
                result.append("EXCEPT\n");
                result.append(addExceptClauseForNegation(negatedComplexQuery));
            }
        }
        result.append(";\n");
        return result.toString();
    }

    private String addExceptClauseForNegation(NegatedComplexQuery negatedComplexQuery) {
        StringBuilder result = new StringBuilder();
        String fromClause = GenerateSQL.generateFromClause(negatedComplexQuery.getComplexQuery().getVariables(), chainingStep);
        String whereClause = GenerateSQL.generateWhereClauseForNegation(negatedComplexQuery.getComplexQuery().getComplexQuery());
        result.append(GenerateSQL.projectionOnTargetValuesFromCorrespondences(negatedComplexQuery.getTargetEqualities().getRightCorrespondences(), fromClause, whereClause));
        result.append("\n");
        return result.toString();
    }

    private String generateTargetValueViewWithDifference(FORule tgd, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String viewName = GenerateSQL.tgdFinalSQLName(tgd);
        result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
        String fromClause = "from " + GenerateSQL.sqlNameForRule(tgd);
        String whereClause = "";
        result.append(projectionOnValues(mappingTask, tgd, fromClause, whereClause));
        result.append(";\n");
        allViewsToDelete.add(viewName);
        return result.toString();

    }

    private String generateTargetValueViewWithoutDifference(FORule tgd, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String viewName = GenerateSQL.tgdFinalSQLName(tgd);
        result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
        String fromClause = "";
        fromClause = GenerateSQL.generateFromClause(tgd.getComplexSourceQuery().getVariables(), chainingStep);
        String whereClause = GenerateSQL.generateWhereClause(tgd.getComplexSourceQuery().getComplexQuery());
        result.append(projectionOnValues(mappingTask, tgd, fromClause, whereClause));
        result.append(";\n");
        allViewsToDelete.add(viewName);
        return result.toString();
    }

    private String projectionOnValues(MappingTask mappingTask, FORule tgd, String fromClause, String whereClause) {
        StringBuilder result = new StringBuilder();
        result.append(INDENT).append("select distinct \n");

        List<SetAlias> generators = tgd.getTargetView().getGenerators();
        for (int i = 0; i < generators.size(); i++) {
            SetAlias generator = generators.get(i);
            List<VariablePathExpression> attributes = generator.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
            for (int j = 0; j < attributes.size(); j++) {
                VariablePathExpression attribute = attributes.get(j);
                IValueGenerator leafGenerator = getLeafGenerator(attribute, tgd, mappingTask, generator);

                if (leafGenerator instanceof FunctionGenerator) {
                    // apply functions
                    VariableCorrespondence correspondence;
                    correspondence = findCorrespondenceFromTargetPathWithSameId(attribute, tgd.getCoveredCorrespondences());
                    if (GenerateSQL.hasDifferences(tgd)) {
                        if (correspondence.getSourcePaths() != null) {
                            VariablePathExpression firstSourcePath = correspondence.getFirstSourcePath();
                            result.append(DOUBLE_INDENT).append(GenerateSQL.attributeNameInVariable(firstSourcePath));
                        } else {
                            String sourcePathName = correspondence.getSourceValue().toString();
                            sourcePathName = sourcePathName.replaceAll("\"", "\'");
                            result.append(DOUBLE_INDENT).append(sourcePathName);
                        }
                    } else {
                        if (correspondence.getSourcePaths() != null) {
                            VariablePathExpression firstSourcePath = correspondence.getFirstSourcePath();
                            result.append(DOUBLE_INDENT).append(GenerateSQL.attributeNameWithVariable(firstSourcePath));
                        } else {
                            String sourcePathName = correspondence.getSourceValue().toString();
                            sourcePathName = sourcePathName.replaceAll("\"", "\'");
                            result.append(DOUBLE_INDENT).append(sourcePathName);
                        }
                    }
                } else if (leafGenerator instanceof NullValueGenerator) {
                    result.append(DOUBLE_INDENT).append(NULL_VALUE);
                } else if (leafGenerator instanceof SkolemFunctionGenerator) {
                    result.append(DOUBLE_INDENT).append(mappingTask.getDBMSHandler().skolemString((SkolemFunctionGenerator) leafGenerator, mappingTask));
                    addGeneratorToFunctorMap(attribute, tgd);
                }

                result.append(" as ").append(GenerateSQL.attributeNameInVariable(attribute));
                if (j != attributes.size() - 1 || i != generators.size() - 1) {
                    result.append(",\n");
                }


            }
        }
        result.append("\n");
        result.append(INDENT).append(fromClause);
        if (!whereClause.equals("")) {
            result.append(INDENT).append(whereClause);
        }
        return result.toString();
    }

    private VariableCorrespondence findCorrespondenceFromTargetPathWithSameId(VariablePathExpression targetPath, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return correspondence;
            }
        }
        return null;
    }

    private IValueGenerator getLeafGenerator(VariablePathExpression attributePath, FORule tgd, MappingTask mappingTask, SetAlias variable) {
        INode attributeNode = attributePath.getLastNode(mappingTask.getTargetProxy().getIntermediateSchema());
        INode leafNode = attributeNode.getChild(0);
        PathExpression leafPath = new GeneratePathExpression().generatePathFromRoot(leafNode);
        Map<PathExpression, IValueGenerator> generatorsForVariable = tgd.getGenerators(mappingTask).getGeneratorsForVariable(variable);
//        //** added to avoid exceptions in XML scenarios
        if (generatorsForVariable == null) {
            return NullValueGenerator.getInstance();
        }
        for (PathExpression pathExpression : generatorsForVariable.keySet()) {
            IValueGenerator generator = generatorsForVariable.get(pathExpression);
            if (pathExpression.equalsUpToClones(leafPath)) {
                return generator;
            }
        }
        return null;
    }

    private void addGeneratorToFunctorMap(VariablePathExpression attributePath, FORule tgd) {
        if (logger.isDebugEnabled()) logger.debug("FunctorsMap = " + functorsMap);
        String viewName = tgd.targetVariablesNames();
        if (logger.isDebugEnabled()) logger.debug("ViewName = " + viewName);
        List<String> skolemFunctors = functorsMap.get(tgd);
        if (logger.isDebugEnabled()) logger.debug("List of attributeNames = " + skolemFunctors);
        if (skolemFunctors == null) {
            if (logger.isDebugEnabled()) logger.debug("Creating a new list of skolem functors");
            skolemFunctors = new ArrayList<String>();
            functorsMap.put(tgd, skolemFunctors);
        }
        String attributeName = GenerateSQL.attributeNameInVariable(attributePath);
        if (!skolemFunctors.contains(attributeName)) {
            if (logger.isDebugEnabled()) logger.debug("Adding attribute: " + attributeName + " in list: " + skolemFunctors);
            skolemFunctors.add(attributeName);
        }
        if (logger.isTraceEnabled()) logger.trace("Updated functorsMap = " + functorsMap);
    }

    private String generateSQLViewForSkolemString() {
        StringBuilder result = new StringBuilder();
        result.append(CREATE_TABLE_OR_VIEW).append(SKOLEM_VIEW_NAME).append(" AS \n");
        int mapSize = 0;
        for (FORule tgd : functorsMap.keySet()) {
            List<String> attributeNames = functorsMap.get(tgd);
            for (int j = 0; j < attributeNames.size(); j++) {
                result.append(INDENT).append("select \n");
                String attributeName = attributeNames.get(j);
                result.append(DOUBLE_INDENT).append(attributeName);
                result.append(" as ").append(SKOLEM_TABLE_COLUMN_SKOLEM);
                result.append("\n");
                result.append(INDENT).append("from ").append(GenerateSQL.tgdFinalSQLName(tgd));
                if (j != attributeNames.size() - 1) {
                    result.append("\n").append(INDENT).append("UNION\n");
                }
            }
            if (mapSize != functorsMap.size() - 1) {
                result.append("\n").append(INDENT).append("UNION\n");
            }
            mapSize++;
        }
        result.append(";\n\n");
        allViewsToDelete.add(SKOLEM_VIEW_NAME);
        return result.toString();
    }

    /////////////////////////////////    FINAL INSERTS  /////////////////////////////////////////////////
    private String generateFinalResult(MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        Map<String, List<SetAlias>> relevantVariablesForRelationName = findRelevantVariablesForRelationName(mappingTask);
        Map<SetAlias, List<FORule>> relevantRulesForVariable = findRelevantRulesForVariable(mappingTask);
        if (logger.isDebugEnabled()) {
            logger.debug("------------------- Generating final result --------------\n** Relevant rules for variable:\n" + relevantRulesForVariable);
        }
        for (String relationName : relevantVariablesForRelationName.keySet()) {
            generateInsertsForRelation(relationName, relevantVariablesForRelationName, relevantRulesForVariable, mappingTask, result);
        }
        result.append(";\n");
        return result.toString();
    }

    private void generateInsertsForRelation(String relationName, Map<String, List<SetAlias>> relevantVariablesForRelationName, Map<SetAlias, List<FORule>> relevantRulesForVariable, MappingTask mappingTask, StringBuilder result) {
        int numberOfRelevantRules = countRelevantRules(relationName, relevantVariablesForRelationName, relevantRulesForVariable);
        if (numberOfRelevantRules == 0) {
            return;
        }
        List<SetAlias> relevantVariablesForRelation = relevantVariablesForRelationName.get(relationName);
        String newViewName = GenerateSQL.finalSqlNameAfterExchange(relevantVariablesForRelation.get(0));
        allViewsToDelete.add(newViewName);
        if (mappingTask.getConfig().useSkolemTable()) {
            result.append(CREATE_TABLE_OR_VIEW).append(newViewName).append(" AS \n");
        } else {
            result.append("insert into ").append(relationName).append("\n");
        }
        for (int j = 0; j < relevantVariablesForRelation.size(); j++) {
            SetAlias relevantVariable = relevantVariablesForRelation.get(j);
            generateInsertForVariableInTgd(relevantRulesForVariable, relevantVariable, mappingTask, result);
        }
        result.delete(result.length() - 1 - (INDENT.length() + "UNION\n".length()), result.length() - 1);
        result.append(";\n");
    }

    private void generateInsertForVariableInTgd(Map<SetAlias, List<FORule>> relevantRulesForVariable, SetAlias setVariable, MappingTask mappingTask, StringBuilder result) {
        List<FORule> relevantRules = relevantRulesForVariable.get(setVariable);
        for (int i = 0; i < relevantRules.size(); i++) {
            FORule tgd = relevantRules.get(i);
            FORule tgdFunctor = checkVariableContainmentInSkolemFunctor(tgd);
            if (logger.isDebugEnabled()) {
                logger.debug("tgdFunctor: " + tgdFunctor);
            }
            for (SetAlias targetVariableInTgd : tgd.getTargetView().getVariables()) {
                if (targetVariableInTgd.getAbsoluteBindingPathExpression().equals(setVariable.getAbsoluteBindingPathExpression())) {
                    if (tgdFunctor != null && mappingTask.getConfig().useSkolemTable()) {
                        result.append(generateSQLViewForTargetRelationWithSkolem(targetVariableInTgd, tgdFunctor, tgd, mappingTask));
                    } else {
                        result.append(generateSQLViewForTargetRelationWithoutSkolem(targetVariableInTgd, tgd, mappingTask));
                    }
                    result.append("\n").append(INDENT).append("UNION\n");
                }
            }
        }
    }

    private Map<String, List<SetAlias>> findRelevantVariablesForRelationName(MappingTask mappingTask) {
        Map<String, List<SetAlias>> result = new HashMap<String, List<SetAlias>>();
        for (SetAlias variable : mappingTask.getTargetProxy().getMappingData().getVariables()) {
            String relationName = GenerateSQL.targetRelationName(variable, mappingTask, chainingStep);
            List<SetAlias> relevantVariables = result.get(relationName);
            if (relevantVariables == null) {
                relevantVariables = new ArrayList<SetAlias>();
                result.put(relationName, relevantVariables);
            }
            relevantVariables.add(variable);
        }
        return result;
    }

    private Map<SetAlias, List<FORule>> findRelevantRulesForVariable(MappingTask mappingTask) {
        Map<SetAlias, List<FORule>> result = new HashMap<SetAlias, List<FORule>>();
        for (SetAlias variable : mappingTask.getTargetProxy().getMappingData().getVariables()) {
            List<FORule> relevantRules = result.get(variable);
            if (relevantRules == null) {
                relevantRules = new ArrayList<FORule>();
                result.put(variable, relevantRules);
            }
            relevantRules.addAll(findRelevantTGDs(variable, mappingTask));
        }
        return result;
    }

    private List<FORule> findRelevantTGDs(SetAlias targetVariable, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) {
            logger.debug("******* Cheching relevance of tgds for variable " + targetVariable);
        }
        List<FORule> tgds = mappingTask.getMappingData().getRewrittenRules();
        Collections.sort(tgds);
        List<FORule> result = new ArrayList<FORule>();
        for (int i = tgds.size() - 1; i >= 0; i--) {
            FORule tgd = tgds.get(i);
            if (logger.isDebugEnabled()) {
                logger.debug("++ Analyzing tgd: " + tgd);
            }
            if (isRelevant(targetVariable, tgd.getTargetView().getVariables())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("++ Tgd is relevant, adding...");
                }
                result.add(tgd);
            }
        }
        return result;
    }

    private boolean isRelevant(SetAlias variable, List<SetAlias> variables) {
        for (SetAlias setVariable : variables) {
            //if (variable.equals(setVariable)){
            if (variable.getAbsoluteBindingPathExpression().equals(setVariable.getAbsoluteBindingPathExpression())) {
                return true;
            }
        }
        return false;
    }

    private int countRelevantRules(String relationName, Map<String, List<SetAlias>> relevantVariablesForRelationName, Map<SetAlias, List<FORule>> relevantRulesForVariable) {
        int result = 0;
        List<SetAlias> variables = relevantVariablesForRelationName.get(relationName);
        for (SetAlias variable : variables) {
            List<FORule> rules = relevantRulesForVariable.get(variable);
            if (!rules.isEmpty()) {
                result += rules.size();
            }
        }
        return result;
    }

    private FORule checkVariableContainmentInSkolemFunctor(FORule tgd) {
        Set<FORule> tgds = functorsMap.keySet();
        if (logger.isDebugEnabled()) {
            logger.debug("Checking tgd: " + tgd.getId() + " in " + functorsMap);
        }
        for (FORule tgdWithFunctors : tgds) {
            if (tgd.getId().equals(tgdWithFunctors.getId())) {
                return tgdWithFunctors;
            }
        }
        return null;
    }

    private String generateSQLViewForTargetRelationWithSkolem(SetAlias targetVariable, FORule tgdFunctor, FORule tgd, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String fromViewName = GenerateSQL.tgdFinalSQLName(tgd);
        List<String> attributeSkolemNames = functorsMap.get(tgdFunctor);
        List<VariablePathExpression> variableAttributePaths = targetVariable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        int skolemTableCounter = 0;
        List<String> joinAttributes = new ArrayList<String>();
        result.append(INDENT).append("select distinct\n");
        for (int j = 0; j < variableAttributePaths.size(); j++) {
            VariablePathExpression attributePath = variableAttributePaths.get(j);
            SetAlias originalVariable = findVariableWithSameIdInTgd(attributePath, tgd);
            String columnName = originalVariable.toShortString() + attributePath.getLastStep();

            if (logger.isDebugEnabled()) {
                logger.debug("Checking attributeName = " + columnName);
            }
            if (!attributeSkolemNames.contains(columnName)) {
                String valueToCast = fromViewName + "." + columnName;
                String castedColumnName = GenerateSQL.generateCasting(attributePath, valueToCast, mappingTask);
                result.append(DOUBLE_INDENT).append(castedColumnName);
            } else {
                String skolemIdColumn = "";
                if (mappingTask.getConfig().useHashTextForSkolems()) {
                    skolemIdColumn = SKOLEM_TABLE_COLUMN_SKOLEM;
                } else {
                    skolemIdColumn = SKOLEM_TABLE_COLUMN_ID + " + " + GenerateSQL.SKOLEM_ID_MIN_VALUE;
                }
                String valueToCast = GenerateSQL.renameSkolemTable(skolemTableCounter) + "." + skolemIdColumn;
                String castedColumnName = GenerateSQL.generateCasting(attributePath, valueToCast, mappingTask);
                result.append(DOUBLE_INDENT).append(castedColumnName);
                joinAttributes.add(columnName);
                skolemTableCounter++;
            }
            result.append(" as ").append(columnName);
            if (j != variableAttributePaths.size() - 1) {
                result.append(",\n");
            }
        }
        result.append("\n");
        result.append(INDENT).append("from \n");
        result.append(DOUBLE_INDENT).append(fromViewName).append("\n");
        for (int j = 0; j < joinAttributes.size(); j++) {
            String skolemTable = GenerateSQL.renameSkolemTable(j);
            String tableWithSkolems = "";
            if (mappingTask.getConfig().useHashTextForSkolems()) {
                tableWithSkolems = SKOLEM_VIEW_NAME;
            } else {
                tableWithSkolems = SKOLEM_TABLE_NAME;
            }
            result.append(DOUBLE_INDENT).append("left join ").append(tableWithSkolems).append(" as ").append(skolemTable).append("\n");
            String attributeName = joinAttributes.get(j);
            result.append(DOUBLE_INDENT).append("on ").append(fromViewName).append(".").append(attributeName).append(" = ").append(skolemTable).append(".").append(SKOLEM_TABLE_COLUMN_SKOLEM);
            if (j != joinAttributes.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String generateSQLViewForTargetRelationWithoutSkolem(SetAlias targetVariable, FORule tgd, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String fromViewName = GenerateSQL.tgdFinalSQLName(tgd);
        List<VariablePathExpression> variableAttributePaths = targetVariable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        result.append(INDENT).append("select distinct\n");
        for (int j = 0; j < variableAttributePaths.size(); j++) {
            VariablePathExpression attributePath = variableAttributePaths.get(j);
            String columnName = targetVariable.toShortString() + attributePath.getLastStep();
            String valueToCast = fromViewName + "." + columnName;
            String castedColumnName = GenerateSQL.generateCasting(attributePath, valueToCast, mappingTask);
            result.append(DOUBLE_INDENT).append(castedColumnName);
            result.append(" as ").append(columnName);
            if (j != variableAttributePaths.size() - 1) {
                result.append(",\n");
            }
        }
        result.append("\n");
        result.append(INDENT).append("from \n");
        result.append(DOUBLE_INDENT).append(fromViewName);
        return result.toString();
    }

    private SetAlias findVariableWithSameIdInTgd(VariablePathExpression attributePath, FORule tgd) {
        for (SetAlias variable : tgd.getTargetView().getVariables()) {
            if (variable.hasSameId(attributePath.getStartingVariable())) {
                return variable;
            }
        }
        return null;
    }

    public List<String> getAllViewsToDelete() {
        return allViewsToDelete;
    }

    public List<String> getAllTablesToDelete() {
        return allTablesToDelete;
    }
}
