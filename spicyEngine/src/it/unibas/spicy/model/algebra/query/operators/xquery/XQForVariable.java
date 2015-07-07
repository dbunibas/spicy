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
 
package it.unibas.spicy.model.algebra.query.operators.xquery;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.operators.IVariablePathVisitor;
import java.util.List;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class XQForVariable {

    public static String toXQueryString(SetAlias variable, List<SetAlias> existingVariables, INode root) {
        VariableToXQueryStringVisitor visitor = new VariableToXQueryStringVisitor(root, existingVariables);
        variable.accept(visitor);
        return visitor.getResult();
    }
}

class VariableToXQueryStringVisitor implements IVariablePathVisitor {

    private static Log logger = LogFactory.getLog(VariableToXQueryStringVisitor.class);
    private String pathDescription = "";
    private Stack<String> stack = new Stack<String>();
    private INode root;
    private List<SetAlias> variablesExistingInView;

    VariableToXQueryStringVisitor(INode root, List<SetAlias> variablesExistingInView) {
        this.root = root;
        this.variablesExistingInView = variablesExistingInView;
    }

    public void visitVariable(SetAlias v) {
//        if (!variablesExistingInView.contains(v)) {
            String variableDescription = v.toShortStringWithDollar();
            String bindingPathString = XQUtility.toStringWithoutVirtualNodesFromRoot(v.getBindingPathExpression(), root);
            SetNode setNode = (SetNode) v.getBindingPathExpression().getLastNode(root);
            SetAlias fatherVariable = v.getBindingPathExpression().getStartingVariable();
            if (fatherVariable == null) {
                bindingPathString = XQUtility.DOC + "/" + bindingPathString;
            } else {
                String firstPart = fatherVariable.toShortStringWithDollar();
                String secondPart = "";
                String descendantLabel = findSingleAttributeUnderVirtualTuple(setNode);
                if (descendantLabel != null) {
                    // node is a virtual set with a virtual tuple and a single attribute
                    secondPart = descendantLabel;
                } else {
                    secondPart = XQUtility.toStringWithoutVirtualNodes(v.getBindingPathExpression(), root);
                }
                if (logger.isDebugEnabled()) logger.debug("Second part: " + secondPart);
                String bindingPathWithSlashes = XQUtility.replaceDotsWithSlashes(firstPart + "/" + secondPart);
                bindingPathString = bindingPathWithSlashes;
            }
            INode variableTuple = setNode.getChild(0);
            variableDescription = variableDescription + " in " + bindingPathString;
            if (!variableDescription.endsWith("/") && !variableTuple.isVirtual()) {
                variableDescription += ".";
            }
            if (!variableTuple.isVirtual()) {
                variableDescription += variableTuple.getLabel();
            }
//            variablesExistingInView.add(v);
            stack.push(XQUtility.replaceDotsWithSlashes(variableDescription));
//        }
        v.getBindingPathExpression().accept(this);
    }

    private String findSingleAttributeUnderVirtualTuple(SetNode setNode) {
        INode child = setNode.getChild(0);
        if (child instanceof TupleNode && child.isVirtual() && child.getChildren().size() == 1) {
            INode descendant = child.getChild(0);
            if (descendant instanceof AttributeNode) {
                return descendant.getLabel();
            }
        }
        return null;
    }

    public void visitPathExpression(VariablePathExpression pathExpression) {
        SetAlias startingVariable = pathExpression.getStartingVariable();
        if (startingVariable == null) {
            generateResult();
        } else {
            startingVariable.accept(this);
        }
    }

    private void generateResult() {
        pathDescription += "";
        while (!stack.isEmpty()) {
            if (!pathDescription.equals("")) {
                pathDescription += ", ";
            }
            pathDescription += stack.pop();
        }
        pathDescription += "";
    }

    public String getResult() {
        return pathDescription;
    }
}