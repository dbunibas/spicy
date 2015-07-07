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
import it.unibas.spicygui.vista.wizard.pm.XMLConfigurationPM;
import it.unibas.spicygui.controllo.file.ActionFileChooserInstances;
import it.unibas.spicygui.controllo.file.ActionFileChooserSchema;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.lookup.Lookups;

public class XMLPanel extends javax.swing.JPanel {

    private XMLConfigurationPM xmlConfigurationPM;
    private PropertyChangeListener xmlPropertyChangeListener;
    private PanelWizardImpl panelWizardImpl;
    private ActionFileChooserSchema actionFileChooserSS;
    private ActionFileChooserInstances actionFileChooserIS;

    public XMLPanel(PanelWizardImpl panelWizardImpl) {
        this.panelWizardImpl = panelWizardImpl;
        initComponents();
        initLabels();
        initActions();
    }

    private void initButtonBean() {
        this.bottoneChooserSchemaSource.setAction(this.actionFileChooserSS);
        this.bottoneChooserIstanzeSource.setAction(this.actionFileChooserIS);
        actionFileChooserSS.setBean(xmlConfigurationPM);
        actionFileChooserIS.setBean(xmlConfigurationPM);
    }

    private void initLabels() {
        etichettaSchemaSource.setText(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.EtichettaSchemaSource));
        etichettaIstanzeSource.setText(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.EtichettaIstanzeSource));
    }

    private void initActions() {
        this.actionFileChooserSS = new ActionFileChooserSchema();
        this.actionFileChooserIS = new ActionFileChooserInstances();
    }

    public XMLConfigurationPM getXmlConfigurationPM() {
        return xmlConfigurationPM;
    }

    public void setXmlConfigurationPM(XMLConfigurationPM xmlConfigurationPM) {
        this.xmlConfigurationPM = xmlConfigurationPM;
        removeListener();
        initListener();
        initButtonBean();
        ripulisciCampi();
    }

    public void initListener() {
        this.xmlPropertyChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                campoIstanzeTargetPropertyChange(evt);
            }

            private void campoIstanzeTargetPropertyChange(PropertyChangeEvent evt) {
                panelWizardImpl.fireChangeEvent();
            }
        };
        xmlConfigurationPM.addPropertyChangeListener(this.xmlPropertyChangeListener);
    }

    public void removeListener() {
        if (this.xmlPropertyChangeListener != null) {
            xmlConfigurationPM.removePropertyChangeListener(this.xmlPropertyChangeListener);
        }
    }

    public void ripulisciCampi() {
        bindingGroup.unbind();
        bindingGroup.bind();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        validatoreCampoTesto = new it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto();
        etichettaSchemaSource = new javax.swing.JLabel();
        campoSchemaSource = new javax.swing.JTextField();
        etichettaIstanzeSource = new javax.swing.JLabel();
        campoIstanzeSource = new javax.swing.JTextField();
        bottoneChooserSchemaSource = new javax.swing.JButton();
        bottoneChooserIstanzeSource = new javax.swing.JButton();

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xmlConfigurationPM.schemaPath}"), campoSchemaSource, org.jdesktop.beansbinding.BeanProperty.create("text"), "schemaPath");
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xmlConfigurationPM.instancePath}"), campoIstanzeSource, org.jdesktop.beansbinding.BeanProperty.create("text"), "instancePath");
        bindingGroup.addBinding(binding);

        bottoneChooserSchemaSource.setText("...");

        bottoneChooserIstanzeSource.setText("...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(etichettaSchemaSource)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(campoSchemaSource, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addComponent(campoIstanzeSource, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bottoneChooserSchemaSource, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bottoneChooserIstanzeSource))))
                .addGap(37, 37, 37))
            .addGroup(layout.createSequentialGroup()
                .addComponent(etichettaIstanzeSource)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(etichettaSchemaSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoSchemaSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bottoneChooserSchemaSource))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etichettaIstanzeSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bottoneChooserIstanzeSource)
                    .addComponent(campoIstanzeSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bottoneChooserIstanzeSource;
    private javax.swing.JButton bottoneChooserSchemaSource;
    private javax.swing.JTextField campoIstanzeSource;
    private javax.swing.JTextField campoSchemaSource;
    private javax.swing.JLabel etichettaIstanzeSource;
    private javax.swing.JLabel etichettaSchemaSource;
    private it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto validatoreCampoTesto;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
