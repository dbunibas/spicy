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

public class IntegerKeyDistribution extends Distribution {
    
    public double getMedia() {
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        int somma = 0;
        for (Object key : this.values.keySet()) {
            int chiave = (Integer)key;
            int valore = this.values.get(chiave);
            somma +=  (chiave * valore);
        }
        double media = ((double) somma) / super.getSumOfValues();
        if (logger.isDebugEnabled()) logger.debug("SOMMA FINALE = "+ somma);
        if (logger.isDebugEnabled()) logger.debug("NUMERO VALORI = " + super.getSumOfValues() );
        if (logger.isDebugEnabled()) logger.debug("MEDIA = " + media );
        return media;
    }
    
    public double getCoefficienteDiVariazione(){
        double devStandard =  getDeviazioneStandard();
        double media = getMedia();
        return ( devStandard / media );
    }
    
    public double getDeviazioneStandard(){
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        double media = this.getMedia();
        double numeroCampioni = this.getSumOfValues();
        double sommaScarti = 0;
        for (Object key : this.values.keySet()) {
            Integer chiave = (Integer)key;
            Integer valore = this.values.get(chiave);
            double scartoQuadratico = Math.pow(Math.abs(chiave - media), 2) * valore;
            if(scartoQuadratico == 0){
                logger.debug("**********scartoQuadratico = 0");
            }
            sommaScarti += scartoQuadratico;
        }
        if (logger.isDebugEnabled()) logger.debug("sommaScarti = " + sommaScarti);
        if (logger.isDebugEnabled()) logger.debug("numeroCampioni = " + numeroCampioni);
        double risultato = Math.sqrt(sommaScarti / (numeroCampioni -1));
        if (logger.isDebugEnabled()) logger.debug("Risultato = "  + risultato);
        return risultato;
    }
    
    public double getMediaDevSt(double fattore) {
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        double media = this.getMedia();
        double devSt = this.getDeviazioneStandard();
        int numCampioni = 0;
        Integer somma = 0;
        for (Object key : this.values.keySet()) {
            Integer chiave = (Integer)key;
            Integer valore = this.values.get(chiave);
            if (logger.isDebugEnabled()) logger.debug("CHIAVE = " + chiave +" -- VALORE = " + valore + " -- MEDIA = " + media+" -- DEVST = "+devSt + " -- FATTORE = " + fattore );
            if(chiave >= (media - devSt * fattore)  && chiave <= (media + devSt * fattore) ){
                somma +=  (chiave * valore);
                numCampioni += valore;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("SOMMA FINALE = "+ somma);
        if (logger.isDebugEnabled()) logger.debug("NUMERO CAMPIONI = " + numCampioni );
        if(somma == 0 && numCampioni == 0){
            return getMedia();
        }
        double risultato = (double)somma / numCampioni;
        if (logger.isDebugEnabled()) logger.debug("RISULTATO = " + risultato);
        return risultato;
    }
    
    public double getMassimo() {
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        int massimo = 0;
        for (Object key : this.values.keySet()) {
            int chiave = (Integer)key;
            if (chiave > massimo) {
                massimo = chiave;
            }
        }
        return (double)massimo;
    }
    
    public double getMinimo() {
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        int minimo = 1000;
        for (Object key : this.values.keySet()) {
            int chiave = (Integer)key;
            if (chiave < minimo) {
                minimo = chiave;
            }
        }
        return (double)minimo;
    }
    
    public double getModa() {
        if (this.values.size() == 0) {
            throw new IllegalArgumentException("Distribuzione vuota. Impossibile calcolare la media");
        }
        Integer chiaveMassimo = null;
        int massimo = 0;
        for (Object key : this.values.keySet()) {
            int chiave = (Integer)key;
            int valore = this.values.get(chiave);
            if (valore > massimo) {
                massimo = valore;
                chiaveMassimo = chiave;
            }
        }
        return chiaveMassimo;
    }
    
    public String toString(){
        String result = super.toString();
        result += " - AVG= " + this.getMedia();
        result += " - DEVSTD= " + this.getDeviazioneStandard();
        return result;
    }

    
}
