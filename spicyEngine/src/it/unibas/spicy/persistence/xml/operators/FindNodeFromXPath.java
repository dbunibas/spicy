/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Alessandro Pappalardo - pappalardo.alessandro@gmail.com
    Gianvito Summa - gianvito.summa@gmail.com

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
 
package it.unibas.spicy.persistence.xml.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FindNodeFromXPath {
    
    INode findNode(INode root, List<String> paths) {
        FindNodeFromXPathVisitor visitor = new FindNodeFromXPathVisitor(root, paths);
        root.accept(visitor);
        return visitor.getResult();
    }
    
}

class FindNodeFromXPathVisitor implements INodeVisitor {

    private static Log logger = LogFactory.getLog(FindNodeFromXPathVisitor.class); 

    private INode root;
    private List<String> paths;
    private int currentPosition = 0;
    private INode targetNode;
    
    FindNodeFromXPathVisitor(INode root, List<String> paths){
        this.root = root;
        this.paths = paths;
    }
    
    private boolean check(INode node){
        if(node instanceof AttributeNode && !(currentPosition == paths.size() - 1)) {
            return false;
        }
        return true;
    }
    
    private void visitGenericNode(INode node) {
        if(paths.size() >= currentPosition && targetNode == null){
            if (logger.isDebugEnabled()) logger.debug(" --- paths.get(" + currentPosition + ") =" + paths.get(currentPosition));
            if (logger.isDebugEnabled()) logger.debug(" --- node label: " + node.getLabel());
            
            if(node.getLabel().equalsIgnoreCase(paths.get(currentPosition)) && !node.isVirtual() && check(node)){
                if (logger.isDebugEnabled()) logger.debug(" --- correspondence founded " + node.getLabel());
                currentPosition++;
            }

            if(paths.size() == currentPosition){
                if (logger.isDebugEnabled()) logger.debug(" --- node founded in: " + node.getFather().getLabel() + "." + node.getLabel());
                this.targetNode = node;
            } else {
                if (logger.isDebugEnabled()) logger.debug(" --- visiting children of " + node.getLabel());
                if (logger.isDebugEnabled()) logger.debug(" --- favorite child: " + paths.get(currentPosition));

                INode favoriteChild = node.getChildStartingWith(paths.get(currentPosition));
                if(favoriteChild == null){
                    for(INode child : node.getChildren()){
                        if (logger.isDebugEnabled()) logger.debug(" --- child " + child.getLabel());
                        child.accept(this);
                    }
                } else {
                    if (logger.isDebugEnabled()) logger.debug(" --- found favorite child: " + paths.get(currentPosition));
                    favoriteChild.accept(this);
                }
            }
        } 
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }
    
    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
    }
    
    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
    }
    
    public void visitUnionNode(UnionNode node) {
        visitGenericNode(node);
    }
    
    public void visitAttributeNode(AttributeNode node) {
        visitGenericNode(node);
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitGenericNode(node);
    }
    
    public void visitLeafNode(LeafNode node) {
        return;
    }
    
    public INode getResult(){
        if(targetNode == null){
            throw new NullPointerException("Error: unable to find target node: " + paths + " in " + root);
        }
        if(logger.isDebugEnabled()) logger.debug(" Node: " + paths + " founded with " + this.targetNode.getFather().getLabel());
        return this.targetNode;
    }
}
