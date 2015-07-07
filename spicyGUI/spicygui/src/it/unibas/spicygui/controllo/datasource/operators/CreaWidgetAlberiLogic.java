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
 
package it.unibas.spicygui.controllo.datasource.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.provider.MyPopupProviderJoinCondition;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstraint;
import it.unibas.spicygui.widget.ConnectionConstraint;
import it.unibas.spicygui.widget.JoinConstraint;
import it.unibas.spicygui.widget.MyConnectionWidget;
import it.unibas.spicygui.widget.VMDPinWidgetKey;
import it.unibas.spicygui.widget.caratteristiche.ConstraintPoint;
import it.unibas.spicygui.widget.caratteristiche.ConstraintsWidgetCollections;
import it.unibas.spicygui.widget.caratteristiche.LineCoordinates;
import it.unibas.spicygui.widget.caratteristiche.LineCoordinatesCollections;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class CreaWidgetAlberiLogic {

//    public static final String MAPPING_TASK_TYPE = "mapping task";
//    public static final String TGd_TYPE = "TGD";
//    public static final String SPICY_TYPE = "spicy";
    private static int OFFSET_ASCISSA_TARGET = 230;
    private static int OFFSET_ASCISSA_TARGET_SECONDO_WIDGET = 130;
    private static int OFFSET_ASCISSA_SOURCE_SECONDO_WIDGET = 60;
    private static int OFFSET_ASCISSA_SOURCE = 100;
    private static int OFFSET_ORDINATA_SOURCE = 3;
    private static int OFFSET_ORDINATA_TARGET = 3;
    private Scene scene;
//    private LayerWidget mainLayer;
    private LayerWidget constraintsLayer;
    private String pinWidgetTreeConstant;
    private String joinConnectionConstraintConstant;
    private Modello modello;
    private FindNode finder = new FindNode();
    private LineCoordinatesCollections lineCoordinatesCollections = null;
    private ConstraintsWidgetCollections constraintsWidgetCollections = null;
    private static Log logger = LogFactory.getLog(CreaWidgetAlberiLogic.class);

    public CreaWidgetAlberiLogic(String type) {
        executeInjection();
        analyzeType(type);
//        pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE;
//        joinConnectionConstraintConstant = Costanti.JOIN_CONNECTION_CONSTRAINT;
//        GraphSceneGlassPane graphSceneGlassPane = analyzeType(type);
//        this.scene = graphSceneGlassPane.getScene();
//        this.mainLayer = graphSceneGlassPane.getMainLayer();
//        this.constraintsLayer = graphSceneGlassPane.getConstraintsLayer();
    }

    public void createPopupJoinCondition(JoinConstraint joinConstraint, List<MyConnectionWidget> connectionWidgets) {
        MyPopupProviderJoinCondition mppjc = new MyPopupProviderJoinCondition((LayerWidget) connectionWidgets.get(0).getParentWidget(), joinConstraint);
        for (ConnectionWidget connectionWidget : connectionWidgets) {
            connectionWidget.getActions().addAction(0, ActionFactory.createPopupMenuAction(mppjc));
        }
//        mcw1.getActions().addAction(ActionFactory.createPopupMenuAction(mppjc));
//        mcw2.getActions().addAction(ActionFactory.createPopupMenuAction(mppjc));
//        mcw3.getActions().addAction(ActionFactory.createPopupMenuAction(mppjc));
    }

    private void analyzeType(String type) {
        if (type.equals(CreaWidgetAlberi.MAPPING_TASK_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE;
            joinConnectionConstraintConstant = Costanti.JOIN_CONNECTION_CONSTRAINT;
        } else if (type.equals(CreaWidgetAlberi.TGD_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE_TGD;
            joinConnectionConstraintConstant = Costanti.JOIN_CONNECTION_CONSTRAINT_TGD;
        } else if (type.equals(CreaWidgetAlberi.SPICY_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE_SPICY;
            joinConnectionConstraintConstant = Costanti.JOIN_CONNECTION_CONSTRAINT_SPICY;
          //  return SpicyTopComponent.findInstance().getJLayeredPane().getGlassPane();
        }else {
            throw new IllegalArgumentException("type not supported: " + type + "  Exepcted: " + CreaWidgetAlberi.MAPPING_TASK_TYPE + "  Or  " + CreaWidgetAlberi.TGD_TYPE);
        }
    }

    private void createCollections() {
        Object a = modello.getBean(Costanti.LINE_COORDINATES_COLLECTIONS);
        Object b = modello.getBean(Costanti.CONSTRAINTS_WIDGET_COLLECTIONS);

        if (a == null) {
            this.lineCoordinatesCollections = new LineCoordinatesCollections();
            this.modello.putBean(Costanti.LINE_COORDINATES_COLLECTIONS, this.lineCoordinatesCollections);
        } else {
            this.lineCoordinatesCollections = (LineCoordinatesCollections) a;
        }
        if (b == null) {
            this.constraintsWidgetCollections = new ConstraintsWidgetCollections();
            this.modello.putBean(Costanti.CONSTRAINTS_WIDGET_COLLECTIONS, this.constraintsWidgetCollections);
        } else {
            this.constraintsWidgetCollections = (ConstraintsWidgetCollections) b;
        }
    }

    private void repositionatingWidgetConstraints(Point keyPoint, VMDPinWidgetKey widgetKey, VMDPinWidgetKey widgetIntermediateKey) {
        ConstraintPoint constraintPoint = new ConstraintPoint(keyPoint);
        constraintsWidgetCollections.calculateFreePoint(constraintPoint);
        constraintsWidgetCollections.putWidget(constraintPoint, widgetKey);
        widgetIntermediateKey.setPreferredLocation(constraintPoint);
        Point pointTmp = widgetKey.getPreferredLocation();
        if (logger.isDebugEnabled()) {
            logger.debug("punto iniziale: " + pointTmp.y);
        }
        pointTmp.y = constraintPoint.y;
        if (logger.isDebugEnabled()) {
            logger.debug("punto dopo modifica: " + pointTmp.y);
        }
        widgetKey.setPreferredLocation(pointTmp);
    }

    private void settaWidgetKey(VMDPinWidget pin) {
        ArrayList lista = new ArrayList();
        lista.add(ImageUtilities.loadImage(Costanti.ICONA_PIN_KEY));
        pin.setOpaque(false);
        pin.setGlyphs(lista);
    }

    public void creaWidgetJoinCondition(JoinCondition joinCondition, IDataSourceProxy dataSource, boolean source) {
        List<PathExpression> fromPaths = joinCondition.getFromPaths();
        List<PathExpression> toPaths = joinCondition.getToPaths();
        List<INode> fromPathNodes = new ArrayList<INode>();
        JoinConstraint joinConstraint = new JoinConstraint();
        joinConstraint.setJoinCondition(joinCondition);
        joinConstraint.setDataSource(dataSource);
        for (int i = 0; i < fromPaths.size(); i++) {
            PathExpression fromPath = fromPaths.get(i);
            PathExpression toPath = toPaths.get(i);
            INode iNodeFromPath = createSingleJoin(fromPath, dataSource, toPath, source, joinConstraint);
            fromPathNodes.add(iNodeFromPath);
        }
        for (INode iNode : fromPathNodes) {
            iNode.addAnnotation(joinConnectionConstraintConstant, joinConstraint);
        }
        //TODO vedere se sia meglio rimanerlo in questo metodo oppure in createSingleJoin()
        joinConstraint.changeForeignkey(joinCondition.isMonodirectional());
        joinConstraint.changeMandatory(joinCondition.isMandatory());
    }

    public INode createSingleJoin(PathExpression fromPath, IDataSourceProxy dataSource, PathExpression toPath, boolean source, JoinConstraint joinConstraint) {


        createCollections();
        INode iNodeFromPath = finder.findNodeInSchema(fromPath, dataSource);
        INode iNodeToPath = finder.findNodeInSchema(toPath, dataSource);
        Widget widgetFromPathOriginale = (Widget) iNodeFromPath.getAnnotation(pinWidgetTreeConstant);
        Widget widgetToPathOriginale = (Widget) iNodeToPath.getAnnotation(pinWidgetTreeConstant);

        //TODO migliorare questo controllo, per non ripetere molte volte questo if
        if (constraintsLayer == null || scene == null) {
            scene = widgetFromPathOriginale.getScene();
            constraintsLayer = (LayerWidget) scene.getChildren().get(Costanti.INDEX_CONSTRAINTS_LAYER);
        }


        VMDPinWidgetKey widgetFromPath = new VMDPinWidgetKey(scene);
        VMDPinWidgetKey widgetToPath = new VMDPinWidgetKey(scene);
        settaWidgetKey(widgetFromPath);
        settaWidgetKey(widgetToPath);
        int ascissa = getAscissaMaggiore(widgetFromPathOriginale, widgetToPathOriginale);
        Point pointFromPath = null;
        Point pointToPath = null;
        if (source) {
            pointFromPath = new Point(ascissa + OFFSET_ASCISSA_SOURCE, widgetFromPathOriginale.getPreferredLocation().y + OFFSET_ORDINATA_SOURCE);
            pointToPath = new Point(ascissa + OFFSET_ASCISSA_SOURCE, widgetToPathOriginale.getPreferredLocation().y + OFFSET_ORDINATA_SOURCE);
        } else {
            pointFromPath = new Point(ascissa + OFFSET_ASCISSA_TARGET, widgetFromPathOriginale.getPreferredLocation().y + OFFSET_ORDINATA_TARGET);
            pointToPath = new Point(ascissa + OFFSET_ASCISSA_TARGET, widgetToPathOriginale.getPreferredLocation().y);
        }
        LineCoordinates lineCoordinates = new LineCoordinates(pointFromPath, pointToPath);
        lineCoordinatesCollections.calculateFreeCoordinate(lineCoordinates);
        lineCoordinatesCollections.addLineCoordinates(lineCoordinates);
        widgetFromPath.setPreferredLocation(pointFromPath);
        widgetToPath.setPreferredLocation(pointToPath);

        ConnectionConstraint connectionConstraint = createConnectionConstraint(source, widgetToPathOriginale, widgetFromPathOriginale, widgetFromPath, ascissa, widgetToPath, Costanti.DASHED_STROKE, joinConstraint);
        joinConstraint.addConnection(connectionConstraint);
//        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderJoinCondition(scene, constraintsLayer)));

        return iNodeFromPath;
    }

    private ConnectionConstraint createConnectionConstraint(boolean source, Widget widgetKeyOriginale, Widget widgetForeignKeyOriginale, VMDPinWidgetKey widgetForeignKey, int ascissa, VMDPinWidgetKey widgetKey, Stroke stroke, JoinConstraint joinConstraint) {
        Point originalPointForeignKey = widgetForeignKeyOriginale.getPreferredLocation();
        Point originalPointKey = widgetKeyOriginale.getPreferredLocation();
        Point newPointForeignKey = new Point(originalPointForeignKey.x, originalPointForeignKey.y);
        Point newPointKey = new Point(originalPointKey.x, originalPointKey.y);

        LayerWidget mainLayer = (LayerWidget) widgetKeyOriginale.getParentWidget();
        mainLayer.addChild(widgetForeignKey, new CaratteristicheWidgetConstraint(Costanti.FOREIGN_KEY, widgetForeignKeyOriginale, newPointForeignKey));
        mainLayer.addChild(widgetKey, new CaratteristicheWidgetConstraint(Costanti.KEY, widgetKeyOriginale, newPointKey));
        ConnectionConstraint connectionConstraint = new ConnectionConstraint();
        VMDPinWidgetKey widgetIntermediateForeignKey = new VMDPinWidgetKey(scene);
        VMDPinWidgetKey widgetIntermediateKey = new VMDPinWidgetKey(scene);
        settaWidgetKey(widgetIntermediateForeignKey);
        settaWidgetKey(widgetIntermediateKey);
        Point keyPoint = null;
        if (source) {
            widgetIntermediateForeignKey.setPreferredLocation(new Point(widgetForeignKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_SOURCE_SECONDO_WIDGET, widgetForeignKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_TARGET));
            keyPoint = new Point(widgetKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_SOURCE_SECONDO_WIDGET, widgetKeyOriginale.getPreferredLocation().y);
        } else {
            widgetIntermediateForeignKey.setPreferredLocation(new Point(widgetForeignKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_TARGET_SECONDO_WIDGET, widgetForeignKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_TARGET));
            keyPoint = new Point(widgetKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_TARGET_SECONDO_WIDGET, widgetKeyOriginale.getPreferredLocation().y);
        }
        repositionatingWidgetConstraints(keyPoint, widgetKey, widgetIntermediateKey);
        mainLayer.addChild(widgetIntermediateForeignKey, new CaratteristicheWidgetConstraint(Costanti.FOREIGN_KEY, widgetForeignKeyOriginale, newPointForeignKey));
        mainLayer.addChild(widgetIntermediateKey, new CaratteristicheWidgetConstraint(Costanti.KEY, widgetKeyOriginale, newPointKey));

        MyConnectionWidget mcw1 = creaConnection(widgetKey, widgetIntermediateKey, true, stroke, joinConstraint);
        MyConnectionWidget mcw2 = creaConnection(widgetKey, widgetForeignKey, false, stroke, joinConstraint);
        MyConnectionWidget mcw3 = creaConnection(widgetIntermediateForeignKey, widgetForeignKey, false, stroke, joinConstraint);

        createPopupJoinCondition(joinConstraint, Arrays.asList(mcw1, mcw2, mcw3));


        connectionConstraint.addConnection(mcw1);
        connectionConstraint.addConnection(mcw2);
        connectionConstraint.addConnection(mcw3);
        scene.validate();
        scene.paint();
        scene.repaint();
        scene.revalidate();
        return connectionConstraint;
    }

    private MyConnectionWidget creaConnection(Widget sourceWidget, Widget targetWidget, boolean direzionale, Stroke stroke, JoinConstraint joinConstraint) {
        MyConnectionWidget connection = new MyConnectionWidget(scene);
        connection.setDirezionale(direzionale);
        connection.setTargetAnchorShape(AnchorShape.NONE);
        if (direzionale) {
            AnchorShape anchorShape = Costanti.ANCHOR_SHAPE;
            connection.setTargetAnchorShape(anchorShape/*AnchorShape.TRIANGLE_HOLLOW*/);
        }
        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createCenterAnchor(targetWidget));
        connection.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT);
        connection.setStroke(stroke);
        constraintsLayer.addChild(connection, joinConstraint);
        constraintsLayer.revalidate();
//        connection.getActions().addAction(ActionFactory.createPopupMenuAction(new MyPopupProviderJoinCondition(scene, constraintsLayer, joinConstraint)));
        return connection;
    }

    private int getAscissaMaggiore(Widget widgetForeignKey, Widget widgetKey) {
        int xForeignKey = widgetForeignKey.getPreferredLocation().x;
        int xKey = widgetKey.getPreferredLocation().x;

        if (xForeignKey >= xKey) {
            return xForeignKey;
        }
        return xKey;
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}

