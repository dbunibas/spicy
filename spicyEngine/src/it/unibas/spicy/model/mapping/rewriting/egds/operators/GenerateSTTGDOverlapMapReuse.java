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

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.IGenerateSTTGDOverlapMap;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.operators.NormalizeSTTGDs;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.mapping.rewriting.egds.STTGDOverlapMap;
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import it.unibas.spicy.model.mapping.rewriting.egds.VariableOverlap;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.EGDUtility;
import it.unibas.spicy.model.mapping.rewriting.operators.CheckTGDHomomorphism;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateSTTGDOverlapMapReuse implements IGenerateSTTGDOverlapMap {

    private static Log logger = LogFactory.getLog(GenerateSTTGDOverlapMapReuse.class);
    private int maxIterations = 100;
//    public int maxIterations = Integer.MAX_VALUE;
    private ChaseOverlap chaser = new ChaseOverlap();
    private CompareTgdsInOverlapMap tgdComparator = new CompareTgdsInOverlapMap();
    private CheckTGDHomomorphism subsumptionChecker = new CheckTGDHomomorphism();
    private NormalizeSTTGDs tgdNormalizer = new NormalizeSTTGDs();

    @SuppressWarnings("unchecked")
    public STTGDOverlapMap generateOverlapMap(List<FORule> stTgds, MappingTask mappingTask) {
        if (!mappingTask.getConfig().rewriteOverlaps()) {
            if (logger.isDebugEnabled()) logger.debug("------------- EGDs must not be rewritten. Returning... --------------------");
            return new STTGDOverlapMap(mappingTask, Collections.EMPTY_MAP);
        }
        if (logger.isDebugEnabled()) logger.debug("------------- GENERATING OVERLAP MAP --------------------");
        List<FORule> renamedTgds = renameTgds(stTgds);
//        if (logger.isDebugEnabled()) logger.debug("Initial st tgds:\n" + SpicyEngineUtility.printTgdsInLogicalForm(renamedTgds, mappingTask));
        if (logger.isDebugEnabled()) logger.debug("Initial st tgds:\n" + SpicyEngineUtility.printTgds(renamedTgds, mappingTask));
        STTGDOverlapMap overlapMap = new STTGDOverlapMap(mappingTask, initializeMap(renamedTgds));
        boolean fixpoint;
        int iterations = 0;
        do {
            fixpoint = true;
            List<FORule> allTgds = new ArrayList<FORule>(overlapMap.getOverlapMap().keySet());
            Collections.sort(allTgds);
            for (int i = 0; i < allTgds.size(); i++) {
                FORule tgdI = allTgds.get(i);
                for (int j = i; j < allTgds.size(); j++) {
                    FORule tgdJ = allTgds.get(j);
                    if (!existsMeaningfulOverlap(tgdI, tgdJ, i, j, overlapMap, mappingTask)) {
                        continue;
                    }
                    fixpoint = fixpoint && generateOverlapTgds(tgdI, tgdJ, overlapMap, mappingTask);
                }
            }
            iterations++;
        } while (!fixpoint && iterations <= maxIterations);
        if (fixpoint == false) {
//            throw new IllegalMappingTaskException("Computation of overlap tgds did not reach fixpoint after fixed number of iterations (" + maxIterations + ")\n" + SpicyEngineUtility.printTgdsInLogicalForm(new ArrayList<TGD>(overlapMap.getOverlapMap().keySet()), mappingTask));
            logger.error("Computation of overlap tgds did not reach fixpoint after fixed number of iterations (" + maxIterations + ")\n" + SpicyEngineUtility.printTgdsInLogicalForm(new ArrayList<FORule>(overlapMap.getOverlapMap().keySet()), mappingTask));
        }
        if (logger.isDebugEnabled()) logger.debug("------------- FINAL OVERLAP MAP --------------------\n" + SpicyEngineUtility.printMap(overlapMap.getOverlapMap()));
        return overlapMap;
    }

    private List<FORule> renameTgds(List<FORule> stTgds) {
        RenameSetAliases renamer = new RenameSetAliases();
        List<FORule> result = new ArrayList<FORule>();
        for (FORule tgd : stTgds) {
            FORule renamedTgd = renamer.renameAliasesInTGD(tgd).getRenamedTgd();
            result.add(renamedTgd);
        }
        return result;
    }

    private Map<FORule, List<VariableOverlap>> initializeMap(List<FORule> tgds) {
        Map<FORule, List<VariableOverlap>> overlapMap = new HashMap<FORule, List<VariableOverlap>>();
        for (FORule tgd : tgds) {
            overlapMap.put(tgd, new ArrayList<VariableOverlap>());
        }
        return overlapMap;
    }

    private boolean existsMeaningfulOverlap(FORule tgd1, FORule tgd2, int i, int j, STTGDOverlapMap overlapMap, MappingTask mappingTask) {
        if (i != j && containTargetVariablesWithSameId(tgd1, tgd2)) {
            return false;
        }
//        if (i != j && (isDerivedFrom(tgd1, tgd2, overlapMap) || isDerivedFrom(tgd2, tgd1, overlapMap))) {
//            return false;
//        }
        if (i != j && (subsumptionChecker.subsumes(tgd1, tgd2, mappingTask.getTargetProxy(), mappingTask)
                || subsumptionChecker.subsumes(tgd2, tgd1, mappingTask.getTargetProxy(), mappingTask))) {
            return false;
        }
        if (i == j && !SpicyEngineUtility.hasSelfJoins(tgd1.getTargetView())) {
            return false;
        }
        return true;
    }

    private boolean containTargetVariablesWithSameId(FORule tgd1, FORule tgd2) {
        for (SetAlias variable : tgd1.getTargetView().getVariables()) {
            if (SpicyEngineUtility.containsVariableWithSameId(tgd2.getTargetView().getVariables(), variable)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDerivedFrom(FORule tgd1, FORule tgd2, STTGDOverlapMap overlapMap) {
        List<VariableOverlap> overlapsForTgd1 = overlapMap.getOverlaps(tgd1);
        for (VariableOverlap variableOverlap : overlapsForTgd1) {
            if (variableOverlap.getOverlapTGDs().contains(tgd2)) {
                return true;
            }
        }
        return false;
    }

    private boolean generateOverlapTgds(FORule tgd1, FORule tgd2, STTGDOverlapMap overlapMap, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("------------- Analyzing tgds:\n" + tgd1 + "\nand\n" + tgd2);
//        if (logger.isDebugEnabled()) logger.debug("------------- Analyzing tgds:\n" + tgd1.toLogicalString(mappingTask) + "\nand\n" + tgd2.toLogicalString(mappingTask));
        if (logger.isTraceEnabled()) logger.trace("Current result: " + SpicyEngineUtility.printMap(overlapMap.getOverlapMap()));
        boolean newTgd = false;
        List<VariableOverlap> tgdOverlaps = findOverlaps(tgd1, tgd2, mappingTask);
        for (VariableOverlap overlap : tgdOverlaps) {
            if (!processed(overlapMap, overlap)) {
                if (logger.isDebugEnabled()) logger.debug("------------- There is an overlap to process: " + overlap);
                FORule overlapTgd = chaser.chaseOverlap(overlap, tgd1, tgd2, mappingTask);
                if (overlapTgd != null) {
                    if (logger.isDebugEnabled()) logger.debug("------------- Generated overlap tgd:\n" + overlapTgd);
                    if (logger.isDebugEnabled()) logger.debug("------------- Generated overlap tgd in logical form:\n" + overlapTgd.toLogicalString(mappingTask));
                    if (logger.isDebugEnabled()) logger.debug("------------- Current overlap tgds:-----------\n" + SpicyEngineUtility.printTgdsInLogicalForm(new ArrayList<FORule>(overlapMap.getOverlapMap().keySet()), mappingTask));
                    List<FORule> normalizedTGDs = tgdNormalizer.normalizeTGD(overlapTgd);
                    for (FORule normalizedTGD : normalizedTGDs) {
                        if (logger.isDebugEnabled()) logger.debug("------------- Normalized overlap tgd:\n" + normalizedTGD);
                        if (logger.isDebugEnabled()) logger.debug("------------- Normalized overlap tgd in logical form:\n" + normalizedTGD.toLogicalString(mappingTask));
                        if (!contains(overlapMap, normalizedTGD, mappingTask)) {
                            if (logger.isDebugEnabled()) logger.debug("------------- Overlap tgd is new, normalizing and adding... ");
                            overlap.addOverlapTGD(normalizedTGD);
                            addNewTgdToMap(overlapMap, normalizedTGD);
                            addOverlapToMap(overlapMap, overlap);
                            newTgd = true;
                            if (logger.isDebugEnabled()) logger.debug("------------- New overlap tgds:-----------\n" + SpicyEngineUtility.printTgdsInLogicalForm(new ArrayList<FORule>(overlapMap.getOverlapMap().keySet()), mappingTask));
                        }
                    }
                    if (newTgd == false) {
                        overlapMap.getDiscardedOverlaps().add(overlap);
                    }
                }
            }
        }
        return !newTgd;
    }

    private List<VariableOverlap> findOverlaps(FORule tgd1, FORule tgd2, MappingTask mappingTask) {
        List<VariableOverlap> tgdOvelaps = new ArrayList<VariableOverlap>();
        // TODO: check for nested
        for (SetAlias variable1 : tgd1.getTargetView().getGenerators()) {
            for (SetAlias variable2 : tgd2.getTargetView().getGenerators()) {
                if (!variable1.isClone(variable2)) {
                    continue;
                }
                if (variable1.hasSameId(variable2)) {
                    continue;
                }
                tgdOvelaps.addAll(checkOverlaps(variable1, variable2, tgd1, tgd2, mappingTask));
            }
        }
        return tgdOvelaps;
    }

    private List<VariableOverlap> checkOverlaps(SetAlias variable1, SetAlias variable2, FORule tgd1, FORule tgd2, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Searching overlaps between: " + variable1 + " and " + variable2);
        List<VariableOverlap> result = new ArrayList<VariableOverlap>();
        List<VariableFunctionalDependency> relevantDependencies = findFunctionalDependenciesForSet(mappingTask.getTargetProxy(), variable1);
        for (VariableFunctionalDependency functionalDependency : relevantDependencies) {
            if (isOverlap(functionalDependency, tgd1, tgd2, mappingTask)) {
                VariableOverlap newOverlap = new VariableOverlap(variable1, variable2, tgd1, tgd2, functionalDependency);
                if (tgd1.equals(tgd2)) {
                    newOverlap.setSingleTgd(true);
                }
                result.add(newOverlap);
            }
        }
        return result;
    }

    private List<VariableFunctionalDependency> findFunctionalDependenciesForSet(IDataSourceProxy dataSource, SetAlias setVariable) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (VariableFunctionalDependency functionalDependency : dataSource.getMappingData().getDependencies()) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing functional dependency: " + functionalDependency);
            if (functionalDependency.getVariable().equalsOrIsClone(setVariable)) {
                result.add(new VariableFunctionalDependency(functionalDependency, setVariable));
            }
        }
        return result;
    }

    private boolean isOverlap(VariableFunctionalDependency functionaDependency, FORule tgd1, FORule tgd2, MappingTask mappingTask) {
        Determination fdUniversalVariablesInTgd1 = EGDUtility.findUniversalVariablesForTargetPaths(functionaDependency.getLeftPaths(), tgd1, mappingTask);
        Determination fdUniversalVariablesInTgd2 = EGDUtility.findUniversalVariablesForTargetPaths(functionaDependency.getLeftPaths(), tgd2, mappingTask);
        if (fdUniversalVariablesInTgd1 != null && fdUniversalVariablesInTgd2 != null) {
            return true;
        }
        return false;
    }

    private boolean processed(STTGDOverlapMap overlapMap, VariableOverlap overlap) {
        for (FORule tgd : overlapMap.getOverlapMap().keySet()) {
            if (overlapMap.getOverlaps(tgd).contains(overlap)) {
                return true;
            }
        }
//        if (overlapMap.getDiscardedOverlaps().contains(overlap)) {
//            return true;
//        }
        return false;
    }

    private boolean contains(STTGDOverlapMap overlapMap, FORule overlapTgd, MappingTask mappingTask) {
        for (FORule tgd : overlapMap.getOverlapMap().keySet()) {
            if (tgdComparator.equals(tgd, overlapTgd, mappingTask)) {
                return true;
            }
        }
        return false;
    }

    private void addOverlapToMap(STTGDOverlapMap overlapMap, VariableOverlap overlap) {
        List<VariableOverlap> overlaps1 = overlapMap.getOverlaps(overlap.getTgd1());
        if (!overlaps1.contains(overlap)) {
            overlaps1.add(overlap);
        }
        List<VariableOverlap> overlaps2 = overlapMap.getOverlaps(overlap.getTgd2());
        if (!overlaps2.contains(overlap)) {
            overlaps2.add(overlap);
        }
    }

    private void addNewTgdToMap(STTGDOverlapMap overlapMap, FORule overlapTgd) {
        List<VariableOverlap> overlapsForTgd = overlapMap.getOverlaps(overlapTgd);
        if (overlapsForTgd == null) {
            overlapMap.getOverlapMap().put(overlapTgd, new ArrayList<VariableOverlap>());
        }
    }
}
