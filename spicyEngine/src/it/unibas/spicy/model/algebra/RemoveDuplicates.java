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
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoveDuplicates extends IntermediateOperator {

    private static Log logger = LogFactory.getLog(RemoveDuplicates.class);

    public String getName() {
        return "remove duplicates";
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitRemoveDuplicates(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        if (this.result != null) {
            if (logger.isDebugEnabled()) logger.debug("Project with duplicates " + printIds() + " - Returning cached result...");
            return result;
        }
        IDataSourceProxy child = children.get(0).execute(dataSource);
        if (logger.isDebugEnabled()) logger.debug("Removing duplicates " + printIds());
        if (logger.isDebugEnabled()) logger.debug("of data source :" + child.toLongString());
        RemoveDuplicatesOperator operator = new RemoveDuplicatesOperator();
        IDataSourceProxy projectResult = operator.removeDuplicates(child);
        if (logger.isDebugEnabled()) logger.debug("Final result of project:\n" + projectResult.toLongString());
        result = projectResult;
        return projectResult;
    }
}

class RemoveDuplicatesOperator {

    private static Log logger = LogFactory.getLog(NestedLoopProjectOperator.class);

    public IDataSourceProxy removeDuplicates(IDataSourceProxy dataSource) {
        IDataSourceProxy clone = SpicyEngineUtility.cloneAlgebraDataSource(dataSource);
        removeDuplicatesFromInstances(clone);
        return clone;
    }

    private void removeDuplicatesFromInstances(IDataSourceProxy dataSource) {
        for (INode instance : dataSource.getInstances()) {
            removeDuplicatedTuples(instance);
        }
    }

    private void removeDuplicatedTuples(INode instanceRoot) {
        Set<String> tupleSummaries = new HashSet<String>();
        for (Iterator<INode> tupleIterator = instanceRoot.getChildren().iterator(); tupleIterator.hasNext(); ) {
            INode tupleNode = tupleIterator.next();
            String tupleSummary = generateSummary(tupleNode);
            if (tupleSummaries.contains(tupleSummary)) {
                tupleIterator.remove();
            } else {
                tupleSummaries.add(tupleSummary);
            }
        }
    }

    private String generateSummary(INode tuple) {
        StringBuilder summary = new StringBuilder("#");
        for (INode attributeNode : tuple.getChildren()) {
            String value = attributeNode.getChild(0).getValue().toString();
            summary.append(value).append("#");
        }
        return summary.toString();
    }

}
