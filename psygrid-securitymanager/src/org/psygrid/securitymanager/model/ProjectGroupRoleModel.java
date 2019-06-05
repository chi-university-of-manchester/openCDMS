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

package org.psygrid.securitymanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.utils.LDAPPropertiesHelper;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

public class ProjectGroupRoleModel
{
	private static final Log LOG = LogFactory.getLog(ProjectGroupRoleModel.class);
	
	private ArrayList<ProjectType> projects = new ArrayList<ProjectType>();
	private HashMap projectGroupMap = new HashMap();
	private HashMap projectRoleMap = new HashMap();
	
	public ArrayList getProjects()
	{
		return projects;
	}
	
	public void addToAssignedProjects(ProjectType project, boolean updateRolesAndGroups)
	{
		projects.add(project);

		if (updateRolesAndGroups)
		{
			try
			{
				UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
				AttributeType[] currentats = upt.getAttribute();
				if (currentats == null)
				{
					return;
				}

				List <ProjectType> userProjects = new ArrayList<ProjectType>();
				
				for (AttributeType at: currentats)
				{
					if (at.getProject().getName().equals(project.getName()))
					{
						RoleType[] roles = at.getRole();
					
						if (roles != null)
						{
							for (RoleType role: roles)
							{
								addProjectRole(project, role);
							}
						}
				
						GroupType[] groups = at.getGroup();

						if (groups != null)
						{
							for (GroupType group: groups)
							{
								addProjectGroup(project, group);
							}
						}
					}
				}
			} catch (Exception ex)
			{
				LOG.info("ProjectGroupRoleModel: exception adding project " + ex.getMessage() + " can mean user has not assigned this project ");
			}
		}
	}
	
	public void addToAssignedProjects(ProjectType project)
	{
		projects.add(project);
	}
	
	public void removeAssignedProjects(ProjectType project)
	{
		//clear up group and role maps as well
		projectGroupMap.remove(project);
		projectRoleMap.remove(project);
		projects.remove(project);
	}
	
	public void addProjectGroup(ProjectType project, GroupType group)
	{
		ArrayList groups = new ArrayList();
		
		if (projectGroupMap.containsKey(project))
		{
			groups = (ArrayList)projectGroupMap.get(project);
		}
		
		groups.add(group);
		projectGroupMap.put(project,groups);
	}

	public void removeProjectGroup(ProjectType project, GroupType group)
	{
		ArrayList groups = (ArrayList)projectGroupMap.get(project);
		groups.remove(group);
		projectGroupMap.put(project,groups);
	}
	
	public void addProjectRole(ProjectType project, RoleType role)
	{
		ArrayList roles = new ArrayList();
		
		if (projectRoleMap.containsKey(project))
		{
			roles = (ArrayList)projectRoleMap.get(project);
		}
		
		roles.add(role);
		projectRoleMap.put(project,roles);
	}
	
	public void removeProjectRole(ProjectType project, RoleType role)
	{
		ArrayList roles = (ArrayList) projectRoleMap.get(project);
		roles.remove(role);
		projectRoleMap.put(project, roles);
	}

	public List<RoleType> getRolesForProject(ProjectType project)
	{
		return (ArrayList)projectRoleMap.get(project);
	}
	
	public List<GroupType> getGroupsForProject(ProjectType project)
	{
		return (ArrayList)projectGroupMap.get(project);
	}
	
	public DefaultListModel getProjectGroupListModel(ProjectType project)
	{
		DefaultListModel groupListModel = new DefaultListModel();
		ArrayList groupList = new ArrayList();
		
		if (projectGroupMap.get(project) != null)
		{
			groupList = (ArrayList)projectGroupMap.get(project);
		}
		
		for (int i=0; i<groupList.size(); i++)
		{
			groupListModel.addElement(groupList.get(i));
		}
		
		return groupListModel;
	}
	
	public DefaultListModel getProjectRoleListModel(ProjectType project)
	{
		DefaultListModel roleListModel = new DefaultListModel();
		ArrayList roleList = new ArrayList();
		
		if (projectRoleMap.get(project) != null)
		{
			roleList = (ArrayList)projectRoleMap.get(project);
		}
		
		for (int i=0; i<roleList.size(); i++)
		{
			roleListModel.addElement(roleList.get(i));
		}
		
		return roleListModel;
	}
	
	public DefaultListModel getProjectsListModel()
	{
		DefaultListModel projectListModel = new DefaultListModel();
		
		for(ProjectType p: projects)
		{
			projectListModel.addElement(p);
		}
		
		return projectListModel;
	}
	
	public void reset()
	{
		projects.clear();
		projectGroupMap.clear();
		projectRoleMap.clear();
	}

}
