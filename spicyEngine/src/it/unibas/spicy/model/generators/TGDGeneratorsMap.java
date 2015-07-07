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
package it.unibas.spicy.model.generators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TGDGeneratorsMap {

    private Map<SetAlias, Map<PathExpression, IValueGenerator>> generators = new HashMap<SetAlias, Map<PathExpression, IValueGenerator>>();

    public Collection<Map<PathExpression, IValueGenerator>> getMaps() {
        return generators.values();
    }

    public IValueGenerator getGeneratorForLeaf(VariablePathExpression path, INode schema) {
        for (SetAlias alias : generators.keySet()) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(alias.getAttributes(schema), path))  {
                Map<PathExpression, IValueGenerator> generatorsForPathVariable = getGeneratorsForVariable(alias);
                PathExpression attributePath = path.getAbsolutePath();
                attributePath.getPathSteps().add(SpicyEngineConstants.LEAF);
                IValueGenerator generatorForLeaf = generatorsForPathVariable.get(attributePath);
                return generatorForLeaf;
            }
        }
        return null;
//        Map<PathExpression, IValueGenerator> generatorsForPathVariable = getGeneratorsForVariable(path.getStartingVariable());
//        if (generatorsForPathVariable == null) {
//            return null;
//        }
//        PathExpression attributePath = path.getAbsolutePath();
//        attributePath.getPathSteps().add(SpicyEngineConstants.LEAF);
//        IValueGenerator generatorForLeaf = generatorsForPathVariable.get(attributePath);
//        return generatorForLeaf;
    }

    public IValueGenerator getGeneratorForSetPath(VariablePathExpression setPath) {
        SetAlias startingVariable = setPath.getStartingVariable();
        if (startingVariable == null) {
            return null;
        }
        Map<PathExpression, IValueGenerator> generatorsForPathVariable = getGeneratorsForFatherVariable(setPath.getStartingVariable());
        if (generatorsForPathVariable == null) {
            throw new IllegalArgumentException("Unable to find generator for set path: " + setPath + " in\n" + this);
        }
        PathExpression attributePath = setPath.getAbsolutePath();
        IValueGenerator generator = generatorsForPathVariable.get(attributePath);
        return generator;
    }

    public Map<PathExpression, IValueGenerator> getGeneratorsForVariable(SetAlias variable) {
        return generators.get(variable);
    }

    public Map<PathExpression, IValueGenerator> getGeneratorsForFatherVariable(SetAlias variable) {
        for (SetAlias targetVariable : generators.keySet()) {
            if (targetVariable.getGenerators().contains(variable)) {
                return generators.get(targetVariable);
            }
        }
        return null;
    }

    public void addGeneratorsForVariable(SetAlias variable, Map<PathExpression, IValueGenerator> variableGenerators) {
        generators.put(variable, variableGenerators);
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append("--------TGD Generators--------\n");
        for (SetAlias variable : generators.keySet()) {
            Map<PathExpression, IValueGenerator> variableGenerators = generators.get(variable);
            result.append("--------------Variable: ").append(variable.toShortString()).append("-------------\n");
            result.append(notNullGeneratorsString(variableGenerators, indent));
        }
        return result.toString();
    }

    private String notNullGeneratorsString(Map<PathExpression, IValueGenerator> variableGenerators, String indent) {
        List<PathExpression> nodePaths = new ArrayList<PathExpression>(variableGenerators.keySet());
        Collections.sort(nodePaths);
        StringBuilder generatorString = new StringBuilder();
        for (PathExpression attributePath : nodePaths) {
            IValueGenerator generator = variableGenerators.get(attributePath);
            if (!(generator instanceof NullValueGenerator)) {
//                generatorString.append("[").append(attributePath).append(" -> ").append(generator).append("], ");
                generatorString.append(indent).append("|-").append(attributePath).append(" -> ").append(generator).append("\n");
            }
        }
        return generatorString.toString();
    }
}
