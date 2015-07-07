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
 
package it.unibas.spicy.persistence.object;

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.object.model.ObjectModel;
import it.unibas.spicy.persistence.object.operators.GenerateObjectDataSource;
import it.unibas.spicy.persistence.object.operators.GenerateObjectInstance;
import it.unibas.spicy.persistence.object.operators.GenerateObjectModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOObject {

    protected static Log logger = LogFactory.getLog(DAOObject.class);
    private GenerateObjectDataSource dataSourceGenerator = new GenerateObjectDataSource();
    private GenerateObjectInstance instanceGenerator = new GenerateObjectInstance();
    private GenerateObjectModel objectModelGenerator = new GenerateObjectModel();
    
    public IDataSourceProxy generateDataSource(String classPathFolder, String objectModelFactoryName) throws DAOException {
        IDataSourceProxy dataSource = null;
        try {
            addFolderToClassPath(classPathFolder);
            IObjectModelFactory objectModelFactory = (IObjectModelFactory) Class.forName(objectModelFactoryName).newInstance();
            ObjectModel objectModel = objectModelFactory.getObjectModel();
            dataSource = generateDataSource(objectModel);
            dataSource.setType(SpicyEngineConstants.TYPE_OBJECT);
            dataSource.addAnnotation(SpicyEngineConstants.CLASSPATH_FOLDER, classPathFolder);
            dataSource.addAnnotation(SpicyEngineConstants.OBJECT_MODEL_FACTORY, objectModelFactoryName);
        } catch (Exception ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
        return dataSource;
    }

    public void addFolderToClassPath(String folderPath) throws DAOException {
        //TODO: handle classpath
    }

    private IDataSourceProxy generateDataSource(ObjectModel objectModel) throws DAOException {
        try {
            List<Class> listOfClasses = new ArrayList<Class>();
            for (String className : objectModel.getClassNames()) {
                listOfClasses.add(Class.forName(className));
                if (logger.isDebugEnabled()) logger.debug("Adding class: " + className + " to collection");
                if (logger.isDebugEnabled()) logger.debug("Added class: " + className + " to collection");
            }
            IDataSourceProxy dataSource = dataSourceGenerator.generateDataSource(objectModel.getSchemaName(), listOfClasses);
            //if (logger.isDebugEnabled()) logger.debug("Data Source: \n" + dataSource.toLongString());
            if (objectModel.getObjects() != null) {
                generateInstance(dataSource, objectModel.getObjects());
            }
            return dataSource;
        } catch (Throwable ex) {
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void generateInstance(IDataSourceProxy dataSource, List<Object> objects) throws DAOException {
        try {
            instanceGenerator.addInstance(dataSource, objects);
        } catch (Throwable exception) {
            throw new DAOException(exception.getMessage());
        }
    }

    public ObjectModel extractObjects(IDataSourceProxy dataSource) throws DAOException {
        try {
            return objectModelGenerator.generate(dataSource);
        } catch (Throwable exception) {
            throw new DAOException(exception.getMessage());
        }
    }
}
