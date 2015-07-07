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
package it.unibas.spicygui.vista;

//import it.unibas.schemamerginggui.view.listener.MyMouseEventListener;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicy.model.datasource.nodes.SetCloneNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.ComplexConjunctiveQuery;
import it.unibas.spicy.model.mapping.ComplexQueryWithNegations;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.NegatedComplexQuery;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberi;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetEsisteSelectionCondition;
import it.unibas.spicygui.controllo.datasource.operators.GenerateSchemaTree;
import it.unibas.spicygui.controllo.datasource.operators.ICreaWidgetCorrespondences;
import it.unibas.spicygui.controllo.provider.MyPopupSceneMatcher;
import it.unibas.spicygui.controllo.provider.intermediatezone.MyPopupProviderIntermedieZone;
import it.unibas.spicygui.controllo.tree.ActionDeleteDuplicateSetCloneNode;
import it.unibas.spicygui.controllo.tree.ActionDuplicateSetNode;
import it.unibas.spicygui.controllo.tree.ActionSelectionCondition;
import it.unibas.spicygui.controllo.tree.ActionViewAllVirtualNode;
import it.unibas.spicygui.vista.listener.ConstraintColoringTreeSelectionListener;
import it.unibas.spicygui.vista.listener.MyMouseEventListener;
import it.unibas.spicygui.vista.listener.ScrollPaneAdjustmentListener;
import it.unibas.spicygui.vista.listener.WidgetMoveExpansionListener;
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.OverlayLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class JLayeredPaneCorrespondences extends JLayeredPane {

    private Scenario scenario;
    private JPanel pannelloPrincipale;
    private JPanel intermediatePanel;
    private JSplitPane split;
    private JSplitPane splitChild;
    private GraphSceneGlassPane glassPane;
    private JTree sourceSchemaTree;
    private JTree targetSchemaTree;
    private JScrollPane scrollSource;
    private JScrollPane scrollTarget;
    private JPopupMenu popUpMenuSource;
    private JPopupMenu popUpMenuTarget;
    private JPopupMenu popUpMenuSourceDuplicate;
    private JPopupMenu popUpMenuTargetDuplicate;
    private JPopupMenu popUpMenuSourceDeleteDuplicate;
    private JPopupMenu popUpMenuTargetDeleteDuplicate;
    private boolean analizzato = false;
    private GenerateSchemaTree treeGenerator = new GenerateSchemaTree();
    private Modello modello;
    private static Log logger = LogFactory.getLog(JLayeredPaneCorrespondences.class);

    public JLayeredPaneCorrespondences(Scenario scenario) {
        this.scenario = scenario;
        executeInjection();
        createComponents();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    ///////////////////////    INIZIALIZZAZIONE  ///////////////////////////////////
    private void createComponents() {
        this.pannelloPrincipale = new javax.swing.JPanel();
        this.pannelloPrincipale.setLayout(new java.awt.BorderLayout());
        this.glassPane = new GraphSceneGlassPane();
        this.intermediatePanel = new JPanel();
        this.intermediatePanel.setName(Costanti.INTERMEDIE);
        this.intermediatePanel.setLayout(new AbsoluteLayout());
        this.intermediatePanel.setBackground(Costanti.getIntermediateColor());

        this.scrollSource = new JScrollPane();
        this.scrollSource.setMinimumSize(new Dimension(200, getHeight()));
        this.splitChild = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollSource, intermediatePanel);
        //this.splitChild.setOneTouchExpandable(true);

        this.scrollTarget = new JScrollPane();
        this.scrollTarget.setMinimumSize(new Dimension(200, getHeight()));
        this.split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.getSplitChild(), scrollTarget);

        this.pannelloPrincipale.setOpaque(false);
        this.glassPane.setOpaque(false);
        this.setOpaque(false);

        OverlayLayout overlaylayout = new OverlayLayout(this);
        this.setLayout(overlaylayout);

        this.add(this.glassPane);
        this.add(this.pannelloPrincipale);
        this.pannelloPrincipale.add(getSplit());

        setSplitPane();
        initMouseListener();
    }

    ////////////////////////////    ANALISI    /////////////////////////// 
    public void drawScene(CreaWidgetAlberi widgetCreator, ICreaWidgetCorrespondences correspondenceCreator) {
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask == null) {
            this.analizzato = false;
            return;
        }
        IDataSourceProxy source = mappingTask.getSourceProxy();
        IDataSourceProxy target = mappingTask.getTargetProxy();
        createTrees(source, target);
        createTreeWidgets(source, target, widgetCreator);
        createConnectionWidgets(correspondenceCreator);
//        updateTree();
        this.analizzato = true;
        this.sourceSchemaTree.updateUI();
        this.targetSchemaTree.updateUI();
        this.moveToFront(this.glassPane);
        this.setVisible(true);
        this.glassPane.getScene().setMaximumBounds(this.glassPane.getScene().getBounds());
        initListener();

    }

    private void createTrees(IDataSourceProxy source, IDataSourceProxy target) {
        this.sourceSchemaTree = treeGenerator.buildSchemaTree(source);
        this.targetSchemaTree = treeGenerator.buildSchemaTree(target);
        this.scrollSource.setViewportView(this.sourceSchemaTree);
        this.sourceSchemaTree.setBorder((BorderFactory.createEmptyBorder(20, 20, 10, 10)));
        this.scrollTarget.setViewportView(this.targetSchemaTree);
        this.targetSchemaTree.setBorder((BorderFactory.createEmptyBorder(20, 20, 10, 10)));
        Utility.expandAll(sourceSchemaTree);
        Utility.expandAll(targetSchemaTree);
    }

    private void createTreeWidgets(IDataSourceProxy source, IDataSourceProxy target, CreaWidgetAlberi widgetCreator) {
        this.glassPane.clearTrees();
        widgetCreator.creaWidgetAlbero(sourceSchemaTree, getSplit(), glassPane, true);
        widgetCreator.creaWidgetAlbero(targetSchemaTree, getSplitChild(), glassPane, false);
        widgetCreator.creaWidgetConstraints(source, true);
        widgetCreator.creaWidgetConstraints(target, false);
        widgetCreator.creaWidgetJoinConditions(source, true);
        widgetCreator.creaWidgetJoinConditions(target, false);
    }

    public void createConnectionWidgets(ICreaWidgetCorrespondences correspondenceCreator) {
        this.glassPane.clearConnections();
        this.glassPane.clearConstants();
        this.glassPane.clearFunctions();
        this.glassPane.validate();
        correspondenceCreator.creaWidgetCorrespondences();
        correspondenceCreator.creaWidgetIconForSelectionCondition();

        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask != null) {
            IDataSourceProxy source = mappingTask.getSourceProxy();
            IDataSourceProxy target = mappingTask.getTargetProxy();
            correspondenceCreator.creaWidgetFunctionalDependencies(source, true);
            correspondenceCreator.creaWidgetFunctionalDependencies(target, false);
            return;
        }
    }

    public void setSplitPane() {
        int frameWidth = WindowManager.getDefault().getMainWindow().getWidth();
        int splitWidth = (int) ((frameWidth * 0.9) * 0.60);
        int splitChildWidth = (int) ((frameWidth * 0.9) * 0.30);
        this.getSplit().setDividerLocation(splitWidth);
        this.getSplitChild().setDividerLocation(splitChildWidth);
    }

    public boolean isAnalizzato() {
        return analizzato;
    }

    public GraphSceneGlassPane getGlassPane() {
        return glassPane;
    }

    public JPanel getPannelloPrincipale() {
        return pannelloPrincipale;
    }

    //TODO intrrodotto per renderlo compatibile con le vecchie ffunzionalità di spicy
    public Scenario getScenario() {
        return scenario;
    }

    public void moveToFront() {
        this.moveToFront(this.glassPane);
    }

    public void clear() {
        this.analizzato = false;
        this.scrollSource.getViewport().removeAll();
        this.scrollTarget.getViewport().removeAll();
        this.glassPane.clear();
        this.setVisible(false);
    }


    /////////////////////////     LISTENER       /////////////////////////////////
    private void initMouseListener() {
        MyMouseEventListener myMouseEventlistener = new MyMouseEventListener(glassPane, pannelloPrincipale, this, getSplit());
        glassPane.getView().addMouseListener(myMouseEventlistener);
        glassPane.getView().addMouseMotionListener(myMouseEventlistener);
    }

    private void initListener() {
//        MyMouseEventListener myMouseEventlistener = new MyMouseEventListener(glassPane, pannelloPrincipale, this, split);
//        glassPane.getView().addMouseListener(myMouseEventlistener);
//        glassPane.getView().addMouseMotionListener(myMouseEventlistener);

        JScrollBar jScrollBarHorizontal = scrollSource.getHorizontalScrollBar();
        JScrollBar jScrollBarVertical = scrollSource.getVerticalScrollBar();
        ScrollPaneAdjustmentListener my = new ScrollPaneAdjustmentListener(this, sourceSchemaTree, glassPane, sourceSchemaTree, "source");
        jScrollBarHorizontal.addAdjustmentListener(my);
        jScrollBarVertical.addAdjustmentListener(my);

        JScrollBar jScrollBarHorizontalTarget = scrollTarget.getHorizontalScrollBar();
        JScrollBar jScrollBarVerticalTarget = scrollTarget.getVerticalScrollBar();
        ScrollPaneAdjustmentListener my2 = new ScrollPaneAdjustmentListener(this, targetSchemaTree, glassPane, targetSchemaTree, "target");
        jScrollBarHorizontalTarget.addAdjustmentListener(my2);
        jScrollBarVerticalTarget.addAdjustmentListener(my2);

        sourceSchemaTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                moveToFront(glassPane);
            }
        });

        this.sourceSchemaTree.addTreeExpansionListener(new WidgetMoveExpansionListener(this, sourceSchemaTree, glassPane, sourceSchemaTree, "source"));
        this.targetSchemaTree.addTreeExpansionListener(new WidgetMoveExpansionListener(this, targetSchemaTree, glassPane, targetSchemaTree, "target"));

//        this.sourceSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_SOURCE));
//        this.targetSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_TARGET));

        initListenerSplit();
//        creaPopUpSource();
//        creaPopUpTarget();
    }

    public void createIntermediateZonePopUp() {
        this.glassPane.getScene().getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderIntermedieZone(this)));
        creaPopUpMappingTaskTreeSource();
        creaPopUpMappingTaskTreeSourceDuplicate();
        creaPopUpMappingTaskTreeSourceDeleteDuplicate();
        creaPopUpMappingTaskTreeTarget();
        creaPopUpMappingTaskTreeTargetDuplicate();
        creaPopUpMappingTaskTreeTargetDeleteDuplicate();
    }

    public void createSchemaMatcherZonePopUp(MyPopupSceneMatcher myPopupSceneMatcher) {
//        this.glassPane.getScene().getActions().addAction(ActionFactory.createPopupMenuAction(myPopupSceneMatcher));
//        creaPopUpSource();
//        creaPopUpTarget();
    }

    public void initTreeSelectionListnerMappingTask() {
        this.sourceSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_SOURCE, Costanti.CONNECTION_CONSTRAINT, Costanti.JOIN_CONNECTION_CONSTRAINT_SOURCE, Costanti.JOIN_CONNECTION_CONSTRAINT));
        this.targetSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_TARGET, Costanti.CONNECTION_CONSTRAINT, Costanti.JOIN_CONNECTION_CONSTRAINT_TARGET, Costanti.JOIN_CONNECTION_CONSTRAINT));
    }

    public void initTreeSelectionListnerMatcher() {
        this.sourceSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_SOURCE_SPICY, Costanti.CONNECTION_CONSTRAINT_SPICY, Costanti.JOIN_CONNECTION_CONSTRAINT_SOURCE_SPICY, Costanti.JOIN_CONNECTION_CONSTRAINT_SPICY));
        this.targetSchemaTree.addTreeSelectionListener(new ConstraintColoringTreeSelectionListener(Costanti.CONNECTION_CONSTRAINT_TARGET_SPICY, Costanti.CONNECTION_CONSTRAINT_SPICY, Costanti.JOIN_CONNECTION_CONSTRAINT_TARGET_SPICY, Costanti.JOIN_CONNECTION_CONSTRAINT_SPICY));
    }

    private void initListenerSplit() {
        this.getSplit().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                moveToFront();
                glassPane.updateUI();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                moveToFront();
                glassPane.updateUI();
            }
        });

        this.getSplitChild().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                moveToFront();
                glassPane.updateUI();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                moveToFront();
                glassPane.updateUI();
            }
        });
    }

//    private void creaPopUpSource() {
//        this.popUpMenuSource = new JPopupMenu();
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
//        if (mappingTask != null) {
//            this.popUpMenuSource.add(new ActionExclusionInclusion(sourceSchemaTree, mappingTask.getSource()));
//            this.sourceSchemaTree.addMouseListener(new PopUpListenerSource());
//        }
//    }
//
//    class PopUpListenerSource extends MouseAdapter {
//
//        public void mousePressed(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        public void mouseReleased(MouseEvent e) {
//            if (e.getButton() == MouseEvent.BUTTON3) {
//                selezionaCella(e);
//            }
//            maybeShowPopup(e);
//        }
//
//        private void maybeShowPopup(MouseEvent e) {
//            if (e.isPopupTrigger()) {
//                popUpMenuSource.show(sourceSchemaTree, e.getX(), e.getY());
//            }
//        }
//
//        private void selezionaCella(MouseEvent e) {
//            Object o = e.getSource();
//            JTree albero = (JTree) o;
//            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
//            albero.setSelectionRow(row);
//        }
//    }
//    private void creaPopUpTarget() {
//        this.popUpMenuTarget = new JPopupMenu();
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
//        if (mappingTask != null) {
//            this.popUpMenuTarget.add(new ActionExclusionInclusion(targetSchemaTree, mappingTask.getTarget()));
//            this.targetSchemaTree.addMouseListener(new PopUpListenerTarget());
//        }
//    }
//
//    class PopUpListenerTarget extends MouseAdapter {
//
//        public void mousePressed(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        public void mouseReleased(MouseEvent e) {
//            if (e.getButton() == MouseEvent.BUTTON3) {
//                selezionaCella(e);
//            }
//            maybeShowPopup(e);
//        }
//
//        private void maybeShowPopup(MouseEvent e) {
//            if (e.isPopupTrigger()) {
//                popUpMenuTarget.show(targetSchemaTree, e.getX(), e.getY());
//            }
//        }
//
//        private void selezionaCella(MouseEvent e) {
//            Object o = e.getSource();
//            JTree albero = (JTree) o;
//            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
//            albero.setSelectionRow(row);
//        }
//    }
    private void creaPopUpMappingTaskTreeSource() {
        this.popUpMenuSource = new JPopupMenu();
        this.popUpMenuSource.add(new ActionViewAllVirtualNode(sourceSchemaTree));
        this.sourceSchemaTree.addMouseListener(new PopUpListenerMappingTaskTreeSource());
    }

    private void creaPopUpMappingTaskTreeSourceDuplicate() {
        this.popUpMenuSourceDuplicate = new JPopupMenu();
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask != null) {
            this.popUpMenuSourceDuplicate.add(new ActionDuplicateSetNode(this, sourceSchemaTree, mappingTask.getSourceProxy()));
            this.popUpMenuSourceDuplicate.add(new ActionSelectionCondition(this, sourceSchemaTree, mappingTask.getSourceProxy()));
        }
    }

    private void creaPopUpMappingTaskTreeSourceDeleteDuplicate() {
        this.popUpMenuSourceDeleteDuplicate = new JPopupMenu();
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask != null) {
            this.popUpMenuSourceDeleteDuplicate.add(new ActionDeleteDuplicateSetCloneNode(this, sourceSchemaTree, mappingTask.getSourceProxy()));
            this.popUpMenuSourceDeleteDuplicate.add(new ActionSelectionCondition(this, sourceSchemaTree, mappingTask.getSourceProxy()));
        }
    }

    public JSplitPane getSplit() {
        return split;
    }

    public JSplitPane getSplitChild() {
        return splitChild;
    }

//    private void updateTree() {
////        createTrees(null, null);
////        creaWidgetIconForSelectionCondition();
//    }

    
//    private void extractPathExpressions(List<VariableSelectionCondition> variableSelectionConditions, List<PathExpression> pathExpressions) {
//        for (VariableSelectionCondition variableSelectionCondition : variableSelectionConditions) {
//            for (VariablePathExpression variablePathExpression : variableSelectionCondition.getSetPaths()) {
//                pathExpressions.add(variablePathExpression.getAbsolutePath());
//            }
//        }
//    }
//    
//    private List<PathExpression> findAllSelectionConditionPath (ComplexQueryWithNegations complexQueryWithNegations) {
//        ComplexConjunctiveQuery complexConjunctiveQuery = complexQueryWithNegations.getComplexQuery();
//        List<PathExpression> pathExpressions = new ArrayList<PathExpression>();
//        List<VariableSelectionCondition> variableSelectionConditions = complexConjunctiveQuery.getAllSelections();
//        extractPathExpressions(variableSelectionConditions, pathExpressions);
//        for (NegatedComplexQuery negatedComplexQuery : complexQueryWithNegations.getNegatedComplexQueries()) {
//            pathExpressions.addAll(findAllSelectionConditionPath(negatedComplexQuery.getComplexQuery()));
//        }
//        return pathExpressions;
//    }
//    private void creaWidgetIconForSelectionCondition() {
//        FORule tgd = scenario.getSelectedFORule();
//        
//        FindNode finder = new FindNode();
//        CreaWidgetEsisteSelectionCondition checker = new CreaWidgetEsisteSelectionCondition();
//        MappingTask mappingTask = scenario.getMappingTask();
//        IDataSourceProxy source = mappingTask.getSourceProxy();
//        List<PathExpression> pathExpressions = findAllSelectionConditionPath(tgd.getComplexSourceQuery());
////        for (SelectionCondition selectionCondition : source.getSelectionConditions()) {
//            for (PathExpression pathExpression : pathExpressions) {
//                INode iNode = finder.findNodeInSchema(pathExpression, source);
//                VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE_TGD);
//                CaratteristicheWidgetTree caratteristicheWidgetTree = (CaratteristicheWidgetTree) glassPane.getMainLayer().getChildConstraint(vMDPinWidget);
//                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) caratteristicheWidgetTree.getTreePath().getLastPathComponent();
//                SelectionConditionInfo selectionConditionInfo = creaSelectionConditionInfo(iNode);
//                selectionConditionInfo.setExpressionString("false");
//                selectionConditionInfo.setSelectionCondition(null);
//                checker.creaWidgetEsisteSelectionCondition(treeNode, "false", null);
//            }
////        }
//    }
//
//    private SelectionConditionInfo creaSelectionConditionInfo(INode iNode) {
//        SelectionConditionInfo selectionConditionInfo = null;
//        if (iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO) != null) {
//            selectionConditionInfo = (SelectionConditionInfo) iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO);
//        } else {
//            selectionConditionInfo = new SelectionConditionInfo();
//            iNode.addAnnotation(Costanti.SELECTION_CONDITON_INFO, selectionConditionInfo);
//        }
//        return selectionConditionInfo;
//    }

    class PopUpListenerMappingTaskTreeSource extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                selezionaCella(e);
            }
            maybeShowPopup(e);
        }

        private TreeNodeAdapter getAdapterFromEvent(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            TreePath treePath = albero.getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
            return adapter;
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                TreeNodeAdapter adapter = getAdapterFromEvent(e);
                if (adapter.getINode() instanceof SetCloneNode) {
                    popUpMenuSourceDeleteDuplicate.show(sourceSchemaTree, e.getX(), e.getY());
                    return;
                } else if ((adapter.getINode() instanceof SetNode)) {
                    popUpMenuSourceDuplicate.show(sourceSchemaTree, e.getX(), e.getY());
                } else {
                    popUpMenuSource.show(sourceSchemaTree, e.getX(), e.getY());
                }

            }
        }

        private void selezionaCella(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
            albero.setSelectionRow(row);
        }
    }

    private void creaPopUpMappingTaskTreeTarget() {
        this.popUpMenuTarget = new JPopupMenu();
        this.popUpMenuTarget.add(new ActionViewAllVirtualNode(targetSchemaTree));
        this.targetSchemaTree.addMouseListener(new PopUpListenerMappingTaskTreeTarget());
    }

    private void creaPopUpMappingTaskTreeTargetDuplicate() {
        this.popUpMenuTargetDuplicate = new JPopupMenu();
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask != null) {
            this.popUpMenuTargetDuplicate.add(new ActionDuplicateSetNode(this, targetSchemaTree, mappingTask.getTargetProxy()));
        }
    }

    private void creaPopUpMappingTaskTreeTargetDeleteDuplicate() {
        this.popUpMenuTargetDeleteDuplicate = new JPopupMenu();
        MappingTask mappingTask = scenario.getMappingTask();
        if (mappingTask != null) {
            this.popUpMenuTargetDeleteDuplicate.add(new ActionDeleteDuplicateSetCloneNode(this, targetSchemaTree, mappingTask.getTargetProxy()));
            this.popUpMenuTargetDeleteDuplicate.add(new ActionSelectionCondition(this, targetSchemaTree, mappingTask.getTargetProxy()));
        }
    }

    class PopUpListenerMappingTaskTreeTarget extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                selezionaCella(e);
            }
            maybeShowPopup(e);
        }

        private TreeNodeAdapter getAdapterFromEvent(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            TreePath treePath = albero.getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
            return adapter;
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                TreeNodeAdapter adapter = getAdapterFromEvent(e);
                if (adapter.getINode() instanceof SetCloneNode) {
                    popUpMenuTargetDeleteDuplicate.show(targetSchemaTree, e.getX(), e.getY());
                    return;
                } else if ((adapter.getINode() instanceof SetNode)) {
                    popUpMenuTargetDuplicate.show(targetSchemaTree, e.getX(), e.getY());

                } else {
                    popUpMenuTarget.show(targetSchemaTree, e.getX(), e.getY());
                }
            }
        }

        private void selezionaCella(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
            albero.setSelectionRow(row);
        }
    }

    public MappingTaskTopComponent getMappingTaskTopComponent() {
        return scenario.getMappingTaskTopComponent();
    }
}
