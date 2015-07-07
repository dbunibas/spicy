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

import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.Transformation;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.spicy.ActionShowSelectedTransformation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class RankedTransformationsTopComponent extends TopComponent {

    private static Log logger = LogFactory.getLog(RankedTransformationsTopComponent.class);
    private static RankedTransformationsTopComponent instance;
    private JTable tabellaRankedTransformations;
    private JTextArea messageLabel;
    private JScrollPane jScrollPane;
    private ActionShowSelectedTransformation showTransformationAction = new ActionShowSelectedTransformation();
    private Modello modello;
    private Scenario scenario;
    private static final String PREFERRED_ID = "RankedTransformationsTopComponent";

    public RankedTransformationsTopComponent(Scenario scenrario, Image image) {
        this.scenario = scenrario;
        initComponent();
        executeInjection();
        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_RANKED_TRANSFORMATIONS_TOP_COMPONENT_TOOLTIP));
        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW_RANKED_TRANSFORMATIONS, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, image, Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    private void initComponent() {
        messageLabel = new JTextArea();
        messageLabel.setRows(2);
        messageLabel.setEditable(false);
        messageLabel.setBackground(this.getBackground());
        tabellaRankedTransformations = new JTable();
        jScrollPane = new JScrollPane();
        jScrollPane.setPreferredSize(new DimensionUIResource(150, this.getHeight()));
        this.setPreferredSize(new Dimension(150, this.getHeight()));
        this.setLayout(new BorderLayout());
        this.add(messageLabel, BorderLayout.NORTH);
        this.add(jScrollPane, BorderLayout.CENTER);
        addSelectionListener();
    }

    public void open() {
        Mode mode = WindowManager.getDefault().findMode("leftSlidingSide");
        if (mode != null) {
            mode.dockInto(this);
        }
        Mode modeRuntime = WindowManager.getDefault().findMode("explorer");
        if (modeRuntime != null) {
            modeRuntime.dockInto(this);
        }

        super.open();
    }

    private void addSelectionListener() {
        tabellaRankedTransformations.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                showTransformationAction.actionPerformedWithScenario(scenario);
            }
        });
    }

    public JTable getTabellaRankedTransformations() {
        return tabellaRankedTransformations;
    }

    public void updateTable() {
        List<Transformation> rankedTransformations = (List<Transformation>) modello.getBean(Costanti.RANKED_TRANSFORMATIONS);
        if (rankedTransformations != null && rankedTransformations.size() > 0) {
            tabellaRankedTransformations.setModel(new RankedTransformationTableModel());
            tabellaRankedTransformations.getSelectionModel().clearSelection();
            jScrollPane.setViewportView(tabellaRankedTransformations);
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_RANKED_TRANSFORMATIONS));
        } else {
            jScrollPane.getViewport().remove(tabellaRankedTransformations);
        }
    }
// <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
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
}

class RankedTransformationTableModel extends AbstractTableModel {

    private Modello modello;
    private static Log logger = LogFactory.getLog(RankedTransformationTableModel.class);

    public RankedTransformationTableModel() {
        executeInjection();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public int getRowCount() {
        List<Transformation> rankedTransformations = (List<Transformation>) modello.getBean(Costanti.RANKED_TRANSFORMATIONS);
        if (rankedTransformations != null) {
            return rankedTransformations.size();
        }
        return 0;
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return NbBundle.getMessage(Costanti.class, Costanti.RANK);
        }
        return NbBundle.getMessage(Costanti.class, Costanti.QUALITY);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Transformation> rankedTransformations = (List<Transformation>) modello.getBean(Costanti.RANKED_TRANSFORMATIONS);
        if (rankedTransformations != null) {
            Transformation transformation = rankedTransformations.get(rowIndex);
            if (columnIndex == 0) {
                return NbBundle.getMessage(Costanti.class, Costanti.TRANSFORMATION) + " n." + transformation.getId();
            } else if (columnIndex == 1) {
                SimilarityCheck similarityCheck = (SimilarityCheck) transformation.getAnnotation(SpicyConstants.SIMILARITY_CHECK);
                double quality = similarityCheck.getQualityMeasure();
                DecimalFormat formatter = new DecimalFormat("#.####");
                return formatter.format(quality);
            }
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return String.class;
        }
        return Number.class;
    }
}
