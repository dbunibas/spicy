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
package it.unibas.spicygui.controllo.tree;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.CompositionTopComponent;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class ActionCreateCompositionScene extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionCreateCompositionScene.class);
    private Modello modello;
    private LastActionBean lastActionBean;
    private JTree albero;

    public ActionCreateCompositionScene(JTree albero) {
        executeInjection();
        this.albero = albero;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_CREATE_COMPOSITION_SCENE));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) treeNode.getUserObject();
        Scenario scenarioSelected = adapter.getScenario();
        if (scenarioSelected.getCompositionTopComponent() == null) {
            CompositionTopComponent compositionTopComponent = new CompositionTopComponent(scenarioSelected);
//            new TreeDropTarget(compositionTopComponent.getGlassPane());
            scenarioSelected.setCompositionTopComponent(compositionTopComponent);
//            this.modello.putBean(Costanti.SELECTED_COMPOSITION, compositionTopComponent);
            
//            CompositionTopComponent compositionTopComponent = scenarioSelected.getCompositionTopComponent();
//            WidgetCreator widgetCreator = new WidgetCreator();
//
//            widgetCreator.createCompositionWidget(compositionTopComponent.getGlassPane().getScene(), compositionTopComponent.getGlassPane().getMainLayer(), compositionTopComponent.getGlassPane().getConnectionLayer(), new Point(10, 10), compositionTopComponent.getGlassPane(), (Scenario) scenarioSelected);
//            compositionTopComponent.getGlassPane().getScene().validate();
//            scenarioSelected.setInComposition(true);

        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.ALREADY_CREATED_COMPOSITION));
        }
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.lastActionBean == null) {
            this.lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        }
    }

    private void enableActions(Object stato) {
        lastActionBean.setLastAction(stato);
    }
}
