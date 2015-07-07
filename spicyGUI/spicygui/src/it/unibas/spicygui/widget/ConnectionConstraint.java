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
 
package it.unibas.spicygui.widget;

import it.unibas.spicygui.Costanti;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;

public class ConnectionConstraint {

    private List<MyConnectionWidget> connections = new ArrayList<MyConnectionWidget>();
    private String joinString;
    private String suffixMandatory;
    private String suffixForeignKey;

    public ConnectionWidget getConnection(int index) {
        return connections.get(index);
    }

    public boolean addConnection(MyConnectionWidget connection) {
        return connections.add(connection);
    }

    public List<MyConnectionWidget> getConnections() {
        return connections;
    }

    public void changeLineColor(Color color) {
        for (ConnectionWidget connectionWidget : connections) {
            connectionWidget.setLineColor(color);
        }
    }

    public void deleteConnectionAndWidget() {
        for (ConnectionWidget connectionWidget : connections) {
            connectionWidget.getSourceAnchor().getRelatedWidget().removeFromParent();
            connectionWidget.getTargetAnchor().getRelatedWidget().removeFromParent();
            connectionWidget.removeFromParent();
        }
    }

    public void changeMandatory(boolean mandatary, String joinString) {
        this.suffixMandatory = Boolean.toString(mandatary);
        this.joinString = joinString;
        for (ConnectionWidget connectionWidget : connections) {
//            connectionWidget.setToolTipText("Mandatory = " + this.suffixMandatory + " Monodirectional = " + this.suffixForeignKey);
            connectionWidget.setToolTipText(joinString);
            if (mandatary) {
                connectionWidget.setStroke(Costanti.DASHED_STROKE_THICK);
            } else {
                connectionWidget.setStroke(Costanti.DASHED_STROKE);
            }
        }
    }

    public void changeForeignKey(boolean foreignkey, String joinString) {
        this.suffixForeignKey = Boolean.toString(foreignkey);
        this.joinString = joinString;
        for (MyConnectionWidget connectionWidget : connections) {
//            connectionWidget.setToolTipText("Mandatory = " + this.suffixMandatory + " Monodirectional = " + this.suffixForeignKey);
            connectionWidget.setToolTipText(joinString);
            if (foreignkey && connectionWidget.isDirezionale()) {
                connectionWidget.setTargetAnchorShape(Costanti.ANCHOR_SHAPE);
            } else {
                connectionWidget.setTargetAnchorShape(AnchorShape.NONE);
            }
        }
    }
}
