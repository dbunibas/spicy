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
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionOrderedPair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class RemoveCyclesInSubsumptions {

    private static Log logger = LogFactory.getLog(RemoveCyclesInSubsumptions.class);

    /////////////////////////   CYCLE REMOVAL FOR ST TGDS   //////////////////////////
    void removeCyclesInSTTGDSubsumptionMap(Map<FORule, List<Subsumption>> subsumptions) {
        //TODO: check cycle removal
        for (FORule tgd : subsumptions.keySet()) {
            List<Subsumption> fathers = subsumptions.get(tgd);
            for (Iterator<Subsumption> fatherIterator = fathers.iterator(); fatherIterator.hasNext();) {
                Subsumption father = fatherIterator.next();
                if (inverse(tgd, father.getRightTgd(), subsumptions)) {
                    if (tgd.compareTo(father.getRightTgd()) > 0) {
                        fatherIterator.remove();
                    }
                }
            }
        }
    }

    private boolean inverse(FORule tgd, FORule father, Map<FORule, List<Subsumption>> subsumptions) {
        List<Subsumption> fathers = subsumptions.get(father);
        if (fathers == null) {
            return false;
        }
        List<FORule> fatherTGD = getSTTGDs(fathers);
        return fatherTGD.contains(tgd);
    }

    private List<FORule> getSTTGDs(List<Subsumption> subsumptions) {
        List<FORule> result = new ArrayList<FORule>();
        for (Subsumption subsumption : subsumptions) {
            result.add(subsumption.getRightTgd());
        }
        return result;
    }

    ///////////////////////////   CYCLE REMOVAL FOR TT SUBSUMPTIONS   //////////////////////////
    void removeCyclesInTTExpansionSubsumptionMap(Map<Expansion, List<ExpansionOrderedPair>> subsumptions) {
        //TODO: check cycle removal
        for (Expansion expansion : subsumptions.keySet()) {
            List<ExpansionOrderedPair> fathers = subsumptions.get(expansion);
            for (Iterator<ExpansionOrderedPair> fatherIterator = fathers.iterator(); fatherIterator.hasNext();) {
                ExpansionOrderedPair father = fatherIterator.next();
                if (inverse(expansion, father.getRightHandSideExpansion(), subsumptions)) {
                    if (expansion.compareTo(father.getRightHandSideExpansion()) > 0) {
                        fatherIterator.remove();
                    }
                }
            }
        }
    }

    private boolean inverse(Expansion expansion, Expansion father, Map<Expansion, List<ExpansionOrderedPair>> subsumptions) {
        List<ExpansionOrderedPair> fathers = subsumptions.get(father);
        if (fathers == null) {
            return false;
        }
        List<Expansion> fathersOfFather = getSubsumptions(fathers);
        return fathersOfFather.contains(expansion);
    }

    private List<Expansion> getSubsumptions(List<ExpansionOrderedPair> subsumptions) {
        List<Expansion> result = new ArrayList<Expansion>();
        for (ExpansionOrderedPair subsumption : subsumptions) {
            result.add(subsumption.getRightHandSideExpansion());
        }
        return result;
    }

    /////////////////////////  CYCLE REMOVAL FOR SELF JOIN SUBSUMPTIONS   ////////////////////////////
    void removeCyclesInSelfJoinSubsumptionMap(Map<Expansion, List<ExpansionOrderedPair>> subsumptions) {
        //TODO: check cycle removal
        for (Expansion expansion : subsumptions.keySet()) {
            List<ExpansionOrderedPair> fathers = subsumptions.get(expansion);
            for (Iterator<ExpansionOrderedPair> fatherIterator = fathers.iterator(); fatherIterator.hasNext();) {
                ExpansionOrderedPair father = fatherIterator.next();
                if (inverse(expansion, father.getRightHandSideExpansion(), subsumptions)) {
                    if (moreCompactSelfJoin(expansion, father.getRightHandSideExpansion()) > 0) {
                        fatherIterator.remove();
                    }
                }
            }
        }
    }

    private int moreCompactSelfJoin(Expansion expansion, Expansion fatherExpansion) {
        if (expansion.getExpansionElements().size() != fatherExpansion.getExpansionElements().size()) {
            return fatherExpansion.getExpansionElements().size() - expansion.getExpansionElements().size();
        }
        return this.toString().compareTo(fatherExpansion.toString());
    }

}
