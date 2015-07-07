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

public class CharacterCategory implements Comparable<CharacterCategory> {
    
    private int id;
    private String name;
    
    public CharacterCategory() {}
    
    public CharacterCategory(int id) {
        this.id = id;
    }

    public CharacterCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
        
    public int getId() {
        return id;
    }
        
    public String toString() {
        return this.name;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof CharacterCategory)) {
            return false;
        }
        return (this.id == ((CharacterCategory)obj).id);
    }
    
    public int hashCode() {
        return (this.id + "").hashCode();
    }
    
    public int compareTo(CharacterCategory otherCategory) {
        return (this.id - otherCategory.id);
    }
     
}
