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

import it.unibas.spicy.AnnotatedMappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.spicy.ActionShowSelectedMappingTask;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

public final class BestMappingsTopComponent extends TopComponent {

    private static Log logger = LogFactory.getLog(BestMappingsTopComponent.class);
    private static final String PREFERRED_ID = "BestMappingsTopComponent";
    private JTable tabellaBestMappings;
    private JTextArea messageLabel;
    private JScrollPane jScrollPane;
    private ActionShowSelectedMappingTask showAction = new ActionShowSelectedMappingTask();
    private Modello modello;
    private Scenario scenario;

    public BestMappingsTopComponent(Scenario scenrario, Image image) {
        this.scenario = scenrario;
        executeInjection();
        initComponent(image);
//        initComponentListener();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    private void initComponent(Image imageNumber) {

        setName(NbBundle.getMessage(Costanti.class, Costanti.VIEW_BEST_MAPPINGS_TOP_COMPONENT));
        setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.VIEW_BEST_MAPPINGS_TOP_COMPONENT_TOOLTIP));

        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_VIEW_BEST_MAPPINGS, true);
        this.setIcon(ImageUtilities.mergeImages(imageDefault, imageNumber, Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));

        messageLabel = new JTextArea();
        messageLabel.setRows(2);
        messageLabel.setEditable(false);
        messageLabel.setBackground(this.getBackground());
        tabellaBestMappings = new JTable();
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
        tabellaBestMappings.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                showAction.actionPerformedWithScenario(scenario);
            }
        });
    }

    public JTable getTabellaBestMappings() {
        return tabellaBestMappings;
    }

    public void updateTable() {
        List<AnnotatedMappingTask> bestMappingTasks = (List<AnnotatedMappingTask>) modello.getBean(Costanti.BEST_MAPPING_TASKS);
        if (bestMappingTasks != null && bestMappingTasks.size() > 0) {
            tabellaBestMappings.setModel(new BestMappingsTableModel());
            tabellaBestMappings.getSelectionModel().clearSelection();
            jScrollPane.setViewportView(tabellaBestMappings);
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_BEST_MAPPINGS));
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_NO_BEST_MAPPINGS));
            jScrollPane.getViewport().remove(tabellaBestMappings);
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

//    private void initComponentListener() {
//        this.addComponentListener(new ComponentListener() {
//
//            public void componentResized(ComponentEvent e) {
//            }
//
//            public void componentMoved(ComponentEvent e) {
//            }
//
//            public void componentShown(ComponentEvent e) {
//                modello.putBean(Costanti.MAPPINGTASK_SHOWED, scenario.getMappingTask());
//            }
//
//            public void componentHidden(ComponentEvent e) {
//            }
//        });
//    }
}

class BestMappingsTableModel extends AbstractTableModel {

    private Modello modello;
    private static Log logger = LogFactory.getLog(BestMappingsTableModel.class);

    public BestMappingsTableModel() {
        executeInjection();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public int getRowCount() {
        List<AnnotatedMappingTask> bestMappingTasks = (List<AnnotatedMappingTask>) modello.getBean(Costanti.BEST_MAPPING_TASKS);
        if (bestMappingTasks != null) {
            return bestMappingTasks.size();
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
        List<AnnotatedMappingTask> bestMappingTasks = (List<AnnotatedMappingTask>) modello.getBean(Costanti.BEST_MAPPING_TASKS);
        if (bestMappingTasks != null) {
            if (columnIndex == 0) {
                return NbBundle.getMessage(Costanti.class, Costanti.SOLUTION) + (rowIndex + 1);
            } else if (columnIndex == 1) {
                AnnotatedMappingTask annotatedMappingTask = bestMappingTasks.get(rowIndex);
                double quality = (Double) annotatedMappingTask.getQualityMeasure();
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
