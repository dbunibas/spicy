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
 
package it.unibas.spicy.persistence;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOMappingTask {

    private static final Log logger = LogFactory.getLog(DAOMappingTask.class);
    
    private final DAOMappingTaskLines daoLines = new DAOMappingTaskLines();
    private final DAOMappingTaskTgds daoTgds = new DAOMappingTaskTgds();

    public MappingTask loadMappingTask(String filePath) throws DAOException {
        if (filePath.endsWith(".xml")) {
            return daoLines.loadMappingTask(filePath);
        } else if (filePath.endsWith(".tgd")) {
            return daoTgds.loadMappingTask(filePath);
        }
        throw new IllegalArgumentException("Illegal file name for mapping task: " + filePath);
    }

    public MappingTask loadMappingTask(String filePath, int type) throws DAOException {
        if (type == SpicyEngineConstants.LINES_BASED_MAPPING_TASK) {
            return daoLines.loadMappingTask(filePath);
        } else if (type == SpicyEngineConstants.TGD_BASED_MAPPING_TASK) {
            return daoTgds.loadMappingTask(filePath);
        }
        throw new IllegalArgumentException("Illegal value for mapping task type: " + type);
    }

    public void saveMappingTask(MappingTask mappingTask, String filePath) throws DAOException {
        if (mappingTask.getType() == SpicyEngineConstants.LINES_BASED_MAPPING_TASK && !filePath.endsWith(".tgd")) {
            daoLines.saveMappingTask(mappingTask, filePath);
        } else if (mappingTask.getType() == SpicyEngineConstants.TGD_BASED_MAPPING_TASK || filePath.endsWith(".tgd")) {
            daoTgds.saveMappingTask(mappingTask, filePath);
        }
    }

}