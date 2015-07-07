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
 
package it.unibas.spicy.findmappings.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.findmappings.TooManyCandidatesException;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.findmappings.strategies.generatecandidates.IGenerateCandidateMappingTaskStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.IStopSearchStrategy;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindMappingsMatchMapCheck implements IFindBestMappingsStrategy {

    private static Log logger = LogFactory.getLog(FindMappingsMatchMapCheck.class);
    private static final double INITIAL_SIMILARITY = 1;
    private static final double STEP = 0.005;
    private static final int RESULT_SIZE = 10;
    @Inject()
    private IMatchAttributes attributeMatcher;
    @Inject()
    private IGenerateCandidateMappingTaskStrategy candidatesGenerator;
    @Inject()
    private IComputeQuality qualityCalculator;
    @Inject()
    private IStopSearchStrategy stopSearchStrategy;
    @Inject()
    @Named(Application.QUALITY_THRESHOLD_KEY)
    private double qualityThreshold;
    private int totalNumberOfCandidates;
    private double requiredSimilarity;
    private int lastNumberOfCandidates;
    private List<AnnotatedMappingTask> result = new ArrayList<AnnotatedMappingTask>();
    private List<AnnotatedMappingTask> discardedResults = new ArrayList<AnnotatedMappingTask>();
    private boolean continuaEsecuzione = true;

    public FindMappingsMatchMapCheck() {
    }

    public FindMappingsMatchMapCheck(IMatchAttributes attributeMatcher, IGenerateCandidateMappingTaskStrategy candidatesGenerator,
            IComputeQuality mappingComparator, IStopSearchStrategy qualityFunction, double qualityThreshold) {
        this.attributeMatcher = attributeMatcher;
        this.candidatesGenerator = candidatesGenerator;
        this.qualityCalculator = mappingComparator;
        this.stopSearchStrategy = qualityFunction;
        this.qualityThreshold = qualityThreshold;
    }

    public List<AnnotatedMappingTask> findBestMappings(MappingTask mappingTask) {
        return findBestMappings(mappingTask, 0);
    }

    public List<AnnotatedMappingTask> findBestMappings(MappingTask mappingTask, int tolerance) {
        this.result.clear();
        DataSource source = mappingTask.getSourceProxy().getDataSource();
        DataSource target = mappingTask.getTargetProxy().getDataSource();
//        SchemaMapperUtility.removeNestAnnotation(source);
        AttributeCorrespondences candidateCorrespondences = null;
        if (mappingTask.getCandidateCorrespondences().isEmpty()) {
            candidateCorrespondences = attributeMatcher.findMatches(source, target);
            attributeMatcher.clearCache();
            mappingTask.setCandidateCorrespondences(candidateCorrespondences.getCorrespondences());
        } else {
            candidateCorrespondences = generateAttributeCorrespondences(mappingTask);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(printCorrespondences(candidateCorrespondences));
        }
        return findBestMappings(source, target, candidateCorrespondences, tolerance);
    }

    private void clearResult() {
        //TODO: check
//        if (this.result != null) {
//            for (MappingTask mappingTask : this.result) {
//                mappingTask.clear();
//            }
//        }
    }

    private AttributeCorrespondences generateAttributeCorrespondences(MappingTask mappingTask) {
        AttributeCorrespondences attributeCorrespondences = new AttributeCorrespondences(mappingTask.getSourceProxy().getDataSource(), mappingTask.getTargetProxy().getDataSource());
        List<ValueCorrespondence> candidateCorrespondences = mappingTask.getCandidateCorrespondences();
        for (ValueCorrespondence candidateCorrespondece : candidateCorrespondences) {
            attributeCorrespondences.addCorrespondence(candidateCorrespondece);
        }
        return attributeCorrespondences;
    }

    private List<AnnotatedMappingTask> findBestMappings(DataSource source, DataSource target, AttributeCorrespondences candidateCorrespondences, int tolerance) {
        requiredSimilarity = INITIAL_SIMILARITY;
        List<AnnotatedMappingTask> currentCandidates = null;
        DecimalFormat formatter = new DecimalFormat("#.####");
        do {
//            // AXE: Aggiunto per per poter fermare dall'esterno l'esecuzione del Finder
//            try {
//                Thread currentThread = Thread.currentThread();
//                currentThread.sleep(1);
//                if (this.isContinuaEsecuzione() == false) {
//                    logger.info("IL FINDBESTMATCH DEL THREAD " + currentThread.getId() + " DEVE FERMARSI");
//                    clearResult();
//                    return Collections.EMPTY_LIST;
//                }
//            } catch (InterruptedException ex) {
//                logger.error(ex.getStackTrace());
//            }
            logger.info("----------------- STARTING SEARCH WITH SIMILARITY=" + formatter.format(requiredSimilarity));
            //   System.out.println("1");
            try {
                currentCandidates = candidatesGenerator.generateAnnotatedMappingTasks(source, target, candidateCorrespondences, requiredSimilarity, tolerance);
            } catch (TooManyCandidatesException e) {
                logger.error("Too many candidates. Stopping...: " + e);
                clearResult(); // AXE: ripulisco result che potrebbe contenere mapping inutili e condivisi
                return Collections.EMPTY_LIST;
            }
            logger.info("CANDIDATE MAPPING TASKS: " + currentCandidates.size());
            //  System.out.println("2");
            requiredSimilarity -= STEP;
            if (currentCandidates.isEmpty() || currentCandidates.size() == lastNumberOfCandidates) {
                continue;
            }
            lastNumberOfCandidates = currentCandidates.size();
            totalNumberOfCandidates += currentCandidates.size();
            int i = 1;
            Iterator it = currentCandidates.iterator();
            while (it.hasNext()) {
                if (this.isContinuaEsecuzione() == true) {
                    AnnotatedMappingTask candidateMappingTask = (AnnotatedMappingTask) it.next();
                    try {
//                        logger.warn("*****QUALITY CALCULATOR: " + qualityCalculator);
                        qualityCalculator.computeQuality(candidateMappingTask);
//                        logger.warn("AFTER COMPUTE QUALITY");
                        logger.info(printCandidate(candidateMappingTask, i++, currentCandidates.size()));
//                        logger.warn("DOPO printCandidates");
                    } catch (Throwable t) {
                        logger.warn("MappingTask scartato: " + t);
//                        t.printStackTrace();
                        it.remove();
                    }
                } else {
                    logger.warn("TIMEOUT DURANTE VALUTAZIONE QUALITA' MAPPINGTASK PRODOTTI");
                    while (it.hasNext()) {
                        it.next();
                        it.remove();
                    }
                }
            }
//            for (MappingTask candidateMappingTask : currentCandidates) {
//                try {
//                    qualityCalculator.computeQuality(candidateMappingTask);
//                    logger.info(printCandidate(candidateMappingTask, i++, currentCandidates.size()));
//                } catch (Throwable t) {
//                    logger.warn("Esperimento scartato: " + t);
//                    System.out.println("Esperimento scartato: " + t);
//                }
//            }
            Collections.sort(currentCandidates, qualityCalculator);
            addResults(currentCandidates);
            if (logger.isTraceEnabled()) {
                logger.trace("Candidate mapping tasks: " + currentCandidates);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Current candidates: " + printCandidates(currentCandidates));
            }
        } while (!stopSearchStrategy.stopSearch(this));
        return result;
    }

    private void addResults(List<AnnotatedMappingTask> currentCandidates) {
        result.clear();
        int added = 0;
        int i = 0;
        while (added < RESULT_SIZE && i < currentCandidates.size()) {
            AnnotatedMappingTask annotatedMappingTask = currentCandidates.get(i);
            double qualityMeasure = (Double) annotatedMappingTask.getQualityMeasure();
            if (qualityMeasure > qualityThreshold) {
                result.add(annotatedMappingTask);
                added++;
            } else {
                discardedResults.add(annotatedMappingTask);
            }
            i++;
        }
    }

    public Object getStatusValue(String key) {
        if (key.equals(SpicyConstants.CURRENT_SIMILARITY)) {
            return requiredSimilarity;
        } else if (key.equals(SpicyConstants.CURRENT_RESULT)) {
            return result;
        } else if (key.equals(SpicyConstants.NUMBER_OF_CANDIDATES)) {
            return totalNumberOfCandidates;
        }
        return null;
    }

    public List<AnnotatedMappingTask> getDiscardedResults() {
        return discardedResults;
    }

    private String printCorrespondences(AttributeCorrespondences candidateCorrespondences) {
        Map<PathExpression, List<ValueCorrespondence>> correspondenceMap = candidateCorrespondences.getCorrespondenceMap();
        String result = "----------------CORRESPONDENCES-----------------------\n";
        for (PathExpression pathExpression : correspondenceMap.keySet()) {
            for (ValueCorrespondence correspondence : correspondenceMap.get(pathExpression)) {
                result += correspondence + "\n";
            }
        }
        result += "-------------------------------------------------";
        return result;
    }

    private String printCandidates(List<AnnotatedMappingTask> mappingTasks) {
        String result = "----------------CANDIDATES-----------------------";
        int i = 0;
        for (AnnotatedMappingTask annotatedMappingTask : mappingTasks) {
            result += printCandidate(annotatedMappingTask, i, mappingTasks.size());
        }
        result += "-------------------------------------------------";
        return result;
    }

    private String printCandidate(AnnotatedMappingTask annotatedMappingTask, int i, int totalNumber) {
        String result = "------------ MAPPING TASK n. " + i + " of " + totalNumber + "---------------\n";
//        result += CandidateCorrespondences.printListOfCorrespondences(mappingTask.getValueCorrespondences());
//        logger.warn("******************: " + annotatedMappingTask);
        result += annotatedMappingTask.toStringWithoutInstances();
//        List<SimilarityCheck> scs = (List<SimilarityCheck>) annotatedMappingTask.getSimilarityChecks();
//        if (scs != null) {
//            for (SimilarityCheck sc : scs) {
//                result += sc;
//                Transformation transformation = (Transformation)sc.getAnnotation(SpicyConstants.TRANSFORMATION);
//                result += transformation;
//                result += transformation.getTranslatedInstances();
//                result += sc.toStringWithCircuitsAndSchemas() + "\n";
//            }
//        }
//        result += "OVERALL QUALITY: " + annotatedMappingTask.getQualityMeasure();
        return result;
    }

    public void cleanUp() {
        if (attributeMatcher != null) {
            attributeMatcher.clearCache();
        }
        this.result = null;
        candidatesGenerator.clearCache();
    }

    public boolean isContinuaEsecuzione() {
        return continuaEsecuzione;
    }

    public void setContinuaEsecuzione(boolean CONTINUA_ESECUZIONE) {
        this.continuaEsecuzione = CONTINUA_ESECUZIONE;
    }
}
