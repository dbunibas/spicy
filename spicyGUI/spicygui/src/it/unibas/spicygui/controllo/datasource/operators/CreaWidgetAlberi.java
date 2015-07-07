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

import it.unibas.spicygui.vista.treepm.TreeNodeAdapter;
import it.unibas.spicy.model.datasource.ForeignKeyConstraint;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.JoinCondition;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.provider.ActionSceneConnection;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesSpicy;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesTGD;
import it.unibas.spicygui.controllo.mapping.operators.ICreateCorrespondences;
import it.unibas.spicygui.controllo.provider.ActionSceneConnectionTarget;
import it.unibas.spicygui.vista.GraphSceneGlassPane;
import it.unibas.spicygui.vista.JLayeredPaneCorrespondences;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetConstraint;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetTree;
import it.unibas.spicygui.widget.ConnectionConstraint;
import it.unibas.spicygui.widget.MyConnectionWidget;
import it.unibas.spicygui.widget.VMDPinWidgetKey;
import it.unibas.spicygui.widget.VMDPinWidgetSource;
import it.unibas.spicygui.widget.VMDPinWidgetTarget;
import it.unibas.spicygui.widget.caratteristiche.ConstraintPoint;
import it.unibas.spicygui.widget.caratteristiche.ConstraintsWidgetCollections;
import it.unibas.spicygui.widget.caratteristiche.LineCoordinates;
import it.unibas.spicygui.widget.caratteristiche.LineCoordinatesCollections;
import java.awt.Component;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.AnchorShapeFactory;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class CreaWidgetAlberi {

    public static final String MAPPING_TASK_TYPE = "mappingTask";
    public static final String TGD_TYPE = "tdg";
    public static final String SPICY_TYPE = "spicy";
    private static int OFFSET_ASCISSA_TARGET = 230;
    private static int OFFSET_ASCISSA_TARGET_SECONDO_WIDGET = 130;
    private static int OFFSET_ASCISSA_SOURCE_SECONDO_WIDGET = 60;
    private static int OFFSET_ASCISSA_SOURCE = 100;
    private static int OFFSET_ORDINATA_SOURCE = 3;
    private static int OFFSET_ORDINATA_TARGET = 3;
    private int contatore = -1;
    private List listaFoglie = new ArrayList();
    private JLayeredPaneCorrespondences jLayeredPane;
    private Scene scene;
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private LayerWidget constraintsLayer;
    private String pinWidgetTreeConstant;
    private String connectionConstraintConstant;
    private ICreateCorrespondences correspondencesCreator;
    private Modello modello;
    private FindNode finder = new FindNode();
    private CreaWidgetAlberiLogic logic = null;
    private LineCoordinatesCollections lineCoordinatesCollections = null;
    private ConstraintsWidgetCollections constraintsWidgetCollections = null;
    private static Log logger = LogFactory.getLog(CreaWidgetAlberi.class);

    public CreaWidgetAlberi(JLayeredPaneCorrespondences jLayeredPane, String type) {
        executeInjection();
        createCollections();
        this.jLayeredPane = jLayeredPane;
        //GraphSceneGlassPane graphSceneGlassPane = analyzeType(type);
        analyzeType(type);
//        logic = new CreaWidgetAlberiLogic(MAPPING_TASK_TYPE);
//        correspondencesCreator = new CreateCorrespondencesMappingTask();
//                    pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE;
//            connectionConstraintConstant = Costanti.CONNECTION_CONSTRAINT;

        this.scene = jLayeredPane.getGlassPane().getScene();
        this.mainLayer = jLayeredPane.getGlassPane().getMainLayer();
        this.connectionLayer = jLayeredPane.getGlassPane().getConnectionLayer();
        this.constraintsLayer = jLayeredPane.getGlassPane().getConstraintsLayer();
    }

    private GraphSceneGlassPane analyzeType(String type) {
        if (type.equals(MAPPING_TASK_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE;
            connectionConstraintConstant = Costanti.CONNECTION_CONSTRAINT;
            correspondencesCreator = new CreateCorrespondencesMappingTask();
            logic = new CreaWidgetAlberiLogic(MAPPING_TASK_TYPE);
        }
        if (type.equals(TGD_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE_TGD;
            connectionConstraintConstant = Costanti.CONNECTION_CONSTRAINT_TGD;
            correspondencesCreator = new CreateCorrespondencesTGD();
            logic = new CreaWidgetAlberiLogic(TGD_TYPE);
        }
        if (type.equals(SPICY_TYPE)) {
            pinWidgetTreeConstant = Costanti.PIN_WIDGET_TREE_SPICY;
            connectionConstraintConstant = Costanti.CONNECTION_CONSTRAINT_SPICY;
            correspondencesCreator = new CreateCorrespondencesSpicy();
            logic = new CreaWidgetAlberiLogic(SPICY_TYPE);
         //   return SpicyTopComponent.findInstance().getJLayeredPane().getGlassPane();
        }
        return null;
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
        pointTmp.y = constraintPoint.y;
        widgetKey.setPreferredLocation(pointTmp);
    }

    private void settaWidgetAlbero(VMDPinWidget pin, boolean source) {
        ArrayList lista = new ArrayList();
        if (source) {
            lista.add(ImageUtilities.loadImage(Costanti.ICONA_PIN_SOURCE));
        } else {
            lista.add(ImageUtilities.loadImage(Costanti.ICONA_PIN_TARGET));
        }
        pin.setOpaque(false);
        pin.setGlyphs(lista);
    }

    private void settaWidgetKey(VMDPinWidget pin) {
        ArrayList lista = new ArrayList();
        lista.add(ImageUtilities.loadImage(Costanti.ICONA_PIN_KEY));
        pin.setOpaque(false);
        pin.setGlyphs(lista);
    }

    public void creaWidgetAlbero(JTree albero, Component source, Component target, boolean sourceFlag) {
        analisiRicorsiva(albero.getModel(), albero.getModel().getRoot());
        scansioneAlbero(albero, source, target, sourceFlag);
    }

    private void analisiRicorsiva(TreeModel model, Object root) {
        contatore++;
        int indice = model.getChildCount(root);
        if (indice == 0) {
            Integer numeroFoglia = contatore /* -1 */;
            listaFoglie.add(numeroFoglia);
            if (logger.isDebugEnabled()) {
                logger.debug(root);
            }
            return;
        }
        for (int i = 0; i < indice; i++) {
            analisiRicorsiva(model, model.getChild(root, i));
        }
    }

    private void scansioneAlbero(JTree albero, Component source, Component target, boolean sourceFlag) {
//VEcchio codice che mette widget solo per le foglie
        //        for (int i = 0; i < listaFoglie.size(); i++) {
//            int tmp = (Integer) listaFoglie.get(i);
        for (int i = 0; i < albero.getRowCount(); i++) {
            TreePath treePath = albero.getPathForRow(i);
            if (treePath != null && albero.isVisible(treePath)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("punto trovato: " + albero.getPathBounds(treePath).getLocation());
                }
                Point convertedPoint = SwingUtilities.convertPoint(source, albero.getPathBounds(treePath).getLocation(), target);
                createWidget(convertedPoint, albero, treePath, sourceFlag);
                if (logger.isTraceEnabled()) {
                    logger.trace("punto convertito: " + convertedPoint);
                }
            }
        }
        contatore = -1;
        listaFoglie = new ArrayList();
    }

    public void creaWidgetJoinConditions(IDataSourceProxy dataSource, boolean source) {
        List<JoinCondition> listaJoinConditions = dataSource.getJoinConditions();
        for (JoinCondition joinCondition : listaJoinConditions) {
            logic.creaWidgetJoinCondition(joinCondition, dataSource, source);
        }

    }

    public void creaWidgetConstraints(IDataSourceProxy dataSource, boolean source) {
        List<ForeignKeyConstraint> listaForeignKey = dataSource.getForeignKeyConstraints();
        for (ForeignKeyConstraint foreignKeyConstraint : listaForeignKey) {
            creaWidgetKeyConstraints(foreignKeyConstraint, dataSource, source);
        }

    }

    private void creaWidgetKeyConstraints(ForeignKeyConstraint foreignKeyConstraint, IDataSourceProxy dataSource, boolean source) {
        KeyConstraint keyConstraint = foreignKeyConstraint.getKeyConstraint();
        for (int i = 0; i <
                foreignKeyConstraint.getForeignKeyPaths().size(); i++) {
            PathExpression foreignPath = foreignKeyConstraint.getForeignKeyPaths().get(i);
            PathExpression keyPath = keyConstraint.getKeyPaths().get(i);
            INode iNodeForeignKey = finder.findNodeInSchema(foreignPath, dataSource);
            INode iNodeKey = finder.findNodeInSchema(keyPath, dataSource);
            Widget widgetForeignKeyOriginale = (Widget) iNodeForeignKey.getAnnotation(pinWidgetTreeConstant);
            Widget widgetKeyOriginale = (Widget) iNodeKey.getAnnotation(pinWidgetTreeConstant);
            VMDPinWidgetKey widgetForeignKey = new VMDPinWidgetKey(scene);
            VMDPinWidgetKey widgetKey = new VMDPinWidgetKey(scene);
            settaWidgetKey(widgetForeignKey);
            settaWidgetKey(widgetKey);
            int ascissa = getAscissaMaggiore(widgetForeignKeyOriginale, widgetKeyOriginale);
            Point pointForeignKey = null;
            Point pointKey = null;
            if (source) {
                pointForeignKey = new Point(ascissa + OFFSET_ASCISSA_SOURCE, widgetForeignKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_SOURCE);
                pointKey =
                        new Point(ascissa + OFFSET_ASCISSA_SOURCE, widgetKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_SOURCE);
            } else {
                pointForeignKey = new Point(ascissa + OFFSET_ASCISSA_TARGET, widgetForeignKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_TARGET);
                pointKey =
                        new Point(ascissa + OFFSET_ASCISSA_TARGET, widgetKeyOriginale.getPreferredLocation().y);
            }

            LineCoordinates lineCoordinates = new LineCoordinates(pointForeignKey, pointKey);
            lineCoordinatesCollections.calculateFreeCoordinate(lineCoordinates);
            lineCoordinatesCollections.addLineCoordinates(lineCoordinates);
            widgetForeignKey.setPreferredLocation(pointForeignKey);
            widgetKey.setPreferredLocation(pointKey);
            ConnectionConstraint connectionConstraint = createConnectionConstraint(source, widgetKeyOriginale, widgetForeignKeyOriginale, widgetForeignKey, ascissa, widgetKey, Costanti.CONSTRAINTS_STROKE);
            iNodeForeignKey.addAnnotation(connectionConstraintConstant, connectionConstraint);
        }

    }

    private ConnectionConstraint createConnectionConstraint(boolean source, Widget widgetKeyOriginale, Widget widgetForeignKeyOriginale, VMDPinWidgetKey widgetForeignKey, int ascissa, VMDPinWidgetKey widgetKey, Stroke stroke) {
        Point originalPointForeignKey = widgetForeignKeyOriginale.getPreferredLocation();
        Point originalPointKey = widgetKeyOriginale.getPreferredLocation();
        Point newPointForeignKey = new Point(originalPointForeignKey.x, originalPointForeignKey.y);
        Point newPointKey = new Point(originalPointKey.x, originalPointKey.y);

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
            keyPoint =
                    new Point(widgetKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_SOURCE_SECONDO_WIDGET, widgetKeyOriginale.getPreferredLocation().y);
        } else {
            widgetIntermediateForeignKey.setPreferredLocation(new Point(widgetForeignKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_TARGET_SECONDO_WIDGET, widgetForeignKeyOriginale.getPreferredLocation().y + OFFSET_ORDINATA_TARGET));
            keyPoint =
                    new Point(widgetKeyOriginale.getPreferredLocation().x + OFFSET_ASCISSA_TARGET_SECONDO_WIDGET, widgetKeyOriginale.getPreferredLocation().y);
        }

        repositionatingWidgetConstraints(keyPoint, widgetKey, widgetIntermediateKey);
        mainLayer.addChild(widgetIntermediateForeignKey, new CaratteristicheWidgetConstraint(Costanti.FOREIGN_KEY, widgetForeignKeyOriginale, newPointForeignKey));
        mainLayer.addChild(widgetIntermediateKey, new CaratteristicheWidgetConstraint(Costanti.KEY, widgetKeyOriginale, newPointKey));
        connectionConstraint.addConnection(creaConnection(widgetKey, widgetIntermediateKey, true, stroke));
        connectionConstraint.addConnection(creaConnection(widgetKey, widgetForeignKey, false, stroke));
        connectionConstraint.addConnection(creaConnection(widgetIntermediateForeignKey, widgetForeignKey, false, stroke));
        scene.validate();

        return connectionConstraint;
    }

    private MyConnectionWidget creaConnection(Widget sourceWidget, Widget targetWidget, boolean direzionale, Stroke stroke) {
        MyConnectionWidget connection = new MyConnectionWidget(scene);
        connection.setTargetAnchorShape(AnchorShape.NONE);
        if (direzionale) {
            AnchorShape anchorShape = AnchorShapeFactory.createTriangleAnchorShape(5, false, false);
            connection.setTargetAnchorShape(anchorShape/*AnchorShape.TRIANGLE_HOLLOW*/);
        }

        connection.setSourceAnchor(AnchorFactory.createCenterAnchor(sourceWidget));
        connection.setTargetAnchor(AnchorFactory.createCenterAnchor(targetWidget));
        connection.setLineColor(Costanti.COLOR_CONNECTION_CONSTRAINT_DEFAULT);
        connection.setStroke(stroke);
        connection.setKey(true);

        constraintsLayer.addChild(connection);

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

    private void createWidget(Point point, JTree albero, TreePath treePath, boolean sourceFlag) {
        Point punto = null;
        TreeNode treeNode = (TreeNode) treePath.getLastPathComponent();
        TreeNodeAdapter nodeAdapter = (TreeNodeAdapter) ((DefaultMutableTreeNode) treeNode).getUserObject();
        INode iNode = nodeAdapter.getINode();
        if (sourceFlag) {
            VMDPinWidgetSource pin = new VMDPinWidgetSource(scene);
            settaWidgetAlbero(pin, sourceFlag);
            int width = albero.getPathBounds(treePath).width;
            int height = albero.getPathBounds(treePath).height;
            if (logger.isTraceEnabled()) {
                logger.trace("width: " + width + " height: " + height);
            }

            punto = new Point(point.x + (width - Costanti.OFFSET_X_WIDGET_SOURCE), point.y + (height / Costanti.OFFSET_Y_WIDGET_SOURCE));
            pin.setPreferredLocation(punto);
            pin.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionSceneConnection(scene, connectionLayer, mainLayer, this.correspondencesCreator)));
            mainLayer.addChild(pin, new CaratteristicheWidgetTree(albero, point, treePath, Costanti.TREE_SOURCE, width, iNode));
            iNode.addAnnotation(pinWidgetTreeConstant, pin);
            //TODO Da togliere l'ultima condizione nel momento in cui le tgd possano tracciare corrispondenze
            if ((iNode.isExcluded()) || !(iNode instanceof AttributeNode) || (pinWidgetTreeConstant.equals(Costanti.PIN_WIDGET_TREE_TGD))) {
                pin.setEnabled(false);
            }

        } else {
            VMDPinWidgetTarget pin = new VMDPinWidgetTarget(scene);
            settaWidgetAlbero(pin, sourceFlag);
            punto =
                    new Point(point.x, point.y + 5);
            pin.setPreferredLocation(punto);
            pin.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ActionSceneConnectionTarget(scene, connectionLayer, mainLayer, this.correspondencesCreator)));
            mainLayer.addChild(pin, new CaratteristicheWidgetTree(albero, point, treePath, Costanti.TREE_TARGET, 0, iNode));
            iNode.addAnnotation(pinWidgetTreeConstant, pin);
            //TODO Da togliere l'ultima condizione nel momento in cui le tgd possano tracciare corrispondenze
            if ((iNode.isExcluded()) || !(iNode instanceof AttributeNode) || (pinWidgetTreeConstant.equals(Costanti.PIN_WIDGET_TREE_TGD)))  {
                pin.setEnabled(false);
            }

        }
        scene.validate();
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }
}

