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
import it.unibas.spicygui.controllo.file.ActionFileChooserExecuteSql;
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import java.awt.BorderLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;

public class SqlDialog extends javax.swing.JDialog {

    private Log logger = LogFactory.getLog(SqlDialog.class);
    private RelationalConfigurationPM relationalConfigurationPM;
//    private PanelSupportAccess panelSupportAccess;
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    public SqlDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setLocationRelativeTo(parent);
        this.relationalConfigurationPM = new RelationalConfigurationPM();
//        this.panelSupportAccess = new PanelSupportAccess();
        this.setTitle(NbBundle.getMessage(Costanti.class, Costanti.SQL_DIALOG));
        initComponents();
//        this.panelSupport.add(this.panelSupportAccess, BorderLayout.CENTER);
//        this.panelSupportAccess.setVisible(checkBoxRecreate.isSelected());
        this.textTmpDatabaseName.setVisible(checkBoxRecreate.isSelected());
        this.labelTmpDatabaseName.setVisible(checkBoxRecreate.isSelected());
        initI18NComboBox();
        initI18NLabel();
        initButtonAction();
        this.pack();
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
        this.labelSchemaSource.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_SCHEMA_SOURCE));
        this.labelInstanceSource.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_INSTANCE_SOURCE));
        this.labelSchemaTarget.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_SCHEMA_TARGET));
        this.labelCheckRecreate.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_CHECK_RECREATE));
        this.labelTmpDatabaseName.setText(NbBundle.getMessage(Costanti.class, Costanti.LABEL_TMP_DATABASE_NAME));
        this.textURI.setText(NbBundle.getMessage(Costanti.class, Costanti.TEXT_URI));
        this.textPassword.setText(NbBundle.getMessage(Costanti.class, Costanti.TEXT_PASSWORD));
        this.textUserName.setText(NbBundle.getMessage(Costanti.class, Costanti.TEXT_USER));
    }

    private void initButtonAction() {
        this.buttonSchemaSource.setAction(new ActionFileChooserExecuteSql(ActionFileChooserExecuteSql.SCHEMA_SOURCE, relationalConfigurationPM));
        this.buttonInstanceSource.setAction(new ActionFileChooserExecuteSql(ActionFileChooserExecuteSql.INSTANCE_SOURCE, relationalConfigurationPM));
        this.buttonSchemaTarget.setAction(new ActionFileChooserExecuteSql(ActionFileChooserExecuteSql.SCHEMA_TARGET, relationalConfigurationPM));
    }

    public RelationalConfigurationPM getRelationalConfigurationPM() {
        return relationalConfigurationPM;
    }

//    public RelationalConfigurationPM getSupportRelationalConfigurationPM() {
//        return this.panelSupportAccess.getRelationalConfigurationPM();
//    }
    public void ripulisciCampi() {
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public boolean isRecreate() {
        return this.checkBoxRecreate.isSelected();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jComboBox1 = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        comboDriver = new javax.swing.JComboBox();
        labelDriver = new javax.swing.JLabel();
        labelUserName = new javax.swing.JLabel();
        textUserName = new javax.swing.JTextField();
        labelURI = new javax.swing.JLabel();
        textURI = new javax.swing.JTextField();
        labelPassword = new javax.swing.JLabel();
        textPassword = new javax.swing.JTextField();
        labelSchemaSource = new javax.swing.JLabel();
        textSchemaSource = new javax.swing.JTextField();
        labelInstanceSource = new javax.swing.JLabel();
        textinstanceSource = new javax.swing.JTextField();
        labelSchemaTarget = new javax.swing.JLabel();
        textSchemaTarget = new javax.swing.JTextField();
        buttonSchemaSource = new javax.swing.JButton();
        buttonInstanceSource = new javax.swing.JButton();
        buttonSchemaTarget = new javax.swing.JButton();
        labelError = new javax.swing.JLabel();
        checkBoxRecreate = new javax.swing.JCheckBox();
        labelCheckRecreate = new javax.swing.JLabel();
        panelSupport = new javax.swing.JPanel();
        textTmpDatabaseName = new javax.swing.JTextField();
        labelTmpDatabaseName = new javax.swing.JLabel();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "Ok_button")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "Cancel_button")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        comboDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.driver}"), comboDriver, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        labelDriver.setText("");

        labelUserName.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.login}"), textUserName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelURI.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.uri}"), textURI, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelPassword.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.password}"), textPassword, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelSchemaSource.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.sourceSchema}"), textSchemaSource, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelInstanceSource.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.sourceInstance}"), textinstanceSource, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelSchemaTarget.setText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.targetSchema}"), textSchemaTarget, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        buttonSchemaSource.setText("...");

        buttonInstanceSource.setText("...");

        buttonSchemaTarget.setText("...");

        labelError.setForeground(new java.awt.Color(255, 0, 0));
        labelError.setText("");

        checkBoxRecreate.setText("");
        checkBoxRecreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxRecreateActionPerformed(evt);
            }
        });

        labelCheckRecreate.setText("");

        panelSupport.setLayout(new java.awt.BorderLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${relationalConfigurationPM.tmpDatabaseName}"), textTmpDatabaseName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        labelTmpDatabaseName.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelSupport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(textUserName, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(comboDriver, javax.swing.GroupLayout.Alignment.LEADING, 0, 249, Short.MAX_VALUE))
                            .addComponent(labelDriver)
                            .addComponent(labelUserName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(textURI, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(labelPassword)
                            .addComponent(labelURI)
                            .addComponent(textPassword))
                        .addContainerGap(26, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelError)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(labelSchemaSource)
                                    .addComponent(labelInstanceSource)
                                    .addComponent(labelSchemaTarget)
                                    .addComponent(textinstanceSource, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                                    .addComponent(textSchemaSource, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                                    .addComponent(textSchemaTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonSchemaTarget)
                                    .addComponent(buttonInstanceSource)
                                    .addComponent(buttonSchemaSource)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelCheckRecreate)
                                    .addComponent(checkBoxRecreate))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(labelTmpDatabaseName)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(textTmpDatabaseName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(26, 26, 26))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
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
                .addGap(19, 19, 19)
                .addComponent(panelSupport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelSchemaSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(textSchemaSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonSchemaSource))
                .addGap(11, 11, 11)
                .addComponent(labelInstanceSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textinstanceSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonInstanceSource))
                .addGap(11, 11, 11)
                .addComponent(labelSchemaTarget)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textSchemaTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSchemaTarget))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCheckRecreate)
                    .addComponent(labelTmpDatabaseName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textTmpDatabaseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxRecreate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(cancelButton))
                    .addComponent(labelError, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (relationalConfigurationPM.checkFieldsAccessConfigurationAndPathFile()) {
            if (checkBoxRecreate.isSelected() && relationalConfigurationPM.checkFieldsAccessConfigurationPathFileAndRecreate()) {
                doClose(RET_OK);
                return;
            } else if (!checkBoxRecreate.isSelected()) {
                doClose(RET_OK);
                return;
            }
        }
        this.labelError.setText(NbBundle.getMessage(Costanti.class, Costanti.INFORMATION_NEEDED));
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void checkBoxRecreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxRecreateActionPerformed
        this.textTmpDatabaseName.setVisible(this.checkBoxRecreate.isSelected());
        this.labelTmpDatabaseName.setVisible(checkBoxRecreate.isSelected());
        this.pack();
    }//GEN-LAST:event_checkBoxRecreateActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonInstanceSource;
    private javax.swing.JButton buttonSchemaSource;
    private javax.swing.JButton buttonSchemaTarget;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox checkBoxRecreate;
    private javax.swing.JComboBox comboDriver;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel labelCheckRecreate;
    private javax.swing.JLabel labelDriver;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelInstanceSource;
    private javax.swing.JLabel labelPassword;
    private javax.swing.JLabel labelSchemaSource;
    private javax.swing.JLabel labelSchemaTarget;
    private javax.swing.JLabel labelTmpDatabaseName;
    private javax.swing.JLabel labelURI;
    private javax.swing.JLabel labelUserName;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel panelSupport;
    private javax.swing.JTextField textPassword;
    private javax.swing.JTextField textSchemaSource;
    private javax.swing.JTextField textSchemaTarget;
    private javax.swing.JTextField textTmpDatabaseName;
    private javax.swing.JTextField textURI;
    private javax.swing.JTextField textUserName;
    private javax.swing.JTextField textinstanceSource;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
