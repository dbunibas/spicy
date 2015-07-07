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
 
package it.unibas.spicy.attributematch;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeCorrespondences {
    
    private DataSource source;
    private DataSource target;
    private Map<PathExpression, List<ValueCorrespondence>> correspondenceMap = new HashMap<PathExpression, List<ValueCorrespondence>>();
    
    public AttributeCorrespondences(DataSource source, DataSource target) {
        this.source = source;
        this.target = target;
    }

    public void addCorrespondence(ValueCorrespondence correspondence) {
        if (correspondence.getSourcePaths() == null || correspondence.getSourcePaths().size() > 1) {
            return;
        }
        this.addCorrespondence(correspondence.getSourcePaths().get(0), correspondence.getTargetPath(), correspondence.getConfidence());
    }
    
    public void addCorrespondence(PathExpression sourcePath, PathExpression targetPath, double confidence) {
        List<ValueCorrespondence> correspondencesForTarget = correspondenceMap.get(targetPath);
        if (correspondencesForTarget == null) {
            correspondencesForTarget = new ArrayList<ValueCorrespondence>();
            correspondenceMap.put(targetPath, correspondencesForTarget);
        }
        ValueCorrespondence correspondence = new ValueCorrespondence(sourcePath, targetPath, confidence);
        correspondencesForTarget.add(correspondence);
    }
    
    @SuppressWarnings("unchecked")
    public List<ValueCorrespondence> getCorrespondences(PathExpression targetPath) {
        List<ValueCorrespondence> correspondencesForTarget = correspondenceMap.get(targetPath);
        if (correspondencesForTarget == null) {
            return Collections.EMPTY_LIST;
        }
        Collections.sort(correspondencesForTarget);
        return correspondencesForTarget;
    }
    
    public List<ValueCorrespondence> getCorrespondences() {
        List<ValueCorrespondence> result = new ArrayList<ValueCorrespondence>();
        for (PathExpression targetPath : correspondenceMap.keySet()) {
            result.addAll(correspondenceMap.get(targetPath));
        }
        return result;
    }
    
    public DataSource getSource() {
        return source;
    }

    public DataSource getTarget() {
        return target;
    }

    public Map<PathExpression, List<ValueCorrespondence>> getCorrespondenceMap() {
        return correspondenceMap;
    }
    
    
}
