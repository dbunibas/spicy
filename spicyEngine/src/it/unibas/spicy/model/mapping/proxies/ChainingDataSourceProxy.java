/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com
Donatello Santoro - donatello.santoro@gmail.com

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
package it.unibas.spicy.model.mapping.proxies;

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;

public class ChainingDataSourceProxy extends AbstractDataSourceProxy {

    private MappingTask mappingTask;
    private String mappingTaskFilePath;
    private DataSource solution;

    public ChainingDataSourceProxy(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public ChainingDataSourceProxy(MappingTask mappingTask, String mappingTaskFilePath) {
        this(mappingTask);
        this.mappingTaskFilePath = mappingTaskFilePath;
    }

    public String getMappingTaskFilePath() {
        return mappingTaskFilePath;
    }

    public MappingTask getMappingTask() {
        return mappingTask;
    }

    public String getProviderType() {
        return SpicyEngineConstants.PROVIDER_TYPE_CHAINING;
    }

    public DataSource getDataSource() {
        boolean wasToBeSaved = super.toBeSaved;
        boolean mappingTaskWasToBeSaved = this.mappingTask.isToBeSaved();
        if (this.solution == null || this.isModified()) {
            this.solution = this.mappingTask.getMappingData().getSolution().getDataSource();
            super.modified = false;
            super.toBeSaved = wasToBeSaved;            
            this.mappingTask.setModified(false);
            this.mappingTask.setToBeSaved(mappingTaskWasToBeSaved);
            this.mappingTask.getMappingData().verifySolution();
        }
        return solution;
    }

    public boolean isModified() {
        return super.modified || this.mappingTask.isModified();
    }

    public List<JoinCondition> getJoinConditions() {
        List<JoinCondition> result = new ArrayList<JoinCondition>(this.joinConditions);
        result.addAll(this.mappingTask.getTargetProxy().getJoinConditions());
        return result;
    }

    public List<FunctionalDependency> getFunctionalDependencies() {
        List<FunctionalDependency> result = new ArrayList<FunctionalDependency>(this.functionalDependencies);
        result.addAll(this.mappingTask.getTargetProxy().getFunctionalDependencies());
        return result;
    }

    public List<Duplication> getDuplications() {
        List<Duplication> result = new ArrayList<Duplication>(this.duplications);
        result.addAll(this.mappingTask.getTargetProxy().getDuplications());
        return result;
    }

    public List<SelectionCondition> getSelectionConditions() {
        List<SelectionCondition> result = new ArrayList<SelectionCondition>(this.selectionConditions);
        result.addAll(this.mappingTask.getTargetProxy().getSelectionConditions());
        return result;
    }

    public List<PathExpression> getInclusions() {
        List<PathExpression> result = new ArrayList<PathExpression>(this.inclusions);
        result.addAll(this.mappingTask.getTargetProxy().getInclusions());
        return result;
    }

    public List<PathExpression> getExclusions() {
        List<PathExpression> result = new ArrayList<PathExpression>(this.exclusions);
        result.addAll(this.mappingTask.getTargetProxy().getExclusions());
        return result;
    }

}
