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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import org.psygrid.datasetdesigner.actions.AddUnitAction;
import org.psygrid.datasetdesigner.actions.ViewUnitAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import javax.swing.DefaultListModel;
import javax.swing.JButton;


/**
 * Configure the Units for datasets
 *
 * @author pwhelan
 */
public class ConfigureUnitsDialog extends AbstractConfigurationDialog {
    /**
     * Constructor - lay out the dialog and initialise
     * @param frame the parent frame
     */
    public ConfigureUnitsDialog(MainFrame frame, boolean viewOnly, boolean readOnly) {
        super(frame,
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.configureunits"), viewOnly, readOnly,
            true, 
            PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.unitsmgmt"));
    }

    /**
     * Initialise the add and edit buttons
     */
    protected void initButtons() {
    	super.initButtons();
    	
    	if (readOnly) {
    		return;
    	}
    	
        if (viewOnly) {
            editButton = new JButton(new ViewUnitAction(this, list));
        } else {
            editButton = new JButton(new AddUnitAction(this, list, true));
        }

        addButton = new JButton(new AddUnitAction(this, list));
    }

    /**
     * Initialise the list with the existing units
     */
    protected void init() {
        if (activeDs != null) {
            list.setModel(ListModelUtility.convertListToListModel(activeDs.getUnits()));
        }
    }

    /**
     * Save the current settings
     */
    protected void save() {
        if (activeDs != null) {
            activeDs.setUnits(ListModelUtility.convertListModelToUnitList(
                    (DefaultListModel) list.getModel()));
        }
    }
}
