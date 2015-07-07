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
 
package it.unibas.spicygui.controllo.validators;

import it.unibas.spicygui.controllo.FormValidation;
import org.jdesktop.beansbinding.Validator;

public class ValidatoreConstantStrNum extends Validator{

    private FormValidation formValidation;
    
    public ValidatoreConstantStrNum() {
    }
    
    @Override
    public Validator.Result validate(Object arg0) {
        if (arg0.equals(true)) {
            formValidation.setComboBoxState(false);
            formValidation.setTextFieldState(true);
        }
        return null;
    }

    public void setFormValidation(FormValidation formValidation) {
        this.formValidation = formValidation;
    }

}
