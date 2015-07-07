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
package it.unibas.spicy.utility;

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionBaseView;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SpicyEngineUtility {

    public static void changePathExpressionForMerge(PathExpression newPath, int i, boolean relational) {
        String firstStep = newPath.getPathSteps().remove(0);
        if (!relational) {
            newPath.getPathSteps().add(0, generateNewLabelForMerge(firstStep, i));
        } else {
            String secondStep = newPath.getPathSteps().remove(0);
            newPath.getPathSteps().add(0, generateNewLabelForMerge(secondStep, i));
        }
        newPath.getPathSteps().add(0, SpicyEngineConstants.MERGE_ROOT_LABEL);
    }

    public static String generateNewLabelForMerge(String label, int i) {
        return SpicyEngineConstants.DATASOURCE_ROOT_LABEL + i + SpicyEngineConstants.SEPARATOR + label;
    }

    @SuppressWarnings("unchecked")
    public static void addNew(List list, Object object) {
        if (!list.contains(object)) {
            list.add(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean equalLists(List list1, List list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        List list2Clone = new ArrayList(list2);
        for (Object o : list1) {
            if (!list2Clone.contains(o)) {
                return false;
            } else {
                list2Clone.remove(o);
            }
        }
        return (list2Clone.isEmpty());
    }

    public static List<PathExpression> generateAbsolutePaths(List<VariablePathExpression> list) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        for (VariablePathExpression path : list) {
            result.add(path.getAbsolutePath());
        }
        return result;
    }

    public static List<VariablePathExpression> generateRelativePaths(List<PathExpression> list, IDataSourceProxy dataSource) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (PathExpression path : list) {
            result.add(path.getRelativePath(dataSource));
        }
        return result;
    }

    public static boolean equalPathsWithSameVariableId(List<VariablePathExpression> list1, List<VariablePathExpression> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        List<VariablePathExpression> list2Clone = new ArrayList<VariablePathExpression>(list2);
        for (VariablePathExpression path1 : list1) {
            if (!containsPathWithSameVariableId(list2Clone, path1)) {
                return false;
            } else {
                list2Clone.remove(path1);
            }
        }
        return (list2Clone.isEmpty());
    }

    public static boolean containsPathWithSameVariableId(List<VariablePathExpression> paths, VariablePathExpression pathExpression) {
        if (paths == null) {
            return false;
        }
        for (VariablePathExpression path : paths) {
            if (path.equalsAndHasSameVariableId(pathExpression)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsPathWithEqualString(List<VariablePathExpression> paths, String pathString) {
        if (paths == null) {
            return false;
        }
        for (VariablePathExpression path : paths) {
            if (path.toString().equals(pathString)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equalListsUpToClones(List<VariablePathExpression> paths1, List<VariablePathExpression> paths2) {
        return (containsAllUpToClones(paths1, paths2) && containsAllUpToClones(paths2, paths1));
    }

    public static boolean containsAllUpToClones(List<VariablePathExpression> paths1, List<VariablePathExpression> paths2) {
        List<VariablePathExpression> paths2Clone = new ArrayList<VariablePathExpression>(paths2);
        for (VariablePathExpression path1 : paths1) {
            VariablePathExpression pathInList = findEqualPathUpToClones(path1, paths2Clone);
            if (pathInList == null) {
                return false;
            } else {
                paths2Clone.remove(pathInList);
            }
        }
        return (paths2Clone.isEmpty());
    }

    private static VariablePathExpression findEqualPathUpToClones(VariablePathExpression path, List<VariablePathExpression> paths) {
        for (VariablePathExpression pathInList : paths) {
            if (pathInList.equalsUpToClones(path)) {
                return pathInList;
            }
        }
        return null;
    }

    public static boolean equalListsUpToVariableIds(List<VariableSelectionCondition> conditions1, List<VariableSelectionCondition> conditions2) {
        return (containsAllUpToVariableIds(conditions1, conditions2) && containsAllUpToVariableIds(conditions2, conditions1));
    }

    public static boolean containsAllUpToVariableIds(List<VariableSelectionCondition> conditions1, List<VariableSelectionCondition> conditions2) {
        List<VariableSelectionCondition> conditions2Clone = new ArrayList<VariableSelectionCondition>(conditions2);
        for (VariableSelectionCondition condition : conditions1) {
            VariableSelectionCondition conditionInList = findEqualConditionUpToVariableIds(condition, conditions2Clone);
            if (conditionInList == null) {
                return false;
            } else {
                conditions2Clone.remove(conditionInList);
            }
        }
        return (conditions2Clone.isEmpty());
    }

    private static VariableSelectionCondition findEqualConditionUpToVariableIds(VariableSelectionCondition condition, List<VariableSelectionCondition> conditions) {
        for (VariableSelectionCondition conditionInList : conditions) {
            if (conditionInList.equalsUpToVariableIds(condition)) {
                return conditionInList;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static boolean contained(List list1, List list2) {
        return (list2.containsAll(list1));
    }

    public static INode createRootNode(INode node) {
        String type = node.getClass().getSimpleName();
        INode rootNode = createNode(type, node.getLabel(), new IntegerOIDGenerator().getNextOID());
        rootNode.setRoot(true);
        return rootNode;
    }

    public static INode createNode(String nodeType, String label, Object value) {
        if (nodeType.equals("SetNode")) {
            return new SetNode(label, value);
        }
        if (nodeType.equals("SetCloneNode")) {
            return new SetCloneNode(label, value);
        }
        if (nodeType.equals("TupleNode")) {
            return new TupleNode(label, value);
        }
        if (nodeType.equals("SequenceNode")) {
            return new SequenceNode(label, value);
        }
        if (nodeType.equals("UnionNode")) {
            return new UnionNode(label, value);
        }
        if (nodeType.equals("AttributeNode")) {
            return new AttributeNode(label, value);
        }
        if (nodeType.equals("MetadataNode")) {
            return new MetadataNode(label, value);
        }
        if (nodeType.equals("LeafNode")) {
            return new LeafNode(label, value);
        }
        return null;
    }

    public static String removeRootLabel(PathExpression path) {
        return removeRootLabel(path.toString());
    }

    public static String removeRootLabel(String pathString) {
        return pathString.substring(pathString.indexOf(".") + 1);
    }

    public static String generateFolderPath(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(File.separator));
    }

    public static boolean hasSingleAtom(Expansion expansion) {
        return expansion.getBaseView().getFormulaAtoms().size() == 1;
    }

    public static boolean hasSingleAtom(ExpansionBaseView view) {
        return view.getFormulaAtoms().size() == 1;
    }

//    public static boolean hasClonesInTgds(MappingTask mappingTask) {
//        for (FORule tgd : mappingTask.getMappingData().getSTTgds()) {
//            if (hasClones(tgd.getTargetView(), mappingTask.getTarget())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean hasClones(ConjunctiveQuery view, DataSource target) {
//        for (SetVariable variable : view.getGenerators()) {
//            SetNode variableNode = variable.getBindingNode(target.getIntermediateSchema());
//            if (variableNode instanceof SetCloneNode) {
//                return true;
//            }
//        }
//        return false;
//    }
    public static boolean hasSelfJoinsInTgds(MappingTask mappingTask) {
//        for (FORule tgd : mappingTask.getMappingData().getCandidateSTTgds()) {
        for (FORule tgd : mappingTask.getMappingData().getSTTgds()) {
//            System.out.println("TGD: " + tgd);
            if (hasSelfJoins(tgd.getTargetView())) {
//                System.out.println("Has self joins");
                return true;
            }
        }
        return false;
    }

    public static boolean hasSelfJoins(SimpleConjunctiveQuery view) {
//        for (SetAlias variable : view.getGenerators()) {
        for (SetAlias variable : view.getVariables()) {
            if (countClones(variable, view) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSelfJoins(FORule tgd) {
        return hasSelfJoins(tgd.getTargetView());
    }

    public static int countClones(SetAlias variable, SimpleConjunctiveQuery view) {
        int counter = 0;
        for (SetAlias otherVariable : view.getGenerators()) {
            if (otherVariable.isClone(variable)) {
                counter++;
            }
        }
        return counter;
    }

    public static List<PathExpression> cloneAndRemoveCopiesFromPaths(List<VariablePathExpression> targetPaths) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        for (VariablePathExpression targetPath : targetPaths) {
            result.add(SpicyEngineUtility.getAbsolutePathWithoutClones(targetPath.clone()));
        }
        return result;
    }

    public static boolean isCloneOrHasClones(SetAlias targetVariable, INode root) {
        if (targetVariable.getBindingNode(root).isCloned()
                || targetVariable.getBindingNode(root) instanceof SetCloneNode) {
            return true;
        }
        return false;
    }

    public static boolean areCopies(SetAlias targetVariable1, SetAlias targetVariable2, INode root) {
        if (targetVariable1.equals(targetVariable2)) {
            return true;
        }
        if (targetVariable1.getBindingNode(root) instanceof SetCloneNode
                && targetVariable2.getBindingNode(root).isCloned()) {
            SetCloneNode setClone1 = (SetCloneNode) targetVariable1.getBindingNode(root);
            if (setClone1.getOriginalNodePath().equals(targetVariable2.getBindingPathExpression().getAbsolutePath())) {
                return true;
            }
        }
        if (targetVariable2.getBindingNode(root) instanceof SetCloneNode
                && targetVariable1.getBindingNode(root).isCloned()) {
            SetCloneNode setClone2 = (SetCloneNode) targetVariable2.getBindingNode(root);
            if (setClone2.getOriginalNodePath().equals(targetVariable1.getBindingPathExpression().getAbsolutePath())) {
                return true;
            }
        }
        if (targetVariable2.getBindingNode(root) instanceof SetCloneNode
                && targetVariable1.getBindingNode(root) instanceof SetCloneNode) {
            SetCloneNode setClone1 = (SetCloneNode) targetVariable1.getBindingNode(root);
            SetCloneNode setClone2 = (SetCloneNode) targetVariable2.getBindingNode(root);
            if (setClone1.getOriginalNodePath().equals(setClone2.getOriginalNodePath())) {
                return true;
            }
        }
        return false;
    }

    public static PathExpression getAbsolutePathWithoutClones(VariablePathExpression variablePath) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        return removeClonesFromAbsolutePath(absolutePath);
    }

    public static PathExpression removeClonesFromAbsolutePath(PathExpression pathExpression) {
        List<String> cleanSteps = new ArrayList<String>();
        for (String step : pathExpression.getPathSteps()) {
            cleanSteps.add(SetCloneNode.removeCloneLabel(step));
        }
        return new PathExpression(cleanSteps);
    }

    public static JoinCondition removeClonesFromJoin(VariableJoinCondition variableJoinCondition) {
        List<PathExpression> fromPaths = new ArrayList<PathExpression>();
        for (VariablePathExpression fromPath : variableJoinCondition.getFromPaths()) {
            fromPaths.add(getAbsolutePathWithoutClones(fromPath));
        }
        List<PathExpression> toPaths = new ArrayList<PathExpression>();
        for (VariablePathExpression toPath : variableJoinCondition.getToPaths()) {
            toPaths.add(getAbsolutePathWithoutClones(toPath));
        }
        JoinCondition result = new JoinCondition(fromPaths, toPaths);
        return result;
    }

//    public static VariableCorrespondence findMatchingCorrespondence(VariablePathExpression targetPath, FORule stTgd) {
//        for (VariableCorrespondence correspondence : stTgd.getCoveredCorrespondences()) {
//            if (correspondence.getTargetPath().equals(targetPath)) {
//                return correspondence;
//            }
//        }
//        return null;
//    }
    public static VariableCorrespondence findCorrespondenceForPath(VariablePathExpression targetPath, FORule tgd) {
        for (VariableCorrespondence correspondence : tgd.getCoveredCorrespondences()) {
            if (correspondence.getTargetPath().equals(targetPath)) {
                return correspondence;
            }
        }
        throw new IllegalArgumentException("Path " + targetPath + " is not universal in rule: " + tgd);
    }

    public static boolean containsCorrespondenceForSamePath(List<VariableCorrespondence> result, VariableCorrespondence variableCorrespondence) {
        for (VariableCorrespondence correspondence : result) {
            if (correspondence.getTargetPath().equalsAndHasSameVariableId(variableCorrespondence.getTargetPath())) {
                return true;
            }
        }
        return false;
    }

    public static List<VariablePathExpression> findTargetPathsInCorrespondences(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : correspondences) {
            result.add(correspondence.getTargetPath());
        }
        return result;
    }

//    public static List<VariablePathExpression> findSourcePathsInCorrespondences(List<VariableCorrespondence> correspondences) {
//        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
//        for (VariableCorrespondence correspondence : correspondences) {
//            if (correspondence.getSourcePaths() == null) {
//                continue;
//            }
//            result.add(correspondence.getSourcePaths().get(0));
//        }
//        return result;
//    }
    public static List<VariablePathExpression> findSourcePathsInCorrespondences(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getSourcePaths() == null) {
                continue;
            }
            for (VariablePathExpression sourcePath : correspondence.getSourcePaths()) {
                result.add(sourcePath);
            }
        }
        return result;
    }

    public static List<VariablePathExpression> findNonNullPaths(List<VariableCorrespondence> correspondences, List<VariablePathExpression> nullPaths) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        List<VariablePathExpression> sourcePaths = findSourcePathsInCorrespondences(correspondences);
        for (VariablePathExpression sourcePath : sourcePaths) {
            if (!nullPaths.contains(sourcePath)) {
                result.add(sourcePath);
            }
        }
        return result;
    }

    public static List<VariablePathExpression> findDistinctSourcePathsInCorrespondences(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : correspondences) {
            if (!correspondence.isConstant()) {
                for (VariablePathExpression sourcePath : correspondence.getSourcePaths()) {
                    if (containsPathWithSameVariableId(result, sourcePath)) {
                        result.add(sourcePath);
                    }
                }
            }
        }
        return result;
    }

    public static List<VariablePathExpression> findNonConstantTargetPathsInCorrespondences(List<VariableCorrespondence> correspondences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableCorrespondence correspondence : correspondences) {
            if (!correspondence.isConstant()) {
                result.add(correspondence.getTargetPath());
            }
        }
        return result;
    }

    public static boolean containsVariableWithSameId(List<SetAlias> variableList, SetAlias variable) {
        for (SetAlias variableInList : variableList) {
            if (variableInList.getId() == variable.getId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsJoinWithSameIds(List<VariableJoinCondition> joinConditions, VariableJoinCondition joinCondition) {
        for (VariableJoinCondition joinInList : joinConditions) {
            if (haveVariablesWithSameId(joinInList, joinCondition) && haveEqualPaths(joinInList, joinCondition)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsSelectionWithSameIds(List<VariableSelectionCondition> selectionConditions, VariableSelectionCondition selectionCondition) {
        for (VariableSelectionCondition selectionInList : selectionConditions) {
            if (selectionInList.getCondition().equals(selectionCondition.getCondition())) {
                return true;
            }
        }
        return false;
    }

    private static boolean haveVariablesWithSameId(VariableJoinCondition join1, VariableJoinCondition join2) {
        return ((join1.getFromVariable().hasSameId(join2.getFromVariable())
                && join1.getToVariable().hasSameId(join2.getToVariable()))
                || (join1.getFromVariable().hasSameId(join2.getToVariable())
                && join1.getToVariable().hasSameId(join2.getFromVariable())));

    }

    private static boolean haveEqualPaths(VariableJoinCondition join1, VariableJoinCondition join2) {
        return (join1.hasEqualPaths(join2) || join1.hasInversePaths(join2));
    }

    public static String generateSetNodeLabel() {
        return "Set";
    }

    public static String generateTupleNodeLabel() {
        return "Tuple";
    }

    public static IDataSourceProxy cloneAlgebraDataSource(IDataSourceProxy dataSource) {
        INode schemaClone = dataSource.getIntermediateSchema().clone();
        DataSource clone = new DataSource(SpicyEngineConstants.TYPE_ALGEBRA_RESULT, schemaClone);
        IDataSourceProxy result = new ConstantDataSourceProxy(clone);
        for (INode instance : dataSource.getInstances()) {
            clone.addInstance(instance.clone());
        }
        return result;
    }

    public static Object findAttributeValue(INode tuple, String attributeLabel) {
        INode attribute = tuple.getChild(attributeLabel);
        if (attribute == null) {
            throw new IllegalArgumentException("Unable to find attribute: " + attributeLabel + " in tuple " + tuple);
        }
        INode leaf = attribute.getChild(0);
        return leaf.getValue();
    }

    public static VariableJoinCondition changeJoinCondition(VariableJoinCondition joinCondition, SetAlias newFromVariable, SetAlias newToVariable) {
        List<VariablePathExpression> newFromPaths = correctPaths(joinCondition.getFromPaths(), newFromVariable);
        List<VariablePathExpression> newToPaths = correctPaths(joinCondition.getToPaths(), newToVariable);
        VariableJoinCondition newJoinCondition = new VariableJoinCondition(newFromPaths, newToPaths,
                joinCondition.isMonodirectional(), joinCondition.isMandatory());
        return newJoinCondition;
    }

    public static List<VariablePathExpression> correctPaths(List<VariablePathExpression> oldPaths, SetAlias newVariable) {
        List<VariablePathExpression> newPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression oldPath : oldPaths) {
            VariablePathExpression newPath = new VariablePathExpression(newVariable, oldPath.getPathSteps());
            newPaths.add(newPath);
        }
        return newPaths;
    }

    /////////////////////////////////////   PRINT METHODS   /////////////////////////////////////
    public static String printCollection(Collection l) {
        return printCollection(l, "");
    }

    public static String printCollection(Collection l, String indent) {
        if (l == null) {
            return indent + "(null)";
        }
        if (l.isEmpty()) {
            return indent + "(empty collection)";
        }
        StringBuilder result = new StringBuilder();
        for (Object o : l) {
            result.append(indent).append(o).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String printVariableList(List<FormulaVariable> l) {
        if (l == null) {
            return "(null)";
        }
        if (l.isEmpty()) {
            return "(empty collection)";
        }
        StringBuilder result = new StringBuilder();
        for (FormulaVariable o : l) {
            result.append(o.toLongString()).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String printListOfVariableLists(List<List<FormulaVariable>> l) {
        if (l == null) {
            return "(null)";
        }
        StringBuilder result = new StringBuilder();
        for (List<FormulaVariable> list : l) {
            result.append(printVariableList(list)).append("\n");
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static String printMap(Map m) {
        String indent = "    ";
        StringBuilder result = new StringBuilder("----------------------------- MAP (size =").append(m.size()).append(") ------------\n");
        List<Object> keys = new ArrayList<Object>(m.keySet());
        Collections.sort(keys, new StringComparator());
        for (Object key : keys) {
            result.append("***************** Key ******************\n").append(key).append("\n");
            Object value = m.get(key);
            result.append(indent).append("---------------- Value ---------------------\n");
            if (value instanceof Collection) {
                result.append("size: ").append(((Collection) value).size()).append("\n");
                result.append(printCollection((Collection) value, indent)).append("\n");
            } else {
                result.append(indent).append(value).append("\n");
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static String printVariableMap(Map m) {
        String indent = "    ";
        StringBuilder result = new StringBuilder("----------------------------- MAP (size =").append(m.size()).append(") ------------\n");
        List<Object> keys = new ArrayList<Object>(m.keySet());
        Collections.sort(keys, new StringComparator());
        for (Object key : keys) {
            result.append("***************** Key ******************\n").append(key).append("\n");
            List<FormulaVariable> variables = (List<FormulaVariable>)m.get(key);
            result.append(indent).append("---------------- Variables ---------------------\n");
            for (FormulaVariable formulaVariable : variables) {
                result.append(indent).append(formulaVariable.toLongString()).append("\n");
            }
        }
        return result.toString();
    }

    public static String printTgdsInLogicalForm(List<FORule> tgds, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        for (FORule tgd : tgds) {
            result.append(tgd.toLogicalString(mappingTask)).append("\n");
        }
        return result.toString();
    }

    public static String printTgds(List<FORule> tgds, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        for (FORule tgd : tgds) {
            result.append(tgd.toString()).append("\n");
        }
        return result.toString();
    }
}

class StringComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }


}
