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


import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.controllo.provider.MyPopupProviderConnectionSpicy;
import it.unibas.spicygui.controllo.provider.MySelectConnectionActionProvider;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.Stroke;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.openide.util.Lookup;

public class CreaWidgetCorrespondencesSpicy implements ICreaWidgetCorrespondences {

    private static Log logger = LogFactory.getLog(CreaWidgetCorrespondencesSpicy.class);
    private Modello modello;
    private JLayeredPaneCorrespondences jLayeredPane;
    private GraphSceneGlassPane glassPane;
    private FindNode finder = new FindNode();

    public CreaWidgetCorrespondencesSpicy(JLayeredPaneCorrespondences jLayeredPane) {
        this.jLayeredPane = jLayeredPane;
        this.glassPane = jLayeredPane.getGlassPane();
        executeInjection();
    }

    public void creaWidgetCorrespondences() {
        MappingTask mappingTask = jLayeredPane.getScenario().getMappingTask();
        if (mappingTask.getCandidateCorrespondences().size() > 0) {
            for (int i = 0; i < mappingTask.getCandidateCorrespondences().size(); i++) {
                ValueCorrespondence valueCorrespondence = mappingTask.getCandidateCorrespondences().get(i);
                creaCandidateCorrespondence(valueCorrespondence, mappingTask);
            }
        }
    }

    private void creaCandidateCorrespondence(ValueCorrespondence valueCorrespondence, MappingTask mappingTask) {
        INode iNodeSource = finder.findNodeInSchema(valueCorrespondence.getSourcePaths().get(0), mappingTask.getSourceProxy());
        VMDPinWidgetSource sourceWidget = (VMDPinWidgetSource) iNodeSource.getAnnotation(Costanti.PIN_WIDGET_TREE_SPICY);
        INode iNodeTarget = finder.findNodeInSchema(valueCorrespondence.getTargetPath(), mappingTask.getTargetProxy());
        VMDPinWidgetTarget targetWidget = (VMDPinWidgetTarget) iNodeTarget.getAnnotation(Costanti.PIN_WIDGET_TREE_SPICY);

        ConnectionWidget connection = new ConnectionWidget(glassPane.getScene());
        connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        Stroke stroke = Costanti.DASHED_STROKE;
        connection.setStroke(stroke);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionWidget(connection);
        connectionInfo.setValueCorrespondence(valueCorrespondence);
        connection.setToolTipText(connectionInfo.getValueCorrespondence().toString());
        setColorOnConfidence(connectionInfo, connection);
        glassPane.getConnectionLayer().addChild(connection, connectionInfo);
        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionSpicy(glassPane.getScene())));
        connection.getActions().addAction(ActionFactory.createSelectAction(new MySelectConnectionActionProvider(glassPane.getConnectionLayer())));
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

    public void creaWidgetIconForSelectionCondition() {
        return;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void creaWidgetFunctionalDependencies(IDataSourceProxy dataSource, boolean isSource) {
        return;
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}

