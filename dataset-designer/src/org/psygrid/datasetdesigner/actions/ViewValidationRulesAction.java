/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.psygrid.datasetdesigner.actions;

import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


/**
 * The option to display the View Validation Rules dialog; used by the DEL
 *
 */
public class ViewValidationRulesAction extends AbstractAction {
    /**
     * The main window of the application
     */
    private MainFrame frame;

    /**
     * True if called in DEL mode; false if not
     */
    private boolean isDEL;

    /**
     * Creates a new ViewValidationRulesAction object.
     *
     * @param frame the main window of the application
     */
    public ViewValidationRulesAction(MainFrame frame) {
        super("View Validation Rules");
        this.frame = frame;
        this.isDEL = false;
    }

    /**
     * Creates a new ViewValidationRulesAction object.
     *
     * @param frame the main window of the application
     * @param isDEL true if in DEL mode; false if not
     */
    public ViewValidationRulesAction(MainFrame frame, boolean isDEL) {
        super(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.viewvalidationrules"));
        this.frame = frame;
        this.isDEL = isDEL;
    }

    /**
     * Show the dialog
     *
     * @param aet trigger event 
     */
    public void actionPerformed(ActionEvent aet) {
        new ConfigureValidationRuleDialog(frame, "View Validation Rules", isDEL);
    }
}
