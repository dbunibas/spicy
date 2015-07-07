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
 
package it.unibas.spicy.model.generators.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.persistence.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CheckIntegerJoinValues {

    private static Log logger = LogFactory.getLog(CheckIntegerJoinValues.class);

    public boolean hasJoinsOnIntegerValues(MappingTask mappingTask) {
        for (JoinCondition joinCondition : mappingTask.getTargetProxy().getJoinConditions()) {
            if (checkJoinCondition(joinCondition, mappingTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkJoinCondition(JoinCondition joinCondition, MappingTask mappingTask) {
        for (PathExpression fromPath : joinCondition.getFromPaths()) {

            AttributeNode attributeNode = (AttributeNode) fromPath.getLastNode(mappingTask.getTargetProxy().getIntermediateSchema());
            String value = attributeNode.getChild(0).getLabel();
            if (!value.equals(Types.STRING)) {
                return true;
            }
        }
        for (PathExpression toPath : joinCondition.getToPaths()) {
            AttributeNode attributeNode = (AttributeNode) toPath.getLastNode(mappingTask.getTargetProxy().getIntermediateSchema());
            String value = attributeNode.getChild(0).getLabel();
            if (!value.equals(Types.STRING)) {
                return true;
            }
        }
        return false;
    }
}
