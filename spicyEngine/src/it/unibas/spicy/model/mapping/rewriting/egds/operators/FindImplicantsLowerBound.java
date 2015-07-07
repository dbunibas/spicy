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
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FindImplicantsLowerBound {

    private static Log logger = LogFactory.getLog(FindImplicantsLowerBound.class);
    private CheckImplication implicationChecker = new CheckImplication();

    Determination findLowerBound(List<Determination> determinations, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("*** Searching lower bounds for determinations: " + determinations);
        Determination currentLowerBound = determinations.get(0).clone();
        for (int i = 1; i < determinations.size(); i++) {
            Determination nextDetermination = determinations.get(i);
            currentLowerBound = findLowerBound(currentLowerBound, nextDetermination, tgd, mappingTask);
            if (currentLowerBound == null) {
                return null;
            }
        }
        return currentLowerBound;
    }

    private Determination findLowerBound(Determination determination, Determination otherDetermination, FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("*** Updating lower bound - Current lower bound: " + determination);
        if (logger.isDebugEnabled()) logger.debug("*** New determination to process: " + otherDetermination);
//        if (implicants.getSize() <= otherImplicants.getSize() &&
//                implicationChecker.implies(implicants, otherImplicants, tgd, mappingTask)) {
//            VariableImplicants clone = implicants.clone();
//            clone.addGeneratingDependencies(otherImplicants.getGeneratingDependencies());

//        if (implicationChecker.implies(otherImplicants, implicants, tgd, mappingTask)) {
//        if (determination.equals(otherDetermination)) {
//            if (logger.isDebugEnabled()) logger.debug("Same determination, returning");
//            return determination;
//        }
        if (implicationChecker.implies(determination, otherDetermination, tgd, mappingTask)) {
            Determination clone = otherDetermination.clone();
            if (clone.getGeneratingDependencies().isEmpty()) {
                clone.addGeneratingDependencies(determination.getGeneratingDependencies());
            }
            if (logger.isDebugEnabled()) logger.debug("New lower bound: " + clone);
            return clone;
        }
//        if (otherImplicants.getSize() <= implicants.getSize() &&
//                implicationChecker.implies(otherImplicants, implicants, tgd, mappingTask)) {
//            VariableImplicants clone = otherImplicants.clone();
//            clone.addGeneratingDependencies(implicants.getGeneratingDependencies());

//        if (implicationChecker.implies(implicants, otherImplicants, tgd, mappingTask)) {
        if (implicationChecker.implies(otherDetermination, determination, tgd, mappingTask)) {
            Determination clone = determination.clone();
            if (clone.getGeneratingDependencies().isEmpty()) {
                clone.addGeneratingDependencies(otherDetermination.getGeneratingDependencies());
            }
            if (logger.isDebugEnabled()) logger.debug("New lower bound: " + clone);
            return clone;
        }
        if (logger.isDebugEnabled()) logger.debug("No lower bound found, returning null...");
        return null;
    }
}
