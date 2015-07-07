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
 
package it.unibas.spicygui.controllo.datasource;


import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberi;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberiLogic;
import it.unibas.spicygui.widget.ConnectionConstraint;
import it.unibas.spicygui.widget.JoinConstraint;
import it.unibas.spicygui.widget.MyConnectionWidget;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.openide.util.Lookup;

public class ActionOkMultipleJoin extends AbstractAction {

    private Modello modello;
    private JDialog dialog;

    public ActionOkMultipleJoin(JDialog dialog) {
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        executeInjection();
        JoinCondition joinCondition = (JoinCondition) modello.getBean(Costanti.JOIN_CONDITION);
        JoinConstraint joinConstraint = (JoinConstraint) this.modello.getBean(Costanti.JOIN_CONSTRIANTS);
        List<INode> fromPathNodes = (List<INode>) this.modello.getBean(Costanti.FROM_PATH_NODES);
        MappingTask mappingTask = (MappingTask) this.modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        for (INode iNode : fromPathNodes) {
            iNode.addAnnotation(Costanti.JOIN_CONNECTION_CONSTRAINT, joinConstraint);
        }
        if (this.modello.getBean(Costanti.JOIN_SESSION_SOURCE) != null) {
            mappingTask.getSourceProxy().addJoinCondition(joinCondition);
        } else {
            mappingTask.getTargetProxy().addJoinCondition(joinCondition);
        }
        creaPopup(joinConstraint);
        removeJoinBean();
        dialog.setVisible(false);
    }

    private void creaPopup(JoinConstraint joinConstraint) {
        List<MyConnectionWidget> connectionWidgets = new ArrayList<MyConnectionWidget>();
        for (ConnectionConstraint connectionConstraint : joinConstraint.getConnections()) {
            connectionWidgets.addAll(connectionConstraint.getConnections());
        }
        CreaWidgetAlberiLogic logic = new CreaWidgetAlberiLogic(CreaWidgetAlberi.MAPPING_TASK_TYPE);
        logic.createPopupJoinCondition(joinConstraint, connectionWidgets);
    }

    private void removeJoinBean() {
        this.modello.removeBean(Costanti.CREATING_JOIN_SESSION);
        this.modello.removeBean(Costanti.JOIN_CONSTRIANTS);
        this.modello.removeBean(Costanti.JOIN_CONDITION);
        this.modello.removeBean(Costanti.FROM_PATH_NODES);
        this.modello.removeBean(Costanti.JOIN_SESSION_SOURCE);
        this.modello.removeBean(Costanti.JOIN_SESSION_TARGET);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}
