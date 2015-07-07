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
package it.unibas.spicy.test.tools;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.xml.operators.LoadXMLFile;
import it.unibas.spicybenchmark.Utility;
import it.unibas.spicybenchmark.model.features.FeatureCollection;
import it.unibas.spicybenchmark.model.features.FeatureResult;
import it.unibas.spicybenchmark.model.features.SimilarityResult;
import it.unibas.spicybenchmark.operators.EvaluateSimilarity;
import it.unibas.spicybenchmark.operators.generators.FeatureCollectionGenerator;
import it.unibas.spicybenchmark.persistence.DAOConfiguration;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MappingTaskXMLInstanceChecker extends AbstractInstanceChecker {

    private static Log logger = LogFactory.getLog(MappingTaskXMLInstanceChecker.class);
    private IDataSourceProxy dataSource;

    public MappingTaskXMLInstanceChecker(EngineConfiguration config, IDataSourceProxy dataSource) {
        super(config);
        this.dataSource = dataSource;
    }

    public void checkInstance(INode solutionInstance, String expectedInstanceFile) throws Exception {
        LoadXMLFile daoXMLFile = new LoadXMLFile();
        INode expectedInstance = daoXMLFile.loadInstance(dataSource, expectedInstanceFile);
        if (logger.isDebugEnabled()) logger.debug("Solution instance: " + solutionInstance);
        if (logger.isDebugEnabled()) logger.debug("Expected instance: " + expectedInstance);
        List<String> exclusionList = new ArrayList<String>();
        List<String> features = new ArrayList<String>();
        features.add(DAOConfiguration.FEATURE_LOCAL_ID);
        features.add(DAOConfiguration.FEATURE_JOINS_LOCAL_ID);
        FeatureCollectionGenerator featureCollectionGenerator = new FeatureCollectionGenerator();
        FeatureCollection featureCollection = featureCollectionGenerator.generate(expectedInstance, solutionInstance, exclusionList, dataSource, features);
        EvaluateSimilarity evaluator = new EvaluateSimilarity();
        SimilarityResult similarityResult = evaluator.getSimilarityResult(featureCollection);
        StringBuilder result = new StringBuilder();
        result.append("\n------------ Similarity Result ------------\n");
        result.append("Number of Tuples in Expected Instance: ").append(similarityResult.getNumberOfExpectedTuples()).append("\n");
        result.append("Number of Tuples in Translated Instance: ").append(similarityResult.getNumberOfTranslatedTuples()).append("\n");
        result.append("Number of Analyzed Features: ").append(similarityResult.sizeOfFeatures()).append("\n");
        result.append("F-Measure: ").append(similarityResult.getFmeasure()).append("\n");
        if (logger.isDebugEnabled()) logger.debug(result);
        if (similarityResult.getFmeasure() != 1.0) {
            StringBuilder violationResult = new StringBuilder();
            for (FeatureResult featureResult : similarityResult.getFeatureResults()) {
                violationResult.append(Utility.printViolations(featureResult));
            }
            Assert.fail(violationResult.toString());
        }

    }

    protected boolean checkNullOrSkolem(Object instanceValue) {
        throw new UnsupportedOperationException();
    }

    protected boolean isSkolem(INode attributeNode) {
        throw new UnsupportedOperationException();
    }
}
