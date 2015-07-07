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

import it.unibas.spicy.model.datasource.values.NullValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Types {

    private static Log logger = LogFactory.getLog(Types.class);
    public final static String BOOLEAN = "boolean";
    public final static String STRING = "string";
    public final static String INTEGER = "integer";
    public final static String LONG = "long";
    public final static String DOUBLE = "double";
    public final static String DATE = "date";
    public final static String DATETIME = "datetime";
    public final static String ANY = "any";

    public static Object getTypedValue(String type, Object value) throws DAOException {
        if (value == null || value.toString().equalsIgnoreCase("NULL")) {
            return NullValueFactory.getNullValue();
        }
        if (type.equals(BOOLEAN)) {
            return Boolean.parseBoolean(value.toString());
        }
        if (type.equals(STRING)) {
            return value.toString();
        }
        if (type.equals(INTEGER)) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ex) {
                logger.error(ex);
                throw new DAOException(ex.getMessage());
            }
        }
        if (type.equals(DOUBLE)) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ex) {
                logger.error(ex);
                throw new DAOException(ex.getMessage());
            }
        }
        if (type.equals(DATE) || type.equals(DATETIME)) {
            return value.toString();
//            try {
//                return DateFormat.getDateInstance().parse(value.toString());
//            } catch (ParseException ex) {
//                logger.error(ex);
//                throw new DAOException(ex.getMessage());
//            }
        }
        return value.toString();
    }

}
