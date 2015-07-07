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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullValueGenerator;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.mapping.rewriting.CoverageAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.mapping.rewriting.FormulaPosition;
import it.unibas.spicy.model.mapping.rewriting.CoverageMap;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateXQueryForSourceToTargetExchange {

    private static Log logger = LogFactory.getLog(GenerateXQueryForSourceToTargetExchange.class);
    static final String INDENT = XQUtility.INDENT;
    static final String DOUBLE_INDENT = XQUtility.DOUBLE_INDENT;

    public String generateXQuery(MappingTask mappingTask) {
        StringBuilder result = new StringBuilder("\n");
        result.append(generateXQueryForExchange(mappingTask));
        return result.toString();
    }

    ///////////// SOURCE TO TARGET EXCHANGE //////////////////
    private String generateXQueryForExchange(MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        List<FORule> tgds = mappingTask.getMappingData().getRewrittenRules();
        result.append("\n(:----------------------------------  EXCHANGE -------------------------------:)\n\n");

        for (FORule tgd : tgds) {
            result.append(analyzeSourceView(tgd.getComplexSourceQuery(), mappingTask));
        }

//        for (FORule tgd : tgds) {
//            ComplexQueryWithNegations query = tgd.getComplexSourceQuery();
//            String viewName = XQNames.xQueryNameForView(query);
//            if (!GenerateXQuery.materializedViews.containsKey(query.getId())) {
////                result.append(materializeView(view, mappingTask)).append("\n");
//                GenerateXQuery.materializedViews.put(query.getId(), viewName);
//            }
//        }
//        result.append("\n(:------------------------------ APPLY FUNCTION VIEWS -----------------------------------:)\n\n");
//        for (FORule tgd : tgds) {
//            result.append("\n(:------------------------------  TGD  -----------------------------------:)\n\n");
//            result.append(generateApplyFunctionsForTGD(tgd)).append("\n");
//        }
//        result.append("\n(:------------------------------ COVERAGE VIEWS -----------------------------------:)\n\n");
//        if (mappingTask.getConfig().rewriteCoverages()) {
//            List<FORule> coverageTgds = mappingTask.getMappingData().getSTCoverageMap().getTgds();
//            for (FORule tgd : coverageTgds) {
//                List<Coverage> coverages = mappingTask.getMappingData().getSTCoverageMap().getCoverage(tgd);
//                for (Coverage coverage : coverages) {
//                    String coverageViewName = XQNames.xQueryNameForCoverage(coverage);
//                    if (!GenerateXQuery.materializedViews.containsKey(coverageViewName)) {
//                        result.append(generateCoverageView(coverage));
//                        materializedViews.add(coverageViewName);
//                    }
//                }
//            }
//        }
        result.append("\n(:------------------------------ TGDS -----------------------------------:)\n\n");
        result.append(materializeRules(mappingTask, tgds));

//        if (mappingTask.getConfig().rewriteCoverages() || mappingTask.getConfig().rewriteSubsumptions()) {
//            result.append(materializeDifferences(mappingTask));
//        }
        result.append(materializeResultOfExchange(mappingTask, tgds));
        return result.toString();
    }

    private String analyzeSourceView(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        if (GenerateXQuery.materializedViews.containsKey(query.getId())) {
            return "";
        }
        result.append(generatePositiveQuery(query, mappingTask));
        if (!query.getNegatedComplexQueries().isEmpty()) {
            for (NegatedComplexQuery negatedComplexQuery : query.getNegatedComplexQueries()) {
                result.append(analyzeSourceView(negatedComplexQuery.getComplexQuery(), mappingTask));
            }
            String viewName = XQNames.xQueryNameForViewWithIntersection(query);
            String positiveViewName = XQNames.xQueryNameForPositiveView(query);
            result.append(generateDifferenceForNegation(query, viewName, positiveViewName));
        }
        return result.toString();
    }

    //////// POSITIVE QUERY
    private String generatePositiveQuery(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        if (!GenerateXQuery.materializedViews.containsKey(query.getId())) {
            String viewName = XQNames.xQueryNameForPositiveView(query);

            result.append(INDENT).append(viewName).append(" := element set {\n");
            result.append(INDENT).append(XQBlocks.generateForEachBlockForView(query, mappingTask.getSourceProxy().getIntermediateSchema()));
            result.append(INDENT).append(generateWhereClauseForView(query));
            result.append(DOUBLE_INDENT).append("return\n");
            result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
            result.append(generateSimpleCopyValuesFromSource(query, mappingTask));
            result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
            result.append(INDENT).append("},\n");
            GenerateXQuery.materializedViews.put(query.getId(), viewName);
        }
        return result.toString();
    }

    private String generateDifferenceForNegation(ComplexQueryWithNegations query, String viewName, String positiveViewName) {
        StringBuilder result = new StringBuilder();
        result.append(INDENT).append(viewName).append(" := element set {\n");
        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(positiveViewName));
        result.append(DOUBLE_INDENT).append("let ");
        for (int i = 0; i < query.getNegatedComplexQueries().size(); i++) {
            NegatedComplexQuery negatedComplexQuery = query.getNegatedComplexQueries().get(i);
            result.append(addJoinForNegatedComplexQuery(negatedComplexQuery, i));
            if (i != query.getNegatedComplexQueries().size() - 1) result.append(",\n").append(DOUBLE_INDENT).append("    ");
        }
        result.append("\n");
        result.append(XQBlocks.generateReturnBlockForDifference(query.getNegatedComplexQueries().size()));
        result.append(",\n");
        GenerateXQuery.materializedViews.put(query.getId(), viewName);
        return result.toString();
    }

    private String findViewName(NegatedComplexQuery negation) {
        String fromViewName;
        ComplexQueryWithNegations negatedQuery = negation.getComplexQuery();
        if (GenerateXQuery.materializedViews.containsKey(negatedQuery.getId())) {
            return GenerateXQuery.materializedViews.get(negatedQuery.getId());
        }
        if (negatedQuery.getNegatedComplexQueries().isEmpty()) {// || isRewiQueryWithOnlySubsumeNegations(negatedQuery)) {
            fromViewName = XQNames.xQueryNameForPositiveView(negatedQuery);
        } else {
            fromViewName = XQNames.xQueryNameForViewWithIntersection(negatedQuery);
        }
        return fromViewName;
    }
    /////// GENERATE VIEWS
//    private String materializeView(SimpleConjunctiveQuery view, MappingTask mappingTask) {
//        StringBuilder result = new StringBuilder();
//        String viewName = XQNames.xQueryNameForView(view);
//        result.append(INDENT).append(viewName).append(" := element set {\n");
//        result.append(INDENT).append(XQBlocks.generateForEachBlockForView(view, mappingTask.getSourceProxy().getIntermediateSchema()));
//        result.append(INDENT).append(generateWhereClauseForView(view));
//        result.append(DOUBLE_INDENT).append("return\n");
//        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
//        result.append(generateSimpleCopyValuesFromSource(view, mappingTask));
//        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
//        result.append(INDENT).append("},\n");
//        return result.toString();
//    }
    ///////////////////////////   TGDs APPLY FUNCTIONS   ////////////////////////////////

    private String generateApplyFunctionsForTGD(FORule tgd) {
        StringBuilder result = new StringBuilder();
        String tgdViewName = XQNames.xQueryNameForApplyFunctions(tgd);
        String sourceViewName = XQNames.xQueryNameForView(tgd.getComplexSourceQuery());
        result.append(INDENT).append(tgdViewName).append(" := ");
        result.append("element set {\n");
        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(sourceViewName));
        result.append(DOUBLE_INDENT).append("return (\n");
        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
        result.append(generateCopyValuesFromCorrespondences(tgd.getCoveredCorrespondences()));
        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
        result.append(DOUBLE_INDENT).append(")\n");
        result.append(INDENT).append("}");
        result.append(INDENT).append(",");
        return result.toString();
    }

    ///////////////////////////   COVERAGES   ////////////////////////////////
//    private String generateCoverageView(Coverage coverage) {
//        StringBuilder result = new StringBuilder();
//        String coverageViewName = XQNames.xQueryNameForCoverage(coverage);
//        result.append(INDENT).append(coverageViewName).append(" := element set {\n");
//
//        result.append(INDENT).append(XQBlocks.generateForEachBlockForCoverage(coverage));
//        result.append(INDENT).append(generateWhereClauseForCoverage(coverage));
//        result.append(DOUBLE_INDENT).append("return\n");
//        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
//        result.append(DOUBLE_INDENT).append(generateCopyAllFromApplyFunctions(coverage));
//        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
//        result.append(INDENT).append("},\n");
//        return result.toString();
//    }
    private String generateCopyAllFromApplyFunctions(Coverage coverage) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < coverage.getCoveringAtoms().size(); i++) {
            FORule coveringTgd = coverage.getCoveringAtoms().get(i).getFirstCoveringAtom().getTgd();
            String tgdViewName = XQNames.xQueryNameForApplyFunctions(coveringTgd);
            result.append(tgdViewName).append("/*");
            if (i != coverage.getCoveringAtoms().size() - 1) result.append(", ");
        }

        return result.toString();
    }

    ////////////////////////////
    private String materializeRules(MappingTask mappingTask, List<FORule> rules) {
        StringBuilder result = new StringBuilder();
        Collections.sort(rules);
        for (int i = 0; i < rules.size(); i++) {
            FORule tgd = rules.get(i);
            result.append(materializeRule(tgd, mappingTask));
        }
        return result.toString();
    }

    private String materializeRule(FORule rule, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
//        if (isComplexRewriting(mappingTask)) {
        if (XQUtility.hasDifferences(rule)) {
            result.append(generateDifferenceForFinalRule(rule));
//            result.append(",\n");
        }
//        } else {
//            if (XQUtility.hasDifferences(rule)) {
//                result.append(generateDifferenceForNegationNoSelfJoins(rule, rule.getComplexSourceQuery()));
//            }
//        }
        result.append("\n");
        ////// TODO: verificare se e' possibile unire questi due metodi
        if (XQUtility.hasDifferences(rule)) {
            result.append(generateTargetValueViewWithDifference(rule, mappingTask));
        } else {
            result.append(generateTargetValueViewWithoutDifference(rule, mappingTask));
        }
        return result.toString();

//        StringBuilder result = new StringBuilder();
//        String newViewName = XQNames.xQueryNameForTgd(rule);
//        String fromViewName = XQNames.xQueryNameForApplyFunctions(rule);
//        result.append(INDENT).append(newViewName).append(" := element set {\n");
//        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(fromViewName));
//        result.append(DOUBLE_INDENT).append("return (\n");
//        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
//        List<String> addedElements = new ArrayList<String>();
//        List<FormulaAtom> atoms = rule.getTargetFormulaAtoms(mappingTask);
//        boolean addedSomeElement = false;
//        for (int i = 0; i < atoms.size(); i++) {
//            FormulaAtom atom = atoms.get(i);
//            SetAlias variable = atom.getVariable();
//            List<FormulaPosition> positions = atom.getPositions();
//            for (int j = 0; j < positions.size(); j++) {
//                FormulaPosition position = positions.get(j);
//                VariablePathExpression targetPath = position.getPathExpression();
//                String elementName = XQNames.xQueryNameForPath(targetPath);
//                if (!addedElements.contains(elementName)) {
//                    addedSomeElement = true;
//                    addedElements.add(elementName);
//                    IValueGenerator generator = getLeafGenerator(targetPath, rule, mappingTask, variable);
//                    result.append(DOUBLE_INDENT).append(DOUBLE_INDENT).append("element ").append(elementName).append(" {");
//                    result.append(XQUtility.xqueryValueForLeaf(generator, targetPath, mappingTask));
//                    result.append("}");
//                    result.append(",\n");
//                }
//            }
//            if (addedSomeElement) result.replace(result.length() - 2, result.length(), " ");
//            if (i != atoms.size() - 1) {
//                if (checkNextAtom(atoms.get(i + 1), addedElements)) {
//                    result.append(",");
//                }
//            }
//            result.append("\n");
//        }
//        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
//        result.append(DOUBLE_INDENT).append(")\n");
//        result.append(INDENT).append("}");
//        return result.toString();
    }

    private boolean isComplexRewriting(MappingTask mappingTask) {
        if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()
                || (mappingTask.getLoadedTgds() != null) && hasLoadedTgdsWithNegation(mappingTask)) {
            return true;
        }
        return false;
    }

    private boolean hasLoadedTgdsWithNegation(MappingTask mappingTask) {
        for (FORule rule : mappingTask.getLoadedTgds()) {
            ComplexQueryWithNegations sourceView = rule.getComplexSourceQuery();
            if (!sourceView.getNegatedComplexQueries().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String generateDifferenceForFinalRule(FORule rule) {
        String viewName = XQNames.xQueryNameForTgd(rule);
        String positiveViewName = "";
        positiveViewName = XQNames.xQueryNameForViewWithIntersection(rule.getComplexSourceQuery());
        return generateDifferenceForNegation(rule.getComplexSourceQuery(), viewName, positiveViewName);
    }

    private boolean checkNextAtom(FormulaAtom atom, List<String> addedElements) {
        List<FormulaPosition> positions = atom.getPositions();
        for (FormulaPosition position : positions) {
            VariablePathExpression targetPath = position.getPathExpression();
            String elementName = XQNames.xQueryNameForPath(targetPath);
            if (!addedElements.contains(elementName)) {
                return true;
            }
        }
        return false;
    }

    private String generateTargetValueViewWithDifference(FORule rule, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String fromViewName = XQNames.xQueryNameForTgd(rule);
        String viewName = XQNames.xQueryFinalTgdName(rule);
        result.append(INDENT).append(viewName).append(" := element set {\n");
        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(fromViewName));
        result.append(DOUBLE_INDENT).append("return (\n");
        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
        result.append(projectionOnValues(mappingTask, rule));
        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
        result.append(DOUBLE_INDENT).append(")\n");
        result.append(INDENT).append("},\n");
//        result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
//        String fromClause = "from " + GenerateSQL.sqlNameForRule(rule);
//        String whereClause = "";
//        result.append(projectionOnValues(mappingTask, rule, fromClause, whereClause));
//        result.append(";\n");
        return result.toString();

    }

    private String generateTargetValueViewWithoutDifference(FORule rule, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String fromViewName = "";
        if (rule.getComplexSourceQuery().getComplexQuery().hasIntersection()) {
            fromViewName = XQNames.xQueryNameForViewWithIntersection(rule.getComplexSourceQuery());
        } else {
            fromViewName = XQNames.xQueryNameForPositiveView(rule.getComplexSourceQuery());
        }
        String viewName = XQNames.xQueryFinalTgdName(rule);
        result.append(INDENT).append(viewName).append(" := element set {\n");
        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(fromViewName));
        result.append(DOUBLE_INDENT).append("return (\n");
        result.append(DOUBLE_INDENT).append(INDENT).append("element tuple {\n");
        result.append(projectionOnValues(mappingTask, rule));
        result.append(DOUBLE_INDENT).append(INDENT).append("}\n");
        result.append(DOUBLE_INDENT).append(")\n");
        result.append(INDENT).append("},\n");
//        result.append(CREATE_TABLE_OR_VIEW).append(viewName).append(" AS \n");
//        String fromClause = "from " + GenerateSQL.sqlNameForRule(rule);
//        String whereClause = "";
//        result.append(projectionOnValues(mappingTask, rule, fromClause, whereClause));
//        result.append(";\n");
        return result.toString();
    }

    private String materializeDifferences(MappingTask mappingTask) {
        if (logger.isTraceEnabled()) logger.trace("Materializing Differences...");
        StringBuilder result = new StringBuilder();
        List<FORule> tgds = mappingTask.getMappingData().getSTTgds();
        Collections.sort(tgds);
        for (int i = 0; i < tgds.size(); i++) {
            FORule rule = tgds.get(i);
            if (XQUtility.hasDifferences(rule)) {
                result.append(materializeDifference(rule, mappingTask)).append("\n");
            }
        }
        return result.toString();
    }

    private String materializeDifference(FORule tgd, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        String diffViewName = XQNames.xqueryNameForDifference(tgd);
        String fromViewName = XQNames.xQueryNameForTgd(tgd);
        result.append(INDENT).append(diffViewName).append(" := element set {\n");
        result.append(INDENT).append(XQBlocks.generateForEachBlockForProjections(fromViewName));
        result.append(DOUBLE_INDENT).append("let ");
        SubsumptionMap subsumptionDag = mappingTask.getMappingData().getSTSubsumptionMap();
        List<Subsumption> subsumptions = subsumptionDag.getFathers(tgd);
        CoverageMap coverageMap = mappingTask.getMappingData().getSTCoverageMap();
        List<Coverage> coverages = coverageMap.getCoverage(tgd);
        if (mappingTask.getConfig().rewriteSubsumptions()) {
            for (int i = 0; i < subsumptions.size(); i++) {
                Subsumption subsumption = subsumptions.get(i);
                result.append(addJoinForSubsumption(subsumption, i));
                if (i != subsumptions.size() - 1 || (coverages.size() > 0 && mappingTask.getConfig().rewriteCoverages())) result.append(", ");
            }
        }
        if (mappingTask.getConfig().rewriteCoverages()) {
            for (int i = 0; i < coverages.size(); i++) {
                Coverage coverage = coverages.get(i);
                result.append(addJoinForCoverage(coverage, i));
                if (i != coverages.size() - 1) result.append(", ");
            }
        }
        result.append(XQBlocks.generateReturnBlockForDifference(mappingTask, subsumptions.size(), coverages.size()));
        result.append(",\n");
        return result.toString();
    }

    private String addJoinForNegatedComplexQuery(NegatedComplexQuery negatedComplexQuery, int i) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.JOIN_DIFFERENCE).append(i).append(" := ");
        String negationViewName = findViewName(negatedComplexQuery);
        result.append(negationViewName).append("/tuple");
        result.append(generateAttributesForDifference(negatedComplexQuery));
        return result.toString();
    }

    private String generateAttributesForDifference(NegatedComplexQuery negation) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        if (negation.isTargetDifference()) {
            for (int i = 0; i < negation.getTargetEqualities().getLeftCorrespondences().size(); i++) {
                VariableCorrespondence leftCorrespondence = negation.getTargetEqualities().getLeftCorrespondences().get(i);
                VariableCorrespondence rightCorrespondence = negation.getTargetEqualities().getRightCorrespondences().get(i);
                VariablePathExpression leftPath = leftCorrespondence.getFirstSourcePath();
                VariablePathExpression rightPath = rightCorrespondence.getFirstSourcePath();
                String leftPathName = XQNames.xQueryNameForPath(leftPath);
                String rightPathName = XQNames.xQueryNameForPath(rightPath);
                result.append("./").append(rightPathName).append(" = ").append(XQUtility.VARIABLE).append("/").append(leftPathName);
                if (i != negation.getTargetEqualities().getLeftCorrespondences().size() - 1) result.append(" and ");
            }
        } else {
            for (int i = 0; i < negation.getSourceEqualities().getLeftPaths().size(); i++) {
                VariablePathExpression leftPath = negation.getSourceEqualities().getLeftPaths().get(i);
                VariablePathExpression rightPath = negation.getSourceEqualities().getRightPaths().get(i);
                String leftPathName = XQNames.xQueryNameForPath(leftPath);
                String rightPathName = XQNames.xQueryNameForPath(rightPath);
                result.append("./").append(rightPathName).append(" = ").append(XQUtility.VARIABLE).append("/").append(leftPathName);
                if (i != negation.getSourceEqualities().getLeftPaths().size() - 1) result.append(" and ");
            }
        }
        result.append("]");
        return result.toString();
    }

    private String addJoinForSubsumption(Subsumption subsumption, int i) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.JOIN_DIFFERENCE).append(i).append(" := ");
        String fromViewName = XQNames.xQueryNameForApplyFunctions(subsumption.getRightTgd());
        result.append(fromViewName).append("/tuple");
        List<VariablePathExpression> leftPaths = XQUtility.extractTargetPaths(subsumption.getLeftCorrespondences());
        List<VariablePathExpression> rightPaths = XQUtility.extractTargetPaths(subsumption.getRightCorrespondences());
        result.append(generateAttributesForDifference(leftPaths, rightPaths));
        return result.toString();
    }

    private String addJoinForCoverage(Coverage coverage, int i) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.JOIN_DIFFERENCE).append(i).append(" := ");
        String fromViewName = XQNames.xQueryNameForCoverage(coverage);
        result.append(fromViewName).append("/tuple");
        List<VariablePathExpression> leftPaths = XQUtility.extractTargetPaths(getOriginalCorrespondences(coverage));
        List<VariablePathExpression> rightPaths = XQUtility.extractTargetPaths(getMatchingCorrespondences(coverage));
        result.append(generateAttributesForDifference(leftPaths, rightPaths));
        return result.toString();
    }

    private List<VariableCorrespondence> getOriginalCorrespondences(Coverage coverage) {
        List<VariableCorrespondence> originalCorrespondences = new ArrayList<VariableCorrespondence>();
        for (CoverageAtom atomCoverage : coverage.getCoveringAtoms()) {
            originalCorrespondences.addAll(atomCoverage.getOriginalCorrespondences());
        }
        return originalCorrespondences;
    }

    private List<VariableCorrespondence> getMatchingCorrespondences(Coverage coverage) {
        List<VariableCorrespondence> matchingCorrespondences = new ArrayList<VariableCorrespondence>();
        for (CoverageAtom atomCoverage : coverage.getCoveringAtoms()) {
            matchingCorrespondences.addAll(atomCoverage.getMatchingCorrespondences());
        }
        return matchingCorrespondences;
    }

    private String generateAttributesForDifference(List<VariablePathExpression> leftPaths, List<VariablePathExpression> rightPaths) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < leftPaths.size(); i++) {
            VariablePathExpression leftPath = leftPaths.get(i);
            VariablePathExpression rightPath = rightPaths.get(i);
            String leftPathName = XQNames.xQueryNameForPath(leftPath);
            String rightPathName = XQNames.xQueryNameForPath(rightPath);
            result.append("./").append(rightPathName).append(" = ").append(XQUtility.VARIABLE).append("/").append(leftPathName);
            if (i != leftPaths.size() - 1) result.append(" and ");
        }
        result.append("]\n");
        return result.toString();
    }

    private String generateCopyValuesFromCorrespondences(List<VariableCorrespondence> correspondences) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < correspondences.size(); i++) {
            VariableCorrespondence correspondence = correspondences.get(i);
            result.append(DOUBLE_INDENT).append(DOUBLE_INDENT).append(XQNames.xQueryNameForCorrespondence(correspondence));
            if (i != correspondences.size() - 1) result.append(",");
            result.append("\n");
        }
        return result.toString();
    }

    private String generateSimpleCopyValuesFromSource(ComplexQueryWithNegations query, MappingTask mappingTask) {
        StringBuilder resultString = new StringBuilder();
        List<VariablePathExpression> sourceAttributes = extractAttributePaths(query, mappingTask);
        for (int i = 0; i < sourceAttributes.size(); i++) {
            VariablePathExpression attributePath = sourceAttributes.get(i);
            resultString.append(DOUBLE_INDENT).append(DOUBLE_INDENT).append("element ").append(XQNames.xQueryNameForPath(attributePath)).append(" {");
            INode attributeNode = attributePath.getLastNode(mappingTask.getSourceProxy().getIntermediateSchema());
            if (attributeNode instanceof MetadataNode) {
                resultString.append(XQUtility.createXQueryRelativePathForMetadataNode(attributePath, attributeNode));
            } else if (attributeNode.isVirtual()) {
                resultString.append(XQUtility.createXQueryRelativePathForVirtualAttribute(attributePath, attributeNode));
            } else if (isSingleAttributeWithVirtualFathers((AttributeNode) attributeNode)) {
                resultString.append(XQUtility.createXQueryRelativePathForSingleAttribute(attributePath, attributeNode));
            } else {
                resultString.append(XQUtility.createXQueryRelativePath(attributePath));
            }
            resultString.append("}");
            if (i != sourceAttributes.size() - 1) resultString.append(",");
            resultString.append("\n");
        }
        return resultString.toString();
    }

    private List<VariablePathExpression> extractAttributePaths(ComplexQueryWithNegations query, MappingTask mappingTask) {
        List<VariablePathExpression> attributePaths = new ArrayList<VariablePathExpression>();
        for (SimpleConjunctiveQuery simpleConjunctiveQuery : query.getComplexQuery().getConjunctions()) {
            attributePaths.addAll(simpleConjunctiveQuery.getAttributePaths(mappingTask.getSourceProxy().getIntermediateSchema()));
        }
        return attributePaths;
    }

    private boolean isSingleAttributeWithVirtualFathers(AttributeNode attributeNode) {
        INode father = attributeNode.getFather();
        INode ancestor = father.getFather();
        return ((father instanceof TupleNode) && father.getChildren().size() == 1 && father.isVirtual()
                && (ancestor instanceof SetNode) && ancestor.isVirtual());
    }

    private String generateWhereClauseForCoverage(Coverage coverage) {
        StringBuilder result = new StringBuilder();
        List<VariableJoinCondition> joinConditions = coverage.getTargetJoinConditions();
        if (!joinConditions.isEmpty()) {
            result.append(INDENT).append("where ");
        }
        result.append(generateWhereContentFromJoinConditionsForCoverage(joinConditions, coverage));
        result.append("\n");
        return result.toString();
    }

    private String generateWhereContentFromJoinConditionsForCoverage(List<VariableJoinCondition> joinConditions, Coverage coverage) {
        StringBuilder result = new StringBuilder();
        if (!joinConditions.isEmpty()) {
            for (int i = 0; i < joinConditions.size(); i++) {
                VariableJoinCondition joinCondition = joinConditions.get(i);
                List<VariablePathExpression> fromPaths = joinCondition.getFromPaths();
                List<VariablePathExpression> toPaths = joinCondition.getToPaths();
                for (int j = 0; j < fromPaths.size(); j++) {
                    VariablePathExpression fromPath = fromPaths.get(j);
                    VariablePathExpression toPath = toPaths.get(j);
                    result.append(XQUtility.createXQueryRelativePathForCoverage(fromPath, coverage));
                    result.append(" = ");
                    result.append(XQUtility.createXQueryRelativePathForCoverage(toPath, coverage));
                    if (j != fromPaths.size() - 1) result.append(" and ");
                }
                if ((i != joinConditions.size() - 1)) result.append(" and ");
            }
        }
        return result.toString();
    }

    private String generateWhereClauseForView(ComplexQueryWithNegations query) {
        StringBuilder result = new StringBuilder();
        List<VariableSelectionCondition> selectionConditions = query.getComplexQuery().getAllSelections();
        List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>(query.getComplexQuery().getJoinConditions());
        for (SimpleConjunctiveQuery simpleConjunctiveQuery : query.getComplexQuery().getConjunctions()) {
            joinConditions.addAll(simpleConjunctiveQuery.getAllJoinConditions());
        }
        if (!joinConditions.isEmpty() || !selectionConditions.isEmpty() || query.getComplexQuery().hasIntersection()) {
            result.append(INDENT).append("where ");
        }
        boolean hasSelectionConditions = !selectionConditions.isEmpty();
        result.append(generateWhereContentFromJoinConditions(joinConditions, query.getComplexQuery().getAllCorrespondences(), hasSelectionConditions, query.getComplexQuery().hasIntersection()));
        result.append(generateWhereContentFromSelectionConditions(selectionConditions, query.getComplexQuery().hasIntersection()));
        result.append(generateWhereContentFromIntersection(query.getComplexQuery()));
        result.append("\n");
        return result.toString();
    }

    private String generateWhereContentFromIntersection(ComplexConjunctiveQuery view) {
        StringBuilder result = new StringBuilder();
        if (view.hasIntersection()) {
            List<VariablePathExpression> leftIntersectionPaths = generateTargetPaths(view.getIntersectionEqualities().getLeftCorrespondences());
            List<VariablePathExpression> rightIntersectionPaths = generateTargetPaths(view.getIntersectionEqualities().getRightCorrespondences());
            List<VariableCorrespondence> allCorrespondences = new ArrayList<VariableCorrespondence>();
            allCorrespondences.addAll(view.getIntersectionEqualities().getLeftCorrespondences());
            allCorrespondences.addAll(view.getIntersectionEqualities().getRightCorrespondences());

            for (int i = 0; i < leftIntersectionPaths.size(); i++) {
                VariablePathExpression leftPath = leftIntersectionPaths.get(i);
                VariablePathExpression rightPath = rightIntersectionPaths.get(i);
                VariablePathExpression leftSourcePath = findSourcePathWithEqualsId(allCorrespondences, leftPath);
                if (leftSourcePath == null) {
                    leftSourcePath = leftPath;
                }
                VariablePathExpression rightSourcePath = findSourcePathWithEqualsId(allCorrespondences, rightPath);
                if (rightSourcePath == null) {
                    rightSourcePath = rightPath;
                }
                result.append(XQUtility.createXQueryRelativePath(leftSourcePath));
                result.append(" = ");
                result.append(XQUtility.createXQueryRelativePath(rightSourcePath));

                if (i != leftIntersectionPaths.size() - 1) result.append(" and\n");
            }
        }
        return result.toString();
    }

    private static List<VariablePathExpression> generateTargetPaths(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : correspondences) {
            result.add(correspondence.getTargetPath());
        }
        return result;
    }

    private static VariablePathExpression findSourcePathWithEqualsId(List<VariableCorrespondence> correspondences, VariablePathExpression targetPath) {
        for (VariableCorrespondence variableCorrespondence : correspondences) {
            if (variableCorrespondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return variableCorrespondence.getFirstSourcePath();
            }
        }
        return null;
    }

    private String generateWhereContentFromJoinConditions(List<VariableJoinCondition> joinConditions, List<VariableCorrespondence> correspondences, boolean hasSelectionConditions, boolean hasIntersection) {
        StringBuilder result = new StringBuilder();
//        if (!joinConditions.isEmpty()) {
        for (int i = 0; i < joinConditions.size(); i++) {
            VariableJoinCondition joinCondition = joinConditions.get(i);
            List<VariablePathExpression> fromPaths = joinCondition.getFromPaths();
            List<VariablePathExpression> toPaths = joinCondition.getToPaths();
            for (int j = 0; j < fromPaths.size(); j++) {

                VariablePathExpression fromPath = fromPaths.get(j);
                VariablePathExpression toPath = toPaths.get(j);
                VariablePathExpression fromSourcePath = XQUtility.findSourcePath(correspondences, fromPath);
                if (fromSourcePath == null) {
                    fromSourcePath = fromPath;
                }
                VariablePathExpression toSourcePath = XQUtility.findSourcePath(correspondences, toPath);
                if (toSourcePath == null) {
                    toSourcePath = toPath;
                }
                result.append(XQUtility.createXQueryRelativePath(fromSourcePath)).append(" = ").append(XQUtility.createXQueryRelativePath(toSourcePath));
                if (j != fromPaths.size() - 1) result.append(" and ");
            }
            if ((i != joinConditions.size() - 1) || hasSelectionConditions || hasIntersection) result.append(" and ");
        }
//        }
        return result.toString();
    }

    private String generateWhereContentFromSelectionConditions(List<VariableSelectionCondition> selectionConditions, boolean hasIntersection) {
        StringBuilder result = new StringBuilder();
        if (!selectionConditions.isEmpty()) {
            result.append("\n").append(DOUBLE_INDENT);
            for (int i = 0; i < selectionConditions.size(); i++) {
                VariableSelectionCondition selectionCondition = selectionConditions.get(i);
                result.append(XQUtility.xQueryStringForExpression(selectionCondition.getCondition()));
                if ((i != selectionConditions.size() - 1) || hasIntersection) result.append(" and ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private List<VariableSelectionCondition> getSelectionConditions(SimpleConjunctiveQuery view) {
        List<VariableSelectionCondition> selectionConditions = new ArrayList<VariableSelectionCondition>();
        for (SetAlias variable : view.getVariables()) {
            for (SetAlias generator : variable.getGenerators()) {
                for (VariableSelectionCondition conditionToAdd : generator.getSelectionConditions()) {
                    if (!selectionConditions.contains(conditionToAdd)) {
                        selectionConditions.add(conditionToAdd);
                    }
                }
            }
        }
        return selectionConditions;
    }

    private String materializeResultOfExchange(MappingTask mappingTask, List<FORule> tgds) {
        if (logger.isTraceEnabled()) logger.trace("Materializing Result of S-T Exchange...");
        StringBuilder result = new StringBuilder();
        result.append("\n(:-----------------------  RESULT OF EXCHANGE ---------------------------:)\n\n");
        List<SetAlias> targetVariables = mappingTask.getTargetProxy().getMappingData().getVariables();
        for (int i = 0; i < targetVariables.size(); i++) {
            SetAlias targetVariable = targetVariables.get(i);
            List<FORule> relevantTGDs = findRelevantTGDs(targetVariable, mappingTask.getMappingData().getRewrittenRules());
            if (!relevantTGDs.isEmpty()) {
                String viewName = XQNames.finalXQueryNameSTExchange(targetVariable);
                /// TODO: check
                GenerateXQuery.materializedViews.put("" + targetVariable.getId(), viewName);
                result.append(generateBlockForShredding(targetVariable, mappingTask, relevantTGDs, viewName));
                result.append(",\n");
            }
        }
        result.replace(result.length() - 2, result.length(), "\n");
        return result.toString();
    }

    private String projectionOnValues(MappingTask mappingTask, FORule tgd) {
        StringBuilder result = new StringBuilder();
        List<VariablePathExpression> generatedAttributes = new ArrayList<VariablePathExpression>();
        
        List<SetAlias> generators = tgd.getTargetView().getGenerators();
        for (int i = 0; i < generators.size(); i++) {
            SetAlias generator = generators.get(i);
            List<VariablePathExpression> attributes = generator.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
            for (int j = 0; j < attributes.size(); j++) {
                VariablePathExpression attribute = attributes.get(j);
                if (generatedAttributes.contains(attribute)) {
                    continue;
                }
                generatedAttributes.add(attribute);
                IValueGenerator leafGenerator = getLeafGenerator(attribute, tgd, mappingTask, generator);

                String elementName = XQNames.xQueryNameForPath(attribute);
                result.append(DOUBLE_INDENT).append(DOUBLE_INDENT).append("element ").append(elementName).append(" {");
                result.append(XQUtility.xqueryValueForLeaf(leafGenerator, attribute, tgd, mappingTask));
                result.append("}");
                result.append(",\n");
            }
        }
        result.replace(result.length() - 2, result.length(), " ");
        result.append("\n");
        return result.toString();
    }

    private IValueGenerator getLeafGenerator(VariablePathExpression attributePath, FORule tgd, MappingTask mappingTask, SetAlias variable) {
        INode attributeNode = attributePath.getLastNode(mappingTask.getTargetProxy().getIntermediateSchema());
        INode leafNode = attributeNode.getChild(0);
        PathExpression leafPath = new GeneratePathExpression().generatePathFromRoot(leafNode);
        Map<PathExpression, IValueGenerator> generatorsForVariable = tgd.getGenerators(mappingTask).getGeneratorsForVariable(variable);
//        //** added to avoid exceptions in XML scenarios
        if (generatorsForVariable == null) {
            return NullValueGenerator.getInstance();
        }
        for (PathExpression pathExpression : generatorsForVariable.keySet()) {
            IValueGenerator generator = generatorsForVariable.get(pathExpression);
            if (pathExpression.equalsUpToClones(leafPath)) {
                return generator;
            }
        }
        return null;
    }

//    private List<FORule> findRelevantTGDs(SetAlias targetVariable, MappingTask mappingTask) {
//        List<FORule> tgds = mappingTask.getMappingData().getRewrittenRules();
//        Collections.sort(tgds);
//        List<FORule> result = new ArrayList<FORule>();
//        for (int i = tgds.size() - 1; i >= 0; i--) {
//            FORule tgd = tgds.get(i);
//            if (logger.isDebugEnabled()) logger.debug("++ Analyzing tgd: " + tgd);
//            if (isRelevant(targetVariable, tgd.getTargetView().getVariables())) {
//                if (logger.isDebugEnabled()) logger.debug("++ Tgd is relevant, adding...");
//                result.add(tgd);
//            }
//        }
//        return result;
//    }

    private List<FORule> findRelevantTGDs(SetAlias targetVariable, List<FORule> tgds) {
        Collections.sort(tgds);
        List<FORule> result = new ArrayList<FORule>();
        for (FORule tgd : tgds) {
            if (logger.isDebugEnabled()) logger.debug("GENERATORS: " + tgd.getTargetView().getGenerators());
            if (logger.isDebugEnabled()) logger.debug("Searching variable: " + targetVariable.toShortString());
            if (tgd.getTargetView().getGenerators().contains(targetVariable)) {
                if (logger.isDebugEnabled()) logger.debug("Adding variable");
                result.add(tgd);
            }
        }
        return result;
    }

    private boolean isRelevant(SetAlias variable, List<SetAlias> variables) {
        for (SetAlias setVariable : variables) {
            //if (variable.equals(setVariable)){
            if (variable.getAbsoluteBindingPathExpression().equals(setVariable.getAbsoluteBindingPathExpression())) {
                return true;
            }
        }
        return false;
    }
    private String generateBlockForShredding(SetAlias targetVariable, MappingTask mappingTask, List<FORule> relevantTgds, String viewName) {
        StringBuilder result = new StringBuilder();
        result.append(INDENT).append(viewName).append(" := ");
        result.append(XQUtility.REMOVE_DUPLICATES_PREFIX);
        result.append("element set {\n");
        result.append(XQBlocks.generateForEachBlockForSTResult(targetVariable, relevantTgds, mappingTask));
        result.append(INDENT).append("}");
        result.append(")");
        return result.toString();
    }
}
