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
 
package it.unibas.spicygui.controllo.mapping.operators;

import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberi;
import it.unibas.spicygui.controllo.datasource.operators.CreaWidgetAlberiLogic;
import it.unibas.spicygui.widget.JoinConstraint;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Stroke;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Lookup;

public class CreateCorrespondencesTGD implements ICreateCorrespondences {

    private GeneratePathExpression generatePathFromNode = new GeneratePathExpression();
    private CreaWidgetAlberiLogic constraintCreator = new CreaWidgetAlberiLogic(CreaWidgetAlberi.TGD_TYPE);
    private Modello modello;
    private static Log logger = LogFactory.getLog(CreateCorrespondencesTGD.class);

    //TODO questa è la classe che si incarica di gestire le connessioni tra source e target qui va fatto tutto
    //questa viene passata ai provider delle azione di tracciamento connessioni in modo tale che lei sappia cosa debba essere fatto
    

    public CreateCorrespondencesTGD() {
        executeInjection();
    }

    public void createCorrespondence(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
//        CaratteristicheWidgetTree caratteristicheWidgetTreeSource = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(sourceWidget);
//        CaratteristicheWidgetTree caratteristicheWidgetTreeTarget = (CaratteristicheWidgetTree) mainLayer.getChildConstraint(targetWidget);
//        PathExpression sourcePathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeSource.getINode());
//        PathExpression targetPathExpression = generatePathFromNode.generatePathFromRoot(caratteristicheWidgetTreeTarget.getINode());
//        ValueCorrespondence valueCorrespondence = new ValueCorrespondence(sourcePathExpression, targetPathExpression);
//        TGD tgd = (TGD) modello.getBean(Costanti.TGD_SHOWED);
//        tgd.
//        mappingTask.addCandidateCorrespondence(valueCorrespondence);
//        connectionInfo.setValueCorrespondence(valueCorrespondence);
//        VariablePathExpression variablePathExpression = new VariablePathExpression
    }

    public void createCorrespondenceWithSourceValue(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo) {
    }

    public void createCorrespondenceWithFunction(LayerWidget mainLayer, Widget targetWidget, CaratteristicheWidgetInterFunction caratteristicheFunction, ConnectionInfo connectionInfo) {
    }

    public void createCorrespondenceWithFunctionalDep(LayerWidget mainLayer, CaratteristicheWidgetInterFunctionalDep caratteristicheFunctionalDep, ConnectionInfo connectionInfo) {
    }


//    public void createImplied(ConnectionInfo connectionInfo, boolean implied) {
////        ValueCorrespondence valueCorrespondence = new ValueCorrespondence(connectionInfo.getValueCorrespondence(), implied);
////        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
////        mappingTask.addCandidateCorrespondence(valueCorrespondence);
////        connectionInfo.setValueCorrespondence(valueCorrespondence);
//    }

    public void undo(ValueCorrespondence valueCorrespondence) {
//        MappingTask mappingTask = (MappingTask) modello.getBean(Costanti.MAPPINGTASK);
//        mappingTask.addCandidateCorrespondence(valueCorrespondence);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    public Stroke getStroke() {
        return Costanti.DASHED_STROKE;
    }

    public void createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget) {
    }


    public JoinCondition createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, JoinConstraint joinConstraint, JoinCondition joinCondition, List<INode> fromPathNodes, boolean source) {
        return null;
    }

    public void createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, boolean source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createSelectionCondition(INode iNode, String expression, IDataSourceProxy dataSource, SelectionConditionInfo selectionConditionInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
