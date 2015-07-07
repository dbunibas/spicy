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
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateViews {

    private static Log logger = LogFactory.getLog(GenerateViews.class);
    private ContextualizePaths contextualizer = new ContextualizePaths();

    public List<SimpleConjunctiveQuery> generateViews(IDataSourceProxy dataSource) {
        List<SimpleConjunctiveQuery> views = constructViews(dataSource);
        List<SimpleConjunctiveQuery> viewsWithCyclicJoins = searchCyclicJoins(views);
        addSelectionConditions(viewsWithCyclicJoins, dataSource);
        return viewsWithCyclicJoins;
    }

    ////////////////////////      VIEW CONSTRUCTION      ///////////////////////////////////

    private List<SimpleConjunctiveQuery> constructViews(IDataSourceProxy dataSource) {
        List<VariableJoinCondition> variableJoinConditions = contextualizeJoinConditions(dataSource);
        List<SimpleConjunctiveQuery> currentViews = initViews(dataSource);
        List<SimpleConjunctiveQuery> discardedViews = new ArrayList<SimpleConjunctiveQuery>();
        if (logger.isDebugEnabled()) logger.debug("\nContextualized join conditions:\n" + SpicyEngineUtility.printCollection(variableJoinConditions));
        if (logger.isDebugEnabled()) logger.debug("\n================= Initial views:\n" + SpicyEngineUtility.printCollection(currentViews));
        boolean fixpoint = false;
        while (!fixpoint) {
            List<SimpleConjunctiveQuery> newViews = addJoinConditions(variableJoinConditions, currentViews, discardedViews);
            if (newViews != null) {
                currentViews = newViews;
            } else {
                fixpoint = true;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("\n================= Final views:\n" + SpicyEngineUtility.printCollection(currentViews));
        return currentViews;
    }

    private List<VariableJoinCondition> contextualizeJoinConditions(IDataSourceProxy dataSource) {
        List<VariableJoinCondition> result = new ArrayList<VariableJoinCondition>();
        for (JoinCondition joinCondition : dataSource.getJoinConditions()) {
            result.add(contextualizer.contextualizeJoinCondition(joinCondition, dataSource));
        }
        return result;
    }

    private List<SimpleConjunctiveQuery> initViews(IDataSourceProxy dataSource) {
        List<SimpleConjunctiveQuery> result = new ArrayList<SimpleConjunctiveQuery>();
        for (SetAlias variable : dataSource.getMappingData().getVariables()) {
            result.add(new SimpleConjunctiveQuery(variable));
        }
        return result;
    }

    private List<SimpleConjunctiveQuery> addJoinConditions(List<VariableJoinCondition> joinConditions, List<SimpleConjunctiveQuery> currentViews, List<SimpleConjunctiveQuery> discardedViews) {
        List<SimpleConjunctiveQuery> newViews = new ArrayList<SimpleConjunctiveQuery>();
        boolean modifications = false;
        if (logger.isDebugEnabled()) logger.debug("\n**************Current views:\n" + SpicyEngineUtility.printCollection(currentViews));
        Collections.sort(currentViews);
        for (int i = currentViews.size() - 1; i >= 0; i--) {
            SimpleConjunctiveQuery view = currentViews.get(i);
            if (logger.isDebugEnabled()) logger.debug("\n**************Current new views:\n" + SpicyEngineUtility.printCollection(newViews));
            if (logger.isDebugEnabled()) logger.debug("\n**************Current discarded views:\n" + SpicyEngineUtility.printCollection(discardedViews));
            if (logger.isDebugEnabled()) logger.debug("\n================= Examining view:\n" + view);
            for (VariableJoinCondition joinCondition : joinConditions) {
                if (isMandatoryForeignKey(joinCondition) && foreignKeyIsApplicable(view, joinCondition)) {
                    modifications = addMandatoryForeignKey(view, joinCondition, currentViews, newViews, discardedViews) || modifications;
                } else if (isNullableForeignKey(joinCondition) && foreignKeyIsApplicable(view, joinCondition)) {
                    modifications = addNullableForeignKey(view, joinCondition, currentViews, newViews, discardedViews) || modifications;
                } else if (isMandatoryJoin(joinCondition) && joinIsApplicable(view, joinCondition)) {
                    modifications = addMandatoryJoin(view, joinCondition, currentViews, newViews, discardedViews) || modifications;
                } else if (isNullableJoin(joinCondition) && joinIsApplicable(view, joinCondition)) {
                    modifications = addNullableJoin(view, joinCondition, currentViews, newViews, discardedViews) || modifications;
                }
            }
            // in case none of the above applies
            if (!newViews.contains(view) && !discardedViews.contains(view)) {
                if (logger.isDebugEnabled()) logger.debug("\nStarting view added to new views");
                newViews.add(view);
            }
        }
        if (modifications) {
            return newViews;
        }
        return null;
    }

    private boolean isMandatoryForeignKey(VariableJoinCondition joinCondition) {
        return joinCondition.isMonodirectional() && joinCondition.isMandatory();
    }

    private boolean isNullableForeignKey(VariableJoinCondition joinCondition) {
        return joinCondition.isMonodirectional() && !joinCondition.isMandatory();
    }

    private boolean isMandatoryJoin(VariableJoinCondition joinCondition) {
        return !joinCondition.isMonodirectional() && joinCondition.isMandatory();
    }

    private boolean isNullableJoin(VariableJoinCondition joinCondition) {
        return !joinCondition.isMonodirectional() && !joinCondition.isMandatory();
    }

    private boolean foreignKeyIsApplicable(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition) {
        SetAlias fromVariable = joinCondition.getFromVariable();
        SetAlias toVariable = joinCondition.getToVariable();
        if (view.getGenerators().contains(fromVariable) && !view.getGenerators().contains(toVariable)) {
            return true;
        } else if (view.getGenerators().contains(fromVariable) && view.getGenerators().contains(toVariable)
                && !view.getJoinConditions().contains(joinCondition)) {
            return true;
        }
        return false;
    }

    private boolean joinIsApplicable(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition) {
        SetAlias fromVariable = joinCondition.getFromPaths().get(0).getStartingVariable();
        SetAlias toVariable = joinCondition.getToPaths().get(0).getStartingVariable();
        if ((view.getGenerators().contains(fromVariable) && !view.getGenerators().contains(toVariable)) ||
                (!view.getGenerators().contains(fromVariable) && view.getGenerators().contains(toVariable))) {
            return true;
        } else if (view.getGenerators().contains(fromVariable) && view.getGenerators().contains(toVariable)
                && !view.getJoinConditions().contains(joinCondition)) {
            return true;
        }
        return false;
    }

    private boolean isOldView(SimpleConjunctiveQuery newView, List<SimpleConjunctiveQuery> previousViews, List<SimpleConjunctiveQuery> newViews, List<SimpleConjunctiveQuery> discardedViews) {
        boolean previous = previousViews.contains(newView);
        boolean discarded = discardedViews.contains(newView);
        boolean alreadyGenerated = newViews.contains(newView);
        if (logger.isTraceEnabled()) logger.trace("New candidate view:\n" + newView + "Discarded: " + discarded + " - Already generated: " + alreadyGenerated);
        return previous || discarded || alreadyGenerated;
    }

    private boolean addMandatoryForeignKey(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition, List<SimpleConjunctiveQuery> previousViews, List<SimpleConjunctiveQuery> newViews, List<SimpleConjunctiveQuery> discardedViews) {
        if (logger.isDebugEnabled()) logger.debug("\n================= Adding mandatory foreign key: " + joinCondition);
        SimpleConjunctiveQuery newView = view.clone();
        SetAlias toVariable = joinCondition.getToVariable();
        if (newView.getGenerators().contains(toVariable)) {
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added cyclic join");
        } else {
            newView.addVariable(toVariable);
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added proper join");
        }
        if (logger.isDebugEnabled()) logger.debug("New view:\n" + newView);
        boolean modifications = false;
        if (!isOldView(newView, previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("This is actually a new view, adding...");
            newViews.add(newView);
            modifications = true;
        }
        // old view must be discarded
        if (!discardedViews.contains(view)) {
            if (logger.isDebugEnabled()) logger.debug("Discarding old view...");
            discardedViews.add(view);
            modifications = true;
        }
        if (logger.isDebugEnabled()) logger.debug("Modifications: " + modifications);
        return modifications;
    }

    private boolean addNullableForeignKey(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition, List<SimpleConjunctiveQuery> previousViews, List<SimpleConjunctiveQuery> newViews, List<SimpleConjunctiveQuery> discardedViews) {
        if (logger.isDebugEnabled()) logger.debug("\n================= Adding nullable foreign key: " + joinCondition);
        SimpleConjunctiveQuery newView = view.clone();
        SetAlias toVariable = joinCondition.getToVariable();
        if (newView.getGenerators().contains(toVariable)) {
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added cyclic join");
        } else {
            newView.addVariable(toVariable);
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added proper join");
        }
        if (logger.isDebugEnabled()) logger.debug("New view:\n" + newView);
        boolean modifications = false;
        if (!isOldView(newView,  previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("This is actually a new view, adding...");
            newViews.add(newView);
            modifications = true;
        }
        // old view must be kept as well
        if (!isOldView(view,  previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("Also old view must be kept, adding...");
            newViews.add(view);
        }
        if (logger.isDebugEnabled()) logger.debug("Modifications: " + modifications);
        return modifications;
    }

    private boolean addMandatoryJoin(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition, List<SimpleConjunctiveQuery> previousViews, List<SimpleConjunctiveQuery> newViews, List<SimpleConjunctiveQuery> discardedViews) {
        if (logger.isDebugEnabled()) logger.debug("\n================= Adding mandatory join: " + joinCondition);
        SimpleConjunctiveQuery newView = view.clone();
        SetAlias fromVariable = joinCondition.getFromVariable();
        SetAlias toVariable = joinCondition.getToVariable();
        if (newView.getGenerators().contains(fromVariable) && newView.getGenerators().contains(toVariable)) {
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added cyclic join");
        } else {
            if (!newView.getGenerators().contains(fromVariable)) {
                newView.addVariable(fromVariable);
            } else {
                newView.addVariable(toVariable);
            }
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added proper join");
        }
        if (logger.isDebugEnabled()) logger.debug("New view:\n" + newView);
        boolean modifications = false;
        if (!isOldView(newView,  previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("This is actually a new view, adding...");
            newViews.add(newView);
            modifications = true;
        }
        // old view must be discarded
        if (!discardedViews.contains(view)) {
            if (logger.isDebugEnabled()) logger.debug("Discarding old view...");
            discardedViews.add(view);
            modifications = true;
        }
        if (logger.isDebugEnabled()) logger.debug("Modifications: " + modifications);
        return modifications;
    }

    private boolean addNullableJoin(SimpleConjunctiveQuery view, VariableJoinCondition joinCondition, List<SimpleConjunctiveQuery> previousViews, List<SimpleConjunctiveQuery> newViews, List<SimpleConjunctiveQuery> discardedViews) {
        if (logger.isDebugEnabled()) logger.debug("\n================= Adding nullable join: " + joinCondition);
        SimpleConjunctiveQuery newView = view.clone();
        SetAlias fromVariable = joinCondition.getFromVariable();
        SetAlias toVariable = joinCondition.getToVariable();
        if (newView.getGenerators().contains(fromVariable) && newView.getGenerators().contains(toVariable)) {
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added cyclic join");
        } else {
            if (!newView.getGenerators().contains(fromVariable)) {
                newView.addVariable(fromVariable);
            } else {
                newView.addVariable(toVariable);
            }
            newView.addJoinCondition(joinCondition);
            if (logger.isDebugEnabled()) logger.debug("Added proper join");
        }
        if (logger.isDebugEnabled()) logger.debug("New view:\n" + newView);
        boolean modifications = false;
        if (!isOldView(newView, previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("This is actually a new view, adding...");
            newViews.add(newView);
            modifications = true;
        }
        //  old view must be kept as well
        if (!isOldView(view,  previousViews, newViews, discardedViews)) {
            if (logger.isDebugEnabled()) logger.debug("Also old view must be kept, adding...");
            newViews.add(view);
        }
        if (logger.isDebugEnabled()) logger.debug("Modifications: " + modifications);
        return modifications;
    }

    ////////////////////////      VIEW NORMALIZATION      ///////////////////////////////////

    private List<SimpleConjunctiveQuery> searchCyclicJoins(List<SimpleConjunctiveQuery> views) {
        CheckCyclesInViews cycleChecker = new CheckCyclesInViews();
        List<SimpleConjunctiveQuery> result = new ArrayList<SimpleConjunctiveQuery>();
        for (SimpleConjunctiveQuery view : views) {
            SimpleConjunctiveQuery newView = view.clone();
            newView.getJoinConditions().clear();
            newView.getCyclicJoinConditions().clear();
            for (VariableJoinCondition joinCondition : view.getJoinConditions()) {
                if (!cycleChecker.isCyclic(joinCondition, newView)) {
                    newView.addJoinCondition(joinCondition);
                } else {
                    newView.addCyclicJoinCondition(joinCondition);
                }
            }
            result.add(newView);
        }
        if (logger.isDebugEnabled()) logger.debug("\n================= Final views after join removal:\n" + SpicyEngineUtility.printCollection(result));
        return result;
    }

    private void addSelectionConditions(List<SimpleConjunctiveQuery> views, IDataSourceProxy dataSource) {
        List<VariableSelectionCondition> selectionConditions = contextualizeSelectionConditions(dataSource);
        for (VariableSelectionCondition selectionCondition : selectionConditions) {
            List<SetAlias> selectionVariables = selectionCondition.getSetVariables();
            for (SimpleConjunctiveQuery view : views) {
                if (view.getGenerators().containsAll(selectionVariables)) {
                    view.addSelectionCondition(selectionCondition);
                }
            }
        }
    }

    private List<VariableSelectionCondition> contextualizeSelectionConditions(IDataSourceProxy dataSource) {
        List<VariableSelectionCondition> result = new ArrayList<VariableSelectionCondition>();
        for (SelectionCondition selectionCondition : dataSource.getSelectionConditions()) {
            result.add(contextualizer.contextualizeSelectionCondition(selectionCondition, dataSource));
        }
        return result;
    }
}
