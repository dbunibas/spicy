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

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.operators.FindJoinGroups;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckTGDHomomorphismWithSelfJoins {

    private static Log logger = LogFactory.getLog(CheckTGDHomomorphismWithSelfJoins.class);
    private FindJoinGroups joinGroupFinder = new FindJoinGroups();
    private MatchJoinGroups joinGroupMatcher = new MatchJoinGroups();

    ////////////////////////////   SELF-JOINS  ////////////////////////////////////
    Subsumption checkSubsumption(FORule tgd, FORule fatherTgd, List<FormulaAtom> tgdAtoms, List<FormulaAtom> fatherAtoms) {
        if (logger.isDebugEnabled()) logger.debug("Checking subsumption of tgd: \n" + tgd + "\nby tgd:\n" + fatherTgd);
        if (logger.isDebugEnabled()) logger.debug("With atoms: \n" + SpicyEngineUtility.printCollection(tgdAtoms) + "\nand :\n" + SpicyEngineUtility.printCollection(fatherAtoms));
        if (logger.isDebugEnabled()) logger.debug("---Analyzing permutation: \n" + SpicyEngineUtility.printCollection(fatherAtoms));
        if (logger.isDebugEnabled()) logger.debug("---Analyzing permutation: \n" + SpicyEngineUtility.printCollection(fatherAtoms));
        List<VariablePathExpression> tgdPaths = findUniversalPaths(tgdAtoms);
        List<VariablePathExpression> matchingPaths = checkCoverageAmongAtoms(tgdAtoms, fatherAtoms);
        if (matchingPaths == null) {
            if (logger.isDebugEnabled()) logger.debug("---Universal paths do not match, returning false...");
            return null;
        }
        if (!checkJoins(tgd, fatherTgd, tgdAtoms, fatherAtoms)) {
            if (logger.isDebugEnabled()) logger.debug("---Joins do not match, returning false...");
            return null;
        }
        List<VariableCorrespondence> tgdCorrespondences = findCorrespondencesForPaths(tgd.getCoveredCorrespondences(), tgdPaths);
        List<VariableCorrespondence> matchingTgdCorrespondences = findCorrespondencesForPaths(fatherTgd.getCoveredCorrespondences(), matchingPaths);
        Subsumption subsumption = new Subsumption(tgd, fatherTgd, tgdCorrespondences, matchingTgdCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("---Found subsumption: " + subsumption);
        return subsumption;
    }

    /////////////////   COVERAGE OF UNIVERSAL PATHS   //////////////////////////
    List<VariablePathExpression> checkCoverageAmongAtoms(List<FormulaAtom> FormulaAtoms, List<FormulaAtom> fatherAtoms) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (int i = 0; i < FormulaAtoms.size(); i++) {
            FormulaAtom FormulaAtom = FormulaAtoms.get(i);
            FormulaAtom fatherAtom = fatherAtoms.get(i);
            List<VariablePathExpression> matchingPaths = checkAtomCoverage(FormulaAtom, fatherAtom);
            if (matchingPaths == null) {
                return null;
            }
            result.addAll(matchingPaths);
        }
        return result;
    }

    private List<VariablePathExpression> checkAtomCoverage(FormulaAtom atom, FormulaAtom fatherAtom) {
        if (logger.isDebugEnabled()) logger.debug("----Checking atom: \n" + atom + "against \n" + fatherAtom);
//        if (!atom.getVariable().equalsOrIsClone(fatherAtom.getVariable())) {
        if (!matches(atom.getVariable(), fatherAtom.getVariable())) {
            if (logger.isDebugEnabled()) logger.debug("----Variables are different, no coverage");
            return null;
        }
        List<VariablePathExpression> atomUniversalPaths = findUniversalPathsInAtom(atom);
        List<VariablePathExpression> fatherAtomUniversalPaths = findUniversalPathsInAtom(fatherAtom);
        List<VariablePathExpression> matchingPaths = findMatchingPaths(atomUniversalPaths, fatherAtomUniversalPaths);
        if (logger.isDebugEnabled()) logger.debug("----Found matching paths: " + matchingPaths);
        return matchingPaths;
    }

    private boolean matches(SetAlias variable, SetAlias fatherVariable) {
        for (SetAlias generator : fatherVariable.getGenerators()) {
            if (generator.equalsOrIsClone(variable)) {
                return true;
            }
        }
        return false;
    }

    private List<VariablePathExpression> findMatchingPaths(List<VariablePathExpression> atomUniversalPaths, List<VariablePathExpression> fatherAtomUniversalPaths) {
        List<VariablePathExpression> matchingPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression atomPath : atomUniversalPaths) {
            VariablePathExpression matchingPath = findMatchingPath(atomPath, fatherAtomUniversalPaths);
            if (matchingPath == null) {
                return null;
            }
            matchingPaths.add(matchingPath);
        }
        return matchingPaths;
    }

    private VariablePathExpression findMatchingPath(VariablePathExpression atomPath, List<VariablePathExpression> fatherUniversalPaths) {
        for (VariablePathExpression fatherPath : fatherUniversalPaths) {
            if (fatherPath.equalsUpToClones(atomPath)) {
                return fatherPath;
            }
        }
        return null;
    }

    public List<VariablePathExpression> findUniversalPaths(List<FormulaAtom> atoms) {
        List<VariablePathExpression> universalPaths = new ArrayList<VariablePathExpression>();
        for (FormulaAtom coveringAtom : atoms) {
            universalPaths.addAll(findUniversalPathsInAtom(coveringAtom));
        }
        return universalPaths;
    }

    private List<VariablePathExpression> findUniversalPathsInAtom(FormulaAtom atom) {
        List<VariablePathExpression> universalPaths = new ArrayList<VariablePathExpression>();
        for (FormulaPosition position : atom.getPositions()) {
            if (position.isUniversal()) {
                universalPaths.add(position.getPathExpression());
            }
        }
        return universalPaths;
    }

    //////////////////////////////   JOIN CHECK
    private boolean checkJoins(FORule rule, FORule fatherRule, List<FormulaAtom> ruleAtoms, List<FormulaAtom> fatherAtoms) {
        List<JoinGroup> fatherJoinGroups = joinGroupFinder.findJoinGroups(fatherRule.getTargetView().getAllJoinConditions());
        for (VariableJoinCondition joinCondition : rule.getTargetView().getAllJoinConditions()) {
            FormulaAtom fatherFromAtom = findAtom(joinCondition.getFromVariable(), ruleAtoms, fatherAtoms);
            FormulaAtom fatherToAtom = findAtom(joinCondition.getFromVariable(), ruleAtoms, fatherAtoms);
            VariableJoinCondition newJoin = SpicyEngineUtility.changeJoinCondition(joinCondition, fatherFromAtom.getVariable(), fatherToAtom.getVariable());
            if (!findContainingJoinGroup(newJoin, fatherJoinGroups)) {
                return false;
            }
        }
        return true;
    }

    private FormulaAtom findAtom(SetAlias variable, List<FormulaAtom> ruleAtoms, List<FormulaAtom> fatherAtoms) {
        for (int i = 0; i < ruleAtoms.size(); i++) {
            FormulaAtom atom = ruleAtoms.get(i);
            if (atom.getVariable().equalsAndHasSameId(variable)) {
                return fatherAtoms.get(i);
            }
        }
        throw new IllegalArgumentException("Unable to find atom for variable " + variable + " in " + ruleAtoms);
    }

    private boolean findContainingJoinGroup(VariableJoinCondition joinCondition, List<JoinGroup> fatherJoinGroups) {
        for (JoinGroup joinGroup : fatherJoinGroups) {
            if (joinGroupMatcher.checkJoinConditionContainment(joinCondition, joinGroup)) {
                return true;
            }
        }
        return false;
    }

    private List<VariableCorrespondence> findCorrespondencesForPaths(List<VariableCorrespondence> correspondences,
            List<VariablePathExpression> universalPaths) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (VariablePathExpression universalPath : universalPaths) {
            result.add(findCorrespondenceForPaths(correspondences, universalPath));
        }
        return result;
    }

    private VariableCorrespondence findCorrespondenceForPaths(List<VariableCorrespondence> correspondences,
            VariablePathExpression pathExpression) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(pathExpression)) {
                return correspondence;
            }
        }
        throw new IllegalArgumentException("Unable to find correspondence for path " + pathExpression + " in\n" + correspondences);
    }

    //////////////////////////////   PROPER HOMOMORPHISM   /////////////////////////////7
    boolean isProperHomomorphism(FORule tgd, FORule fatherTgd, List<FormulaAtom> tgdAtoms, List<FormulaAtom> fatherTgdAtoms) {
        if (logger.isDebugEnabled()) logger.debug("Checking proper homomorphism between:\n" + tgd + "and:\n" + fatherTgd);
        if (logger.isDebugEnabled()) logger.debug("Tgd atoms:\n" + tgdAtoms + "Father tgd atoms:\n" + fatherTgdAtoms);
        if (fatherTgd.getTargetView().getGenerators().size() > tgd.getTargetView().getGenerators().size()) {
            if (logger.isDebugEnabled()) logger.debug("Homomorphism is proper, generators in father are more than in child");
            return true;
        }
        for (int i = 0; i < tgdAtoms.size(); i++) {
            FormulaAtom tgdAtom = tgdAtoms.get(i);
            FormulaAtom fatherAtom = fatherTgdAtoms.get(i);
            for (FormulaPosition position : tgdAtom.getPositions()) {
                FormulaPosition fatherPosition = findPositionInClone(position, fatherAtom);
                if (!position.isUniversal() && fatherPosition.isUniversal()) {
                    return true;
                }
                if (position.isNull() && fatherPosition.isSkolem()) {
                    if (logger.isTraceEnabled()) logger.debug("---- Found less informative position: " + position);
                    if (logger.isTraceEnabled()) logger.debug("---- Position in father: " + fatherPosition);
                    return true;
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Returning false...");
        return false;
    }

    private FormulaPosition findPositionInClone(FormulaPosition position, FormulaAtom coveringAtom) {
        for (FormulaPosition coveringPosition : coveringAtom.getPositions()) {
            if (coveringPosition.getPathExpression().equalsUpToClones(position.getPathExpression())) {
                return coveringPosition;
            }
        }
        throw new IllegalArgumentException("Unable to find position: " + position + " in " + coveringAtom);
    }
}
