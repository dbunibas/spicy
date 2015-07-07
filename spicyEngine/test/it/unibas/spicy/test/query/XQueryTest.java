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
 
package it.unibas.spicy.test.query;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.DAOXsd;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import it.unibas.spicy.test.tools.IInstanceChecker;
import it.unibas.spicy.test.tools.XQueryInstanceChecker;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;

public abstract class XQueryTest extends MappingTaskTest {

    protected String sourceInstanceXMLFile;
    protected String targetXMLSchemaFile;
    protected String expectedInstanceFile;
    private File tempOutputFile = new File("C:/tmp/tempOutput.xml");
    private File tempXQueryScriptFile = new File("C:/tmp/tempXQueryScript.xq");

    protected abstract void initFileReferences(); // to initialize file references in subclass

    protected INode loadInstance() {
        try {
            INode targetSchema = mappingTask.getTargetProxy().getSchema();
            DAOXsd daoXsd = new DAOXsd();
            IDataSourceProxy verification = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_XML, targetSchema));
            daoXsd.loadInstance(verification, tempOutputFile.getAbsolutePath());
            INode instance = verification.getOriginalInstances().get(0);
            logger.debug("Loaded instance: \n" + instance.toShortString());
            return instance;
        } catch (DAOException ex) {
            fail();
            logger.error("Error loading instance: " + ex);
            return null;
        }
    }

    protected String generateScript() {
        if (logger.isTraceEnabled()) logger.trace(mappingTask.getMappingData());
        if (logger.isDebugEnabled()) logger.debug("getting script...");

        String xqueryScript = generateXQuery(mappingTask);
        if (logger.isDebugEnabled()) logger.debug("\nXQuery Script: \n" + xqueryScript);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempXQueryScriptFile);
            outputStream.write(xqueryScript.getBytes());
        } catch (Exception ex) {
            logger.error("Exception handling temporary xquery file: " + ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                logger.error("Error closing output temp file: " + ex);
            }
        }
        return xqueryScript;
    }

    protected String generateXQuery(MappingTask mappingTask) {
        return mappingTask.getMappingData().getXQueryScript(sourceInstanceXMLFile);
    }

    protected void executeScripts() throws XQException {
        OutputStream outputStream = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Initializing file references...");
            initFileReferences();
            if (logger.isDebugEnabled()) logger.debug("Generating script...");
//            String xqueryScript = generateScript();
            String xqueryScript = generateXQuery(mappingTask);
            if (logger.isDebugEnabled()) logger.debug("Executing script...");
            if (logger.isDebugEnabled()) logger.debug(xqueryScript);
            XQPreparedExpression exp = new SaxonXQDataSource().getConnection().prepareExpression(xqueryScript);
            XQResultSequence result = exp.executeQuery();
            if (logger.isDebugEnabled()) logger.debug("Creating output stream...");
            outputStream = new FileOutputStream(tempOutputFile);
            result.writeSequence(outputStream, new Properties());
            if (logger.isDebugEnabled()) logger.debug("Writing output stream...");
            tempOutputFile.deleteOnExit();
        } catch (FileNotFoundException ex) {
            logger.error("Temp file not found: " + ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                logger.error("Error closing output temp file: " + ex);
            }
        }
    }

    public void checkExpectedInstances(INode targetInstance, String expectedInstanceFile) throws Exception {
        IInstanceChecker checker = new XQueryInstanceChecker(mappingTask.getConfig());
        checker.checkInstance(targetInstance, expectedInstanceFile);
    }
}
