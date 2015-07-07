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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NestedLoopProjectOperator implements IProjectOperator {

    private static Log logger = LogFactory.getLog(NestedLoopProjectOperator.class);

    public IDataSourceProxy project(IDataSourceProxy dataSource, List<VariablePathExpression> attributePaths) {
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
            removeDuplicatedTuples(instance);
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

    private void removeDuplicatedTuples(INode setNode) {
        int i = 0;
        List<INode> tuples = setNode.getChildren();
        while (i < tuples.size()) {
            INode tuple = tuples.get(i);
            if (countOccurrences(tuple, tuples) > 1) {
                tuples.remove(i);
            } else {
                i++;
            }
        }
    }

    private int countOccurrences(INode tuple, List<INode> tuples) {
        int occurrences = 0;
        for (INode otherTuple : tuples) {
            if (areEqual(tuple, otherTuple)) {
                occurrences++;
            }
        }
        return occurrences;
    }

    private boolean areEqual(INode tuple, INode otherTuple) {
        if (tuple.getChildren().size() != otherTuple.getChildren().size()) {
            return false;
        }
        for (int i = 0; i < tuple.getChildren().size(); i++) {
            INode tupleAttribute = tuple.getChild(i);
            INode otherTupleAttribute = otherTuple.getChild(i);
            Object tupleAttributeValue = tupleAttribute.getChild(0).getValue();
            Object otherTupleAttributeValue = otherTupleAttribute.getChild(0).getValue();
            if (!tupleAttributeValue.equals(otherTupleAttributeValue)) {
                return false;
            }
        }
        return true;
    }

}
