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
 
package it.unibas.spicy.model.algebra.query.operators.xquery;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.DAOXsd;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExecuteXQuery {

    private static Log logger = LogFactory.getLog(ExecuteXQuery.class);
    private String TEMP_OUTPUT_FILE_NAME = "tempSaxonOutput.xml";

    public INode executeScript(MappingTask mappingTask, String sourceInstanceFileName) throws Exception {
        return executeScript(mappingTask, sourceInstanceFileName, generateTempOutputFileName(mappingTask));
    }

    public INode executeScript(MappingTask mappingTask, String sourceInstanceFileName, String tempOutputFileName) throws Exception {
        OutputStream outputStream = null;
        try {
            String xqueryScript = mappingTask.getMappingData().getXQueryScript(sourceInstanceFileName);
            if (logger.isDebugEnabled()) logger.debug("Executing script...");
            if (logger.isDebugEnabled()) logger.debug(xqueryScript);
            XQPreparedExpression exp = new SaxonXQDataSource().getConnection().prepareExpression(xqueryScript);
            XQResultSequence result = exp.executeQuery();
            if (logger.isDebugEnabled()) logger.debug("Creating output stream...");
            File tempOutputFile = new File(tempOutputFileName);
            outputStream = new FileOutputStream(tempOutputFile);
            result.writeSequence(outputStream, new Properties());
            if (logger.isDebugEnabled()) logger.debug("Writing output stream...");
            tempOutputFile.deleteOnExit();
            return loadInstance(mappingTask, tempOutputFileName);
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
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

    private String generateTempOutputFileName(MappingTask mappingTask) {
        String tempOutputFile = SpicyEngineUtility.generateFolderPath(mappingTask.getFileName()) + File.separator + TEMP_OUTPUT_FILE_NAME;
        return tempOutputFile;
    }

    private INode loadInstance(MappingTask mappingTask, String tempOutputFileName) throws DAOException {
        INode targetSchema = mappingTask.getTargetProxy().getSchema();
        DAOXsd daoXsd = new DAOXsd();
        IDataSourceProxy verification = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_XML, targetSchema));
        daoXsd.loadInstance(verification, tempOutputFileName);
        INode instance = verification.getOriginalInstances().get(0);
        logger.debug("Loaded instance: \n" + instance.toShortString());
        return instance;
    }
}
