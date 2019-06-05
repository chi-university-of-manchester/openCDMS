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

package org.psygrid.esl.util;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.randomise.RandomisationException;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * Utility class for commonly used methods when sending emails
 * 
 * @author Lucy Bridges
 *
 */
public class EmailUtil {
	
	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(EmailUtil.class);

	/**
	 * Attribute authority query client (set using applicationContext.xml)
	 */
	private AAQCWrapper aaqc;
	
	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}
	
	
	/**
	 * Retrieve the email addresses of the users having one of the roles to be emailed
	 * at a particular point during randomisation of the subject
	 * 
	 * @param subject
	 * @param roles
	 * @return recipients
	 * @throws RandomisationException
	 */
	public List<String> getEmailRecipients(ISubject subject, List<String> roles) throws NotAuthorisedFaultMessage, ConnectException {
		
		String projectCode = subject.getGroup().getProject().getProjectCode();
		ProjectType project = new ProjectType(null, projectCode, null, null, false);

		/*
		 * The full list of people to send emails to, who have one of the roles
		 * specified 
		 */
		List<String> users = new ArrayList<String>();

		/*
		 * The users' email addresses (provided by the aaqc)
		 */
		List<String> emails = new ArrayList<String>();

		if ( null == aaqc ){
			sLog.info("Attribute authority query client has not been initialised.");
		}
		else{
			try {
				for (String role: roles) {
					try{
						//retrieve the users having each role listed as receiving an email 
						for (String user: aaqc.getUsersInProjectWithRole(project, new RoleType(role, null))) {
							if (user != null) {
								users.add(user);
							}
						} 
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up users for project='"+project.getIdCode()+
				 				"' with role='"+role+"'.", ex);
					}
				}
				
				String subjectGroup = subject.getGroup().getGroupCode();

				//restrict list of users to only those having access to the group belonging to the Subject
				for ( String user: users){
					boolean found = false;

					List<GroupType> usersGroups = null;
					try{
						usersGroups = aaqc.getUsersGroupsInProject(user, project);
					}
					catch(PGSecuritySAMLVerificationException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}
					catch(PGSecurityInvalidSAMLException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}

					//find out whether the subject's group is listed in the user's allowed groups
					for (GroupType gt: usersGroups){
						if ( subjectGroup.equals(gt.getIdCode()) ){
							found = true;
							break;
						}
					}

					if ( found ){
						try{
							InternetAddress email = aaqc.lookUpEmailAddress(user);
							if ( email != null ){
								emails.add(email.getAddress());
							}
							else{
								sLog.info("RemoteRandomiser-getEmailRecipients: no email address for user '"+user+"'");
							}
						}
						catch(PGSecurityException ex){
							sLog.error("RemoteRandomiser-getEmailRecipients: Unable to look up email address for user='"+user+"'",ex);
						}
					}
				}
			}
			catch(NotAuthorisedFaultMessage ex){
				throw new NotAuthorisedFaultMessage("Not authorised to connect to attribute authority query client.", ex);
			}
			catch (ConnectException ex){
				throw ex;
			}

		}
		return emails;

	}
	
	/**
	 * Retrieve the email addresses of the users having one of the roles to be emailed
	 * at a particular point during randomisation of the subject
	 * 
	 * @param subject
	 * @param roles
	 * @return recipients names and numbers
	 * @throws RandomisationException
	 */
	public Map<String, String> getPhoneNumbers(ISubject subject, List<String> roles) throws NotAuthorisedFaultMessage, ConnectException {

		String projectCode = subject.getGroup().getProject().getProjectCode();
		ProjectType project = new ProjectType(null, projectCode, null, null, false);

		/*
		 * The full list of people (and their phone numbers) to send SMSs to, who 
		 * have one of the roles specified 
		 */
		Map<String, String> users = new HashMap<String, String>();

		if ( null == aaqc ){
			sLog.info("Attribute authority query client has not been initialised.");
		}
		else{
			try {
				for (String role: roles) {
					try{
						//retrieve the users having each role listed as receiving an email 
						for (String user: aaqc.getUsersInProjectWithRole(project, new RoleType(role, null))) {
							if (user != null) {
								users.put(user, null);
							}
						}
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up users for project='"+project.getIdCode()+
								"' with role='"+role+"'.", ex);
					}
				}

				String subjectGroup = subject.getGroup().getGroupCode();

				//restrict list of users to only those having access to the group belonging to the Subject
				for ( String user: users.keySet()){
					boolean found = false;

					List<GroupType> usersGroups = null;
					try{
						usersGroups = aaqc.getUsersGroupsInProject(user, project);
					}
					catch(PGSecuritySAMLVerificationException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}
					catch(PGSecurityInvalidSAMLException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up groups of user='"+user+"' for project='"+project.getIdCode()+"'", ex);
					}

					//find out whether the subject's group is listed in the user's allowed groups
					for (GroupType gt: usersGroups){
						if ( subjectGroup.equals(gt.getIdCode()) ){
							found = true;
							break;
						}
					}

					if ( found ){
						try{
							String number = aaqc.lookUpMobileNumber(user);
				
							if ( number != null ){
								users.put(user, number);
							}
							else{
								sLog.info("RemoteRandomiser-getEmailRecipients: no phone number for user '"+user+"'");
							}
						}
						catch(PGSecurityException ex){
							sLog.error("RemoteRandomiser-getEmailRecipients: Unable to look up phone number for user='"+user+"'",ex);
						}
					}
				}
			}
			catch(NotAuthorisedFaultMessage ex){
				throw new NotAuthorisedFaultMessage("Not authorised to connect to attribute authority query client.", ex);
			}
		}

		Map<String, String> finalusers = new HashMap<String, String>();
		//Ignore all users without a phone number
		for (String user: users.keySet()) {
			if (users.get(user) != null) {
				finalusers.put(user, users.get(user));
			}
		}
		
		return finalusers;

	}
}
