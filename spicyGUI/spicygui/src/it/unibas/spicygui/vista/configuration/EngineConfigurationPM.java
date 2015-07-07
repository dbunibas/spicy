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
 
package it.unibas.spicygui.vista.configuration;

import it.unibas.spicy.model.mapping.EngineConfiguration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EngineConfigurationPM implements Cloneable{

    private static Log logger = LogFactory.getLog(EngineConfigurationPM.class);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected EngineConfiguration engineConfiguration;
    // SQL generation

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public EngineConfigurationPM(EngineConfiguration engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
    }

    public boolean isDebugMode() {
        return engineConfiguration.useDebugMode();
    }

    public void setDebugMode(boolean debugMode) {
        this.engineConfiguration.setDebugMode(debugMode);
    }

    public boolean isRewriteCoverages() {
        return engineConfiguration.rewriteCoverages();
    }

    public void setRewriteCoverages(boolean rewriteCoverages) {
        this.engineConfiguration.setRewriteCoverages(rewriteCoverages);
    }

    public boolean isRewriteEGDs() {
        return engineConfiguration.rewriteEGDs();
    }

    public void setRewriteEGDs(boolean rewriteEGDs) {
        this.engineConfiguration.setRewriteEGDs(rewriteEGDs);
    }

    public boolean isRewriteSelfJoins() {
        return engineConfiguration.rewriteSelfJoins();
    }

    public void setRewriteSelfJoins(boolean rewriteSelfJoins) {
        this.engineConfiguration.setRewriteSelfJoins(rewriteSelfJoins);
    }

    public boolean isRewriteSubsumptions() {
        return engineConfiguration.rewriteSubsumptions();
    }

    public void setRewriteSubsumptions(boolean rewriteSubsumptions) {
        this.engineConfiguration.setRewriteSubsumptions(rewriteSubsumptions);
    }

    public int getSortStrategy() {
        if (this.engineConfiguration.getSortStrategy() == EngineConfiguration.NO_SORT) {
            return 0;
        }
        if (this.engineConfiguration.getSortStrategy() == EngineConfiguration.SORT) {
            return 1;
        }
        return 2;
    }

    public void setSortStrategy(int sortStrategy) {
        if (sortStrategy == 0) {
            int oldSortStrategy = this.engineConfiguration.getSortStrategy();
            this.engineConfiguration.setSortStrategy(EngineConfiguration.NO_SORT);
            support.firePropertyChange("sortStrategy", oldSortStrategy, this.engineConfiguration.getSortStrategy());
        }
        if (sortStrategy == 1) {
            int oldSortStrategy = this.engineConfiguration.getSortStrategy();
            this.engineConfiguration.setSortStrategy(EngineConfiguration.SORT);
            support.firePropertyChange("sortStrategy", oldSortStrategy, this.engineConfiguration.getSortStrategy());
        }
        if (sortStrategy == 2) {
            int oldSortStrategy = this.engineConfiguration.getSortStrategy();
            this.engineConfiguration.setSortStrategy(EngineConfiguration.AUTO_SORT);
            support.firePropertyChange("sortStrategy", oldSortStrategy, this.engineConfiguration.getSortStrategy());
        }

    }

    public int getSkolemTableStrategy() {
        if (this.engineConfiguration.getSkolemTableStrategy() == EngineConfiguration.NO_SKOLEM_TABLE) {
            return 0;
        }
        if (this.engineConfiguration.getSkolemTableStrategy() == EngineConfiguration.SKOLEM_TABLE) {
            return 1;
        }
        return 2;
    }

    public void setSkolemTableStrategy(int skolemTableStrategy) {
        if (skolemTableStrategy == 0) {
            int oldSkolemTableStrategy = this.engineConfiguration.getSkolemTableStrategy();
            this.engineConfiguration.setSkolemTableStrategy(EngineConfiguration.NO_SKOLEM_TABLE);
            support.firePropertyChange("skolemTableStrategy", oldSkolemTableStrategy, this.engineConfiguration.getSkolemTableStrategy());
        }
        if (skolemTableStrategy == 1) {
            int oldSkolemTableStrategy = this.engineConfiguration.getSkolemTableStrategy();
            this.engineConfiguration.setSkolemTableStrategy(EngineConfiguration.SKOLEM_TABLE);
            support.firePropertyChange("skolemTableStrategy", oldSkolemTableStrategy, this.engineConfiguration.getSkolemTableStrategy());
        }
        if (skolemTableStrategy == 2) {
            int oldSkolemTableStrategy = this.engineConfiguration.getSkolemTableStrategy();
            this.engineConfiguration.setSkolemTableStrategy(EngineConfiguration.AUTO_SKOLEM_TABLE);
            support.firePropertyChange("skolemTableStrategy", oldSkolemTableStrategy, this.engineConfiguration.getSkolemTableStrategy());
        }

    }

    public boolean isUseCreateTableInSTExchange() {
        return engineConfiguration.useCreateTableInSTExchange();
    }

    public void setUseCreateTableInSTExchange(boolean useCreateTableInSTExchange) {
        this.engineConfiguration.setUseCreateTableSTExchange(useCreateTableInSTExchange);
    }

    public boolean isUseCreateTableInTargetExchange() {
        return engineConfiguration.useCreateTableInTargetExchange();
    }

    public void setUseCreateTableInTargetExchange(boolean useCreateTableInTargetExchange) {
        this.engineConfiguration.setUseCreateTableTargetExchange(useCreateTableInTargetExchange);
    }

    public boolean isUseHashTextForSkolems() {
        return engineConfiguration.useHashTextForSkolems();
    }

    public void setUseHashTextForSkolems(boolean useHashTextForSkolems) {
        this.engineConfiguration.setUseHashTextForSkolems(useHashTextForSkolems);
    }

    
    public void setUseSkolemStrings (boolean useSkolemString) {
        this.engineConfiguration.setUseSkolemStrings(useSkolemString); 
    }
    
    public boolean isUseSkolemStrings () {
        return this.engineConfiguration.useSkolemStrings();
    }

//    public void setRewriteAllHomomorphisms(boolean rewriteAllHomomorphisms) {
//        this.engineConfiguration.setRewriteAllHomorphisms(rewriteAllHomomorphisms);
//    }
//
//    public boolean isRewriteAllHomomorphisms() {
//        return this.engineConfiguration.rewriteAllHomomorphisms();
//    }

    public void setUseLocalSkolem(boolean useLocalSkolem) {
        this.engineConfiguration.setUseLocalSkolem(useLocalSkolem);
    }

    public boolean isUseLocalSkolem() {
        return this.engineConfiguration.useLocalSkolem();
    }

    @Override
    public EngineConfigurationPM clone() {
        try {
            EngineConfigurationPM clone = (EngineConfigurationPM) super.clone();
            clone.engineConfiguration = (EngineConfiguration) this.engineConfiguration.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return null;

    }

    public EngineConfiguration getEngineConfiguration () {
        return this.engineConfiguration;
    }
}
