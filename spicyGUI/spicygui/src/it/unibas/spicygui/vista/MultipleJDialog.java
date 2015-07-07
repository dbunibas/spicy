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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.datasource.ActionCancelMultipleJoin;
import it.unibas.spicygui.controllo.datasource.ActionOkMultipleJoin;

public class MultipleJDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    public MultipleJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle(org.openide.util.NbBundle.getMessage(Costanti.class, Costanti.MULTIPLE_JOIN_DIALOG_TITLE));
        this.setLocationRelativeTo(parent);
        initComponents();
        initAction();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void initAction() {
        this.okButton.setAction(new ActionOkMultipleJoin(this));
        this.cancelButton.setAction(new ActionCancelMultipleJoin(this));
        okButton.setText("Ok");
        cancelButton.setText("Cancel");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane = new javax.swing.JScrollPane();
        joinConditionTextArea = new javax.swing.JTextArea();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("Ok");

        cancelButton.setText("Cancel");

        joinConditionTextArea.setColumns(20);
        joinConditionTextArea.setEditable(false);
        joinConditionTextArea.setLineWrap(true);
        joinConditionTextArea.setRows(5);
        jScrollPane.setViewportView(joinConditionTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(186, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
        this.cancelButton.doClick();
    /*    this.modello.removeBean(Costanti.CREATING_JOIN_SESSION);
        this.modello.removeBean(Costanti.JOIN_CONSTRIANTS);
        this.modello.removeBean(Costanti.JOIN_CONDITION);
        this.modello.removeBean(Costanti.FROM_PATH_NODES);*/
  
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    public void cleanTextArea () {
        this.joinConditionTextArea.removeAll();
    }
    
    public void appendText (String text) {
        this.joinConditionTextArea.append("\n" + text);
    }
        
    public void setText (String text) {
        this.joinConditionTextArea.setText(text);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTextArea joinConditionTextArea;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
