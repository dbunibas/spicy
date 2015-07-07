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
 
package it.unibas.spicygui.controllo.provider.intermediatezone;

import it.unibas.spicy.model.exceptions.ExpressionSyntaxException;
import it.unibas.spicygui.Costanti;
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterConst;
import it.unibas.spicygui.widget.ConstantWidget;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.Utility;
import it.unibas.spicygui.vista.intermediatezone.ConstantDialog;
import it.unibas.spicygui.widget.caratteristiche.ConnectionInfo;
import javax.swing.JDialog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class MyEditProviderConst implements EditProvider {

    private ReviewCorrespondences review = new ReviewCorrespondences();
    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private CaratteristicheWidgetInterConst caratteristiche;
    private ConstantDialog dialog;
    private ConstantWidget rootNode;
    private Log logger = LogFactory.getLog(MyEditProviderConst.class);

    public MyEditProviderConst(CaratteristicheWidgetInterConst caratteristiche) {
        this.caratteristiche = caratteristiche;
        this.dialog = new ConstantDialog(WindowManager.getDefault().getMainWindow(), caratteristiche, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void edit(Widget widget) {
        if (!(caratteristiche.getTipoFunzione() || caratteristiche.getTipoNumero() || caratteristiche.getTipoStringa())) {
            caratteristiche.setTipoStringa(true);
            caratteristiche.getFormValidation().setTextFieldState(true);
            caratteristiche.getFormValidation().setButtonState(false);
        }
        rootNode = (ConstantWidget) widget;
        dialog.clean();
        CaratteristicheWidgetInterConst oldCaratteristiche = caratteristiche.clone();
        boolean oldButtonState = dialog.getFormValidation().getButtonState();
        dialog.setVisible(true);
        try {
            verificaDati();
            if (dialog.getReturnStatus() == ConstantDialog.RET_CANCEL) {
                ripristina(oldButtonState, oldCaratteristiche);
            } else if (caratteristiche.getConnectionList().size() > 0) {
                updateCorrespondences(oldButtonState, oldCaratteristiche);
            }
        } catch (ExpressionSyntaxException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e, DialogDescriptor.WARNING_MESSAGE));
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING));
            ripristina(oldButtonState, oldCaratteristiche);
        }
    }

    private void ripristina(boolean oldButtonState, CaratteristicheWidgetInterConst oldCaratteristiche) {
        dialog.getFormValidation().setButtonState(oldButtonState);
        caratteristiche.setCostante(oldCaratteristiche.getCostante());
        caratteristiche.setTipoFunzione(oldCaratteristiche.getTipoFunzione());
        caratteristiche.setTipoNumero(oldCaratteristiche.getTipoNumero());
        caratteristiche.setTipoStringa(oldCaratteristiche.getTipoStringa());
    }

    private void updateCorrespondences(boolean oldButtonState, CaratteristicheWidgetInterConst oldCaratteristiche) {
        ConnectionInfo connectionInfoExtern = null;
        try {
            for (ConnectionInfo connectionInfo : caratteristiche.getConnectionList()) {
                connectionInfoExtern = connectionInfo;
                review.removeCorrespondence(connectionInfo.getValueCorrespondence());
                creator.createCorrespondenceWithSourceValue((LayerWidget) rootNode.getParentWidget(), rootNode, connectionInfo.getTargetWidget(), connectionInfo);
            }
        } catch (ExpressionSyntaxException ese) {
            creator.undo(connectionInfoExtern.getValueCorrespondence());
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + ese.getMessage(), DialogDescriptor.WARNING_MESSAGE));
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING));
            ripristina(oldButtonState, oldCaratteristiche);
        }
    }

    private void verificaDati() {
        if (this.dialog.getJRadioButtonFunction().isSelected()) {
            caratteristiche.setCostante(this.dialog.getJComboBoxFunction().getSelectedItem());
        }
        if (this.dialog.getJRadioButtonNumber().isSelected()) {
            Double.parseDouble((String) caratteristiche.getCostante());
        }
        if (this.dialog.getJRadioButtonString().isSelected()) {
            String valoreCostante = (String) caratteristiche.getCostante();
            caratteristiche.setCostante(Utility.sostituisciVirgolette(valoreCostante));
        }
    }
}
