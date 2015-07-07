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

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.attributematch.strategies.MatchAttributes1to1StructuralAnalysis;
import it.unibas.spicy.findmappings.operators.RankTransformations;
import it.unibas.spicy.findmappings.strategies.FindMappingsMatchMapCheck;
import it.unibas.spicy.findmappings.strategies.generatecandidates.GenerateCandidates1to1PowersetCaching;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.findmappings.strategies.generatecandidates.IGenerateCandidateMappingTaskStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.IStopSearchStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.StopNonEmptyCandidatesSimilarityThreshold;
import it.unibas.spicy.findmappings.strategies.computequality.ComputeQualityStructuralAnalysis;
import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.strategies.FindNodesUndersampledConstraints;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IFindNodesToExclude;
import it.unibas.spicy.structuralanalysis.strategies.PerformStructuralAnalysis;
import it.unibas.spicy.structuralanalysis.circuits.strategies.BuildCircuitSSReducedWithCaching;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.compare.SimilarityFeature;
import it.unibas.spicy.structuralanalysis.compare.comparators.IFeatureComparator;
import it.unibas.spicy.structuralanalysis.compare.comparators.OutputCurrentComparator;
import it.unibas.spicy.structuralanalysis.compare.operators.CompareFeatures;
import it.unibas.spicy.structuralanalysis.compare.strategies.AggregateAsWeightedAverage;
import it.unibas.spicy.structuralanalysis.compare.strategies.IAggregateSimilarityFeatures;
import it.unibas.spicy.structuralanalysis.sampling.CharacterCategory;
import it.unibas.spicy.structuralanalysis.sampling.operators.SampleInstances;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateConsistencyPolynomial;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStandardCharCategoryDistribution;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStandardLengthDistribution;
import it.unibas.spicy.structuralanalysis.sampling.strategies.GenerateStressAverageLength;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateCharCategoryDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateConsistencyStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateLengthDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateStressStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestFixture extends TestCase {

    private Log logger = LogFactory.getLog(this.getClass());
    protected MappingTask mappingTask;
    protected DAOMappingTask daoMappingTask = new DAOMappingTask();
    protected IDataSourceProxy finalResult;
    // datasources
//    protected DataSource statDBReduced = MockSourceStatDBReduced.getInstance();
//    protected DataSource statDB = MockSourceStatDB.getInstance();
//    protected DataSource statDBBalance = MockSourceStatDBBalance.getInstance();
//    protected DataSource expenseDB = MockSourceExpenseDB.getInstance();
//    protected DataSource expenseDBSponsor = MockSourceExpenseDBSponsor.getInstance();
//
//    // mapping tasks
//    protected MappingTask statDBExpenseDB = MockMappingTaskStatDBExpenseDB.getInstance().getMappingTask();
//    protected MappingTask expenseDBstatDB = MockMappingTaskExpenseDBStatDB.getInstance().getMappingTask();
//    protected MappingTask expenseDBSponsorStatDBBalance = MockMappingTaskExpenseDBSponsorStatDBBalance.getInstance().getMappingTask();
    // constants
    protected CharacterCategory[] categories;
    protected Map<Integer, Double> grades;
    protected double multiplyingFactorForCircuit;
    protected double levelResistance;
    protected double externalResistance;
    protected int minSampleSize;
    protected double similarityThreshold;
    protected double qualityThreshold;
    // sampling
    protected SampleInstances sampler;
    protected IGenerateLengthDistributionStrategy lengthDistributionGenerator;
    protected IGenerateCharCategoryDistributionStrategy charCategoryDistributionGenerator;
    protected IGenerateStressStrategy stressGenerator;
    protected IGenerateConsistencyStrategy consistencyGenerator;
    // circuit
    protected IFindNodesToExclude excluder;
    protected IBuildCircuitStrategy circuitBuilder;
    // compare
    protected IAggregateSimilarityFeatures meanCalculator;
    protected List<IFeatureComparator> comparatorChain;
    protected CompareFeatures comparator;
    // structural analysis
//    protected GenerateTransformationsAndTranslate mappingTaskSolver;
    protected PerformStructuralAnalysis analyzer;
    // attribute match
    protected IMatchAttributes attributeMatcher;
    // find mappings
    protected IFindBestMappingsStrategy mappingFinder;
    protected IGenerateCandidateMappingTaskStrategy candidatesGenerator;
    protected IComputeQuality mappingAnalyzer;
    protected IStopSearchStrategy stopStrategy;
    protected RankTransformations transformationRanker;

    protected void setUp() throws Exception {
        // constants
        categories = Application.getInstance().getCategories();
        grades = Application.getInstance().getGrades();
        multiplyingFactorForCircuit = 1.0;
        levelResistance = 2;
        externalResistance = 0.1;
        minSampleSize = 1;
        similarityThreshold = -1.0;
        qualityThreshold = 0.7;

        // sampler
        lengthDistributionGenerator = new GenerateStandardLengthDistribution();
        stressGenerator = new GenerateStressAverageLength(lengthDistributionGenerator);
        charCategoryDistributionGenerator = new GenerateStandardCharCategoryDistribution(categories);
        consistencyGenerator = new GenerateConsistencyPolynomial(categories, grades, charCategoryDistributionGenerator);
        sampler = new SampleInstances(stressGenerator, consistencyGenerator);
        excluder = new FindNodesUndersampledConstraints(minSampleSize);

        // circuit
        //circuitBuilder = new BuildCircuitSimpsonSimpson(multiplyingFactorForCircuit, levelResistance, externalResistance);
        circuitBuilder = new BuildCircuitSSReducedWithCaching(excluder, multiplyingFactorForCircuit, multiplyingFactorForCircuit, levelResistance, externalResistance);

        // compare
        meanCalculator = new AggregateAsWeightedAverage();
        comparatorChain = new ArrayList<IFeatureComparator>();
        comparatorChain.add(new OutputCurrentComparator(1.0));
        comparator = new CompareFeatures(meanCalculator, similarityThreshold);

        // structural analysis
//        mappingTaskSolver = new GenerateTransformationsAndTranslate();
        analyzer = new PerformStructuralAnalysis(sampler, circuitBuilder, comparator);

        // attribute matcher
        attributeMatcher = new MatchAttributes1to1StructuralAnalysis(sampler, circuitBuilder, comparator, similarityThreshold, minSampleSize);

        // find mappings
        candidatesGenerator = new GenerateCandidates1to1PowersetCaching();
        mappingAnalyzer = new ComputeQualityStructuralAnalysis(analyzer);
        stopStrategy = new StopNonEmptyCandidatesSimilarityThreshold();
        mappingFinder = new FindMappingsMatchMapCheck(attributeMatcher, candidatesGenerator, mappingAnalyzer, stopStrategy, qualityThreshold);
        transformationRanker = new RankTransformations(sampler, circuitBuilder, comparator);
    }

    public void solveAndPrintResults() {
//        changeConfiguration();
        IAlgebraOperator completeTree = mappingTask.getMappingData().getAlgebraTree();
        IAlgebraOperator canonicalTree = mappingTask.getMappingData().getCanonicalAlgebraTree();
        IDataSourceProxy source = mappingTask.getSourceProxy();
//        if (logger.isTraceEnabled()) logger.trace("------------------Algebra tree:---------------------\n");
//        if (logger.isTraceEnabled()) logger.trace("\n" + completeTree);
        IDataSourceProxy canonicalSolutions = null;
        canonicalSolutions = canonicalTree.execute(mappingTask.getSourceProxy());
        this.finalResult = completeTree.execute(source);
        if (mappingTask != null) {
            if (logger.isTraceEnabled()) {
                if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().toVeryLongString());
            }
            if (logger.isDebugEnabled()) {
                if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().rewritingStatsString());
                if (logger.isInfoEnabled()) logger.info("------------------Source instance:---------------------\n");
                if (logger.isDebugEnabled()) logger.debug(source.toInstanceString());
                if (logger.isInfoEnabled()) logger.info("------------------Canonical solution:---------------------\n");
//                if (logger.isDebugEnabled()) logger.debug(canonicalSolutions.toInstanceString());
                if (logger.isInfoEnabled()) logger.info("------------------Final result:---------------------\n");
//                if (logger.isInfoEnabled()) logger.info(finalResult.toInstanceString());
            }
        }
    }

    public void testDummy() {
    }

    protected void printLog(List<AnnotatedMappingTask> annotatedMappingTasks) {
        for (AnnotatedMappingTask annotatedMappingTask : annotatedMappingTasks) {
            if (logger.isDebugEnabled()) logger.debug(annotatedMappingTask.toString());
            List<Transformation> transformations = annotatedMappingTask.getTransformations();
            if (logger.isDebugEnabled()) logger.debug(transformations);
            for (Transformation transformation : transformations) {
                List<INode> translatedInstances = transformation.getTranslatedInstances();
                if (logger.isDebugEnabled()) logger.debug(translatedInstances);
            }
            List<SimilarityCheck> similarityChecks = annotatedMappingTask.getSimilarityChecks();
            for (SimilarityCheck similarityCheck : similarityChecks) {
                if (logger.isDebugEnabled()) logger.debug(similarityCheck.toStringWithCircuits());
                List<SimilarityFeature> features = similarityCheck.getFeatures();
                if (logger.isDebugEnabled()) logger.debug(features);
            }
        }
    }
}
