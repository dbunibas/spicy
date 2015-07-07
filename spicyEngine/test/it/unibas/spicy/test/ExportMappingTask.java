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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.DAOMappingTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportMappingTask extends TestCase {

    private static Log logger = LogFactory.getLog(ExportMappingTask.class);

    private final String DIRECTORY_PATH_BASE = "c:\\tmp\\mockExport";

    public ExportMappingTask(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void saveAndReload(MappingTask mappingTask) {
        String sourceName = mappingTask.getSourceProxy().getIntermediateSchema().getLabel();
        IDataSourceProxy source = mappingTask.getSourceProxy();
        source.setType(SpicyEngineConstants.TYPE_XML);
        source.addAnnotation(SpicyEngineConstants.XML_SCHEMA_FILE, DIRECTORY_PATH_BASE + File.separator + sourceName + ".xsd");
        List<String> sourceInstances = new ArrayList<String>();
        sourceInstances.add(DIRECTORY_PATH_BASE + File.separator + sourceName + "-instance0.xml");
        source.addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, sourceInstances);
        String targetName = mappingTask.getTargetProxy().getIntermediateSchema().getLabel();
        IDataSourceProxy target = mappingTask.getTargetProxy();
        target.setType(SpicyEngineConstants.TYPE_XML);
        target.addAnnotation(SpicyEngineConstants.XML_SCHEMA_FILE, DIRECTORY_PATH_BASE + File.separator + targetName + ".xsd");
        List<String> targetInstances = new ArrayList<String>();
        target.addAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST, targetInstances);
        try {
            DAOMappingTask daoMappingTask = new DAOMappingTask();
            daoMappingTask.saveMappingTask(mappingTask, DIRECTORY_PATH_BASE + File.separator + sourceName + "-" + targetName + ".xml");
            
            MappingTask nuovo = daoMappingTask.loadMappingTask(DIRECTORY_PATH_BASE + File.separator + sourceName + "-" + targetName + ".xml");
            System.out.println(nuovo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
