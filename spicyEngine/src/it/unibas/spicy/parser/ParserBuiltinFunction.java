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

import java.util.ArrayList;
import java.util.List;

public class ParserBuiltinFunction implements Cloneable {

    private static final Function[] supportedFunctions = {new Function("isNull", 1),
                                                          new Function("isNotNull", 1)};
    private String function;
    private List<ParserArgument> arguments = new ArrayList<ParserArgument>();

    public ParserBuiltinFunction(String function, List<ParserArgument> arguments) {
        checkFunction(function, arguments.size());
        this.function = function;
        this.arguments = arguments;
    }

    public List<ParserArgument> getArguments() {
        return arguments;
    }

    public String getFunction() {
        return function;
    }

    public ParserBuiltinFunction clone() {
        try {
            ParserBuiltinFunction clone = (ParserBuiltinFunction) super.clone();
            clone.arguments = new ArrayList<ParserArgument>();
            for (ParserArgument argument : arguments) {
                clone.arguments.add(argument.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(function).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            result.append(arguments.get(i));
            if (i != arguments.size() - 1) {
                result.append(", ");
            }
        }
        result.append(") ");
        return result.toString();
    }

    public String toExpressionString() {
        StringBuilder result = new StringBuilder();
        result.append(function).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).isVariable()) {
                result.append(arguments.get(i).getAttributePath());
            } else {
                result.append(arguments.get(i));
            }
            if (i != arguments.size() - 1) {
                result.append(", ");
            }
        }
        result.append(") ");
        return result.toString();
    }

    private static void checkFunction(String function, int arguments) {
        for (Function supportedFunction : supportedFunctions) {
            if (supportedFunction.getName().equals(function)) {
                if (supportedFunction.getArguments() == arguments) {
                    return;
                }
            }
        }
        throw new ParserException("Function " + function + " with " + arguments + " arguments is not supported");
    }
}

class Function {

    private String name;
    private int arguments;

    public Function(String name, int arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public int getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }
}
