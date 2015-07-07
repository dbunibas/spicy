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

import it.unibas.spicygui.commons.Modello;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class ActionExitApplication {

    private Modello modello;
    private ActionSaveMappingTask actionSaveMappingTask;

    public boolean canExit() {
//        executeInjection();
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
//        if (mappingTask == null) {
//            return true;
//        }
//        if (mappingTask.isToBeSaved) {
//            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.SAVE_ON_CLOSE), DialogDescriptor.YES_NO_CANCEL_OPTION);
//            DialogDisplayer.getDefault().notify(notifyDescriptor);
//            if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
//                actionSaveMappingTask.performAction();
//                if (!actionSaveMappingTask.isEsito()) {
//                    return false;
//                }
//            } else if (notifyDescriptor.getValue().equals(NotifyDescriptor.CANCEL_OPTION) ||
//                    notifyDescriptor.getValue().equals(NotifyDescriptor.CLOSED_OPTION)) {
//                return false;
//            }
//        }
        return true;
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionSaveMappingTask == null) {
            this.actionSaveMappingTask = Lookups.forPath("Azione").lookup(ActionSaveMappingTask.class);
        }
    }
}
