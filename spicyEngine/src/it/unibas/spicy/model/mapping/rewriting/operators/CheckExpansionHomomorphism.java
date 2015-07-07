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

import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.operators.FindJoinGroups;
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.ExpansionOrderedPair;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckExpansionHomomorphism {

    private static Log logger = LogFactory.getLog(CheckExpansionHomomorphism.class);

    public ExpansionOrderedPair checkHomomorphism(Expansion expansion, List<ExpansionElement> expansionAtoms, Expansion fatherExpansion, List<ExpansionElement> fatherAtoms) {
        if (logger.isDebugEnabled()) logger.debug("---Analyzing permutation: \n" + fatherAtoms);
        List<VariablePathExpression> matchingPaths = checkCoverageAmongAtoms(expansionAtoms, fatherAtoms);
        if (matchingPaths == null) {
            return null;
        }
        if (!checkJoins(expansion, fatherExpansion, expansionAtoms, fatherAtoms)) {
            return null;
        }
        List<VariablePathExpression> expansionPaths = findUniversalPaths(expansionAtoms);
        ExpansionOrderedPair subsumption = new ExpansionOrderedPair(expansion, fatherExpansion, expansionPaths, matchingPaths, SpicyEngineConstants.MORE_INFORMATIVE);
        return subsumption;
    }

    List<VariablePathExpression> findUniversalPaths(List<ExpansionElement> atoms) {
        List<VariablePathExpression> universalPaths = new ArrayList<VariablePathExpression>();
        for (ExpansionElement coveringAtom : atoms) {
            for (ExpansionFormulaPosition position : coveringAtom.getCoveringAtom().getPositions()) {
                if (position.isUniversal()) {
                    universalPaths.add(position.getPathExpression());
                }
            }
        }
        return universalPaths;
    }

    List<VariablePathExpression> checkCoverageAmongAtoms(List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (int i = 0; i < expansionAtoms.size(); i++) {
            ExpansionElement expansionAtom = expansionAtoms.get(i);
            ExpansionElement fatherAtom = fatherAtoms.get(i);
            List<VariablePathExpression> matchingPaths = checkAtomCoverage(expansionAtom, fatherAtom);
            if (matchingPaths == null) {
                return null;
            }
            result.addAll(matchingPaths);
        }
        return result;
    }

    private List<VariablePathExpression> checkAtomCoverage(ExpansionElement atom, ExpansionElement fatherAtom) {
        if (logger.isDebugEnabled()) logger.debug("----Checking atom: \n" + atom.getCoveringAtom() + "against \n" + fatherAtom.getCoveringAtom());
        if (!atom.getCoveringAtom().getVariable().equalsOrIsClone(fatherAtom.getCoveringAtom().getVariable())) {
            if (logger.isDebugEnabled()) logger.debug("----Variables are different, no coverage");
            return null;
        }
        List<VariablePathExpression> atomUniversalPaths = findUniversalPathsInAtom(atom);
        List<VariablePathExpression> fatherAtomUniversalPaths = findUniversalPathsInAtom(fatherAtom);
        List<VariablePathExpression> matchingPaths = findMatchingPaths(atomUniversalPaths, fatherAtomUniversalPaths);
        if (logger.isDebugEnabled()) logger.debug("----Found matching paths: " + matchingPaths);
        return matchingPaths;
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

//    public List<VariablePathExpression> findUniversalPaths(List<ExpansionElement> atoms) {
//        List<VariablePathExpression> universalPaths = new ArrayList<VariablePathExpression>();
//        for (ExpansionElement coveringAtom : atoms) {
//            universalPaths.addAll(findUniversalPathsInAtom(coveringAtom));
//        }
//        return universalPaths;
//    }
//
    private List<VariablePathExpression> findUniversalPathsInAtom(ExpansionElement atom) {
        List<VariablePathExpression> universalPaths = new ArrayList<VariablePathExpression>();
        for (ExpansionFormulaPosition position : atom.getCoveringAtom().getPositions()) {
            if (position.isUniversal()) {
                universalPaths.add(position.getPathExpression());
            }
        }
        return universalPaths;
    }

    //////////////////////////////   JOIN CHECK
    private boolean checkJoins(Expansion expansion, Expansion fatherExpansion,
            List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        FindJoinGroups joinGroupFinder = new FindJoinGroups();
        List<JoinGroup> expansionJoinGroups = joinGroupFinder.findJoinGroups(expansion.getAllJoinConditions());
        List<JoinGroup> fatherJoinGroups = joinGroupFinder.findJoinGroups(fatherExpansion.getAllJoinConditions());
        for (JoinGroup expansionJoinGroup : expansionJoinGroups) {
            if (!findEqualJoinGroupAndRemove(expansionJoinGroup, fatherJoinGroups, expansionAtoms, fatherAtoms)) {
                return false;
            }
        }
        return true;
    }

    private boolean findEqualJoinGroupAndRemove(JoinGroup joinGroup, List<JoinGroup> fatherJoinGroups, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        MatchJoinGroupsForExpansions joinGroupMatcher = new MatchJoinGroupsForExpansions();
        for (Iterator<JoinGroup> it = fatherJoinGroups.iterator(); it.hasNext();) {
            JoinGroup fatherJoinGroup = it.next();
            if (joinGroupMatcher.checkJoinGroups(joinGroup, fatherJoinGroup, expansionAtoms, fatherAtoms)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    ///////////////   MORE COMPACT PROPER HOMOMORPHISM
    public boolean checkIfMoreCompact(Expansion expansion, Expansion fatherExpansion, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        if (expansion.getExpansionElements().size() > fatherExpansion.getExpansionElements().size()) {
            return true;
        }
        if (expansion.getExpansionElements().size() < fatherExpansion.getExpansionElements().size()) {
            return false;
        }
        if (logger.isTraceEnabled()) logger.debug("----------- Checking proper homomorphism");
        if (logger.isTraceEnabled()) logger.debug("----------- Expansion: " + expansion);
        if (logger.isTraceEnabled()) logger.debug("----------- Father Expansion: " + fatherExpansion);
        if (logger.isTraceEnabled()) logger.debug("----------- Father atoms: " + SpicyEngineUtility.printCollection(fatherAtoms));
        // assumes that checks on the number of atoms has been made outside
//        List<ExpansionElement> expansionAtoms = expansion.getExpansionElements();
        for (int i = 0; i < expansionAtoms.size(); i++) {
            ExpansionAtom expansionAtom = expansionAtoms.get(i).getCoveringAtom();
            ExpansionAtom fatherAtom = fatherAtoms.get(i).getCoveringAtom();
            for (ExpansionFormulaPosition position : expansionAtom.getPositions()) {
                ExpansionFormulaPosition fatherPosition = findPositionInClone(position, fatherAtom);
//                if ((!position.isUniversal() && fatherPosition.isUniversal()) ||
//                        (position.isNull() && fatherPosition.isSkolem())) {
                if ((!position.isUniversal() && fatherPosition.isUniversal())) {
                    if (logger.isTraceEnabled()) logger.debug("---- Found less informative position: " + position);
                    if (logger.isTraceEnabled()) logger.debug("---- Position in father: " + fatherPosition);
                    return true;
                }
            }
        }
        if (logger.isTraceEnabled()) logger.debug("----------- Homomorphism is not proper\n");
        return false;
    }

    private ExpansionFormulaPosition findPositionInClone(ExpansionFormulaPosition position, ExpansionAtom coveringAtom) {
        for (ExpansionFormulaPosition coveringPosition : coveringAtom.getPositions()) {
            if (coveringPosition.getPathExpression().equalsUpToClones(position.getPathExpression())) {
                return coveringPosition;
            }
        }
        throw new IllegalArgumentException("Unable to find position: " + position + " in " + coveringAtom);
    }

    ///////////////   MORE INFORMATIVE PROPER HOMOMORPHISM
    public boolean checkIfMoreInformative(Expansion expansion, Expansion fatherExpansion, List<ExpansionElement> expansionAtoms, List<ExpansionElement> fatherAtoms) {
        if (expansion.getExpansionElements().size() < fatherExpansion.getExpansionElements().size()) {
            return true;
        }
//        if (expansion.getExpansionElements().size() = fatherExpansion.getExpansionElements().size()) {
//            return false;
//        }
//        if (logger.isTraceEnabled()) logger.debug("----------- Checking proper homomorphism");
//        if (logger.isTraceEnabled()) logger.debug("----------- Expansion: " + expansion);
//        if (logger.isTraceEnabled()) logger.debug("----------- Father atoms: " + SpicyEngineUtility.printCollection(fatherAtoms));
//        if (logger.isTraceEnabled()) logger.debug("----------- Father joins: " + SpicyEngineUtility.printCollection(fatherExpansion.getAllJoinConditions()));
//        // assumes that checks on the number of atoms has been made outside
////        List<ExpansionElement> expansionAtoms = expansion.getExpansionElements();
//        for (int i = 0; i < expansionAtoms.size(); i++) {
//            ExpansionAtom expansionAtom = expansionAtoms.get(i).getAtom();
//            ExpansionAtom fatherAtom = fatherAtoms.get(i).getAtom();
//            for (ExpansionFormulaPosition position : expansionAtom.getPositions()) {
//                ExpansionFormulaPosition fatherPosition = findPositionInClone(position, fatherAtom);
//                if (!position.isUniversal() && fatherPosition.isUniversal()) {
//                    return true;
//                }
//                if (isNull(position, expansion) && isSkolem(fatherPosition, fatherExpansion)) {
//                    if (logger.isTraceEnabled()) logger.debug("---- Found less informative position: " + position);
//                    if (logger.isTraceEnabled()) logger.debug("---- Position in father: " + fatherPosition);
//                    return true;
//                }
//            }
//        }
//        if (logger.isTraceEnabled()) logger.debug("----------- Homomorphism is not proper\n");
        return false;
    }

    private boolean isNull(ExpansionFormulaPosition position, Expansion expansion) {
        return (!position.isUniversal() && !isSkolem(position, expansion));
    }

    private boolean isSkolem(ExpansionFormulaPosition position, Expansion expansion) {
        for (VariableJoinCondition joinCondition : expansion.getAllJoinConditions()) {
            if (joinCondition.getFromPaths().contains(position.getPathExpression()) ||
                    joinCondition.getToPaths().contains(position.getPathExpression())) {
                return true;
            }
        }
        return false;
    }
}
