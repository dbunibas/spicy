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
import it.unibas.spicygui.controllo.tree.operators.ExcludeNodes;
import it.unibas.spicygui.controllo.tree.operators.IncludeNodes;
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
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class ActionExclusionInclusion extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionExclusionInclusion.class);
    private Modello modello;
    private JTree albero;
    private IDataSourceProxy dataSource;
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private ExcludeNodes excludeNodes;
    private IncludeNodes includeNodes = new IncludeNodes();

    public ActionExclusionInclusion(JTree albero, IDataSourceProxy dataSource) {
        this.executeInjection();
        this.dataSource = dataSource;
        this.albero = albero;
        excludeNodes = new ExcludeNodes(dataSource);
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_INCLUSION_EXCLUSION));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
        INode iNode = adapter.getINode();
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(iNode);
        if (iNode.isExcluded()) {
            if (!dataSource.getExclusions().contains(pathExpression)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.INCLUDE_NODES_WARNING), DialogDescriptor.INFORMATION_MESSAGE));
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.INCLUDE_NODES_WARNING));
                return;
            }
            includeNodes.include(iNode);
            dataSource.getExclusions().remove(pathExpression);
        } else {
            if (!chiediConferma()) {
                return;
            }
            excludeNodes.exclude(iNode);
            if (dataSource.getInclusions().isEmpty()) {
                dataSource.addInclusion(pathGenerator.generatePathFromRoot(dataSource.getIntermediateSchema()));
            }
            dataSource.addExclusion(pathExpression);
        }
        albero.updateUI();
    }

    private boolean chiediConferma() {
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.DELETE_EXCLUSIONE_CORRESPONDENCES), DialogDescriptor.YES_NO_OPTION);
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
            return true;
        }
        return false;

    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
