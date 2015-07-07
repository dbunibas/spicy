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
 
package it.unibas.spicy.test.attributematch;

import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.TestFixture;
import java.io.File;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestMatcher extends TestFixture {

    private Log logger = LogFactory.getLog(this.getClass());

//    public void testStatDBExpenseDB() throws Exception {
//        String mappingTaskFile = new File(this.getClass().getResource(References.statDBexpenseDB).toURI()).getAbsolutePath();
//        if (logger.isDebugEnabled()) logger.debug("Loading mapping task...");
//        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
//        DataSource source = mappingTask.getSource();
//        DataSource target = mappingTask.getTarget();
//        AttributeCorrespondences result = attributeMatcher.findMatches(source, target);
//        printLog(result);
//    }
//
    public void testLivesIn() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.livesIn).toURI()).getAbsolutePath();
        if (logger.isDebugEnabled()) logger.debug("Loading mapping task...");
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
        DataSource source = mappingTask.getSourceProxy().getDataSource();
        DataSource target = mappingTask.getTargetProxy().getDataSource();
        AttributeCorrespondences result = attributeMatcher.findMatches(source, target);
        printLog(result);
    }


    private void printLog(AttributeCorrespondences correspondences) {
        DataSource source = correspondences.getSource();
        DataSource target = correspondences.getTarget();
        if (logger.isDebugEnabled()) logger.debug(printCorrespondenceList(correspondences.getCorrespondences()));
    }

    private String printCorrespondenceList(List<ValueCorrespondence> correspondences) {
        String result = "CORRESPONDENCES:\n";
        for (ValueCorrespondence correspondence : correspondences) {
            result += correspondence + "\n";
        }
        return result;

    }
}
