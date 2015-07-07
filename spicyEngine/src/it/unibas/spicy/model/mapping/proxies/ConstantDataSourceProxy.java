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
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.List;

public class ConstantDataSourceProxy extends AbstractDataSourceProxy {

    private DataSource dataSource;

    public ConstantDataSourceProxy(DataSource dataSource) {
        this.dataSource = dataSource;
        this.intermediateSchema = dataSource.getSchema().clone();
    }

    public String getProviderType() {
        return SpicyEngineConstants.PROVIDER_TYPE_CONSTANT;
    }

    public boolean isModified() {
        return this.modified;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public List<JoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public List<FunctionalDependency> getFunctionalDependencies() {
        return functionalDependencies;
    }

    public List<Duplication> getDuplications() {
        return duplications;
    }

    public List<SelectionCondition> getSelectionConditions() {
        return selectionConditions;
    }

    public List<PathExpression> getInclusions() {
        return inclusions;
    }

    public List<PathExpression> getExclusions() {
        return exclusions;
    }
}
