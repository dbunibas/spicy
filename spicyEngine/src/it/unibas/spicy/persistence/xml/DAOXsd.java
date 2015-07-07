/*
 Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
 Giansalvatore Mecca - giansalvatore.mecca@unibas.it
 Salvatore Raunich - salrau@gmail.com
 Alessandro Pappalardo - pappalardo.alessandro@gmail.com
 Gianvito Summa - gianvito.summa@gmail.com

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
package it.unibas.spicy.persistence.xml;

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.xml.operators.ExportXMLInstances;
import it.unibas.spicy.persistence.xml.operators.ExportXSD;
import it.unibas.spicy.persistence.xml.operators.LoadXMLFile;
import it.unibas.spicy.persistence.xml.model.XSDSchema;
import it.unibas.spicy.persistence.xml.operators.CutDataSource;
import it.unibas.spicy.persistence.xml.operators.GenerateSchemaFromXSDTree;
import it.unibas.spicy.persistence.xml.operators.UpdateDataSourceWithConstraints;
import it.unibas.spicy.persistence.xml.operators.GenerateXSDNodeTree;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOXsd {

    protected static Log logger = LogFactory.getLog(DAOXsd.class);
    private static final String INSTANCE_SUFFIX = "-instance";
    private static final String TRANSLATED_INSTANCE_SUFFIX = "-translatedInstance";
    private static final String CANONICAL_INSTANCE_SUFFIX = "-canonicalInstance";
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public IDataSourceProxy loadSchema(String fileName) throws DAOException {
        try {
            GenerateXSDNodeTree xsdTreeGenerator = new GenerateXSDNodeTree();
            XSDSchema xsdSchema = xsdTreeGenerator.generateXSDNodeTree(fileName);
            if (logger.isDebugEnabled()) logger.debug("XSD Schema:\n" + xsdSchema);
            GenerateSchemaFromXSDTree schemaGenerator = new GenerateSchemaFromXSDTree();
            INode schema = schemaGenerator.generateSchema(xsdSchema);
            IDataSourceProxy dataSource = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_XML, schema));
            dataSource.addAnnotation(SpicyEngineConstants.XML_SCHEMA_FILE, fileName);
            dataSource.addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, new ArrayList<String>());
            // UpdateDataSourceWithConstraints has state, and therefore we need a fresh copy for each invocation
            UpdateDataSourceWithConstraints updateDataSource = new UpdateDataSourceWithConstraints();
            updateDataSource.updateDataSource(dataSource, xsdSchema);
            if (logger.isDebugEnabled()) logger.debug(dataSource.getSchema());
            return dataSource;
        } catch (Throwable ex) {
            logger.error("Error: " + ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void loadInstance(IDataSourceProxy dataSource, String fileName) throws DAOException {
        loadInstance(dataSource, false, fileName);
    }

    @SuppressWarnings("unchecked")
    public void loadInstance(IDataSourceProxy dataSource, boolean skipSetElement, String fileName) throws DAOException {
        try {
            LoadXMLFile xmlLoader = new LoadXMLFile();
            INode instanceNode = xmlLoader.loadInstance(dataSource, skipSetElement, fileName);
            dataSource.addInstanceWithCheck(instanceNode);
            List<String> instanceFiles = (List<String>) dataSource.getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
            if (instanceFiles == null) {
                instanceFiles = new ArrayList<String>();
                dataSource.addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
            }
            instanceFiles.add(fileName);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void exportDataSource(IDataSourceProxy dataSource, String directoryPath) throws DAOException {
        exportSchema(dataSource, directoryPath);
        exportXMLinstances(dataSource, directoryPath);
        dataSource.setType(SpicyEngineConstants.TYPE_XML);
    }

    public void exportSchema(IDataSourceProxy dataSource, String directoryPath) throws DAOException {
        try {
            ExportXSD xsdExporter = new ExportXSD();
            xsdExporter.exportXSDSchema(dataSource, directoryPath);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void exportXMLinstances(IDataSourceProxy dataSource, String directoryPath) throws DAOException {
        try {
            ExportXMLInstances exporter = new ExportXMLInstances();
            exporter.exportXMLinstances(dataSource.getOriginalInstances(), directoryPath, INSTANCE_SUFFIX);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void exportTranslatedXMLinstances(DataSource dataSource, String directoryPath) throws DAOException {
        try {
            ExportXMLInstances exporter = new ExportXMLInstances();
            exporter.exportXMLinstances(dataSource.getInstances(), directoryPath, TRANSLATED_INSTANCE_SUFFIX);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void exportCanonicalXMLinstances(DataSource dataSource, String directoryPath) throws DAOException {
        try {
            ExportXMLInstances exporter = new ExportXMLInstances();
            exporter.exportXMLinstances(dataSource.getInstances(), directoryPath, CANONICAL_INSTANCE_SUFFIX);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void cutAndExportSchema(IDataSourceProxy dataSource, String subTreePathString, String directoryPath) throws DAOException {
        PathExpression subTreePath = pathGenerator.generatePathFromString(subTreePathString);
        CutDataSource cutter = new CutDataSource();
        IDataSourceProxy cuttedDataSource = cutter.cutSchema(dataSource, subTreePath);
        exportDataSource(cuttedDataSource, directoryPath);
    }

    public void cutAndExportInstances(IDataSourceProxy dataSource, int maxNumberOfChildren, String directoryPath) throws DAOException {
        CutDataSource cutter = new CutDataSource();
        IDataSourceProxy cuttedDataSource = cutter.cutInstance(dataSource, maxNumberOfChildren);
        exportDataSource(cuttedDataSource, directoryPath);
    }
}