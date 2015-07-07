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
 
package it.unibas.spicy.attributematch.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;

public class FindAttributes {
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
        
    public List<INode> findAttributes(INode schema, boolean ignoreExcluded) {
        FindAttributesVisitor visitor = new FindAttributesVisitor(ignoreExcluded);
        schema.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findAttributes(INode schema) {
        return findAttributes(schema, false);
    }

    public List<PathExpression> findAttributePaths(INode schema) {
        return findAttributePaths(schema, false);    
    }

    public List<PathExpression> findAttributePaths(INode schema, boolean ignoreExcluded) {
        List<INode> attributeNodes = findAttributes(schema, ignoreExcluded);
        List<PathExpression> attributePaths = new ArrayList<PathExpression>();
        for (INode attributeNode : attributeNodes) {
            attributePaths.add(pathGenerator.generatePathFromRoot(attributeNode));
        }
        return attributePaths;
    }

}

class FindAttributesVisitor implements INodeVisitor {

    private List<INode> result = new ArrayList<INode>();
    private boolean ignoreExcluded = false;
    
    public FindAttributesVisitor() {}
    
    public FindAttributesVisitor(boolean ignoreExcluded) {
        this.ignoreExcluded = ignoreExcluded;
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

    private void visitNode(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    public void visitAttributeNode(AttributeNode node) {
        if (this.ignoreExcluded && node.isExcluded()) {
            return;
        } 
        result.add(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode leafNode) {
        return;
    }

    public List<INode> getResult() {
        return result;
    }
    
}