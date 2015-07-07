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
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.Vista;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.awt.HeadlessException;
import java.io.File;
import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

public class ActionSaveMappingTask extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionSaveMappingTask.class);
    private Modello modello;
    private Vista vista;
    private LastActionBean lastActionBean;
    private boolean continua;
    private boolean esito;
    private DAOMappingTask daoMappingTask = new DAOMappingTask();

    public ActionSaveMappingTask() {
        executeInjection();
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_SAVE_TOOLTIP));
        this.setEnabled(false);
        registraAzione();
    }

    private void chiediConferma(File file, MappingTask mappingTask) throws DAOException {
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.FILE_EXISTS), DialogDescriptor.YES_NO_OPTION);
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
            daoMappingTask.saveMappingTask(mappingTask, file.getAbsolutePath());
            mappingTask.setModified(false);
            mappingTask.setToBeSaved(false);
            continua = false;
            esito = true;
            Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
            scenario.setSaveFile(file);
//            modello.putBean(Costanti.ACTUAL_SAVE_FILE, file);
            enableActions();
        }
    }

    private void enableActions() {
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
    }

    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.CLOSE) || (stato.equals(LastActionBean.NO_SCENARIO_SELECTED)) || (stato.equals(LastActionBean.TGD_SESSION))) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    private void richiediUtilizzoFileChooser(MappingTask mappingTask, File file) throws HeadlessException, MissingResourceException {
        JFileChooser chooser = vista.getFileChooserSalvaXmlXsd();
        continua = true;
        while (continua) {
            int returnVal = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    file = chooser.getSelectedFile();
                    if (!file.exists()) {
                        daoMappingTask.saveMappingTask(mappingTask, file.getAbsolutePath());
                        mappingTask.setModified(false);
                        mappingTask.setToBeSaved(false);
                        continua = false;
                        esito = true;
                        // modello.putBean(Costanti.ACTUAL_SAVE_FILE, file);
                        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
                        scenario.setSaveFile(file);
                        MappingTaskTopComponent mappingTaskTopComponent = scenario.getMappingTaskTopComponent();
                        mappingTaskTopComponent.initNameTab();
                        enableActions();
                    } else {
                        chiediConferma(file, mappingTask);
                    }
                } catch (DAOException ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SAVE_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                    logger.error(ex);
                    continua = false;
                    esito = true;
                }
            } else {
                continua = false;
                esito = false;
            }
        }
    }

    public boolean isEsito() {
        return esito;
    }

    public void myPerformAction(Scenario scenario) {
        MappingTask  mappingTask = scenario.getMappingTask();
        if (!mappingTask.isToBeSaved()) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.NOT_MODIFIED));
            mappingTask = null;
            return;
        }
        File file = null;

//        if (modello.getBean(Costanti.ACTUAL_SAVE_FILE) != null) {
        if (scenario.getSaveFile() != null) {
            try {
//                file = (File) modello.getBean(Costanti.ACTUAL_SAVE_FILE);
                file = scenario.getSaveFile();
                daoMappingTask.saveMappingTask(mappingTask, file.getAbsolutePath());
                mappingTask.setModified(false);
                mappingTask.setToBeSaved(false);
                esito = true;
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SAVE_OK));
            } catch (DAOException ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SAVE_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                logger.error(ex);
                esito = false;
            }
        } else {
            richiediUtilizzoFileChooser(mappingTask, file);
        }
    }

    @Override
    public void performAction() {
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        myPerformAction(scenario);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_SAVE);
    }

    @Override
    public String iconResource() {
        return Costanti.ICONA_SAVE;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
