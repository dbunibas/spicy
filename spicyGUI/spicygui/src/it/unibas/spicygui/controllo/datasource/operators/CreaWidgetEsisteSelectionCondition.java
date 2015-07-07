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
package it.unibas.spicygui.controllo.datasource.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.SelectionCondition;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;

public class CreaWidgetEsisteSelectionCondition {

    private static Log logger = LogFactory.getLog(CreaWidgetEsisteSelectionCondition.class);

    public void creaWidgetEsisteSelectionCondition(DefaultMutableTreeNode treeNode, String espressione, SelectionCondition selectionCondition) {
        TreeNodeAdapter adapter = (TreeNodeAdapter) treeNode.getUserObject();
        INode iNode = adapter.getINode();

        if (!espressione.equalsIgnoreCase("true") && !espressione.equalsIgnoreCase("")) {
            adapter.setSelectionCondition(true);
            VMDPinWidget vMDPinWidget = (VMDPinWidget) iNode.getAnnotation(Costanti.PIN_WIDGET_TREE);
            if (selectionCondition != null) {
                vMDPinWidget.setToolTipText(selectionCondition.toString());
            }
        } else if ((espressione.equalsIgnoreCase("true") || espressione.equalsIgnoreCase(""))) {
            adapter.setSelectionCondition(false);
        }
    }
}
