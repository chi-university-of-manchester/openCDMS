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

import org.psygrid.datasetdesigner.actions.AddEslCustomFieldAction;
import org.psygrid.datasetdesigner.actions.RemoveEslCustomFieldFromListAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * @author Rob Harper
 *
 */
public class ConfigureRegisterDialog extends AbstractConfigurationDialog implements ActionListener {

	public ConfigureRegisterDialog(MainFrame frame) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configureregister"), false, false, true, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.registermgmt"), true, true);
	}
	
	public ConfigureRegisterDialog(MainFrame frame, boolean readOnly) {
		super(frame, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.viewregister"), false, readOnly, true, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.registermgmt"), true, true);
	}

	protected void init() {
		if (activeDs != null) {
			list.setModel(ListModelUtility.convertArrayListToListModel(activeDs.getEslCustomFields()));
		}
	}

	protected void initButtons() {
		removeButton = new JButton(new RemoveEslCustomFieldFromListAction(this, activeDs.getRandomHolderModel(), list));
		addButton = new JButton(new AddEslCustomFieldAction(this, list));
		editButton = new JButton(new AddEslCustomFieldAction(this, list, true));
	}

	@Override
	protected void save() {
		if (activeDs != null) {
			activeDs.setEslCustomFields(ListModelUtility.convertListModelToEslCustomFieldList((DefaultListModel)list.getModel()));
		}
		
	}

}
