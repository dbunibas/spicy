/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com

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
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.algebra.query.operators.sql.IDBMSHandler;
import it.unibas.spicy.model.algebra.query.operators.sql.PostgresHandler;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.SetAlias;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;

public class MappingTask {

    private int type;
    private String fileName;
    private IDataSourceProxy sourceProxy;
    private IDataSourceProxy targetProxy;
    private List<ValueCorrespondence> valueCorrespondences = new ArrayList<ValueCorrespondence>();
    private List<ValueCorrespondence> candidateCorrespondences = new ArrayList<ValueCorrespondence>();
    private List<FORule> loadedTgds;
    private MappingData mappingData;
    private EngineConfiguration config;
    private boolean modified;
    private boolean toBeSaved;

    public MappingTask(DataSource source, DataSource target, int type) {
        IntegerOIDGenerator.clearCache();
        SetAlias.resetId();
        this.sourceProxy = new ConstantDataSourceProxy(source);
        this.targetProxy = new ConstantDataSourceProxy(target);
        this.config = new EngineConfiguration(this);
        checkType(type);
        this.type = type;
    }

    public MappingTask(DataSource source, DataSource target, List<ValueCorrespondence> valueCorrespondences) {
        this(source, target, SpicyEngineConstants.LINES_BASED_MAPPING_TASK);
        this.valueCorrespondences = valueCorrespondences;
    }

    public MappingTask(IDataSourceProxy sourceProxy, IDataSourceProxy targetProxy, int type) {
        IntegerOIDGenerator.clearCache();
        SetAlias.resetId();
        this.sourceProxy = sourceProxy;
        this.targetProxy = targetProxy;
        this.config = new EngineConfiguration(this);
        checkType(type);
        this.type = type;
    }

    public MappingTask(IDataSourceProxy sourceProvider, IDataSourceProxy targetProvider, List<ValueCorrespondence> valueCorrespondences) {
        this(sourceProvider, targetProvider, SpicyEngineConstants.LINES_BASED_MAPPING_TASK);
        this.valueCorrespondences = valueCorrespondences;
    }

    private static void checkType(int type) {
        if (type != SpicyEngineConstants.LINES_BASED_MAPPING_TASK
                && type != SpicyEngineConstants.TGD_BASED_MAPPING_TASK) {
            throw new IllegalArgumentException("Illegal value of attribute type for mapping task: " + type);
        }
    }

    public int getType() {
        return type;
    }

    public boolean isLoadedFromParser() {
        return this.loadedTgds != null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public EngineConfiguration getConfig() {
        return config;
    }

    public IDBMSHandler getDBMSHandler() {
        return new PostgresHandler();
    }

    public IDataSourceProxy getSourceProxy() {
        return this.sourceProxy;
    }

    public MappingData getMappingData() {
        if (this.mappingData == null || this.isModified()) {
            this.mappingData = new MappingData(this);
            this.modified = false;
        }
        return mappingData;
    }

    public boolean isModified() {
        return modified || this.sourceProxy.isModified() || this.targetProxy.isModified();
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        if (modified) {
            this.setToBeSaved(true);
        }
    }

    public boolean isToBeSaved() {
        return toBeSaved || this.sourceProxy.isToBeSaved() || this.targetProxy.isToBeSaved();
    }

    public void setToBeSaved(boolean toBeSaved) {
        this.toBeSaved = toBeSaved;
    }

    public IDataSourceProxy getTargetProxy() {
        return targetProxy;
    }

    public List<ValueCorrespondence> getValueCorrespondences() {
        return valueCorrespondences;
    }

    public void addCorrespondence(ValueCorrespondence correspondence) {
        assert (sourceProxy != null && targetProxy != null) : "Source and target cannot be null";
        this.valueCorrespondences.add(correspondence);
        setModified(true);
    }

    public void removeCorrespondence(ValueCorrespondence correspondence) {
        assert (sourceProxy != null && targetProxy != null) : "Source and target cannot be null";
        this.valueCorrespondences.remove(correspondence);
        setModified(true);
    }

    public void setValueCorrespondences(List<ValueCorrespondence> valueCorrespondences) {
        this.valueCorrespondences = valueCorrespondences;
        setModified(true);
    }

    public void clearCorrespondences() {
        this.valueCorrespondences.clear();
        setModified(true);
    }

    public List<FORule> getLoadedTgds() {
        return loadedTgds;
    }

    public void setLoadedTgds(List<FORule> loadedTgds) {
        this.loadedTgds = loadedTgds;
    }

    public List<ValueCorrespondence> getCandidateCorrespondences() {
        return candidateCorrespondences;
    }

    public void addCandidateCorrespondence(ValueCorrespondence correspondence) {
        assert (sourceProxy != null && targetProxy != null) : "Source and target cannot be null";
        this.candidateCorrespondences.add(correspondence);
        setModified(true);
    }

    public void removeCandidateCorrespondence(ValueCorrespondence correspondence) {
        assert (sourceProxy != null && targetProxy != null) : "Source and target cannot be null";
        this.candidateCorrespondences.remove(correspondence);
        setModified(true);
    }

    public void setCandidateCorrespondences(List<ValueCorrespondence> candidateCorrespondences) {
        this.candidateCorrespondences = candidateCorrespondences;
        setModified(true);
    }

    public void clearCandidateCorrespondences() {
        this.candidateCorrespondences.clear();
        setModified(true);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("=============================== MAPPING TASK ================================\n");
        result.append(config);
        result.append("Source:\n").append(this.sourceProxy.toLongString()).append("\n");
        result.append("Target Schema:\n").append(this.targetProxy.toLongString()).append("\n");
        if (!this.valueCorrespondences.isEmpty()) {
            result.append("================ Correspondences ===================\n");
            for (ValueCorrespondence correspondence : this.valueCorrespondences) {
                result.append(correspondence).append("\n");
            }
        }
        if (this.loadedTgds != null && !this.loadedTgds.isEmpty()) {
            result.append("================ Loaded Tgds ===================\n");
            for (FORule tgd : this.loadedTgds) {
                result.append(tgd).append("\n");
            }
        }
        return result.toString();
    }
}
