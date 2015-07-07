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
 
package it.unibas.spicygui.controllo.provider;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheBarra;
import java.awt.Point;
import javax.swing.JPanel;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.Widget;

public class MyMoveProviderSlider implements MoveStrategy, MoveProvider {


    public MyMoveProviderSlider() {
    }

    public Point locationSuggested(Widget widget, Point sourcePoint, Point destPoint) {
        return destPoint;
    }

    public void movementStarted(Widget widget) {
        return;
    }

    public void movementFinished(Widget widget) {
        return;
    }

    public Point getOriginalLocation(Widget widget) {
        return widget.getPreferredLocation();
    }

    public void setNewLocation(Widget widget, Point point) {
        CaratteristicheBarra caratteristicheBarra = (CaratteristicheBarra) widget.getParentWidget().getChildConstraint(widget);
        Widget rootWidget = caratteristicheBarra.getRootWidget();
        rootWidget.setPreferredLocation(new Point(point.x + Costanti.OFF_SET_X_WIDGET_BARRA, point.y + Costanti.OFF_SET_Y_WIDGET_BARRA));
        widget.setPreferredLocation(point);
    }
    

}
