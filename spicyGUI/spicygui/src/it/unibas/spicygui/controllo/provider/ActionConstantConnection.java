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
import it.unibas.spicygui.controllo.provider.intermediatezone.MyPopupProviderConnectionConst;
import it.unibas.spicygui.controllo.FormValidation;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.ConstantWidget;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
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
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

public class ActionConstantConnection implements ConnectProvider {

    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private CaratteristicheWidgetInterConst caratteristicheWidget;

    public ActionConstantConnection(LayerWidget mainLayer, LayerWidget connectionLayer, CaratteristicheWidgetInterConst caratteristicheWidget) {
        this.mainLayer = mainLayer;
        this.connectionLayer = connectionLayer;
        this.caratteristicheWidget = caratteristicheWidget;
    }

    public boolean isSourceWidget(Widget widget) {
        if (caratteristicheWidget.getCostante() == null || caratteristicheWidget.getCostante().equals("")) {
            return false;
        }
        return true;
    }

    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        if ((sourceWidget instanceof ConstantWidget && targetWidget instanceof VMDPinWidgetTarget) && (sourceWidget != targetWidget) && targetWidget.isEnabled()) {
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

            connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionConst(sourceWidget.getScene(), caratteristicheWidget)));
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setTargetWidget(targetWidget);
            connectionInfo.setConnectionWidget(connection);
            caratteristicheWidget.setFormValidation(new FormValidation(false));
            creator.createCorrespondenceWithSourceValue(mainLayer, sourceWidget, targetWidget, connectionInfo);
            caratteristicheWidget.addConnectionInfo(connectionInfo);
            connectionLayer.addChild(connection, connectionInfo);

        } catch (ExpressionSyntaxException ese) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + ese, DialogDescriptor.WARNING_MESSAGE));
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING));
        }

    }
}
