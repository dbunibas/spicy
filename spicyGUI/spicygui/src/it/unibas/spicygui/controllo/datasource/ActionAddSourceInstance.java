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
 
package it.unibas.spicygui.controllo.datasource;


import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.DAOXsd;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.InstancesTopComponent;
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
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

public class ActionAddSourceInstance extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionAddSourceInstance.class);
    private Vista vista;
    private Modello modello;
    private LastActionBean lastActionBean;

    public ActionAddSourceInstance() {
        executeInjection();
        registraAzione();
        this.setEnabled(false);
    }

    @Override
    public void performAction() {
        JFileChooser chooser = vista.getFileChooserApriXML();
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        MappingTask mappingTask = scenario.getMappingTask();
        InstancesTopComponent viewInstancesTopComponent = scenario.getInstancesTopComponent();
        File file;
        int returnVal = chooser.showDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(Costanti.class, Costanti.LOAD));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                file = chooser.getSelectedFile();
                DAOXsd daoXsd = new DAOXsd();
                daoXsd.loadInstance(mappingTask.getSourceProxy(), file.getAbsolutePath());
                scenario.addSourceInstance(file.getAbsolutePath());
                if (!viewInstancesTopComponent.isRipulito()) {
                    viewInstancesTopComponent.clearSource();
                    viewInstancesTopComponent.createSourceInstanceTree();
                    viewInstancesTopComponent.requestActive();
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.ADD_INSTANCE_OK));
                }
            } catch (DAOException ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.OPEN_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                logger.error(ex);
            }

        }

    }

    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.CLOSE) || (stato.equals(LastActionBean.NO_SCENARIO_SELECTED))) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_ADD_SOURCE_INSTANCE);
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_ADD_TARGET_INSTANCE;
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
