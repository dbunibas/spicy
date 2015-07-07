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
 
package it.unibas.spicy.model.mapping.rewriting;

import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CoverageAtom implements Cloneable {

    private static Log logger = LogFactory.getLog(CoverageAtom.class);

    private List<FormulaAtom> originalAtoms = new ArrayList<FormulaAtom>();
    private List<FormulaAtom> coveringAtoms = new ArrayList<FormulaAtom>();
    private List<VariableCorrespondence> originalCorrespondences;
    private List<VariableCorrespondence> matchingCorrespondences;

    public CoverageAtom(FormulaAtom originalAtom, FormulaAtom coveringAtom) {
        this.originalAtoms.add(originalAtom);
        this.coveringAtoms.add(coveringAtom);
    }

    public FormulaAtom getFirstCoveringAtom() {
        return coveringAtoms.get(0);
    }

    public FormulaAtom getFirstOriginalAtom() {
        return originalAtoms.get(0);
    }

    public void addOriginalAtom(FormulaAtom originalAtom) {
        this.originalAtoms.add(originalAtom);
    }

    public void addCoveringAtom(FormulaAtom coveringAtom) {
        this.coveringAtoms.add(coveringAtom);
    }

    public List<FormulaAtom> getCoveringAtoms() {
        return coveringAtoms;
    }

    public List<VariableCorrespondence> getMatchingCorrespondences() {
        return matchingCorrespondences;
    }

    public void setMatchingCorrespondences(List<VariableCorrespondence> matchingCorrespondences) {
        this.matchingCorrespondences = matchingCorrespondences;
    }

    public List<VariableCorrespondence> getOriginalCorrespondences() {
        return originalCorrespondences;
    }

    public void setOriginalCorrespondences(List<VariableCorrespondence> originalCorrespondences) {
        this.originalCorrespondences = originalCorrespondences;
    }

    @Override
    public CoverageAtom clone() {
        try {
            CoverageAtom clone = (CoverageAtom) super.clone();
            clone.originalAtoms = new ArrayList<FormulaAtom>(originalAtoms);
            clone.coveringAtoms = new ArrayList<FormulaAtom>(coveringAtoms);
            clone.originalCorrespondences = new ArrayList<VariableCorrespondence>(originalCorrespondences);
            clone.matchingCorrespondences = new ArrayList<VariableCorrespondence>(matchingCorrespondences);
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Atom coverage:\n");
        for (int i = 0; i < originalAtoms.size(); i++) {
            result.append(indent).append("---original atom: ").append(originalAtoms.get(i)).append("\n");
            result.append(indent).append("---covering atom: ").append(coveringAtoms.get(i)).append("\n");
        }
        result.append(indent).append("---original corr:\n").append(SpicyEngineUtility.printCollection(originalCorrespondences, indent)).append("\n");
        result.append(indent).append("---covering corr:\n").append(SpicyEngineUtility.printCollection(matchingCorrespondences, indent)).append("\n");
        return result.toString();
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        for (FormulaAtom coveringAtom : coveringAtoms) {
            result.append(coveringAtom.getVariable().toShortString());
        }
        result.append("c");
        for (FormulaAtom originalAtom : originalAtoms) {
            result.append(originalAtom.getVariable().toShortString());
        }
        return result.toString();
    }
}
