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
 
package it.unibas.spicy.test.mappingtask.subsumptions;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.Utility;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestMappingTaskDeptOtherDept extends MappingTaskTest {

    // DEPTS SET PATH
    private static final String DEPTS_DEPTS = "depts";
    private static final String DEPTS_EMPS = DEPTS_DEPTS + ".emp.emps";
    private static final String DEPTS_DEPENDENTS = DEPTS_EMPS + ".emp.dependents";
    private static final String DEPTS_PROJECTS = DEPTS_EMPS + ".emp.projects";
    // ATTRIBUTES

    // DEPTS SET PATH
    private static final String TDEPTS_TDEPTS = "tdepts";
    private static final String TDEPTS_PROJECTS = TDEPTS_TDEPTS + ".tdept.projects";
    private static final String TDEPTS_EMPS = TDEPTS_TDEPTS + ".tdept.emps";
    private static final String TDEPTS_DEPENDENTS = TDEPTS_EMPS + ".emp.dependents";
    // TUPLE NODES PATHS
    private static final String TDEPTS_TDEPT = TDEPTS_TDEPTS + ".tdept";
    private static final String TDEPTS_PROJECT = TDEPTS_PROJECTS + ".project";
    private static final String TDEPTS_EMP = TDEPTS_EMPS + ".emp";
    private static final String TDEPTS_DEPENDENT = TDEPTS_DEPENDENTS + ".dependent";
    // ATTRIBUTES
    private static final String TDEPTS_TDEPT_DEPTNAME = "deptName";
    private static final String TDEPTS_TDEPT_LOCATION = "location";
    private static final String TDEPTS_TDEPT_RESEARCHSUBJECT = "researchSubject";
    private static final String TDEPTS_PROJECT_PROJECTID = "projectId";
    private static final String TDEPTS_PROJECT_PROJECTNAME = "projectName";
    private static final String TDEPTS_EMP_EMPNAME = "empName";
    private static final String TDEPTS_EMP_SALARY = "salary";
    private static final String TDEPTS_DEPENDENT_DEPENDENTNAME = "dependentName";
    private static final String TDEPTS_DEPENDENT_AGE = "age";
    // ATTRIBUTES PATHS
    private static final String TDEPTS_TDEPT_DEPTNAME_PATH = TDEPTS_TDEPT + "." + TDEPTS_TDEPT_DEPTNAME;
    private static final String TDEPTS_TDEPT_LOCATION_PATH = TDEPTS_TDEPT + "." + TDEPTS_TDEPT_LOCATION;
    private static final String TDEPTS_TDEPT_RESEARCHSUBJECT_PATH = TDEPTS_TDEPT + "." + TDEPTS_TDEPT_RESEARCHSUBJECT;
    private static final String TDEPTS_EMP_EMPNAME_PATH = TDEPTS_EMP + "." + TDEPTS_EMP_EMPNAME;
    private static final String TDEPTS_EMP_SALARY_PATH = TDEPTS_EMP + "." + TDEPTS_EMP_SALARY;
    // FILTERS
    private String[][] filtersDeptMath = {
        {TDEPTS_TDEPT_DEPTNAME_PATH, "Department of Mathematics and Computer Science"},
        {TDEPTS_TDEPT_LOCATION_PATH, "Potenza"},
        {TDEPTS_TDEPT_RESEARCHSUBJECT_PATH, "Math"}
    };
    private String[][] filtersDeptEng = {
        {TDEPTS_TDEPT_DEPTNAME_PATH, "Department of Engineering and Enviromental Physics"},
        {TDEPTS_TDEPT_LOCATION_PATH, "Matera"},
        {TDEPTS_TDEPT_RESEARCHSUBJECT_PATH, "Engineering"}
    };
    private String[][] filtersEmpKorchmaros = {
        {TDEPTS_EMP_EMPNAME_PATH, "Prof. Gabor Korchmaros"},
        {TDEPTS_EMP_SALARY_PATH, "4000"}
    };
    private String[][] filtersEmpMastroianni = {
        {TDEPTS_EMP_EMPNAME_PATH, "Prof. Giuseppe Mastroianni"},
        {TDEPTS_EMP_SALARY_PATH, "5000"}
    };
    private String[][] filtersEmpFiorentino = {
        {TDEPTS_EMP_EMPNAME_PATH, "Prof. Mauro Fiorentino"},
        {TDEPTS_EMP_SALARY_PATH, "6000"}
    };

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.deptOtherDept).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        initFixture();
        initFilters();
    }

    private void initFilters() {
        filtersEmpKorchmaros = Utility.addFiltersToArray(filtersDeptMath, filtersEmpKorchmaros);
        filtersEmpMastroianni = Utility.addFiltersToArray(filtersDeptMath, filtersEmpMastroianni);
        filtersEmpFiorentino = Utility.addFiltersToArray(filtersDeptEng, filtersEmpFiorentino);
    }

    public void testMappingTask() {
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode translatedInstance = finalResult.getInstances().get(0);
        checkTDepts(translatedInstance);
        checkProjects(translatedInstance);
        checkEmps(translatedInstance);
        checkDependents(translatedInstance);
    }

    private void checkTDepts(INode instanceNode) {
        List<INode> tdeptNodes = new FindNode().findNodesInInstance(TDEPTS_TDEPT, instanceNode);
        assertEquals(2, tdeptNodes.size());
        assertTrue(checkTDept("Department of Mathematics and Computer Science", "Potenza", "Math", tdeptNodes));
        assertTrue(checkTDept("Department of Engineering and Enviromental Physics", "Matera", "Engineering", tdeptNodes));
    }

    private boolean checkTDept(Object expectedDeptName, Object expectedLocation, Object expectedResearchSubject, List<INode> tdeptNodes) {
        for (INode deptNode : tdeptNodes) {
            Object deptName = deptNode.getChild(TDEPTS_TDEPT_DEPTNAME).getChild(0).getValue();
            Object location = deptNode.getChild(TDEPTS_TDEPT_LOCATION).getChild(0).getValue();
            Object researchSubject = deptNode.getChild(TDEPTS_TDEPT_RESEARCHSUBJECT).getChild(0).getValue();
            if (deptName.toString().equals(expectedDeptName.toString()) &&
                    location.toString().equals(expectedLocation.toString()) &&
                    researchSubject.toString().equals(expectedResearchSubject.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkProjects(INode instanceNode) {
        List<INode> projectsMathNodes = Utility.getNodesWithFilters(instanceNode, TDEPTS_PROJECT, filtersDeptMath);
        assertEquals(4, projectsMathNodes.size());
        assertTrue(checkProject("Rette", projectsMathNodes));
        assertTrue(checkProject("Crittografia", projectsMathNodes));
        assertTrue(checkProject("Calcolo Numerico", projectsMathNodes));
        assertTrue(checkProject("Equazioni Integrali", projectsMathNodes));
        List<INode> projectsEngNodes = Utility.getNodesWithFilters(instanceNode, TDEPTS_PROJECT, filtersDeptEng);
        assertEquals(1, projectsEngNodes.size());
        assertTrue(checkProject("Costruzioni Idrauliche", projectsEngNodes));
    }

    private boolean checkProject(Object expectedProjectName, List<INode> projectsNodes) {
        for (INode projectNode : projectsNodes) {
            Object projectName = projectNode.getChild(TDEPTS_PROJECT_PROJECTNAME).getChild(0).getValue();
            if (projectName.toString().equals(expectedProjectName.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkEmps(INode instanceNode) {
        List<INode> empsMath = Utility.getNodesWithFilters(instanceNode, TDEPTS_EMP, filtersDeptMath);
        assertEquals(2, empsMath.size());
        assertTrue(checkEmp("Prof. Gabor Korchmaros", 4000, empsMath));
        assertTrue(checkEmp("Prof. Giuseppe Mastroianni", 5000, empsMath));
        List<INode> empsEng = Utility.getNodesWithFilters(instanceNode, TDEPTS_EMP, filtersDeptEng);
        assertEquals(1, empsEng.size());
        assertTrue(checkEmp("Prof. Mauro Fiorentino", 6000, empsEng));
    }

    private boolean checkEmp(Object expectedEmpName, Object expectedSalary, List<INode> empsNodes) {
        for (INode empNode : empsNodes) {
            Object empName = empNode.getChild(TDEPTS_EMP_EMPNAME).getChild(0).getValue();
            Object salary = empNode.getChild(TDEPTS_EMP_SALARY).getChild(0).getValue();
            if (empName.toString().equals(expectedEmpName.toString()) &&
                    salary.toString().equals(expectedSalary.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkDependents(INode instanceNode) {
        List<INode> korchmarosDependents = Utility.getNodesWithFilters(instanceNode, TDEPTS_DEPENDENT, filtersEmpKorchmaros);
        assertEquals(2, korchmarosDependents.size());
        assertTrue(checkDependent("Sonnino", 40, korchmarosDependents));
        assertTrue(checkDependent("Siciliano", 38, korchmarosDependents));
        List<INode> mastroianniDependents = Utility.getNodesWithFilters(instanceNode, TDEPTS_DEPENDENT, filtersEmpMastroianni);
        assertEquals(2, mastroianniDependents.size());
        assertTrue(checkDependent("Russo", 40, mastroianniDependents));
        assertTrue(checkDependent("Occorsio", 41, mastroianniDependents));
        List<INode> fiorentinoDependents = Utility.getNodesWithFilters(instanceNode, TDEPTS_DEPENDENT, filtersEmpFiorentino);
        assertEquals(1, fiorentinoDependents.size());
        assertTrue(checkDependent("Oliveto", 40, fiorentinoDependents));
    }

    private boolean checkDependent(Object expectedDependentName, Object expectedAge, List<INode> dependentsNodes) {
        for (INode dependentNode : dependentsNodes) {
            Object dependentName = dependentNode.getChild(TDEPTS_DEPENDENT_DEPENDENTNAME).getChild(0).getValue();
            Object location = dependentNode.getChild(TDEPTS_DEPENDENT_AGE).getChild(0).getValue();
            if (dependentName.toString().equals(expectedDependentName.toString()) &&
                    location.toString().equals(expectedAge.toString())) {
                return true;
            }
        }
        return false;
    }
}
