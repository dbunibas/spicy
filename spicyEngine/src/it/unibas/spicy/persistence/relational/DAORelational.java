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
 
package it.unibas.spicy.persistence.relational;

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.persistence.AccessConfiguration;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.Types;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAORelational {

    private static Log logger = LogFactory.getLog(DAORelational.class);
    private static IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();
    private DBFragmentDescription dataDescription = null;
    private static int NUMBER_OF_SAMPLE = 4000;
    private static final String TUPLE_SUFFIX = "Tuple";

    private static OID getOID() {
        return oidGenerator.getNextOID();
    }

    //////////////////////////////////////////////////////////
    //////////////////////// SCHEMA
    //////////////////////////////////////////////////////////
    public IDataSourceProxy loadSchema(AccessConfiguration accessConfiguration, DBFragmentDescription dataDescription, IConnectionFactory dataSourceDB) throws DAOException {
        this.dataDescription = dataDescription;
        INode root = null;
        String catalog = null;
        String schemaName = accessConfiguration.getSchemaName();
        DatabaseMetaData databaseMetaData = null;
        Connection connection = dataSourceDB.getConnection(accessConfiguration);
        IDataSourceProxy dataSource = null;
        try {
            databaseMetaData = connection.getMetaData();
            catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            root = this.createRootNode(catalog);
            String[] tableTypes = new String[]{"TABLE"};
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, tableTypes);
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (!this.dataDescription.checkLoadTable(tableName)) {
                    continue;
                }
                INode setTable = new SetNode(tableName);
                setTable.addChild(getTuple(databaseMetaData, catalog, schemaName, tableName));
                setTable.setRequired(false);
                setTable.setNotNull(true);
                root.addChild(setTable);
                addNode(tableName, setTable);
            }
            dataSource = new ConstantDataSourceProxy(new DataSource(SpicyEngineConstants.TYPE_RELATIONAL, root));
            dataSource.addAnnotation(SpicyEngineConstants.ACCESS_CONFIGURATION, accessConfiguration);
            loadPrimaryKeys(dataSource, databaseMetaData, catalog, schemaName);
            loadForeignKeys(dataSource, databaseMetaData, catalog, schemaName);
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        } finally {
            dataSourceDB.close(connection);
        }
        return dataSource;
    }

    private INode createRootNode(String catalog) {
        INode root = new TupleNode(catalog);
        root.setNotNull(true);
        root.setRequired(true);
        root.setRoot(true);
        addNode(catalog, root);
        return root;
    }

    private TupleNode getTuple(DatabaseMetaData databaseMetaData, String catalog, String schemaName, String tableName) throws SQLException {
        if (logger.isDebugEnabled()) logger.debug("\nTable: " + tableName);
        TupleNode tupleNode = new TupleNode(tableName + TUPLE_SUFFIX);
        tupleNode.setRequired(false);
        tupleNode.setNotNull(true);
        tupleNode.setVirtual(true);
        addNode(tableName + TUPLE_SUFFIX, tupleNode);
        ResultSet columnsResultSet = databaseMetaData.getColumns(catalog, schemaName, tableName, null);
        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("COLUMN_NAME");
            String keyColumn = tableName + "." + columnName;
            String columnType = columnsResultSet.getString("TYPE_NAME");
            String isNullable = columnsResultSet.getString("IS_NULLABLE");
            if (!this.dataDescription.checkLoadAttribute(tableName, columnName)) {
                continue;
            }
            boolean isNull = false;
            if (isNullable.equalsIgnoreCase("YES")) {
                isNull = true;
            }
            INode columnNode = new AttributeNode(columnName);
            addNode(keyColumn, columnNode);
            columnNode.setNotNull(!isNull);
            String typeOfColumn = convertDBTypeToDataSourceType(columnType);
            columnNode.addChild(new LeafNode(typeOfColumn));
            tupleNode.addChild(columnNode);
            if (logger.isDebugEnabled()) logger.debug("\n\tColumn Name: " + columnName + "(" + columnType + ") " + " type of column= " + typeOfColumn + "[IS_Nullable: " + isNullable + "]");
        }
        return tupleNode;
    }

    private void loadPrimaryKeys(IDataSourceProxy dataSource, DatabaseMetaData databaseMetaData, String catalog, String schemaName) {
        try {
            String[] tableTypes = new String[]{"TABLE"};
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, tableTypes);
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (!this.dataDescription.checkLoadTable(tableName)) {
                    logger.debug("Excluding table: " + tableName);
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("Searching primary keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getPrimaryKeys(catalog, null, tableName);
                List<PathExpression> listOfPath = new ArrayList<PathExpression>();
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (logger.isDebugEnabled()) logger.debug("Analyzing primary key: " + columnName);
                    if (!this.dataDescription.checkLoadAttribute(tableName, columnName)) {
                        continue;
                    }
                    if (logger.isDebugEnabled()) logger.debug("Found a Primary Key: " + columnName);
                    String keyPrimary = tableName + "." + columnName;
                    listOfPath.add(DAORelationalUtility.generatePath(keyPrimary));
                }
                if (!listOfPath.isEmpty()) {
                    KeyConstraint keyConstraint = new KeyConstraint(listOfPath, true);
                    dataSource.addKeyConstraint(keyConstraint);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    private void loadForeignKeys(IDataSourceProxy dataSource, DatabaseMetaData databaseMetaData, String catalog, String schemaName) {
        try {
            String[] tableTypes = new String[]{"TABLE"};
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, tableTypes);
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (!this.dataDescription.checkLoadTable(tableName)) {
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("Searching foreign keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getImportedKeys(catalog, null, tableName);
                List<String> listOfPrimaryKey = new ArrayList<String>();
                List<String> listOfForeignKey = new ArrayList<String>();
                String previousTableName = "";
                while (resultSet.next()) {
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                    String keyPrimaryKey = pkTableName + "." + pkColumnName;
                    //AttributeNode primaryKey = (AttributeNode)DataSourceFactory.getNode(keyPrimary);

                    String fkTableName = resultSet.getString("FKTABLE_NAME");
                    String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                    String keyForeignKey = fkTableName + "." + fkColumnName;
                    if (logger.isDebugEnabled()) logger.debug("Analyzing foreign key: " + keyForeignKey + " references " + keyPrimaryKey);
                    if (!this.dataDescription.checkLoadTable(pkTableName) || !this.dataDescription.checkLoadTable(fkTableName)) {
                        if (logger.isDebugEnabled()) logger.debug("Check load tables. Foreign key discarded: " + keyForeignKey + " references " + keyPrimaryKey);
                        continue;
                    }
                    if (!this.dataDescription.checkLoadAttribute(pkTableName, pkColumnName) || !this.dataDescription.checkLoadAttribute(fkTableName, fkColumnName)) {
                        if (logger.isDebugEnabled()) logger.debug("Check load attributes. Foreign key discarded: " + keyForeignKey + " references " + keyPrimaryKey);
                        continue;
                    }
                    if (logger.isDebugEnabled()) logger.debug("Analyzing Primary Key: " + keyPrimaryKey + " Found a Foreign Key: " + fkColumnName + " in table " + fkTableName);
                    if (!listOfPrimaryKey.contains(keyPrimaryKey) && (previousTableName.equals("") || previousTableName.equals(pkTableName))) {
                        if (logger.isDebugEnabled()) logger.debug("Adding nodes to collection: " + keyPrimaryKey + " - " + keyForeignKey);
                        listOfPrimaryKey.add(keyPrimaryKey);
                        listOfForeignKey.add(keyForeignKey);
                    } else if (!listOfPrimaryKey.isEmpty() && !listOfForeignKey.isEmpty()) {
                        if (logger.isDebugEnabled()) logger.debug("Generating constraint: " + listOfForeignKey + " reference " + listOfPrimaryKey);
                        DAORelationalUtility.generateConstraint(listOfForeignKey.toArray(), listOfPrimaryKey.toArray(), dataSource);
                        listOfPrimaryKey.clear();
                        listOfForeignKey.clear();
                        listOfPrimaryKey.add(keyPrimaryKey);
                        listOfForeignKey.add(keyForeignKey);
                    }
                    previousTableName = pkTableName;
                }
                if (logger.isDebugEnabled()) logger.debug("Main loop: " + listOfForeignKey + " reference " + listOfPrimaryKey);
                if (!listOfPrimaryKey.isEmpty() && !listOfForeignKey.isEmpty()) {
                    DAORelationalUtility.generateConstraint(listOfForeignKey.toArray(), listOfPrimaryKey.toArray(), dataSource);
                }
                if (logger.isDebugEnabled()) logger.debug("Foreign keys loaded. Exiting");
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    private void addNode(String key, INode node) {
        DAORelationalUtility.addNode(key, node);
    }

    private INode getNode(String label) {
        return DAORelationalUtility.getNode(label);
    }

    /////////////////////////////////////////////////////////////
    //////////////////////// INSTANCE
    /////////////////////////////////////////////////////////////
    public INode loadInstance(AccessConfiguration accessConfiguration, DBFragmentDescription dataDescription, IConnectionFactory dataSourceDB) throws DAOException {
        INode root = null;
        String catalog = null;
        String schemaName = accessConfiguration.getSchemaName();
        DatabaseMetaData databaseMetaData = null;
        Connection connection = dataSourceDB.getConnection(accessConfiguration);
        try {
            databaseMetaData = connection.getMetaData();
            catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            if (logger.isDebugEnabled()) logger.debug("Catalog: " + catalog);
            root = new TupleNode(DAORelationalUtility.getNode(catalog).getLabel(), getOID());
            root.setRoot(true);
            String[] tableTypes = new String[]{"TABLE"};
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, tableTypes);
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (!this.dataDescription.checkLoadTable(tableName)) {
                    continue;
                }
                INode setTable = new SetNode(DAORelationalUtility.getNode(tableName).getLabel(), getOID());
                if (logger.isDebugEnabled()) logger.debug("extracting value for table " + tableName + " ....");
                getInstanceByTable(dataSourceDB, connection, schemaName, tableName, setTable);
                root.addChild(setTable);
            }
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        } finally {
            dataSourceDB.close(connection);
        }
        return root;
    }

    private void getInstanceByTable(IConnectionFactory dataSourceDB, Connection connection, String schemaName, String tableName, INode setTable) throws DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String tablePath = tableName;
            if (!schemaName.equals("")) {
                tablePath = schemaName + "." + tableName;
            }
            statement = connection.prepareStatement("select * from " + tablePath);
            statement.setMaxRows(NUMBER_OF_SAMPLE);
            resultSet = statement.executeQuery();
            if (resultSet == null) {
                throw new DAOException("ResultSet is NULL !");
            }
            int counterOfSample = 0;
            while (resultSet.next() && counterOfSample < NUMBER_OF_SAMPLE) {
                counterOfSample++;
                TupleNode tupleNode = new TupleNode(getNode(tableName + TUPLE_SUFFIX).getLabel(), getOID());
                setTable.addChild(tupleNode);
                for (INode attributeNodeSchema : getNode(tableName + TUPLE_SUFFIX).getChildren()) {
                    AttributeNode attributeNode = new AttributeNode(attributeNodeSchema.getLabel(), getOID());
                    String columnName = attributeNodeSchema.getLabel();
                    Object columnvalue = resultSet.getObject(columnName);
                    LeafNode leafNode = createLeafNode(attributeNodeSchema, columnvalue);
                    attributeNode.addChild(leafNode);
                    tupleNode.addChild(attributeNode);
                }
            }
        } catch (SQLException sqle) {
            throw new DAOException(sqle.getMessage());
        } finally {
            dataSourceDB.close(resultSet);
            dataSourceDB.close(statement);
        }
    }

    private LeafNode createLeafNode(INode attributeNode, Object untypedValue) throws DAOException {
        LeafNode leafNodeInSchema = (LeafNode) attributeNode.getChild(0);
        String type = leafNodeInSchema.getLabel();
        Object typedValue = Types.getTypedValue(type, untypedValue);
        return new LeafNode(type, typedValue);
    }

    private String convertDBTypeToDataSourceType(String columnType) {
        if (columnType.equalsIgnoreCase("varchar") || columnType.equalsIgnoreCase("char") ||
                columnType.equalsIgnoreCase("text") || columnType.equalsIgnoreCase("bpchar") ||
                columnType.equalsIgnoreCase("bit") || columnType.equalsIgnoreCase("mediumtext") ||
                columnType.equalsIgnoreCase("longtext")) {
            return Types.STRING;
        }
        if (columnType.equalsIgnoreCase("serial") || columnType.equalsIgnoreCase("enum")) {
            return Types.STRING;
        }
        if (columnType.equalsIgnoreCase("date")) {
            return Types.DATE;
        }
        if (columnType.equalsIgnoreCase("datetime") || columnType.equalsIgnoreCase("timestamp")) {
            return Types.DATETIME;
        }
        if (columnType.toLowerCase().startsWith("serial") || columnType.toLowerCase().startsWith("int") || columnType.toLowerCase().startsWith("tinyint") || columnType.toLowerCase().startsWith("bigint") || columnType.toLowerCase().startsWith("smallint")) {
            return Types.INTEGER;
        }
        if (columnType.toLowerCase().startsWith("float") || columnType.toLowerCase().startsWith("real") || columnType.toLowerCase().startsWith("float")) {
            return Types.DOUBLE;
        }
        if (columnType.equalsIgnoreCase("bool")) {
            return Types.BOOLEAN;
        }
        return Types.STRING;
    }
}
