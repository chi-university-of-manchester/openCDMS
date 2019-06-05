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
 * The action used to produce the validation rules configuration
 * dialogs used both by DSD and DEL (within DSD)
 *
 * @author pwhelan
  */
public class ConfigureValidationRulesAction extends AbstractAction {
    /**
     * The main window of the application
     */
    private MainFrame frame;

    /**
     * The flag to indicate if validation rules dialog is used in DEL mode
     */
    private boolean isDEL;

    /**
     * The flag to indicate if validation rules dialog is used in DEL mode
     */
    private boolean readOnly;

    
    /**
     * The validation rule 'manage validation rules option
     * @param frame the main window of the application
     */
    public ConfigureValidationRulesAction(MainFrame frame) {
        super(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.configurevalidationrules"));
        this.frame = frame;
        this.isDEL = false;
        this.readOnly = false;
    }

    /**
     * The validation rule 'view validation rules optino
     * @param frame the main window of the application
     * @param isDEL true if using DEL; false if not
     */
    public ConfigureValidationRulesAction(MainFrame frame, boolean isDEL) {
        super(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.configurevalidationrules"));
        this.frame = frame;
        this.isDEL = isDEL;
        this.readOnly = false;
    }

    /**
     * The validation rule 'view validation rules optino
     * @param frame the main window of the application
     * @param readOnly the file is open by someone else
     * @param isDEL true if using DEL; false if not
     */
    public ConfigureValidationRulesAction(MainFrame frame, boolean isDEL, boolean readOnly) {
        super(PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.actions.viewvalidationrules"));
        this.frame = frame;
        this.isDEL = isDEL;
        this.readOnly = readOnly;
    }
    
    /**
     * Show the configuration validation rule dialog
     */
    public void actionPerformed(ActionEvent aet) {
    	if (!isDEL && !readOnly) {
    		new ConfigureValidationRuleDialog(frame, isDEL);
    	} else {
            new ConfigureValidationRuleDialog(frame, isDEL, isDEL, readOnly);
    	}
    }
}
