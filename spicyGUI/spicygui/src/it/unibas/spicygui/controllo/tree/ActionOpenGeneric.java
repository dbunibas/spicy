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

import it.unibas.spicy.model.exceptions.IllegalMappingTaskException;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.datasource.ActionViewInstances;
import it.unibas.spicygui.controllo.mapping.ActionGenerateSql;
import it.unibas.spicygui.controllo.mapping.ActionGenerateXQuery;
import it.unibas.spicygui.controllo.mapping.ActionViewTGD;
import it.unibas.spicygui.controllo.mapping.ActionViewTGDs;
import it.unibas.spicygui.controllo.mapping.ActionViewTransformation;
import it.unibas.spicygui.vista.InstancesTopComponent;
import it.unibas.spicygui.vista.SqlTopComponent;
import it.unibas.spicygui.vista.TGDCorrespondencesTopComponent;
import it.unibas.spicygui.vista.TGDListTopComponent;
import it.unibas.spicygui.vista.TransformationTopComponent;
import it.unibas.spicygui.vista.XQueryTopComponent;
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class ActionOpenGeneric extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionOpenGeneric.class);
    private JTree albero;

    public ActionOpenGeneric(JTree albero) {

        this.albero = albero;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_OPEN_GENERIC_TC));
    }

    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) albero.getLastSelectedPathComponent();
        TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) treeNode.getUserObject();
        TopComponent tc = adapter.getTopComponent();
        try {
            if (tc.isOpened()) {
                tc.requestVisible();
                return;
            }


            if (tc instanceof InstancesTopComponent) {
                Lookups.forPath("Azione").lookup(ActionViewInstances.class).myPerformAction((InstancesTopComponent) tc);
            } else if (tc instanceof TransformationTopComponent) {
                Lookups.forPath("Azione").lookup(ActionViewTransformation.class).myPerformAction(((TransformationTopComponent) tc).getScenario());
            } else if (tc instanceof XQueryTopComponent) {
                Lookups.forPath("Azione").lookup(ActionGenerateXQuery.class).myPerformAction(((XQueryTopComponent) tc).getScenario());
            } else if (tc instanceof SqlTopComponent) {
                Lookups.forPath("Azione").lookup(ActionGenerateSql.class).myPerformAction(((SqlTopComponent) tc).getScenario());
            } else if (tc instanceof TGDListTopComponent) {
                Lookups.forPath("Azione").lookup(ActionViewTGDs.class).myPerformAction(((TGDListTopComponent) tc).getScenario());
            } else if (tc instanceof TGDCorrespondencesTopComponent) {
                Lookups.forPath("Azione").lookup(ActionViewTGD.class).myPerformAction(((TGDCorrespondencesTopComponent) tc).getScenario());
            } else {
                tc.open();
                tc.requestVisible();
            }
        } catch (IllegalMappingTaskException exception) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(exception.getMessage(), DialogDescriptor.ERROR_MESSAGE));
        }
    }
}
