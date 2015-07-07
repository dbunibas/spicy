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
import it.unibas.spicygui.vista.configuration.EngineConfigurationPM;
import it.unibas.spicygui.vista.configuration.SettingEngineConfigurationDialog;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ActionSettingEngineConfiguration extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionSettingEngineConfiguration.class);
    private SettingEngineConfigurationDialog dialog;
    private JTree albero;

    public ActionSettingEngineConfiguration(JTree albero) {
        this.albero = albero;
        this.dialog = new SettingEngineConfigurationDialog(WindowManager.getDefault().getMainWindow(), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_SETTING_ENGINE_CONFIGURATION));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) treeNode.getUserObject();
        Scenario scenario = adapter.getScenario();
        EngineConfigurationPM engineConfigurationOld =  scenario.getEngineConfigurationPM().clone();
        dialog.changeMappingTask(scenario);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == SettingEngineConfigurationDialog.RET_CANCEL) {
           ripristinaConfigurazione(scenario.getEngineConfigurationPM(), engineConfigurationOld);
        } else {
            scenario.getEngineConfigurationPM().setSortStrategy(dialog.getComboBoxUseSortStrategy().getSelectedIndex());
            scenario.getEngineConfigurationPM().setSkolemTableStrategy(dialog.getComboBoxUseSkolemTableStrategy().getSelectedIndex());
        }
    }

    private void ripristinaConfigurazione(EngineConfigurationPM engineConfigurationCanceled, EngineConfigurationPM engineConfiguration) {
        engineConfigurationCanceled.setDebugMode(engineConfiguration.isDebugMode());
        engineConfigurationCanceled.setRewriteCoverages(engineConfiguration.isRewriteCoverages());
        engineConfigurationCanceled.setRewriteEGDs(engineConfiguration.isRewriteEGDs());
        engineConfigurationCanceled.setRewriteSelfJoins(engineConfiguration.isRewriteSelfJoins());
        engineConfigurationCanceled.setRewriteSubsumptions(engineConfiguration.isRewriteSubsumptions());

        //TODO implementare un metodo in engine Configuration per farsi dare la sortStrategy
        engineConfigurationCanceled.setSortStrategy(engineConfiguration.getSortStrategy());
        engineConfigurationCanceled.setSkolemTableStrategy(engineConfiguration.getSkolemTableStrategy());

        engineConfigurationCanceled.setUseCreateTableInSTExchange(engineConfiguration.isUseCreateTableInSTExchange());
        engineConfigurationCanceled.setUseCreateTableInTargetExchange(engineConfiguration.isUseCreateTableInTargetExchange());
        engineConfigurationCanceled.setUseHashTextForSkolems(engineConfiguration.isUseHashTextForSkolems());
    }

}
