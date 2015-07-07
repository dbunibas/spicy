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
import it.unibas.spicygui.commons.Modello;
import java.awt.Color;
import java.awt.Point;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;

public class MySelectActionProvider implements SelectProvider {

    private Modello modello;

    public MySelectActionProvider() {
        executeInjection();
    }

    public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
        return true;
    }

    public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
        return true;
    }

    public void select(Widget widget, Point arg1, boolean arg2) {
        widget.getParentWidget().bringToFront();
     //   selezionaConnessione((ConnectionWidget) widget.getParentWidget());
        widget.getScene().validate();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    //TODO vedere se eliminarlo oppure no
    private void selezionaConnessione(ConnectionWidget connectionWidget) {
        ConnectionWidget connectionWidgetOld = (ConnectionWidget) modello.getBean(Costanti.CONNECTION_SELECTED);
        connectionWidget.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_SELECTED);
        if (connectionWidgetOld == null) {
        } else {
            connectionWidgetOld.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT_CORRESPONDENCE);
        }
        modello.putBean(Costanti.CONNECTION_SELECTED, connectionWidget);
    }
}
