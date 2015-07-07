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
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.test.References;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;
import java.util.List;

public class TestMappingTaskSelfJoins extends MappingTaskTest {

    // Target SET PATH
    private static final String TARGET_GENE_SET = "Target.GeneSet";
    private static final String TARGET_SYNONYM_SET = "Target.SynonymSet";
    // Target TUPLE PATH
    private static final String TARGET_GENE = TARGET_GENE_SET + ".Gene";
    private static final String TARGET_SYNONYM = TARGET_SYNONYM_SET + ".Synonym";
    // ATTRIBUTES
    private static final String TARGET_NAME = "Name";
    private static final String TARGET_PROTEIN = "Protein";
    private static final String TARGET_GENE_WID = "GeneWID";

    protected void setUp() throws Exception {
        String mappingTaskFile = new File(this.getClass().getResource(References.stBenchmarkSelfJoins).toURI()).getAbsolutePath();
        mappingTask = daoMappingTask.loadMappingTask(mappingTaskFile);
    }

    public void testMappingTask() {
        solveAndPrintResults();
        checkTranslatedInstance();
    }

    private void checkTranslatedInstance() {
        INode instanceNode = finalResult.getInstances().get(0);
        checkGenes(instanceNode);
        checkSynonyms(instanceNode);
    }

    private void checkGenes(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_GENE, instanceNode);
        assertEquals(4, nodes.size());
        assertTrue(checkGene("GF14A", "14-3-3-like protein A", nodes));
        assertTrue(checkGene("hspX", "14 kDa antigen", nodes));
        assertTrue(checkGene("ACS9", "1-aminocyclopropane-1-carboxylate synthase 9", nodes));
        assertTrue(checkGene("HLA-A", "HLA class I histocompatibility antigen, A-26 alpha chain precursor", nodes));
    }

    private boolean checkGene(Object expectedName, Object expectedProtein, List<INode> nodes) {
        for (INode node : nodes) {
            Object name = node.getChild(TARGET_NAME).getChild(0).getValue();
            Object protein = node.getChild(TARGET_PROTEIN).getChild(0).getValue();
            if (name.toString().equals(expectedName.toString()) &&
                    protein.toString().equals(expectedProtein.toString())) {
                return true;
            }
        }
        return false;
    }

    private void checkSynonyms(INode instanceNode) {
        List<INode> nodes = new FindNode().findNodesInInstance(TARGET_SYNONYM, instanceNode);
        assertEquals(5, nodes.size());
        assertTrue(checkSynonym("Mb2057c", "hspX", nodes));
        assertTrue(checkSynonym("ETO3", "hspX", nodes));
        assertTrue(checkSynonym("At3g49700", "ACS9", nodes));
        assertTrue(checkSynonym("T16K5.50", "ACS9", nodes));
        assertTrue(checkSynonym("HLAA", "HLA-A", nodes));
    }

    private boolean checkSynonym(Object expectedName, Object expectedGeneWID, List<INode> nodes) {
        for (INode node : nodes) {
            Object name = node.getChild(TARGET_NAME).getChild(0).getValue();
            Object geneWID = node.getChild(TARGET_GENE_WID).getChild(0).getValue();
            if (name.toString().equals(expectedName.toString()) &&
                    geneWID.toString().equals(expectedGeneWID.toString())) {
                return true;
            }
        }
        return false;
    }
}

