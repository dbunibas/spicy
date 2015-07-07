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
package it.unibas.spicygui.controllo.composition.operators;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.proxies.ChainingDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.MergeDataSourceProxy;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.widget.ChainWidget;
import it.unibas.spicygui.widget.ConstantCompositionWidget;
import it.unibas.spicygui.widget.MergeWidget;
import it.unibas.spicygui.widget.caratteristiche.AbstractCaratteristicheWidgetComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetChainComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstantComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetMergeComposition;
import java.awt.Stroke;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;

public class CreateCorrespondencesComposition {

    private GeneratePathExpression generatePathFromNode = new GeneratePathExpression();
//    private CreaWidgetAlberiLogic constraintCreator = new CreaWidgetAlberiLogic(CreaWidgetAlberi.MAPPING_TASK_TYPE);
    private static Log logger = LogFactory.getLog(CreateCorrespondencesComposition.class);
    private Modello modello;

    public CreateCorrespondencesComposition() {
        executeInjection();
    }

    public void createFromChainToChainCorrespondence(LayerWidget mainLayer, ChainWidget sourceWidget, ChainWidget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionSource = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);

        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetChainCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();

        mutableMappingTaskTarget.addSourceProxies(mutableMappingTaskSource.getMappingTask().getTargetProxy());

        caratteristicheWidgetChainCompositionTarget.setSource(new ChainingDataSourceProxy(caratteristicheWidgetChainCompositionSource.getMappingTask()));

        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);


//        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetChainCompositionSource.getMutableMappingTask();
//        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();
//
//        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
//
//        mutableMappingTaskSource.addTargetProxies(mut);
//        mutableMappingTaskTarget.addS;

    }

    public void createFromChainToMergeCorrespondence(LayerWidget mainLayer, ChainWidget sourceWidget, MergeWidget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionSource = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionTarget = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(targetWidget);

        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetChainCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetMergeCompositionTarget.getMutableMappingTask();

        mutableMappingTaskTarget.addSourceProxies(mutableMappingTaskSource.getMappingTask().getTargetProxy());
//
        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
//
//        mutableMappingTaskSource.addTargetProxies(mut);
//        mutableMappingTaskTarget.addS;
    }

//    public void createFromConstantToMergeCorrespondence(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
////        CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(sourceWidget);
////        CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionTarget = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(targetWidget);
////        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
////        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetCompositionTarget.getMutableMappingTask();
////
////        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
////
////        mutableMappingTaskSource.addTargetProxies(mut);
////        mutableMappingTaskTarget.addSourceProxies(null);
//    }
    public void createFromConstantToChainCorrespondence(LayerWidget mainLayer, ConstantCompositionWidget sourceWidget, ChainWidget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetConstantComposition caratteristicheWidgetCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);
//        Scenario scenarioSource = caratteristicheWidgetCompositionSource.getScenario();
//        Scenario scenarioTarget = caratteristicheWidgetCompositionTarget.getScenario();
//        IDataSourceProxy dataSourceProxySource = scenarioSource.getMappingTask().getSourceProxy();
//        IDataSourceProxy dataSourceProxyTarget = scenarioTarget.getMappingTask().getSourceProxy();
//        MappingTask mappingTaskTarget = scenarioTarget.getMappingTask();
        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();
        caratteristicheWidgetChainCompositionTarget.setSource(new ChainingDataSourceProxy(mutableMappingTaskSource.getMappingTask()));
        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
        mutableMappingTaskTarget.addSourceProxies(mutableMappingTaskSource.getMappingTask().getTargetProxy());
//        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();
//
//        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
//
//        mutableMappingTaskSource.addTargetProxies(mut);
//        mutableMappingTaskTarget.addSourceProxies(null);
//        logger.debug("before");
//        if (dataSourceProxyTarget.getProviderType().equals(SpicyEngineConstants.PROVIDER_TYPE_CHAINING)) {
//            ChainingDataSourceProxy chainingDataSourceProxy = (ChainingDataSourceProxy)dataSourceProxyTarget;
//
////            chainingDataSourceProxy.
//            logger.debug("chaining");
//        } else if (dataSourceProxyTarget.getProviderType().equals(SpicyEngineConstants.PROVIDER_TYPE_MERGE)) {
//            MergeDataSourceProxy mergeDataSourceProxy = (MergeDataSourceProxy)dataSourceProxyTarget;
//            logger.debug("merge");
//        } else if (dataSourceProxyTarget.getProviderType().equals(SpicyEngineConstants.PROVIDER_TYPE_CONSTANT)) {
//            ConstantDataSourceProxy constantDataSourceProxy = (ConstantDataSourceProxy)dataSourceProxyTarget;
////            constantDataSourceProxy.set
//            logger.debug("constant");
//        }
//        logger.debug("after");
//
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
//        mappingTask.addCorrespondence(valueCorrespondence);
//        connectionInfo.setValueCorrespondence(valueCorrespondence);
    }

    public void createCorrespondenceForMerge(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetConstantComposition caratteristicheWidgetConstantCompositionSource = (CaratteristicheWidgetConstantComposition) mainLayer.getChildConstraint(sourceWidget);
        AbstractCaratteristicheWidgetComposition abstractCaratteristicheWidgetCompositionTarget = (AbstractCaratteristicheWidgetComposition) mainLayer.getChildConstraint(targetWidget);
//        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);
        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetConstantCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = abstractCaratteristicheWidgetCompositionTarget.getMutableMappingTask();
        mutableMappingTaskTarget.addSourceProxies(mutableMappingTaskSource.getMappingTask().getTargetProxy());
        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
    }

    public void createCorrespondenceFromMerge(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
        CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeCompositionSource = (CaratteristicheWidgetMergeComposition) mainLayer.getChildConstraint(sourceWidget);
        CaratteristicheWidgetChainComposition caratteristicheWidgetChainCompositionTarget = (CaratteristicheWidgetChainComposition) mainLayer.getChildConstraint(targetWidget);
        MutableMappingTask mutableMappingTaskSource = caratteristicheWidgetMergeCompositionSource.getMutableMappingTask();
        MutableMappingTask mutableMappingTaskTarget = caratteristicheWidgetChainCompositionTarget.getMutableMappingTask();
        MergeDataSourceProxy dataSource = new MergeDataSourceProxy(mutableMappingTaskSource.getSourceProxies());
        caratteristicheWidgetChainCompositionTarget.setSource(dataSource);

//        for (IDataSourceProxy iDataSourceProxy : mutableMappingTaskSource.getSourceProxies()) {
//            for (INode instance : iDataSourceProxy.getInstances()) {
//                dataSource.addInstance(instance);
//            }
//            
//        }

        mutableMappingTaskSource.setMappingTask(new MappingTask(null, dataSource, SpicyEngineConstants.LINES_BASED_MAPPING_TASK));
        mutableMappingTaskTarget.addSourceProxies(mutableMappingTaskSource.getMappingTask().getTargetProxy());
        mutableMappingTaskTarget.addMutableMappingTask(mutableMappingTaskSource);
    }

    public void undo(ValueCorrespondence valueCorrespondence) {
        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.addCorrespondence(valueCorrespondence);
    }

    public void undo(SelectionCondition selectionCondition, IDataSourceProxy dataSource) {
        dataSource.addSelectionCondition(selectionCondition);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Stroke getStroke() {
        return Costanti.BASIC_STROKE;
    }
}
