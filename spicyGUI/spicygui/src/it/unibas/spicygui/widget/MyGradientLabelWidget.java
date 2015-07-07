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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

public class MyGradientLabelWidget extends LabelWidget {

    private Color colorStart;
    private Color colorEnd;

    public MyGradientLabelWidget(Scene scene) {
        super(scene);
    }

    public MyGradientLabelWidget(Scene scene, String name) {
        super(scene, name);
    }

    public MyGradientLabelWidget(Scene scene, String name, Color colorStart, Color colorEnd) {
        super(scene, name);
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        Graphics2D gr = getGraphics();
        Rectangle bounds = getBounds();
        if (this.colorStart == null && this.colorEnd == null) {
            this.colorStart = Color.white;
            this.colorEnd = Color.blue;
        }

        //drawGradient(gr, bounds, colorStart, colorEnd, 0f, 0f);
        drawGradient(gr, bounds, colorStart, colorEnd, 0.3f, 0.764f);
//        drawGradient(gr, bounds, color3, color4, 0.764f, 0.927f);
//        drawGradient(gr, bounds, color4, color5, 0.927f, 1f);

    }

    private void drawGradient(Graphics2D gr, Rectangle bounds, Color color2, Color color1, float y1, float y2) {
        y1 = bounds.y + y1 * bounds.height;
        y2 = bounds.y + y2 * bounds.height;
        //    GradientPaint gradientPaint = new GradientPaint(bounds.x, y1, color1, bounds.x + bounds.width + 10, bounds.y + bounds.height + 10, color2);
        GradientPaint gradientPaint = new GradientPaint((bounds.x + bounds.width) /2, bounds.y, color1, (bounds.x + bounds.width)/2, bounds.y + bounds.height, color2);
        gr.setPaint(gradientPaint);
        //
        gr.fill(new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height));
    }
}
