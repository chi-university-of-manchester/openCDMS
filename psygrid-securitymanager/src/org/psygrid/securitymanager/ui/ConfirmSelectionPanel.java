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

import org.psygrid.securitymanager.utils.DisplayTool;
import org.psygrid.securitymanager.utils.SpringUtilities;
import org.psygrid.securitymanager.wizard.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

import org.psygrid.securitymanager.utils.PropertiesHelper;

public class ConfirmSelectionPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private JLabel firstnameLabel = new JLabel();
	private CustomLabel firstnameResponseLabel;
	private JLabel lastnameLabel = new JLabel();
	private CustomLabel lastnameResponseLabel;
	private JLabel emailLabel = new JLabel();
	private CustomLabel emailResponseLabel;
	
	private JLabel mobileLabel = new JLabel();
	private CustomLabel mobileResponseLabel;
	
	private JLabel usernameLabel = new JLabel();
	private CustomLabel usernameResponseLabel;
	
	private boolean modify = false;
	
	private JPanel groupsPanel;
	
	public ConfirmSelectionPanel(boolean modify)
	{
		this.modify=modify; 
		setLayout(new BorderLayout());
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createMainPanel(), BorderLayout.CENTER);
	}
	
	public ConfirmSelectionPanel()
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
		mainPanel.add(createGroupsPanel());
		mainPanel.add(createRolesPanel());
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
		userDetailsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userdetails")));
		firstnameLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "firstname") + " ");
		firstnameResponseLabel = new CustomLabel(UserModel.getInstance().getFirstname());
		lastnameLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "lastname") + " ");
		lastnameResponseLabel = new CustomLabel(UserModel.getInstance().getLastname());
		
		mobileLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"mobilenumber"));
		mobileResponseLabel = new CustomLabel(UserModel.getInstance().getMobileNumber());
		
		
		emailLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "emailaddress") + " ");
		emailResponseLabel = new CustomLabel(UserModel.getInstance().getEmailAddress());

		userDetailsPanel.add(firstnameLabel);
		userDetailsPanel.add(firstnameResponseLabel);
		userDetailsPanel.add(lastnameLabel);
		userDetailsPanel.add(lastnameResponseLabel);
		
		if (!modify)
		{
			userDetailsPanel.add(emailLabel);
			userDetailsPanel.add(emailResponseLabel);
			usernameLabel = new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "userid"));
			usernameResponseLabel = new CustomLabel(UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname());
			userDetailsPanel.add(usernameLabel);
			userDetailsPanel.add(usernameResponseLabel);
			userDetailsPanel.add(mobileLabel);
			userDetailsPanel.add(mobileResponseLabel);
		}
		
		if (modify)
		{
			userDetailsPanel.add(emailLabel);
			userDetailsPanel.add(emailResponseLabel);
			userDetailsPanel.add(mobileLabel);
			userDetailsPanel.add(mobileResponseLabel);
			SpringUtilities.makeCompactGrid(userDetailsPanel,
                    4, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 2);       //xPad, yPad
		} else {
		//		Lay out the panel.
				SpringUtilities.makeCompactGrid(userDetailsPanel,
				                                5, 2, //rows, cols
				                                6, 6,        //initX, initY
				                                6, 2);       //xPad, yPad
		}
		
		//configure size
		configurePanelSize(userDetailsPanel);
		return userDetailsPanel;
	}
	
	private JPanel createProjectsPanel()
	{
		JPanel projectsPanel = new JPanel(new SpringLayout());
		projectsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignedprojects")));
		projectsPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"followingprojectsassigned")));
		
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
	
	private JPanel createGroupsPanel()
	{
		groupsPanel = new JPanel(new SpringLayout());
		groupsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"assignedgroups")));
		groupsPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "followinggroupsassigned")));
		
		ArrayList projects = UserModel.getInstance().getProjects();
		int projectsSize = projects.size();
		int numGroups = 0;
		
		for (int i=0; i<projectsSize; i++)
		{
			ProjectType currentProject = (ProjectType)projects.get(i);
			DefaultListModel groups = UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel(currentProject);
			
			for (int j=0; j<groups.getSize(); j++ )
			{
				GroupType currentGroup = (GroupType)groups.get(j);
				String fullGroupName = DisplayTool.getFullGroupName(currentGroup);
				groupsPanel.add(new CustomLabel(currentProject.getName() +  " - " + fullGroupName));
				numGroups++;
			}
		}
		
		SpringUtilities.makeCompactGrid(groupsPanel,
                numGroups+1, 1, //rows, cols
                6, 6,        //initX, initY
                6, 2);       //xPad, yPad

		configurePanelSize(groupsPanel);
		
		return groupsPanel;
	}
	
	private JPanel createRolesPanel()
	{
		JPanel rolesPanel = new JPanel(new SpringLayout());
		rolesPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignedroles")));
		rolesPanel.add(new CustomLabel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "followingrolesassigned")));
		
		ArrayList projects = UserModel.getInstance().getProjects();
		int projectsSize = projects.size();
		int numRoles = 0;
		
		for (int i=0; i<projectsSize; i++)
		{
			ProjectType currentProject = (ProjectType)projects.get(i);
			DefaultListModel roles = UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(currentProject);
			
			for (int j=0; j<roles.getSize(); j++)
			{
				RoleType currentRole = (RoleType)roles.get(j);
				rolesPanel.add(new CustomLabel((currentProject.getName() + " - " + currentRole.getName())));
				numRoles++;
			}
		}
		
		SpringUtilities.makeCompactGrid(rolesPanel,
                numRoles+1, 1, //rows, cols
                6, 6,        //initX, initY
                6, 2);       //xPad, yPad
		
		configurePanelSize(rolesPanel);
		
		return rolesPanel;
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