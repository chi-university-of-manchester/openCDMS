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

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;

import org.psygrid.securitymanager.actions.SwitchListsAction;
import org.psygrid.securitymanager.controller.ModelFetchingController;
import org.psygrid.securitymanager.listeners.ProjectBoxActionListener;
import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.renderers.AttributeListCellRenderer;
import org.psygrid.securitymanager.utils.LayoutUtils;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.wizard.WizardPanel;
import org.psygrid.www.xml.security.core.types.ProjectType;

import org.psygrid.securitymanager.utils.JListTransferHandler;

/**
 * Select projects dialog for project and role selection.
 * @author pwhelan
 *
 */
public class ModifyRolesPanel extends JPanel implements WizardPanel
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

	
	public ModifyRolesPanel()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(LayoutUtils.getInstance().createHeaderPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignrolestouser")), BorderLayout.NORTH);
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
		roleAssignedList = new JList();
		roleAssignedList.setTransferHandler(new JListTransferHandler());
		roleAssignedList.setCellRenderer(new AttributeListCellRenderer());

		//add project box here to the north
		projectBox = new JComboBox();
		projectBox.addActionListener(new ProjectBoxActionListener(ProjectBoxActionListener.ROLE_TYPE, projectBox, roleList, roleAssignedList));
		projectBox.setRenderer(new AttributeListCellRenderer());
		
		String addRoleName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignrole");
		JButton addRoleButton = new CustomIconButton(new SwitchListsAction(addRoleName, projectBox, roleList, roleAssignedList, true), "Assign Role");
		addRoleButton.setPreferredSize(new Dimension(40, 20));
		String removeRoleName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "unassignrole");
		JButton removeRoleButton = new CustomIconButton(new SwitchListsAction(removeRoleName, projectBox, roleList, roleAssignedList, false), "Unassign Role");
		
		selectionPanel.add(LayoutUtils.getInstance().createProjectSelectionPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "chooseproject"), projectBox),  BorderLayout.NORTH);
		selectionPanel.add(LayoutUtils.getInstance().createSelectionPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"roles"), PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"assignedRoles"), addRoleButton, removeRoleButton, 
											roleList, roleAssignedList), BorderLayout.CENTER);
		
		return selectionPanel;
	}
	
	public void refreshPanel()
	{
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
		oldSelectedProject = (ProjectType)projectBox.getSelectedItem();
		
		//ensure this is set
		UserModel.getInstance().setActiveProject(oldSelectedProject);
	}
	
	public void restoreModels()
	{
		String newUserName = UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname();

		//if user name is new; fetch fresh models
		if (currentUserName == null || !currentUserName.equals(newUserName) || UserModel.getInstance().isProjectsDirty(oldProjectBoxModel))
		{
			projectBox.setModel(UserModel.getInstance().getProjectsAsComboBoxModel());
			ProjectType selectedProject = (ProjectType)projectBox.getSelectedItem();
			roleList.setModel(ModelFetchingController.getInstance().getRolesModelForProject(selectedProject));
			roleAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(selectedProject));
			currentUserName = newUserName;
			storeModels();
		//if selected project isn't the same as the active project; set the active project
		} else {
			if (oldSelectedProject != UserModel.getInstance().getActiveProject())
			{
				projectBox.setModel(UserModel.getInstance().getProjectsAsComboBoxModel());
				projectBox.setSelectedItem(UserModel.getInstance().getActiveProject());
				roleList.setModel(ModelFetchingController.getInstance().getRolesModelForProject(UserModel.getInstance().getActiveProject()));
				roleAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(UserModel.getInstance().getActiveProject()));
				currentUserName = newUserName;
				storeModels();
			} else  {
				projectBox.setModel(oldProjectBoxModel);
				projectBox.setSelectedItem(oldSelectedProject);
				roleList.setModel(oldRolesListModel);
				roleAssignedList.setModel(oldRolesAssignedListModel);
			}
		}
	}

		
	
}
