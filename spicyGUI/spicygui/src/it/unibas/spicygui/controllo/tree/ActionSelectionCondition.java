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
 
package it.unibas.spicygui.controllo.tree;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetEsisteSelectionCondition;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.vista.intermediatezone.SelectionConditionDialog;
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ActionSelectionCondition extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionSelectionCondition.class);
    private CreaWidgetEsisteSelectionCondition analisiSelection = new CreaWidgetEsisteSelectionCondition();
    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private ReviewCorrespondences review = new ReviewCorrespondences();
    private SelectionConditionDialog dialog;
    private Modello modello;
    private JLayeredPaneCorrespondences jLayeredPane;
    private JTree jTree;
    private IDataSourceProxy dataSource;

    public ActionSelectionCondition(JLayeredPaneCorrespondences jLayeredPane, JTree jTree, IDataSourceProxy dataSource) {
        this.executeInjection();
        this.jLayeredPane = jLayeredPane;
        this.jTree = jTree;
        this.dataSource = dataSource;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_EDIT_SELECTION_CONDITION));
        dialog = new SelectionConditionDialog(WindowManager.getDefault().getMainWindow(), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        SelectionConditionInfo selectionConditionInfo = getSelectionCondition();
        dialog.setSelectionConditionInfo(selectionConditionInfo);
        dialog.clean();
        dialog.setSelectionConditionInfo(selectionConditionInfo);
        dialog.getFormValidation().setButtonState((selectionConditionInfo.getExpressionString() != null) && !(selectionConditionInfo.getExpressionString().equals("")));
        dialog.setDeleteButton(selectionConditionInfo.getSelectionCondition() != null);
        String oldExpression = selectionConditionInfo.getExpressionString();
        boolean oldButtonState = dialog.getFormValidation().getButtonState();
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == SelectionConditionDialog.RET_CANCEL) {
            ripristina(oldButtonState, oldExpression, selectionConditionInfo);
        } else if (dialog.getReturnStatus() == SelectionConditionDialog.RET_OK) {
            updateSelectionCondition(oldButtonState, oldExpression, selectionConditionInfo);
        } else {
            deleteSelectionCondition(oldButtonState, oldExpression, selectionConditionInfo);
        }

    }

    private void aggiorna() {
        jTree.updateUI();
        jLayeredPane.getMappingTaskTopComponent().getJLayeredPane().moveToFront();
    }

    private void deleteSelectionCondition(boolean oldButtonState, String oldSelectionCondition, SelectionConditionInfo selectionConditionInfo) {
        try {
            INode iNode = getNodeSelected();
            TreePath treePath = jTree.getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

            review.removeSelectionCondition(selectionConditionInfo.getSelectionCondition(), dataSource);
            selectionConditionInfo.setSelectionCondition(null);
            selectionConditionInfo.setExpressionString("");
            analisiSelection.creaWidgetEsisteSelectionCondition(treeNode, selectionConditionInfo.getExpressionString(), selectionConditionInfo.getSelectionCondition());
            VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE); 
            vMDPinWidget.setToolTipText(null);
            aggiorna();
        } catch (ExpressionSyntaxException e) {
            creator.undo(selectionConditionInfo.getSelectionCondition(), dataSource);
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING));
            ripristina(oldButtonState, oldSelectionCondition, selectionConditionInfo);
        }   
    }

    private SelectionConditionInfo getSelectionCondition() {
        INode iNode = getNodeSelected();
        SelectionConditionInfo selectionConditionInfo = null;
        if (iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO) != null) {
            selectionConditionInfo = (SelectionConditionInfo) iNode.getAnnotation(Costanti.SELECTION_CONDITON_INFO);
        } else {
            selectionConditionInfo = new SelectionConditionInfo();
            iNode.addAnnotation(Costanti.SELECTION_CONDITON_INFO, selectionConditionInfo);
        }
        return selectionConditionInfo;
    }

    private void updateSelectionCondition(boolean oldButtonState, String oldSelectionCondition, SelectionConditionInfo selectionConditionInfo) {
        try {
            INode iNode = getNodeSelected();
            TreePath treePath = jTree.getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

            review.removeSelectionCondition(selectionConditionInfo.getSelectionCondition(), dataSource);
            creator.createSelectionCondition(iNode, selectionConditionInfo.getExpressionString(), dataSource, selectionConditionInfo);
            analisiSelection.creaWidgetEsisteSelectionCondition(treeNode, selectionConditionInfo.getExpressionString(),selectionConditionInfo.getSelectionCondition());
            VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE); 
            vMDPinWidget.setToolTipText(selectionConditionInfo.getSelectionCondition().toString());
            aggiorna();
        } catch (ExpressionSyntaxException e) {
            creator.undo(selectionConditionInfo.getSelectionCondition(), dataSource);
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING));
            ripristina(oldButtonState, oldSelectionCondition, selectionConditionInfo);
        }
    }

    private INode getNodeSelected() {
        TreePath treePath = jTree.getSelectionPath();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
        return adapter.getINode();
    }

    private void ripristina(boolean oldButtonState, String oldSelectionCondition, SelectionConditionInfo selectionConditionInfo) {
        selectionConditionInfo.setExpressionString(oldSelectionCondition);
        dialog.getFormValidation().setButtonState(oldButtonState);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }

    }
}
