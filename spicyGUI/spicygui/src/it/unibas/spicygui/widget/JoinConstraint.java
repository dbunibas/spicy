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
 
package it.unibas.spicygui.widget;

import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class JoinConstraint {

    private List<ConnectionConstraint> connections = new ArrayList<ConnectionConstraint>();
    private JoinCondition joinCondition;
    private IDataSourceProxy dataSource;

    public ConnectionConstraint getConnection(int index) {
        return connections.get(index);
    }

    public boolean addConnection(ConnectionConstraint connection) {
        return connections.add(connection);
    }

    public List<ConnectionConstraint> getConnections() {
        return connections;
    }

    public JoinCondition getJoinCondition() {
        return joinCondition;
    }

    public IDataSourceProxy getDataSource() {
        return dataSource;
    }

    public void setDataSource(IDataSourceProxy dataSource) {
        this.dataSource = dataSource;
    }

    public void setJoinCondition(JoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public void changeLineColor(Color color) {
        for (ConnectionConstraint connectionConstraint : connections) {
            connectionConstraint.changeLineColor(color);
        }
    }


    public void deleteAll() {
        for (ConnectionConstraint connectionConstraint : this.connections) {
            connectionConstraint.deleteConnectionAndWidget();
        }
        dataSource.getJoinConditions().remove(this.joinCondition);
    }

    public void changeMandatory(boolean mandatory) {
        for (ConnectionConstraint connectionConstraint : connections) {
            connectionConstraint.changeMandatory(mandatory, joinCondition.toString());
        }
    }

    public void changeForeignkey(boolean foreignkey) {
        for (ConnectionConstraint connectionConstraint : connections) {
            connectionConstraint.changeForeignKey(foreignkey, joinCondition.toString());
        }
    }
}
