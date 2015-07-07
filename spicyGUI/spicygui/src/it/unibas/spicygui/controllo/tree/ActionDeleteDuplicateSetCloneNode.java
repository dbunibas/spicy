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
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class ActionDeleteDuplicateSetCloneNode extends AbstractAction {

    private Modello modello;
    private JLayeredPaneCorrespondences jLayeredPane;
    private JTree jTree;
    private IDataSourceProxy dataSource;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private static Log logger = LogFactory.getLog(ActionDeleteDuplicateSetCloneNode.class);

    public ActionDeleteDuplicateSetCloneNode(JLayeredPaneCorrespondences jLayeredPane, JTree jTree, IDataSourceProxy dataSource) {
        this.executeInjection();
        this.jLayeredPane = jLayeredPane;
        this.jTree = jTree;
        this.dataSource = dataSource;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_DELETE_DUPLICATE_SET_CLONE_NODE));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
        INode iNode = adapter.getINode();
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(iNode);
        try {
            if (logger.isDebugEnabled()) logger.debug(dataSource);
            if (logger.isDebugEnabled()) logger.debug(pathExpression);
            dataSource.removeDuplication(pathExpression);
            recreateTree();
        } catch (IllegalArgumentException iae) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.DELETE_DUPLICATION_NO) + ": " + iae.getMessage(), DialogDescriptor.ERROR_MESSAGE));
        }
    }

    private void recreateTree() {
        MappingTaskTopComponent mappingTaskTopComponent = jLayeredPane.getMappingTaskTopComponent();
        mappingTaskTopComponent.lightclear();
        mappingTaskTopComponent.drawScene();
        mappingTaskTopComponent.drawConnections();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
