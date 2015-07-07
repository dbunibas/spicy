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
 
package it.unibas.spicygui.controllo.provider;


import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.widget.JoinConstraint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class MyPopupProviderJoinCondition implements PopupMenuProvider, ActionListener {

    private static Log logger = LogFactory.getLog(MyPopupProviderJoinCondition.class);
    private JoinCondition joinCondition;
    private BindingGroup bindingGroup;
    private JPopupMenu menu;
    private ConnectionWidget rootWidget;
    private Modello modello;
    private final String DELETE = "delete";
    private final String TOGGLE_MANDATORY = "toggle mandatory";
    private final String TOGGLE_FOREIGN = "toggle foreign";
    private LayerWidget constraintsLayer;

    public MyPopupProviderJoinCondition(LayerWidget constraintsLayer, JoinConstraint joinConstraint) {
        this.constraintsLayer = constraintsLayer;
        this.joinCondition = joinConstraint.getJoinCondition();
        createPopupMenu();
    }

    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        rootWidget = (ConnectionWidget) widget;
        return menu;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(DELETE)) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_DELETE));
            deleteAll();
        } else if (e.getActionCommand().equals(TOGGLE_MANDATORY)) {
            toggleMandatory();
        } else if (e.getActionCommand().equals(TOGGLE_FOREIGN)) {
            toggleForeignKey();
        } else {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_ERROR));
        }
    }

    private void createPopupMenu() {
        bindingGroup = new BindingGroup();

        menu = new JPopupMenu("Popup menu");
        JMenuItem item1;
        JCheckBoxMenuItem item2;
        JCheckBoxMenuItem item3;

        item1 = new JMenuItem(NbBundle.getMessage(Costanti.class, Costanti.GENERIC_DELETE));
        item1.setActionCommand(DELETE);
        item1.addActionListener(this);

        item2 = new JCheckBoxMenuItem(NbBundle.getMessage(Costanti.class, Costanti.TOGGLE_MANDATORY));
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, joinCondition, ELProperty.create("${mandatory}"), item2, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        item2.setActionCommand(TOGGLE_MANDATORY);
        item2.addActionListener(this);

        item3 = new JCheckBoxMenuItem(NbBundle.getMessage(Costanti.class, Costanti.TOGGLE_FOREIGN));
        Binding binding2 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, joinCondition, ELProperty.create("${monodirectional}"), item3, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding2);
        item3.setActionCommand(TOGGLE_FOREIGN);
        item3.addActionListener(this);

        menu.add(item1);
        menu.add(item2);
        menu.add(item3);

        bindingGroup.bind();
    }

    private void deleteAll() {
        JoinConstraint joinConstraint = (JoinConstraint) this.constraintsLayer.getChildConstraint(rootWidget);
        joinConstraint.deleteAll();
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.DELETE_CONNECTION));
        rootWidget.removeFromParent();
        rootWidget.getScene().validate();
    }

    private void toggleMandatory() {
        JoinConstraint joinConstraint = (JoinConstraint) this.constraintsLayer.getChildConstraint(rootWidget);
        joinConstraint.getDataSource().setMandatoryForJoin(joinCondition, !joinCondition.isMandatory());
        joinConstraint.changeMandatory(joinCondition.isMandatory());
        updateMappingTask();
        StatusDisplayer.getDefault().setStatusText("Mandatory: " + joinCondition.isMandatory());
    }

    private void toggleForeignKey() {
        JoinConstraint joinConstraint = (JoinConstraint) this.constraintsLayer.getChildConstraint(rootWidget);
        joinConstraint.getDataSource().setForeignKeyForJoin(joinCondition, !joinCondition.isMonodirectional());
        joinConstraint.changeForeignkey(joinCondition.isMonodirectional());
        updateMappingTask();
        StatusDisplayer.getDefault().setStatusText("Monodirectional: " + joinCondition.isMonodirectional());
    }

    private void updateMappingTask() {
        executeInjection();
        MappingTask mappingTask = (MappingTask) this.modello.getBean(Costanti.MAPPINGTASK_SHOWED);
        mappingTask.setModified(true);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }


}
