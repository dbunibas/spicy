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
 
package it.unibas.spicy.model.algebra.operators;

import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.algebra.Join;
import it.unibas.spicy.model.algebra.Merge;
import it.unibas.spicy.model.algebra.Compose;
import it.unibas.spicy.model.algebra.Difference;
import it.unibas.spicy.model.algebra.Nest;
import it.unibas.spicy.model.algebra.Project;
import it.unibas.spicy.model.algebra.Select;
import it.unibas.spicy.model.algebra.Unnest;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.algebra.DifferenceOnTargetValues;
import it.unibas.spicy.model.algebra.IntersectionOnTargetValues;
import it.unibas.spicy.model.algebra.JoinOnTargetValues;
import it.unibas.spicy.model.algebra.SelectOnTargetValues;
import it.unibas.spicy.model.algebra.SelectProvenance;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateAlgebraTree {

    private TreeGenerator generator = new TreeGenerator();

    public IAlgebraOperator generateTreeForMappingTask(MappingTask mappingTask) {
        return generateTreeForRules(mappingTask, mappingTask.getMappingData().getRewrittenRules());
    }

    public IAlgebraOperator generateCanonicalTreeForMappingTask(MappingTask mappingTask) {
        return generateTreeForRules(mappingTask, mappingTask.getMappingData().getSTTgdsForCanonicalSolutions());
    }

    public IAlgebraOperator generateTreeForVariable(SetAlias variable) {
        return generator.generateTreeForVariable(variable);
    }

    public IAlgebraOperator generateTreeForSimpleConjunctiveQuery(SimpleConjunctiveQuery view) {
        return generator.generateTreeForSimpleConjunctiveQuery(view);
    }

    public IAlgebraOperator generateTreeForComplexConjunctiveQuery(ComplexConjunctiveQuery view) {
        return generator.generateTreeForComplexConjunctiveQuery(view);
    }

    public IAlgebraOperator generateTreeForComplexQueryWithNegations(ComplexQueryWithNegations view) {
        return generator.generateTreeForComplexQueryWithNegations(view);
    }

    public IAlgebraOperator generateTreeForRules(MappingTask mappingTask, List<FORule> tgds) {
        return generator.generateTree(mappingTask, tgds);
    }
}

class TreeGenerator {

    private static Log logger = LogFactory.getLog(TreeGenerator.class);

    IAlgebraOperator generateTree(MappingTask mappingTask, List<FORule> tgds) {
        IAlgebraOperator stExchangeAlgebraTree = generateTreeForSTExchange(mappingTask, tgds);
        IAlgebraOperator compose = new Compose(mappingTask);
        compose.addChild(stExchangeAlgebraTree);
//        if (mappingTask.getMappingData().hasDoubleExchange()) {
//            compose.addChild(generateTreeForTTExchange(mappingTask));
//        }
        if (logger.isTraceEnabled()) logger.trace("Final algebra tree: " + compose);
        return compose;
    }

    private IAlgebraOperator generateTreeForSTExchange(MappingTask mappingTask, List<FORule> tgds) {
        IAlgebraOperator merge = new Merge();
        for (FORule tgd : tgds) {
            merge.addChild(generateTreeForSTTGD(tgd, mappingTask));
        }
        return merge;
    }

    ////  TGD
    IAlgebraOperator generateTreeForSTTGD(FORule tgd, MappingTask mappingTask) {
        IAlgebraOperator sourceViewTree = tgd.getComplexSourceQuery().getAlgebraTree();
        List<VariablePathExpression> sourcePathsInCorrespondences = SpicyEngineUtility.findSourcePathsInCorrespondences(tgd.getCoveredCorrespondences());
        Project projectOperator = new Project(sourcePathsInCorrespondences);
        projectOperator.addChild(sourceViewTree);
        Nest nestOperator = new Nest(tgd, mappingTask.getTargetProxy(), tgd.getGenerators(mappingTask), mappingTask);
        nestOperator.addChild(projectOperator);
        nestOperator.setId(tgd.getId());
        return nestOperator;
    }

    ////  COMPLEX CONJUNCTIVE QUERY
    IAlgebraOperator generateTreeForComplexConjunctiveQuery(ComplexConjunctiveQuery complexQuery) {
        IAlgebraOperator currentRoot = complexQuery.getConjunctions().get(0).getAlgebraTree();
        for (int i = 0; i < complexQuery.getJoinConditions().size(); i++) {
            VariableJoinCondition joinCondition = complexQuery.getJoinConditions().get(i);
            IAlgebraOperator viewTreeToJoin = complexQuery.getConjunctions().get(i + 1).getAlgebraTree();
            List<VariableCorrespondence> leftCorrespondences = allPreviousCorrespondences(complexQuery.getCorrespondencesForConjunctions(), i);
            List<VariableCorrespondence> rightCorrespondences = complexQuery.getCorrespondencesForConjunctions().get(i + 1);
            JoinOnTargetValues joinOperator = new JoinOnTargetValues(joinCondition, leftCorrespondences, rightCorrespondences);
            joinOperator.addChild(currentRoot);
            joinOperator.addChild(viewTreeToJoin);
            currentRoot = joinOperator;
        }
        for (VariableJoinCondition cyclicJoinCondition : complexQuery.getCyclicJoinConditions()) {
            SelectOnTargetValues select = new SelectOnTargetValues(createSelectionCondition(cyclicJoinCondition), complexQuery.getAllCorrespondences());
            select.addChild(currentRoot);
            currentRoot = select;
        }
        if (complexQuery.hasIntersection()) {
            IAlgebraOperator treeToIntersect = complexQuery.getConjunctionForIntersection().getAlgebraTree();
            List<VariableCorrespondence> leftCorrespondences = complexQuery.getIntersectionEqualities().getLeftCorrespondences();
            List<VariableCorrespondence> rightCorrespondences = complexQuery.getIntersectionEqualities().getRightCorrespondences();
            IntersectionOnTargetValues intersection = new IntersectionOnTargetValues(leftCorrespondences, rightCorrespondences);
            intersection.addChild(currentRoot);
            intersection.addChild(treeToIntersect);
            currentRoot = intersection;
        }
        currentRoot.setId(complexQuery.getProvenance());
        return currentRoot;
    }

    private List<VariableCorrespondence> allPreviousCorrespondences(List<List<VariableCorrespondence>> correspondencesForConjunctions, int limit) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (int i = 0; i <= limit; i++) {
            result.addAll(correspondencesForConjunctions.get(i));
        }
        return result;
    }

    ////  COMPLEX QUERY WITH NEGATIONS
    IAlgebraOperator generateTreeForComplexQueryWithNegations(ComplexQueryWithNegations query) {
        IAlgebraOperator currentRoot = query.getComplexQuery().getAlgebraTree();
        for (NegatedComplexQuery negatedComplexQuery : query.getNegatedComplexQueries()) {
            IAlgebraOperator negatedCurrentRoot = negatedComplexQuery.getComplexQuery().getAlgebraTree();
            if (negatedComplexQuery.isTargetDifference()) {
                if (negatedComplexQuery.getAdditionalCyclicJoins() != null && !negatedComplexQuery.getAdditionalCyclicJoins().isEmpty()) {
                    for (VariableJoinCondition cyclicJoinCondition : negatedComplexQuery.getAdditionalCyclicJoins()) {
                        SelectOnTargetValues select = new SelectOnTargetValues(createSelectionCondition(cyclicJoinCondition), negatedComplexQuery.getCorrespondencesForJoin());
                        select.addChild(negatedCurrentRoot);
                        negatedCurrentRoot = select;
                    }
                }
                DifferenceOnTargetValues difference = new DifferenceOnTargetValues(negatedComplexQuery.getTargetEqualities().getLeftCorrespondences(), negatedComplexQuery.getTargetEqualities().getRightCorrespondences());
                difference.addChild(currentRoot);
                difference.addChild(negatedCurrentRoot);
                difference.setId(negatedComplexQuery.getProvenance());
                currentRoot = difference;
            } else {
                Difference difference = new Difference(negatedComplexQuery.getSourceEqualities().getLeftPaths(), negatedComplexQuery.getSourceEqualities().getRightPaths());
                difference.addChild(currentRoot);
                difference.addChild(negatedCurrentRoot);
                difference.setId(negatedComplexQuery.getProvenance());
                currentRoot = difference;
            }
        }
        currentRoot.setId(query.getProvenance());
        return currentRoot;
    }

    ////  VARIABLE
    IAlgebraOperator generateTreeForVariable(SetAlias variable) {
        Unnest unnestOperator = new Unnest(variable);
        IAlgebraOperator currentRoot = unnestOperator;
        if (variable.getProvenanceCondition() != null) {
            SelectProvenance selectProvenanceOperator = new SelectProvenance(variable.getProvenanceCondition());
            selectProvenanceOperator.addChild(currentRoot);
            currentRoot = selectProvenanceOperator;
        }
        List<VariableSelectionCondition> conditions = new ArrayList<VariableSelectionCondition>();
        for (SetAlias generator : variable.getGenerators()) {
            conditions.addAll(generator.getSelectionConditions());
        }
        if (!conditions.isEmpty()) {
            Select selectOperator = new Select(conditions);
            selectOperator.addChild(currentRoot);
            currentRoot = selectOperator;
        }
        return currentRoot;
    }

    ////  SIMPLE CONJUNCTIVE QUERY
    IAlgebraOperator generateTreeForSimpleConjunctiveQuery(SimpleConjunctiveQuery view) {
        NormalizeViewForExecutionPlan viewNormalizer = new NormalizeViewForExecutionPlan();
        SimpleConjunctiveQuery normalizedView = viewNormalizer.normalizeView(view);
        IAlgebraOperator currentRoot = normalizedView.getVariables().get(0).getAlgebraTree();
        for (int i = 0; i < normalizedView.getJoinConditions().size(); i++) {
            VariableJoinCondition joinCondition = normalizedView.getJoinConditions().get(i);
            SetAlias variable = normalizedView.getVariables().get(i + 1);
            Join joinOperator = new Join(joinCondition);
            joinOperator.addChild(currentRoot);
            joinOperator.addChild(variable.getAlgebraTree());
            currentRoot = joinOperator;
        }
        for (VariableJoinCondition cyclicJoinCondition : normalizedView.getCyclicJoinConditions()) {
            Select select = new Select(createSelectionCondition(cyclicJoinCondition));
            select.addChild(currentRoot);
            currentRoot = select;
        }
        if (!view.getSelectionConditions().isEmpty()) {
            Select selectOperator = new Select(view.getSelectionConditions());
            selectOperator.addChild(currentRoot);
            currentRoot = selectOperator;
        }
        currentRoot.setId(view.getProvenance());
        return currentRoot;
    }

    private VariableSelectionCondition createSelectionCondition(VariableJoinCondition cyclicJoinCondition) {
        StringBuilder result = new StringBuilder();
        int numberOfPaths = cyclicJoinCondition.getFromPaths().size();
        for (int i = 0; i < numberOfPaths; i++) {
            VariablePathExpression fromPath = cyclicJoinCondition.getFromPaths().get(i);
            VariablePathExpression toPath = cyclicJoinCondition.getToPaths().get(i);
            result.append(fromPath).append(" == ").append(toPath);
            if (i != numberOfPaths - 1) {
                result.append(" && ");
            }
        }
        Expression selection = new Expression(result.toString());
        VariableSelectionCondition selectionCondition = new VariableSelectionCondition(selection);
        return selectionCondition;
    }
}
