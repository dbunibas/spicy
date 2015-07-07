/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com

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
 
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.mapping.operators.GenerateSetVariables;
import it.unibas.spicy.model.mapping.operators.GenerateViews;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.model.paths.operators.SimplifyDependencies;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSourceMappingData {

    public IDataSourceProxy dataSourceProvider;
    public List<SetAlias> variables;
    public List<SimpleConjunctiveQuery> views;
    public List<VariableFunctionalDependency> dependencies;
    public List<VariableFunctionalDependency> simplifiedDependencies;

    public DataSourceMappingData(IDataSourceProxy dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public List<SetAlias> getVariables() {
        if (this.variables == null) {
            this.variables = new ArrayList<SetAlias>(new GenerateSetVariables().generateVariables(dataSourceProvider).values());
            Collections.sort(variables);
//            new ContextualizePaths().contextualizeSelectionConditions(dataSourceProvider);
        }
        return this.variables;
    }

    public boolean isNested() {
        for (SetAlias variable : getVariables()) {
            if (variable.getGenerators().size() > 1) {
                return true;
            }
        }
        return false;
    }

    public List<SimpleConjunctiveQuery> getViews() {
        if (this.views == null) {
            this.views = new GenerateViews().generateViews(dataSourceProvider);
        }
        return this.views;
    }

    public List<VariableFunctionalDependency> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new ContextualizePaths().contextualizeFunctionalDependencies(dataSourceProvider);
        }
        return this.dependencies;
    }

    public List<VariableFunctionalDependency> getSimplifiedDependencies() {
        if (this.simplifiedDependencies == null) {
            this.simplifiedDependencies = new SimplifyDependencies().simplifyDependencies(getDependencies());
        }
        return this.simplifiedDependencies;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("--------------------  Variables ------------------------\n");
        for (SetAlias variable : getVariables()) {
            result.append(variable).append("\n");
        }
        result.append("--------------------   Views   -------------------------\n");
        for (int i = 0; i < getViews().size(); i++) {
            result.append(getViews().get(i));
            if (i != getViews().size() - 1) {
                result.append("\n");
            }
        }
        if (!this.getDependencies().isEmpty()) {
            result.append("--------------------   Functional Dependencies   -------------------------\n");
            for (int i = 0; i < getDependencies().size(); i++) {
                result.append(getDependencies().get(i)).append("\n");
            }
        }
        result.append("-------------------------------------------------------\n");
        return result.toString();
    }
}
