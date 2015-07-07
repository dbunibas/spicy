/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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

import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class TGDToLogicalStringUtility {

    public final String INDENT = "    ";
    public final String SECONDARY_INDENT = "  ";
    private FindFormulaVariablesUtility variableFinder = new FindFormulaVariablesUtility();

    public String printAtomName(SetAlias alias, boolean nested) {
        StringBuilder result = new StringBuilder();
        if (nested) {
//        if (true) {
            result.append(SpicyEngineUtility.removeRootLabel(alias.getAbsoluteBindingPathExpression().getLastStep())).append("(");
            result.append("oid: ").append(alias.toShortString()).append(", ");
            SetAlias fatherAlias = alias.getBindingVariable();
            if (fatherAlias != null) {
                result.append("fatherOid: ").append(fatherAlias.toShortString()).append(", ");
            }
        } else {
            String variableLabel = SpicyEngineUtility.removeRootLabel(alias.getAbsoluteBindingPathExpression());
            result.append(SetCloneNode.removeCloneLabel(variableLabel)).append("(");
        }
        return result.toString();
    }

    public String printAtomNameForSaveFormat(SetAlias alias, boolean nested) {
        StringBuilder result = new StringBuilder();
        if (nested) {
            result.append(SpicyEngineUtility.removeRootLabel(alias.getAbsoluteBindingPathExpression().getLastStep())).append("(");
        } else {
            String variableLabel = SpicyEngineUtility.removeRootLabel(alias.getAbsoluteBindingPathExpression());
            result.append(SetCloneNode.removeCloneLabel(variableLabel)).append("(");
        }
        return result.toString();
    }

    public String printVariables(List<FormulaVariable> variables) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < variables.size(); i++) {
            FormulaVariable formulaVariable = variables.get(i);
            result.append(formulaVariable);
            if (i != variables.size() - 1) {
                result.append(", ");
            }
        }
        result.append(": ");
        return result.toString();
    }

    public boolean setVariableDescriptionsForSkolems(Expression expression, List<FormulaVariable> variables) {
        List<List<FormulaVariable>> listOfLists = new ArrayList<List<FormulaVariable>>();
        listOfLists.add(variables);
        return setVariableDescriptions(expression, listOfLists);
    }

    public boolean setVariableDescriptions(Expression expression, List<List<FormulaVariable>> variables) {
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        List<FormulaVariable> formulaVariables = new ArrayList<FormulaVariable>();
        for (Variable variable : symbolTable.getVariables()) {
//            VariablePathExpression variablePath = (VariablePathExpression) variable.getDescription();
            String pathString = variable.getDescription().toString();
            FormulaVariable formulaVariable = findVariableForString(pathString, variables);
            variable.setDescription(formulaVariable);
            if (!formulaVariables.contains(formulaVariable)) {
                formulaVariables.add(formulaVariable);
            }
        }
        return formulaVariables.size() > 1;
    }

    public FormulaVariable findVariableForString(String pathString, List<List<FormulaVariable>> variables) {
//        for (int i = variables.size() - 1; i >= 0; i--) {
        for (int i = 0; i < variables.size(); i++) {
            List<FormulaVariable> variableList = variables.get(i);
            for (FormulaVariable formulaVariable : variableList) {
                if (SpicyEngineUtility.containsPathWithEqualString(formulaVariable.getOriginalSourceOccurrencePaths(), pathString)) {
                    return formulaVariable;
                }
            }
            for (FormulaVariable formulaVariable : variableList) {
                if (SpicyEngineUtility.containsPathWithEqualString(formulaVariable.getSourceOccurrencePaths(), pathString)) {
                    return formulaVariable;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find variable for path " + pathString + " in " + SpicyEngineUtility.printListOfVariableLists(variables));
    }

    String printOIDVariables(SimpleConjunctiveQuery targetView) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < targetView.getGenerators().size(); i++) {
            result.append(targetView.getGenerators().get(i).toShortString());
            if (i != targetView.getGenerators().size() - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }
}
