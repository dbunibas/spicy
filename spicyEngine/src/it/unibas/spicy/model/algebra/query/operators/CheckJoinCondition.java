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
 
package it.unibas.spicy.model.algebra.query.operators;

import it.unibas.spicy.model.mapping.rewriting.ExpansionElement;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionFormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.ExpansionJoin;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class CheckJoinCondition {

    public boolean allPathsInJoinAreSkolems(VariableJoinCondition joinCondition, Expansion expansion) {
        ExpansionElement fromAtom = findAtomFromVariable(joinCondition.getFromVariable(), expansion);
        for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
            if (isUniversal(fromPath, fromAtom)) {
                return false;
            }
        }
        return true;
    }

    public boolean existsSkolemPathInJoin(VariableJoinCondition joinCondition, Expansion expansion) {
        ExpansionElement fromAtom = findAtomFromVariable(joinCondition.getFromVariable(), expansion);
        for (VariablePathExpression fromPath : joinCondition.getFromPaths()) {
            if (!isUniversal(fromPath, fromAtom)) {
                return true;
            }
        }
        return false;
    }

    public ExpansionElement findAtomFromVariable(SetAlias variable, Expansion expansion) {
        for (ExpansionElement coveringAtom : expansion.getExpansionElements()) {
            if (coveringAtom.getCoveringAtom().getVariable().hasSameId(variable)) {
                return coveringAtom;
            }
        }
        throw new IllegalArgumentException("Unable to find atom for variable " + variable + " in expansion " + expansion);
    }

    private boolean isUniversal(VariablePathExpression pathExpression, ExpansionElement coveringAtom) {
        for (ExpansionFormulaPosition position : coveringAtom.getCoveringAtom().getPositions()) {
            if (position.getPathExpression().equals(pathExpression)) {
                return position.isUniversal();
            }
        }
        throw new IllegalArgumentException("Unable to find position for path " + pathExpression + " in atom " + coveringAtom);
    }

    public boolean joinPathIsSkolem(VariablePathExpression joinPath, Expansion expansion) {
        SetAlias pathVariable = joinPath.getStartingVariable();
        ExpansionElement atom = findAtomFromVariable(pathVariable, expansion);
        return isUniversal(joinPath, atom);
    }

    public List<TgdWithAtoms> groupAtoms(Expansion expansion) {
        List<TgdWithAtoms> result = new ArrayList<TgdWithAtoms>();
        ExpansionElement firstAtom = expansion.getExpansionElements().get(0);
        TgdWithAtoms firstTgdWithAtoms = new TgdWithAtoms(firstAtom);
        result.add(firstTgdWithAtoms);
        for (int i = 0; i < expansion.getJoinConditions().size(); i++) {
            ExpansionJoin expansionJoin = expansion.getJoinConditions().get(i);
            VariableJoinCondition joinCondition = expansionJoin.getJoinCondition();
            ExpansionElement atom = expansion.getExpansionElements().get(i + 1);
            if (allPathsInJoinAreSkolems(joinCondition, expansion)) {
                addAtomToExistingTgd(result, joinCondition, atom, expansion);
            } else {
                TgdWithAtoms newTgdWithAtoms = new TgdWithAtoms(atom);
                result.add(newTgdWithAtoms);
            }
        }
        return result;
    }

    private void addAtomToExistingTgd(List<TgdWithAtoms> result, VariableJoinCondition joinCondition, ExpansionElement atom, Expansion expansion) {
        ExpansionElement existingAtom =  findExistingAtom(joinCondition, atom, expansion);
        for (TgdWithAtoms tgdWithAtoms : result) {
            if (tgdWithAtoms.getAtoms().contains(existingAtom)) {
                tgdWithAtoms.addAtom(atom);
                return ;
            }
        }
        throw new IllegalArgumentException("Unable to find tgd for atom " + existingAtom + " in " + result);
    }

    private ExpansionElement findExistingAtom(VariableJoinCondition joinCondition, ExpansionElement atom, Expansion expansion) {
        if (atom.getCoveringAtom().getVariable().hasSameId(joinCondition.getFromVariable())) {
            return findAtomFromVariable(joinCondition.getToVariable(), expansion);
        }
        return findAtomFromVariable(joinCondition.getFromVariable(), expansion);
    }

}
