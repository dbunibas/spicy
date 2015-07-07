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
 
package it.unibas.spicygui.vista.configuration;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.openide.util.NbBundle;

public class SettingEngineConfigurationDialog extends javax.swing.JDialog {

    private Log logger = LogFactory.getLog(SettingEngineConfigurationDialog.class);
    private EngineConfigurationPM engineConfiguration;
    private BindingGroup bindingGroup;
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    public SettingEngineConfigurationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setLocationRelativeTo(parent);
        this.setTitle(NbBundle.getMessage(Costanti.class, Costanti.ACTION_SETTING_ENGINE_CONFIGURATION));
        bindingGroup = new BindingGroup();
        initComponents();
//        this.checkRewriteEDGs.setVisible(false);
    initBinding();
    }

    public void changeMappingTask(Scenario scenario) {
        this.engineConfiguration = scenario.getEngineConfigurationPM();
        clean();
    }

    private void clean() {
        bindingGroup.unbind();
        bindingGroup.bind();

    }

    private void initBinding() {
        Binding bindingMode = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.debugMode}"), checkDebugMode, BeanProperty.create("selected"), "modeBinding");
        bindingGroup.addBinding(bindingMode);
        Binding bindingRewriteCoverage = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.rewriteCoverages}"), checkRewriteCoverages, BeanProperty.create("selected"), "rewriteCoverageBinding");
        bindingGroup.addBinding(bindingRewriteCoverage);
        Binding bindingRewriteEgds = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.rewriteEGDs}"), checkRewriteEDGs, BeanProperty.create("selected"), "rewriteEDGsBinding");
        bindingGroup.addBinding(bindingRewriteEgds);
        Binding bindingRewriteSelfJoins = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.rewriteSelfJoins}"), checkRewriteSelfJoins, BeanProperty.create("selected"), "rewriteSelfJoinsBinding");
        bindingGroup.addBinding(bindingRewriteSelfJoins);
        Binding bindingRewriteSubsumptions = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.rewriteSubsumptions}"), checkRewriteSubsumptions, BeanProperty.create("selected"), "rewriteSubsumptionsBinding");
        bindingGroup.addBinding(bindingRewriteSubsumptions);
        Binding bindingRewriteAllHomomorphisms = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.rewriteAllHomomorphisms}"), checkRewriteAllHomomorphisms, BeanProperty.create("selected"), "rewriteAllHomomorphismsBinding");
        bindingGroup.addBinding(bindingRewriteAllHomomorphisms);
        Binding bindingCreateTableInSTExchange = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.useCreateTableInSTExchange}"), checkUseCreateTableSTExchange, BeanProperty.create("selected"), "useCreateTableInSTExchangeBinding");
        bindingGroup.addBinding(bindingCreateTableInSTExchange);
        Binding bindingCreateTableInTargetExchange = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.useCreateTableInTargetExchange}"), checkUseCreateTableTargetExchange, BeanProperty.create("selected"), "useCreateTableInTargetExchangeBinding");
        bindingGroup.addBinding(bindingCreateTableInTargetExchange);
        Binding bindingUseSkolemStrings = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.useSkolemStrings}"), checkUseSkolemStrings, BeanProperty.create("selected"), "useSkolemStrings");
        bindingGroup.addBinding(bindingUseSkolemStrings);
        Binding bindingHashTextForSkolemStrings = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.useHashTextForSkolems}"), checkUseHashTextForSkolems, BeanProperty.create("selected"), "useHashTextForSkolemsBinding");
        bindingGroup.addBinding(bindingHashTextForSkolemStrings);
        Binding bindingSkolemTableStrategy = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.skolemTableStrategy}"), comboBoxUseSkolemTableStrategy, BeanProperty.create("selectedIndex"));
        bindingGroup.addBinding(bindingSkolemTableStrategy);
        Binding bindingLocalSkolems = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.useLocalSkolem}"), checkUseLocalSkolems, BeanProperty.create("selected"), "useLocalSkolemsBinding");
        bindingGroup.addBinding(bindingLocalSkolems);
        Binding bindingSortStrategy = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${engineConfiguration.sortStrategy}"), comboBoxUseSortStrategy, BeanProperty.create("selectedIndex"));
        bindingGroup.addBinding(bindingSortStrategy);

//        Binding binding10 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, comboBoxUseSortStrategy, BeanProperty.create("selectedIndex"), this, ELProperty.create("${engineConfiguration.sortStrategy}"), "comboSortBinding2");
//        bindingGroup.addBinding(binding10);


        bindingGroup.bind();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        checkDebugMode = new javax.swing.JCheckBox();
        checkRewriteCoverages = new javax.swing.JCheckBox();
        checkRewriteEDGs = new javax.swing.JCheckBox();
        checkRewriteSelfJoins = new javax.swing.JCheckBox();
        checkRewriteSubsumptions = new javax.swing.JCheckBox();
        checkUseCreateTableSTExchange = new javax.swing.JCheckBox();
        checkUseCreateTableTargetExchange = new javax.swing.JCheckBox();
        labelUseSortStrategy = new javax.swing.JLabel();
        comboBoxUseSortStrategy = new javax.swing.JComboBox();
        checkUseSkolemStrings = new javax.swing.JCheckBox();
        checkUseHashTextForSkolems = new javax.swing.JCheckBox();
        checkUseLocalSkolems = new javax.swing.JCheckBox();
        checkRewriteAllHomomorphisms = new javax.swing.JCheckBox();
        labelUseSkolemTableStrategy = new javax.swing.JLabel();
        comboBoxUseSkolemTableStrategy = new javax.swing.JComboBox();

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

        checkDebugMode.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_debugMode_text")); // NOI18N

        checkRewriteCoverages.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_RewriteCoverages_text")); // NOI18N

        checkRewriteEDGs.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_RewriteEDGs_text")); // NOI18N

        checkRewriteSelfJoins.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_RewriteSelfJoins_text")); // NOI18N

        checkRewriteSubsumptions.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_RewriteSubsumptions_text")); // NOI18N

        checkUseCreateTableSTExchange.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_UseCreateTableSTExchange_text")); // NOI18N

        checkUseCreateTableTargetExchange.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_UseCreateTableTargetExchange_text")); // NOI18N

        labelUseSortStrategy.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "label_UseSortStrategy_text")); // NOI18N

        comboBoxUseSortStrategy.setModel(new DefaultComboBoxModel(new String[] { NbBundle.getMessage(Costanti.class, Costanti.COMBO_NO_SORT), NbBundle.getMessage(Costanti.class, Costanti.COMBO_SORT), NbBundle.getMessage(Costanti.class, Costanti.COMBO_AUTO_SORT)}));

        checkUseSkolemStrings.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_UseSkolemStrings_text")); // NOI18N

        checkUseHashTextForSkolems.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "check_UseHashTextForSkolem_text")); // NOI18N

        checkUseLocalSkolems.setLabel(org.openide.util.NbBundle.getMessage(SettingEngineConfigurationDialog.class, "SettingEngineConfigurationDialog.checkUseLocalSkolems.label")); // NOI18N

        checkRewriteAllHomomorphisms.setText(org.openide.util.NbBundle.getMessage(SettingEngineConfigurationDialog.class, "SettingEngineConfigurationDialog.checkRewriteAllHomomorphisms.text")); // NOI18N

        labelUseSkolemTableStrategy.setText(org.openide.util.NbBundle.getMessage(Costanti.class, "SettingEngineConfigurationDialog.labelUseSkolemTableStrategy.text")); // NOI18N

        comboBoxUseSkolemTableStrategy.setModel(new DefaultComboBoxModel(new String[] { NbBundle.getMessage(Costanti.class, Costanti.COMBO_NO_SKOLEM_TABLE), NbBundle.getMessage(Costanti.class, Costanti.COMBO_SKOLEM_TABLE), NbBundle.getMessage(Costanti.class, Costanti.COMBO_AUTO_SKOLEM_TABLE)}));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkRewriteSubsumptions)
                            .addComponent(checkRewriteSelfJoins)
                            .addComponent(checkRewriteCoverages)
                            .addComponent(checkRewriteEDGs)
                            .addComponent(checkRewriteAllHomomorphisms)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelUseSortStrategy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboBoxUseSortStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(checkUseCreateTableTargetExchange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(checkDebugMode)
                                .addComponent(checkUseCreateTableSTExchange, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                .addComponent(checkUseSkolemStrings)
                                .addComponent(checkUseHashTextForSkolems)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(labelUseSkolemTableStrategy)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(comboBoxUseSkolemTableStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(checkUseLocalSkolems))
                        .addGap(34, 34, 34))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkRewriteSubsumptions)
                        .addGap(18, 18, 18)
                        .addComponent(checkRewriteCoverages)
                        .addGap(18, 18, 18)
                        .addComponent(checkRewriteSelfJoins)
                        .addGap(18, 18, 18)
                        .addComponent(checkRewriteEDGs))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkUseCreateTableTargetExchange)
                        .addGap(18, 18, 18)
                        .addComponent(checkUseCreateTableSTExchange)
                        .addGap(18, 18, 18)
                        .addComponent(checkDebugMode)
                        .addGap(18, 18, 18)
                        .addComponent(checkUseSkolemStrings)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkUseHashTextForSkolems)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUseSkolemTableStrategy)
                            .addComponent(comboBoxUseSkolemTableStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkRewriteAllHomomorphisms)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUseSortStrategy)
                            .addComponent(comboBoxUseSortStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(checkUseLocalSkolems)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

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
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox checkDebugMode;
    private javax.swing.JCheckBox checkRewriteAllHomomorphisms;
    private javax.swing.JCheckBox checkRewriteCoverages;
    private javax.swing.JCheckBox checkRewriteEDGs;
    private javax.swing.JCheckBox checkRewriteSelfJoins;
    private javax.swing.JCheckBox checkRewriteSubsumptions;
    private javax.swing.JCheckBox checkUseCreateTableSTExchange;
    private javax.swing.JCheckBox checkUseCreateTableTargetExchange;
    private javax.swing.JCheckBox checkUseHashTextForSkolems;
    private javax.swing.JCheckBox checkUseLocalSkolems;
    private javax.swing.JCheckBox checkUseSkolemStrings;
    private javax.swing.JComboBox comboBoxUseSkolemTableStrategy;
    private javax.swing.JComboBox comboBoxUseSortStrategy;
    private javax.swing.JLabel labelUseSkolemTableStrategy;
    private javax.swing.JLabel labelUseSortStrategy;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    /**
     * @return the engineConfiguration
     */
    public EngineConfigurationPM getEngineConfiguration() {
        return engineConfiguration;
    }

    public JComboBox getComboBoxUseSortStrategy() {
        return comboBoxUseSortStrategy;
    }
    
    public JComboBox getComboBoxUseSkolemTableStrategy() {
        return comboBoxUseSkolemTableStrategy;
    }

}
