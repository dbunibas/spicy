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
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.operators.FindJoinGroups;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckTGDHomomorphismWithoutSelfJoins {

    private static Log logger = LogFactory.getLog(CheckTGDHomomorphismWithoutSelfJoins.class);
    private FindJoinGroups joinGroupFinder = new FindJoinGroups();
    private MatchJoinGroups joinGroupMatcher = new MatchJoinGroups();

    ////////////////////////////   NO SELF-JOINS  ////////////////////////////////////
    Subsumption checkHomomorphismWithoutSelfJoins(FORule fatherTgd, FORule tgd, IDataSourceProxy target, MappingTask mappingTask) {
        if (tgd.equals(fatherTgd)) {
            throw new IllegalArgumentException("Cannot check subsumption of tgd by itself: " + tgd);
        }
//        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
        if (SpicyEngineUtility.hasSelfJoins(tgd.getTargetView()) || SpicyEngineUtility.hasSelfJoins(fatherTgd.getTargetView())) {
            throw new IllegalArgumentException("Cannot check subsumption with self joins: " + tgd + "\n" + fatherTgd);
        }
        // checks subsumption in absence of self-joins: it suffices to check correspondences (universal paths)
        // and join conditions (skolem paths), both up to clones
        if (logger.isDebugEnabled()) logger.debug("***********************************Checking subsumption of TGD: \n" + fatherTgd + "**************wrt TGD: \n" + tgd);
        List<VariableCorrespondence> matchingCorrespondences = checkContainmentOfCorrespondences(tgd.getCoveredCorrespondences(), fatherTgd.getCoveredCorrespondences());
        if (matchingCorrespondences == null) {
            if (logger.isDebugEnabled()) logger.debug("Correspondences do not match, returning null");
            return null;
        }
        if (!checkContainmentOfJoins(tgd.getTargetView().getAllJoinConditions(), fatherTgd, mappingTask)) {
            if (logger.isDebugEnabled()) logger.debug("Joins do not match, returning null");
            return null;
        }
        if (logger.isDebugEnabled()) logger.debug("Matching correspondences: " + SpicyEngineUtility.printCollection(matchingCorrespondences));
        if (logger.isDebugEnabled()) logger.debug("Target join match");
        Subsumption result = new Subsumption(tgd, fatherTgd, tgd.getCoveredCorrespondences(), matchingCorrespondences);
        if (logger.isDebugEnabled()) logger.debug("***********************************Result: " + result);
        return result;
    }

    private List<VariableCorrespondence> checkContainmentOfCorrespondences(List<VariableCorrespondence> correspondences,
            List<VariableCorrespondence> fatherCorrespondences) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (VariableCorrespondence correspondence : correspondences) {
            if (logger.isDebugEnabled()) logger.debug("Looking for correspondence: " + correspondence);
            VariableCorrespondence matchingCorrespondence = containsEqualCorrespondence(correspondence, fatherCorrespondences);
            if (logger.isDebugEnabled()) logger.debug("Matching correspondence: " + matchingCorrespondence);
            if (matchingCorrespondence == null) {
                return null;
            }
            result.add(matchingCorrespondence);
        }
        return result;
    }

    private VariableCorrespondence containsEqualCorrespondence(VariableCorrespondence correspondence, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence otherCorrespondence : correspondences) {
//            if (correspondence.getTargetPath().equals(otherCorrespondence.getTargetPath())) {
            if (correspondence.getTargetPath().equalsUpToClones(otherCorrespondence.getTargetPath())) {
                if (correspondence.getSourceValue() == null && otherCorrespondence.getSourceValue() == null) {
                    return otherCorrespondence;
                }
                if (correspondence.getSourceValue() != null && otherCorrespondence.getSourceValue() != null) {
                    if (correspondence.getSourceValue().equals(otherCorrespondence.getSourceValue())) {
                        return otherCorrespondence;
                    }
                }
            }
        }
        return null;
    }

    ///////////////////////   JOIN CONTAINMENT
    private boolean checkContainmentOfJoins(List<VariableJoinCondition> joins, FORule fatherTgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Checking containment of joins:\n" + SpicyEngineUtility.printCollection(joins) + "\nin rule:\n" + fatherTgd);
        List<VariableJoinCondition> fatherJoins = fatherTgd.getTargetView().getAllJoinConditions();
        List<JoinGroup> fatherJoinGroups = joinGroupFinder.findJoinGroups(fatherJoins);
        for (VariableJoinCondition joinCondition : joins) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing join:\n" + joinCondition);
            SetAlias fatherFromAlias = findFatherAlias(joinCondition.getFromVariable(), fatherTgd, mappingTask);
            SetAlias fatherToAlias = findFatherAlias(joinCondition.getToVariable(), fatherTgd, mappingTask);
            if (fatherFromAlias == null || fatherToAlias == null) {
                if (logger.isDebugEnabled()) logger.debug("Unable to find matching atoms in father, returning null");
                return false;
            }
            VariableJoinCondition newJoinCondition = SpicyEngineUtility.changeJoinCondition(joinCondition, fatherFromAlias, fatherToAlias);
            if (logger.isDebugEnabled()) logger.debug("Join condition in father:\n" + newJoinCondition);
            JoinGroup singleton = new JoinGroup(newJoinCondition);
            if (!findContainingJoinGroupInFather(singleton, fatherJoinGroups)) {
                if (logger.isDebugEnabled()) logger.debug("Unable to find matching join groups in father, returning null");
                return false;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Returning true...");
        return true;
    }

    private SetAlias findFatherAlias(SetAlias variable, FORule fatherTGD, MappingTask mappingTask) {
        for (FormulaAtom fatherAtom : fatherTGD.getTargetFormulaAtoms(mappingTask)) {
            SetAlias generatorInFather = findMatchingAliasInAtom(variable, fatherAtom);
            if (generatorInFather != null) {
                return generatorInFather;
            }
        }
        return null;
    }

    private boolean findContainingJoinGroupInFather(JoinGroup joinGroup, List<JoinGroup> fatherJoinGroups) {
        for (JoinGroup fatherJoinGroup : fatherJoinGroups) {
            if (joinGroupMatcher.checkJoinGroupContainment(joinGroup, fatherJoinGroup)) {
                return true;
            }
        }
        return false;
    }

    ///////////////////////   PROPER HOMOMORPHISM
    boolean isProperHomomorphism(FORule tgd, FORule fatherTGD, MappingTask mappingTask) {
//        return true;
        if (tgd.getTargetView().getVariables().size() < fatherTGD.getTargetView().getVariables().size()) {
            return true;
        }
        for (FormulaAtom tgdAtom : tgd.getTargetFormulaAtoms(mappingTask)) {
            FormulaAtom fatherAtom = findFatherAtom(tgdAtom.getVariable(), fatherTGD, mappingTask);
            for (FormulaPosition position : tgdAtom.getPositions()) {
                FormulaPosition fatherPosition = findPosition(position, fatherAtom);
                if ((position.isSkolemOrNull() && fatherPosition.isUniversal())
                        || (position.isNull() && fatherPosition.isSkolem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private FormulaAtom findFatherAtom(SetAlias variable, FORule fatherTGD, MappingTask mappingTask) {
        for (FormulaAtom fatherAtom : fatherTGD.getTargetFormulaAtoms(mappingTask)) {
            if (findMatchingAliasInAtom(variable, fatherAtom) != null) {
                return fatherAtom;
            }
        }
        return null;
    }

    private SetAlias findMatchingAliasInAtom(SetAlias variable, FormulaAtom fatherAtom) {
        for (SetAlias generator : fatherAtom.getVariable().getGenerators()) {
            if (generator.equalsOrIsClone(variable)) {
                return generator;
            }
        }
        return null;
    }

    private FormulaPosition findPosition(FormulaPosition position, FormulaAtom fatherAtom) {
        for (FormulaPosition coveringPosition : fatherAtom.getPositions()) {
            if (coveringPosition.getPathExpression().equalsUpToClones(position.getPathExpression())) {
                return coveringPosition;
            }
        }
        throw new IllegalArgumentException("Unable to find position: " + position + " in " + fatherAtom);
    }
}
