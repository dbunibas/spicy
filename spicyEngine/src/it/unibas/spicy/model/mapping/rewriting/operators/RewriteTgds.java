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
package it.unibas.spicy.model.mapping.rewriting.operators;

import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.QueryRenaming;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.mapping.operators.RenameSetAliasesUtility;
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.mapping.rewriting.CoverageAtom;
import it.unibas.spicy.model.mapping.rewriting.CoverageMap;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RewriteTgds {

    private static Log logger = LogFactory.getLog(RewriteTgds.class);

    private RemoveClonesFromConclusion cloneRemover = new RemoveClonesFromConclusion();
    private RenameSetAliases aliasRenamer = new RenameSetAliases();
    private RenameSetAliasesUtility renamerUtility = new RenameSetAliasesUtility();

    public List<FORule> rewriteTgds(List<FORule> originalTgds, MappingTask mappingTask) {
        if (mappingTask.isLoadedFromParser() && mappingTask.getConfig().noRewriting()) {
            return cleanLoadedTgds(originalTgds);
        }
        List<FORule> rewrittenRules = null;
        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
            RewriteTgdsWithSelfJoins selfJoinRewriter = new RewriteTgdsWithSelfJoins();
            rewrittenRules = selfJoinRewriter.rewriteTgdsWithSelfJoins(originalTgds, mappingTask);
        } else {
            rewrittenRules = rewriteTgdsWithoutSelfJoins(originalTgds, mappingTask);
        }
//        RenameVariables renamer = new RenameVariables();
//        List<FORule> result = renamer.renameAliasesInRules(rewrittenRules);
//        return result;
        return rewrittenRules;
    }

    private List<FORule> cleanLoadedTgds(List<FORule> originalTgds) {
        List<FORule> finalRules = new ArrayList<FORule>();
        for (FORule originalRule : originalTgds) {
            finalRules.add(originalRule.clone());
        }
        removeClonesFromConclusions(finalRules);
        return finalRules;
    }

    private List<FORule> rewriteTgdsWithoutSelfJoins(List<FORule> originalTgds, MappingTask mappingTask) {
        if (mappingTask.getConfig().rewriteSubsumptions() == false && mappingTask.getConfig().rewriteCoverages() == false) {
            return new ArrayList<FORule>(originalTgds);
        }
        List<FORule> rewrittenRules = new ArrayList<FORule>();
        for (FORule tgd : originalTgds) {
            tgd.getComplexSourceQuery().setProvenance(SpicyEngineConstants.PREMISE + " of " + tgd.getId());
            FORule rule = tgd.clone();
            addNegationsForSubsumptions(tgd, rule, mappingTask);
            addNegationsForCoverages(tgd, rule, mappingTask);
            rewrittenRules.add(rule);
        }
        removeClonesFromConclusions(rewrittenRules);
        Collections.sort(rewrittenRules);
        return rewrittenRules;
    }

    private void removeClonesFromConclusions(List<FORule> rewrittenRules) {
        for (FORule fORule : rewrittenRules) {
            cloneRemover.removeClonesFromRuleConclusion(fORule);
        }
    }

    ////////////////////////    SUBSUMPTIONS   ////////////////////////////////

    private void addNegationsForSubsumptions(FORule tgd, FORule rule, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("********* Adding negations for tgd: \n" + tgd);
        SubsumptionMap tgdDag = mappingTask.getMappingData().getSTSubsumptionMap();
        if (logger.isDebugEnabled()) logger.debug("********* Subsumption Map: \n" + tgdDag);
        List<Subsumption> subsumptions = tgdDag.getFathers(tgd);
        if (logger.isDebugEnabled()) logger.debug("Subsumptions: " + subsumptions);
        if (logger.isDebugEnabled()) logger.debug("Config : " + mappingTask.getConfig());
        if ((subsumptions.isEmpty() || !mappingTask.getConfig().rewriteSubsumptions())) {
            return;
        }
        for (Subsumption subsumption : subsumptions) {
            List<VariableCorrespondence> leftCorrespondences = subsumption.getLeftCorrespondences();
            List<VariableCorrespondence> rightCorrespondences = subsumption.getRightCorrespondences();
            TargetEqualities equalities = new TargetEqualities(leftCorrespondences, rightCorrespondences);
            NegatedComplexQuery negatedQuery = new NegatedComplexQuery(subsumption.getRightTgd().getComplexSourceQuery().clone(), equalities);
            negatedQuery.setProvenance(subsumption.getId());
            rule.getComplexSourceQuery().addNegatedComplexQuery(negatedQuery);
        }
    }

    ////////////////////////    COVERAGES   ////////////////////////////////

//    private void addNegationsForCoverages(FORule tgd, FORule rule, MappingTask mappingTask) {
//        CoverageMap coverageMap = mappingTask.getMappingData().getSTCoverageMap();
//        List<Coverage> coverages = coverageMap.getCoverage(tgd);
//        if ((coverages.isEmpty() || !mappingTask.getConfig().rewriteCoverages())) {
//            return;
//        }
//        for (Coverage coverage : coverages) {
//            ComplexQueryWithNegations coverageView = generateCoverageView(coverage);
//            TargetEqualities equalities = new TargetEqualities(coverage.getOriginalCorrespondences(), coverage.getMatchingCorrespondences());
//            NegatedComplexQuery negatedQuery = new NegatedComplexQuery(coverageView, equalities);
//            negatedQuery.setProvenance(SpicyEngineConstants.COVERAGE + " of " + tgd.getId());
//            rule.getComplexSourceQuery().addNegatedComplexQuery(negatedQuery);
//        }
//    }
//
//    private ComplexQueryWithNegations generateCoverageView(Coverage coverage) {
//        ComplexConjunctiveQuery conjunctiveQuery = new ComplexConjunctiveQuery();
//        for (CoverageAtom coverageAtom : coverage.getCoveringAtoms()) {
//            SimpleConjunctiveQuery atomSourceView = coverageAtom.getFirstCoveringAtom().getTgd().getSimpleSourceView();
//            conjunctiveQuery.addConjuntion(atomSourceView);
//            conjunctiveQuery.addCorrespondencesForConjuntion(coverageAtom.getTargetCorrespondences());
//        }
//        conjunctiveQuery.setJoinConditions(coverage.getTargetJoinConditions());
//        conjunctiveQuery.setCyclicJoinConditions(coverage.getTargetCyclicJoinConditions());
//        ComplexQueryWithNegations coverageView = new ComplexQueryWithNegations(conjunctiveQuery);
//        coverageView.setProvenance(coverage.toShortString());
//        return coverageView;
//    }

   private void addNegationsForCoverages(FORule tgd, FORule rule, MappingTask mappingTask) {
        CoverageMap coverageMap = mappingTask.getMappingData().getSTCoverageMap();
        List<Coverage> coverages = coverageMap.getCoverage(tgd);
        if ((coverages.isEmpty() || !mappingTask.getConfig().rewriteCoverages())) {
            return;
        }
        for (Coverage coverage : coverages) {
            NegatedComplexQuery negatedQuery = generateNegatedQueryForCoverage(coverage);
            negatedQuery.setProvenance(SpicyEngineConstants.COVERAGE + " of " + tgd.getId());
            rule.getComplexSourceQuery().addNegatedComplexQuery(negatedQuery);
        }
    }

    private NegatedComplexQuery generateNegatedQueryForCoverage(Coverage coverage) {
        if (logger.isDebugEnabled()) logger.debug("************ Generating query for coverage:\n" + coverage);
        ComplexConjunctiveQuery complexQuery = new ComplexConjunctiveQuery();
        List<VariableCorrespondence> matchingCorrespondences = new ArrayList<VariableCorrespondence>();
        for (CoverageAtom coverageAtom : coverage.getCoveringAtoms()) {
            SimpleConjunctiveQuery simpleQuery = coverageAtom.getFirstCoveringAtom().getTgd().getSimpleSourceView();
            List<VariableCorrespondence> matchingCorrespondencesForConjunction = coverageAtom.getMatchingCorrespondences();
            List<VariableCorrespondence> targetCorrespondencesForConjunction = getTargetCorrespondences(coverageAtom);
            if (containsSameAliases(complexQuery, simpleQuery)) {
                QueryRenaming queryRenaming = aliasRenamer.renameAliasesInSimpleConjunctiveQuery(simpleQuery);
                simpleQuery = (SimpleConjunctiveQuery)queryRenaming.getRenamedView();
                targetCorrespondencesForConjunction = renamerUtility.renameSourceSetAliasesInCorrespondences(targetCorrespondencesForConjunction, queryRenaming.getRenamings());
                matchingCorrespondencesForConjunction = renamerUtility.renameSourceSetAliasesInCorrespondences(matchingCorrespondencesForConjunction, queryRenaming.getRenamings());
            }
            complexQuery.addConjunction(simpleQuery);
            complexQuery.addCorrespondencesForConjuntion(targetCorrespondencesForConjunction);
            matchingCorrespondences.addAll(matchingCorrespondencesForConjunction);
        }
        complexQuery.setJoinConditions(coverage.getTargetJoinConditions());
        complexQuery.setCyclicJoinConditions(coverage.getTargetCyclicJoinConditions());
        ComplexQueryWithNegations coverageView = new ComplexQueryWithNegations(complexQuery);
        coverageView.setProvenance(coverage.toShortString());
        TargetEqualities equalities = new TargetEqualities(getOriginalCorrespondences(coverage), matchingCorrespondences);
        NegatedComplexQuery negatedQuery = new NegatedComplexQuery(coverageView, equalities);
        if (logger.isDebugEnabled()) logger.debug("************ Final result:\n" + negatedQuery);
        return negatedQuery;
    }

    private boolean containsSameAliases(ComplexConjunctiveQuery complexQuery, SimpleConjunctiveQuery simpleQuery) {
        for (SetAlias simple : simpleQuery.getVariables()) {
            if (SpicyEngineUtility.containsVariableWithSameId(complexQuery.getGenerators(), simple)) {
                return true;
            }
        }
        return false;
    }

    private List<VariableCorrespondence> getOriginalCorrespondences(Coverage coverage) {
        List<VariableCorrespondence> originalCorrespondences = new ArrayList<VariableCorrespondence>();
        for (CoverageAtom atomCoverage : coverage.getCoveringAtoms()) {
            originalCorrespondences.addAll(atomCoverage.getOriginalCorrespondences());
        }
        return originalCorrespondences;
    }

    public List<VariableCorrespondence> getTargetCorrespondences(CoverageAtom atomCoverage) {
        List<VariableCorrespondence> targetCorrespondences = new ArrayList<VariableCorrespondence>();
        for (FormulaAtom coveringAtom : atomCoverage.getCoveringAtoms()) {
            for (FormulaPosition position : coveringAtom.getPositions()) {
                if (position.isUniversal()) {
                    targetCorrespondences.add(position.getCorrespondence());
                }
            }
        }
        return targetCorrespondences;
    }
}
