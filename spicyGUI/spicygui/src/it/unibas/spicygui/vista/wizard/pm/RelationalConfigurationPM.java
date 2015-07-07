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

import it.unibas.spicy.persistence.AccessConfiguration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RelationalConfigurationPM {

    private Log logger = LogFactory.getLog(RelationalConfigurationPM.class);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private AccessConfiguration accessConfiguration = new AccessConfiguration();
    private String sourceSchema;
    private String sourceInstance;
    private String targetSchema;
    private String tmpDatabaseName;

    public AccessConfiguration getAccessConfiguration() {
        return accessConfiguration;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getDriver() {
        return accessConfiguration.getDriver();
    }

    public void setDriver(String driver) {
        String oldDriver = this.accessConfiguration.getDriver();
        this.accessConfiguration.setDriver(driver);
        fireEvent("driver", oldDriver, driver);
    }

    public String getUri() {
        return this.accessConfiguration.getUri();
    }

    public void setUri(String uri) {
        String oldUri = this.accessConfiguration.getUri();
        this.accessConfiguration.setUri(uri);
        fireEvent("uri", oldUri, uri);
    }

    public String getLogin() {
        return this.accessConfiguration.getLogin();
    }

    public void setLogin(String login) {
        String oldLogin = this.accessConfiguration.getLogin();
        this.accessConfiguration.setLogin(login);
        fireEvent("login", oldLogin, login);
    }

    public String getPassword() {
        return this.accessConfiguration.getPassword();
    }

    public void setPassword(String password) {
        String oldPassword = this.accessConfiguration.getPassword();
        this.accessConfiguration.setPassword(password);
        fireEvent("password", oldPassword, password);
    }

    public String getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(String sourceInstance) {
        String oldInstance = this.sourceInstance;
        this.sourceInstance = sourceInstance;
        fireEvent("sourceInstance", oldInstance, sourceInstance);
    }

    public String getSourceSchema() {
        return sourceSchema;
    }

    public void setSourceSchema(String sourceSchema) {
        String oldSchema = this.sourceSchema;
        this.sourceSchema = sourceSchema;
        fireEvent("sourceSchema", oldSchema, sourceSchema);
    }

    public String getTargetSchema() {
        return targetSchema;
    }

    public void setTargetSchema(String targetSchema) {
        String oldSchema = this.targetSchema;
        this.targetSchema = targetSchema;
        fireEvent("targetSchema", oldSchema, targetSchema);
    }

    public String getTmpDatabaseName() {
        return tmpDatabaseName;
    }

    public void setTmpDatabaseName(String tmpDatabaseName) {
        String oldTmpDatabaseName = this.tmpDatabaseName;
        this.tmpDatabaseName = tmpDatabaseName;
        fireEvent("tmpDatabaseName", oldTmpDatabaseName, tmpDatabaseName);
    }

    public boolean checkFieldsAccessConfiguration() {
        return (this.getDriver() != null && !this.getDriver().equals("") &&
                this.getUri() != null && !this.getUri().equals("") &&
                this.getLogin() != null && !this.getLogin().equals(""));
    }

    public boolean checkFieldsAccessConfigurationAndPathFile() {
        if (checkFieldsAccessConfiguration()) {
            return (this.sourceSchema != null && !this.sourceSchema.equals("") &&
                    this.sourceInstance != null && !this.sourceInstance.equals("") &&
                    this.targetSchema != null && !this.targetSchema.equals(""));
        }
        return false;
    }

    public boolean checkFieldsAccessConfigurationPathFileAndRecreate() {
        if (checkFieldsAccessConfigurationAndPathFile()) {
            return (this.tmpDatabaseName != null && !this.tmpDatabaseName.equals(""));
        }
        return false;
    }

    private void fireEvent(String nomePropieta, String vecchioValore, String nuovoValore) {
        support.firePropertyChange(nomePropieta, vecchioValore, nuovoValore);
    }
}
