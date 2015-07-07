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

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

public class ConnectionInfo {

    private Widget sourceWidget;
    private Widget targetWidget;
    private ConnectionWidget connectionWidget;
    private ValueCorrespondence valueCorrespondence;
    private VariableCorrespondence variableCorrespondence;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ConnectionInfo() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public double getConfidence() {
        return valueCorrespondence.getConfidence();
    }

    public void setConfidence(double confidence) {
        double oldConfidence = this.valueCorrespondence.getConfidence();
        this.valueCorrespondence.setConfidence(confidence);
        sparaEventi("confidence", oldConfidence, this.valueCorrespondence.getConfidence());
    }

    public ValueCorrespondence getValueCorrespondence() {
        return valueCorrespondence;
    }

    public void setValueCorrespondence(ValueCorrespondence valueCorrespondence) {
        this.valueCorrespondence = valueCorrespondence;
    }

    public VariableCorrespondence getVariableCorrespondence() {
        return variableCorrespondence;
    }

    public void setVariableCorrespondence(VariableCorrespondence variableCorrespondence) {
        this.variableCorrespondence = variableCorrespondence;
    }
    
    private void sparaEventi(String nomePropieta, Object vecchioValore, Object nuovoValore) {
        support.firePropertyChange(nomePropieta, vecchioValore, nuovoValore);
    }

    public Widget getSourceWidget() {
        return sourceWidget;
    }

    public void setSourceWidget(Widget sourceWidget) {
        this.sourceWidget = sourceWidget;
    }

    public Widget getTargetWidget() {
        return targetWidget;
    }

    public void setTargetWidget(Widget targetWidget) {
        this.targetWidget = targetWidget;
    }

    public ConnectionWidget getConnectionWidget() {
        return connectionWidget;
    }

    public void setConnectionWidget(ConnectionWidget connectionWidget) {
        this.connectionWidget = connectionWidget;
    }
}
