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
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.file.ActionCloseMappingTask;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

public class ActionRemoveScenario extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionRemoveScenario.class);
    private JTree albero;

    public ActionRemoveScenario(JTree albero) {

        this.albero = albero;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_REMOVE_SCENARIO));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) treeNode.getUserObject();
        Scenario scenario = adapter.getScenario();
        boolean isClose = false;
        if (scenario.getMappingTaskTopComponent().isOpened()) {
            isClose = scenario.getMappingTaskTopComponent().close();
        } else {
            isClose = scenario.getMappingTaskTopComponent().canClose();
        }
        if (isClose) {
            for (Scenario scenarioRelated : scenario.getRelatedScenarios()) {
                if (scenarioRelated.getMappingTaskTopComponent().isOpened()) {
                    scenarioRelated.getMappingTaskTopComponent().close();
                } else {
                    scenarioRelated.getMappingTaskTopComponent().canClose();
                }
            }
        }
//        boolean esito = scenario.getMappingTaskTopComponent().canClose();
//        if (esito) {
//            scenario.getMappingTaskTopComponent().close();
//        }

//        Lookups.forPath("Azione").lookup(ActionCloseMappingTask.class).checkClosure(scenario.getMappingTaskTopComponent());
        albero.updateUI();
    }
}
