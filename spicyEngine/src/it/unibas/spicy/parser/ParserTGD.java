/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com

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
 
package it.unibas.spicy.parser;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import java.util.ArrayList;
import java.util.List;

public class ParserTGD {

    private ParserView sourceView;
    private List<ParserView> negatedSourceViews = new ArrayList<ParserView>();
    private ParserView targetView;
    private List<ValueCorrespondence> correspondences = new ArrayList<ValueCorrespondence>();

    public void addCorrespondence(ValueCorrespondence correspondence) {
        this.correspondences.add(correspondence);
    }

    public List<ValueCorrespondence> getCorrespondences() {
        return correspondences;
    }

    public ParserView getSourceView() {
        return sourceView;
    }

    public List<ParserView> getNegatedSourceViews() {
        return negatedSourceViews;
    }

    public ParserView getTargetView() {
        return targetView;
    }

    public void setSourceView(ParserView sourceView) {
        this.sourceView = sourceView;
    }

    public void addNegatedSourceView(ParserView negatedSourceView) {
        this.negatedSourceViews.add(negatedSourceView);
    }

    public void setTargetView(ParserView targetView) {
        this.targetView = targetView;
    }

    public String toString() {
        String result = "for each " + sourceView;
        for (ParserView negatedsourceView : negatedSourceViews) {
            result += negatedsourceView;
        }
        result += targetView;
        return result;
//
//        return "for each " + sourceView + negatedSourceView + " -> " + targetView;
    }
}
