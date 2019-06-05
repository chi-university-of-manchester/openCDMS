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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;

import org.psygrid.common.security.PasswordStrengthCheck;
import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.security.PasswordGenerator;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.wizard.WizardPanel;

public class ResetPasswordPanel extends JPanel implements WizardPanel {

	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private JPasswordField passwordField = new JPasswordField(30);
	private JPasswordField secondPasswordField = new JPasswordField(30);
	private JLabel passwordLabel;
	
	private JButton generatePwdButton;
	
	private String currentUserName;
	
	public ResetPasswordPanel()
	{
		add(createMainPanel());
	}
	
	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(null), BorderLayout.NORTH);
		mainPanel.add(createFormPanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel createFormPanel()
	{
		JPanel formPanel = new JPanel(new SpringLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(6,0,0,0));
		passwordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"enterpassword"), JLabel.TRAILING);
		JLabel reenterPasswordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"reenterpassword"), JLabel.TRAILING);
		final JLabel passwordStrengthLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"passwordstrength"), JLabel.TRAILING);
		final JLabel passwordStrength = new JLabel();

		passwordStrength.setPreferredSize(new Dimension(passwordStrength.getPreferredSize().width, passwordLabel.getPreferredSize().height));
		
		passwordField.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent arg0) {
				int strength = PasswordStrengthCheck.check(new String(passwordField.getPassword()));
				passwordStrength.setText(PasswordStrengthCheck.textualResult(strength));
			}
			public void keyTyped(KeyEvent arg0) {
			}
        	
        });
		
		generatePwdButton = new JButton("Generate");
		generatePwdButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						String password = PasswordGenerator.generatePassword(10, true);
						passwordField.setText(password);
						secondPasswordField.setText(password);
						int strength = PasswordStrengthCheck.check(password);
						passwordStrength.setText(PasswordStrengthCheck.textualResult(strength));
						JOptionPane.showMessageDialog(ResetPasswordPanel.this, "<html>Password is <b>"+password+"</b></html>", "Password", JOptionPane.INFORMATION_MESSAGE);
					}
				}
		);
		
		formPanel.add(passwordLabel);
		formPanel.add(passwordField);
		formPanel.add(generatePwdButton);		
		formPanel.add(reenterPasswordLabel);
		formPanel.add(secondPasswordField);
		formPanel.add(new JLabel());
		formPanel.add(passwordStrengthLabel);
		formPanel.add(passwordStrength);
		formPanel.add(new JLabel());
		
		SpringUtilities.makeCompactGrid(formPanel,
                3, 3, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		return formPanel;
	}
	
	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
//		headerPanel.setBackground(new Color(255,255,206));
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null)
		{
			headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "changepassword") + " " + UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname()));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
		}
		return headerPanel;
	}

	
	public void updatePassword() 
	{
		char[] charPassword = passwordField.getPassword();
		short[] password = new short[passwordField.getPassword().length];
		
		for (int i=0; i<charPassword.length; i++)
		{
			password[i] = (short)charPassword[i];
		}
		
		UserModel.getInstance().setPassword(password);
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
		String newUserName = UserModel.getInstance().getFirstname()+UserModel.getInstance().getLastname();
		
		if (currentUserName == null || !currentUserName.equals(newUserName))
		{
			currentUserName = newUserName;
			clearFields();
		}
		
		mainPanel.add(createFormPanel(), BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
	}
	
	public boolean confirmMatch()
	{
		return passwordField.getText().equals(secondPasswordField.getText());
	}
	
	public boolean confirmSixCharacters()
	{
		boolean greaterThanSix = true;
		
		if (passwordField.getPassword().length < 6
				|| secondPasswordField.getPassword().length < 6)
		{
			greaterThanSix = false;
		}
			
		return greaterThanSix;
	}
	
	public void clearFields() {
		passwordField.setText("");
		secondPasswordField.setText("");
	}

	
}
