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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.psygrid.securitymanager.utils.PropertiesHelper;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Login Dialog
 * Simple username and password dialog to allow PysGird users acces the Security Manager.
 * 
 * @author pwhelan
 */
public class LoginDialog extends JDialog implements ActionListener
{
	private final static int LOGIN_DIALOG_WIDTH = 300;
	private final static int LOGIN_DIALOG_HEIGHT = 100;
	
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private TextFieldWithStatus userField;
	private JPasswordField passwordField;
	
	private JButton okButton;
	private JButton cancelButton;
	
	public LoginDialog()
	{
		getContentPane().setPreferredSize(new Dimension(LOGIN_DIALOG_WIDTH, LOGIN_DIALOG_HEIGHT));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createMainPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
		
		//Get the screen size and set the location.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(new Point((int)(screenSize.getWidth()/2 - LOGIN_DIALOG_WIDTH/2), (int)(screenSize.getHeight()/2 -LOGIN_DIALOG_HEIGHT/2)));
		pack();
		
		setTitle(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"logindialogtitle"));
	}
	
	/**
	 * Creates the main panel containing an entry field for the username 
	 * and password
	 * @return holderPanel - a JPanel containing the username and password fields.
	 */
	private JPanel createMainPanel()
	{
		//create a holder here just to get the layout right.
		JPanel holderPanel = new JPanel();
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(200, 60));
		FormLayout layout = new FormLayout( 
				"pref, 4dlu, 50dlu, 4dlu, min", // columns
				 "pref, 2dlu, pref, 2dlu, pref"); // rows
		CellConstraints cc = new CellConstraints();
		mainPanel.setLayout(layout);
		JLabel userLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"username"));
		JLabel passwordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"password"));
		
		userField = new TextFieldWithStatus(60, true);
		passwordField = new JPasswordField(60);
		mainPanel.add(userLabel, cc.xy(1, 1));
		mainPanel.add(userField, cc.xyw(3, 1, 3));
		mainPanel.add(passwordLabel, cc.xy(1, 3));
		mainPanel.add(passwordField, cc.xyw(3, 3, 3));
		holderPanel.add(mainPanel,BorderLayout.SOUTH);
		return holderPanel;
	}

	/**
	 * Creates the ok and cancel button option
	 * 
	 * @return
	 */
	private JPanel createButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		okButton = new JButton (PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"ok"));
		cancelButton = new JButton (PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"cancel"));
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	/**
	 * 
	 * Action Handler currently used by cancel button; move this to a better location.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		System.exit(-1);
	}
	
}
