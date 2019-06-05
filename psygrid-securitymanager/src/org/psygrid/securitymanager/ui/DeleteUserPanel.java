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

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.BorderLayout;

import org.psygrid.securitymanager.utils.*;

import org.psygrid.securitymanager.wizard.*;

import org.psygrid.securitymanager.model.UserModel;

import org.psygrid.securitymanager.ui.TextFieldWithStatus;

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
public class DeleteUserPanel extends JPanel implements WizardPanel, ActionListener
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private TextFieldWithStatus firstnameField;
	private TextFieldWithStatus lastnameField;
	private TextFieldWithStatus userIDField;
	
	private JLabel firstnameLabel;
	private JLabel lastnameLabel;
	private JLabel userIDLabel;
	
	JRadioButton byNameButton = new JRadioButton("Search by Name");
	JRadioButton byUserIDButton = new JRadioButton("Search by User ID");

	
	public DeleteUserPanel()
	{
		add(createMainPanel());
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
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

		firstnameField = new TextFieldWithStatus(30, true);
		lastnameField = new TextFieldWithStatus(30, true);
		userIDField = new TextFieldWithStatus(30, true);
		
		ButtonGroup group = new ButtonGroup();
		
		byUserIDButton.addActionListener(this);
		byNameButton.addActionListener(this);
		
		byNameButton.setSelected(true);
		userIDField.setEnabled(false);

		group.add(byNameButton);
		group.add(byUserIDButton);
		
		firstnameField.setText(UserModel.getInstance().getFirstname());
		lastnameField.setText(UserModel.getInstance().getLastname());
		userIDField.setText(UserModel.getInstance().getUserID());
		
		firstnameLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"firstname"), JLabel.TRAILING);
		lastnameLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"lastname"), JLabel.TRAILING);
		userIDLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userid"), JLabel.TRAILING);
		
		userPanel.add(byNameButton);
		userPanel.add(new JLabel(""));

		userPanel.add(firstnameLabel);
		userPanel.add(firstnameField);
		userPanel.add(lastnameLabel);
		userPanel.add(lastnameField);
		
		JLabel orLabel = new JLabel("OR");
		JLabel dummyLabel = new JLabel("");

		userPanel.add(dummyLabel);
		userPanel.add(orLabel);
		userPanel.add(byUserIDButton);
		userPanel.add(new JLabel(""));

		userPanel.add(userIDLabel);
		userPanel.add(userIDField);
		
		SpringUtilities.makeCompactGrid(userPanel,
                6, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		return userPanel;
	}
	
	private JPanel createHeaderPanel()
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userdetailstobedeleted")));
		return headerPanel;
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

	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null)
		{
			headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userdetailstobedeleted")));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
			firstnameField.setText("");
			lastnameField.setText("");
			userIDField.setText("");
			//focus will go back to name searching, ensure that user model is cleaned up
			UserModel.getInstance().setUserID(userIDField.getText(), true);
		}

		return headerPanel;
	}

	public void refreshPanel()
	{
		//nothing needs to be updated here.
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == byNameButton)
		{
			if (byNameButton.isSelected())
			{
				firstnameField.setText("");
				lastnameField.setText("");
				userIDField.setText("");
				firstnameField.setEnabled(true);
				lastnameField.setEnabled(true);
				userIDField.setEnabled(false);
			} else {
				firstnameField.setEnabled(false);
				lastnameField.setEnabled(false);
				userIDField.setEnabled(true);
			}
		} else if (e.getSource() == byUserIDButton)
		{
			if (byUserIDButton.isSelected())
			{
				firstnameField.setText("");
				lastnameField.setText("");
				userIDField.setText("");
				firstnameField.setEnabled(false);
				lastnameField.setEnabled(false);
				userIDField.setEnabled(true);
			} else {
				firstnameField.setEnabled(true);
				lastnameField.setEnabled(true);
				userIDField.setEnabled(false);
			}
		}
	}

	public void updateUserModel()
	{
		if(byNameButton.isSelected())
		{
			UserModel.getInstance().setFirstname(firstnameField.getText());
			UserModel.getInstance().setLastname(lastnameField.getText());
			//don't cause the user model to update the names again!
			UserModel.getInstance().setUserID(userIDField.getText(), true);
		} else {
			UserModel.getInstance().setUserID(userIDField.getText());
		}
	}

}