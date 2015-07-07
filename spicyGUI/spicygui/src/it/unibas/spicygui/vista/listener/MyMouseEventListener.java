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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public final class MyMouseEventListener implements MouseListener, MouseMotionListener {

    private static Log logger = LogFactory.getLog(MyMouseEventListener.class);
    private GraphSceneGlassPane component;
    private JPanel pannelloPrincipale;
    private JLayeredPaneCorrespondences jLayeredPane;
    private Component tmp12;
    private Component split;
    private Modello modello;

    public MyMouseEventListener(GraphSceneGlassPane component, JPanel pannelloPrincipale, JLayeredPaneCorrespondences jLayeredPane, Component split) {
        executeInjection();
        this.component = component;
        this.pannelloPrincipale = pannelloPrincipale;
        this.jLayeredPane = jLayeredPane;
        this.split = split;
    }

    public void mouseDragged(MouseEvent e) {
        e.consume();
        dispacciaEventoDrag(tmp12, e, e.getPoint(), true);
        jLayeredPane.moveToFront(component);
    }

    public void mousePressed(MouseEvent e) {
        e.consume();
        Component comS = SwingUtilities.getDeepestComponentAt(pannelloPrincipale, e.getX(), e.getY());
        if ((comS instanceof JScrollBar || !((comS instanceof JPanel) || (comS instanceof JTree))) && comS != null) {
            if (tmp12 == null) {
                tmp12 = comS;
            }

        }
        if (!(Costanti.INTERMEDIE.equals(comS.getName()))) {
            dispacciaEvento(null, e, e.getPoint(), false);
        }
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    public void mouseReleased(MouseEvent e) {
        e.consume();
        dispacciaEvento(null, e, e.getPoint(), false);
        dispacciaEvento(tmp12, e, e.getPoint(), false);


        tmp12 = null;
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    public void mouseMoved(MouseEvent e) {
        e.consume();
        jLayeredPane.moveToFront(component);
        component.updateUI();
        //TODO vedere se questa tecnica va bene per ricreare l'albero in caso di duplicazione dovuta alla creazione di un join
        Boolean recreateTree = (Boolean) modello.getBean(Costanti.RECREATE_TREE);
        if ((recreateTree == null) || recreateTree == Boolean.FALSE) {
           return;            
        }
        modello.removeBean(Costanti.RECREATE_TREE);
        if (logger.isDebugEnabled()) logger.debug("ricreo albero");
        duplicateTree();
        
    }

    public void mouseEntered(MouseEvent e) {
        e.consume();
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    public void mouseExited(MouseEvent e) {
        e.consume();
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    public void mouseClicked(MouseEvent e) {
        e.consume();
        dispacciaEvento(null, e, e.getPoint(), false);
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    private void dispacciaEvento(Component com, MouseEvent e, Point point, boolean draggedEvent) {
        if (com == null) {
            com = SwingUtilities.getDeepestComponentAt(pannelloPrincipale, point.x, point.y);
        }
        Point componentPoint = SwingUtilities.convertPoint(component, e.getPoint(), com);
        if (com != null) {
            if (com instanceof JTree) {
                TreePath treePath = ((JTree) com).getPathForLocation(componentPoint.x, componentPoint.y);
                if (treePath == null || draggedEvent) {
                    jLayeredPane.moveToFront(component);
                    component.updateUI();
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        return;
                    }
                }
            }
            com.dispatchEvent(new MouseEvent(com, e.getID(), e.getWhen(), e.getModifiers(),
                    componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));

            jLayeredPane.moveToFront(component);
            component.updateUI();

        }
        jLayeredPane.moveToFront(component);
        component.updateUI();

    }

    private void dispacciaEventoDrag(Component com, MouseEvent e, Point point, boolean draggedEvent) {
        Point componentPoint = SwingUtilities.convertPoint(component, e.getPoint(), com);
        if (com != null) {
            if (com instanceof JTree) {
                TreePath treePath = ((JTree) com).getPathForLocation(componentPoint.x, componentPoint.y);
                if (treePath == null || draggedEvent) {
                    component.updateUI();
                    return;
                }
            }
            if ((!(com instanceof JScrollBar) && (tmp12 instanceof JScrollBar))) {
                jLayeredPane.moveToFront(component);
                component.updateUI();
                return;
            }

            com.dispatchEvent(new MouseEvent(component, e.getID(), e.getWhen(), e.getModifiers(),
                    componentPoint.x, componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
            jLayeredPane.moveToFront(component);
            component.updateUI();

        }
        jLayeredPane.moveToFront(component);
        component.updateUI();
    }

    private void duplicateTree() {
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
