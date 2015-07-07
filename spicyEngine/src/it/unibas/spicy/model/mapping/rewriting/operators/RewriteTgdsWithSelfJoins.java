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

import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.operators.NormalizeSTTGDs;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionJoin;
import it.unibas.spicy.model.mapping.rewriting.ExpansionOrderedPair;
import it.unibas.spicy.model.mapping.rewriting.ExpansionPartialOrder;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RewriteTgdsWithSelfJoins {

    private static Log logger = LogFactory.getLog(RewriteTgdsWithSelfJoins.class);
    private GenerateExpansionRules ruleGenerator = new GenerateExpansionRules();
    private NormalizeSTTGDs normalizer = new NormalizeSTTGDs();
    private GenerateTgdSubsumptionMapWithSelfJoins subsumptionFinder = new GenerateTgdSubsumptionMapWithSelfJoins();
    private RemoveClonesFromConclusion cloneRemover = new RemoveClonesFromConclusion();

    public List<FORule> rewriteTgdsWithSelfJoins(List<FORule> originalTgds, MappingTask mappingTask) {
        if (!mappingTask.getConfig().rewriteSelfJoins()) {
            List<FORule> finalRules = new ArrayList<FORule>(originalTgds);
            cloneRemover.removeClonesFromRuleConclusion(finalRules);
            return finalRules;
        }
        if (logger.isDebugEnabled()) logger.debug("***************************   STARTING REWRITING FOR SCENARIO WITH SELF-JOINS ***********************");
        List<Expansion> expansions = mappingTask.getMappingData().getExpansions();
        if (logger.isDebugEnabled()) logger.debug("---------------------------  Expansions: ------------------------------\n" + expansions);
        // step 1: generate expansion rules
        Map<Expansion, FORule> expansionRules = ruleGenerator.generateExpansionRules(expansions, mappingTask);
        if (logger.isDebugEnabled()) logger.trace("---------------------------  Expansion Rules: ------------------------------\n" + expansionRules);
        // step 2: rewrite expansion rules (\rewc, \rewi)
        rewriteExpansionRules(expansions, expansionRules, mappingTask);
        if (logger.isDebugEnabled()) logger.trace("---------------------------  Rewritten Expansion Rules: ------------------------------\n" + expansionRules);
        // step 3: normalize rewritten rules
        Map<FORule, List<FORule>> normalization = normalizer.findNomalizationOfSTTGDs(new ArrayList<FORule>(expansionRules.values()));
        if (logger.isDebugEnabled()) logger.trace("---------------------------  Normalized Expansion Rules: ------------------------------\n" + expansionRules);
        // step 4: find subsumption among normalized rules
        List<FORule> finalRules = findSubmptionsAndAddDifferences(normalization, mappingTask);
        // step 5: remove clones
        cloneRemover.removeClonesFromRuleConclusion(finalRules);
        Collections.sort(finalRules);
        if (logger.isDebugEnabled()) logger.debug("---------------------------  Final Rules: ------------------------------\n" + finalRules);
        return finalRules;
    }

    //////////////////////    STEP 2 : rewrite expansion rules  ////////////////////////////
    private void rewriteExpansionRules(List<Expansion> expansions, Map<Expansion, FORule> expansionRules, MappingTask mappingTask) {
        for (Expansion expansion : expansions) {
            addDifferencesForMoreCompactExpansions(expansion, expansionRules, mappingTask);
        }
        for (Expansion expansion : expansions) {
            addDifferencesForMoreInformativeExpansions(expansion, expansionRules, mappingTask);
        }
    }

    private void addDifferencesForMoreCompactExpansions(Expansion expansion, Map<Expansion, FORule> expansionRules, MappingTask mappingTask) {
        FORule expansionRule = expansionRules.get(expansion);
        expansion.setRew_c(expansion.getSourceRewriting().cloneAndKeepComplexQuery());
        expansion.getRew_c().setProvenance(SpicyEngineConstants.REW_C + " - " + expansion.getId());
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More compact rewriting) Expansion: ------------------------------\n" + expansion);
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More compact rewriting) Expansion rule: ------------------------------\n" + expansionRule);
        ExpansionPartialOrder moreCompactPartialOrder = mappingTask.getMappingData().getExpansionsMoreCompactOrderMap();
        List<ExpansionOrderedPair> orderedPairsForExpansion = moreCompactPartialOrder.getOrderedPairsForExpansion(expansion);
        for (ExpansionOrderedPair expansionOrderedPair : orderedPairsForExpansion) {
            Expansion moreCompactExpansion = expansionOrderedPair.getRightHandSideExpansion();
            FORule ruleForMoreCompactExpansion = expansionRules.get(moreCompactExpansion);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More compact rewriting) More compact expansion: ------------------------------\n" + expansionOrderedPair);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More compact rewriting) More compact expansion rule: ------------------------------\n" + ruleForMoreCompactExpansion);
            List<VariableCorrespondence> leftCorrespondences = findCorrespondencences(expansionOrderedPair.getLeftPaths(), expansionRule);
            List<VariableCorrespondence> rightCorrespondences = findCorrespondencences(expansionOrderedPair.getRightPaths(), ruleForMoreCompactExpansion);
            TargetEqualities equalities = new TargetEqualities(leftCorrespondences, rightCorrespondences);
            NegatedComplexQuery negatedQuery = new NegatedComplexQuery(moreCompactExpansion.getSourceRewriting(), equalities);
            negatedQuery.setAdditionalCyclicJoins(extractJoins(expansionOrderedPair.getAdditionalCyclicJoins()));
            negatedQuery.setCorrespondencesForJoin(ruleForMoreCompactExpansion.getCoveredCorrespondences());
            negatedQuery.setProvenance(expansionOrderedPair.getId());
            expansion.getRew_c().addNegatedComplexQuery(negatedQuery);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More compact rewriting) Current rewritten rule: ------------------------------\n" + expansionRule);
        }
        expansionRule.setComplexSourceView(expansion.getRew_c());
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More compact rewriting) Final Rewritten Expansion rule: ------------------------------\n" + expansionRule);
    }

    private List<VariableJoinCondition> extractJoins(List<ExpansionJoin> expansionJoins) {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        if (expansionJoins != null) {
            for (ExpansionJoin expansionJoin : expansionJoins) {
                result.add(expansionJoin.getJoinCondition());
            }
        }
        return result;
    }

    private List<VariableCorrespondence> findCorrespondencences(List<VariablePathExpression> targetPaths, FORule rule) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (VariablePathExpression atomPath : targetPaths) {
            result.add(findCorrespondenceForPath(atomPath, rule));
        }
        return result;
    }

    private VariableCorrespondence findCorrespondenceForPath(VariablePathExpression targetPath, FORule tgd) {
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return correspondence;
            }
        }
        throw new IllegalArgumentException("Path " + targetPath + " is not universal in rule: " + tgd);
    }

    private void addDifferencesForMoreInformativeExpansions(Expansion expansion, Map<Expansion, FORule> expansionRules, MappingTask mappingTask) {
        FORule rewrittenExpansionRule = expansionRules.get(expansion);
        expansion.setRew_i(expansion.getRew_c().cloneAndKeepComplexQuery());
        expansion.getRew_i().setProvenance(SpicyEngineConstants.REW_I + " - " + expansion.getId());
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More informative rewriting) Expansion: ------------------------------\n" + expansion);
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More informative rewriting) Expansion rule: ------------------------------\n" + rewrittenExpansionRule);
        ExpansionPartialOrder moreInformativePartialOrder = mappingTask.getMappingData().getExpansionsMoreInformativeOrderMap();
        List<ExpansionOrderedPair> orderedPairsForExpansion = moreInformativePartialOrder.getOrderedPairsForExpansion(expansion);
        for (ExpansionOrderedPair expansionOrderedPair : orderedPairsForExpansion) {
            Expansion moreInformativeExpansion = expansionOrderedPair.getRightHandSideExpansion();
            FORule rewrittenRuleForMoreInformativeExpansion = expansionRules.get(moreInformativeExpansion);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More informative rewriting) More informative expansion: ------------------------------\n" + expansionOrderedPair);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More informative rewriting) More informative expansion rule: ------------------------------\n" + rewrittenRuleForMoreInformativeExpansion);
            List<VariableCorrespondence> leftCorrespondences = findCorrespondencences(expansionOrderedPair.getLeftPaths(), rewrittenExpansionRule);
            List<VariableCorrespondence> rightCorrespondences = findCorrespondencences(expansionOrderedPair.getRightPaths(), rewrittenRuleForMoreInformativeExpansion);
            TargetEqualities equalities = new TargetEqualities(leftCorrespondences, rightCorrespondences);
            NegatedComplexQuery negatedQuery = new NegatedComplexQuery(moreInformativeExpansion.getRew_c(), equalities);
            negatedQuery.setAdditionalCyclicJoins(extractJoins(expansionOrderedPair.getAdditionalCyclicJoins()));
            negatedQuery.setCorrespondencesForJoin(rewrittenRuleForMoreInformativeExpansion.getCoveredCorrespondences());
            negatedQuery.setProvenance(expansionOrderedPair.getId());
            expansion.getRew_i().addNegatedComplexQuery(negatedQuery);
            if (logger.isDebugEnabled()) logger.trace("------------------------------------  (More informative rewriting) Current rewritten rule: ------------------------------\n" + rewrittenExpansionRule);
        }
        rewrittenExpansionRule.setComplexSourceView(expansion.getRew_i());
        if (logger.isDebugEnabled()) logger.trace("---------------------------  (More informative rewriting) Final Rewritten Expansion rule: ------------------------------\n" + rewrittenExpansionRule);
    }

    //////////////////////    STEP 4 : find subsumptions expansion rules  ////////////////////////////
    private List<FORule> findSubmptionsAndAddDifferences(Map<FORule, List<FORule>> normalizedRules, MappingTask mappingTask) {
        SubsumptionMap expansionRulesSubsumptionMap = subsumptionFinder.generateSubsumptionMap(normalizedRules, mappingTask);
        mappingTask.getMappingData().setExpansionRulesSubsumptionMap(expansionRulesSubsumptionMap);
        List<FORule> finalRules = expansionRulesSubsumptionMap.getRules();
        for (FORule rule : finalRules) {
            List<Subsumption> subsumptions = expansionRulesSubsumptionMap.getFathers(rule);
            for (Subsumption subsumption : subsumptions) {
                List<VariableCorrespondence> leftCorrespondences = subsumption.getLeftCorrespondences();
                List<VariableCorrespondence> rightCorrespondences = subsumption.getRightCorrespondences();
                TargetEqualities equalities = new TargetEqualities(leftCorrespondences, rightCorrespondences);
                NegatedComplexQuery negatedQuery = new NegatedComplexQuery(subsumption.getRightTgd().getComplexSourceQuery(), equalities);
                negatedQuery.setProvenance(subsumption.getId());
                rule.getComplexSourceQuery().addNegatedComplexQuery(negatedQuery);
            }
        }
        return finalRules;
    }
}
