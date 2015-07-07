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
 
package it.unibas.spicygui.controllo.spicy;

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.BestMappingsTopComponent;
import it.unibas.spicygui.vista.InstancesTopComponent;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import it.unibas.spicygui.vista.TransformationTopComponent;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.JTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class ActionShowSelectedMappingTask {

    private static Log logger = LogFactory.getLog(ActionShowSelectedMappingTask.class);
    private Modello modello;
//    private CreateMappingTaskInfo mappingTaskInfoCreator = new CreateMappingTaskInfo();

    public ActionShowSelectedMappingTask() {
        executeInjection();
    }

    private void execute(Scenario scenario) throws MissingResourceException {
        BestMappingsTopComponent topComponent = scenario.getBestMappingsTopComponent();
        JTable tabellaBestMappings = topComponent.getTabellaBestMappings();
        int selectedRow = tabellaBestMappings.getSelectedRow();
        if (selectedRow != -1) {
            List<AnnotatedMappingTask> bestMappingTasks = (List<AnnotatedMappingTask>) modello.getBean(Costanti.BEST_MAPPING_TASKS);
            modello.putBean(Costanti.MAPPINGTASK_SELECTED, NbBundle.getMessage(Costanti.class, Costanti.TRANSFORMATION) + " n. " + selectedRow);
            AnnotatedMappingTask selectedAnnotatedMappingTask = bestMappingTasks.get(selectedRow);
            MappingTask mappingTask = scenario.getMappingTask();
            mappingTask.setValueCorrespondences(selectedAnnotatedMappingTask.getMappingTask().getValueCorrespondences());
            //            MappingTaskInfo mappingTaskInfo = mappingTaskInfoCreator.createMappingTaskInfo(mappingTask);
            //            modello.putBean(Costanti.MAPPINGTASK_INFO, mappingTaskInfo);
            //            logger.info(mappingTaskInfo.toString());
            List<SimilarityCheck> similarityChecks = (List<SimilarityCheck>) selectedAnnotatedMappingTask.getSimilarityChecks();
            if (similarityChecks != null) {
                for (SimilarityCheck similarityCheck : similarityChecks) {
                    logger.info(similarityCheck.toStringWithCircuits());
                }
            }
            logger.info("OVERALL QUALITY: " + selectedAnnotatedMappingTask.getQualityMeasure());
            TransformationTopComponent transformationTopComponent = scenario.getTransformationTopComponent();
            transformationTopComponent.close();
            InstancesTopComponent instancesTopComponent = scenario.getInstancesTopComponent();
            instancesTopComponent.clearTranslated();
            instancesTopComponent.close();
            mappingTask.getTargetProxy().getIntermediateInstances().clear();
            MappingTaskTopComponent mappingTaskTopComponent = scenario.getMappingTaskTopComponent();
            mappingTaskTopComponent.drawConnections();
            mappingTaskTopComponent.openAtTabPosition(-1);
            mappingTaskTopComponent.requestActive();
        }
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void actionPerformed() {
        Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        execute(scenario);
    }

    public void actionPerformedWithScenario(Scenario scenario) {
        execute(scenario);
    }
}
