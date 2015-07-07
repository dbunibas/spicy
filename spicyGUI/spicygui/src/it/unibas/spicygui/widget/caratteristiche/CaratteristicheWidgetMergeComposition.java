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

import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.widget.ICompositionWidget;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;


public class CaratteristicheWidgetMergeComposition extends AbstractCaratteristicheWidgetComposition {

//    private String treeType;
    private Widget widgetBarra;
    private List<ICompositionWidget> sourceList = new ArrayList<ICompositionWidget>();

    private ConnectionInfo connectionInfo;

    public CaratteristicheWidgetMergeComposition(MutableMappingTask mutableMappingTask) {
        super(mutableMappingTask);
    }

    public CaratteristicheWidgetMergeComposition() {
        super(new MutableMappingTask());
    }

//    public String getTreeType() {
//        return this.treeType;
//    }
//
//    public void setTreeType(String treeType) {
//        this.treeType = treeType;
//    }

    public void addSourceWidget(ICompositionWidget source) {
        this.sourceList.add(source);
    }

     @Override
    public void removeSourceScenario(ICompositionWidget source) {
        this.sourceList.remove(source);
    }

    public ICompositionWidget getSourceWidget(int i) {
        return this.sourceList.get(i);
    }

    public List<ICompositionWidget> getSourceList() {
        return sourceList;
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
