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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import org.psygrid.datasetdesigner.actions.AddTransformerAction;
import org.psygrid.datasetdesigner.actions.ViewTransformerAction;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.utils.ListModelUtility;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Configuration dialog for datasets' transformers
 * @author pwhelan
 */
public class ConfigureTransformersDialog extends AbstractConfigurationDialog {

	/**
	 * Constructor
	 * @param frame the main window of the application
	 */
	public ConfigureTransformersDialog(MainFrame frame, boolean viewOnly, boolean readOnly) {
		super(frame,  PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.configuretransformers"), viewOnly, readOnly, false,PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.transformermgmt"));
	}


	/*
	 * Initialise the transformers in the list 
	 */
	protected void init() {
		if (activeDs != null) {
			list.setModel(ListModelUtility.convertArrayListToListModel(activeDs.getTransformers()));
		}
	}
	
	/**
	 * Initialise the edit and add buttons
	 */
	protected void initButtons() {
		super.initButtons();
		if (viewOnly) {
			editButton = new JButton(new ViewTransformerAction(this, list));
		}
		else {
			addButton = new JButton(new AddTransformerAction(this, list));
			if (activeDs.getDs().isPublished()) {
				editButton = new JButton(new ViewTransformerAction(this, list));
			} else {
				editButton = new JButton(new AddTransformerAction(this, list, true));
			}
		}
	}

	/**
	 * Save the currently configured transformers
	 */
	protected void save() {
		if (activeDs != null) {
			activeDs.setTransformers(ListModelUtility.convertListModelToTransformerList((DefaultListModel)list.getModel()));
		}
	}

}