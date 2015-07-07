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
 
package it.unibas.spicygui.controllo.file;


import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.parser.operators.ParseMappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.AbstractScenario;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.ActionViewSchema;
import it.unibas.spicygui.controllo.window.ActionProjectTree;
import it.unibas.spicygui.vista.Vista;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

public class ActionLoadTGDs extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionLoadTGDs.class);
    private Modello modello;
    private Vista vista;
    private ActionViewSchema actionViewSchema;
    private ActionProjectTree actionProjectTree;
    private LastActionBean lastActionBean;

    public ActionLoadTGDs() {
        executeInjection();
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_LOAD_TGDS_TOOLTIP));
        registraAzione();
    }

    private void enableActions() {
        lastActionBean.setLastAction(LastActionBean.OPEN);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
        if (this.actionViewSchema == null) {
            this.actionViewSchema = Lookups.forPath("Azione").lookup(ActionViewSchema.class);
        }
        if (this.actionProjectTree == null) {
            this.actionProjectTree = Lookups.forPath("Azione").lookup(ActionProjectTree.class);
        }
    }

    public void update(Observable o, Object stato) {
//        if (stato.equals(LastActionBean.CLOSE)) {
//            this.setEnabled(true);
//        } else {
//            this.setEnabled(false);
//        }
    }

    private void gestioneScenario(MappingTask mappingTask) {
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        if (scenarios == null) {
            scenarios = new Scenarios("");
            scenarios.addObserver(this.actionProjectTree);
            modello.putBean(Costanti.SCENARIOS, scenarios);
            actionProjectTree.performAction();
        }
        AbstractScenario scenario = new Scenario("", mappingTask, true);
        scenario.addObserver(this.actionProjectTree);
        scenarios.addScenario(scenario);
        Scenario scenarioOld = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        if (scenarioOld != null) {
            LastActionBean lab = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
            scenarioOld.setStato(lab.getLastAction());
        }
        modello.putBean(Costanti.CURRENT_SCENARIO, scenario);
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void performAction() {
        this.executeInjection();
        JFileChooser chooser = vista.getFileChooserApriTGD();
        File file;
        int returnVal = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = chooser.getSelectedFile();
                String fileAbsoluteFile = file.getPath();
                ParseMappingTask generator = new ParseMappingTask();
                MappingTask mappingTask = generator.generateMappingTask(fileAbsoluteFile);
                gestioneScenario(mappingTask);
                this.actionViewSchema.performAction();
                enableActions();
            } catch (Exception ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.OPEN_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                logger.error(ex);
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_LOAD_TGDS);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_OPEN;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    
}


