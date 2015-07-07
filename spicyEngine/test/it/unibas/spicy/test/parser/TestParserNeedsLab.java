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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestParserNeedsLab extends MappingTaskTest {

    private static final String TARGET_TUTORS = "needsLabTarget.Tutors";
    private static final String TARGET_TEACHESSET = "needsLabTarget.TeachesSet";
    private static final String TARGET_COURSES = "needsLabTarget.Courses";
    private static final String TARGET_NEEDSLABSET = "needsLabTarget.NeedsLabSet";
    private static final String TARGET_TUTOR = TARGET_TUTORS + ".Tutor";
    private static final String TARGET_TEACHES = TARGET_TEACHESSET + ".Teaches";
    private static final String TARGET_COURSE = TARGET_COURSES + ".Course";
    private static final String TARGET_NEEDSLAB = TARGET_NEEDSLABSET + ".NeedsLab";
    // ATTRIBUTES
    private static final String TARGET_TUTOR_TUTOR = "tutor";
    private static final String TARGET_COURSE_COURSE = "course";
    private static final String TARGET_NEEDSLAB_LAB = "lab";

    public void test1() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.needsLabTGD).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        solveAndPrintResults();
        checkTranslatedInstance(mappingTask);
    }

    public void test2() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.needsLabReorderedTGD).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        solveAndPrintResults();
        checkTranslatedInstance(mappingTask);
    }

    private void checkTranslatedInstance(MappingTask mappingTask) {
        INode instanceNode = finalResult.getInstances().get(0);
        checkTutorSet(instanceNode);
        checkTeachSet(instanceNode);
        checkCourses(instanceNode);
        checkNeedsLabSet(instanceNode);
    }

    private void checkTutorSet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_TUTOR, instanceNode);
        assertEquals(1, nodes.size());
        assertTrue(checkTutor("Yves", nodes));
    }

    private boolean checkTutor(Object expectedTutor, List<INode> nodes) {
        for (INode node : nodes) {
            Object tutor = node.getChild(TARGET_TUTOR_TUTOR).getChild(0).getValue();
            if (tutor.toString().equals(expectedTutor.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkTeachSet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_TEACHES, instanceNode);
        assertEquals(1, nodes.size());
    }

    private void checkCourses(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_COURSE, instanceNode);
        assertEquals(1, nodes.size());
        assertTrue(checkCourse("Java", nodes));
    }

    private boolean checkCourse(Object expectedCourse, List<INode> nodes) {
        for (INode node : nodes) {
            Object course = node.getChild(TARGET_COURSE_COURSE).getChild(0).getValue();
            if (course.toString().equals(expectedCourse.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkNeedsLabSet(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_NEEDSLAB, instanceNode);
        assertEquals(1, nodes.size());
        assertTrue(checkNeedsLab(NullValueFactory.getNullValue(), nodes));
    }

    private boolean checkNeedsLab(Object expectedLab, List<INode> nodes) {
        for (INode node : nodes) {
            Object lab = node.getChild(TARGET_NEEDSLAB_LAB).getChild(0).getValue();
            if (lab.toString().equals(expectedLab.toString())) {
                return true;
            }
        }
        return false;
    }
}
