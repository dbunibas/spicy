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
package it.unibas.spicygui.widget.caratteristiche;

import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.widget.ICompositionWidget;
import org.netbeans.api.visual.widget.Widget;

public class CaratteristicheWidgetConstantComposition extends AbstractCaratteristicheWidgetComposition {

//    private String treeType;
    private Widget widgetBarra;
    private ICompositionWidget sourceScenario;
//    private List<ChainWidget> targetList = new ArrayList<ChainWidget>();
    private ConnectionInfo connectionInfo;
    private Scenario scenarioRelated;
//    private MutableMappingTask mutableMappingTask;
//    private ValueCorrespondence valueCorrespondence;

//    public CaratteristicheWidgetComposition(MutableMappingTask mutableMappingTask) {
//        this.mutableMappingTask = mutableMappingTask;
//    }

    public CaratteristicheWidgetConstantComposition(MutableMappingTask mutableMappingTask, Scenario scenario) {
        super(mutableMappingTask);
        this.scenarioRelated = scenario;
    }

//    public String getTreeType() {
//        return this.treeType;
//    }
//
//    public void setTreeType(String treeType) {
//        this.treeType = treeType;
//    }

    public ICompositionWidget getSourceScenario() {
        return sourceScenario;
    }

    public void setSourceScenario(ICompositionWidget sourceScenario) {
        this.sourceScenario = sourceScenario;
    }

    public Scenario getScenarioRelated() {
        return scenarioRelated;
    }

    public void setScenarioRelated(Scenario scenarioRelated) {
        this.scenarioRelated = scenarioRelated;
    }

//    public void addSourceWidget(CompositionWidget source) {
//        this.sourceList.add(source);
//    }
//
//    public CompositionWidget getSourceWidget(int i) {
//        return this.sourceList.get(i);
//    }
//
//    public List<CompositionWidget> getSourceList() {
//        return sourceList;
//    }
//    public void addTargetWidget(ChainWidget target) {
//        this.targetList.add(target);
//    }
//
//    public ChainWidget getTargetWidget(int i) {
//        return this.targetList.get(i);
//    }
//
//    public List<ChainWidget> getTargetList() {
//        return targetList;
//    }


//    public ValueCorrespondence getValueCorrespondence() {
//        return valueCorrespondence;
//    }
//
//    public void setValueCorrespondence(ValueCorrespondence valueCorrespondence) {
//        this.valueCorrespondence = valueCorrespondence;
//    }
    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public Widget getWidgetBarra() {
        return widgetBarra;
    }

    public void setWidgetBarra(Widget widgetBarra) {
        this.widgetBarra = widgetBarra;
    }

    @Override
    public void removeSourceScenario(ICompositionWidget sourceWidget) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
