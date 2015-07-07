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

import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.utility.FindCorrespondencesBasedOnLabels;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateCorrespondences extends TestCase {

    private static Log logger = LogFactory.getLog(GenerateCorrespondences.class);
    private final String DIRECTORY_PATH_BASE = "D:\\Sviluppo\\unibas\\leipzig\\mergedSchemaGenerator\\misc\\resources\\webDirectory\\";

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testFindCorrespondences() {
        String inputFile = DIRECTORY_PATH_BASE + "webGoogleLebensmittel-mappingTask-noLines.xml";
        String outputFile = DIRECTORY_PATH_BASE + "webGoogleLebensmittel-mappingTask.xml";
        try {
            new FindCorrespondencesBasedOnLabels().findCorrespondences(inputFile, outputFile);
        } catch (DAOException ex) {
            logger.error(ex);
            ex.printStackTrace();
        }
    }
}
