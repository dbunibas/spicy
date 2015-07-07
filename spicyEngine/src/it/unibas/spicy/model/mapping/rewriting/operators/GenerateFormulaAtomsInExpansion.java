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
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.ExpansionBaseView;
import it.unibas.spicy.model.mapping.rewriting.ExpansionAtom;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class GenerateFormulaAtomsInExpansion {

    public List<ExpansionAtom> generateExpansionFormulaAtoms(ExpansionBaseView baseView, MappingTask mappingTask) {
        List<ExpansionAtom> result = new ArrayList<ExpansionAtom>();
//        for (SetAlias variable : baseView.getView().getGenerators()) {
        for (SetAlias variable : baseView.getView().getVariables()) {
            ExpansionAtom atom = new ExpansionAtom(variable, baseView.getStTgd());
            findPositionsInExpansionFormulaAtom(atom, mappingTask);
            result.add(atom);
        }
        return result;
    }

    private void findPositionsInExpansionFormulaAtom(ExpansionAtom atom, MappingTask mappingTask) {
        List<ExpansionFormulaPosition> positions = new ArrayList<ExpansionFormulaPosition>();
        List<VariablePathExpression> variablePaths = atom.getVariable().getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        for (VariablePathExpression variablePath : variablePaths) {
            ExpansionFormulaPosition position = new ExpansionFormulaPosition(variablePath);
            positions.add(position);
            checkIfPositionIsUniversal(position, atom.getTgd());
            checkIfPositionIsSkolem(position, atom.getTgd());
        }
        atom.setPositions(positions);
    }

    private void checkIfPositionIsUniversal(ExpansionFormulaPosition position, FORule tgd) {
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equals(position.getPathExpression())) {
                position.setUniversal(true);
//                position.setImplied(correspondence.isImplied());
            }
        }
    }

    private void checkIfPositionIsSkolem(ExpansionFormulaPosition position, FORule tgd) {
        for (VariableJoinCondition targetJoin : tgd.getTargetView().getAllJoinConditions()) {
            if (targetJoin.getFromPaths().contains(position.getPathExpression()) ||
                    targetJoin.getToPaths().contains(position.getPathExpression())) {
                position.setSkolem(true);
            }
        }
    }

}
