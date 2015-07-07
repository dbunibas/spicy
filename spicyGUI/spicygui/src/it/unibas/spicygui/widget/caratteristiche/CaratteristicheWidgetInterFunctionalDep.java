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

import it.unibas.spicy.model.datasource.FunctionalDependency;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.Widget;


public class CaratteristicheWidgetInterFunctionalDep implements ICaratteristicheWidget {

    private String treeType;
    private Boolean source;
    private Widget widgetBarra;
    private List<VMDPinWidget> sourceList = new ArrayList<VMDPinWidget>();
    private List<VMDPinWidget> targetList = new ArrayList<VMDPinWidget>();
    private ConnectionInfo connectionInfo;
    private FunctionalDependency functionalDependency;

    public CaratteristicheWidgetInterFunctionalDep() {
    }

    public String getTreeType() {
        return this.treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public Boolean isSource() {
        return source;
    }

    public void setSource(Boolean source) {
        this.source = source;
    }

    public void addSourceWidget(VMDPinWidget source) {
        this.sourceList.add(source);
    }

    public VMDPinWidget getSourceWidget(int i) {
        return this.sourceList.get(i);
    }

    public List<VMDPinWidget> getSourceList() {
        return sourceList;
    }

    public void addTargetWidget(VMDPinWidget target) {
        this.targetList.add(target);
    }

    public VMDPinWidget getTargetWidget(int i) {
        return this.targetList.get(i);
    }

    public List<VMDPinWidget> getTargetList() {
        return targetList;
    }

    public FunctionalDependency getFunctionalDependency() {
        return functionalDependency;
    }

    public void setFunctionalDependency(FunctionalDependency functionalDependency) {
        this.functionalDependency = functionalDependency;
    }

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
    
    
}
