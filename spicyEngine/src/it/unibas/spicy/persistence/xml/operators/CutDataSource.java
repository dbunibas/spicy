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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.CheckPathContainment;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CutDataSource {
    
    private static Log logger = LogFactory.getLog(CutDataSource.class);
    
    private FindNode nodeFinder = new FindNode();
    private CheckPathContainment pathChecker = new CheckPathContainment();
    
    ////////////////////// schema
    
    public IDataSourceProxy cutSchema(IDataSourceProxy dataSource, PathExpression subTreePath) {
        INode nodeSchema = nodeFinder.findNodeInSchema(subTreePath, dataSource);
        INode cuttedSchemaRoot = nodeSchema.clone();
        cuttedSchemaRoot.setFather(null);
        cuttedSchemaRoot.setRoot(true);
        cuttedSchemaRoot.setVirtual(false);
        IDataSourceProxy cuttedDataSource = new ConstantDataSourceProxy(new DataSource(dataSource.getType(), cuttedSchemaRoot));
        cuttedDataSource.setAnnotations(dataSource.getAnnotations());
        if(logger.isDebugEnabled()) logger.debug("\n PathExpression TO CUT :\n\n" + subTreePath);
        if(logger.isDebugEnabled()) logger.debug("\n SCHEMA CUTTED :\n\n" + cuttedDataSource.getIntermediateSchema());
        List<INode> listOfOriginalInstances = dataSource.getInstances();
        for(INode nodeOriginalInstance: listOfOriginalInstances){
            List<INode> listOfCuttedInstances = nodeFinder.findNodesInInstance(subTreePath, nodeOriginalInstance );
            for(INode cuttedInstance : listOfCuttedInstances){
                INode cuttedInstanceRoot = cuttedInstance.clone();
                cuttedInstanceRoot.setFather(null);
                cuttedInstanceRoot.setRoot(true);
                cuttedInstanceRoot.setVirtual(false);
                if(logger.isDebugEnabled()) logger.debug(" INSTANCE CUTTED TO ADD:\n\n" + cuttedInstanceRoot);
                cuttedDataSource.addInstanceWithCheck(cuttedInstanceRoot);
            }
        }
        checkAndAddConstraints(dataSource, cuttedDataSource, subTreePath);
        return cuttedDataSource;
    }
    
    private void checkAndAddConstraints(IDataSourceProxy dataSource, IDataSourceProxy cuttedDataSource, PathExpression subTreePath){
        List<KeyConstraint> cuttedKeyConstraints = checkKeyConstraints(dataSource.getKeyConstraints(),  subTreePath);
        for(KeyConstraint keyConstraintCutted : cuttedKeyConstraints){
            cuttedDataSource.addKeyConstraint(keyConstraintCutted);
        }
        List<ForeignKeyConstraint> cuttedForeignKeyConstraints = checkForeignKeyConstraints(dataSource.getForeignKeyConstraints(),  subTreePath);
        for(ForeignKeyConstraint foreigKeyConstraintCutted : cuttedForeignKeyConstraints){
            cuttedDataSource.addForeignKeyConstraint(foreigKeyConstraintCutted);
        }
    }
    
    private List<KeyConstraint> checkKeyConstraints(List<KeyConstraint> listOfKeyConstraints, PathExpression subTreePath){
        List<KeyConstraint> cuttedKeyConstraints = new ArrayList<KeyConstraint>();
        for(KeyConstraint keyConstraint : listOfKeyConstraints){
            if(checkKeyConstraint(keyConstraint, subTreePath)){
                cuttedKeyConstraints.add(keyConstraint);
            }
        }
        return cuttedKeyConstraints;
    }
    
    private boolean checkKeyConstraint(KeyConstraint keyConstraint, PathExpression subTreePath) {
        for(PathExpression keyPathExpression : keyConstraint.getKeyPaths()){
            if(!pathChecker.isPrefixOf(subTreePath, keyPathExpression) ){
                return false;
            }
        }
        return true;
    }
    
    private List<ForeignKeyConstraint> checkForeignKeyConstraints(List<ForeignKeyConstraint> listOfForeignKeyConstraints, PathExpression subTreePath) {
        List<ForeignKeyConstraint> listOfForeignKeyConstraintsCutted = new ArrayList<ForeignKeyConstraint>();
        for(ForeignKeyConstraint foreignkeyConstraint : listOfForeignKeyConstraints){
            if(checkForeignKeyConstraint(foreignkeyConstraint, subTreePath)){
                listOfForeignKeyConstraintsCutted.add(foreignkeyConstraint);
            }
        }
        return listOfForeignKeyConstraintsCutted;
    }
    
    private boolean checkForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint, PathExpression subTreePath) {
        if (!checkKeyConstraint(foreignKeyConstraint.getKeyConstraint(), subTreePath)) {
            return false;
        }
        for(PathExpression foreignKeyPath : foreignKeyConstraint.getForeignKeyPaths()){
            if(!pathChecker.isPrefixOf(subTreePath, foreignKeyPath) ){
                return false;
            }
        }
        return true;
    }
    
    ///////////////////////// instance
    
    public IDataSourceProxy cutInstance(IDataSourceProxy dataSource, int maxNumberOfChildren) {
        IDataSourceProxy cuttedDataSource = new ConstantDataSourceProxy(new DataSource(dataSource.getType(), dataSource.getIntermediateSchema().clone()));
        cuttedDataSource.setAnnotations(dataSource.getAnnotations());
        for (INode instance : dataSource.getInstances()) {
            CutInstanceVisitor visitor = new CutInstanceVisitor(maxNumberOfChildren);
            INode cuttedInstance = instance.clone();
            cuttedInstance.accept(visitor);
            cuttedDataSource.addInstanceWithCheck(cuttedInstance);
        }
        return cuttedDataSource;
    }
}

class CutInstanceVisitor implements INodeVisitor {
    
    private int maxNumberOfChildren;
    
    public CutInstanceVisitor(int maxNumberOfChildren) {
        this.maxNumberOfChildren = maxNumberOfChildren;
    }
    
    public void visitSetNode(SetNode node) {
        int numberOfChildren = node.getChildren().size();
        if (numberOfChildren > maxNumberOfChildren) {
            List<INode> nodeChildren = node.getChildren();
            for (int i = numberOfChildren - 1; i > maxNumberOfChildren - 1; i--) {
                nodeChildren.remove(i);
            }
        }
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

    private void visitNode(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    public void visitLeafNode(LeafNode node) {
        return;
    }

    public Object getResult() {
        return null;
    }
    
}
