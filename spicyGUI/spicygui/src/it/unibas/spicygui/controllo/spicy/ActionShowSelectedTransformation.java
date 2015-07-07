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

import it.unibas.spicy.Transformation;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.RankedTransformationsTopComponent;
import it.unibas.spicygui.vista.SelectedTransformationTopComponent;
import java.util.List;
import javax.swing.JTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public class ActionShowSelectedTransformation {

    private static Log logger = LogFactory.getLog(ActionShowSelectedTransformation.class);
    private Modello modello;

    public ActionShowSelectedTransformation() {
        executeInjection();
    }

    private void execute(Scenario scenario) {
        RankedTransformationsTopComponent topComponent = scenario.getRankedTransformationsTopComponent();
        JTable tabellaRankedTransformations = topComponent.getTabellaRankedTransformations();
        int selectedRow = tabellaRankedTransformations.getSelectedRow();
        if (selectedRow != -1) {
            List<Transformation> rankedTransformations = (List<Transformation>) modello.getBean(Costanti.RANKED_TRANSFORMATIONS);
            Transformation selectedTransformation = rankedTransformations.get(selectedRow);
            modello.putBean(Costanti.SELECTED_TRANSFORMATION, selectedTransformation);
            SelectedTransformationTopComponent selectedTransformationTopComponent = scenario.getSelectedTransformationTopComponent();
            selectedTransformationTopComponent.createTransformationOutput();
            selectedTransformationTopComponent.openAtTabPosition(-1);
            selectedTransformationTopComponent.requestActive();
        }
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void actionPerformed() {
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        execute(scenario);
    }
    public void actionPerformedWithScenario(Scenario scenario) {
        execute(scenario);
    }
}
