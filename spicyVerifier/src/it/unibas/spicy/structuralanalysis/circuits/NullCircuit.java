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

import java.util.Collections;
import java.util.List;

public class NullCircuit extends Circuit {
    
    private static NullCircuit singleton = new NullCircuit();
    
    public static NullCircuit getInstance() {
        return singleton;
    }    
    
    private NullCircuit() {}
    
    public void addElement(ICircuitElement element){}
    
    public List<ICircuitElement> getElements() {
        return Collections.EMPTY_LIST;
    }
    
    public void addNode(CircuitNode node) {}
    
    public List<CircuitNode> getNodes() {
        return Collections.EMPTY_LIST;
    }
    
    public double[] getCurrents() {
        return new double[0];
    }
    
    public void setCurrents(double[] currents) {}
    
    public void addAnnotation(String key, Object value) {}
    
    public Object getAnnotation(String key) {
        return null;
    }
    
}
