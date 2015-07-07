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

import it.unibas.spicy.model.mapping.*;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.List;

public class Subsumption {

    private FORule leftTgd;
    private FORule rightTgd;
    private List<VariableCorrespondence> leftCorrespondences;
    private List<VariableCorrespondence> rightCorrespondences;

    public Subsumption(FORule leftTgd, FORule rightTgd, List<VariableCorrespondence> leftCorrespondences, List<VariableCorrespondence> rightCorrespondences) {
        this.leftTgd = leftTgd;
        this.rightTgd = rightTgd;
        this.leftCorrespondences = leftCorrespondences;
        this.rightCorrespondences = rightCorrespondences;
    }

    public List<VariableCorrespondence> getLeftCorrespondences() {
        return leftCorrespondences;
    }

    public List<VariableCorrespondence> getRightCorrespondences() {
        return rightCorrespondences;
    }

    public String getId() {
        return rightTgd.getId() + SpicyEngineConstants.SUBSUMES + leftTgd.getId();
    }

    public FORule getRightTgd() {
        return rightTgd;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("----Subsumption---- (").append(getId()).append(")\n");
        result.append(rightTgd.toString(indent));
        result.append(indent).append("----with correspondences:\n");
        result.append(indent).append(leftCorrespondences).append("\n");
        result.append(indent).append("----and:\n");
        result.append(indent).append(rightCorrespondences).append("\n");
        return result.toString();
    }

}