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

import it.unibas.spicy.persistence.object.IllegalModelException;
import it.unibas.spicy.persistence.object.model.ClassNode;
import it.unibas.spicy.persistence.object.model.ClassTree;
import it.unibas.spicy.persistence.object.model.IClassNode;
import java.lang.reflect.Constructor;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class GenerateClassModelTree {

    private static Log logger = LogFactory.getLog(GenerateClassModelTree.class);

    private AnalyzeFields generatorFieldModelTree = new AnalyzeFields();

    ClassTree generateClassModelTree(List<Class> classes) {
        ClassTree classTree = buildClassTree(classes);
        if (logger.isDebugEnabled()) logger.debug("Generating fields...");
        generatorFieldModelTree.generateFields(classTree);
        return classTree;
    }

    private ClassTree buildClassTree(List<Class> classes) {
        ClassTree classTree = new ClassTree();
        for (Class currentClass : classes) {
            if (logger.isDebugEnabled()) logger.debug("Analyzing class: " + currentClass);
            analyzeClass(classTree, currentClass);
        }
        return classTree;
    }

    private void analyzeClass(ClassTree classTree, Class currentClass) {
        IClassNode currentClassNode = getClassNode(classTree, currentClass);
        IClassNode rootNode = classTree.getRoot();
        // Base step
        Class superClass = currentClass.getSuperclass();
        if (logger.isDebugEnabled()) logger.debug("Analyzing class node: " + currentClassNode);
        if (logger.isDebugEnabled()) logger.debug("Superclass: " + superClass);
        if (superClass.equals(Object.class)) {
            rootNode.addChild(currentClassNode);
            if (logger.isDebugEnabled()) logger.debug("Base step, adding to root node");
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Recursive step");
        analyzeClass(classTree, superClass);
        IClassNode superClassNode = getClassNode(classTree, superClass);
        superClassNode.addChild(currentClassNode);
    }

    private IClassNode getClassNode(ClassTree classTree, Class currentClass) {
        if (logger.isDebugEnabled()) logger.debug("Getting class node: " + currentClass.getCanonicalName());
        IClassNode currentClassNode = classTree.get(currentClass.getCanonicalName());
        if (currentClassNode == null) {
            currentClassNode = buildClassNode(currentClass);
            classTree.put(currentClassNode.getName(), currentClassNode);
        }
        return currentClassNode;
    }

    private IClassNode buildClassNode(Class currentClass) {
        if (logger.isDebugEnabled()) logger.debug("Creating new class node: " + currentClass.getCanonicalName());
        if (!isConstructorNoArgAvailable(currentClass)) {
            throw new IllegalModelException("Missing no-arg constructor for class: " + currentClass.getCanonicalName());
        }
        IClassNode classNode = new ClassNode(currentClass.getCanonicalName());
        classNode.setCorrespondingClass(currentClass);
        return classNode;
    }

    private boolean isConstructorNoArgAvailable(Class currentClass) {
        Constructor[] constructors = currentClass.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }
}
