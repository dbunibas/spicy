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

import com.ibatis.common.logging.Log;
import com.ibatis.common.logging.LogFactory;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.generators.operators.CheckSortForSkolems;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.test.References;
import java.io.File;
import junit.framework.TestCase;

public class CheckSortTest extends TestCase {

    private static Log logger = LogFactory.getLog(CheckSortTest.class);

    private MappingTask mappingTask;
    private DAOMappingTask daoMappingTask = new DAOMappingTask();
    private CheckSortForSkolems sortChecker = new CheckSortForSkolems();

    public void testStatDBExpenseDB() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.statDBexpenseDBMandatory).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testRS() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.chaseRSRed).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testSelfJoinSubsumption() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.mixedRed).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testRSNostro() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.RSNostroRed).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testRSSkolem() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.RSSkolem).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testRSCycle() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.RSCycleRed).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testRSCycle2() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.RSCycle2Red).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testKidsChildren() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.kidsPresentsChildrenDBSimpleSelfJoin).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void testSelfJoinInverse() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.stBenchmarkSelfJoinsInverse).toURI()).getAbsolutePath();
        execute(mappingTaskFile);
    }

    public void execute(String mappingTaskFile) throws Exception {
        String fileName = mappingTaskFile.substring(mappingTaskFile.lastIndexOf(File.separator));
        if (logger.isDebugEnabled()) logger.debug("###################################### " + fileName + "########################################");
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        if (logger.isDebugEnabled()) logger.debug("Requires sort: " + sortChecker.needsRuntimeSortInSkolems(mappingTask));
    }


}
