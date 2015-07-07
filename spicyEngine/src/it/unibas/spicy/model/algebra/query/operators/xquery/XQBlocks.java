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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XQBlocks {

    private static Log logger = LogFactory.getLog(XQBlocks.class);

    public static String generateForEachBlockForProjections(String fromViewName) {
        StringBuilder foreachBlock = new StringBuilder();
        foreachBlock.append(XQUtility.INDENT).append("for ").append(XQUtility.VARIABLE).append(" in ").append(fromViewName).append("/tuple").append("\n");
        return foreachBlock.toString();
    }

    public static String generateReturnBlockForDifference(int numberOfDifferences) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.DOUBLE_INDENT).append("return (\n");
        result.append(XQUtility.DOUBLE_INDENT).append("if (");
        for (int i = 0; i < numberOfDifferences; i++) {
            result.append("empty(").append(XQUtility.JOIN_DIFFERENCE).append(i).append(")");
            if ((i != numberOfDifferences - 1)) result.append(" and ");
        }
        result.append(")\n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.INDENT).append("then \n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.INDENT).append("element tuple \n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("{ $variable/* }\n");
        result.append(XQUtility.DOUBLE_INDENT).append("else ()\n");
        result.append(XQUtility.DOUBLE_INDENT).append(")\n");
        result.append(XQUtility.INDENT).append("}");
        if (numberOfDifferences == 0) result.append(",\n");
        return result.toString();
    }

    public static String generateReturnBlockForDifference(MappingTask mappingTask, int numberOfSubsumptions, int numberOfCoverages) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.DOUBLE_INDENT).append("return (\n");
        result.append(XQUtility.DOUBLE_INDENT).append("if (");
        if (mappingTask.getConfig().rewriteSubsumptions()) {
            for (int i = 0; i < numberOfSubsumptions; i++) {
                result.append("empty(").append(XQUtility.JOIN_DIFFERENCE).append(i).append(")");
                if ((i != numberOfSubsumptions - 1) || (numberOfCoverages > 0 && mappingTask.getConfig().rewriteCoverages())) result.append(" and ");
            }
        }
        if (mappingTask.getConfig().rewriteCoverages()) {
            for (int i = 0; i < numberOfCoverages; i++) {
                result.append("empty(").append(XQUtility.JOIN_DIFFERENCE).append(i).append(")");
                if (i != numberOfCoverages - 1) result.append(" and ");
            }
        }
        result.append(")\n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.INDENT).append("then \n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.INDENT).append("element tuple \n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("{ $variable/* }\n");
        result.append(XQUtility.DOUBLE_INDENT).append("else ()\n");
        result.append(XQUtility.DOUBLE_INDENT).append(")\n");
        result.append(XQUtility.INDENT).append("}");
        if (numberOfSubsumptions == 0 && numberOfCoverages == 0) result.append(",\n");
        return result.toString();
    }

    public static String generateForEachBlockForView(ComplexQueryWithNegations query, INode schemaNode) {
        StringBuilder foreachBlock = new StringBuilder();
        List<SetAlias> sourceVariables = query.getVariables();
        List<SetAlias> viewVariables = new ArrayList<SetAlias>();
        foreachBlock.append(XQUtility.INDENT).append("for \n");
        for (int i = 0; i < sourceVariables.size(); i++) {
            SetAlias variable = sourceVariables.get(i);
            foreachBlock.append(XQUtility.DOUBLE_INDENT).append(XQForVariable.toXQueryString(variable, viewVariables, schemaNode));
            if (i != sourceVariables.size() - 1) foreachBlock.append(",");
            foreachBlock.append("\n");
        }
        return foreachBlock.toString();
    }

    public static String generateForEachBlockForCoverage(Coverage coverage) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.INDENT).append("for \n");
        result.append(XQUtility.DOUBLE_INDENT);
        for (int i = 0; i < coverage.getCoveringAtoms().size(); i++) {
            FORule coveringTgd = coverage.getCoveringAtoms().get(i).getFirstCoveringAtom().getTgd();
            String tgdViewName = XQNames.xQueryNameForApplyFunctions(coveringTgd);
            result.append(tgdViewName).append(" in ").append(tgdViewName).append("/tuple");
            if (i != coverage.getCoveringAtoms().size() - 1) result.append(", ");
        }
        result.append("\n");
        return result.toString();
    }

    public static String generateForEachBlockForSTResult(SetAlias targetVariable, List<FORule> relevantTgds, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < relevantTgds.size(); i++) {
            FORule rule = relevantTgds.get(i);
//            String fromViewName = XQUtility.findViewName(tgd, mappingTask);
            String fromViewName = XQNames.xQueryFinalTgdName(rule);
            result.append(XQUtility.INDENT).append("for $variable in ").append(fromViewName).append("/tuple\n");
            result.append(generateReturnClauseForSTResult(targetVariable, mappingTask, rule));
            if (i != relevantTgds.size() - 1) result.append(",");
            result.append("\n");
        }
        return result.toString();
    }

    private static String generateReturnClauseForSTResult(SetAlias targetVariable, MappingTask mappingTask, FORule tgd) {
        StringBuilder result = new StringBuilder();
        result.append(XQUtility.INDENT).append("return (\n");
        result.append(XQUtility.DOUBLE_INDENT).append("element tuple {\n");
        result.append(generateCopyValuesForSTResult(targetVariable, mappingTask, tgd));
        result.append(XQUtility.DOUBLE_INDENT).append("}\n");
        result.append(XQUtility.INDENT).append(")");
        return result.toString();
    }

    private static String generateCopyValuesForSTResult(SetAlias targetVariable, MappingTask mappingTask, FORule tgd) {
        StringBuilder result = new StringBuilder();
        SetNode setNode = targetVariable.getBindingNode(mappingTask.getTargetProxy().getIntermediateSchema());
        if (logger.isDebugEnabled()) logger.debug("Set node: " + setNode);
        INode tupleNode = setNode.getChild(0);
        if (logger.isDebugEnabled()) logger.debug("Tuple node: " + tupleNode);
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("element ").append(XQUtility.TUPLE_ID).append(" {");
        result.append(createIdFromGenerators(tupleNode, mappingTask, tgd, false, targetVariable));
        result.append("},\n");
        result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("element ").append(XQUtility.SET_ID).append(" {");
        result.append(createIdFromGenerators(setNode, mappingTask, tgd, false, targetVariable));
        result.append("},\n");
        List<VariablePathExpression> targetPaths = targetVariable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema());
        List<INode> setNodeChildren = findSetChildren(tupleNode);
        if (logger.isDebugEnabled()) logger.debug("Set node children: " + setNodeChildren);
        // generate copy values from tgd view
        for (int i = 0; i < targetPaths.size(); i++) {
            VariablePathExpression targetPath = targetPaths.get(i);
            if (targetPath.getStartingVariable().equals(targetVariable)) {
                result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("element ");
                String targetPathName = XQNames.xQueryNameForPath(targetPath);
                result.append(targetPathName).append(" {").append(XQUtility.VARIABLE).append("/").append(targetPathName).append("/text()");
                result.append("}");
                if ((i != targetPaths.size() - 1) || !setNodeChildren.isEmpty()) result.append(",");
                result.append("\n");
            }
        }
        // add the set id of all children sets
        for (int i = 0; i < setNodeChildren.size(); i++) {
            SetNode targetNode = (SetNode) setNodeChildren.get(i);
            if (logger.isDebugEnabled()) logger.debug("TargetNode: " + targetNode);
            result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append("element ");
            result.append(targetNode.getLabel()).append(" {").append(createIdFromGenerators(targetNode, mappingTask, tgd, false, targetVariable));
            result.append("}");
            if (i != setNodeChildren.size() - 1) result.append(",");
            result.append("\n");
        }
        return result.toString();
    }

    private static List<INode> findSetChildren(INode tupleNode) {
        List<INode> result = new ArrayList<INode>();
        return findSetChildrenRecursive(tupleNode, result);
    }

    private static List<INode> findSetChildrenRecursive(INode tupleNode, List<INode> result) {
        for (INode child : tupleNode.getChildren()) {
            if (child instanceof TupleNode) {
                findSetChildrenRecursive(child, result);
            } else if (child instanceof SetNode) {
                result.add(child);
            }
        }
        return result;
    }

    private static String createIdFromGenerators(INode node, MappingTask mappingTask, FORule tgd, boolean skolem, SetAlias variable) {
        if (logger.isDebugEnabled()) logger.debug("Creating id from generators: " + node + " for variable: " + variable.toShortString());
        String nodeId = nodeId(node, mappingTask, tgd, skolem, variable);
        if (nodeId.contains(" in ")) {
            return "fn:concat(" + nodeId(node, mappingTask, tgd, skolem, variable) + ")";
        }
        return nodeId;
    }

    private static String nodeId(INode node, MappingTask mappingTask, FORule tgd, boolean skolem, SetAlias variable) {
        if (logger.isDebugEnabled()) logger.debug("Generating nodeId for variable: " + variable.toShortString());
        PathExpression pathExpression = new GeneratePathExpression().generatePathFromRoot(node);
        IValueGenerator valueGenerator = getNodeGenerator(pathExpression, mappingTask, tgd, variable);
        if (logger.isDebugEnabled()) logger.debug("valueGenerator: " + valueGenerator + " - node: " + pathExpression);
        if (node.getFather() == null) {
            return XQUtility.xqueryValueForIntermediateNode(valueGenerator, mappingTask);
        } else {
            return XQUtility.xqueryValueForIntermediateNode(valueGenerator, mappingTask) + ", \" in \", " + nodeId(node.getFather(), mappingTask, tgd, skolem, variable);
        }
    }

    private static IValueGenerator getNodeGenerator(PathExpression nodePath, MappingTask mappingTask, FORule tgd, SetAlias variable) {
        if (logger.isDebugEnabled()) logger.debug("getNodeGenerator - tgd: " + tgd.getId());
        if (logger.isDebugEnabled()) logger.debug("getNodeGenerator - generators: " + tgd.getGenerators(mappingTask));
        if (logger.isDebugEnabled()) logger.debug("getNodeGenerator - generatorsForVariable: " + tgd.getGenerators(mappingTask).getGeneratorsForVariable(variable));
        return tgd.getGenerators(mappingTask).getGeneratorsForFatherVariable(variable).get(nodePath);
    }
}
