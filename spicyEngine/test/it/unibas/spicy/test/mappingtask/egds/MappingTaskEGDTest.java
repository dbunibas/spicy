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
package it.unibas.spicy.test.mappingtask.egds;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDImplicants;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.FindMinimalImplicants;
import it.unibas.spicy.test.mappingtask.MappingTaskTest;
import java.io.File;

public class MappingTaskEGDTest extends MappingTaskTest {

    private FindMinimalImplicants implicantsFinder = new FindMinimalImplicants();

    public void solveAndPrintResults() {
        if (!mappingTask.getConfig().rewriteSkolemsForEGDs()) {
            mappingTask.getConfig().setRewriteEGDs(true);
        }
        mappingTask.getConfig().setUseSkolemStrings(true);
        RuntimeException ex = null;
        try {
            super.solveAndPrintResults();
        } catch (Throwable t) {
            ex = new RuntimeException(t);
        }
        if (logger.isTraceEnabled()) {
            if (mappingTask != null) {
                StringBuilder result = new StringBuilder();
                int index = mappingTask.getFileName().indexOf("-mappingTask.");
                if (index < 0) {
                    index = mappingTask.getFileName().length() - 1;
                }
                String fileName = mappingTask.getFileName().substring(mappingTask.getFileName().lastIndexOf(File.separator) + 1, index);
                result.append("###################################### ").append(fileName).append(" ########################################\n");
//            result.append(mappingTask.getConfig());
                result.append(mappingTask.getMappingData().rewritingStatsString());
                result.append("*********************** Implicants ****************************\n");
                for (FORule tgd : mappingTask.getMappingData().getRewrittenRules()) {
                    result.append("*********************** FO Rule ****************************\n");
                    result.append(tgd.getId()).append(": ");
                    result.append(tgd.toLogicalString(mappingTask)).append("\n");
                    TGDImplicants implicantsMap = implicantsFinder.findMinimalImplicants(tgd, mappingTask);
                    if (!implicantsMap.isEmpty()) {
                        result.append(implicantsMap.toLongString());
                    }
                }
                result.append("*********************** Source instance ****************************\n");
                result.append(mappingTask.getSourceProxy().toInstanceString());
                result.append("*********************** Final result ****************************\n");
                if (mappingTask.getMappingData().hasSelfJoinsInTgdConclusions()) {
                    result.append(finalResult.toIntermediateInstancesString());
                }
                result.append(finalResult.toInstanceString());
                if (logger.isTraceEnabled()) logger.trace(result.toString());
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
