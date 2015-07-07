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
 
package it.unibas.spicygui.vista.intermediatezone;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.FormValidation;
import it.unibas.spicygui.controllo.validators.ValidazioneBindingListener;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;

public class ConfidenceDialog extends javax.swing.JDialog {

    private ConnectionInfo connectionInfo;
    private FormValidation formValidation = new FormValidation(true);
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    public ConfidenceDialog(java.awt.Frame parent, ConnectionInfo connectionInfo, boolean modal) {
        super(parent, modal);
        this.setTitle(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.INPUT_TEXT_CONFIDENCE_TITLE));
        this.connectionInfo = connectionInfo;
        this.setLocationRelativeTo(parent);
        initComponents();
        initMyBinding();
        initListener();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        validatoreConfidence = new it.unibas.spicygui.controllo.validators.ValidatoreConfidence();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        textFieldConfidence = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancell");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${connectionInfo.confidence}"), textFieldConfidence, org.jdesktop.beansbinding.BeanProperty.create("text"), "confidenceBinding");
        bindingGroup.addBinding(binding);

        textFieldConfidence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textfieldAction(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldConfidence, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(errorLabel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textFieldConfidence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void initListener() {
        this.bindingGroup.addBindingListener(new ValidazioneBindingListener(this.errorLabel, "confidenceBinding", formValidation));
        this.validatoreConfidence.setFormValidation(formValidation);
    }

    private void initMyBinding() {
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${formValidation.buttonState}"), okButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "buttonBinding");
        bindingGroup.addBinding(binding);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void textfieldAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textfieldAction
        if (this.okButton.isEnabled()) {
            doClose(RET_OK);
        }
    }//GEN-LAST:event_textfieldAction

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField textFieldConfidence;
    private it.unibas.spicygui.controllo.validators.ValidatoreConfidence validatoreConfidence;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void clean() {
        bindingGroup.unbind();
        bindingGroup.bind();
        formValidation.setButtonState(true);
    }

    public FormValidation getFormValidation() {
        return formValidation;
    }
}
