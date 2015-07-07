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
 
package it.unibas.spicy.test;

import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.persistence.DAOMappingTask;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MappingTaskTest extends TestCase {

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

    private void changeConfiguration() {
//        mappingTask.getConfig().setSortStrategy(EngineConfiguration.NO_SORT);
//        mappingTask.getConfig().setUseSkolemTable(false);
//        mappingTask.getConfig().setRewriteAllHomorphisms(true);
//        mappingTask.getConfig().setUseLocalSkolem(true);
//        mappingTask.getConfig().setUseSkolemStrings(true);
    }

    public void solveAndPrintResults() {
        changeConfiguration();
        IAlgebraOperator completeTree = mappingTask.getMappingData().getAlgebraTree();
        IAlgebraOperator canonicalTree = mappingTask.getMappingData().getCanonicalAlgebraTree();
        IDataSourceProxy source = mappingTask.getSourceProxy();
//        if (logger.isTraceEnabled()) logger.trace("------------------Algebra tree:---------------------\n");
//        if (logger.isTraceEnabled()) logger.trace("\n" + completeTree);
        IDataSourceProxy canonicalSolutions = null;
        canonicalSolutions = canonicalTree.execute(mappingTask.getSourceProxy());
        this.finalResult = completeTree.execute(source);
        if (mappingTask != null) {
            if (logger.isTraceEnabled()) {
                if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().toVeryLongString());
            }
            if (logger.isDebugEnabled()) {
                if (logger.isInfoEnabled()) logger.info(mappingTask.getMappingData().rewritingStatsString());
                if (logger.isInfoEnabled()) logger.info("------------------Source instance:---------------------\n");
                if (logger.isDebugEnabled()) logger.debug(source.toInstanceString());
                if (logger.isInfoEnabled()) logger.info("------------------Canonical solution:---------------------\n");
                if (logger.isDebugEnabled()) logger.debug(canonicalSolutions.toInstanceString());
                if (logger.isInfoEnabled()) logger.info("------------------Final result:---------------------\n");
                if (logger.isInfoEnabled()) logger.info(finalResult.toInstanceString());

            }
        }
    }
}
