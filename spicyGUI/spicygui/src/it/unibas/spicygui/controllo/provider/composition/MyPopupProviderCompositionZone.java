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

import it.unibas.spicygui.controllo.provider.intermediatezone.*;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class MyPopupProviderCompositionZone implements PopupMenuProvider, ActionListener {

    private JPopupMenu menuComposition;
    private JPanel pannelloPrincipale;
    private GraphSceneGlassPane myGraphScene;
    private Modello modello;
    private Scene scene;
    private Point point;
    private WidgetCreator widgetCreator = new WidgetCreator();

    public MyPopupProviderCompositionZone(GraphSceneGlassPane graphSceneGlassPaneg) {
        this.myGraphScene = graphSceneGlassPaneg;
        this.scene = myGraphScene.getScene();
        createPopupMenuIntermediateZone();
        executeInjection();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        //qui il widget passato e proprio la scena

        this.point = point;

        return menuComposition;

    }

    public void actionPerformed(ActionEvent e) {
        LayerWidget mainLayer = myGraphScene.getMainLayer();
        LayerWidget connectionLayer = myGraphScene.getConnectionLayer();
        if (e.getActionCommand().equals(Costanti.CREATE_MERGE_WIDGET)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_MERGE_WIDGET));
            widgetCreator.createCompositionMergeWidget(scene, mainLayer, connectionLayer, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else if (e.getActionCommand().equals(Costanti.CREATE_UNDEFINED_CHAIN_WIDGET)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_UNDEFINED_CHAIN_WIDGET));
            widgetCreator.createUndefinedChainWidget(scene, mainLayer, connectionLayer, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenuIntermediateZone() {
        menuComposition = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_MERGE_WIDGET));
        item.setActionCommand(Costanti.CREATE_MERGE_WIDGET);
        item.addActionListener(this);
        menuComposition.add(item);

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_UNDEFINED_CHAIN_WIDGET));
        item.setActionCommand(Costanti.CREATE_UNDEFINED_CHAIN_WIDGET);
        item.addActionListener(this);
        menuComposition.add(item);

    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Modello getModello() {
        return this.modello;
    }
}
