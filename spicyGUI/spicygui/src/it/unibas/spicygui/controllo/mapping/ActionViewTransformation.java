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
 
package it.unibas.spicygui.controllo.mapping;


import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.vista.TransformationTopComponent;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public class ActionViewTransformation extends CallableSystemAction implements Observer {

    private Modello modello;
    private LastActionBean lastActionBean;

    public ActionViewTransformation() {
        this.setEnabled(false);
        executeInjection();
        registraAzione();
    }

    private List<Integer> analizzaStringa(TransformationTopComponent component) {
        MappingTask mappingTask = component.getScenario().getMappingTask();

        String match = "*************************** TGD *******************************";
        boolean flag = true;
        List<Integer> lista = new ArrayList<Integer>();
        int firstIndex = mappingTask.getMappingData().toLongString().indexOf(match);
        lista.add(firstIndex);
        while (flag) {
            firstIndex++;
            int secondIndex = mappingTask.getMappingData().toLongString().indexOf(match, firstIndex);
            if (secondIndex == -1) {
                flag = false;
            } else {
                lista.add(secondIndex);
                firstIndex = secondIndex;
            }
        }
        return lista;
    }

    private void execute(Scenario scenario) {
        TransformationTopComponent viewTransformation = scenario.getTransformationTopComponent();
        if (viewTransformation == null) {
            viewTransformation = new TransformationTopComponent(scenario);
            scenario.setTransformationTopComponent(viewTransformation);
        }
        //TODO controllare se la finestra necessita di essere ripulita
        if (viewTransformation.isRipulito()) {
            viewTransformation.createTransformationOutput();
        }
//        viewTraformation.clear();
        viewTransformation.openAtTabPosition(2);
        viewTransformation.requestActive();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void update(Observable o, Object stato) {
        if (stato.equals(LastActionBean.TRANSLATE) || stato.equals(LastActionBean.SOLVE)) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }

    private void registraAzione() {
        lastActionBean = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
        lastActionBean.addObserver(this);
    }

    public void myPerformAction(Scenario scenario) {
        execute(scenario);
    }

    @Override
    public void performAction() {
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        execute(scenario);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Costanti.class, Costanti.ACTION_VIEW_TRANSFORMATIONS);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return Costanti.ICONA_VIEW_TRASFORMATION;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
