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
package it.unibas.spicy.model.mapping.rewriting.sourcenulls;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.GenericCombinationsGenerator;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class RewriteTgdsToHandleSourceNulls {

    private static Log logger = LogFactory.getLog(RewriteTgdsToHandleSourceNulls.class);
    private FindNullablePathsInSource pathFinder = new FindNullablePathsInSource();

    public List<FORule> rewrite(List<FORule> originalRules, IDataSourceProxy sourceProxy) {
        if (logger.isDebugEnabled()) logger.debug("Rewriting tgds for source nulls:\n" + SpicyEngineUtility.printCollection(originalRules));
        if (logger.isDebugEnabled()) logger.debug("Source proxy:\n" + sourceProxy.toString());
        PathClassification classification = pathFinder.classifyPaths(sourceProxy);
        List<FORule> result = new ArrayList<FORule>();
        for (FORule originalRule : originalRules) {
            result.addAll(generateRulesForCombinationsOfPaths(originalRule, classification));
        }
        if (logger.isDebugEnabled()) logger.debug("Final result:\n" + SpicyEngineUtility.printCollection(result));
        return result;
    }

    private List<FORule> generateRulesForCombinationsOfPaths(FORule originalRule, PathClassification classification) {
        List<FORule> result = new ArrayList<FORule>();
        List<VariablePathExpression> allPathsInLines = findAllPathsInLines(originalRule);
        List<VariablePathExpression> nullablePathsInLines = findNullablePathsInLines(allPathsInLines, classification);
        for (int i = 1; i <= nullablePathsInLines.size(); i++) {
            GenericCombinationsGenerator<VariablePathExpression> combinationsGenerator = new GenericCombinationsGenerator<VariablePathExpression>(nullablePathsInLines, i);
            while (combinationsGenerator.hasMoreElements()) {
                List<VariablePathExpression> combination = combinationsGenerator.nextElement();
                FORule ruleForCombination = generateTgdForCombination(originalRule, combination, allPathsInLines);
                if (ruleForCombination != null && !result.contains(ruleForCombination)) {
                    result.add(ruleForCombination);
                }
            }
        }
        result.add(generateRuleForAllAttributes(originalRule, nullablePathsInLines));
        return result;
    }

    private List<VariablePathExpression> findAllPathsInLines(FORule originalRule) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : originalRule.getCoveredCorrespondences()) {
            if (correspondence.isConstant()) {
                continue;
            }
            List<VariablePathExpression> sourcePaths = correspondence.getSourcePaths();
            for (VariablePathExpression sourcePath : sourcePaths) {
                if (!result.contains(sourcePath)) {
                    result.add(sourcePath);
                }
            }
        }
        return result;
    }

    private List<VariablePathExpression> findNullablePathsInLines(List<VariablePathExpression> allPathsInLines, PathClassification classification) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression sourcePath : allPathsInLines) {
            if (isNullable(sourcePath, classification)) {
                if (!result.contains(sourcePath)) {
                    result.add(sourcePath);
                }
            }
        }
        return result;
    }

    private boolean isNullable(VariablePathExpression sourcePath, PathClassification classification) {
        PathExpression absoluteSourcePathWithoutClones = SpicyEngineUtility.removeClonesFromAbsolutePath(sourcePath.getAbsolutePath());
        if (classification.getNullablePaths().contains(absoluteSourcePathWithoutClones) || 
                classification.getAlwaysNullPaths().contains(absoluteSourcePathWithoutClones)) {
                return true;
        }
        return false;
    }

    private FORule generateTgdForCombination(FORule originalRule, List<VariablePathExpression> combination, List<VariablePathExpression> allPathsInLines) {
        FORule rule = originalRule.clone();
        for (VariablePathExpression pathInCombination : combination) {
            VariableSelectionCondition conditionForPath = generateConditionForPath(pathInCombination, "isNull");
            rule.getSimpleSourceView().addSelectionCondition(conditionForPath);
        }
        for (VariablePathExpression otherPath : allPathsInLines) {
            if (combination.contains(otherPath)) {
                continue;
            }
            VariableSelectionCondition conditionForPath = generateConditionForPath(otherPath, "isNotNull");
            rule.getSimpleSourceView().addSelectionCondition(conditionForPath);
        }
        rule.getCoveredCorrespondences().clear();
        for (VariableCorrespondence correspondence : originalRule.getCoveredCorrespondences()) {
            if (!correspondence.isConstant() && hasNullableSourcePaths(correspondence, combination)) {
                continue;
            }
            rule.addCoveredCorrespondence(correspondence);
        }
        if (rule.getCoveredCorrespondences().isEmpty()) {
            return null;
        }
        return rule;
    }

    private VariableSelectionCondition generateConditionForPath(VariablePathExpression pathInCombination, String functionName) throws ExpressionSyntaxException {
//        VariablePathExpression setPath = pathInCombination.getStartingVariable().getBindingPathExpression();
//        Expression condition = new Expression(functionName + "(" + pathInCombination + ")");
//        VariableSelectionCondition conditionForPath = new VariableSelectionCondition(setPath, condition);
        Expression condition = new Expression(functionName + "(" + pathInCombination + ")");
        VariableSelectionCondition conditionForPath = new VariableSelectionCondition(condition);
        JEP jepExpression = condition.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        Variable variable = symbolTable.getVariables().get(0);
        variable.setDescription(pathInCombination);
        variable.setOriginalDescription(pathInCombination.getAbsolutePath());
        return conditionForPath;
    }

    private boolean hasNullableSourcePaths(VariableCorrespondence correspondence, List<VariablePathExpression> combination) {
        for (VariablePathExpression sourcePath : correspondence.getSourcePaths()) {
            if (combination.contains(sourcePath)) {
                return true;
            }
        }
        return false;
    }

    private FORule generateRuleForAllAttributes(FORule originalRule, List<VariablePathExpression> nullablePathsInLines) {
        FORule rule = originalRule.clone();
        for (VariablePathExpression nullablePath : nullablePathsInLines) {
            VariableSelectionCondition conditionForPath = generateConditionForPath(nullablePath, "isNotNull");
            rule.getSimpleSourceView().addSelectionCondition(conditionForPath);
        }
        return rule;
    }

}
