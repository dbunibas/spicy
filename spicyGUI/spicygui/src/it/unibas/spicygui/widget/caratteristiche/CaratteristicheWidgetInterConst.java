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

import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.controllo.FormValidation;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.Widget;

public class CaratteristicheWidgetInterConst implements ICaratteristicheWidget, Cloneable {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private static Log logger = LogFactory.getLog(CaratteristicheWidgetInterConst.class);
    private String treeType;
    private Widget widgetBarra;
    private boolean tipoStringa;
    private boolean tipoNumero;
    private boolean tipoFunzione;
    private Object costante;
    private FormValidation formValidation;
    private List<ConnectionInfo> connectionList = new ArrayList<ConnectionInfo>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getTreeType() {
        return this.treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public Object getCostante() {
        return costante;
    }

    public void setCostante(Object costante) {
        Object oldCostante = this.costante;
        this.costante = costante;
        fireEvent("costante", oldCostante, this.costante);
    }

    public boolean getTipoStringa() {
        return tipoStringa;
    }

    public void setTipoStringa(boolean tipoStringa) {
        boolean oldTipoStringa = this.tipoStringa;
        this.tipoStringa = tipoStringa;
        fireEvent("tipoStringa", oldTipoStringa, this.tipoStringa);
    }

    public boolean getTipoNumero() {
        return tipoNumero;
    }

    public void setTipoNumero(boolean tipoNumero) {
        boolean oldTipoNumero = this.tipoNumero;
        this.tipoNumero = tipoNumero;
        fireEvent("tipoNumero", oldTipoNumero, this.tipoNumero);
    }

    public boolean getTipoFunzione() {
        return tipoFunzione;
    }

    public void setTipoFunzione(boolean tipoFunzione) {
        boolean oldTipoFunzione = this.tipoFunzione;
        this.tipoFunzione = tipoFunzione;
        fireEvent("tipoFunzione", oldTipoFunzione, this.tipoFunzione);
    }

    private void fireEvent(String nomePropieta, Object vecchioValore, Object nuovoValore) {
        // spara gli eventi  di binding
        support.firePropertyChange(nomePropieta, vecchioValore, nuovoValore);
    }

    @Override
    public CaratteristicheWidgetInterConst clone() {
        try {
            return (CaratteristicheWidgetInterConst) super.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return null;
    }

    public List<ConnectionInfo> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<ConnectionInfo> connectionList) {
        this.connectionList = connectionList;
    }

    public void addConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionList.add(connectionInfo);
    }

    public void removeConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionList.remove(connectionInfo);
    }

    public FormValidation getFormValidation() {
        return formValidation;
    }

    public void setFormValidation(FormValidation formValidation) {
        this.formValidation = formValidation;
    }

    public Widget getWidgetBarra() {
        return widgetBarra;
    }

    public void setWidgetBarra(Widget widgetBarra) {
        this.widgetBarra = widgetBarra;
    }
}
