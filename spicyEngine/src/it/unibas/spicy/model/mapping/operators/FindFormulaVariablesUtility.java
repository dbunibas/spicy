/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Donatello Santoro - donatello.santoro@gmail.com

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
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FormulaVariableMaps;
import it.unibas.spicy.model.mapping.IIdentifiable;
import it.unibas.spicy.model.mapping.VariableOccurrence;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FindFormulaVariablesUtility {
    
    public String generateId(List<IIdentifiable> stack) {
        StringBuilder result = new StringBuilder();
//        result.append("#");
        for (int i = stack.size() - 1; i >= 0; i--) {
            IIdentifiable identifiable = stack.get(i);
            result.append(identifiable.getId()).append("#");
        }
        return result.toString();
    }

    public List<FormulaVariable> getUniversalVariables(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        List<FormulaVariable> variables = variableMaps.getUniversalVariables(generateId(stack));
        if (variables == null) {
            throw new IllegalArgumentException("Unable to find universal variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        return variables;
    }

    public List<FormulaVariable> getExistentialVariables(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        List<FormulaVariable> variables = variableMaps.getExistentialVariables(generateId(stack));
        if (variables == null) {
            throw new IllegalArgumentException("Unable to find existential variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        return variables;
    }

    public List<Expression> getEqualities(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        List<Expression> equalities = variableMaps.getEqualities(generateId(stack));
        return equalities;
    }


    public List<FormulaVariable> getUniversalVariablesForChild(IIdentifiable query, FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        stack.add(0, query);
        List<FormulaVariable> universalVariables = variableMaps.getUniversalVariables(generateId(stack));
        if (universalVariables == null) {
            throw new IllegalArgumentException("Unable to find universal variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.remove(0);
        return universalVariables;
    }

    public List<FormulaVariable> getExistentialVariablesForChild(IIdentifiable query, FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        stack.add(0, query);
        List<FormulaVariable> existential = variableMaps.getExistentialVariables(generateId(stack));
        if (existential == null) {
            throw new IllegalArgumentException("Unable to find existential variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.remove(0);
        return existential;
    }

    public List<Expression> getEqualitiesForChild(IIdentifiable query, FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        stack.add(0, query);
        List<Expression> equalities = variableMaps.getEqualities(generateId(stack));
        if (equalities == null) {
            throw new IllegalArgumentException("Unable to find equalities for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.remove(0);
        return equalities;
    }

    public List<FormulaVariable> getUniversalVariablesForFather(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        IIdentifiable query = stack.remove(0);
        List<FormulaVariable> universalVariables = variableMaps.getUniversalVariables(generateId(stack));
        if (universalVariables == null) {
            throw new IllegalArgumentException("Unable to find universal variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.add(0, query);
        return universalVariables;
    }

    public List<FormulaVariable> getUniversalVariablesForAncestor(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        IIdentifiable father = stack.remove(0);
        IIdentifiable ancestor = stack.remove(0);
        List<FormulaVariable> universalVariables = variableMaps.getUniversalVariables(generateId(stack));
        if (universalVariables == null) {
            throw new IllegalArgumentException("Unable to find universal variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.add(0, ancestor);
        stack.add(0, father);
        return universalVariables;
    }

    public List<FormulaVariable> getExistentialVariablesForAncestor(FormulaVariableMaps variableMaps, List<IIdentifiable> stack) {
        IIdentifiable father = stack.remove(0);
        IIdentifiable ancestor = stack.remove(0);
        List<FormulaVariable> variables = variableMaps.getExistentialVariables(generateId(stack));
        if (variables == null) {
            throw new IllegalArgumentException("Unable to find existential variables for id " + generateId(stack) + "\nVariable maps: " + variableMaps);
        }
        stack.add(0, ancestor);
        stack.add(0, father);
        return variables;
    }


    public boolean inJoinGroup(VariablePathExpression pathExpression, List<JoinGroup> joinGroups) {
        for (JoinGroup joinGroup : joinGroups) {
            if (inJoinGroup(pathExpression, joinGroup)) {
                return true;
            }
        }
        return false;
    }

    public boolean inJoinGroup(VariablePathExpression pathExpression, JoinGroup joinGroup) {
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            if (SpicyEngineUtility.containsPathWithSameVariableId(joinCondition.getFromPaths(), pathExpression) || SpicyEngineUtility.containsPathWithSameVariableId(joinCondition.getToPaths(), pathExpression)) {
                return true;
            }
        }
        return false;
    }

    // each join group generates several variables due to the possibility of a multiple-attribute join
    public List<FormulaVariable> createVariablesForJoinGroup(JoinGroup joinGroup, boolean existential) {
        List<FormulaVariable> variables = initializeVariables(joinGroup, existential);
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            updateOccurrences(variables, joinCondition, existential);
        }
        return variables;
    }

    private List<FormulaVariable> initializeVariables(JoinGroup joinGroup, boolean existential) {
        VariableJoinCondition firstJoin = joinGroup.getJoinConditions().get(0);
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (int i = 0; i < firstJoin.getFromPaths().size(); i++) {
            VariablePathExpression fromPath = firstJoin.getFromPaths().get(i);
            VariablePathExpression toPath = firstJoin.getToPaths().get(i);
            FormulaVariable formulaVariable = new FormulaVariable(fromPath, existential);
            if (!existential) {
                formulaVariable.addSourceOccurrencePath(toPath);
            }
            result.add(formulaVariable);
        }
        return result;
    }

    private void updateOccurrences(List<FormulaVariable> variables, VariableJoinCondition joinCondition, boolean existential) {
        List<FormulaVariable> fromPathVariables = findVariablesForPath(variables, joinCondition.getFromPaths(), existential);
        List<FormulaVariable> toPathVariables = findVariablesForPath(variables, joinCondition.getToPaths(), existential);
        if (fromPathVariables != null && toPathVariables != null) {
            return;
        }
        if (fromPathVariables != null) {
            for (int i = 0; i < joinCondition.getToPaths().size(); i++) {
                if (!existential) {
                    fromPathVariables.get(i).addSourceOccurrencePath(joinCondition.getToPaths().get(i));
                } else {
                    fromPathVariables.get(i).addTargetOccurrencePath(joinCondition.getToPaths().get(i));
                }
            }
        }
        if (toPathVariables != null) {
            for (int i = 0; i < joinCondition.getFromPaths().size(); i++) {
                if (!existential) {
                    toPathVariables.get(i).addSourceOccurrencePath(joinCondition.getFromPaths().get(i));
                } else {
                    toPathVariables.get(i).addTargetOccurrencePath(joinCondition.getFromPaths().get(i));
                }
            }
        }
    }

    private List<FormulaVariable> findVariablesForPath(List<FormulaVariable> variables, List<VariablePathExpression> paths, boolean existential) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (VariablePathExpression path : paths) {
            FormulaVariable variable = findContainingVariable(variables, path, existential);
            if (variable == null) {
                return null;
            }
            result.add(variable);
        }
        return result;
    }

    private FormulaVariable findContainingVariable(List<FormulaVariable> variables, VariablePathExpression path, boolean existential) {
        for (FormulaVariable variable : variables) {
            List<VariablePathExpression> paths = null;
            if (!existential) {
                paths = variable.getSourceOccurrencePaths();
            } else {
                paths = variable.getTargetOccurrencePaths();
            }
            if (SpicyEngineUtility.containsPathWithSameVariableId(paths, path)) {
                return variable;
            }
        }
        return null;
    }

    public void removeCopies(List<FormulaVariable> variables) {
        for (Iterator<FormulaVariable> it = variables.iterator(); it.hasNext(); ) {
            if (countOccurrences(it.next(), variables) > 1) {
                it.remove();
            }
        }
    }

    private int countOccurrences(FormulaVariable variable, List<FormulaVariable> variables) {
        int counter = 0;
        for (FormulaVariable otherVariable : variables) {
            if (otherVariable.equals(variable)) {
                counter++;
            }
        }
        return counter;
    }

    String printVariables(List<FormulaVariable> variables) {
        StringBuilder result = new StringBuilder();
        result.append("------------- Variables ------------------------\n");
        for (FormulaVariable formulaVariable : variables) {
            result.append(formulaVariable.toLongString()).append("\n");
        }
        result.append("------------------------------------------------");
        return result.toString();
    }

    List<VariableJoinCondition> simplifyJoins(List<VariableJoinCondition> allJoins) {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (VariableJoinCondition joinCondition : allJoins) {
            if (joinCondition.isSimple()) {
                result.add(joinCondition.clone());
            } else {
                for (int i = 0; i < joinCondition.size(); i++) {
                    VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
                    VariablePathExpression toPath = joinCondition.getToPaths().get(i);
                    VariableJoinCondition newJoin = new VariableJoinCondition(fromPath, toPath, joinCondition.isMonodirectional(), joinCondition.isMandatory());
                    result.add(newJoin);
                }
            }
        }
        return result;
    }

    public List<VariablePathExpression> getPaths(List<VariableOccurrence> occurrences) {
        List<VariablePathExpression> result = new ArrayList<VariablePathExpression>();
        for (VariableOccurrence occurrence : occurrences) {
            result.add(occurrence.getPath());
        }
        return result;
    }

}


