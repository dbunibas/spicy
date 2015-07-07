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

package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.Duplication;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.List;
import java.util.Map;

public interface IDataSourceProxy {

    public DataSource getDataSource();

    public boolean isModified();

    public void setModified(boolean modified);

    public boolean isToBeSaved();
    
    public void setToBeSaved(boolean toBeSaved);
    
    public boolean isNested();

    public String getProviderType();

    public String getType();

    public void setType(String type);

    public DataSourceMappingData getMappingData();

    public INode getSchema();

    public INode getIntermediateSchema();

    public List<KeyConstraint> getKeyConstraints();

    public void addKeyConstraint(KeyConstraint keyConstraint);

    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint);

    public List<ForeignKeyConstraint> getForeignKeyConstraints();

    public Boolean addJoinForForeignKey(ForeignKeyConstraint foreignKeyConstraint);

    public List<JoinCondition> getJoinConditions();

    public Boolean addJoinCondition(JoinCondition joinCondition);

    public void setForeignKeyForJoin(JoinCondition joinCondition, boolean foreignKey);

    public void setMandatoryForJoin(JoinCondition joinCondition, boolean mandatory);

    public List<FunctionalDependency> getFunctionalDependencies();

    public void addFunctionalDependency(FunctionalDependency functionalDependency);

    public void removeFunctionalDependency(FunctionalDependency functionalDependency);

    public List<Duplication> getDuplications();

    public PathExpression addDuplication(PathExpression duplication);

    public void removeDuplication(PathExpression duplication);

    public List<SelectionCondition> getSelectionConditions();

    public void addSelectionCondition(SelectionCondition selectionCondition);

    public void removeSelectionCondition(SelectionCondition selectionCondition);

    public void addInstanceWithCheck(INode instance);

    public void addInstance(INode instance);

    public List<INode> getInstances();

    public List<INode> getOriginalInstances();

    public List<INode> getIntermediateInstances();

    public void generateIntermediateSchema();

    public void clearIntermediateInstances();

    public void addIntermediateInstance(INode instance);

    public void addInclusion(PathExpression inclusion);

    public void addExclusion(PathExpression exclusion);

    public List<PathExpression> getInclusions();

    public List<PathExpression> getExclusions();

    public Object getAnnotation(String key);

    public Map<String, Object> getAnnotations();

    public void setAnnotations(Map<String, Object> annotations);

    public void addAnnotation(String key, Object value);

    public List<PathExpression> getAbsoluteSetPaths();

    public String toLongString();

    public String toInstanceString();

    public String toIntermediateInstancesString();

    public String toLongStringWithOids();
}
