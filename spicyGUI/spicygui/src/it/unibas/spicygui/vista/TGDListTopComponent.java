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


import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.TGDRenaming;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import java.awt.Image;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class TGDListTopComponent extends TopComponent {

    private static final String PREFERRED_ID = "TransformationTopComponent";
    private static Log logger = LogFactory.getLog(TGDListTopComponent.class);
    private Scenario scenario;

    public TGDListTopComponent(Scenario scenario) {
        this.scenario = scenario;
        initComponents();
        this.close();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_TGD_LIST_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_TGD_LIST_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW_TGD, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
            if (!tgdsEquals()) {
                NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.REFRESH_TGD), DialogDescriptor.YES_NO_OPTION);
                DialogDisplayer.getDefault().notify(notifyDescriptor);
                if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
                    clear();
                    createTGD();
                }
            }
    }

    public void createTGD() throws IllegalMappingTaskException {
        MappingTask mappingTask = scenario.getMappingTask();
        List<FORule> foRules = mappingTask.getMappingData().getSTTgds();
        int i = 1;
        for (FORule foRule : foRules) {
            TGDTabbedPane tGDTabbedPane = new TGDTabbedPane(foRule, scenario, mappingTask);
            this.tabbedPaneTGDs.addTab("TGD - FORule " + i++, tGDTabbedPane);
        }
    }

    public void clear() {
        this.tabbedPaneTGDs.removeAll();
    }

    @Override
    public void open() {
        if (!scenario.getMappingTask().getMappingData().getSTTgds().isEmpty()) {
            scenario.setSelectedFORule(scenario.getMappingTask().getMappingData().getSTTgds().get(0));
        }
        Mode mode = WindowManager.getDefault().findMode("output");
        if (mode.canDock(this)) {
            mode.dockInto(this);
        }
        super.open();
    }

    

    private boolean tgdsEquals() {
        MappingTask mappingTask = scenario.getMappingTask();
        List<FORule> foRules = mappingTask.getMappingData().getSTTgds();
        if (!(foRules.size() == this.tabbedPaneTGDs.getTabCount())) {
            return false;
        }
        for (int i = 0; i < foRules.size(); i++) {
            FORule foRule = foRules.get(i);
            TGDTabbedPane tGDTabbedPane = (TGDTabbedPane) this.tabbedPaneTGDs.getComponentAt(i);
            if (tGDTabbedPane == null) {
                return true;
            }
            if (!foRule.equals(tGDTabbedPane.getFORule())) {
                return false;
            }
        }
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPaneTGDs = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneTGDs, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPaneTGDs, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPaneTGDs;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public Scenario getScenario() {
        return scenario;
    }
}
