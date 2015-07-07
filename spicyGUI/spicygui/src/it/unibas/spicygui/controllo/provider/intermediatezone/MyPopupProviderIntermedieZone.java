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


import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class MyPopupProviderIntermedieZone implements PopupMenuProvider, ActionListener {

    private JPopupMenu menuIntermediate;
    private JPopupMenu menuGlass;
    private JLayeredPaneCorrespondences jLayeredPaneCorrespondences;
    private JPanel pannelloPrincipale;
    private GraphSceneGlassPane myGraphScene;
    private Modello modello;
    private Scene scene;
    private Point point;
    private JCheckBoxMenuItem item2;
    private WidgetCreator widgetCreator = new WidgetCreator();

    public MyPopupProviderIntermedieZone(JLayeredPaneCorrespondences jLayeredPaneCorrespondences) {
        this.jLayeredPaneCorrespondences = jLayeredPaneCorrespondences;
        this.myGraphScene = jLayeredPaneCorrespondences.getGlassPane();
        this.scene = myGraphScene.getScene();
        this.pannelloPrincipale = jLayeredPaneCorrespondences.getPannelloPrincipale();
        createPopupMenuIntermediateZone();
        createPopupMenuGlassPane();
        executeInjection();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        //qui il widget passato e proprio la scena
        Component comS = SwingUtilities.getDeepestComponentAt(pannelloPrincipale, point.x, point.y);
        this.point = point;
        if (Costanti.INTERMEDIE.equals(comS.getName())) {
            return menuIntermediate;
        } else {
            Boolean flag = (Boolean) this.modello.getBean(Costanti.CREATING_JOIN_SESSION);
            if (flag == null || !flag.booleanValue()) {
                item2.setSelected(false);
            } else {
                item2.setSelected(true);
            }
            return menuGlass;
        }
    }

    public void actionPerformed(ActionEvent e) {
        LayerWidget mainLayer = myGraphScene.getMainLayer();
        LayerWidget connectionLayer = myGraphScene.getConnectionLayer();
        if (e.getActionCommand().equals(Costanti.CREATE_CONSTANT)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_CONSTANT));
            widgetCreator.createConstantInterWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else if (e.getActionCommand().equals(Costanti.CREATE_FUNCTION)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_FUNCTION));
            widgetCreator.createFunctionWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else if (e.getActionCommand().equals(Costanti.CREATE_ATTRIBUTE_GROUP)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_ATTRIBUTE_GROUP));
            widgetCreator.createAttributeGroupWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else if (e.getActionCommand().equals(Costanti.CREATE_FUNCTIONAL_DEPENDECY)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.CREATE_FUNCTIONAL_DEPENDECY));
            widgetCreator.createFunctionalDependencyWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, myGraphScene);
            myGraphScene.getScene().validate();
        } else if (e.getActionCommand().equals(Costanti.DELETE_ALL_CONNECTIONS)) {
            deleteAll();
        } else if (e.getActionCommand().equals(Costanti.SET_MULTIPLE_JOIN_SESSION)) {
            activeMultipleJoinSession();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenuIntermediateZone() {
        menuIntermediate = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_CONSTANT));
        item.setActionCommand(Costanti.CREATE_CONSTANT);
        item.addActionListener(this);
        menuIntermediate.add(item);

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_FUNCTION));
        item.setActionCommand(Costanti.CREATE_FUNCTION);
        item.addActionListener(this);
        menuIntermediate.add(item);

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_ATTRIBUTE_GROUP));
        item.setActionCommand(Costanti.CREATE_ATTRIBUTE_GROUP);
        item.addActionListener(this);
        item.setEnabled(false);
        menuIntermediate.add(item);

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.CREATE_FUNCTIONAL_DEPENDECY));
        item.setActionCommand(Costanti.CREATE_FUNCTIONAL_DEPENDECY);
        item.addActionListener(this);
        menuIntermediate.add(item);

    }

    private void createPopupMenuGlassPane() {
        menuGlass = new JPopupMenu("Popup menu");
        JMenuItem item;
//        JCheckBoxMenuItem item2;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.DELETE_ALL_CONNECTIONS));
        item.setActionCommand(Costanti.DELETE_ALL_CONNECTIONS);
        item.addActionListener(this);

        item2 = new JCheckBoxMenuItem(NbBundle.getMessage(Costanti.class, Costanti.SET_MULTIPLE_JOIN_SESSION));
        item2.setActionCommand(Costanti.SET_MULTIPLE_JOIN_SESSION);
        item2.addActionListener(this);

        menuGlass.add(item);
        menuGlass.add(item2);
    }

    private void deleteAll() {
        MappingTask mappingTask = jLayeredPaneCorrespondences.getMappingTaskTopComponent().getScenario().getMappingTask();
        mappingTask.clearCorrespondences();
        //TODO da cambiare nel momento in cui verra aggiunto il metodo per la rimozione delle functionalDependencies
        mappingTask.getSourceProxy().getFunctionalDependencies().clear();
        mappingTask.getTargetProxy().getFunctionalDependencies().clear();
        
        myGraphScene.clearConnectionLayer();
    }

    private void activeMultipleJoinSession() {
        Boolean flag = (Boolean) this.modello.getBean(Costanti.CREATING_JOIN_SESSION);
        if (flag == null || !flag.booleanValue()) {
            this.modello.putBean(Costanti.CREATING_JOIN_SESSION, new Boolean(true));
        } else {
            this.modello.removeBean(Costanti.CREATING_JOIN_SESSION);
        }
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


