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
package it.unibas.spicy.model.mapping.rewriting.egds.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.algebra.operators.NormalizeViewForExecutionPlan;
import it.unibas.spicy.model.datasource.operators.DuplicateSet;
import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.operators.RenameSetAliasesUtility;
import it.unibas.spicy.model.mapping.rewriting.egds.AttributeGroup;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDAttributeGroups;
import it.unibas.spicy.model.mapping.rewriting.egds.VariableOverlap;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.EGDUtility;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class ChaseOverlap {

    private static Log logger = LogFactory.getLog(ChaseOverlap.class);
    private NormalizeViewForExecutionPlan viewNormalizer = new NormalizeViewForExecutionPlan();
    private RenameSetAliasesUtility renamerUtility = new RenameSetAliasesUtility();
    private FindAttributeGroups attributeGroupFinder = new FindAttributeGroups();
    private ChaseOperator chaseOperator = new ChaseOperator();
    private CheckMeaningfulOverlap checker = new CheckMeaningfulOverlap();

    FORule chaseOverlap(VariableOverlap overlap, FORule tgd1, FORule tgd2, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("********************** Chasing overlap :" + overlap + " between \n" + tgd1.toLogicalString(mappingTask) + "\nand\n" + tgd2.toLogicalString(mappingTask));
        SimpleConjunctiveQuery sourceView = generateSourceView(tgd1, tgd2, overlap);
        FORule overlapTgd = generateOverlapTgd(tgd1, tgd2, sourceView, overlap, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("********************** Final overlap tgd:\n" + overlapTgd.toLogicalString(mappingTask));
        if (logger.isDebugEnabled()) logger.debug("********************** Original tgds:\n" + tgd1.toLogicalString(mappingTask) + "\nand\n" + tgd2.toLogicalString(mappingTask));
        return overlapTgd;
    }

    /////////////////////////////    source view   ///////////////////////////////////
    private SimpleConjunctiveQuery generateSourceView(FORule firstTgd, FORule secondTgd, VariableOverlap overlap) {
        SimpleConjunctiveQuery sourceView = mergeViews(firstTgd.getSimpleSourceView(), secondTgd.getSimpleSourceView());
        VariableJoinCondition overlapJoin = generateSourceJoinForOverlaps(firstTgd, secondTgd, overlap);
        if (!isSelfJoin(overlapJoin) && !SpicyEngineUtility.containsJoinWithSameIds(sourceView.getAllJoinConditions(), overlapJoin)) {
            sourceView.addJoinCondition(overlapJoin);
        }
        SimpleConjunctiveQuery normalizedSourceView = viewNormalizer.normalizeView(sourceView);
        return normalizedSourceView;
    }

    private SimpleConjunctiveQuery mergeViews(SimpleConjunctiveQuery firstView, SimpleConjunctiveQuery secondView) {
        if (logger.isTraceEnabled()) logger.trace("Merging views:\n" + firstView + "\n    and:\n" + secondView);
        SimpleConjunctiveQuery newView = firstView.clone();
        addVariables(newView, secondView);
        addJoins(newView, secondView);
        addSelections(newView, secondView);
        if (logger.isTraceEnabled()) logger.trace("Merged view" + newView);
        return newView;
    }

    private void addVariables(SimpleConjunctiveQuery sourceView, SimpleConjunctiveQuery otherView) {
        for (SetAlias variable : otherView.getVariables()) {
            if (!SpicyEngineUtility.containsVariableWithSameId(sourceView.getGenerators(), variable)) {
                sourceView.getVariables().add(variable);
            }
        }
    }

    private void addJoins(SimpleConjunctiveQuery sourceView, SimpleConjunctiveQuery otherView) {
        for (VariableJoinCondition joinCondition : otherView.getAllJoinConditions()) {
            if (!SpicyEngineUtility.containsJoinWithSameIds(sourceView.getAllJoinConditions(), joinCondition)) {
                sourceView.addJoinCondition(joinCondition);
            }
        }
    }

    private void addSelections(SimpleConjunctiveQuery sourceView, SimpleConjunctiveQuery otherView) {
        for (VariableSelectionCondition selectionCondition : otherView.getSelectionConditions()) {
            if (!SpicyEngineUtility.containsSelectionWithSameIds(sourceView.getSelectionConditions(), selectionCondition)) {
                sourceView.addSelectionCondition(selectionCondition);
            }
        }
    }

    private VariableJoinCondition generateSourceJoinForOverlaps(FORule tgd1, FORule tgd2, VariableOverlap overlap) {
        List<VariablePathExpression> firstKeyPaths = findSourcePathsForKey(overlap.getFunctionalDependency(), overlap.getVariable1(), tgd1);
        List<VariablePathExpression> secondKeyPaths = findSourcePathsForKey(overlap.getFunctionalDependency(), overlap.getVariable2(), tgd2);
        VariableJoinCondition overlapJoin = new VariableJoinCondition(firstKeyPaths, secondKeyPaths, false, true);
        return overlapJoin;
    }

    private List<VariablePathExpression> findSourcePathsForKey(VariableFunctionalDependency functionalDependency, SetAlias newVariable, FORule tgd) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression keyPath : functionalDependency.getLeftPaths()) {
            VariablePathExpression newKeyPath = new VariablePathExpression(keyPath, newVariable);
            VariableCorrespondence keyCorrespondence = findCorrespondenceForPath(newKeyPath, tgd);
            result.add(keyCorrespondence.getFirstSourcePath());
        }
        return result;
    }

    private VariableCorrespondence findCorrespondenceForPath(VariablePathExpression targetPath, FORule tgd) {
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return correspondence;
            }
        }
        throw new IllegalArgumentException("Key path " + targetPath + " is not universal in tgd: " + tgd);
    }

    private boolean isSelfJoin(VariableJoinCondition joinCondition) {
        for (int i = 0; i < joinCondition.getFromPaths().size(); i++) {
            VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
            VariablePathExpression toPath = joinCondition.getToPaths().get(i);
            if (!fromPath.equalsAndHasSameVariableId(toPath)) {
                return false;
            }
        }
        return true;
    }

    /////////////////////////////    target view   ///////////////////////////////////
    private FORule generateOverlapTgd(FORule tgd1, FORule tgd2, SimpleConjunctiveQuery sourceView, VariableOverlap overlap, MappingTask mappingTask) {
        if (logger.isTraceEnabled()) logger.trace("Generating target view for overlapping tgds:\n" + tgd1.toLogicalString(mappingTask) + "\n" + tgd2.toLogicalString(mappingTask));
        Map<Integer, SetAlias> replacements = new HashMap<Integer, SetAlias>();
        SimpleConjunctiveQuery candidateTargetView = mergeViews(tgd1.getTargetView(), tgd2.getTargetView());
        FORule candidateTGD = new FORule(sourceView, candidateTargetView);
        candidateTGD.getCoveredCorrespondences().addAll(tgd1.getCoveredCorrespondences());
        candidateTGD.getCoveredCorrespondences().addAll(tgd2.getCoveredCorrespondences());
        TGDAttributeGroups candidateTgdGroups = attributeGroupFinder.findAttributeGroupsInTgd(candidateTGD, mappingTask);
        List<AttributeGroup> attributeGroups = new ArrayList<AttributeGroup>();
        attributeGroups.addAll(candidateTgdGroups.getUniversalGroups());
        attributeGroups.addAll(candidateTgdGroups.getExistentialGroups());
        if (logger.isTraceEnabled()) logger.trace("Attribute groups: " + SpicyEngineUtility.printCollection(attributeGroups));
        equateOverlappingAttributes(tgd1, tgd2, overlap, attributeGroups, mappingTask);
        chaseOperator.doChase(attributeGroups, mappingTask, tgd1, tgd2);
        SimpleConjunctiveQuery targetView = new SimpleConjunctiveQuery();
        addVariablesToView(targetView, candidateTargetView, attributeGroups, replacements, mappingTask);
        addJoinConditions(candidateTargetView.getAllJoinConditions(), replacements, targetView);
        //TODO: check what happens if the tgd is not normalized
        SimpleConjunctiveQuery normalizedTargetView = viewNormalizer.normalizeView(targetView);
        FORule overlapTgd = new FORule(sourceView, normalizedTargetView);
        if (logger.isTraceEnabled()) logger.trace("Initial Overlap TGD:\n" + overlapTgd);
        List<AttributeGroup> targetGroups = attributeGroupFinder.findAttributeGroupsInView(targetView, mappingTask.getTargetProxy());
        List<VariableJoinCondition> additionalConditions = addCorrespondences(tgd1.getCoveredCorrespondences(), replacements, overlapTgd, targetGroups, attributeGroups);
        additionalConditions.addAll(addCorrespondences(tgd2.getCoveredCorrespondences(), replacements, overlapTgd, targetGroups, attributeGroups));
        sourceView.addCyclicJoinConditions(additionalConditions);
        //TODO***********************: EGD optimize
        if (logger.isDebugEnabled()) logger.debug("Candidate overlap tgd:\n" + overlapTgd + "\n" + overlapTgd.toLogicalString(false, mappingTask));
        if (!checker.isMeaningful(tgd1, tgd2, overlapTgd, replacements, mappingTask)) {
            return null;
        }
        return overlapTgd;
    }

    private void equateOverlappingAttributes(FORule tgd1, FORule tgd2, VariableOverlap overlap, List<AttributeGroup> attributeGroups, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Equating target attributes for overlap: " + overlap);
//        List<VariablePathExpression> firstKeyPaths = EGDUtility.findCorrectPaths(overlap.getFunctionalDependency().getLeftPaths(), tgd1, mappingTask);
//        List<VariablePathExpression> secondKeyPaths = EGDUtility.findCorrectPaths(overlap.getFunctionalDependency().getLeftPaths(), tgd2, mappingTask);
        List<VariablePathExpression> firstKeyPaths = EGDUtility.correctPaths(overlap.getFunctionalDependency().getLeftPaths(), overlap.getVariable1());
        List<VariablePathExpression> secondKeyPaths = EGDUtility.correctPaths(overlap.getFunctionalDependency().getLeftPaths(), overlap.getVariable2());
        if (logger.isTraceEnabled()) logger.trace("First left paths: " + firstKeyPaths);
        if (logger.isTraceEnabled()) logger.trace("Second left paths: " + secondKeyPaths);
        for (int i = 0; i < firstKeyPaths.size(); i++) {
            VariablePathExpression firstKeyPath = firstKeyPaths.get(i);
            VariablePathExpression secondKeyPath = secondKeyPaths.get(i);
            EGDUtility.mergeGroups(firstKeyPath, secondKeyPath, attributeGroups);
        }
    }

    private void addVariablesToView(SimpleConjunctiveQuery result, SimpleConjunctiveQuery view, List<AttributeGroup> attributeGroups,
            Map<Integer, SetAlias> replacements, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Adding variables from view:\n" + view + "\nto result:\n" + result);
        Map<Integer, SetAlias> duplications = new HashMap<Integer, SetAlias>();
        for (SetAlias variable : view.getVariables()) {
            if (logger.isDebugEnabled()) logger.debug("Adding variable :" + variable);
            if (logger.isTraceEnabled()) logger.trace("Attribute groups :" + SpicyEngineUtility.printCollection(attributeGroups));
            SetAlias existingVariable = findExistingVariable(variable, result, attributeGroups, duplications, mappingTask);
            if (logger.isDebugEnabled()) logger.debug("Existing variable :" + existingVariable);
            if (existingVariable == null) {
                if (EGDUtility.containsEqualVariable(result, variable)) {
                    if (logger.isDebugEnabled()) logger.debug("Target view contains equal variable, duplicating... ");
                    SetAlias newVariable = duplicateVariable(variable, mappingTask);
                    result.addVariable(newVariable);
                    replacements.put(variable.getId(), newVariable);
                    duplications.put(newVariable.getId(), variable);
                } else {
                    result.addVariable(variable);
                }
            } else {
                replacements.put(variable.getId(), existingVariable);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result of adding variables:\n" + result + "\nVariable replacements: " + SpicyEngineUtility.printMap(replacements));
    }

    private SetAlias findExistingVariable(SetAlias variable, SimpleConjunctiveQuery result, List<AttributeGroup> attributeGroups,
            Map<Integer, SetAlias> duplications, MappingTask mappingTask) {
        for (SetAlias variableInView : result.getGenerators()) {
            if (variable.equalsOrIsClone(variableInView)
                    && haveEqualAttributes(variable, variableInView, attributeGroups, duplications, mappingTask)) {
                return variableInView;
            }
        }
        return null;
    }

    private boolean haveEqualAttributes(SetAlias variable, SetAlias variableInView, List<AttributeGroup> attributeGroups,
            Map<Integer, SetAlias> duplications, MappingTask mappingTask) {
        if (logger.isTraceEnabled()) logger.trace("Comparing variable " + variable + " and " + variableInView);
        SetAlias renamingForVariable = findReplacementForVariable(variable, duplications);
        SetAlias renamingForVariableInView = findReplacementForVariable(variableInView, duplications);
        List<VariablePathExpression> variablePaths = renamingForVariable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        List<VariablePathExpression> variablePathsInView = renamingForVariableInView.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        for (int i = 0; i < variablePaths.size(); i++) {
            VariablePathExpression path = variablePaths.get(i);
            VariablePathExpression pathInView = variablePathsInView.get(i);
            if (!EGDUtility.areEqual(path, pathInView, attributeGroups)) {
                return false;
            }
        }
        return true;
    }

    private SetAlias findReplacementForVariable(SetAlias variable, Map<Integer, SetAlias> duplications) {
        SetAlias replacement = duplications.get(variable.getId());
        if (replacement == null) {
            replacement = variable;
        }
        if (logger.isTraceEnabled()) logger.trace("Replacement for variable: " + variable + " = " + replacement);
        return replacement;
    }

    private SetAlias duplicateVariable(SetAlias variable, MappingTask mappingTask) {
        DuplicateSet duplicator = new DuplicateSet();
        PathExpression originalPath = SpicyEngineUtility.removeClonesFromAbsolutePath(variable.getAbsoluteBindingPathExpression());
        PathExpression duplicatePath = duplicator.duplicateSet(originalPath, mappingTask.getTargetProxy());
        VariablePathExpression bindingPath = new VariablePathExpression(duplicatePath);
        SetAlias newVariable = variable.cloneWithoutAlgebraTreeAndWithNewId();
        newVariable.setBindingPathExpression(bindingPath);
        mappingTask.getTargetProxy().getMappingData().getVariables().add(newVariable);
        if (logger.isDebugEnabled()) logger.debug("Duplicating variable: " + variable + ". New variable: " + newVariable);
        return newVariable;
    }

    private void addJoinConditions(List<VariableJoinCondition> joinConditions, Map<Integer, SetAlias> replacements, SimpleConjunctiveQuery view) {
        for (VariableJoinCondition joinConditionToAdd : joinConditions) {
            VariableJoinCondition newJoinCondition = renamerUtility.generateNewJoin(joinConditionToAdd, replacements);
            if (!selfJoinOnSameAtom(newJoinCondition) && !view.getAllJoinConditions().contains(newJoinCondition)) {
                view.addJoinCondition(newJoinCondition);
            }
        }
    }

    private boolean selfJoinOnSameAtom(VariableJoinCondition newJoinCondition) {
        return SpicyEngineUtility.equalLists(newJoinCondition.getFromPaths(), newJoinCondition.getToPaths());
    }

    /////////////////////////////    correspondences   ///////////////////////////////////
    private List<VariableJoinCondition> addCorrespondences(List<VariableCorrespondence> correspondences, Map<Integer, SetAlias> renamings, FORule overlapTGD,
            List<AttributeGroup> targetGroups, List<AttributeGroup> attributeGroups) {
        if (logger.isDebugEnabled()) logger.debug("Adding correspondences:\n" + SpicyEngineUtility.printCollection(correspondences) + " to overlap tgd\n" + overlapTGD);
        List<VariableJoinCondition> additionalConditions = new ArrayList<VariableJoinCondition>();
        for (VariableCorrespondence correspondence : correspondences) {
            VariablePathExpression newTargetPath = renamerUtility.correctPath(correspondence.getTargetPath(), renamings);
            AttributeGroup attributeGroupForPath = EGDUtility.findGroupForPath(newTargetPath, targetGroups);
            for (VariablePathExpression targetPath : attributeGroupForPath.getAttributePaths()) {
                VariableCorrespondence newCorrespondence = new VariableCorrespondence(correspondence, targetPath);
                VariableJoinCondition additionalCondition = addNewCorrespondence(overlapTGD, newCorrespondence, attributeGroups);
                if (additionalCondition != null) {
                    additionalConditions.add(additionalCondition);
                }
            }
        }
        return additionalConditions;
    }

    private VariableJoinCondition addNewCorrespondence(FORule overlapTGD, VariableCorrespondence newCorrespondence, List<AttributeGroup> attributeGroups) {
        for (VariableCorrespondence correspondence : overlapTGD.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(newCorrespondence.getTargetPath())) {
                if (correspondence.equals(newCorrespondence)) {
                    return null;
                }
                if (sourcePathsAreInSameGroup(correspondence, newCorrespondence, attributeGroups)) {
                    return null;
                } else {
                    if (!correspondence.isValueCopy()) {
                        throw new IllegalMappingTaskException("Unable to add condition for complex correspondence " + newCorrespondence + ". Different correspondence for same target path:\n" + SpicyEngineUtility.printCollection(overlapTGD.getCoveredCorrespondences()) + "\nAttribute groups:\n" + SpicyEngineUtility.printCollection(attributeGroups));
                    }
                    return new VariableJoinCondition(correspondence.getFirstSourcePath(), newCorrespondence.getFirstSourcePath(), false, true);
                } 
            }
        }
        if (logger.isDebugEnabled()) logger.debug("New correspondence to add: " + newCorrespondence);
        overlapTGD.addCoveredCorrespondence(newCorrespondence);
        return null;
    }

    private boolean sourcePathsAreInSameGroup(VariableCorrespondence correspondence, VariableCorrespondence newCorrespondence, List<AttributeGroup> attributeGroups) {
        for (AttributeGroup attributeGroup : attributeGroups) {
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, correspondence.getFirstSourcePath())
                    && EGDUtility.containsPathWithSameVariableId(attributeGroup, newCorrespondence.getFirstSourcePath())) {
                return true;
            }
        }
        return false;
    }
}

class ChaseOperator {

    private static Log logger = LogFactory.getLog(ChaseOperator.class);

    void doChase(List<AttributeGroup> attributeGroups, MappingTask mappingTask, FORule tgd1, FORule tgd2) {
        if (logger.isDebugEnabled()) logger.debug("################### Chasing dependencies on attribute groups:\n" + SpicyEngineUtility.printCollection(attributeGroups));
        if (logger.isTraceEnabled()) logger.trace("First tgd: " + tgd1.toLogicalString(mappingTask));
        if (logger.isTraceEnabled()) logger.trace("Second tgd: " + tgd2.toLogicalString(mappingTask));
        List<VariableFunctionalDependency> functionalDependencies = mappingTask.getTargetProxy().getMappingData().getDependencies();
        boolean fixpoint;
        do {
            fixpoint = true;
            if (logger.isDebugEnabled()) logger.debug("New chase step towards fixpoint...");
            for (VariableFunctionalDependency functionalDependency : functionalDependencies) {
                fixpoint = chaseDependency(functionalDependency, tgd1, tgd2, attributeGroups);
            }
        } while (!fixpoint);
    }

    private boolean chaseDependency(VariableFunctionalDependency functionalDependency,
            FORule tgd1, FORule tgd2, List<AttributeGroup> attributeGroups) {
        if (logger.isDebugEnabled()) logger.debug("Chasing dependency: " + functionalDependency);
        List<SetAlias> clonesInTgd1 = findClonesForVariableDependency(functionalDependency, tgd1);
        List<SetAlias> clonesInTgd2 = findClonesForVariableDependency(functionalDependency, tgd2);
        if (logger.isDebugEnabled()) logger.debug("Aliases for dependency in tgd 1: " + clonesInTgd1);
        if (logger.isDebugEnabled()) logger.debug("Aliases for dependency in tgd 2: " + clonesInTgd2);
        boolean fixpoint = true;
        for (int i = 0; i < clonesInTgd1.size(); i++) {
            SetAlias variable1 = clonesInTgd1.get(i);
            for (int j = i; j < clonesInTgd2.size(); j++) {
                SetAlias variable2 = clonesInTgd2.get(j);
                fixpoint = fixpoint && chaseDependencyOnVariables(functionalDependency, variable1, variable2, attributeGroups);
            }
        }
        return fixpoint;
    }

    private List<SetAlias> findClonesForVariableDependency(VariableFunctionalDependency functionalDependency, FORule tgd) {
        SetAlias dependencyVariable = functionalDependency.getVariable();
        List<SetAlias> result = new ArrayList<SetAlias>();
        for (SetAlias variableInTgd : tgd.getTargetView().getGenerators()) {
            if (variableInTgd.equalsOrIsClone(dependencyVariable)) {
                result.add(variableInTgd);
            }
        }
        return result;
    }

    private boolean chaseDependencyOnVariables(VariableFunctionalDependency functionalDependency, SetAlias variable1, SetAlias variable2, List<AttributeGroup> attributeGroups) {
        if (logger.isDebugEnabled()) logger.debug("################### Chasing dependency : " + functionalDependency);
        if (logger.isDebugEnabled()) logger.debug("Attribute groups:\n" + SpicyEngineUtility.printCollection(attributeGroups));
        List<VariablePathExpression> renamedLeftPaths1 = EGDUtility.correctPaths(functionalDependency.getLeftPaths(), variable1);
        List<VariablePathExpression> renamedLeftPaths2 = EGDUtility.correctPaths(functionalDependency.getLeftPaths(), variable2);
        if (logger.isTraceEnabled()) logger.trace("First left paths : " + SpicyEngineUtility.printCollection(renamedLeftPaths1));
        if (logger.isTraceEnabled()) logger.trace("Second left paths : " + SpicyEngineUtility.printCollection(renamedLeftPaths2));
        if (renamedLeftPaths1 == null || renamedLeftPaths2 == null) {
            if (logger.isDebugEnabled()) logger.debug("Paths are null, returning... ");
            return true;
        }
        List<VariablePathExpression> renamedRightPaths1 = EGDUtility.correctPaths(functionalDependency.getRightPaths(), variable1);
        List<VariablePathExpression> renamedRightPaths2 = EGDUtility.correctPaths(functionalDependency.getRightPaths(), variable2);
        if (logger.isTraceEnabled()) logger.trace("First right paths : " + SpicyEngineUtility.printCollection(renamedRightPaths1));
        if (logger.isTraceEnabled()) logger.trace("Second right paths : " + SpicyEngineUtility.printCollection(renamedRightPaths2));
        if (areEqual(renamedLeftPaths1, renamedLeftPaths2, attributeGroups)) {
            if (logger.isDebugEnabled()) logger.debug("Left paths are equal");
            if (!areEqual(renamedRightPaths1, renamedRightPaths2, attributeGroups)) {
                if (logger.isDebugEnabled()) logger.debug("Right paths are not equal, merging...");
                mergeGroups(renamedRightPaths1, renamedRightPaths2, attributeGroups);
                return false;
            } else {
                if (logger.isDebugEnabled()) logger.debug("Right paths are equal, returning...");
            }
        } else {
            if (logger.isDebugEnabled()) logger.debug("Left paths are not equal, returning");
        }
        return true;
    }

    private boolean areEqual(List<VariablePathExpression> paths1, List<VariablePathExpression> paths2, List<AttributeGroup> attributeGroups) {
        for (int i = 0; i < paths1.size(); i++) {
            VariablePathExpression path1 = paths1.get(i);
            VariablePathExpression path2 = paths2.get(i);
            if (!EGDUtility.areEqual(path1, path2, attributeGroups)) {
                return false;
            }
        }
        return true;
    }

    private void mergeGroups(List<VariablePathExpression> paths1, List<VariablePathExpression> paths2, List<AttributeGroup> attributeGroups) {
        for (int i = 0; i < paths1.size(); i++) {
            VariablePathExpression path1 = paths1.get(i);
            VariablePathExpression path2 = paths2.get(i);
            EGDUtility.mergeGroups(path1, path2, attributeGroups);
        }
    }
}
