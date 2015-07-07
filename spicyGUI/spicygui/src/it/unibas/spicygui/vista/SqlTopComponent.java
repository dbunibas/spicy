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
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import java.awt.Image;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public final class SqlTopComponent extends TopComponent {

    private static final String PREFERRED_ID = "SqlTopComponent";
    private Scenario scenario;
    private Modello modello;

    public SqlTopComponent(Scenario scenario) {
        this.scenario = scenario;
        initComponents();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_GENERATE_SQL_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_GENERATE_SQL_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_GENERATE_QUERY, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));

    }

    public void createQueryTabs() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        String query = mappingTask.getMappingData().getSQLScript();
        QueryTabbedPane xQueryTabbedPane = new QueryTabbedPane(query, scenario, false);
        this.tabbedSql.addTab("Sql Script", xQueryTabbedPane);
    }

    public void clear() {
        this.tabbedSql.removeAll();
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
        if (!sqlEquals()) {
            NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.REFRESH_SQL), DialogDescriptor.YES_NO_OPTION);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
                clear();
                createQueryTabs();
            }
        }
    }

    private boolean sqlEquals() {
        MappingTask mappingTask = scenario.getMappingTask();
        String newQuery = mappingTask.getMappingData().getSQLScript();
        if(this.tabbedSql.getTabCount() <= 0) {
            return false;
        }
        QueryTabbedPane queryTabbedPane = (QueryTabbedPane) this.tabbedSql.getComponentAt(0);
        return newQuery.equalsIgnoreCase(queryTabbedPane.getQuery());
    }

// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedSql = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedSql, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedSql, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedSql;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
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
