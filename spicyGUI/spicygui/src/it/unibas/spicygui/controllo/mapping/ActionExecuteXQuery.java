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


import it.unibas.spicy.model.algebra.query.operators.xquery.ExecuteXQuery;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.Vista;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class ActionExecuteXQuery extends AbstractAction {

    private static Log logger = LogFactory.getLog(ActionExecuteXQuery.class);
    private MappingTask mappingTask;
    private String instance;
    private Vista vista;

    public ActionExecuteXQuery(String instance, MappingTask mappingTask) {
        executeInjection();
        this.mappingTask = mappingTask;
        this.instance = instance;
        this.putValue(NAME,findTitle(instance));
    }

    private String findTitle(String absoluteTitle) {
        int start = absoluteTitle.lastIndexOf("\\");
        int end = absoluteTitle.length();
        return absoluteTitle.substring(++start, end);
    }

    public void actionPerformed(ActionEvent e) {
        File outputFile = generateFile();
        if (outputFile != null) {
            try {
                ExecuteXQuery executor = new ExecuteXQuery();
//                List<String> instanceFileNames = (List<String>) mappingTask.getSource().getAnnotation(SpicyEngineConstants.XML_INSTANCE_FILE_LIST);
//                if (instanceFileNames == null && instanceFileNames.size() == 0) {
//                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_QUERY_NOT_EXECUTED), DialogDescriptor.INFORMATION_MESSAGE));
//                    return;
//                }
//                String percorso = "";
//                if (instanceFileNames.size() == 1) {
//                    percorso = instanceFileNames.get(0);
//                    percorso = percorso.replace("\\", "/");
//                } else {
//
//                }
                String pathInstanceFile = instance.replace("\\", "/");
                executor.executeScript(mappingTask, pathInstanceFile, outputFile.getAbsolutePath());
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.MESSAGE_QUERY_EXECUTED), DialogDescriptor.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), DialogDescriptor.ERROR_MESSAGE));
            }
        }
    }

    private File generateFile() {
        JFileChooser chooser = vista.getFileChooserSalvaXmlXsd();
        boolean continua = true;
        File file;
        while (continua) {
            int returnVal = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                if (!file.exists() || chiediConferma()) {
                    return file;
                }
            } else {
                continua = false;
            }
        }
        return null;
    }

    private boolean chiediConferma() {
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.FILE_EXISTS), DialogDescriptor.YES_NO_OPTION);
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue().equals(NotifyDescriptor.YES_OPTION)) {
            return true;
        }
        return false;
    }

//    private void writeResult(File outputFile, XQResultSequence result) {
//        OutputStream outputStream = null;
//        try {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Creating output stream...");
//            }
//            outputStream = new FileOutputStream(outputFile);
//            result.writeSequence(outputStream, new Properties());
//            if (logger.isDebugEnabled()) {
//                logger.debug("Writing output stream...");
//            }
//        } catch (XQException ex) {
//            logger.error("Unable to generate result: " + ex);
//        } catch (FileNotFoundException ex) {
//            logger.error("Temp file not found: " + ex);
//        } finally {
//            try {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            } catch (IOException ex) {
//                logger.error("Error closing output temp file: " + ex);
//            }
//        }
//    }
    private void executeInjection() {
        if (this.vista == null) {
            this.vista = Lookup.getDefault().lookup(Vista.class);
        }
    }
}
