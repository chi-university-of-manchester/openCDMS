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

import javax.swing.*;

import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.wizard.*;

import org.psygrid.securitymanager.actions.SwitchListsAction;

import org.psygrid.securitymanager.renderers.*;

import org.psygrid.securitymanager.controller.ModelFetchingController;

import org.psygrid.securitymanager.model.UserModel;

import org.psygrid.securitymanager.utils.LayoutUtils;

import org.psygrid.securitymanager.utils.JListTransferHandler;

/**
 * Select projects dialog for project and role selection.
 * @author pwhelan
 *
 */
public class AddProjectsPanel extends JPanel implements WizardPanel
{
	private final static String STRINGS_PREFIX = "org.psygrid.securitymanager.ui.";
	
	private JList projectList;
	private JList projectAssignedList;
	
	//stores currentUserName at time of last refresh (so that we don't refresh every time!)
	private String currentUserName = null;
	
	private ListModel oldProjectAssignedListModel;
	private ListModel oldProjectListModel;
	
	public AddProjectsPanel()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		String assignProjects = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignprojectstouser");
		mainPanel.add(LayoutUtils.getInstance().createHeaderPanel(assignProjects), BorderLayout.NORTH);
		mainPanel.add(createSelectionPanel(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	private JPanel createSelectionPanel()
	{
		projectList = new JList();
		projectList.setTransferHandler(new JListTransferHandler());
		projectList.setCellRenderer(new AttributeListCellRenderer());
		projectAssignedList = new JList();
		projectAssignedList.setTransferHandler(new JListTransferHandler());
		projectAssignedList.setCellRenderer(new AttributeListCellRenderer());
		String addProjectName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"assignproject");
		String assignProjectName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"assignprojectheader");
		JButton addProjectButton = new CustomIconButton(new SwitchListsAction(addProjectName, projectList, projectAssignedList, true), assignProjectName);
		String removeProjectName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX+"unassignproject");
		String unassignProjectName = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX +"unassignprojectheader");
		JButton removeProjectButton = new CustomIconButton(new SwitchListsAction(removeProjectName, projectList, projectAssignedList, false), unassignProjectName);
		String projects = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "projects");
		String assignedProjects = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignedprojects");
		return LayoutUtils.getInstance().createSelectionPanel(projects, assignedProjects, addProjectButton, removeProjectButton, projectList, projectAssignedList);
	}
	
	private JPanel createHeaderPanel(String headerText)
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		if (headerText == null) {
			String assignProjects = PropertiesHelper.getPropertyHelper().getStringFor(STRINGS_PREFIX + "assignprojectstouser");
			headerPanel.add(new CustomLabel(assignProjects));
		//implies error has occurred so change font to red
		} else {
			headerPanel.add(new CustomLabel(headerText, Color.red));
		}
		return headerPanel;
	}

	//TODO need to work this out so that it's not overwritten by refreshPanel()
	public void refreshPanel(String error)
	{
		removeAll();	
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Color.red);
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		mainPanel.add(createHeaderPanel(error), BorderLayout.NORTH);
		mainPanel.add(createSelectionPanel(), BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
		revalidate();
		restoreModels();
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
		oldProjectAssignedListModel = projectAssignedList.getModel();
		oldProjectListModel = projectList.getModel();
	}

	public void restoreModels()
	{
		String newUserName = UserModel.getInstance().getFirstname() + UserModel.getInstance().getLastname();
		//if user name is new; fetch fresh models
		if (currentUserName == null || !newUserName.equals(currentUserName) || UserModel.getInstance().isProjectsDirty(oldProjectAssignedListModel))
		{
			projectAssignedList.setModel(UserModel.getInstance().getProjectsAsListModel());
			projectList.setModel(ModelFetchingController.getInstance().getProjectsListModel());
			currentUserName = newUserName;
			storeModels();
			//if same user name; restore the old models
		} else
		{
			projectAssignedList.setModel(oldProjectAssignedListModel);
			projectList.setModel(oldProjectListModel);
		}
	}
	
	
}