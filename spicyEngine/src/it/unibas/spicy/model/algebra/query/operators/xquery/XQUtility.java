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
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.generators.FunctionGenerator;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.NullValueGenerator;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.Coverage;
import it.unibas.spicy.model.mapping.rewriting.CoverageAtom;
import it.unibas.spicy.model.mapping.rewriting.CoverageMap;
import it.unibas.spicy.model.mapping.rewriting.Subsumption;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XQUtility {

    private static final Log logger = LogFactory.getLog(XQUtility.class);
    public static final String DOC = "$doc";
    public static final String CREATE_VIEW = "let ";
    public static final String VARIABLE = "$variable";
    public static final String PROVENANCE_ID = "provenanceId";
    public static final String SET_ID = "setId";
    public static final String TUPLE_ID = "tupleId";
    public static final String REMOVE_DUPLICATES_PREFIX = "local:removeDuplicateTuples(";
    public static final String XQUERY_FUNCTION = "local:skolem";
    public static final String INDENT = "      ";
    public static final String DOUBLE_INDENT = "            ";
    public static final String JOIN_DIFFERENCE = "$join_difference";
    public static final String JOIN_COVERAGE = "$join_coverage";
//    private static Map<FORule, List<String>> functorsMap = new HashMap<FORule, List<String>>();

    public static String xqueryValueForLeaf(IValueGenerator generator, VariablePathExpression targetPath, FORule tgd, MappingTask mappingTask) {
        if (generator instanceof NullValueGenerator) {
            return "\"NULL\"";
        }
        if (generator instanceof FunctionGenerator) {
            StringBuilder result = new StringBuilder();
//            result.append(XQUtility.VARIABLE).append("/").append(XQNames.xQueryNameForPath(targetPath)).append("/text()");

            VariableCorrespondence correspondence = XQUtility.findCorrespondenceFromTargetPathWithSameId(targetPath, tgd.getCoveredCorrespondences());
            if (XQUtility.hasDifferences(tgd)) {
                if (correspondence.getSourcePaths() != null) {
                    VariablePathExpression firstSourcePath = correspondence.getFirstSourcePath();
//                    String elementName = XQNames.xQueryNameForPath(firstSourcePath);
                    result.append(XQNames.attributeNameInVariable(firstSourcePath));
                } else {
                    String sourcePathName = correspondence.getSourceValue().toString();
                    sourcePathName = sourcePathName.replaceAll("\"", "\'");
                    result.append(sourcePathName);
                }
            } else {
                if (correspondence.getSourcePaths() != null) {
                    VariablePathExpression firstSourcePath = correspondence.getFirstSourcePath();
//                    result.append(XQUtility.DOUBLE_INDENT).append(XQUtility.DOUBLE_INDENT).append(XQNames.attributeNameWithVariable(firstSourcePath));
                    result.append(XQNames.attributeNameInVariable(firstSourcePath));
                } else {
                    String sourcePathName = correspondence.getSourceValue().toString();
                    sourcePathName = sourcePathName.replaceAll("\"", "\'");
                    result.append(sourcePathName);
                }
            }
            return result.toString();
        }
        if (generator instanceof SkolemFunctionGenerator) {
            SkolemFunctionGenerator skolemGenerator = (SkolemFunctionGenerator) generator;
            StringBuilder result = new StringBuilder();

            result.append(XQUtility.XQUERY_FUNCTION).append("(\"").append(removeRootLabel(skolemGenerator.getName())).append("\", (");
            if (generator.getSubGenerators().size() > 0) {
                String skolemString = new XQSkolemHandler().skolemString(skolemGenerator, mappingTask, false).toString();
                result.append(skolemString);
            }
            result.append("))");
            return result.toString();
        }
        return null;
    }

    public static String removeRootLabel(String pathString) {
        return pathString.substring(pathString.indexOf(".") + 1);
    }

    public static String xqueryValueForIntermediateNode(IValueGenerator generator, MappingTask mappingTask) {
        if (generator instanceof NullValueGenerator) {
            return "\"NULL\"";
        }
        if (generator instanceof SkolemFunctionGenerator) {
            SkolemFunctionGenerator skolemGenerator = (SkolemFunctionGenerator) generator;
            StringBuilder result = new StringBuilder();
            result.append(XQUtility.XQUERY_FUNCTION).append("(\"").append(removeRootLabel(skolemGenerator.getName())).append("\", (");
            if (generator.getSubGenerators().size() > 0) {
                String skolemString = new XQSkolemHandler().skolemString(skolemGenerator, mappingTask, false).toString();
                result.append(skolemString);
            }
            result.append("))");
            return result.toString();
        }
        return null;
    }

    public static String xQueryStringForExpression(Expression expression) {
        // TODO: check expression extraction to verify conditions different from equalities
        StringBuilder result = new StringBuilder();
        String condition = expression.toString();
        condition = condition.substring(condition.lastIndexOf(" ") + 1, condition.length() - 1);
        VariablePathExpression variablePathExpression = expression.getAttributePaths().get(0);
        result.append(variablePathExpression.getStartingVariable().toShortStringWithDollar()).append("/").append(variablePathExpression.getLastStep());
        result.append(" = ").append(condition);
        return result.toString();
    }

    public static String findViewName(FORule tgd, MappingTask mappingTask) {
        if (hasDifferences(tgd)) {
            return XQNames.xqueryNameForDifference(tgd);
        }
        return XQNames.xQueryNameForTgd(tgd);
    }

    public static VariablePathExpression findSourcePath(List<VariableCorrespondence> correspondences, VariablePathExpression targetPath) {
        for (VariableCorrespondence variableCorrespondence : correspondences) {
            if (variableCorrespondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return variableCorrespondence.getFirstSourcePath();
            }
        }
        return null;
    }

    public static VariableCorrespondence findCorrespondenceFromTargetPathWithSameId(VariablePathExpression targetPath, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(targetPath)) {
                return correspondence;
            }
        }
        return null;
    }

    public static boolean hasDifferences(FORule tgd) {
        return !tgd.getComplexSourceQuery().getNegatedComplexQueries().isEmpty();
    }

    public static boolean hasSubsumptions(FORule tgd, MappingTask mappingTask) {
        SubsumptionMap subsumptionDag = mappingTask.getMappingData().getSTSubsumptionMap();
        List<Subsumption> subsumptions = subsumptionDag.getFathers(tgd);
        return (subsumptions != null && subsumptions.size() != 0);
    }

    public static boolean hasCoverages(FORule tgd, MappingTask mappingTask) {
        CoverageMap coverageMap = mappingTask.getMappingData().getSTCoverageMap();
        List<Coverage> coverages = coverageMap.getCoverage(tgd);
        return (coverages != null && coverages.size() != 0);
    }

    public static List<VariablePathExpression> extractTargetPaths(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence variableCorrespondence : correspondences) {
            result.add(variableCorrespondence.getTargetPath());
        }
        return result;
    }

    public static String toStringWithoutVirtualNodes(VariablePathExpression variablePath, INode root) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        List<INode> absolutePathNodes = new GeneratePathExpression().generatePathStepNodes(absolutePath.getPathSteps(), root);
        List<String> variablePathSteps = variablePath.getPathSteps();
        StringBuilder result = new StringBuilder();
        int offset = absolutePathNodes.size() - variablePathSteps.size();
        for (int i = variablePathSteps.size() - 1; i > 0; i--) {
            INode pathNode = absolutePathNodes.get(i + offset);
            if (!pathNode.isVirtual()) {
                if (result.length() != 0) {
                    result.insert(0, "/");
                }
                result.insert(0, variablePathSteps.get(i));
            }
        }
        return result.toString();
    }

    public static String toStringWithoutVirtualNodesFromRoot(VariablePathExpression variablePath, INode root) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        List<INode> pathNodes = new GeneratePathExpression().generatePathStepNodes(absolutePath.getPathSteps(), root);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pathNodes.size(); i++) {
            INode pathNode = pathNodes.get(i);
            if (!pathNode.isVirtual()) {
                if (result.length() != 0) {
                    result.append("/");
                }
                result.append(pathNode.getLabel());
            }
        }
        return result.toString();
    }

    public static String createXQueryRelativePath(VariablePathExpression path) {
        StringBuilder relativePath = new StringBuilder();
        relativePath.append(path.getStartingVariable().toShortStringWithDollar()).append("/").append(path.getLastStep()).append("/text()");
        return relativePath.toString();
    }

    public static String createXQueryRelativePathForMetadataNode(VariablePathExpression path, INode metatadaNode) {
        StringBuilder relativePath = new StringBuilder();
        INode father = metatadaNode.getFather();
        INode ancestor = father.getFather();
        if (ancestor instanceof SetNode) {
            relativePath.append("xs:string(").append(path.getStartingVariable().toShortStringWithDollar()).append("/@").append(path.getLastStep()).append(")");
        } else {
            relativePath.append("xs:string(").append(path.getStartingVariable().toShortStringWithDollar()).append("/");
            relativePath.append(father.getLabel()).append("/@").append(path.getLastStep()).append(")");
        }
        return relativePath.toString();
    }

    public static String createXQueryRelativePathForVirtualAttribute(VariablePathExpression path, INode attributeNode) {
        StringBuilder relativePath = new StringBuilder();
        INode father = attributeNode.getFather();
        INode ancestor = father.getFather();
        INode ancestorOfAncestor = ancestor.getFather();
        if (father.isVirtual()) {
            if (ancestorOfAncestor instanceof SetNode) {
                relativePath.append(path.getStartingVariable().toShortStringWithDollar()).append("/text()");
            } else {
                relativePath.append(path.getStartingVariable().toShortStringWithDollar()).append("/").append(ancestor.getLabel()).append("/text()");
            }
        } else {
            relativePath.append(path.getStartingVariable().toShortStringWithDollar()).append("/").append(father.getLabel()).append("/text()");
        }
        return relativePath.toString();
    }

    public static String createXQueryRelativePathForSingleAttribute(VariablePathExpression path, INode attributeNode) {
        StringBuilder relativePath = new StringBuilder();
        relativePath.append(path.getStartingVariable().toShortStringWithDollar()).append("/text()");
        return relativePath.toString();
    }

    public static String createXQueryRelativePathForCoverage(VariablePathExpression path, Coverage coverage) {
        StringBuilder relativePath = new StringBuilder();
        FORule tgd = findCoveringTGD(path, coverage);
        String variableName = XQNames.xQueryNameForApplyFunctions(tgd);
        relativePath.append(variableName).append("/").append(path.getStartingVariable().toShortString()).append("_").append(path.getLastStep()).append("/text()");
        return relativePath.toString();
    }

    private static FORule findCoveringTGD(VariablePathExpression path, Coverage coverage) {
        for (CoverageAtom atom : coverage.getCoveringAtoms()) {
            SetAlias variable = atom.getFirstCoveringAtom().getVariable();
            if (path.getStartingVariable().equals(variable)) {
                return atom.getFirstCoveringAtom().getTgd();
            }

        }
        return null;
    }

    public static String replaceDotsWithSlashes(String originalString) {
        return originalString.replaceAll("\\.", "/");
    }
}
