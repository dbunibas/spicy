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

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.controllo.Scenario;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TGDTabbedPane extends JPanel {

    private Log logger = LogFactory.getLog(this.getClass());
    private JTextArea textArea;
    private JScrollPane jScrollPane;
    private FORule fORule;
    private Scenario scenario;

    public TGDTabbedPane(FORule fORuleParameter, Scenario scenarioParameter, MappingTask mappingTask) {
        this.fORule = fORuleParameter;
        this.scenario = scenarioParameter;
        initComponents();
        initArea(fORuleParameter.toLogicalString(mappingTask));
        this.setVisible(true);
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
               scenario.setSelectedFORule(fORule);
               scenario.getTGDCorrespondencesTopComponent().drawConnections();
            }
        });
//        creaPopUp();
    }

    private void initArea(String tgd) {
        textArea = new JTextArea(tgd);
        textArea.setEditable(false);
        jScrollPane.setViewportView(textArea);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        jScrollPane = new JScrollPane();
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    public FORule getFORule() {
        return fORule;
    }

//    private void creaPopUp() {
//        this.popUpMenu = new JPopupMenu();
//        this.popUpMenu.add(new ActionIncreaseFont(textArea));
//        this.popUpMenu.add(new ActionDecreaseFont(textArea));
//        this.popUpMenu.add(new ActionExportQuery(textArea));
//        if (xQuery) {
//            this.popUpMenu.add(new ActionExecuteXQuery(textArea, scenario.getMappingTask()));
//        } else {
//            this.popUpMenu.add(new ActionExecuteSql(textArea, scenario.getMappingTask()));
//        }
//        this.textArea.addMouseListener(new PopUpListener());
//    }
//
//    class PopUpListener extends MouseAdapter {
//
//        public void mousePressed(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        public void mouseReleased(MouseEvent e) {
//            maybeShowPopup(e);
//        }
//
//        private void maybeShowPopup(MouseEvent e) {
//            if (e.isPopupTrigger()) {
//                popUpMenu.show(textArea, e.getX(), e.getY());
//            }
//        }
//    }
}
