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
package it.unibas.spicygui.controllo.provider.intermediatezone;

import it.unibas.spicy.model.correspondence.ISourceValue;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.provider.ActionConstantConnection;
import it.unibas.spicygui.controllo.provider.ActionFunctionConnection;
import it.unibas.spicygui.controllo.FormValidation;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.composition.MutableMappingTask;
import it.unibas.spicygui.controllo.provider.composition.ActionConstantCompositionConnection;
import it.unibas.spicygui.controllo.provider.ActionFunctionalDepConnection;
import it.unibas.spicygui.controllo.provider.MyMoveProviderGeneric;
import it.unibas.spicygui.controllo.provider.composition.ActionMergeConnection;
import it.unibas.spicygui.controllo.provider.composition.ActionUndefinedChainConnection;
import it.unibas.spicygui.controllo.provider.composition.MyPopupProviderWidgetChainComposition;
import it.unibas.spicygui.controllo.provider.composition.MyPopupProviderWidgetChainComposition;
import it.unibas.spicygui.controllo.provider.composition.MyPopupProviderWidgetConstantComposition;
import it.unibas.spicygui.controllo.provider.composition.MyPopupProviderWidgetMergeComposition;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.widget.AttributeGroupWidget;
import it.unibas.spicygui.widget.MergeWidget;
import it.unibas.spicygui.widget.ChainWidget;
import it.unibas.spicygui.widget.ConstantCompositionWidget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheBarra;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.ConstantWidget;
import it.unibas.spicygui.widget.FunctionWidget;
import it.unibas.spicygui.widget.FunctionalDependencyWidget;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetChainComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstantComposition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterAttributeGroup;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunctionalDep;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetMergeComposition;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.openide.util.ImageUtilities;

public class WidgetCreator {

    private static Log logger = LogFactory.getLog(WidgetCreator.class);

    public Widget createConstantWidgetFromSourceValue(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, JPanel pannelloPrincipale, Point point, ISourceValue sourceValue, GraphSceneGlassPane glassPane) {
        Widget widget = createConstantInterWidget(scene, mainLayer, connectionLayer, pannelloPrincipale, point, glassPane);
        CaratteristicheWidgetInterConst caratteristicheWidget = (CaratteristicheWidgetInterConst) mainLayer.getChildConstraint(widget);
        caratteristicheWidget.setCostante(sourceValue.toString());
        return widget;
    }

    public Widget createConstantInterWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, JPanel pannelloPrincipale, Point point, GraphSceneGlassPane glassPane) {
        CaratteristicheWidgetInterConst caratteristicheWidget = new CaratteristicheWidgetInterConst();
        caratteristicheWidget.setTreeType(Costanti.INTERMEDIE);
        caratteristicheWidget.setFormValidation(new FormValidation(true));
        ConstantWidget rootWidget = new ConstantWidget(scene, point, caratteristicheWidget);
        rootWidget.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderConst(caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionConstantConnection(mainLayer, connectionLayer, caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderDeleteConst(glassPane)));

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        IntermediateMoveProvider moveProvider = new IntermediateMoveProvider(pannelloPrincipale);
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addConstant(rootWidget);
        glassPane.addConstant(barra);

        return rootWidget;
    }

    public Widget createFunctionWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, JPanel pannelloPrincipale, Point point, GraphSceneGlassPane glassPane) {
        FunctionWidget rootWidget = new FunctionWidget(scene, point);
        CaratteristicheWidgetInterFunction caratteristicheWidget = new CaratteristicheWidgetInterFunction();
        caratteristicheWidget.setTreeType(Costanti.INTERMEDIE);
        rootWidget.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderFunction(caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionFunctionConnection(connectionLayer, mainLayer, caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderDeleteFunc(glassPane)));

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        IntermediateMoveProvider moveProvider = new IntermediateMoveProvider(pannelloPrincipale);
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunction(rootWidget);
        glassPane.addFunction(barra);

        return rootWidget;
    }

    public Widget createAttributeGroupWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, JPanel pannelloPrincipale, Point point, GraphSceneGlassPane glassPane) {
        AttributeGroupWidget rootWidget = new AttributeGroupWidget(scene, point);
        CaratteristicheWidgetInterAttributeGroup caratteristicheWidget = new CaratteristicheWidgetInterAttributeGroup();
        caratteristicheWidget.setTreeType(Costanti.INTERMEDIE);
//        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderDeleteAttributeGroup(glassPane)));

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        IntermediateMoveProvider moveProvider = new IntermediateMoveProvider(pannelloPrincipale);
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

//        glassPane.addAttributeGroup(rootWidget);
//        glassPane.addAttributeGroup(barra);

        return rootWidget;
    }

    public Widget createFunctionalDependencyWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, JPanel pannelloPrincipale, Point point, GraphSceneGlassPane glassPane) {
        FunctionalDependencyWidget rootWidget = new FunctionalDependencyWidget(scene, point);
        CaratteristicheWidgetInterFunctionalDep caratteristicheWidget = new CaratteristicheWidgetInterFunctionalDep();
        caratteristicheWidget.setTreeType(Costanti.INTERMEDIE);

        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionFunctionalDepConnection(connectionLayer, mainLayer, caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderDeleteFunctionalDep(glassPane)));

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        IntermediateMoveProvider moveProvider = new IntermediateMoveProvider(pannelloPrincipale);
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunctionalDependency(rootWidget);
        glassPane.addFunctionalDependency(barra);
        scene.validate();

        return rootWidget;
    }

    public Widget createUndefinedChainWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, Point point, GraphSceneGlassPane glassPane) {
        ChainWidget rootWidget = new ChainWidget(scene, point, ImageUtilities.loadImage(Costanti.UNDEFINED_IMAGE, true));
        CaratteristicheWidgetChainComposition caratteristicheWidget = new CaratteristicheWidgetChainComposition();

        caratteristicheWidget.setTreeType(Costanti.COMPOSITION_TYPE);

        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionUndefinedChainConnection(connectionLayer, mainLayer, caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction( new MyPopupProviderWidgetChainComposition(scene, connectionLayer)));
//        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction( new MyPopupProviderWidgetChainComposition(scene)));

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        MyMoveProviderGeneric moveProvider = new MyMoveProviderGeneric();
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunction(rootWidget);
        glassPane.addFunction(barra);
        scene.validate();
//        scenario.setInComposition(true);
        
        return rootWidget;
    }
    
    public Widget createDefinedChainWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, Point point, GraphSceneGlassPane glassPane, Scenario scenario) {
        ChainWidget rootWidget = new ChainWidget(scene, point, scenario.getImageNumber());
        CaratteristicheWidgetChainComposition caratteristicheWidget = new CaratteristicheWidgetChainComposition(new MutableMappingTask(scenario.getMappingTask()));

        caratteristicheWidget.setTreeType(Costanti.COMPOSITION_TYPE);

//        rootWidget.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderFunction(caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionUndefinedChainConnection(connectionLayer, mainLayer, caratteristicheWidget)));
        MyPopupProviderWidgetChainComposition loadDataSource = new MyPopupProviderWidgetChainComposition(scene, connectionLayer);
        loadDataSource.setEnable(false);
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(loadDataSource));
//        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderDeleteFunc(glassPane)));
//        rootWidget.getActions().addAction(ActionFactory.createMoveAction());

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        MyMoveProviderGeneric moveProvider = new MyMoveProviderGeneric();
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunction(rootWidget);
        glassPane.addFunction(barra);
        scene.validate();
//        scenario.setInComposition(true);
        
        return rootWidget;
    }
    
    public Widget createConstantWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, Point point, GraphSceneGlassPane glassPane, Scenario scenario) {
        if(alrearyExistsConstantCompositionWidget(glassPane, scenario)) {
            return null;
        }
        ConstantCompositionWidget rootWidget = new ConstantCompositionWidget(scene, point, scenario.getImageNumber());
        CaratteristicheWidgetConstantComposition caratteristicheWidget = new CaratteristicheWidgetConstantComposition(new MutableMappingTask(scenario.getMappingTask()), scenario);

        caratteristicheWidget.setTreeType(Costanti.COMPOSITION_TYPE);

//        rootWidget.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderFunction(caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionConstantCompositionConnection(connectionLayer, mainLayer, caratteristicheWidget)));
        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderWidgetConstantComposition(glassPane.getScene())));
//        rootWidget.getActions().addAction(ActionFactory.createMoveAction());

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        MyMoveProviderGeneric moveProvider = new MyMoveProviderGeneric();
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunction(rootWidget);
        glassPane.addFunction(barra);
        scene.validate();
//        scenario.setInComposition(true);
        
        return rootWidget;
    }
    
    public Widget createCompositionMergeWidget(Scene scene, LayerWidget mainLayer, LayerWidget connectionLayer, Point point, GraphSceneGlassPane glassPane) {
        MergeWidget rootWidget = new MergeWidget(scene, point);
        CaratteristicheWidgetMergeComposition caratteristicheWidget = new CaratteristicheWidgetMergeComposition();

        caratteristicheWidget.setTreeType(Costanti.COMPOSITION_TYPE);

//        rootWidget.getActions().addAction(ActionFactory.createEditAction(new MyEditProviderFunction(caratteristicheWidget)));
 
        rootWidget.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionMergeConnection(connectionLayer, mainLayer, caratteristicheWidget)));

        rootWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderWidgetMergeComposition(glassPane.getScene(),connectionLayer)));
//        rootWidget.getActions().addAction(ActionFactory.createMoveAction());

        CaratteristicheBarra caratteristicheBarra = new CaratteristicheBarra(rootWidget, Costanti.INTERMEDIE_BARRA);
        IconNodeWidget barra = new IconNodeWidget(scene);
        barra.setImage(ImageUtilities.loadImage(Costanti.ICONA_MOVE));
        Point pointBarra = new Point(rootWidget.getPreferredLocation().x - Costanti.OFF_SET_X_WIDGET_BARRA, rootWidget.getPreferredLocation().y - Costanti.OFF_SET_Y_WIDGET_BARRA);
        barra.setPreferredLocation(pointBarra);
        MyMoveProviderGeneric moveProvider = new MyMoveProviderGeneric();
        barra.getActions().addAction(ActionFactory.createMoveAction(moveProvider, moveProvider));
        caratteristicheWidget.setWidgetBarra(barra);

        mainLayer.addChild(rootWidget, caratteristicheWidget);
        mainLayer.addChild(barra, caratteristicheBarra);

        glassPane.addFunction(rootWidget);
        glassPane.addFunction(barra);
        scene.validate();

        return rootWidget;
    }

    private boolean alrearyExistsConstantCompositionWidget(GraphSceneGlassPane glassPane, Scenario scenario) {
        List<Widget> widgets = glassPane.getMainLayer().getChildren();
        for (Widget widget : widgets) {
            if(widget instanceof ConstantCompositionWidget) {
                CaratteristicheWidgetConstantComposition caratteristicheWidgetConstantComposition = (CaratteristicheWidgetConstantComposition) glassPane.getMainLayer().getChildConstraint(widget);
                if(caratteristicheWidgetConstantComposition.getScenarioRelated().equals(scenario))
                    return true;
            }
        }
        return false;
    }
}
