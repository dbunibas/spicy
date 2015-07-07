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
 
package it.unibas.spicy.test.query.sql.egd.datacleaning;

import it.unibas.spicy.parser.operators.IParseMappingTask;
import it.unibas.spicy.parser.operators.ParseMappingTask;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.query.SQLQueryTest;
import java.io.File;
import java.io.InputStreamReader;

public class SQLQueryDataCleaningEmployees extends SQLQueryTest {

    protected void setUp() throws Exception {
        IParseMappingTask generator = new ParseMappingTask();
        String mappingTaskFile = new File(this.getClass().getResource(References.dataCleaningEmployee).toURI()).getAbsolutePath();
        mappingTask = generator.generateMappingTask(mappingTaskFile);
        mappingTask.getConfig().setRewriteEGDs(true);
        initFixture();
    }

    protected void initFileReferences() {
        sourceSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.dataCleaningEmployeeSourceSQLScriptSchema));
        sourceInstanceSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.dataCleaningEmployeeSourceSQLScriptInstances));
        targetSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.dataCleaningEmployeeTargetSQLScriptSchema));
        expectedInstanceFile = this.getClass().getResource(References.dataCleaningEmployeeExpectedInstance).getPath();
    }
}
