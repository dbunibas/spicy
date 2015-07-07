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

import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.proxies.ChainingDataSourceProxy;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.widget.ICompositionWidget;
import org.netbeans.api.visual.widget.Widget;

public class CaratteristicheWidgetChainComposition extends AbstractCaratteristicheWidgetComposition {

//    private String treeType;
    private Widget widgetBarra;
    private ICompositionWidget sourceScenario;
//    private List<ICompositionWidget> targetList = new ArrayList<ICompositionWidget>();
    private ConnectionInfo connectionInfo;
    private IDataSourceProxy source;
    private IDataSourceProxy target;
    private MappingTask mappingTask;
//    private MutableMappingTask mutableMappingTask;
//    private ValueCorrespondence valueCorrespondence;

    public CaratteristicheWidgetChainComposition(MutableMappingTask mutableMappingTask) {
        super(mutableMappingTask);
    }

    public CaratteristicheWidgetChainComposition() {
        super(new MutableMappingTask());
    }
    
//    public CaratteristicheWidgetChainComposition(MutableMappingTask mutableMappingTask) {
//        this.mutableMappingTask = mutableMappingTask;;
//    }

    
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
//    public void addTargetWidget(ICompositionWidget target) {
//        this.targetList.add(target);
//    }
//
//    public ICompositionWidget getTargetWidget(int i) {
//        return this.targetList.get(i);
//    }
//
//    public List<ICompositionWidget> getTargetList() {
//        return targetList;
//    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public void setMappingTask(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public IDataSourceProxy getSource() {
        return source;
    }

    public void setSource(IDataSourceProxy source) {
        this.source = source;
    }

    public IDataSourceProxy getTarget() {
        return target;
    }

    public void setTarget(IDataSourceProxy target) {
        this.target = target;
    }

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
        this.sourceScenario = null;
    }
}
