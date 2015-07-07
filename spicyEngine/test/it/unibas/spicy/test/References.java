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
 
package it.unibas.spicy.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class References {

    private static Log logger = LogFactory.getLog(References.class);

    //////////////////////////////////////////// MAPPING TASK /////////////////////////////////////////////////

    public static final String childrenDBKidsPresents = "/resources/childrenDBKidsPresentsDB/childrenDBKidsPresentsDB-mappingTask.xml";
    public static final String childrenDBKidsPresentsMandatory = "/resources/childrenDBKidsPresentsDB/childrenDBKidsPresentsDB-mandatory-mappingTask.xml";
    public static final String childrenDBKidsPresentsKeys = "/resources/childrenDBKidsPresentsDB/childrenDBKidsPresentsDBKeys-mappingTask.xml";
    public static final String kidsPresentsChildrenDB = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBChildrenDB-mappingTask.xml";
    public static final String kidsPresentsChildrenDBKeys = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBChildrenDBKeys-mappingTask.xml";
    public static final String kidsPresentsChildrenDBSelfJoin = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBChildrenDBSelfJoin-mappingTask.xml";
    public static final String kidsPresentsChildrenDBSelfJoinKeys = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBChildrenDBSelfJoinKeys-mappingTask.xml";
    public static final String kidsPresentsChildrenDBSimpleSelfJoin = "/resources/kidsChildrenSimple/kidsPresentsDBChildrenDB-mappingTask.xml";
    public static final String nestedST = "/resources/nestedST/nestedST-mappingTask.xml";
    public static final String statDBexpenseDB = "/resources/statDBExpenseDB/statDBExpenseDB-mappingTask.xml";
    public static final String statDBexpenseDBMandatory = "/resources/statDBExpenseDB/statDBExpenseDB-mandatory-mappingTask.xml";
    public static final String statDBexpenseDBMandatory2 = "/resources/statDBExpenseDB/statDBExpenseDB-mandatory2-mappingTask.xml";
    public static final String expenseDBStatDB = "/resources/statDBExpenseDB/expenseDBStatDB-mappingTask.xml";
    public static final String expenseDBStatDBEGD = "/resources/statDBExpenseDB/expenseDBStatDBEGD-mappingTask.xml";
    public static final String expenseDBStatDBMandatory = "/resources/statDBExpenseDB/expenseDBStatDB-mandatory-mappingTask.xml";
    public static final String expenseDBStatDBChaining = "/resources/chaining/expenseDBStatDB-mappingTask.xml";
    public static final String livesIn = "/resources/livesIn/livesIn-mappingTask.xml";
    public static final String livesInSelectionConditions = "/resources/livesIn/livesIn-SelectionConditions-mappingTask.xml";
    public static final String needsLab = "/resources/needsLab/needsLab-mappingTask.xml";
    public static final String deptOtherDept = "/resources/deptOtherDept/deptOtherDept-mappingTask.xml";
    public static final String deptOtherDeptMandatory = "/resources/deptOtherDept/deptOtherDept-mandatory-mappingTask.xml";
    public static final String deptOtherDeptBasic = "/resources/deptOtherDept/deptOtherDept-mandatory-mappingTask-Basic.xml";
    public static final String bookstore = "/resources/bookstore/personsBooksBookstore-mappingTask.xml";
    // coverage
    public static final String coverageOriginal = "/resources/coverage/coverageOriginal-mappingTask.xml";
    public static final String coverageOriginalVariant = "/resources/coverage/coverageOriginalVariant-mappingTask.xml";
    public static final String coverage = "/resources/coverage/coverage-mappingTask.xml";
    public static final String coverageEasy = "/resources/coverage/coverageEasy-mappingTask.xml";
    public static final String coverageBig = "/resources/coverage/coverageBig-mappingTask.xml";
    public static final String coverageBig2 = "/resources/coverage/coverageBig2-mappingTask.xml";
    public static final String coverageBig3 = "/resources/coverage/coverageBig3-mappingTask.xml";
    // STBenchMark
    public static final String stBenchmarkCopying = "/resources/STBenchmark/01Copying/01Copying.xml";
    public static final String stBenchmarkConstantValueGeneration = "/resources/STBenchmark/02ConstantValueGeneration/02ConstantValueGeneration.xml";
    public static final String stBenchmarkHorizontalPartitionSelection = "/resources/STBenchmark/03HorizontalPartition/03HorizontalPartitionSelection.xml";
    public static final String stBenchmarkSurrogateKeyAssignment = "/resources/STBenchmark/04SurrogateKeyAssignment/04SurrogateKeyAssignment.xml";
    public static final String stBenchmarkVerticalPartition = "/resources/STBenchmark/05VerticalPartition/05VerticalPartition-mappingTask.xml";
    public static final String stBenchmarkUnnesting = "/resources/STBenchmark/06Unnesting/06Unnesting.xml";
    public static final String stBenchmarkNesting = "/resources/STBenchmark/07Nesting/07Nesting.xml";
    public static final String stBenchmarkSelfJoins = "/resources/STBenchmark/08SelfJoins/08SelfJoins-mappingTask.xml";
    public static final String stBenchmarkSelfJoinsInverse = "/resources/STBenchmark/08SelfJoins/08SelfJoinsInverse-mappingTask.xml";
    public static final String stBenchmarkDenormalization = "/resources/STBenchmark/09Denormalization/09Denormalization.xml";
    public static final String stBenchmarkKeysAndObjectFusion = "/resources/STBenchmark/10KeysAndObjectFusion/10KeysAndObjectFusion.xml";
    public static final String stBenchmarkAtomicValueManagement = "/resources/STBenchmark/11AtomicValueManagement/11AtomicValueManagement.xml";
    // self-joins
    public static final String SJ7 = "/resources/selfjoins/sj7/sj7-mappingTask.xml";
    public static final String chaseRS = "/resources/selfjoins/chaseRS/chaseRS-mappingTask.xml";
    public static final String chaseRSRed = "/resources/selfjoins/chaseRS/chaseRS-mappingTaskRed.xml";
    public static final String RSCycle = "/resources/selfjoins/RSCycle/RSCycle-mappingTask.xml";
    public static final String RSCycleRed = "/resources/selfjoins/RSCycle/RSCycle-mappingTaskRed.xml";
    public static final String RSCycle2 = "/resources/selfjoins/RSCycle2/RSCycle2-mappingTask.xml";
    public static final String RSCycle2Red = "/resources/selfjoins/RSCycle2/RSCycle2-mappingTaskRed.xml";
    public static final String RSImproperSelfJoin = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-mappingTask.xml";
    public static final String RSImproperSelfJoinRed = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-mappingTaskRed.xml";
    public static final String RSNoCoverage = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-mappingTask.xml";
    public static final String RSNoCoverageRed = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-mappingTaskRed.xml";
    public static final String RSSimpleCoverage = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-mappingTask.xml";
    public static final String RSSimpleCoverageRed = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-mappingTaskRed.xml";
    public static final String RSNostro = "/resources/selfjoins/RSNostro/RSNostro-mappingTask.xml";
    public static final String RSNostroRed = "/resources/selfjoins/RSNostro/RSNostro-mappingTaskRed.xml";
    public static final String RSSkolem = "/resources/selfjoins/RSSkolem/RSSkolem-mappingTask.xml";
    public static final String RSSkolemRed = "/resources/selfjoins/RSSkolem/RSSkolem-mappingTaskRed.xml";
    public static final String mixed = "/resources/selfjoins/mixed/SelfJoinSubsumption-mappingTask.xml";
    public static final String mixedRed = "/resources/selfjoins/mixed/SelfJoinSubsumption-mappingTaskRed.xml";
    public static final String rsSingle = "/resources/selfjoins/RSSingle/RSSingle-gen.tgd";
    public static final String sjRewritingExample = "/resources/selfjoins/sjRewritingExample/sjRewritingExample-gen.tgd";
    // cycles
    public static final String cyclesEasy = "/resources/cycles/cyclesEasy-mappingTask.xml";
    public static final String cyclesSelf = "/resources/cycles/cyclesSelf-mappingTask.xml";
    public static final String cyclesTransitive = "/resources/cycles/cyclesTransitive-mappingTask.xml";

    // egds
    public static final String personParent = "/resources/egds/personParent/personParent-gen.tgd";
    public static final String livesInEGD = "/resources/egds/livesIn/livesIn-gen.tgd";
    public static final String parallelVertical = "/resources/egds/08parallelVertical/parallelVertical-gen.tgd";
    public static final String manyToMany = "/resources/egds/07manyToMany/manyToMany-gen.tgd";
    public static final String starJoin = "/resources/egds/06starJoin/starJoin-gen.tgd";
    public static final String doubleJoin = "/resources/egds/05doubleJoin/doubleJoin-gen.tgd";
    public static final String doubleVertical = "/resources/egds/04doubleVertical/doubleVertical-gen.tgd";
    public static final String verticalPartition1 = "/resources/egds/01verticalPartition1/verticalPartition1-gen.tgd";
    public static final String verticalPartition2 = "/resources/egds/01verticalPartition2/verticalPartition2-gen.tgd";
    public static final String personCarCity = "/resources/egds/personCarCity/personCarCity-gen.tgd";
    public static final String morris = "/resources/egds/morris/morris-gen.tgd";
    public static final String secondaryOverlap = "/resources/egds/09secondaryOverlap/secondaryOverlap-gen.tgd";
    public static final String mergeTuples = "/resources/egds/02mergeTuples/mergeTuples-gen.tgd";
    public static final String verticalMerge = "/resources/egds/03verticalMerge/verticalMerge-gen.tgd";
    public static final String extendedOverlaps = "/resources/egds/extendedOverlaps/extendedOverlaps-gen.tgd";
    public static final String overlapWithDuplicate = "/resources/egds/overlapWithDuplicate/overlapWithDuplicate-gen.tgd";
    public static final String falseOverlaps = "/resources/egds/falseOverlaps/falseOverlaps-gen.tgd";
    public static final String falseOverlaps2 = "/resources/egds/falseOverlaps/falseOverlaps2-gen.tgd";
    public static final String secondIterationOverlap = "/resources/egds/secondIterationOverlap/secondIterationOverlap-gen.tgd";
    public static final String complexOverlaps = "/resources/egds/complexOverlaps/complexOverlaps-gen.tgd";
    public static final String complexOverlapsRed1 = "/resources/egds/complexOverlaps/complexOverlapsRed1-gen.tgd";
    public static final String complexOverlapsRed2 = "/resources/egds/complexOverlaps/complexOverlapsRed2-gen.tgd";
    public static final String doubleOverlap1 = "/resources/egds/doubleOverlap/doubleOverlap1-gen.tgd";
    public static final String doubleOverlap2 = "/resources/egds/doubleOverlap/doubleOverlap2-gen.tgd";
    public static final String selfJoinOverlap = "/resources/egds/selfJoinOverlap/selfJoinOverlap-gen.tgd";
    public static final String nestedEgdHobby = "/resources/egds/nestedEgdHobby/nestedEgd-mappingTask.xml";
    public static final String nestedEgdSports = "/resources/egds/nestedEgdSports/nestedEgd-mappingTask.xml";
    public static final String nestedEgdBoss = "/resources/egds/nestedEgdBoss/nestedEgd-mappingTask.xml";

    public static final String rsMini = "/resources/egds/varie/RSMini-gen.tgd";
    public static final String betaGamma = "/resources/egds/varie/betaGamma-gen.tgd";
    public static final String mixedNulls = "/resources/egds/varie/mixedNulls-gen.tgd";

    public static final String dataCleaningEmployee = "/resources/dataCleaning/employees/dataCleaningEmployees-gen.tgd";
    public static final String dataCleaningEmployeeRewriting = "/resources/dataCleaning/employees/dataCleaningEmployeesRewriting-gen.tgd";
    public static final String dataCleaningWithConditionEmployee = "/resources/dataCleaning/employees/dataCleaningWithConditionEmployees-gen.tgd";
    public static final String dataCleaningBooksRewriting = "/resources/dataCleaning/books/dataCleaningBooksMerge-gen.tgd";

    // ETL
    public static final String etlCreditCardMatchCustomers = "/resources/etl/creditCard/creditCardMatchCustomers-gen.tgd";
    public static final String etlCreditCardLoadRates = "/resources/etl/creditCard/creditCardLoadRates-gen.tgd";
    public static final String etlCreditCardRebaseCurrency = "/resources/etl/creditCard/creditCardRebaseCurrency-gen.tgd";
    public static final String etlCreditCardPartition = "/resources/etl/creditCard/creditCardPartition-gen.tgd";

    // parser
    public static final String livesInTGD = "/resources/livesIn/livesIn.tgd";
    public static final String livesInReorderedTGD = "/resources/livesIn/livesInReordered.tgd";
    public static final String livesInGenerateTGD = "/resources/livesIn/livesInGenerate.tgd";
    public static final String morrisTGD = "/resources/egds/morris/morris.tgd";
    public static final String needsLabTGD = "/resources/needsLab/needsLab.tgd";
    public static final String needsLabReorderedTGD = "/resources/needsLab/needsLab-reordered.tgd";
    public static final String chaseRSTGD = "/resources/selfjoins/chaseRS/chaseRS.tgd";
    public static final String chaseRSReorderedTGD = "/resources/selfjoins/chaseRS/chaseRS-reordered.tgd";
    public static final String rsNostroVariant = "/resources/selfjoins/RSNostro/RSNostroVariant-gen.tgd";
    public static final String automorphismTGD = "/resources/selfjoins/automorphism/automorphism-gen.tgd";
    public static final String negationExample = "/resources/parser/negationExample.tgd";
    public static final String negationExample2 = "/resources/parser/negationExample2.tgd";
    public static final String negationExample3 = "/resources/parser/negationExample3.tgd";
    public static final String negationExample3NoSelfJoin = "/resources/parser/negationExample3NoSelfJoin.tgd";
    public static final String negationExample4 = "/resources/parser/negationExample4.tgd";
    public static final String negationExample5 = "/resources/parser/rsSimpleMinicon.tgd";
    public static final String negationExample6 = "/resources/parser/rsSimpleinput.tgd";
    public static final String equalitiesExample1 = "/resources/parser/equalitiesExample1.tgd";

    // chase
    public static final String chaseExample1 = "/resources/chase/example1-subsumptionsAndCoverages-gen.tgd";
    public static final String chaseExample2 = "/resources/chase/example2-selfJoin-gen.tgd";
    public static final String chaseExample3 = "/resources/chase/example3-RSSingle-gen.tgd";
    public static final String chaseExample4 = "/resources/chase/example4-gen.tgd";

    // chaining
    public static final String chaining = "/resources/chaining/statDBExpenseDB-chaining-mappingTask.xml";
    public static final String chainingPreviousStep = "/resources/chaining/livesIn.tgd";
    public static final String chainingParser = "/resources/chaining/livesIn-chaining.tgd";

    // merge
    public static final String mergeTgd = "/resources/chaining/livesIn-merge.tgd";
    public static final String merge = "/resources/chaining/expenseDBLivesInStatDB-merge-mappingTask.xml";
    public static final String expenseDBStatDBConfig = "/resources/chaining/expenseDBStatDB-config-mappingTask.xml";
    public static final String livesInConfig = "/resources/chaining/livesIn-config.tgd";

    //////////////////////////////////////////// SQL /////////////////////////////////////////////////

    public static final String childrenDBSQLScriptSchema = "/resources/childrenDBKidsPresentsDB/childrenDB.sql";
    public static final String childrenDBSQLScriptInstances = "/resources/childrenDBKidsPresentsDB/childrenDB-instance.sql";
    public static final String kidsPresentsSQLScriptSchema = "/resources/childrenDBKidsPresentsDB/kidsPresentsDB.sql";
    public static final String kidsPresentsSQLScriptInstance = "/resources/childrenDBKidsPresentsDB/kidsPresentsDB-instance.sql";
    public static final String kidsPresentsExpectedInstance = "/resources/childrenDBKidsPresentsDB/kidsPresentsDB-instance-expected.csv";
    public static final String kidsPresentsKeysExpectedInstance = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBKeys-instance-expected.csv";
    public static final String kidsPresentsSelfJoinExpectedInstance = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBSelfJoin-instance-expected.csv";
    public static final String kidsPresentsSelfJoinKeysExpectedInstance = "/resources/childrenDBKidsPresentsDB/kidsPresentsDBSelfJoinKeys-instance-expected.csv";

    public static final String childrenDBSimpleSQLScriptSchema = "/resources/kidsChildrenSimple/childrenDB.sql";
    public static final String childrenDBSimpleSQLScriptInstances = "/resources/kidsChildrenSimple/childrenDB-instance.sql";
    public static final String kidsPresentsSimpleSQLScriptSchema = "/resources/kidsChildrenSimple/kidsPresentsDB.sql";
    public static final String kidsPresentsSimpleSQLScriptInstance = "/resources/kidsChildrenSimple/kidsPresentsDB-instance.sql";
    public static final String childrenSimpleExpectedInstance = "/resources/kidsChildrenSimple/kidsPresentsDB-instance-expected.csv";

    public static final String livesInSourceSQLScriptSchema = "/resources/livesIn/livesIn-source.sql";
    public static final String livesInSourceSQLScriptInstances = "/resources/livesIn/livesIn-instance.sql";
    public static final String livesInSelectionConditionsSourceSQLScriptInstances = "/resources/livesIn/livesIn-SelectionConditions-instance.sql";
    public static final String livesInTargetSQLScriptSchema = "/resources/livesIn/livesIn-target.sql";
    public static final String livesInExpectedInstance = "/resources/livesIn/livesIn-source-instance-expected.csv";
    public static final String livesInSelectionConditionsExpectedInstance = "/resources/livesIn/livesIn-SelectionConditions-source-instance-expected.csv";
    
    public static final String bookstoreSourceSQLScriptSchema = "/resources/bookstore/bookstore-source.sql";
    public static final String bookstoreSourceSQLScriptInstances = "/resources/bookstore/bookstore-instance.sql";
    public static final String bookstoreTargetSQLScriptSchema = "/resources/bookstore/bookstore-target.sql";
    public static final String bookstoreExpectedInstance = "/resources/bookstore/personsBooks-instance-expected.csv";

    public static final String coverageOriginalSourceSQLScriptSchema = "/resources/coverage/coverageOriginal-source.sql";
    public static final String coverageOriginalSourceSQLScriptInstances = "/resources/coverage/coverageOriginal-instance.sql";
    public static final String coverageOriginalTargetSQLScriptSchema = "/resources/coverage/coverageOriginal-target.sql";
    public static final String coverageOriginalExpectedInstance = "/resources/coverage/coverageOriginal-source-instance-expected.csv";

    public static final String coverageOriginalVariantSourceSQLScriptSchema = "/resources/coverage/coverageOriginalVariant-source.sql";
    public static final String coverageOriginalVariantSourceSQLScriptInstances = "/resources/coverage/coverageOriginalVariant-instance.sql";
    public static final String coverageOriginalVariantTargetSQLScriptSchema = "/resources/coverage/coverageOriginalVariant-target.sql";
    public static final String coverageOriginalVariantExpectedInstance = "/resources/coverage/coverageOriginalVariant-source-instance-expected.csv";

    public static final String coverageSourceSQLScriptSchema = "/resources/coverage/coverage-source.sql";
    public static final String coverageSourceSQLScriptInstances = "/resources/coverage/coverage-instance.sql";
    public static final String coverageTargetSQLScriptSchema = "/resources/coverage/coverage-target.sql";
    public static final String coverageExpectedInstance = "/resources/coverage/coverage-source-instance-expected.csv";

    public static final String coverageEasySourceSQLScriptSchema = "/resources/coverage/coverageEasy-source.sql";
    public static final String coverageEasySourceSQLScriptInstances = "/resources/coverage/coverageEasy-instance.sql";
    public static final String coverageEasyTargetSQLScriptSchema = "/resources/coverage/coverageEasy-target.sql";
    public static final String coverageEasyExpectedInstance = "/resources/coverage/coverageEasy-source-instance-expected.csv";

    public static final String coverageBigSourceSQLScriptSchema = "/resources/coverage/coverageBig-source.sql";
    public static final String coverageBigSourceSQLScriptInstances = "/resources/coverage/coverageBig-instance.sql";
    public static final String coverageBigTargetSQLScriptSchema = "/resources/coverage/coverageBig-target.sql";
    public static final String coverageBigExpectedInstance = "/resources/coverage/coverageBig-source-instance-expected.csv";

    public static final String coverageBig2SourceSQLScriptSchema = "/resources/coverage/coverageBig2-source.sql";
    public static final String coverageBig2SourceSQLScriptInstances = "/resources/coverage/coverageBig2-instance.sql";
    public static final String coverageBig2TargetSQLScriptSchema = "/resources/coverage/coverageBig2-target.sql";
    public static final String coverageBig2ExpectedInstance = "/resources/coverage/coverageBig2-source-instance-expected.csv";

    public static final String coverageBig3SourceSQLScriptSchema = "/resources/coverage/coverageBig3-source.sql";
    public static final String coverageBig3SourceSQLScriptInstances = "/resources/coverage/coverageBig3-instance.sql";
    public static final String coverageBig3TargetSQLScriptSchema = "/resources/coverage/coverageBig3-target.sql";
    public static final String coverageBig3ExpectedInstance = "/resources/coverage/coverageBig3-source-instance-expected.csv";
    
    public static final String negation3SourceSQLScriptSchema = "/resources/parser/negation3-source.sql";
    public static final String negation3SourceSQLScriptInstances = "/resources/parser/negation3-instance.sql";
    public static final String negation3TargetSQLScriptSchema = "/resources/parser/negation3-target.sql";
    public static final String negation3ExpectedInstance = "/resources/parser/negationExample3-instance0-expected.csv";
    public static final String negation3NoSelfJoinExpectedInstance = "/resources/parser/negationExample3NoSelfJoin-instance0-expected.csv";
    
    public static final String negation5SourceSQLScriptSchema = "/resources/parser/rsSimpleMinicon-source.sql";
    public static final String negation5SourceSQLScriptInstances = "/resources/parser/rsSimpleMinicon-instance.sql";
    public static final String negation5TargetSQLScriptSchema = "/resources/parser/rsSimpleMinicon-target.sql";
    public static final String negation5ExpectedInstance = "/resources/parser/rsSimpleMinicon-instance0-expected.csv";
    

    // EGDS

    public static final String extendedOverlapsSourceSQLScriptSchema = "/resources/egds/extendedOverlaps/extendedOverlaps-source.sql";
    public static final String extendedOverlapsSourceSQLScriptInstances = "/resources/egds/extendedOverlaps/extendedOverlaps-source-instance.sql";
    public static final String extendedOverlapsTargetSQLScriptSchema = "/resources/egds/extendedOverlaps/extendedOverlaps-target.sql";
    public static final String extendedOverlapsExpectedInstance = "/resources/egds/extendedOverlaps/extendedOverlaps-gen-instance0-expected.csv";

    public static final String complexOverlapsRed1SourceSQLScriptSchema = "/resources/egds/complexOverlaps/complexOverlapsRed1-source.sql";
    public static final String complexOverlapsRed1SourceSQLScriptInstances = "/resources/egds/complexOverlaps/complexOverlapsRed1-source-instance.sql";
    public static final String complexOverlapsRed1TargetSQLScriptSchema = "/resources/egds/complexOverlaps/complexOverlapsRed1-target.sql";
    public static final String complexOverlapsRed1ExpectedInstance = "/resources/egds/complexOverlaps/complexOverlapsRed1-gen-instance0-expected.csv";

    public static final String morrisSourceSQLScriptSchema = "/resources/egds/morris/morris-source.sql";
    public static final String morrisSourceSQLScriptInstances = "/resources/egds/morris/morris-source-instance.sql";
    public static final String morrisTargetSQLScriptSchema = "/resources/egds/morris/morris-target.sql";
    public static final String morrisExpectedInstance = "/resources/egds/morris/morris-source-instance-expected.csv";

    public static final String secondIterationOverlapSourceSQLScriptSchema = "/resources/egds/secondIterationOverlap/secondIterationOverlap-source.sql";
    public static final String secondIterationOverlapSourceSQLScriptInstances = "/resources/egds/secondIterationOverlap/secondIterationOverlap-source-instance.sql";
    public static final String secondIterationOverlapTargetSQLScriptSchema = "/resources/egds/secondIterationOverlap/secondIterationOverlap-target.sql";
    public static final String secondIterationOverlapExpectedInstance = "/resources/egds/secondIterationOverlap/secondIterationOverlap-gen-instance0-expected.csv";

    public static final String personCarCitySourceSQLScriptSchema = "/resources/egds/personCarCity/personCarCity-source.sql";
    public static final String personCarCitySourceSQLScriptInstances = "/resources/egds/personCarCity/personCarCity-source-instance.sql";
    public static final String personCarCityTargetSQLScriptSchema = "/resources/egds/personCarCity/personCarCity-target.sql";
    public static final String personCarCityExpectedInstance = "/resources/egds/personCarCity/personCarCity-source-instance-expected.csv";

    // DATA CLEANING
    public static final String dataCleaningEmployeeSourceSQLScriptSchema = "/resources/dataCleaning/employees/dataCleaningEmployees-source.sql";
    public static final String dataCleaningEmployeeSourceSQLScriptInstances = "/resources/dataCleaning/employees/dataCleaningEmployees-source-instance.sql";
    public static final String dataCleaningEmployeeTargetSQLScriptSchema = "/resources/dataCleaning/employees/dataCleaningEmployees-target.sql";
    public static final String dataCleaningEmployeeExpectedInstance = "/resources/dataCleaning/employees/dataCleaningEmployees-gen-instance0-expected.csv";
    
    public static final String dataCleaningWithConditionEmployeeSourceSQLScriptSchema = dataCleaningEmployeeSourceSQLScriptSchema;
    public static final String dataCleaningWithConditionEmployeeSourceSQLScriptInstances = "/resources/dataCleaning/employees/dataCleaningWithConditionEmployees-source-instance.sql";
    public static final String dataCleaningWithConditionEmployeeTargetSQLScriptSchema = dataCleaningEmployeeTargetSQLScriptSchema;
    public static final String dataCleaningWithConditionEmployeeExpectedInstance = "/resources/dataCleaning/employees/dataCleaningWithConditionEmployees-gen-instance0-expected.csv";
    
    // CHAINING
    public static final String livesInChainingSourceSQLScriptSchema = "/resources/chaining/livesInChaining-source.sql";
    public static final String livesInChainingSourceSQLScriptInstances = "/resources/chaining/livesInChaining-instance.sql";
    public static final String livesInChainingTargetSQLScriptSchema = "/resources/chaining/livesInChaining-target.sql";
    public static final String livesInChainingIntermediateSQLScriptSchema = "/resources/chaining/livesInChaining-intermediate.sql";
    public static final String livesInChainingExpectedInstance = "/resources/chaining/livesInChaining-source-instance-expected.csv";
    
    // DOUBLE EXCHANGE
    public static final String rsNoCoverageSourceSQLScriptSchema = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_6 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance6.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_10 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance10.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_20 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance20.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_150 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance150.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_300 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance300.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_64 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance64.sql";
    public static final String rsNoCoverageSourceSQLScriptInstances_192 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance192.sql";
    public static final String rsNoCoverageTargetSQLScriptSchema = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-target.sql";
    public static final String rsNoCoverageExpectedInstance = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance-expected.csv";
    public static final String rsNoCoverageExpectedInstance_6 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance6-expected.csv";
    public static final String rsNoCoverageExpectedInstance_10 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance10-expected.csv";
    public static final String rsNoCoverageExpectedInstance_20 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance20-expected.csv";
    public static final String rsNoCoverageExpectedInstance_150 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance150-expected.csv";
    public static final String rsNoCoverageExpectedInstance_300 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance300-expected.csv";
    public static final String rsNoCoverageExpectedInstance_64 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance64-expected.csv";
    public static final String rsNoCoverageExpectedInstance_192 = "/resources/selfjoins/RSNoCoverage/RSNoCoverage-source-instance192-expected.csv";

    public static final String rsSimpleCoverageSourceSQLScriptSchema = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_6 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance6.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_10 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance10.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_20 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance20.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_150 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance150.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_300 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance300.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_64 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance64.sql";
    public static final String rsSimpleCoverageSourceSQLScriptInstances_192 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance192.sql";
    public static final String rsSimpleCoverageTargetSQLScriptSchema = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-target.sql";
    public static final String rsSimpleCoverageExpectedInstance = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_6 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance6-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_10 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance10-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_20 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance20-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_150 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance150-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_300 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance300-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_64 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance64-expected.csv";
    public static final String rsSimpleCoverageExpectedInstance_192 = "/resources/selfjoins/RSSimpleCoverage/RSSimpleCoverage-source-instance192-expected.csv";

    public static final String rsCycleSourceSQLScriptSchema = "/resources/selfjoins/RSCycle/RSCycle-source.sql";
    public static final String rsCycleSourceSQLScriptInstances = "/resources/selfjoins/RSCycle/RSCycle-source-instance.sql";
    public static final String rsCycleTargetSQLScriptSchema = "/resources/selfjoins/RSCycle/RSCycle-target.sql";
    public static final String rsCycleExpectedInstance = "/resources/selfjoins/RSCycle/RSCycle-source-instance-expected.csv";
    public static final String rsCycleSourceSQLScriptInstances_6 = "/resources/selfjoins/RSCycle/RSCycle-source-instance6.sql";
    public static final String rsCycleSourceSQLScriptInstances_10 = "/resources/selfjoins/RSCycle/RSCycle-source-instance10.sql";
    public static final String rsCycleSourceSQLScriptInstances_20 = "/resources/selfjoins/RSCycle/RSCycle-source-instance20.sql";
    public static final String rsCycleSourceSQLScriptInstances_100 = "/resources/selfjoins/RSCycle/RSCycle-source-instance100.sql";
    public static final String rsCycleSourceSQLScriptInstances_200 = "/resources/selfjoins/RSCycle/RSCycle-source-instance200.sql";
    public static final String rsCycleSourceSQLScriptInstances_64 = "/resources/selfjoins/RSCycle/RSCycle-source-instance64.sql";
    public static final String rsCycleSourceSQLScriptInstances_128 = "/resources/selfjoins/RSCycle/RSCycle-source-instance128.sql";
    public static final String rsCycleExpectedInstance_6 = "/resources/selfjoins/RSCycle/RSCycle-source-instance6-expected.csv";
    public static final String rsCycleExpectedInstance_10 = "/resources/selfjoins/RSCycle/RSCycle-source-instance10-expected.csv";
    public static final String rsCycleExpectedInstance_20 = "/resources/selfjoins/RSCycle/RSCycle-source-instance20-expected.csv";
    public static final String rsCycleExpectedInstance_100 = "/resources/selfjoins/RSCycle/RSCycle-source-instance100-expected.csv";
    public static final String rsCycleExpectedInstance_200 = "/resources/selfjoins/RSCycle/RSCycle-source-instance200-expected.csv";
    public static final String rsCycleExpectedInstance_64 = "/resources/selfjoins/RSCycle/RSCycle-source-instance64-expected.csv";
    public static final String rsCycleExpectedInstance_128 = "/resources/selfjoins/RSCycle/RSCycle-source-instance128-expected.csv";

    public static final String rsCycle2SourceSQLScriptSchema = "/resources/selfjoins/RSCycle2/RSCycle2-source.sql";
    public static final String rsCycle2SourceSQLScriptInstances = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance.sql";
    public static final String rsCycle2TargetSQLScriptSchema = "/resources/selfjoins/RSCycle2/RSCycle2-target.sql";
    public static final String rsCycle2ExpectedInstance = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance-expected.csv";
    public static final String rsCycle2SourceSQLScriptInstances_6 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance6.sql";
    public static final String rsCycle2SourceSQLScriptInstances_10 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance10.sql";
    public static final String rsCycle2SourceSQLScriptInstances_20 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance20.sql";
    public static final String rsCycle2SourceSQLScriptInstances_100 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance100.sql";
    public static final String rsCycle2SourceSQLScriptInstances_200 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance200.sql";
    public static final String rsCycle2SourceSQLScriptInstances_64 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance64.sql";
    public static final String rsCycle2SourceSQLScriptInstances_128 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance128.sql";
    public static final String rsCycle2ExpectedInstance_6 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance6-expected.csv";
    public static final String rsCycle2ExpectedInstance_10 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance10-expected.csv";
    public static final String rsCycle2ExpectedInstance_20 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance20-expected.csv";
    public static final String rsCycle2ExpectedInstance_100 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance100-expected.csv";
    public static final String rsCycle2ExpectedInstance_200 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance200-expected.csv";
    public static final String rsCycle2ExpectedInstance_64 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance64-expected.csv";
    public static final String rsCycle2ExpectedInstance_128 = "/resources/selfjoins/RSCycle2/RSCycle2-source-instance128-expected.csv";

    public static final String rsImproperSelfJoinSourceSQLScriptSchema = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance.sql";
    public static final String rsImproperSelfJoinTargetSQLScriptSchema = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-target.sql";
    public static final String rsImproperSelfJoinExpectedInstance = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance-expected.csv";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_2 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance2.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_10 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance10.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_20 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance20.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_100 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance100.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_200 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance200.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_64 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance64.sql";
    public static final String rsImproperSelfJoinSourceSQLScriptInstances_128 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance128.sql";
    public static final String rsImproperSelfJoinExpectedInstance_2 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance2-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_10 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance10-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_20 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance20-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_100 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance100-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_200 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance200-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_64 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance64-expected.csv";
    public static final String rsImproperSelfJoinExpectedInstance_128 = "/resources/selfjoins/RSImproperSelfJoin/RSImproperSelfJoin-source-instance128-expected.csv";

    public static final String rsNostroSourceSQLScriptSchema = "/resources/selfjoins/RSNostro/RSNostro-source.sql";
    public static final String rsNostroSourceSQLScriptInstances = "/resources/selfjoins/RSNostro/RSNostro-source-instance.sql";
    public static final String rsNostroSourceSQLScriptInstances_10 = "/resources/selfjoins/RSNostro/RSNostro-source-instance10.sql";
    public static final String rsNostroSourceSQLScriptInstances_20 = "/resources/selfjoins/RSNostro/RSNostro-source-instance20.sql";
    public static final String rsNostroSourceSQLScriptInstances_2bis = "/resources/selfjoins/RSNostro/RSNostro-source-instance2bis.sql";
    public static final String rsNostroSourceSQLScriptInstances_256 = "/resources/selfjoins/RSNostro/RSNostro-source-instance256.sql";
    public static final String rsNostroTargetSQLScriptSchema = "/resources/selfjoins/RSNostro/RSNostro-target.sql";
    public static final String rsNostroExpectedInstance = "/resources/selfjoins/RSNostro/RSNostro-source-instance-expected.csv";
    public static final String rsNostroExpectedInstance_10 = "/resources/selfjoins/RSNostro/RSNostro-source-instance10-expected.csv";
    public static final String rsNostroExpectedInstance_20 = "/resources/selfjoins/RSNostro/RSNostro-source-instance20-expected.csv";
    public static final String rsNostroExpectedInstance_2bis = "/resources/selfjoins/RSNostro/RSNostro-source-instance2bis-expected.csv";
    public static final String rsNostroExpectedInstance_256 = "/resources/selfjoins/RSNostro/RSNostro-source-instance256-expected.csv";

    public static final String rsSkolemSourceSQLScriptSchema = "/resources/selfjoins/RSSkolem/RSSkolem-source.sql";
    public static final String rsSkolemSourceSQLScriptInstances = "/resources/selfjoins/RSSkolem/RSSkolem-source-instance27.sql";
    public static final String rsSkolemTargetSQLScriptSchema = "/resources/selfjoins/RSSkolem/RSSkolem-target.sql";
    public static final String rsSkolemExpectedInstance = "/resources/selfjoins/RSSkolem/RSSkolem-source-instance27-expected.csv";

    public static final String selfJoinInverseSourceSQLScriptSchema = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-source.sql";
    public static final String selfJoinInverseSourceSQLScriptInstances = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-source-instance.sql";
    public static final String selfJoinInverseSourceSQLScriptInstances_100 = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-source-instance100.sql";
    public static final String selfJoinInverseTargetSQLScriptSchema = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-target.sql";
    public static final String selfJoinInverseExpectedInstance = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-source-instance-expected.csv";
    public static final String selfJoinInverseExpectedInstance_100 = "/resources/STBenchmark/08SelfJoins/selfJoinInverse-source-instance100-expected.csv";

    public static final String chaseRSSourceSQLScriptSchema = "/resources/selfjoins/chaseRS/chaseRS-source.sql";
    public static final String chaseRSSourceSQLScriptInstances = "/resources/selfjoins/chaseRS/chaseRS-source-instance.sql";
    public static final String chaseRSTargetSQLScriptSchema = "/resources/selfjoins/chaseRS/chaseRS-target.sql";
    public static final String chaseRSExpectedInstance = "/resources/selfjoins/chaseRS/chaseRS-source-instance-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_2 = "/resources/selfjoins/chaseRS/chaseRS-source-instance2.sql";
    public static final String chaseRSExpectedInstance_2 = "/resources/selfjoins/chaseRS/chaseRS-source-instance2-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_2bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance2bis.sql";
    public static final String chaseRSExpectedInstance_2bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance2bis-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_5 = "/resources/selfjoins/chaseRS/chaseRS-source-instance5.sql";
    public static final String chaseRSExpectedInstance_5 = "/resources/selfjoins/chaseRS/chaseRS-source-instance5-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_10 = "/resources/selfjoins/chaseRS/chaseRS-source-instance10.sql";
    public static final String chaseRSExpectedInstance_10 = "/resources/selfjoins/chaseRS/chaseRS-source-instance10-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_10bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance10bis.sql";
    public static final String chaseRSExpectedInstance_10bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance10bis-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_10ter = "/resources/selfjoins/chaseRS/chaseRS-source-instance10ter.sql";
    public static final String chaseRSExpectedInstance_10ter = "/resources/selfjoins/chaseRS/chaseRS-source-instance10ter-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_20 = "/resources/selfjoins/chaseRS/chaseRS-source-instance20.sql";
    public static final String chaseRSExpectedInstance_20 = "/resources/selfjoins/chaseRS/chaseRS-source-instance20-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_50 = "/resources/selfjoins/chaseRS/chaseRS-source-instance50.sql";
    public static final String chaseRSExpectedInstance_50 = "/resources/selfjoins/chaseRS/chaseRS-source-instance50-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_100 = "/resources/selfjoins/chaseRS/chaseRS-source-instance100.sql";
    public static final String chaseRSExpectedInstance_100 = "/resources/selfjoins/chaseRS/chaseRS-source-instance100-expected.csv";
    public static final String chaseRSSourceSQLScriptInstances_100bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance100bis.sql";
    public static final String chaseRSExpectedInstance_100bis = "/resources/selfjoins/chaseRS/chaseRS-source-instance100bis-expected.csv";

    public static final String rsSingleSourceSQLScriptSchema = "/resources/selfjoins/RSSingle/RSSingle-source.sql";
    public static final String rsSingleSourceSQLScriptInstances_2bis = "/resources/selfjoins/RSSingle/RSSingle-source-instance2bis.sql";
    public static final String rsSingleTargetSQLScriptSchema = "/resources/selfjoins/RSSingle/RSSingle-target.sql";
    public static final String rsSingleExpectedInstance_2bis = "/resources/selfjoins/RSSingle/RSSingle-gen-instance0-expected.csv";

    public static final String selfJoinSubsumptionSourceSQLScriptSchema = "/resources/selfjoins/mixed/SelfJoinSubsumption-source.sql";
    public static final String selfJoinSubsumptionSourceSQLScriptInstances = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance.sql";
    public static final String selfJoinSubsumptionSourceSQLScriptInstances_64 = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance64.sql";
    public static final String selfJoinSubsumptionSourceSQLScriptInstances_256 = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance256.sql";
    public static final String selfJoinSubsumptionTargetSQLScriptSchema = "/resources/selfjoins/mixed/SelfJoinSubsumption-target.sql";
    public static final String selfJoinSubsumptionExpectedInstance = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance-expected.csv";
    public static final String selfJoinSubsumptionExpectedInstance_64 = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance64-expected.csv";
    public static final String selfJoinSubsumptionExpectedInstance_256 = "/resources/selfjoins/mixed/SelfJoinSubsumption-source-instance256-expected.csv";

    public static final String chaseExample2SourceSQLScriptSchema = "/resources/chase/example2-source.sql";
    public static final String chaseExample2SQLScriptInstances = "/resources/chase/example2-instance.sql";
    public static final String chaseExample2TargetSQLScriptSchema = "/resources/chase/example2-target.sql";
    public static final String chaseExample2ExpectedInstance = "/resources/chase/example2-instance-expected.csv";

    //////////////////////////////////////////// XQUERY /////////////////////////////////////////////////

    public static final String expenseDBXMLInstance = "/resources/statDBExpenseDB/expenseDB-instance.xml";
    public static final String statDBXMLExpectedInstance = "/resources/statDBExpenseDB/statDB-instance-expected.csv";

    public static final String statDBXMLInstance = "/resources/statDBExpenseDB/statDB-instance.xml";
    public static final String expenseDBXMLExpectedInstance = "/resources/statDBExpenseDB/expenseDB-instance-expected.csv";

    public static final String deptsXMLInstance = "/resources/deptOtherDept/depts-instance.xml";
    public static final String otherDeptsXMLExpectedInstance = "/resources/deptOtherDept/otherDepts-instance-expected.csv";

    public static final String coverageBig2XMLInstance = "/resources/coverage/coverageBig2-source-instance.xml";
    public static final String coverageBig2XMLExpectedInstance = "/resources/coverage/coverageBig2-source-instance-expected.csv";



}