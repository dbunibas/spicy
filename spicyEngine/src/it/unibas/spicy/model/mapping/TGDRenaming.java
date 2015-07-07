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
 
package it.unibas.spicy.model.mapping;

public class TGDRenaming {

    private FORule renamedTgd;
    private FORule originalTgd;
    private QueryRenaming renamedSourceView;
    private QueryRenaming renamedTargetView;

    public TGDRenaming(FORule renamedTgd, FORule originalTgd, QueryRenaming renamedSourceView, QueryRenaming renamedTargetView) {
        this.renamedTgd = renamedTgd;
        this.originalTgd = originalTgd;
        this.renamedSourceView = renamedSourceView;
        this.renamedTargetView = renamedTargetView;
    }

    public FORule getOriginalTgd() {
        return originalTgd;
    }

    public FORule getRenamedTgd() {
        return renamedTgd;
    }

    public QueryRenaming getRenamedSourceView() {
        return renamedSourceView;
    }

    public QueryRenaming getRenamedTargetView() {
        return renamedTargetView;
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-----------------  TGD Renaming -------------------\n");
        result.append("Original tgd:\n").append(originalTgd);
        result.append("Renamed tgd:\n").append(renamedTgd);
        result.append("---------------------------------------------------\n");
        return result.toString();
    }

}