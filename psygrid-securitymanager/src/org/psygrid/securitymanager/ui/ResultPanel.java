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


package org.psygrid.securitymanager.ui;

import org.psygrid.securitymanager.wizard.WizardPanel;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.ui.CustomLabel;

/**
 * ResultPanel to display the result of an attempt to add or 
 * modify the user.
 *  
 * @author pwhelan
 */
public class ResultPanel extends JPanel implements WizardPanel
{
	
	public ResultPanel()
	{
		refreshPanel();
	}
	
	/**
	 * Create the main panel containing the results of the add/modify user.
	 * @return
	 */
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.add(new CustomLabel("User "  + UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + "has been added."));
		return mainPanel;
	}
	
	public void refreshPanel() {
		removeAll();
		add(createMainPanel(), BorderLayout.CENTER);
		revalidate();
	}
	
}