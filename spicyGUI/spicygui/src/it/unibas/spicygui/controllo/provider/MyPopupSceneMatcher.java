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
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.listener.SliderChangeListener;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheBarra;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class MyPopupSceneMatcher implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupSceneMatcher.class);
    private JPopupMenu popupMenu;
    private GraphSceneGlassPane myGraphScene;
    private Scene scene;
    private Point point;
    private JSlider slider = new JSlider(0, 100);
    private JLabel jLabelConfidenceLevel = new JLabel();

    public MyPopupSceneMatcher(GraphSceneGlassPane myGraphScene) {
        this.myGraphScene = myGraphScene;
        this.scene = myGraphScene.getScene();
        createPopupMenuGlassPane();
        settaSlider();
        settaBindingLabel();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        //qui il widget passato e proprio la scena
        this.point = point;
        return popupMenu;
    }

    public void actionPerformed(ActionEvent e) {
        LayerWidget constraintsLayer = myGraphScene.getConstraintsLayer();
        if (e.getActionCommand().equals(Costanti.ACTION_SLIDER)) {
            showSlider(constraintsLayer);
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    public void resetSlider() {
        this.slider.setValue(0);
    }

    public void creaSliderIniziale() {
        LayerWidget constraLayerWidget = myGraphScene.getConstraintsLayer();
        if (checkSliderShow(constraLayerWidget) == null) {
            JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
            int x = (frame.getSize().width / 2) - 20;
            int y = (int) (frame.getSize().height * 0.5);
            Point point = SwingUtilities.convertPoint(frame, x, y, myGraphScene);
            showSliderImpl(constraLayerWidget, point);
        }
    }

    private ComponentWidget checkSliderShow(LayerWidget constraLayerWidget) {
        ComponentWidget componentWidget = null;
        for (Widget widget : constraLayerWidget.getChildren()) {
            if (widget instanceof ComponentWidget) {
                componentWidget = (ComponentWidget) widget;
                break;
            }
        }
        return componentWidget;
    }

    private void createPopupMenuGlassPane() {
        popupMenu = new JPopupMenu("Popup menu");
        JMenuItem item;

        item = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.ACTION_SLIDER));
        item.setActionCommand(Costanti.ACTION_SLIDER);
        item.addActionListener(this);
        popupMenu.add(item);

    }

    private void settaBindingLabel() {
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, slider, ELProperty.create("${value/100}"), jLabelConfidenceLevel, BeanProperty.create("text"), "Slider");
        binding.bind();
    }

    private void settaSlider() {
        SliderChangeListener sliderChangeListener = new SliderChangeListener(myGraphScene);
        slider.addChangeListener(sliderChangeListener);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setOpaque(false);
        slider.setValue(0);

        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("0"));
        labelTable.put(new Integer(50), new JLabel("0.5"));
        labelTable.put(new Integer(100), new JLabel("1"));
        slider.setLabelTable(labelTable);

    }

    private void showSlider(LayerWidget constraLayerWidget) {
        ComponentWidget componentWidget = checkSliderShow(constraLayerWidget);
        if (componentWidget != null) {
            ((Widget) constraLayerWidget.getChildConstraint(componentWidget)).removeFromParent();
            componentWidget.removeFromParent();
            scene.validate();
        } else {
            showSliderImpl(constraLayerWidget, this.point);
        }

    }

    private void showSliderImpl(LayerWidget constraintslayer, Point point) {

        ComponentWidget componentWidget = new ComponentWidget(scene, slider);

        componentWidget.getActions().addAction(ActionFactory.createMoveAction());

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(componentWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setLayout(LayoutFactory.createHorizontalFlowLayout());
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        ComponentWidget labelConfidence = new ComponentWidget(scene, jLabelConfidenceLevel);
        labelConfidence.setPreferredSize(new Dimension(100, 10));
        barra.addChild(labelConfidence);
        Point pointBarra = new Point(point.x - Costanti.OFF_SET_X_WIDGET_BARRA, point.y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        MyMoveProviderGeneric moveProvider = new MyMoveProviderGeneric();
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));

//        int x = myGraphScene.getBounds()
        componentWidget.setPreferredLocation(new Point(point.x, point.y));
        constraintslayer.addChild(componentWidget, barra);
        constraintslayer.addChild(barra, caratteristicheBarra);
        scene.validate();
        myGraphScene.updateUI();
    }
}
