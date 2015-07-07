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

import it.unibas.spicy.structuralanalysis.circuits.strategies.ICircuitVisitor;
import java.util.ArrayList;
import java.util.List;

public class CircuitNode {
    
    public final static int GROUND = -2;
    public final static int TOP = -1;
    public final static int GENERATOR_SOURCE = -3;
    
    private int id;
    private List<ICircuitElement> inboundElements = new ArrayList<ICircuitElement>();
    private List<ICircuitElement> outboundElements = new ArrayList<ICircuitElement>();
    
    public CircuitNode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isTop() {
        return this.id == TOP;
    }
    
    public boolean isGround() {
        return this.id == GROUND;
    }

    public void addInboundElement(ICircuitElement element) {
        this.inboundElements.add(element);        
    }
    
    public void addOutboundElement(ICircuitElement element) {
        this.outboundElements.add(element);        
    }
    
    public List<ICircuitElement> getInboundElements() {
        return inboundElements;
    }

    public List<ICircuitElement> getOutboundElements() {
        return outboundElements;
    }

    public boolean isSourceNodeForGenerator() {
        if (outboundElements == null) {
            return false;
        }
        if (outboundElements.size() != 1) {
            return false;
        }
        ICircuitElement elementoUscente = outboundElements.get(0);
        if (!(elementoUscente instanceof Generator)) {
            return false;
        }
        return true;
    }
    
    public void accept(ICircuitVisitor visitor) {
        visitor.visitNode(this);
    }

    public String toString() {
        if (this.id == GROUND) {
            return "GROUND";
        } else if (this.id == TOP) {
            return "TOP";
        } 
        return "Node " + this.id;
    }

    public String toLongString() {
        String result = this.toString() + "\n";
        result += "Inbound elements: \n";
        for (ICircuitElement element : inboundElements) {
            result += element + "\n";
        }
        result += "Outbound elements: \n";
        for (ICircuitElement element : outboundElements) {
            result += element + "\n";
        }
        return result;
    }
}
