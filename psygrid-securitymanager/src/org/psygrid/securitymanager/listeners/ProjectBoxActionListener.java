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

package org.psygrid.securitymanager.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.psygrid.www.xml.security.core.types.ProjectType;

import org.psygrid.securitymanager.controller.ModelFetchingController;

import org.psygrid.securitymanager.model.UserModel;

public class ProjectBoxActionListener implements ActionListener
{
	public final static int ROLE_TYPE = 1;
	public final static int GROUP_TYPE = 2;
	
	private int listeningType;
	private JComboBox projectBox;
	
	private JList dependentList;
	private JList dependentAssignedList;
	
	private ProjectType lastSelectedProject;
	
	public ProjectBoxActionListener(int listeningType, JComboBox projectBox, JList dependentList, JList dependentAssignedList)
	{
		this.listeningType = listeningType;
		this.projectBox = projectBox;
		this.dependentList = dependentList;
		this.dependentAssignedList = dependentAssignedList;
	}
	
	public void actionPerformed(ActionEvent aet)
	{
		if (projectBox.getSelectedItem() != lastSelectedProject)
		{
			if (listeningType == ROLE_TYPE)
			{
				dependentList.setModel(ModelFetchingController.getInstance().getRolesModelForProject((ProjectType)projectBox.getSelectedItem()));
				dependentAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel((ProjectType)projectBox.getSelectedItem()));
			}
			else if (listeningType == GROUP_TYPE)
			{
				dependentList.setModel(ModelFetchingController.getInstance().getGroupsModel((ProjectType)projectBox.getSelectedItem()));
				dependentAssignedList.setModel(UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel((ProjectType)projectBox.getSelectedItem()));
			}
			//set the currently active project
			UserModel.getInstance().setActiveProject((ProjectType)projectBox.getSelectedItem());
			lastSelectedProject = (ProjectType)projectBox.getSelectedItem();
		}
	}
	
	public ProjectType getLastSelectedProject()
	{
		return lastSelectedProject;
	}
	
}