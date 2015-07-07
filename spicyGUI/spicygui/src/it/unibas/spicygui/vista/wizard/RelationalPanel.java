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
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;

public class RelationalPanel extends javax.swing.JPanel {

    private static Log logger = LogFactory.getLog(RelationalPanel.class);
    private RelationalConfigurationPM relationalConfigurationPM;
    private PropertyChangeListener relationalPropertyChangeListener;
    private PanelWizardImpl pannPanelWizardImpl;

    public RelationalPanel(RelationalConfigurationPM relationalConfigurationPM) {
        this.relationalConfigurationPM = relationalConfigurationPM;
        initComponents();
        initI18NComboBox();
        initI18NLabel();
    }

    public RelationalPanel(PanelWizardImpl pannePanelWizardImpl) {
        this.pannPanelWizardImpl = pannePanelWizardImpl;
        initComponents();
        initI18NComboBox();
        initI18NLabel();
    }

    private void initI18NComboBox() {
        this.comboDriver.removeAllItems();
        this.comboDriver.addItem("org.postgresql.Driver");
        this.comboDriver.addItem("com.mysql.jdbc.Driver");
        this.comboDriver.addItem("org.apache.derby.jdbc.ClientDriver");
    }

    private void initI18NLabel() {
        this.labelDriver.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_DRIVER));
        this.labelURI.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_URI));
        this.labelUserName.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_USER_NAME));
        this.labelPassword.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_PASSWORD));
        this.textURI.setText(NbBundle.getMessage(Costanti.class, Costanti.TEXT_URI));
    }

    public RelationalConfigurationPM getRelationalConfigurationPM() {
        return relationalConfigurationPM;
    }

    public void setRelationalConfigurationPM(RelationalConfigurationPM relationalConfigurationPM) {
        this.relationalConfigurationPM = relationalConfigurationPM;
        removeListener();
        initListener();
        ripulisciCampi();
    }

    public void initListener() {
        this.relationalPropertyChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                campoIstanzeTargetPropertyChange(evt);
            }

            private void campoIstanzeTargetPropertyChange(PropertyChangeEvent evt) {
                pannPanelWizardImpl.fireChangeEvent();
            }
        };
        relationalConfigurationPM.addPropertyChangeListener(this.relationalPropertyChangeListener);
    }

    public void removeListener() {
        if (this.relationalPropertyChangeListener != null) {
            relationalConfigurationPM.removePropertyChangeListener(this.relationalPropertyChangeListener);
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
        labelDriver = new javax.swing.JLabel();
        comboDriver = new javax.swing.JComboBox();
        labelURI = new javax.swing.JLabel();
        labelUserName = new javax.swing.JLabel();
        labelPassword = new javax.swing.JLabel();
        textURI = new javax.swing.JTextField();
        textLogin = new javax.swing.JTextField();
        textPassword = new javax.swing.JPasswordField();

        labelDriver.setText("");

        comboDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.driver}"), comboDriver, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "driver");
        bindingGroup.addBinding(binding);

        labelURI.setText("");

        labelUserName.setText("");

        labelPassword.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.uri}"), textURI, org.jdesktop.beansbinding.BeanProperty.create("text"), "Uri");
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.login}"), textLogin, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.password}"), textPassword, org.jdesktop.beansbinding.BeanProperty.create("text"), "password");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDriver)
                    .addComponent(labelURI)
                    .addComponent(labelPassword)
                    .addComponent(labelUserName))
                .addGap(87, 87, 87)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(textPassword)
                    .addComponent(textLogin)
                    .addComponent(textURI)
                    .addComponent(comboDriver, 0, 206, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDriver)
                    .addComponent(comboDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelURI)
                    .addComponent(textURI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelUserName)
                    .addComponent(textLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPassword)
                    .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboDriver;
    private javax.swing.JLabel labelDriver;
    private javax.swing.JLabel labelPassword;
    private javax.swing.JLabel labelURI;
    private javax.swing.JLabel labelUserName;
    private javax.swing.JTextField textLogin;
    private javax.swing.JPasswordField textPassword;
    private javax.swing.JTextField textURI;
    private it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto validatoreCampoTesto;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
