/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it

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
 
package it.unibas.spicy.model.datasource.values;

import java.util.HashMap;
import java.util.Map;

public class IntegerOIDGenerator implements IOIDGeneratorStrategy {

    private static Integer counter = 0;
    private static Map<String, OID> cache = new HashMap<String, OID>();

    public static void clearCache() {
        cache.clear();
    }

    public static void resetCounter() {
        counter = 0;
    }

    public OID generateOIDForSkolemString(String string) {
        OID oid = cache.get(string);
        if (oid == null) {
            oid = getNextOID();
            oid.setSkolemString(string);
            cache.put(string, oid);
        }
        return oid;
    }
        
    public OID getNextOID() {
        return new OID(counter++);
    }
    
}
