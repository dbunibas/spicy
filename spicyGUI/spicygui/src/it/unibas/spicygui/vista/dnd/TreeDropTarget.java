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
package it.unibas.spicygui.vista.dnd;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.provider.intermediatezone.WidgetCreator;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class TreeDropTarget implements DropTargetListener {

    DropTarget target;
    GraphSceneGlassPane targetCompositionTopComponent;

    public TreeDropTarget(GraphSceneGlassPane compositionTopComponent) {
        compositionTopComponent.setEnabled(true);
        targetCompositionTopComponent = compositionTopComponent;
        target = new DropTarget(compositionTopComponent.getScene().getView(), this);
    }


//    private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
//        Point p = dtde.getLocation();
//        DropTargetContext dtc = dtde.getDropTargetContext();
//        JTree tree = (JTree) dtc.getComponent();
//        TreePath path = tree.getClosestPathForLocation(p.x, p.y);
//        return (TreeNode) path.getLastPathComponent();
//    }

    public void dragEnter(DropTargetDragEvent dtde) {
//        TreeNode node = getNodeForEvent(dtde);
//        if (node.isLeaf()) {
//            dtde.rejectDrag();
//        } else {
        // start by supporting move operations
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
//        dtde.acceptDrag(dtde.getDropAction());
//        }
    }

    public void dragOver(DropTargetDragEvent dtde) {
//        TreeNode node = getNodeForEvent(dtde);
//        if (node.isLeaf()) {
//            dtde.rejectDrag();
//        } else {
        // start by supporting move operations
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
//        dtde.acceptDrag(dtde.getDropAction());
//        }
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void drop(DropTargetDropEvent dtde) {
        Point pt = dtde.getLocation();
//        DropTargetContext dtc = dtde.getDropTargetContext();
//        JTree tree = (JTree) dtc.getComponent();
//        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
//        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
//        if (parent.isLeaf()) {
//            dtde.rejectDrop();
//            return;
//        }

//        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    dtde.acceptDrop(dtde.getDropAction());
                    Scenario p = null;
                try {
                    p = (Scenario) tr.getTransferData(flavors[i]);
                } catch (UnsupportedFlavorException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                    Scenario scenarioTreeSelected = p;
                    if(p == null) {
                        return;
                    }
                    WidgetCreator widgetCreator = new WidgetCreator();
                    Widget widget = widgetCreator.createConstantWidget(targetCompositionTopComponent.getScene(), targetCompositionTopComponent.getMainLayer(), targetCompositionTopComponent.getConnectionLayer(), pt, targetCompositionTopComponent, scenarioTreeSelected);
                    if (widget == null) {
                        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.NOT_ADDED_IN_COMPOSITION));
                    }


//                    dtde.dropComplete(true);
                    return;
                }
            }
            dtde.rejectDrop();

    }
}
