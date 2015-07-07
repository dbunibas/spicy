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
 
package it.unibas.spicygui.controllo.window;

import it.unibas.spicygui.controllo.file.*;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.ActionViewSchema;
import it.unibas.spicygui.vista.ProjectTreeTopComponent;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class ActionProjectTree extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionProjectTree.class);
    private ActionSaveMappingTask actionSaveMappingTask;
    private Modello modello;
    private ActionViewSchema actionViewSchema;
    private LastActionBean lastActionBean;

    public ActionProjectTree() {
        executeInjection();
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.ACTION_PROJECT_TOOLTIP));
        this.setEnabled(false);
        registraAzione();
    }

    private void enableActions() {
        lastActionBean.setLastAction(LastActionBean.CLOSE);
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

    public void update(Observable observable, Object stato) {
        if (observable instanceof Scenario) {
            if (stato instanceof TopComponent) {
                ProjectTreeTopComponent.findInstance().aggiornaAlbero((Scenario) observable, (TopComponent) stato);
                return;
            } else if (stato instanceof String) {
                ProjectTreeTopComponent.findInstance().aggiornaAlberoPerIstanze((Scenario) observable, (String) stato);
            }
        }
        if (observable instanceof Scenarios) {
            ProjectTreeTopComponent.findInstance().aggiornaAlbero((Scenario) stato, null);
        }

        if (stato.equals(LastActionBean.CLOSE)) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void performAction() {
        ProjectTreeTopComponent projectTreeTopComponent = ProjectTreeTopComponent.findInstance();
        if (!projectTreeTopComponent.isAggiornato()) {
            projectTreeTopComponent.creaAlbero();
        }
        projectTreeTopComponent.open();

    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_PROJECT);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_PROJECT;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
