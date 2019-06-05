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

import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.ui.configurationdialogs.AddEslCustomFieldDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to display add ESL custom field dialog
 * 
 * @author Rob Harper
 *
 */
public class AddEslCustomFieldAction extends AbstractAction {
	
	//the list of existing document groups
	private JList customFieldList;
	
	//the owner dialog
	private JDialog parentDialog;
	
	//true if doc group is in edit mode; false if not
	private boolean edit = false;
	
	/**
	 * Constructor
	 * @param parentDialog - the owner dialog
	 * @param customFieldList - the list 
	 */
	public AddEslCustomFieldAction(JDialog parentDialog,
								  JList customFieldList) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.parentDialog = parentDialog;
		this.customFieldList = customFieldList;
	}
	
	/**
	 * Constructor
	 * @param parentDialog - the owner dialog
	 * @param customFieldList - the list 
	 */
	public AddEslCustomFieldAction(JDialog parentDialog,
			                      JList customFieldList, 
			                      boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.parentDialog = parentDialog;
		this.customFieldList = customFieldList;
		this.edit = edit;
	}
	
	/**
	 * show the add document group dialog
	 * @param aet show the add document group dialog
	 */
	public void actionPerformed(ActionEvent aet) {
		
		if (edit) {
			new AddEslCustomFieldDialog(parentDialog, 
									   customFieldList,
									   ((EslCustomField)customFieldList.getSelectedValue()));
		} else {
			new AddEslCustomFieldDialog(parentDialog, customFieldList);
		}
	}
	
}