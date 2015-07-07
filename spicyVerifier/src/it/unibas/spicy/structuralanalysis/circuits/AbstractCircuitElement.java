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
 
package it.unibas.spicy.structuralanalysis.circuits;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;

public abstract class AbstractCircuitElement implements ICircuitElement {
    
    private int id;
    private String description;
    private CircuitNode sourceNode;
    private CircuitNode targetNode;
    private INode schemaNode;
    private double value;
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
        
    public AbstractCircuitElement(int id, String description, double value, INode schemaNode, CircuitNode sourceNode, CircuitNode targetNode){
        this.id = id;
        this.description = description;
        this.value = value;
        this.schemaNode = schemaNode;
        this.sourceNode = sourceNode;
        this.sourceNode.addOutboundElement(this);
        this.targetNode = targetNode;
        this.targetNode.addInboundElement(this);
    }
        
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public CircuitNode getSourceNode() {
        return sourceNode;
    }

    public CircuitNode getTargetNode() {
        return targetNode;
    }
   
    public String getKey() {
        String nodeDescription = "NULL";
        if (this.schemaNode != null) {
            nodeDescription = pathGenerator.generatePathFromRoot(schemaNode).toString();
        }
        return nodeDescription + this.description;
    }
    
    public String toString(){
        String attributeName = "null";
        if (this.schemaNode != null) {
//            GeneratePathFromNode pathGenerator = new GeneratePathFromNode();
            PathExpression pathToNode = pathGenerator.generatePathFromRoot(schemaNode);
            attributeName = pathToNode.toString();
        }
        return this.getType() + "-" + this.id + " - " + this.description + " - Value: "+ this.value + " Schema element: " + attributeName + " (" + this.sourceNode + ", " + this.targetNode + ")";
    }

}
