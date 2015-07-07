/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

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
 
package it.unibas.spicygui.vista.treepm;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

public class TreeTopComponentAdapter implements ITreeNodeAdapter {

    private static Log logger = LogFactory.getLog(TreeTopComponentAdapter.class);
    private TopComponent topComponent;
    private Scenario scenario;
    private String name;
    private boolean selected;
    private boolean root;
    private boolean abstracted;

    public TreeTopComponentAdapter(Object generic, boolean selected, boolean root, boolean abstracted) {
        if (generic instanceof TopComponent) {
            this.topComponent = (TopComponent) generic;
        } else if (generic instanceof Scenario) {
            this.scenario = (Scenario) generic;
        } else if (generic != null) {
            throw new IllegalArgumentException("not supported class: " + generic.getClass());
        }
        this.selected = selected;
        this.root = root;
        this.abstracted = abstracted;
    }

    public boolean isRoot() {
        return root;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAbstracted() {
        return abstracted;
    }

    public TopComponent getTopComponent() {
        return topComponent;
    }

    public Scenario getScenario() {
        return scenario;
    }

    @Override
    public String toString() {
        StringBuffer nodeString = new StringBuffer();
        if (topComponent instanceof MappingTaskTopComponent && ((MappingTaskTopComponent) topComponent).getScenario().isSelected()) {
            nodeString.append(topComponent.getName());
        } else if (topComponent != null) {
            nodeString.append(topComponent.getName());
        } else if (root) {
            nodeString.append("Project");
        } else if (abstracted) {
            nodeString.append(name);
        } else {
            nodeString.append("Scenario: " + this.scenario.getMappingTaskTopComponent().getName());
        }
        return nodeString.toString();
    }

    public ImageIcon getImageIcon() {
        if (root) {
            return Costanti.ICONA_PROJECT_TREE_ROOT;
        }
        if (topComponent != null /*|| abstracted*/) {
//            if (topComponent instanceof MappingTaskTopComponent && ((MappingTaskTopComponent) topComponent).getScenario().isSelected()) {
//                return Costanti.ICONA_PROJECT_TREE_TC_SELECTED;
//            } else {

            return new ImageIcon(topComponent.getIcon());
//            return Costanti.ICONA_PROJECT_TREE_TC;

//            }
        }
        if (abstracted) {
            return ImageUtilities.loadImageIcon(Costanti.ICONA_PUNTO, false);
        }
        if (scenario.isSelected()) {
//            return Costanti.ICONA_PROJECT_TREE_SCENARIO_SELECTED;
            Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_PROJECT_TREE_SCENARIO_SELECTED_STRING);
            return new ImageIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
        }
//        return Costanti.ICONA_PROJECT_TREE_SCENARIO;
        Image imageDefault = ImageUtilities.loadImage(Costanti.ICONA_PROJECT_TREE_SCENARIO_STRING);
        return new ImageIcon(ImageUtilities.mergeImages(imageDefault, scenario.getImageNumber(), Scenario.X_OFFSET_IMAGE_NUMBER, Scenario.Y_OFFSET_IMAGE_NUMBER));
    }

//    public ImageIcon getImageIcon() {
//        if (node.isSchemaNode()) {
//            if (this.type.equals(SpicyEngineConstants.TYPE_RELATIONAL)) {
//                if (node.isRoot()) {
//                    return Costanti.ICONA_RELATIONAL_ROOT_ALBERO;
//                }
//
//                if (node.isExcluded()) {
//                    return Costanti.ICONA_EXCLUSION_NODE;
//                }
//
//                if (selectionCondition) {
//                    return Costanti.ICONA_SELECTION_CONDITION_SET;
//                }
//
//                if (node instanceof SetNode) {
//                    return Costanti.ICONA_RELATIONAL_ELEMENTO_SET;
//                }
//
//                if (node instanceof TupleNode) {
//                    return Costanti.ICONA_RELATIONAL_ELEMENTO_TUPLE;
//                }
//
//                if (node instanceof AttributeNode) {
//                    if (key && foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
//                    }
//
//                    if (key) {
//                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
//                    }
//
//                    if (foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
//                    }
//
//                    return Costanti.ICONA_ELEMENTO_ALBERO;
//                }
//
//            } else if (this.type.equals(SpicyEngineConstants.TYPE_XML)) {
//                if (node.isRoot()) {
//                    return Costanti.ICONA_XML_ROOT_ALBERO;
//                }
//
//                if (node.isExcluded()) {
//                    return Costanti.ICONA_EXCLUSION_NODE;
//                }
//
//                if (selectionCondition) {
//                    return Costanti.ICONA_SELECTION_CONDITION_SET;
//                }
//
//                if (node instanceof SetNode) {
//                    return Costanti.ICONA_ELEMENTO_SET;
//                }
//
//                if (node instanceof TupleNode) {
//                    return Costanti.ICONA_ELEMENTO_TUPLE;
//                }
//
//                if (node instanceof AttributeNode) {
//                    if (key && foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
//                    }
//
//                    if (key) {
//                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
//                    }
//
//                    if (foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
//                    }
//
//                    return Costanti.ICONA_ELEMENTO_ALBERO;
//                }
//
//            } else if (this.type.equals(SpicyEngineConstants.TYPE_OBJECT)) {
//                if (node.isRoot()) {
//                    return Costanti.ICONA_OBJECT_ROOT_ALBERO;
//                }
//
//                if (node.isExcluded()) {
//                    return Costanti.ICONA_EXCLUSION_NODE;
//                }
//
//                if (selectionCondition) {
//                    return Costanti.ICONA_SELECTION_CONDITION_SET;
//                }
//
//                if (node instanceof SetNode) {
//                    String setTipe = (String) node.getAnnotation(Constants.SET_TYPE);
//                    if (Constants.HIERARCHY.equals(setTipe)) {
//                        return Costanti.ICONA_OBJECT_HIERARCH;
//                    } else if (Constants.ASSOCIATION.equals(setTipe)) {
//                        return Costanti.ICONA_OBJECT_ASSOCIATION;
//                    }
//
//                }
//                if (node instanceof TupleNode) {
//                    String setTipe = checkFatherType();
//                    if (Constants.HIERARCHY.equals(setTipe)) {
//                        return Costanti.ICONA_OBJECT_TUPLE_HIERARCH;
//                    } else if (Constants.ASSOCIATION.equals(setTipe)) {
//                        return Costanti.ICONA_OBJECT_TUPLE_ASSOCIATION;
//                    }
//
//                }
//                if (node instanceof AttributeNode) {
//                    if (key && foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
//                    }
//
//                    if (key) {
//                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
//                    }
//
//                    if (foreignKey) {
//                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
//                    }
//
//                    return Costanti.ICONA_ELEMENTO_ALBERO;
//                }
//
//            }
//        }
//        if (node.isRoot()) {
//            return Costanti.ICONA_INSTANCE_ROOT;
//        }
//
//        if (node instanceof AttributeNode) {
//            return Costanti.ICONA_INSTANCE_LEAF;
//        }
//
//        return Costanti.ICONA_INSTANCE_INTERMEDIATE;
//    }
//
//    private String checkFatherType() {
//        String setTipe = null;
//        INode fatherNode = node;
//        while (true) {
//            fatherNode = fatherNode.getFather();
//            setTipe =
//                    (String) fatherNode.getAnnotation(Constants.SET_TYPE);
//            if (setTipe != null) {
//                break;
//            }
//
//        }
//        return setTipe;
//    }
}
