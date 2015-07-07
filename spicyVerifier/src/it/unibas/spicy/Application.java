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
 
package it.unibas.spicy;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import it.unibas.spicy.attributematch.strategies.IMatchAttributes;
import it.unibas.spicy.findmappings.strategies.computequality.IComputeQuality;
import it.unibas.spicy.findmappings.strategies.IFindBestMappingsStrategy;
import it.unibas.spicy.findmappings.strategies.generatecandidates.IGenerateCandidateMappingTaskStrategy;
import it.unibas.spicy.findmappings.strategies.stopsearch.IStopSearchStrategy;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IBuildCircuitStrategy;
import it.unibas.spicy.structuralanalysis.circuits.strategies.IFindNodesToExclude;
import it.unibas.spicy.structuralanalysis.compare.comparators.IFeatureComparator;
import it.unibas.spicy.structuralanalysis.compare.strategies.IAggregateSimilarityFeatures;
import it.unibas.spicy.structuralanalysis.sampling.CharacterCategory;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateCharCategoryDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateConsistencyStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateLengthDistributionStrategy;
import it.unibas.spicy.structuralanalysis.sampling.strategies.IGenerateStressStrategy;
import it.unibas.spicy.structuralanalysis.strategies.IPerformStructuralAnalysis;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Application {

    private static Log logger = LogFactory.getLog(Application.class);
    public static Application singleton;
    private Injector injector;
    private static Properties configurationProperties;
    private static Properties customProperties;
    private static final String CONFIG_FILE = "/it/unibas/spicy/spicy-standard.properties";

    public static Application getInstance() {
        if (singleton == null) {
            singleton = new Application();
        }
        return singleton;
    }

    public static void reset() {
        singleton = null;
        customProperties = null;
        System.gc();
    }

    public static void reset(Properties properties) {
        singleton = null;
        System.gc();
        customProperties = properties;
    }

    @SuppressWarnings(value = "unchecked")
    public Object getComponentInstance(Class type) {
        return this.injector.getInstance(type);
    }
    public static final String MULTIPLYING_FACTOR_FOR_ELEMENTS_KEY = "MULTIPLYING_FACTOR_FOR_ELEMENTS_IN_CIRCUIT";
    public double MULTIPLYING_FACTOR_FOR_ELEMENTS;
    public static final String MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_KEY = "MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_IN_CIRCUIT";
    public double MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS;
    public static final String LEVEL_RESISTANCE_KEY = "LEVEL_RESISTANCE";
    public double LEVEL_RESISTANCE;
    public static final String EXTERNAL_RESISTANCE_KEY = "EXTERNAL_RESISTANCE";
    public double EXTERNAL_RESISTANCE;
    public static final String MIN_SAMPLE_SIZE_KEY = "MIN_SAMPLE_SIZE";
    public int MIN_SAMPLE_SIZE;
    public static final String SIMILARITY_THRESHOLD_FOR_MATCHER_KEY = "SIMILARITY_THRESHOLD_FOR_MATCHER";
    public double SIMILARITY_THRESHOLD_FOR_MATCHER;
    public static final String SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS_KEY = "SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS";
    public double SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS;
    public static final String SIMILARITY_THRESHOLD_FOR_PRUNING_KEY = "SIMILARITY_THRESHOLD_FOR_PRUNING";
    public double SIMILARITY_THRESHOLD_FOR_PRUNING;
    public static final String QUALITY_THRESHOLD_KEY = "QUALITY_THRESHOLD";
    public double QUALITY_THRESHOLD;
    public static final String CIRCUIT_BUILDER_FOR_MATCH = "CIRCUIT_BUILDER_FOR_MATCH";
    public static final String CIRCUIT_BUILDER_FOR_COMPARE = "CIRCUIT_BUILDER_FOR_COMPARE";
    public static final String CHARACTER_CATEGORIES_KEY = "CHARACTER_CATEGORIES";
    public static final String NON_CAPITAL_LETTER_KEY = "NON_CAPITAL_LETTER";
    public static final String CAPITAL_LETTER_KEY = "CAPITAL_LETTER";
    public static final String BLANK_KEY = "BLANK";
    public static final String COLON_KEY = "COLON";
    public static final String DASH_KEY = "DASH";
    public static final String NUMBER_KEY = "NUMBER";
    public static final String AT_SIGN_KEY = "AT_SIGN";
    public static final String SLASH_KEY = "SLASH";
    public double NON_CAPITAL_LETTER_GRADE;
    public double CAPITAL_LETTER_GRADE;
    public double BLANK_GRADE;
    public double COLON_GRADE;
    public double DASH_GRADE;
    public double NUMBER_GRADE;
    public double AT_SIGN_GRADE;
    public double SLASH_GRADE;
    public static final String COMPARATOR_CHAIN_FOR_MATCH_KEY = "COMPARATOR_CHAIN_FOR_MATCH";
    public String COMPARATOR_CHAIN_FOR_MATCH;
    public static final String GRADES_KEY = "GRADES";
    public static final String COMPARATOR_CHAIN_KEY = "COMPARATOR_CHAIN"; //global
    public String COMPARATOR_CHAIN;

    private Application() {
        loadStandardProperties();
        addCustomProperties();
        reloadProperties();
        this.injector = Guice.createInjector(Stage.DEVELOPMENT, new ConstantsModule(), new SamplingModule(), new CircuitGenerationModule(), new CompareModule(), new AttributeMatchModule(), new FindMappingsModule());
    }

    private void addCustomProperties() {
        if (customProperties != null) {
            Set customPropertiesKey = customProperties.keySet();
            Iterator it = customPropertiesKey.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                configurationProperties.setProperty(key, customProperties.getProperty(key));
            }
        }
    }

    private void loadStandardProperties() {
        configurationProperties = new Properties();
        try {
            InputStream in = Application.class.getResourceAsStream(CONFIG_FILE);
            configurationProperties.load(in);
        } catch (IOException e) {
            logger.error("Impossibile leggere il file di properties: spicystandard.properties" + "\n\n" + e.getMessage());
        }
    }

    private void reloadProperties() {
        MULTIPLYING_FACTOR_FOR_ELEMENTS = Double.parseDouble(configurationProperties.getProperty(MULTIPLYING_FACTOR_FOR_ELEMENTS_KEY));
        MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS = Double.parseDouble(configurationProperties.getProperty(MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_KEY));
        LEVEL_RESISTANCE = Double.parseDouble(configurationProperties.getProperty(LEVEL_RESISTANCE_KEY));
        EXTERNAL_RESISTANCE = Double.parseDouble(configurationProperties.getProperty(EXTERNAL_RESISTANCE_KEY));
        MIN_SAMPLE_SIZE = Integer.parseInt(configurationProperties.getProperty(MIN_SAMPLE_SIZE_KEY));
        SIMILARITY_THRESHOLD_FOR_MATCHER = Double.parseDouble(configurationProperties.getProperty(SIMILARITY_THRESHOLD_FOR_MATCHER_KEY));
        SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS = Double.parseDouble(configurationProperties.getProperty(SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS_KEY));
        SIMILARITY_THRESHOLD_FOR_PRUNING = Double.parseDouble(configurationProperties.getProperty(SIMILARITY_THRESHOLD_FOR_PRUNING_KEY));
        QUALITY_THRESHOLD = Double.parseDouble(configurationProperties.getProperty(QUALITY_THRESHOLD_KEY));
        //GRADE
        StringTokenizer strTokenizer = new StringTokenizer(configurationProperties.getProperty(SpicyConstants.CHARACTER_GRADES), ",");
        List<Double> listCharacterGrades = new ArrayList<Double>();
        while (strTokenizer.hasMoreTokens()) {
            String stringCharacterGrade = strTokenizer.nextToken().trim();
            listCharacterGrades.add(Double.parseDouble(stringCharacterGrade));
        }
        NON_CAPITAL_LETTER_GRADE = listCharacterGrades.get(0);
        CAPITAL_LETTER_GRADE = listCharacterGrades.get(1);
        BLANK_GRADE = listCharacterGrades.get(2);
        COLON_GRADE = listCharacterGrades.get(3);
        DASH_GRADE = listCharacterGrades.get(4);
        NUMBER_GRADE = listCharacterGrades.get(5);
        AT_SIGN_GRADE = listCharacterGrades.get(6);
        SLASH_GRADE = listCharacterGrades.get(7);
        //CHAIN
        COMPARATOR_CHAIN_FOR_MATCH = configurationProperties.getProperty(COMPARATOR_CHAIN_FOR_MATCH_KEY);
        COMPARATOR_CHAIN = configurationProperties.getProperty(COMPARATOR_CHAIN_KEY);
    }

    public CharacterCategory[] getCategories() {
        CharacterCategory[] categories = {
            new CharacterCategory(SpicyConstants.NON_CAPITAL_LETTER, "small-caps"),
            new CharacterCategory(SpicyConstants.CAPITAL_LETTER, "caps"),
            new CharacterCategory(SpicyConstants.BLANK, "blanks"),
            new CharacterCategory(SpicyConstants.COLON, "colons"),
            new CharacterCategory(SpicyConstants.DASH, "dashes"),
            new CharacterCategory(SpicyConstants.NUMBER, "numbers"),
            new CharacterCategory(SpicyConstants.AT_SIGN, "at-signs"),
            new CharacterCategory(SpicyConstants.SLASH, "slashes")
        };
        return categories;
    }

    public Map<Integer, Double> getGrades() {
        Map<Integer, Double> grades = new HashMap<Integer, Double>();
        grades.put(SpicyConstants.NON_CAPITAL_LETTER, NON_CAPITAL_LETTER_GRADE);
        grades.put(SpicyConstants.CAPITAL_LETTER, CAPITAL_LETTER_GRADE);
        grades.put(SpicyConstants.BLANK, BLANK_GRADE);
        grades.put(SpicyConstants.COLON, COLON_GRADE);
        grades.put(SpicyConstants.DASH, DASH_GRADE);
        grades.put(SpicyConstants.NUMBER, NUMBER_GRADE);
        grades.put(SpicyConstants.AT_SIGN, AT_SIGN_GRADE);
        grades.put(SpicyConstants.SLASH, SLASH_GRADE);
        return grades;
    }

    public List<IFeatureComparator> getComparatorChainForMatch() {
        List<IFeatureComparator> comparatorChain = new ArrayList<IFeatureComparator>();
        StringTokenizer strTokenizer = new StringTokenizer(COMPARATOR_CHAIN_FOR_MATCH, ",");
        while (strTokenizer.hasMoreTokens()) {
            try {
                String className = strTokenizer.nextToken().trim();
                Class comparatorClass = Class.forName(className);
                Object obj = comparatorClass.newInstance();
                ((IFeatureComparator) obj).setWeight(1.0);
                comparatorChain.add(((IFeatureComparator) obj));
            } catch (InstantiationException ex) {
                logger.error(ex);
            } catch (IllegalAccessException ex) {
                logger.error(ex);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
        return comparatorChain;
    }

    public List<IFeatureComparator> getComparatorChain() {
        List<IFeatureComparator> comparatorChain = new ArrayList<IFeatureComparator>();
        StringTokenizer strTokenizer = new StringTokenizer(COMPARATOR_CHAIN, ",");
        while (strTokenizer.hasMoreTokens()) {
            try {
                String className = strTokenizer.nextToken().trim();
                Class comparatorClass = Class.forName(className);
                Object obj = comparatorClass.newInstance();
                ((IFeatureComparator) obj).setWeight(1.0);
                comparatorChain.add(((IFeatureComparator) obj));
            } catch (InstantiationException ex) {
                logger.error(ex);
            } catch (IllegalAccessException ex) {
                logger.error(ex);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
        return comparatorChain;
    }

    private class CompareModule implements Module {

        public void configure(Binder binder) {
            try {
                binder.bind(IAggregateSimilarityFeatures.class).to((Class) Class.forName(configurationProperties.getProperty(IAggregateSimilarityFeatures.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IPerformStructuralAnalysis.class).to((Class) Class.forName(configurationProperties.getProperty(IPerformStructuralAnalysis.class.getSimpleName()))).in(Scopes.SINGLETON); //GLOBAL + LOCAL
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
    }

    private class FindMappingsModule implements Module {

        public void configure(Binder binder) {
            try {
                binder.bind(IComputeQuality.class).to((Class) Class.forName(configurationProperties.getProperty(IComputeQuality.class.getSimpleName()))).in(Scopes.SINGLETON); // CIRCUITS
                binder.bind(IGenerateCandidateMappingTaskStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IGenerateCandidateMappingTaskStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IStopSearchStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IStopSearchStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IFindBestMappingsStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IFindBestMappingsStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
    }

    private class AttributeMatchModule implements Module {

        public void configure(Binder binder) {
            try {
                binder.bind(IMatchAttributes.class).to((Class) Class.forName(configurationProperties.getProperty(IMatchAttributes.class.getSimpleName()))).in(Scopes.SINGLETON);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
    }

    private class SamplingModule implements Module {

        public void configure(Binder binder) {
            try {
                binder.bind(IGenerateLengthDistributionStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IGenerateLengthDistributionStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IGenerateCharCategoryDistributionStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IGenerateCharCategoryDistributionStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IGenerateStressStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IGenerateStressStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IGenerateConsistencyStrategy.class).to((Class) Class.forName(configurationProperties.getProperty(IGenerateConsistencyStrategy.class.getSimpleName()))).in(Scopes.SINGLETON);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
    }

    private class CircuitGenerationModule implements Module {

        public void configure(Binder binder) {
            try {
                binder.bind(IFindNodesToExclude.class).to((Class) Class.forName(configurationProperties.getProperty(IFindNodesToExclude.class.getSimpleName()))).in(Scopes.SINGLETON);
                binder.bind(IBuildCircuitStrategy.class).annotatedWith(Names.named(CIRCUIT_BUILDER_FOR_MATCH)).to((Class) Class.forName(configurationProperties.getProperty(CIRCUIT_BUILDER_FOR_MATCH))).in(Scopes.SINGLETON);
                binder.bind(IBuildCircuitStrategy.class).annotatedWith(Names.named(CIRCUIT_BUILDER_FOR_COMPARE)).to((Class) Class.forName(configurationProperties.getProperty(CIRCUIT_BUILDER_FOR_COMPARE))).in(Scopes.SINGLETON);
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }
        }
    }

    private class ConstantsModule implements Module {

        public void configure(Binder binder) {
            binder.bindConstant().annotatedWith(Names.named(MULTIPLYING_FACTOR_FOR_ELEMENTS_KEY)).to(MULTIPLYING_FACTOR_FOR_ELEMENTS);
            binder.bindConstant().annotatedWith(Names.named(MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_KEY)).to(MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS);
            binder.bindConstant().annotatedWith(Names.named(LEVEL_RESISTANCE_KEY)).to(LEVEL_RESISTANCE);
            binder.bindConstant().annotatedWith(Names.named(EXTERNAL_RESISTANCE_KEY)).to(EXTERNAL_RESISTANCE);
            binder.bindConstant().annotatedWith(Names.named(MIN_SAMPLE_SIZE_KEY)).to(MIN_SAMPLE_SIZE);
            binder.bindConstant().annotatedWith(Names.named(SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS_KEY)).to(SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS);
            binder.bindConstant().annotatedWith(Names.named(SIMILARITY_THRESHOLD_FOR_MATCHER_KEY)).to(SIMILARITY_THRESHOLD_FOR_MATCHER);
            binder.bindConstant().annotatedWith(Names.named(SIMILARITY_THRESHOLD_FOR_PRUNING_KEY)).to(SIMILARITY_THRESHOLD_FOR_PRUNING);
            binder.bindConstant().annotatedWith(Names.named(QUALITY_THRESHOLD_KEY)).to(QUALITY_THRESHOLD);
        }
    }

    public String toString() {
        String result = "------------------ APPLICATION CONFIGURATION --------------------\n";
        result += MULTIPLYING_FACTOR_FOR_ELEMENTS_KEY + " = " + MULTIPLYING_FACTOR_FOR_ELEMENTS + "\n";
        result += MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_KEY + " = " + MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS + "\n";
        result += LEVEL_RESISTANCE_KEY + " = " + LEVEL_RESISTANCE + "\n";
        result += EXTERNAL_RESISTANCE_KEY + " = " + EXTERNAL_RESISTANCE + "\n";
        result += MIN_SAMPLE_SIZE_KEY + " = " + MIN_SAMPLE_SIZE + "\n";
        result += SIMILARITY_THRESHOLD_FOR_MATCHER_KEY + " = " + SIMILARITY_THRESHOLD_FOR_MATCHER + "\n";
        result += SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS_KEY + " = " + SIMILARITY_THRESHOLD_FOR_FIND_MAPPINGS + "\n";
        result += SIMILARITY_THRESHOLD_FOR_PRUNING_KEY + " = " + SIMILARITY_THRESHOLD_FOR_PRUNING + "\n";
        result += QUALITY_THRESHOLD_KEY + " = " + QUALITY_THRESHOLD + "\n";
        result += CHARACTER_CATEGORIES_KEY + "\n";

        for (CharacterCategory category : getCategories()) {
            result += category.getName() + " - grade: " + getGrades().get(category.getId()) + "\n";
        }
        result += COMPARATOR_CHAIN_KEY + "\n";
        for (IFeatureComparator comparator : getComparatorChain()) {
            result += comparator + "\n";
        }
        result += COMPARATOR_CHAIN_FOR_MATCH_KEY + "\n";
        for (IFeatureComparator comparator : getComparatorChainForMatch()) {
            result += comparator + "\n";
        }
        for (Binding binding : this.injector.getBindings().values()) {
            Key key = binding.getKey();
            if (key.toString().indexOf("it.unibas.spicy") != -1) {
                //excludes constants
                String annotation = "";
                if (key.getAnnotation() != null) {
                    annotation = key.getAnnotation().toString();
                    annotation = annotation.substring(annotation.indexOf("="), annotation.indexOf(")"));
                }
                result += getClassName(key.getTypeLiteral().toString()) + " " + annotation + " \t" + getClassName(injector.getInstance(binding.getKey()).toString()) + "\n";
            }
        }
        result += "----------------------------------------------------------";
        return result;
    }

    public String getClassName(String fullyQualifiedName) {
        return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
    }
}
