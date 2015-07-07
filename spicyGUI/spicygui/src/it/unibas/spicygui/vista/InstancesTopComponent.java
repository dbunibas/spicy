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

import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.exceptions.UnsatisfiableEGDException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public final class InstancesTopComponent extends TopComponent {

    private static Log logger = LogFactory.getLog(InstancesTopComponent.class);
    private Modello modello;
    private Scenario scenario;
    private List<JComponent> listaTabTranslated = new ArrayList<JComponent>();
    private List<JComponent> listaTabCanonical = new ArrayList<JComponent>();
    private List<JComponent> listaTabIntermediate = new ArrayList<JComponent>();
    private boolean ripulito = true;
    private boolean ripulitoTranslated = true;
    private boolean ripulitoCanonical = true;
    private boolean ripulitoIntermediate = true;
    public static final String PREFERRED_ID = "InstancesTopComponent";

    public InstancesTopComponent(Scenario scenario, Image imageNumber) {
        this.scenario = scenario;
        initComponents();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_SOURCE_INSTANCES_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_SOURCE_INSTANCES_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, imageNumber, Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
    }

    public void createSourceInstanceTree() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        IDataSourceProxy source = mappingTask.getSourceProxy();
        List<String> sourceInstancesName = (List<String>) source.getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
        if (sourceInstancesName != null && sourceInstancesName.size() > 0) {
            int i = 0;
            for (INode instanceNode : source.getOriginalInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, true, mappingTask);
                String title = findTitle(sourceInstancesName.get(i));
                this.tabbedPaneSource.addTab("Source Instance: " + title, viewInstanceTree);
                i++;
            }
        } else {
            int i = 1;
            for (INode instanceNode : source.getOriginalInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, true, mappingTask);
                this.tabbedPaneSource.addTab("Source Instance: " + i++, viewInstanceTree);
            }
        }
        this.ripulito = false;
    }

    public void createTargetInstanceTree() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        IDataSourceProxy target = mappingTask.getTargetProxy();
        List<String> targetInstanceNames = (List<String>) target.getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
        if (targetInstanceNames != null) {
            int i = 0;
            for (INode instanceNode : target.getOriginalInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, true, mappingTask);
                String title = findTitle(targetInstanceNames.get(i));
                this.tabbedPaneTarget.addTab("Target Instance: " + title, viewInstanceTree);
                i++;
            }
        } else {
            int i = 1;
            for (INode instanceNode : target.getOriginalInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, false, mappingTask);
                this.tabbedPaneTarget.addTab("Target Instance" + i++, viewInstanceTree);
            }
        }

        this.ripulito = false;
    }

    public void createTranslatedInstanceTree() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        IDataSourceProxy solution = null;
        try {
            //TODO: Without this call the nested egd scenarios fail
            mappingTask.setModified(true);
            mappingTask.setToBeSaved(true);
            solution = mappingTask.getMappingData().getSolution();
            mappingTask.getMappingData().verifySolution();
//        } catch (UnsatisfiableEGDException ex) {
        } catch (Exception ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
            //solution = mappingTask.getMappingData().getSolution();
        }
        if (solution != null && solution.getInstances() != null) {
            int i = 1;
//            tabbedPaneTarget.removeAll();
            for (INode instanceNode : solution.getInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, false, mappingTask);
                listaTabTranslated.add(viewInstanceTree);
                String tabName = "Solution: " + i++;
                this.tabbedPaneTarget.addTab(tabName, viewInstanceTree);
            }
            this.ripulitoTranslated = false;
        }
    }

    public void createCanonicalInstanceTree() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        IDataSourceProxy canonicalSolution = null;
        try {
            canonicalSolution = mappingTask.getMappingData().getCanonicalSolution();
        } catch (Exception ex) {
//            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
        }
        if (canonicalSolution != null && canonicalSolution.getInstances() != null) {
            int i = 1;
            for (INode instanceNode : canonicalSolution.getInstances()) {
                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, false, mappingTask);
                listaTabCanonical.add(viewInstanceTree);
                String tabName = "Canonical Solution: " + i++;
                this.tabbedPaneTarget.addTab(tabName, viewInstanceTree);
            }
            this.ripulitoCanonical = false;
        }
    }

// DEPRECATO dopo l'introduzione del singolo exchange
//    public void createIntermediateInstanceTree() {
//        executeInjection();
//        MappingTask mappingTask = scenario.getMappingTask();
//        if (mappingTask.getMappingData().hasDoubleExchange()) {
//            DataSource target = mappingTask.getMappingData().getSolution();
//            int i = 1;
//            for (INode instanceNode : target.getIntermediateInstances()) {
//                InstanceTreeTabbedPane viewInstanceTree = new InstanceTreeTabbedPane(instanceNode, false, mappingTask);
//                listaTabIntermediate.add(viewInstanceTree);
//                String tabName = "Intermediate Solution: " + i++;
//                this.tabbedPaneTarget.addTab(tabName, viewInstanceTree);
//            }
//            this.ripulitoIntermediate = false;
//        }
//    }
    private String findTitle(String absoluteTitle) {
        int start = absoluteTitle.lastIndexOf("\\");
        int end = absoluteTitle.length();
        return absoluteTitle.substring(++start, end);
    }

    public void clearSource() {
        this.tabbedPaneSource.removeAll();
        this.ripulito = true;
    }

    public void clearTarget() {
        this.tabbedPaneTarget.removeAll();
        this.ripulito = true;
    }

    public void clearTranslated() {
        for (JComponent jComponent : listaTabTranslated) {
            this.tabbedPaneTarget.remove(jComponent);
        }
        this.listaTabTranslated = new ArrayList<JComponent>();
        this.ripulitoTranslated = true;
        clearCanonical();
        clearIntermediate();
    }

    private void clearCanonical() {
        for (JComponent jComponent : listaTabCanonical) {
            this.tabbedPaneTarget.remove(jComponent);
        }
        this.listaTabCanonical = new ArrayList<JComponent>();
        this.ripulitoCanonical = true;
    }

    private void clearIntermediate() {
        for (JComponent jComponent : listaTabIntermediate) {
            this.tabbedPaneTarget.remove(jComponent);
        }
        this.listaTabIntermediate = new ArrayList<JComponent>();
        this.ripulitoIntermediate = true;
    }

    public void clear() {
        this.tabbedPaneSource.removeAll();
        this.tabbedPaneTarget.removeAll();
        this.ripulito = true;
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPaneInstances = new javax.swing.JSplitPane();
        tabbedPaneSource = new javax.swing.JTabbedPane();
        tabbedPaneTarget = new javax.swing.JTabbedPane();

        splitPaneInstances.setDividerLocation(0.5);
        splitPaneInstances.setLeftComponent(tabbedPaneSource);
        splitPaneInstances.setRightComponent(tabbedPaneTarget);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneInstances, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPaneInstances, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPaneInstances;
    private javax.swing.JTabbedPane tabbedPaneSource;
    private javax.swing.JTabbedPane tabbedPaneTarget;
    // End of variables declaration//GEN-END:variables

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

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public boolean isRipulito() {
        return ripulito;
    }

    public boolean isRipulitoTranslated() {
        return ripulitoTranslated;
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setSplitPaneDivider() {
        splitPaneInstances.setDividerLocation(0.5);
    }
}
