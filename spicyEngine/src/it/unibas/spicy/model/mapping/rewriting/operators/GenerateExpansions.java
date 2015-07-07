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

import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.operators.FindJoinGroups;
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.ExpansionBaseView;
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionJoin;
import it.unibas.spicy.model.mapping.rewriting.ExpansionMap;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableProvenanceCondition;
import it.unibas.spicy.utility.GenericListGenerator;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateExpansions {

    private static Log logger = LogFactory.getLog(GenerateExpansions.class);
    private MatchJoinGroups joinGroupMatcher = new MatchJoinGroups();

    public ExpansionMap generateExpansions(List<FORule> stTgds, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Generating expansions of st views");
        Map<ExpansionBaseView, List<Expansion>> expansionMap = new HashMap<ExpansionBaseView, List<Expansion>>();
        List<ExpansionBaseView> baseViews = generateBaseViews(stTgds);
        if (logger.isDebugEnabled()) logger.debug("Base views: " + baseViews);
        generateAtoms(baseViews, mappingTask);
        for (ExpansionBaseView baseView : baseViews) {
            List<Expansion> viewExpansions = generateExpansions(baseView, baseViews);
            expansionMap.put(baseView, viewExpansions);
        }
        ExpansionMap expansions = new ExpansionMap(mappingTask, expansionMap);
        if (logger.isDebugEnabled()) logger.debug("View expansions: " + expansionMap);
        return expansions;
    }

    ////////////////////       BASE VIEWS      //////////////////////////////
    private List<ExpansionBaseView> generateBaseViews(List<FORule> stTgds) {
        List<ExpansionBaseView> result = new ArrayList<ExpansionBaseView>();
        for (FORule stTgd : stTgds) {
            result.add(generateBaseView(stTgd));
        }
        return result;
    }

    private ExpansionBaseView generateBaseView(FORule stTgd) {
        SimpleConjunctiveQuery ttView = stTgd.getTargetView().cloneWithoutAlgebraTree();
        for (SetAlias generator : ttView.getGenerators()) {
            VariableProvenanceCondition provenanceCondition = new VariableProvenanceCondition(generator.getBindingPathExpression().getAbsolutePath(), stTgd.getId());
            generator.setProvenanceCondition(provenanceCondition);
        }
        return new ExpansionBaseView(stTgd, ttView);
    }

    ////////////////////       EXPANSIONS     //////////////////////////////
    private void generateAtoms(List<ExpansionBaseView> baseViews, MappingTask mappingTask) {
        GenerateFormulaAtomsInExpansion atomGenerator = new GenerateFormulaAtomsInExpansion();
        for (ExpansionBaseView baseView : baseViews) {
            List<ExpansionAtom> viewAtoms = atomGenerator.generateExpansionFormulaAtoms(baseView, mappingTask);
            baseView.setFormulaAtoms(viewAtoms);
        }
    }

    private List<Expansion> generateExpansions(ExpansionBaseView baseView, List<ExpansionBaseView> baseViews) {
        if (logger.isDebugEnabled()) logger.debug("\n----Looking for coverage for: \n" + baseView);
        if (SpicyEngineUtility.hasSingleAtom(baseView)) {
            List<Expansion> result = generateSingleAtomExpansion(baseView);
            return result;
        }
        List<ExpansionAtom> viewAtoms = getViewAtoms(baseViews);
        List<List<ExpansionElement>> listOfLists = new ArrayList<List<ExpansionElement>>();
        for (ExpansionAtom atom : baseView.getFormulaAtoms()) {
            List<ExpansionElement> coveringAtoms = findCoveringAtoms(atom, viewAtoms);
            listOfLists.add(coveringAtoms);
        }
        if (logger.isTraceEnabled()) logger.debug("----List of covering atoms found:" + SpicyEngineUtility.printCollection(listOfLists));
        return generateExpansionsForView(baseView, listOfLists);
    }

    private List<Expansion> generateSingleAtomExpansion(ExpansionBaseView baseView) {
        ExpansionAtom singleAtom = baseView.getFormulaAtoms().get(0);
        ExpansionElement coveringAtom = new ExpansionElement(singleAtom, singleAtom);
        Expansion expansion = new Expansion(baseView, coveringAtom);
        expansion.setBase(true);
        List<Expansion> result = new ArrayList<Expansion>();
        result.add(expansion);
        return result;
    }

    private List<ExpansionAtom> getViewAtoms(List<ExpansionBaseView> baseViews) {
        List<ExpansionAtom> result = new ArrayList<ExpansionAtom>();
        for (ExpansionBaseView baseView : baseViews) {
            result.addAll(baseView.getFormulaAtoms());
        }
        return result;
    }

    private List<ExpansionElement> findCoveringAtoms(ExpansionAtom atom, List<ExpansionAtom> viewAtoms) {
        if (logger.isTraceEnabled()) logger.debug("\n----Searching coverage atoms for \n" + atom);
        List<ExpansionElement> result = new ArrayList<ExpansionElement>();
        for (ExpansionAtom otherAtom : viewAtoms) {
            ExpansionElement coveringAtom = checkCoverageForAtom(atom, otherAtom);
            if (coveringAtom != null) {
                result.add(coveringAtom);
            }
        }
        if (logger.isTraceEnabled()) logger.debug("----Result: \n" + result);
        return result;
    }

    private ExpansionElement checkCoverageForAtom(ExpansionAtom atom, ExpansionAtom otherAtom) {
        if (logger.isTraceEnabled()) logger.debug("----Checking atom: \n" + otherAtom);
        if (!atom.getVariable().equalsOrIsClone(otherAtom.getVariable())) {
            if (logger.isTraceEnabled()) logger.debug("----Variables are different, no coverage");
            return null;
        }
        List<VariablePathExpression> originalPaths = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> matchingPaths = new ArrayList<VariablePathExpression>();
        for (ExpansionFormulaPosition position : atom.getPositions()) {
            if (position.isUniversal() || position.isSkolem()) {
                VariablePathExpression originalPath = position.getPathExpression();
                VariablePathExpression matchingPath = findMatchingPath(position, otherAtom);
                if (matchingPath == null) {
                    return null;
                }
                if (position.isUniversal()) {
                    originalPaths.add(originalPath);
                    matchingPaths.add(matchingPath);
                }
            }
        }
        ExpansionElement coveringAtom = new ExpansionElement(atom, otherAtom);
        coveringAtom.setOriginalUniversalPaths(originalPaths);
        coveringAtom.setMatchingUniversalPaths(matchingPaths);
        if (logger.isTraceEnabled()) logger.debug("----Found atom coverage: " + coveringAtom);
        return coveringAtom;
    }

    private VariablePathExpression findMatchingPath(ExpansionFormulaPosition position, ExpansionAtom otherAtom) {
        for (ExpansionFormulaPosition otherPosition : otherAtom.getPositions()) {
            if ((position.isUniversal() && otherPosition.isUniversal()) || position.isSkolemOrNull()) {
                if (otherPosition.getPathExpression().equalsUpToClones(position.getPathExpression())) {
                    return otherPosition.getPathExpression();
                }
            }
        }
        return null;
    }

    private List<Expansion> generateExpansionsForView(ExpansionBaseView baseView, List<List<ExpansionElement>> listOfLists) {
        if (logger.isDebugEnabled()) logger.debug("\n\n----Generating expansions for: " + baseView);
        if (logger.isDebugEnabled()) logger.debug("----List of covering atoms: " + SpicyEngineUtility.printCollection(listOfLists));
        List<Expansion> result = new ArrayList<Expansion>();
        GenericListGenerator<ExpansionElement> listGenerator = new GenericListGenerator<ExpansionElement>();
        List<List<ExpansionElement>> examinedCombinations = new ArrayList<List<ExpansionElement>>();
        for (List<ExpansionElement> combination : listGenerator.generateListsOfElements(listOfLists)) {
            // we are processing all expansions, including the base one
//            if (!meaningfulCombination(combination)) {
//                continue;
//            }
            if (!examinedCombinations.contains(combination)) {
                examinedCombinations.add(combination);
                combination = cloneDuplicateAtoms(combination);
                if (logger.isDebugEnabled()) logger.debug("----Analyzing combination: " + SpicyEngineUtility.printCollection(combination));
                if (logger.isDebugEnabled()) logger.debug("----Checking join conditions.... ");
                List<ExpansionJoin> newJoinConditions = checkJoinsInCoverage(baseView, baseView.getView().getJoinConditions(), combination);
                if (logger.isDebugEnabled()) logger.debug("----Checking cyclic join conditions...");
                List<ExpansionJoin> newCyclicJoinConditions = checkJoinsInCoverage(baseView, baseView.getView().getCyclicJoinConditions(), combination);
                if (newJoinConditions == null || newCyclicJoinConditions == null) {
                    if (logger.isDebugEnabled()) logger.debug("----Combination does not respect joins, skipping");
                    continue;
                }
                Expansion expansion = generateExpansion(baseView, combination, newJoinConditions, newCyclicJoinConditions);
                if (logger.isDebugEnabled()) logger.debug("----Adding new coverage: " + expansion);
                result.add(expansion);
            }
        }
        return result;
    }

//    private boolean meaningfulCombination(List<ExpansionElement> combination) {
//        for (ExpansionElement expansionElement : combination) {
//            if (!expansionElement.getCoveringAtom().equals(expansionElement.getFirstOriginalAtom())) {
//                return true;
//            }
//        }
//        return false;
//    }

    private List<ExpansionElement> cloneDuplicateAtoms(List<ExpansionElement> combination) {
        List<ExpansionElement> newCombination = new ArrayList<ExpansionElement>();
        List<SetAlias> existingVariables = new ArrayList<SetAlias>();
        for (ExpansionElement coveringAtom : combination) {
            if (!containsVariableWithSameId(existingVariables, coveringAtom.getCoveringAtom().getVariable())) {
                existingVariables.add(coveringAtom.getCoveringAtom().getVariable());
                newCombination.add(coveringAtom);
            } else {
                ExpansionElement clone = cloneExpansionElementWithNewId(coveringAtom);
                existingVariables.add(clone.getCoveringAtom().getVariable());
                newCombination.add(clone);
            }
        }
        return newCombination;
    }

    private boolean containsVariableWithSameId(List<SetAlias> list, SetAlias variable) {
        for (SetAlias variableInList : list) {
            if (variableInList.hasSameId(variable)) {
                return true;
            }
        }
        return false;
    }

    private ExpansionElement cloneExpansionElementWithNewId(ExpansionElement element) {
        SetAlias originalVariable = element.getCoveringAtom().getVariable();
        SetAlias newVariable = originalVariable.clone();
        newVariable.changeId();
        ExpansionElement newElement = new ExpansionElement(element, newVariable);
        return newElement;
    }

    private boolean isBaseExpansion(List<ExpansionElement> combination, ExpansionBaseView baseView) {
        for (int i = 0; i < combination.size(); i++) {
            ExpansionElement coveringAtom = combination.get(i);
            ExpansionAtom baseAtom = baseView.getFormulaAtoms().get(i);
            if (!(coveringAtom.getCoveringAtom().getVariable().hasSameId(baseAtom.getVariable())
                    && coveringAtom.getCoveringAtom().getVariable().equals(baseAtom.getVariable())
                    && coveringAtom.getOriginalAtoms().size() == 1)) {
                return false;
            }
        }
        return true;
    }

    ////////////////////       JOINS      //////////////////////////////
    private List<ExpansionJoin> checkJoinsInCoverage(ExpansionBaseView baseView, List<VariableJoinCondition> joinConditions, List<ExpansionElement> combination) {
        if (logger.isDebugEnabled()) logger.debug("----Checking join conditions for combination: " + combination);
        List<ExpansionJoin> result = new ArrayList<ExpansionJoin>();
        for (VariableJoinCondition joinCondition : joinConditions) {
            if (logger.isDebugEnabled()) logger.debug("----Checking join condition " + joinCondition + " in combination");
            ExpansionJoin newJoinCondition = checkJoinCondition(baseView, joinCondition, combination);
            if (logger.isDebugEnabled()) logger.debug("----New join condition " + newJoinCondition);
            if (newJoinCondition == null) {
                return null;
            }
            if (!result.contains(newJoinCondition)) {
                result.add(newJoinCondition);
            }
        }
        return result;
    }

    private ExpansionJoin checkJoinCondition(ExpansionBaseView baseView, VariableJoinCondition joinCondition, List<ExpansionElement> combination) {
        if (logger.isDebugEnabled()) logger.debug("Checking join condition: " + joinCondition +"\nin combination:\n" + SpicyEngineUtility.printCollection(combination));
        ExpansionAtom fromAtom = findCoveringAtomForJoinPaths(baseView, joinCondition.getFromVariable(), combination);
        ExpansionAtom toAtom = findCoveringAtomForJoinPaths(baseView, joinCondition.getToVariable(), combination);
        if (logger.isDebugEnabled()) logger.debug("From atom: " + fromAtom);
        if (logger.isDebugEnabled()) logger.debug("To atom: " + toAtom);
        SetAlias fromAlias = findAliasInAtom(joinCondition.getFromVariable(), fromAtom);
        SetAlias toAlias = findAliasInAtom(joinCondition.getToVariable(), toAtom);
        if (canBeASelfJoin(joinCondition) && fromAtom.equals(toAtom)) {
            if (logger.isDebugEnabled()) logger.debug("----Self join, ok");
            VariableJoinCondition newJoin = SpicyEngineUtility.changeJoinCondition(joinCondition, fromAlias, toAlias);
            ExpansionJoin newExpansionJoin = new ExpansionJoin(newJoin);
            newExpansionJoin.setSelfJoin(true);
            if (!areUniversal(newJoin.getFromPaths(), fromAtom) && !areUniversal(newJoin.getToPaths(), toAtom)) {
                newExpansionJoin.setJoinOnSkolems(true);
            }
            return newExpansionJoin;
        }
        if (areUniversal(joinCondition.getFromPaths(), fromAtom) && areUniversal(joinCondition.getToPaths(), toAtom)) {
            if (logger.isDebugEnabled()) logger.debug("----Positions are universal, ok");
            VariableJoinCondition newJoin = SpicyEngineUtility.changeJoinCondition(joinCondition, fromAtom.getVariable(), toAtom.getVariable());
            ExpansionJoin newExpansionJoin = new ExpansionJoin(newJoin);
            return newExpansionJoin;
        }
        if (areSkolem(joinCondition.getFromPaths(), fromAtom) && areSkolem(joinCondition.getToPaths(), toAtom)) {
            if (logger.isDebugEnabled()) logger.debug("----Positions are skolem");
            if (fromAtom.getTgd().equals(toAtom.getTgd())) {
                if (logger.isDebugEnabled()) logger.debug("----Positions are skolem in the same tgd, ok");
                VariableJoinCondition newJoin = SpicyEngineUtility.changeJoinCondition(joinCondition, fromAtom.getVariable(), toAtom.getVariable());
                if (existsContainingJoinGroup(newJoin, fromAtom.getTgd())) {
                    if (logger.isDebugEnabled()) logger.debug("----They belong to the same join group");
                    ExpansionJoin newExpansionJoin = new ExpansionJoin(newJoin);
                    newExpansionJoin.setJoinOnSkolems(true);
                    return newExpansionJoin;
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("----Join condition failed. Returning false");
        return null;
    }

    private boolean canBeASelfJoin(VariableJoinCondition joinCondition) {
        for (int i = 0; i < joinCondition.getFromPaths().size(); i++) {
            if (!joinCondition.getFromPaths().get(i).equalsUpToClones(joinCondition.getToPaths().get(i))) {
                return false;
            }
        }
        return true;
    }

    private ExpansionAtom findCoveringAtomForJoinPaths(ExpansionBaseView baseView, SetAlias joinVariable, List<ExpansionElement> combination) {
        int positionOfVariableInTgd = findPositionOfVariableInTgd(baseView, joinVariable);
        return combination.get(positionOfVariableInTgd).getCoveringAtom();
    }

    private int findPositionOfVariableInTgd(ExpansionBaseView baseView, SetAlias variable) {
        for (int i = 0; i < baseView.getView().getVariables().size(); i++) {
            SetAlias tgdVariable = baseView.getView().getVariables().get(i);
            for (SetAlias generator : tgdVariable.getGenerators()) {
                if (generator.hasSameId(variable)) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find join variable: " + variable + "\nin source view: " + baseView.getView());
    }

    private SetAlias findAliasInAtom(SetAlias alias, ExpansionAtom atom) {
        for (SetAlias generator : atom.getVariable().getGenerators()) {
            if (generator.equalsOrIsClone(alias)) {
                return generator;
            }
        }
        throw new IllegalMappingTaskException("Unable to find alias for " + alias + "\nin atom:\n" + atom);
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

    private boolean areSkolem(List<VariablePathExpression> joinPaths, ExpansionAtom atom) {
        for (VariablePathExpression joinPath : joinPaths) {
            if (!isSkolem(joinPath, atom)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSkolem(VariablePathExpression joinPath, ExpansionAtom atom) {
        for (ExpansionFormulaPosition position : atom.getPositions()) {
            if (position.getPathExpression().equalsUpToClones(joinPath)) {
                return position.isSkolem();
            }
        }
        throw new IllegalArgumentException("Path is not contained in atom :" + joinPath + " - " + atom);
    }

    private boolean existsContainingJoinGroup(VariableJoinCondition joinCondition, FORule tgd) {
        if (logger.isDebugEnabled()) logger.trace("----Checking join groups in tgd " + tgd + "\nfor join condition: " + joinCondition);
        FindJoinGroups joinGroupFinder = new FindJoinGroups();
        List<JoinGroup> tgdJoinGroups = joinGroupFinder.findJoinGroups(tgd.getTargetView().getAllJoinConditions());
        if (logger.isDebugEnabled()) logger.trace("----Found join groups" + tgdJoinGroups);
        SetAlias fromVariableInTgd = findVariableInTgd(joinCondition.getFromVariable(), tgd);
        SetAlias toVariableInTgd = findVariableInTgd(joinCondition.getToVariable(), tgd);
        VariableJoinCondition joinInOriginalTgd = SpicyEngineUtility.changeJoinCondition(joinCondition, fromVariableInTgd, toVariableInTgd);
        for (JoinGroup tgdJoinGroup : tgdJoinGroups) {
            if (joinGroupMatcher.checkJoinConditionContainment(joinInOriginalTgd, tgdJoinGroup)) {
                if (logger.isDebugEnabled()) logger.trace("----Found matching join group" + tgdJoinGroup);
                return true;
            }
        }
        if (logger.isDebugEnabled()) logger.trace("----Unable to find matching join group...");
        return false;
    }

    private SetAlias findVariableInTgd(SetAlias variable, FORule tgd) {
        for (SetAlias tgdVariable : tgd.getTargetView().getGenerators()) {
            if (tgdVariable.equalsUpToProvenance(variable)) {
                return tgdVariable;
            }
        }
//        return null;
        throw new IllegalArgumentException("Unable to find variable " + variable + " in tgd " + tgd);
    }

    /////////////////////////////   COVERAGE GENERATION (WITH REDUCTION)  ///////////////////////////////
    private Expansion generateExpansion(ExpansionBaseView baseView, List<ExpansionElement> combination,
            List<ExpansionJoin> joinConditions, List<ExpansionJoin> cyclicJoinConditions) {
        if (logger.isDebugEnabled()) logger.debug("Generating reduced expansion for: " + combination + " with " + joinConditions + " - Base view: " + baseView);
        List<ExpansionElement> reducedAtoms = new ArrayList<ExpansionElement>();
        List<ExpansionJoin> reducedJoinConditions = new ArrayList<ExpansionJoin>();
        addFirstAtom(combination, reducedAtoms);
        for (int i = 0; i < joinConditions.size(); i++) {
            VariableJoinCondition originalJoinCondition = baseView.getView().getJoinConditions().get(i);
            ExpansionJoin joinCondition = joinConditions.get(i);
            ExpansionElement atom = combination.get(i + 1);
            addJoinToReducedCoverage(originalJoinCondition, joinCondition, atom, reducedAtoms, reducedJoinConditions, combination);
        }
        List<ExpansionJoin> reducedCyclicJoinConditions = new ArrayList<ExpansionJoin>();
        for (int i = 0; i < cyclicJoinConditions.size(); i++) {
            VariableJoinCondition originalCyclicJoinCondition = baseView.getView().getCyclicJoinConditions().get(i);
            ExpansionJoin cyclicJoinCondition = cyclicJoinConditions.get(i);
            addCyclicJoinToReducedCoverage(originalCyclicJoinCondition, cyclicJoinCondition, reducedAtoms, reducedCyclicJoinConditions, reducedJoinConditions, combination);
        }
        Expansion reducedExpansion = new Expansion(baseView, reducedAtoms, reducedJoinConditions, reducedCyclicJoinConditions);
        setPaths(reducedExpansion);
        if (isBaseExpansion(combination, baseView)) {
            reducedExpansion.setBase(true);
        }
        if (logger.isDebugEnabled()) logger.debug("Final reduced expansion: " + reducedExpansion);
        return reducedExpansion;
    }

    private void addFirstAtom(List<ExpansionElement> combination, List<ExpansionElement> reducedAtoms) {
        ExpansionElement firstAtom = combination.get(0);
        reducedAtoms.add(firstAtom.clone());
    }

    private void addJoinToReducedCoverage(VariableJoinCondition originalJoinCondition, ExpansionJoin newJoinCondition,
            ExpansionElement newAtom, List<ExpansionElement> reducedAtoms,
            List<ExpansionJoin> reducedJoinConditions, List<ExpansionElement> combination) {
        if (logger.isDebugEnabled()) logger.debug("Adding join condition: " + newJoinCondition + " - New atom " + newAtom + " - Combination: " + combination + " - Reduced atoms: " + reducedAtoms);
        // NOTE: this could be optimized: it performs join on Skolems that are actually non needed
//        if (!isSelfJoin(newJoinCondition) || joinOnUniversal(newJoinCondition, newAtom)) {
        if (!newJoinCondition.isSelfJoin() || newJoinCondition.isJoinOnUniversal()) {
            // IMPORTANT: if this is a self-join on universal attributes
            // both copies of the variable are needed to perform the join in all possible ways
            // NOTE: due to possible alterations in the order of variables in reduced atoms we need in general to re-change the join
            reducedAtoms.add(newAtom.clone());
            ExpansionElement fromAtomInCoverage = findAtom(originalJoinCondition.getFromVariable(), reducedAtoms);
            ExpansionElement toAtomInCoverage = findAtom(originalJoinCondition.getToVariable(), reducedAtoms);
            // join condition must be corrected again because from or to atom could have been covered at this point
            VariableJoinCondition correctJoinCondition = SpicyEngineUtility.changeJoinCondition(originalJoinCondition, fromAtomInCoverage.getCoveringAtom().getVariable(), toAtomInCoverage.getCoveringAtom().getVariable());
            ExpansionJoin correctedExpansionJoin = new ExpansionJoin(newJoinCondition, correctJoinCondition);
            correctedExpansionJoin.setFromElement(fromAtomInCoverage);
            correctedExpansionJoin.setToElement(toAtomInCoverage);
            reducedJoinConditions.add(correctedExpansionJoin);
        } else {
            // join on skolems -- don't need to add the join condition
            ExpansionElement fromAtomInCoverage = findAtom(originalJoinCondition.getFromVariable(), reducedAtoms);
            ExpansionElement toAtomInCoverage = findAtom(originalJoinCondition.getToVariable(), reducedAtoms);
            if (fromAtomInCoverage != null && toAtomInCoverage == null) {
                // fromAtom exists, newAtom is toAtom
                addToExistingAtom(fromAtomInCoverage, newAtom);
            } else if (toAtomInCoverage != null && fromAtomInCoverage == null) {
                // toAtom exists, newAtom is fromAtom
                addToExistingAtom(toAtomInCoverage, newAtom);
            } else {
//                logger.error("Join condition i must add atom i+1 - Join to add: " + newJoinCondition + " - New atom " + newAtom + " - Combination: " + combination + " - Reduced atoms: " + reducedAtoms + " - Reduced joins: " + reducedJoinConditions);
                throw new IllegalArgumentException("Join condition i must add atom i+1 " + newJoinCondition + " - New atom " + newAtom + " - Combination: " + combination + " - Reduced atoms: " + reducedAtoms);
            }
        }
    }

    private void addCyclicJoinToReducedCoverage(VariableJoinCondition originalJoinCondition, ExpansionJoin newJoinCondition,
            List<ExpansionElement> reducedAtoms, List<ExpansionJoin> reducedCyclicJoinConditions, List<ExpansionJoin> reducedJoinConditions, List<ExpansionElement> combination) {
        if (logger.isDebugEnabled()) logger.debug("--- Adding join condition: " + newJoinCondition + " - corresponding to original join: " + originalJoinCondition);
//        ExpansionElement fromAtom = findAtomInCombination(originalJoinCondition.getFromVariable(), combination);
//        if (!isSelfJoin(newJoinCondition) || joinOnUniversal(newJoinCondition, fromAtom)) {
        if (!newJoinCondition.isSelfJoin() || newJoinCondition.isJoinOnUniversal()) {
            ExpansionElement fromAtomInCoverage = findAtom(originalJoinCondition.getFromVariable(), reducedAtoms);
            ExpansionElement toAtomInCoverage = findAtom(originalJoinCondition.getToVariable(), reducedAtoms);
            // join condition must be corrected again because from or to atom could have been covered at this point
            VariableJoinCondition correctJoinCondition = SpicyEngineUtility.changeJoinCondition(originalJoinCondition, fromAtomInCoverage.getCoveringAtom().getVariable(), toAtomInCoverage.getCoveringAtom().getVariable());
            ExpansionJoin correctedExpansionJoin = new ExpansionJoin(newJoinCondition, correctJoinCondition);
            correctedExpansionJoin.setFromElement(fromAtomInCoverage);
            correctedExpansionJoin.setToElement(toAtomInCoverage);
            if (!contains(reducedCyclicJoinConditions, correctedExpansionJoin) && !contains(reducedJoinConditions, correctedExpansionJoin)) {
                reducedCyclicJoinConditions.add(correctedExpansionJoin);
            }
        }
    }

    private boolean contains(List<ExpansionJoin> expansionJoins, ExpansionJoin joinCondition) {
        for (ExpansionJoin expansionJoin : expansionJoins) {
            if (expansionJoin.getJoinCondition().equals(joinCondition.getJoinCondition())) {
                return true;
            }
        }
        return false;
    }

    private ExpansionElement findAtom(SetAlias variable, List<ExpansionElement> atoms) {
        for (ExpansionElement coveringAtom : atoms) {
            for (ExpansionAtom coveredAtom : coveringAtom.getOriginalAtoms()) {
                if (coveredAtom.getVariable().hasSameId(variable)) {
                    return coveringAtom;
                }
            }
        }
        return null;
    }

    private void addToExistingAtom(ExpansionElement existingAtom, ExpansionElement newAtom) {
        existingAtom.addOriginalAtom(newAtom.getFirstOriginalAtom());
        existingAtom.getOriginalUniversalPaths().addAll(newAtom.getOriginalUniversalPaths());
        addPaths(existingAtom.getCoveringAtom().getVariable(), existingAtom.getMatchingUniversalPaths(), newAtom.getMatchingUniversalPaths());
    }

    private void addPaths(SetAlias originalVariable, List<VariablePathExpression> originalPaths, List<VariablePathExpression> newPaths) {
        for (VariablePathExpression newPath : newPaths) {
            VariablePathExpression correctedPath = new VariablePathExpression(newPath, originalVariable);
            originalPaths.add(correctedPath);
        }
    }

    private void setPaths(Expansion reducedCoverage) {
        List<VariablePathExpression> originalPaths = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> matchingPaths = new ArrayList<VariablePathExpression>();
        for (ExpansionElement coveringAtom : reducedCoverage.getExpansionElements()) {
            originalPaths.addAll(coveringAtom.getOriginalUniversalPaths());
            matchingPaths.addAll(coveringAtom.getMatchingUniversalPaths());
        }
        reducedCoverage.setOriginalUniversalPaths(originalPaths);
        reducedCoverage.setMatchingUniversalPaths(matchingPaths);
    }

}
