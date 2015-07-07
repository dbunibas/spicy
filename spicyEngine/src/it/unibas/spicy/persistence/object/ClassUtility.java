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

import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.PersistentProperty;
import it.unibas.spicy.persistence.object.model.annotations.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassUtility {

    private static Log logger = LogFactory.getLog(ClassUtility.class);

    private static final String DOT_REPLACEMENT = "_";
    
    public static String encodeClassName(String className) {
        return className.replaceAll("\\.", DOT_REPLACEMENT);
    }

    public static String decodeClassName(String className) {
        return className.replaceAll(DOT_REPLACEMENT, ".");
    }
    
    public static String convertFirstCapitalLetter(String stringToConvert) {
        String firstChar = stringToConvert.substring(0, 1);
        String firstCharCapital = firstChar.toUpperCase();
        String convertedFieldName = firstCharCapital + stringToConvert.substring(1, stringToConvert.length());
        return convertedFieldName;
    }

    public static String generateGetMethodName(String propertyName) {
        return ("get" + convertFirstCapitalLetter(propertyName));
    }

    public static String generateSetMethodName(String propertyName) {
        return ("set" + convertFirstCapitalLetter(propertyName));
    }

    public static String extractSimpleClassName(String className) {
        if (className.contains(DOT_REPLACEMENT)) {
            className = decodeClassName(className);
        }
        String result = className.substring(className.lastIndexOf(".") + 1);
        return result;
    }

    public static boolean isAReferenceCollection(Field field) {
        Class type = field.getType();
        return findInterface(type, java.util.Collection.class);
    }

    private static boolean findInterface(Class currentClass, Class currentInterface) {
        Class[] interfaces = currentClass.getInterfaces();
        for (Class class1 : interfaces) {
            if (class1.equals(currentInterface)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTransient(Field field) {
        return field.isAnnotationPresent(Transient.class);
    }

    public static Object invokeGetMethod(String methodName, Object object) {
        try {
            Object result = null;
            Method method = object.getClass().getMethod(methodName);
            result = method.invoke(object);
            if (logger.isDebugEnabled()) logger.debug("Invoked method: " + method + " on object: " + object);
            return result;
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }

    public static void invokeSetMethod(String methodName, Object object, Object arg) {
        try {
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    method.invoke(object, arg);
                    return;
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static PersistentProperty getIdNode(IClassNode classNode) {
        PersistentProperty id = classNode.getId();
        while (id == null) {
            classNode = classNode.getFather();
            id = classNode.getId();
            if (logger.isDebugEnabled()) logger.debug("Seeking id in: " + classNode.getName());
        }
        return id;
    }

}
