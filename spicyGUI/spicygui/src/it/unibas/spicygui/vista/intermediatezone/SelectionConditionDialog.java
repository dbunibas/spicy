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
import org.jdesktop.beansbinding.Binding;
import it.unibas.spicygui.controllo.validators.ValidazioneBindingListener;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

public class SelectionConditionDialog extends javax.swing.JDialog {

    private FormValidation formValidation = new FormValidation(false);
    private Binding binding;
    private SelectionConditionInfo selectionConditionInfo;
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    public static final int RET_DELETE = 2;

    public SelectionConditionDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.INPUT_TEXT_SELECTION_CONDITION_TITLE));
        this.setLocationRelativeTo(parent);
        initComponents();
        initMyBinding();
        initListener();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void initListener() {

        this.binding.addBindingListener(new ValidazioneBindingListener(this.errorLabel, "filterBinding", formValidation));
    }

    private void initMyBinding() {
        //     binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${formValidation.buttonState}"), okButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "buttonBinding");

        org.jdesktop.beansbinding.Binding binding3 = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${formValidation.buttonState}"), okButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "buttonBinding");
        bindingGroup.addBinding(binding3);
        this.binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this.getSelectionConditionInfo(), ELProperty.create("${expressionString}"), jTextFieldSelectionCondition, ELProperty.create("${text}"), "selectionConditionBinding");
        //  ValidatoreCampoTesto validatoreCampoTesto = new it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto();
        validatoreCampoTesto.setFormValidation(formValidation);
        this.binding.setValidator(validatoreCampoTesto);
        bindingGroup.addBinding(binding);
        bindingGroup.bind();
    // this.binding.bind();
    }

    public void clean() {
        bindingGroup.unbind();
        bindingGroup.bind();
//        formValidation.setButtonState(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        validatoreCampoTesto = new it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTextFieldSelectionCondition = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "Ok_button"));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "Cancel_button"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectionConditionInfo.expressionString}"), jTextFieldSelectionCondition, org.jdesktop.beansbinding.BeanProperty.create("text"), "selection condition");
        binding.setValidator(validatoreCampoTesto);
        bindingGroup.addBinding(binding);

        jTextFieldSelectionCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSelectionConditionActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("");

        deleteButton.setText(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.GENERIC_DELETE));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldSelectionCondition, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deleteButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addComponent(jTextFieldSelectionCondition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(deleteButton))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

private void jTextFieldSelectionConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSelectionConditionActionPerformed
    if (this.okButton.isEnabled()) {
        doClose(RET_OK);
    }
}//GEN-LAST:event_jTextFieldSelectionConditionActionPerformed

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    doClose(RET_DELETE);

}//GEN-LAST:event_deleteButtonActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTextField jTextFieldSelectionCondition;
    private javax.swing.JButton okButton;
    private it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto validatoreCampoTesto;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    public FormValidation getFormValidation() {
        return formValidation;
    }

    public SelectionConditionInfo getSelectionConditionInfo() {
        return selectionConditionInfo;
    }

    public void setSelectionConditionInfo(SelectionConditionInfo selectionConditionInfo) {
        this.selectionConditionInfo = selectionConditionInfo;
    }
    
    public void setDeleteButton (boolean enable) {
        this.deleteButton.setEnabled(enable);
    }
}
