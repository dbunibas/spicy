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

import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;

public class ParserAtom implements Cloneable {

    private String name;
    private List<ParserAtom> rightTargetTGDatoms = new ArrayList<ParserAtom>();
    private List<ParserAttribute> attributes = new ArrayList<ParserAttribute>();
    private List<SelectionCondition> selectionConditions = new ArrayList<SelectionCondition>();
    private PathExpression absolutePath;

    public ParserAtom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSelectionCondition(SelectionCondition selectionCondition) {
        this.selectionConditions.add(selectionCondition);
    }

    public List<SelectionCondition> getSelectionConditions() {
        return selectionConditions;
    }

    public void addRightTargetTGDatom(ParserAtom atom) {
        rightTargetTGDatoms.add(atom);
    }

    public List<ParserAtom> getRightTargetTGDatoms() {
        return rightTargetTGDatoms;
    }

    public boolean addAttribute(ParserAttribute e) {
        return attributes.add(e);
    }

    public List<ParserAttribute> getAttributes() {
        return attributes;
    }

    public PathExpression getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(PathExpression absolutePath) {
        this.absolutePath = absolutePath;
    }

    public ParserAtom clone() {
        try {
            ParserAtom clone = (ParserAtom) super.clone();
            clone.attributes = new ArrayList<ParserAttribute>();
            for (ParserAttribute attribute : attributes) {
                clone.attributes.add(attribute.clone());
            }
            clone.selectionConditions = new ArrayList<SelectionCondition>(this.selectionConditions);
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name).append("(");
        for (int i = 0; i < attributes.size(); i++) {
            result.append(attributes.get(i));
            if (i != attributes.size() - 1) {
                result.append(", ");
            }
        }
        result.append(") ");
        for (SelectionCondition selectionCondition : selectionConditions) {
            result.append(selectionCondition).append("\n");
        }
        if (absolutePath != null) {
            result.append("Path: ").append(absolutePath);
        }
        return result.toString();
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append(name).append("(");
        for (int i = 0; i < attributes.size(); i++) {
            result.append(attributes.get(i));
            if (i != attributes.size() - 1) {
                result.append(", ");
            }
        }
        result.append(")");
        return result.toString();
    }
}
