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
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.operators.CheckTGDHomomorphism;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MinimizeTGDs {

    private static Log logger = LogFactory.getLog(MinimizeTGDs.class);
    private GenerateCandidateSTTGDs tgdGenerator = new GenerateCandidateSTTGDs();
    private CheckTGDHomomorphism subsumptionChecker = new CheckTGDHomomorphism();

    public List<FORule> generateAndMinimizeTGDs(MappingTask mappingTask) {
        List<FORule> tgds = tgdGenerator.generateCandidateTGDs(mappingTask);
        if (logger.isDebugEnabled()) logger.debug("*************************Candidate TGDs: " + tgds.size() + "\n" + SpicyEngineUtility.printCollection(tgds));
        removeEquivalentMappings(tgds);
        if (logger.isDebugEnabled()) logger.debug("*************************After equivalence removals: " + tgds.size() + "\n" + SpicyEngineUtility.printCollection(tgds));
        removeFullySubsumedTGDs(tgds, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("*************************After full subsumption removals: " + tgds.size() + "\n" + SpicyEngineUtility.printCollection(tgds));
        return tgds;
    }

    private void removeEquivalentMappings(List<FORule> candidateTGDs) {
        Map<List<VariableCorrespondence>, List<FORule>> equivalenceClasses = findEquivalenceClasses(candidateTGDs);
        for (List<FORule> equivalenceClass : equivalenceClasses.values()) {
            Collections.sort(equivalenceClass);
            for (int i = 1; i < equivalenceClass.size(); i++) {
                candidateTGDs.remove(equivalenceClass.get(i));
            }
        }
    }

    private Map<List<VariableCorrespondence>, List<FORule>> findEquivalenceClasses(List<FORule> candidateTGDs) {
        Map<List<VariableCorrespondence>, List<FORule>> equivalenceClasses = new HashMap<List<VariableCorrespondence>, List<FORule>>();
        for (FORule tgd : candidateTGDs) {
//            if (SpicyEngineUtility.hasSelfJoins(tgd)) {
//                continue;
//            }
            List<FORule> equivalenceClass = equivalenceClasses.get(tgd.getCoveredCorrespondences());
            if (equivalenceClass == null) {
                equivalenceClass = new ArrayList<FORule>();
                equivalenceClasses.put(tgd.getCoveredCorrespondences(), equivalenceClass);
            }
            equivalenceClass.add(tgd);
        }
        if (logger.isTraceEnabled()) logger.trace("Equivalence classes: " + equivalenceClasses);
        return equivalenceClasses;
    }

    private void removeFullySubsumedTGDs(List<FORule> tgds, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.trace("*************Removing fully subsumed tgds from tgd list: " + SpicyEngineUtility.printCollection(tgds));
        for (Iterator<FORule> it = tgds.iterator(); it.hasNext();) {
            FORule tgd = it.next();
            FORule fatherTGD = existsFatherWithEqualSourceView(tgd, tgds, mappingTask);
            if (fatherTGD != null) {
                if (logger.isDebugEnabled()) logger.trace("Removing tgd: " + tgd + "because of father: " + fatherTGD);
                it.remove();
            }
        }
    }

    private FORule existsFatherWithEqualSourceView(FORule tgd, List<FORule> tgds, MappingTask mappingTask) {
        for (FORule otherTGD : tgds) {
            if (SpicyEngineUtility.hasSelfJoins(tgd) || SpicyEngineUtility.hasSelfJoins(otherTGD)) {
                continue;
            }
            if (tgd.equals(otherTGD)) {
                continue;
            }
            if (otherTGD.getSimpleSourceView().equals(tgd.getSimpleSourceView())
                    && subsumptionChecker.subsumes(otherTGD, tgd, mappingTask.getTargetProxy(), mappingTask)) {
                return otherTGD;
            }
        }
        return null;
    }
}
