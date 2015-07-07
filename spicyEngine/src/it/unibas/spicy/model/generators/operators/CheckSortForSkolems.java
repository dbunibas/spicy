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
 
package it.unibas.spicy.model.generators.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionOrderedPair;
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.operators.CheckExpansionHomomorphism;
import it.unibas.spicy.utility.GenericCombinationsGenerator;
import it.unibas.spicy.utility.GenericPermutationsGenerator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckSortForSkolems {

    private static Log logger = LogFactory.getLog(CheckSortForSkolems.class);

    public boolean needsRuntimeSortInSkolems(MappingTask mappingTask) {
        if (!mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
            return false;
        }
        return true;
//        return existsTgdWithMutuallyHomomorphicAtoms(mappingTask);
    }

    //////////////////      MUTUALLY HOMOMORPHIC ATOMS IN SINGLE EXPANSION    //////////////////////////
    private boolean existsTgdWithMutuallyHomomorphicAtoms(MappingTask mappingTask) {
        for (FORule tgd : mappingTask.getMappingData().getRewrittenRules()) {
            if (tgd.getTargetView().getVariables().size() == 1) {
                continue;
            }
            if (hasMutuallyHomomorphicAtoms(tgd, mappingTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMutuallyHomomorphicAtoms(FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Analyzing tgd:\n" + tgd);
        List<FormulaAtom> tgdAtoms = tgd.getTargetFormulaAtoms(mappingTask);
        for (FormulaAtom atom : tgdAtoms) {
            if (existsMutuallyHomomorphicAtom(atom, tgdAtoms)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsMutuallyHomomorphicAtom(FormulaAtom atom, List<FormulaAtom> tgdAtoms) {
        for (FormulaAtom otherAtom : tgdAtoms) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing atoms:\n" + atom + "\n" + otherAtom);
            if (otherAtom.getVariable().hasSameId(atom.getVariable())) {
                if (logger.isDebugEnabled()) logger.debug("Atoms are equal, skipping...");
                continue;
            }
            if (checkPositions(atom, otherAtom)) {
                if (logger.isDebugEnabled()) logger.debug("Mapping task has mutually homomorphic atoms:\n" + atom + "\n" + otherAtom);
                return true;
            }
        }
        return false;
    }

    private boolean checkPositions(FormulaAtom atom, FormulaAtom otherAtom) {
        if (!otherAtom.getVariable().isClone(atom.getVariable())) {
            if (logger.isDebugEnabled()) logger.debug("Variables are not clone, skipping...");
            return false;
        }
        for (int i = 0; i < atom.getPositions().size(); i++) {
            FormulaPosition position = atom.getPositions().get(i);
            FormulaPosition otherPosition = otherAtom.getPositions().get(i);
            if (!areEquallyInformative(position, otherPosition)) {
                return false;
            }
        }
        return true;
    }

    private boolean areEquallyInformative(FormulaPosition position, FormulaPosition otherPosition) {
        if (logger.isDebugEnabled()) logger.debug("Analyzing positions: " + position + " - " + otherPosition);
        if (position.isUniversal() || otherPosition.isUniversal()) {
            return position.isUniversal() && otherPosition.isUniversal();
        } else {
            return true;
        }
//        if (position.isSkolem() && !position.isUniversal() && otherPosition.isSkolem() && !otherPosition.isUniversal()) {
//            return true;
//        }
//        if (position.isNull() && otherPosition.isNull()) {
//            return true;
//        }
//        return false;
    }

    //////////////////      EXPANSIONS WITH MUTUAL HOMOMORPHISMS    //////////////////////////
    private boolean existMutualHomomorphisms(MappingTask mappingTask) {
        for (Expansion expansion : mappingTask.getMappingData().getExpansions()) {
            if (expansion.getExpansionElements().size() == 1) {
                continue;
            }
            if (!hasSelfJoins(expansion)) {
                continue;
            }
            if (existsMutualHomomorphicExpansion(expansion, mappingTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSelfJoins(Expansion expansion) {
        for (ExpansionElement atom : expansion.getExpansionElements()) {
            if (existsClone(atom, expansion)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsClone(ExpansionElement atom, Expansion expansion) {
        for (ExpansionElement otherAtom : expansion.getExpansionElements()) {
            if (atom.equals(otherAtom)) {
                continue;
            }
            if (atom.getCoveringAtom().getVariable().equalsOrIsClone(otherAtom.getCoveringAtom().getVariable())) {
                return true;
            }
        }
        return false;
    }

    private boolean existsMutualHomomorphicExpansion(Expansion expansion, MappingTask mappingTask) {
        for (Expansion otherExpansion : mappingTask.getMappingData().getExpansions()) {
            if (otherExpansion.equals(expansion)) {
                continue;
            }
            if (subsumes(otherExpansion, expansion) && subsumes(expansion, otherExpansion)) {
                if (differentOrdersInAtoms(otherExpansion, expansion)) {
                    if (logger.isDebugEnabled()) logger.debug("Mapping task has mutually homomorphic expansions:\n" + expansion + "\n" + otherExpansion);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean subsumes(Expansion fatherExpansion, Expansion expansion) {
        if (expansion.equals(fatherExpansion)) {
            throw new IllegalArgumentException("Cannot check subsumption of expansion by itself: " + expansion);
        }
        CheckExpansionHomomorphism subsumptionChecker = new CheckExpansionHomomorphism();
        List<ExpansionElement> expansionAtoms = expansion.getExpansionElements();
        List<ExpansionElement> fatherExpansionAtoms = fatherExpansion.getExpansionElements();
        if (expansionAtoms.size() > fatherExpansionAtoms.size()) {
            return false;
        }
        GenericCombinationsGenerator<ExpansionElement> combinationsGenerator = new GenericCombinationsGenerator<ExpansionElement>(fatherExpansionAtoms, expansionAtoms.size());
        while (combinationsGenerator.hasMoreElements()) {
            List<ExpansionElement> combination = combinationsGenerator.nextElement();
            GenericPermutationsGenerator<ExpansionElement> permutationsGenerator = new GenericPermutationsGenerator<ExpansionElement>(combination);
            while (permutationsGenerator.hasMoreElements()) {
                List<ExpansionElement> permutation = permutationsGenerator.nextElement();
                ExpansionOrderedPair subsumption = subsumptionChecker.checkHomomorphism(expansion, expansionAtoms, fatherExpansion, permutation);
                if (subsumption != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean differentOrdersInAtoms(Expansion otherExpansion, Expansion expansion) {
        for (int i = 0; i < expansion.getExpansionElements().size(); i++) {
            ExpansionElement atom = expansion.getExpansionElements().get(i);
            ExpansionElement otherAtom = otherExpansion.getExpansionElements().get(i);
            if (!checkPositions(atom.getCoveringAtom(), otherAtom.getCoveringAtom())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPositions(ExpansionAtom atom, ExpansionAtom otherAtom) {
        for (int i = 0; i < atom.getPositions().size(); i++) {
            ExpansionFormulaPosition position = atom.getPositions().get(i);
            ExpansionFormulaPosition otherPosition = otherAtom.getPositions().get(i);
            if (!areEquallyInformative(position, otherPosition)) {
                return false;
            }
        }
        return true;
    }

    private boolean areEquallyInformative(ExpansionFormulaPosition position, ExpansionFormulaPosition otherPosition) {
        if (position.isUniversal() || otherPosition.isUniversal()) {
            return position.isUniversal() && otherPosition.isUniversal();
        } else {
            return true;
        }
//        if (position.isUniversal() && otherPosition.isUniversal()) {
//            return true;
//        }
//        if (position.isSkolem() && otherPosition.isSkolem()) {
//            return true;
//        }
//        if (position.isNull() && otherPosition.isNull()) {
//            return true;
//        }
//        return false;
    }
}
