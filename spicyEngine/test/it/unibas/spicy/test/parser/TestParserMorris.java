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
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestParserMorris extends MappingTaskTest {

    // SOURCE SET PATH
    public static final String SOURCE_STUDENTS = "morrisSource.students";
    public static final String SOURCE_EMPLOYEES = "morrisSource.employees";

    // TARGET SET PATH
    public static final String TARGET_PERSONS = "morrisTarget.persons";
    // TUPLE NODES PATHS
    private static final String TARGET_PERSON = TARGET_PERSONS + ".person";
    // ATTRIBUTES
    public static final String TARGET_PERSON_NAME = "name";
    public static final String TARGET_PERSON_BIRTHDATE = "birthdate";
    public static final String TARGET_PERSON_SSN = "ssn";
    public static final String TARGET_PERSON_PHONE = "phone";

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.morrisTGD).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
    }

    public void testMappingTask() {
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode instanceNode = finalResult.getInstances().get(0);
        checkPersons(instanceNode);
    }

    private void checkPersons(INode instanceNode) {
        List<INode> personNodes = new FindNode().findNodesInInstance(TARGET_PERSON, instanceNode);
        assertEquals(5, personNodes.size());

        assertTrue(checkPerson("Maxwell", 1980, 12345, NullValueFactory.getNullValue(), personNodes));
        assertTrue(checkPerson("Morris", NullValueFactory.getNullValue(), 10022, 3030, personNodes));
        assertTrue(checkPerson("Bolte", 1979, 25555, NullValueFactory.getNullValue(), personNodes));
        assertTrue(checkPerson("Morris", 1982, 10022, NullValueFactory.getNullValue(), personNodes));
        assertTrue(checkPerson("Lempel", NullValueFactory.getNullValue(), 99999, 2020, personNodes));
    }

    private boolean checkPerson(Object expectedName, Object expectedBirthdate, Object expectedSSN, Object expectedPhone, List<INode> nodes) {
        for (INode node : nodes) {
            Object name = node.getChild(TARGET_PERSON_NAME).getChild(0).getValue();
            Object birthdate = node.getChild(TARGET_PERSON_BIRTHDATE).getChild(0).getValue();
            Object ssn = node.getChild(TARGET_PERSON_SSN).getChild(0).getValue();
            Object phone = node.getChild(TARGET_PERSON_PHONE).getChild(0).getValue();
            if (name.toString().equals(expectedName.toString()) &&
                    birthdate.toString().equals(expectedBirthdate.toString()) &&
                    ssn.toString().equals(expectedSSN.toString()) &&
                    phone.toString().equals(expectedPhone.toString())) {
                return true;
            }
        }
        return false;
    }
}
