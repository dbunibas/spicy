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
 
package it.unibas.spicygui.controllo.file;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.vista.wizard.pm.XMLConfigurationPM;
import it.unibas.spicygui.vista.Vista;
import it.unibas.spicygui.vista.wizard.pm.RelationalConfigurationPM;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class ActionFileChooserExecuteSql extends AbstractAction {

    private Modello modello;
    private Vista vista;
    public static final int SCHEMA_SOURCE = 1;
    public static final int INSTANCE_SOURCE = 2;
    public static final int SCHEMA_TARGET = 3;
    private int type;
    private RelationalConfigurationPM relationalConfigurationPM;

    public ActionFileChooserExecuteSql(int type, RelationalConfigurationPM relationalConfigurationPM) {
        super("...");
        this.relationalConfigurationPM = relationalConfigurationPM;
        this.type = type;
    }

    public void actionPerformed(ActionEvent e) {
        executeInjection();
        JFileChooser chooser = vista.getFileChooserApriSQL();
        File file;
        int returnVal = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (type == SCHEMA_SOURCE) {
                relationalConfigurationPM.setSourceSchema(file.getAbsolutePath());
            } else if (type == INSTANCE_SOURCE) {
                relationalConfigurationPM.setSourceInstance(file.getAbsolutePath());
            } else if (type == SCHEMA_TARGET) {
                relationalConfigurationPM.setTargetSchema(file.getAbsolutePath());
            }            
        }
    }

    private void executeInjection() {
        if (this.modello == null) {
            this.modello = Lookup.getDefault().lookup(Modello.class);
        }
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
    }
}
