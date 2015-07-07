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
package it.unibas.spicygui.controllo.provider.composition;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.controllo.composition.operators.CreateCorrespondencesComposition;
import it.unibas.spicygui.widget.MergeWidget;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.ChainWidget;
import it.unibas.spicygui.widget.ConstantCompositionWidget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetChainComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstantComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetMergeComposition;
import java.awt.Point;
import java.awt.Stroke;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class ActionConstantCompositionConnection implements ConnectProvider {

    private CreateCorrespondencesComposition creator = new CreateCorrespondencesComposition();
    private CaratteristicheWidgetConstantComposition caratteristiche;
    private LayerWidget connectionLayer;
    private LayerWidget mainLayer;

    public ActionConstantCompositionConnection(LayerWidget connectionLayer, LayerWidget mainLayer, CaratteristicheWidgetConstantComposition caratteristiche) {
        this.connectionLayer = connectionLayer;
        this.mainLayer = mainLayer;
        this.caratteristiche = caratteristiche;

    }

    public boolean isSourceWidget(Widget arg0) {
        return true;
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        if ((sourceWidget instanceof ConstantCompositionWidget && targetWidget instanceof ChainWidget) && (sourceWidget != targetWidget)) {
            CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(sourceWidget);
            CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);
            MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
            MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();
            if ((caratteristicheWidgetChainCompositionTarget.getSourceScenario() == null) && !mutableMappingTaskTarget.mutableMappingTasksContains(mutableMappingTaskSource) && !mutableMappingTaskSource.mutableMappingTasksContains(mutableMappingTaskTarget)) {
                return ConnectorState.ACCEPT;
            }
        } else if ((sourceWidget instanceof ConstantCompositionWidget && targetWidget instanceof MergeWidget) && (sourceWidget != targetWidget)) {
            CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(sourceWidget);
            CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionTarget = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(targetWidget);
            MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
            MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetMergeCompositionTarget.getMutableMappingTask();
            if (/*(caratteristicheWidgetMergeCompositionTarget.getSourceScenario() == null) &&*/ !mutableMappingTaskTarget.mutableMappingTasksContains(mutableMappingTaskSource) && !mutableMappingTaskSource.mutableMappingTasksContains(mutableMappingTaskTarget)) {
                return ConnectorState.ACCEPT;
            }
//            CaratteristicheWidgetComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetComposition) mainLayer.getChildConstraint(sourceWidget);
//            CaratteristicheWidgetMergeComposition CaratteristicheWidgetMergeCompositionTarget = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(targetWidget);
//            MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
//            MutableMappingTask mutableMappingTaskTarget = CaratteristicheWidgetMergeCompositionTarget.getMutableMappingTask();
//            if (||!mutableMappingTaskTarget.mutableMappingTasksContains(mutableMappingTaskSource) && !mutableMappingTaskSource.mutableMappingTasksContains(mutableMappingTaskTarget)) {
//            return ConnectorState.ACCEPT;
//            }
        }
        return ConnectorState.REJECT_AND_STOP;
    }

    public boolean hasCustomTargetWidgetResolver(Scene arg0) {
        return false;
    }

    public Widget resolveTargetWidget(Scene arg0, Point arg1) {
        return null;
    }

    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        try {
            ConnectionWidget connection = new ConnectionWidget(sourceWidget.getScene());
            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
            connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(sourceWidget));
            connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
            Stroke stroke = Costanti.BASIC_STROKE;
            connection.setStroke(stroke);
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setSourceWidget(sourceWidget);
            connectionInfo.setTargetWidget(targetWidget);
            connectionInfo.setConnectionWidget(connection);
            if (targetWidget instanceof ChainWidget) {
                setCompositionWidget(sourceWidget, targetWidget, connection, connectionInfo);
            } else if (targetWidget instanceof MergeWidget) {
                setMergeWidget(sourceWidget, targetWidget, connection, connectionInfo);
            }
            caratteristiche.setConnectionInfo(connectionInfo);
            connectionLayer.addChild(connection, connectionInfo);
        } catch (ExpressionSyntaxException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
        }

    }

    private void setCompositionWidget(Widget sourceWidget, Widget targetWidget, ConnectionWidget connection, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionChainComposition(sourceWidget.getScene())));
        caratteristiche.addTargetWidget((ChainWidget) targetWidget);
        caratteristicheWidgetChainTarget.setSourceScenario((ConstantCompositionWidget) sourceWidget);
        //TODO Cambiare in FromConstantToChain
        creator.createFromConstantToChainCorrespondence(mainLayer, (ConstantCompositionWidget) sourceWidget, (ChainWidget) targetWidget, connectionInfo);
    }

    private void setMergeWidget(Widget sourceWidget, Widget targetWidget, ConnectionWidget connection, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionTarget = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(targetWidget);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionMergeComposition(sourceWidget.getScene())));
//        caratteristiche.addTargetWidget((CompositionWidget) targetWidget);
        caratteristicheWidgetMergeCompositionTarget.addSourceWidget((ConstantCompositionWidget) sourceWidget);
        creator.createCorrespondenceForMerge(mainLayer, sourceWidget, targetWidget, connectionInfo);
    }
}
