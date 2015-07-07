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
 
package it.unibas.spicy.attributematch.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.Application;
import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOComaCorrespondences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatchAttributes1to1Coma implements IMatchAttributes {

    private Log logger = LogFactory.getLog(this.getClass());
    private static final String DIRECTORY_PATH = "/resources/coma/";
    @Inject()
    @Named(Application.MIN_SAMPLE_SIZE_KEY)
    private int minSampleSize;
    private DAOComaCorrespondences daoComa = new DAOComaCorrespondences();

    public MatchAttributes1to1Coma() {
    }

    public MatchAttributes1to1Coma(int minSampleSize) {
        this.minSampleSize = minSampleSize;
    }

    public void findMatches(MappingTask mappingTask) {
        AttributeCorrespondences candidateCorrespondences = findMatches(mappingTask.getSourceProxy().getDataSource(), mappingTask.getTargetProxy().getDataSource());
        mappingTask.setCandidateCorrespondences(candidateCorrespondences.getCorrespondences());
    }

    public AttributeCorrespondences findMatches(DataSource source, DataSource target) {
        try {
            return daoComa.loadComaAttributeCorrespondences(buildComaLogFile(source, target), source, target);
        } catch (DAOException ex) {
            logger.error(ex);
            return null;
        }
    }

    private String buildComaLogFile(DataSource source, DataSource target) {
        return DIRECTORY_PATH + source.getSchema().getLabel() + "-" + target.getSchema().getLabel() + "-comaLog.txt";
    }

    public void clearCache() {
    }
}
