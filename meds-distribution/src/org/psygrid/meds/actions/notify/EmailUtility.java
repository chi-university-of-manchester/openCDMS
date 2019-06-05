package org.psygrid.meds.actions.notify;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.email.Email;
import org.psygrid.common.email.QueuedEmail;
import org.psygrid.common.email.CommonEmailBodyConverter;
import org.psygrid.meds.project.Project;
import org.psygrid.meds.project.ProjectDao;
import org.psygrid.meds.utils.security.NotAuthorisedFault;
import org.psygrid.meds.utils.security.RetrievePharmacyMappingException;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

public class EmailUtility {
	
	private enum PrivilegeRestriction{
		byCentre,
		byPharmacy
	};
	
	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(EmailUtility.class);

	
	/**
	 * Attribute authority query client (set using applicationContext.xml)
	 */
	private AAQCWrapper aaqc;
	
	private NotificationDao notifyDao;
	
	private ProjectDao projDao;
	
	private String fromAddress;
	
	private String toAddress;

	public EmailUtility(){}
	
	public List<String> getDefaultRolesToNotify(){
		List<String> notificationList = new ArrayList<String>();
		notificationList.add(RBACRole.ClinicalResearchOfficer.toString());
		notificationList.add(RBACRole.Pharmacist.toString());
		return notificationList;
	}
	
	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}
	
	
	public void createEmail(String projectCode, String pharmacyCode, Map<String, String> tokenSubstitutions, NotificationType emailType) throws UnexpectedException{
		
		//Recipients depend on
		//1) What type of email this is (we need a place that stores a list of contactable roles per type of email
		//2) What particular locale this email is particular to. For example, if the email is an package allocation
		
		Map<String, PrivilegeRestriction> rolesAndRestrictions = new HashMap<String, PrivilegeRestriction>();
		
		switch(emailType){
		case medsAllocation:
			rolesAndRestrictions.put(RBACRole.ClinicalResearchOfficer.toString(), PrivilegeRestriction.byCentre);
			rolesAndRestrictions.put(RBACRole.Pharmacist.toString(), PrivilegeRestriction.byPharmacy);
		break;
		case medicationStockWarning:
			rolesAndRestrictions.put(RBACRole.Pharmacist.toString(), PrivilegeRestriction.byPharmacy);
		break;
		}
		
		
		
		QueuedEmail newEmail = new QueuedEmail();
		
		Project p = projDao.getProject(projectCode);
		Email email = p.getEmailByType(emailType);
		
		
		newEmail.setSubject(email.getSubject());
		
		String emailBody = email.getBody();
		String modifiedEmailBody = CommonEmailBodyConverter.substituteParamsIntoEmailBody(emailBody, tokenSubstitutions);
		newEmail.setBody(modifiedEmailBody);


		try {
			
			Map<String, String> pharmacyToCentreMap = null;
			
			try{
				pharmacyToCentreMap = aaqc.getPharmacyToCentreMapping(projectCode);	
			}catch(RetrievePharmacyMappingException ex){
				throw new UnexpectedException("Unexpected exception occurred when generating email", ex);
			}
			
			
			//Go through the roles and restrictions map. If we find a role that just has a centre restriction then no problem,
			//just call getEmailRecipients(...).
			
			//but if we find a role that has a pharmacy restriction we need to first get the pharmacyToCentreMap.
			//This will tell us what centre.
			
			//We can then call getEmailRecipients(...) with the centre, and get all candidates.
			
			Set<String> roles = rolesAndRestrictions.keySet();
			List<String> addresses = new ArrayList<String>();
			for(String role: roles){
				PrivilegeRestriction restriction = rolesAndRestrictions.get(role);
				if(restriction == PrivilegeRestriction.byCentre){
					List<String> rolesList = new ArrayList<String>();
					rolesList.add(role);
					addresses.addAll(getEmailRecipients(projectCode, pharmacyToCentreMap.get(pharmacyCode), rolesList));
				}else if (restriction == PrivilegeRestriction.byPharmacy){
					addresses.addAll(this.getEmailRecipientsBoundedByPharmacy(projectCode, pharmacyToCentreMap.get(pharmacyCode), pharmacyCode, role));
				}
			}
			
				
			newEmail.setBccAddresses(addresses);
		}
		catch(NotAuthorisedFaultMessage ex){
			throw new NotAuthorisedFault("Not authorised to connect to attribute authority query client.", ex);
		}
		catch (ConnectException ex){
			throw new UnexpectedException("Unable to connect to attribute authority query client.", ex);
		}
		newEmail.setFromAddress(fromAddress);
		newEmail.setToAddress(toAddress);

		//save email
		sLog.info("Email queued for "+newEmail.getToAddress());
		notifyDao.saveEmail(newEmail);	//add email to queue to be sent

		
		
	}
	
	public List<String> getEmailRecipientsBoundedByPharmacy(String projectCode, String centreId, String pharmacyId, String role){
		
		List<String> emails = new ArrayList<String>();
		
		//We know what role. So we get the users by role in the centre.
		//Then we get the group attributes for those users. Those that don't have belong to the pharmacy get cut.
		
		String[] matchingUsers = null;
		
		try {
			matchingUsers = aaqc.getUserInProjectWithRoleGroupAndPharmacy(new ProjectType(null, projectCode, null, null, false), new RoleType(role, null), new GroupType(null, centreId, null), pharmacyId);
		} catch (ProcessingFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotAuthorisedFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<String> matchingUsersList = Arrays.asList(matchingUsers);
		
		for(String user : matchingUsersList){
			
			try{
				InternetAddress email = aaqc.lookUpEmailAddress(user);
				if ( email != null ){
					emails.add(email.getAddress());
				}
				else{
					sLog.info("Meds dist-getEmailRecipients: no email address for user '"+user+"'");
				}
			}
			catch(PGSecurityException ex){
				sLog.error("Meds dist-getEmailRecipients: Unable to look up email address for user='"+user+"'");
			} catch (NotAuthorisedFaultMessage e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return emails;
	}
	
	
	/**
	 * Retrieves a the email addresses for all users in the project with the specified centre and role 
	 * @param projectCode
	 * @param centreCode
	 * @param roles
	 * @return
	 * @throws NotAuthorisedFaultMessage
	 * @throws ConnectException
	 */
	public List<String> getEmailRecipients(String projectCode, String centreCode, List<String> roles) throws NotAuthorisedFaultMessage, ConnectException{
		
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
						if ( centreCode.equals(gt.getIdCode()) ){
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
								sLog.info("Meds dist-getEmailRecipients: no email address for user '"+user+"'");
							}
						}
						catch(PGSecurityException ex){
							sLog.error("Meds dist-getEmailRecipients: Unable to look up email address for user='"+user+"'");
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


	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public NotificationDao getNotifyDao() {
		return notifyDao;
	}

	public void setNotifyDao(NotificationDao notifyDao) {
		this.notifyDao = notifyDao;
	}
	
	
	public List<String> getRecipientsForNotification(String projectCode, String pharmacyCode, String notificationType){
		return null;
	}

	public ProjectDao getProjDao() {
		return projDao;
	}

	public void setProjDao(ProjectDao projDao) {
		this.projDao = projDao;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	
}
