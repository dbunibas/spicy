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
 
package it.unibas.spicygui.controllo.provider;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.provider.intermediatezone.MyPopupProviderConnectionFunc;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.FunctionWidget;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import java.awt.BasicStroke;
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

public class ActionFunctionConnection implements ConnectProvider {

    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private CaratteristicheWidgetInterFunction caratteristiche;
    private LayerWidget connectionLayer;
    private LayerWidget mainLayer;

    public ActionFunctionConnection(LayerWidget connectionLayer, LayerWidget mainLayer, CaratteristicheWidgetInterFunction caratteristiche) {
        this.connectionLayer = connectionLayer;
        this.mainLayer = mainLayer;
        this.caratteristiche = caratteristiche;

    }

    public boolean isSourceWidget(Widget arg0) {
        if (caratteristiche.getExpressionFunction() == null || caratteristiche.getExpressionFunction().equals("")) {
            return false;
        }
        return true;
    }

    public ConnectorState isTargetWidget(Widget arg0, Widget arg1) {
        if ((arg0 instanceof FunctionWidget && arg1 instanceof VMDPinWidgetTarget) && (arg0 != arg1) && arg1.isEnabled()) {
            return ConnectorState.ACCEPT;
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
            connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunc(sourceWidget.getScene(), mainLayer, caratteristiche)));
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setTargetWidget(targetWidget);
            connectionInfo.setConnectionWidget(connection);
            caratteristiche.setTargetWidget((VMDPinWidgetTarget) targetWidget);
            creator.createCorrespondenceWithFunction(mainLayer, targetWidget, caratteristiche, connectionInfo);
            caratteristiche.setConnectionInfo(connectionInfo);
            connectionLayer.addChild(connection, connectionInfo);
        } catch (ExpressionSyntaxException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
        }

    }
}
