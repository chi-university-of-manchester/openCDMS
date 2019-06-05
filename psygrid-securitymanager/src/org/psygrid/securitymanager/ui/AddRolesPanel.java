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

import javax.swing.*;

import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.wizard.*;

import org.psygrid.securitymanager.listeners.*;
import org.psygrid.securitymanager.model.UserModel;

import org.psygrid.securitymanager.actions.SwitchListsAction;
import org.psygrid.securitymanager.controller.ModelFetchingController;
import org.psygrid.securitymanager.renderers.*;

import org.psygrid.www.xml.security.core.types.ProjectType;

import org.psygrid.securitymanager.utils.*;

import org.psygrid.securitymanager.ui.CustomIconButton;

import org.psygrid.securitymanager.utils.JListTransferHandler;

/**
 * Select projects dialog for project and role selection.
 * @author pwhelan
 *
 */
public class AddRolesPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	//combo boxes to choose from
	private JComboBox projectBox;
	private JList roleList;
	private JList roleAssignedList;
	
	private String currentUserName = null;
	
	private ComboBoxModel oldProjectBoxModel = null;
	private ListModel oldRolesListModel = null;
	private ListModel oldRolesAssignedListModel = null;
	private ProjectType oldSelectedProject = null;
	
	private ProjectBoxActionListener projectBoxActionListener = null;
	
	
	public AddRolesPanel()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		String assignroles = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignrolestouser");
		mainPanel.add(LayoutUtils.getInstance().createHeaderPanel(assignroles), BorderLayout.NORTH);
		mainPanel.add(createSelectionPanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel createSelectionPanel()
	{
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout());
		
		roleList = new JList();
		roleList.setTransferHandler(new JListTransferHandler());
		roleList.setCellRenderer(new AttributeListCellRenderer());
		String roles = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "roles");
		String assignedRoles = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignedroles");
		roleAssignedList = new JList();
		roleAssignedList.setTransferHandler(new JListTransferHandler());
		roleAssignedList.setCellRenderer(new AttributeListCellRenderer());

		//add project box here to the north
		projectBox = new JComboBox();
		projectBoxActionListener = new ProjectBoxActionListener(ProjectBoxActionListener.ROLE_TYPE, projectBox, roleList, roleAssignedList);
		projectBox.addActionListener(projectBoxActionListener);
		projectBox.setRenderer(new AttributeListCellRenderer());
		
		String addRoleName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignrole");
		String assignRoleHeader = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignrolesheader");
		JButton addRoleButton = new CustomIconButton(new SwitchListsAction(addRoleName, projectBox, roleList, roleAssignedList, true), assignRoleHeader);
		addRoleButton.setPreferredSize(new Dimension(40, 20));
		String removeRoleName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "unassignrole");
		String unassignRoleHeader = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "unassignrolesheader");
		JButton removeRoleButton = new CustomIconButton(new SwitchListsAction(removeRoleName, projectBox, roleList, roleAssignedList, false), unassignRoleHeader);
		String chooseProject = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "chooseproject");
		selectionPanel.add(LayoutUtils.getInstance().createProjectSelectionPanel(chooseProject, projectBox),  BorderLayout.NORTH);
		selectionPanel.add(LayoutUtils.getInstance().createSelectionPanel(roles, assignedRoles, addRoleButton, removeRoleButton, 
											roleList, roleAssignedList), BorderLayout.CENTER);
		return selectionPanel;
	}
	
	public void refreshPanel()
	{
		projectBox.removeActionListener(projectBoxActionListener);
		projectBoxActionListener = null;
		removeAll();
		add(createMainPanel(), BorderLayout.CENTER);
		revalidate();
		restoreModels();
	}

	
	public void storeModels()
	{
		oldProjectBoxModel = projectBox.getModel();
		oldRolesAssignedListModel = roleAssignedList.getModel();
		oldRolesListModel = roleList.getModel();
		
		if (projectBox.getSelectedItem() != null)
		{
			oldSelectedProject = (ProjectType)projectBox.getSelectedItem();
		} else {
			oldSelectedProject = (ProjectType)projectBox.getModel().getElementAt(0);
		}

		//ensure this is set
		UserModel.getInstance().setActiveProject(oldSelectedProject);
	}
	
	public void restoreModels()
	{
		String newUserName = UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname();

		//if user name is new or project model is dirty 
		//or active project is different to currently selected, fetch fresh models
		if (currentUserName == null 
				|| !currentUserName.equals(newUserName) 
				|| UserModel.getInstance().isProjectsDirty(oldProjectBoxModel)
				|| !oldSelectedProject.equals(UserModel.getInstance().getActiveProject()))
		{
			projectBox.setModel(UserModel.getInstance().getProjectsAsComboBoxModel());
			if (UserModel.getInstance().getActiveProject() != null) {
				projectBox.setSelectedItem(UserModel.getInstance().getActiveProject());
			}
			ProjectType selectedProject = (ProjectType)projectBox.getSelectedItem();
			roleList.setModel(ModelFetchingController.getInstance().getRolesModelForProject(selectedProject));
			roleAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(selectedProject));
			currentUserName = newUserName;
			storeModels();
		//if selected project isn't the same as the active project; set the active project
		} else  {
			projectBox.setModel(oldProjectBoxModel);
			projectBox.setSelectedItem(oldSelectedProject);
			roleList.setModel(oldRolesListModel);
			roleAssignedList.setModel(oldRolesAssignedListModel);
			
			roleList.validate();
			roleList.repaint();
			roleAssignedList.validate();
			roleAssignedList.repaint();
			
			//adding this to fix refreshing bug (Swing update problem?)
			if (UserModel.getInstance().getActiveProject() != null) {
				projectBox.setSelectedItem(UserModel.getInstance().getActiveProject());
			}
		}
	}
	
	
}
