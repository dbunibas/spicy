/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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
package it.unibas.spicy.model.generators;

import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.MappingTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppendSkolemPart implements ISkolemPart {

    private boolean sort;
    private boolean sql = false;
    private String leftDelimiter;
    private String rightDelimiter;
    private String oddSeparator;
    private String evenSeparator;
    private List<ISkolemPart> children = new ArrayList<ISkolemPart>();

    public AppendSkolemPart(boolean sort) {
        this.sort = sort;
    }

    public AppendSkolemPart(boolean sort, boolean sql) {
        this.sort = sort;
    }

    public AppendSkolemPart() {
        this(false);
    }

    public AppendSkolemPart(boolean sort, String leftDelimiter, String rightDelimiter, String oddSeparator, String evenSeparator) {
        this(sort);
        this.leftDelimiter = leftDelimiter;
        this.rightDelimiter = rightDelimiter;
        this.oddSeparator = oddSeparator;
        this.evenSeparator = evenSeparator;
    }

    public AppendSkolemPart(boolean sort, String leftDelimiter, String rightDelimiter, String separator) {
        this(sort, leftDelimiter, rightDelimiter, separator, separator);
    }

    public AppendSkolemPart(boolean sort, boolean sql, String leftDelimiter, String rightDelimiter, String oddSeparator, String evenSeparator) {
        this(sort);
        this.sql = sql;
        this.leftDelimiter = leftDelimiter;
        this.rightDelimiter = rightDelimiter;
        this.oddSeparator = oddSeparator;
        this.evenSeparator = evenSeparator;
    }

    public AppendSkolemPart(boolean sort, boolean sql, String leftDelimiter, String rightDelimiter, String separator) {
        this(sort, sql, leftDelimiter, rightDelimiter, separator, separator);
    }

    public String getValue(TupleNode sourceTuple, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        List<String> values = new ArrayList<String>();
        for (ISkolemPart child : children) {
            values.add(child.getValue(sourceTuple, mappingTask));
        }
        if (sort) {
            Collections.sort(values);
        }
        result.append(printString(leftDelimiter));
        for (int i = 0; i < values.size(); i++) {
            result.append(values.get(i));
            if (i != values.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        return result.toString();
    }

    private String printString(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    private String getSeparator(int i) {
        if (i % 2 == 0) {
            return printString(oddSeparator);
        } else {
            return printString(evenSeparator);
        }
    }

    public List<ISkolemPart> getChildren() {
        return this.children;
    }

    public void addChild(ISkolemPart child) {
        this.children.add(child);
    }

    public String toString() {
        if (sql) {
            return toStringSQL();
        }
        StringBuilder result = new StringBuilder();
        if (sort && children.size() > 1) {
            result.append("cast(sort(array[");
        }
        result.append(printString(leftDelimiter));
        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i));
            if (i != children.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        if (sort && children.size() > 1) {
            result.append("]) as text)");
        }
        return result.toString();
    }

    public String toStringWithVariableArguments(List<FormulaVariable> universalVariables) {
        StringBuilder result = new StringBuilder();
        if (sort && children.size() > 1) {
            result.append("sort(");
        }
        result.append(printString(leftDelimiter));
        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).toStringWithVariableArguments(universalVariables));
            if (i != children.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        if (sort && children.size() > 1) {
            result.append(")");
        }
        return result.toString();
    }

    private String toStringSQL() {
        StringBuilder result = new StringBuilder();
        if (sort && children.size() > 1) {
            result.append("cast(sort(array[");
        }
        result.append(printString(leftDelimiter));
        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i));
            if (i != children.size() - 1) {
                result.append(getSeparator(i));
            }
        }
        result.append(printString(rightDelimiter));
        if (sort && children.size() > 1) {
            result.append("]) as text)");
        }
        return result.toString();
    }
}
