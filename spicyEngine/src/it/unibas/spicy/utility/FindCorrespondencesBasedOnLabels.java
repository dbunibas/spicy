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
 
package it.unibas.spicy.utility;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
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
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FindCorrespondencesBasedOnLabels {

    private static Log logger = LogFactory.getLog(FindCorrespondencesBasedOnLabels.class);

    public void findCorrespondences(String inputFile, String outputFile) throws DAOException {
        DAOMappingTask daoMappingTask = new DAOMappingTask();
        MappingTask mappingTask = daoMappingTask.loadMappingTask(inputFile);
        INode source = mappingTask.getSourceProxy().getSchema();
        INode target = mappingTask.getTargetProxy().getSchema();

        NodeExtractorVisitor visitorSource = new NodeExtractorVisitor();
        source.accept(visitorSource);
        List<INode> sourceAttributes = visitorSource.getResult();

        NodeExtractorVisitor visitorTarget = new NodeExtractorVisitor();
        target.accept(visitorTarget);
        List<INode> targetAttributes = visitorTarget.getResult();

        generateCorrespondences(sourceAttributes, targetAttributes, mappingTask);
        daoMappingTask.saveMappingTask(mappingTask, outputFile);
    }

    private void generateCorrespondences(List<INode> sourceAttributes, List<INode> targetAttributes, MappingTask mappingTask) {
        for (INode sourceAttribute : sourceAttributes) {
            for (INode targetAttribute : targetAttributes) {
                if (sourceAttribute.getLabel().equals(targetAttribute.getLabel())) {
                    PathExpression sourcePath = new GeneratePathExpression().generatePathFromRoot(sourceAttribute);
                    PathExpression targetPath = new GeneratePathExpression().generatePathFromRoot(targetAttribute);
                    ValueCorrespondence correspondence = new ValueCorrespondence(sourcePath, targetPath);
                    mappingTask.addCorrespondence(correspondence);
                    break;
                }
            }
        }
    }
}
class NodeExtractorVisitor implements INodeVisitor {

    private List<INode> result = new ArrayList<INode>();

    public List<INode> getResult() {
        return result;
    }

    public void visitSetNode(SetNode node) {
        visitChildren(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitChildren(node);
    }

    public void visitUnionNode(UnionNode node) {
        visitChildren(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        result.add(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        return;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitChildren(INode node) {
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            for (INode child : listOfChildren) {
                child.accept(this);
            }
        }
    }
}

