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
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import java.util.Iterator;

public class Compose extends IntermediateOperator {

    private MappingTask mappingTask;

    public Compose(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public String getName() {
        return "compose";
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitCompose(this);
    }

    public IDataSourceProxy execute(IDataSourceProxy dataSource) {
        IDataSourceProxy stTgdResult = children.get(0).execute(dataSource);
        if (children.size() == 1) {
            removeSetClones(stTgdResult);
            result = stTgdResult;
        } else {
//        IDataSourceProxy result = null;
//            GenerateSkolemValuesHypergraph.clearCache();
//            IDataSourceProxy ttTgdResult = children.get(1).execute(stTgdResult);
//            removeSetClones(ttTgdResult);
//            result = ttTgdResult;
//            addIntermediateInstancesToResult(stTgdResult, result);
        }
        DataSource finalResult = mappingTask.getTargetProxy().getDataSource().clone();
        finalResult.getInstances().clear();
        for (INode instance : result.getInstances()) {
            finalResult.addInstanceWithCheck(instance);
        }
        IDataSourceProxy finalResultProxy = new ConstantDataSourceProxy(finalResult);
        return finalResultProxy;
    }

//    private void addIntermediateInstancesToResult(IDataSourceProxy stTgdResult, IDataSourceProxy result) {
//        result.clearIntermediateInstances();
//        for (INode instance : stTgdResult.getInstances()) {
//            result.addIntermediateInstance(instance);
//        }
//    }

    private void removeSetClones(IDataSourceProxy ttTgdResult) {
        for (INode instance : ttTgdResult.getInstances()) {
            RemoveClonesVisitor visitor = new RemoveClonesVisitor();
            instance.accept(visitor);
        }
    }
}

class RemoveClonesVisitor implements INodeVisitor {

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        for (Iterator<INode> it = node.getChildren().iterator(); it.hasNext();) {
            INode child = it.next();
            if (child instanceof SetCloneNode) {
                it.remove();
            }
        }
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitTupleNode(node);
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
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }
}
