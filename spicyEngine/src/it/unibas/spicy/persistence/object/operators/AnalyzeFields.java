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
 
package it.unibas.spicy.persistence.object.operators;

import it.unibas.spicy.persistence.object.ClassUtility;
import it.unibas.spicy.persistence.object.Constants;
import it.unibas.spicy.persistence.object.IllegalModelException;
import it.unibas.spicy.persistence.object.model.ClassTree;
import it.unibas.spicy.persistence.object.model.IClassNode;
import it.unibas.spicy.persistence.object.model.PersistentProperty;
import it.unibas.spicy.persistence.object.model.ReferenceProperty;
import it.unibas.spicy.persistence.object.model.annotations.ID;
import it.unibas.spicy.persistence.object.model.annotations.Inverse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class AnalyzeFields {

    private static Log logger = LogFactory.getLog(AnalyzeFields.class);

    void generateFields(ClassTree classTree) {
        Collection<IClassNode> elementsValue = classTree.getElements().values();
        for (IClassNode currentNode : elementsValue) {
            if (logger.isDebugEnabled()) logger.debug("Generating fields for node: " + currentNode);
            extractFields(classTree, currentNode);
        }
        if (logger.isDebugEnabled()) logger.debug("ClassTree: " + classTree);
        if (logger.isDebugEnabled()) logger.debug("Checking Ids...: ");
        checkIds(classTree.getElements());
        if (logger.isDebugEnabled()) logger.debug("Checking References...: ");
        checkReferences(classTree.getReferences());
    }

    private void extractFields(ClassTree classTree, IClassNode currentNode) {
        Class currentClass = currentNode.getCorrespondingClass();
        Field[] fields = currentClass.getDeclaredFields();
        if (logger.isDebugEnabled()) logger.debug("number of fields: " + fields.length);
        for (Field field : fields) {
            if (ClassUtility.isTransient(field)) {
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("Found a non-transient field: " + field);
            if (!isGetAndSetAvailable(field)) {
                throw new IllegalModelException("set and get method not available for field: " + field.getName() + " - Class: " + currentClass);
            }
            if (isPrimitiveType(field) || isWrapperType(field)) {
                generateLeafProperty(currentNode, field);
            } else if (ClassUtility.isAReferenceCollection(field)) {
                generateCollectionOfReferences(classTree, currentNode, field);
            } else {
                generateReferenceProperty(classTree, currentNode, field);
            }
        }
    }

    private boolean isGetAndSetAvailable(Field field) {
        String fieldName = field.getName();
        fieldName = ClassUtility.convertFirstCapitalLetter(fieldName);
        Class currentClass = field.getDeclaringClass();
        String getMethod = "get" + fieldName;
        String setMethod = "set" + fieldName;
        if (findPublicMethodInClass(currentClass, getMethod) && findPublicMethodInClass(currentClass, setMethod)) {
            return true;
        }
        return false;
    }


    private boolean findPublicMethodInClass(Class currentClass, String methodName) {
        Method[] methods = currentClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    private void generateLeafProperty(IClassNode currentNode, Field field) {
        if (logger.isDebugEnabled()) logger.debug("Found a primitive or wrapperType field: " + field);
        PersistentProperty property = generateField(field);
        if (field.isAnnotationPresent(ID.class)) {
            currentNode.setId(property);
        } else {
            currentNode.addPersistentProperty(property);
        }
    }

    private void generateCollectionOfReferences(ClassTree classTree, IClassNode currentNode, Field field) throws IllegalModelException {
        if (logger.isDebugEnabled()) logger.debug("Found a collection:" + field);
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        // list the actual type arguments
        Type[] targs = parameterizedType.getActualTypeArguments();
        if (targs.length == 0 || targs.length > 1) {
            throw new IllegalModelException("Collection of non generic type: " + field);
        }
        Class typeArgumentClass = (Class) targs[0];
        String typeArgumentName = typeArgumentClass.getName();
        if (logger.isDebugEnabled()) logger.debug("Generic type:" + typeArgumentName);
        IClassNode argumentTypeNode = classTree.get(typeArgumentName);
        if (argumentTypeNode == null) {
            throw new IllegalModelException("Illegal collection of references: " + field);
        }
        ReferenceProperty referenceProperty = new ReferenceProperty(field.getName(), field, currentNode, argumentTypeNode, Constants.MANY);
        currentNode.addReferenceProperty(referenceProperty);
        classTree.getReferences().add(referenceProperty);
    }

    private void generateReferenceProperty(ClassTree classTree, IClassNode currentNode, Field field) throws IllegalModelException {
        if (logger.isDebugEnabled()) logger.debug("Found a reference:" + field);
        IClassNode referenceClassNode = classTree.get(field.getType().getName());
        if (referenceClassNode == null) {
            throw new IllegalModelException("Incorrect reference: " + field);
        }
        ReferenceProperty referenceProperty = new ReferenceProperty(field.getName(), field, currentNode, referenceClassNode, Constants.ONE);
        currentNode.addReferenceProperty(referenceProperty);
        classTree.getReferences().add(referenceProperty);
    }

    private boolean isPrimitiveType(Field field) {
        return field.getType().isPrimitive();
    }

    private boolean isWrapperType(Field field) {
        Class type = field.getType();
        if (type.equals(java.lang.String.class) ||
                type.equals(java.lang.Integer.class) ||
                type.equals(java.lang.Double.class) ||
                type.equals(java.lang.Float.class) ||
                type.equals(java.lang.Boolean.class) ||
                type.equals(java.lang.Character.class)) {
            return true;
        }
        return false;
    }

    private PersistentProperty generateField(Field field) {
        String fieldName = field.getName();
        String typeName = field.getType().getSimpleName();
        PersistentProperty property = new PersistentProperty(fieldName, typeName);
        return property;
    }

    private void checkIds(Map<String, IClassNode> elements) {
        IClassNode object = elements.get(Constants.OBJECT_CLASS);
        for (IClassNode child : object.getChildren()) {
            if (child.getId() == null) {
                throw new IllegalModelException("Missing ID in class: " + child.getName());
            }
        }
    }

    private void checkReferences(List<ReferenceProperty> references) {
        for (ReferenceProperty reference : references) {
            if (reference.getInverse() != null) {
                if (logger.isDebugEnabled()) logger.debug("Inverse is not null for reference: " + reference);
                continue;
            }
            if (!isInverseReference(reference)) {
                if (logger.isDebugEnabled()) logger.debug("Annotation Inverse is not present for reference: " + reference);
                continue;
            }
            IClassNode classNode = reference.getTarget();
            Field field = reference.getField();
            String inverseValue = field.getAnnotation(Inverse.class).value();
            ReferenceProperty inverse = classNode.findReferencePropertyByName(inverseValue);
            if (logger.isDebugEnabled()) logger.debug("Setting inverse: " + inverse.toShortString() + " for reference: " + reference.toShortString());
            reference.setInverse(inverse);
            inverse.setInverse(reference);
        }
    }

    private boolean isInverseReference(ReferenceProperty reference) {
        return reference.getField().isAnnotationPresent(Inverse.class);
    }
}
