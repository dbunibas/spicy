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
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.TGDRenaming;
import it.unibas.spicy.model.mapping.TargetEqualities;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.ExpansionJoin;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateExpansionRules {

    private static Log logger = LogFactory.getLog(GenerateExpansionRules.class);
    private RenameSetAliases renamer = new RenameSetAliases();

    public Map<Expansion, FORule> generateExpansionRules(List<Expansion> expansions, MappingTask mappingTask) {
        Map<Expansion, FORule> expansionRules = new HashMap<Expansion, FORule>();
        for (Expansion expansion : expansions) {
            if (logger.isDebugEnabled()) logger.debug("---------------------------  Generating rule for expansion: -\n" + expansion);
            FORule expansionRule = null;
            if (expansion.isBase()) {
                expansionRule = generateExpansionRuleForBaseExpansion(expansion);
            } else {
                Map<ExpansionPremise, TGDRenaming> renamedTgds = renameTgds(expansion);
                ComplexQueryWithNegations sourceRewriting = generateSourceRewriting(expansion, renamedTgds);
                SimpleConjunctiveQuery targetView = generateTargetView(expansion);
                List<VariableCorrespondence> correspondences = generateCorrespondences(expansion, renamedTgds, targetView);
                expansionRule = new FORule(sourceRewriting, targetView, correspondences);
            }
            if (logger.isDebugEnabled()) logger.debug("---------------------------  Generated expansion rule: -\n" + expansionRule);
            expansionRules.put(expansion, expansionRule);
            expansion.setSourceRewriting(expansionRule.getComplexSourceQuery());
            expansionRule.getComplexSourceQuery().setProvenance(SpicyEngineConstants.SOURCE_REWRITING + " - " + expansion.getId());
        }
        return expansionRules;
    }

    private FORule generateExpansionRuleForBaseExpansion(Expansion expansion) {
        FORule originalTgd = expansion.getBaseView().getStTgd();
        FORule newTgd = originalTgd.clone();
        return newTgd;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////
    ////////                         STEP 1:  SOURCE REWRITING
    ////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private ComplexQueryWithNegations generateSourceRewriting(Expansion expansion, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        if (logger.isDebugEnabled()) logger.debug("Generating source rewriting for expansion:\n" + expansion);
        if (logger.isDebugEnabled()) logger.debug("Renamed tgds:\n" + SpicyEngineUtility.printMap(renamedTgds));
        ComplexConjunctiveQuery sourceRewriting = new ComplexConjunctiveQuery();
        ExpansionElement firstElement = expansion.getExpansionElements().get(0);
        ExpansionPremise firstPremise = findPremiseForElement(firstElement, renamedTgds);
        FORule firstRenamedTgd = renamedTgds.get(firstPremise).getRenamedTgd();
        sourceRewriting.addConjunction(firstRenamedTgd.getSimpleSourceView());
        sourceRewriting.addCorrespondencesForConjuntion(firstRenamedTgd.getCoveredCorrespondences());
        for (int i = 0; i < expansion.getJoinConditions().size(); i++) {
            ExpansionJoin expansionJoin = expansion.getJoinConditions().get(i);
            if (logger.isDebugEnabled()) logger.debug("Analyzing join: " + expansionJoin);
            if (!expansionJoin.isJoinOnSkolems()) {
                ExpansionElement expansionElement = expansion.getExpansionElements().get(i + 1);
                ExpansionPremise expansionPremise = findPremiseForElement(expansionElement, renamedTgds);
                FORule renamedTgd = renamedTgds.get(expansionPremise).getRenamedTgd();
                sourceRewriting.addConjunction(renamedTgd.getSimpleSourceView());
                sourceRewriting.addCorrespondencesForConjuntion(renamedTgd.getCoveredCorrespondences());
                VariableJoinCondition newJoin = renameJoin(expansionJoin, renamedTgds);
                if (logger.isDebugEnabled()) logger.debug("Adding renamed join: " + newJoin);
                if (!sourceRewriting.getJoinConditions().contains(newJoin)) {
                    sourceRewriting.addJoinCondition(newJoin);
                }
            }
        }
        for (ExpansionJoin cyclicJoin : expansion.getCyclicJoinConditions()) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing cyclic join: " + cyclicJoin);
            if (!cyclicJoin.isJoinOnSkolems()) {
                VariableJoinCondition renamedJoin = renameJoin(cyclicJoin, renamedTgds);
                if (logger.isDebugEnabled()) logger.debug("Adding cyclic renamed join: " + renamedJoin);
                sourceRewriting.addCyclicJoinCondition(renamedJoin);
            }
        }
        // intersection
        sourceRewriting.setConjunctionForIntersection(expansion.getBaseView().getStTgd().getSimpleSourceView());
        TargetEqualities intersectionEqualities = generateIntersectionEqualities(expansion, renamedTgds);
        sourceRewriting.setIntersectionEqualities(intersectionEqualities);
        return new ComplexQueryWithNegations(sourceRewriting);
    }

    private ExpansionPremise findPremiseForElement(ExpansionElement element, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        for (ExpansionPremise expansionPremise : renamedTgds.keySet()) {
            if (expansionPremise.getExpansionElements().contains(element)) {
                return expansionPremise;
            }
        }
        throw new IllegalArgumentException("Unable to find expansion premise for element " + element + " in " + renamedTgds);
    }

    ///////////////////////   STEP 1.a: group expansion elements and rename needed tgds
    private Map<ExpansionPremise, TGDRenaming> renameTgds(Expansion expansion) {
        // tgds are needed only for once for groups of atoms that join/selfjoin on skolems
        Map<ExpansionPremise, TGDRenaming> result = new HashMap<ExpansionPremise, TGDRenaming>();
        ExpansionElement firstElement = expansion.getExpansionElements().get(0);
        TGDRenaming firstRenamedTgd = renamer.renameAliasesInTGD(firstElement.getCoveringAtom().getTgd());
        result.put(new ExpansionPremise(firstElement), firstRenamedTgd);
        for (int i = 0; i < expansion.getJoinConditions().size(); i++) {
            ExpansionJoin expansionJoin = expansion.getJoinConditions().get(i);
            ExpansionElement expansionElement = expansion.getExpansionElements().get(i + 1);
            if (expansionJoin.isJoinOnSkolems()) {
                ExpansionPremise premiseForJoin = findPremiseForJoin(result, expansionJoin);
                premiseForJoin.addExpansionElement(expansionElement);
            } else {
                TGDRenaming renamedTgd = renamer.renameAliasesInTGD(expansionElement.getCoveringAtom().getTgd());
                result.put(new ExpansionPremise(expansionElement), renamedTgd);
            }
        }
        return result;
    }

    private ExpansionPremise findPremiseForJoin(Map<ExpansionPremise, TGDRenaming> renamedTgds, ExpansionJoin expansionJoin) {
        for (ExpansionPremise expansionPremise : renamedTgds.keySet()) {
            if (expansionPremise.getExpansionElements().contains(expansionJoin.getFromElement())
                    || expansionPremise.getExpansionElements().contains(expansionJoin.getToElement())) {
                return expansionPremise;
            }
        }
        throw new IllegalArgumentException("Unable to find expansion premise for join " + expansionJoin + " in " + renamedTgds);
    }

    ///////////////////////   STEP 1.b: rename joins according to new tgds
    private VariableJoinCondition renameJoin(ExpansionJoin joinCondition, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        if (logger.isDebugEnabled()) logger.debug("Renaming join: " + joinCondition);
        ExpansionPremise fromPremise = findPremiseForElement(joinCondition.getFromElement(), renamedTgds);
        ExpansionPremise toPremise = findPremiseForElement(joinCondition.getToElement(), renamedTgds);
        SetAlias newFromVariable = findNewVariable(joinCondition.getJoinCondition().getFromVariable(), fromPremise, renamedTgds);
        SetAlias newToVariable = findNewVariable(joinCondition.getJoinCondition().getToVariable(), toPremise, renamedTgds);
        VariableJoinCondition newJoin = generateNewJoin(joinCondition.getJoinCondition(), newFromVariable, newToVariable);
        if (logger.isDebugEnabled()) logger.debug("Renamed join: " + newJoin);        
        return newJoin;
    }

    private SetAlias findNewVariable(SetAlias variable, ExpansionPremise expansionPremise, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        FORule originalTgd = expansionPremise.getTGD();
        SetAlias originaTargetVariable = findOriginalVariable(variable, originalTgd);
        TGDRenaming tgdRenaming = renamedTgds.get(expansionPremise);
        SetAlias newVariable = tgdRenaming.getRenamedTargetView().getRenamings().get(originaTargetVariable.getId());
        SetAlias result = findGeneratorInVariable(variable, newVariable);
        if (result == null) {
            throw new IllegalArgumentException("Unable to find variable " + originaTargetVariable + " in renamed variables " + tgdRenaming.getRenamedTargetView().getRenamings());
        }
        return result;
    }

    private SetAlias findOriginalVariable(SetAlias variable, FORule originalTgd) {
        for (SetAlias targetVariable : originalTgd.getTargetView().getVariables()) {
            for (SetAlias generator : targetVariable.getGenerators()) {
                if (generator.equalsUpToProvenance(variable)) {
                    return targetVariable;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find variable " + variable + " in tgd " + originalTgd);
    }

    private SetAlias findGeneratorInVariable(SetAlias variable, SetAlias newVariable) {
        for (SetAlias generator : newVariable.getGenerators()) {
            if (generator.equalsOrIsClone(variable)) {
                return generator;
            }
        }
        return null;
    }

    public VariableJoinCondition generateNewJoin(VariableJoinCondition joinCondition, SetAlias newFromVariable, SetAlias newToVariable) {
        List<VariablePathExpression> newFromPaths = correctPaths(joinCondition.getFromPaths(), newFromVariable);
        List<VariablePathExpression> newToPaths = correctPaths(joinCondition.getToPaths(), newToVariable);
        VariableJoinCondition newJoinCondition = new VariableJoinCondition(newFromPaths, newToPaths,
                joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return newJoinCondition;
    }

    public List<VariablePathExpression> correctPaths(List<VariablePathExpression> oldPaths, SetAlias newVariable) {
        List<VariablePathExpression> newPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression oldPath : oldPaths) {
            VariablePathExpression newPath = new VariablePathExpression(newVariable, oldPath.getPathSteps());
            newPaths.add(newPath);
        }
        return newPaths;
    }

    ///////////////////////   STEP 1.c: generate intersection equalities
    private TargetEqualities generateIntersectionEqualities(Expansion expansion, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        List<VariableCorrespondence> leftCorrespondences = new ArrayList<VariableCorrespondence>();
        List<VariableCorrespondence> rightCorrespondences = new ArrayList<VariableCorrespondence>();
        List<VariableCorrespondence> expansionCorrespondences = generateAllCorrespondences(expansion, renamedTgds);
        List<VariableCorrespondence> baseViewCorrespondences = expansion.getBaseView().getStTgd().getCoveredCorrespondences();
        List<VariablePathExpression> leftPaths = renamePaths(expansion, expansion.getMatchingUniversalPaths(), renamedTgds);
        List<VariablePathExpression> rightPaths = expansion.getOriginalUniversalPaths();
        for (int i = 0; i < leftPaths.size(); i++) {
            VariablePathExpression leftPath = leftPaths.get(i);
            VariablePathExpression rightPath = rightPaths.get(i);
            VariableCorrespondence leftCorrespondence = findCorrespondenceForPathWithEqualId(leftPath, expansionCorrespondences);
            VariableCorrespondence rightCorrespondence = findCorrespondenceForPathWithEqualId(rightPath, baseViewCorrespondences);
            leftCorrespondences.add(leftCorrespondence);
            rightCorrespondences.add(rightCorrespondence);
        }
        return new TargetEqualities(leftCorrespondences, rightCorrespondences);
    }

    private List<VariableCorrespondence> generateAllCorrespondences(Expansion expansion, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (TGDRenaming tgdRenaming : renamedTgds.values()) {
            result.addAll(tgdRenaming.getRenamedTgd().getCoveredCorrespondences());
        }
        return result;
    }

    private List<VariablePathExpression> renamePaths(Expansion expansion, List<VariablePathExpression> originalPaths, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        if (logger.isDebugEnabled()) logger.debug("Renaming paths:\n" + SpicyEngineUtility.printCollection(originalPaths) + "\nin expansion: " + expansion);
        List<VariablePathExpression> newPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression pathExpression : originalPaths) {
            if (logger.isDebugEnabled()) logger.debug("Examining path: " + pathExpression);
            ExpansionPremise premise = findPremiseForPath(pathExpression, renamedTgds);
            if (logger.isDebugEnabled()) logger.debug("Expansion premise for path:\n" + premise);
            SetAlias newVariable = findNewVariable(pathExpression.getStartingVariable(), premise, renamedTgds);
            newPaths.add(new VariablePathExpression(newVariable, pathExpression.getPathSteps()));
        }
        return newPaths;
    }

    private ExpansionPremise findPremiseForPath(VariablePathExpression path, Map<ExpansionPremise, TGDRenaming> renamedTgds) {
        for (ExpansionPremise expansionPremise : renamedTgds.keySet()) {
            for (ExpansionElement expansionElement : expansionPremise.getExpansionElements()) {
                for (ExpansionFormulaPosition position : expansionElement.getCoveringAtom().getPositions()) {
//                    if (position.getPathExpression().equals(path)) {
                    if (position.getPathExpression().equalsAndHasSameVariableId(path)) {
                        return expansionPremise;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unable to find expansion premise for path " + path + " in " + renamedTgds);
    }

    private VariableCorrespondence findCorrespondenceForPathWithEqualId(VariablePathExpression pathExpression, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(pathExpression)) {
                return correspondence;
            }
        }
        throw new IllegalArgumentException("Unable to find correspondence for path " + pathExpression + " in " + correspondences);
    }

    ///////////////////////   STEP 2: generate target view
    private SimpleConjunctiveQuery generateTargetView(Expansion expansion) {
        ExpansionElement firstCoveringAtom = expansion.getExpansionElements().get(0);
        SimpleConjunctiveQuery view = new SimpleConjunctiveQuery(firstCoveringAtom.getCoveringAtom().getVariable().clone());
        for (int i = 0; i < expansion.getJoinConditions().size(); i++) {
            ExpansionJoin expansionJoin = expansion.getJoinConditions().get(i);
            VariableJoinCondition joinCondition = expansionJoin.getJoinCondition();
            ExpansionElement coveringAtom = expansion.getExpansionElements().get(i + 1);
            view.addVariable(coveringAtom.getCoveringAtom().getVariable().clone());
            view.addJoinCondition(joinCondition);
        }
        for (ExpansionJoin cyclicExpansionJoin : expansion.getCyclicJoinConditions()) {
            VariableJoinCondition cyclicJoinCondition = cyclicExpansionJoin.getJoinCondition();
            view.addCyclicJoinCondition(cyclicJoinCondition);
        }
        return view;
    }

    ///////////////////////   STEP 3: generate final correspondences
    private List<VariableCorrespondence> generateCorrespondences(Expansion expansion, Map<ExpansionPremise, TGDRenaming> renamedTgds, SimpleConjunctiveQuery targetView) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (ExpansionElement element : expansion.getExpansionElements()) {
            ExpansionPremise premise = findPremiseForElement(element, renamedTgds);
            List<VariablePathExpression> universalPaths = findUniversalPaths(element.getCoveringAtom());
            FORule renamedTgd = renamedTgds.get(premise).getRenamedTgd();
            List<VariableCorrespondence> renamedCorrespondences = findAndChangeCorrespondencences(universalPaths, renamedTgd);
            for (VariableCorrespondence renamedCorrespondence : renamedCorrespondences) {
                if (!SpicyEngineUtility.containsCorrespondenceForSamePath(result, renamedCorrespondence)) {
                    result.add(renamedCorrespondence);
                }
            }
        }
        return result;
    }

    private List<VariablePathExpression> findUniversalPaths(ExpansionAtom atom) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (ExpansionFormulaPosition position : atom.getPositions()) {
            if (position.isUniversal()) {
                result.add(position.getPathExpression());
            }
        }
        return result;
    }

    private List<VariableCorrespondence> findAndChangeCorrespondencences(List<VariablePathExpression> targetPaths, FORule rule) {
        List<VariableCorrespondence> result = new ArrayList<VariableCorrespondence>();
        for (VariablePathExpression atomPath : targetPaths) {
            VariableCorrespondence renamedCorrespondence = SpicyEngineUtility.findCorrespondenceForPath(atomPath, rule);
            VariableCorrespondence newCorrespondence = renamedCorrespondence.clone();
            newCorrespondence.setTargetPath(atomPath);
            result.add(newCorrespondence);
        }
        return result;
    }
}

class ExpansionPremise {

    private List<ExpansionElement> expansionElements = new ArrayList<ExpansionElement>();

    public ExpansionPremise(ExpansionElement element) {
        expansionElements.add(element);
    }

    public List<ExpansionElement> getExpansionElements() {
        return expansionElements;
    }

    public boolean addExpansionElement(ExpansionElement e) {
        return expansionElements.add(e);
    }

    public FORule getTGD() {
        if (expansionElements.isEmpty()) {
            return null;
        }
        return expansionElements.get(0).getCoveringAtom().getTgd();
    }

    @Override
    public String toString() {
        return "Expansion premise: " + expansionElements;
    }
}
