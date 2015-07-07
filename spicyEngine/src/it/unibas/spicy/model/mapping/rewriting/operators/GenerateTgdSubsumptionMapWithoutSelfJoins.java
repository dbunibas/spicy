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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateTgdSubsumptionMapWithoutSelfJoins {

    private static Log logger = LogFactory.getLog(GenerateTgdSubsumptionMapWithoutSelfJoins.class);

    private CheckTGDHomomorphismWithoutSelfJoins subsumptionChecker = new CheckTGDHomomorphismWithoutSelfJoins();

    public SubsumptionMap generateSTSubsumptionMap(List<FORule> tgds, MappingTask mappingTask) {
        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
            throw new IllegalArgumentException("Unable to find subsumptions for mapping tasks with self joins in conclusions");
        }
        if (logger.isTraceEnabled()) logger.trace("*************************Candidate TGDs: " + SpicyEngineUtility.printCollection(tgds));
        Map<FORule, List<Subsumption>> subsumptions = generateSubsumptions(tgds, mappingTask);
        if (logger.isTraceEnabled()) logger.trace("*************************Full subsumptions: " + SpicyEngineUtility.printMap(subsumptions));
//        removeFullySubsumedTGDs(subsumptions);
//        if (logger.isTraceEnabled()) logger.trace("*************************Full subsumptions after removal: " + SpicyEngineUtility.printMap(subsumptions));
//        if (!mappingTask.getConfig().rewriteOnlyProperHomomorphisms()) {
//            new RemoveCyclesInSubsumptions().removeCyclesInSTTGDSubsumptionMap(subsumptions);
//        }
        if (logger.isTraceEnabled()) logger.trace("*************************Full subsumptions after cycle removal: " + SpicyEngineUtility.printMap(subsumptions));
        SubsumptionMap subsumptionMap = new SubsumptionMap(mappingTask, subsumptions);
        return subsumptionMap;
    }

    private Map<FORule, List<Subsumption>> generateSubsumptions(List<FORule> candidateTgds, MappingTask mappingTask) {
        Map<FORule, List<Subsumption>> subsumptionMap = new HashMap<FORule, List<Subsumption>>();
        for (FORule candidatetgd : candidateTgds) {
            List<Subsumption> ancestors = new ArrayList<Subsumption>();
            subsumptionMap.put(candidatetgd, ancestors);
            for (FORule othertgd : candidateTgds) {
                if (!candidatetgd.equals(othertgd)) {
                    Subsumption subsumption = subsumptionChecker.checkHomomorphismWithoutSelfJoins(othertgd, candidatetgd, mappingTask.getTargetProxy(), mappingTask);
                    if (subsumption != null) {
//                            if (!mappingTask.getConfig().rewriteOnlyProperHomomorphisms() ||
//                                    properSubsumptionChecker.checkProperHomomorphism(candidatetgd, othertgd, mappingTask)) {
                        if (subsumptionChecker.isProperHomomorphism(candidatetgd, othertgd, mappingTask)) {
                            ancestors.add(subsumption);
                        }
                    }
                }
            }
        }
        return subsumptionMap;
    }
//    /////////////////////////   FULLY SUBSUMED TGD REMOVAL   //////////////////////////
//    private void removeFullySubsumedTGDs(Map<TGD, List<STTGDSubsumption>> subsumptions) {
//        List<TGD> toRemove = new ArrayList<TGD>();
//        for (TGD tgd : subsumptions.keySet()) {
//            List<STTGDSubsumption> fathers = subsumptions.get(tgd);
//            TGD fatherWithEqualSourceView = existsFatherWithEqualSourceView(tgd, fathers, toRemove);
//            if (fatherWithEqualSourceView != null) {
//                logger.info("Found tgd to remove: " + tgd + "\nfather tgd: " + fatherWithEqualSourceView);
//                toRemove.add(tgd);
//            }
//        }
//        for (TGD tgdToRemove : toRemove) {
//            subsumptions.remove(tgdToRemove);
//        }
//        for (TGD tgd : subsumptions.keySet()) {
//            List<STTGDSubsumption> tgdSubsumptions = subsumptions.get(tgd);
//            for (Iterator<STTGDSubsumption> it = tgdSubsumptions.iterator(); it.hasNext();) {
//                if (toRemove.contains(it.next().getTgd())) {
//                    it.remove();
//                }
//            }
//        }
//    }
//
//    private TGD existsFatherWithEqualSourceView(TGD tgd, List<STTGDSubsumption> fathers, List<TGD> tgdsToRemove) {
//        for (TGD fatherTGD : getTGDs(fathers)) {
//            if (fatherTGD.getSourceView().equals(tgd.getSourceView()) && !tgdsToRemove.contains(fatherTGD)) {
//                return fatherTGD;
//            }
//        }
//        return null;
//    }
//
//    private List<TGD> getTGDs(List<STTGDSubsumption> subsumptions) {
//        List<TGD> result = new ArrayList<TGD>();
//        for (STTGDSubsumption subsumption : subsumptions) {
//            result.add(subsumption.getTgd());
//        }
//        return result;
//    }
}
