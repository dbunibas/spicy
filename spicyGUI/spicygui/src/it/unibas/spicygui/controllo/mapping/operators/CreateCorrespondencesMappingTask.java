/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
 
package it.unibas.spicygui.controllo.mapping.operators;


import it.unibas.spicy.model.correspondence.ConstantValue;
import it.unibas.spicy.model.correspondence.DateFunction;
import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.correspondence.NewIdFunction;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberi;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberiLogic;
import it.unibas.spicygui.widget.JoinConstraint;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class CreateCorrespondencesMappingTask implements ICreateCorrespondences {

    private GeneratePathExpression generatePathFromNode = new GeneratePathExpression();
    private CreaWidgetAlberiLogic constraintCreator = new CreaWidgetAlberiLogic(CreaWidgetAlberi.MAPPING_TASK_TYPE);
    private static Log logger = LogFactory.getLog(CreateCorrespondencesMappingTask.class);
    private Modello modello;

    public CreateCorrespondencesMappingTask() {
        executeInjection();
    }

    public void createCorrespondence(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetTree caratteristicheWidgetTreeSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
        PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeSource.getINode());
        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());
        ValueCorrespondence valueCorrespondence = new ValueCorrespondence(sourcePathExpression, targetPathExpression);
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.addCorrespondence(valueCorrespondence);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
    }

    public void createCorrespondenceWithSourceValue(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetInterConst caratteristicheConst = (CaratteristicheWidgetInterConst) mainLayer.getChildConstraint(sourceWidget);
        ISourceValue sourceValue = typeOfSourceValue(caratteristicheConst);
        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());

        ValueCorrespondence valueCorrespondence = new ValueCorrespondence(sourceValue, targetPathExpression);
        if (logger.isDebugEnabled()) {
            logger.debug(valueCorrespondence);
        }
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.addCorrespondence(valueCorrespondence);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
    }

    public void createCorrespondenceWithFunction(LayerWidget mainLayer, Widget targetWidget, CaratteristicheWidgetInterFunction caratteristicheFunction, ConnectionInfo connectionInfo) {
        List<VMDPinWidgetSource> listaSource = caratteristicheFunction.getSourceList();
        List<PathExpression> sourcePaths = new ArrayList<PathExpression>();
        for (VMDPinWidgetSource sourceWidget : listaSource) {
            CaratteristicheWidgetTree caratteristicheSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(sourceWidget);
            PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheSource.getINode());
            sourcePaths.add(sourcePathExpression);

        }
        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());
        Expression transformationFunction = new Expression(caratteristicheFunction.getExpressionFunction());

        ValueCorrespondence valueCorrespondence = new ValueCorrespondence(sourcePaths, targetPathExpression, transformationFunction);
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);

        mappingTask.addCorrespondence(valueCorrespondence);
        caratteristicheFunction.setValueCorrespondence(valueCorrespondence);
        if (connectionInfo != null) {
            connectionInfo.setValueCorrespondence(valueCorrespondence);
        }
    }

    public void createCorrespondenceWithFunctionalDep(LayerWidget mainLayer, CaratteristicheWidgetInterFunctionalDep caratteristicheFunctionalDep, ConnectionInfo connectionInfo) {
        List<VMDPinWidget> listaSource = caratteristicheFunctionalDep.getSourceList();
        List<PathExpression> leftPaths = new ArrayList<PathExpression>();
        for (VMDPinWidget source : listaSource) {
            CaratteristicheWidgetTree caratteristicheSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(source);
            PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheSource.getINode());
            leftPaths.add(sourcePathExpression);

        }
        List<VMDPinWidget> listaTarget = caratteristicheFunctionalDep.getTargetList();
        List<PathExpression> rightPaths = new ArrayList<PathExpression>();
        for (VMDPinWidget target : listaTarget) {
            CaratteristicheWidgetTree caratteristicheSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(target);
            PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheSource.getINode());
            rightPaths.add(sourcePathExpression);

        }

        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        IDataSourceProxy dataSource = null;
        if (caratteristicheFunctionalDep.isSource()) {
            dataSource = mappingTask.getSourceProxy();
        } else {
            dataSource = mappingTask.getTargetProxy();
        }
        FunctionalDependency functionalDependency = new FunctionalDependency(leftPaths, rightPaths);
        dataSource.addFunctionalDependency(functionalDependency);
        caratteristicheFunctionalDep.setFunctionalDependency(functionalDependency);

////////////        if (connectionInfo != null) {
////////////            connectionInfo.setValueCorrespondence(valueCorrespondence);
////////////        }

    }


//    public void createImplied(ConnectionInfo connectionInfo, boolean implied) {
//        ValueCorrespondence valueCorrespondence = null;
//        if (connectionInfo.getValueCorrespondence().getSourceValue() != null) {
//            valueCorrespondence = new ValueCorrespondence(connectionInfo.getValueCorrespondence(), implied);
//        } else if (verificaFunzioneDiTrasformazione(connectionInfo.getValueCorrespondence())) {
//            valueCorrespondence = new ValueCorrespondence(connectionInfo.getValueCorrespondence(), implied);
//        } else {
//            valueCorrespondence = new ValueCorrespondence(connectionInfo.getValueCorrespondence(), implied);
//        }
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
//        mappingTask.addCorrespondence(valueCorrespondence);
//        connectionInfo.setValueCorrespondence(valueCorrespondence);
//    }

    private ISourceValue typeOfSourceValue(CaratteristicheWidgetInterConst caratteristicheConst) {
        if (caratteristicheConst.getTipoStringa() || caratteristicheConst.getTipoNumero()) {
            return new ConstantValue(caratteristicheConst.getCostante());
        }
        if (caratteristicheConst.getTipoFunzione()) {
            String dateFunction = NbBundle.getMessage(Costanti.class, Costanti.INPUT_TEXT_CONSTANT_FUNCTION1);
            String incrementFunction = NbBundle.getMessage(Costanti.class, Costanti.INPUT_TEXT_CONSTANT_FUNCTION2);
            if (caratteristicheConst.getCostante().equals(dateFunction)) {
                return new DateFunction();
            }
            if (caratteristicheConst.getCostante().equals(incrementFunction)) {
                return new NewIdFunction();
            }
        }
        return null;
    }

    public void undo(ValueCorrespondence valueCorrespondence) {
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.addCorrespondence(valueCorrespondence);
    }

    public void undo(SelectionCondition selectionCondition, IDataSourceProxy dataSource) {
        dataSource.addSelectionCondition(selectionCondition);
    }

    private boolean verificaFunzioneDiTrasformazione(ValueCorrespondence valueCorrespondence) {
        if (valueCorrespondence.getSourcePaths().size() > 1) {
            return true;
        }
        String transformationFunctionString = valueCorrespondence.getTransformationFunction().toString();
        String sourcePathString = valueCorrespondence.getSourcePaths().get(0).toString();
        return (!transformationFunctionString.equals(sourcePathString));
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Stroke getStroke() {
        return Costanti.BASIC_STROKE;
    }

    public void createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, boolean source) {
        CaratteristicheWidgetTree caratteristicheWidgetTreeSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
        PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeSource.getINode());
        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());

        JoinCondition joinCondition = new JoinCondition(sourcePathExpression, targetPathExpression);
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        IDataSourceProxy DTsource = null;
        if (source) {
            DTsource = mappingTask.getSourceProxy();
        } else {
            DTsource = mappingTask.getTargetProxy();
        }

        try {
            Boolean success = DTsource.addJoinCondition(joinCondition);
            checkAddJoincondition(success);
            if (success != null) {
                constraintCreator.creaWidgetJoinCondition(joinCondition, DTsource, source);
            }
        } catch (Exception iae) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.JOINCONDITION_NO), DialogDescriptor.ERROR_MESSAGE));
        }
    }

    private void checkAddJoincondition(Boolean success) {
        if (success == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.JOINCONDITION_NO), DialogDescriptor.ERROR_MESSAGE));
        } else if (success == Boolean.FALSE) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.JOINCONDITION_DUPLICATE), DialogDescriptor.INFORMATION_MESSAGE));
            this.modello.putBean(Costanti.RECREATE_TREE, new Boolean(true));
        }
    }

    public JoinCondition createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, JoinConstraint joinConstraint, JoinCondition joinCondition, List<INode> fromPathNodes, boolean source) {
        CaratteristicheWidgetTree caratteristicheWidgetTreeSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
        PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeSource.getINode());
        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());
        if (joinCondition != null) {
            joinCondition.addPaths(sourcePathExpression, targetPathExpression);
        } else {
            joinCondition = new JoinCondition(sourcePathExpression, targetPathExpression);
            joinConstraint.setJoinCondition(joinCondition);
        }
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        IDataSourceProxy dataSource;
        if (source) {
            dataSource = mappingTask.getSourceProxy();
        } else {
            dataSource = mappingTask.getTargetProxy();
        }
        joinConstraint.setDataSource(dataSource);
        INode iNode = constraintCreator.createSingleJoin(sourcePathExpression, dataSource, targetPathExpression, source, joinConstraint);
        fromPathNodes.add(iNode);
        return joinCondition;
    }

    public void createSelectionCondition(INode iNode, String expressionString, IDataSourceProxy dataSource, SelectionConditionInfo selectionConditionInfo) {
        PathExpression pathExpression = generatePathFromNode.generatePathFromRoot(iNode);
        Expression expression = new Expression(expressionString);
        SelectionCondition selectionCondition = new SelectionCondition(pathExpression, expression, dataSource.getIntermediateSchema());
        dataSource.addSelectionCondition(selectionCondition);
        selectionConditionInfo.setSelectionCondition(selectionCondition);
    }
}
