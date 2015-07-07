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
 
package it.unibas.spicy.model.algebra;

import it.unibas.spicy.model.algebra.operators.AlgebraTreeToString;
import it.unibas.spicy.model.mapping.IDataSourceProxy;

abstract class AbstractOperator implements IAlgebraOperator {

    protected IAlgebraOperator father;
    protected IDataSourceProxy result;
    protected String id;

    public IAlgebraOperator getFather() {
        return father;
    }

    public void setFather(IAlgebraOperator father) {
        this.father = father;
    }

    public String getId() {
        return id;
    }

    public String printIds() {
        StringBuilder allIds = new StringBuilder();
        if (this.id != null) {
            allIds.append("\n").append(id).append("\n");
        }
        IAlgebraOperator ancestor = this.father;
        while (ancestor != null) {
            if (ancestor.getId() != null) {
                allIds.insert(0, ancestor.getId() + "\n");
            }
            ancestor = ancestor.getFather();
        }
        return allIds.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return new AlgebraTreeToString().treeToString(this);
    }
}
