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
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.exceptions.UnsatisfiableEGDException;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDImplicants;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckEGDSatisfiability {

    private static Log logger = LogFactory.getLog(CheckEGDSatisfiability.class);

    public void checkEGDSatisfiability(MappingTask mappingTask) throws UnsatisfiableEGDException {
        for (ValueCorrespondence correspondence : mappingTask.getValueCorrespondences()) {
            if (correspondence.getSourcePaths().size() > 1) {
                throw new UnsatisfiableEGDException("Unable to enforce EGDs. Mapping task contains n:1 correspondences: " + correspondence);
            }
        }
        checkExistentialVariableImplicants(mappingTask);
        checkUniversalVariableDependencies(mappingTask);
    }

    private void checkExistentialVariableImplicants(MappingTask mappingTask) throws UnsatisfiableEGDException {
        FindMinimalImplicants implicantsFinder = new FindMinimalImplicants();
        for (FORule tgd : mappingTask.getMappingData().getCandidateSTTgds()) {
            TGDImplicants implicantsMap = implicantsFinder.findMinimalImplicants(tgd, mappingTask);
            if (!implicantsMap.isEmpty()) {
                if (!implicantsMap.allClear()) {
                    throw new UnsatisfiableEGDException("Unable to satisfy EGDs in TGD \n" + tgd.toLogicalString(mappingTask) + "Implicants map :" + implicantsMap.toLongString());
                }
            }
        }
//        if (mappingTask.getMappingData().hasSelfJoins()) {
//            for (TargetTGD tgd : mappingTask.getMappingData().getTTTgds()) {
//                TGDImplicantsMap implicantsMap = tgd.getImplicantsMap(mappingTask);
//                if (!implicantsMap.isEmpty()) {
//                    if (!implicantsMap.allClear()) {
//                        throw new UnsatisfiableEGDException("Unable to satisfy EGDs in TGD \n" + tgd.toLogicalString(mappingTask) + "Implicants map :" + implicantsMap.toLongString());
//                    }
//                }
//            }
//        }
    }

    private void checkUniversalVariableDependencies(MappingTask mappingTask) {
        for (FORule tgd : mappingTask.getMappingData().getCandidateSTTgds()) {
            List<VariableFunctionalDependency> targetDependencies = findTargetDependencies(tgd, mappingTask);
            for (VariableFunctionalDependency targetDependency : targetDependencies) {
                checkDependency(targetDependency, tgd, mappingTask);
            }
        }

    }

    private List<VariableFunctionalDependency> findTargetDependencies(FORule tgd, MappingTask mappingTask) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (SetAlias targetVariable : tgd.getTargetView().getGenerators()) {
            List<VariableFunctionalDependency> keyConstraintsForVariable = EGDUtility.findKeyConstraintsForSet(mappingTask.getTargetProxy(), targetVariable);
            result.addAll(keyConstraintsForVariable);
        }
        return result;
    }

    private void checkDependency(VariableFunctionalDependency targetDependency, FORule tgd, MappingTask mappingTask) {
        List<VariableFunctionalDependency> simpleDependencies = EGDUtility.generateSimpleDependencies(targetDependency);
        for (VariableFunctionalDependency simpleDependency : simpleDependencies) {
            if (EGDUtility.findUniversalVariablesForTargetPaths(simpleDependency.getLeftPaths(), tgd, mappingTask) == null ||
                    EGDUtility.findUniversalVariablesForTargetPaths(simpleDependency.getRightPaths(), tgd, mappingTask) == null) {
                continue;
            }
            if (isTrivial(simpleDependency)) {
                continue;
            }
            //TODO: EGD check if this is actually needed
//            if (!findMatchingInducedDependency(simpleDependency, tgd, mappingTask)) {
//                throw new UnsatisfiableEGDException("Mapping task might violate EGD " + simpleDependency + " in tgd " + tgd.toLogicalString(mappingTask));
//            }
        }
    }

    private boolean isTrivial(VariableFunctionalDependency simpleDependency) {
        return simpleDependency.getLeftPaths().containsAll(simpleDependency.getRightPaths());
    }
}



