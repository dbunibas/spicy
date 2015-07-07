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
 
package it.unibas.spicygui.widget.caratteristiche;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.Widget;

public class ConstraintsWidgetCollections {

    private static Log logger = LogFactory.getLog(ConstraintsWidgetCollections.class);
    private Map<ConstraintPoint, Widget> constraintsMap = new HashMap<ConstraintPoint, Widget>();

    public void putWidget(ConstraintPoint point, Widget widget) {
        this.constraintsMap.put(point, widget);
    }

    public Widget getWidget(ConstraintPoint point) {
        return this.constraintsMap.get(point);
    }



    public void calculateFreePoint(ConstraintPoint point) {
        if (this.constraintsMap.get(point) != null) {
            checkRangePointOverlap(point);
        }

    }

    private void checkRangePointOverlap(ConstraintPoint point) {
        boolean flag = true;
        int contatore = 2;
        while (flag && contatore < 6) {
            point.y += contatore;
            if (constraintsMap.get(point) == null) {
                return;
            }
            point.y -= (contatore + contatore);
            if (constraintsMap.get(point) == null) {
                return;
            }
            point.y += contatore;
            contatore += 2;
        }
    }
}
