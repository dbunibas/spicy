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

import it.unibas.spicy.model.exceptions.UnsatisfiableEGDException;
import it.unibas.spicy.model.generators.operators.CheckIntegerJoinValues;
import it.unibas.spicy.model.generators.operators.CheckSortForSkolems;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EngineConfiguration implements Cloneable {

    private static Log logger = LogFactory.getLog(EngineConfiguration.class);
    public static final int AUTO_SORT = -1;
    public static final int SORT = 1;
    public static final int NO_SORT = 0;
    public static final int AUTO_SKOLEM_TABLE = -1;
    public static final int SKOLEM_TABLE = 1;
    public static final int NO_SKOLEM_TABLE = 0;
    private MappingTask mappingTask;
    // TGD rewriting
    private boolean rewriteSubsumptions = true;
    private boolean rewriteCoverages = true;
    private boolean rewriteSelfJoins = true;
    private boolean rewriteOnlyProperHomomorphisms = true;
    // EGD rewriting
//    private boolean rewriteEGDs = false;
    private boolean rewriteOverlaps = false;
    private boolean rewriteSkolemsForEGDs = false;
    // SORUCE NULL REWRITING
    private boolean rewriteTgdsForSourceNulls = false;
    // Skolem functions
    private int sortStrategy = AUTO_SORT;
//    private int sortStrategy = SORT;
    private int skolemTableStrategy = AUTO_SKOLEM_TABLE;
    private Boolean useSortInSkolems;
    private Boolean useSkolemTable;
    private boolean useHashTextForSkolems = false;
    private boolean useSkolemStrings = false;
    private boolean useLocalSkolem = false;
    // SQL generation
    private boolean useCreateTableTargetExchange = true;
    private boolean useCreateTableSTExchange = true;
    private boolean debugMode = true;

    public EngineConfiguration(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public boolean useSortInSkolems() {
        if (useSortInSkolems == null) {
            if (sortStrategy == AUTO_SORT) {
                CheckSortForSkolems checkSort = new CheckSortForSkolems();
                this.useSortInSkolems = checkSort.needsRuntimeSortInSkolems(mappingTask);
            } else if (sortStrategy == SORT) {
                this.useSortInSkolems = true;
            } else if (sortStrategy == NO_SORT) {
                this.useSortInSkolems = false;
            }
        }
        return useSortInSkolems;
    }

    public int getSortStrategy() {
        return sortStrategy;
    }

    public void setSortStrategy(int sortStrategy) {
        if (sortStrategy < AUTO_SORT || sortStrategy > SORT) {
            throw new IllegalArgumentException("Illegal value for sort strategy: " + sortStrategy);
        }
        this.sortStrategy = sortStrategy;
        this.mappingTask.setModified(true);
    }

    public boolean useSkolemTable() {
        if (useSkolemTable == null) {
            if (skolemTableStrategy == AUTO_SKOLEM_TABLE) {
                CheckIntegerJoinValues checkJoins = new CheckIntegerJoinValues();
                this.useSkolemTable = checkJoins.hasJoinsOnIntegerValues(mappingTask);
            } else if (skolemTableStrategy == SKOLEM_TABLE) {
                this.useSkolemTable = true;
            } else if (skolemTableStrategy == NO_SKOLEM_TABLE) {
                this.useSkolemTable = false;
            }
        }
        return useSkolemTable;
    }

    public int getSkolemTableStrategy() {
        return skolemTableStrategy;
    }

    public void setSkolemTableStrategy(int skolemTableStrategy) {
        if (skolemTableStrategy < AUTO_SKOLEM_TABLE || skolemTableStrategy > SKOLEM_TABLE) {
            throw new IllegalArgumentException("Illegal value for skolem table strategy: " + skolemTableStrategy);
        }
        this.skolemTableStrategy = skolemTableStrategy;
        this.mappingTask.setModified(true);
    }

    public boolean useSkolemStrings() {
        return useSkolemStrings;
    }

    public void setUseSkolemStrings(boolean useSkolemStrings) {
        this.useSkolemStrings = useSkolemStrings;
        this.mappingTask.setModified(true);
    }

    public boolean useLocalSkolem() {
        return useLocalSkolem;
    }

    public void setUseLocalSkolem(boolean useLocalSkolem) {
        this.useLocalSkolem = useLocalSkolem;
        this.mappingTask.setModified(true);
    }

    public boolean useDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        this.mappingTask.setModified(true);
    }

    public void setNoRewriting() {
        this.rewriteSubsumptions = false;
        this.rewriteCoverages = false;
        this.rewriteSelfJoins = false;
    }

    public void setRewriting(EngineConfiguration config) {
        this.rewriteSubsumptions = config.rewriteSubsumptions;
        this.rewriteCoverages = config.rewriteCoverages;
        this.rewriteSelfJoins = config.rewriteSelfJoins;
    }

    public boolean noRewriting() {
        return !(this.rewriteSubsumptions || this.rewriteCoverages || this.rewriteSelfJoins);
    }

    public boolean rewriteCoverages() {
        return rewriteCoverages;
    }

    public void setRewriteCoverages(boolean rewriteCoverages) {
        this.rewriteCoverages = rewriteCoverages;
        this.mappingTask.setModified(true);
    }

    public void setRewriteEGDs(boolean rewrite) throws UnsatisfiableEGDException {
        this.rewriteOverlaps = rewrite;
        this.rewriteSkolemsForEGDs = rewrite;
        this.mappingTask.setModified(true);
    }

    public boolean rewriteEGDs() {
        return this.rewriteOverlaps && this.rewriteSkolemsForEGDs;
    }

    public boolean rewriteOverlaps() {
        return rewriteOverlaps;
    }

    public void setRewriteOverlaps(boolean rewriteOverlaps) {
        this.rewriteOverlaps = rewriteOverlaps;
    }

    public boolean rewriteSkolemsForEGDs() {
        return rewriteSkolemsForEGDs;
    }

    public void setRewriteSkolemsForEGDs(boolean rewriteSkolemsForEGDs) {
        this.rewriteSkolemsForEGDs = rewriteSkolemsForEGDs;
    }

    public boolean rewriteTgdsForSourceNulls() {
        return rewriteTgdsForSourceNulls;
    }

    public void setRewriteTgdsForSourceNulls(boolean rewriteTgdsForSourceNulls) {
        this.rewriteTgdsForSourceNulls = rewriteTgdsForSourceNulls;
        this.mappingTask.setModified(true);
    }
    
    public boolean rewriteSelfJoins() {
        return rewriteSelfJoins;
    }

    public void setRewriteSelfJoins(boolean rewriteSelfJoins) {
        this.rewriteSelfJoins = rewriteSelfJoins;
        this.mappingTask.setModified(true);
    }

    public boolean rewriteSubsumptions() {
        return rewriteSubsumptions;
    }

    public void setRewriteSubsumptions(boolean rewriteSubsumptions) {
        this.rewriteSubsumptions = rewriteSubsumptions;
        this.mappingTask.setModified(true);
    }

    public boolean rewriteOnlyProperHomomorphisms() {
        return rewriteOnlyProperHomomorphisms;
    }

    public void setRewriteOnlyProperHomomorphisms(boolean rewriteOnlyProperHomomorphisms) {
        this.rewriteOnlyProperHomomorphisms = rewriteOnlyProperHomomorphisms;
        this.mappingTask.setModified(true);
    }

    public boolean useCreateTableInSTExchange() {
        return useCreateTableSTExchange;
    }

    public void setUseCreateTableSTExchange(boolean useCreateTableSTExchange) {
        this.useCreateTableSTExchange = useCreateTableSTExchange;
        this.mappingTask.setModified(true);
    }

    public boolean useCreateTableInTargetExchange() {
        return useCreateTableTargetExchange;
    }

    public void setUseCreateTableTargetExchange(boolean useCreateTableTargetExchange) {
        this.useCreateTableTargetExchange = useCreateTableTargetExchange;
        this.mappingTask.setModified(true);
    }

    public boolean useHashTextForSkolems() {
        return useHashTextForSkolems;
    }

    public void setUseHashTextForSkolems(boolean useHashText) {
        this.useHashTextForSkolems = useHashText;
        this.mappingTask.setModified(true);
    }

    @Override
    public Object clone() {
        try {
            EngineConfiguration clone = (EngineConfiguration) super.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
            return null;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------------- Engine Configuration ------------\n");
        result.append("  Rewrite subsumptions: ").append(rewriteSubsumptions).append("\n");
        result.append("  Rewrite coverages: ").append(rewriteCoverages).append("\n");
        result.append("  Rewrite self-joins: ").append(rewriteSelfJoins).append("\n");
//        result.append("  Rewrite all homomorphisms: ").append(rewriteAllHomorphisms).append("\n");
        result.append("  Rewrite Overlaps: ").append(rewriteOverlaps).append("\n");
        result.append("  Rewrite Skolems for EGDs: ").append(rewriteSkolemsForEGDs).append("\n");
        result.append("  Skolem Strings: ").append(useSkolemStrings).append("\n");
        result.append("  Local Skolems: ").append(useLocalSkolem).append("\n");
        result.append("  Skolem Table Strategy: ").append(skolemTableStrategy).append("\n");
        result.append("  Sort strategy: ").append(sortStrategy).append("\n");
        result.append("------------------------------------------------\n");
        return result.toString();
    }
}
