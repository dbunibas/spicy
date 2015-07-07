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
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.ExpansionOrderedPair;
import it.unibas.spicy.model.mapping.rewriting.ExpansionPartialOrder;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.utility.GenericCombinationsGenerator;
import it.unibas.spicy.utility.GenericPermutationsGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateExpansionMoreInformativeOrder {

    private static Log logger = LogFactory.getLog(GenerateExpansionMoreInformativeOrder.class);

    private CheckExpansionHomomorphism homomorphismChecker = new CheckExpansionHomomorphism();

    @SuppressWarnings("unchecked")
    public ExpansionPartialOrder generateOrdering(List<Expansion> expansions, MappingTask mappingTask) {
        Map<Expansion, List<ExpansionOrderedPair>> subsumptions = new HashMap<Expansion, List<ExpansionOrderedPair>>();
        for (Expansion expansion : expansions) {
            subsumptions.put(expansion, findSubsumptionsForExpansion(expansion, expansions, mappingTask));
        }
//        if (mappingTask.getConfig().rewriteAllHomomorphisms()) {
//            new RemoveCyclesInSubsumptions().removeCyclesInTTExpansionSubsumptionMap(subsumptions);
//        }
        return new ExpansionPartialOrder(mappingTask, subsumptions);
    }

    /////////////////////////   SUBSUMPTIONS SEARCH
    private List<ExpansionOrderedPair> findSubsumptionsForExpansion(Expansion expansion, List<Expansion> expansions, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Finding subsumptions for expansion: " + expansion);
        List<ExpansionOrderedPair> subsumptions = new ArrayList<ExpansionOrderedPair>();
        for (Expansion otherExpansion : expansions) {
            if (otherExpansion.equals(expansion)) {
                continue;
            }
            List<ExpansionOrderedPair> subsumption = isMoreInformative(otherExpansion, expansion, mappingTask);
            if (subsumption != null) {
                subsumptions.addAll(subsumption);
            }
        }
        return subsumptions;
    }

    @SuppressWarnings("unchecked")
    private List<ExpansionOrderedPair> isMoreInformative(Expansion fatherExpansion, Expansion expansion, MappingTask mappingTask) {
        if (expansion.equals(fatherExpansion)) {
            throw new IllegalArgumentException("Cannot check subsumption of expansion by itself: " + expansion);
        }
        if (logger.isDebugEnabled()) logger.debug("***********************************Checking subsumption of : \n" + expansion + "by : \n" + fatherExpansion);
        List<List<ExpansionElement>> examinedPermutations = new ArrayList<List<ExpansionElement>>();
        List<ExpansionOrderedPair> result = new ArrayList<ExpansionOrderedPair>();
        List<ExpansionElement> expansionAtoms = expansion.getExpansionElements();
        List<ExpansionElement> fatherExpansionAtoms = fatherExpansion.getExpansionElements();
        if (expansionAtoms.size() > fatherExpansionAtoms.size()) {
            return Collections.EMPTY_LIST;
        }
        GenericCombinationsGenerator<ExpansionElement> combinationsGenerator = new GenericCombinationsGenerator<ExpansionElement>(fatherExpansionAtoms, expansionAtoms.size());
        while (combinationsGenerator.hasMoreElements()) {
            List<ExpansionElement> combination = combinationsGenerator.nextElement();
            GenericPermutationsGenerator<ExpansionElement> permutationsGenerator = new GenericPermutationsGenerator<ExpansionElement>(combination);
            while (permutationsGenerator.hasMoreElements()) {
                List<ExpansionElement> permutation = permutationsGenerator.nextElement();
                if (!contains(examinedPermutations, permutation)) {
                    examinedPermutations.add(permutation);
                    ExpansionOrderedPair orderedPair = homomorphismChecker.checkHomomorphism(expansion, expansionAtoms, fatherExpansion, permutation);
                    if (orderedPair != null) {
//                        if (mappingTask.getConfig().rewriteAllHomomorphisms() ||
//                                isProperSubsumption(expansion, fatherExpansion, expansionAtoms, permutation)) {
                        if (homomorphismChecker.checkIfMoreInformative(expansion, fatherExpansion, expansionAtoms, permutation)) {
                            result.add(orderedPair);
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("***********************************Result: " + result);
        return result;
    }

    private boolean contains(List<List<ExpansionElement>> examinedPermutations, List<ExpansionElement> permutation) {
        for (List<ExpansionElement> examinedPermutation : examinedPermutations) {
            if (equals(examinedPermutation, permutation)) {
                return true;
            }
        }
        return false;
    }

    private boolean equals(List<ExpansionElement> examinedPermutation, List<ExpansionElement> permutation) {
        if (examinedPermutation.size() != permutation.size()) {
            return false;
        }
        for (int i = 0; i < examinedPermutation.size(); i++) {
            ExpansionElement examinedAtom = examinedPermutation.get(i);
            ExpansionElement atom = permutation.get(i);
            if (!examinedAtom.getCoveringAtom().equals(atom.getCoveringAtom())) {
                return false;
            }
        }
        return true;
    }
}
