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

import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class SliderChangeListener implements ChangeListener {

    private static Log logger = LogFactory.getLog(SliderChangeListener.class);
    GraphSceneGlassPane myGraphScene;
    Scene scene;
    LayerWidget connectionLayer;

    public SliderChangeListener(GraphSceneGlassPane myGraphScene) {
        this.myGraphScene = myGraphScene;
        this.scene = myGraphScene.getScene();
        this.connectionLayer = myGraphScene.getConnectionLayer();
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            double confidence = (double)source.getValue()/100;
            for (Widget connectionWidget : connectionLayer.getChildren()) {
                ConnectionInfo connectionInfo = (ConnectionInfo) connectionLayer.getChildConstraint(connectionWidget);
                if (connectionInfo != null) {
                    if (connectionInfo.getConfidence() < confidence) {
                        connectionWidget.setVisible(false);
                        scene.validate();
                    } else {
                        connectionWidget.setVisible(true);
                        scene.validate();
                    }
                }
            }
        }
    }
}
