/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com
Donatello Santoro - donatello.santoro@gmail.com

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
package it.unibas.spicy.test.mappingtask.etl;

import it.unibas.spicy.test.References;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestETLCreditCard extends MappingTaskTest {
    
    public void testMatchCustomers() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.etlCreditCardMatchCustomers).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        List<String> instanceFiles = new ArrayList<String>();
        String mappingTaskFolder = mappingTask.getFileName().substring(0, mappingTask.getFileName().lastIndexOf(File.separator));
        instanceFiles.add(mappingTaskFolder + File.separator + "creditCardMatchCustomers-gen-instance0.");
        mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
        solveAndPrintResults();
        checkExpectedCSVInstances();
    }
    
    public void testLoadRates() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.etlCreditCardLoadRates).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        List<String> instanceFiles = new ArrayList<String>();
        String mappingTaskFolder = mappingTask.getFileName().substring(0, mappingTask.getFileName().lastIndexOf(File.separator));
        instanceFiles.add(mappingTaskFolder + File.separator + "creditCardLoadRates-gen-instance1.");
        mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
        solveAndPrintResults();
        checkExpectedCSVInstances();
    }

    public void testRebaseCurrency() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.etlCreditCardRebaseCurrency).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        List<String> instanceFiles = new ArrayList<String>();
        String mappingTaskFolder = mappingTask.getFileName().substring(0, mappingTask.getFileName().lastIndexOf(File.separator));
        instanceFiles.add(mappingTaskFolder + File.separator + "creditCardRebaseCurrency-gen-instance2.");
        mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
        solveAndPrintResults();
        checkExpectedCSVInstances();
    }

    public void testPartition() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.etlCreditCardPartition).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        List<String> instanceFiles = new ArrayList<String>();
        String mappingTaskFolder = mappingTask.getFileName().substring(0, mappingTask.getFileName().lastIndexOf(File.separator));
        instanceFiles.add(mappingTaskFolder + File.separator + "creditCardPartition-gen-instance3.");
        mappingTask.getSourceProxy().addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
        solveAndPrintResults();
        checkExpectedCSVInstances();
    }
}
