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
import it.unibas.spicygui.controllo.tree.ActionDecreaseFont;
import it.unibas.spicygui.controllo.tree.ActionIncreaseFont;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

public final class TransformationTopComponent extends TopComponent {

    private static final String PREFERRED_ID = "TransformationTopComponent";
    private static Log logger = LogFactory.getLog(TransformationTopComponent.class);
    private Modello modello;
    private Scenario scenario;
    private boolean ripulito = true;
    private JPopupMenu popUpMenu;

    public TransformationTopComponent(Scenario scenario) {
        this.scenario = scenario;
        initComponents();
        this.close();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_TRANSFORMATIONS_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW_TRASFORMATION, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));

        creaPopUp();
    }

//    public void createTransformationOutput(List<Integer> listaTGD) {
//        executeInjection();
////        this.setLayout(new BorderLayout());
//        MappingTask mappingTask = scenario.getMappingTask();
//
//        jScrollPane1 = new javax.swing.JScrollPane();
//        JPanel pannelloPrincipale = new JPanel();
//        pannelloPrincipale.setLayout(new javax.swing.BoxLayout(pannelloPrincipale, javax.swing.BoxLayout.Y_AXIS));
//        textAreaTrasformation = new javax.swing.JTextArea();
//        textAreaTrasformation.setColumns(20);
//        textAreaTrasformation.setEditable(false);
//        textAreaTrasformation.setRows(5);
//
//        pannelloPrincipale.add(textAreaTrasformation);
//
//
//        jScrollPane1.setViewportView(pannelloPrincipale);
//
//        textAreaTrasformation.setText(mappingTask.getMappingData().toLongString().substring(0, listaTGD.get(0)));
//        for (int i = 0; i < listaTGD.size(); i++) {
//            JPanel pannelloInterno = new JPanel();
//            pannelloInterno.setLayout(new javax.swing.BoxLayout(pannelloInterno, javax.swing.BoxLayout.X_AXIS));
//            pannelloInterno.setBackground(Color.WHITE);
//            Checkbox checkbox = new Checkbox("prova");
//            JTextArea textAreaInterno = new JTextArea();
//            if ((i + 1) < listaTGD.size()) {
//                int inizio = listaTGD.get(i);
//                int fine = listaTGD.get(i + 1);
//                textAreaInterno.setText(mappingTask.getMappingData().toLongString().substring(inizio, fine));
//            } else {
//                int inizio = listaTGD.get(i);
//                textAreaInterno.setText(mappingTask.getMappingData().toLongString().substring(inizio));
//            }
//            pannelloInterno.add(checkbox);
//            pannelloInterno.add(textAreaInterno);
//            pannelloPrincipale.add(pannelloInterno);
//        }
//
//
//        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
//        this.setLayout(layout);
//        layout.setHorizontalGroup(
//                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
//        layout.setVerticalGroup(
//                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE));
//
//
////        this.add(jScrollPane1, BorderLayout.CENTER);
//        this.ripulito = false;
//    }
    public void createTransformationOutput() {
        executeInjection();
        MappingTask mappingTask = scenario.getMappingTask();
        textAreaTrasformation.setText(mappingTask.getMappingData().toLongString());
        this.ripulito = false;
    }

    public void clear() {
        textAreaTrasformation.setText("");
        this.ripulito = true;
    }

//    public void update() {
//        MappingTaskInfo mappingTaskInfo = (MappingTaskInfo) this.modello.getBean(Costanti.MAPPINGTASK_INFO);
//        if (mappingTaskInfo != null) {
//            textAreaTrasformation.setText(mappingTaskInfo.toString());
//            this.ripulito = false;
//        } else {
//            textAreaTrasformation.setText("");
//            this.ripulito = true;
//        }
//    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaTrasformation = new javax.swing.JTextArea();

        textAreaTrasformation.setColumns(20);
        textAreaTrasformation.setEditable(false);
        textAreaTrasformation.setRows(5);
        jScrollPane1.setViewportView(textAreaTrasformation);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textAreaTrasformation;
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

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Scenario getScenario() {
        return scenario;
    }

    private void creaPopUp() {
        this.popUpMenu = new JPopupMenu();
        this.popUpMenu.add(new ActionIncreaseFont(textAreaTrasformation));
        this.popUpMenu.add(new ActionDecreaseFont(textAreaTrasformation));
        this.textAreaTrasformation.addMouseListener(new PopUpListener());
    }

    class PopUpListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popUpMenu.show(textAreaTrasformation, e.getX(), e.getY());
            }
        }
    }
}
