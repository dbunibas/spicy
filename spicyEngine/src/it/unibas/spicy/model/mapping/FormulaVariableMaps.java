/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Donatello Santoro - donatello.santoro@gmail.com

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

package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaVariableMaps {

    private Map<ViewId, List<FormulaVariable>> universalVariables = new HashMap<ViewId, List<FormulaVariable>>();
    private Map<ViewId, List<FormulaVariable>> existentialVariables = new HashMap<ViewId, List<FormulaVariable>>();
    private Map<ViewId, List<Expression>> equalities = new HashMap<ViewId, List<Expression>>();

    public List<FormulaVariable> getUniversalVariables(String viewId) {
        return this.universalVariables.get(new ViewId(viewId));
    }

    public void setUniversalVariables(String viewId, Object view, List<FormulaVariable> variables) {
        assert(variables != null) : "Null universal variables for view " + view;
        this.universalVariables.put(new ViewId(viewId, view), variables);
    }

    public List<FormulaVariable> getExistentialVariables(String viewId) {
        return this.existentialVariables.get(new ViewId(viewId));
    }

    public void setExistentialVariables(String viewId, Object view, List<FormulaVariable> variables) {
        assert(variables != null) : "Null existential variables for view " + view;
        this.existentialVariables.put(new ViewId(viewId, view), variables);
    }

    public List<Expression> getEqualities(String viewId) {
        return this.equalities.get(new ViewId(viewId));
    }

    public void setEqualities(String viewId, Object view, List<Expression> equalities) {
        assert(equalities != null) : "Null equalities for view " + view;
        this.equalities.put(new ViewId(viewId, view), equalities);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------------------  Variable Maps ------------------------\n");
        result.append("*********** Universal variables *********** \n");
        result.append(SpicyEngineUtility.printVariableMap(universalVariables));
        result.append("\n*********** Existential variables *********** \n");
        result.append(SpicyEngineUtility.printVariableMap(existentialVariables));
        result.append("\n*********** Equalities *********** \n");
        result.append(SpicyEngineUtility.printMap(equalities));
        return result.toString();
    }

}

class ViewId {

    private String id;
    private Object view;

    public ViewId(String id, Object view) {
        this.id = id;
        this.view = view;
    }

    public ViewId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getView() {
        return view;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ViewId other = (ViewId) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id;
    }

}