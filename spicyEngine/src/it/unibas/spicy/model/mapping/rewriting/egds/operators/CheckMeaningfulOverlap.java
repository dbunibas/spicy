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
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class CheckMeaningfulOverlap {

    private static Log logger = LogFactory.getLog(CheckMeaningfulOverlap.class);
    private static final int NULL = 0;
    private static final int SKOLEM = 1;
    private static final int UNIVERSAL = 2;

    boolean isMeaningful(FORule tgd1, FORule tgd2, FORule overlapTgd, Map<Integer, SetAlias> variableReplacements, MappingTask mappingTask) {
//        if (SpicyEngineUtility.hasSelfJoins(tgd1) || SpicyEngineUtility.hasSelfJoins(tgd2)) {
//            return true;
//        }
        List<FormulaAtom> atomsInTgd1 = tgd1.getTargetFormulaAtoms(mappingTask);
        List<FormulaAtom> atomsInTgd2 = tgd2.getTargetFormulaAtoms(mappingTask);
        List<FormulaAtom> atomsInOverlapTgd = overlapTgd.getTargetFormulaAtoms(mappingTask);
        boolean changeOfSortInTgd1 = checkPositions(atomsInTgd1, atomsInOverlapTgd, variableReplacements);
        boolean changeOfSortInTgd2 = checkPositions(atomsInTgd2, atomsInOverlapTgd, variableReplacements);
        return changeOfSortInTgd1 || changeOfSortInTgd2;
    }

    private boolean checkPositions(List<FormulaAtom> originalAtoms, List<FormulaAtom> overlapAtoms, Map<Integer, SetAlias> variableReplacements) {
        for (FormulaAtom originalAtom : originalAtoms) {
            FormulaAtom overlapAtom = findOverlapAtom(originalAtom, overlapAtoms, variableReplacements);
            for (FormulaPosition originalPosition : originalAtom.getPositions()) {
                FormulaPosition overlapPosition = findOverlapPosition(originalPosition, overlapAtom);
                int originalSort = findSort(originalPosition);
                int newSort = findSort(overlapPosition);
                if (originalSort != newSort) {
                    return true;
                }
            }
        }
        return false;
    }

    private FormulaAtom findOverlapAtom(FormulaAtom originalAtom, List<FormulaAtom> overlapAtoms, Map<Integer, SetAlias> variableReplacements) {
        SetAlias newVariable = findRenamedVariable(originalAtom.getVariable(), variableReplacements);
        for (FormulaAtom overlapAtom : overlapAtoms) {
            for (SetAlias generator : overlapAtom.getVariable().getGenerators()) {
                if (generator.hasSameId(newVariable)) {
                    return overlapAtom;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find overlap atom for original atom:\n" + originalAtom + "\n---Overlap atoms:\n" + SpicyEngineUtility.printCollection(overlapAtoms) + "\n---Variable replacements:\n" + SpicyEngineUtility.printMap(variableReplacements));
    }

    private FormulaPosition findOverlapPosition(FormulaPosition originalPosition, FormulaAtom overlapAtom) {
        for (FormulaPosition overlapPosition : overlapAtom.getPositions()) {
            if (areEqualUpToClones(overlapPosition, originalPosition)) {
                return overlapPosition;
            }
        }
        throw new IllegalArgumentException("Unable to find position " + originalPosition + "\nin overlap atom" + overlapAtom);
    }

    private SetAlias findRenamedVariable(SetAlias originalVariable, Map<Integer, SetAlias> variableReplacements) {
        SetAlias newVariable = variableReplacements.get(originalVariable.getId());
        if (newVariable == null) {
            newVariable = originalVariable;
        }
        return newVariable;
    }

    private int findSort(FormulaPosition position) {
        if (position.isUniversal()) {
            return UNIVERSAL;
        }
        if (position.isSkolem()) {
            return SKOLEM;
        }
        return NULL;
    }

    private boolean areEqualUpToClones(FormulaPosition overlapPosition, FormulaPosition originalPosition) {
        PathExpression originalPath = SpicyEngineUtility.removeClonesFromAbsolutePath(originalPosition.getPathExpression().getAbsolutePath());
        PathExpression overlapPath = SpicyEngineUtility.removeClonesFromAbsolutePath(overlapPosition.getPathExpression().getAbsolutePath());
        return originalPath.equals(overlapPath);
    }

}
