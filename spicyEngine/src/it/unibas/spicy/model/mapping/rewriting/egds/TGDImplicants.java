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
 
package it.unibas.spicy.model.mapping.rewriting.egds;

import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TGDImplicants {

    private FORule tgd;
    private Determination standardImplicants;
    private Map<FormulaVariable, Determination> minimalImplicants = new HashMap<FormulaVariable, Determination>();
    private Map<FormulaVariable, List<Determination>> minimalImplicantsForOccurrences = new HashMap<FormulaVariable, List<Determination>>();

    public TGDImplicants(FORule tgd, Determination standardImplicants) {
        this.tgd = tgd;
        this.standardImplicants = standardImplicants;
    }

    public FORule getTGD() {
        return tgd;
    }

    public Determination getStandardImplicants() {
        return standardImplicants;
    }

    public boolean isEmpty() {
        return minimalImplicants.isEmpty();
    }

    public boolean allClear() {
        for (FormulaVariable variable : minimalImplicants.keySet()) {
            if (minimalImplicants.get(variable) == null) {
                return false;
            }
        }
        return true;
    }

    public Determination getMinimalImplicants(FormulaVariable formulaVariable) {
        Determination implicantsForVariable = minimalImplicants.get(formulaVariable);
        if (implicantsForVariable == null) {
            throw new IllegalArgumentException("Unable to find implicants for variable: " + formulaVariable + " in map " + SpicyEngineUtility.printMap(minimalImplicants));
        }
        return implicantsForVariable;
    }

    public void addMinimalImplicants(FormulaVariable variable, Determination implicants) {
        minimalImplicants.put(variable, implicants);
    }

    public List<Determination> getMinimalImplicantsForOccurrences(FormulaVariable formulaVariable) {
        return minimalImplicantsForOccurrences.get(formulaVariable);
    }

    public void addMinimalImplicantsForOccurrences(FormulaVariable variable, List<Determination> implicants) {
        minimalImplicantsForOccurrences.put(variable, implicants);
    }

    public String toString() {
        List<FormulaVariable> variables = new ArrayList<FormulaVariable>(minimalImplicants.keySet());
        Collections.sort(variables);
        StringBuilder result = new StringBuilder("--------------  IMPLICATION MAP  --------------\n");
        result.append("Standard implicants: ").append(standardImplicants).append("\n");
        for (FormulaVariable variable : variables) {
            result.append("---Variable: ").append(variable.toShortString()).append(" - Minimal implicants: ").append(minimalImplicants.get(variable)).append("\n");
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }

    public String toLongString() {
        List<FormulaVariable> variables = new ArrayList<FormulaVariable>(minimalImplicants.keySet());
        Collections.sort(variables);
        StringBuilder result = new StringBuilder("--------------  IMPLICATION MAP  (all minimized = ").append(allClear()).append(") --------------\n");
        result.append("Standard implicants: ").append(standardImplicants).append("\n");
        for (FormulaVariable variable : variables) {
            result.append("----------Variable: ").append(variable.toShortString()).append(" - Minimal implicants: ").append(minimalImplicants.get(variable)).append("\n");
            List<Determination> implicantsForOccurrences = minimalImplicantsForOccurrences.get(variable);
            for (int i = 0; i < variable.getTargetOccurrencePaths().size(); i++) {
                VariablePathExpression occurrence = variable.getTargetOccurrencePaths().get(i);
                result.append("Occurrence: ").append(occurrence).append("\n");
            }
            for (int i = 0; i < implicantsForOccurrences.size(); i++) {
                Determination implicantsForOccurrence = implicantsForOccurrences.get(i);
                result.append(" - Implicants: ").append(implicantsForOccurrence).append("\n");
            }
        }
        result.append("---------------------------------------------------------------\n");
        return result.toString();
    }
}
