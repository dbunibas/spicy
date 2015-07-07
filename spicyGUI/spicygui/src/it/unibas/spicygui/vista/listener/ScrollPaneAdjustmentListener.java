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
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstraint;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.caratteristiche.ICaratteristicheWidget;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLayeredPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

public class ScrollPaneAdjustmentListener implements AdjustmentListener {

    private JLayeredPane jLayeredPane;
    private GraphSceneGlassPane glassPane;
    private Component source;
    private Component connectedComponent;
    private String type;
    private static Log logger = LogFactory.getLog(ScrollPaneAdjustmentListener.class);

    public ScrollPaneAdjustmentListener() {
    }

    public ScrollPaneAdjustmentListener(JLayeredPane jLayeredPane, Component source, GraphSceneGlassPane glassPane, Component connectedComponent, String type) {
        this.jLayeredPane = jLayeredPane;
        this.glassPane = glassPane;
        this.source = source;
        this.connectedComponent = connectedComponent;
        this.type = type;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        LayerWidget mainLayer = glassPane.getMainLayer();
        List<Widget> listaWidget = mainLayer.getChildren();
        Iterator iteratore = listaWidget.iterator();
        this.jLayeredPane.moveToFront(this.glassPane);
        while (iteratore.hasNext()) {
            Widget widget = (Widget) iteratore.next();
            ICaratteristicheWidget caratteristicheWidget = (ICaratteristicheWidget) mainLayer.getChildConstraint(widget);
            Point newPoint = null;
            if (caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.TREE_SOURCE) || caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.TREE_TARGET)) {
                newPoint = findNewLocationForTree(caratteristicheWidget, (JTree) connectedComponent);
            } else if (caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.FOREIGN_KEY) || caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.KEY)) {

                newPoint = findNewLocationForOther(caratteristicheWidget, widget);
            }
            if (newPoint != null) {
                widget.setPreferredLocation(newPoint);
            }
            glassPane.getScene().validate();
            this.jLayeredPane.moveToFront(this.glassPane);
            glassPane.updateUI();
        }
        glassPane.updateUI();
    }

    private Point findNewLocationForOther(ICaratteristicheWidget caratteristicheWidget, Widget widget) {
        CaratteristicheWidgetConstraint caratteristicheWidgetConstraint = (CaratteristicheWidgetConstraint) caratteristicheWidget;
        Widget originalWidget = caratteristicheWidgetConstraint.getWidgetOriginale();
        Point originalPoint = caratteristicheWidgetConstraint.getOriginalPoint();

        int x = (originalWidget.getPreferredLocation().x - originalPoint.x) + widget.getPreferredLocation().x;
        int y = (originalWidget.getPreferredLocation().y - originalPoint.y) + widget.getPreferredLocation().y;
        caratteristicheWidgetConstraint.setOriginalPoint(new Point(originalWidget.getPreferredLocation().x, originalWidget.getPreferredLocation().y));
        return new Point(x, y);
    }

    private Point findNewLocationForTree(ICaratteristicheWidget icaratteristicheWidget, JTree albero) {
        CaratteristicheWidgetTree caratteristicheWidget = (CaratteristicheWidgetTree) icaratteristicheWidget;
        Point oldPoint = caratteristicheWidget.getPosizione();

        if (logger.isTraceEnabled()) logger.trace("oldPoint: " + oldPoint);
        TreePath treePath = caratteristicheWidget.getTreePath();
        Rectangle rect = albero.getPathBounds(treePath);
        if (rect != null && this.type.equalsIgnoreCase(caratteristicheWidget.getTreeType())) {
            Point newPoint = albero.getPathBounds(treePath).getLocation();
            Point convertedPoint = SwingUtilities.convertPoint(source, newPoint, glassPane);
            if (logger.isTraceEnabled()) logger.trace(" -- newPoint: " + convertedPoint);
            if (caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.TREE_SOURCE)) {
                return new Point(convertedPoint.x + (albero.getPathBounds(treePath).width - Costanti.OFFSET_X_WIDGET_SOURCE), convertedPoint.y + (albero.getPathBounds(treePath).height / Costanti.OFFSET_Y_WIDGET_SOURCE));
            }
            return new Point(convertedPoint.x, convertedPoint.y + 5);

        } else if (this.type.equalsIgnoreCase(caratteristicheWidget.getTreeType())) {
            TreePath treePathInterno = treePath;
            Rectangle rectInterno = albero.getPathBounds(treePathInterno);
            while (rectInterno == null) {
                if (treePathInterno == null) {
                    return null;
                }
                treePathInterno = treePathInterno.getParentPath();
                rectInterno = albero.getPathBounds(treePathInterno);
            }
            Point newPoint = albero.getPathBounds(treePathInterno).getLocation();
            Point convertedPoint = SwingUtilities.convertPoint(source, newPoint, glassPane);
            if (logger.isTraceEnabled()) logger.trace(" -- newPoint: " + convertedPoint);
            if (caratteristicheWidget.getTreeType().equalsIgnoreCase(Costanti.TREE_SOURCE)) {
                return new Point(convertedPoint.x + (albero.getPathBounds(treePathInterno).width - Costanti.OFFSET_X_WIDGET_SOURCE), convertedPoint.y + (albero.getPathBounds(treePathInterno).height / Costanti.OFFSET_Y_WIDGET_SOURCE));
            }
            return new Point(convertedPoint.x, convertedPoint.y + 5);
        }
        return null;
    }
}
