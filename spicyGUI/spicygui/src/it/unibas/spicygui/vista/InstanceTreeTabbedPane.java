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
 
package it.unibas.spicygui.vista;


import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.controllo.tree.ActionViewAllSkolemFunctors;
import it.unibas.spicygui.controllo.tree.ActionViewOID;
import it.unibas.spicygui.controllo.tree.ActionViewSkolemFunctor;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.controllo.datasource.operators.GenerateInstanceTree;

import it.unibas.spicygui.controllo.tree.ActionViewAllProvenance;
import it.unibas.spicygui.controllo.tree.ActionViewProvenance;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public class InstanceTreeTabbedPane extends JPanel {

    private Log logger = LogFactory.getLog(this.getClass());
    private Modello modello = null;
    private JTree albero;
    private JScrollPane jScrollPane;
    private JPopupMenu popUpMenu;
    private GenerateInstanceTree treeCreator = new GenerateInstanceTree();

    public InstanceTreeTabbedPane(INode instanceRoot, boolean source, MappingTask mappingTask) {
        initComponents();
        executeInjection();
        if (mappingTask == null) {
            return;
        }
        creaAlbero(instanceRoot, source, mappingTask);
        this.setVisible(true);
    }

    private void creaAlbero(INode instanceRoot, boolean source, MappingTask mappingTask) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sto eseguendo creaAlbero");
        }
        IDataSourceProxy dataSource;
        if (source) {
            dataSource = mappingTask.getSourceProxy();
        } else {
            dataSource = mappingTask.getTargetProxy();
        }
        this.albero = treeCreator.buildInstanceTree(instanceRoot, dataSource);
        this.albero.setBorder((BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        creaPopUp();
        this.jScrollPane.setViewportView(albero);
        Utility.expandAll(albero);
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        jScrollPane = new JScrollPane();
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    private void creaPopUp() {
        this.popUpMenu = new JPopupMenu();
        this.popUpMenu.add(new ActionViewSkolemFunctor(this.albero));
        this.popUpMenu.add(new ActionViewAllSkolemFunctors(this.albero));
        this.popUpMenu.add(new ActionViewProvenance(this.albero));
        this.popUpMenu.add(new ActionViewAllProvenance(this.albero));
        this.popUpMenu.add(new ActionViewOID(this.albero));

//        JCheckBoxMenuItem item = new JCheckBoxMenuItem(new ActionSetOIDType(this.albero));
//        this.popUpMenu.add(item);

        this.albero.addMouseListener(new PopUpListener());
    }

    class PopUpListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                selezionaCella(e);
            }
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popUpMenu.show(albero, e.getX(), e.getY());
            }
        }

        private void selezionaCella(MouseEvent e) {
            Object o = e.getSource();
            JTree albero = (JTree) o;
            int row = albero.getClosestRowForLocation(e.getX(), e.getY());
            albero.setSelectionRow(row);
        }
    }
}
