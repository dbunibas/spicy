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

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;


public class CaratteristicheWidgetInterAttributeGroup implements ICaratteristicheWidget {

    private String treeType;
    private Widget widgetBarra;
    private List<VMDPinWidgetSource> sourceList = new ArrayList<VMDPinWidgetSource>();
    private String expressionFunction;
    private VMDPinWidgetTarget targetWidget;
    private ConnectionInfo connectionInfo;
    private ValueCorrespondence valueCorrespondence;

    public CaratteristicheWidgetInterAttributeGroup() {
    }

    public String getTreeType() {
        return this.treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public void addSourceWidget(VMDPinWidgetSource source) {
        this.sourceList.add(source);
    }

    public VMDPinWidgetSource getSourceWidget(int i) {
        return this.sourceList.get(i);
    }

    public List<VMDPinWidgetSource> getSourceList() {
        return sourceList;
    }

    public VMDPinWidgetTarget getTargetWidget() {
        return targetWidget;
    }

    public void setTargetWidget(VMDPinWidgetTarget targetWidget) {
        this.targetWidget = targetWidget;
    }

    public String getExpressionFunction() {
        return expressionFunction;
    }

    public void setExpressionFunction(String expressionFunction) {
        this.expressionFunction = expressionFunction;
    }

    public ValueCorrespondence getValueCorrespondence() {
        return valueCorrespondence;
    }

    public void setValueCorrespondence(ValueCorrespondence valueCorrespondence) {
        this.valueCorrespondence = valueCorrespondence;
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
