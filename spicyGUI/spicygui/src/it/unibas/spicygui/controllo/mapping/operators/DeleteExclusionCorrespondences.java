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


import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicygui.Costanti;
import org.netbeans.api.visual.widget.Widget;

public class DeleteExclusionCorrespondences {

    //TODO Vedere se questa classe deve essere eliminata

    public void eliminaConnessioni(INode node) {
        Widget widget = (Widget) node.getAnnotation(Costanti.PIN_WIDGET_TREE_SPICY);
//        MappingTaskTopComponent topComponent = MappingTaskTopComponent.findInstance();
//        SpicyTopComponent topComponent = SpicyTopComponent.findInstance();
//        JLayeredPaneCorrespondences jLayeredPane = topComponent.getJLayeredPane();
//        LayerWidget connectionLayer = jLayeredPane.getGlassPane().getConnectionLayer();
//        LayerWidget mainLayer = jLayeredPane.getGlassPane().getMainLayer();
//        List<ConnectionWidget> listaEliminazioni = new ArrayList<ConnectionWidget>();
//        for (Widget connectionWidget : connectionLayer.getChildren()) {
//            ConnectionWidget connection = (ConnectionWidget) connectionWidget;
//            if (connection.getSourceAnchor().getRelatedWidget().equals(widget) &&
//                    connection.getTargetAnchor().getRelatedWidget() instanceof FunctionWidget) {
//                deleteFunction(mainLayer, listaEliminazioni, connectionLayer, widget, connection);
//            } else if (connection.getSourceAnchor().getRelatedWidget().equals(widget) ||
//                    connection.getTargetAnchor().getRelatedWidget().equals(widget)) {
//                deleteCorrespondence(connection, listaEliminazioni, connectionLayer);
//            }
//        }
//        for (ConnectionWidget connectionWidget : listaEliminazioni) {
//            connectionWidget.removeFromParent();
//            connectionWidget.getScene().validate();
//        }
//
    }

//    private void deleteFunction(LayerWidget mainLayer, List<ConnectionWidget> listaEliminazioni, LayerWidget connectionLayer, Widget widget, ConnectionWidget connection) {
//        FunctionWidget functionWidget = (FunctionWidget) connection.getTargetAnchor().getRelatedWidget();
//        CaratteristicheWidgetInterFunction caratteristicheWidget = (CaratteristicheWidgetInterFunction) mainLayer.getChildConstraint(connection.getTargetAnchor().getRelatedWidget());
//        List<VMDPinWidgetSource> listaConnessioni = caratteristicheWidget.getSourceList();
//        listaConnessioni.remove(widget);
//        listaEliminazioni.add(connection);
//        if (listaConnessioni.size() <= 0 && caratteristicheWidget.getTargetWidget() != null) {
//            VMDPinWidgetTarget widgetTarget = caratteristicheWidget.getTargetWidget();
//            ConnectionWidget connectionWidgetTarget = null;
//            for (Widget connectionTarget : connectionLayer.getChildren()) {
//                ConnectionWidget connectionToTaget = (ConnectionWidget) connectionTarget;
//                if (connectionToTaget.getSourceAnchor().getRelatedWidget().equals(functionWidget) && connectionToTaget.getTargetAnchor().getRelatedWidget().equals(widgetTarget)) {
//                    connectionWidgetTarget = connectionToTaget;
//                }
//            }
//            deleteCorrespondence(connectionWidgetTarget, listaEliminazioni, connectionLayer);
//        }
//    }
//
//    private void deleteCorrespondence(ConnectionWidget connection, List<ConnectionWidget> listaEliminazioni, LayerWidget connectionLayer) {
//        ConnectionInfo connectionInfo = (ConnectionInfo) connectionLayer.getChildConstraint(connection);
//        Modello modello = Lookup.getDefault().lookup(Modello.class);
//        listaEliminazioni.add(connection);
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
////        MappingTaskInfo mappingTaskInfo = (MappingTaskInfo) modello.getBean(Costanti.MAPPINGTASK_INFO);
//        mappingTask.removeCandidateCorrespondence(connectionInfo.getValueCorrespondence());
//    //        mappingTask.removeCorrespondence(connectionInfo.getValueCorrespondence());
////        mappingTaskInfo.removeCorrespondence(connectionInfo.getVariableCorrespondence());
//    }
}
