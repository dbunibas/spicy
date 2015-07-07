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
 
package it.unibas.spicygui.controllo.spicy;

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
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

public class ActionFindMappings extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionFindMappings.class);
    private Modello modello;
    private Boolean firstCheck = null;
    private LastActionBean lastActionBean;
    private ActionViewSpicy actionViewSpicy;
    private ActionViewBestMappings actionViewBestMappings;
    private IFindBestMappingsStrategy mappingFinder;

    public ActionFindMappings() {
        executeInjection();
        this.setEnabled(false);
        registraAzione();
    }

    @Override
    public void performAction() {
        Properties config = new Properties();
        config.setProperty(IComputeQuality.class.getSimpleName(), "it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityStructuralAnalysis");
        Application.reset(config);
        executeInjection();
        mappingFinder.cleanUp();
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask.getTargetProxy().getInstances().isEmpty()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.WARNING_NOT_TARGET_INSTANCES), DialogDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        checkValueCorrespondenceInMappingTask(mappingTask);
        SwingWorker swingWorker = new SwingWorker() {

            private Scenario scenario;

            @Override
            protected Object doInBackground() throws Exception {
                scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
                MappingTask mappingTask = scenario.getMappingTask();

                List<AnnotatedMappingTask> bestMappingTasks = mappingFinder.findBestMappings(mappingTask);
                modello.putBean(Costanti.BEST_MAPPING_TASKS, bestMappingTasks);
                IOProvider.getDefault().getIO(Costanti.FLUSSO_SPICY, false).select();
                return true;
            }

            @Override
            protected void done() {
                try {
                    if ((Boolean) get() && !isCancelled()) {
                        actionViewSpicy.performActionWithScenario(this.scenario);
                        actionViewBestMappings.performActionWithScenario(this.scenario);
                    }
                } catch (InterruptedException ex) {
                    logger.error(ex);
                } catch (ExecutionException ex) {
                    logger.error(ex);
                }
            }
        };
        swingWorker.execute();

    }

    private void addCorrespondencesToCandidate(MappingTask mappingTask) {
        List<ValueCorrespondence> candidateCorrespondences = new ArrayList<ValueCorrespondence>();
        for (ValueCorrespondence valueCorrespondence : mappingTask.getValueCorrespondences()) {
            if (checkValueCorrespondences(valueCorrespondence)) {
                for (ValueCorrespondence candidateCorrespondence : mappingTask.getCandidateCorrespondences()) {
                    if (valueCorrespondence.hasEqualPaths(candidateCorrespondence)) {
                        candidateCorrespondences.add(candidateCorrespondence);
                        break;
                    }
                }
                mappingTask.addCandidateCorrespondence(valueCorrespondence);
            }
        }
        mappingTask.getValueCorrespondences().clear();
        mappingTask.getCandidateCorrespondences().removeAll(candidateCorrespondences);
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        MappingTaskTopComponent mappingTaskTopComponent = scenario.getMappingTaskTopComponent();
        mappingTaskTopComponent.getJLayeredPane().getGlassPane().clearConnectionLayer();
    }

    private void checkValueCorrespondenceInMappingTask(MappingTask mappingTask) {

        if (!mappingTask.getValueCorrespondences().isEmpty()) {
            if (firstCheck == null) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.FIND_VALUE_CORRESPONDENCES), DialogDescriptor.YES_NO_OPTION);
                DialogDisplayer.getDefault().notify(nd);
                if (nd.getValue().equals(NotifyDescriptor.YES_OPTION)) {
                    this.firstCheck = true;
                    addCorrespondencesToCandidate(mappingTask);
                } else {
                    this.firstCheck = false;
                }
            }
        }
    }

    private boolean checkValueCorrespondences(ValueCorrespondence valueCorrespondence) {
        if (valueCorrespondence.getSourceValue() == null) {
            if (valueCorrespondence.getSourcePaths().size() == 1) {
                return valueCorrespondence.getTransformationFunction().toString().equals(valueCorrespondence.getSourcePaths().get(0).toString());
            }
        }
        return false;
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionViewSpicy == null) {
            this.actionViewSpicy = Lookups.forPath("Azione").lookup(ActionViewSpicy.class);
        }
        if (this.actionViewBestMappings == null) {
            this.actionViewBestMappings = Lookups.forPath("Azione").lookup(ActionViewBestMappings.class);
        }
        this.mappingFinder = (IFindBestMappingsStrategy) Application.getInstance().getComponentInstance(IFindBestMappingsStrategy.class);
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.CREATE_CANDIDATE)) {
            this.setEnabled(true);
        } else if (stato.equals(LastActionBean.CLOSE)) {
            this.setEnabled(false);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_FIND_MAPPINGS);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.firstCheck = null;
    }
}
