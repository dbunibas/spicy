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
 
package it.unibas.spicy.parser;

import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserJoinGraph {

    private Map<ParserAtom, List<ParserJoinEdge>> successorMap = new HashMap<ParserAtom, List<ParserJoinEdge>>();

    public Map<ParserAtom, List<ParserJoinEdge>> getSuccessors() {
        return successorMap;
    }

    public void addNode(ParserAtom atom) {
        if (!successorMap.keySet().contains(atom)) {
            successorMap.put(atom, new ArrayList<ParserJoinEdge>());
        }
    }

    public void addSuccessor(ParserAtom firstAtom, ParserJoinEdge joinEdge) {
        List<ParserJoinEdge> successors = successorMap.get(firstAtom);
        successors.add(joinEdge);
    }

    public String toString() {
        return "-----------------------Parser Join graph: -----------------------------\n" + SpicyEngineUtility.printMap(successorMap);
    }

}
