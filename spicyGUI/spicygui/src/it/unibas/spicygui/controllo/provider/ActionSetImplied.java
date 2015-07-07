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
 
package it.unibas.spicygui.controllo.provider;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;

public class ActionSetImplied extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionSetImplied.class);
    private static ReviewCorrespondences review = new ReviewCorrespondences();
    private static CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private ConnectionInfo connectionInfo;

    public ActionSetImplied(ConnectionInfo connectionInfo) {
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.IMPLIED));
        this.connectionInfo = connectionInfo;
        this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
//        setImplied();
    }


//    private void setImplied() {
//        ValueCorrespondence oldValueCorrespondence = connectionInfo.getValueCorrespondence();
//        boolean implied = oldValueCorrespondence.isImplied();
//        review.removeCorrespondence(connectionInfo.getValueCorrespondence());
//        analizzaConnessione(implied);
//    }
//
//    private void analizzaConnessione(boolean implied) {
//        creator.createImplied(connectionInfo, !implied);
//    }
}
