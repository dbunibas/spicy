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
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.border.LineBorder;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;

public class ConstantWidget extends Widget {

    private CaratteristicheWidgetInterConst caratteristicheWidget;

    public ConstantWidget(Scene scene, Point point, CaratteristicheWidgetInterConst caratteristicheWidget) {
        super(scene);
        this.caratteristicheWidget = caratteristicheWidget;
        creaWidget(scene, point);
    }

    private MyGradientLabelWidget creaHeaderWidget(Scene scene) {
        float[] coloreEnd = new float[3];
        Color.RGBtoHSB(208, 236, 255, coloreEnd);
        Color colorEndVMD = Color.getHSBColor(coloreEnd[0], coloreEnd[1], coloreEnd[2]);

        MyGradientLabelWidget gradientHeaderWidget = new MyGradientLabelWidget(scene, "Const", Color.WHITE, colorEndVMD);
        gradientHeaderWidget.setLayout(LayoutFactory.createOverlayLayout());
        gradientHeaderWidget.setPreferredSize(new Dimension(25, 25));
        return gradientHeaderWidget;
    }

    private void creaWidget(Scene scene, Point point) {

        this.setLayout(LayoutFactory.createHorizontalFlowLayout());
        MyGradientLabelWidget gradientWidget = creaHeaderWidget(scene);
        ImageWidget imageWidget = new ImageWidget(scene, ImageUtilities.loadImage(Costanti.ICONA_CONSTANT));
        gradientWidget.addChild(imageWidget);
        LabelWidget constantWidget = new LabelWidget(scene, "");
        constantWidget.setVerticalAlignment(LabelWidget.VerticalAlignment.CENTER);
        this.addChild(gradientWidget);
        this.addChild(constantWidget);
        this.setPreferredLocation(new Point(point.x - 1, point.y - 1));
        settaBordoConstant();

        Binding bindingConst = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, caratteristicheWidget, ELProperty.create("${costante}"), constantWidget, BeanProperty.create("label"));
        bindingConst.bind();
        
        this.setOpaque(true);

    }

    private void settaBordoConstant() {
        float[] coloreBordo = new float[3];
        Color.RGBtoHSB(186, 205, 240, coloreBordo);
        Color colorBordo = Color.getHSBColor(coloreBordo[0], coloreBordo[1], coloreBordo[2]);
        LineBorder lineBorder = new LineBorder(colorBordo, 1, true);
        this.setBorder(lineBorder);
    }
}


