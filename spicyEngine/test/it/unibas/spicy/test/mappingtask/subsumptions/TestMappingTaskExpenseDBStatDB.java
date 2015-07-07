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

public class TestMappingTaskExpenseDBStatDB extends MappingTaskTest {

    // STATDB SET PATH
    private static final String STATDB = "statDB";
    private static final String STATDB_ORGANIZATIONS = STATDB + ".cityStat.organizations";
    private static final String STATDB_FUNDINGS = STATDB_ORGANIZATIONS + ".organization.fundings";
    private static final String STATDB_FINANCIALS = STATDB + ".cityStat.financials";
    // TUPLE NODES PATHS
    private static final String STATDB_CITYSTAT = STATDB + ".cityStat";
    private static final String STATDB_ORGANIZATION = STATDB_ORGANIZATIONS + ".organization";
    private static final String STATDB_FUND = STATDB_FUNDINGS + ".fund";
    private static final String STATDB_FINANCIAL = STATDB_FINANCIALS + ".financial";
    // ATTRIBUTES
    private static final String STATDB_CITY = "city";
    private static final String STATDB_COMPANYNAME = "companyName";
    private static final String STATDB_COMPANYID = "companyId";
    private static final String STATDB_PRINCIPALINVESTIGATOR = "principalInvestigator";
    private static final String STATDB_AMOUNT = "amount";
    private static final String STATDB_PROJECT = "project";
    private static final String STATDB_YEAR = "year";
    private static final String STATDB_FINANCIALID = "financialId";
    private static final String STATDB_FINANCIALIDREF = "financialIdRef";
    // ATTRIBUTES PATHS
    private static final String STATDB_COMPANYNAME_PATH = STATDB_ORGANIZATION + "." + STATDB_COMPANYNAME;

    // FILTERS
    private String[][] microsoftFilter = {
        {STATDB_COMPANYNAME_PATH, "Microsoft"}
    };
    private String[][] ibmFilter = {
        {STATDB_COMPANYNAME_PATH, "IBM"}
    };
    private String[][] appleFilter = {
        {STATDB_COMPANYNAME_PATH, "Apple"}
    };


    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.expenseDBStatDB).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        initFixture();
    }

    public void testMappingTask() {
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode translatedInstance = finalResult.getInstances().get(0);
        checkCityStats(translatedInstance);
        checkOrganizations(translatedInstance);
        checkFundings(translatedInstance);
        checkFinancials(translatedInstance);
    }

    private void checkCityStats(INode instanceNode) {
        List<INode> cityStatNodes = new FindNode().findNodesInInstance(STATDB_CITYSTAT, instanceNode);
        assertEquals(1, cityStatNodes.size());
        assertTrue(checkCityStat(NullValueFactory.getNullValue(), cityStatNodes));
    }

    private boolean checkCityStat(Object expectedCity, List<INode> nodes) {
        for (INode node : nodes) {
            Object city = node.getChild(STATDB_CITY).getChild(0).getValue();
            if (city.toString().equals(expectedCity.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkOrganizations(INode instanceNode) {
        List<INode> organizationNodes = new FindNode().findNodesInInstance(STATDB_ORGANIZATION, instanceNode);
        assertEquals(3, organizationNodes.size());
        assertTrue(checkOrganization(NullValueFactory.getNullValue(), "Microsoft", organizationNodes));
        assertTrue(checkOrganization(NullValueFactory.getNullValue(), "IBM", organizationNodes));
        assertTrue(checkOrganization(NullValueFactory.getNullValue(), "Apple", organizationNodes));
    }

    private boolean checkOrganization(Object expectedCompanyId, Object expectedCompanyName, List<INode> nodes) {
        for (INode node : nodes) {
            Object companyId = node.getChild(STATDB_COMPANYID).getChild(0).getValue();
            Object companyName = node.getChild(STATDB_COMPANYNAME).getChild(0).getValue();
            if (companyId.toString().equals(expectedCompanyId.toString()) &&
                    companyName.toString().equals(expectedCompanyName.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkFundings(INode instanceNode) {
        List<INode> microsoftFunds = Utility.getNodesWithFilters(instanceNode, STATDB_FUND, microsoftFilter);
        assertEquals(2, microsoftFunds.size());
        // TODO: CHECK VALORI INVENTATI...
        assertTrue(checkFund("Peter Parker", microsoftFunds));
        assertTrue(checkFund("Bruce Banner", microsoftFunds));
        assertEquals(2, Utility.getDifferentGeneratedValues(STATDB_FINANCIALIDREF, microsoftFunds));

        List<INode> ibmFunds = Utility.getNodesWithFilters(instanceNode, STATDB_FUND, ibmFilter);
        assertEquals(2, ibmFunds.size());
        assertTrue(checkFund("Frank Castle", ibmFunds));
        assertTrue(checkFund("Tony Stark", ibmFunds));
        assertEquals(2, Utility.getDifferentGeneratedValues(STATDB_FINANCIALIDREF, microsoftFunds));

        List<INode> appleFunds = Utility.getNodesWithFilters(instanceNode, STATDB_FUND, appleFilter);
        assertEquals(0, appleFunds.size());
    }

    private boolean checkFund(Object expectedPrincipalInvestigator, List<INode> nodes) {
        for (INode node : nodes) {
            Object principalInvestigator = node.getChild(STATDB_PRINCIPALINVESTIGATOR).getChild(0).getValue();
            if (principalInvestigator.toString().equals(expectedPrincipalInvestigator.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkFinancials(INode instanceNode) {
        List<INode> financialsNodes = new FindNode().findNodesInInstance(STATDB_FINANCIAL, instanceNode);
        assertEquals(4, financialsNodes.size());
        assertTrue(checkFinancial(1000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), financialsNodes));
        assertTrue(checkFinancial(5000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), financialsNodes));
        assertTrue(checkFinancial(NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), financialsNodes));
        assertTrue(checkFinancial(5000.0, NullValueFactory.getNullValue(), NullValueFactory.getNullValue(), financialsNodes));
        
        assertEquals(4, Utility.getDifferentGeneratedValues(STATDB_FINANCIALID, financialsNodes));
    }

    private boolean checkFinancial(Object expectedAmount, Object expectedProject, Object expectedYear, List<INode> nodes) {
        for (INode node : nodes) {
            Object amount = node.getChild(STATDB_AMOUNT).getChild(0).getValue();
            Object project = node.getChild(STATDB_PROJECT).getChild(0).getValue();
            Object year = node.getChild(STATDB_YEAR).getChild(0).getValue();
            if (amount.toString().equals(expectedAmount.toString()) &&
                    project.toString().equals(expectedProject.toString()) &&
                    year.toString().equals(expectedYear.toString())) {
                return true;
            }
        }
        return false;
    }
}