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
 
package it.unibas.spicygui.commons;

import java.util.Observable;

public class LastActionBean extends Observable {

    public static final String NEW = "new";
    public static final String OPEN = "open";
    public static final String CLOSE = "close";
    public static final String SOLVE = "solve";
    public static final String TRANSLATE = "translate";
    public static final String SOLVE_AND_TRANSLATE = "solve_and_translate";
    public static final String NO_SCENARIO_SELECTED ="NO_SCENARIO_SELECTED";
    public static final String CREATE_CANDIDATE = "create_candidate";
    public static final String RANK_TRANSFORMATIONS = "rank_transformations";
    public static final String SPICY = "spicy";
    public static final String TGD_SESSION = "tgd_session";
    
    private Object lastAction;

    public Object getLastAction() {
        return lastAction;
    }

    public void setLastAction(Object lastAction) {
        Object oldLastAction = this.lastAction;
        this.lastAction = lastAction;
        setChanged();
        notifyObservers(lastAction);
    }
}
