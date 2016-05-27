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
import it.unibas.spicy.model.mapping.operators.NormalizeSTTGDs;
import it.unibas.spicy.model.mapping.rewriting.operators.RewriteTgds;
import it.unibas.spicy.persistence.DAOMappingTask;
import it.unibas.spicy.test.References;
import java.io.File;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

public class TestRewriteTgds2 extends TestCase {
    
    protected MappingTask mappingTask;
    protected DAOMappingTask daoMappingTask = new DAOMappingTask();
    protected NormalizeSTTGDs normalizer = new NormalizeSTTGDs();
    protected RewriteTgds rewriter = new RewriteTgds();

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.rewriter2).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
    }

    public void testRewriting() throws Exception {
        mappingTask.getConfig().setRewriteSubsumptions(true);
        mappingTask.getConfig().setRewriteCoverages(true);
        mappingTask.getConfig().setRewriteSelfJoins(false);
        mappingTask.getConfig().setRewriteOnlyProperHomomorphisms(false);
        
        mappingTask.getConfig().setRewriteOverlaps(true);
        
        mappingTask.getConfig().setUseLocalSkolem(true);
        
        List<FORule> stTgds = mappingTask.getMappingData().getSTTgds();
//        List<FORule> normalizedSTTgds = normalizeTgds(stTgds);
        List<FORule> rewrittenSTTgds = rewriteTgds(stTgds, mappingTask);
        
        for (FORule stTgd : rewrittenSTTgds) {
            System.out.println(stTgd.toSaveString("", mappingTask));
        }
    }

//    private List<FORule> normalizeTgds(List<FORule> tgds) {
//        List<FORule> normalizedTgds = normalizer.normalizeSTTGDs(tgds);
//        Collections.sort(normalizedTgds);    
//        return normalizedTgds;
//    }
    
    private List<FORule> rewriteTgds(List<FORule> tgds, MappingTask mappingTask) {
        List<FORule> normalizedTgds = rewriter.rewriteTgds(tgds, mappingTask);
        Collections.sort(normalizedTgds);    
        return normalizedTgds;
    }
}

