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
 
package it.unibas.spicygui.vista.wizard.pm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewMappingTaskPM {

    private static Log logger = LogFactory.getLog(NewMappingTaskPM.class);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    String sourceElement = "";
    String targetElement = "";

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getSourceElement() {
        return sourceElement;
    }

    public void setSourceElement(String sourceElement) {
        String oldSourceIndex = this.sourceElement;
        this.sourceElement = sourceElement;
        fireEvent("sourceIndex", oldSourceIndex, sourceElement);
    }

    public String getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(String targetElement) {
        String oldTargetIndex = this.targetElement;
        this.targetElement = targetElement;
        fireEvent("targetIndex", oldTargetIndex, targetElement);
    }

    private void fireEvent(String nomePropieta, Object vecchioValore, Object nuovoValore) {
        support.firePropertyChange(nomePropieta, vecchioValore, nuovoValore);
    }
}
