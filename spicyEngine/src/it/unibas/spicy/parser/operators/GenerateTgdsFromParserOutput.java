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
package it.unibas.spicy.parser.operators;

import it.unibas.spicy.model.algebra.operators.NormalizeViewForExecutionPlan;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.SourceEqualities;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.parser.ParserAtom;
import it.unibas.spicy.parser.ParserAttribute;
import it.unibas.spicy.parser.ParserEquality;
import it.unibas.spicy.parser.ParserException;
import it.unibas.spicy.parser.ParserTGD;
import it.unibas.spicy.parser.ParserView;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateTgdsFromParserOutput {

    private static Log logger = LogFactory.getLog(GenerateTgdsFromParserOutput.class);

    private static enum CompareType {

        SYMBOL, NONE, IMPLICIT
    };
//    private CompareType compareType = CompareType.NONE;
    protected ContextualizePaths pathContextualizer = new ContextualizePaths();

    public List<FORule> generateTGDs(List<ParserTGD> stParserTgds, MappingTask mappingTask) {
        List<FORule> result = new ArrayList<FORule>();
        for (ParserTGD parserTGD : stParserTgds) {
            SimpleConjunctiveQuery simpleSourceView = generateView(parserTGD.getSourceView(), mappingTask.getSourceProxy());
            if (logger.isDebugEnabled()) logger.debug("Generated view: " + simpleSourceView);
            ComplexConjunctiveQuery sourceView = new ComplexConjunctiveQuery(simpleSourceView);
            ComplexQueryWithNegations complexQueryWithNegations = new ComplexQueryWithNegations(sourceView);
            if (parserTGD.getNegatedSourceViews().size() > 0) {
                generateNegatedView(complexQueryWithNegations, parserTGD.getNegatedSourceViews(), parserTGD.getSourceView(), mappingTask.getSourceProxy());
            }
            complexQueryWithNegations.setProvenance(complexQueryWithNegations.getId());
            SimpleConjunctiveQuery targetView = generateView(parserTGD.getTargetView(), mappingTask.getTargetProxy());
            List<VariableCorrespondence> variableCorrespondences = new ArrayList<VariableCorrespondence>();
            for (ValueCorrespondence correspondence : parserTGD.getCorrespondences()) {
                variableCorrespondences.add(pathContextualizer.contextualizeCorrespondence(mappingTask.getSourceProxy(), mappingTask.getTargetProxy(), correspondence));
            }
            NormalizeViewForExecutionPlan normalizer = new NormalizeViewForExecutionPlan();
            //      SimpleConjunctiveQuery normalizedSourceView = normalizer.normalizeView(sourceView);
            SimpleConjunctiveQuery normalizedTargetView = normalizer.normalizeView(targetView);
            FORule tgd = new FORule(complexQueryWithNegations, normalizedTargetView, variableCorrespondences);
            tgd.setLoadedFromParser(true);
            result.add(tgd);
        }
        return result;
    }

    private void disassemblyInAtoms(List<ParserView> parserViews, List<ParserAtom> parserAtoms) {
        for (ParserView parserView : parserViews) {
            parserAtoms.addAll(parserView.getAtoms());
            if (parserView.getSubViews().size() >= 1) {
                disassemblyInAtoms(parserView.getSubViews(), parserAtoms);
            }
        }
    }

    private SimpleConjunctiveQuery generateView(ParserView parserView, IDataSourceProxy dataSource) {
        SimpleConjunctiveQuery view = new SimpleConjunctiveQuery();
        for (ParserAtom atom : parserView.getAtoms()) {
            SetAlias variable = findVariable(atom, dataSource);
            view.addVariable(variable);
            for (SelectionCondition selectionCondition : atom.getSelectionConditions()) {
                VariableSelectionCondition variableCondition = pathContextualizer.contextualizeSelectionCondition(selectionCondition, dataSource);
                view.addSelectionCondition(variableCondition);
            }
        }
        for (SelectionCondition selectionCondition : parserView.getSelectionConditions()) {
            VariableSelectionCondition variableCondition = pathContextualizer.contextualizeSelectionCondition(selectionCondition, dataSource);
            view.addSelectionCondition(variableCondition);
        }
        for (JoinCondition joinCondition : parserView.getJoinConditions()) {
            VariableJoinCondition variableJoinCondition = pathContextualizer.contextualizeJoinCondition(joinCondition, dataSource);
            view.addJoinCondition(variableJoinCondition);
        }
        NormalizeViewForExecutionPlan viewNormalizer = new NormalizeViewForExecutionPlan();
        SimpleConjunctiveQuery normalizedView = viewNormalizer.normalizeView(view);
        return normalizedView;
    }

    private void generateNegatedView(ComplexQueryWithNegations complexQueryWithNegations, List<ParserView> negatedParserViews, ParserView sourceParserView, IDataSourceProxy dataSource) {
        for (ParserView parserView : negatedParserViews) {
            if (logger.isTraceEnabled()) logger.trace("Generating NegatedView \n" + parserView);
            SimpleConjunctiveQuery view = new SimpleConjunctiveQuery();
            for (ParserAtom atom : parserView.getAtoms()) {
                SetAlias variable = findVariable(atom, dataSource);
                view.addVariable(variable);
                for (SelectionCondition selectionCondition : atom.getSelectionConditions()) {
                    VariableSelectionCondition variableCondition = pathContextualizer.contextualizeSelectionCondition(selectionCondition, dataSource);
                    view.addSelectionCondition(variableCondition);
                }
            }
            for (SelectionCondition selectionCondition : parserView.getSelectionConditions()) {
                VariableSelectionCondition variableCondition = pathContextualizer.contextualizeSelectionCondition(selectionCondition, dataSource);
                view.addSelectionCondition(variableCondition);
            }
            for (JoinCondition joinCondition : parserView.getJoinConditions()) {
                VariableJoinCondition variableJoinCondition = pathContextualizer.contextualizeJoinCondition(joinCondition, dataSource);
                view.addJoinCondition(variableJoinCondition);
            }
            NormalizeViewForExecutionPlan viewNormalizer = new NormalizeViewForExecutionPlan();
            SimpleConjunctiveQuery normalizedView = viewNormalizer.normalizeView(view);
            ComplexQueryWithNegations complexQueryWithNegationsInternal = new ComplexQueryWithNegations(new ComplexConjunctiveQuery(normalizedView));
            complexQueryWithNegationsInternal.setProvenance(complexQueryWithNegationsInternal.getId());
            if (parserView.getSubViews().size() > 0) {
                generateNegatedView(complexQueryWithNegationsInternal, parserView.getSubViews(), parserView, dataSource);
            }
            SourceEqualities sourceEqualities = new SourceEqualities();
            compareAtomWithSymbol(parserView, sourceParserView.getAtoms(), sourceEqualities, dataSource);
            compareAtom(parserView, sourceParserView.getAtoms(), sourceEqualities, dataSource);

            NegatedComplexQuery negatedComplexQuery = new NegatedComplexQuery(complexQueryWithNegationsInternal, sourceEqualities);
            negatedComplexQuery.setProvenance(negatedComplexQuery.getId());
            complexQueryWithNegations.addNegatedComplexQuery(negatedComplexQuery);
        }
    }

    private void compareAtomWithSymbol(ParserView parserView, List<ParserAtom> sourceAtoms, SourceEqualities sourceEqualities, IDataSourceProxy source) {
//        if (this.compareType == CompareType.IMPLICIT) {
//            return;
//        }
        for (ParserEquality parserEquality : parserView.getEqualities()) {
            if (logger.isTraceEnabled()) logger.trace("Checking parser equality: " + parserEquality);
            for (ParserAtom sourceAtom : sourceAtoms) {
                for (ParserAtom negatedAtom : parserView.getAtoms()) {
                    for (ParserAttribute negatedAttribute : negatedAtom.getAttributes()) {
                        for (ParserAttribute sourceAttribute : sourceAtom.getAttributes()) {
                            if (!(sourceAttribute.isVariable() && negatedAttribute.isVariable())) {
                                continue;
                            }
                            if (parserEquality.getLeftAttribute().getVariable().equals(sourceAttribute.getVariable()) && parserEquality.getRightAttribute().getVariable().equals(negatedAttribute.getVariable())) {
                                sourceEqualities.addLeftPath(sourceAttribute.getAttributePath().getRelativePath(source));
                                sourceEqualities.addRightPath(negatedAttribute.getAttributePath().getRelativePath(source));
                                if (logger.isTraceEnabled()) logger.trace("Adding equality - Left Path: " + sourceAttribute.getAttributePath().getRelativePath(source) + " Right Path: " + negatedAttribute.getAttributePath().getRelativePath(source));
//                                this.compareType = CompareType.SYMBOL;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void compareAtom(ParserView parserView, List<ParserAtom> sourceAtoms, SourceEqualities sourceEqualities, IDataSourceProxy source) {
//        if (this.compareType == CompareType.SYMBOL) {
//            return;
//        }
        for (ParserAtom negatedAtom : parserView.getAtoms()) {
            ParserAtom cloneNegatedAtom = negatedAtom.clone();
            for (ParserAtom sourceAtom : sourceAtoms) {
                for (ParserAttribute negatedAttribute : cloneNegatedAtom.getAttributes()) {
                    for (ParserAttribute sourceAttribute : sourceAtom.getAttributes()) {
                        if (!(sourceAttribute.isVariable() && negatedAttribute.isVariable())) {
                            continue;
                        }
                        if (sourceAttribute.getVariable().equals(negatedAttribute.getVariable())) {
                            sourceEqualities.addLeftPath(sourceAttribute.getAttributePath().getRelativePath(source));
                            sourceEqualities.addRightPath(negatedAttribute.getAttributePath().getRelativePath(source));
//                            this.compareType = CompareType.IMPLICIT;
//                            parserAtoms.remove(negatedAtom);
//                            negatedAttribute.setVariable(null);
                            break;
                        }
                    }
                }

            }
            //     compareAtomInNegated(flag, parserAtoms, negatedAtom, cloneNegatedAtom, sourceEqualities, source);
        }
    }

    private SetAlias findVariable(ParserAtom atom, IDataSourceProxy dataSource) {
        for (SetAlias variable : dataSource.getMappingData().getVariables()) {
            if (variable.getAbsoluteBindingPathExpression().equals(atom.getAbsolutePath())) {
                return variable;
            }
        }
        throw new ParserException("Unable to find variable for atom " + atom + " in: " + dataSource);
    }
//    private String generateProvenance(String string, Object query) {
////        return string + counter++;
//        return string + "_" + Math.abs(query.toString().hashCode());
//    }
}
