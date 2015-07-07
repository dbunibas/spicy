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

import it.unibas.spicy.Application;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestApplication extends TestCase {

    private Log logger = LogFactory.getLog(this.getClass());

    public void testFindMappings() {
        Application application = Application.getInstance();
        logger.debug(application.toString());
        Application.reset();
    }

    public void testFindMappingsCustom() {
        Properties properties = new Properties();
        properties.setProperty("MULTIPLYING_FACTOR_FOR_ELEMENTS_IN_CIRCUIT", "-1.0");
        properties.setProperty("MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_IN_CIRCUIT", "-1.0");
        Application.reset(properties);
        logger.debug(Application.getInstance().toString());
    }
}
