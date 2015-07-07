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
 
package it.unibas.spicy.model.mapping.rewriting.egds.operators;

import it.unibas.spicy.model.mapping.operators.*;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.*;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.joingraph.JoinGroup;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.SimpleConjunctiveQuery;
import it.unibas.spicy.model.mapping.rewriting.egds.AttributeGroup;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDAttributeGroups;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariableJoinCondition;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FindAttributeGroups {

    private static Log logger = LogFactory.getLog(FindAttributeGroups.class);

    ///////////////////////////    TGD   ////////////////////////////////
    public TGDAttributeGroups findAttributeGroupsInTgd(FORule tgd, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("---------------- Finding attribute groups in tgd: " + tgd.toLogicalString(mappingTask));
        if (logger.isTraceEnabled()) logger.trace(tgd);
        List<AttributeGroup> sourceGroups = findAttributeGroupsInView(tgd.getSimpleSourceView(), mappingTask.getSourceProxy());
        List<AttributeGroup> targetGroups = findAttributeGroupsInView(tgd.getTargetView(), mappingTask.getTargetProxy());
        mergeGroups(sourceGroups, targetGroups, tgd.getCoveredCorrespondences());
        TGDAttributeGroups tgdGroups = new TGDAttributeGroups(sourceGroups, targetGroups);
        if (logger.isDebugEnabled()) logger.debug("---------------- Final result: " + tgdGroups);
        return tgdGroups;
    }

    private void mergeGroups(List<AttributeGroup> sourceGroups, List<AttributeGroup> targetGroups, List<VariableCorrespondence> correspondences) {
        for (VariableCorrespondence correspondence : correspondences) {
            if (correspondence.getSourcePaths() == null || correspondence.getSourcePaths().size() > 1) {
                throw new IllegalArgumentException("Attribute groups cannot be found in tgds with n-1 correspondences: " + correspondence);
            }
            if (logger.isDebugEnabled()) logger.debug("Merging groups for correspondence: " + correspondence);
            if (logger.isDebugEnabled()) logger.debug("Current source groups: " + SpicyEngineUtility.printCollection(sourceGroups));
            if (logger.isDebugEnabled()) logger.debug("Current target groups: " + SpicyEngineUtility.printCollection(targetGroups));
            AttributeGroup sourceGroup = findGroupForPath(correspondence.getFirstSourcePath(), sourceGroups);
            AttributeGroup targetGroup = findGroupForPath(correspondence.getTargetPath(), targetGroups);
            if (targetGroup == null) {
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("Correspondence source group: " + sourceGroup);
            if (logger.isDebugEnabled()) logger.debug("Correspondence target group: " + targetGroup);
            sourceGroup.getAttributePaths().addAll(targetGroup.getAttributePaths());
            targetGroups.remove(targetGroup);
        }
    }

    private AttributeGroup findGroupForPath(VariablePathExpression attributePath, List<AttributeGroup> attributeGroups) {
        for (AttributeGroup attributeGroup : attributeGroups) {
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, attributePath)) {
                return attributeGroup;
            }
        }
        return null;
    }

    ///////////////////////////    VIEW   ////////////////////////////////
    public List<AttributeGroup> findAttributeGroupsInView(SimpleConjunctiveQuery view, IDataSourceProxy dataSource) {
        if (logger.isDebugEnabled()) logger.debug("------------- Finding Attribute Groups for view: " + view);
        FindJoinGroups joinGroupFinder = new FindJoinGroups();
        List<JoinGroup> joinGroups = joinGroupFinder.findJoinGroups(view.getAllJoinConditions());
        List<AttributeGroup> attributeGroups = findAttributeGroupsForJoins(joinGroups);
        addSingletonGroups(view, attributeGroups, dataSource);
        if (logger.isDebugEnabled()) logger.debug("------------- Final set of attribute groups: " + SpicyEngineUtility.printCollection(attributeGroups));
        return attributeGroups;
    }

    private List<AttributeGroup> findAttributeGroupsForJoins(List<JoinGroup> joinGroups) {
        List<AttributeGroup> result = new ArrayList<AttributeGroup>();
        for (JoinGroup joinGroup : joinGroups) {
            List<AttributeGroup> attributeGroups = findAttributeGroupsForJoinGroup(joinGroup);
            result.addAll(attributeGroups);
        }
        return result;
    }

    private List<AttributeGroup> findAttributeGroupsForJoinGroup(JoinGroup joinGroup) {
        List<AttributeGroup> result = new ArrayList<AttributeGroup>();
        for (VariableJoinCondition joinCondition : joinGroup.getJoinConditions()) {
            for (int i = 0; i < joinCondition.getFromPaths().size(); i++) {
                VariablePathExpression fromPath = joinCondition.getFromPaths().get(i);
                VariablePathExpression toPath = joinCondition.getToPaths().get(i);
                addToAttributeGroups(fromPath, toPath, result);
            }
        }
        return result;
    }

    private void addToAttributeGroups(VariablePathExpression fromPath, VariablePathExpression toPath, List<AttributeGroup> attributeGroups) {
        for (AttributeGroup attributeGroup : attributeGroups) {
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, fromPath) && !EGDUtility.containsPathWithSameVariableId(attributeGroup, toPath)) {
                attributeGroup.addAttributePath(toPath);
                return;
            }
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, toPath) && !EGDUtility.containsPathWithSameVariableId(attributeGroup, fromPath)) {
                attributeGroup.addAttributePath(fromPath);
                return;
            }
        }
        AttributeGroup newGroup = new AttributeGroup();
        newGroup.addAttributePath(fromPath);
        newGroup.addAttributePath(toPath);
        attributeGroups.add(newGroup);
    }

    private void addSingletonGroups(SimpleConjunctiveQuery view, List<AttributeGroup> attributeGroups, IDataSourceProxy dataSource) {
        for (SetAlias variable : view.getVariables()) {
            for (VariablePathExpression attributePath : variable.getAttributes(dataSource.getIntermediateSchema())) {
                if (!belongsToGroups(attributePath, attributeGroups)) {
                    AttributeGroup newGroup = new AttributeGroup();
                    newGroup.addAttributePath(attributePath);
                    attributeGroups.add(newGroup);
                }
            }
        }
    }

    private boolean belongsToGroups(VariablePathExpression attributePath, List<AttributeGroup> attributeGroups) {
        for (AttributeGroup attributeGroup : attributeGroups) {
            if (EGDUtility.containsPathWithSameVariableId(attributeGroup, attributePath)) {
                return true;
            }
        }
        return false;
    }
}
