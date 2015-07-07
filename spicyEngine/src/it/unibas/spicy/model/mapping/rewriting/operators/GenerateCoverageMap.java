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
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.operators.FindJoinGroups;
import it.unibas.spicy.model.mapping.operators.RenameSetAliasesUtility;
import it.unibas.spicy.model.mapping.rewriting.CoverageAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.CoverageMap;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.GenericListGenerator;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateCoverageMap {

    private static Log logger = LogFactory.getLog(GenerateCoverageMap.class);

    private RenameSetAliasesUtility renamer = new RenameSetAliasesUtility();
    private FindJoinGroups joinGroupFinder = new FindJoinGroups();
    private MatchJoinGroups joinGroupMatcher = new MatchJoinGroups();

    public CoverageMap generateCoverageMap(List<FORule> tgds, MappingTask mappingTask) {
        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
            throw new IllegalArgumentException("Unable to find coverages for mapping tasks with self joins in conclusions");
        }
        Map<FORule, List<Coverage>> coverageHashMap = new HashMap<FORule, List<Coverage>>();
        for (FORule tgd : tgds) {
            List<Coverage> coverages = findCoveragesForTgd(tgd, tgds, mappingTask);
            coverageHashMap.put(tgd, coverages);
        }
        CoverageMap coverageMap = new CoverageMap(mappingTask, coverageHashMap);
        return coverageMap;
    }

    @SuppressWarnings("unchecked")
    private List<Coverage> findCoveragesForTgd(FORule tgd, List<FORule> tgds, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("----Looking for coverage for: \n" + tgd);
        if (tgd.getTargetView().getVariables().size() == 1) {
            if (logger.isDebugEnabled()) logger.debug("----TGD has a single target atom, returning empty list ");
            return Collections.EMPTY_LIST;
        }
        List<List<CoverageAtom>> listOfLists = generateCoveringAtomsForTgd(tgd, tgds, mappingTask);
        if (listOfLists == null) {
            return Collections.EMPTY_LIST;
        }
        return generateCoverages(tgd, listOfLists, mappingTask);
    }

    //////////////////////////    FINDING COVERING ATOMS   //////////////////////////////////
    private List<List<CoverageAtom>> generateCoveringAtomsForTgd(FORule tgd, List<FORule> tgds, MappingTask mappingTask) {
        List<List<CoverageAtom>> listOfLists = new ArrayList<List<CoverageAtom>>();
        for (FormulaAtom atom : tgd.getTargetFormulaAtoms(mappingTask)) {
            List<CoverageAtom> coveringAtoms = findCoveringAtoms(atom, tgds, mappingTask);
            if (coveringAtoms.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("----No coverage found");
                return null;
            }
            listOfLists.add(coveringAtoms);
        }
        if (logger.isDebugEnabled()) logger.debug("----List of covering atoms found:" + SpicyEngineUtility.printCollection(listOfLists));
        return listOfLists;
    }

    private List<CoverageAtom> findCoveringAtoms(FormulaAtom atom, List<FORule> otherTgds, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("----Searching coverage atoms for \n" + atom);
        List<CoverageAtom> result = new ArrayList<CoverageAtom>();
        for (FORule otherTgd : otherTgds) {
            if (atom.getTgd().equals(otherTgd)) {
                if (logger.isDebugEnabled()) logger.debug("----Same tgd, no coverage");
                continue;
            }
            for (FormulaAtom otherAtom : otherTgd.getTargetFormulaAtoms(mappingTask)) {
                CoverageAtom atomCoverage = checkCoverageAmongAtoms(atom, otherAtom);
                if (atomCoverage != null) {
                    result.add(atomCoverage);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("----Result: \n" + result);
        return result;
    }

    private CoverageAtom checkCoverageAmongAtoms(FormulaAtom atom, FormulaAtom otherAtom) {
        if (logger.isDebugEnabled()) logger.debug("----Checking atom: \n" + otherAtom);
        if (!atom.getVariable().equals(otherAtom.getVariable())) {
            if (logger.isDebugEnabled()) logger.debug("----Variables are different, no coverage");
            return null;
        }
        CoverageAtom atomCoverage = new CoverageAtom(atom, otherAtom);
        List<VariableCorrespondence> originalCorrespondences = new ArrayList<VariableCorrespondence>();
        List<VariableCorrespondence> matchingCorrespondences = new ArrayList<VariableCorrespondence>();
        for (FormulaPosition position : atom.getPositions()) {
            if (position.isUniversal()) {
                VariableCorrespondence correspondence = position.getCorrespondence();
                VariableCorrespondence matchingCorrespondence = findMatchingCorrespondence(position, otherAtom);
                if (matchingCorrespondence == null) {
                    return null;
                }
                originalCorrespondences.add(correspondence);
                matchingCorrespondences.add(matchingCorrespondence);
            }
        }
        atomCoverage.setOriginalCorrespondences(originalCorrespondences);
        atomCoverage.setMatchingCorrespondences(matchingCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("----Found atom coverage: " + atomCoverage);
        return atomCoverage;
    }

    private VariableCorrespondence findMatchingCorrespondence(FormulaPosition position, FormulaAtom otherAtom) {
        for (FormulaPosition otherPosition : otherAtom.getPositions()) {
            if (otherPosition.isUniversal()) {
//                if (otherPosition.getCorrespondence().getTargetPath().equals(position.getCorrespondence().getTargetPath())) {
                if (otherPosition.getCorrespondence().getTargetPath().equalsUpToClones(position.getCorrespondence().getTargetPath())) {
                    return otherPosition.getCorrespondence();
                }
            }
        }
        return null;
    }

    //////////////////////////    GENERATE COVERAGE   //////////////////////////////////
    private List<Coverage> generateCoverages(FORule tgd, List<List<CoverageAtom>> listOfLists, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("----Generating coverage for: " + SpicyEngineUtility.printCollection(listOfLists));
        List<Coverage> result = new ArrayList<Coverage>();
        GenericListGenerator<CoverageAtom> listGenerator = new GenericListGenerator<CoverageAtom>();
        for (List<CoverageAtom> combination : listGenerator.generateListsOfElements(listOfLists)) {
            if (!inDifferentSTTGD(combination)) {
                continue;
            }
            if (!checkJoinsInCoverage(tgd, combination)) {
                continue;
            }
            Map<Integer, SetAlias> variableRenamings = generateRenamings(combination);
            List<VariableJoinCondition> renamedJoins = remameJoins(tgd.getTargetView().getJoinConditions(), variableRenamings);
            List<VariableJoinCondition> renamedCyclicJoins = remameJoins(tgd.getTargetView().getJoinConditions(), variableRenamings);
            Coverage coverage = new Coverage(combination, renamedJoins, renamedCyclicJoins);
            if (containsAtomsFromSameSTTGD(coverage.getCoveringAtoms())) {
                coverage = generateReducedCoverage(coverage);
            }
            if (coverage.getTargetJoinConditions().isEmpty()) {
                continue;
            }
            result.add(coverage);
        }
        return result;
    }

    private boolean inDifferentSTTGD(List<CoverageAtom> atomCoverages) {
        FORule firstTgd = atomCoverages.get(0).getFirstCoveringAtom().getTgd();
        for (CoverageAtom coverage : atomCoverages) {
            if (!firstTgd.equals(coverage.getFirstCoveringAtom().getTgd())) {
                return true;
            }
        }
        return false;
    }

    private Map<Integer, SetAlias> generateRenamings(List<CoverageAtom> combination) {
        Map<Integer, SetAlias> result = new HashMap<Integer, SetAlias>();
        for (int i = 0; i < combination.size(); i++) {
            CoverageAtom coveringAtom = combination.get(i);
            SetAlias originalVariable = coveringAtom.getFirstOriginalAtom().getVariable();
            SetAlias newVariable = coveringAtom.getFirstCoveringAtom().getVariable();
            result.put(originalVariable.getId(), newVariable);
        }
        return result;
    }

    private List<VariableJoinCondition> remameJoins(List<VariableJoinCondition> joinConditions, Map<Integer, SetAlias> variableRenamings) {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition joinCondition : joinConditions) {
            result.add(renamer.generateNewJoin(joinCondition, variableRenamings));
        }
        return result;
    }

    private boolean containsAtomsFromSameSTTGD(List<CoverageAtom> atomCoverages) {
        for (int i = 0; i < atomCoverages.size(); i++) {
            CoverageAtom coveragei = atomCoverages.get(i);
            for (int j = i + 1; j < atomCoverages.size(); j++) {
                CoverageAtom coveragej = atomCoverages.get(j);
                if (coveragei.getFirstCoveringAtom().getTgd().equals(coveragej.getFirstCoveringAtom().getTgd())) {
                    return true;
                }
            }
        }
        return false;
    }

    //////////////////////////    JOIN CHECK   //////////////////////////////////
    private boolean checkJoinsInCoverage(FORule tgd, List<CoverageAtom> combination) {
        for (VariableJoinCondition joinCondition : tgd.getTargetView().getAllJoinConditions()) {
            if (!checkJoinCondition(joinCondition, combination)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkJoinCondition(VariableJoinCondition joinCondition, List<CoverageAtom> combination) {
        if (isJoinAmongUniversalPositions(joinCondition, combination)) {
            return true;
        }
        if (isJoinAmongSkolemPositions(joinCondition, combination)) {
            FormulaAtom fromAtom = findAtom(joinCondition.getFromPaths(), combination);
            FormulaAtom toAtom = findAtom(joinCondition.getToPaths(), combination);
            if (fromAtom.getTgd().equals(toAtom.getTgd())) {
                VariableJoinCondition newJoin = SpicyEngineUtility.changeJoinCondition(joinCondition, fromAtom.getVariable(), toAtom.getVariable());
                if (existsContainingJoinGroup(newJoin, fromAtom.getTgd())) {
                    return true;
                }
            }
        }
        return false;
    }

    private FormulaAtom findAtom(List<VariablePathExpression> joinPaths, List<CoverageAtom> combination) {
        SetAlias joinVariable = joinPaths.get(0).getStartingVariable();
        for (CoverageAtom atomCoverage : combination) {
            if (atomCoverage.getFirstCoveringAtom().getVariable().equals(joinVariable)) {
                return atomCoverage.getFirstCoveringAtom();
            }
        }
        return null;
    }

    private boolean isJoinAmongUniversalPositions(VariableJoinCondition joinCondition, List<CoverageAtom> combination) {
        FormulaAtom fromAtom = findAtom(joinCondition.getFromPaths(), combination);
        FormulaAtom toAtom = findAtom(joinCondition.getToPaths(), combination);
        return areUniversal(joinCondition.getFromPaths(), fromAtom) && areUniversal(joinCondition.getToPaths(), toAtom);
    }

    private boolean areUniversal(List<VariablePathExpression> joinPaths, FormulaAtom atom) {
        VariablePathExpression firstPath = joinPaths.get(0);
        for (FormulaPosition position : atom.getPositions()) {
            if (position.getPathExpression().equals(firstPath)) {
                return position.isUniversal();
            }
        }
        throw new IllegalArgumentException("Path is not contained in atom :" + firstPath + " - " + atom);
    }

    private boolean isJoinAmongSkolemPositions(VariableJoinCondition joinCondition, List<CoverageAtom> combination) {
        FormulaAtom fromAtom = findAtom(joinCondition.getFromPaths(), combination);
        FormulaAtom toAtom = findAtom(joinCondition.getToPaths(), combination);
        return areSkolem(joinCondition.getFromPaths(), fromAtom) && areSkolem(joinCondition.getToPaths(), toAtom);
    }

    private boolean areSkolem(List<VariablePathExpression> joinPaths, FormulaAtom atom) {
        VariablePathExpression firstPath = joinPaths.get(0);
        for (FormulaPosition position : atom.getPositions()) {
            if (position.getPathExpression().equals(firstPath)) {
                return position.isSkolem();
            }
        }
        throw new IllegalArgumentException("Path is not contained in atom :" + firstPath + " - " + atom);
    }

    private boolean existsContainingJoinGroup(VariableJoinCondition joinCondition, FORule tgd) {
        List<JoinGroup> tgdJoinGroups = joinGroupFinder.findJoinGroups(tgd.getTargetView().getAllJoinConditions());
        JoinGroup singleton = new JoinGroup(joinCondition);
        for (JoinGroup tgdJoinGroup : tgdJoinGroups) {
            if (joinGroupMatcher.checkJoinGroupContainment(singleton, tgdJoinGroup)) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////    REDUCED COVERAGE   //////////////////////////////////
    private Coverage generateReducedCoverage(Coverage coverage) {
        // covering atoms belonging to the same tgd should not give rise to coverage joins
        if (logger.isDebugEnabled()) logger.debug("Generating reduced coverage for: " + coverage);
        List<CoverageAtom> reducedAtoms = new ArrayList<CoverageAtom>();
        Map<FORule, CoverageAtom> atomMap = new HashMap<FORule, CoverageAtom>();
        for (int i = 0; i < coverage.getCoveringAtoms().size(); i++) {
            CoverageAtom atomCoverage = coverage.getCoveringAtoms().get(i);
            if (logger.isDebugEnabled()) logger.debug("Examining atom coverage: " + atomCoverage);
            FORule tgd = atomCoverage.getFirstCoveringAtom().getTgd();
            if (atomMap.get(tgd) == null) {
                if (logger.isDebugEnabled()) logger.debug("This is an atom with a new tgd, adding...");
                CoverageAtom clone = atomCoverage.clone();
                reducedAtoms.add(clone);
                atomMap.put(tgd, clone);
            } else {
                CoverageAtom existingAtom = atomMap.get(tgd);
                if (logger.isDebugEnabled()) logger.debug("This is an atom with an existing tgd. Previous atom: " + existingAtom);
                existingAtom.addOriginalAtom(atomCoverage.getFirstOriginalAtom());
                existingAtom.addCoveringAtom(atomCoverage.getFirstCoveringAtom());
                existingAtom.getOriginalCorrespondences().addAll(atomCoverage.getOriginalCorrespondences());
                existingAtom.getMatchingCorrespondences().addAll(atomCoverage.getMatchingCorrespondences());
            }
        }
        List<VariableJoinCondition> reducedJoinConditions = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition joinCondition : coverage.getTargetJoinConditions()) {
            if (isJoinAmongUniversalPositions(joinCondition, coverage.getCoveringAtoms())) {
                reducedJoinConditions.add(joinCondition);
            }
        }
        List<VariableJoinCondition> reducedCyclicJoinConditions = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition cyclicJoinCondition : coverage.getTargetCyclicJoinConditions()) {
            if (isJoinAmongUniversalPositions(cyclicJoinCondition, coverage.getCoveringAtoms())) {
                reducedCyclicJoinConditions.add(cyclicJoinCondition);
            }
        }
        Coverage reducedCoverage = new Coverage(reducedAtoms, reducedJoinConditions, reducedCyclicJoinConditions);
        if (logger.isDebugEnabled()) logger.debug("Final result: " + reducedCoverage);
        return reducedCoverage;
    }
}
