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

import it.unibas.spicy.utility.SpicyUtility;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Distribution {
    
    protected Log logger = LogFactory.getLog(this.getClass());
    
    protected Map<Object, Integer> values = new HashMap<Object, Integer>();
    
    public Collection<Object> getKeys() {
        return this.values.keySet();
    }
    
    public void addValue(Object chiave, int valore) {
        Integer vecchioValore = this.values.get(chiave);
        if (vecchioValore == null) {
            this.values.put(chiave, valore);
        } else {
            this.values.put(chiave, vecchioValore + valore);
        }
    }
    
    public int getValue(int idChiave) {
        Integer valore = this.values.get(new CharacterCategory(idChiave));
        if (valore == null) {
            return 0;
        }
        return valore;
    }

    public int getValue(Object chiave) {
        Integer valore = this.values.get(chiave);
        if (valore == null) {
            return 0;
        }
        return valore;
    }
    
    public void add(Distribution distribuzione) {
        for (Object chiave : distribuzione.getKeys()) {
            this.addValue(chiave, distribuzione.getValue(chiave));
        }
    }
            
    public int getSize() {
        return this.values.values().size();
    }
        
    public int getSumOfValues() {
        int totale = 0;
        for (Integer valore : this.values.values()) {
            totale += valore;
        }
        return totale;
    }
    
    public double getSumOfValues(CharacterCategory[] categorieUsate) {
        int totale = 0;
        for (CharacterCategory categoria : categorieUsate) {
            Integer valore = this.values.get(categoria);
            if(valore == null){
                logger.error("Non vi sono valori per la categoria " + categoria.getName());
                continue;
            }
            totale += valore;
        }
        return totale;
    }

    public double getShannonWienerIndex() {
        double coefficiente = 0;
        double sommaValori = this.getSumOfValues();
        int numeroChiavi = this.values.size();
        if (numeroChiavi == 1) {
            return 0;
        }
        for (Object chiave : this.values.keySet()) {
            double valore = this.values.get(chiave) / sommaValori;
            if (valore != 0) {
                coefficiente += valore * SpicyUtility.log2(valore);
            }
        }
        return - coefficiente / SpicyUtility.log2(numeroChiavi);
    }


    public double getSimpsonConcentrationIndex() {
        double indice = 0;
        double sommaValori = this.getSumOfValues();
        for (Object chiave : this.values.keySet()) {
            double valore = this.values.get(chiave) / sommaValori;
            indice += valore * valore;
        }
        return indice;
    }

    public String toString(){
        String result = "";
        result += values;
        result += " - H = " + this.getShannonWienerIndex();
        result += " - IC = " + this.getSimpsonConcentrationIndex();
        return result;
    }

}
