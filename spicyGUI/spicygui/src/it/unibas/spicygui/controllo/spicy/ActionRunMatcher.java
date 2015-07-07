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
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.SpicyTopComponent;
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

public class ActionRunMatcher extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionRunMatcher.class);
    private Modello modello;
    private LastActionBean lastActionBean;
    private ActionViewSpicy actionViewSpicy;
    private IMatchAttributes attributeMatcher;

    public ActionRunMatcher() {
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
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.WARNING_NOT_TARGET_INSTANCES), DialogDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        if (mappingTask.getCandidateCorrespondences().size() != 0 && mappingTask.isToBeSaved()) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.DISCARD_CANDIDATE_CORRESPONDENCES), DialogDescriptor.YES_NO_OPTION);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (notifyDescriptor.getValue().equals(NotifyDescriptor.NO_OPTION) || notifyDescriptor.getValue().equals(NotifyDescriptor.CLOSED_OPTION)) {
                return;
            }
        }
        mappingTask.clearCandidateCorrespondences();
        attributeMatcher.clearCache();
        attributeMatcher.findMatches(mappingTask);

        SpicyTopComponent spicyTopComponent = scenario.getSpicyTopComponent()/*SpicyTopComponent.findInstance()*/;
        if (spicyTopComponent.getJLayeredPane().isAnalizzato()) {
            spicyTopComponent.drawConnections();
        } else {
            spicyTopComponent.drawScene();
        }
        spicyTopComponent.resetSlider();
        actionViewSpicy.performAction();
        spicyTopComponent.creaSlider();
        enableActions();
        IOProvider.getDefault().getIO(Costanti.FLUSSO_SPICY, false).select();
        logger.info("--------------Candidate correspondences--------------- (" + mappingTask.getCandidateCorrespondences().size() + ")");
        for (ValueCorrespondence correspondence : mappingTask.getCandidateCorrespondences()) {
            logger.info(correspondence);
        }
        return;

    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionViewSpicy == null) {
            this.actionViewSpicy = Lookups.forPath("Azione").lookup(ActionViewSpicy.class);
        }
        this.attributeMatcher = (IMatchAttributes) Application.getInstance().getComponentInstance(IMatchAttributes.class);
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.CLOSE)) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }

    }

    private void enableActions() {
        lastActionBean.setLastAction(LastActionBean.CREATE_CANDIDATE);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_RUN_MATCHER);
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
