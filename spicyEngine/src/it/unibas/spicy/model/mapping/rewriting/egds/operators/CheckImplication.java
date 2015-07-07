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
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class CheckImplication {

    private static Log logger = LogFactory.getLog(CheckImplication.class);

    //private static Map<Determination, Determination> cache = new HashMap<Determination, Determination>();

    boolean implies(Determination implicants, FormulaVariable implicant, FORule tgd, MappingTask mappingTask) {
        Determination singleton = new Determination(implicant);
        return implies(implicants, singleton, tgd, mappingTask);
    }

    boolean implies(Determination determination, Determination otherDetermination, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("##### Checking implication of determination: " + otherDetermination + " by " + determination);
        if (determination.getImplicants().isEmpty()) {
            return false;
        }
        if (otherDetermination.getImplicants().isEmpty()) {
            return true;
        }
        Determination closure = computeClosure(determination, tgd, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("##### Closure of determination: " + closure);
        if (closure.contains(otherDetermination)) {
            if (logger.isDebugEnabled()) logger.debug("##### Result: " + true);
            return true;
        } else {
            if (logger.isDebugEnabled()) logger.debug("##### Result: " + false);
            return false;
        }
    }

    private Determination computeClosure(Determination implicants, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("******************* Computing clusure of implicants: " + implicants + "\nin tgd\n" + tgd.toLogicalString(mappingTask));
        //Determination closure = cache.get(implicants);
        Determination closure = null;
        if (closure == null) {
            List<VariableFunctionalDependency> functionalDependencies = mappingTask.getSourceProxy().getMappingData().getDependencies();
            if (logger.isDebugEnabled()) logger.debug("Relevant functional dependencies:\n" + SpicyEngineUtility.printCollection(functionalDependencies));
            closure = new Determination(implicants);
            boolean fixpoint;
            do {
                if (logger.isDebugEnabled()) logger.debug("New fixpoint iteration. Current result: " + closure);
                fixpoint = true;
                for (VariableFunctionalDependency functionalDependency : functionalDependencies) {
                    List<VariableFunctionalDependency> allDependencies = findDependenciesForClones(functionalDependency, tgd);
                    for (VariableFunctionalDependency dependency : allDependencies) {
                        if (logger.isDebugEnabled()) logger.debug("**** Analyzing dependency: " + functionalDependency);
                        Determination leftVariables = findOriginalUniversalVariablesForSourcePaths(dependency.getLeftPaths(), tgd, mappingTask);
                        Determination rightVariables = findOriginalUniversalVariablesForSourcePaths(dependency.getRightPaths(), tgd, mappingTask);
                        if (logger.isDebugEnabled()) logger.debug("Left variables: " + leftVariables);
                        if (logger.isDebugEnabled()) logger.debug("Right variables: " + rightVariables);
                        if (leftVariables == null || rightVariables == null) {
                            continue;
                        }
                        if (closure.contains(leftVariables) && !closure.contains(rightVariables)) {
                            closure.addAllImplicants(rightVariables);
                            fixpoint = false;
                            if (logger.isDebugEnabled()) logger.debug("**** New closure: " + closure);
                        }
                    }
                }
            } while (!fixpoint);
//            cache.put(implicants, closure);
            if (logger.isDebugEnabled()) logger.debug("******************* Final result: " + closure);
        }
        return closure;
    }

    private List<VariableFunctionalDependency> findDependenciesForClones(VariableFunctionalDependency functionalDependency, FORule tgd) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (SetAlias sourceVariable : tgd.getComplexSourceQuery().getGenerators()) {
            if (functionalDependency.getVariable().equalsOrIsClone(sourceVariable)) {
                VariableFunctionalDependency newDependency = new VariableFunctionalDependency(functionalDependency, sourceVariable);
                result.add(newDependency);
            }
        }
        return result;
    }

    private Determination findOriginalUniversalVariablesForSourcePaths(List<VariablePathExpression> paths, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Searching variables for paths:\n" + SpicyEngineUtility.printCollection(paths) + "\nin tgd\n" + tgd.toLogicalString(mappingTask));
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (VariablePathExpression path : paths) {
            FormulaVariable variable = findOriginalUniversalVariableForSourcePath(path, tgd, mappingTask);
            if (variable == null) {
                return null;
            }
            result.add(variable);
        }
        return new Determination(result);
    }

    private FormulaVariable findOriginalUniversalVariableForSourcePath(VariablePathExpression path, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Searching variables for path: " + path + "\nin tgd\n" + tgd.toLogicalString(mappingTask));
        for (FormulaVariable formulaVariable : tgd.getUniversalFormulaVariables(mappingTask)) {
            if (logger.isDebugEnabled()) logger.debug("Examinining variable: " + formulaVariable.toLongString());
            if (formulaVariable.getOriginalSourceOccurrencePaths().contains(path)) {
                if (logger.isDebugEnabled()) logger.debug("Variable found...");
                return formulaVariable;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variable not found, returning null...");
        return null;
    }

}
