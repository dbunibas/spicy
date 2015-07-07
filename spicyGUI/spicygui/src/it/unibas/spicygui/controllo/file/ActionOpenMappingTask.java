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
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.ActionViewSchema;
import it.unibas.spicygui.controllo.mapping.ActionViewTGD;
import it.unibas.spicygui.controllo.mapping.ActionViewTGDs;
import it.unibas.spicygui.controllo.window.ActionComposition;
import it.unibas.spicygui.controllo.window.ActionProjectTree;
import it.unibas.spicygui.file.TGDDataObject;
import it.unibas.spicygui.vista.CompositionTopComponent;
import it.unibas.spicygui.vista.Vista;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.EditorSupport.Editor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

public class ActionOpenMappingTask extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionOpenMappingTask.class);
    private Modello modello;
    private Vista vista;
    private ActionViewSchema actionViewSchema;
    private ActionComposition actionComposition;
    private ActionProjectTree actionProjectTree;
    private LastActionBean lastActionBean;
//    private DAOMappingTask daoMappingTask = new DAOMappingTask();

    public ActionOpenMappingTask() {
        executeInjection();
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_OPEN_TOOLTIP));
        registraAzione();
    }

    private void enableActions() {
        lastActionBean.setLastAction(LastActionBean.OPEN);
    }

    private void enableActionsTGDs() {
        lastActionBean.setLastAction(LastActionBean.TGD_SESSION);
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
        if (this.actionComposition == null) {
            this.actionComposition = Lookups.forPath("Azione").lookup(ActionComposition.class);
        }
    }

    public void update(Observable o, Object stato) {
//        if (stato.equals(LastActionBean.CLOSE)) {
//            this.setEnabled(true);
//        } else {
//            this.setEnabled(false);
//        }
    }

    private Scenario gestioneScenario(File file, MappingTask mappingTask, boolean TGDSession, boolean isSelected) {
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        if (scenarios == null) {
            scenarios = new Scenarios("PROGETTO DI PROVA");
            scenarios.addObserver(this.actionProjectTree);
            modello.putBean(Costanti.SCENARIOS, scenarios);
            actionProjectTree.performAction();
        }
//        modello.putBean(Costanti.ACTUAL_SAVE_FILE, file);
        Scenario scenario = new Scenario("SCENARIO DI PROVA", mappingTask, isSelected, file);
//        scenario.setSaveFile(file);
        scenario.addObserver(this.actionProjectTree);
        scenarios.addScenario(scenario, false);
        LastActionBean lab = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        scenario.setStato(lab.getLastAction());
        if (isSelected) {
            Scenario scenarioOld = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
            if (scenarioOld != null) {
                scenarioOld.setSelected(false);
//                LastActionBean lab = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
                scenarioOld.setStato(lab.getLastAction());
            }
            modello.putBean(Costanti.CURRENT_SCENARIO, scenario);
        }
        scenario.setTGDSession(TGDSession);
        return scenario;
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    public Scenario openCompositionFile(String fileAbsoluteFile, File file, boolean isSelected) {
        Scenario scenario = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Apro il file : " + fileAbsoluteFile);
            }
            FileNameExtensionFilter tgdFilter = new FileNameExtensionFilter("TGD", "tgd");
            FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML", "xml");
            MappingTask mappingTask = null;
            DAOMappingTask daoMappingTask = new DAOMappingTask();
            if (xmlFilter.accept(file)) {
                mappingTask = daoMappingTask.loadMappingTask(fileAbsoluteFile, SpicyEngineConstants.LINES_BASED_MAPPING_TASK);
                enableActions();
                scenario = gestioneScenario(file, mappingTask, false, isSelected);
                this.actionViewSchema.performAction();
            } else if (tgdFilter.accept(file)) {
                mappingTask = daoMappingTask.loadMappingTask(fileAbsoluteFile, SpicyEngineConstants.TGD_BASED_MAPPING_TASK);
                enableActionsTGDs();
                scenario = gestioneScenario(file, mappingTask, true, isSelected);
                this.actionViewSchema.setEnabled(false);
                Lookups.forPath("Azione").lookup(ActionViewTGDs.class).myPerformAction(scenario);
                Lookups.forPath("Azione").lookup(ActionViewTGD.class).myPerformAction(scenario);
            }
            Lookups.forPath("Azione").lookup(ActionProjectTree.class).performAction();
        } catch (DAOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.OPEN_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
            logger.error(ex);
        }
        return scenario;

    }

    @Override
    public void performAction() {
        this.executeInjection();
        JFileChooser chooser = vista.getFileChooserApriXMLAndTGD();
        File file;
        int returnVal = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        Scenario scenario = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            file = chooser.getSelectedFile();
            String fileAbsoluteFile = file.getPath();
            scenario = openCompositionFile(fileAbsoluteFile, file, true);
            MappingTask mappingTask = scenario.getMappingTask();
            File f = chooser.getSelectedFile();
            FileObject fo = FileUtil.toFileObject(f);

            TGDDataObject dobj = null;
            try {
                TGDEditorSupport tGDEditorSupport = new TGDEditorSupport(fo);
//                dobj = tGDEditorSupport.getDataObject();
                tGDEditorSupport.open();
//    dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException ex) {
                ex.printStackTrace();
            }
//if (dobj != null){
//    OpenCookie lc = (OpenCookie)dobj.getCookie(OpenCookie.class);
//    if (lc == null) {/* cannot do it */ return;}
//    lc.open();
////    Line l = lc.getLineSet().getOriginal(lineNumber);
////    l.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS);
//}

//try {
//    Editor.getDefaultLocale().
////    new ROEditor(fo).open();
//    
//    new TGDEditorSupport(fo).edit();
//    
////    DataObject dataObject = new ROEditor(fo).getDataObject();
////
////    EditorCookie cookie = (EditorCookie)dataObject.getCookie(EditorCookie.class);
////    cookie.open();
//    
//    System.out.println("eseguito");
//} catch (DataObjectNotFoundException e) {
//    e.printStackTrace();
//}
// 
            if (!(mappingTask.getSourceProxy() instanceof ConstantDataSourceProxy)) {
//                Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
                scenario.setCompositionTopComponent(new CompositionTopComponent(scenario));
                actionComposition.performAction();
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_OPEN);
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
