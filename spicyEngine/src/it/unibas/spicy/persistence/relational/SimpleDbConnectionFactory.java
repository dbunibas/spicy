/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Alessandro Pappalardo - pappalardo.alessandro@gmail.com
    Gianvito Summa - gianvito.summa@gmail.com

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
 
package it.unibas.spicy.persistence.relational;

import it.unibas.spicy.persistence.AccessConfiguration;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.relational.IConnectionFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleDbConnectionFactory implements IConnectionFactory {
    
    private static Log logger = LogFactory.getLog(SimpleDbConnectionFactory.class);
    
    private boolean initialized = false;
    
    public void reset() {
        this.initialized = false;
    }
    
    private void init(AccessConfiguration configuration) throws DAOException {
        try {
            Class.forName(configuration.getDriver());
            logger.debug("Driver initialized: " + configuration.getDriver());
        } catch (Exception e) {
            logger.fatal(" Wrong parameter in driver configuration: " + e);
            logger.fatal("Requested driver: " + configuration.getDriver());
            throw new DAOException(e.getMessage());
        }
        this.initialized = true;
    }
    
    public Connection getConnection(AccessConfiguration configuration) throws DAOException {
        init(configuration);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(configuration.getUri(),configuration.getLogin(),configuration.getPassword());
        } catch (SQLException sqle) {
            close(connection);
            throw new DAOException(" getConnection: " + sqle + "\n\ndriver: " + configuration.getDriver() + " - uri: " + configuration.getUri() + " - login: " + configuration.getLogin() + " - password: " +  configuration.getPassword() + "\n");
        }
        if(connection == null){
            throw new DAOException("Connection is NULL !"+ "\n\ndriver: " + configuration.getDriver() + " - uri: " + configuration.getUri() + " - login: " + configuration.getLogin() + " - password: " +  configuration.getPassword() + "\n");
        }
        return connection;
    }
    
    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            logger.fatal(sqle.toString());
        }
    }
    
    public void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException sqle) {
            logger.fatal(sqle.toString());
        }
    }
    
    public void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException sqle) {
            logger.fatal(sqle.toString());
        }
    }
    
}
