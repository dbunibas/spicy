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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.tree.ActionAddMappingInCompositionScene;
import it.unibas.spicygui.controllo.tree.ActionCreateCompositionScene;
import it.unibas.spicygui.controllo.tree.ActionOpenGeneric;
import it.unibas.spicygui.controllo.tree.ActionRemoveScenario;
import it.unibas.spicygui.controllo.tree.ActionSelectMappingTask;
import it.unibas.spicygui.controllo.tree.ActionSettingEngineConfiguration;
import it.unibas.spicygui.controllo.window.operator.ProjectTreeGenerator;
import it.unibas.spicygui.vista.dnd.TreeDragSource;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

public final class ProjectTreeTopComponent extends TopComponent {

    private static Log logger = LogFactory.getLog(ProjectTreeTopComponent.class);
    private static ProjectTreeTopComponent instance;
    private static final String PREFERRED_ID = "ProjectTreeTopComponent";
    private ProjectTreeGenerator projectTreeGenerator = new ProjectTreeGenerator();
    private boolean aggiornato;
    private JScrollPane scrollPane;
    private JTree jTree;
    private JPopupMenu popUpMenu;
    private ActionSelectMappingTask actionSelectMappingTask;
    private ActionOpenGeneric actionOpenGeneric;
    private ActionRemoveScenario actionRemoveScenario;
    private ActionSettingEngineConfiguration actionSettingEngineConfiguration;
    private ActionAddMappingInCompositionScene actionAddMappingInCompositionScene;
    private ActionCreateCompositionScene actionCreateCompositionScene;

    private ProjectTreeTopComponent() {
        //initComponents();
        myInitComponents();
        initAction();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_PROJECT_TREE_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_PROJECT_TREE_TOP_COMPONENT_TOOLTIP));
        this.setIcon(ImageUtilities.loadImage(Costanti.ICONA_PROJECT, true));

    }

    private void myInitComponents() {
        this.setLayout(new BorderLayout());
        this.scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new DimensionUIResource(100, this.getHeight()));
        this.setPreferredSize(new Dimension(100, this.getHeight()));
        this.jTree = new JTree(null, false);
        new TreeDragSource(jTree, DnDConstants.ACTION_COPY_OR_MOVE);
        this.jTree.setDragEnabled(true);
//        new TreeDropTarget(tree);
        this.scrollPane.setViewportView(jTree);
        this.add(this.scrollPane, BorderLayout.CENTER);
    }

    private void initAction() {
        this.actionSelectMappingTask = new ActionSelectMappingTask(this.jTree);
        this.actionOpenGeneric = new ActionOpenGeneric(this.jTree);
        this.actionRemoveScenario = new ActionRemoveScenario(this.jTree);
        this.actionSettingEngineConfiguration = new ActionSettingEngineConfiguration(this.jTree);
        this.actionAddMappingInCompositionScene = new ActionAddMappingInCompositionScene(this.jTree);
        this.actionCreateCompositionScene = new ActionCreateCompositionScene(this.jTree);
    }

    public boolean isAggiornato() {
        return aggiornato;
    }

    public void setAggiornato(boolean aggiornato) {
        this.aggiornato = aggiornato;
    }

    public void aggiornaAlbero(Scenario scenario, TopComponent topComponentAdded) {
        //TODO vedere se sia opportuno creare un proprio modello per l'albero e aggiungere ogni volta in quel modello
        //i nuovi rami che sono stati creati, in modo tale che lo stato dell'albero venga preservato (rimangano i rami, aperti o chiusi se lo erano prima dell'analisi)
        projectTreeGenerator.generateTree(jTree, scenario, topComponentAdded);
        //   this.scrollPane.removeAll();
        //      this.scrollPane.setViewportView(jTree);
        //    Utility.expandAll(jTree);
        jTree.updateUI();
        //  this.aggiornato = true;
    }

    public void aggiornaAlberoPerIstanze(Scenario scenario, String stringa) {
        projectTreeGenerator.generateTree(jTree, scenario, stringa);
        jTree.updateUI();
    }

    public void creaAlbero() {
        //TODO vedere se sia opportuno creare un proprio modello per l'albero e aggiungere ogni volta in quel modello
        //i nuovi rami che sono stati creati, in modo tale che lo stato dell'albero venga preservato (rimangano i rami, aperti o chiusi se lo erano prima dell'analisi)
        projectTreeGenerator.generateTree(jTree);
        //   this.scrollPane.removeAll();
        jTree.updateUI();
        creaPopUp();
        //  this.aggiornato = true;
    }

    private void creaPopUp() {
        this.popUpMenu = new JPopupMenu();
        this.popUpMenu.add(this.actionSelectMappingTask);
        this.popUpMenu.add(this.actionOpenGeneric);
        this.popUpMenu.add(this.actionRemoveScenario);
        this.popUpMenu.add(this.actionSettingEngineConfiguration);
        this.popUpMenu.add(this.actionAddMappingInCompositionScene);
        this.popUpMenu.add(this.actionCreateCompositionScene);
        this.jTree.addMouseListener(new PopUpListener());
    }

    class PopUpListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
                selezionaCella(e);
                TreeTopComponentAdapter adapter = getAdapterFromEvent(e);
                TopComponent tc = adapter.getTopComponent();
                if (tc != null) {
                    actionOpenGeneric.actionPerformed(new ActionEvent(e.getSource(), e.getID(), e.paramString()));
                }

            }
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                selezionaCella(e);
            }
            maybeShowPopup(e);
        }

        private TreeTopComponentAdapter getAdapterFromEvent(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            TreePath treePath = albero.getSelectionPath();
            if (treePath != null) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) treeNode.getUserObject();
                return adapter;
            }
            return new TreeTopComponentAdapter(null, false, false, false);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                TreeTopComponentAdapter adapter = getAdapterFromEvent(e);
                TopComponent tc = adapter.getTopComponent();
                if (tc != null) {
                    //TODO se non ci sono altri cambiamenti per le azioni sui nodi, e possibile accorpare questo "if then else"
                    if (tc instanceof MappingTaskTopComponent) {
                        actionSelectMappingTask.setEnabled(false);
                        actionOpenGeneric.setEnabled(true);
                        actionRemoveScenario.setEnabled(false);
                        actionSettingEngineConfiguration.setEnabled(false);
                        actionAddMappingInCompositionScene.setEnabled(false);
                        actionCreateCompositionScene.setEnabled(false);
                        popUpMenu.show(jTree, e.getX(), e.getY());
                        return;
                    } else {
                        actionSelectMappingTask.setEnabled(false);
                        actionOpenGeneric.setEnabled(true);
                        actionRemoveScenario.setEnabled(false);
                        actionSettingEngineConfiguration.setEnabled(false);
                        actionAddMappingInCompositionScene.setEnabled(false);
                        actionCreateCompositionScene.setEnabled(false);
                        popUpMenu.show(jTree, e.getX(), e.getY());
                        return;
                    }
                } else if (adapter.getScenario() != null) {
                    actionSelectMappingTask.setEnabled(true);
                    actionOpenGeneric.setEnabled(false);
                    actionRemoveScenario.setEnabled(true);
                    actionSettingEngineConfiguration.setEnabled(true);
                    actionAddMappingInCompositionScene.setEnabled(true);
                    actionCreateCompositionScene.setEnabled(true);
                    popUpMenu.show(jTree, e.getX(), e.getY());
                    return;
                }
            }
        }

//        private void maybeShowPopup(MouseEvent e) {
//            if (e.isPopupTrigger()) {
//                popUpMenu.show(jTree, e.getX(), e.getY());
//            }
//        }
        private void selezionaCella(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
            albero.setSelectionRow(row);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public static synchronized ProjectTreeTopComponent getDefault() {
        if (instance == null) {
            instance = new ProjectTreeTopComponent();
        }
        return instance;
    }

    public static synchronized ProjectTreeTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ProjectTreeTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ProjectTreeTopComponent) {
            return (ProjectTreeTopComponent) win;
        }
        Logger.getLogger(ProjectTreeTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return ProjectTreeTopComponent.getDefault();
        }
    }
}
