/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
 
package it.unibas.spicygui.controllo.tree.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.mapping.operators.DeleteExclusionCorrespondences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.Widget;


public class ExcludeNodes {
    private ExcludeNodesVisitor visitor;

    public ExcludeNodes(IDataSourceProxy dataSource) {
        this.visitor = new ExcludeNodesVisitor(dataSource);
        
    }
    
    public void exclude(INode node) {
        node.accept(visitor);
    }
    
}

class ExcludeNodesVisitor implements INodeVisitor {

    private Log logger = LogFactory.getLog(ExcludeNodesVisitor.class);
    private DeleteExclusionCorrespondences deleter = new DeleteExclusionCorrespondences();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private IDataSourceProxy dataSource;

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

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitNode(INode node) {
        node.setExcluded(true);
        disabilitaWidget(node);
        deleter.eliminaConnessioni(node);
        visitChildren(node);
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
            dataSource.getExclusions().remove(pathExpression);
            child.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }

    private void disabilitaWidget(INode node) {
        Widget widget = (Widget) node.getAnnotation(Costanti.PIN_WIDGET_TREE_SPICY);
        if (widget != null) {
            widget.setEnabled(false);
        }
    }
}
