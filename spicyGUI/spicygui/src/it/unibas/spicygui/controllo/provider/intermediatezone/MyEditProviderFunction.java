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
import it.unibas.spicygui.widget.caratteristiche.CaratteristicheWidgetInterFunction;
import it.unibas.spicygui.widget.FunctionWidget;
import it.unibas.spicygui.controllo.mapping.operators.CreateCorrespondencesMappingTask;
import it.unibas.spicygui.controllo.mapping.operators.ReviewCorrespondences;
import it.unibas.spicygui.vista.intermediatezone.FunctionDialog;
import javax.swing.JDialog;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

public class MyEditProviderFunction implements EditProvider {

    private ReviewCorrespondences review = new ReviewCorrespondences();
    private CreateCorrespondencesMappingTask creator = new CreateCorrespondencesMappingTask();
    private CaratteristicheWidgetInterFunction caratteristiche;
    private FunctionWidget rootWidget;
    private FunctionDialog dialog;

    public MyEditProviderFunction(CaratteristicheWidgetInterFunction caratteristiche) {
        this.caratteristiche = caratteristiche;
        dialog = new FunctionDialog(WindowManager.getDefault().getMainWindow(), caratteristiche, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void edit(Widget widget) {
        rootWidget = (FunctionWidget) widget;
        if (caratteristiche.getSourceList().size() > 0) {
            dialog.clean();
            boolean oldButtonState = dialog.getFormValidation().getButtonState();
            String oldExpressionFunction = this.caratteristiche.getExpressionFunction();
            dialog.setVisible(true);
            if (dialog.getReturnStatus() == FunctionDialog.RET_CANCEL) {
                ripristina(oldButtonState, oldExpressionFunction);
            }
            if (caratteristiche.getTargetWidget() != null) {
                updateCorrespondences(oldButtonState, oldExpressionFunction);
            }
        }
    }

    private void updateCorrespondences(boolean oldButtonState, String oldExpressionFunction) {
        try {
            review.removeCorrespondence(caratteristiche.getValueCorrespondence());
            creator.createCorrespondenceWithFunction((LayerWidget) rootWidget.getParentWidget(), caratteristiche.getTargetWidget(), caratteristiche, caratteristiche.getConnectionInfo());
        } catch (ExpressionSyntaxException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Costanti.class, Costanti.SYNTAX_WARNING) + " : " + e.getMessage(), DialogDescriptor.WARNING_MESSAGE));
            creator.undo(caratteristiche.getValueCorrespondence());
            ripristina(oldButtonState, oldExpressionFunction);
        }
    }

    private void ripristina(boolean oldButtonState, String oldExpressionFunction) {
        dialog.getFormValidation().setButtonState(oldButtonState);
        this.caratteristiche.setExpressionFunction(oldExpressionFunction);
    }
}
