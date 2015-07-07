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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LineCoordinatesCollections {

    private static Log logger = LogFactory.getLog(LineCoordinatesCollections.class);
    private List<LineCoordinates> listLineCoordinates = new ArrayList<LineCoordinates>();
    private static final int OFFSET_X = 5;
    private static final int OFFSET_OVERLAP_X = 3;

    public void addLineCoordinates(LineCoordinates lineCoordinates) {
        this.listLineCoordinates.add(lineCoordinates);
    }

    public LineCoordinates calculateFreeCoordinate(LineCoordinates lineCoordinates) {
        boolean flag = true;
        List<LineCoordinates> overlapLines = generateOverlapLines(lineCoordinates);
        while (flag) {
            if (!findOverlapX(overlapLines, lineCoordinates)) { 
                flag = false;
            }
        }
        return lineCoordinates;
    }

    private boolean findOverlapX(List<LineCoordinates> overlapLines, LineCoordinates lineCoordinates) {
        for (LineCoordinates currentLine : overlapLines) {
            if (chechOverlapX(currentLine, lineCoordinates.getTopPoint())) {
                overlapLines.remove(currentLine);
                lineCoordinates.increaseXValue(OFFSET_X);
                return true;
            }
        }
        return false;
    }

    private List<LineCoordinates> generateOverlapLines(LineCoordinates lineCoordinates) {
        List<LineCoordinates> overlapLines = new ArrayList<LineCoordinates>();
        for (LineCoordinates currentLine : listLineCoordinates) {
            if (checkOverlap(lineCoordinates, currentLine)) {
                overlapLines.add(currentLine);
            }
        }
        return overlapLines;
    }

    private boolean checkOverlap(LineCoordinates lineCoordinates, LineCoordinates currentLine) {
        Point topPoint = lineCoordinates.getTopPoint();
        Point bottomPoint = lineCoordinates.getBottomPoint();
        Point currentTopPoint = currentLine.getTopPoint();
        Point currentBottomPoint = currentLine.getBottomPoint();
        return ((chechOverlapY(currentLine, bottomPoint) || chechOverlapY(currentLine, topPoint)) ||
                (chechOverlapY(lineCoordinates, currentBottomPoint) || chechOverlapY(lineCoordinates, currentTopPoint)));
    }

    private boolean chechOverlapY(LineCoordinates currentLine, Point point) {
        Point topPoint = currentLine.getTopPoint();
        Point bottomPoint = currentLine.getBottomPoint();
        if (logger.isTraceEnabled()) logger.trace("top y: " + point.y + " -- " + topPoint.y + "y bottom: " + point.y + " -- " + bottomPoint.y);
        return (point.y >= topPoint.y && point.y <= bottomPoint.y);
    }

    private boolean chechOverlapX(LineCoordinates currentLine, Point point) {
        Point topPoint = currentLine.getTopPoint();
        int offset = Math.abs(point.x - topPoint.x);
        return (offset <= OFFSET_OVERLAP_X);
    }
}
