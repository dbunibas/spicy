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
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.ImageUtilities;

public class ConnectionInfoWidget extends Widget {

    private IconNodeWidget rootWidget;
    private LabelWidget labelValueConfidenceWidget;

    public ConnectionInfoWidget(Scene scene) {
        super(scene);
        creaWidget(scene);
        settaAzioni();
    }

    private void creaWidget(Scene scene) {
        creaRootWidget(scene);
        MyGradientLabelWidget gradientHeaderWidget = creaGradientHeader(scene);

        ImageWidget imageWidget = new ImageWidget(scene, ImageUtilities.loadImage(Costanti.ICONA_INFORMATION));
        gradientHeaderWidget.addChild(imageWidget);


        LabelWidget footerWidgetConfidence = new LabelWidget(scene);
        footerWidgetConfidence.setLayout(LayoutFactory.createHorizontalFlowLayout());
        LabelWidget labelTypeConfidenceWidget = new LabelWidget(scene, "Cnf: ");
        labelValueConfidenceWidget = new LabelWidget(scene);
        footerWidgetConfidence.addChild(labelTypeConfidenceWidget);
        footerWidgetConfidence.addChild(labelValueConfidenceWidget);

        rootWidget.addChild(gradientHeaderWidget);
        rootWidget.addChild(footerWidgetConfidence);

        //this.setLayout(LayoutFactory.createOverlayLayout());
        this.addChild(rootWidget);
        this.setOpaque(true);

    }

    public LabelWidget getLabelValueConfidenceWidget() {
        return labelValueConfidenceWidget;
    }
    

    private void settaAzioni() {
        getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {

            public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
                return true;
            }

            public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
                return true;
            }

            public void select(Widget widget, Point arg1, boolean arg2) {
                widget.getParentWidget().bringToFront();
                widget.getScene().validate();
            }
        }));
        
        getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {

            public JPopupMenu getPopupMenu(Widget arg0, Point arg1) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }));
    }

    private void creaRootWidget(Scene scene) {
        rootWidget = new IconNodeWidget(scene);
        rootWidget.setLayout(LayoutFactory.createVerticalFlowLayout());
        rootWidget.removeChildren();
//        rootWidget.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private MyGradientLabelWidget creaGradientHeader(Scene scene) {
        float[] colori = new float[3];
        float[] coloreBordo = new float[3];
        float[] coloreEnd = new float[3];
        Color.RGBtoHSB(222, 236, 246, coloreEnd);
        Color.RGBtoHSB(186, 205, 240, coloreBordo);
        Color.RGBtoHSB(241, 253, 254, colori);
        Color colorEndVMD = Color.getHSBColor(coloreEnd[0], coloreEnd[1], coloreEnd[2]);
        Color colorBordo = Color.getHSBColor(coloreBordo[0], coloreBordo[1], coloreBordo[2]);
        Color colorEnd = Color.getHSBColor(colori[0], colori[1], colori[2]);
        MyGradientLabelWidget gradientHeaderWidget = new MyGradientLabelWidget(scene, "Funct", Color.WHITE, colorEndVMD);
        gradientHeaderWidget.setLayout(LayoutFactory.createOverlayLayout());
        gradientHeaderWidget.setPreferredSize(new Dimension(50, 20));
      //  gradientHeaderWidget.setBorder(BorderFactory.createLineBorder(colorBordo));
        rootWidget.setBorder(BorderFactory.createLineBorder(colorBordo));
        return gradientHeaderWidget;
    }
}
