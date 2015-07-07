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
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.controllo.provider.intermediatezone.ConnectionCreator;
import it.unibas.spicygui.controllo.provider.intermediatezone.WidgetCreator;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class CreaWidgetCorrespondencesTGD implements ICreaWidgetCorrespondences {

    private static Log logger = LogFactory.getLog(CreaWidgetCorrespondencesTGD.class);
    private FindNode finder = new FindNode();
    private Random random = new Random();
    private WidgetCreator widgetCreator = new WidgetCreator();
    private ConnectionCreator connectionCreator = new ConnectionCreator();
    private Modello modello;
    private GraphSceneGlassPane glassPane;
    private JLayeredPaneCorrespondences jLayeredPane;
    private JPanel pannelloPrincipale;
    private MappingTask mappingTask;

    public CreaWidgetCorrespondencesTGD(JLayeredPaneCorrespondences jLayeredPane) {
        this.glassPane = jLayeredPane.getGlassPane();
        this.pannelloPrincipale = jLayeredPane.getPannelloPrincipale();
        this.jLayeredPane = jLayeredPane;
        this.mappingTask = jLayeredPane.getMappingTaskTopComponent().getScenario().getMappingTask();
//        jLayeredPane.createIntermediateZonePopUp();
        executeInjection();
    }

    public void creaWidgetCorrespondences() {
        FORule foRule = jLayeredPane.getMappingTaskTopComponent().getScenario().getSelectedFORule();
        if (foRule != null && foRule.getCoveredCorrespondences().size() > 0) {
            for (int i = 0; i < foRule.getCoveredCorrespondences().size(); i++) {
                VariableCorrespondence variableCorrespondence = foRule.getCoveredCorrespondences().get(i);
//                creaCandidateCorrespondence(variableCorrespondence, mappingTask);
//                creaCandidateCorrespondence(valueCorrespondence, mappingTask);
                if (variableCorrespondence.getSourceValue() != null) {
                    creaSourceValue(variableCorrespondence);
                } else {
                    if (verificaFunzioneDiTrasformazione(variableCorrespondence)) {
                        creaFunzione(variableCorrespondence);
                    } else {
                        creaCorrespondence(variableCorrespondence);
                    }
                }

            }
        }
    }

    private Point calculateRandomPoint(GraphSceneGlassPane glassPane) {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        int x = (frame.getSize().width / 2) - 20;
        int y = random.nextInt((int) (frame.getSize().height * 0.5)) + (int) (frame.getSize().height * 0.2);
        Point point = SwingUtilities.convertPoint(frame, x, y, glassPane);
        return point;
    }

    private void creaSourceValue(VariableCorrespondence variableCorrespondence) {
        ISourceValue sourceValue = variableCorrespondence.getSourceValue();

        Scene scene = glassPane.getScene();
        LayerWidget mainLayer = glassPane.getMainLayer();
        LayerWidget connectionLayer = glassPane.getConnectionLayer();
        Point point = calculateRandomPoint(glassPane);
        Widget sourceWidget = widgetCreator.createConstantWidgetFromSourceValue(scene, mainLayer, connectionLayer, pannelloPrincipale, point, sourceValue, glassPane);

        IDataSourceProxy target = mappingTask.getTargetProxy();

//        INode targetNode = finder.findNodeInSchema(variableCorrespondence.getTargetPath(), target);
        INode targetNode = finder.findNodeInSchema(variableCorrespondence.getTargetPath().getAbsolutePath(), target);
        Widget targetWidget = (Widget) targetNode.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);

        CaratteristicheWidgetInterConst caratteristicheWidget = (CaratteristicheWidgetInterConst) mainLayer.getChildConstraint(sourceWidget);
        impostaTipo(sourceValue, caratteristicheWidget);

        ConnectionInfo connectionInfo = connectionCreator.createConnectionToTarget(sourceWidget, targetWidget, mainLayer, connectionLayer);
        connectionInfo.setVariableCorrespondence(variableCorrespondence);
//        connectionInfo.setValueCorrespondence(variableCorrespondence.);
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

    private boolean verificaFunzioneDiTrasformazione(VariableCorrespondence variableCorrespondence) {
        if (variableCorrespondence.getSourcePaths().size() > 1) {
            return true;
        }
        String transformationFunctionString = variableCorrespondence.getTransformationFunction().toString();
        String sourcePathString = variableCorrespondence.getSourcePaths().get(0).toString();
        return (!transformationFunctionString.equals(sourcePathString));
    }

    private void creaFunzione(VariableCorrespondence variableCorrespondence) {
        Scene scene = glassPane.getScene();
        LayerWidget mainLayer = glassPane.getMainLayer();
        LayerWidget connectionLayer = glassPane.getConnectionLayer();
        Point point = calculateRandomPoint(glassPane);
        Widget functionWidget = widgetCreator.createFunctionWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, glassPane);
        CaratteristicheWidgetInterFunction caratteristicheWidget = (CaratteristicheWidgetInterFunction) mainLayer.getChildConstraint(functionWidget);
        caratteristicheWidget.setExpressionFunction(variableCorrespondence.getTransformationFunction().toString());
//        caratteristicheWidget.setVariableCorrespondence(variableCorrespondence);
        IDataSourceProxy source = mappingTask.getSourceProxy();

        for (VariablePathExpression variablePathExpression : variableCorrespondence.getSourcePaths()) {
//            INode sourceNode = finder.findNodeInSchema(variablePathExpression, source);
            INode sourceNode = finder.findNodeInSchema(variablePathExpression.getAbsolutePath(), source);
            Widget sourceWidget = (Widget) sourceNode.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);
            connectionCreator.createConnectionToFunction(sourceWidget, functionWidget, mainLayer, connectionLayer);
        }

        IDataSourceProxy target = mappingTask.getTargetProxy();
//        INode targetNode = finder.findNodeInSchema(variableCorrespondence.getTargetPath(), target);
        INode targetNode = finder.findNodeInSchema(variableCorrespondence.getTargetPath().getAbsolutePath(), target);
        Widget targetWidget = (Widget) targetNode.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);
        caratteristicheWidget.setTargetWidget((VMDPinWidgetTarget) targetWidget);
        ConnectionInfo connectionInfo = connectionCreator.createConnectionFromFunction(functionWidget, targetWidget, mainLayer, connectionLayer);
//        connectionInfo.setVariableCorrespondence(variableCorrespondence);
        connectionInfo.setVariableCorrespondence(variableCorrespondence);
//        analisiFiltro.creaWidgetEsisteFiltro(connectionInfo.getConnectionWidget(), connectionInfo);
        scene.validate();
    }

    private void creaCorrespondence(VariableCorrespondence variableCorrespondence) {
//        INode iNodeSource = finder.findNodeInSchema(variableCorrespondence.getSourcePaths().get(0), mappingTask.getSource());
        INode iNodeSource = finder.findNodeInSchema(variableCorrespondence.getSourcePaths().get(0).getAbsolutePath(), mappingTask.getSourceProxy());
        VMDPinWidgetSource sourceWidget = (VMDPinWidgetSource) iNodeSource.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);
//        INode iNodeTarget = finder.findNodeInSchema(variableCorrespondence.getTargetPath(), mappingTask.getTarget());
        INode iNodeTarget = finder.findNodeInSchema(variableCorrespondence.getTargetPath().getAbsolutePath(), mappingTask.getTargetProxy());
        VMDPinWidgetTarget targetWidget = (VMDPinWidgetTarget) iNodeTarget.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);

        ConnectionWidget connection = new ConnectionWidget(glassPane.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        Stroke stroke = Costanti.BASIC_STROKE;
        connection.setStroke(stroke);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionWidget(connection);
//        connectionInfo.setVariableCorrespondence(variableCorrespondence);
        connectionInfo.setVariableCorrespondence(variableCorrespondence);

        connection.setToolTipText(connectionInfo.getVariableCorrespondence().toString());

        glassPane.getConnectionLayer().addChild(connection, connectionInfo);
//        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionMappingTask(glassPane.getScene())));
//        connection.getActions().addAction(ActionFactory.createSelectAction(new MySelectConnectionActionProvider(glassPane.getConnectionLayer())));

//        analisiFiltro.creaWidgetEsisteFiltro(connection, connectionInfo);

        glassPane.getScene().validate();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    private void setColorOnConfidence(ConnectionInfo connectionInfo, ConnectionWidget connection) {
        if (connectionInfo.getConfidence() != 1) {
            connection.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT);
        } else {
            connection.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT_CORRESPONDENCE);
        }
    }

//    public void creaWidgetIconForSelectionCondition() {
//      //  logger.warn("funzione ancora da implementare");
//        creaWidgetIconForSelectionCondition();
//    }
    private void extractPathExpressions(List<VariableSelectionCondition> variableSelectionConditions, List<PathExpression> pathExpressions) {
        for (VariableSelectionCondition variableSelectionCondition : variableSelectionConditions) {
            for (SetAlias variable : variableSelectionCondition.getSetVariables()) {
                pathExpressions.add(variable.getBindingPathExpression().getAbsolutePath());
            }
        }
    }

    private List<PathExpression> findAllSelectionConditionPath(ComplexQueryWithNegations complexQueryWithNegations) {
        ComplexConjunctiveQuery complexConjunctiveQuery = complexQueryWithNegations.getComplexQuery();
        List<PathExpression> pathExpressions = new ArrayList<PathExpression>();
        List<VariableSelectionCondition> variableSelectionConditions = complexConjunctiveQuery.getAllSelections();
        extractPathExpressions(variableSelectionConditions, pathExpressions);
        for (NegatedComplexQuery negatedComplexQuery : complexQueryWithNegations.getNegatedComplexQueries()) {
            pathExpressions.addAll(findAllSelectionConditionPath(negatedComplexQuery.getComplexQuery()));
        }
        return pathExpressions;
    }

    public void creaWidgetIconForSelectionCondition() {
        FORule tgd = jLayeredPane.getMappingTaskTopComponent().getScenario().getSelectedFORule();

//        FindNode finder = new FindNode();
        CreaWidgetEsisteSelectionCondition checker = new CreaWidgetEsisteSelectionCondition();
//        MappingTask mappingTask = scenario.getMappingTask();
        IDataSourceProxy source = mappingTask.getSourceProxy();
        List<PathExpression> pathExpressions = findAllSelectionConditionPath(tgd.getComplexSourceQuery());
//        for (SelectionCondition selectionCondition : source.getSelectionConditions()) {
        for (PathExpression pathExpression : pathExpressions) {
            INode iNode = finder.findNodeInSchema(pathExpression, source);
            VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);
            CaratteristicheWidgetTree caratteristicheWidgetTree = (CaratteristicheWidgetTree) glassPane.getMainLayer().getChildConstraint(vMDPinWidget);
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) caratteristicheWidgetTree.getTreePath().getLastPathComponent();
            SelectionConditionInfo selectionConditionInfo = creaSelectionConditionInfo(iNode);
            selectionConditionInfo.setExpressionString("false");
            selectionConditionInfo.setSelectionCondition(null);
            checker.creaWidgetEsisteSelectionCondition(treeNode, "false", null);
        }
//        }
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

    public void creaWidgetFunctionalDependencies(IDataSourceProxy dataSource, boolean isSource) {
//        logger.warn("funzione ancora da implementare");
    }
}
