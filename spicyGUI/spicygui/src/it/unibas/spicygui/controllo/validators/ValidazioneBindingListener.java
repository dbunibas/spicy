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

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.FormValidation;
import javax.swing.JLabel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.openide.util.NbBundle;

public class ValidazioneBindingListener extends AbstractBindingListener {

    private static Log logger = LogFactory.getLog(ValidazioneBindingListener.class);
    private FormValidation formValidation;
    private JLabel etichettaErrori = null;
    private String nomeBinding = null;

    public ValidazioneBindingListener(JLabel etichettaErrori, String nomeBinding, FormValidation formValidation) {
        if (etichettaErrori == null || nomeBinding == null || formValidation == null) {
            throw new IllegalArgumentException();
        }
        this.etichettaErrori = etichettaErrori;
        this.nomeBinding = nomeBinding;
        this.formValidation = formValidation;
    }

    public ValidazioneBindingListener(JLabel etichettaErrori, String nomeBinding) {
        if (etichettaErrori == null || nomeBinding == null) {
            throw new IllegalArgumentException();
        }
        this.etichettaErrori = etichettaErrori;
        this.nomeBinding = nomeBinding;
    }

    @Override
    public void syncFailed(Binding binding, SyncFailure fail) {
        if (binding.getName().equals(this.nomeBinding)) {
            String description;
            if ((fail != null) && (fail.getType() == Binding.SyncFailureType.VALIDATION_FAILED)) {
                description = fail.getValidationResult().getDescription();
            } else {
                description = NbBundle.getMessage(Costanti.class, Costanti.ERROR_DATE_TYPE);
            }
            if (formValidation != null) {
                formValidation.setButtonState(false);
            }
            String msg = "[" + binding.getName() + "] " + description;
            this.etichettaErrori.setText(msg);
        }
    }

    @Override
    public void synced(Binding binding) {
        if (binding.getName().equals(this.nomeBinding)) {
            String bindName = binding.getName();
            String msg = "[" + bindName + "] Synced";
            this.etichettaErrori.setText("");
        }
    }
}
