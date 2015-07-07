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
 
package it.unibas.spicy.findmappings.strategies.generatecandidates;

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.Application;
import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.attributematch.operators.FindAttributes;
import it.unibas.spicy.findmappings.CandidateCorrespondences;
import it.unibas.spicy.findmappings.TooManyCandidatesException;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.operators.CheckJoinsAndKeys;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.utility.GenericListGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateCandidates1to1FixedSizeCaching implements IGenerateCandidateMappingTaskStrategy {

    private Log logger = LogFactory.getLog(this.getClass());
    private static final int MAX_COMBINATIONS = 500;
    private List<PathExpression> targetAttributePaths;
    private Map<CandidateCorrespondences, MappingTask> cache = new HashMap<CandidateCorrespondences, MappingTask>();
    private FindAttributes attributeFinder = new FindAttributes();
    private GenericListGenerator<ValueCorrespondence> listGenerator = new GenericListGenerator<ValueCorrespondence>();
    private CheckJoinsAndKeys keyChecker = new CheckJoinsAndKeys();

    public List<AnnotatedMappingTask> generateAnnotatedMappingTasks(DataSource source, DataSource target, AttributeCorrespondences correspondences, double minSimilarity) {
        return generateAnnotatedMappingTasks(source, target, correspondences, minSimilarity, 0);
    }

    public List<AnnotatedMappingTask> generateAnnotatedMappingTasks(DataSource source, DataSource target, AttributeCorrespondences correspondences, double minSimilarity, int tolerance) {
        initPathExpressions(target);
        List<List<ValueCorrespondence>> correspondencesAboveMin = findCorrespondencesAboveMin(correspondences, minSimilarity);
        logger.info("Number of CorrespondencesAboveMin = " + countNumberOfValueCorrespondence(correspondencesAboveMin));
        if(countNumberOfValueCorrespondence(correspondencesAboveMin) > 50){
            return new ArrayList<AnnotatedMappingTask>();
        }
        List<List<ValueCorrespondence>> combinations = listGenerator.generateListsOfElements(correspondencesAboveMin);
        logger.info("Number of combinations = " + countNumberOfValueCorrespondence(combinations));
        removeMeaninglessCombinations(combinations, target, tolerance);
        IFindBestMappingsStrategy mappingFinder = (IFindBestMappingsStrategy) Application.getInstance().getComponentInstance(IFindBestMappingsStrategy.class);
        if (!mappingFinder.isContinuaEsecuzione()){
            return new ArrayList<AnnotatedMappingTask>();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Correspondences above min: " + CandidateCorrespondences.printListOfListsOfCorrespondences(correspondencesAboveMin));
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Combinations: " + CandidateCorrespondences.printListOfListsOfCorrespondences(combinations));
        }
        logger.info("Combinations produced: " + combinations.size());
        if (combinations.size() > MAX_COMBINATIONS) {
            throw new TooManyCandidatesException("Too many candidates: " + combinations.size());
        }
        List<AnnotatedMappingTask> result = createAnnotatedMappingTasks(source, target, combinations);
        if (logger.isDebugEnabled()) {
            logger.debug("Number of mapping tasks: " + result.size());
        }
        return result;
    }

    private void initPathExpressions(DataSource dataSource) {
        if (targetAttributePaths == null) {
            targetAttributePaths = attributeFinder.findAttributePaths(dataSource.getSchema(), true);
        }
    }

    private List<List<ValueCorrespondence>> findCorrespondencesAboveMin(AttributeCorrespondences correspondences, double minSimilarity) {
        List<List<ValueCorrespondence>> result = new ArrayList<List<ValueCorrespondence>>();
        for (PathExpression targetPath : targetAttributePaths) {
            List<ValueCorrespondence> correspondencesForTarget = correspondences.getCorrespondences(targetPath);
            List<ValueCorrespondence> correspondencesAboveMinForTarget = findCorrespondencesAboveMinForTarget(correspondencesForTarget, minSimilarity);
            if (!correspondencesAboveMinForTarget.isEmpty()) {
                result.add(correspondencesAboveMinForTarget);
            }
        }
        return result;
    }

    private List<ValueCorrespondence> findCorrespondencesAboveMinForTarget(List<ValueCorrespondence> correspondencesForTarget, double minSimilarity) {
        List<ValueCorrespondence> result = new ArrayList<ValueCorrespondence>();
        for (ValueCorrespondence correspondence : correspondencesForTarget) {
            if (correspondence.getConfidence() < minSimilarity) {
                break;
            }
            result.add(correspondence);
        }
        return result;
    }

    private void removeMeaninglessCombinations(List<List<ValueCorrespondence>> combinations, DataSource target, int tolerance) {
        IFindBestMappingsStrategy mappingFinder = (IFindBestMappingsStrategy) Application.getInstance().getComponentInstance(IFindBestMappingsStrategy.class);
        for (Iterator<List<ValueCorrespondence>> it = combinations.iterator(); it.hasNext();) {
            if (mappingFinder.isContinuaEsecuzione()) {
                List<ValueCorrespondence> combination = it.next();
                if (!coversEntireTarget(combination, target, tolerance) || hasDuplicateSourceAttributes(combination)) {
//            if (!coversEntireTarget(combination, target, tolerance)) {
                    it.remove();
                }
            }else{
                return;
            }
        }
    }

    private boolean coversEntireTarget(List<ValueCorrespondence> combination, DataSource target, int tolerance) {
        int uncoveredTargetAttributes = 0;
        for (PathExpression targetPath : targetAttributePaths) {
            if (!contains(combination, targetPath)) {
                if (!keyChecker.checkIfIsKey(targetPath, target) && !keyChecker.checkIfIsForeignKey(targetPath, target)) {
                    uncoveredTargetAttributes++;
                }
            }
        }
        return (uncoveredTargetAttributes <= tolerance);
    }

    private boolean contains(List<ValueCorrespondence> combination, PathExpression targetPath) {
        for (ValueCorrespondence correspondence : combination) {
            if (correspondence.getTargetPath().equals(targetPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateSourceAttributes(List<ValueCorrespondence> combination) {
        for (ValueCorrespondence correspondence : combination) {
            PathExpression sourcePath = correspondence.getSourcePaths().get(0);
            if (countOccurrences(sourcePath, combination) > 1) {
                return true;
            }
        }
        return false;
    }

    private int countOccurrences(PathExpression pathExpression, List<ValueCorrespondence> combination) {
        int counter = 0;
        for (ValueCorrespondence correspondence : combination) {
            PathExpression sourcePath = correspondence.getSourcePaths().get(0);
            if (sourcePath.equals(pathExpression)) {
                counter++;
            }
        }
        return counter;
    }

    private List<AnnotatedMappingTask> createAnnotatedMappingTasks(DataSource source, DataSource target, List<List<ValueCorrespondence>> listOfListsOfCorrespondences) {
        int targetSize = targetAttributePaths.size();
        if (logger.isDebugEnabled()) {
            logger.debug("Target size: " + targetSize);
        }
        List<AnnotatedMappingTask> result = new ArrayList<AnnotatedMappingTask>();
        for (List<ValueCorrespondence> correspondences : listOfListsOfCorrespondences) {
            if (logger.isDebugEnabled()) {
                logger.debug("Analyzing combination: " + correspondences);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Subset added");
            }
            CandidateCorrespondences candidateCorrespondences = new CandidateCorrespondences(correspondences);
            MappingTask mappingTask = this.cache.get(candidateCorrespondences);
            if (mappingTask == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Mapping task not in cache. Creating...");
                }
                mappingTask = new MappingTask(source, target, correspondences);
                cache.put(candidateCorrespondences, mappingTask);
            }
            AnnotatedMappingTask annotatedMappingTask = new AnnotatedMappingTask(mappingTask);
            result.add(annotatedMappingTask);
        }
        return result;
    }

    public void clearCache() {
        cache.clear();
        attributeFinder = new FindAttributes();
        if (targetAttributePaths != null) {
            targetAttributePaths.clear();
        }
    }


    
    private int countNumberOfValueCorrespondence(List<List<ValueCorrespondence>> listOfListOfValueCorrespondence){
        int count = 0;
        for(List<ValueCorrespondence> listOfCorrespondence : listOfListOfValueCorrespondence){
            count += listOfCorrespondence.size();
        }
        return count;
    }
}
