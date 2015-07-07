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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.exceptions.PathSyntaxException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneratePathExpression {

    private static Log logger = LogFactory.getLog(GeneratePathExpression.class);

    private static FindNode nodeFinder = new FindNode();
    
    ////////////////   ABSOLUTE PATHS FROM NODE   //////////////////////

    public PathExpression generatePathFromRoot(INode node) {
        GeneratePathExpressionFromNodeVisitor visitor = new GeneratePathExpressionFromNodeVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }

    public PathExpression generatePathFromNode(INode node, INode startingNode) {
        GeneratePathExpressionFromNodeVisitor visitor = new GeneratePathExpressionFromNodeVisitor(startingNode);
        node.accept(visitor);
        return visitor.getResult();
    }

    ////////////////   RELATIVE PATHS   //////////////////////

    public VariablePathExpression generateRelativePath(INode node, IDataSourceProxy dataSource) {
        PathExpression pathExpression = generatePathFromRoot(node);
        VariablePathExpression relativePath = pathExpression.getRelativePath(dataSource);
        return relativePath;
    }

    public PathExpression generateAbsolutePath(VariablePathExpression pathExpression) {
        List<String> pathSteps = new ArrayList<String>(pathExpression.getPathSteps());
        SetAlias currentVariable = pathExpression.getStartingVariable();
        while (currentVariable != null) {
            addSteps(pathSteps, currentVariable.getBindingPathExpression());
            currentVariable = currentVariable.getBindingPathExpression().getStartingVariable();
        }
        return new PathExpression(pathSteps);
    }

    private void addSteps(List<String> pathSteps, VariablePathExpression pathExpression) {
        for (int i = pathExpression.getPathSteps().size() - 1; i >= 0; i--) {
            pathSteps.add(0, pathExpression.getPathSteps().get(i));
        }
    }

    public VariablePathExpression generateRelativePath(PathExpression absolutePath, IDataSourceProxy dataSource) {
        for (SetAlias variable : dataSource.getMappingData().getVariables()) {
            VariablePathExpression relativePath = generateContextualPathForNode(variable, absolutePath, dataSource.getIntermediateSchema());
            if (relativePath != null) {
                return relativePath;
            }
        }
        return null;
    }

    public VariablePathExpression generateContextualPathForNode(SetAlias variable, PathExpression pathExpression, INode root) {
        PathExpression variableAbsoluteBindingPath = variable.getAbsoluteBindingPathExpression();
        if (logger.isDebugEnabled()) logger.debug("Contextualizing path: " + pathExpression + " wrt variable: " + variable + " in " + root);
        if (pathExpression.toString().equals(variableAbsoluteBindingPath.toString())) {
            return new VariablePathExpression(variable, new ArrayList<String>());
        }
        for (VariablePathExpression nodePath : variable.getChildrenPaths(root)) {
            PathExpression childPath = generateAbsolutePath(nodePath);
            if (logger.isDebugEnabled()) logger.debug("Analyzing child path: " + childPath);
            if (pathExpression.toString().equals(childPath.toString())) {
                if (logger.isDebugEnabled()) logger.debug("********Path found********");
                return nodePath;
            }
        }
        return null;
    }

    public PathExpression append(PathExpression prefixPath, PathExpression suffixPath, INode root) {
        PathExpression newPath = new PathExpression(suffixPath);
        if (prefixPath.getLastStep().equals(suffixPath.getFirstStep())) {
            newPath.getPathSteps().remove(0);
        }
        newPath.getPathSteps().addAll(0, prefixPath.getPathSteps());
        return newPath;
    }


   ////////////////   FATHER AND CHILDREN PATHS   //////////////////////

    public List<VariablePathExpression> generateFirstLevelChildrenRelativePaths(SetNode startingNode) {
        GenerateFirstLevelChildrenRelativePathsVisitor visitor = new GenerateFirstLevelChildrenRelativePathsVisitor(startingNode);
        startingNode.accept(visitor);
        return visitor.getResult();
    }

    public List<VariablePathExpression> generateFirstLevelChildrenRelativePaths(VariablePathExpression variablePath, INode root) {
        PathExpression absolutePath = variablePath.getAbsolutePath();
        INode startingNode = nodeFinder.findNodeInSchema(absolutePath, root);
        GenerateFirstLevelChildrenRelativePathsVisitor visitor = new GenerateFirstLevelChildrenRelativePathsVisitor((SetNode) startingNode);
        startingNode.accept(visitor);
        return visitor.getResult();
    }

    public List<PathExpression> generateFirstLevelChildrenAbsolutePaths(SetNode startingNode) {
        GenerateFirstLevelChildrenAbsolutePathsVisitor visitor = new GenerateFirstLevelChildrenAbsolutePathsVisitor(startingNode);
        startingNode.accept(visitor);
        return visitor.getResult();
    }

    public List<PathExpression> generateFirstLevelAttributeAbsolutePaths(PathExpression setPath, INode root) {
        List<PathExpression> childrenPaths = generateFirstLevelChildrenAbsolutePaths((SetNode)setPath.getLastNode(root));
        for (Iterator<PathExpression> it = childrenPaths.iterator(); it.hasNext(); ) {
            if (! (it.next().getLastNode(root) instanceof AttributeNode)) {
                it.remove();
            }
        }
        return childrenPaths;
    }

    public PathExpression generateSetPathForAttribute(PathExpression attributeAbsolutePath, IDataSourceProxy dataSource) {
        assert (attributeAbsolutePath.getLastNode(dataSource.getIntermediateSchema()) instanceof AttributeNode) : "Path must be an attribute path: " + attributeAbsolutePath;
        INode currentNode = attributeAbsolutePath.getLastNode(dataSource.getIntermediateSchema());
        while (true) {
            if (currentNode instanceof SetNode) {
                return generatePathFromRoot(currentNode);
            }
            if (currentNode.isRoot()) {
                return null;
            }
            currentNode = currentNode.getFather();
        }
    }

    ////////////////   ABSOLUTE PATHS FROM STRING   //////////////////////

    private static final String PATH_SEPARATOR = ".";

    public PathExpression generatePathFromString(String pathDescription) {
        StringTokenizer tokenizer = new StringTokenizer(pathDescription, PATH_SEPARATOR);
        List<String> pathStepStrings = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            pathStepStrings.add(tokenizer.nextToken());
        }
        if (pathStepStrings.isEmpty()) {
            throw new PathSyntaxException("Path string is empty or syntactically wrong: " + pathDescription);
        }
        return new PathExpression(pathStepStrings);
    }

    public List<INode> generatePathStepNodes(List<String> pathStepStrings, INode root) {
        List<INode> result = new ArrayList<INode>();
        String rootString = pathStepStrings.get(0);
        if (!rootString.equals(root.getLabel())) {
            throw new PathSyntaxException("Node does not exist: " + rootString);
        }
        result.add(root);
        INode currentNode = root;
        for (int i = 1; i < pathStepStrings.size(); i++) {
            String nextStepString = pathStepStrings.get(i);
            INode nextNode = currentNode.getChild(nextStepString);
            if (nextNode == null) {
                throw new PathSyntaxException("Node does not exist: " + nextStepString);
            }
            result.add(nextNode);
            currentNode = nextNode;
        }
        return result;
    }

}
class GeneratePathExpressionFromNodeVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GeneratePathExpressionFromNodeVisitor.class);

    // if pathRoot == null path must be rooted
    // else path will be relative to pathRoot
    private INode pathRoot;
    private List<String> nodeList = new ArrayList<String>();

    public GeneratePathExpressionFromNodeVisitor() {}

    public GeneratePathExpressionFromNodeVisitor(INode pathRoot) {
        this.pathRoot = pathRoot;
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        this.nodeList.add(0, SpicyEngineConstants.LEAF);
        node.getFather().accept(this);
    }

    private void visitNode(INode node) {
        this.nodeList.add(0, node.getLabel());
        if (isPathRoot(node)) {
            return;
        }
        node.getFather().accept(this);
    }

    private boolean isPathRoot(INode node) {
        return (pathRoot == null && node.isRoot()) || (node.equals(pathRoot));
    }

    public PathExpression getResult() {
        return new PathExpression(nodeList);
    }

}

class GenerateFirstLevelChildrenRelativePathsVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateFirstLevelChildrenRelativePathsVisitor.class);
    private SetNode startingSetNode;
    private List<VariablePathExpression> pathsToChildren = new ArrayList<VariablePathExpression>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public GenerateFirstLevelChildrenRelativePathsVisitor(SetNode startingSetNode) {
        // extracts paths for descendants of a set node that do not belong to a child set
        this.startingSetNode = startingSetNode;
    }

    public void visitSetNode(SetNode node) {
        if (node.equals(startingSetNode)) {
            visitChildren(node);
        } else {
            return;
        }
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
        visitChildren(node);
    }

    private void visitNode(INode node) {
        this.pathsToChildren.add(new VariablePathExpression(pathGenerator.generatePathFromNode(node, startingSetNode)));
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<VariablePathExpression> getResult() {
        return this.pathsToChildren;
    }
}

class GenerateFirstLevelChildrenAbsolutePathsVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(GenerateFirstLevelChildrenAbsolutePathsVisitor.class);
    private SetNode startingSetNode;
    private List<PathExpression> pathsToChildren = new ArrayList<PathExpression>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public GenerateFirstLevelChildrenAbsolutePathsVisitor(SetNode startingSetNode) {
        // extracts paths for descendants of a set node that do not belong to a child set
        this.startingSetNode = startingSetNode;
    }

    public void visitSetNode(SetNode node) {
        if (node.equals(startingSetNode)) {
            visitChildren(node);
        } else {
            return;
        }
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitNode(node);
        visitChildren(node);
    }

    private void visitNode(INode node) {
        this.pathsToChildren.add(pathGenerator.generatePathFromRoot(node));
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<PathExpression> getResult() {
        return this.pathsToChildren;
    }
}


