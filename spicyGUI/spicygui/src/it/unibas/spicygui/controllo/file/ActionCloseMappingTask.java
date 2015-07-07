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
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.ActionViewSchema;
import it.unibas.spicygui.vista.CompositionTopComponent;
import it.unibas.spicygui.vista.XQueryTopComponent;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import it.unibas.spicygui.vista.InstancesTopComponent;
import it.unibas.spicygui.vista.SqlTopComponent;
import it.unibas.spicygui.vista.TGDCorrespondencesTopComponent;
import it.unibas.spicygui.vista.TGDListTopComponent;
import it.unibas.spicygui.vista.TransformationTopComponent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
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
import org.openide.windows.IOProvider;

public class ActionCloseMappingTask extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionCloseMappingTask.class);
    private ActionSaveMappingTask actionSaveMappingTask;
    private Modello modello;
    private ActionViewSchema actionViewSchema;
    private LastActionBean lastActionBean;
    private boolean esito;

    public ActionCloseMappingTask() {
        executeInjection();
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_CLOSE_TOOLTIP));
        this.setEnabled(false);
        registraAzione();
    }

    private void clearModelloLight() {
        //TODO Cancellare tutti questi componenti dal modello porta ad avere nelle altre sessioni errori vista la non presenza di questi bean

//        modello.removeBean(Costanti.MAPPINGTASK);
//        modello.removeBean(Costanti.ACTUAL_SAVE_FILE);
        modello.removeBean(Costanti.BEST_MAPPING_TASKS);
        modello.removeBean(Costanti.CONNECTION_SELECTED);
        modello.removeBean(Costanti.LINE_COORDINATES_COLLECTIONS);
        modello.removeBean(Costanti.CONSTRAINTS_WIDGET_COLLECTIONS);
        // MappingTaskTopComponent.findInstance().initNameTab();
    }

    private void clearModello() {
        clearModelloLight();
        modello.removeBean(Costanti.CURRENT_SCENARIO);
//        modello.removeBean(Costanti.PROJECT);
    }

    private void closeInstancesView(Scenario scenario) {
        InstancesTopComponent topComponentViewInstances = scenario.getInstancesTopComponent();
        topComponentViewInstances.clear();
        topComponentViewInstances.close();

        TransformationTopComponent trasformationTopComponent = scenario.getTransformationTopComponent();
        if (trasformationTopComponent != null) {
            trasformationTopComponent.clear();
            trasformationTopComponent.close();
        }
//        SpicyTopComponent spicyTopComponent = SpicyTopComponent.findInstance();
//        spicyTopComponent.clear();
//        spicyTopComponent.close();
        XQueryTopComponent topComponentXQuery = scenario.getXQueryTopComponent();
        if (topComponentXQuery != null) {
            topComponentXQuery.clear();
            topComponentXQuery.close();
        }
        SqlTopComponent topComponentSql = scenario.getSqlTopComponent();
        if (topComponentSql != null) {
            topComponentSql.clear();
            topComponentSql.close();
        }
        TGDListTopComponent tgdListTopComponent = scenario.getTGDListTopComponent();
        if (tgdListTopComponent != null) {
            tgdListTopComponent.clear();
            tgdListTopComponent.close();
        }
        TGDCorrespondencesTopComponent tgdCorrespondencesTopComponent = scenario.getTGDCorrespondencesTopComponent();
        if (tgdCorrespondencesTopComponent != null) {
            tgdCorrespondencesTopComponent.clear();
            tgdCorrespondencesTopComponent.close();
        }
        CompositionTopComponent compositionTopComponent = scenario.getCompositionTopComponent();
        if (compositionTopComponent != null) {
//            compositionTopComponent.clear();
            compositionTopComponent.close();
        }
//        BestMappingsTopComponent bestMappingsTopComponent = BestMappingsTopComponent.findInstance();
//        bestMappingsTopComponent.close();
//        RankedTransformationsTopComponent rankedTransformationsTopComponent = RankedTransformationsTopComponent.findInstance();
//        rankedTransformationsTopComponent.close();
//        SelectedTransformationTopComponent selectedTransformationTopComponent = SelectedTransformationTopComponent.findInstance();
//        selectedTransformationTopComponent.clear();
//        selectedTransformationTopComponent.close();
        Utility.closeOutputWindow();
//        bestMappingsTopComponent.clear();
        try {
            IOProvider.getDefault().getIO(Costanti.FLUSSO_SPICY, false).getOut().reset();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }


    }

    private void enableActionsForClose() {
        lastActionBean.setLastAction(LastActionBean.CLOSE);
    }

    private void enableActionsForNoScenario() {
        lastActionBean.setLastAction(LastActionBean.NO_SCENARIO_SELECTED);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionSaveMappingTask == null) {
            this.actionSaveMappingTask = Lookups.forPath("Azione").lookup(ActionSaveMappingTask.class);
        }
        if (this.actionViewSchema == null) {
            this.actionViewSchema = Lookups.forPath("Azione").lookup(ActionViewSchema.class);
        }
    }

    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.NO_SCENARIO_SELECTED)) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
    }

    private void myPerformAction(MappingTaskTopComponent mappingTaskTopComponent) {
        //MappingTaskTopComponent topComponent = MappingTaskTopComponent.findInstance();
        MappingTask mappingTask = mappingTaskTopComponent.getScenario().getMappingTask();
        if (mappingTask.isToBeSaved()) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.SAVE_ON_CLOSE), DialogDescriptor.YES_NO_CANCEL_OPTION);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
                actionSaveMappingTask.myPerformAction(mappingTaskTopComponent.getScenario());
                if (!actionSaveMappingTask.isEsito()) {
                    actionViewSchema.performAction();
                    esito = false;
                    return;
                }
            } else if (notifyDescriptor.getValue().equals(NotifyDescriptor.CANCEL_OPTION)
                    || notifyDescriptor.getValue().equals(NotifyDescriptor.CLOSED_OPTION)) {
                actionViewSchema.performAction();
                esito = false;
                return;
            }
        }
        //TODO vedere se deve essere ripulito o no, non credo visto che ne creo sempre uno nuovo
        controlloScenario(mappingTaskTopComponent);
        esito = true;
    }

    private void controlloScenario(MappingTaskTopComponent mappingTaskTopComponent) {
//        mappingTaskTopComponent.clear();
        Scenario scenario = mappingTaskTopComponent.getScenario();
//        Utility.closeInstancesView(scenario);
        closeInstancesView(scenario);
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        Scenario currentScenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        if (currentScenario == scenario) {
            clearModello();
            enableActionsForNoScenario();
        } else {
            clearModelloLight();
            //   enableActionsForClose();
        }
        scenarios.removeScenario(scenario);
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void performAction() {
        //TODO controllare l'opportunità che l'azione richiamata dalla toolbar, debba comportarsi modo diverso rispetto a questa;
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        MappingTaskTopComponent tc = scenario.getMappingTaskTopComponent();
        if (tc.isOpened()) {
            tc.close();
        } else {
            tc.canClose();
        }
//        myPerformAction(tc);
    }

    public boolean checkClosure(MappingTaskTopComponent mappingTaskTopComponent) {
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.REALLY_CLOSE), DialogDescriptor.YES_NO_OPTION);
        notifyDescriptor.setTitle(mappingTaskTopComponent.getName());
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
            myPerformAction(mappingTaskTopComponent);
            return esito;
        }
        return false;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_CLOSE);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_CLOSE;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
