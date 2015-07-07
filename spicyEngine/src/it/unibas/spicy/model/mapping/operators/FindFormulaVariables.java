/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Donatello Santoro - donatello.santoro@gmail.com

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
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FormulaVariableMaps;
import it.unibas.spicy.model.mapping.IIdentifiable;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.SourceEqualities;
import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.mapping.VariableOccurrence;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindFormulaVariables {

    private static Log logger = LogFactory.getLog(FindFormulaVariables.class);
    private FindFormulaVariablesUtility utility = new FindFormulaVariablesUtility();

    public FormulaVariableMaps findFormulaVariables(FORule rule, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("********** Finding variables for rule: " + rule);
        FormulaVariableMaps variableMaps = new FormulaVariableMaps();
        List<IIdentifiable> stack = new ArrayList<IIdentifiable>();
        FindVariablesQueryVisitor visitor = new FindVariablesQueryVisitor(rule, variableMaps, stack, mappingTask);
        ComplexQueryWithNegations sourceQuery = rule.getComplexSourceQuery();
        sourceQuery.accept(visitor);
        List<FormulaVariable> universalVariables = utility.getUniversalVariablesForChild(sourceQuery, variableMaps, stack);
        variableMaps.setUniversalVariables(rule.getId(), rule, universalVariables);
        for (FormulaVariable universalVariable : universalVariables) {
            setTargetPaths(universalVariable, rule, mappingTask);
        }
        List<FormulaVariable> existentialVariables = findExistentialVariables(rule, universalVariables, mappingTask);
        variableMaps.setExistentialVariables(rule.getId(), rule, existentialVariables);
        return variableMaps;
    }

    ////// EXISTENTIAL
    private List<FormulaVariable> findExistentialVariables(FORule tgd, List<FormulaVariable> universalVariables, MappingTask mappingTask) {
        List<FormulaVariable> existentialVariables = new ArrayList<FormulaVariable>();
        FindJoinGroups joinFinder = new FindJoinGroups();
        List<VariableJoinCondition> allJoins = tgd.getTargetView().getAllJoinConditions();
        List<VariableJoinCondition> simplifiedJoins = utility.simplifyJoins(allJoins);
        List<JoinGroup> joinGroups = joinFinder.findJoinGroups(simplifiedJoins);
        for (SetAlias variable : tgd.getTargetView().getGenerators()) {
            for (VariablePathExpression pathExpression : variable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema())) {
                if (!universal(pathExpression, tgd, universalVariables) && !utility.inJoinGroup(pathExpression, joinGroups)) {
                    FormulaVariable newVariable = new FormulaVariable(pathExpression, true);
                    existentialVariables.add(newVariable);
                }
            }
        }
        for (JoinGroup joinGroup : joinGroups) {
            List<FormulaVariable> joinVariables = utility.createVariablesForJoinGroup(joinGroup, true);
            existentialVariables.addAll(joinVariables);
        }
        utility.removeCopies(existentialVariables);
        return existentialVariables;
    }

    private void setTargetPaths(FormulaVariable variable, FORule tgd, MappingTask mappingTask) {
        for (VariablePathExpression sourcePath : variable.getOriginalSourceOccurrencePaths()) {
            for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
                VariablePathExpression targetPath = correspondence.getTargetPath();
                if (SpicyEngineUtility.containsPathWithSameVariableId(correspondence.getSourcePaths(), sourcePath)) {
                    if (!SpicyEngineUtility.containsPathWithSameVariableId(variable.getTargetOccurrencePaths(), targetPath)) {
                        variable.addTargetOccurrencePath(correspondence.getTargetPath());
                        if (!correspondence.isValueCopy()) {
                            Expression tranformationFunctionClone = correspondence.getTransformationFunction().clone();
                            variable.addTransformationFunction(targetPath, tranformationFunctionClone);
                        }
                    }
                }
            }
        }
    }

    private boolean universal(VariablePathExpression pathExpression, FORule tgd, List<FormulaVariable> universalVariables) {
        for (FormulaVariable universalVariable : universalVariables) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(universalVariable.getTargetOccurrencePaths(), pathExpression)) {
                return true;
            }
        }
        return false;
    }
}

class FindVariablesQueryVisitor implements IQueryVisitor {

    private static Log logger = LogFactory.getLog(FindVariablesQueryVisitor.class);
    private static final String EQUALS = "==";
    private FindFormulaVariablesUtility utility = new FindFormulaVariablesUtility();
    private FindJoinGroups joinFinder = new FindJoinGroups();
    private FormulaVariableMaps variableMaps;
    private MappingTask mappingTask;
    private List<IIdentifiable> stack;

    public FindVariablesQueryVisitor(FORule rule, FormulaVariableMaps variableMaps, List<IIdentifiable> stack, MappingTask mappingTask) {
        this.variableMaps = variableMaps;
        this.mappingTask = mappingTask;
        this.stack = stack;
        this.stack.add(0, rule);
    }

    public void visitComplexQueryWithNegation(ComplexQueryWithNegations query) {
        if (logger.isDebugEnabled()) logger.debug("Generating variables for complex query with negations: " + query);
        stack.add(0, query);
        ComplexConjunctiveQuery complexConjunctiveQuery = query.getComplexQuery();
        complexConjunctiveQuery.accept(this);
        List<FormulaVariable> universalVariables = utility.getUniversalVariablesForChild(complexConjunctiveQuery, variableMaps, stack);
        variableMaps.setUniversalVariables(utility.generateId(stack), query, universalVariables);
        for (NegatedComplexQuery negatedQuery : query.getNegatedComplexQueries()) {
            negatedQuery.accept(this);
        }
        stack.remove(0);
    }

    public void visitComplexConjunctiveQuery(ComplexConjunctiveQuery query) {
        if (logger.isDebugEnabled()) logger.debug("Generating variables for complex conjunctive query: " + query);
        stack.add(0, query);
        for (SimpleConjunctiveQuery simpleQuery : query.getConjunctions()) {
            simpleQuery.accept(this);
        }
        List<VariableJoinCondition> variableGroupJoins = new ArrayList<VariableJoinCondition>();
        List<VariableJoinCondition> equalityGeneratingJoins = new ArrayList<VariableJoinCondition>();
        List<VariableJoinCondition> allRelevantJoins = findAllRelevantJoins(query);
        List<VariableCorrespondence> allRelevantCorrespondences = findAllRelevantCorrespondences(query);
        classifyJoinConditions(allRelevantJoins, allRelevantCorrespondences, variableGroupJoins, equalityGeneratingJoins);
        if (logger.isTraceEnabled()) logger.debug("Variable group joins: " + variableGroupJoins);
        List<FormulaVariable> universalVariables = generateUniversalVariables(variableGroupJoins, query);
        variableMaps.setUniversalVariables(utility.generateId(stack), query, universalVariables);
        List<Expression> functionEqualities = generateFunctionEqualities(equalityGeneratingJoins, universalVariables, query.getAllCorrespondences());
        variableMaps.setEqualities(utility.generateId(stack), query, functionEqualities);
        if (query.hasIntersection()) {
            SimpleConjunctiveQuery intersectionQuery = query.getConjunctionForIntersection();
            intersectionQuery.accept(this);
            generateExistentialVariablesAndEqualitiesForIntersection(query, universalVariables);
        }
        stack.remove(0);
    }

    private List<VariableJoinCondition> findAllRelevantJoins(ComplexConjunctiveQuery query) {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>(query.getAllJoins());
        if (stack.size() >= 3) {
            Object ancestor = stack.get(2);
            if (ancestor instanceof NegatedComplexQuery) {
                NegatedComplexQuery negatedQuery = (NegatedComplexQuery) ancestor;
                result.addAll(negatedQuery.getAdditionalCyclicJoins());
            }
        }
        return result;
    }

    private List<VariableCorrespondence> findAllRelevantCorrespondences(ComplexConjunctiveQuery query) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>(query.getAllCorrespondences());
        if (stack.size() >= 3) {
            Object ancestor = stack.get(2);
            if (ancestor instanceof NegatedComplexQuery) {
                NegatedComplexQuery negatedQuery = (NegatedComplexQuery) ancestor;
                result.addAll(negatedQuery.getCorrespondencesForJoin());
            }
        }
        return result;
    }

    private List<FormulaVariable> generateUniversalVariables(List<VariableJoinCondition> variableGroupJoins, ComplexConjunctiveQuery query) {
        if (logger.isTraceEnabled()) logger.debug("Equating variables for complex query: " + query + "\nwith joins: " + variableGroupJoins);
        List<FormulaVariable> universalVariables = new ArrayList<FormulaVariable>();
        List<VariableJoinCondition> simplifiedJoins = utility.simplifyJoins(variableGroupJoins);
        List<JoinGroup> joinGroups = joinFinder.findJoinGroups(simplifiedJoins);
        for (SimpleConjunctiveQuery simpleConjunctiveQuery : query.getConjunctions()) {
            List<FormulaVariable> variablesForConjunction = utility.getUniversalVariablesForChild(simpleConjunctiveQuery, variableMaps, stack);
            for (FormulaVariable formulaVariable : variablesForConjunction) {
                if (logger.isTraceEnabled()) logger.debug("Analyzing variable: " + formulaVariable);
                FormulaVariable existingVariable = findVariableForSameJoinGroup(formulaVariable, universalVariables, joinGroups);
                if (existingVariable == null) {
                    if (logger.isTraceEnabled()) logger.debug("Found existing variable: " + existingVariable);
                    universalVariables.add(formulaVariable.clone());
                } else {
                    addStandardOccurrences(existingVariable, formulaVariable);
                }
            }
        }
        return universalVariables;
    }

    public void visitSimpleConjunctiveQuery(SimpleConjunctiveQuery query) {
        if (logger.isDebugEnabled()) logger.debug("Generating variables for simple conjunctive query: " + query);
        stack.add(0, query);
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        List<VariableJoinCondition> allJoins = query.getAllJoinConditions();
        List<VariableJoinCondition> simplifiedJoins = utility.simplifyJoins(allJoins);
        List<JoinGroup> joinGroups = joinFinder.findJoinGroups(simplifiedJoins);
        if (logger.isTraceEnabled()) logger.debug("Simplified joins: " + simplifiedJoins);
        if (logger.isTraceEnabled()) logger.debug("Join Groups: " + joinGroups);
        for (SetAlias variable : query.getGenerators()) {
            List<VariablePathExpression> variableAttributes = findVariableAttributes(variable, mappingTask);
            for (VariablePathExpression pathExpression : variableAttributes) {
                if (!utility.inJoinGroup(pathExpression, joinGroups)) {
                    FormulaVariable newVariable = new FormulaVariable(pathExpression, false);
                    result.add(newVariable);
                }
            }
        }
        for (JoinGroup joinGroup : joinGroups) {
            List<FormulaVariable> joinVariables = utility.createVariablesForJoinGroup(joinGroup, false);
            result.addAll(joinVariables);
        }
        utility.removeCopies(result);
        if (logger.isDebugEnabled()) logger.debug("Final result:\n" + utility.printVariables(result));
        variableMaps.setUniversalVariables(utility.generateId(stack), query, result);
        stack.remove(0);
    }

    public void visitNegatedComplexQuery(NegatedComplexQuery negatedQuery) {
        if (logger.isDebugEnabled()) logger.debug("Generating variables for negated complex query: " + negatedQuery);
        stack.add(0, negatedQuery);
        ComplexQueryWithNegations queryToNegate = negatedQuery.getComplexQuery();
        queryToNegate.accept(this);
        generateExistentialVariablesAndEqualitiesForNegation(negatedQuery);
        stack.remove(0);
    }

    public Object getResult() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private List<VariablePathExpression> findVariableAttributes(SetAlias variable, MappingTask mappingTask) {
        return variable.getAttributes(mappingTask.getSourceProxy().getIntermediateSchema());
    }

    ///////////////////  JOIN CONDITIONS
    private void classifyJoinConditions(List<VariableJoinCondition> joins, List<VariableCorrespondence> correspondences, List<VariableJoinCondition> variableGroupJoins, List<VariableJoinCondition> equalityGeneratingJoins) {
        for (VariableJoinCondition joinCondition : joins) {
            VariableJoinCondition sourceJoin = generateSourceJoin(joinCondition, correspondences);
            if (sourceJoin != null) {
                variableGroupJoins.add(sourceJoin);
            } else {
                equalityGeneratingJoins.add(joinCondition);
            }
        }
    }

    private VariableJoinCondition generateSourceJoin(VariableJoinCondition joinCondition, List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> sourceFromPaths = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> sourceToPaths = new ArrayList<VariablePathExpression>();
        for (int i = 0; i < joinCondition.getFromPaths().size(); i++) {
            VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
            VariablePathExpression toPath = joinCondition.getToPaths().get(i);
            VariableCorrespondence fromCorrespondence = findCorrespondence(fromPath, correspondences);
            VariableCorrespondence toCorrespondence = findCorrespondence(toPath, correspondences);
            if (!fromCorrespondence.isValueCopy() || !toCorrespondence.isValueCopy()) {
                return null;
            }
            sourceFromPaths.add(fromCorrespondence.getFirstSourcePath());
            sourceToPaths.add(toCorrespondence.getFirstSourcePath());
        }
        VariableJoinCondition sourceJoin = new VariableJoinCondition(sourceFromPaths, sourceToPaths, joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return sourceJoin;
    }

    private VariableCorrespondence findCorrespondence(VariablePathExpression joinPath, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence variableCorrespondence : correspondences) {
            if (variableCorrespondence.getTargetPath().equalsAndHasSameVariableId(joinPath)) {
                return variableCorrespondence;
            }
        }
        throw new IllegalArgumentException("Unable to find a correspondence for join path: " + joinPath + " in " + correspondences);
    }

    private FormulaVariable findVariableForSameJoinGroup(FormulaVariable formulaVariable, List<FormulaVariable> variableList, List<JoinGroup> joinGroups) {
        for (FormulaVariable existingVariable : variableList) {
            for (JoinGroup joinGroup : joinGroups) {
                if (hasOccurrencesInJoinGroup(formulaVariable, joinGroup) && hasOccurrencesInJoinGroup(existingVariable, joinGroup)) {
                    return existingVariable;
                }
            }
        }
        return null;
    }

    private boolean hasOccurrencesInJoinGroup(FormulaVariable formulaVariable, JoinGroup joinGroup) {
        for (VariablePathExpression sourcePath : formulaVariable.getSourceOccurrencePaths()) {
            if (utility.inJoinGroup(sourcePath, joinGroup)) {
                return true;
            }
        }
        return false;
    }

    private List<Expression> generateFunctionEqualities(List<VariableJoinCondition> equalityGeneratingJoins, List<FormulaVariable> universalVariables, List<VariableCorrespondence> correspondences) {
        List<Expression> result = new ArrayList<Expression>();
        for (VariableJoinCondition equalityJoin : equalityGeneratingJoins) {
            List<Expression> equalitiesForJoin = generateFunctionEqualities(equalityJoin, correspondences);
            result.addAll(equalitiesForJoin);
        }
        return result;
    }

    private List<Expression> generateFunctionEqualities(VariableJoinCondition equalityJoin, List<VariableCorrespondence> correspondences) {
        List<Expression> result = new ArrayList<Expression>();
        for (int i = 0; i < equalityJoin.getFromPaths().size(); i++) {
            VariablePathExpression fromPath = equalityJoin.getFromPaths().get(i);
            VariablePathExpression toPath = equalityJoin.getToPaths().get(i);
            VariableCorrespondence fromCorrespondence = findCorrespondence(fromPath, correspondences);
            VariableCorrespondence toCorrespondence = findCorrespondence(toPath, correspondences);
            Expression fromExpression = fromCorrespondence.getTransformationFunction();
            Expression toExpression = toCorrespondence.getTransformationFunction();
            String equalityString = fromExpression.toString() + EQUALS + toExpression.toString();
            Expression functionEquality = new Expression(equalityString);
            result.add(functionEquality);
        }
        return result;
    }

    /////////////////////////////       INTERSECTION     /////////////////////////////////////////////////
    private void generateExistentialVariablesAndEqualitiesForIntersection(ComplexConjunctiveQuery query, List<FormulaVariable> externalVariables) {
        SimpleConjunctiveQuery intersectionQuery = query.getConjunctionForIntersection();
        List<FormulaVariable> internalVariables = utility.getUniversalVariablesForChild(intersectionQuery, variableMaps, stack);
        List<FormulaVariable> existentialVariables = new ArrayList<FormulaVariable>(internalVariables);
        List<Expression> equalities = new ArrayList<Expression>();
        TargetEqualities targetEqualities = query.getIntersectionEqualities();
        for (int i = 0; i < targetEqualities.size(); i++) {
            VariableCorrespondence leftCorrespondence = targetEqualities.getLeftCorrespondences().get(i);
            VariableCorrespondence rightCorrespondence = targetEqualities.getRightCorrespondences().get(i);
            if (leftCorrespondence.isValueCopy() && rightCorrespondence.isValueCopy()) {
                Expression expression = modifyVariablesOrGenerateEquality(leftCorrespondence.getFirstSourcePath(), rightCorrespondence.getFirstSourcePath(), externalVariables, existentialVariables, intersectionQuery);
                if (expression != null) {
                    equalities.add(expression);
                }
            } else {
                equalities.add(generateEqualityForCorrespondences(leftCorrespondence, rightCorrespondence));
            }
        }
        stack.add(0, intersectionQuery);
        variableMaps.setExistentialVariables(utility.generateId(stack), intersectionQuery, existentialVariables);
        variableMaps.setEqualities(utility.generateId(stack), intersectionQuery, equalities);
        stack.remove(0);
    }

    private Expression modifyVariablesOrGenerateEquality(VariablePathExpression leftSourcePath, VariablePathExpression rightSourcePath,
            List<FormulaVariable> externalVariables, List<FormulaVariable> existentialVariables, IIdentifiable view) {
        if (logger.isDebugEnabled()) logger.debug("Handling equality of paths: " + leftSourcePath + " and " + rightSourcePath);
        FormulaVariable leftExternalVariable = findVariableForPath(leftSourcePath, externalVariables);
        FormulaVariable rightExternalVariable = findVariableForPath(rightSourcePath, externalVariables);
        FormulaVariable rightInternalVariable = findVariableForPath(rightSourcePath, existentialVariables);
        if (logger.isDebugEnabled()) logger.debug("Left external variable: " + (leftExternalVariable == null ? "null" : leftExternalVariable.toLongString()));
        if (logger.isDebugEnabled()) logger.debug("Right external variable: " + (rightExternalVariable == null ? "null" : rightExternalVariable.toLongString()));
        if (logger.isDebugEnabled()) logger.debug("Right internal variable: " + (rightInternalVariable == null ? "null" : rightInternalVariable.toLongString()));
        if (rightInternalVariable != null) {
            addAddedOccurrences(leftExternalVariable, rightInternalVariable);
            existentialVariables.remove(rightInternalVariable);
            if (logger.isDebugEnabled()) logger.debug("Removing existential variable. New existential variables:\n" + SpicyEngineUtility.printVariableList(existentialVariables));
            if (logger.isDebugEnabled()) logger.debug("New external variables:\n" + SpicyEngineUtility.printVariableList(externalVariables));
            return null;
        } else {
            if (!rightExternalVariable.hasSameId(leftExternalVariable)) {
                Expression equality = generateEqualityForSourcePaths(leftSourcePath, rightSourcePath);
                if (logger.isDebugEnabled()) logger.debug("External variables are different, generating equality:  " + equality);
                return equality;
            } else {
                if (logger.isDebugEnabled()) logger.debug("External variables are the same, returning null");
                return null;
            }
        }
    }

    private Expression generateEqualityForSourcePaths(VariablePathExpression leftSourcePath, VariablePathExpression rightSourcePath) {
        String equalityString = leftSourcePath.toString() + EQUALS + rightSourcePath.toString();
        Expression equality = new Expression(equalityString);
        return equality;
    }

    private Expression generateEqualityForCorrespondences(VariableCorrespondence leftCorrespondence, VariableCorrespondence rightCorrespondence) {
        String equalityString = leftCorrespondence.getTransformationFunction().toString() + EQUALS + rightCorrespondence.getTransformationFunction().toString();
        Expression equality = new Expression(equalityString);
        return equality;
    }

    private FormulaVariable findVariableForPath(VariablePathExpression sourcePath, List<FormulaVariable> variables) {
        for (FormulaVariable variable : variables) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(variable.getOriginalSourceOccurrencePaths(), sourcePath)) {
                return variable;
            }
        }
        for (FormulaVariable variable : variables) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(variable.getSourceOccurrencePaths(), sourcePath)) {
                return variable;
            }
        }
        return null;
    }

    /////////////////////////////       NEGATION     /////////////////////////////////////////////////
    private void generateExistentialVariablesAndEqualitiesForNegation(NegatedComplexQuery negatedQuery) {
        List<FormulaVariable> externalVariables = utility.getUniversalVariablesForFather(variableMaps, stack);
        List<FormulaVariable> internalVariables = utility.getUniversalVariablesForChild(negatedQuery.getComplexQuery(), variableMaps, stack);
        List<FormulaVariable> existentialVariables = new ArrayList<FormulaVariable>(internalVariables);
        if (logger.isDebugEnabled()) logger.debug("Generating existential variables for negation: " + negatedQuery);
        if (logger.isDebugEnabled()) logger.debug("External variables:\n" + SpicyEngineUtility.printVariableList(externalVariables));
        if (logger.isDebugEnabled()) logger.debug("Existential variables:\n" + SpicyEngineUtility.printVariableList(existentialVariables));
        List<Expression> equalities = new ArrayList<Expression>();
        if (negatedQuery.isTargetDifference()) {
//            TargetEqualities targetEqualities = generateAllEqualities(negatedQuery);
            TargetEqualities targetEqualities = negatedQuery.getTargetEqualities();
            for (int i = 0; i < targetEqualities.size(); i++) {
                VariableCorrespondence leftCorrespondence = targetEqualities.getLeftCorrespondences().get(i);
                VariableCorrespondence rightCorrespondence = targetEqualities.getRightCorrespondences().get(i);
                if (leftCorrespondence.isValueCopy() && rightCorrespondence.isValueCopy()) {
                    Expression expression = modifyVariablesOrGenerateEquality(leftCorrespondence.getFirstSourcePath(), rightCorrespondence.getFirstSourcePath(), externalVariables, existentialVariables, negatedQuery);
                    if (expression != null) {
                        equalities.add(expression);
                    }
                } else {
                    equalities.add(generateEqualityForCorrespondences(leftCorrespondence, rightCorrespondence));
                }
            }
        } else {
            SourceEqualities sourceEqualities = negatedQuery.getSourceEqualities();
            for (int i = 0; i < sourceEqualities.size(); i++) {
                VariablePathExpression leftPath = sourceEqualities.getLeftPaths().get(i);
                VariablePathExpression rightPath = sourceEqualities.getRightPaths().get(i);
                Expression expression = modifyVariablesOrGenerateEquality(leftPath, rightPath, externalVariables, existentialVariables, negatedQuery);
                if (expression != null) {
                    equalities.add(expression);
                } 
            }
        }
        variableMaps.setExistentialVariables(utility.generateId(stack), negatedQuery, existentialVariables);
        variableMaps.setEqualities(utility.generateId(stack), negatedQuery, equalities);
    }

    private void addStandardOccurrences(FormulaVariable existingVariable, FormulaVariable formulaVariable) {
        for (VariablePathExpression pathExpression : formulaVariable.getSourceOccurrencePaths()) {
            existingVariable.addSourceOccurrencePath(pathExpression);
        }
    }

    private void addAddedOccurrences(FormulaVariable existingVariable, FormulaVariable formulaVariable) {
        for (VariableOccurrence occurrence : formulaVariable.getSourceOccurrences()) {
            existingVariable.addSourceOccurrence(new VariableOccurrence(occurrence, true));
        }
    }
}
