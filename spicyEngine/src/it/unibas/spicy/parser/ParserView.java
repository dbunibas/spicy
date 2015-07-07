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

import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.SelectionCondition;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParserView implements Cloneable {

    private static Log logger = LogFactory.getLog(ParserView.class);
    private List<ParserView> subViews = new ArrayList<ParserView>();
    private List<ParserAtom> atoms = new ArrayList<ParserAtom>();
    private List<ParserBuiltinFunction> functions = new ArrayList<ParserBuiltinFunction>();
    private List<ParserBuiltinOperator> operators = new ArrayList<ParserBuiltinOperator>();
    private List<JoinCondition> joinConditions = new ArrayList<JoinCondition>();
    private List<SelectionCondition> selectionConditions = new ArrayList<SelectionCondition>();
    private List<ParserEquality> equalities = new ArrayList<ParserEquality>();

    public void addJoinCondition(JoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public List<JoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addSelectionCondition(SelectionCondition selectionCondition) {
        this.selectionConditions.add(selectionCondition);
    }

    public List<SelectionCondition> getSelectionConditions() {
        return selectionConditions;
    }

    public List<ParserAtom> getAtoms() {
        return atoms;
    }

    public void addAtom(ParserAtom atom) {
        this.atoms.add(atom);
    }

    public List<ParserView> getSubViews() {
        return subViews;
    }

    public List<ParserBuiltinFunction> getFunctions() {
        return functions;
    }

    public List<ParserBuiltinOperator> getOperators() {
        return operators;
    }

    public void addSubView(ParserView subView) {
        this.subViews.add(subView);
    }

    public void addFunction(ParserBuiltinFunction function) {
        this.functions.add(function);
    }

    public void addOperator(ParserBuiltinOperator operator) {
        this.operators.add(operator);
    }

    public void addEquality(ParserEquality equality) {
        if (logger.isTraceEnabled()) logger.trace("Add equality " + equality + " to view \n" + this.toString() + " -------------- ");
        this.equalities.add(equality);
    }

    public List<ParserEquality> getEqualities() {
        return equalities;
    }

    public ParserView clone() {
        try {
            ParserView clone = (ParserView) super.clone();
            clone.atoms = new ArrayList<ParserAtom>(this.atoms);
            clone.joinConditions = new ArrayList<JoinCondition>(this.joinConditions);
            clone.subViews = new ArrayList<ParserView>(this.subViews);
            clone.functions = new ArrayList<ParserBuiltinFunction>(this.functions);
            clone.operators = new ArrayList<ParserBuiltinOperator>(this.operators);
            clone.selectionConditions = new ArrayList<SelectionCondition>(this.selectionConditions);
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ParserAtom atom : atoms) {
            result.append(atom).append("\n");
        }
        for (ParserBuiltinFunction function : functions) {
            result.append(function).append("\n");
        }
        for (ParserBuiltinOperator operator : operators) {
            result.append(operator).append("\n");
        }
        if (!equalities.isEmpty()) {
            result.append(" [ ");
            for (int i = 0; i < equalities.size(); i++) {
                result.append(equalities.get(i));
                if (i != equalities.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(" ] ");
        }
        return result.toString();
    }
}
