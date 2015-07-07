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
 
package it.unibas.spicygui.vista.wizard;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import it.unibas.spicygui.vista.wizard.pm.NewMappingTaskPM;
import it.unibas.spicygui.vista.wizard.pm.XMLConfigurationPM;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.openide.WizardDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class PanelWizardImpl extends javax.swing.JPanel {

    private Log logger = LogFactory.getLog(PanelWizardImpl.class);
    private RelationalPanel relationalPanel = new RelationalPanel(this);
    private XMLPanel xmlPanel = new XMLPanel(this);
    private String dataSource;
    private NewMappingTaskPM newMappingTaskPM;
    private WizardDescriptor.Panel wizardDescriptor;
    private Modello modello;

    public PanelWizardImpl(String dataSource, WizardDescriptor.Panel wizardDescriptor) {
        this.dataSource = dataSource;
        this.wizardDescriptor = wizardDescriptor;
        executeIjection();
        initComponents();
        initI18N();
        initBinding();
        initListener();
    }

    private void initBinding() {
        String elBinding = "";
        if (dataSource.equalsIgnoreCase(Costanti.SOURCE)) {
            elBinding = "${modello.mappa['NEW_MAPPING_TASK_PM'].sourceElement}";
        } else {
            elBinding = "${modello.mappa['NEW_MAPPING_TASK_PM'].targetElement}";
        }
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create(elBinding), comboDataSourceType, BeanProperty.create("selectedItem"), "combo type datasource");
        binding.bind();

    }

    private void initI18N() {
        this.comboDataSourceType.removeAllItems();
        this.comboDataSourceType.addItem(NbBundle.getMessage(Costanti.class, Costanti.DATASOURCE_TYPE_XML));
        this.comboDataSourceType.addItem(NbBundle.getMessage(Costanti.class, Costanti.DATASOURCE_TYPE_RELATIONAL));
        this.labelDataSourceType.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_DATA_SOURCE_TYPE));
        this.createXMLPanel();
    }

    public void createRelationalPanel() {
        this.panelOptions.removeAll();
        if (dataSource.equalsIgnoreCase(Costanti.SOURCE)) {
            relationalPanel.setRelationalConfigurationPM((RelationalConfigurationPM) modello.getBean(Costanti.RELATIONAL_CONFIGURATION_SOURCE));
            relationalPanel.ripulisciCampi();
            this.panelOptions.add(relationalPanel, BorderLayout.CENTER);
        } else {
            relationalPanel.setRelationalConfigurationPM((RelationalConfigurationPM) modello.getBean(Costanti.RELATIONAL_CONFIGURATION_TARGET));
            relationalPanel.ripulisciCampi();
            this.panelOptions.add(relationalPanel, BorderLayout.CENTER);
        }
    }

    public void createXMLPanel() {
        this.panelOptions.removeAll();
        if (dataSource.equalsIgnoreCase(Costanti.SOURCE)) {
            xmlPanel.setXmlConfigurationPM((XMLConfigurationPM) modello.getBean(Costanti.XML_CONFIGURATION_SOURCE));
            xmlPanel.ripulisciCampi();
            this.panelOptions.add(xmlPanel, BorderLayout.CENTER);
        } else {
            xmlPanel.setXmlConfigurationPM((XMLConfigurationPM) modello.getBean(Costanti.XML_CONFIGURATION_TARGET));
            xmlPanel.ripulisciCampi();
            this.panelOptions.add(xmlPanel, BorderLayout.CENTER);
        }
    }

    public NewMappingTaskPM getNewMappingTaskPM() {
        return newMappingTaskPM;
    }

    private void executeIjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public void fireChangeEvent() {
        //TODO cambiare il metodo
        this.wizardDescriptor.readSettings(null);
    }

    public Modello getModello() {
        return modello;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelDataSource = new javax.swing.JLabel();
        comboDataSourceType = new javax.swing.JComboBox();
        labelDataSourceType = new javax.swing.JLabel();
        panelOptions = new javax.swing.JPanel();

        labelDataSource.setText("");

        comboDataSourceType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        labelDataSourceType.setText("");

        panelOptions.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOptions, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelDataSourceType)
                        .addGap(18, 18, 18)
                        .addComponent(comboDataSourceType, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelDataSource))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDataSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDataSourceType)
                    .addComponent(comboDataSourceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(panelOptions, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboDataSourceType;
    private javax.swing.JLabel labelDataSource;
    private javax.swing.JLabel labelDataSourceType;
    private javax.swing.JPanel panelOptions;
    // End of variables declaration//GEN-END:variables
    private void initListener() {
        this.comboDataSourceType.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String type = (String) comboDataSourceType.getSelectedItem();
                if (type.equals(NbBundle.getMessage(Costanti.class, Costanti.DATASOURCE_TYPE_RELATIONAL))) {
                    createRelationalPanel();
                } else if (type.equals(NbBundle.getMessage(Costanti.class, Costanti.DATASOURCE_TYPE_XML))) {
                    createXMLPanel();
                }
                updateUI();
            }
        });
    }
}
