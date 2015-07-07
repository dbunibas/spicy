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
 
package it.unibas.spicy.findmappings;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import java.util.List;

public class CandidateCorrespondences {
    
    private List<ValueCorrespondence> correspondences;
    
    public CandidateCorrespondences(List<ValueCorrespondence> correspondences) {
        this.correspondences = correspondences;
    }
    
    public List<ValueCorrespondence> getCorrespondences() {
        return correspondences;
    }
    
    public String toString() {
        return printListOfCorrespondences(correspondences);
    }
    
    public int hashCode() {
        int code = 1;
        for (ValueCorrespondence correspondence : correspondences) {
            code = 31 * code + correspondence.hashCode();
        }
        return code;
    }
    
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CandidateCorrespondences)) {
            return false;
        }
        CandidateCorrespondences other = (CandidateCorrespondences)obj;
        if (this.correspondences.size() != other.correspondences.size()) {
            return false;
        }
        return checkCorrespondences(this.correspondences, other.correspondences);
    }

    private boolean checkCorrespondences(List<ValueCorrespondence> thisCorrespondences, List<ValueCorrespondence> otherCorrespondences) {
        for (ValueCorrespondence thisCorrespondence : thisCorrespondences) {
            if (!existsMatchingCorrespondence(thisCorrespondence, otherCorrespondences)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean existsMatchingCorrespondence(ValueCorrespondence thisCorrespondence, List<ValueCorrespondence> otherCorrespondences) {
        for (ValueCorrespondence otherCorrespondence : otherCorrespondences) {
            if (thisCorrespondence.hasEqualPaths(otherCorrespondence)) {
                return true;
            }
        }
        return false;
    }

    public static String printListOfCorrespondences(List<ValueCorrespondence> correspondences) {
        String result = "---------------------------------------------------------------------\n";
        for (ValueCorrespondence correspondence : correspondences) {
            result += correspondence + "\n";
        }
        result += "---------------------------------------------------------------------------\n";
        return result;
    }
    
    public static String printListOfListsOfCorrespondences(List<List<ValueCorrespondence>> correspondences) {
        String result = "---------------------------------------------------------------------\n";
        for (List<ValueCorrespondence> correspondenceList : correspondences) {
            result += printListOfCorrespondences(correspondenceList);
        }
        result += "---------------------------------------------------------------------------\n";
        return result;
    }
    
}
