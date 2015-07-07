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
import it.unibas.spicygui.vista.treepm.TreeTopComponentAdapter;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class TreeDragSource implements DragSourceListener, DragGestureListener {

    DragSource source;
    DragGestureRecognizer recognizer;
    TransferableTreeNode transferable;
    DefaultMutableTreeNode oldNode;
    JTree sourceTree;

    public TreeDragSource(JTree tree, int actions) {
        sourceTree = tree;
        source = new DragSource();
        recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
                actions, this);
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        TreePath path = sourceTree.getSelectionPath();
        if ((path == null) || (path.getPathCount() <= 0) ) {
            return;
        }


        oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        TreeTopComponentAdapter adapter = (TreeTopComponentAdapter) oldNode.getUserObject();
        Scenario scenarioTreeSelected = adapter.getScenario();
        if(scenarioTreeSelected == null) {
            this.sourceTree.setCursor(Cursor.getDefaultCursor());
            return;
        }
        transferable = new TransferableTreeNode(scenarioTreeSelected);
        
        source.startDrag(dge, Toolkit.getDefaultToolkit().createCustomCursor(Costanti.IMAGE_DND_DENIED, new Point(0, 0), "DND_DENIED"), transferable, this);
        
    }


    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dsde) {
        Cursor myPointer = Toolkit.getDefaultToolkit().createCustomCursor(Costanti.IMAGE_DND_DENIED, new Point(0, 0), "DND_DENIED");
        dsde.getDragSourceContext().setCursor(myPointer);

    }

    public void dragOver(DragSourceDragEvent dsde) {
        Cursor myPointer = Toolkit.getDefaultToolkit().createCustomCursor(Costanti.IMAGE_DND_ACCEPT, new Point(0, 0), "DND_ACCEPRT");


        dsde.getDragSourceContext().setCursor(myPointer);
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
//        System.out.println("Action: " + dsde.getDropAction());
//        System.out.println("Target Action: " + dsde.getTargetActions());
//        System.out.println("User Action: " + dsde.getUserAction());
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()
                && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
            ((DefaultTreeModel) sourceTree.getModel()).removeNodeFromParent(oldNode);
        }
    }
}
