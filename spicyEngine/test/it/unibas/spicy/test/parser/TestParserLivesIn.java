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
 
package it.unibas.spicy.test.parser;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.Utility;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestParserLivesIn extends MappingTaskTest {

    // livesInTarget SET PATH
    private static final String LIVESINTARGET_EMPDEPTSET = "livesInTarget.EmpDeptSet";
    private static final String LIVESINTARGET_DEPTCITYSET = "livesInTarget.DeptCitySet";
    private static final String LIVESINTARGET_HOMESET = "livesInTarget.HomeSet";
    // livesInTarget TUPLE PATH
    private static final String LIVESINTARGET_EMPDEPT = LIVESINTARGET_EMPDEPTSET + ".EmpDept";
    private static final String LIVESINTARGET_DEPTCITY = LIVESINTARGET_DEPTCITYSET + ".DeptCity";
    private static final String LIVESINTARGET_HOME = LIVESINTARGET_HOMESET + ".Home";
    // ATTRIBUTES
    private static final String LIVESINTARGET_HOME_HOMENAME = "homeName";
    private static final String LIVESINTARGET_HOME_HOMECITY = "homeCity";
    private static final String LIVESINTARGET_EMPDEPT_ENAME = "eName";
    private static final String LIVESINTARGET_EMPDEPT_DEPTID = "deptId";
    private static final String LIVESINTARGET_DEPTCITY_CITY = "city";

    public void test1() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.livesInTGD).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    public void test2() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.livesInReorderedTGD).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode instanceNode = finalResult.getInstances().get(0);
        checkEmpDeptSet(instanceNode);
        checkDeptCitySet(instanceNode);
        checkHomeSet(instanceNode);
    }

    private void checkEmpDeptSet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(LIVESINTARGET_EMPDEPT, instanceNode);
        assertEquals(4, nodes.size());
        assertTrue(checkEmpDept("Alice", nodes));
        assertTrue(checkEmpDept("Bob", nodes));
        assertTrue(checkEmpDept("FrankNoLivesIn", nodes));
        assertTrue(checkEmpDept("MaryNoEmpCity", nodes));

        assertEquals(4, Utility.getDifferentGeneratedValues(LIVESINTARGET_EMPDEPT_DEPTID, nodes));
    }

    private boolean checkEmpDept(Object expectedEName, List<INode> nodes) {
        for (INode node : nodes) {
            Object ename = node.getChild(LIVESINTARGET_EMPDEPT_ENAME).getChild(0).getValue();
            if (ename.toString().equals(expectedEName.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkDeptCitySet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(LIVESINTARGET_DEPTCITY, instanceNode);
        assertEquals(4, nodes.size()); // ATTENZIONE: dovuto alla foreign key mandatory
        assertTrue(checkDeptCity("SJ", nodes));
        assertTrue(checkDeptCity("SD", nodes));
        assertTrue(checkDeptCity("NY", nodes));
    }

    private boolean checkDeptCity(Object expectedCity, List<INode> nodes) {
        for (INode node : nodes) {
            Object city = node.getChild(LIVESINTARGET_DEPTCITY_CITY).getChild(0).getValue();
            if (city.toString().equals(expectedCity.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkHomeSet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(LIVESINTARGET_HOME, instanceNode);
        assertEquals(4, nodes.size());
        assertTrue(checkHome("Alice", "SF", nodes));
        assertTrue(checkHome("Bob", "LA", nodes));
        assertTrue(checkHome("MaryNoEmpCity", "MW", nodes));
        assertTrue(checkHome("FrankNoLivesIn", NullValueFactory.getNullValue(), nodes));
    }

    private boolean checkHome(Object expectedHomeName, Object expectedHomeCity, List<INode> nodes) {
        for (INode node : nodes) {
            Object homeName = node.getChild(LIVESINTARGET_HOME_HOMENAME).getChild(0).getValue();
            Object homeCity = node.getChild(LIVESINTARGET_HOME_HOMECITY).getChild(0).getValue();
            if (homeName.toString().equals(expectedHomeName.toString()) &&
                    homeCity.toString().equals(expectedHomeCity.toString())) {
                return true;
            }
        }
        return false;
    }
}

