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
 
package it.unibas.spicygui.vista.sql;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import org.openide.util.NbBundle;

public class PanelSupportAccess extends javax.swing.JPanel {

   private RelationalConfigurationPM relationalConfigurationPM;

    public PanelSupportAccess() {
        this.relationalConfigurationPM = new RelationalConfigurationPM();
        initComponents();
        initI18NComboBox();
        initI18NLabel();
    }

    public RelationalConfigurationPM getRelationalConfigurationPM() {
        return relationalConfigurationPM;
    }


    private void initI18NComboBox() {
        this.comboDriver.removeAllItems();
        this.comboDriver.addItem("org.postgresql.Driver");
        this.comboDriver.addItem("com.mysql.jdbc.Driver");
        this.comboDriver.addItem("org.apache.derby.jdbc.ClientDriver");
    }

    private void initI18NLabel() {
        this.labelDriver.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_DRIVER_SUPPORT));
        this.labelURI.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_URI_SUPPORT));
        this.labelUserName.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_USER_NAME_SUPPORT));
        this.labelPassword.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_PASSWORD_SUPPORT));
        this.textURI.setText(NbBundle.getMessage(Costanti.class, Costanti.TEXT_URI));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        labelDriver = new javax.swing.JLabel();
        labelURI = new javax.swing.JLabel();
        textURI = new javax.swing.JTextField();
        textPassword = new javax.swing.JTextField();
        labelPassword = new javax.swing.JLabel();
        textUserName = new javax.swing.JTextField();
        labelUserName = new javax.swing.JLabel();
        comboDriver = new javax.swing.JComboBox();

        labelDriver.setText("");

        labelURI.setText("");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.uri}"), textURI, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.password}"), textPassword, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelPassword.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.login}"), textUserName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelUserName.setText("");

        comboDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.driver}"), comboDriver, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(textUserName, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(comboDriver, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelDriver)
                    .addComponent(labelUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(textURI, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(labelPassword)
                    .addComponent(labelURI)
                    .addComponent(textPassword)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelDriver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textURI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelURI)
                        .addGap(26, 26, 26)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelUserName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelPassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
    private javax.swing.JTextField textPassword;
    private javax.swing.JTextField textURI;
    private javax.swing.JTextField textUserName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
