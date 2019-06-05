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

import java.util.ArrayList;

import javax.swing.*;

import org.psygrid.securitymanager.model.UserModel;

import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.wizard.*;

import com.jgoodies.forms.layout.FormLayout;

import org.psygrid.www.xml.security.core.types.ProjectType;

public class ConfirmDeletionPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	JLabel firstnameLabel = new JLabel();
	CustomLabel firstnameResponseLabel;
	JLabel lastnameLabel = new JLabel();
	CustomLabel lastnameResponseLabel;
	JLabel emailLabel = new JLabel();
	CustomLabel emailResponseLabel;
	
	public ConfirmDeletionPanel()
	{
		setLayout(new BorderLayout());
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createMainPanel(), BorderLayout.CENTER);
	}	
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		FormLayout layout = new FormLayout( 
				"pref", // columns
				 "pref, pref, pref, pref, pref"); // rows
		mainPanel.setLayout(layout);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(createUserDetailsPanel());
		mainPanel.add(createProjectsPanel());
		return mainPanel;
	}
	
	private void configurePanelSize(JPanel panel)
	{
		panel.setPreferredSize(new Dimension(600, (int)panel.getPreferredSize().getHeight()));
		panel.setMaximumSize(new Dimension(600, (int)panel.getMaximumSize().getHeight()));
		panel.setMinimumSize(new Dimension(600, (int)panel.getMinimumSize().getHeight()));
	}
	
	private JPanel createUserDetailsPanel()
	{
		JPanel userDetailsPanel = new JPanel(new SpringLayout());
		String userDetails = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userdetails");
		userDetailsPanel.setBorder(BorderFactory.createTitledBorder(userDetails));
		firstnameLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "firstname")+ " ");
		firstnameResponseLabel = new CustomLabel(UserModel.getInstance().getFirstname());
		lastnameLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "lastname") + " ");
		lastnameResponseLabel = new CustomLabel(UserModel.getInstance().getLastname());

		userDetailsPanel.add(firstnameLabel);
		userDetailsPanel.add(firstnameResponseLabel);
		userDetailsPanel.add(lastnameLabel);
		userDetailsPanel.add(lastnameResponseLabel);
		
//		Lay out the panel.
		SpringUtilities.makeCompactGrid(userDetailsPanel,
		                                2, 2, //rows, cols
		                                6, 6,        //initX, initY
		                                6, 2);       //xPad, yPad
		
		//configure size
		configurePanelSize(userDetailsPanel);
		return userDetailsPanel;
	}
	
	private JPanel createProjectsPanel()
	{
		JPanel projectsPanel = new JPanel(new SpringLayout());
		projectsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "deletingfromprojects")));
		projectsPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userdeleted")));
		
		ArrayList projects = UserModel.getInstance().getProjects();
		int projectsSize = projects.size();
		
		for (int i=0; i<projectsSize; i++)
		{
			projectsPanel.add(new CustomLabel(((ProjectType)projects.get(i)).getName()));
		}
		
		SpringUtilities.makeCompactGrid(projectsPanel,
                projectsSize+1, 1, //rows, cols
                6, 6,        //initX, initY
                6, 2);       //xPad, yPad
		
		configurePanelSize(projectsPanel);
		
		return projectsPanel;
	}
	
	private JPanel createHeaderPanel()
	{
		JPanel headerPanel = new JPanel();
		headerPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "summary")));
		return headerPanel;
	}
	
	public void refreshPanel()
	{
		this.removeAll();
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createMainPanel(), BorderLayout.CENTER);
		this.revalidate();
	}
	
}