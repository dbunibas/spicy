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
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.widget.FunctionalDependencyWidget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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

public class MyPopupProviderDeleteFunctionalDep implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupProviderDeleteFunctionalDep.class);
    private ReviewCorrespondences review = new ReviewCorrespondences();
    private JPopupMenu menu;
    private FunctionalDependencyWidget rootWidget;
    private Scene scene;
    private GraphSceneGlassPane graphScene;
    private final String DELETE = "delete";
    private LayerWidget connectionLayer;

    public MyPopupProviderDeleteFunctionalDep(GraphSceneGlassPane graphScene) {
        this.graphScene = graphScene;
        this.connectionLayer = graphScene.getConnectionLayer();
        this.scene = graphScene.getScene();
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point arg1) {
        rootWidget = (FunctionalDependencyWidget) widget;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_DELETE));
            deleteAll();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenu() {
        menu = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_DELETE));
        item.setActionCommand(DELETE);
        item.addActionListener(this);
        menu.add(item);
    }

    private void deleteAll() {
        LayerWidget mainLayer = (LayerWidget) rootWidget.getParentWidget();
        CaratteristicheWidgetInterFunctionalDep caratteristicheWidget = (CaratteristicheWidgetInterFunctionalDep) mainLayer.getChildConstraint(rootWidget);
        review.removeFunctionalDependency(caratteristicheWidget.getFunctionalDependency(), caratteristicheWidget.isSource());
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_FUNCTIONAL_DEPENDECY));

        List<ConnectionWidget> listaConnection = new ArrayList<ConnectionWidget>();
        for (Widget widget : connectionLayer.getChildren()) {
            ConnectionWidget connectionWidget = (ConnectionWidget) widget;
            if (connectionWidget.getSourceAnchor().getRelatedWidget().equals(rootWidget) || connectionWidget.getTargetAnchor().getRelatedWidget().equals(rootWidget)) {
                listaConnection.add(connectionWidget);
            }
        }
        for (ConnectionWidget widget : listaConnection) {
            widget.removeFromParent();
        }
        caratteristicheWidget.getWidgetBarra().removeFromParent();
        rootWidget.removeFromParent();
        scene.validate();

        graphScene.removeFunctionalDependencies(rootWidget);
        graphScene.removeFunctionalDependencies(caratteristicheWidget.getWidgetBarra());
    }
}
