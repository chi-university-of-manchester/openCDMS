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
public class ModifyGroupsPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	//combo boxes to choose from
	private JComboBox projectBox;
	
	private JList groupList;
	private JList groupAssignedList;
	
	private String currentUserName;
	
	//store so that we don't need to reset the models each type a user goes back or forth
	private ProjectType oldSelectedProject = null;
	private ComboBoxModel oldProjectBoxModel = null;
	private ListModel oldGroupsAssignedListModel = null;
	private ListModel oldGroupsListModel = null;

	
	public ModifyGroupsPanel()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}
	
	/**
	 * Create the main panel for the add groups panel.
	 * This panel contains all the usu
	 * @return
	 */
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(LayoutUtils.getInstance().createHeaderPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX +"assigngroupstouser")), BorderLayout.NORTH);
		mainPanel.add(createSelectionPanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel createSelectionPanel()
	{
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout());
		
		groupList = new JList();
		groupList.setTransferHandler(new JListTransferHandler());
		groupList.setCellRenderer(new AttributeListCellRenderer());
		
		groupAssignedList = new JList();
		groupAssignedList.setTransferHandler(new JListTransferHandler());
		groupAssignedList.setCellRenderer(new AttributeListCellRenderer());

		projectBox = new JComboBox();
		projectBox.setRenderer(new AttributeListCellRenderer());
		//add listener for updating
		projectBox.addActionListener(new ProjectBoxActionListener(ProjectBoxActionListener.GROUP_TYPE, projectBox, groupList, groupAssignedList));
		
		String addGroupName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"assigngroup");
		JButton addGroupButton = new CustomIconButton(new  SwitchListsAction(addGroupName, projectBox, groupList, groupAssignedList, true), PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assigngroupsheader"));
		String removeGroupName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"unassigngroup");
		JButton removeGroupButton = new CustomIconButton(new SwitchListsAction(removeGroupName, projectBox,  groupList, groupAssignedList, false), PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "unassigngroupsheader"));
		
		selectionPanel.add(LayoutUtils.getInstance().createProjectSelectionPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "chooseproject"), projectBox), BorderLayout.NORTH);
		selectionPanel.add(LayoutUtils.getInstance().createSelectionPanel(PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "groups"), PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignedgroups"), addGroupButton, removeGroupButton, groupList, groupAssignedList), BorderLayout.CENTER);
		
		return selectionPanel;
	}

	public void refreshPanel()
	{
		removeAll();
		add(createMainPanel());
		revalidate();
		restoreModels();
	}
	
	public void storeModels() {
		oldProjectBoxModel = projectBox.getModel();
		oldSelectedProject = (ProjectType)projectBox.getSelectedItem();
		oldGroupsAssignedListModel = groupAssignedList.getModel();
		oldGroupsListModel = groupList.getModel();
		
		//ensure this is set
		UserModel.getInstance().setActiveProject(oldSelectedProject);
	}
	
	public void restoreModels() {
		String newUserName = UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname();
		
		//if user name is new; fetch fresh models
		if (currentUserName == null || !newUserName.equals(currentUserName) || UserModel.getInstance().isProjectsDirty(oldProjectBoxModel))
		{
			projectBox.setModel(UserModel.getInstance().getProjectsAsComboBoxModel());
			ProjectType selectedProject = (ProjectType)projectBox.getSelectedItem();
			groupList.setModel(ModelFetchingController.getInstance().getGroupsModel(selectedProject));
			groupAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel(selectedProject));
			currentUserName = newUserName;
			storeModels();
		} else {
			if (oldSelectedProject != UserModel.getInstance().getActiveProject())
			{
				projectBox.setModel(UserModel.getInstance().getProjectsAsComboBoxModel());
				projectBox.setSelectedItem(UserModel.getInstance().getActiveProject());
				groupList.setModel(ModelFetchingController.getInstance().getGroupsModel(UserModel.getInstance().getActiveProject()));
				groupAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel(UserModel.getInstance().getActiveProject()));
				currentUserName = newUserName;
				storeModels();
			} else {
				projectBox.setModel(oldProjectBoxModel);
				groupList.setModel(oldGroupsListModel);
				groupAssignedList.setModel(oldGroupsAssignedListModel);
			}
		}
	}
	

	
	
}
