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
 
package it.unibas.spicygui.vista;

import it.unibas.spicygui.Costanti;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class GraphSceneGlassPane extends JPanel {

    private JComponent view;
    private JScrollPane jScroolPane;
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private LayerWidget constraintsLayer;
    private ObjectScene scene;
    private List<Widget> constants = new ArrayList<Widget>();
    private List<Widget> functions = new ArrayList<Widget>();
    private List<Widget> functionalDependencies = new ArrayList<Widget>();

    public GraphSceneGlassPane() {
        this.scene = new ObjectScene();
        this.scene.setOpaque(false);
        this.mainLayer = new LayerWidget(this.scene);
        this.connectionLayer = new LayerWidget(this.scene);
        this.constraintsLayer = new LayerWidget(this.scene);
        this.jScroolPane = new JScrollPane();
        this.jScroolPane.setOpaque(false);
        this.scene.addChild(Costanti.INDEX_MAIN_LAYER,this.mainLayer);
        this.scene.addChild(Costanti.INDEX_CONNECTION_LAYER,this.connectionLayer);
        this.scene.addChild(Costanti.INDEX_CONSTRAINTS_LAYER,this.constraintsLayer);
        this.view = scene.createView();
        this.view.setVisible(true);
        this.view.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.add(this.view, BorderLayout.CENTER);
    }

    public void clear() {
        mainLayer.removeChildren();
        constraintsLayer.removeChildren();
        connectionLayer.removeChildren();
        constants.clear();
        functions.clear();
        scene.validate();
    }

    public void clearTrees() {
        clearConnectionLayer();
        mainLayer.removeChildren();
        constraintsLayer.removeChildren();
        
    }

    public void clearConnections() {
        connectionLayer.removeChildren();
    }

    public void addConstant(Widget constant) {
        constants.add(constant);
    }

    public void clearConstants() {
        mainLayer.removeChildren(constants);
        constants.clear();
        scene.validate();
    }

    public void clearConnectionLayer() {
        clearConnections();
        clearConstants();
        clearFunctions();
        clearFunctionalDependencies();
    }

    public void removeConstant(Widget widget) {
        constants.remove(widget);
    }

    public void addFunction(Widget function) {
        functions.add(function);
    }

    public void clearFunctions() {
        mainLayer.removeChildren(functions);
        functions.clear();
        scene.validate();
    }

    public void removeFunction(Widget widget) {
        functions.remove(widget);
    }

    public void addFunctionalDependency(Widget functionalDependency) {
        functionalDependencies.add(functionalDependency);
    }

    public void clearFunctionalDependencies() {
        mainLayer.removeChildren(functionalDependencies);
        functionalDependencies.clear();
        scene.validate();
    }

    public void removeFunctionalDependencies(Widget widget) {
        functionalDependencies.remove(widget);
    }

    public JComponent getView() {
        return this.view;
    }

    public LayerWidget getMainLayer() {
        return this.mainLayer;
    }

    public Scene getScene() {
        return this.scene;
    }

    public LayerWidget getConnectionLayer() {
        return this.connectionLayer;
    }

    public LayerWidget getConstraintsLayer() {
        return constraintsLayer;
    }
}
