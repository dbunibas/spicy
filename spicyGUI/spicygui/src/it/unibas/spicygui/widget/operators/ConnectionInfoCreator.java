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
 
package it.unibas.spicygui.widget.operators;

import it.unibas.spicygui.controllo.provider.ActionSetImplied;
import it.unibas.spicygui.controllo.provider.MyEditProviderConfidence;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.netbeans.api.visual.widget.ConnectionWidget;

public class ConnectionInfoCreator {

    private static Log logger = LogFactory.getLog(ConnectionInfoCreator.class);

    public void createPropertyItem( JMenuItem itemConfidence, JCheckBoxMenuItem itemCheckImplied, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection) {
        createPropertyItem( itemConfidence, itemCheckImplied, bindingGroup, connectionInfo, connection, true);
    }

    public void createPropertyItem(JMenuItem itemConfidence, JCheckBoxMenuItem itemCheckImplied, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection, boolean editFilter) {
        createConfidenceLabel(itemConfidence, itemCheckImplied, bindingGroup, connectionInfo, connection);
    }

    public void createConfidenceLabel(JMenuItem itemConfidence, JCheckBoxMenuItem itemCheckImplied, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection) {
        // binding
        Binding bindingImplied = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, connectionInfo.getValueCorrespondence(), ELProperty.create("${implied}"), itemCheckImplied, BeanProperty.create("selected"));
        Binding bindingConfidence = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, connectionInfo, ELProperty.create("${confidence}"), itemConfidence, BeanProperty.create("text"));
        bindingGroup.addBinding(bindingConfidence);
        bindingGroup.addBinding(bindingImplied);
        //action
        itemCheckImplied.setAction(new ActionSetImplied(connectionInfo));
        itemConfidence.setAction(new MyEditProviderConfidence(connection, connectionInfo));
    }

//    public VMDNodeWidget createPropertyWidget(Scene scene, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection) {
//        return createPropertyWidget(scene, bindingGroup, connectionInfo, connection, true);
//    }
//
//    public VMDNodeWidget createPropertyWidget(Scene scene, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection, boolean editFilter) {
//        VMDNodeWidget vmdNodeWidget = new VMDNodeWidget(scene);
//        vmdNodeWidget.setNodeName(NbBundle.getMessage(Costanti.class, Costanti.PROPRIETA_CONNESSIONI));
//
//        createFilterLabel(vmdNodeWidget, bindingGroup, connectionInfo, connection, editFilter);
//        createConfidenceLabel(vmdNodeWidget, bindingGroup, connectionInfo, connection);
//        vmdNodeWidget.setOpaque(true);
//        return vmdNodeWidget;
//    }
//
//    private void createConfidenceLabel(VMDNodeWidget vmdNodeWidget, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection) {
//        Scene scene = vmdNodeWidget.getScene();
//        LabelWidget etichettaConfidenceRoot = new LabelWidget(scene);
//        etichettaConfidenceRoot.setLayout(LayoutFactory.createHorizontalFlowLayout());
//        LabelWidget etichettaConfidenceIntermedie1 = new LabelWidget(scene, " ");
//        etichettaConfidenceRoot.addChild(etichettaConfidenceIntermedie1);
//        IconNodeWidget etichettaConfidencePrefix = new IconNodeWidget(scene);
//        etichettaConfidencePrefix.setImage(ImageUtilities.loadImage(Costanti.ICONA_CONFIDENCE));
//        etichettaConfidenceRoot.addChild(etichettaConfidencePrefix);
//        etichettaConfidencePrefix.setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.TOOL_TIP_CONFIDENCE));
//        LabelWidget etichettaConfidenceIntermedie = new LabelWidget(scene, "  ");
//        etichettaConfidenceRoot.addChild(etichettaConfidenceIntermedie);
//        LabelWidget etichettaConfidenceSuffix = new LabelWidget(scene);
//        etichettaConfidenceSuffix.setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.TOOL_TIP_CONFIDENCE));
//        etichettaConfidenceRoot.addChild(etichettaConfidenceSuffix);
//        LabelWidget etichettaImplied = new LabelWidget(scene, " (" + NbBundle.getMessage(Costanti.class, Costanti.IMPLIED) + ") ");
//        if (connectionInfo.getValueCorrespondence().isImplied()) {
//            etichettaImplied.setVisible(true);
//        } else {
//            etichettaImplied.setVisible(false);
//        }
//        etichettaConfidenceRoot.addChild(etichettaImplied);
//        connection.addChild(vmdNodeWidget, etichettaImplied);
//        vmdNodeWidget.attachPinWidget(etichettaConfidenceRoot);
//        // binding
//        Binding bindingConfidence = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, connectionInfo, ELProperty.create("${confidence}"), etichettaConfidenceSuffix, BeanProperty.create("label"));
//        bindingGroup.addBinding(bindingConfidence);
//        //action
//        etichettaConfidenceRoot.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderConfidence(connection, connectionInfo)));
//    }
//
//    private void createFilterLabel(VMDNodeWidget vmdNodeWidget, BindingGroup bindingGroup, ConnectionInfo connectionInfo, ConnectionWidget connection, boolean edit) {
//        Scene scene = vmdNodeWidget.getScene();
//        LabelWidget etichettaFilterRoot = new LabelWidget(scene);
//        etichettaFilterRoot.setLayout(LayoutFactory.createHorizontalFlowLayout());
//        LabelWidget etichettaFilterIntermedie1 = new LabelWidget(scene, " ");
//        etichettaFilterRoot.addChild(etichettaFilterIntermedie1);
//        IconNodeWidget etichettaFilterPrefix = new IconNodeWidget(scene);
//        etichettaFilterPrefix.setImage(ImageUtilities.loadImage(Costanti.ICONA_FILTRO));
//        etichettaFilterRoot.addChild(etichettaFilterPrefix);
//        etichettaFilterPrefix.setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.TOOL_TIP_FILTER));
//        LabelWidget etichettaFilterIntermedie2 = new LabelWidget(scene, "  ");
//        etichettaFilterRoot.addChild(etichettaFilterIntermedie2);
//        LabelWidget etichettaFilterSuffix = new LabelWidget(scene);
//        etichettaFilterSuffix.setToolTipText(NbBundle.getMessage(Costanti.class, Costanti.TOOL_TIP_FILTER));
//        etichettaFilterRoot.addChild(etichettaFilterSuffix);
//        vmdNodeWidget.attachPinWidget(etichettaFilterRoot);
//        //binding
//        Binding bindingFilter = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, connectionInfo, ELProperty.create("${filter}"), etichettaFilterSuffix, BeanProperty.create("label"));
//        bindingGroup.addBinding(bindingFilter);
//        //action
//        if (edit) {
//            etichettaFilterRoot.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderFilter(connection, connectionInfo)));
//        }
//    }
}
