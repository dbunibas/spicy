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
import it.unibas.spicygui.controllo.provider.MyPopupProviderConnectionInfo;
import it.unibas.spicygui.controllo.provider.MySelectActionProvider;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.operators.ConnectionInfoCreator;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jdesktop.beansbinding.BindingGroup;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

public class MyPopupProviderConnectionConst implements PopupMenuProvider, ActionListener {

    private ReviewCorrespondences review = new ReviewCorrespondences();
    private ConnectionInfoCreator infoCreator = new ConnectionInfoCreator();
    private CaratteristicheWidgetInterConst caratteristicheWidget;
    private JPopupMenu menu;
    private JMenuItem itemConfidence;
    private JCheckBoxMenuItem itemImplied;
    private BindingGroup bindingGroup;
    private boolean first = true;
    private ConnectionWidget connection;
    private Scene scene;
    private VMDNodeWidget vmdNodeWidget;
    private final String SHOW = "show";
    private final String DELETE = "delete";

    public MyPopupProviderConnectionConst(Scene scene, CaratteristicheWidgetInterConst caratteristicheWidget) {
        this.caratteristicheWidget = caratteristicheWidget;
        this.scene = scene;
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point arg1) {
        if (first) {
            connection = (ConnectionWidget) widget;
            setInfoLabel();
            first = false;
        }
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
            deleteConnection();
        } else if (e.getActionCommand().equals(SHOW)) {
//            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SHOW_HIDE_INFO_CONNECTION));
//            if (vmdNodeWidget != null) {
//                vmdNodeWidget.removeFromParent();
//                vmdNodeWidget = null;
//                scene.validate();
//            } else {
//                showInfo();
//            }
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenu() {
        menu = new JPopupMenu("Popup menu");
        JMenuItem item;

//        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.SHOW_HIDE_INFO_CONNECTION));
//        item.setActionCommand(SHOW);
//        item.addActionListener(this);
//        menu.add(item);


        itemConfidence = new JMenuItem();
        menu.add(itemConfidence);

        itemImplied = new JCheckBoxMenuItem(NbBundle.getMessage(Costanti.class, Costanti.IMPLIED));
        menu.add(itemImplied);

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
        item.setActionCommand(DELETE);
        item.addActionListener(this);
        menu.add(item);
    }

    private void setInfoLabel() {
        ConnectionInfo connectionInfo = (ConnectionInfo) connection.getParentWidget().getChildConstraint(connection);
        bindingGroup = new BindingGroup();
//        vmdNodeWidget = infoCreator.createPropertyWidget(scene, bindingGroup, connectionInfo, connection);
        infoCreator.createPropertyItem( itemConfidence, itemImplied, bindingGroup, connectionInfo, connection);
        bindingGroup.bind();
//        connection.setConstraint(vmdNodeWidget, LayoutFactory.ConnectionWidgetLayoutAlignment.TOP_CENTER, 0.5f);
        scene.validate();
//        vmdNodeWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderConnectionInfo(scene)));
//        vmdNodeWidget.getActions().addAction(ActionFactory.createSelectAction(new MySelectActionProvider()));
    }

    private void deleteConnection() {
        LayerWidget connectionLayer = (LayerWidget) connection.getParentWidget();
        ConnectionInfo connectionInfo = (ConnectionInfo) connectionLayer.getChildConstraint(connection);
        review.removeCorrespondence(connectionInfo.getValueCorrespondence());
        caratteristicheWidget.removeConnectionInfo(connectionInfo);
        connection.removeFromParent();
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
    }
}
