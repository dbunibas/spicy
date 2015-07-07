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
 
package it.unibas.spicy.model.datasource.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.CheckPathContainment;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarkNodesToExclude {
    
    public void excludeNodes(IDataSourceProxy dataSource) {
        if (dataSource.getInclusions().isEmpty()) {
            return;
        }
        ExcludeNodesVisitor visitor = new ExcludeNodesVisitor(dataSource);
        dataSource.getIntermediateSchema().accept(visitor);
    }
    
}

class ExcludeNodesVisitor implements INodeVisitor {
    
    private static Log logger = LogFactory.getLog(ExcludeNodesVisitor.class);
    
    private IDataSourceProxy dataSource;
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private CheckPathContainment pathContainmentChecker = new CheckPathContainment();
    
    public ExcludeNodesVisitor(IDataSourceProxy dataSource) {
        this.dataSource = dataSource;
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
    
    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }
    
    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }
    
    private void visitNode(INode node) {
        node.setExcluded(true);
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        if (checkInclusions(nodePath) && !checkExclusions(nodePath)) {
            node.setExcluded(false);
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    private boolean checkInclusions(PathExpression nodePath) {
        for (PathExpression inclusionPath : dataSource.getInclusions()) {
            if (pathContainmentChecker.isPrefixOf(inclusionPath, nodePath)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkExclusions(PathExpression nodePath) {
        for (PathExpression exclusionPath : dataSource.getExclusions()) {
            if (pathContainmentChecker.isPrefixOf(exclusionPath, nodePath)) {
                return true;
            }
        }
        return false;
    }
    
    public void visitLeafNode(LeafNode node) {
        return;
    }
    
    public Object getResult() {
        return null;
    }
    
}
