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
import it.unibas.spicy.model.datasource.values.NullValueFactory;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.Utility;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestMappingTaskStatDBExpenseDB extends MappingTaskTest {

    // EXPENSEDB SET PATH
    private static final String EXPENSEDB_COMPANIES = "expenseDB.companies";
    private static final String EXPENSEDB_PROJECTS = "expenseDB.projects";
    private static final String EXPENSEDB_GRANTS = "expenseDB.grants";
    // TUPLE NODES PATHS
    private static final String EXPENSEDB_COMPANY = EXPENSEDB_COMPANIES + ".company";
    private static final String EXPENSEDB_PROJECT = EXPENSEDB_PROJECTS + ".project";
    private static final String EXPENSEDB_GRANT = EXPENSEDB_GRANTS + ".grant";
    // ATTRIBUTES
    private static final String EXPENSEDB_CITY = "city";
    private static final String EXPENSEDB_COMPANYNAME = "companyName";
    private static final String EXPENSEDB_PRINCIPALINVESTIGATOR = "principalInvestigator";
    private static final String EXPENSEDB_AMOUNT = "amount";
    private static final String EXPENSEDB_GRANTEE = "grantee";
    private static final String EXPENSEDB_SPONSORID = "sponsorId";
    private static final String EXPENSEDB_PROJECTREF = "projectRef";

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.statDBexpenseDB).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        initFixture();
    }

    protected void tearDown() throws Exception {
    }

    public void testMappingTask() {
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode instanceNode = finalResult.getInstances().get(0);
        checkCompanies(instanceNode);
        checkGrants(instanceNode);
        checkProjects(instanceNode);
    }

    private void checkCompanies(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(EXPENSEDB_COMPANY, instanceNode);
        assertEquals(2, nodes.size());
        assertTrue(checkCompany("Microsoft", NullValueFactory.getNullValue(), nodes));
        assertTrue(checkCompany("Boing", NullValueFactory.getNullValue(), nodes));
    }

    private boolean checkCompany(Object expectedCompanyName, Object expectedCity, List<INode> nodes) {
        for (INode node : nodes) {
            Object companyName = node.getChild(EXPENSEDB_COMPANYNAME).getChild(0).getValue();
            Object city = node.getChild(EXPENSEDB_CITY).getChild(0).getValue();
            if (companyName.toString().equals(expectedCompanyName.toString()) &&
                    city.toString().equals(expectedCity.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkGrants(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(EXPENSEDB_GRANT, instanceNode);
        assertEquals(3, nodes.size());
        assertTrue(checkGrant("Stan Lee", 100000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), nodes));
        assertTrue(checkGrant("Steve Ditko", 100000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), nodes));
        assertTrue(checkGrant(NullValueFactory.getNullValue(), 20000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), nodes));

        assertEquals(3, Utility.getDifferentGeneratedValues(EXPENSEDB_GRANTEE, nodes));
    }

    private boolean checkGrant(Object expectedPI, Object expectedAmount, Object expectedSponsorId,
            Object expectedProjectRef, List<INode> nodes) {
        for (INode node : nodes) {
            Object principalInvestigator = node.getChild(EXPENSEDB_PRINCIPALINVESTIGATOR).getChild(0).getValue();
            Object amount = node.getChild(EXPENSEDB_AMOUNT).getChild(0).getValue();
            Object sponsorId = node.getChild(EXPENSEDB_SPONSORID).getChild(0).getValue();
            Object projectRef = node.getChild(EXPENSEDB_PROJECTREF).getChild(0).getValue();
            if (principalInvestigator.toString().equals(expectedPI.toString()) &&
                    amount.toString().equals(expectedAmount.toString()) &&
                    sponsorId.toString().equals(expectedSponsorId.toString()) &&
                    projectRef.toString().equals(expectedProjectRef.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkProjects(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(EXPENSEDB_PROJECT, instanceNode);
        assertEquals(0, nodes.size());
    }
}