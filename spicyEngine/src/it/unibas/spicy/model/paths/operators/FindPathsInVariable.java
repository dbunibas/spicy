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
 
package it.unibas.spicy.model.paths.operators;

import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindPathsInVariable {

    public List<VariablePathExpression> findAttributePaths(SetAlias variable, INode root) {
        List<VariablePathExpression> childrenPaths = findChidrenPaths(variable, root);
        List<VariablePathExpression> attributePaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression childPath : childrenPaths) {
            if (childPath.getLastNode(root) instanceof AttributeNode) {
                attributePaths.add(childPath);
            }
        }
        return attributePaths;
    }

    public List<VariablePathExpression> findFirstLevelAttributePaths(SetAlias variable, INode root) {
        List<VariablePathExpression> allAttributePaths = findAttributePaths(variable, root);
        List<VariablePathExpression> firstLevelPaths = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression childPath : allAttributePaths) {
            if (childPath.getStartingVariable().hasSameId(variable)) {
                firstLevelPaths.add(childPath);
            }
        }
        return firstLevelPaths;
    }

    public List<VariablePathExpression> findChidrenPaths(SetAlias variable, INode root) {
        ExtractNodePathsFromVariableVisitor visitor = new ExtractNodePathsFromVariableVisitor(root);
        variable.accept(visitor);
        return visitor.getResult();
    }

    public List<VariablePathExpression> findAttributePaths(SimpleConjunctiveQuery view, INode root) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (SetAlias variable : view.getVariables()) {
            addChildrenPaths(findAttributePaths(variable, root), result, root);
        }
        return result;
    }

    public List<VariablePathExpression> findChildrenPaths(SimpleConjunctiveQuery view, INode root) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (SetAlias variable : view.getVariables()) {
            addChildrenPaths(findChidrenPaths(variable, root), result, root);
        }
        return result;
    }

    private void addChildrenPaths(List<VariablePathExpression> newPaths, List<VariablePathExpression> result, INode root) {
        for (VariablePathExpression newPath : newPaths) {
            if (!result.contains(newPath)) {
                result.add(newPath);
            }
        }
    }
}

class ExtractNodePathsFromVariableVisitor implements IVariablePathVisitor {

    private static Log logger = LogFactory.getLog(ExtractNodePathsFromVariableVisitor.class);

    private INode root;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();

    ExtractNodePathsFromVariableVisitor(INode root) {
        this.root = root;
    }

    public void visitVariable(SetAlias variable) {
        if (logger.isDebugEnabled()) logger.debug("Extracting attributes from variable: " + variable);
        VariablePathExpression pathExpression = variable.getBindingPathExpression();
        pathExpression.accept(this);
        List<VariablePathExpression> relativePaths = pathGenerator.generateFirstLevelChildrenRelativePaths(pathExpression, root);
        for (VariablePathExpression relativePath : relativePaths) {
            List<String> pathSteps = relativePath.getPathSteps();
            pathSteps.remove(0);
            result.add(new VariablePathExpression(variable, pathSteps));
        }
        if (logger.isDebugEnabled()) logger.debug("Extracted attributes: " + result);
    }

    public void visitPathExpression(VariablePathExpression pathExpression) {
        SetAlias startingVariable = pathExpression.getStartingVariable();
        if (startingVariable != null) {
            startingVariable.accept(this);
        }
    }

    public List<VariablePathExpression> getResult() {
        return this.result;
    }
}

