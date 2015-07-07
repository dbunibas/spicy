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
 
package it.unibas.spicy.model.mapping.rewriting.egds;

import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Determination implements Cloneable {

    private static Log logger = LogFactory.getLog(Determination.class);
    private List<FormulaVariable> implicants;
    private List<VariableFunctionalDependency> generatingDependencies = new ArrayList<VariableFunctionalDependency>();

    public Determination(FormulaVariable variable) {
        this.implicants = new ArrayList<FormulaVariable>();
        this.implicants.add(variable);
    }

    public Determination(List<FormulaVariable> implicants) {
        this.implicants = implicants;
    }

    public Determination(Determination implicants) {
        this.implicants = new ArrayList<FormulaVariable>();
        this.implicants.addAll(implicants.getImplicants());
    }

    public int getSize() {
        return this.implicants.size();
    }

    public List<FormulaVariable> getImplicants() {
        return implicants;
    }

    public void addImplicant(FormulaVariable variable) {
        if (!implicants.contains(variable)) {
            implicants.add(variable);
        }
    }

    public void addAllImplicants(Determination implicants) {
        for (FormulaVariable variable : implicants.getImplicants()) {
            addImplicant(variable);
        }
    }

    public List<VariableFunctionalDependency> getGeneratingDependencies() {
        return generatingDependencies;
    }

    public void addGeneratingDependency(VariableFunctionalDependency dependency) {
        if (!generatingDependencies.contains(dependency)) {
            this.generatingDependencies.add(dependency);
        }
    }

    public void addGeneratingDependencies(List<VariableFunctionalDependency> dependencies) {
        for (VariableFunctionalDependency variableFunctionalDependency : dependencies) {
            addGeneratingDependency(variableFunctionalDependency);
        }
    }

    public boolean contains(Determination implicants) {
        return this.implicants.containsAll(implicants.getImplicants());
    }

    public boolean contains(FormulaVariable variable) {
        return this.implicants.contains(variable);
    }

    public Determination clone() {
        try {
            Determination clone = (Determination) super.clone();
            clone.implicants = new ArrayList<FormulaVariable>();
            for (FormulaVariable formulaVariable : this.implicants) {
                clone.implicants.add(formulaVariable.clone());
            }
            clone.generatingDependencies = new ArrayList<VariableFunctionalDependency>(this.generatingDependencies);
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Determination)) {
            return false;
        }
        Determination otherImplicants = (Determination) o;
        return this.implicants.equals(otherImplicants.implicants);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        Collections.sort(implicants);
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < implicants.size(); i++) {
            FormulaVariable implicant = implicants.get(i);
            result.append(implicant.toLongString());
            if (i != implicants.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        if (!generatingDependencies.isEmpty()) {
            result.append(" - Deps: [");
            for (int i = 0; i < generatingDependencies.size(); i++) {
                VariableFunctionalDependency dependency = generatingDependencies.get(i);
                result.append(dependency.toString());
                if (i != generatingDependencies.size() - 1) {
                    result.append(", ");
                }
            }
            result.append("]");
        }
        return result.toString();
    }
}
