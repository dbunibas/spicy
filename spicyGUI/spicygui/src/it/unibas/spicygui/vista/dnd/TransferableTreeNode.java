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

package it.unibas.spicygui.vista.dnd;

import it.unibas.spicygui.controllo.Scenario;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class TransferableTreeNode implements Transferable {

    public static DataFlavor SCENARIO_FLAVOR = new DataFlavor(Scenario.class, "Scenario");
    DataFlavor flavors[] = {SCENARIO_FLAVOR};
    Scenario scenario;

    public TransferableTreeNode(Scenario tp) {
        scenario = tp;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.getRepresentationClass() == Scenario.class);
    }

    public synchronized Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return (Object) scenario;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
