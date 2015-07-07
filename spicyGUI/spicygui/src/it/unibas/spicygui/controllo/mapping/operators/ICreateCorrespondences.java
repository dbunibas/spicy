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
import it.unibas.spicygui.widget.JoinConstraint;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import it.unibas.spicygui.widget.caratteristiche.SelectionConditionInfo;
import java.awt.Stroke;
import java.util.List;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

public interface ICreateCorrespondences {

    void createCorrespondence(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo);

    void createCorrespondenceWithFunction(LayerWidget mainLayer, Widget targetWidget, CaratteristicheWidgetInterFunction caratteristicheFunction, ConnectionInfo connectionInfo);

    void createCorrespondenceWithFunctionalDep(LayerWidget mainLayer, CaratteristicheWidgetInterFunctionalDep caratteristicheFunctionalDep, ConnectionInfo connectionInfo);

    void createCorrespondenceWithSourceValue(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, ConnectionInfo connectionInfo);
    
    void createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, boolean  source);
    
    JoinCondition createCorrespondenceJoinCondition(LayerWidget mainLayer, Widget sourceWidget, Widget targetWidget, JoinConstraint joinConstraint, JoinCondition joinCondition, List<INode> fromPathNodes, boolean source);

//    void createImplied(ConnectionInfo connectionInfo, boolean implied);
    
    void createSelectionCondition(INode iNode, String expression, IDataSourceProxy dataSource, SelectionConditionInfo selectionConditionInfo);

    void undo(ValueCorrespondence valueCorrespondence);
    
    Stroke getStroke();

}
