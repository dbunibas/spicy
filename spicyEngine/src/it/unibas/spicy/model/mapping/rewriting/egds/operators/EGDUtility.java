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
 
package it.unibas.spicy.model.mapping.rewriting.egds.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.TGDRenaming;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.rewriting.egds.AttributeGroup;
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EGDUtility {

    private static Log logger = LogFactory.getLog(EGDUtility.class);

    public static Determination findUniversalVariablesForTargetPaths(List<VariablePathExpression> paths, FORule tgd, MappingTask mappingTask) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (VariablePathExpression path : paths) {
            FormulaVariable variable = findUniversalVariableForTargetPath(path, tgd, mappingTask);
            if (variable == null) {
                return null;
            }
            result.add(variable);
        }
        return new Determination(result);
    }

    static FormulaVariable findUniversalVariableForTargetPath(VariablePathExpression path, FORule tgd, MappingTask mappingTask) {
        for (FormulaVariable formulaVariable : tgd.getUniversalFormulaVariables(mappingTask)) {
            if (formulaVariable.getTargetOccurrencePaths().contains(path)) {
                return formulaVariable;
            }
        }
        return null;
    }

    static FormulaVariable findExistentialVariableForTargetPath(VariablePathExpression path, FORule tgd, MappingTask mappingTask) {
        for (FormulaVariable formulaVariable : tgd.getExistentialFormulaVariables(mappingTask)) {
            if (formulaVariable.getTargetOccurrencePaths().contains(path)) {
                return formulaVariable;
            }
        }
        return null;
    }

    public static FormulaVariable findVariableForTargetPath(VariablePathExpression path, FORule tgd, MappingTask mappingTask) {
        FormulaVariable variable = findUniversalVariableForTargetPath(path, tgd, mappingTask);
        if (variable == null) {
            variable = findExistentialVariableForTargetPath(path, tgd, mappingTask);
        }
        return variable;
    }

    static List<VariableFunctionalDependency> generateSimpleDependencies(VariableFunctionalDependency complexDependency) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (VariablePathExpression rightPath : complexDependency.getRightPaths()) {
            VariableFunctionalDependency simpleDependency = new VariableFunctionalDependency(complexDependency.getLeftPaths(), rightPath);
            result.add(simpleDependency);
        }
        return result;
    }

    static List<VariableFunctionalDependency> findKeyConstraintsForSet(IDataSourceProxy dataSource, SetAlias setVariable) {
        List<VariableFunctionalDependency> result = new ArrayList<VariableFunctionalDependency>();
        for (VariableFunctionalDependency functionalDependency : dataSource.getMappingData().getDependencies()) {
            if (functionalDependency.getVariable().equalsOrIsClone(setVariable) && functionalDependency.isKey()) {
                result.add(new VariableFunctionalDependency(functionalDependency, setVariable));
            }
        }
        return result;
    }

    public static boolean containsPathWithSameVariableId(AttributeGroup attributeGroup, VariablePathExpression fromPath) {
        for (VariablePathExpression attributePath : attributeGroup.getAttributePaths()) {
            if (attributePath.equalsAndHasSameVariableId(fromPath)) {
                return true;
            }
        }
        return false;
    }

    public static List<VariablePathExpression> findRenamedPaths(List<VariablePathExpression> attributePaths, TGDRenaming tgdRenaming) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        Map<Integer, SetAlias> renamings = tgdRenaming.getRenamedTargetView().getRenamings();
        for (VariablePathExpression attributePath : attributePaths) {
            SetAlias renamedVariable = renamings.get(attributePath.getStartingVariable().getId());
            if (renamedVariable == null) {
                return null;
            }
            VariablePathExpression renamedPath = new VariablePathExpression(attributePath, renamedVariable);
            result.add(renamedPath);
        }
        return result;
    }

    public static List<VariablePathExpression> findCorrectPaths(List<VariablePathExpression> originalPaths, FORule tgd, MappingTask mappingTask) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression attributePath : originalPaths) {
            VariablePathExpression renamedPath = findPath(attributePath, tgd.getTargetView(), mappingTask);
            if (renamedPath == null) {
                return null;
            }
            result.add(renamedPath);
        }
        return result;
    }

    public static VariablePathExpression findPath(VariablePathExpression attributePath, SimpleConjunctiveQuery targetView, MappingTask mappingTask) {
        for (SetAlias variable : targetView.getVariables()) {
            for (VariablePathExpression attributePathInVariable : variable.getAttributes(mappingTask.getTargetProxy().getIntermediateSchema())) {
                if (attributePathInVariable.equals(attributePath)) {
                    return attributePathInVariable;
                }
            }
        }
        return null;
    }

    public static void mergeGroups(VariablePathExpression firstPath, VariablePathExpression secondPath, List<AttributeGroup> attributeGroups) {
        AttributeGroup firstGroup = EGDUtility.findGroupForPath(firstPath, attributeGroups);
        AttributeGroup secondGroup = EGDUtility.findGroupForPath(secondPath, attributeGroups);
        if (firstGroup.equals(secondGroup)) {
            return;
        }
        attributeGroups.remove(secondGroup);
        EGDUtility.addPathsToGroup(firstGroup, secondGroup);
    }

    public static boolean areEqual(VariablePathExpression firstPath, VariablePathExpression secondPath, List<AttributeGroup> attributeGroups) {
        AttributeGroup firstGroup = findGroupForPath(firstPath, attributeGroups);
        AttributeGroup secondGroup = findGroupForPath(secondPath, attributeGroups);
        return firstGroup.equals(secondGroup);
    }

    public static AttributeGroup findGroupForPath(VariablePathExpression attributePath, List<AttributeGroup> attributeGroups) {
        for (AttributeGroup attributeGroup : attributeGroups) {
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, attributePath)) {
                return attributeGroup;
            }
        }
        throw new IllegalArgumentException("Unable to find attribute group for path: " + attributePath + "\nGroups: " + SpicyEngineUtility.printCollection(attributeGroups));
    }

    public static void addPathsToGroup(AttributeGroup group, AttributeGroup groupToAdd) {
        for (VariablePathExpression pathExpression : groupToAdd.getAttributePaths()) {
            if (!EGDUtility.containsPathWithSameVariableId(group, pathExpression)) {
                group.addAttributePath(pathExpression);
            }
        }
    }

    public static boolean containsEqualVariable(SimpleConjunctiveQuery targetView, SetAlias targetVariableToAdd) {
        for (SetAlias targetVariable : targetView.getGenerators()) {
            if (targetVariable.equals(targetVariableToAdd)) {
                return true;
            }
        }
        return false;
    }

    public static List<VariablePathExpression> correctPaths(List<VariablePathExpression> originalPaths, SetAlias variable) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariablePathExpression originalPath : originalPaths) {
            VariablePathExpression newPath = new VariablePathExpression(originalPath, variable);
            result.add(newPath);
        }
        return result;
    }
}
