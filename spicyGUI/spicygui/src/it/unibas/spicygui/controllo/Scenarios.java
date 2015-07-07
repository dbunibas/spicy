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
package it.unibas.spicygui.controllo;

import it.unibas.spicygui.commons.IScenario;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Scenarios extends Observable {

    private String projectName = "";
    private int maxScenarioNumber = 100000;
    private List<IScenario> listaSceneri = new ArrayList<IScenario>();
    private List<Integer> listaNumeri = new ArrayList<Integer>();
    private static Log logger = LogFactory.getLog(Scenarios.class);

    public Scenarios(String projectName) {
        this.projectName = projectName;
    }

    public List<IScenario> getListaSceneri() {
        return listaSceneri;
    }

    public void setListaSceneri(List<IScenario> listaSceneri) {
        this.listaSceneri = listaSceneri;
    }

    public void addScenario(IScenario scenario) {
        addScenario(scenario, true);
    }

    public void addScenario(IScenario scenario, boolean setSelected) {
        int numero = getNextFreeNumber();
        IScenario scenarioSelected = getScenarioSelected();
        if (scenarioSelected != null && setSelected) {
            scenarioSelected.setSelected(false);
        }
        scenario.initializeScenario(numero);
        this.listaSceneri.add(scenario);
        setChanged();
        notifyObservers(scenario);
    }

    public IScenario getScenario(int index) {
        return this.listaSceneri.get(index);
    }

    public IScenario getScenarioSelected() {
        for (IScenario scenario : listaSceneri) {
            if (scenario.isSelected()) {
                return scenario;
            }
        }
        return null;
    }

    public boolean removeScenario(IScenario scenario) {
        boolean esito = this.listaSceneri.remove(scenario);
        if (esito) {
            this.listaNumeri.remove((Integer) scenario.getNumber());
        }
        setChanged();
        notifyObservers(scenario);
        return esito;

    }

    public boolean containsScenario(IScenario scenario) {
        return this.listaSceneri.contains(scenario);
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private int getNextFreeNumber() {
        boolean flag = true;
        int contatore = 1;
        while (flag && contatore < maxScenarioNumber) {
            if (!listaNumeri.contains(contatore)) {
                listaNumeri.add(contatore);
                flag = false;
            } else {
                contatore++;
            }
        }
        return contatore;
    }
}
