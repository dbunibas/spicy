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
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.ExpansionJoin;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.GenericPermutationsGenerator;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateExpansionMoreCompactOrder {

    private static Log logger = LogFactory.getLog(GenerateExpansionMoreCompactOrder.class);

    private CheckExpansionHomomorphism homomorphismChecker = new CheckExpansionHomomorphism();

    @SuppressWarnings("unchecked")
    public ExpansionPartialOrder generateOrdering(List<Expansion> expansions, MappingTask mappingTask) {
        Map<Expansion, List<ExpansionOrderedPair>> partialOrder = new HashMap<Expansion, List<ExpansionOrderedPair>>();
        for (Expansion expansion : expansions) {
//            if (SpicyEngineUtility.hasSingleAtom(expansion)) {
//                subsumptions.put(expansion, Collections.EMPTY_LIST);
//            } else {
            partialOrder.put(expansion, findMoreCompactExpansions(expansion, expansions, mappingTask));
//            }
        }
//        if (mappingTask.getConfig().rewriteAllHomomorphisms()) {
//            new RemoveCyclesInSubsumptions().removeCyclesInSelfJoinSubsumptionMap(subsumptions);
//        }
        return new ExpansionPartialOrder(mappingTask, partialOrder);
    }

    private List<ExpansionOrderedPair> findMoreCompactExpansions(Expansion expansion, List<Expansion> expansions, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Finding subsumptions for expansion: " + expansion);
        List<ExpansionOrderedPair> subsumptions = new ArrayList<ExpansionOrderedPair>();
        for (Expansion otherExpansion : expansions) {
            if (otherExpansion.equals(expansion)) {
                continue;
            }
            if (!otherExpansion.getBaseView().equals(expansion.getBaseView())) {
                continue;
            }
            List<ExpansionOrderedPair> moreCompactExpansions = isMoreCompact(otherExpansion, expansion, mappingTask);
            if (moreCompactExpansions != null) {
                subsumptions.addAll(moreCompactExpansions);
            }
        }
        return subsumptions;
    }

    @SuppressWarnings("unchecked")
    private List<ExpansionOrderedPair> isMoreCompact(Expansion fatherExpansion, Expansion expansion, MappingTask mappingTask) {
        if (expansion.equals(fatherExpansion)) {
            throw new IllegalArgumentException("Cannot check subsumption of expansion by itself: " + expansion);
        }
        List<List<ExpansionElement>> examinedPermutations = new ArrayList<List<ExpansionElement>>();
        if (logger.isDebugEnabled()) logger.debug("Checking subsumption of : \n" + expansion + "by : \n" + fatherExpansion);
        List<ExpansionOrderedPair> result = new ArrayList<ExpansionOrderedPair>();
        // it is necessary to use the expanded coverage to avoid penalizing more compact self-joins
        List<ExpansionElement> expansionAtoms = expandAtoms(expansion);
        List<ExpansionElement> fatherExpansionAtoms = expandAtoms(fatherExpansion);
        if (logger.isDebugEnabled()) logger.debug("Expanded atoms : \n" + expansionAtoms + " and : \n" + fatherExpansionAtoms);
        GenericPermutationsGenerator<ExpansionElement> permutationsGenerator = new GenericPermutationsGenerator<ExpansionElement>(fatherExpansionAtoms);
        while (permutationsGenerator.hasMoreElements()) {
            List<ExpansionElement> permutation = permutationsGenerator.nextElement();
            if (!contains(examinedPermutations, permutation)) {
                examinedPermutations.add(permutation);
                ExpansionOrderedPair homomorphism = checkHomomorphism(expansion, fatherExpansion, expansionAtoms, permutation, mappingTask);
                if (homomorphism != null) {
                    result.add(homomorphism);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return result;
    }

    private List<ExpansionElement> expandAtoms(Expansion expansion) {
        List<ExpansionElement> result = new ArrayList<ExpansionElement>();
        for (ExpansionElement atom : expansion.getExpansionElements()) {
            for (int i = 0; i < atom.getOriginalAtoms().size(); i++) {
                result.add(atom);
            }
        }
        return result;
    }

    private boolean contains(List<List<ExpansionElement>> examinedPermutations, List<ExpansionElement> permutation) {
        for (List<ExpansionElement> examinedPermutation : examinedPermutations) {
//            if (equals(examinedPermutation, permutation)) {
            if (examinedPermutation.equals(permutation)) {
                return true;
            }
        }
        return false;
    }

//    private boolean equals(List<ExpansionElement> examinedPermutation, List<ExpansionElement> permutation) {
//        if (examinedPermutation.size() != permutation.size()) {
//            return false;
//        }
//        for (int i = 0; i < examinedPermutation.size(); i++) {
//            ExpansionElement examinedAtom = examinedPermutation.get(i);
//            ExpansionElement atom = permutation.get(i);
//            if (!examinedAtom.getAtom().getVariable().equalsAndHasSameId(atom.getAtom().getVariable())) {
//                return false;
//            }
//        }
//        return true;
//    }

    private ExpansionOrderedPair checkHomomorphism(Expansion expansion, Expansion fatherExpansion, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("---Analyzing permutation: \n" + fatherAtoms);
        List<VariablePathExpression> matchingPaths = homomorphismChecker.checkCoverageAmongAtoms(expansionAtoms, fatherAtoms);
        if (matchingPaths == null) {
            return null;
        }
        List<ExpansionJoin> additionalJoins = checkJoins(expansion, fatherExpansion, expansionAtoms, fatherAtoms);
        if (additionalJoins == null) {
            return null;
        }
        if (!homomorphismChecker.checkIfMoreCompact(expansion, fatherExpansion, expansionAtoms, fatherAtoms)) {
            return null;
        }
        List<VariablePathExpression> expansionUniversalPaths = homomorphismChecker.findUniversalPaths(expansionAtoms);
        ExpansionOrderedPair subsumption = new ExpansionOrderedPair(expansion, fatherExpansion, expansionUniversalPaths, matchingPaths, SpicyEngineConstants.MORE_COMPACT);
        subsumption.setAdditionalCyclicJoins(additionalJoins);
        return subsumption;
    }

    /////////////////////////  JOIN CHECK
    private List<ExpansionJoin> checkJoins(Expansion expansion, Expansion fatherExpansion, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        if (logger.isDebugEnabled()) logger.debug("---Checking joins: \n" + expansion.getJoinConditions());
        // notice: we assume that these are expansions of the same base view, so joins are already homomorphic
        List<ExpansionJoin> result = new ArrayList<ExpansionJoin>();
        for (ExpansionJoin expansionJoin : expansion.getJoinConditions()) {
            VariableJoinCondition joinCondition = expansionJoin.getJoinCondition();
            ExpansionAtom fatherFromAtom = findFatherVariable(joinCondition.getFromVariable(), expansionAtoms, fatherAtoms);
            ExpansionAtom fatherToAtom = findFatherVariable(joinCondition.getToVariable(), expansionAtoms, fatherAtoms);
            if (existsFatherJoin(joinCondition, fatherFromAtom.getVariable(), fatherToAtom.getVariable(), fatherExpansion)) {
                continue;
            }
            if (fatherFromAtom.getVariable().hasSameId(fatherToAtom.getVariable())) {
                continue;
            }
            if (fatherFromAtom.equals(fatherToAtom)) {
                if (areUniversal(joinCondition.getFromPaths(), fatherFromAtom) && areUniversal(joinCondition.getToPaths(), fatherToAtom)) {
                    // only if it is not on Skolems
                    result.add(changeExpansionJoin(expansionJoin, fatherFromAtom, fatherToAtom));
                } else {
                    // two different copies of the same atom
                    // this is actually a hack; the actual solution would be to keep both the expansion
                    // with a single copy of the atom and that with the two copies, and say that the second
                    // is NOT more compact
                    if (!fatherFromAtom.getVariable().hasSameId(fatherToAtom.getVariable())) {
                        result.add(generateJoinForAllUniversalPaths(fatherFromAtom, fatherToAtom));
                    }
                }
            } else {
                return null;
            }
        }
        return result;
    }

    private ExpansionAtom findFatherVariable(SetAlias variable, List<ExpansionElement> atoms, List<ExpansionElement> fatherAtoms) {
        for (int i = 0; i < atoms.size(); i++) {
            ExpansionElement atom = atoms.get(i);
            if (atom.getCoveringAtom().getVariable().hasSameId(variable)) {
                return fatherAtoms.get(i).getCoveringAtom();
            }
        }
        throw new IllegalArgumentException("Unable to find atom for variable: " + variable + " in " + atoms);
    }

    private boolean existsFatherJoin(VariableJoinCondition joinCondition, SetAlias fatherFromVariable, SetAlias fatherToVariable, Expansion fatherExpansion) {
        for (ExpansionJoin fatherExpansionJoin : fatherExpansion.getJoinConditions()) {
            VariableJoinCondition fatherJoinCondition = fatherExpansionJoin.getJoinCondition();
            if (fatherJoinCondition.getFromVariable().hasSameId(fatherFromVariable)
                    && fatherJoinCondition.getToVariable().hasSameId(fatherToVariable)
                    && fatherJoinCondition.equalsUpToClones(joinCondition)) {
                return true;
            }
        }
        return false;
    }

    private ExpansionJoin changeExpansionJoin(ExpansionJoin expansionJoin, ExpansionAtom fatherFromAtom, ExpansionAtom fatherToAtom) {
        VariableJoinCondition joinCondition = expansionJoin.getJoinCondition();
        List<VariablePathExpression> newFromPaths = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> newToPaths = new ArrayList<VariablePathExpression>();
        int numberOfPaths = joinCondition.getFromPaths().size();
        for (int i = 0; i < numberOfPaths; i++) {
            VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
            VariablePathExpression toPath = joinCondition.getToPaths().get(i);
            VariablePathExpression fatherFromPath = new VariablePathExpression(fatherFromAtom.getVariable(), fromPath.getPathSteps());
            VariablePathExpression fatherToPath = new VariablePathExpression(fatherToAtom.getVariable(), toPath.getPathSteps());
            newFromPaths.add(fatherFromPath);
            newToPaths.add(fatherToPath);
        }
        VariableJoinCondition newJoinCondition = new VariableJoinCondition(newFromPaths, newToPaths, joinCondition.isMonodirectional(), joinCondition.isMandatory());
        ExpansionJoin newExpansionJoin = new ExpansionJoin(newJoinCondition);
        newExpansionJoin.setSelfJoin(true);
        newExpansionJoin.setJoinOnSkolems(false);
        return newExpansionJoin;
    }

    private boolean areUniversal(List<VariablePathExpression> joinPaths, ExpansionAtom atom) {
        for (VariablePathExpression joinPath : joinPaths) {
            if (!isUniversal(joinPath, atom)) {
                return false;
            }
        }
        return true;
    }

    private boolean isUniversal(VariablePathExpression joinPath, ExpansionAtom atom) {
        for (ExpansionFormulaPosition position : atom.getPositions()) {
            if (position.getPathExpression().equalsUpToClones(joinPath)) {
                return position.isUniversal();
            }
        }
        throw new IllegalArgumentException("Path is not contained in atom :" + joinPath + " - " + atom);
    }

    private ExpansionJoin generateJoinForAllUniversalPaths(ExpansionAtom fatherFromAtom, ExpansionAtom fatherToAtom) {
        SetAlias fromVariable = fatherFromAtom.getVariable();
        SetAlias toVariable = fatherToAtom.getVariable();
        List<VariablePathExpression> fromPaths = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> toPaths = new ArrayList<VariablePathExpression>();
        for (ExpansionFormulaPosition position : fatherFromAtom.getPositions()) {
            if (position.isUniversal()) {
                fromPaths.add(new VariablePathExpression(position.getPathExpression(), fromVariable));
                toPaths.add(new VariablePathExpression(position.getPathExpression(), toVariable));
            }
        }
        VariableJoinCondition join = new VariableJoinCondition(fromPaths, toPaths, false, true);
        ExpansionJoin expansionJoin = new ExpansionJoin(join);
        expansionJoin.setSelfJoin(true);
        return expansionJoin;
    }
}
