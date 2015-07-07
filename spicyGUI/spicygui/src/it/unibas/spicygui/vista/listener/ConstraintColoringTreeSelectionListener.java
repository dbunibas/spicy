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
 
package it.unibas.spicygui.vista.listener;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import it.unibas.spicygui.widget.ConnectionConstraint;
import it.unibas.spicygui.widget.JoinConstraint;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public class ConstraintColoringTreeSelectionListener implements TreeSelectionListener {

    private static Log logger = LogFactory.getLog(ConstraintColoringTreeSelectionListener.class);
    private Modello modello;
    private String connectionConstraintConst;
    private String nodeConnectionAnnotation;
    private String joinConnectionConst;
    private String joinNodeConnectionAnnotation;

    public ConstraintColoringTreeSelectionListener(String connectionConstraintConst, String nodeConnectionAnnotation, String joinConnectionConst, String joinNodeConnectionAnnotation) {
        this.connectionConstraintConst = connectionConstraintConst;
        this.nodeConnectionAnnotation = nodeConnectionAnnotation;
        this.joinConnectionConst = joinConnectionConst;
        this.joinNodeConnectionAnnotation = joinNodeConnectionAnnotation;
        executeInjection();
    }

    public void valueChanged(TreeSelectionEvent e) {
        JTree albero = (JTree) e.getSource();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        if (treeNode == null) {
            return;
        }
        TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
        INode node = adapter.getINode();
        analyzeConnectionConstraints(node);
        analyzeJoinConditions(node);
    }

    private void analyzeConnectionConstraints(INode node) {
        ConnectionConstraint connectionConstraintOld = (ConnectionConstraint) modello.getBean(connectionConstraintConst);
        if (connectionConstraintOld != null) {
            connectionConstraintOld.changeLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT);
        }
        ConnectionConstraint connectionConstraint = (ConnectionConstraint) node.getAnnotation(this.nodeConnectionAnnotation);
        if (connectionConstraint == null) {
            return;
        }
        connectionConstraint.changeLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_SELECTED);
        modello.putBean(connectionConstraintConst, connectionConstraint);
    }

    private void analyzeJoinConditions(INode node) {
        JoinConstraint joinConstrainOld = (JoinConstraint) modello.getBean(joinConnectionConst);
        if (joinConstrainOld != null) {
            joinConstrainOld.changeLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT);
        }
        JoinConstraint joinConstraints = (JoinConstraint) node.getAnnotation(this.joinNodeConnectionAnnotation);
        if (joinConstraints == null) {
            return;
        }
        joinConstraints.changeLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_SELECTED);
        modello.putBean(joinConnectionConst, joinConstraints);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
