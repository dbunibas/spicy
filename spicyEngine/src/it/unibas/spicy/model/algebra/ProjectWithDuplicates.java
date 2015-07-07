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
 
package it.unibas.spicy.model.algebra;

import it.unibas.spicy.model.algebra.operators.IAlgebraTreeVisitor;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectWithDuplicates extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(ProjectWithDuplicates.class);
    private List<VariablePathExpression> attributePaths;

    public ProjectWithDuplicates(List<VariablePathExpression> attributePaths) {
        this.attributePaths = attributePaths;
    }

    public String getName() {
        return "project with duplicates";
    }

    public List<VariablePathExpression> getAttributePaths() {
        return attributePaths;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitProjectWithDuplicates(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Project with duplicates " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Starting projection " + printIds());
        if (logger.isDebugEnabled()) logger.debug("on attributes :" + attributePaths);
        if (logger.isDebugEnabled()) logger.debug("of data source :" + child.toLongString());
        ProjectWithDuplicatesOperator projectOperator = new ProjectWithDuplicatesOperator();
        IDataSourceProxy projectResult = projectOperator.projectWithDuplicates(child, attributePaths);
        if (logger.isDebugEnabled()) logger.debug("Final result of project:\n" + projectResult.toLongString());
        result = projectResult;
        return projectResult;
    }
}

class ProjectWithDuplicatesOperator {

    private static Log logger = LogFactory.getLog(NestedLoopProjectOperator.class);

    public IDataSourceProxy projectWithDuplicates(IDataSourceProxy dataSource, List<VariablePathExpression> attributePaths) {
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(dataSource);
        List<String> attributeLabels = generateAttributeLabels(attributePaths);
        projectSchema(clone, attributeLabels);
        projectInstances(clone, attributeLabels);
        return clone;
    }

    private List<String> generateAttributeLabels(List<VariablePathExpression> attributePaths) {
        List<String> result = new ArrayList<String>();
        for (VariablePathExpression pathExpression : attributePaths) {
            result.add(pathExpression.toString());
        }
        return result;
    }

    private void projectSchema(IDataSourceProxy dataSource, List<String> attributeLabels) {
        INode tuple = dataSource.getIntermediateSchema().getChild(0);
        for (Iterator<INode> it = tuple.getChildren().iterator(); it.hasNext();) {
            INode attributeNode = it.next();
            if (!attributeLabels.contains(attributeNode.getLabel())) {
                it.remove();
            }
        }
    }

    public void projectInstances(IDataSourceProxy dataSource, List<String> attributeLabels) {
        for (INode instance : dataSource.getInstances()) {
            removeAttribute(instance, attributeLabels);
        }
    }

    private void removeAttribute(INode instanceRoot, List<String> attributeLabels) {
        for (INode tuple : instanceRoot.getChildren()) {
            for (Iterator<INode> it = tuple.getChildren().iterator(); it.hasNext();) {
                INode attributeNode = it.next();
                if (!attributeLabels.contains(attributeNode.getLabel())) {
                    it.remove();
                }
            }
        }
    }

}
