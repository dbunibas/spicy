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
import it.unibas.spicygui.widget.operators.ConnectionInfoCreator;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
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

public class MyPopupProviderConnectionFunctionalDep implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupProviderConnectionFunctionalDep.class);
    private ReviewCorrespondences review = new ReviewCorrespondences();
    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private ConnectionInfoCreator infoCreator = new ConnectionInfoCreator();
    private CaratteristicheWidgetInterFunctionalDep caratteristicheWidget;
    private JPopupMenu menu;
    private ConnectionWidget connection;
    private Scene scene;
    private LayerWidget mainLayer;
    private final String DELETE = "delete";

    public MyPopupProviderConnectionFunctionalDep(Scene scene, LayerWidget mainLayer, CaratteristicheWidgetInterFunctionalDep caratteristicheWidget) {
        this.caratteristicheWidget = caratteristicheWidget;
        this.scene = scene;
        this.mainLayer = mainLayer;
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point arg1) {
        connection = (ConnectionWidget) widget;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
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
        if (caratteristicheWidget.getSourceList().remove(connection.getSourceAnchor().getRelatedWidget())) {
            review.removeFunctionalDependency(caratteristicheWidget.getFunctionalDependency(), caratteristicheWidget.isSource());
            if (!caratteristicheWidget.getTargetList().isEmpty() && !caratteristicheWidget.getSourceList().isEmpty()) {
                creator.createCorrespondenceWithFunctionalDep(mainLayer, caratteristicheWidget, caratteristicheWidget.getConnectionInfo());
            } else if (caratteristicheWidget.getSourceList().isEmpty()){
                caratteristicheWidget.setSource(null);
            }
            connection.removeFromParent();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("rimuovo target");
            }
            review.removeFunctionalDependency(caratteristicheWidget.getFunctionalDependency(), caratteristicheWidget.isSource());
            caratteristicheWidget.getTargetList().clear();
            connection.removeFromParent();
        }
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
    }
}
