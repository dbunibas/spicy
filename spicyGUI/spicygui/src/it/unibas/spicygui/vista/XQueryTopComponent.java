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

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import java.awt.Image;
import java.util.List;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public final class XQueryTopComponent extends TopComponent {

    private Modello modello;
    private Scenario scenario;
    private static final String PREFERRED_ID = "XQueryTopComponent";

    public XQueryTopComponent(Scenario scenario) {
        this.scenario = scenario;
        initComponents();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_GENERATE_XQUERY_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_GENERATE_XQUERY_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_GENERATE_QUERY, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
    }

    public void createXQueryTabs() {

        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        String query = getPercorso(mappingTask);
        QueryTabbedPane xQueryTabbedPane = new QueryTabbedPane(query, scenario, true);
        this.tabbedXQuery.addTab("XQuery Script", xQueryTabbedPane);
    }

    public void clear() {
        this.tabbedXQuery.removeAll();
    }

    public void update() {
        if (this.tabbedXQuery.getTabCount() > 0) {
            QueryTabbedPane queryTabbedPane = (QueryTabbedPane) this.tabbedXQuery.getComponentAt(0);
            queryTabbedPane.creaPopUp();
        }
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Scenario getScenario() {
        return scenario;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        if (!xQueryEquals()) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.REFRESH_XQUERY), DialogDescriptor.YES_NO_OPTION);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
                clear();
                createXQueryTabs();
            }
        }
    }

    private String getPercorso(MappingTask mappingTask) {
        String percorso = "";
        if (mappingTask.getSourceProxy().getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_XML)) {
            List<String> sourceInstancesName = (List<String>) mappingTask.getSourceProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
            percorso = sourceInstancesName.get(sourceInstancesName.size() - 1);
            percorso = percorso.replace("\\", "/");
        }
        String query = mappingTask.getMappingData().getXQueryScript(percorso);
        return query;
    }

    private boolean xQueryEquals() {
        MappingTask mappingTask = scenario.getMappingTask();

//        List<String> sourceInstancesName = (List<String>) mappingTask.getSource().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
//        String percorso = sourceInstancesName.get(sourceInstancesName.size() - 1);
//        percorso = percorso.replace("\\", "/");
//        String newQuery =  mappingTask.getMappingData().getXQueryScript(percorso);
        String newQuery = getPercorso(mappingTask);
        if (this.tabbedXQuery.getTabCount() <= 0) {
            return false;
        }
        QueryTabbedPane queryTabbedPane = (QueryTabbedPane) this.tabbedXQuery.getComponentAt(0);
        return newQuery.equalsIgnoreCase(queryTabbedPane.getQuery());
    }

// <editor-fold defaultstate="collapsed" desc=" Generated Code ">    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedXQuery = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedXQuery, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedXQuery, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedXQuery;
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
    // </editor-fold>   
}
