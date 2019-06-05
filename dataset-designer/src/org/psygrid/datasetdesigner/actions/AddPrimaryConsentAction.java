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

import java.awt.event.ActionEvent;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigurePrimaryConsentDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to show a primary consent form
 * @author pwhelan
 */
public class AddPrimaryConsentAction extends AbstractAction {
	
	private JList consentList;
	
	private JDialog parentDialog;
	
	private boolean edit;
	
	public AddPrimaryConsentAction(JDialog parentDialog, 
							JList consentList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.consentList = consentList;
		this.parentDialog = parentDialog;
	}
	
	public AddPrimaryConsentAction(JDialog parentDialog,
								JList consentList,
								boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.consentList = consentList;
		this.edit=edit;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			new ConfigurePrimaryConsentDialog(parentDialog,
									          consentList, 
									          true);
		} else {
			new ConfigurePrimaryConsentDialog(parentDialog,
					                   		  consentList);
		}
	}
	
}
