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
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.controllo.provider.intermediatezone.MyPopupProviderConnectionFunctionalDep;
import it.unibas.spicygui.widget.FunctionalDependencyWidget;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import java.awt.Point;
import java.awt.Stroke;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class ActionFunctionalDepConnection implements ConnectProvider {

    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private CaratteristicheWidgetInterFunctionalDep caratteristiche;
    private LayerWidget connectionLayer;
    private LayerWidget mainLayer;
    private ReviewCorrespondences review = new ReviewCorrespondences();
    private Modello modello;

    public ActionFunctionalDepConnection(LayerWidget connectionLayer, LayerWidget mainLayer, CaratteristicheWidgetInterFunctionalDep caratteristiche) {
        this.connectionLayer = connectionLayer;
        this.mainLayer = mainLayer;
        this.caratteristiche = caratteristiche;

    }

    public boolean isSourceWidget(Widget arg0) {
        if (caratteristiche.isSource() == null) {
            return false;
        }
        return true;
    }

    public ConnectorState isTargetWidget(Widget source, Widget target) {
        if (caratteristiche.isSource()) {
            if ((source instanceof FunctionalDependencyWidget && target instanceof VMDPinWidgetSource) && (source != target) && target.isEnabled()) {
                return ConnectorState.ACCEPT;
            }
        } else {
            if ((source instanceof FunctionalDependencyWidget && target instanceof VMDPinWidgetTarget) && (source != target) && target.isEnabled()) {
                return ConnectorState.ACCEPT;
            }
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
            connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionFunctionalDep(sourceWidget.getScene(), mainLayer, caratteristiche)));
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setTargetWidget(targetWidget);
            connectionInfo.setConnectionWidget(connection);
            if (!caratteristiche.getTargetList().isEmpty()) {
                review.removeFunctionalDependency(caratteristiche.getFunctionalDependency(), caratteristiche.isSource());
            }
            caratteristiche.addTargetWidget((VMDPinWidget) targetWidget);
            creator.createCorrespondenceWithFunctionalDep(mainLayer, caratteristiche, connectionInfo);
            caratteristiche.setConnectionInfo(connectionInfo);
            connectionLayer.addChild(connection, connectionInfo);

        } catch (ExpressionSyntaxException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
        }

    }
}
