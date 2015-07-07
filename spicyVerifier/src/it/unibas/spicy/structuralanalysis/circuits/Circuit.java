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

import it.unibas.spicy.structuralanalysis.circuits.operators.BuildCurrentMap;
import it.unibas.spicy.structuralanalysis.circuits.strategies.ICircuitVisitor;
import it.unibas.spicy.utility.SpicyUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Circuit {
    
    private static Log logger = LogFactory.getLog(Circuit.class);
    
    private CircuitNode topNode;
    private CircuitNode groundNode;
    private List<ICircuitElement> elements = new ArrayList<ICircuitElement>();
    private List<CircuitNode> nodes;
    private double[][] A;
    private double[] B;
    private double[] currents;
    private Map<String, Object> annotations = new HashMap<String, Object>();
    private Map<String, Double> currentMap = new HashMap<String, Double>();
    private BuildCurrentMap mapBuilder = new BuildCurrentMap();
    
    public Circuit() {
        this.topNode = new CircuitNode(CircuitNode.TOP);
        this.groundNode = new CircuitNode(CircuitNode.GROUND);
        this.nodes = new ArrayList<CircuitNode>();
        this.nodes.add(topNode);
        this.nodes.add(groundNode);
    }
    
    public CircuitNode getTopNode() {
        return topNode;
    }
    
    public CircuitNode getGroundNode() {
        return groundNode;
    }
    
    public void addElement(ICircuitElement element){
        this.elements.add(element);
        this.addNode(element.getSourceNode());
        this.addNode(element.getTargetNode());
    }
    
    public List<ICircuitElement> getElements() {
        return this.elements;
    }
    
    public void addNode(CircuitNode node) {
        if (!nodes.contains(node)) {
            this.nodes.add(node);
        }
    }
    
    public List<CircuitNode> getNodes() {
        return this.nodes;
    }
    
    public double[][] getA() {
        return A;
    }

    public void setA(double[][] A) {
        this.A = A;
    }

    public double[] getB() {
        return B;
    }

    public void setB(double[] B) {
        this.B = B;
    }

    public double[] getCurrents() {
        return currents;
    }
    
    public void setCurrents(double[] currents) {
        this.currents = currents;
        this.currentMap = mapBuilder.buildCurrentMap(this);
    }

    public Map<String, Double> getCurrentMap() {
        return currentMap;
    }
    
    public void addAnnotation(String key, Object value) {
        this.getAnnotations().put(key, value);
    }
    
    public Object getAnnotation(String key) {
        return this.getAnnotations().get(key);
    }

    public void accept(ICircuitVisitor visitor) {
        visitor.visitCircuit(this);
    }
    
    public String toString() {
        String result = "------------------CIRCUIT----------------------\n";
        result += "Elements: " + elements.size() + " - Nodes: " + nodes.size() + "\n";
        result += "----------ELEMENTS:\n";
        for(ICircuitElement element : elements){
            result += element + "\n";
        }
        if (currents != null) {
            result += "CURRENTS:\n";
            result += SpicyUtility.printDoubleArray(currents);
        }
        result += "--------------------------------------------\n";
        return result;
    }

    public String toLongString() {
        String result = toString();
        result += "----------NODES:\n";
        for(CircuitNode node : nodes){
            result += node.toLongString();
        }
        result += currentMap + "\n";
        if (A != null) {
            result += "A:\n";
            result += SpicyUtility.printDoubleMatrix(A);
        }        
        if (B != null) {
            result += "B:\n";
            result += SpicyUtility.printDoubleArray(B);
        }
        if (currents != null) {
            result += "CURRENTS:\n";
            result += SpicyUtility.printDoubleArray(currents);
        }
        result += "--------------------------------------------\n";
        return result;
    }

    public Map<String, Object> getAnnotations() {
        return annotations;
    }
}
