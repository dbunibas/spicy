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
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.controllo.validators.ValidazioneBindingListener;

public class FunctionDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    private CaratteristicheWidgetInterFunction caratteristiche;
    private FormValidation formValidation = new FormValidation(false);

    public FunctionDialog(java.awt.Frame parent, CaratteristicheWidgetInterFunction caratteristiche, boolean modal) {
        super(parent, modal);
        this.caratteristiche = caratteristiche;
        this.setLocationRelativeTo(parent);
        initComponents();
        initBinding();
        initListener();
    }

    private void initBinding() {
        org.jdesktop.beansbinding.Binding binding3 = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${formValidation.buttonState}"), okButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"), "buttonBinding");
        bindingGroup.addBinding(binding3);
        validatoreCampoTesto.setFormValidation(getFormValidation());

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${caratteristiche.sourceList}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jListSource, "lista binding");
        jListBinding.setDetailBinding(org.jdesktop.beansbinding.ELProperty.create("${string}"));
        bindingGroup.addBinding(jListBinding);
    }

    private void initListener() {
        this.bindingGroup.addBindingListener(new ValidazioneBindingListener(this.errorLabel, "functionBinding", getFormValidation()));
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        validatoreCampoTesto = new it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaFunction = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListSource = new javax.swing.JList();
        etichettaSource = new javax.swing.JLabel();
        etichettaFunzione = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

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

        jTextAreaFunction.setColumns(20);
        jTextAreaFunction.setRows(5);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${caratteristiche.expressionFunction}"), jTextAreaFunction, org.jdesktop.beansbinding.BeanProperty.create("text"), "functionBinding");
        binding.setValidator(validatoreCampoTesto);
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextAreaFunction);

        jScrollPane2.setViewportView(jListSource);

        etichettaSource.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "FunctionDialog.etichettaSource.text")); // NOI18N

        etichettaFunzione.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "FunctionDialog.etichettaFunzione.text")); // NOI18N

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addComponent(etichettaSource)
                    .addComponent(etichettaFunzione))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(etichettaSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(etichettaFunzione)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addComponent(errorLabel))
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

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel etichettaFunzione;
    private javax.swing.JLabel etichettaSource;
    private javax.swing.JList jListSource;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaFunction;
    private javax.swing.JButton okButton;
    private it.unibas.spicygui.controllo.validators.ValidatoreCampoTesto validatoreCampoTesto;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    public CaratteristicheWidgetInterFunction getCaratteristiche() {
        return caratteristiche;
    }

    public void setCaratteristiche(CaratteristicheWidgetInterFunction caratteristiche) {
        this.caratteristiche = caratteristiche;
    }

    public void clean() {
        bindingGroup.unbind();
        bindingGroup.bind();
    }

    public FormValidation getFormValidation() {
        return formValidation;
    }
}
