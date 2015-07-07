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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.utility.GenericCombinationsGenerator;
import it.unibas.spicy.utility.GenericPermutationsGenerator;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateTgdSubsumptionMapWithSelfJoins {

    private static Log logger = LogFactory.getLog(GenerateTgdSubsumptionMapWithSelfJoins.class);
    private CheckTGDHomomorphismWithSelfJoins subsumptionChecker = new CheckTGDHomomorphismWithSelfJoins();

    public SubsumptionMap generateSubsumptionMap(Map<FORule, List<FORule>> normalizedRules, MappingTask mappingTask) {
        if (!mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
            throw new IllegalArgumentException("Unable to find subsumptions for mapping tasks without self joins in conclusions");
        }
        if (logger.isTraceEnabled()) logger.trace("*************************Normalized rules: " + SpicyEngineUtility.printMap(normalizedRules));
        Map<FORule, List<Subsumption>> subsumptions = generateSubsumptions(normalizedRules, mappingTask);
        if (logger.isTraceEnabled()) logger.trace("*************************Full subsumptions: " + SpicyEngineUtility.printMap(subsumptions));
        if (logger.isTraceEnabled()) logger.trace("*************************Full subsumptions after cycle removal: " + SpicyEngineUtility.printMap(subsumptions));
        SubsumptionMap subsumptionMap = new SubsumptionMap(mappingTask, subsumptions);
        return subsumptionMap;
    }

    private Map<FORule, List<Subsumption>> generateSubsumptions(Map<FORule, List<FORule>> normalizedRules, MappingTask mappingTask) {
        List<FORule> allNormalizedRules = generateAllNormalizedRules(normalizedRules);
        Map<FORule, List<Subsumption>> subsumptionMap = new HashMap<FORule, List<Subsumption>>();
        for (FORule rule : normalizedRules.keySet()) {
            List<FORule> derivedRules = normalizedRules.get(rule);
            if (derivedRules.size() == 1 && derivedRules.get(0).equals(rule) && !mappingTask.getTargetProxy().isNested()) {
                subsumptionMap.put(rule, new ArrayList<Subsumption>());
                continue;
            }
            for (FORule normalizedRule : derivedRules) {
                List<Subsumption> ancestors = new ArrayList<Subsumption>();
                subsumptionMap.put(normalizedRule, ancestors);
                for (FORule otherRule : allNormalizedRules) {
                    if (normalizedRule.getId().equals(otherRule.getId())) {
                        continue;
                    }
                    if (otherRule.getTargetView().getVariables().size() <= normalizedRule.getTargetView().getVariables().size()) {
                        continue;
                    }
                    List<Subsumption> subsumptions = checkHomomorphismWithSelfJoins(otherRule, normalizedRule, mappingTask.getTargetProxy(), mappingTask);
                    if (subsumptions != null) {
                        ancestors.addAll(subsumptions);
                    }
                }
            }
        }
        return subsumptionMap;
    }

    private List<FORule> generateAllNormalizedRules(Map<FORule, List<FORule>> normalizedRules) {
        List<FORule> result = new ArrayList<FORule>();
        for (FORule rule : normalizedRules.keySet()) {
            result.addAll(normalizedRules.get(rule));
        }
        return result;
    }

    List<Subsumption> checkHomomorphismWithSelfJoins(FORule fatherTgd, FORule tgd, IDataSourceProxy target, MappingTask mappingTask) {
        if (fatherTgd.getId().equals(tgd.getId())) {
            throw new IllegalArgumentException("Cannot check subsumption of a tgd by itself: " + tgd);
        }
        if (fatherTgd.getTargetView().getVariables().size() < tgd.getTargetView().getVariables().size()) {
            return null;
        }
        List<Subsumption> result = new ArrayList<Subsumption>();
        if (logger.isDebugEnabled()) logger.debug("***********************************Checking subsumption of : \n" + tgd + "by : \n" + fatherTgd);
        List<List<FormulaAtom>> examinedPermutations = new ArrayList<List<FormulaAtom>>();
        List<FormulaAtom> tgdAtoms = tgd.getTargetFormulaAtoms(mappingTask);
        List<FormulaAtom> fatherTgdAtoms = fatherTgd.getTargetFormulaAtoms(mappingTask);
        GenericCombinationsGenerator<FormulaAtom> combinationsGenerator = new GenericCombinationsGenerator<FormulaAtom>(fatherTgdAtoms, tgdAtoms.size());
        while (combinationsGenerator.hasMoreElements()) {
            List<FormulaAtom> combination = combinationsGenerator.nextElement();
            GenericPermutationsGenerator<FormulaAtom> permutationsGenerator = new GenericPermutationsGenerator<FormulaAtom>(combination);
            while (permutationsGenerator.hasMoreElements()) {
                List<FormulaAtom> permutation = permutationsGenerator.nextElement();
                if (!contains(examinedPermutations, permutation)) {
                    examinedPermutations.add(permutation);
                    Subsumption subsumption = subsumptionChecker.checkSubsumption(tgd, fatherTgd, tgdAtoms, permutation);
                    if (subsumption != null) {
//                        if (mappingTask.getConfig().rewriteAllHomomorphisms() ||
//                                isProperSubsumption(expansion, fatherExpansion, expansionAtoms, permutation)) {
                        if (subsumptionChecker.isProperHomomorphism(tgd, fatherTgd, tgdAtoms, permutation)) {
                            result.add(subsumption);
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("***********************************Result : \n" + result);
        return result;
    }

    private boolean contains(List<List<FormulaAtom>> examinedPermutations, List<FormulaAtom> permutation) {
        for (List<FormulaAtom> examinedPermutation : examinedPermutations) {
            if (equals(examinedPermutation, permutation)) {
                return true;
            }
        }
        return false;
    }

    private boolean equals(List<FormulaAtom> examinedPermutation, List<FormulaAtom> permutation) {
        if (examinedPermutation.size() != permutation.size()) {
            return false;
        }
        for (int i = 0; i < examinedPermutation.size(); i++) {
            FormulaAtom examinedAtom = examinedPermutation.get(i);
            FormulaAtom atom = permutation.get(i);
            if (!examinedAtom.equals(atom)) {
                return false;
            }
        }
        return true;
    }
}
