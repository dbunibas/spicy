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
package it.unibas.spicy.test.mappingtask;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.test.tools.IInstanceChecker;
import it.unibas.spicy.test.tools.MappingTaskCSVInstanceChecker;
import it.unibas.spicy.test.tools.MappingTaskXMLInstanceChecker;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MappingTaskTest extends TestCase {

    private static final String TYPE_CSV = "csv";
    private static final String TYPE_XML = "xml";
    protected Log logger = LogFactory.getLog(this.getClass());
    protected MappingTask mappingTask;
    protected DAOMappingTask daoMappingTask = new DAOMappingTask();
    protected IDataSourceProxy finalResult;

    protected void setUp() throws Exception {
    }

    protected void initFixture() {
        changeConfiguration();
    }

    public void testDummy() {
    }

    protected void tearDown() throws Exception {
        SetAlias.resetId();
        IntegerOIDGenerator.resetCounter();
        IntegerOIDGenerator.clearCache();
    }

    public void changeConfiguration() {
//        mappingTask.getConfig().setRewriteOnlyProperHomomorphisms(false);
//        mappingTask.getConfig().setSortStrategy(EngineConfiguration.NO_SORT);
//        mappingTask.getConfig().setUseSkolemTable(false);
//        mappingTask.getConfig().setUseLocalSkolem(true);
        mappingTask.getConfig().setUseSkolemStrings(true);
    }

    public void printMappingTask() {
        if (logger.isDebugEnabled()) logger.debug(mappingTask);
        if (logger.isDebugEnabled()) {
            logger.debug("================ Candidate Source to Target TGDs ==================\n");
            for (FORule tgd : mappingTask.getMappingData().getCandidateSTTgds()) {
                logger.debug(tgd);
            }
            logger.debug("================ Candidate Source to Target TGDs  (in logical form) ==================\n");
            for (FORule tgd : mappingTask.getMappingData().getCandidateSTTgds()) {
                logger.debug(tgd.toLogicalString(mappingTask));
            }
        }
    }

    public void solveAndPrintResults() {
        RuntimeException ex = null;
        changeConfiguration();
        try {
//            this.finalResult = completeTree.execute(source);
            this.finalResult = mappingTask.getMappingData().getSolution();
            mappingTask.getMappingData().verifySolution();
        } catch (Throwable t) {
            logger.error(t);
            ex = new RuntimeException(t);
        }
        if (logger.isTraceEnabled()) {
            if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().toVeryLongString());
        }
        if (logger.isDebugEnabled()) {
            if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().rewritingStatsString());
            if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().toLongString());
            if (logger.isTraceEnabled()) logger.debug("------------------Formula variables:---------------------\n");
            List<FORule> finalRules = mappingTask.getMappingData().getRewrittenRules();
            for (FORule fORule : finalRules) {
                if (logger.isTraceEnabled()) logger.debug(fORule.getVariableMaps(mappingTask));
            }
            if (logger.isInfoEnabled()) logger.info("------------------Source instance:---------------------\n");
            IDataSourceProxy source = mappingTask.getSourceProxy();
            if (logger.isInfoEnabled()) logger.debug(source.toInstanceString());
//            IAlgebraOperator completeTree = mappingTask.getMappingData().getAlgebraTree();
//            if (logger.isTraceEnabled()) logger.trace("------------------Algebra tree:---------------------\n");
//            if (logger.isTraceEnabled()) logger.trace("\n" + completeTree);
            IDataSourceProxy canonicalSolutions = mappingTask.getMappingData().getCanonicalSolution();
            if (logger.isInfoEnabled()) logger.info("------------------Canonical solution:---------------------\n");
            if (logger.isInfoEnabled()) logger.debug(canonicalSolutions.toInstanceString());
            if (logger.isInfoEnabled()) logger.info("------------------Final result:---------------------\n");
            if (logger.isInfoEnabled()) if (finalResult != null) logger.info(finalResult.toInstanceString());
//            if (logger.isInfoEnabled()) if (finalResult != null) logger.info(finalResult.toLongStringWithOids());
        }
        if (ex != null) throw ex;
    }

    public void checkExpectedCSVInstances() throws Exception {
        checkExpectedInstances(TYPE_CSV);
    }

    public void checkExpectedXMLInstances() throws Exception {
        checkExpectedInstances(TYPE_XML);
    }

    @SuppressWarnings("unchecked")
    private void checkExpectedInstances(String type) throws Exception {
        if (mappingTask != null) {
            List<String> instanceFiles = (List<String>) mappingTask.getSourceProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
            assertTrue("No translated instances...", !(finalResult.getInstances().isEmpty()));
            for (INode targetInstance : finalResult.getInstances()) {
                Integer instanceId = (Integer) targetInstance.getAnnotation(SpicyEngineConstants.SOURCE_INSTANCE_POSITION);
                String instanceFile = null;
                if (instanceFiles == null) {
                    String mappingTaskFilePath = mappingTask.getFileName();
                    String mappingTaskFolder = mappingTaskFilePath.substring(0, mappingTaskFilePath.lastIndexOf(File.separator));
                    instanceFile = mappingTaskFolder + File.separator + "solution" + instanceId + ".";
                } else {
                    instanceFile = instanceFiles.get(instanceId);
                }
                String expectedResultFile = generateExpectedFileName(instanceFile, type);
                IInstanceChecker checker = getInstanceChecker(mappingTask.getConfig(), mappingTask.getTargetProxy(), type);
                checker.checkInstance(targetInstance, expectedResultFile);
            }
        }
    }

    public void checkRewriting() throws Exception {
        if (logger.isDebugEnabled()) logger.debug("------------------Rewritten scenario---------------------\n");
        File tmpFile = File.createTempFile("spicyRewritingTest", ".tgd");
        if (!logger.isDebugEnabled()) tmpFile.deleteOnExit();
        if (logger.isDebugEnabled()) logger.debug("Rewriting File path: " + tmpFile.toString());
        String originalExpectedInstance = getInstanceFiles(0);
        daoMappingTask.saveMappingTask(mappingTask, tmpFile.toString());
        mappingTask = daoMappingTask.loadMappingTask(tmpFile.toString());
        List<String> instanceFiles = new ArrayList<String>();
        instanceFiles.add(originalExpectedInstance);
        mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
        solveAndPrintResults();
        checkExpectedCSVInstances();
    }

    public String getInstanceFiles(int position) {
        if(position >= finalResult.getInstances().size()){
            throw new IllegalArgumentException("There are only " + finalResult.getInstances().size() + " instances");
        }
        INode targetInstance = finalResult.getInstances().get(position);
        List<String> instanceFiles = (List<String>) mappingTask.getSourceProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
        Integer instanceId = (Integer) targetInstance.getAnnotation(SpicyEngineConstants.SOURCE_INSTANCE_POSITION);
        String instanceFile = null;
        if (instanceFiles == null) {
            String mappingTaskFilePath = mappingTask.getFileName();
            String mappingTaskFolder = mappingTaskFilePath.substring(0, mappingTaskFilePath.lastIndexOf(File.separator));
            instanceFile = mappingTaskFolder + File.separator + "solution" + instanceId + ".";
        } else {
            instanceFile = instanceFiles.get(instanceId);
        }
        return instanceFile;
    }

    private String generateExpectedFileName(String instanceFile, String type) {
        String fileName = instanceFile.substring(0, instanceFile.lastIndexOf("."));
        return fileName + "-expected." + type;
    }

    private IInstanceChecker getInstanceChecker(EngineConfiguration config, IDataSourceProxy dataSource, String type) {
        if (type.equals(TYPE_CSV)) {
            return new MappingTaskCSVInstanceChecker(config);
        }
        if (type.equals(TYPE_XML)) {
            return new MappingTaskXMLInstanceChecker(config, dataSource);
        }
        throw new IllegalArgumentException("There is no instance checker for type " + type);
    }
}
