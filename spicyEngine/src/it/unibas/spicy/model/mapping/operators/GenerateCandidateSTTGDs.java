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
 
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateCandidateSTTGDs {

    private static Log logger = LogFactory.getLog(GenerateCandidateSTTGDs.class);

    private NormalizeSTTGDs normalizer = new NormalizeSTTGDs();

    public List<FORule> generateCandidateTGDs(MappingTask mappingTask) {
        List<FORule> candidateTgds = new ArrayList<FORule>();
        for (SimpleConjunctiveQuery sourceView : mappingTask.getSourceProxy().getMappingData().getViews()) {
            for (SimpleConjunctiveQuery targetView : mappingTask.getTargetProxy().getMappingData().getViews()) {
                FORule tgd = new FORule(sourceView, targetView);
                candidateTgds.add(tgd);
            }
        }
        addCoveredCorrespondences(candidateTgds, mappingTask);
        if (logger.isDebugEnabled()) logger.debug("Candidate mappings before pruning: " + candidateTgds.size());
        removeEmptyTGDs(candidateTgds);
        if (logger.isDebugEnabled()) logger.debug("Candidate mappings after pruning: " + candidateTgds.size());
        List<FORule> normalizedTgds = normalizer.normalizeSTTGDs(candidateTgds);
        removeEmptyTGDs(normalizedTgds);
        Collections.sort(normalizedTgds);
        if (logger.isDebugEnabled()) logger.debug("Candidate mappings after normalization: " + normalizedTgds.size());
        return normalizedTgds;
    }

    private void addCoveredCorrespondences(List<FORule> tgds, MappingTask mappingTask) {
        List<VariableCorrespondence> correspondences = mappingTask.getMappingData().getCorrespondences();
        for (FORule tgd : tgds) {
            for (VariableCorrespondence correspondence : correspondences) {
                if (checkCoverage(tgd, correspondence)) {
                    tgd.addCoveredCorrespondence(correspondence);
                }
            }
        }
    }

    public boolean checkCoverage(FORule tgd, VariableCorrespondence correspondence) {
        if (logger.isDebugEnabled()) logger.trace("======================= Checking coverage ================\n" + tgd + "\n================================= Correspondence to cover: " + correspondence);
        boolean matchesInTarget = checkMatchInTarget(correspondence.getTargetPath(), tgd.getTargetView());
        if (correspondence.isConstant()) {
            return matchesInTarget;
        } else {
            boolean matchesInSource = checkMatchesInSource(correspondence.getSourcePaths(), tgd.getSimpleSourceView());
            return matchesInSource && matchesInTarget;
        }
    }

    private boolean checkMatchesInSource(List<VariablePathExpression> pathExpressions, SimpleConjunctiveQuery view) {
        List<SetAlias> sourceVariables = findVariablesInPaths(pathExpressions);
        for (SetAlias sourceVariable : sourceVariables) {
            if (!view.getGenerators().contains(sourceVariable)) {
                return false;
            }
        }
        return true;
    }

    public static List<SetAlias> findVariablesInPaths(List<VariablePathExpression> pathExpressions) {
        Map<SetAlias, SetAlias> map = new HashMap<SetAlias, SetAlias>();
        for (VariablePathExpression pathExpression : pathExpressions) {
            SetAlias pathVariable = pathExpression.getStartingVariable();
            map.put(pathVariable, pathVariable);
        }
        List<SetAlias> result = new ArrayList<SetAlias>(map.values());
        Collections.sort(result);
        return result;
    }

    private boolean checkMatchInTarget(VariablePathExpression pathExpression, SimpleConjunctiveQuery view) {
        return view.getGenerators().contains(pathExpression.getStartingVariable());
    }

    private void removeEmptyTGDs(List<FORule> tgds) {
        for (Iterator<FORule> iterator = tgds.iterator(); iterator.hasNext();) {
            FORule tgd = iterator.next();
            if (tgd.getCoveredCorrespondences().isEmpty()) {
                if (logger.isDebugEnabled()) logger.trace("\n===Removing empty mapping:\n" + tgd);
                iterator.remove();
            }
        }
    }

}
