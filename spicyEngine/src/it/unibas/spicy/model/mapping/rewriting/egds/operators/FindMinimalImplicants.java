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
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDImplicants;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindMinimalImplicants {

    private static Log logger = LogFactory.getLog(FindMinimalImplicants.class);
    
    private CheckImplication implicationChecker = new CheckImplication();
    private FindImplicantsLowerBound lowerBoundFinder = new FindImplicantsLowerBound();

    public TGDImplicants findMinimalImplicants(FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Generating implicants map for tgd\n" + tgd);
        if (logger.isDebugEnabled()) logger.debug("Tgd in logical form:\n" + tgd.toLogicalString(false, mappingTask));
        if (logger.isTraceEnabled()) logger.debug("Universal variables:\n" + SpicyEngineUtility.printCollection(tgd.getUniversalFormulaVariables(mappingTask)));
        if (logger.isTraceEnabled()) logger.debug("Existential variables:\n" + SpicyEngineUtility.printCollection(tgd.getExistentialFormulaVariables(mappingTask)));
        if (logger.isTraceEnabled()) logger.debug("Universal variables in target:\n" + SpicyEngineUtility.printCollection(tgd.getUniversalFormulaVariablesInTarget(mappingTask)));
        TGDImplicants result = new TGDImplicants(tgd, new Determination(tgd.getUniversalFormulaVariablesInTarget(mappingTask)));
        for (FormulaVariable variable : tgd.getExistentialFormulaVariables(mappingTask)) {
            List<Determination> implicantsForOccurrences = findMinimalDeterminationsForOccurrences(variable, tgd, mappingTask);
            Determination implicants = findUniqueDeterminationForVariable(variable, implicantsForOccurrences, tgd, mappingTask);
            result.addMinimalImplicants(variable, implicants);
            result.addMinimalImplicantsForOccurrences(variable, implicantsForOccurrences);
        }
        return result;
    }

    private List<Determination> findMinimalDeterminationsForOccurrences(FormulaVariable variable, FORule tgd, MappingTask mappingTask) {
        List<Determination> result = new ArrayList<Determination>();
        for (VariablePathExpression occurrence : variable.getTargetOccurrencePaths()) {
            List<Determination> determinations = findDeterminationsForOccurrence(occurrence, tgd, mappingTask);
            for (Determination determination : determinations) {
                minimize(determination, tgd, mappingTask);
            }
            result.addAll(determinations);
        }
        return result;
    }

    private List<Determination> findDeterminationsForOccurrence(VariablePathExpression occurrencePath, FORule tgd, MappingTask mappingTask) {
        List<Determination> result = new ArrayList<Determination>();
        SetAlias setVariable = occurrencePath.getStartingVariable();
        List<VariableFunctionalDependency> dependencies = findSimplifiedFunctionalDependenciesForSet(mappingTask.getTargetProxy(), setVariable);
        for (VariableFunctionalDependency functionalDependency : dependencies) {
            if (contains(functionalDependency.getRightPaths(), occurrencePath)
                    && allLeftAttributesAreUniversal(functionalDependency, tgd, mappingTask)) {
                List<FormulaVariable> universalVariablesForDependency = findUniversalVariablesForDependency(functionalDependency, tgd, mappingTask);
                Determination determination = new Determination(universalVariablesForDependency);
                determination.addGeneratingDependency(functionalDependency);
                result.add(determination);
            }
        }
        if (result.isEmpty()) {
            result.add(new Determination(tgd.getUniversalFormulaVariablesInTarget(mappingTask)));
        }
        return result;
    }

    private List<VariableFunctionalDependency> findSimplifiedFunctionalDependenciesForSet(IDataSourceProxy dataSource, SetAlias setVariable) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (VariableFunctionalDependency functionalDependency : dataSource.getMappingData().getSimplifiedDependencies()) {
            if (functionalDependency.getVariable().equalsOrIsClone(setVariable)) {
                result.add(new VariableFunctionalDependency(functionalDependency, setVariable));
            }
        }
        return result;
    }

    private boolean allLeftAttributesAreUniversal(VariableFunctionalDependency functionalDependency, FORule tgd, MappingTask mappingTask) {
        for (VariablePathExpression leftPath : functionalDependency.getLeftPaths()) {
            FormulaVariable pathUniversalVariable = findUniversalVariable(leftPath, tgd, mappingTask);
            if (pathUniversalVariable == null) {
                return false;
            }
        }
        return true;
    }

    private FormulaVariable findUniversalVariable(VariablePathExpression path, FORule tgd, MappingTask mappingTask) {
        for (FormulaVariable formulaVariable : tgd.getUniversalFormulaVariables(mappingTask)) {
            if (contains(formulaVariable.getTargetOccurrencePaths(), path)) {
                return formulaVariable;
            }
        }
        return null;
    }

    private List<FormulaVariable> findUniversalVariablesForDependency(VariableFunctionalDependency functionalDependency, FORule tgd, MappingTask mappingTask) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (VariablePathExpression leftPath : functionalDependency.getLeftPaths()) {
            FormulaVariable leftPathUniversalVariable = findUniversalVariable(leftPath, tgd, mappingTask);
            if (leftPathUniversalVariable == null) {
                throw new IllegalArgumentException("Unable to find universal variable for path " + leftPath + " in tgd " + tgd);
            }
            if (!result.contains(leftPathUniversalVariable)) {
                result.add(leftPathUniversalVariable);
            }
        }
        return result;
    }

    private Determination findUniqueDeterminationForVariable(FormulaVariable variable, List<Determination> determinations,
            FORule tgd, MappingTask mappingTask) {
        if (!variable.isExistential()) {
            throw new IllegalArgumentException("Implicants can be minimized for existential variables only " + variable);
        }
        if (logger.isDebugEnabled()) logger.debug("#### Searching unique determination for variable: " + variable + ". Determinations: " + determinations);
        Determination lowerBound = lowerBoundFinder.findLowerBound(determinations, tgd, mappingTask);
        if (lowerBound == null) {
            throw new IllegalArgumentException("Unable to find unique skolemization for variable " + variable + " - Candidate implicants:\n" + SpicyEngineUtility.printCollection(determinations) + " in tgd " + tgd + "\n" + tgd.toLogicalString(false, mappingTask));
        }
        return lowerBound;
    }

    private boolean contains(List<VariablePathExpression> pathList, VariablePathExpression pathExpression) {
        for (VariablePathExpression targetOccurrence : pathList) {
            if (targetOccurrence.equalsAndHasSameVariableId(pathExpression)) {
                return true;
            }
        }
        return false;
    }

    private void minimize(Determination determination, FORule tgd, MappingTask mappingTask) {
        if (determination.getImplicants().size() == 1) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("----------------Minimizing implicants " + determination + " in tgd " + tgd.toLogicalString(false, mappingTask));
        if (logger.isDebugEnabled()) logger.debug("Source dependencies" + mappingTask.getSourceProxy().getMappingData().getDependencies());
        boolean fixpoint;
        do {
            fixpoint = true;
            for (Iterator<FormulaVariable> it = determination.getImplicants().iterator(); it.hasNext();) {
                FormulaVariable variable = it.next();
                Determination clone = determination.clone();
                clone.getImplicants().remove(variable);
                Determination implicantSubset = new Determination(clone);
                if (implicationChecker.implies(implicantSubset, variable, tgd, mappingTask)) {
                    it.remove();
                    fixpoint = false;
                    break;
                }
            }
        } while (!fixpoint);
        if (logger.isDebugEnabled()) logger.debug("----------------Minimized implicants " + determination);
    }
}
