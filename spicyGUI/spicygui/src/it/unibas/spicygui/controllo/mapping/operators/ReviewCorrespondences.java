/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
 
package it.unibas.spicygui.controllo.mapping.operators;


import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public class ReviewCorrespondences {

    private static Log logger = LogFactory.getLog(ReviewCorrespondences.class);
    private Modello modello;

    public ReviewCorrespondences() {
        executeInjection();
    }

    public void removeCorrespondence(ValueCorrespondence valueCorrespondence) {
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.removeCorrespondence(valueCorrespondence);
    }

    public void removeCandidateCorrespondence(ValueCorrespondence valueCorrespondence) {
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.removeCandidateCorrespondence(valueCorrespondence);
    }

    public void removeSelectionCondition(SelectionCondition selectionCondition, IDataSourceProxy dataSource) {
        dataSource.removeSelectionCondition(selectionCondition);
    }

    public void removeFunctionalDependency(FunctionalDependency functionalDependency, Boolean source) {
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        if (source != null && source) {
            mappingTask.getSourceProxy().removeFunctionalDependency(functionalDependency);
        } else if(source != null && !source){
            mappingTask.getTargetProxy().removeFunctionalDependency(functionalDependency);
        }
        mappingTask.setModified(true);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
