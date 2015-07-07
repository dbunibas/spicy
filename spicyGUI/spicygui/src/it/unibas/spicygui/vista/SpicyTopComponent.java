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
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberi;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetCorrespondencesSpicy;
import it.unibas.spicygui.controllo.provider.MyPopupSceneMatcher;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public final class SpicyTopComponent extends TopComponent {

    private static final Log logger = LogFactory.getLog(SpicyTopComponent.class);
    private static final String PREFERRED_ID = "SpicyTopComponent";
    private JLayeredPaneCorrespondences jLayeredPane;
    private Modello modello;
    private Scenario scenario;
    private MyPopupSceneMatcher myPopupScenematcher;

    public SpicyTopComponent(Scenario scenrario, Image image) {
        this.scenario = scenrario;
        executeInjection();
        createComponents(image);
        initComponentListener();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    ///////////////////////    INIZIALIZZAZIONE  ///////////////////////////////////
    private void createComponents(Image imageNumber) {
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_SPICY_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_SPICY, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, imageNumber, Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
//                ImageIconUIResource i = new  ImageIconUIResource(imageDefault);
        this.setLayout(new BorderLayout());
        this.jLayeredPane = new JLayeredPaneCorrespondences(this.scenario);
        this.add(jLayeredPane, BorderLayout.CENTER);
    }

    ////////////////////////////    ANALISI    /////////////////////////// 
    public void drawScene() {
        this.myPopupScenematcher = new MyPopupSceneMatcher(jLayeredPane.getGlassPane());
        jLayeredPane.drawScene(new CreaWidgetAlberi(jLayeredPane, CreaWidgetAlberi.SPICY_TYPE), new CreaWidgetCorrespondencesSpicy(jLayeredPane));
        jLayeredPane.createSchemaMatcherZonePopUp(myPopupScenematcher);
        jLayeredPane.initTreeSelectionListnerMatcher();
        initNameTab();

    }

    public void drawConnections() {
        jLayeredPane.createConnectionWidgets(new CreaWidgetCorrespondencesSpicy(jLayeredPane));
    }

    public void initNameTab() {
        File fileSalvato = (File) modello.getBean("actual_save_file");
        if (fileSalvato != null) {
            this.setName("Spicy - " + fileSalvato.getName());
        } else {
            this.setName("Spicy");
        }
    }

// <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
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

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    // </editor-fold> 
    public JLayeredPaneCorrespondences getJLayeredPane() {
        return this.jLayeredPane;
    }

    public void clear() {
        this.jLayeredPane.clear();
        this.close();
    }

    /////////////////////////     LISTENER       /////////////////////////////////
    @Override
    public boolean canClose() {
        scenario.getBestMappingsTopComponent().close();
        return true;
    }

    private void initComponentListener() {
        this.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                jLayeredPane.getGlassPane().updateUI();
            }

            public void componentMoved(ComponentEvent e) {
                jLayeredPane.getGlassPane().updateUI();
            }

            public void componentShown(ComponentEvent e) {
                if (!jLayeredPane.isAnalizzato()) {
                    drawScene();
                    jLayeredPane.moveToFront();
                }
                jLayeredPane.getGlassPane().updateUI();
                modello.putBean(Costanti.MAPPINGTASK_SHOWED, scenario.getMappingTask());
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    public void resetSlider() {
        this.myPopupScenematcher.resetSlider();
    }

    public void creaSlider() {
        this.myPopupScenematcher.creaSliderIniziale();
    }
}
