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
 
package it.unibas.spicy.model.paths.operators;

import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.List;


public class FindVariableGenerators {
    
    public List<SetAlias> findVariableGenerators(SetAlias variable) {
        FindVariableGeneratorsVisitor visitor = new FindVariableGeneratorsVisitor();
        variable.accept(visitor);
        return visitor.getResult();
    }
    
    public List<SetAlias> findViewGenerators(SimpleConjunctiveQuery view) {
        List<SetAlias> result = new ArrayList<SetAlias>();
        for (SetAlias variable : view.getVariables()){
            addVariables(findVariableGenerators(variable), result);
        }
        return result;
    }

//    private void addVariables(List<Variable> variableList, List<Variable> result) {
//        for (Variable variable : variableList) {
//            if (!result.contains(variable)) {
//                result.add(variable);
//            }
//        }
//    }

    private void addVariables(List<SetAlias> variableList, List<SetAlias> result) {
        for (SetAlias variable : variableList) {
            if (!contains(result, variable)) {
                result.add(variable);
            }
        }
    }

    private boolean contains(List<SetAlias> result, SetAlias variable) {
        for (SetAlias existingVariable : result) {
            if (existingVariable.getId() == variable.getId()) {
                return true;
            }
        }
        return false;
    }

}
class FindVariableGeneratorsVisitor implements IVariablePathVisitor {
    
    private List<SetAlias> result = new ArrayList<SetAlias>();
    
    public void visitVariable(SetAlias v) {
        result.add(v);
        v.getBindingPathExpression().accept(this);
    }
    
    public void visitPathExpression(VariablePathExpression pathExpression) {
        SetAlias startingVariable = pathExpression.getStartingVariable();
        if (startingVariable != null) {
            startingVariable.accept(this);
        }
    }
    
    public List<SetAlias> getResult() {
        return result;
    }
}