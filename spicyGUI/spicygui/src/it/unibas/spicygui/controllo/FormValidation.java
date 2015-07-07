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
 
package it.unibas.spicygui.controllo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FormValidation {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private static Log logger = LogFactory.getLog(FormValidation.class);
    private boolean buttonState;
    private boolean textFieldState;
    private boolean comboBoxState;

    public FormValidation(boolean buttonState) {
        this.buttonState = buttonState;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public boolean getButtonState() {
        return buttonState;
    }

    public void setButtonState(boolean buttonState) {
        boolean oldButtonState = this.buttonState;
        this.buttonState = buttonState;
        support.firePropertyChange("buttonState", oldButtonState, buttonState);
    }

    public boolean getTextFieldState() {
        return textFieldState;
    }

    public void setTextFieldState(boolean textFieldState) {
        boolean oldTextFieldState = this.textFieldState;
        this.textFieldState = textFieldState;
        support.firePropertyChange("textFieldState", oldTextFieldState, textFieldState);
    }

    public boolean getComboBoxState() {
        return comboBoxState;
    }

    public void setComboBoxState(boolean comboBoxState) {
        boolean oldComboBoxState = this.comboBoxState;
        this.comboBoxState = comboBoxState;
        support.firePropertyChange("comboBoxState", oldComboBoxState, comboBoxState);
    }
}
