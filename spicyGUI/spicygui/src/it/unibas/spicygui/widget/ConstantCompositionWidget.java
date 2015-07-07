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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;

public class ConstantCompositionWidget extends Widget implements ICompositionWidget{

    private IconNodeWidget rootWidget;
    private LabelWidget labelValueWidget;
    private ImageWidget imageWidget;

    public ConstantCompositionWidget(Scene scene, Point point, Image image) {
        super(scene);
        creaWidget(scene, point, image);
    }

    private MyGradientLabelWidget creaHeaderWidget(Scene scene) {
        float[] coloreEnd = new float[3];
        Color.RGBtoHSB(208, 236, 255, coloreEnd);
        Color colorEndVMD = Color.getHSBColor(coloreEnd[0], coloreEnd[1], coloreEnd[2]);
        float[] coloreBordo = new float[3];
        Color.RGBtoHSB(186, 205, 240, coloreBordo);
        Color colorBordo = Color.getHSBColor(coloreBordo[0], coloreBordo[1], coloreBordo[2]);

        MyGradientLabelWidget gradientHeaderWidget = new MyGradientLabelWidget(scene, "Composition", Color.WHITE, colorEndVMD);
        gradientHeaderWidget.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 0));
        gradientHeaderWidget.setPreferredSize(new Dimension(25, 25));
        gradientHeaderWidget.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, colorBordo));
        return gradientHeaderWidget;
    }

    private void creaRoot(Scene scene) {
        float[] coloreBordo = new float[3];
        Color.RGBtoHSB(186, 205, 240, coloreBordo);
        Color colorBordo = Color.getHSBColor(coloreBordo[0], coloreBordo[1], coloreBordo[2]);

        rootWidget = new IconNodeWidget(scene);
        rootWidget.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 0));
        rootWidget.removeChildren();
        LineBorder lineBorder = new LineBorder(colorBordo, 1, true);
        rootWidget.setBorder(lineBorder);
    }

    private void creaWidget(Scene scene, Point point, Image image) {
        creaRoot(scene);
        MyGradientLabelWidget gradientHeaderWidget = creaHeaderWidget(scene);
        imageWidget = new ImageWidget(scene, image);
        gradientHeaderWidget.addChild(imageWidget);
        rootWidget.addChild(gradientHeaderWidget);
        this.setPreferredLocation(new Point(point.x - 1, point.y - 1));
        this.addChild(rootWidget);
        settaBordoFunction();
        this.setOpaque(true);

    }
    
    public LabelWidget getLabelValueWidget() {
        return labelValueWidget;
    }

    private void settaBordoFunction() {
        Color colore = Color.WHITE;
        LineBorder lineBorder = new LineBorder(colore, 5, true);
        this.setBorder(lineBorder);
    }
}
