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
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.widget.ConnectionConstraint;
import it.unibas.spicygui.widget.JoinConstraint;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.openide.util.Lookup;

public class ActionCancelMultipleJoin extends AbstractAction {

    private Modello modello;
    private JDialog jDialog;

    public ActionCancelMultipleJoin(JDialog jDialog) {
        this.jDialog = jDialog;
    }

    public void actionPerformed(ActionEvent e) {
        executeInjection();
        cleanMultipleJoin();
        removeJoinBean();
        this.jDialog.setVisible(false);
    }

    private void cleanMultipleJoin() {
        JoinConstraint joinConstraint = (JoinConstraint) this.modello.getBean(Costanti.JOIN_CONSTRIANTS);
        for (ConnectionConstraint connectionConstraint : joinConstraint.getConnections()) {
            connectionConstraint.deleteConnectionAndWidget();            
        }
    }

    private void removeJoinBean() {
        this.modello.removeBean(Costanti.CREATING_JOIN_SESSION);
        this.modello.removeBean(Costanti.JOIN_CONSTRIANTS);
        this.modello.removeBean(Costanti.JOIN_CONDITION);
        this.modello.removeBean(Costanti.FROM_PATH_NODES);
        this.modello.removeBean(Costanti.JOIN_SESSION_SOURCE);
        this.modello.removeBean(Costanti.JOIN_SESSION_TARGET);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
