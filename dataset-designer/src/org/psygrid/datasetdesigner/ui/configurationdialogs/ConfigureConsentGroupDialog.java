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

import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;


import org.psygrid.datasetdesigner.actions.RemoveFromListAction;
import org.psygrid.datasetdesigner.actions.AddPrimaryConsentAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;




public class ConfigureConsentGroupDialog extends AbstractConfigurationDialog implements ActionListener {

	/**
	 * constructor
	 * @param frame main window of the application
	 */
	public ConfigureConsentGroupDialog(MainFrame frame) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureconsentgroups"), 
				false, false, false, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentformgroupsmgmt"));
	}
	

	/**
	 * Constructor
	 * @param frame the main window of the application
	 * @param readOnly readonly mode - file is in use by someone else
	 */
	public ConfigureConsentGroupDialog(MainFrame frame, boolean readOnly) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureconsentgroups"), 
				false, readOnly, false, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.consentformgroupsmgmt"));
	}

	
	protected void init() {
		if (activeDs != null) {
			list.setModel(ListModelUtility.convertArrayListToListModel(activeDs.getConsentGroups()));
		}
	}
	
	protected void save() {
		if (activeDs != null) {
			activeDs.setConsentFormGroups(ListModelUtility.convertListModelToConsentFormGroupList((DefaultListModel)list.getModel()));
		}
	}
	protected void initButtons() {
		super.initButtons();
		addButton = new JButton(new AddPrimaryConsentAction(this, list));
		editButton = new JButton(new AddPrimaryConsentAction(this, list, true));
	}


}
