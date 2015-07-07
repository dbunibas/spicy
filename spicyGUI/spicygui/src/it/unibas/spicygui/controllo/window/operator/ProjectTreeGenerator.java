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
 
package it.unibas.spicygui.controllo.window.operator;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.AccessConfiguration;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.IScenario;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.treepm.TreeRenderer;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

public class ProjectTreeGenerator {

    private static Log logger = LogFactory.getLog(ProjectTreeGenerator.class);
    private Modello modello;

    public ProjectTreeGenerator() {
        executeInjection();
    }

    public void generateTree(JTree jTree) {
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        DefaultMutableTreeNode nodeRoot = new DefaultMutableTreeNode(new TreeTopComponentAdapter(null, false, true, false));
        for (IScenario scenario : scenarios.getListaSceneri()) {
            analizzaScenario(nodeRoot, (Scenario) scenario);
        }
        ((DefaultTreeModel) jTree.getModel()).setRoot(nodeRoot);
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        TreeRenderer rendererTree = new TreeRenderer();
        jTree.setCellRenderer(rendererTree);
        return;
    }

    public void generateTree(JTree jTree, Scenario scenario, String stringa) {
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        TreeNode root = (TreeNode) model.getRoot();
        if (root != null) {
            analisiRicorsivaAggiuntaIstanza(root, scenario, model, stringa);
        }
    }

    public void generateTree(JTree jTree, Scenario scenario, TopComponent topComponentAdded) {
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        TreeNode root = (TreeNode) model.getRoot();
        if (root != null) {
            if (topComponentAdded != null) {
                analisiRicorsivaAggiuntaTC(root, scenario, model, topComponentAdded);
            } else if (scenarios.containsScenario(scenario)) {
                analizzaScenario((DefaultMutableTreeNode) root, scenario);
            } else {
                analisiRicorsivaCancellaScenario(root, scenario, model);
            }
        }
    }

    private void analizzaScenario(DefaultMutableTreeNode treeNode, Scenario scenario) {
        DefaultMutableTreeNode nodeScenario = null;
        if (scenario.isSelected()) {
            nodeScenario = new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario, true, false, false));
        } else {
            nodeScenario = new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario, false, false, false));
        }
        treeNode.add(nodeScenario);
        if (scenario.getMappingTaskTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getMappingTaskTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);

            createMappingTaskChild(nodeTopComponent, scenario.getMappingTask().getSourceProxy(), "Source");
            createMappingTaskChild(nodeTopComponent, scenario.getMappingTask().getTargetProxy(), "Target");

        }
        if (scenario.getTransformationTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getTransformationTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);
        }
        if (scenario.getInstancesTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getInstancesTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);

            createInstanceChild(nodeTopComponent, scenario.getMappingTask().getSourceProxy(), "Source");
            createInstanceChild(nodeTopComponent, scenario.getMappingTask().getTargetProxy(), "Taget");
        }
        if (scenario.getXQueryTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getXQueryTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);
        }
        if (scenario.getSqlTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getSqlTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);
        }
        if (scenario.getTGDListTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getTGDListTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);
        }
        if (scenario.getTGDCorrespondencesTopComponent() != null) {
            DefaultMutableTreeNode nodeTopComponent =
                    new DefaultMutableTreeNode(new TreeTopComponentAdapter(scenario.getTGDCorrespondencesTopComponent(), false, false, false));
            nodeScenario.add(nodeTopComponent);
        }
    }

    private void createInstacesNode(IDataSourceProxy source, DefaultMutableTreeNode nodeChildFirst) {
        List<String> sourceInstancesName = (List<String>) source.getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
        if (sourceInstancesName != null) {
            int i = 0;
            for (String stringa : sourceInstancesName) {
                String title = findTitle(stringa);
                TreeTopComponentAdapter ttca = new TreeTopComponentAdapter(null, false, false, true);
                DefaultMutableTreeNode nodeChild = new DefaultMutableTreeNode(ttca);
                ttca.setName(title);
                nodeChildFirst.add(nodeChild);
            }
        } else {
            int i = 1;
            for (INode instanceNode : source.getOriginalInstances()) {
                TreeTopComponentAdapter ttca = new TreeTopComponentAdapter(null, false, false, true);
                DefaultMutableTreeNode nodeChild = new DefaultMutableTreeNode(ttca);
                ttca.setName("Instance: " + i++);
                nodeChildFirst.add(nodeChild);
            }
        }
    }

    private void createInstanceChild(DefaultMutableTreeNode nodeTopComponent, IDataSourceProxy source, String type) {

        TreeTopComponentAdapter ttcaFirst = new TreeTopComponentAdapter(null, false, false, true);
        DefaultMutableTreeNode nodeChildFirst = new DefaultMutableTreeNode(ttcaFirst);
        ttcaFirst.setName(type);
        nodeTopComponent.add(nodeChildFirst);
        createInstacesNode(source, nodeChildFirst);
    }

    private String findTitle(String absoluteTitle) {
        int start = absoluteTitle.lastIndexOf("\\");
        int end = absoluteTitle.length();
        return absoluteTitle.substring(++start, end);
    }

    private void createMappingTaskChild(DefaultMutableTreeNode nodeTopComponent, IDataSourceProxy datasource, String type) {

        if (datasource.getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_RELATIONAL)) {

            AccessConfiguration accessConfiguration = (AccessConfiguration) datasource.getAnnotation(SpicyEngineConstants.ACCESS_CONFIGURATION);

            TreeTopComponentAdapter ttca0 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild0 = new DefaultMutableTreeNode(ttca0);
            ttca0.setName(type);
            nodeTopComponent.add(nodeChild0);

            TreeTopComponentAdapter ttca1 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild1 = new DefaultMutableTreeNode(ttca1);
            ttca1.setName("driver: " + accessConfiguration.getDriver());
            nodeChild0.add(nodeChild1);

            TreeTopComponentAdapter ttca2 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild2 = new DefaultMutableTreeNode(ttca2);
            ttca2.setName("uri: " + accessConfiguration.getUri());
            nodeChild0.add(nodeChild2);

            TreeTopComponentAdapter ttca3 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild3 = new DefaultMutableTreeNode(ttca3);
            ttca3.setName("login: " + accessConfiguration.getLogin());
            nodeChild0.add(nodeChild3);

            TreeTopComponentAdapter ttca4 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild4 = new DefaultMutableTreeNode(ttca4);
            ttca4.setName("password: " + accessConfiguration.getPassword());
            nodeChild0.add(nodeChild4);

        } else if (datasource.getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_XML)) {

            TreeTopComponentAdapter ttca1 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild = new DefaultMutableTreeNode(ttca1);
            String absolutePathSource = (String) datasource.getAnnotation(SpicyEngineConstants.XML_SCHEMA_FILE);
            ttca1.setName(type + " : " + absolutePathSource.substring(absolutePathSource.lastIndexOf("\\") + 1));
            nodeTopComponent.add(nodeChild);

        } else if (datasource.getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_OBJECT)) {


            TreeTopComponentAdapter ttca0 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild0 = new DefaultMutableTreeNode(ttca0);
            ttca0.setName(type);
            nodeTopComponent.add(nodeChild0);

            String classPathFolder = (String) datasource.getAnnotation(SpicyEngineConstants.CLASSPATH_FOLDER);
            TreeTopComponentAdapter ttca1 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild1 = new DefaultMutableTreeNode(ttca1);
            ttca1.setName("classPathFolder: " + classPathFolder);
            nodeChild0.add(nodeChild1);

            String objectModelFactoryName = (String) datasource.getAnnotation(SpicyEngineConstants.OBJECT_MODEL_FACTORY);
            TreeTopComponentAdapter ttca2 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild2 = new DefaultMutableTreeNode(ttca2);
            ttca2.setName("objectModelFactory: " + objectModelFactoryName);
            nodeChild0.add(nodeChild2);

        } else if (datasource.getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_MOCK)) {

            TreeTopComponentAdapter ttca0 = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode nodeChild0 = new DefaultMutableTreeNode(ttca0);
            ttca0.setName(type + " : Mock");
            nodeTopComponent.add(nodeChild0);
        }

    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    private void analisiRicorsivaAggiuntaTC(Object root, Scenario scenario, DefaultTreeModel model, TopComponent topComponentAdded) {
        int indice = model.getChildCount(root);
        for (int i = 0; i < indice; i++) {
            analisiRicorsivaAggiuntaTC(model.getChild(root, i), scenario, model, topComponentAdded);
        }
//        if (indice == 2) {
        TreeTopComponentAdapter componentAdapter = (TreeTopComponentAdapter) ((DefaultMutableTreeNode) root).getUserObject();
        TopComponent tc = componentAdapter.getTopComponent();
        if (tc != null && tc == scenario.getMappingTaskTopComponent()) {
            TreeNode treeNode = (TreeNode) root;
            int place = model.getChildCount(treeNode.getParent());
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new TreeTopComponentAdapter(topComponentAdded, false, false, false));
            model.insertNodeInto(newNode, (DefaultMutableTreeNode) treeNode.getParent(), place);
        }
//            return;
//        }


    }

    private void analisiRicorsivaAggiuntaIstanza(Object root, Scenario scenario, DefaultTreeModel model, String stringa) {
        int indice = model.getChildCount(root);
        for (int i = 0; i < indice; i++) {
            analisiRicorsivaAggiuntaIstanza(model.getChild(root, i), scenario, model, stringa);
        }
//        if (indice == 2) {
        TreeTopComponentAdapter componentAdapter = (TreeTopComponentAdapter) ((DefaultMutableTreeNode) root).getUserObject();
        TopComponent tc = componentAdapter.getTopComponent();
        if (tc != null && tc == scenario.getMappingTaskTopComponent()) {

            TreeTopComponentAdapter ttca = new TreeTopComponentAdapter(null, false, false, true);
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(ttca);
            TreeNode treeNode = ((TreeNode) root).getParent();
            TreeNode nodeInstancesFather = treeNode.getChildAt(2);
//            if (stringa.equals(Costanti.SOURCE)) {
            TreeNode nodeInstacesSource = nodeInstancesFather.getChildAt(0);
            TreeNode nodeInstacesTarget = nodeInstancesFather.getChildAt(1);
            List<String> sourceInstancesName = (List<String>) scenario.getMappingTask().getSourceProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
            List<String> targetInstancesName = (List<String>) scenario.getMappingTask().getTargetProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
            int place = 0;
            String name = "xxx";
            if (sourceInstancesName.size() != nodeInstacesSource.getChildCount()) {
                place = nodeInstacesSource.getChildCount();
                name = findTitle(sourceInstancesName.get(sourceInstancesName.size() - 1));
                model.insertNodeInto(newNode, (DefaultMutableTreeNode) nodeInstacesSource, place);
            } else if (targetInstancesName.size() != nodeInstacesTarget.getChildCount()) {
                place = nodeInstacesTarget.getChildCount();
                name = findTitle(targetInstancesName.get(targetInstancesName.size() - 1));
                model.insertNodeInto(newNode, (DefaultMutableTreeNode) nodeInstacesTarget, place);
            }
            ttca.setName(name);
            scenario.getXQueryTopComponent().update();

//                return;
//            }

        }
//            return;
//        }


    }

    private boolean analisiRicorsivaCancellaScenario(Object root, Scenario scenario, DefaultTreeModel model) {
        int indice = model.getChildCount(root);
//        if (indice == 1 || indice == 2) {
            TreeTopComponentAdapter componentAdapter = (TreeTopComponentAdapter) ((DefaultMutableTreeNode) root).getUserObject();
            TopComponent tc = componentAdapter.getTopComponent();
            if (tc != null && tc == scenario.getMappingTaskTopComponent()) {
                TreeNode treeNode = (TreeNode) root;
                model.removeNodeFromParent((MutableTreeNode) treeNode.getParent());
                return false;
            }
//        }

        for (int i = 0; i < indice; i++) {
            if (!analisiRicorsivaCancellaScenario(model.getChild(root, i), scenario, model)) {
                return false;
            }
        }
        return true;



//        for (int i = 0; i < root.getChildCount(); i++) {
//            TreePath treePath = jTree.getPathForRow(i);
//            TreeNode treeNode = (TreeNode) treePath.getLastPathComponent();
//            TreeTopComponentAdapter componentAdapter = (TreeTopComponentAdapter) ((DefaultMutableTreeNode) treeNode).getUserObject();
//            TopComponent tc = componentAdapter.getTopComponent();
//            if (tc != null && tc == scenario.getMappingTaskTopComponent()) {
//                int place = model.getChildCount(treeNode.getParent());
//                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new TreeTopComponentAdapter(topComponentAdded, false, false));
//                model.insertNodeInto(newNode, (DefaultMutableTreeNode) treeNode.getParent(), place);
//                model.reload();
//                break;
//            }
//        }
    }
}

