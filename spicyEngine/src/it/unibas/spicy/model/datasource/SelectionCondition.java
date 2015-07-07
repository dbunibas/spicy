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
package it.unibas.spicy.model.datasource;

import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.expressions.operators.CheckExpressions;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectionCondition implements Cloneable {

    private static Log logger = LogFactory.getLog(SelectionCondition.class);

    private List<PathExpression> setPaths = new ArrayList<PathExpression>();
    private Expression condition;

    private static List<PathExpression> generateList(PathExpression pathExpression) {
        List<PathExpression> result = new ArrayList<PathExpression>();
        result.add(pathExpression);
        return result;
    }

    public SelectionCondition(PathExpression setPath, Expression condition, INode root) {
        this(generateList(setPath), condition, root);
    }

    public SelectionCondition(List<PathExpression> setPaths, Expression condition, INode root) {
        for (PathExpression setPath : setPaths) {
            checkSetPath(setPath, root);
        }
        this.setPaths = setPaths;
        this.condition = condition;
        checkExpression(this.setPaths, condition, root);
    }

    public Expression getCondition() {
        return condition;
    }

    public List<PathExpression> getSetPaths() {
        return setPaths;
    }

    private void checkSetPath(PathExpression setPath, INode root) {
        if (logger.isDebugEnabled()) logger.debug("Checking set path: " + setPath + " in:\n" + root);
        INode lastNode = setPath.getLastNode(root);
        if (!(lastNode instanceof SetNode)) {
            throw new IllegalArgumentException("Incorrect condition. Node must be a set: " + lastNode.getLabel());
        }
    }
    
    private void checkExpression(List<PathExpression> setPaths, Expression transformationFunction, INode root) {
        CheckExpressions functionChecker = new CheckExpressions();
        functionChecker.checkSelectionCondition(setPaths, transformationFunction, root);
    }

    public SelectionCondition clone() {
        SelectionCondition clone = null;
        try {
            clone = (SelectionCondition) super.clone();
            clone.setPaths = new ArrayList<PathExpression>();
            for (PathExpression setPath : setPaths) {
                clone.setPaths.add(setPath.clone());
            }
            clone.condition = this.condition.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("select from ").append(setPaths).append("\nwhere ").append(condition);
        return result.toString();
    }
}
