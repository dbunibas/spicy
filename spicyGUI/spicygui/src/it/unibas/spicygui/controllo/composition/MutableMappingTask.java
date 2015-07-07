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
package it.unibas.spicygui.controllo.composition;

import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import java.util.ArrayList;
import java.util.List;

public class MutableMappingTask {

    private List<MutableMappingTask> mutableMappingTasks = new ArrayList<MutableMappingTask>();
    private List<IDataSourceProxy> sourceProxies = new ArrayList<IDataSourceProxy>();
    private List<IDataSourceProxy> targetProxies = new ArrayList<IDataSourceProxy>();
    private MappingTask mappingTask;

    public MutableMappingTask() {
    }

    public MutableMappingTask(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public List<MutableMappingTask> getMutableMappingTasks() {
        return mutableMappingTasks;
    }

    public void addMutableMappingTask(MutableMappingTask mutableMappingTask) {
        this.mutableMappingTasks.add(mutableMappingTask);
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public void setMappingTask(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public List<IDataSourceProxy> getSourceProxies() {
        return sourceProxies;
    }

    public void setSourcetProxies(List<IDataSourceProxy> sourceProxies) {
        this.sourceProxies = sourceProxies;
    }

    public void addSourceProxies(IDataSourceProxy sourceProxy) {
        this.sourceProxies.add(sourceProxy);
    }

    public List<IDataSourceProxy> getTargetProxies() {
        return targetProxies;
    }

    public void setTargetProxies(List<IDataSourceProxy> targetProxies) {
        this.targetProxies = targetProxies;
    }

    public void addTargetProxies(IDataSourceProxy targetProxy) {
        this.targetProxies.add(targetProxy);
    }

    public boolean mutableMappingTasksContains(MutableMappingTask mutableMappingTask) {
        return this.mutableMappingTasks.contains(mutableMappingTask);
    }
}
