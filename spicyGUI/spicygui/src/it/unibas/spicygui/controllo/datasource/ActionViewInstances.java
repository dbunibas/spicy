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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.InstancesTopComponent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public class ActionViewInstances extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionViewInstances.class);
    private Modello modello;
    private ComponentAdapter componentAdapter;
    private LastActionBean lastActionBean;

    public ActionViewInstances() {
        executeInjection();

        componentAdapter = new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                InstancesTopComponent instancesTopComponent = (InstancesTopComponent) e.getComponent();
                instancesTopComponent.setSplitPaneDivider();
            }
        };

        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_VIEW_INSTANCES_TOOLTIP));
        this.setEnabled(false);
        registraAzione();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.CLOSE) || (stato.equals(LastActionBean.NO_SCENARIO_SELECTED))) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
    }

    private void openComponent(final InstancesTopComponent instancesTopComponent) {
        instancesTopComponent.removeComponentListener(componentAdapter);
        instancesTopComponent.addComponentListener(componentAdapter);

        instancesTopComponent.openAtTabPosition(3);
        instancesTopComponent.close();
        instancesTopComponent.openAtTabPosition(3);
        instancesTopComponent.requestActive();
        if (instancesTopComponent.isRipulito()) {
            instancesTopComponent.createSourceInstanceTree();
            instancesTopComponent.createTargetInstanceTree();
        }
        if (instancesTopComponent.isRipulitoTranslated()) {
            instancesTopComponent.createTranslatedInstanceTree();
            instancesTopComponent.createCanonicalInstanceTree();
// deprecato dopo introduzione del singolo exchange
//            instancesTopComponent.createIntermediateInstanceTree();
//            instancesTopComponent.createTargetInstanceTree();
        }
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    public void myPerformAction(InstancesTopComponent instancesTopComponent) {
        openComponent(instancesTopComponent);
    }

    @Override
    public void performAction() {

        //TODO controllare bene questo metodo perchè mette un nuovo listner ad ogni innesco dell'azione
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        final InstancesTopComponent instancesTopComponent = scenario.getInstancesTopComponent();


        openComponent(instancesTopComponent);

    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_VIEW_INSTANCES);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_VIEW;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
