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
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class GenerateTgdFormulaAtoms {

    public List<FormulaAtom> generateFormulaAtoms(FORule tgd, SimpleConjunctiveQuery view, MappingTask mappingTask) {
        List<FormulaAtom> result = new ArrayList<FormulaAtom>();
//        for (SetAlias variable : view.getGenerators()) {
        for (SetAlias variable : view.getVariables()) {
            FormulaAtom atom = new FormulaAtom(variable, tgd);
            findPositionsInSTFormulaAtom(atom, view, mappingTask);
            result.add(atom);
        }
        return result;
    }

    private void findPositionsInSTFormulaAtom(FormulaAtom atom, SimpleConjunctiveQuery view, MappingTask mappingTask) {
        List<FormulaPosition> positions = new ArrayList<FormulaPosition>();
        List<VariablePathExpression> variablePaths = atom.getVariable().getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        for (VariablePathExpression variablePath : variablePaths) {
            FormulaPosition position = new FormulaPosition(atom, variablePath);
            positions.add(position);
            checkIfSTPositionIsUniversal(position);
            checkIfSTPositionIsSkolem(position, view);
        }
        atom.setPositions(positions);
    }

    private void checkIfSTPositionIsUniversal(FormulaPosition position) {
        for (VariableCorrespondence correspondence : position.getAtom().getTgd().getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equals(position.getPathExpression())) {
                position.setCorrespondence(correspondence);
            }
        }
    }

    private void checkIfSTPositionIsSkolem(FormulaPosition position, SimpleConjunctiveQuery view) {
        for (VariableJoinCondition targetJoin : view.getAllJoinConditions()) {
            if (targetJoin.getFromPaths().contains(position.getPathExpression()) ||
                    targetJoin.getToPaths().contains(position.getPathExpression())) {
                position.addJoinCondition(targetJoin);
            }
        }
    }

}
