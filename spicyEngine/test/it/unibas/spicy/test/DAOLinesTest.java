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

import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import junit.framework.Assert;

public class DAOLinesTest extends MappingTaskTest {

    protected void setUp() throws Exception {
    }

    public void testConfig() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.expenseDBStatDBConfig).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        Assert.assertEquals(false, mappingTask.getConfig().rewriteSubsumptions());
        Assert.assertEquals(false, mappingTask.getConfig().rewriteCoverages());
        Assert.assertEquals(false, mappingTask.getConfig().rewriteSelfJoins());
        Assert.assertEquals(true, mappingTask.getConfig().rewriteEGDs());
        Assert.assertEquals(1, mappingTask.getConfig().getSortStrategy());
        Assert.assertEquals(1, mappingTask.getConfig().getSkolemTableStrategy());
        Assert.assertEquals(true, mappingTask.getConfig().useLocalSkolem());
//        solveAndPrintResults();
//        checkExpectedCSVInstances();
    }
}

