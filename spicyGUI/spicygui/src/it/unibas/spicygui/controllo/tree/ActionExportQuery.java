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
 
package it.unibas.spicygui.controllo.tree;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.Vista;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ActionExportQuery extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionIncreaseFont.class);
    private Vista vista;
    private JTextArea textArea;

    public ActionExportQuery(JTextArea textArea) {
        this.textArea = textArea;
        this.putValue(NAME, NbBundle.getMessage(Costanti.class, Costanti.ACTION_EXPORT_QUERY));
    }

    public void actionPerformed(ActionEvent e) {
        this.executeInjection();
        JFileChooser chooser = vista.getFileChooserSalvaFileGenerico();
        File file;
        int returnVal = chooser.showDialog(WindowManager.getDefault().getMainWindow(), NbBundle.getMessage(Costanti.class, Costanti.EXPORT));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileWriter writer = null;
            try {
                file = chooser.getSelectedFile();
                writer = new FileWriter(file);
                writer.write(this.textArea.getText());
                writer.flush();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.EXPORT_OK));
            } catch (IOException ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.EXPORT_ERROR) + " : " + ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
                logger.error(ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {}
            }
        }
    }

    private void executeInjection() {
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
    }
}
