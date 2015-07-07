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

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.List;

public class InducedFunctionalDependency extends VariableFunctionalDependency {

    private FORule tgd;
    private VariableFunctionalDependency sourceFunctionalDependency;
    private Determination leftVariables;
    private Determination rightVariables;

    public InducedFunctionalDependency(List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths,
            FORule tgd, VariableFunctionalDependency sourceFunctionalDependency, Determination leftVariables, Determination rightVariables) {
        super(leftPaths, rightPaths);
        this.tgd = tgd;
        this.sourceFunctionalDependency = sourceFunctionalDependency;
        this.leftVariables = leftVariables;
        this.rightVariables = rightVariables;
    }

    public Determination getLeftVariables() {
        return leftVariables;
    }

    public Determination getRightVariables() {
        return rightVariables;
    }

    public VariableFunctionalDependency getSourceFunctionalDependency() {
        return sourceFunctionalDependency;
    }

    public FORule getTgd() {
        return tgd;
    }

    public boolean equals(Object o) {
        if (!(o instanceof InducedFunctionalDependency)) {
            return false;
        }
        InducedFunctionalDependency otherDependency = (InducedFunctionalDependency)o;
        boolean sameTgd = this.tgd.equals(otherDependency.tgd);
        boolean equalPaths = (SpicyEngineUtility.equalLists(this.leftPaths, otherDependency.leftPaths) &&
                SpicyEngineUtility.equalLists(this.rightPaths, otherDependency.rightPaths));
        return sameTgd && equalPaths;
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("\n");
        result.append("TGD : ").append(tgd.getId()).append("\n");
        result.append("Source FD: ").append(sourceFunctionalDependency).append("\n");
        result.append("Left variables: ").append(leftVariables);
        result.append("Right variables: ").append(rightVariables);
        return result.toString();
    }

}
