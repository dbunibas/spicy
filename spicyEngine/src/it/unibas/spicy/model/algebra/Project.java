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
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Project extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(Project.class);
    private List<VariablePathExpression> attributePaths;

    public Project(List<VariablePathExpression> attributePaths) {
        this.attributePaths = attributePaths;
    }

    public String getName() {
        return "project";
    }

    public List<VariablePathExpression> getAttributePaths() {
        return attributePaths;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitProject(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Project " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Starting projection " + printIds());
        if (logger.isDebugEnabled()) logger.debug("on attributes :" + attributePaths);
        if (logger.isDebugEnabled()) logger.debug("of data source :" + child.toLongString());
        IProjectOperator projectOperator = new NestedLoopProjectOperator();
        IDataSourceProxy projectResult = projectOperator.project(child, attributePaths);
        if (logger.isDebugEnabled()) logger.debug("Final result of project:\n" + projectResult.toLongString());
        result = projectResult;
        return projectResult;
    }
}
