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

public abstract class AbstractCaratteristicheWidgetComposition implements ICaratteristicheWidget {

    private String treeType;
    private MutableMappingTask mutableMappingTask;
    private List<ICompositionWidget> targetList = new ArrayList<ICompositionWidget>();

    public AbstractCaratteristicheWidgetComposition() {
    }

    public AbstractCaratteristicheWidgetComposition(MutableMappingTask mutableMappingTask) {
        this.mutableMappingTask = mutableMappingTask;
    }

    public String getTreeType() {
        return this.treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public MutableMappingTask getMutableMappingTask() {
        return mutableMappingTask;
    }

    public void setMutableMappingTask(MutableMappingTask mutableMappingTask) {
        this.mutableMappingTask = mutableMappingTask;
    }

    public void addTargetWidget(ICompositionWidget target) {
        this.targetList.add(target);
    }

    public ICompositionWidget getTargetWidget(int i) {
        return this.targetList.get(i);
    }

    public List<ICompositionWidget> getTargetList() {
        return targetList;
    }

    public abstract void removeSourceScenario(ICompositionWidget sourceWidget);
}
