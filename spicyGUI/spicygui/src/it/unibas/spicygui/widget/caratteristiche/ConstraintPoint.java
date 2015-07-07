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

import java.awt.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConstraintPoint extends Point{
    
    private static Log logger = LogFactory.getLog(ConstraintPoint.class);

    public ConstraintPoint(Point p) {
        super(p);
    }

    public ConstraintPoint() {
    }
    
    @Override
    public int hashCode() {
        return this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConstraintPoint other = (ConstraintPoint) obj;
        return other.x == this.x && other.y == this.y;
    }
    
}
