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
 
package it.unibas.spicy.structuralanalysis.sampling;

import it.unibas.spicy.model.datasource.values.NullValueFactory;

public class SampleValue {
    
    private Object value;
    
    public SampleValue() {}
    
    public SampleValue(Object valore) {
        this.value = valore;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
            
    //TODO: attenzione, abbiamo cambiato getLength togliendo getBytes
    public int getLength() {
        if (value instanceof NullValueFactory){
            return 0;
        }
        return getValueString().length();
    }
            
    public String getValueString() {
        return this.value.toString().trim();
    }
            
    public String toString() {
        return this.getValueString();
    }

}
