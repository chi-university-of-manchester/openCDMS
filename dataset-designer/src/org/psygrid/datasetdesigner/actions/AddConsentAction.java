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

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.ConsentForm;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureConsentDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Add consent dialog
 * @author pwhelan
 */
public class AddConsentAction extends AbstractAction {

	
	//list of existing consent forms
	private final JList consentList;
	
	//the owner dialog
	private JDialog parentDialog;
	
	//true if editing consent; false if not
	private boolean edit;
	
	/**
	 * Constructor
	 * @param parentDialog the owner dialog
	 * @param datasetbox the dataset selector drop-down
	 * @param consentList the list of existing consents
	 * @param consentMap the map containing consents
	 */
	public AddConsentAction(JDialog parentDialog, 
							JList consentList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.consentList = consentList;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * Constructor 
	 * @param parentDialog the owner dialog
	 * @param datasetBox the dataset selector
	 * @param consentList the list of consents
	 * @param consentMap the consent mapping
	 * @param edit true if editing consent; false if not
	 */
	public AddConsentAction(JDialog parentDialog,
								JList consentList,
								boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.consentList = consentList;
		this.edit=edit;
		this.parentDialog = parentDialog;
	}
	
	/**
	 * show consent configuration dialog
	 * @param aet the calling action event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (edit) {
			if (consentList.getSelectedValue() != null) {
				new ConfigureConsentDialog(parentDialog,
						 consentList, 
						 (ConsentForm)consentList.getSelectedValue());
			}
		} else {
			new ConfigureConsentDialog(parentDialog,
									   consentList);
		}
	}
	
}
