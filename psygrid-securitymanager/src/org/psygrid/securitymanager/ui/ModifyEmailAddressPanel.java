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
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.psygrid.securitymanager.controller.AAController;
import org.psygrid.securitymanager.listeners.InputFieldListener;
import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.wizard.WizardPanel;

public class ModifyEmailAddressPanel extends JPanel implements WizardPanel {

	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private TextFieldWithStatus emailField = new TextFieldWithStatus(30, false);
	private JLabel passwordLabel;
	
	private TextFieldWithStatus mobileField = new TextFieldWithStatus(30, false);
	private JLabel mobileLabel;
	
	private String currentUserName;
	private String emailString;
	
	public ModifyEmailAddressPanel()
	{
		add(createMainPanel());
	}
	
	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(null), BorderLayout.NORTH);
		mainPanel.add(createFormPanel(false), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel createFormPanel(boolean refresh)
	{
		JPanel formPanel = new JPanel(new SpringLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));
		passwordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"enteremail"), JLabel.TRAILING);

		emailField.getDocument().addDocumentListener(new InputFieldListener(InputFieldListener.EMAIL_ADDRESS));
		emailField.setText(UserModel.getInstance().getEmailAddress());
		
		mobileLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"verifymobile"), JLabel.TRAILING);
		mobileField.setText(UserModel.getInstance().getMobileNumber());
		
		formPanel.add(passwordLabel);
		formPanel.add(emailField);
		
		formPanel.add(mobileLabel);
		formPanel.add(mobileField);
		
		SpringUtilities.makeCompactGrid(formPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		return formPanel;
	}
	
	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null)
		{
			headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "emailuser") + " " + UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname()));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
		}
		return headerPanel;
	}

	
	public void updateEmailAddress() 
	{
		UserModel.getInstance().setEmailAddress(emailField.getText(), false);
		UserModel.getInstance().setMobileNumber(mobileField.getText());
	}
	
	public void refreshPanel() {
		refreshPanel(null);
	}
	
	public void refreshPanel(String headerText)
	{
		removeAll();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(headerText), BorderLayout.NORTH);
		
		String newUserName = UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname();
		//if user name is new; fetch fresh models
		if (currentUserName == null || !newUserName.equals(currentUserName)) {
			UserModel.getInstance().setEmailAddress(AAController.getInstance().getEmailAddressForCurrentUser(), false);
			UserModel.getInstance().setMobileNumber(AAController.getInstance().getMobileNumberForCurrentUser());
			mainPanel.add(createFormPanel(true), BorderLayout.CENTER);
			currentUserName = newUserName;
		} else {
			mainPanel.add(createFormPanel(false), BorderLayout.CENTER);
		}
		
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
	}

	
}
