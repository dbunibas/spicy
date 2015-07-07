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

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.values.OID;
import it.unibas.spicy.persistence.object.Constants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import javax.swing.ImageIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TreeNodeAdapter implements ITreeNodeAdapter {

    private static Log logger = LogFactory.getLog(TreeNodeAdapter.class);
    private INode node;
    private boolean key;
    private boolean foreignKey;
    private boolean skolem;
    private boolean flagVirtual = true;
    private boolean oids;
    private boolean selectionCondition;
    private boolean provenance;
    private String type;

    public TreeNodeAdapter(INode node, boolean key, boolean foreignKey, String type) {
        this.node = node;
        this.key = key;
        this.foreignKey = foreignKey;
        this.type = type;
    }

    public TreeNodeAdapter(INode node, String type) {
        this.node = node;
        this.type = type;
    }

    public INode getINode() {
        return node;
    }

    public void toggleSkolem() {
        this.skolem = !this.skolem;
    }

    public void toggleFlagVirtual() {
        this.flagVirtual = !this.flagVirtual;
    }

    public void setSkolem(boolean skolem) {
        this.skolem = skolem;
    }

    public void setFlagVirtual(boolean flagVirtual) {
        this.flagVirtual = flagVirtual;
    }

    public void setOids(boolean oids) {
        this.oids = oids;
    }

    public void toggleOIDs() {
        this.oids = !this.oids;
    }

    public boolean isSelectionCondition() {
        return selectionCondition;
    }

    public void setSelectionCondition(boolean selectionCondition) {
        this.selectionCondition = selectionCondition;
    }

    public void toggleProvenance() {
        this.provenance = !this.provenance;
    }

    public void setProvenance(boolean provenance) {
        this.provenance = provenance;
    }

    public String toString() {
        StringBuffer nodeString = new StringBuffer();
        if (node.isSchemaNode()) {
            if (node.isVirtual() && this.flagVirtual && !(node instanceof AttributeNode)) {
                nodeString.append("{...}");
            } else {
                nodeString.append(node.getLabel());
                if (this.node instanceof AttributeNode) {
                    INode leafNode = node.getChild(0);
                    nodeString.append(" : (").append(leafNode.getLabel().toString()).append(")");
                }
            }
            if (node instanceof TupleNode) {
                INode fatherNode = node.getFather();
                if (fatherNode != null && fatherNode instanceof SetNode) {
                    if (node.isRequired()) {
                        nodeString.append(" [1..*]");
                    } else {
                        nodeString.append(" [0..*]");
                    }
                }
            }
            if ((selectionCondition) && (node instanceof SetNode)) {
                SelectionConditionInfo selectionConditionInfo;
                selectionConditionInfo = (SelectionConditionInfo) node.getAnnotation(Costanti.SELECTION_CONDITON_INFO);
                String selectionString = selectionConditionInfo.getExpressionString();
                if (selectionString.length() > 50) {
                    selectionString = selectionString.substring(0, 50);
                }
                nodeString.append(" [").append(selectionString).append("]");
            }
        } else {
            nodeString.append(node.getLabel());
            if (oids) {
                nodeString.append(" - ").append(node.getValue().toString());
            }
            if (this.node instanceof AttributeNode) {
                INode leafNode = node.getChild(0);
                nodeString.append(" : (").append(leafNode.getValue().toString()).append(")");
            }
            if (skolem) {
                Object value = node.getValue();
                if (value instanceof OID) {
                    OID oid = (OID) value;
                    String skolemString = oid.getSkolemString();
                    nodeString.append(" - Functor: [").append(skolemString).append("]");
                }
            }
            if (this.node instanceof TupleNode && provenance && !node.isRoot()) {
                TupleNode tupleNode = (TupleNode) this.node;
                nodeString.append(" - Provenance: ").append(tupleNode.getProvenance().toString());
            }
            if (this.node instanceof SetNode) {
                nodeString.append(" - Children: ").append(this.node.getChildren().size());
            }
        }
        return nodeString.toString();
    }

    public ImageIcon getImageIcon() {
        if (node.isSchemaNode()) {
            if (this.type.equals(SpicyEngineConstants.TYPE_RELATIONAL)) {
                if (node.isRoot()) {
                    return Costanti.ICONA_RELATIONAL_ROOT_ALBERO;
                }
                if (node.isExcluded()) {
                    return Costanti.ICONA_EXCLUSION_NODE;
                }
                if (selectionCondition) {
                    return Costanti.ICONA_SELECTION_CONDITION_SET;
                }
                if (node instanceof SetNode) {
                    return Costanti.ICONA_RELATIONAL_ELEMENTO_SET;
                }
                if (node instanceof TupleNode) {
                    return Costanti.ICONA_RELATIONAL_ELEMENTO_TUPLE;
                }
                if (node instanceof AttributeNode) {
                    if (key && foreignKey) {
                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
                    }
                    if (key) {
                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
                    }
                    if (foreignKey) {
                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
                    }
                    return Costanti.ICONA_ELEMENTO_ALBERO;
                }
            } else if (this.type.equals(SpicyEngineConstants.TYPE_XML)) {
                if (node.isRoot()) {
                    return Costanti.ICONA_XML_ROOT_ALBERO;
                }
                if (node.isExcluded()) {
                    return Costanti.ICONA_EXCLUSION_NODE;
                }
                if (selectionCondition) {
                    return Costanti.ICONA_SELECTION_CONDITION_SET;
                }
                if (node instanceof SetNode) {
                    return Costanti.ICONA_ELEMENTO_SET;
                }
                if (node instanceof TupleNode) {
                    return Costanti.ICONA_ELEMENTO_TUPLE;
                }
                if (node instanceof AttributeNode) {
                    if (key && foreignKey) {
                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
                    }
                    if (key) {
                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
                    }
                    if (foreignKey) {
                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
                    }
                    return Costanti.ICONA_ELEMENTO_ALBERO;
                }
            } else if (this.type.equals(SpicyEngineConstants.TYPE_OBJECT)) {
                if (node.isRoot()) {
                    return Costanti.ICONA_OBJECT_ROOT_ALBERO;
                }
                if (node.isExcluded()) {
                    return Costanti.ICONA_EXCLUSION_NODE;
                }
                if (selectionCondition) {
                    return Costanti.ICONA_SELECTION_CONDITION_SET;
                }
                if (node instanceof SetNode) {
                    String setTipe = (String) node.getAnnotation(Constants.SET_TYPE);
                    if (Constants.HIERARCHY.equals(setTipe)) {
                        return Costanti.ICONA_OBJECT_HIERARCH;
                    } else if (Constants.ASSOCIATION.equals(setTipe)) {
                        return Costanti.ICONA_OBJECT_ASSOCIATION;
                    }
                }
                if (node instanceof TupleNode) {
                    String setTipe = checkFatherType();
                    if (Constants.HIERARCHY.equals(setTipe)) {
                        return Costanti.ICONA_OBJECT_TUPLE_HIERARCH;
                    } else if (Constants.ASSOCIATION.equals(setTipe)) {
                        return Costanti.ICONA_OBJECT_TUPLE_ASSOCIATION;
                    }
                }
                if (node instanceof AttributeNode) {
                    if (key && foreignKey) {
                        return Costanti.ICONA_ELEMENTO_KEY_FOREIGN_KEY_ALBERO;
                    }
                    if (key) {
                        return Costanti.ICONA_ELEMENTO_KEY_ALBERO;
                    }
                    if (foreignKey) {
                        return Costanti.ICONA_ELEMENTO_FOREIGN_KEY_ALBERO;
                    }
                    return Costanti.ICONA_ELEMENTO_ALBERO;
                }
            }
        }
        if (node.isRoot()) {
            return Costanti.ICONA_INSTANCE_ROOT;
        }
        if (node instanceof AttributeNode) {
            return Costanti.ICONA_INSTANCE_LEAF;
        }
        if (selectionCondition) {
            return Costanti.ICONA_SELECTION_CONDITION_SET;
        }
        return Costanti.ICONA_INSTANCE_INTERMEDIATE;
    }

    private String checkFatherType() {
        String setTipe = null;
        INode fatherNode = node;
        while (true) {
            fatherNode = fatherNode.getFather();
            setTipe = (String) fatherNode.getAnnotation(Constants.SET_TYPE);
            if (setTipe != null) {
                break;
            }
        }
        return setTipe;
    }
}
