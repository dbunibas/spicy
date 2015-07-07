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

import it.unibas.spicy.Application;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.findmappings.operators.RankTransformations;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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

public class ActionRankTransformations extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionRankTransformations.class);
    private Modello modello;
    private LastActionBean lastActionBean;
    private ActionViewRankedTransformations actionViewRankedTranformations;
    private RankTransformations tranformationRanker;

    public ActionRankTransformations() {
        executeInjection();
        this.setEnabled(false);
        registraAzione();
    }

    @Override
    public void performAction() {
        executeInjection();
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask.getTargetProxy().getInstances().isEmpty()) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.WARNING_NOT_TARGET_INSTANCES), DialogDescriptor.INFORMATION_MESSAGE));
            return;
        }
//        MappingTaskInfo mappingTaskInfo = (MappingTaskInfo) modello.getBean(Costanti.MAPPINGTASK_INFO);
        if (mappingTask.getMappingData() == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.NOT_MAPPED), DialogDescriptor.INFORMATION_MESSAGE));
            return;
        }
        // TODO: check
//        if (mappingTaskInfo.getTransformations().size() == 1) {
//            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SINGLE_TRANSFORMATION), DialogDescriptor.INFORMATION_MESSAGE));
//            return;
//        }
        SwingWorker swingWorker = new SwingWorker() {

            private Scenario scenario;

            @Override
            protected Object doInBackground() throws Exception {
                scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
                MappingTask mappingTask = scenario.getMappingTask();
                List<Transformation> rankedTransformations = tranformationRanker.rankTransformations(mappingTask);
                modello.putBean(Costanti.RANKED_TRANSFORMATIONS, rankedTransformations);
                return true;
            }

            @Override
            protected void done() {
                try {
                    if ((Boolean) get() && !isCancelled()) {
                        actionViewRankedTranformations.performActionWithScenario(scenario);
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

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionViewRankedTranformations == null) {
            this.actionViewRankedTranformations = Lookups.forPath("Azione").lookup(ActionViewRankedTransformations.class);
        }
        this.tranformationRanker = (RankTransformations) Application.getInstance().getComponentInstance(RankTransformations.class);
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.SOLVE) || stato.equals(LastActionBean.SOLVE_AND_TRANSLATE)) {
            this.setEnabled(true);
        } else if (stato.equals(LastActionBean.CLOSE)) {
            this.setEnabled(false);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_RANK_TRANSFORMATIONS);
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
    }
}
