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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.ui.TextFieldWithStatus;
import org.psygrid.securitymanager.wizard.WizardPanel;

/**
 * Add user dialog.  Initial entry point for user management.
 * 
 * Dialog to enter first name, last name and email address (this is all that's required
 * to create a new user.
 * uid is created from firstname.lastname
 * 
 * @author pwhelan
 *
 */
public class AddUserPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private TextFieldWithStatus firstnameField = new TextFieldWithStatus(30, true);
	private TextFieldWithStatus lastnameField = new TextFieldWithStatus(30, true);
	private TextFieldWithStatus emailField = new TextFieldWithStatus(30, true);
	
	private JLabel firstnameLabel;
	private JLabel lastnameLabel;
	private JLabel emailLabel;
	
	public AddUserPanel()
	{
		add(createMainPanel());
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(null), BorderLayout.NORTH);
		mainPanel.add(createFormPanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	/**
	 * Creating the form panel for entering first name, last name and email
	 * address.
	 * 
	 * @return userPanel A JPanel containing first name, last name and email.
	 */
	private JPanel createFormPanel()
	{
		JPanel userPanel = new JPanel(new SpringLayout());
		userPanel.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));

		firstnameLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"firstname"), JLabel.TRAILING);
		lastnameLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"lastname"), JLabel.TRAILING);
		emailLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"emailaddress"), JLabel.TRAILING);
		
		userPanel.add(firstnameLabel);
		userPanel.add(firstnameField);
		userPanel.add(lastnameLabel);
		userPanel.add(lastnameField);
		userPanel.add(emailLabel);
		userPanel.add(emailField);
		
		SpringUtilities.makeCompactGrid(userPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		return userPanel;
	}
	
	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null)
		{
			headerPanel.add(new CustomLabel("Fill in the details for the new user below."));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
		}
		return headerPanel;
	}
	
	/**
	 * Checks that a firstname, lastname and email address has been set
	 * @return String containing error message of validation result; null if input is ok
	 */
	public String validateInput()
	{
		String result = null;
		
		if (firstnameField.getText() == null || firstnameField.getText().equals(""))
		{
			result = "First name must be set to proceed.";
		}
		else if (lastnameField.getText() == null || lastnameField.getText().equals(""))
		{
			result = "Last name must be set to proceed.";
		} else if (emailField.getText() == null || emailField.getText().equals(""))
		{
			result = "Email address must be set.";
		} else if (emailField.getText().indexOf("@") == -1)
		{
			result = "Please enter a valid email address";
		}
		
		return result;
	}
	
	/**
	 * Refresh panel with new header text but maintain user input.
	 * 
	 * @param headerText
	 */
	public void refreshPanel(String headerText)
	{
		removeAll();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(headerText), BorderLayout.NORTH);
		mainPanel.add(createFormPanel(), BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
	}
	
	public void refreshPanel()
	{
		//nothing needs to be updated here.
	}
	
	public void updateUserModel()
	{
		UserModel.getInstance().setEmailAddress(emailField.getText());
		UserModel.getInstance().setFirstname(firstnameField.getText());
		UserModel.getInstance().setLastname(lastnameField.getText());
	}

	
}