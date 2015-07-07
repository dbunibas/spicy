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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.rewriting.egds.AttributeGroup;
import it.unibas.spicy.model.mapping.rewriting.egds.TGDAttributeGroups;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class CompareTgdsInOverlapMap {

    private static Log logger = LogFactory.getLog(CompareTgdsInOverlapMap.class);

    private FindAttributeGroups groupFinder = new FindAttributeGroups();

    boolean equals(FORule tgd1, FORule tgd2, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) logger.debug("Comparing tgds: \n" + tgd1 + "\n   and\n" + tgd2);
        TGDAttributeGroups tgdGroups1 = groupFinder.findAttributeGroupsInTgd(tgd1, mappingTask);
        TGDAttributeGroups tgdGroups2 = groupFinder.findAttributeGroupsInTgd(tgd2, mappingTask);
        boolean sameUniversalGroups = areEqualUpToClones(tgdGroups1.getUniversalGroups(), tgdGroups2.getUniversalGroups());
        boolean sameExistentialGroups = areEqualUpToClones(tgdGroups1.getExistentialGroups(), tgdGroups2.getExistentialGroups());
        boolean sameGroups = sameUniversalGroups && sameExistentialGroups;
        boolean sameSourceConditions = sameSourceConditions(tgd1, tgd2);
        if (logger.isDebugEnabled()) logger.debug("Same universal: " + sameUniversalGroups + " - Same existential: " + sameExistentialGroups);
        if (logger.isDebugEnabled()) logger.debug("Same conditions: " + sameSourceConditions);
        if (logger.isDebugEnabled()) logger.debug("Result: " + (sameGroups && sameSourceConditions));
        return sameGroups && sameSourceConditions;
    }

    private boolean areEqualUpToClones(List<AttributeGroup> groups1, List<AttributeGroup> groups2) {
        if (groups1.size() != groups2.size()) {
            return false;
        }
        List<AttributeGroup> groups2Clone = new ArrayList<AttributeGroup>(groups2);
        for (AttributeGroup group : groups1) {
            AttributeGroup groupInList = findGroup(group, groups2Clone);
            if (groupInList == null) {
                return false;
            } else {
                groups2Clone.remove(groupInList);
            }
        }
        return (groups2Clone.isEmpty());
    }

    private AttributeGroup findGroup(AttributeGroup group, List<AttributeGroup> groups) {
        for (AttributeGroup groupInList : groups) {
            if (equalsUpToClones(groupInList, group)) {
                return groupInList;
            }
        }
        return null;
    }

    private boolean equalsUpToClones(AttributeGroup group1, AttributeGroup group2) {
        return SpicyEngineUtility.equalListsUpToClones(group1.getAttributePaths(), group2.getAttributePaths());
    }

    private boolean sameSourceConditions(FORule tgd1, FORule tgd2) {
        List<VariableSelectionCondition> conditions1 = tgd1.getComplexSourceQuery().getComplexQuery().getAllSelections();
        List<VariableSelectionCondition> conditions2 = tgd2.getComplexSourceQuery().getComplexQuery().getAllSelections();
        return SpicyEngineUtility.equalListsUpToVariableIds(conditions1, conditions2);
    }


}
