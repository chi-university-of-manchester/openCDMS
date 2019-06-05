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

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;


/**
 * Abstract dialog for configuring permissions of an element
 *
 * @author Lucy Bridges
 */
public abstract class AbstractConfigurePermissionsDialog extends JDialog implements ActionListener {
		
	protected final String VIEW = "view";
	protected final String EDIT = "edit";

	/**
	 * Ok button; save changes and dismiss
	 */
	protected JButton okButton;

	/**
	 * Cancel button; dismiss
	 */
	protected JButton cancelButton;

	/**
	 * Flag to indicate if the dialog should be opened in read-only
	 * mode
	 */
	protected boolean readOnly;

	/**
	 * Creates a new ConfigureReportsDialog object.
	 *
	 * @param frame
	 */
	public AbstractConfigurePermissionsDialog(JDialog parent) {
		this(parent, true);
	}

	/**
	 * Creates a new ConfigureReportsDialog object.
	 *
	 * @param frame
	 */
	public AbstractConfigurePermissionsDialog(JDialog parent, boolean readOnly) {
		super(parent);
		this.readOnly = readOnly;
	}

	/**
	 *Initalise the ...
	 */
	protected void init() {        
	}

	/**
	 * Build the ok cancel button panel
	 *
	 * @return the ok cancel button panel
	 */
	protected JPanel buildButtonPanel() {
		okButton = new JButton(PropertiesHelper.getStringFor(
		"org.psygrid.datasetdesigner.ok"));
		okButton.addActionListener(this);
		cancelButton = new JButton(PropertiesHelper.getStringFor(
		"org.psygrid.datasetdesigner.cancel"));
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);

		if (!readOnly) {
			buttonPanel.add(cancelButton);
		}

		return buttonPanel;
	}
}
