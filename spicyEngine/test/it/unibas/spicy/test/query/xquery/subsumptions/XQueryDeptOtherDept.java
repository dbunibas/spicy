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
 
package it.unibas.spicy.test.query.xquery.subsumptions;

import it.unibas.spicy.model.algebra.query.operators.xquery.ExecuteXQuery;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.query.XQueryTest;
import java.io.File;

public class XQueryDeptOtherDept extends XQueryTest {

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.deptOtherDept).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        initFixture();
    }

    protected void initFileReferences() {
        sourceInstanceXMLFile = this.getClass().getResource(References.deptsXMLInstance).getPath();
//        expectedInstanceFile = this.getClass().getResource(References.otherDeptsXMLExpectedInstance).getPath();
    }

    public void testScript() throws Exception {
//        executeScripts();
//        INode instance = loadInstance();
        initFileReferences();
        ExecuteXQuery executor = new ExecuteXQuery();
        INode instance = executor.executeScript(mappingTask, sourceInstanceXMLFile);
//        checkExpectedInstances(instance, expectedInstanceFile);
    }

}
