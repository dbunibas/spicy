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

import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.model.generators.operators.GenerateSkolemFunctions;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SkolemFunctionGenerator extends AbstractGenerator {

    public static final int STANDARD = 0;
    public static final int EGD_BASED = 1;
    public static final int INTERMEDIATE = 2;
    public static final int KEY = 3;
    private String name;
    private int type;
    private boolean leafGenerator;
    private FORule tgd;
    private List<VariableJoinCondition> joinConditions = new ArrayList<VariableJoinCondition>();
    private List<VariableFunctionalDependency> functionalDependencies = new ArrayList<VariableFunctionalDependency>();
    private List<GeneratorWithPath> subGenerators = new ArrayList<GeneratorWithPath>();
    private Integer position;
    private ISkolemPart skolemFunction;

    public SkolemFunctionGenerator(String name, boolean leafGenerator) {
        this.name = name;
        this.leafGenerator = leafGenerator;
    }

    public SkolemFunctionGenerator(String name, boolean leafGenerator, FORule tgd, List<GeneratorWithPath> subGenerators) {
        this(name, leafGenerator);
        this.tgd = tgd;
        for (GeneratorWithPath generator : subGenerators) {
            this.subGenerators.add(generator);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLeafGenerator() {
        return leafGenerator;
    }

    public String getName() {
        return this.name;
    }

    public List<VariableJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addJoinCondition(VariableJoinCondition joinCondition) {
        this.joinConditions.add(joinCondition);
    }

    public List<VariableFunctionalDependency> getFunctionalDependencies() {
        return functionalDependencies;
    }

    public void addFunctionalDependencies(VariableFunctionalDependency dependency) {
        this.functionalDependencies.add(dependency);
    }

    public void setFunctionalDependencies(List<VariableFunctionalDependency> functionalDependencies) {
        this.functionalDependencies = functionalDependencies;
    }

    public List<GeneratorWithPath> getSubGenerators() {
        Collections.sort(subGenerators, new GeneratorComparator());
        return subGenerators;
    }

    public FORule getTgd() {
        return tgd;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Object generateValue(TupleNode sourceTuple, MappingTask mappingTask) {
        String skolemString = this.getSkolemFunction(mappingTask).getValue(sourceTuple, mappingTask);
        IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
        if (mappingTask.getConfig().useSkolemStrings()) {
            // TO HAVE FUNCTOR IN OID
            skolemString = "N" + oidGenerator.generateOIDForSkolemString(skolemString) + "[" + skolemString + "]";
            return new OID(skolemString);
        } else {
            // TO REMOVE FUNCTOR FROM OID
            return oidGenerator.generateOIDForSkolemString(skolemString);
        }
    }

    public ISkolemPart getSkolemFunction(MappingTask mappingTask) {
        if (this.skolemFunction == null) {
            GenerateSkolemFunctions generator = new GenerateSkolemFunctions();
            this.skolemFunction = generator.generateSkolemFunction(this, mappingTask);
        }
        return this.skolemFunction;
    }

    public void setVariableDescriptions(List<FormulaVariable> formulaVariables) {
        if (this.variableDescriptionsSet) {
            return;
        }
        for (GeneratorWithPath subGenerator : this.subGenerators) {
            subGenerator.getGenerator().setVariableDescriptions(formulaVariables);
        }
        this.variableDescriptionsSet = true;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SkolemFunctionGenerator)) {
            return false;
        }
        SkolemFunctionGenerator otherFunction = (SkolemFunctionGenerator) obj;
        if (!(this.name.equals(otherFunction.name))) {
            return false;
        }
        if (this.subGenerators.size() != otherFunction.subGenerators.size()) {
            return false;
        }
        for (int i = 0; i < this.subGenerators.size(); i++) {
            GeneratorWithPath thisSubGenerator = this.subGenerators.get(i);
            GeneratorWithPath otherSubGenerator = otherFunction.subGenerators.get(i);
            if (!(thisSubGenerator.equals(otherSubGenerator))) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (this.skolemFunction == null) {
            return "Skolem generator: " + name + " - Subgenerators: " + subGenerators;
        }
        return this.skolemFunction.toString();
    }

    public String toStringWithVariableArguments(List<FormulaVariable> variables) {
        return this.skolemFunction.toStringWithVariableArguments(variables);
    }
}

class GeneratorComparator implements Comparator<GeneratorWithPath> {

    public int compare(GeneratorWithPath g1, GeneratorWithPath g2) {
        return g1.getTargetPath().getAbsolutePath().toString().compareTo(g2.getTargetPath().getAbsolutePath().toString());
    }
}
