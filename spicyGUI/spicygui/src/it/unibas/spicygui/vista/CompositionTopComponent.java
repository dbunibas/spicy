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
import it.unibas.spicygui.controllo.composition.operators.CreateWidgetAnalyzeComposition;
import it.unibas.spicygui.controllo.file.ActionOpenMappingTask;
import it.unibas.spicygui.controllo.provider.composition.MyPopupProviderCompositionZone;
import it.unibas.spicygui.vista.dnd.TreeDropTarget;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.ImageUtilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.openide.util.ImageUtilities;
import org.openide.windows.Mode;

//@ConvertAsProperties(dtd = "-//it.unibas.spicygui.vista//Composition//EN", autostore = false)
public final class CompositionTopComponent extends TopComponent {

    private static CompositionTopComponent instance;
    private Scenario scenario;
    private GraphSceneGlassPane glassPane;
    private ActionOpenMappingTask actionOpenMappingTask;
    private Modello modello;
    private boolean analizzato;
    private CreateWidgetAnalyzeComposition createWidgetAnalyzeComposition = new CreateWidgetAnalyzeComposition();
    private static final String PREFERRED_ID = "CompositionTopComponent";

    public CompositionTopComponent(Scenario scenario) {
        this.scenario = scenario;
//        executeInjection();
        createComponents();
        initComponentListener();
        //initComponents();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_COMPOSITION_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_COMPOSITION_TOP_COMPONENT_TOOLTIP));


        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW_COMPOSITION, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
         new TreeDropTarget(this.glassPane);
    }

    ///////////////////////    INIZIALIZZAZIONE  ///////////////////////////////////
    private void createComponents() {
        JScrollPane shapePane = new javax.swing.JScrollPane();
        this.setLayout(new BorderLayout());
        add(shapePane, java.awt.BorderLayout.CENTER);
        this.glassPane = new GraphSceneGlassPane();

        this.glassPane.setOpaque(false);
        this.glassPane.getScene().setOpaque(true);
        this.setOpaque(false);
        this.glassPane.getScene().getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderCompositionZone(this.glassPane)));

        JComponent myView = this.glassPane.getView();

        shapePane.setViewportView(myView);
//        myView.setOpaque(true);
        this.add(this.glassPane.getScene().createSatelliteView(), BorderLayout.WEST);


    }

    private void initComponentListener() {
        this.addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                glassPane.updateUI();
            }

            public void componentMoved(ComponentEvent e) {
                glassPane.updateUI();
            }

            public void componentShown(ComponentEvent e) {
                if (!analizzato) {
                    draw();
                }
//                jLayeredPane.getGlassPane().updateUI();
//                modello.putBean(Costanti.MAPPINGTASK_SHOWED, scenario.getMappingTask());
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    private void draw() {
        analizzato = true;
        createWidgetAnalyzeComposition.analyzeComposition(glassPane, scenario);
    }

    public GraphSceneGlassPane getGlassPane() {
        return glassPane;
    }

    @Override
    public void open() {
        Mode mode = WindowManager.getDefault().findMode("output");
        if (mode.canDock(this)) {
            mode.dockInto(this);
        }
        super.open();
    }

    public Scenario getScenario() {
        return scenario;
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
//    public static synchronized CompositionTopComponent getDefault() {
//        if (instance == null) {
//            instance = new CompositionTopComponent();
//        }
//        return instance;
//    }
//
//    public static synchronized CompositionTopComponent findInstance() {
//        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
//        if (win == null) {
//            Logger.getLogger(CompositionTopComponent.class.getName()).warning(
//                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
//            return getDefault();
//        }
//        if (win instanceof CompositionTopComponent) {
//            return (CompositionTopComponent) win;
//        }
//        Logger.getLogger(CompositionTopComponent.class.getName()).warning(
//                "There seem to be multiple components with the '" + PREFERRED_ID
//                + "' ID. That is a potential source of errors and unexpected behavior.");
//        return getDefault();
//    }
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

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void validateScene() {
        this.glassPane.getScene().revalidate();
    }
}
