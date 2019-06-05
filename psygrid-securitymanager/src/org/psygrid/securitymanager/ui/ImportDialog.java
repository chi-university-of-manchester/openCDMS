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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.securitymanager.Application;
import org.psygrid.securitymanager.actions.BrowseForCertAction;
import org.psygrid.securitymanager.actions.CancelDeleteAction;
import org.psygrid.securitymanager.actions.OKImportAction;
import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.ui.TextFieldWithStatus;

/**
 * Dialog GUI for importing certificates to use with the security manager.
 * 
 * @author pwhelan
 */
public class ImportDialog extends JDialog
{
	
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private JDialog parentDialog;
	private TextFieldWithStatus keystoreField;
	private JPasswordField passwordField;
	private Application application;
	
	public ImportDialog(Application application, JDialog parentDialog)
	{
		super(application, true);
		setTitle(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "importcertificate"));
        ((java.awt.Frame)getOwner()).setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
		this.parentDialog = parentDialog;
		this.application = application;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createHeaderPanel(null), BorderLayout.NORTH);
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}
	
	public JPanel buildMainPanel()
	{
		keystoreField = new TextFieldWithStatus(40, true);
		passwordField = new JPasswordField(40);

		JPanel mainPanel = new JPanel(new SpringLayout());
		JLabel keystoreLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "keystorelocation"));
		JLabel passwordLabel = new JLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "keystorepassword"));
		JButton browseButton = new JButton(new BrowseForCertAction(this, keystoreField));
		
		mainPanel.add(keystoreLabel);
		mainPanel.add(keystoreField);
		mainPanel.add(browseButton);
		mainPanel.add(passwordLabel);
		mainPanel.add(passwordField);
		mainPanel.add(new JLabel(""));
		
		SpringUtilities.makeCompactGrid(mainPanel,
                2, 3, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
        return mainPanel;
	}
	
	public JPanel buildButtonPanel()
	{	
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(new OKImportAction(application, this, parentDialog, keystoreField, passwordField));
		JButton cancelButton = new JButton(new CancelDeleteAction(this));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}
	
	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null)
		{
			headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "entercert")));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
		}
		return headerPanel;
	}
	
	/**
	 * Refresh panel with new header text but maintain user input.
	 * 
	 * @param headerText
	 */
	public void refreshPanel(String headerText)
	{
		getContentPane().removeAll();
		getContentPane().add(createHeaderPanel(headerText), BorderLayout.NORTH);
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(),  BorderLayout.SOUTH);
		pack();
	}
	
}