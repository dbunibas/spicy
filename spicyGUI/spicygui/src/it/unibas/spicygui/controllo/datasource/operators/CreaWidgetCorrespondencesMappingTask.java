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
package it.unibas.spicygui.controllo.datasource.operators;

import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingData;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.controllo.provider.MyPopupProviderConnectionMappingTask;
import it.unibas.spicygui.controllo.provider.MySelectConnectionActionProvider;
import it.unibas.spicygui.controllo.provider.intermediatezone.ConnectionCreator;
import it.unibas.spicygui.controllo.provider.intermediatezone.WidgetCreator;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Point;
import java.awt.Stroke;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class CreaWidgetCorrespondencesMappingTask implements ICreaWidgetCorrespondences {

    private static Log logger = LogFactory.getLog(CreaWidgetCorrespondencesMappingTask.class);
    private ConnectionCreator connectionCreator = new ConnectionCreator();
    private Modello modello;
    private GraphSceneGlassPane glassPane;
    private JLayeredPaneCorrespondences jLayeredPane;
    private JPanel pannelloPrincipale;
    private WidgetCreator widgetCreator = new WidgetCreator();
    private FindNode finder = new FindNode();
    private CreaWidgetEsisteSelectionCondition checker = new CreaWidgetEsisteSelectionCondition();
    private Random random = new Random();

    public CreaWidgetCorrespondencesMappingTask(JLayeredPaneCorrespondences jLayeredPane) {
        this.glassPane = jLayeredPane.getGlassPane();
        this.pannelloPrincipale = jLayeredPane.getPannelloPrincipale();
        this.jLayeredPane = jLayeredPane;
//        jLayeredPane.createIntermediateZonePopUp();
        executeInjection();
    } 

    public void creaWidgetIconForSelectionCondition() {
        MappingTask mappingTask = jLayeredPane.getMappingTaskTopComponent().getScenario().getMappingTask();
        IDataSourceProxy source = mappingTask.getSourceProxy();
        IDataSourceProxy target = mappingTask.getTargetProxy();
        for (SelectionCondition selectionCondition : source.getSelectionConditions()) {
            for (PathExpression pathExpression : selectionCondition.getSetPaths()) {
                INode iNode = finder.findNodeInSchema(pathExpression, source);
                VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
                CaratteristicheWidgetTree caratteristicheWidgetTree = (CaratteristicheWidgetTree) glassPane.getMainLayer().getChildConstraint(vMDPinWidget);
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) caratteristicheWidgetTree.getTreePath().getLastPathComponent();
                SelectionConditionInfo selectionConditionInfo = creaSelectionConditionInfo(iNode);
                selectionConditionInfo.setExpressionString(selectionCondition.getCondition().toString());
                selectionConditionInfo.setSelectionCondition(selectionCondition);
                checker.creaWidgetEsisteSelectionCondition(treeNode, selectionCondition.getCondition().toString(), selectionCondition);
            }
        }
        for (SelectionCondition selectionCondition : target.getSelectionConditions()) {
            for (PathExpression pathExpression : selectionCondition.getSetPaths()) {
                INode iNode = finder.findNodeInSchema(pathExpression, target);
                VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
                CaratteristicheWidgetTree caratteristicheWidgetTree = (CaratteristicheWidgetTree) glassPane.getMainLayer().getChildConstraint(vMDPinWidget);
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) caratteristicheWidgetTree.getTreePath().getLastPathComponent();
                SelectionConditionInfo selectionConditionInfo = creaSelectionConditionInfo(iNode);
                selectionConditionInfo.setExpressionString(selectionCondition.getCondition().toString());
                selectionConditionInfo.setSelectionCondition(selectionCondition);
                checker.creaWidgetEsisteSelectionCondition(treeNode, selectionCondition.getCondition().toString(), selectionCondition);
            }
        }
    }

    private SelectionConditionInfo creaSelectionConditionInfo(INode iNode) {
        SelectionConditionInfo selectionConditionInfo = null;
        if (iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO) != null) {
            selectionConditionInfo = (SelectionConditionInfo) iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO);
        } else {
            selectionConditionInfo = new SelectionConditionInfo();
            iNode.addAnnotation(Costanti.SELECTION_CONDITON_INFO, selectionConditionInfo);
        }
        return selectionConditionInfo;
    }

    public void creaWidgetCorrespondences() {
        MappingTask mappingTask = jLayeredPane.getMappingTaskTopComponent().getScenario().getMappingTask();
        MappingData mappingData = mappingTask.getMappingData();
        if (mappingTask.getValueCorrespondences().size() > 0) {
            for (int i = 0; i < mappingTask.getValueCorrespondences().size(); i++) {
                ValueCorrespondence valueCorrespondence = mappingTask.getValueCorrespondences().get(i);
                VariableCorrespondence variableCorrespondence = mappingData.getCorrespondences().get(i);
                if (valueCorrespondence.getSourceValue() != null) {
                    creaSourceValue(valueCorrespondence, variableCorrespondence, mappingTask);
                } else {
                    if (verificaFunzioneDiTrasformazione(valueCorrespondence)) {
                        creaFunzione(valueCorrespondence, variableCorrespondence, mappingTask);
                    } else {
                        creaCorrespondence(variableCorrespondence, valueCorrespondence, mappingTask);
                    }
                }
            }
        }
    }

    private Point calculateRandomPoint(GraphSceneGlassPane glassPane) {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        int x = (frame.getSize().width / 2) + 20;
        int y = random.nextInt((int) (frame.getSize().height * 0.5)) + (int) (frame.getSize().height * 0.2);
        Point point = SwingUtilities.convertPoint(frame, x, y, glassPane);
        return point;
    }

    private void creaSourceValue(ValueCorrespondence valueCorrespondence, VariableCorrespondence variableCorrespondence, MappingTask mappingTask) {
        ISourceValue sourceValue = valueCorrespondence.getSourceValue();

        Scene scene = glassPane.getScene();
        LayerWidget mainLayer = glassPane.getMainLayer();
        LayerWidget connectionLayer = glassPane.getConnectionLayer();
        Point point = calculateRandomPoint(glassPane);
        Widget sourceWidget = widgetCreator.createConstantWidgetFromSourceValue(scene, mainLayer, connectionLayer, pannelloPrincipale, point, sourceValue, glassPane);

        IDataSourceProxy target = mappingTask.getTargetProxy();

        INode targetNode = finder.findNodeInSchema(valueCorrespondence.getTargetPath(), target);
        Widget targetWidget = (Widget) targetNode.getAnnotation(Costanti.PIN_WIDGET_TREE);

        CaratteristicheWidgetInterConst caratteristicheWidget = (CaratteristicheWidgetInterConst) mainLayer.getChildConstraint(sourceWidget);
        impostaTipo(sourceValue, caratteristicheWidget);

        ConnectionInfo connectionInfo = connectionCreator.createConnectionToTarget(sourceWidget, targetWidget, mainLayer, connectionLayer);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
//        analisiFiltro.creaWidgetEsisteFiltro(connectionInfo.getConnectionWidget(), connectionInfo);
        scene.validate();
    }

    private void impostaTipo(ISourceValue sourceValue, CaratteristicheWidgetInterConst caratteristicheWidget) {
        String valoreCostante = sourceValue.toString();
        if (Utility.verificaVirgolette(valoreCostante)) {
            caratteristicheWidget.setTipoStringa(true);
            caratteristicheWidget.getFormValidation().setTextFieldState(true);
            return;
        }
        if (Utility.verificaNumero(valoreCostante)) {
            caratteristicheWidget.setTipoNumero(true);
            caratteristicheWidget.getFormValidation().setTextFieldState(true);
            return;
        }
        caratteristicheWidget.setTipoFunzione(true);
        caratteristicheWidget.getFormValidation().setComboBoxState(true);
    }

    private boolean verificaFunzioneDiTrasformazione(ValueCorrespondence valueCorrespondence) {
        if (valueCorrespondence.getSourcePaths().size() > 1) {
            return true;
        }
        String transformationFunctionString = valueCorrespondence.getTransformationFunction().toString();
        String sourcePathString = valueCorrespondence.getSourcePaths().get(0).toString();
        return (!transformationFunctionString.equals(sourcePathString));
    }

    private void creaFunzione(ValueCorrespondence valueCorrespondence, VariableCorrespondence variableCorrespondence, MappingTask mappingTask) {
        Scene scene = glassPane.getScene();
        LayerWidget mainLayer = glassPane.getMainLayer();
        LayerWidget connectionLayer = glassPane.getConnectionLayer();
        Point point = calculateRandomPoint(glassPane);
        Widget functionWidget = widgetCreator.createFunctionWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, glassPane);
        CaratteristicheWidgetInterFunction caratteristicheWidget = (CaratteristicheWidgetInterFunction) mainLayer.getChildConstraint(functionWidget);
        caratteristicheWidget.setExpressionFunction(valueCorrespondence.getTransformationFunction().toString());
        caratteristicheWidget.setValueCorrespondence(valueCorrespondence);
        IDataSourceProxy source = mappingTask.getSourceProxy();

        for (PathExpression pathExpression : valueCorrespondence.getSourcePaths()) {
            INode sourceNode = finder.findNodeInSchema(pathExpression, source);
            Widget sourceWidget = (Widget) sourceNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
            connectionCreator.createConnectionToFunction(sourceWidget, functionWidget, mainLayer, connectionLayer);
        }

        IDataSourceProxy target = mappingTask.getTargetProxy();
        INode targetNode = finder.findNodeInSchema(valueCorrespondence.getTargetPath(), target);
        Widget targetWidget = (Widget) targetNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
        caratteristicheWidget.setTargetWidget((VMDPinWidgetTarget) targetWidget);
        ConnectionInfo connectionInfo = connectionCreator.createConnectionFromFunction(functionWidget, targetWidget, mainLayer, connectionLayer);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
//        analisiFiltro.creaWidgetEsisteFiltro(connectionInfo.getConnectionWidget(), connectionInfo);
        scene.validate();
    }

    public void creaWidgetFunctionalDependecies(FunctionalDependency functionalDependency, IDataSourceProxy dataSource, boolean isSource) {
        Scene scene = glassPane.getScene();
        LayerWidget mainLayer = glassPane.getMainLayer();
        LayerWidget connectionLayer = glassPane.getConnectionLayer();
        Point point = calculateRandomPoint(glassPane);
        Widget functionalDependenciesWidget = widgetCreator.createFunctionalDependencyWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, glassPane);
        CaratteristicheWidgetInterFunctionalDep caratteristicheWidget = (CaratteristicheWidgetInterFunctionalDep) mainLayer.getChildConstraint(functionalDependenciesWidget);
        caratteristicheWidget.setFunctionalDependency(functionalDependency);
        caratteristicheWidget.setSource(isSource);

        for (PathExpression pathExpression : functionalDependency.getLeftPaths()) {
            INode sourceNode = finder.findNodeInSchema(pathExpression, dataSource);
            Widget sourceWidget = (Widget) sourceNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
            connectionCreator.createConnectionToFunctionalDependecy(sourceWidget, functionalDependenciesWidget, mainLayer, connectionLayer);
        }

        for (PathExpression pathExpression : functionalDependency.getRightPaths()) {
            INode targetNode = finder.findNodeInSchema(pathExpression, dataSource);
            Widget targetWidget = (Widget) targetNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
            connectionCreator.createConnectionFromFunctionalDependecy(functionalDependenciesWidget, targetWidget, mainLayer, connectionLayer);
        }


//        ConnectionInfo connectionInfo = connectionCreator.createConnectionFromFunction(
//        connectionInfo.setValueCorrespondence(valueCorrespondence);
//        analisiFiltro.creaWidgetEsisteFiltro(connectionInfo.getConnectionWidget(), connectionInfo);
        scene.validate();
    }

    private void creaCorrespondence(VariableCorrespondence variableCorrespondence, ValueCorrespondence valueCorrespondence, MappingTask mappingTask) {
        INode iNodeSource = finder.findNodeInSchema(valueCorrespondence.getSourcePaths().get(0), mappingTask.getSourceProxy());
        VMDPinWidgetSource sourceWidget = (VMDPinWidgetSource) iNodeSource.getAnnotation(Costanti.PIN_WIDGET_TREE);
        INode iNodeTarget = finder.findNodeInSchema(valueCorrespondence.getTargetPath(), mappingTask.getTargetProxy());
        VMDPinWidgetTarget targetWidget = (VMDPinWidgetTarget) iNodeTarget.getAnnotation(Costanti.PIN_WIDGET_TREE);

        ConnectionWidget connection = new ConnectionWidget(glassPane.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        Stroke stroke = Costanti.BASIC_STROKE;
        connection.setStroke(stroke);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionWidget(connection);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
        connection.setToolTipText(connectionInfo.getValueCorrespondence().toString());
        glassPane.getConnectionLayer().addChild(connection, connectionInfo);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionMappingTask(glassPane.getScene())));
        connection.getActions().addAction(ActionFactory.createSelectAction(new MySelectConnectionActionProvider(glassPane.getConnectionLayer())));
//        analisiFiltro.creaWidgetEsisteFiltro(connection, connectionInfo);
        glassPane.getScene().validate();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void creaWidgetFunctionalDependencies(IDataSourceProxy dataSource, boolean isSource) {
        List<FunctionalDependency> listaFunctionalDependencies = dataSource.getFunctionalDependencies();
        CreaWidgetCorrespondencesMappingTask creator = new CreaWidgetCorrespondencesMappingTask(jLayeredPane);
        for (FunctionalDependency functionalDependency : listaFunctionalDependencies) {
            creator.creaWidgetFunctionalDependecies(functionalDependency, dataSource, isSource);
        }

    }
}
