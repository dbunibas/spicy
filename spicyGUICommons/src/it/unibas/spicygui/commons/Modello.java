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

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class Modello {

    private Map<String, Object> mappa = new HashMap<String, Object>();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void putBean(String chiave, Object bean) {

        Object oldBean = mappa.get(chiave);
        mappa.put(chiave, bean);
        support.firePropertyChange("bean", oldBean, bean);
    }

    public Object getBean(String chiave) {
        return mappa.get(chiave);
    }

    public Object removeBean(String chiave) {
        return mappa.remove(chiave);
    }

    public Map<String, Object> getMappa() {
        return mappa;
    }
}
