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
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.psygrid.securitymanager.controller.AAController;
import org.psygrid.securitymanager.controller.ModelFetchingController;

import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.utils.LDAPPropertiesHelper;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

/**
 * User Model keeps track of user specified in the wizards
 * and their to be assigned/deleted/modified projects, groups and roles.
 * 
 * @author pwhelan
 */
public class UserModel 
{
	private static final Log LOG = LogFactory.getLog(UserModel.class);
	
	private String firstname = "";
	private String lastname = "";
	private String emailAddress = "";
	private String userID = "";
	private short[] password;
	private String mobileNumber = "";
	
	private ProjectGroupRoleModel projectGroupRoleModel = new ProjectGroupRoleModel();
	
	// Represents active model in the project combo box of the gui wizards
	private ProjectType activeProject = null;
	
	private static UserModel userModel;
	
	
	public static UserModel getInstance()
	{
		if (userModel == null)
		{
			userModel = new UserModel();
		}
		return userModel;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress, boolean updateModels)
	{
		String oldEmailAddress = this.emailAddress;
		this.emailAddress = emailAddress;

		if (updateModels)
		{
			if (!oldEmailAddress.equals(emailAddress))
			{
				updateProjectGroupRoleModelForUser();
			}
		}
	}
	
	/**
	 * Sets the email address; if it differs from the current email address,
	 * reset the project models
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {

		String oldEmailAddress = this.emailAddress;
		this.emailAddress = emailAddress;

		if (!oldEmailAddress.equals(emailAddress))
		{
			updateProjectGroupRoleModelForUser();
		}
	}
	public String getFirstname() {
		return firstname;
	}
	
	/**
	 * Sets the firstname; if it differs from the current firstname;
	 * reset the project models
	 * @param firstname
	 */
	public void setFirstname(String firstname) {
		String oldFirstName = this.firstname;
		this.firstname = firstname.trim();
		
		if (!oldFirstName.equals(firstname) )
		{
			updateProjectGroupRoleModelForUser();
			password = null;
			mobileNumber = "";
		}

	}
	
	public String getLastname() {
		return lastname;
	}
	
	public String getUserID()
	{
		return userID;
	}
	
	public void setPassword(short[] password)
	{
		this.password = password;
	}
	
	public short[] getPassword()
	{
		return password;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	/**
	 * Sets the lastname; if it differs from the current lastname,
	 * reset the project models
	 * 
	 * @param lastname
	 */
	public void setLastname(String lastname) {
		String oldLastName = this.lastname;
		this.lastname = lastname.trim();
		
		if (!oldLastName.equals(lastname) )
		{
			updateProjectGroupRoleModelForUser();
			password = null;
			mobileNumber = "";
		}
		
	}
	
	/*
	 * Sets the userID; if it differs from the current lastname,
	 * reset the project models
	 * 
	 * @param userID
	 */
	public void setUserID(String userID, boolean noUpdateNames)
	{
		if (noUpdateNames)
		{
			this.userID = userID;
		}
	}

	
	/*
	 * Sets the userID; if it differs from the current lastname,
	 * reset the project models
	 * 
	 * @param userID
	 */
	public void setUserID(String userID)
	{
		try
		{
			if (AAController.getInstance().getCommonNameFromUserID(userID)!=null
				&& !AAController.getInstance().getCommonNameFromUserID(userID).equals(""))
				{
					setNames(AAController.getInstance().getCommonNameFromUserID(userID));
				}
			else
			{
				setFirstname("");
				setLastname("");
			}
		} catch (Exception ex)
		{
			
			setFirstname("");
			setLastname("");
		}

		this.userID = userID;
	}
	
	public DefaultComboBoxModel getProjectsAsComboBoxModel()
	{
		DefaultComboBoxModel projectBoxModel = new DefaultComboBoxModel();
		
		for (int i=0; i<projectGroupRoleModel.getProjects().size(); i++)
		{
			projectBoxModel.addElement(projectGroupRoleModel.getProjects().get(i));
		}
		
		return projectBoxModel;
	}
	
	public DefaultListModel getProjectsAsListModel()
	{
		DefaultListModel projectListModel = new DefaultListModel();
		
		for (int i=0; i<projectGroupRoleModel.getProjects().size(); i++)
		{
			projectListModel.addElement(projectGroupRoleModel.getProjects().get(i));
		}
	
		return projectListModel;
	}
	
	
	public ArrayList getProjects()
	{
		return projectGroupRoleModel.getProjects();
	}
	
	public void addProject(ProjectType project, boolean updateAttrs)
	{
		projectGroupRoleModel.addToAssignedProjects(project, updateAttrs);
	}
	
	public void addProject(ProjectType project)
	{
		projectGroupRoleModel.addToAssignedProjects(project);
	}
	
	public void removeProject(ProjectType project)
	{
		projectGroupRoleModel.removeAssignedProjects(project);
		//when we remove a project; also remove the associated groups and roles

	}
	
	public ProjectGroupRoleModel getProjectGroupRoleModel()
	{
		return projectGroupRoleModel;
	}
	
	public void addRole(ProjectType project, RoleType role)
	{
		projectGroupRoleModel.addProjectRole(project, role);
	}
	
	public void removeRole(ProjectType project, RoleType role)
	{
		projectGroupRoleModel.removeProjectRole(project, role);
	}
	
	public void addGroup(ProjectType project, GroupType group)
	{
		projectGroupRoleModel.addProjectGroup(project, group);
	}

	public void removeGroup(ProjectType project, GroupType group)
	{
		projectGroupRoleModel.removeProjectGroup(project, group);
	}
	
	/**
	 * Clear everything for new user
	 *
	 */
	public void reset()
	{
		firstname = "";
		lastname = "";
		emailAddress = "";
		userID = "";
		password = null;
		mobileNumber = "";
		projectGroupRoleModel.reset();
	}
	
	/**
	 * Used to update the models with groups, projects and 
	 * roles currently assigned to the user in the attribute 
	 * authority.
	 */
	public void updateProjectGroupRoleModelForUser()
	{
		//ensure we clear the currently stored projects and groups first
		projectGroupRoleModel.reset();
		
		try
		{
			AttributeType[] currentats = null;

			UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+firstname+ " " + lastname + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
			currentats = upt.getAttribute();			
			if (currentats == null) {
				return;
			}			

			List <ProjectType> userProjects = new ArrayList<ProjectType>();
		
			for (AttributeType at: currentats) {
				
				boolean isSysAdmin = false;
				List<RoleType> myRoles = new ArrayList<RoleType>();		
				if(org.psygrid.securitymanager.security.SecurityManager.getInstance().isSuperUser()){
					RoleType[] rta = SecurityHelper.getAAQueryClient().getPort().getRolesInProject(at.getProject());	
					for(RoleType r : rta){
						myRoles.add(r);
					}
				} else {
					myRoles = SecurityHelper.getAAQueryClient().getMyRolesInProject(at.getProject());
				}
				for (RoleType myRole: myRoles) {
					if (myRole.getName().equals("SystemAdministrator")) {
						isSysAdmin = true;
					}
				}
				
				//SYSTEM project should only be shown if user is sysadmin
				if (at.getProject().getName().equals("SYSTEM") && isSysAdmin) {
					userProjects.add(at.getProject());
					addProject(at.getProject());
				} else {
					userProjects.add(at.getProject());
					//check permissions on model fetched projects
					if (ModelFetchingController.getInstance().getProjectsListModel().contains(at.getProject())) {
						addProject(at.getProject());
					}					
				}
							
				RoleType[] roles = at.getRole();
				
				if (roles != null)
				{
					for (RoleType role: roles) {
						if (role.getName().equals("SystemAdministrator")) {
							if (isSysAdmin) {
								addRole(at.getProject(), role);
							}
						} else {
							addRole(at.getProject(), role);
						}
					}
				}
				
				GroupType[] groups = at.getGroup();

				if (groups != null) {
					for (GroupType group: groups) {
						addGroup(at.getProject(), group);
					}
				}
			}
		} catch (Exception pge) {
			LOG.error("User Model : Exception occurred updating projects, groups and roles", pge);
		}
	}
	
	/**
	 * Is the user model passed in the same as models currently 
	 * assigned in the AA?
	 * @param oldProjectBoxModel
	 * @return
	 */
	public boolean isProjectsDirty(ComboBoxModel oldProjectBoxModel)
	{
		boolean isProjectsDirty = false;
		
		if (oldProjectBoxModel == null && projectGroupRoleModel.getProjects().size() > 0)
		{
			isProjectsDirty = true;
		} else {
			 if (projectGroupRoleModel.getProjects().size() != oldProjectBoxModel.getSize())
			 {
				 isProjectsDirty = true;
			 } else {
				 for (int i =0; i< oldProjectBoxModel.getSize(); i++)
				 {
					 if (!projectGroupRoleModel.getProjects().contains(oldProjectBoxModel.getElementAt(i))) {
						 isProjectsDirty = true;
					 }
				 }
			 }
		}
		
		return isProjectsDirty;
	}

	/**
	 * Is the user model passed in the same as models currently 
	 * assigned in the AA?
	 * @param oldProjectBoxModel
	 * @return
	 */
	public boolean isProjectsDirty(ListModel oldProjectBoxModel)
	{
		boolean isProjectsDirty = false;
		
		if (oldProjectBoxModel == null && projectGroupRoleModel.getProjects().size() > 0)
		{
			isProjectsDirty = true;
		} else {
			 if (projectGroupRoleModel.getProjects().size() != oldProjectBoxModel.getSize())
			 {
				 isProjectsDirty = true;
			 } else {
				 for (int i =0; i< oldProjectBoxModel.getSize(); i++)
				 {
					 if (!projectGroupRoleModel.getProjects().contains(oldProjectBoxModel.getElementAt(i))) {
						 isProjectsDirty = true;
					 }
				 }
			 }
		}
		
		return isProjectsDirty;
	}

	
	public ProjectType getActiveProject()
	{
		return activeProject;
	}
	
	public void setActiveProject(ProjectType activeProject)
	{
		this.activeProject = activeProject;
	}
	
	public void setNames(String fullName)
	{
		setFirstname(fullName.substring(0, fullName.indexOf(" ")));
		setLastname(fullName.substring(fullName.indexOf(" ")+1, fullName.length()));
	}
	
}