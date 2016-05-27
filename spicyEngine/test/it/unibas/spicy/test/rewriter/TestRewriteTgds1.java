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
 
package it.unibas.spicy.test.rewriter;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.test.References;
import java.io.File;
import junit.framework.TestCase;

public class TestRewriteTgds1 extends TestCase {
    protected MappingTask mappingTask;
    protected DAOMappingTask daoMappingTask = new DAOMappingTask();

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.rewriter1).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
    }

    public void testRewriting() throws Exception {
        mappingTask.getConfig().setRewriteSubsumptions(true);
        mappingTask.getConfig().setRewriteCoverages(true);
        mappingTask.getConfig().setRewriteSelfJoins(true);
        mappingTask.getConfig().setRewriteOnlyProperHomomorphisms(false);
        
        mappingTask.getConfig().setRewriteOverlaps(true);
        
        mappingTask.getConfig().setUseLocalSkolem(true);
        
        for (FORule stTgd : mappingTask.getMappingData().getSTTgds()) {
            System.out.println(stTgd.toSaveString("", mappingTask));
        }
        System.out.println("------------------------");
        for (FORule stTgd : mappingTask.getMappingData().getRewrittenRules()) {
            System.out.println(stTgd.toSaveString("", mappingTask));
        }
    }
}

