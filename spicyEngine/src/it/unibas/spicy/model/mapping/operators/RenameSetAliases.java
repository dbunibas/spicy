/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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

import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.TGDRenaming;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IIdentifiable;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.QueryRenaming;
import it.unibas.spicy.model.mapping.SourceEqualities;
import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RenameSetAliases {

    private static Log logger = LogFactory.getLog(RenameSetAliases.class);
    private RenameSetAliasesUtility utility = new RenameSetAliasesUtility();

    public List<FORule> renameAliasesInRules(List<FORule> rules) {
        if (logger.isDebugEnabled()) logger.debug("******** Renaming variables in rules ");
        List<FORule> renamedRules = new ArrayList<FORule>();
        for (FORule rule : rules) {
            renamedRules.add(renameAliasesInFORule(rule).getRenamedTgd());
        }
        return renamedRules;
    }

    public TGDRenaming renameAliasesInTGD(FORule tgd) {
        if (logger.isDebugEnabled()) logger.debug("=============== Renaming source variables in tgd: " + tgd);
        QueryRenaming sourceViewRenaming = renameAliasesInSimpleConjunctiveQuery(tgd.getSimpleSourceView());
        QueryRenaming targetViewRenaming = renameAliasesInSimpleConjunctiveQuery(tgd.getTargetView());
        List<VariableCorrespondence> renamedCorrespondences = utility.renameTargetSetAliasInCorrespondences(tgd.getCoveredCorrespondences(), sourceViewRenaming, targetViewRenaming);
        FORule renamedTgd = new FORule((SimpleConjunctiveQuery) sourceViewRenaming.getRenamedView(), (SimpleConjunctiveQuery) targetViewRenaming.getRenamedView(), renamedCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("=============== Renamed tgd: " + renamedTgd);
        return new TGDRenaming(renamedTgd, tgd, sourceViewRenaming, targetViewRenaming);
    }

    public TGDRenaming renameAliasesInFORule(FORule rule) {
        if (logger.isDebugEnabled()) logger.debug("=============== Renaming source variables in rule: " + rule);
        QueryRenaming sourceViewRenaming = renameAliasesInComplexQuery(rule.getComplexSourceQuery());
        QueryRenaming targetViewRenaming = renameAliasesInSimpleConjunctiveQuery(rule.getTargetView());
        List<VariableCorrespondence> renamedCorrespondences = utility.renameTargetSetAliasInCorrespondences(rule.getCoveredCorrespondences(), sourceViewRenaming, targetViewRenaming);
        FORule renamedTgd = new FORule((ComplexQueryWithNegations) sourceViewRenaming.getRenamedView(), (SimpleConjunctiveQuery) targetViewRenaming.getRenamedView(), renamedCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("=============== Renamed tgd: " + renamedTgd);
        return new TGDRenaming(renamedTgd, rule, sourceViewRenaming, targetViewRenaming);
    }

    public QueryRenaming renameAliasesInSimpleConjunctiveQuery(SimpleConjunctiveQuery view) {
        RenameSetAliasesViewVisitor visitor = new RenameSetAliasesViewVisitor();
        view.accept(visitor);
        return visitor.getRenamingForView(view);
    }

    public QueryRenaming renameAliasesInComplexQuery(ComplexQueryWithNegations view) {
        RenameSetAliasesViewVisitor visitor = new RenameSetAliasesViewVisitor();
        view.accept(visitor);
        return visitor.getRenamingForView(view);
    }

}

class RenameSetAliasesViewVisitor implements IQueryVisitor {

    private static Log logger = LogFactory.getLog(RenameSetAliasesViewVisitor.class);
    private RenameSetAliasesUtility utility = new RenameSetAliasesUtility();
    private Map<String, QueryRenaming> renamings = new HashMap<String, QueryRenaming>();
    private List<IIdentifiable> stack = new ArrayList<IIdentifiable>();

    public void visitComplexQueryWithNegation(ComplexQueryWithNegations query) {
        if (logger.isDebugEnabled()) logger.debug("Renaming variables for complex query with negations: " + query);
        stack.add(0, query);
        ComplexConjunctiveQuery complexConjunctiveQuery = query.getComplexQuery();
        complexConjunctiveQuery.accept(this);
        QueryRenaming compleQueryRenaming = utility.getRenamingForChild(query.getComplexQuery(), renamings, stack);
        ComplexQueryWithNegations newQuery = new ComplexQueryWithNegations((ComplexConjunctiveQuery) compleQueryRenaming.getRenamedView());
        for (NegatedComplexQuery negatedQuery : query.getNegatedComplexQueries()) {
            negatedQuery.accept(this);
            QueryRenaming negatedQueryRenaming = utility.getRenamingForChild(negatedQuery, renamings, stack);
            NegatedComplexQuery newNegatedQuery = (NegatedComplexQuery) negatedQueryRenaming.getRenamedView();
            if (negatedQuery.isTargetDifference()) {
                TargetEqualities targetEqualities = newNegatedQuery.getTargetEqualities();
                newNegatedQuery.setTargetEqualities(utility.renameLeftCorrespondencesInEqualities(targetEqualities, compleQueryRenaming));
            } else {
                SourceEqualities sourceEqualities = newNegatedQuery.getSourceEqualities();
                newNegatedQuery.setSourceEqualities(utility.changeLeftPathsInEqualities(sourceEqualities, compleQueryRenaming.getRenamings()));
            }
            newQuery.addNegatedComplexQuery((NegatedComplexQuery) negatedQueryRenaming.getRenamedView());
        }
        newQuery.setProvenance(query.getProvenance());
        QueryRenaming renaming = new QueryRenaming(query, newQuery, compleQueryRenaming.getRenamings());
        renamings.put(utility.generateId(stack), renaming);
        stack.remove(0);
    }

    public void visitComplexConjunctiveQuery(ComplexConjunctiveQuery complexQuery) {
        if (logger.isDebugEnabled()) logger.debug("Renaming variables for complex conjunctive query: " + complexQuery);
        stack.add(0, complexQuery);
        for (SimpleConjunctiveQuery simpleQuery : complexQuery.getConjunctions()) {
            simpleQuery.accept(this);
        }
        ComplexConjunctiveQuery newQuery = new ComplexConjunctiveQuery();
        List<QueryRenaming> subQueryRenamings = new ArrayList<QueryRenaming>();
        QueryRenaming firstRenamedQuery = utility.getRenamingForChild(complexQuery.getFirstConjunct(), renamings, stack);
        subQueryRenamings.add(firstRenamedQuery);
        newQuery.addConjunction((SimpleConjunctiveQuery) firstRenamedQuery.getRenamedView());
        if (complexQuery.getConjunctions().size() > 1) {
            List<VariableCorrespondence> firstCorrespondences = complexQuery.getCorrespondencesForConjunctions().get(0);
            newQuery.addCorrespondencesForConjuntion(utility.renameSourceSetAliasesInCorrespondences(firstCorrespondences, firstRenamedQuery.getRenamings()));
        }
        for (int i = 0; i < complexQuery.getJoinConditions().size(); i++) {
            VariableJoinCondition joinCondition = complexQuery.getJoinConditions().get(i);
            SimpleConjunctiveQuery conjunction = complexQuery.getConjunctions().get(i + 1);
            List<VariableCorrespondence> correspondences = complexQuery.getCorrespondencesForConjunctions().get(i + 1);
            QueryRenaming renamingForConjunction = utility.getRenamingForChild(conjunction, renamings, stack);
            subQueryRenamings.add(renamingForConjunction);
            newQuery.addConjunction((SimpleConjunctiveQuery) renamingForConjunction.getRenamedView());
            newQuery.addJoinCondition(joinCondition);
            newQuery.addCorrespondencesForConjuntion(utility.renameSourceSetAliasesInCorrespondences(correspondences, renamingForConjunction.getRenamings()));
        }
        newQuery.setCyclicJoinConditions(complexQuery.getCyclicJoinConditions());
        if (complexQuery.hasIntersection()) {
            SimpleConjunctiveQuery intersectionQuery = complexQuery.getConjunctionForIntersection();
            intersectionQuery.accept(this);
            QueryRenaming intersectionRenaming = utility.getRenamingForChild(complexQuery.getConjunctionForIntersection(), renamings, stack);
            newQuery.setConjunctionForIntersection((SimpleConjunctiveQuery) intersectionRenaming.getRenamedView());
            newQuery.setIntersectionEqualities(utility.renamePathsInIntersectionEqualities(complexQuery.getIntersectionEqualities(), subQueryRenamings, intersectionRenaming));
        }
        newQuery.setProvenance(complexQuery.getProvenance());
        QueryRenaming renaming = new QueryRenaming(complexQuery, newQuery, utility.generateAllRenamings(subQueryRenamings));
        renamings.put(utility.generateId(stack), renaming);
        stack.remove(0);
    }

    public void visitSimpleConjunctiveQuery(SimpleConjunctiveQuery query) {
        if (logger.isDebugEnabled()) logger.debug("Renaming variables for simple conjunctive query: " + query);
        stack.add(0, query);
        Map<Integer, SetAlias> variableRenamings = new HashMap<Integer, SetAlias>();
        SimpleConjunctiveQuery renamedView = new SimpleConjunctiveQuery();
        for (SetAlias variable : query.getVariables()) {
//            SetAlias newVariable = recursivelyCloneWithNewId(variable, variableRenamings);
            SetAlias newVariable = cloneAndSaveVariable(variable, variableRenamings);
            renamedView.addVariable(newVariable);
        }
        for (VariableJoinCondition joinCondition : query.getJoinConditions()) {
            renamedView.addJoinCondition(utility.generateNewJoin(joinCondition, variableRenamings));
        }
        for (VariableJoinCondition joinCondition : query.getCyclicJoinConditions()) {
            renamedView.addCyclicJoinCondition(utility.generateNewJoin(joinCondition, variableRenamings));
        }
        for (VariableSelectionCondition selectionCondition : query.getSelectionConditions()) {
            renamedView.addSelectionCondition(utility.generateNewSelection(selectionCondition, variableRenamings));
        }
        QueryRenaming renaming = new QueryRenaming(query, renamedView, variableRenamings);
        renamings.put(utility.generateId(stack), renaming);
        stack.remove(0);
    }

    private SetAlias cloneAndSaveVariable(SetAlias variable, Map<Integer, SetAlias> variableRenamings) {
        SetAlias newVariable = variable.cloneWithoutAlgebraTreeAndWithNewId();
        newVariable.setProvenanceCondition(null);
        variableRenamings.put(variable.getId(), newVariable);
        return newVariable;
    }

    private SetAlias recursivelyCloneWithNewId(SetAlias variable, Map<Integer, SetAlias> variableRenamings) {
        SetAlias newVariable = cloneAndSaveVariable(variable, variableRenamings);
        SetAlias currentVariable = newVariable;
        while (currentVariable.getBindingVariable() != null) {
            SetAlias bindingVariable = currentVariable.getBindingVariable();
            SetAlias newBindingVariable = cloneAndSaveVariable(bindingVariable, variableRenamings);
            VariablePathExpression newBindingPathExpression = new VariablePathExpression(currentVariable.getBindingPathExpression(), newBindingVariable);
            currentVariable.setBindingPathExpression(newBindingPathExpression);
            currentVariable = currentVariable.getBindingVariable();
        }
        return newVariable;
    }

    public void visitNegatedComplexQuery(NegatedComplexQuery negatedQuery) {
        if (logger.isDebugEnabled()) logger.debug("Renaming variables for negated complex query: " + negatedQuery);
        stack.add(0, negatedQuery);
        ComplexQueryWithNegations queryToNegate = negatedQuery.getComplexQuery();
        queryToNegate.accept(this);
        QueryRenaming complexQueryRenaming = utility.getRenamingForChild(negatedQuery.getComplexQuery(), renamings, stack);
        NegatedComplexQuery newQuery = null;
        if (negatedQuery.isTargetDifference()) {
            TargetEqualities newEqualities = utility.renameRightCorrespondencesInEqualities(negatedQuery.getTargetEqualities(), complexQueryRenaming);
            newQuery = new NegatedComplexQuery((ComplexQueryWithNegations) complexQueryRenaming.getRenamedView(), newEqualities);
            newQuery.setAdditionalCyclicJoins(negatedQuery.getAdditionalCyclicJoins());
            List<VariableCorrespondence> newCorrespondences = utility.renameSourceSetAliasesInCorrespondences(negatedQuery.getCorrespondencesForJoin(), complexQueryRenaming.getRenamings());
            newQuery.setCorrespondencesForJoin(newCorrespondences);
        } else {
            SourceEqualities newEqualities = utility.renameRightPathsInEqualities(negatedQuery.getSourceEqualities(), complexQueryRenaming.getRenamings());
            newQuery = new NegatedComplexQuery((ComplexQueryWithNegations) complexQueryRenaming.getRenamedView(), newEqualities);
        }
        newQuery.setProvenance(negatedQuery.getProvenance());
        QueryRenaming renaming = new QueryRenaming(negatedQuery, newQuery, complexQueryRenaming.getRenamings());
        renamings.put(utility.generateId(stack), renaming);
        stack.remove(0);
    }

    public Map<String, QueryRenaming> getResult() {
        return renamings;
    }

    public QueryRenaming getRenamingForView(IIdentifiable view) {
        return renamings.get(utility.generateIdForView(view));
    }
}
