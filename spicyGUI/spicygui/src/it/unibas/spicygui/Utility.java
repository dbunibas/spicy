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
package it.unibas.spicygui;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicy.model.expressions.Expression;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicygui.commons.AbstractScenario;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.commons.Modello;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.Scenarios;
import it.unibas.spicygui.controllo.window.ActionProjectTree;
import it.unibas.spicygui.vista.MappingTaskTopComponent;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Utility {

    private static Log logger = LogFactory.getLog(Utility.class);
    private static int X_OFFSET = 8;
    private static int Y_OFFSET = 0;

    public static Scenario gestioneScenario(MappingTask mappingTask, Modello modello, ActionProjectTree actionProjectTree) {
        Scenarios scenarios = (Scenarios) modello.getBean(Costanti.SCENARIOS);
        if (scenarios == null) {
            scenarios = new Scenarios("PROGETTO DI PROVA");
            scenarios.addObserver(actionProjectTree);
            modello.putBean(Costanti.SCENARIOS, scenarios);
            actionProjectTree.performAction();
        }
//        modello.putBean(Costanti.ACTUAL_SAVE_FILE, null);
        AbstractScenario scenario = new Scenario("SCENARIO DI PROVA", mappingTask, true);
        scenario.addObserver(actionProjectTree);
        scenarios.addScenario(scenario);
        Scenario scenarioOld = (Scenario) modello.getBean(Costanti.CURRENT_SCENARIO);
        if (scenarioOld != null) {
            LastActionBean lab = (LastActionBean) modello.getBean(Costanti.LAST_ACTION_BEAN);
            scenarioOld.setStato(lab.getLastAction());
        }
        modello.putBean(Costanti.CURRENT_SCENARIO, scenario);

        return (Scenario) scenario;
    }

    public static String sostituisciVirgolette(String stringa) {
        if (stringa == null) {
            return null;
        }
        if (stringa.startsWith("\"")) {
        } else {
            stringa = "\"" + stringa;
        }
        if (stringa.endsWith("\"")) {
        } else {
            stringa = stringa + "\"";
        }
        return stringa;
    }

    public static boolean verificaVirgolette(String stringa) {
        return (stringa.startsWith("\"") && stringa.endsWith("\""));
    }

    public static boolean verificaNumero(String stringa) {
        try {
            Double.parseDouble(stringa);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean verificaFunzione(String stringa) {
        try {
            new Expression(stringa);
            return true;
        } catch (ExpressionSyntaxException ese) {
            return false;
        }
    }

    public static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    public static void closeOutputWindow() {
        TopComponent outputWindow = null;
        WindowManager manager = WindowManager.getDefault();
        Set<TopComponent> topComponents = manager.getRegistry().getOpened();
        for (TopComponent tc : topComponents) {
            if (tc.getDisplayName() != null
                    && tc.getDisplayName().equalsIgnoreCase("Output")) {
                outputWindow = tc;
            }
        }
        if (outputWindow != null) {
            outputWindow.close();
        }
    }

    public static boolean closeAllTopComponent() {
        //TODO rimuove il metodo quando sarà risolto il bug della persistenza, vedere se sia possibile
        //basarsi su sugli scenari per poter chiudere tutto

        //   MappingTaskTopComponent mappingTaskTopComponent = MappingTaskTopComponent.findInstance();
        //if (mappingTaskTopComponent.close()) {
        WindowManager manager = WindowManager.getDefault();
        Set<TopComponent> topComponents = manager.getRegistry().getOpened();
        List<TopComponent> openTopComponents = new ArrayList<TopComponent>();
        List<MappingTaskTopComponent> mappingTaskTopComponents = new ArrayList<MappingTaskTopComponent>();
        boolean esito = true;
        for (TopComponent tc : topComponents) {
            if (tc != null) {
                if (tc instanceof MappingTaskTopComponent) {
                    mappingTaskTopComponents.add((MappingTaskTopComponent) tc);
                } else {
                    openTopComponents.add(tc);
                }
            }
        }
        if (openTopComponents.size() <= 1 && mappingTaskTopComponents.isEmpty()) {
            return true;
        }
        for (MappingTaskTopComponent mappingTaskTopComponent : mappingTaskTopComponents) {
            if (!mappingTaskTopComponent.close()) {
//                for (TopComponent tc : openTopComponents) {
//                    if (!tc.close()) {
//                        return false;
//                    }
//                }
                esito = false;
            }
            //}
        }
        if (esito) {
            for (TopComponent topComponent : openTopComponents) {
                topComponent.close();
            }
        }
        return esito;
    }

    private static Image buildImage(int numero) {
        Image image = null;
        switch (numero) {
            case 1:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_1, true);
                break;
            case 2:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_2, true);
                break;
            case 3:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_3, true);
                break;
            case 4:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_4, true);
                break;
            case 5:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_5, true);
                break;
            case 6:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_6, true);
                break;
            case 7:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_7, true);
                break;
            case 8:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_8, true);
                break;
            case 9:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_9, true);
                break;
            case 0:
                image = ImageUtilities.loadImage(Costanti.ICONA_NUMBER_0, true);
                break;
        }
        return image;
    }

    private static Image mergeImage(int numero, Image image) {
        Image imageReturn = null;
        switch (numero) {
            case 1:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_1, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 2:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_2, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 3:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_3, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 4:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_4, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 5:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_5, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 6:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_6, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 7:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_7, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 8:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_8, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 9:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_9, true), image, X_OFFSET, Y_OFFSET);
                break;
            case 0:
                imageReturn = ImageUtilities.mergeImages(ImageUtilities.loadImage(Costanti.ICONA_NUMBER_0, true), image, X_OFFSET, Y_OFFSET);
                break;
        }
        return imageReturn;
    }

    public static Image getImageNumber(Integer number) {
        String stringNumber = number.toString();
        int count = stringNumber.length();
        Image image = null;
        for (int i = count; i >= 1; i--) {
            char c = stringNumber.charAt(i - 1);
            int numero = Integer.parseInt(String.valueOf(c));
            if (i == count) {
                image = buildImage(numero);
            } else {
                image = mergeImage(numero, image);
            }
        }
        return image;
    }
}
