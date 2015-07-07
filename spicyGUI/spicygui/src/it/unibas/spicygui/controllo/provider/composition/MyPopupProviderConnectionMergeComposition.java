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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.widget.ChainWidget;
import it.unibas.spicygui.widget.ICompositionWidget;
import it.unibas.spicygui.widget.MergeWidget;
import it.unibas.spicygui.widget.caratteristiche.AbstractCaratteristicheWidgetComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstantComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetMergeComposition;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

public class MyPopupProviderConnectionMergeComposition implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupProviderConnectionMergeComposition.class);
    private JPopupMenu menu;
    private boolean first = true;
    private ConnectionWidget connection;
    private Scene scene;
    private final String DELETE = "delete";

    public MyPopupProviderConnectionMergeComposition(Scene scene) {
        this.scene = scene;
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point arg1) {
        if (first) {
            connection = (ConnectionWidget) widget;
            first = false;
        }
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE)) {
            deleteConnection();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenu() {
        menu = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
        item.setActionCommand(DELETE);
        item.addActionListener(this);

        menu.add(item);
    }

    private void deleteConnection() {
        LayerWidget connectionLayer = (LayerWidget) connection.getParentWidget();
        ConnectionInfo connectionInfo = (ConnectionInfo) connectionLayer.getChildConstraint(connection);
        LayerWidget mainLayer = (LayerWidget) connectionInfo.getSourceWidget().getParentWidget();
        AbstractCaratteristicheWidgetComposition caratteristicheWidgetCompositionSource = (AbstractCaratteristicheWidgetComposition) mainLayer.getChildConstraint(connectionInfo.getSourceWidget());
        AbstractCaratteristicheWidgetComposition caratteristicheWidgetCompositionTarget = (AbstractCaratteristicheWidgetComposition) mainLayer.getChildConstraint(connectionInfo.getTargetWidget());
        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetCompositionTarget.getMutableMappingTask();
        mutableMappingTaskSource.getMutableMappingTasks().remove(mutableMappingTaskTarget);
        mutableMappingTaskTarget.getMutableMappingTasks().remove(mutableMappingTaskSource);

//        mutableMappingTaskSource.getTargetProxies().remove();
//        mutableMappingTaskTarget.getSourceProxies().remove();
//        if (connectionInfo.getTargetWidget() instanceof ChainWidget) {
        caratteristicheWidgetCompositionTarget.removeSourceScenario((ICompositionWidget) connectionInfo.getSourceWidget());
        caratteristicheWidgetCompositionSource.getTargetList().remove((ICompositionWidget)connectionInfo.getTargetWidget());
//        } else if (connectionInfo.getTargetWidget() instanceof MergeWidget) {
//        }

//        if (connectionInfo.getSourceWidget() instanceof ChainWidget) {
//            deleteToMergeWidget(mainLayer, connectionInfo);
//        } else {
//            deleteFromMergeWidget(mainLayer, connectionInfo);
//        }

        connection.removeFromParent();
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
    }
//
//    private void deleteFromMergeWidget(LayerWidget mainLayer, ConnectionInfo connectionInfo) {
//        CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionSource = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(connectionInfo.getSourceWidget());
//        CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionTarget = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(connectionInfo.getTargetWidget());
//        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetMergeCompositionSource.getMutableMappingTask();
//        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetCompositionTarget.getMutableMappingTask();
////        mutableMappingTaskSource.getMutableMappingTasks().remove(mutableMappingTaskTarget);
////        mutableMappingTaskTarget.getMutableMappingTasks().remove(mutableMappingTaskSource);
//        caratteristicheWidgetCompositionTarget.setSourceScenario(null);
//    }
//
//    private void deleteToMergeWidget(LayerWidget mainLayer, ConnectionInfo connectionInfo) {
//        CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(connectionInfo.getSourceWidget());
//        CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionTarget = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(connectionInfo.getTargetWidget());
//        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
//        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetMergeCompositionTarget.getMutableMappingTask();
////        mutableMappingTaskSource.getMutableMappingTasks().remove(mutableMappingTaskTarget);
////        mutableMappingTaskTarget.getMutableMappingTasks().remove(mutableMappingTaskSource);
//        caratteristicheWidgetMergeCompositionTarget.removeSourceScenario((ChainWidget) connectionInfo.getSourceWidget());
//    }
}
