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

import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.ChainingDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.MergeDataSourceProxy;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.controllo.file.ActionOpenMappingTask;
import it.unibas.spicygui.controllo.provider.composition.ActionConstantCompositionConnection;
import it.unibas.spicygui.controllo.provider.composition.ActionMergeConnection;
import it.unibas.spicygui.controllo.provider.composition.ActionUndefinedChainConnection;
import it.unibas.spicygui.controllo.provider.intermediatezone.WidgetCreator;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetChainComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstantComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetMergeComposition;
import java.awt.Point;
import java.io.File;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

public class CreateWidgetAnalyzeComposition {

    private Modello modello;
    private Random random = new Random();
    private ActionOpenMappingTask actionOpenMappingTask;

    public CreateWidgetAnalyzeComposition() {
        executeInjection();

    }

    public void analyzeComposition(GraphSceneGlassPane glassPane, Scenario scenario) {
        if (scenario != null && !((scenario.getMappingTask().getSourceProxy() instanceof ConstantDataSourceProxy) && (scenario.getMappingTask().getTargetProxy() instanceof ConstantDataSourceProxy))) {
            WidgetCreator widgetCreator = new WidgetCreator();
//            GraphSceneGlassPane glassPane = CompositionTopComponent.getDefault().getGlassPane();
            Widget widget = widgetCreator.createDefinedChainWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane, scenario);
            IDataSourceProxy dataSourceProxy = scenario.getMappingTask().getSourceProxy();
            checkComposition(dataSourceProxy, new MutableMappingTask(scenario.getMappingTask()), widget, glassPane, scenario);
//            scenario.setSelected(true);
//            Scenario scenarioOld = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
//            LastActionBean lab = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
//            scenarioOld.setStato(lab.getLastAction());
//            modello.putBean(Costanti.CURRENT_SCENARIO, scenario);
//            scenarioOld.setSelected(false);
//            scenario.setSelected(true);
        }
    }

    private void checkComposition(IDataSourceProxy dataSourceProxy, MutableMappingTask mutableMappingTask, Widget widget, GraphSceneGlassPane glassPane, Scenario rootScenario) {
        if (dataSourceProxy instanceof ConstantDataSourceProxy) {
//            System.out.println("costante");
//            ConstantDataSourceProxy constantDataSourceProxy = (ConstantDataSourceProxy) dataSourceProxy;
//            
//            String fileName = constantDataSourceProxygetDataSource().getMappingTask().getFileName();
//            Scenario scenario = actionOpenMappingTask.openCompositionFile(fileName, new File(fileName), false);
//            rootScenario.addRelatedScenario(scenario);
//            WidgetCreator widgetCreator = new WidgetCreator();
//           Widget widgetInternal = widgetCreator.createConstantWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane, scenario);
////            Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
////             widgetCreator.createCompositionMergeWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane);
//            ActionMergeConnection actionMergeConnection = new ActionMergeConnection(glassPane.getConnectionLayer(), glassPane.getMainLayer(), new CaratteristicheWidgetMergeComposition());
//            actionMergeConnection.createConnection(widgetInternal, widget);
//            for (IDataSourceProxy iDataSourceProxy : mergeDataSourceProxy.getProxies()) {
//                checkComposition(iDataSourceProxy, new MutableMappingTask(null), widgetInternal, glassPane, rootScenario);
//            }

        } else if (dataSourceProxy instanceof ChainingDataSourceProxy) {
            ChainingDataSourceProxy chainingDataSourceProxy = (ChainingDataSourceProxy) dataSourceProxy;
            if (chainingDataSourceProxy.getMappingTask() != null && chainingDataSourceProxy.getMappingTask().getSourceProxy() instanceof ConstantDataSourceProxy) {
                mutableMappingTask.addSourceProxies(dataSourceProxy);
                String fileName = chainingDataSourceProxy.getMappingTask().getFileName();
                Scenario scenario = actionOpenMappingTask.openCompositionFile(fileName, new File(fileName), false);
                rootScenario.addRelatedScenario(scenario);
                WidgetCreator widgetCreator = new WidgetCreator();
                mutableMappingTask.setMappingTask(scenario.getMappingTask());
                Widget widgetInternal = widgetCreator.createConstantWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane, scenario);
                CaratteristicheWidgetConstantComposition caratteristicheWidgetConstantComposition = (CaratteristicheWidgetConstantComposition) glassPane.getMainLayer().getChildConstraint(widgetInternal);
                ActionConstantCompositionConnection actionConstantCompositionConnection = new ActionConstantCompositionConnection(glassPane.getConnectionLayer(), glassPane.getMainLayer(), caratteristicheWidgetConstantComposition);
                actionConstantCompositionConnection.createConnection(widgetInternal, widget);
            } else {

                mutableMappingTask.addSourceProxies(dataSourceProxy);
                String fileName = chainingDataSourceProxy.getMappingTask().getFileName();
                Scenario scenario = actionOpenMappingTask.openCompositionFile(fileName, new File(fileName), false);
                rootScenario.addRelatedScenario(scenario);
                WidgetCreator widgetCreator = new WidgetCreator();
                mutableMappingTask.setMappingTask(scenario.getMappingTask());
                Widget widgetInternal = widgetCreator.createDefinedChainWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane, scenario);
//            ActionConstantCompositionConnection actionCompositionConnection = new ActionConstantCompositionConnection(glassPane.getConnectionLayer(), glassPane.getMainLayer(), new CaratteristicheWidgetConstantComposition(mutableMappingTask));
//            actionCompositionConnection.createConnection(widgetInternal, widget);
                CaratteristicheWidgetChainComposition caratteristicheWidgetChainComposition = (CaratteristicheWidgetChainComposition) glassPane.getMainLayer().getChildConstraint(widgetInternal);
                ActionUndefinedChainConnection actionUndefinedChainConnection = new ActionUndefinedChainConnection(glassPane.getConnectionLayer(), glassPane.getMainLayer(), caratteristicheWidgetChainComposition);
                actionUndefinedChainConnection.createConnection(widgetInternal, widget);
                if (chainingDataSourceProxy.getMappingTask() != null) {
                    checkComposition(chainingDataSourceProxy.getMappingTask().getSourceProxy(), new MutableMappingTask(null), widgetInternal, glassPane, rootScenario);
                }
            }

        } else if (dataSourceProxy instanceof MergeDataSourceProxy) {
            MergeDataSourceProxy mergeDataSourceProxy = (MergeDataSourceProxy) dataSourceProxy;
            //TODO Vedere come gestire il mergeper tenere le datasource
            mutableMappingTask.setSourcetProxies(mergeDataSourceProxy.getProxies());
            WidgetCreator widgetCreator = new WidgetCreator();
//            Scenario scenario = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
            Widget widgetInternal = widgetCreator.createCompositionMergeWidget(glassPane.getScene(), glassPane.getMainLayer(), glassPane.getConnectionLayer(), calculateRandomPoint(glassPane), glassPane);
            CaratteristicheWidgetMergeComposition caratteristicheWidgetMergeComposition = (CaratteristicheWidgetMergeComposition) glassPane.getMainLayer().getChildConstraint(widgetInternal);
            ActionMergeConnection actionMergeConnection = new ActionMergeConnection(glassPane.getConnectionLayer(), glassPane.getMainLayer(), caratteristicheWidgetMergeComposition);
            actionMergeConnection.createConnection(widgetInternal, widget);
            for (IDataSourceProxy iDataSourceProxy : mergeDataSourceProxy.getProxies()) {
                checkComposition(iDataSourceProxy, new MutableMappingTask(null), widgetInternal, glassPane, rootScenario);
            }

        }
    }

    private Point calculateRandomPoint(GraphSceneGlassPane glassPane) {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        int x = random.nextInt((int) (frame.getSize().width * 0.4));
        int y = random.nextInt((int) (frame.getSize().height * 0.2));
        Point point = SwingUtilities.convertPoint(frame, x, y, glassPane);
        return point;
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.actionOpenMappingTask == null) {
            this.actionOpenMappingTask = Lookups.forPath("Azione").lookup(ActionOpenMappingTask.class);
        }
    }
}
