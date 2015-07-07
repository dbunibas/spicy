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

import it.unibas.spicygui.Costanti;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class Vista {

    private JFileChooser fileChooser = new JFileChooser();
    private JFileChooser fileChooserFolder = new JFileChooser();
    private MultipleJDialog multipleJDialog = null;
    private Map<String, Object> mappa = new HashMap<String, Object>();

    public Vista() {
        fileChooserFolder.setAcceptAllFileFilterUsed(false);
        UIManager.put("FileChooser.fileNameLabelText",  NbBundle.getMessage(Costanti.class, Costanti.JFILECHOOSER_FOLDER_FILE_NAME));
        UIManager.put("FileChooser.filesOfTypeLabelText", NbBundle.getMessage(Costanti.class, Costanti.JFILECHOOSER_FOLDER_TYPE_FILE));

        SwingUtilities.updateComponentTreeUI(fileChooserFolder);
    }

    public void putVista(String chiave, Object vista) {
        mappa.put(chiave, vista);
    }

    public Object getVista(String chiave) {
        return mappa.get(chiave);
    }

    public Object removeVista(String chiave) {
        return mappa.remove(chiave);
    }

    public Map<String, Object> getMappa() {
        return mappa;
    }

    public JFileChooser getFileChooserApriTXT() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("TXT ", "txt"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserApriXSD() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("XSD ", "xsd"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserApriSQL() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("SQL", "sql"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserApriXML() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML", "xml"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserApriXMLAndTGD() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Mapping Task", "xml", "tgd"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserApriTGD() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(new FileNameExtensionFilter("TGD", "tgd"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserSalvaXmlXsd() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML, XSD ", "xml", "xsd"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserSalvaFileGenerico() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    public JFileChooser getFileChooserSalvaFolder() {

        fileChooserFolder.resetChoosableFileFilters();
        //fileChooser.setFileFilter(new FileNameExtensionFilter("XSD ", "xsd"));
        fileChooserFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fileChooserFolder.updateUI();
        return fileChooserFolder;
    }

    public MultipleJDialog getNewMultipleJDialog() {
        this.multipleJDialog = new MultipleJDialog(WindowManager.getDefault().getMainWindow(), false);
        return this.multipleJDialog;
    }

    public MultipleJDialog getMultipleJDialog() {
        if (this.multipleJDialog == null) {
            this.multipleJDialog = new MultipleJDialog(WindowManager.getDefault().getMainWindow(), false);
        }
        return this.multipleJDialog;
    }
}
