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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.securitymanager.model.UserModel;
import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.utils.LDAPPropertiesHelper;
import org.psygrid.securitymanager.utils.UserType;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;

import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;


import org.psygrid.security.attributeauthority.types.ResetPasswordRequestType;

import java.rmi.RemoteException;

/**
 * Class that manages adding, deleting and modifying users in the AA.
 * @author pwhelan
 */
public class AAController
{
	/** singletone instance */
	private static AAController aaController;
		
	private static final Log LOG = LogFactory.getLog(AAController.class);
	
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	
	/** 
	 * Fetch the singleton
	 * @return
	 */
	public static AAController getInstance()
	{
		if (aaController == null) {
			aaController = new AAController();
		}

		return aaController;
	}
	
	
    /***
     * Add the user to the attribute authority
     * 
     */
     public boolean addToAA() throws RemoteException, NotAuthorisedFaultMessage,
     								ProcessingFaultMessage, InputFaultMessage
     {
    	 boolean addUser = false;
    	 try{
			AAManagementClient mc = SecurityHelper.getAAManagementClient();
			ArrayList userProjects = UserModel.getInstance().getProjects();
			int numUserProjects = userProjects.size();
			AttributeType[] ats = new AttributeType[userProjects.size()];
			
			for (int i=0; i<numUserProjects; i++)
			{
				ProjectType project = (ProjectType)userProjects.get(i);
				GroupType[] groups = null;
				RoleType[] roles = null;
		   
				//get the groups for this project
				DefaultListModel groupList = UserModel.getInstance().getProjectGroupRoleModel().getProjectGroupListModel(project);
				if (groupList != null)
				{
					groups = new GroupType[groupList.getSize()];
							   
					for (int j=0; j<groupList.getSize(); j++)
					{
						groups[j] = (GroupType)groupList.get(j);
					}
				}
	   
				DefaultListModel roleList = UserModel.getInstance().getProjectGroupRoleModel().getProjectRoleListModel(project);
	   
			if (roleList != null)
			{
				roles = new RoleType[roleList.getSize()];
		   
				for (int r=0; r<roleList.getSize(); r++)
				{
					roles[r] = (RoleType)roleList.get(r);
				}
			}
	   
			ats[i] = new AttributeType(project, groups, roles);
		}
			
		short[] password = UserModel.getInstance().getPassword();
		char[] passwordChars = new char[password.length];
		
		for (int i=0; i<password.length; i++) {
			passwordChars[i] = (char)password[i];
		}
		
//		UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(UserModel.getInstance().getFirstname(), UserModel.getInstance().getLastname(), UserModel.getInstance().getEmailAddress(), null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + ", OU=users, O=psygrid, C=uk", null), ats);

		UserPrivilegesType usrpt;
		
		if (UserModel.getInstance().getMobileNumber() == null || (UserModel.getInstance().getMobileNumber().equals(""))){
			usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(
					UserModel.getInstance().getFirstname(), 
					UserModel.getInstance().getLastname(), 
					UserModel.getInstance().getEmailAddress(), 
					UserModel.getInstance().getFirstname()+UserModel.getInstance().getLastname(),
					"CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), 
					new String(passwordChars), null), ats);
		} else {
			usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(
					UserModel.getInstance().getFirstname(), 
					UserModel.getInstance().getLastname(), 
					UserModel.getInstance().getEmailAddress(), 
					UserModel.getInstance().getFirstname()+UserModel.getInstance().getLastname(),
					"CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), 
					new String(passwordChars), UserModel.getInstance().getMobileNumber()), ats);
		}
		
		addUser = mc.getPort().addUser(new UserPrivilegesType[]{usrpt});
		fireActionEvent();
		
     	} catch (NullPointerException except) {
    		 LOG.error("AA Cotnroller exception in adding user " + except.getMessage());
	   	}
	   	return addUser;
     }
     
     /**
      * Check first if user exists in the LDAP directory and then if
      * the user exists in the AA.
      * @return exits true if the user exists in the LDAP and AA; false if not.
      */
     public boolean userExists() throws RemoteException, 
     									NotAuthorisedFaultMessage,
     									ProcessingFaultMessage, 
     									InputFaultMessage
     {
    	 boolean exists = false;
    	 
    	 try
    	 {
    		 exists = SecurityHelper.getAAManagementClient().getPort().userExists(UserModel.getInstance().getFirstname(), UserModel.getInstance().getLastname());
    	 } catch (NullPointerException ex)
    	 {
    		 LOG.info("Exception on attribute authority." + ex.getMessage() + " CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
    	 }

    	 return exists;
     }
     
     public boolean userAccountIsDormant() throws RemoteException,
			NotAuthorisedFaultMessage, ProcessingFaultMessage,
			InputFaultMessage, ConnectException {
		boolean exists = false;

		try {
			exists = SecurityHelper.getAAQueryClient().getPort()
					.isAccountDormant("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
		} catch (NullPointerException ex) {
			LOG.info("Exception on attribute authority." + ex.getMessage()
					+ " CN=" + UserModel.getInstance().getFirstname() + " "
					+ UserModel.getInstance().getLastname()
					+ LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
		}

		return exists;
	}
	
     public boolean modifyUser() throws RemoteException, 
     									NotAuthorisedFaultMessage,
     									ProcessingFaultMessage, 
     									InputFaultMessage
     {
    	 boolean modifiedUser = false;

    	 if (updateUserEmailAndMobile())
    	 {
        	 if (updateRoles())
        	 {
        		 if (updateGroups())
        		 {
        			 if (updateProjects())
        			 {
        				 modifiedUser = true;
        			 }
        		 }
        	 }
    	 }
    	 
    	 if (modifiedUser)
    	 {
    		 fireActionEvent();
    	 }
    	 
    	 return modifiedUser;
     }
     
     private boolean updateUserEmailAndMobile() throws RemoteException, 
     										  NotAuthorisedFaultMessage,
     										  ProcessingFaultMessage, 
     										  InputFaultMessage
     {
		boolean success = false;

		try {
			UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
			upt.getUser().setEmailAddress(
					UserModel.getInstance().getEmailAddress());
			upt.getUser().setMobileNumber(UserModel.getInstance().getMobileNumber());
			SecurityHelper.getAAManagementClient().getPort().updateUser(new UserPrivilegesType[]{upt});
		 	success = true;
		} catch (NullPointerException nex) {
			LOG.error("Null Pointer Exception updating user email and mobile " + nex);
		}
			
		return success;
     }
     
     private boolean updateProjects() throws RemoteException,
			NotAuthorisedFaultMessage, ProcessingFaultMessage,
			InputFaultMessage {
		boolean success = false;
		// delete group in projects from user

		try {
			UserPrivilegesType upt = SecurityHelper.getAAManagementClient()
					.getPort().getUser(
							"CN="
									+ UserModel.getInstance().getFirstname()
									+ " "
									+ UserModel.getInstance().getLastname()
									+ LDAPPropertiesHelper.getPropertyHelper()
											.getUserBaseDN());
			AttributeType[] currentats = upt.getAttribute();
			List<ProjectType> currentlyAssignedProjects = new ArrayList<ProjectType>();

			if (currentats != null) {
				for (AttributeType at : currentats) {
					currentlyAssignedProjects.add(at.getProject());
				}
			}

			List<ProjectType> newlyAssignedProjects = UserModel.getInstance()
					.getProjects();

			if (currentlyAssignedProjects != null
					&& newlyAssignedProjects != null) {
				for (ProjectType p : currentlyAssignedProjects) {
					if (!newlyAssignedProjects.contains(p)) {
						AttributeType[] ats = new AttributeType[1];
						ats[0] = new AttributeType(p, null, null);
						UserPrivilegesType usrpt = new UserPrivilegesType(
								new org.psygrid.www.xml.security.core.types.UserType(
										null, null, null, null, "CN="
												+ UserModel.getInstance()
														.getFirstname()
												+ " "
												+ UserModel.getInstance()
														.getLastname()
												+ LDAPPropertiesHelper
														.getPropertyHelper()
														.getUserBaseDN(), null,
										null), ats);
						// delete it
						SecurityHelper.getAAManagementClient().getPort()
								.deleteProjectFromUser(
										new UserPrivilegesType[] { usrpt });
					}
				}
			}


			for (ProjectType p : newlyAssignedProjects) {
				if (currentlyAssignedProjects != null) {
					if (!currentlyAssignedProjects.contains(p)) {
						newlyAssignedProjects.remove(p);
					}
				}
			}
			AttributeType[] ats = new AttributeType[newlyAssignedProjects.size()];
			for (int i=0;i<ats.length;i++) {
				ats[i]=new AttributeType(newlyAssignedProjects.get(i), null, null);				
			}
			UserPrivilegesType usrpt = new UserPrivilegesType(
					new org.psygrid.www.xml.security.core.types.UserType(
							null, null, null, null, "CN="
									+ UserModel.getInstance()
											.getFirstname()
									+ " "
									+ UserModel.getInstance()
											.getLastname()
									+ LDAPPropertiesHelper
											.getPropertyHelper()
											.getUserBaseDN(), null,
							null), ats);
			SecurityHelper.getAAManagementClient().getPort().addProjectToUser(
					new UserPrivilegesType[] { usrpt });
			success = true;
		} catch (NullPointerException ex) {
			LOG.error("AAController : exception thrown " + ex.getMessage());
		}
		return success;
	}
     
     
     private boolean updateGroups() throws RemoteException, 
     									   NotAuthorisedFaultMessage,
     									   ProcessingFaultMessage, 
     									   InputFaultMessage	
     {
    	 boolean success = false;
		 try
    	 {
			 List <ProjectType> newlyAssignedProjects = UserModel.getInstance().getProjects();

			 for (ProjectType p : newlyAssignedProjects)
			 {
				 List <GroupType> newlyAssignedGroups = UserModel.getInstance().getProjectGroupRoleModel().getGroupsForProject(p);
				 UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
				 AttributeType[] currentats = upt.getAttribute();
				 List <GroupType> currentlyAssignedGroups = new ArrayList<GroupType>();
				 if(currentats!=null){
					 for (AttributeType at: currentats)
					 {
						 if (at.getProject().equals(p))
						 {
							 GroupType[] projectGroups = at.getGroup();
							 if (projectGroups != null)
							 {
								 for (GroupType r: projectGroups)
								 {
									 currentlyAssignedGroups.add(r);
								 }
							 }
						 }
					 }
				 }
				 if (currentlyAssignedGroups != null && newlyAssignedGroups != null)
				 {
					 for (GroupType g : currentlyAssignedGroups) {
						 if (!newlyAssignedGroups.contains(g)) {
							 AttributeType[] ats = new AttributeType[1];
							 ats[0] = new AttributeType(p, new GroupType[]{g}, null);
							 UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);
							 //delete it
							 SecurityHelper.getAAManagementClient().getPort().deleteGroupInProjectFromUser(new UserPrivilegesType[]{usrpt});
						 }
					 }
				 }
				 			
				 if (newlyAssignedGroups != null)
				 {
					 if (currentlyAssignedGroups!=null){
						 for (GroupType g: newlyAssignedGroups)
						 {
							 if(!currentlyAssignedGroups.contains(g)) {
								currentlyAssignedGroups.remove(g);
							 }
						 }
					 } 
					 GroupType[] groups = newlyAssignedGroups.toArray(new GroupType[newlyAssignedGroups.size()]);
					 AttributeType[] ats = new AttributeType[1];
					 ats[0] = new AttributeType(p, groups, null);
					 UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);
					 SecurityHelper.getAAManagementClient().getPort().addGroupInProjectToUser(new UserPrivilegesType[]{usrpt});										 
				 }
			 }
			 success = true;
    	 } catch (NullPointerException ex)
    	 {
    		 LOG.error("AAController : Error updating groups" + ex.getMessage());
    	 }
    	 
    	 return success;
     }
     
     private boolean updateRoles() throws RemoteException, 
     									  NotAuthorisedFaultMessage,
     									  ProcessingFaultMessage, 
     									  InputFaultMessage
     {
    	 boolean success = false;
		 try
    	 {
			 List <ProjectType> newlyAssignedProjects = UserModel.getInstance().getProjects();

			 for (ProjectType p : newlyAssignedProjects)
			 {
				 List <RoleType> newlyAssignedRoles = UserModel.getInstance().getProjectGroupRoleModel().getRolesForProject(p);
				 UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
				 AttributeType[] currentats = upt.getAttribute();
				 List <RoleType> currentlyAssignedRoles = new ArrayList<RoleType>();
				 
				 if(currentats!=null){
					 for (AttributeType at: currentats)
					 {
						 if (at.getProject().equals(p))
						 {
							 RoleType[] projectRoles = at.getRole();
							 if (projectRoles != null)
							 {
								 for (RoleType r: projectRoles)
								 {
									 currentlyAssignedRoles.add(r);
								 }
							 }
						 }
					 }
				 }
				 
				if (currentlyAssignedRoles != null)
				{
					for (RoleType r: currentlyAssignedRoles) {
						if (!newlyAssignedRoles.contains(r)) {
							AttributeType[] ats = new AttributeType[1];
					        ats[0] = new AttributeType(p, null, new RoleType[]{r});
					        UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);
					        //if not sysadmin, then role will not be visible so don't delete it
					        if (r.getName().equals("SystemAdministrator"))
					        {
					        	if (isSysAdmin(p))
					        	{
						            SecurityHelper.getAAManagementClient().getPort().deleteRoleInProjectFromUser(new UserPrivilegesType[]{usrpt});
					        	}
					        } else {
					            SecurityHelper.getAAManagementClient().getPort().deleteRoleInProjectFromUser(new UserPrivilegesType[]{usrpt});
					        }
						}
					}
				 }
					
				 if (newlyAssignedRoles != null)
				 {
					 for (RoleType r: newlyAssignedRoles) {
						 if(!currentlyAssignedRoles.contains(r)) {
				        	AttributeType[] ats = new AttributeType[1];
				        	ats[0] = new AttributeType(p, null, new RoleType[]{r});
				        	UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);
			            	SecurityHelper.getAAManagementClient().getPort().addRoleInProjectToUser(new UserPrivilegesType[]{usrpt});
						}
					}
			 	} else {
			 		AttributeType[] ats = new AttributeType[1];
			 		ats[0] = new AttributeType(p, null, newlyAssignedRoles.toArray(new RoleType[newlyAssignedRoles.size()]));
			 		UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);
			 		SecurityHelper.getAAManagementClient().getPort().addRoleInProjectToUser(new UserPrivilegesType[]{usrpt});
			 	}
		 	}
			success = true;
    	 } catch (NullPointerException ex)
    	 {
    		 LOG.error("Error occured during update roles",ex);
    	 }
    	 return success;
     }
     
     public ArrayList<String> verifyMultiUsers() throws RemoteException
     {
    	 ArrayList<String> mUsers = new ArrayList<String>();
    	 String[] multiUsers = SecurityHelper.getAAManagementClient().getPort().multiUserExists(UserModel.getInstance().getFirstname(), UserModel.getInstance().getLastname());
    	 
    	 for (String user: multiUsers) {
    		 mUsers.add(user);
    	 }
    	 
    	 return mUsers;
     }
     
     /**
      * Deletes from the AA.  If user has no more project, also deletes from LDAP
      * @return
      */
     public boolean deleteFromAA() throws RemoteException, 
     									  NotAuthorisedFaultMessage,
     									  ProcessingFaultMessage, 
     									  InputFaultMessage
     {
    	 boolean success = false;
    	
    	 try
    	 {
 			UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
			AttributeType[] currentats = upt.getAttribute();
			
			List <ProjectType> currentlyAssignedProjects = new ArrayList<ProjectType>();
			
			if (currentats != null) {
				for (AttributeType at: currentats) {
					currentlyAssignedProjects.add(at.getProject());
				}
				
				List <ProjectType> newlyAssignedProjects = UserModel.getInstance().getProjects();
				List<AttributeType> lat = new ArrayList<AttributeType>();
				for (ProjectType p: newlyAssignedProjects) {
					if (currentlyAssignedProjects.contains(p)) {
				        lat.add(new AttributeType(p, null, null));
				     }
				}
				if(lat.size()>0){
			        SecurityHelper.getAAManagementClient().getPort().deleteProjectFromUser(
			        		new UserPrivilegesType[]{
			        				new UserPrivilegesType(
			        						new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), 
			        						lat.toArray(new AttributeType[lat.size()])
			        						)
			        				}
			        		);
				}		
				success = true;
			}
    	 } catch (NullPointerException ex) {
    		 LOG.error("Exception occurred removing user project from AA." + " CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(),ex);
    		 success=false;
    	 }

    	 if (success) {
    		 fireActionEvent();
    	 }
    	 
    	 return success;
     }
     
     public void addActionListener(ActionListener al)
     {
    	 actionListeners.add(al);
     }
     
     public void removeActionListener(ActionListener al)
     {
    	 actionListeners.remove(al);
     }
     
     public void fireActionEvent()
     {
    	 for(ActionListener al: actionListeners)
    	 {
    		 al.actionPerformed(new ActionEvent(this, 1, "Update Tree"));
    	 }
     }
     
     public boolean deleteGroupInProjectFromUser(ProjectType p, GroupType g, UserType u)
     {
		AttributeType[] ats = new AttributeType[1];
		ats[0] = new AttributeType(p, new GroupType[]{g}, null);
        UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+u.getFirstName() + " " + u.getLastName() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);

        try
        {
	        return SecurityHelper.getAAManagementClient().getPort().deleteGroupInProjectFromUser(new UserPrivilegesType[]{usrpt});
        } catch (Exception ex)
        {
        	return false;
        }
     }
     
     public boolean deleteRoleInProjectFromUser(ProjectType p, RoleType r, UserType u)
     {
		AttributeType[] ats = new AttributeType[1];
		ats[0] = new AttributeType(p, null, new RoleType[]{r});
        UserPrivilegesType usrpt = new UserPrivilegesType(new org.psygrid.www.xml.security.core.types.UserType(null, null, null, null, "CN="+u.getFirstName() + " " + u.getLastName() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN(), null, null), ats);

        try
        {
	        return SecurityHelper.getAAManagementClient().getPort().deleteRoleInProjectFromUser(new UserPrivilegesType[]{usrpt});
        } catch (Exception ex)
        {
        	return false;
        }
     }
     
 	public String getUserIDFromCommonName(String commonName) throws 
 													RemoteException,
 													NotAuthorisedFaultMessage,
 													ProcessingFaultMessage,
 													InputFaultMessage
	{
 		String uid = "";
 		try
 		{
 	 		uid = SecurityHelper.getAAManagementClient().getPort().getUIDFromCN(commonName);
 		} catch(NotAuthorisedFaultMessage nafm) {
 			LOG.warn("AAController - not authorised to get UID from CN" + commonName);
 		} catch (InputFaultMessage ifm) {
 			LOG.warn("AAController - input fault getting UID from CN" + commonName);
 		} catch (ProcessingFaultMessage pfm) {
 			LOG.warn("AAController - processing fault getting UID from CN" + commonName);
 		} catch (RemoteException rex) {
 			LOG.warn("AAController - remote exception getting uid from CN" + commonName);
 		}
 		
 		return uid;
	}
	
	public String getCommonNameFromUserID(String userID) throws
												RemoteException,
												NotAuthorisedFaultMessage,
												ProcessingFaultMessage,
												InputFaultMessage
	{
		String cn = "";
		try {
			cn = SecurityHelper.getAAManagementClient().getPort().getCNFromUID(userID); 
		} catch (NotAuthorisedFaultMessage nafm)
		{
			LOG.warn("AAController - not authorised to get CN from UID " + userID);
		} catch (InputFaultMessage ifm) {
			LOG.warn("AAController - input fault message getting CN from UID " + userID);
		} catch (ProcessingFaultMessage pfm) {
			LOG.warn("AAController - processing fault getting UID from CN " + userID);
		} catch (RemoteException rex) {
			LOG.warn("AAController - remote exception getting UID from CN " + userID);
		}
		
		return cn;
	}
	
	
	public boolean resetPassword () throws
								RemoteException,
								NotAuthorisedFaultMessage,
								ProcessingFaultMessage,
								InputFaultMessage
	{
		boolean success = false;
		
		try
		{
			String userID = "CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN();
			ResetPasswordRequestType rprt = new ResetPasswordRequestType(userID, UserModel.getInstance().getPassword()); 
			success = SecurityHelper.getAAManagementClient().getPort().resetPassword(rprt);
		} catch (NullPointerException nex) {
			LOG.warn("AAController : null pointer resetting password" + nex);
		} 
		
		return success;
	}
      
	public String getEmailAddressForCurrentUser()
	{
		String emailAddress = "";
		try
		{
			org.psygrid.www.xml.security.core.types.UserType uType = SecurityHelper.getAAManagementClient().getPort().getUserFromLDAP("CN=" +UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
			emailAddress = uType.getEmailAddress();
		} catch (NotAuthorisedFaultMessage nafm) {
			LOG.error("AA Controller : updating email address " + nafm);
		} catch (ProcessingFaultMessage pgfe) {
			LOG.error("AA Controller : updating email address " + pgfe);
		} catch (RemoteException rex) {
			LOG.error("AA Controller : updating email address " + rex);
		}
		return emailAddress;
	}
	
	public String getMobileNumberForCurrentUser() 
	{
		String mobileNumber = "";
		try
		{
			org.psygrid.www.xml.security.core.types.UserType uType = SecurityHelper.getAAManagementClient().getPort().getUserFromLDAP("CN=" +UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
			mobileNumber = uType.getMobileNumber();
		} catch (NotAuthorisedFaultMessage nafm) {
			LOG.error("AA Controller : updating email address " + nafm);
		} catch (ProcessingFaultMessage pgfe) {
			LOG.error("AA Controller : updating email address " + pgfe);
		} catch (RemoteException rex) {
			LOG.error("AA Controller : updating email address " + rex);
		}
		return mobileNumber;
	}
	
	private boolean isSysAdmin(ProjectType p)
	{
		 boolean isSysAdmin = false;
		 try
		 {
			 if(org.psygrid.securitymanager.security.SecurityManager.getInstance().isSuperUser()){
				 isSysAdmin = true;
			 } else {
				 List<RoleType> myRoles = SecurityHelper.getAAQueryClient().getMyRolesInProject(p);
				 for (RoleType myRole: myRoles)
				 {
					 if (myRole.getName().equals("SystemAdministrator")) {
						 isSysAdmin = true;
					 }
				 }
			 }
		}  catch (PGSecurityException pge) {
			LOG.error("AAController : PGSecurityException pge "  + pge.getMessage());
		} catch (PGSecurityInvalidSAMLException pginsamlex) {
			LOG.error("AAController : PGSecurityInvalidSAMLException " + pginsamlex.getMessage());
		} catch (PGSecuritySAMLVerificationException pgsmve) {
			LOG.error("AAController : PGSecuritySAMLVerificationException " + pgsmve.getMessage());
		} catch (ConnectException cex) {
			LOG.error("AAController : ConnectionException " + cex.getMessage());
		} catch (NotAuthorisedFaultMessage nafm) {
			LOG.error("AAController : NotAuthorisedFaultMessage " + nafm.getMessage());
		}

		return isSysAdmin;
	}
	
	public boolean userHasNoProjects() throws RemoteException, 
	  NotAuthorisedFaultMessage,
		  ProcessingFaultMessage, 
		  InputFaultMessage {
	
		boolean result = true;
	
		UserPrivilegesType upt = SecurityHelper.getAAManagementClient().getPort().getUser("CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN());
		AttributeType[] currentats = upt.getAttribute();				
		if (currentats != null) {
			if (currentats.length>0){
				result=false;
			}
		}

		return result;
	}
	
	public boolean makeUserAccountDormant() throws RemoteException, 
	  NotAuthorisedFaultMessage,
		  ProcessingFaultMessage, 
		  InputFaultMessage {
	
		boolean result = false;
	
		result = SecurityHelper.getAAManagementClient().getPort().deleteUser(
				new String[]{"CN="+UserModel.getInstance().getFirstname() + " " + UserModel.getInstance().getLastname() + LDAPPropertiesHelper.getPropertyHelper().getUserBaseDN()});

		return result;
	}

}