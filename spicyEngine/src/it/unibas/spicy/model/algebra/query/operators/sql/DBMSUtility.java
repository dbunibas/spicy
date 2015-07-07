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
 
package it.unibas.spicy.model.algebra.query.operators.sql;

import it.unibas.spicy.persistence.Types;

class DBMSUtility {

    private final static String SQL_TYPE_STRING = "text";
    private final static String SQL_TYPE_DATE = "date";
    private final static String SQL_TYPE_DOUBLE = "double precision";
    private final static String SQL_TYPE_INTEGER = "integer";
    private final static String SQL_TYPE_BOOLEAN = "bool";
    private final static String SQL_TYPE_DATETIME = "timestamp";
    private final static String CURRENT_TIME = "current_time";
    private final static String CURRENT_DATE = "current_date";
    private final static String NOW = "now";

    static String getSqlType(String spicyType) {
        String result = "";
        if (spicyType.equals(Types.STRING)) {
            result = SQL_TYPE_STRING;
        } else if (spicyType.equals(Types.DATE)) {
            result = SQL_TYPE_DATE;
        } else if (spicyType.equals(Types.DOUBLE)) {
            result = SQL_TYPE_DOUBLE;
        } else if (spicyType.equals(Types.INTEGER) || (spicyType.equals(Types.LONG))) {
            result = SQL_TYPE_INTEGER;
        } else if (spicyType.equals(Types.BOOLEAN)) {
            result = SQL_TYPE_BOOLEAN;
        } else if (spicyType.equals(Types.DATETIME)) {
            result = SQL_TYPE_DATETIME;
        }
        return result;
    }

    static String cast(Object value, String sqlType) {
        return "cast(" + value + " as " + sqlType + ")";
    }
}
