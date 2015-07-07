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
package it.unibas.spicy.model.algebra.query.operators.xquery;

import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.mapping.rewriting.CoverageAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;

public class XQNames {

    final static String POSITIVE_QUERY = "pos_";
    final static String NULL_VALUE = "null";

    public static String attributeNameInVariable(VariablePathExpression attributePath) {
        return XQUtility.VARIABLE + "/" + attributePath.getStartingVariable().toShortString() + "_" + attributePath.getLastStep() + "/text()";
    }

    public static String xQueryNameForView(ComplexQueryWithNegations view) {
        StringBuilder result = new StringBuilder();
        result.append("$VIEW_");
        for (SetAlias variable : view.getVariables()) {
            result.append(variable.toShortString());
        }
        return result.toString();
    }

    public static String xQueryNameForApplyFunctions(FORule tgd) {
        return xQueryNameForTgd(tgd) + "_AF";
    }

    public static String xQueryNameForCoverage(Coverage coverage) {
        StringBuilder result = new StringBuilder();
        result.append("$COV_VIEW_");
        for (CoverageAtom coveringAtom : coverage.getCoveringAtoms()) {
            for (FormulaAtom formulaAtom : coveringAtom.getCoveringAtoms()) {
                result.append(formulaAtom.getTgd().getId());
            }
        }
        return result.toString();
    }

    public static String xQueryNameForTgd(FORule tgd) {
        return "$" + tgd.getId();
    }

    public static String xQueryFinalTgdName(FORule tgd) {
        return "$TARGET_VALUES_" + tgd.getId();
    }

    public static String xQueryNameForPath(VariablePathExpression attributePath) {
        StringBuilder result = new StringBuilder();
        result.append(attributePath.getStartingVariable().toShortString()).append("_").append(attributePath.getLastStep());
        return result.toString();
    }

    public static String xqueryNameForDifference(FORule tgd) {
        return xQueryNameForTgd(tgd) + "_DIFF";
    }

    public static String xQueryNameForCorrespondence(VariableCorrespondence correspondence) {
        //TODO: Apply functions...
        String sourcePathName = "";
        if (correspondence.getSourcePaths() != null) {
            VariablePathExpression sourcePath = correspondence.getSourcePaths().get(0);
            sourcePathName = xQueryNameForPath(sourcePath);
        } else {
            sourcePathName = correspondence.getSourceValue().toString();
            sourcePathName = sourcePathName.replaceAll("\"", "\'");
        }
        VariablePathExpression targetPath = correspondence.getTargetPath();
        return "element " + xQueryNameForPath(targetPath) + " { " + xQueryNameFullPathInVariable(sourcePathName) + " }";
    }

    public static String xQueryNameForPositiveView(ComplexQueryWithNegations query) {
        return "$" + POSITIVE_QUERY + query.getId();
    }

    private static String xQueryNameFullPathInVariable(String path) {
        return XQUtility.VARIABLE + "/" + path + "/text()";
    }

    public static String xQueryNameForViewWithIntersection(ComplexQueryWithNegations query) {
        return "$" + query.getId();
    }

    public static String finalXQueryNameSTExchange(SetAlias variable) {
        return "$ST_EXCHANGE_RESULT_FOR_" + variable.toShortString() + "_" + variable.getBindingPathExpression().getLastStep();
    }

    public static String finalXQueryNameTExchange(SetAlias variable) {
        return "$TARGET_EXCHANGE_RESULT_FOR_" + variable.getBindingPathExpression().getLastStep();
    }
}
