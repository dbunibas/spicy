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
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public class ActionViewSchema extends CallableSystemAction implements Observer {

    private static Log logger = LogFactory.getLog(ActionViewSchema.class);
    private Modello modello;
    private LastActionBean lastActionBean;

    public ActionViewSchema() {
        executeInjection();
        registraAzione();
        this.setEnabled(false);
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

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    @Override
    public void performAction() {
        Scenario scenario = (Scenario) this.modello.getBean(Costanti.CURRENT_SCENARIO);
        if (scenario != null) {
            final MappingTaskTopComponent win = scenario.getMappingTaskTopComponent();
//        win.openAtTabPosition(-1);
            win.open();
            win.requestActive();
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_SHOW_SCHEMA);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_SCHEMA_ALBERI;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
