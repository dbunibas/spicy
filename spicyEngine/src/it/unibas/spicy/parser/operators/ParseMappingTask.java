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
package it.unibas.spicy.parser.operators;

import it.unibas.spicy.model.correspondence.ConstantValue;
import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.parser.ParserArgument;
import it.unibas.spicy.parser.ParserAtom;
import it.unibas.spicy.parser.ParserAttribute;
import it.unibas.spicy.parser.ParserBuiltinFunction;
import it.unibas.spicy.parser.ParserBuiltinOperator;
import it.unibas.spicy.parser.ParserException;
import it.unibas.spicy.parser.ParserFD;
import it.unibas.spicy.parser.ParserFact;
import it.unibas.spicy.parser.ParserInstance;
import it.unibas.spicy.parser.ParserTGD;
import it.unibas.spicy.parser.ParserView;
import it.unibas.spicy.persistence.Types;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;

public class ParseMappingTask extends AbstractParseMappingTask {

    private static Log logger = LogFactory.getLog(ParseMappingTask.class);
    public final static String NULL = "#NULL#";
    private GenerateTgdsFromParserOutput tgdGenerator = new GenerateTgdsFromParserOutput();

    // final callback method for processing tgds
    public void processTGDs() {
        try {
            if (logger.isDebugEnabled()) logger.debug("Mapping task before creating data sources: \n" + mappingTask);
            checkTargetTGDs(stParserTgds, targetParserTgds);
            if (logger.isTraceEnabled()) logger.debug("Tgds after processing target tgds: \n" + stParserTgds);
            if (this.generateSource || this.generateTarget) {
                createDataSources();
            }
            if (logger.isDebugEnabled()) logger.debug("Mapping task after creating data sources: \n" + mappingTask);
            for (ParserTGD parserTgd : stParserTgds) {
                checkView(parserTgd.getSourceView(), mappingTask.getSourceProxy());
                checkViews(parserTgd.getNegatedSourceViews(), mappingTask.getSourceProxy());
                checkView(parserTgd.getTargetView(), mappingTask.getTargetProxy());

                checkFunctions(parserTgd.getSourceView());
                checkFunctionsForNegation(parserTgd.getNegatedSourceViews());
                checkOperators(parserTgd.getSourceView());
                checkOperatorsForNegation(parserTgd.getNegatedSourceViews());

                findSelectionConditionsForView(parserTgd.getSourceView(), mappingTask.getSourceProxy());
                findSelectionConditionsForNegation(parserTgd.getNegatedSourceViews(), mappingTask.getSourceProxy());
                if (logger.isDebugEnabled()) logger.debug("View after discovering selection conditions: \n" + parserTgd.getSourceView());
                findBidirectionalJoinConditions(parserTgd.getSourceView(), new ArrayList<String>());
                findCyclicJoinConditionsForSingleAtoms(parserTgd.getSourceView(), new ArrayList<String>());

                List<String> sourceVariables = findSourceVariables(parserTgd.getSourceView());

                findConditionsForNegated(parserTgd.getNegatedSourceViews());

                findBidirectionalJoinConditions(parserTgd.getTargetView(), sourceVariables);
                findForeignKeyJoinConditions(parserTgd.getTargetView(), sourceVariables);
                findCyclicJoinConditionsForSingleAtoms(parserTgd.getTargetView(), sourceVariables);
            }
            for (ParserTGD parserTgd : stParserTgds) {
                findConstantCorrespondences(parserTgd);
                findSourceToTargetCorrespondences(parserTgd);
                findComplexExpressionCorrespondeces(parserTgd);
            }
            processFDs(sourceFDs, mappingTask.getSourceProxy(), this.generateSource);
            processFDs(targetFDs, mappingTask.getTargetProxy(), this.generateTarget);
            if (logger.isDebugEnabled()) logger.debug("Final parser tgds: \n" + stParserTgds);
            List<FORule> tgds = tgdGenerator.generateTGDs(stParserTgds, mappingTask);
//        mappingTask.getMappingData().setCandidateSTTgds(tgds);
            mappingTask.setLoadedTgds(tgds);
            if (logger.isDebugEnabled()) logger.debug("Final tgds: \n" + tgds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw new ParserException(e);
        }
    }

    private void findConditionsForNegated(List<ParserView> parserViews) {
        for (ParserView view : parserViews) {
            if (view.getSubViews().size() >= 1) {
                findConditionsForNegated(view.getSubViews());
            }
            findBidirectionalJoinConditions(view, new ArrayList<String>());
            findCyclicJoinConditionsForSingleAtoms(view, new ArrayList<String>());
        }
    }

    /////////////////////   TARGET TGDS ////////////////////////////
    private void checkTargetTGDs(List<ParserTGD> stTgds, List<ParserTGD> targetTgds) {
        for (ParserTGD targetTGD : targetTgds) {
            for (ParserTGD stTGD : stTgds) {
                rewriteSTTGD(targetTGD, stTGD);
            }
        }
    }

    private void rewriteSTTGD(ParserTGD targetTGD, ParserTGD stTGD) {
        ParserView stView = stTGD.getTargetView();
        ParserAtom leftAtom = targetTGD.getSourceView().getAtoms().get(0);
        ParserAtom rightAtom = targetTGD.getTargetView().getAtoms().get(0);
        if (findAtomInView(stView, leftAtom.getName()) != null) {
            if (findAtomInView(stView, rightAtom.getName()) == null) {
                if (logger.isDebugEnabled()) logger.debug("Processing target tgd: \n" + targetTGD + " against\n" + stTGD);
                List<ParserAttribute> leftAttributes = new ArrayList<ParserAttribute>();
                List<ParserAttribute> rightAttributes = new ArrayList<ParserAttribute>();
                findCommonAttributes(leftAtom, rightAtom, leftAttributes, rightAttributes, new ArrayList<String>());
                if (logger.isDebugEnabled()) logger.debug("Common attributes:" + leftAttributes + " - " + rightAttributes);
                addTargetAtomToView(stView, leftAtom, rightAtom, leftAttributes, rightAttributes);
                if (logger.isDebugEnabled()) logger.debug("Final result: \n" + stTGD);
            } else {
                // TODO: check if this is possible ??
            }
        }
    }

    private List<String> findSourceVariables(ParserView sourceView) {
        List<String> result = new ArrayList<String>();
        for (ParserAtom atom : sourceView.getAtoms()) {
            for (ParserAttribute attribute : atom.getAttributes()) {
                if (attribute.isVariable()) {
                    result.add(attribute.getVariable());
                }
            }
        }
        return result;
    }

    private void findCommonAttributes(ParserAtom firstAtom, ParserAtom secondAtom,
            List<ParserAttribute> firstAttributes, List<ParserAttribute> secondAttributes,
            List<String> sourceVariables) {
        for (ParserAttribute firstAttribute : firstAtom.getAttributes()) {
            for (ParserAttribute secondAttribute : secondAtom.getAttributes()) {
                if (!(firstAttribute.isVariable() && secondAttribute.isVariable())) {
                    continue;
                }
                if (sourceVariables.contains(firstAttribute.getVariable()) || sourceVariables.contains(secondAttribute.getVariable())) {
                    continue;
                }
                if (firstAttribute.getVariable().equals(secondAttribute.getVariable())) {
                    firstAttributes.add(firstAttribute);
                    secondAttributes.add(secondAttribute);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Common attributes between: " + firstAtom + " and " + secondAtom + "\n" + firstAttributes + "\n" + secondAttributes);
    }

    private void addTargetAtomToView(ParserView targetSTView, ParserAtom leftAtomInTargetTGD, ParserAtom rightAtomInTargetTGD, List<ParserAttribute> leftAttributes, List<ParserAttribute> rightAttributes) {
        ParserAtom rightAtomToAdd = rightAtomInTargetTGD.clone();
        setDontCares(rightAtomToAdd);
        ParserAtom leftAtomInView = findAtomInView(targetSTView, leftAtomInTargetTGD.getName());
        leftAtomInView.addRightTargetTGDatom(rightAtomToAdd);
        if (logger.isTraceEnabled()) logger.debug("Left atom in view: \n" + leftAtomInView);
        if (logger.isTraceEnabled()) logger.debug("New atom to add: \n" + rightAtomToAdd);
        for (int i = 0; i < leftAttributes.size(); i++) {
            ParserAttribute leftAttributeInTargetTGD = leftAttributes.get(i);
            ParserAttribute rightAttributeInTargetTGD = rightAttributes.get(i);
            if (logger.isTraceEnabled()) logger.debug("Checking attributes: \n" + leftAttributeInTargetTGD + " ----- " + rightAttributeInTargetTGD);
            ParserAttribute leftAttributeInView = findAttributeInAtom(leftAtomInView, leftAttributeInTargetTGD.getName());
            ParserAttribute rightAttributeInView = findAttributeInAtom(rightAtomToAdd, rightAttributeInTargetTGD.getName());
            if (logger.isTraceEnabled()) logger.debug("Attribute in view: \n" + leftAttributeInView);
            if (logger.isTraceEnabled()) logger.debug("Attribute to add: \n" + rightAttributeInView);
            rightAttributeInView.setValue(leftAttributeInView.getValue());
            rightAttributeInView.setVariable(leftAttributeInView.getVariable());
        }
        targetSTView.addAtom(rightAtomToAdd);
    }

    private ParserAttribute findAttributeInAtom(ParserAtom atom, String attributeName) {
        for (ParserAttribute attribute : atom.getAttributes()) {
            if (attribute.getName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    private void setDontCares(ParserAtom atom) {
        for (int i = 0; i < atom.getAttributes().size(); i++) {
            ParserAttribute attribute = atom.getAttributes().get(i);
            attribute.setValue(null);
            attribute.setVariable(atom.getName() + "_" + attribute.getName() + "_DONT_CARE_" + i + "_");
        }
    }

    private ParserAtom findAtomInView(ParserView view, String atomName) {
        for (ParserAtom atom : view.getAtoms()) {
            if (atom.getName().equals(atomName)) {
                return atom;
            }
        }
        return null;
    }

    /////////////////////   SOURCE TO TARGET TGDS ////////////////////////////
    private void createDataSources() {
        for (ParserTGD parserTGD : stParserTgds) {
            if (this.generateSource) {
                ParserView sourceView = parserTGD.getSourceView();
                addAtomsForNegatedView(parserTGD.getNegatedSourceViews(), mappingTask.getSourceProxy());
                addAtomsForView(sourceView, mappingTask.getSourceProxy());
            }
            if (this.generateTarget) {
                ParserView targetView = parserTGD.getTargetView();
                addAtomsForView(targetView, mappingTask.getTargetProxy());
            }
        }
        if (this.generateSource) {
            mappingTask.getSourceProxy().generateIntermediateSchema();
            List<String> instanceIds = new ArrayList<String>();
            for (ParserInstance parserInstance : sourceInstances) {
                String mappingTaskName = mappingTaskFilePath.substring(0, mappingTaskFilePath.lastIndexOf("."));
                instanceIds.add(mappingTaskName + "-" + parserInstance.getId());
                INode rootInstance = new TupleNode(SpicyEngineConstants.DATASOURCE_ROOT_LABEL, oidGenerator.getNextOID());
                rootInstance.setRoot(true);
                addFactsToInstance(rootInstance, parserInstance);
                mappingTask.getSourceProxy().addInstanceWithCheck(rootInstance);
            }
            mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceIds);
        }
        if (this.generateTarget) {
            mappingTask.getTargetProxy().generateIntermediateSchema();
        }
        if (logger.isDebugEnabled()) logger.debug("Created source: " + mappingTask.getSourceProxy());
        if (logger.isDebugEnabled()) logger.debug("Created target: " + mappingTask.getTargetProxy());
    }

    private void addAtomsForNegatedView(List<ParserView> views, IDataSourceProxy dataSource) {
        for (ParserView parserView : views) {
            addAtomsForView(parserView, dataSource);
            if (parserView.getSubViews().size() > 0) {
                addAtomsForNegatedView(parserView.getSubViews(), dataSource);
            }
        }
    }

    private void addAtomsForView(ParserView view, IDataSourceProxy dataSource) {
        INode rootNode = dataSource.getSchema();
        for (ParserAtom atom : view.getAtoms()) {
            if (findSetNode(atom.getName(), dataSource) == false) {
                INode setNode = new SetNode(atom.getName());
                INode tupleNode = new TupleNode(atom.getName() + "Tuple");
                rootNode.addChild(setNode);
                setNode.addChild(tupleNode);
                for (ParserAttribute attribute : atom.getAttributes()) {
                    INode attributeNode = new AttributeNode(attribute.getName());
                    INode leafNode = new LeafNode(Types.STRING);
                    attributeNode.addChild(leafNode);
                    tupleNode.addChild(attributeNode);
                }
            }
        }
    }

    private boolean findSetNode(String setName, IDataSourceProxy dataSource) {
        INode tupleNode = dataSource.getSchema();
        for (INode setNode : tupleNode.getChildren()) {
            if (setNode.getLabel().equals(setName)) {
                return true;
            }
        }
        return false;
    }

    private void addFactsToInstance(INode rootInstance, ParserInstance parserInstance) {
        if (logger.isDebugEnabled()) logger.debug("Parser instance: " + parserInstance);
        for (ParserFact parserFact : parserInstance.getFacts()) {
            INode setNodeInstance = findSetNode(parserFact.getSet(), rootInstance);
            if (setNodeInstance == null) {
                setNodeInstance = new SetNode(parserFact.getSet(), oidGenerator.getNextOID());
                rootInstance.addChild(setNodeInstance);
            }
            TupleNode tupleNodeInstance = new TupleNode(parserFact.getSet() + "Tuple", oidGenerator.getNextOID());
            setNodeInstance.addChild(tupleNodeInstance);
            for (ParserAttribute parserAttribute : parserFact.getAttributes()) {
                AttributeNode attributeNodeInstance = new AttributeNode(parserAttribute.getName(), oidGenerator.getNextOID());
                LeafNode leafNodeInstance = null;
                if (parserAttribute.getValue().equals(NULL)) {
                    leafNodeInstance = new LeafNode(Types.STRING, NullValueFactory.getNullValue());
                } else {
                    String valueType = findType(parserAttribute.getValue());
                    leafNodeInstance = new LeafNode(valueType, parserAttribute.getValue());
                }
                attributeNodeInstance.addChild(leafNodeInstance);
                tupleNodeInstance.addChild(attributeNodeInstance);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Final instance" + rootInstance);
    }

    private String findType(Object value) {
        try {
            Integer.parseInt(value.toString());
            return Types.INTEGER;
        } catch (NumberFormatException e) {
        }
        try {
            Double.parseDouble(value.toString());
            return Types.DOUBLE;
        } catch (NumberFormatException e) {
        }
        return Types.STRING;
    }

    private INode findSetNode(String setName, INode rootInstance) {
        for (INode child : rootInstance.getChildren()) {
            if (child.getLabel().equals(setName)) {
                return child;
            }
        }
        return null;
    }

    private void checkViews(List<ParserView> views, IDataSourceProxy dataSource) {
        for (ParserView parserView : views) {
            if (parserView.getSubViews().size() > 0) {
                checkViews(parserView.getSubViews(), dataSource);
            }
            checkView(parserView, dataSource);
        }
    }

    private void checkView(ParserView view, IDataSourceProxy dataSource) {
        for (ParserAtom atom : view.getAtoms()) {
            PathExpression atomPath = findAbsolutePath(atom, dataSource);
            if (containsAtomWithEqualPath(view, atomPath)) {
                PathExpression clonePath = dataSource.addDuplication(atomPath);
                atomPath = clonePath;
            }
            atom.setAbsolutePath(atomPath);
            for (ParserAttribute attribute : atom.getAttributes()) {
                PathExpression attributePath = findAttributePath(attribute, atomPath, dataSource);
                attribute.setAttributePath(attributePath);
            }
        }
    }

    private PathExpression findAbsolutePath(ParserAtom atom, IDataSourceProxy dataSource) {
        for (PathExpression setPath : dataSource.getAbsoluteSetPaths()) {
            if (setPath.getLastStep().equals(atom.getName())) {
                return setPath;
            }
        }
        throw new ParserException("Atom " + atom.getName() + " does not exist in source: " + dataSource);
    }

    private boolean containsAtomWithEqualPath(ParserView view, PathExpression atomPath) {
        for (ParserAtom atom : view.getAtoms()) {
            if (atom.getAbsolutePath() != null && atom.getAbsolutePath().equals(atomPath)) {
                return true;
            }
        }
        return false;
    }

    private PathExpression findAttributePath(ParserAttribute attribute, PathExpression setPath, IDataSourceProxy dataSource) {
        for (PathExpression attributePath : setPath.getAttributePaths(dataSource.getIntermediateSchema())) {
            if (attributePath.getLastStep().equals(attribute.getName())) {
                return attributePath;
            }
        }
        throw new ParserException("Attribute " + attribute.getName() + " does not exist in set " + setPath + " - " + dataSource);
    }

    private void checkFunctions(ParserView sourceView) {
        for (ParserBuiltinFunction function : sourceView.getFunctions()) {
            for (ParserArgument argument : function.getArguments()) {
                checkArgument(argument, sourceView);
            }
        }

    }
    private void checkFunctionsForNegation(List<ParserView> negatedViews) {
        for (ParserView negatedView : negatedViews) {
            checkFunctions(negatedView);
            checkFunctionsForNegation(negatedView.getSubViews());
        }
    }

    private void checkOperators(ParserView sourceView) {
        for (ParserBuiltinOperator operator : sourceView.getOperators()) {
            if (logger.isDebugEnabled()) logger.debug("Checking operator " + operator);
            checkArgument(operator.getFirstArgument(), sourceView);
            checkArgument(operator.getSecondArgument(), sourceView);
        }
    }
    
    private void checkOperatorsForNegation(List<ParserView> negatedViews) {
        for (ParserView negatedView : negatedViews) {
            checkOperators(negatedView);
            checkOperatorsForNegation(negatedView.getSubViews());
        }
    }

    private void checkArgument(ParserArgument argument, ParserView sourceView) {
        if (!argument.isVariable()) {
            return;
        }
        for (ParserAtom atom : sourceView.getAtoms()) {
            for (ParserAttribute attribute : atom.getAttributes()) {
                if (attribute.isVariable() && attribute.getVariable().equals(argument.getVariable())) {
                    argument.setAttributePath(attribute.getAttributePath());
                    argument.setSetPath(atom.getAbsolutePath());
                    return;
                }
            }
        }
        throw new ParserException("Variable " + argument.getVariable() + " does not appear in atoms of view: " + sourceView);
    }

    /////////////////   SELECTION CONDITIONS  ////////////////////////
    private void findSelectionConditionsForView(ParserView view, IDataSourceProxy source) {
        if(logger.isDebugEnabled())logger.debug("Finding selection condition in " + view);
        for (ParserAtom atom : view.getAtoms()) {
            for (ParserAttribute attribute : atom.getAttributes()) {
                if (!attribute.isVariable()) {
                    Expression expression = null;
                    if (attribute.getValue().equals(NULL)) {
                        expression = new Expression("isNull(" + attribute.getName() + ")");
                    } else {
                        expression = new Expression(attribute.getName() + "==" + "\"" + attribute.getValue() + "\"");
                    }
                    SelectionCondition condition = new SelectionCondition(atom.getAbsolutePath(), expression, source.getIntermediateSchema());
                    atom.addSelectionCondition(condition);
                }
            }
        }
        for (ParserBuiltinFunction function : view.getFunctions()) {
            List<PathExpression> setPaths = new ArrayList<PathExpression>();
            for (ParserArgument argument : function.getArguments()) {
                if (argument.isVariable()) {
                    setPaths.add(argument.getSetPath());
                }
            }
            Expression condition = new Expression(function.toExpressionString());
            SelectionCondition functionCondition = new SelectionCondition(setPaths, condition, source.getIntermediateSchema());
            view.addSelectionCondition(functionCondition);
        }
        for (ParserBuiltinOperator operator : view.getOperators()) {
            List<PathExpression> setPaths = new ArrayList<PathExpression>();
            if (operator.getFirstArgument().isVariable()) {
                setPaths.add(operator.getFirstArgument().getSetPath());
            }
            if (operator.getSecondArgument().isVariable()) {
                setPaths.add(operator.getSecondArgument().getSetPath());
            }
            Expression condition = new Expression(operator.toExpressionString());
            SelectionCondition functionCondition = new SelectionCondition(setPaths, condition, source.getIntermediateSchema());
            view.addSelectionCondition(functionCondition);
        }
    }
    private void findSelectionConditionsForNegation(List<ParserView> negatedViews, IDataSourceProxy source) {
        for (ParserView negatedView : negatedViews) {
            findSelectionConditionsForView(negatedView, source);
            findSelectionConditionsForNegation(negatedView.getSubViews(), source);
        }
    }

    /////////////////   JOIN CONDITIONS  ////////////////////////
    private void findBidirectionalJoinConditions(ParserView view, List<String> sourceVariablesToExclude) {
        if (logger.isDebugEnabled()) logger.debug("Searching joins in view: \n" + view);
        for (int i = 0; i < view.getAtoms().size(); i++) {
            ParserAtom firstAtom = view.getAtoms().get(i);
            for (int j = i + 1; j < view.getAtoms().size(); j++) {
                ParserAtom secondAtom = view.getAtoms().get(j);
                // bidirectional joins first
                if (firstAtom.getRightTargetTGDatoms().contains(secondAtom) || secondAtom.getRightTargetTGDatoms().contains(firstAtom)) {
                    continue;
                }
                JoinCondition joinCondition = generateJoinsForAtoms(firstAtom, secondAtom, sourceVariablesToExclude);
                if (joinCondition != null) {
                    joinCondition.setMonodirectional(false);
                    joinCondition.setMandatory(true);
                    view.addJoinCondition(joinCondition);
                }
            }
        }
    }

    private void findForeignKeyJoinConditions(ParserView view, List<String> sourceVariablesToExclude) {
        if (logger.isDebugEnabled()) logger.debug("Searching foreign key joins in view: \n" + view);
        for (ParserAtom leftAtom : view.getAtoms()) {
            for (ParserAtom rightAtom : leftAtom.getRightTargetTGDatoms()) {
                JoinCondition joinCondition = generateJoinsForAtoms(leftAtom, rightAtom, sourceVariablesToExclude);
                if (joinCondition != null && !existsJoinConditionWithEqualPaths(joinCondition, view)) {
                    joinCondition.setMonodirectional(true);
                    joinCondition.setMandatory(true);
                    view.addJoinCondition(joinCondition);
                }
            }
        }
    }

    private boolean existsJoinConditionWithEqualPaths(JoinCondition newJoinCondition, ParserView view) {
        for (JoinCondition joinCondition : view.getJoinConditions()) {
            if (SpicyEngineUtility.equalLists(joinCondition.getFromPaths(), newJoinCondition.getFromPaths())
                    && SpicyEngineUtility.equalLists(joinCondition.getToPaths(), newJoinCondition.getToPaths())) {
                return true;
            }
            if (SpicyEngineUtility.equalLists(joinCondition.getToPaths(), newJoinCondition.getFromPaths())
                    && SpicyEngineUtility.equalLists(joinCondition.getFromPaths(), newJoinCondition.getToPaths())) {
                return true;
            }
        }
        return false;
    }

    private JoinCondition generateJoinsForAtoms(ParserAtom firstAtom, ParserAtom secondAtom, List<String> sourceVariablesToExclude) {
        List<ParserAttribute> firstAttributes = new ArrayList<ParserAttribute>();
        List<ParserAttribute> secondAttributes = new ArrayList<ParserAttribute>();
        findCommonAttributes(firstAtom, secondAtom, firstAttributes, secondAttributes, sourceVariablesToExclude);
        if (!firstAttributes.isEmpty() && !secondAttributes.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Generating joins for atoms: \n" + firstAtom + "\nand\n" + secondAtom);
            JoinCondition joinCondition = generateJoinCondition(firstAttributes, secondAttributes);
            return joinCondition;
        }
        return null;
    }

    private JoinCondition generateJoinCondition(List<ParserAttribute> firstAttributes, List<ParserAttribute> secondAttributes) {
        List<PathExpression> fromPaths = new ArrayList<PathExpression>();
        List<PathExpression> toPaths = new ArrayList<PathExpression>();
        for (int i = 0; i < firstAttributes.size(); i++) {
            fromPaths.add(firstAttributes.get(i).getAttributePath());
            toPaths.add(secondAttributes.get(i).getAttributePath());
        }
        return new JoinCondition(fromPaths, toPaths);
    }

    private void findCyclicJoinConditionsForSingleAtoms(ParserView view, List<String> sourceVariablesToExclude) {
        for (ParserAtom parserAtom : view.getAtoms()) {
            generateCyclicJoinConditionsForSingleAtom(parserAtom, view, sourceVariablesToExclude);
        }
    }

    private void generateCyclicJoinConditionsForSingleAtom(ParserAtom atom, ParserView view, List<String> sourceVariablesToExclude) {
        for (int i = 0; i < atom.getAttributes().size(); i++) {
            ParserAttribute attributeI = atom.getAttributes().get(i);
            if (!attributeI.isVariable() || sourceVariablesToExclude.contains(attributeI.getVariable())) {
                continue;
            }
            for (int j = i + 1; j < atom.getAttributes().size(); j++) {
                ParserAttribute attributeJ = atom.getAttributes().get(j);
                if (!attributeJ.isVariable() || sourceVariablesToExclude.contains(attributeI.getVariable())) {
                    continue;
                }
                if (attributeI.getVariable().equals(attributeJ.getVariable())) {
                    JoinCondition cyclicJoin = new JoinCondition(attributeI.getAttributePath(), attributeJ.getAttributePath());
                    cyclicJoin.setMonodirectional(false);
                    cyclicJoin.setMandatory(true);
                    view.addJoinCondition(cyclicJoin);
                }
            }
        }
    }

    /////////////////   CORRESPONDENCES  ////////////////////////
    private void findConstantCorrespondences(ParserTGD parserTgd) {
        for (ParserAtom atom : parserTgd.getTargetView().getAtoms()) {
            for (ParserAttribute attribute : atom.getAttributes()) {
                if (!attribute.isVariable() && !attribute.isExpression()) {
                    ISourceValue sourceValue = new ConstantValue(attribute.getValue());
                    ValueCorrespondence correspondence = new ValueCorrespondence(sourceValue, attribute.getAttributePath());
                    parserTgd.addCorrespondence(correspondence);
                }
            }
        }
    }

    private void findSourceToTargetCorrespondences(ParserTGD tgd) {
        Map<ParserAttribute, List<ParserAttribute>> equalities = new HashMap<ParserAttribute, List<ParserAttribute>>();
        for (ParserAtom leftAtom : tgd.getSourceView().getAtoms()) {
            for (ParserAtom rightAtom : tgd.getTargetView().getAtoms()) {
                findCommonAttributesForCorrespondences(leftAtom, rightAtom, equalities);
            }
        }
        for (ParserAttribute leftAttribute : equalities.keySet()) {
            for (ParserAttribute rightAttribute : equalities.get(leftAttribute)) {
                if (!containsCorrespondenceForSameTargetPath(tgd.getCorrespondences(), rightAttribute.getAttributePath()))  {
                    ValueCorrespondence correspondence = new ValueCorrespondence(leftAttribute.getAttributePath(), rightAttribute.getAttributePath());
                    tgd.addCorrespondence(correspondence);
                }
            }
        }
    }

    private void findComplexExpressionCorrespondeces(ParserTGD parserTgd) {
        for (ParserAtom atom : parserTgd.getTargetView().getAtoms()) {
            for (ParserAttribute attribute : atom.getAttributes()) {
                if (attribute.isExpression()) {
                    Expression expression = (Expression) attribute.getValue();
                    List<PathExpression> pathList = replaceVariablesInExpression(expression, parserTgd);
                    Expression newExpression = new Expression(expression.toString());
                    ValueCorrespondence correspondence = new ValueCorrespondence(pathList, attribute.getAttributePath(), newExpression);
                    parserTgd.addCorrespondence(correspondence);
                }
            }
        }
    }

    private List<PathExpression> replaceVariablesInExpression(Expression expression, ParserTGD parserTgd) {
        if (logger.isDebugEnabled()) logger.debug("Replacing paths in expression: " + expression);
        List<PathExpression> pathList = new ArrayList<PathExpression>();
        JEP jepExpression = expression.getJepExpression();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            String formulaVariable = (String) variable.getDescription();
            PathExpression variablePath = findVariablePath(formulaVariable, parserTgd);
            pathList.add(variablePath);
            variable.setDescription(variablePath);
            variable.setOriginalDescription(variablePath);
        }
        if (logger.isDebugEnabled()) logger.debug("Resulting expression: " + expression);
        return pathList;
    }

    private PathExpression findVariablePath(String formulaVariable, ParserTGD parserTgd) {
        for (ParserAtom parserAtom : parserTgd.getSourceView().getAtoms()) {
            for (ParserAttribute parserAttribute : parserAtom.getAttributes()) {
                if (parserAttribute.isVariable()) {
                    String attributeVariable = "$" + (String) parserAttribute.getVariable();
                    if (attributeVariable.equals(formulaVariable)) {
                        return parserAttribute.getAttributePath();
                    }
                }
            }
        }
        throw new ParserException("Variable " + formulaVariable + " does not appear in atoms of view: " + parserTgd.getSourceView());
    }

    private void findCommonAttributesForCorrespondences(ParserAtom firstAtom, ParserAtom secondAtom, Map<ParserAttribute, List<ParserAttribute>> equalities) {
        for (ParserAttribute firstAttribute : firstAtom.getAttributes()) {
            for (ParserAttribute secondAttribute : secondAtom.getAttributes()) {
                if (!(firstAttribute.isVariable() && secondAttribute.isVariable())) {
                    continue;
                }
                if (firstAttribute.getVariable().equals(secondAttribute.getVariable())) {
                    List<ParserAttribute> equalitiesForAttribute = equalities.get(firstAttribute);
                    if (equalitiesForAttribute == null) {
                        equalitiesForAttribute = new ArrayList<ParserAttribute>();
                        equalities.put(firstAttribute, equalitiesForAttribute);
                    }
                    equalitiesForAttribute.add(secondAttribute);
                }
            }
        }
    }

    private void processFDs(List<ParserFD> sourceFDs, IDataSourceProxy source, boolean generateSource) {
        GeneratePathExpression pathGenerator = new GeneratePathExpression();
        for (ParserFD parserFD : sourceFDs) {
            INode setNode = findNode(parserFD.getSet(), source);
            PathExpression setPath = pathGenerator.generatePathFromRoot(setNode);
            List<String> setPathSteps = setPath.getPathSteps();
            List<PathExpression> leftPaths = new ArrayList<PathExpression>();
            for (String attributeName : parserFD.getLeftAttributes()) {
                leftPaths.add(generatePath(setPathSteps, parserFD, attributeName));
            }
            List<PathExpression> rightPaths = new ArrayList<PathExpression>();
            for (String attributeName : parserFD.getRightAttributes()) {
                rightPaths.add(generatePath(setPathSteps, parserFD, attributeName));
            }
            if (parserFD.isKey() || parserFD.isPrimaryKey()) {
                KeyConstraint keyConstraint = new KeyConstraint(leftPaths, parserFD.isPrimaryKey());
                source.addKeyConstraint(keyConstraint);
                if (parserFD.isPrimaryKey() && generateSource) {
                    for (PathExpression leftPath : leftPaths) {
                        INode nodeInSchema = new FindNode().findNodeInSchema(leftPath, source);
                        nodeInSchema.setNotNull(true);
                    }
                }
            } else {
                FunctionalDependency dependency = new FunctionalDependency(leftPaths, rightPaths);
                source.addFunctionalDependency(dependency);
            }
        }
    }

    private INode findNode(String setName, IDataSourceProxy dataSource) {
        INode tupleNode = dataSource.getSchema();
        for (INode setNode : tupleNode.getChildren()) {
            if (setNode.getLabel().equals(setName)) {
                return setNode;
            }
        }
        throw new ParserException("Unable to find set for functional dependency: " + setName + " in: " + dataSource);
    }

    private PathExpression generatePath(List<String> setPathSteps, ParserFD parserFD, String attributeName) {
        List<String> attributeSteps = new ArrayList<String>(setPathSteps);
        attributeSteps.add(parserFD.getSet() + "Tuple");
        attributeSteps.add(attributeName);
        return new PathExpression(attributeSteps);

    }

    public String clean(String expressionString) {
        String result = expressionString.trim();
        return result.substring(1, result.length() - 1);
    }

    private boolean containsCorrespondenceForSameTargetPath(List<ValueCorrespondence> correspondences, PathExpression attributePath) {
        for (ValueCorrespondence correspondence : correspondences) {
            if (correspondence.getTargetPath().equals(attributePath)) {
                return true;
            }
        }
        return false;
    }
}
