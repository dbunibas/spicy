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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

public class MyPopupProviderConnectionInfo implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupProviderConnectionInfo.class);
    private static ReviewCorrespondences review = new ReviewCorrespondences();
    private static CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private final String HIDE_CONNECTION_INFO = "hide_connection_info";
    private final String SET_IMPLIED = "set_implied";
    private JPopupMenu menu;
    private Scene scene;
    private ConnectionInfo connectionInfo;
    private VMDNodeWidget rootWidget;

    public MyPopupProviderConnectionInfo(Scene scene) {
        this.scene = scene;
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point arg1) {
        rootWidget = (VMDNodeWidget) widget;
        ConnectionWidget connectionWidget = ((ConnectionWidget) rootWidget.getParentWidget());
        connectionInfo = (ConnectionInfo) connectionWidget.getParentWidget().getChildConstraint(connectionWidget);
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(HIDE_CONNECTION_INFO)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.HIDE_INFO_CONNECTION));
            hideInfo();
//        } else if (e.getActionCommand().equals(SET_IMPLIED)) {
//            setImplied();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenu() {
        menu = new JPopupMenu("Popup menu");
        JMenuItem item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.HIDE_INFO_CONNECTION));
        item.setActionCommand(HIDE_CONNECTION_INFO);
        item.addActionListener(this);
        menu.add(item);

//        menu.addSeparator();
//
//        JMenuItem item2 = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.IMPLIED));
//        item2.setActionCommand(SET_IMPLIED);
//        item2.addActionListener(this);
//        menu.add(item2);

    }

    private void hideInfo() {
        ((ConnectionWidget) rootWidget.getParentWidget()).setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT_CORRESPONDENCE);
        rootWidget.removeFromParent();
    }

//    private void setImplied() {
//        ValueCorrespondence oldValueCorrespondence = connectionInfo.getValueCorrespondence();
//        boolean implied = oldValueCorrespondence.isImplied();
//        review.removeCorrespondence(connectionInfo.getValueCorrespondence());
//        analizzaConnessione(implied);
//    }
//
//    private void analizzaConnessione(boolean implied) {
//        creator.createImplied(connectionInfo, !implied);
//        LabelWidget etichettaImplied = (LabelWidget) rootWidget.getParentWidget().getChildConstraint(rootWidget);
//        etichettaImplied.setVisible(!implied);
//
//    }
}
