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
 
package it.unibas.spicygui.controllo.provider;

import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.vista.intermediatezone.ConfidenceDialog;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class MyEditProviderConfidence extends AbstractAction {

    private ConnectionInfo connectionInfo;
    private ConnectionWidget connectionWidget;
    private ConfidenceDialog dialog;

    public MyEditProviderConfidence(ConnectionWidget connectionWidget, ConnectionInfo connectionInfo) {
        this.putValue(SMALL_ICON, ImageUtilities.image2Icon(ImageUtilities.loadImage(Costanti.ICONA_CONFIDENCE)));
        this.putValue(SHORT_DESCRIPTION, NbBundle.getMessage(Costanti.class, Costanti.TOOL_TIP_CONFIDENCE));
        this.connectionInfo = connectionInfo;
        this.connectionWidget = connectionWidget;
        dialog = new ConfidenceDialog(WindowManager.getDefault().getMainWindow(), connectionInfo, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }


    public void actionPerformed(ActionEvent e) {
        dialog.clean();
        double oldConfidence = this.connectionInfo.getConfidence();
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == ConfidenceDialog.RET_CANCEL) {
            this.connectionInfo.setConfidence(oldConfidence);
        } else {
            
        }


    }
}
