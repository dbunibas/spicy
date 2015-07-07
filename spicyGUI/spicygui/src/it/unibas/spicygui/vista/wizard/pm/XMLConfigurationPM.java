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
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class XMLConfigurationPM {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String schemaPath;
    private String instancePath;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getSchemaPath() {
        return this.schemaPath;
    }

    public void setSchemaPath(String schemaPath) {
        String oldSchemaPath = this.schemaPath;
        this.schemaPath = schemaPath;
        fireEvents("schemaPath", oldSchemaPath, this.schemaPath);

    }

    public String getInstancePath() {
        return instancePath;
    }

    public void setInstancePath(String instancePath) {
        String oldInstancePath = this.instancePath;
        this.instancePath = instancePath;
        fireEvents("instancePath", oldInstancePath, this.instancePath);
    }

    public boolean checkFieldsForSource() {
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(null, "xml", "xsd");
        File percorso1 = null;
        File percorso2 = null;
        try {
            percorso1 = new File(this.schemaPath);
            percorso2 = new File(this.instancePath);
        } catch (NullPointerException ex) {
            return false;
        }
        if ((percorso1 != null) && (percorso2 != null)) {
            return (filtro.accept(percorso1) && filtro.accept(percorso2));
        }
        return false;
    }
    
    public boolean checkFieldsForTarget() {
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(null, "xml", "xsd");
        File percorso1 = null;
        try {
            percorso1 = new File(this.schemaPath);
        } catch (NullPointerException ex) {
            return false;
        }
        if (percorso1 != null) {
            return filtro.accept(percorso1);
        }
        return false;
    }
    
    private void fireEvents(String propertyName, String oldValue, String newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
}
        