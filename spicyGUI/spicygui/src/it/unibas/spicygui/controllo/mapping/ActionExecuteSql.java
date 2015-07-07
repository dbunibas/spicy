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
 
package it.unibas.spicygui.controllo.mapping;


import it.unibas.spicy.model.algebra.query.operators.sql.ExecuteSQL;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.AccessConfiguration;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.sql.SqlDialog;
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ActionExecuteSql extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionExecuteSql.class);
    private JTextArea textArea;
    private MappingTask mappingTask;

    public ActionExecuteSql(JTextArea textArea, MappingTask mappingTask) {
        this.textArea = textArea;
        this.mappingTask = mappingTask;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_EXECUTE_SQL));
    }

    public void actionPerformed(ActionEvent e) {
//        if (mappingTask.getSource().getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_RELATIONAL) &&
//                mappingTask.getTarget().getType().equalsIgnoreCase(SpicyEngineConstants.TYPE_RELATIONAL)) {
        SqlDialog dialog = new SqlDialog(WindowManager.getDefault().getMainWindow(), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == SqlDialog.RET_OK) {
            RelationalConfigurationPM relationalConfigurationPM = dialog.getRelationalConfigurationPM();
            try {
                Reader sourceSQLScriptReader = new InputStreamReader((new File(relationalConfigurationPM.getSourceSchema())).toURI().toURL().openStream());
                Reader sourceInstanceSQLScriptReader = new InputStreamReader((new File(relationalConfigurationPM.getSourceInstance())).toURI().toURL().openStream());
                Reader targetSQLScriptReader = new InputStreamReader((new File(relationalConfigurationPM.getTargetSchema())).toURI().toURL().openStream());
                if (dialog.isRecreate()) {
                    AccessConfiguration accessConfigurationSupport = creaAccessConfigurationSupport(relationalConfigurationPM);
                    new ExecuteSQL().executeScript(mappingTask, relationalConfigurationPM.getAccessConfiguration(), accessConfigurationSupport, textArea.getText(), sourceSQLScriptReader, sourceInstanceSQLScriptReader, targetSQLScriptReader, null);
                } else {
                    new ExecuteSQL().executeScript(mappingTask, relationalConfigurationPM.getAccessConfiguration(), textArea.getText(), sourceSQLScriptReader, sourceInstanceSQLScriptReader, targetSQLScriptReader, null);
                }
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_QUERY_EXECUTED), DialogDescriptor.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                logger.error(ex);
            }
        }
//        } else {
//            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_NO_RELATIONAL_DATASOURCE), DialogDescriptor.WARNING_MESSAGE));
//        }


    }

    private AccessConfiguration creaAccessConfigurationSupport(RelationalConfigurationPM relationalConfigurationPM) {
        AccessConfiguration accessConfigurationSupport = new AccessConfiguration();
        accessConfigurationSupport.setDriver(relationalConfigurationPM.getAccessConfiguration().getDriver());
        accessConfigurationSupport.setUri(extractDatabaseUri(relationalConfigurationPM));
        accessConfigurationSupport.setLogin(relationalConfigurationPM.getAccessConfiguration().getLogin());
        accessConfigurationSupport.setPassword(relationalConfigurationPM.getAccessConfiguration().getPassword());
        return accessConfigurationSupport;
    }

    public String extractDatabaseUri(RelationalConfigurationPM relationalConfigurationPM) {
        String uri = relationalConfigurationPM.getAccessConfiguration().getUri();
        if (uri.lastIndexOf("/") != -1) {
            return uri.substring(0, uri.lastIndexOf("/") + 1) + relationalConfigurationPM.getTmpDatabaseName();
        }
        return uri.substring(0, uri.lastIndexOf(":") + 1) + relationalConfigurationPM.getTmpDatabaseName();
    }
}
