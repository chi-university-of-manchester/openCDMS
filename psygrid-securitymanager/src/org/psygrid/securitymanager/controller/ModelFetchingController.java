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

package org.psygrid.securitymanager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.securitymanager.model.ProjectGroupRoleModel;
import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.security.SecurityManager;
import org.psygrid.securitymanager.utils.DisplayTool;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

public class ModelFetchingController {

	private static ModelFetchingController modelFetchingController;
	private static final Log LOG = LogFactory.getLog(ModelFetchingController.class);
	
	private ProjectGroupRoleModel fromServerModels; 
	
	/**
	 * Fetches models from the AA for the currently logged in user.
	 *
	 */
    public ModelFetchingController() {
    	//only fetch from server once... assuming logged in users groups/roles/etc will not change!
    	initProjectRolesAndGroups();
    }
    
    public void reset() {
    	initProjectRolesAndGroups();
    }
    
    public static ModelFetchingController getInstance()
    {
    	 if (modelFetchingController == null)
    	 {
    		 modelFetchingController = new ModelFetchingController();
    	 }
    	 return modelFetchingController;
    }
    
    public void initProjectRolesAndGroups()
    {
    	fromServerModels = new ProjectGroupRoleModel();
    	
    	try
    	{
    		List<ProjectType> projects = new ArrayList<ProjectType>();;
    		if(SecurityManager.getInstance().isSuperUser()){
        		ProjectDescriptionType[] pdta = SecurityHelper.getAAQueryClient().getPort().getProjects();
        		for(ProjectDescriptionType a : pdta){
        			if(a.getProject()!=null){
        				projects.add(a.getProject());
        			}
        		}
			} else {
				projects = SecurityHelper.getAAQueryClient().getMyProjects();
    		}
    		
    		for (ProjectType p : projects)
        	{
        		RoleType[] roles = SecurityHelper.getAAQueryClient().getRolesInProject(p);
    			
    			boolean isSystemAdministrator = false;
    			boolean isSysAdminOrProjManager = false;
  
    			
    			//build in some error handling to handle problems fetching roles or groups from the 
    			//server (caused by half-saving studies etc); skip on to the next project
    			try {
        			if(!SecurityManager.getInstance().isSuperUser()){
        				
        				List<RoleType> myRoles = SecurityHelper.getAAQueryClient().getMyRolesInProject(p);
    	    			for (RoleType r: myRoles)
    	    			{
    	    				if (r.getName().equals("SystemAdministrator"))
    	    				{
    	    					isSystemAdministrator = true;
    	    					isSysAdminOrProjManager = true;
    	    				}
    	    				if (r.getName().equals("ProjectManager"))
    	    				{
    	    					isSysAdminOrProjManager = true;
    	    				}
    	    			}
        			}
        			
        			if (!isSysAdminOrProjManager && !SecurityManager.getInstance().isSuperUser()) {
        				continue;
        			} 

        			if (p.getName().equals("SYSTEM"))
        			{
        				if (isSystemAdministrator || SecurityManager.getInstance().isSuperUser())
        				{
        					fromServerModels.addToAssignedProjects(p, false);
        				}  
        			} else {
                		fromServerModels.addToAssignedProjects(p, false);
        			}
        			
        			List<GroupType> groups = new ArrayList<GroupType>();
        			
        			//TODO remove : this looks redundant at this point? can only be a sys admin, proj man or super user by now?
        			if (isSysAdminOrProjManager || SecurityManager.getInstance().isSuperUser())
        			{
        				GroupType[] groupTypeArray = SecurityHelper.getAAQueryClient().getGroupsInProject(p);
        				for (int i=0; i<groupTypeArray.length; i++) {
        					groups.add(groupTypeArray[i]);
        				}
        			} else {
            			groups = SecurityHelper.getAAQueryClient().getMyGroupsInProject(p);
        			}
        			
        			for(GroupType g: groups)
        			{
        				fromServerModels.addProjectGroup(p, g);
        			}
        			
        			for (RoleType r: roles)
        			{
            			if (r.getName().equals("SystemAdministrator")) {
            				if (isSystemAdministrator || SecurityManager.getInstance().isSuperUser())
            				{
            					fromServerModels.addProjectRole(p, r);
            				}
            			} else {
            				fromServerModels.addProjectRole(p, r);
            			}
        			}
        		//Log an error and skip on to the next project	
    			} catch (Exception ex) {
    				LOG.error("MFC : connect exception" + ex.getMessage());
    				continue;
    			}
    			
        	}
    	} catch (Exception ex)
    	{
    		LOG.error("MFC : connect exception" + ex.getMessage());
    	}
    	
    }
  
    public DefaultListModel getProjectsListModel()
    {
    	DefaultListModel projectListModel = new DefaultListModel();
  	
		projectListModel = fromServerModels.getProjectsListModel();
		DefaultListModel userProjects = UserModel.getInstance().getProjectsAsListModel();
	      	
      	ArrayList toRemove = new ArrayList();
      	for (int z=0; z<projectListModel.getSize(); z++)
      	{
  			ProjectType projectType = (ProjectType)projectListModel.getElementAt(z);
      		for (int y=0; y<userProjects.getSize(); y++)
      		{
      			ProjectType userProject = (ProjectType)userProjects.get(y);
      			if (projectType.getName().equals(userProject.getName()))
      			{
      				toRemove.add(projectType);
      			}
      		}
      	}
      	
      	for (int i=0; i<toRemove.size(); i++)
      	{
      		projectListModel.removeElement(toRemove.get(i));
      	}
      	return projectListModel;
  }

  
	  public DefaultListModel getGroupsModel(ProjectType project)
	  {
		  DefaultListModel groupModel = new DefaultListModel();
	
		  if (project == null) {
				return groupModel;
		  }
	  	
		  groupModel = fromServerModels.getProjectGroupListModel(project);
		  ArrayList toRemove = new ArrayList();
	  	
		  for(int i=0; i<groupModel.getSize(); i++) {
	  		GroupType groupType = (GroupType)groupModel.getElementAt(i);
	  		String groupTypeFullName = DisplayTool.getFullGroupName(groupType);
	
	  		DefaultListModel userGroups = UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel(project);
	  		for (int j=0; j<userGroups.getSize(); j++)
	  		{
	  			GroupType userGroupType = (GroupType)userGroups.getElementAt(j);
	  			String userGroupTypeFullName = DisplayTool.getFullGroupName(userGroupType);
	  			if (userGroupTypeFullName.equals(groupTypeFullName))
	  			{
	  				toRemove.add(groupType);
	  			}
	  		}
	  	}
	  	
	  	for (Object z: toRemove)
	  	{
	  		groupModel.removeElement(z);
	  	}
	  	
	  	return groupModel;
	}
  
  	public DefaultListModel getRolesModelForProject(ProjectType project) {
  		DefaultListModel roleModel = new DefaultListModel();
  		
  		if (project == null)
  		{
  			return roleModel;
  		}
  		
  		roleModel = fromServerModels.getProjectRoleListModel(project);
  		ArrayList toRemove = new ArrayList();
  		for(int i=0; i<roleModel.getSize(); i++)
  		{
  			RoleType groupType = (RoleType)roleModel.getElementAt(i);

  			DefaultListModel userGroups = UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(project);

  			for (int j=0; j<userGroups.getSize(); j++)
  			{
  				RoleType userGroupType = (RoleType)userGroups.getElementAt(j);
  				if (groupType.getName().equals(userGroupType.getName()))
  				{
  					toRemove.add(groupType);
  				}
  			}
  		}
  	
  		for (Object z: toRemove)
  		{
  			roleModel.removeElement(z);
  		}
  		return roleModel;
  	}
    
}
	