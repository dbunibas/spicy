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
 
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.correspondence.operators.GenerateCorrespondence;
import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.generators.TGDGeneratorsMap;
import it.unibas.spicy.model.generators.operators.GenerateValueGenerators;
import it.unibas.spicy.model.mapping.operators.FindFormulaVariables;
import it.unibas.spicy.model.mapping.operators.GenerateTgdFormulaAtoms;
import it.unibas.spicy.model.mapping.operators.TGDToLogicalString;
import it.unibas.spicy.model.mapping.rewriting.FormulaAtom;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FORule implements Cloneable, Comparable<FORule>, IIdentifiable {

    private static Log logger = LogFactory.getLog(FORule.class);
    private boolean loadedFromParser;
    // algebra form
    private ComplexQueryWithNegations sourceView;
    private SimpleConjunctiveQuery targetView;
    private List<VariableCorrespondence> coveredCorrespondences = new ArrayList<VariableCorrespondence>();
    // logical form
    private List<FormulaAtom> targetFormulaAtoms;
    // generators
    private TGDGeneratorsMap generators;
    private FormulaVariableMaps variableMaps;

    public FORule(SimpleConjunctiveQuery sourceView, SimpleConjunctiveQuery targetView) {
        this.sourceView = new ComplexQueryWithNegations(new ComplexConjunctiveQuery(sourceView));
        this.targetView = targetView;
    }

    public FORule(SimpleConjunctiveQuery sourceView, SimpleConjunctiveQuery targetView, List<VariableCorrespondence> correspondences) {
        this(sourceView, targetView);
        this.coveredCorrespondences = correspondences;
    }

    public FORule(ComplexQueryWithNegations sourceView, SimpleConjunctiveQuery targetView) {
        this.sourceView = sourceView;
        this.targetView = targetView;
    }

    public FORule(ComplexQueryWithNegations sourceView, SimpleConjunctiveQuery targetView, List<VariableCorrespondence> correspondences) {
        this(sourceView, targetView);
        this.coveredCorrespondences = correspondences;
    }

    public String getId() {
        return toShortString();
    }

    public boolean isLoadedFromParser() {
        return loadedFromParser;
    }

    public void setLoadedFromParser(boolean loadedFromParser) {
        this.loadedFromParser = loadedFromParser;
    }

    public ComplexQueryWithNegations getComplexSourceQuery() {
        return this.sourceView;
    }

    public void setComplexSourceView(ComplexQueryWithNegations sourceView) {
        this.sourceView = sourceView;
    }

    public SimpleConjunctiveQuery getSimpleSourceView() {
        if (this.sourceView.getComplexQuery().isSimple()) {
            return sourceView.getComplexQuery().getFirstConjunct();
        }
        throw new IllegalArgumentException("This rule does not have a simple source view: \n" + this);
    }

    public SimpleConjunctiveQuery getTargetView() {
        return targetView;
    }

    public void setTargetView(SimpleConjunctiveQuery targetView) {
        this.targetView = targetView;
    }

    public void addCoveredCorrespondence(VariableCorrespondence correspondence) {
        VariableCorrespondence existingCorrespondence = new GenerateCorrespondence().checkCorrespondences(this, correspondence);
        if (existingCorrespondence != null) {
            throw new IllegalMappingTaskException("There are two different correspondences with equal target paths in the same TGD: " + correspondence + " - " + existingCorrespondence);
        }
        this.coveredCorrespondences.add(correspondence);
    }

    public void addCoveredCorrespondences(List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            addCoveredCorrespondence(correspondence);
        }
    }

    public List<VariableCorrespondence> getCoveredCorrespondences() {
        return coveredCorrespondences;
    }

    public void setCoveredCorrespondences(List<VariableCorrespondence> coveredCorrespondences) {
        this.coveredCorrespondences = coveredCorrespondences;
    }

    public List<FormulaAtom> getTargetFormulaAtoms(MappingTask mappingTask) {
        if (targetFormulaAtoms == null) {
            targetFormulaAtoms = new GenerateTgdFormulaAtoms().generateFormulaAtoms(this, this.getTargetView(), mappingTask);
        }
        return targetFormulaAtoms;
    }

    public FormulaVariableMaps getVariableMaps(MappingTask mappingTask) {
        if (this.variableMaps == null) {
            FindFormulaVariables variableFinder = new FindFormulaVariables();
            this.variableMaps = variableFinder.findFormulaVariables(this, mappingTask);
        }
        return variableMaps;
    }

    public List<FormulaVariable> getUniversalFormulaVariables(MappingTask mappingTask) {
        return getVariableMaps(mappingTask).getUniversalVariables(this.getId());
    }

    public List<FormulaVariable> getExistentialFormulaVariables(MappingTask mappingTask) {
        return getVariableMaps(mappingTask).getExistentialVariables(this.getId());
    }

    public List<FormulaVariable> getUniversalFormulaVariablesInTarget(MappingTask mappingTask) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable universalVariable : getUniversalFormulaVariables(mappingTask)) {
            if (universalVariable.getTargetOccurrencePaths().isEmpty()) {
                continue;
            }
            result.add(universalVariable);
        }
        return result;
    }

    public TGDGeneratorsMap getGenerators(MappingTask mappingTask) {
        if (generators == null) {
            generators = new GenerateValueGenerators().generateValueGenerators(this, mappingTask);
        }
        return generators;
    }

    public int compareTo(FORule otherTGD) {
        if (this.coveredCorrespondences.size() != otherTGD.coveredCorrespondences.size()) {
            return this.coveredCorrespondences.size() - otherTGD.coveredCorrespondences.size();
        }
        if (targetVariableSize(this) != targetVariableSize(otherTGD)) {
            return targetVariableSize(this) - targetVariableSize(otherTGD);
        }
        if (targetJoinConditions(this) != targetJoinConditions(otherTGD)) {
            return targetJoinConditions(this) - targetJoinConditions(otherTGD);
        }
        if (sourceVariableSize(this) != sourceVariableSize(otherTGD)) {
            return sourceVariableSize(this) - sourceVariableSize(otherTGD);
        }
//        if (sourceJoinConditions(this) != sourceJoinConditions(otherTGD)) {
//            return sourceJoinConditions(this) - sourceJoinConditions(otherTGD);
//        }
        return this.toString().compareTo(otherTGD.toString());
    }

    private static int sourceVariableSize(FORule tgd) {
        return tgd.getComplexSourceQuery().getGenerators().size();
    }

    private static int targetVariableSize(FORule tgd) {
        return tgd.getTargetView().getGenerators().size();
    }

    private static int targetJoinConditions(FORule tgd) {
        return tgd.getTargetView().getJoinConditions().size() + tgd.getTargetView().getCyclicJoinConditions().size();
    }

    public boolean hasSameId(FORule tgd) {
        return this.getId().equals(tgd.getId());
    }

//    @Override
//    public int hashCode() {
//        int hash = this.getId().hashCode();
//        return hash;
//    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FORule)) {
            return false;
        }
        FORule tgd = (FORule) obj;
        boolean equalViews = this.hasEqualViews(tgd);
        boolean equalCorrespondences = SpicyEngineUtility.equalLists(this.getCoveredCorrespondences(), tgd.getCoveredCorrespondences());
        return equalViews && equalCorrespondences;
    }

    public boolean hasEqualViews(FORule tgd) {
        return (this.sourceView.equals(tgd.sourceView) && this.getTargetView().equals(tgd.getTargetView()));
    }

    public FORule clone() {
        try {
            FORule clone = (FORule) super.clone();
            clone.sourceView = this.sourceView.clone();
            clone.targetView = this.targetView.clone();
            clone.coveredCorrespondences = new ArrayList<VariableCorrespondence>(this.coveredCorrespondences);
            clone.generators = null;
            clone.targetFormulaAtoms = null;
            clone.variableMaps = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append(getId()).append(": ");
        result.append(toStringNoId(indent));
        return result.toString();
    }

    public String toStringNoId(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("for each  \n");
        result.append(this.sourceView.toString(indent));
        result.append("\n");
        result.append(indent).append("generate     \n");
        result.append(this.getTargetView().variableString(indent)).append("\n");
        if (this.getTargetView().hasJoinConditions()) {
            result.append(indent).append("where     \n");
            for (VariableJoinCondition joinCondition : this.getTargetView().getJoinConditions()) {
                result.append(indent).append(joinCondition.toStringWithSet()).append("\n");
            }
            for (VariableJoinCondition cyclicJoinCondition : this.getTargetView().getCyclicJoinConditions()) {
                result.append(indent).append(cyclicJoinCondition.toStringWithSet()).append(" (cyclic)").append("\n");
            }
        }
        result.append(correspondenceString(indent));
        return result.toString();
    }

    private String correspondenceString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("such that \n");
        for (VariableCorrespondence correspondence : this.getCoveredCorrespondences()) {
            result.append(indent).append(correspondence).append("\n");
        }
        return result.toString();
    }

    public String toLongString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append(getId()).append(": for each  \n");
        result.append(this.sourceView.toString(indent));
        result.append("\n");
        result.append(indent).append("generate     \n");
        result.append(this.getTargetView().variableString(indent)).append("\n");
        if (this.getTargetView().hasJoinConditions()) {
            result.append(indent).append("where     \n");
            for (VariableJoinCondition joinCondition : this.getTargetView().getJoinConditions()) {
                result.append(indent).append(joinCondition.toStringWithSet()).append("\n");
            }
            for (VariableJoinCondition cyclicJoinCondition : this.getTargetView().getCyclicJoinConditions()) {
                result.append(indent).append(cyclicJoinCondition.toStringWithSet()).append(" (cyclic)").append("\n");
            }
        }
        result.append(correspondenceString(indent));
        return result.toString();
    }

    public String toLogicalString(MappingTask mappingTask) {
        return toLogicalString("", true, mappingTask);
    }

    public String toLogicalString(boolean printSkolems, MappingTask mappingTask) {
        return toLogicalString("", printSkolems, mappingTask);
    }

    private String toLogicalString(String indent, boolean printSkolems, MappingTask mappingTask) {
        TGDToLogicalString toStringOperator = new TGDToLogicalString(printSkolems, false, false);
        return toStringOperator.toLogicalString(this, mappingTask, indent);
    }

    public String toSaveString(String indent, MappingTask mappingTask) {
        TGDToLogicalString toStringOperator = new TGDToLogicalString(false, true, false);
        return toStringOperator.toLogicalString(this, mappingTask, indent);
    }

    public String toExportString(String indent, MappingTask mappingTask) {
        TGDToLogicalString toStringOperator = new TGDToLogicalString(false, true, true);
        return toStringOperator.toLogicalString(this, mappingTask, indent);
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append("Rule_");
        result.append(sourceVariablesNames());
        result.append("_");
        result.append(targetVariablesNames());
        result.append("_");
        result.append(Math.abs(correspondenceString("").hashCode()));
        return result.toString();
    }

    public String sourceVariablesNames() {
        StringBuilder result = new StringBuilder();
        for (SetAlias variable : sourceView.getVariables()) {
            result.append(variable.toShortString());
        }
        return result.toString();
    }

    public String targetVariablesNames() {
        StringBuilder result = new StringBuilder();
        for (SetAlias variable : targetView.getVariables()) {
            result.append(variable.toShortString());
        }
        return result.toString();
    }

}
