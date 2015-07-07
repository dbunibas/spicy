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

import it.unibas.spicy.model.algebra.query.operators.sql.ExecuteSQL;
import it.unibas.spicy.model.algebra.query.operators.sql.GenerateSQL;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.persistence.AccessConfiguration;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import it.unibas.spicy.test.tools.IInstanceChecker;
import it.unibas.spicy.test.tools.SQLInstanceChecker;
import java.io.Reader;

public abstract class SQLQueryTest extends MappingTaskTest {

    private String tempDatabaseName = "testdb";
    protected Reader sourceSQLScriptReader;
    protected Reader sourceInstanceSQLScriptReader;
    protected Reader targetSQLScriptReader;
    protected Reader intermediateSQLScriptReader;
    protected String expectedInstanceFile;

    protected abstract void initFileReferences(); // to initialize file references in subclass

    public void testScript() throws Exception {
//        generateScript();
        INode instance = executeScripts(generateDatabaseName());
        checkExpectedInstances(instance, expectedInstanceFile);
    }

    protected String generateDatabaseName() {
        String className = this.getClass().getSimpleName();
        className = className.substring(8);
        className += "_test";
        return className.toLowerCase();
    }

    private AccessConfiguration getAccessConfiguration(String databaseName) {
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver("org.postgresql.Driver");
        accessConfiguration.setUri("jdbc:postgresql:" + databaseName);
        accessConfiguration.setSchemaName(GenerateSQL.TARGET_SCHEMA_NAME);
        accessConfiguration.setLogin("pguser");
        accessConfiguration.setPassword("pguser");
        return accessConfiguration;
    }

    protected AccessConfiguration getTempAccessConfiguration() {
        return getAccessConfiguration(tempDatabaseName);
    }

    protected String generateScript() {
        if (logger.isTraceEnabled()) logger.trace(mappingTask.getMappingData().toLongString());
        String sqlScript = mappingTask.getMappingData().getSQLScript();
        if (logger.isDebugEnabled()) logger.debug(sqlScript);
        return sqlScript;
    }

    protected INode executeScripts(String databaseName) {
        try {
            initFileReferences();
            AccessConfiguration accessConfiguration = getAccessConfiguration(databaseName);
            AccessConfiguration tempAccessConfiguration = getAccessConfiguration(tempDatabaseName);
            String sqlScript = generateScript();
            INode instance = new ExecuteSQL().executeScript(mappingTask, accessConfiguration, tempAccessConfiguration, sqlScript, sourceSQLScriptReader, sourceInstanceSQLScriptReader, targetSQLScriptReader, intermediateSQLScriptReader);
            logger.debug("Loaded instance: \n" + instance.toShortString());
            return instance;
        } catch (DAOException ex) {
            logger.error(ex);
            return null;
        }
    }

    public void checkExpectedInstances(INode targetInstance, String expectedInstanceFile) throws Exception {
        IInstanceChecker checker = new SQLInstanceChecker(mappingTask.getConfig());
        checker.checkInstance(targetInstance, expectedInstanceFile);
    }
}
