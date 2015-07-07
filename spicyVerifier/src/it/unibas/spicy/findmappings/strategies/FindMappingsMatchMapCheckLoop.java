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
import it.unibas.spicy.attributematch.operators.FindAttributes;
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.findmappings.strategies.generatecandidates.IGenerateCandidateMappingTaskStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.IStopSearchStrategy;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.CheckJoinsAndKeys;
import it.unibas.spicy.model.mapping.MappingTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindMappingsMatchMapCheckLoop implements IFindBestMappingsStrategy {

    private static final int MAX_DISCARDED = 3;
    private static Log logger = LogFactory.getLog(FindMappingsMatchMapCheckLoop.class);
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
    private FindAttributes attributeFinder = new FindAttributes();
    private CheckJoinsAndKeys keyChecker = new CheckJoinsAndKeys();
    private FindMappingsMatchMapCheck mappingFinder = new FindMappingsMatchMapCheck();

    private boolean continuaEsecuzione = true;
    
    public FindMappingsMatchMapCheckLoop() {
    }

    public FindMappingsMatchMapCheckLoop(IMatchAttributes attributeMatcher, IGenerateCandidateMappingTaskStrategy candidatesGenerator,
            IComputeQuality mappingComparator, IStopSearchStrategy qualityFunction, double qualityThreshold) {
        this.attributeMatcher = attributeMatcher;
        this.candidatesGenerator = candidatesGenerator;
        this.qualityCalculator = mappingComparator;
        this.stopSearchStrategy = qualityFunction;
        this.qualityThreshold = qualityThreshold;
        this.mappingFinder = new FindMappingsMatchMapCheck(attributeMatcher, candidatesGenerator, qualityCalculator, stopSearchStrategy, qualityThreshold);
    }

    public List<AnnotatedMappingTask> findBestMappings(MappingTask mappingTask) {
        this.mappingFinder = new FindMappingsMatchMapCheck(attributeMatcher, candidatesGenerator, qualityCalculator, stopSearchStrategy, qualityThreshold);
        return findBestMappings(mappingTask, 0);
    }

    public List<AnnotatedMappingTask> findBestMappings(MappingTask mappingTask, int tolerance) {
        this.mappingFinder = new FindMappingsMatchMapCheck(attributeMatcher, candidatesGenerator, qualityCalculator, stopSearchStrategy, qualityThreshold);
        int targetSize = findTargetSize(mappingTask);
        logger.info("===================  STARTING SEARCH TO COVER THE WHOLE TARGET =======================");
        logger.info("======================================================================================");
        List<AnnotatedMappingTask> result = mappingFinder.findBestMappings(mappingTask, tolerance);
        List<AnnotatedMappingTask> discardedResults = new ArrayList<AnnotatedMappingTask>();
        while (tolerance < targetSize && (result == null || result.isEmpty()) && this.isContinuaEsecuzione() == true) {
            logger.info("===================  SEARCH FAILED. RESTARTING WITH TOLERANCE = " + tolerance + " (Max tolerance: " + (targetSize - 1) + ") =======================");
            logger.info("===================================================================================================================");
            tolerance++;
            result = mappingFinder.findBestMappings(mappingTask, tolerance);
            addDiscardedResults(discardedResults);
        }
        if(result == null){
            return null;
        }
        if (result.isEmpty()) {
            result = new ArrayList<AnnotatedMappingTask>();
            Collections.sort(discardedResults, qualityCalculator);
            for (int i = 0; i < discardedResults.size() && i < MAX_DISCARDED; i++) {
                result.add(discardedResults.get(i));
            }
        }
        logger.info("===================  SEARCH FINISHED. FOUND = " + result.size() + " MAPPINGS =======================");
        logger.info("======================================================================================");
        return result;
    }

    private void addDiscardedResults(List<AnnotatedMappingTask> discardedResults) {
        for (AnnotatedMappingTask discarded : mappingFinder.getDiscardedResults()) {
            if (!discardedResults.contains(discarded)) {
                discardedResults.add(discarded);
            }
        }
    }

    private int findTargetSize(MappingTask mappingTask) {
        List<INode> attributeNodes = attributeFinder.findAttributes(mappingTask.getTargetProxy().getDataSource().getSchema(), true);
        int targetSize = attributeNodes.size();
        for (INode attributeNode : attributeNodes) {
            if (keyChecker.checkIfIsKey(attributeNode, mappingTask.getTargetProxy().getDataSource()) || keyChecker.checkIfIsForeignKey(attributeNode, mappingTask.getTargetProxy().getDataSource())) {
                targetSize--;
            }
        }
        return targetSize;
    }
    
    public Object getStatusValue(String key) {
        return this.mappingFinder.getStatusValue(key);
    }

    public void cleanUp() {
        if (attributeMatcher != null) {
            attributeMatcher.clearCache();
        }
        candidatesGenerator.clearCache();
    }

    public boolean isContinuaEsecuzione() {
        return continuaEsecuzione;
    }

    public void setContinuaEsecuzione(boolean CONTINUA_ESECUZIONE) {
        this.continuaEsecuzione = CONTINUA_ESECUZIONE;
        if(this.mappingFinder != null){
            this.mappingFinder.setContinuaEsecuzione(CONTINUA_ESECUZIONE);
        }
    }
}
