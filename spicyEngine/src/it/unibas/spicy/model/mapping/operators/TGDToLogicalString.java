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
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullValueGenerator;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.generators.TGDGeneratorsMap;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.FormulaVariableMaps;
import it.unibas.spicy.model.mapping.IIdentifiable;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TGDToLogicalString {

    private TGDToLogicalStringUtility utility = new TGDToLogicalStringUtility();
    private boolean useSaveFormat;
    private boolean printSkolems;

    public TGDToLogicalString(boolean printSkolems, boolean useSaveFormat) {
        this.printSkolems = printSkolems;
        this.useSaveFormat = useSaveFormat;
    }

    public String toLogicalString(FORule rule, MappingTask mappingTask, String indent) {
        StringBuilder result = new StringBuilder();
        if (!useSaveFormat) {
            result.append(indent).append(rule.getId()).append(": \n");
        }
        result.append(premiseString(rule, mappingTask, indent));
//        result.append("\n").append(indent).append(SECONDARY_INDENT).append(" -->  \n");
//        result.append(" --> \n");
        result.append(" -> ");
        result.append(conclusionString(rule, mappingTask, indent + utility.INDENT));
        result.append(".\n");
        if (!useSaveFormat && printSkolems) {
            result.append("\n");
            result.append(printSkolems(rule, mappingTask));
            result.append("-----------------------------------\n");
        }
        return result.toString();
    }

    ////////////////////////////////   PREMISE   ////////////////////////////////////////////////
    private String premiseString(FORule rule, MappingTask mappingTask, String indent) {
        StringBuilder result = new StringBuilder();
        List<List<FormulaVariable>> variables = new ArrayList<List<FormulaVariable>>();
        List<FormulaVariable> universalVariables = rule.getUniversalFormulaVariables(mappingTask);
        variables.add(0, universalVariables);
        if (!useSaveFormat) {
            result.append(indent).append("for each ");
            result.append(utility.printVariables(universalVariables));
            result.append("\n");
        }
        result.append(runVisitor(rule, mappingTask, indent));
        variables.remove(0);
        return result.toString();
    }

    private String runVisitor(FORule rule, MappingTask mappingTask, String indent) {
        FormulaVariableMaps variableMaps = rule.getVariableMaps(mappingTask);
        List<IIdentifiable> stack = new ArrayList<IIdentifiable>();
        stack.add(0, rule);
        TGDToStringVisitor visitor = new TGDToStringVisitor(rule, variableMaps, stack, mappingTask, indent, useSaveFormat);
        rule.getComplexSourceQuery().accept(visitor);
        return visitor.getResult();
    }

    ////////////////////////////////   CONCLUSION   ////////////////////////////////////////////////
    public String conclusionString(FORule tgd, MappingTask mappingTask, String indent) {
        StringBuilder result = new StringBuilder();
        List<FormulaVariable> universalVariables = tgd.getUniversalFormulaVariables(mappingTask);
        List<FormulaVariable> existentialVariables = tgd.getExistentialFormulaVariables(mappingTask);
        if (!useSaveFormat && (!existentialVariables.isEmpty() || mappingTask.getTargetProxy().isNested())) {
            result.append("exist ");
            if (mappingTask.getTargetProxy().isNested()) {
                utility.printOIDVariables(tgd.getTargetView());
            }
            if (!existentialVariables.isEmpty()) {
                result.append(utility.printVariables(existentialVariables));
            }
        }
        result.append("\n");
        for (int i = 0; i < tgd.getTargetView().getGenerators().size(); i++) {
            SetAlias targetVariable = tgd.getTargetView().getGenerators().get(i);
            result.append(indent).append(utility.INDENT);
            if (!useSaveFormat) {
                result.append(utility.printAtomName(targetVariable, mappingTask.getTargetProxy().isNested()));
            } else {
                result.append(utility.printAtomNameForSaveFormat(targetVariable, mappingTask.getTargetProxy().isNested()));
            }
            List<VariablePathExpression> attributePaths = targetVariable.getFirstLevelAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
            for (int j = 0; j < attributePaths.size(); j++) {
                VariablePathExpression attributePath = attributePaths.get(j);
                FormulaVariable attributeVariable = findUniversalVariableForTargetPath(attributePath, universalVariables);
                if (attributeVariable == null) {
                    attributeVariable = findExistentialVariable(attributePath, tgd.getExistentialFormulaVariables(mappingTask));
                }
                result.append(attributePath.getLastStep()).append(": ");
                Expression transformation = attributeVariable.getTransformationFunction(attributePath);
                if (transformation == null) {
                    if (useSaveFormat) {
                        result.append("$");
                    }
                    result.append(attributeVariable.toShortString());
                } else {
                    List<List<FormulaVariable>> variables = new ArrayList<List<FormulaVariable>>();
                    variables.add(tgd.getUniversalFormulaVariables(mappingTask));
                    utility.setVariableDescriptions(transformation, variables);
                    result.append(transformation);
                }
                if (j != attributePaths.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(")");
            if (i != tgd.getTargetView().getGenerators().size() - 1) {
                result.append(", \n");
            }
        }
        return result.toString();
    }

    private FormulaVariable findUniversalVariableForTargetPath(VariablePathExpression attributePath, List<FormulaVariable> universalFormulaVariables) {
        for (FormulaVariable formulaVariable : universalFormulaVariables) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(formulaVariable.getTargetOccurrencePaths(), attributePath)) {
                return formulaVariable;
            }
        }
        return null;
    }

    private FormulaVariable findExistentialVariable(VariablePathExpression attributePath, List<FormulaVariable> existentialFormulaVariables) {
        for (FormulaVariable formulaVariable : existentialFormulaVariables) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(formulaVariable.getTargetOccurrencePaths(), attributePath)) {
                return formulaVariable;
            }
        }
        throw new IllegalArgumentException("Unable to find variable for path " + attributePath + " in " + SpicyEngineUtility.printVariableList(existentialFormulaVariables));
    }

    ///////////////////////////    SKOLEMS   ///////////////////////////////
    private String printSkolems(FORule rule, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        List<FormulaVariable> existentialVariables = rule.getExistentialFormulaVariables(mappingTask);
        for (FormulaVariable variable : existentialVariables) {
            String generatorString = findGeneratorForVariable(variable, rule, mappingTask);
            result.append(variable.toShortString()).append(": ").append(generatorString).append("\n");
        }
        if (mappingTask.getTargetProxy().isNested()) {
            for (SetAlias alias : rule.getTargetView().getGenerators()) {
                VariablePathExpression bindingPath = alias.getBindingPathExpression();
                String generatorString = findGeneratorForPath(bindingPath, rule, mappingTask);
                result.append(alias.toShortString()).append(": ").append(generatorString).append("\n");
            }
        }
        return result.toString();
    }

    private String findGeneratorForVariable(FormulaVariable variable, FORule rule, MappingTask mappingTask) {
        List<FormulaVariable> universalVariables = rule.getUniversalFormulaVariablesInTarget(mappingTask);
        TGDGeneratorsMap generatorsMap = rule.getGenerators(mappingTask);
        for (VariablePathExpression targetPath : variable.getTargetOccurrencePaths()) {
            IValueGenerator generator = generatorsMap.getGeneratorForLeaf(targetPath, mappingTask.getTargetProxy().getIntermediateSchema());
            if (generator != null && generator instanceof SkolemFunctionGenerator) {
                SkolemFunctionGenerator skolemGenerator = (SkolemFunctionGenerator) generator;
                return skolemGenerator.toStringWithVariableArguments(universalVariables);
            }
        }
        return NullValueGenerator.getInstance().toString();
        //throw new IllegalArgumentException("Unable to find generator for variable: " + variable.toLongString() + "\nin rule: " + rule + "\n" + SpicyEngineUtility.printVariableList(rule.getExistentialFormulaVariables(mappingTask)) + "\nGenerators: " + generatorsMap);
    }

    private String findGeneratorForPath(VariablePathExpression setPath, FORule rule, MappingTask mappingTask) {
        List<FormulaVariable> universalVariables = rule.getUniversalFormulaVariables(mappingTask);
        TGDGeneratorsMap generatorsMap = rule.getGenerators(mappingTask);
        IValueGenerator generator = generatorsMap.getGeneratorForSetPath(setPath);
        if (generator == null) {
            return "SK_" + setPath.toString() + "()";
        }
        if (generator instanceof NullValueGenerator) {
            return NullValueGenerator.getInstance().toString();
        }
        SkolemFunctionGenerator skolemGenerator = (SkolemFunctionGenerator) generator;
        return skolemGenerator.toStringWithVariableArguments(universalVariables);
    }
}

class TGDToStringVisitor implements IQueryVisitor {

    private static Log logger = LogFactory.getLog(TGDToStringVisitor.class);
    private StringBuilder result = new StringBuilder();
    private MappingTask mappingTask;
    private FormulaVariableMaps variableMaps;
    private List<IIdentifiable> stack;
    private List<List<FormulaVariable>> variables = new ArrayList<List<FormulaVariable>>();
    private String indent;
    private boolean useSaveFormat;
    private TGDToLogicalStringUtility utility = new TGDToLogicalStringUtility();
    private FindFormulaVariablesUtility variableFinder = new FindFormulaVariablesUtility();

    public TGDToStringVisitor(FORule rule, FormulaVariableMaps variableMaps, List<IIdentifiable> stack, MappingTask mappingTask, String indent, boolean useSaveFormat) {
        this.mappingTask = mappingTask;
        this.variableMaps = variableMaps;
        this.stack = stack;
        this.indent = indent;
        this.useSaveFormat = useSaveFormat;
        List<FormulaVariable> universalVariables = rule.getUniversalFormulaVariables(mappingTask);
        variables.add(universalVariables);
    }

    public String generateIndent() {
        StringBuilder localResult = new StringBuilder();
        localResult.append(indent);
        int indentDepth = stack.size() - 2;
        if (useSaveFormat) {
            indentDepth--;
        }
        for (int i = 0; i < indentDepth; i++) {
            localResult.append(utility.INDENT);
        }
        return localResult.toString();
    }

    public void visitComplexQueryWithNegation(ComplexQueryWithNegations query) {
        stack.add(0, query);
//        List<FormulaVariable> universalVariables = variableFinder.getUniversalVariables(variableMaps, stack);
//        variables.add(0, universalVariables);
        ComplexConjunctiveQuery positiveQuery = query.getComplexQuery();
        positiveQuery.accept(this);
        List<NegatedComplexQuery> sortedQueries = new ArrayList<NegatedComplexQuery>(query.getNegatedComplexQueries());
        Collections.sort(sortedQueries);
        for (NegatedComplexQuery negatedQuery : sortedQueries) {
            negatedQuery.accept(this);
        }
//        variables.remove(0);
        stack.remove(0);
    }

    public void visitComplexConjunctiveQuery(ComplexConjunctiveQuery complexQuery) {
        stack.add(0, complexQuery);
//        List<FormulaVariable> universalVariables = variableFinder.getUniversalVariables(variableMaps, stack);
//        variables.add(0, universalVariables);
        result.append(generateConjunctionString(complexQuery.getGenerators(), variables, mappingTask.getSourceProxy().getIntermediateSchema()));
        result.append(generateBuiltinsForSelections(complexQuery));
        List<Expression> equalities = variableFinder.getEqualities(variableMaps, stack);
        if (!equalities.isEmpty()) {
            result.append(generateEqualityString(equalities, variables, false));
        }
        // intersection
        if (complexQuery.hasIntersection()) {
            if (useSaveFormat) {
                result.append(", \n");
            } else {
                result.append("\n").append(generateIndent()).append(utility.SECONDARY_INDENT).append("and ");
            }
            SimpleConjunctiveQuery intersectionQuery = complexQuery.getConjunctionForIntersection();
            stack.add(0, intersectionQuery);
            List<FormulaVariable> existentialVariables = variableFinder.getExistentialVariables(variableMaps, stack);
            variables.add(0, existentialVariables);
            if (!existentialVariables.isEmpty() && !useSaveFormat) {
                result.append("exist ");
                result.append(utility.printVariables(existentialVariables));
            }
            if (!useSaveFormat) {
                result.append(" (// base view ").append(intersectionQuery.getId()).append("\n");
            }
            result.append(generateConjunctionString(intersectionQuery.getGenerators(), variables, mappingTask.getSourceProxy().getIntermediateSchema()));
            List<Expression> intersectionEqualities = variableFinder.getEqualities(variableMaps, stack);
            if (!intersectionEqualities.isEmpty()) {
                result.append(generateEqualityString(intersectionEqualities, variables, false));
            }
            variables.remove(0);
            stack.remove(0);
            if (!useSaveFormat) {
                result.append("\n").append(generateIndent()).append(utility.SECONDARY_INDENT).append(")");
            }
        }
//        variables.remove(0);
        stack.remove(0);
    }

    private String generateBuiltinsForSelections(ComplexConjunctiveQuery query) {
        StringBuilder localResult = new StringBuilder();
        if (query.getAllSelections().size() > 0) {
            localResult.append(", ");
        }
        for (int i = 0; i < query.getAllSelections().size(); i++) {
            VariableSelectionCondition selection = query.getAllSelections().get(i);
            Expression expression = selection.getCondition().clone();
            utility.setVariableDescriptions(expression, variables);
            if (useSaveFormat) {
                String expressionString = expression.toSaveString();
                localResult.append(expressionString);
            } else {
                localResult.append(expression.toString());
            }
            if (i != query.getAllSelections().size() - 1) {
                localResult.append(", ");
            }
        }
        return localResult.toString();
    }

    public void visitSimpleConjunctiveQuery(SimpleConjunctiveQuery query) {
    }

    public void visitNegatedComplexQuery(NegatedComplexQuery negatedQuery) {
        stack.add(0, negatedQuery);
        List<FormulaVariable> existentialVariables = variableFinder.getExistentialVariables(variableMaps, stack);
        variables.add(0, existentialVariables);
        result.append("\n").append(generateIndent()).append(utility.SECONDARY_INDENT).append("and not ");
        if (useSaveFormat) {
            result.append("exists ");
        } else {
            if (!existentialVariables.isEmpty()) {
                result.append("exist ");
                result.append(utility.printVariables(existentialVariables));
            }
        }
        ComplexQueryWithNegations childQuery = negatedQuery.getComplexQuery();
        result.append(" (");
        if (!useSaveFormat) {
            result.append("//").append(negatedQuery.getProvenance()).append(" - ").append(childQuery.getId());
        }
        result.append("\n");
        childQuery.accept(this);
        List<Expression> equalities = variableFinder.getEqualities(variableMaps, stack);
        if (!equalities.isEmpty()) {
            result.append(generateEqualityString(equalities, variables, true));
        }
        result.append("\n").append(generateIndent()).append(utility.SECONDARY_INDENT).append(")");
        variables.remove(0);
        stack.remove(0);
    }

    public String getResult() {
        return result.toString();
    }

    private String generateConjunctionString(List<SetAlias> setVariables, List<List<FormulaVariable>> variables, INode schema) {
        StringBuilder localResult = new StringBuilder();
        List<SetAlias> sortedAliases = new ArrayList<SetAlias>(setVariables);
        Collections.sort(sortedAliases);
        for (int i = 0; i < sortedAliases.size(); i++) {
            SetAlias sourceVariable = sortedAliases.get(i);
            localResult.append(generateIndent());
            if (!useSaveFormat) {
                localResult.append(utility.printAtomName(sourceVariable, mappingTask.getSourceProxy().isNested()));
            } else {
                localResult.append(utility.printAtomNameForSaveFormat(sourceVariable, mappingTask.getSourceProxy().isNested()));
            }
            List<VariablePathExpression> attributePaths = sourceVariable.getFirstLevelAttributes(schema);
            for (int j = 0; j < attributePaths.size(); j++) {
                VariablePathExpression attributePath = attributePaths.get(j);
                FormulaVariable attributeVariable = findVariableForPath(attributePath, variables);
                localResult.append(attributePath.getLastStep()).append(": ");
                if (useSaveFormat) {
                    localResult.append("$");
                }
                localResult.append(attributeVariable.toShortString());
                if (j != attributePaths.size() - 1) {
                    localResult.append(", ");
                }
            }
            localResult.append(")");
            if (i != sortedAliases.size() - 1) {
                localResult.append(", \n");
            }
        }
        return localResult.toString();
    }

    private String generateEqualityString(List<Expression> equalities, List<List<FormulaVariable>> variables, boolean with) {
        StringBuilder localResult = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        if (useSaveFormat) {
            if (with) {
                prefix.append("\n").append(generateIndent()).append("with ");
            } else {
                prefix.append(",\n").append(generateIndent());
            }
        } else {
            prefix.append(",\n").append(generateIndent());
        }
        for (int i = 0; i < equalities.size(); i++) {
            Expression expression = equalities.get(i).clone();
            if (utility.setVariableDescriptions(expression, variables)) {
                if (useSaveFormat) {
                    String expressionString = expression.toSaveString();
                    if (with) {
                        expressionString = expressionString.replaceAll("==", "=");
                    }
                    localResult.append(expressionString);
                } else {
                    localResult.append(expression.toString());
                }
                if (i != equalities.size() - 1) {
                    localResult.append(", ");
                }
            }
        }
        if (!localResult.toString().isEmpty()) {
            localResult.insert(0, prefix.toString());
        }
        return localResult.toString();
    }

    private FormulaVariable findVariableForPath(VariablePathExpression attributePath, List<List<FormulaVariable>> variables) {
//        for (int i = variables.size() - 1; i >= 0; i--) {
        for (int i = 0; i < variables.size(); i++) {
            List<FormulaVariable> variableList = variables.get(i);
            for (FormulaVariable formulaVariable : variableList) {
                if (SpicyEngineUtility.containsPathWithSameVariableId(formulaVariable.getOriginalSourceOccurrencePaths(), attributePath)) {
                    return formulaVariable;
                }
            }
            for (FormulaVariable formulaVariable : variableList) {
                if (SpicyEngineUtility.containsPathWithSameVariableId(formulaVariable.getSourceOccurrencePaths(), attributePath)) {
                    return formulaVariable;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find variable for path " + attributePath + " in " + SpicyEngineUtility.printListOfVariableLists(variables));
    }

    private String generateAncestorId() {
        if (stack.size() < 2) {
            return "";
        }
        return stack.get(2).getId();
    }
}
