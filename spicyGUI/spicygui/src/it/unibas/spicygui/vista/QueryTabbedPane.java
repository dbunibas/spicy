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


import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.mapping.ActionExecuteSql;
import it.unibas.spicygui.controllo.mapping.ActionExecuteXQuery;
import it.unibas.spicygui.controllo.tree.ActionDecreaseFont;
import it.unibas.spicygui.controllo.tree.ActionExportQuery;
import it.unibas.spicygui.controllo.tree.ActionIncreaseFont;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.NbBundle;

public class QueryTabbedPane extends JPanel {

    private Log logger = LogFactory.getLog(this.getClass());
    private JTextArea textArea;
    private Scenario scenario;
    private boolean xQuery;
    private String query;
    private JScrollPane jScrollPane;
    private JPopupMenu popUpMenu;

    public QueryTabbedPane(String query, Scenario scenario, boolean xQuery) {
        this.scenario = scenario;
        this.xQuery = xQuery;
        this.query = query;
        initComponents();
        initArea(query);
        this.setVisible(true);
        creaPopUp();
    }

    private void initArea(String xQuery) {
        textArea = new JTextArea(xQuery);
        textArea.setEditable(false);
        jScrollPane.setViewportView(textArea);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        jScrollPane = new JScrollPane();
        this.add(jScrollPane, BorderLayout.CENTER);
    }

    public void creaPopUp() {
        this.popUpMenu = new JPopupMenu();
        this.popUpMenu.add(new ActionIncreaseFont(textArea));
        this.popUpMenu.add(new ActionDecreaseFont(textArea));
        this.popUpMenu.add(new ActionExportQuery(textArea));
        if (xQuery) {
            MappingTask mappingTask = scenario.getMappingTask();
            if (mappingTask.getSourceProxy().getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_XML)) {
                JMenu jMenu = new JMenu(NbBundle.getMessage(Costanti.class, Costanti.ACTION_EXECUTE_XQUERY));
                this.popUpMenu.add(jMenu);
                List<String> sourceInstancesName = (List<String>) mappingTask.getSourceProxy().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
                for (String instance : sourceInstancesName) {
                    jMenu.add(new ActionExecuteXQuery(instance, scenario.getMappingTask()));
                }
            }
        } else {
            this.popUpMenu.add(new ActionExecuteSql(textArea, scenario.getMappingTask()));
        }
        this.textArea.addMouseListener(new PopUpListener());
    }

    protected String getQuery() {
        return this.query;
    }

    class PopUpListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popUpMenu.show(textArea, e.getX(), e.getY());
            }
        }
    }
}
