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
 
package it.unibas.spicy.model.datasource.nodes;

import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import java.util.ArrayList;
import java.util.List;

public class TupleNode extends IntermediateNode {

    private List<String> provenance = new ArrayList<String>();

    public TupleNode(String label) {
        super(label);
    }

    public TupleNode(String label, Object value) {
        super(label, value);
    }

    public List<String> getProvenance() {
        return provenance;
    }

    public void addProvenance(String tgdId) {
        if (!provenance.contains(tgdId)) {
            provenance.add(tgdId);
        }
    }

    public void addProvenanceList(List<String> newProvenance) {
        for (String tgdId : newProvenance) {
            if (!provenance.contains(tgdId)) {
                provenance.add(tgdId);
            }
        }
    }

    public void accept(INodeVisitor visitor) {
        visitor.visitTupleNode(this);
    }
}
