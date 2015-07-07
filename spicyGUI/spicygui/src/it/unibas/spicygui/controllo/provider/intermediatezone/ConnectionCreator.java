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
 
package it.unibas.spicygui.controllo.provider.intermediatezone;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import java.awt.Color;
import java.awt.Stroke;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

public class ConnectionCreator {

    private Log logger = LogFactory.getLog(ConnectionCreator.class);

    public ConnectionInfo createConnectionToTarget(Widget sourceWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {
        return createConnectionToTargetImpl(sourceWidget, targetWidget, mainLayer, connectionLayer, stroke);
    }

    public ConnectionInfo createConnectionToTarget(Widget sourceWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer) {
        Stroke stroke = Costanti.BASIC_STROKE;
        return createConnectionToTargetImpl(sourceWidget, targetWidget, mainLayer, connectionLayer, stroke);
    }

    private ConnectionInfo createConnectionToTargetImpl(Widget sourceWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {

        ConnectionWidget connection = new ConnectionWidget(sourceWidget.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        connection.setStroke(stroke);

        CaratteristicheWidgetInterConst caratteristicheWidget = (CaratteristicheWidgetInterConst) mainLayer.getChildConstraint(sourceWidget);

        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionConst(sourceWidget.getScene(), caratteristicheWidget)));
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setTargetWidget(targetWidget);
        connectionInfo.setConnectionWidget(connection);
        caratteristicheWidget.addConnectionInfo(connectionInfo);

        connectionLayer.addChild(connection, connectionInfo);
        return connectionInfo;
    }

    public void createConnectionToFunction(Widget sourceWidget, Widget functionWidget, LayerWidget mainLayer, LayerWidget connectionLayer) {
        Stroke stroke = Costanti.BASIC_STROKE;
        createConnectionToFunctionImpl(sourceWidget, functionWidget, mainLayer, connectionLayer, stroke);
    }

    public void createConnectionToFunction(Widget sourceWidget, Widget functionWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {
        createConnectionToFunctionImpl(sourceWidget, functionWidget, mainLayer, connectionLayer, stroke);
    }

    private void createConnectionToFunctionImpl(Widget sourceWidget, Widget functionWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {

        ConnectionWidget connection = new ConnectionWidget(sourceWidget.getScene());

        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);

        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(functionWidget));

        connection.setStroke(stroke);
        CaratteristicheWidgetInterFunction caratteristiche = (CaratteristicheWidgetInterFunction) mainLayer.getChildConstraint(functionWidget);

        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunc(sourceWidget.getScene(), mainLayer, caratteristiche)));
        caratteristiche.addSourceWidget((VMDPinWidgetSource) sourceWidget);

        connectionLayer.addChild(connection);
    }

    public void createConnectionToFunctionalDependecy(Widget sourceWidget, Widget functionalDepWidget, LayerWidget mainLayer, LayerWidget connectionLayer) {
        Stroke stroke = Costanti.BASIC_STROKE;
        createConnectionToFunctionalDependecyImpl(sourceWidget, functionalDepWidget, mainLayer, connectionLayer, stroke);
    }

    private void createConnectionToFunctionalDependecyImpl(Widget sourceWidget, Widget functionalDepWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {

        ConnectionWidget connection = new ConnectionWidget(sourceWidget.getScene());

        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);

        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(functionalDepWidget));

        connection.setStroke(stroke);
        CaratteristicheWidgetInterFunctionalDep caratteristiche = (CaratteristicheWidgetInterFunctionalDep) mainLayer.getChildConstraint(functionalDepWidget);

        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunctionalDep(sourceWidget.getScene(), mainLayer, caratteristiche)));
        caratteristiche.addSourceWidget((VMDPinWidget) sourceWidget);

        connectionLayer.addChild(connection);
    }

    public ConnectionInfo createConnectionFromFunctionalDependecy(Widget functionalDepWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer) {
        Stroke stroke = Costanti.BASIC_STROKE;
        return createConnectionFromFunctionalDependecyImpl(functionalDepWidget, targetWidget, mainLayer, connectionLayer, stroke);
    }

    private ConnectionInfo createConnectionFromFunctionalDependecyImpl(Widget functionalDepWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {
        ConnectionWidget connection = new ConnectionWidget(functionalDepWidget.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(functionalDepWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        connection.setStroke(stroke);
        connection.setVisible(true);


        CaratteristicheWidgetInterFunctionalDep caratteristicheWidget = (CaratteristicheWidgetInterFunctionalDep) mainLayer.getChildConstraint(functionalDepWidget);

        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunctionalDep(functionalDepWidget.getScene(), mainLayer, caratteristicheWidget)));
        caratteristicheWidget.addTargetWidget((VMDPinWidget) targetWidget);

        ConnectionInfo connectionInfo = new ConnectionInfo();
//        connectionInfo.setTargetWidget(targetWidget);
//        connectionInfo.setConnectionWidget(connection);
        connectionLayer.addChild(connection, connectionInfo);
        return connectionInfo;
    }

    public ConnectionInfo createConnectionFromFunction(Widget functionWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer) {
        Stroke stroke = Costanti.BASIC_STROKE;
        return createConnectionFromFunctionImpl(functionWidget, targetWidget, mainLayer, connectionLayer, stroke);
    }

    public ConnectionInfo createConnectionFromFunction(Widget functionWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {
        return createConnectionFromFunctionImpl(functionWidget, targetWidget, mainLayer, connectionLayer, stroke);
    }

    private ConnectionInfo createConnectionFromFunctionImpl(Widget functionWidget, Widget targetWidget, LayerWidget mainLayer, LayerWidget connectionLayer, Stroke stroke) {
        ConnectionWidget connection = new ConnectionWidget(functionWidget.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(functionWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        connection.setStroke(stroke);

        CaratteristicheWidgetInterFunction caratteristicheWidget = (CaratteristicheWidgetInterFunction) mainLayer.getChildConstraint(functionWidget);

        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunc(functionWidget.getScene(), mainLayer, caratteristicheWidget)));
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setTargetWidget(targetWidget);
        connectionInfo.setConnectionWidget(connection);
        connectionLayer.addChild(connection, connectionInfo);
        return connectionInfo;
    }
}
