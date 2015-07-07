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
package it.unibas.spicy.model.generators.operators;

import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.generators.FunctionGenerator;
import it.unibas.spicy.model.generators.GeneratorWithPath;
import it.unibas.spicy.model.generators.IValueGenerator;
import it.unibas.spicy.model.generators.SkolemFunctionGenerator;
import it.unibas.spicy.model.mapping.FormulaVariable;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDImplicants;
import it.unibas.spicy.model.mapping.rewriting.egds.Determination;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.CheckEGDSatisfiability;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.FindMinimalImplicants;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateSkolemGenerators {

    private static Log logger = LogFactory.getLog(GenerateSkolemGenerators.class);
    private FindMinimalImplicants implicantsFinder = new FindMinimalImplicants();

    public void findGeneratorsForSkolems(FORule tgd, MappingTask mappingTask, Map<String, IValueGenerator> generators, List<GeneratorWithPath> functionGeneratorsForSkolemFunctions) {
        if (logger.isDebugEnabled()) logger.debug("****************  Generating skolem function generators for tgd " + tgd + " - Function generators: " + SpicyEngineUtility.printCollection(functionGeneratorsForSkolemFunctions));
        generateStandardSkolemGenerators(tgd, generators, functionGeneratorsForSkolemFunctions);
        if (mappingTask.getConfig().rewriteEGDs()) {
            addSkolemGeneratorsForKeys(tgd, generators, functionGeneratorsForSkolemFunctions, mappingTask.getTargetProxy());
        }
        if (logger.isDebugEnabled()) logger.debug("****************  Standard generators " + SpicyEngineUtility.printMap(generators));
        if (mappingTask.getConfig().rewriteSkolemsForEGDs()) {
            CheckEGDSatisfiability checker = new CheckEGDSatisfiability();
            checker.checkEGDSatisfiability(mappingTask);
            findGeneratorsForSkolemsForEGDs(tgd, mappingTask, generators, functionGeneratorsForSkolemFunctions);
            if (logger.isDebugEnabled()) logger.debug("****************  EGD generators " + SpicyEngineUtility.printMap(generators));
        }
    }

    //////////////////////////////   STANDARD CHASE, NO EGDs   /////////////////////////////////////////////
    private void generateStandardSkolemGenerators(FORule tgd, Map<String, IValueGenerator> generators, List<GeneratorWithPath> functionGeneratorsForSkolemFunctions) {
        for (VariableJoinCondition joinCondition : tgd.getTargetView().getAllJoinConditions()) {
            addStandardSkolemGenerators(tgd, joinCondition, generators, functionGeneratorsForSkolemFunctions);
        }
    }

    private void addStandardSkolemGenerators(FORule tgd, VariableJoinCondition joinCondition,
            Map<String, IValueGenerator> generators, List<GeneratorWithPath> functionGeneratorsForSkolems) {
        for (int i = 0; i < joinCondition.getToPaths().size(); i++) {
            VariablePathExpression toPath = joinCondition.getToPaths().get(i);
            VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
            IValueGenerator toGenerator = generators.get(toPath.toString());
            IValueGenerator fromGenerator = generators.get(fromPath.toString());
            if (toGenerator == null && fromGenerator != null && fromGenerator instanceof FunctionGenerator) {
                throw new IllegalMappingTaskException("Key does not have generator, while foreign key has: " + fromGenerator);
            }
            if (fromGenerator == null && toGenerator != null && toGenerator instanceof FunctionGenerator) {
                throw new IllegalMappingTaskException("Foreign key does not have generator, while key has: " + toGenerator);
            }
            if (toGenerator != null && fromGenerator == null) {
                if (toGenerator instanceof SkolemFunctionGenerator) {
                    SkolemFunctionGenerator toSkolemGenerator = (SkolemFunctionGenerator) toGenerator;
                    toSkolemGenerator.addJoinCondition(joinCondition);
                }
                generators.put(fromPath.toString(), toGenerator);
            } else if (toGenerator == null && fromGenerator != null) {
                if (fromGenerator instanceof SkolemFunctionGenerator) {
                    SkolemFunctionGenerator fromSkolemGenerator = (SkolemFunctionGenerator) fromGenerator;
                    fromSkolemGenerator.addJoinCondition(joinCondition);
                }
                generators.put(toPath.toString(), fromGenerator);
            } else if (toGenerator == null && fromGenerator == null) {
                SkolemFunctionGenerator toSkolemGenerator = new SkolemFunctionGenerator(toPath.getAbsolutePath().toString(), true, tgd, functionGeneratorsForSkolems);
                toSkolemGenerator.setType(SkolemFunctionGenerator.STANDARD);
                toSkolemGenerator.addJoinCondition(joinCondition);
                if (joinCondition.getFromPaths().size() > 1) {
                    toSkolemGenerator.setPosition(i);
                }
                generators.put(toPath.toString(), toSkolemGenerator);
                generators.put(fromPath.toString(), toSkolemGenerator);
            }
        }
    }

    private void addSkolemGeneratorsForKeys(FORule tgd, Map<String, IValueGenerator> generators, List<GeneratorWithPath> functionGenerators, IDataSourceProxy target) {
        for (SetAlias targetSetVariable : tgd.getTargetView().getGenerators()) {
            VariableFunctionalDependency primaryKey = findPrimaryKeyForSet(targetSetVariable, target);
            if (primaryKey != null) {
                for (int i = 0; i < primaryKey.getLeftPaths().size(); i++) {
                    VariablePathExpression keyPath = primaryKey.getLeftPaths().get(i);
                    VariablePathExpression correctedPath = new VariablePathExpression(keyPath, targetSetVariable);
                    IValueGenerator keyGenerator = generators.get(correctedPath.toString());
                    if (keyGenerator == null) {
                        List<GeneratorWithPath> generatorsForKey = findGeneratorsForKey(keyPath, functionGenerators);
                        SkolemFunctionGenerator keySkolemGenerator = new SkolemFunctionGenerator(correctedPath.getAbsolutePath().toString(), true, tgd, generatorsForKey);
                        keySkolemGenerator.setType(SkolemFunctionGenerator.KEY);
                        keySkolemGenerator.addFunctionalDependencies(primaryKey);
                        keySkolemGenerator.setPosition(i);
                        generators.put(correctedPath.toString(), keySkolemGenerator);
                    }
                }
            }
        }
    }

    private VariableFunctionalDependency findPrimaryKeyForSet(SetAlias targetVariable, IDataSourceProxy target) {
        for (VariableFunctionalDependency functionalDependency : target.getMappingData().getDependencies()) {
            if (functionalDependency.isPrimaryKey()) {
                SetAlias keyVariable = functionalDependency.getVariable();
                if (keyVariable.equals(targetVariable)) {
                    return functionalDependency;
                }
            }
        }
        return null;
    }

    private List<GeneratorWithPath> findGeneratorsForKey(VariablePathExpression keyPath, List<GeneratorWithPath> functionGenerators) {
        List<GeneratorWithPath> result = new ArrayList<GeneratorWithPath>();
        for (GeneratorWithPath generatorWithPath : functionGenerators) {
            VariablePathExpression targetPath = generatorWithPath.getTargetPath();
            if (contains(targetPath.getStartingVariable(), keyPath.getStartingVariable().getGenerators())) {
                result.add(generatorWithPath);
            }
        }
        return result;
    }

    private boolean contains(SetAlias startingVariable, List<SetAlias> generators) {
        for (SetAlias setAlias : generators) {
            if (setAlias.equalsOrIsClone(startingVariable)) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////////   EGDs   /////////////////////////////////////////////
    private void findGeneratorsForSkolemsForEGDs(FORule tgd, MappingTask mappingTask, Map<String, IValueGenerator> generators, List<GeneratorWithPath> functionGenerators) {
        TGDImplicants variableImplicants = implicantsFinder.findMinimalImplicants(tgd, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("****************  Generating EGD-based skolem function generators for tgd " + tgd);
        if (logger.isDebugEnabled()) logger.debug("****************  Generating EGD-based skolem function generators for tgd " + tgd.toLogicalString(mappingTask));
        if (logger.isDebugEnabled()) logger.debug("****************  Implicants map: " + variableImplicants);
        for (FormulaVariable existentialVariable : tgd.getExistentialFormulaVariables(mappingTask)) {
            if (logger.isDebugEnabled()) logger.debug("****************  Analyzing existential variable: " + existentialVariable);
            if (existentialVariable.isPureNull()) {
                if (isNotInPrimaryKey(existentialVariable, mappingTask.getTargetProxy())) {
                    if (logger.isDebugEnabled()) logger.debug("****************  Variable is pure null, continue...");
                    continue;
                } else {
                    // variable is a primary key: check if it has determinations...
                    if (hasNoDetermination(existentialVariable, variableImplicants, functionGenerators)) {
                        continue;
                    }
                }
            }
            Determination implicants = variableImplicants.getMinimalImplicants(existentialVariable);
            if (logger.isDebugEnabled()) logger.debug("****************  Implicants for variable: " + implicants);
            if (!implicants.getGeneratingDependencies().isEmpty()) {
                List<GeneratorWithPath> generatorsForImplicants = findGeneratorsForImplicants(implicants, functionGenerators);
                SkolemFunctionGenerator skolemFunction = new SkolemFunctionGenerator(existentialVariable.getId(), true, tgd, generatorsForImplicants);
                skolemFunction.setType(SkolemFunctionGenerator.EGD_BASED);
                skolemFunction.setFunctionalDependencies(implicants.getGeneratingDependencies());
                for (VariablePathExpression targetOccurrence : existentialVariable.getTargetOccurrencePaths()) {
                    generators.put(targetOccurrence.toString(), skolemFunction);
                }
            }
        }
    }

    private boolean hasNoDetermination(FormulaVariable existentialVariable, TGDImplicants variableImplicants, List<GeneratorWithPath> functionGenerators) {
        try {
            Determination implicants = variableImplicants.getMinimalImplicants(existentialVariable);
            if (!implicants.getGeneratingDependencies().isEmpty()) {
                findGeneratorsForImplicants(implicants, functionGenerators);
            }
            return false;
        } catch (Exception ex) {
            return true;
        }
    }

    private List<GeneratorWithPath> findGeneratorsForImplicants(Determination implicants, List<GeneratorWithPath> functionGenerators) {
        List<GeneratorWithPath> result = new ArrayList<GeneratorWithPath>();
        for (FormulaVariable formulaVariable : implicants.getImplicants()) {
            GeneratorWithPath generatorForVariable = findGeneratorForTargetPath(functionGenerators, formulaVariable.getTargetOccurrencePaths().get(0));
            if (generatorForVariable == null) {
                throw new IllegalArgumentException("Unable to find generator for variable " + formulaVariable.toLongString() + " in generators :" + functionGenerators);
            }
            result.add(generatorForVariable);
        }
        return result;
    }

    private GeneratorWithPath findGeneratorForTargetPath(List<GeneratorWithPath> functionGenerators, VariablePathExpression targetPath) {
        if (logger.isDebugEnabled()) logger.debug("Finding generator for path " + targetPath + " in: " + SpicyEngineUtility.printCollection(functionGenerators));
        for (GeneratorWithPath generatorWithPath : functionGenerators) {
            if (generatorWithPath.getTargetPath().equalsUpToClonesAndHasSameVariableId(targetPath)) {
                return generatorWithPath;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Not found, returning null...");
        return null;
    }

    private boolean isNotInPrimaryKey(FormulaVariable existentialVariable, IDataSourceProxy target) {
        PathExpression variablePath = existentialVariable.getTargetOccurrencePaths().get(0).getAbsolutePath();
        for (KeyConstraint keyConstraint : target.getKeyConstraints()) {
            if (keyConstraint.isPrimaryKey() && keyConstraint.getKeyPaths().contains(variablePath)) {
                return false;
            }
        }
        return true;
    }
}
