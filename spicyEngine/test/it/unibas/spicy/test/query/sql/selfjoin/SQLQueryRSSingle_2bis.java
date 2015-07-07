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
 
package it.unibas.spicy.test.query.sql.selfjoin;

import it.unibas.spicy.parser.operators.IParseMappingTask;
import it.unibas.spicy.parser.operators.ParseMappingTask;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.query.SQLQueryTest;
import java.io.File;
import java.io.InputStreamReader;

public class SQLQueryRSSingle_2bis extends SQLQueryTest {

    protected IParseMappingTask generator = new ParseMappingTask();

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.rsSingle).toURI()).getAbsolutePath();
        mappingTask = generator.generateMappingTask(mappingTaskFile);
        initFixture();
    }

    protected void initFileReferences() {
        sourceSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.rsSingleSourceSQLScriptSchema));
        sourceInstanceSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.rsSingleSourceSQLScriptInstances_2bis));
        targetSQLScriptReader = new InputStreamReader(this.getClass().getResourceAsStream(References.rsSingleTargetSQLScriptSchema));
        expectedInstanceFile = this.getClass().getResource(References.rsSingleExpectedInstance_2bis).getPath();
    }
}
