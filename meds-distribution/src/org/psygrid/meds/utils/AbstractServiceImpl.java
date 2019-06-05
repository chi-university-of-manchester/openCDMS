package org.psygrid.meds.utils;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.logging.AuditLogger;
import org.psygrid.meds.actions.notify.EmailUtility;
import org.psygrid.meds.events.MedsEventDao;
import org.psygrid.meds.export.MedsExportDao;
import org.psygrid.meds.medications.MedicationPackageDao;
import org.psygrid.meds.project.ProjectDao;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.meds.utils.security.NotAuthorisedFault;
import org.psygrid.meds.utils.security.RetrievePharmacyMappingException;

/**
 * Provides a common base class for services.
 * 
 * Mostly contains security relation code.
 * 
 * @author Terry Child
 */
public abstract class AbstractServiceImpl  {

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(AbstractServiceImpl.class);

	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(AbstractServiceImpl.class);

	protected ProjectDao projectDao;
	
	protected MedicationPackageDao medsDao;
	
	protected MedsEventDao eventDao;
	
	protected MedsExportDao medsExportDao;
	
	private EmailUtility emailUtility;
	
	
	/**
	 * Called to check permissions
	 */
    protected IAccessEnforcementFunction accessControl = null;


	public IAccessEnforcementFunction getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(IAccessEnforcementFunction accessControl) {
		this.accessControl = accessControl;
	}


	protected abstract String getComponentName();

	/**
	 * Gets the user name from the supplied saml assertion.
	 * @param saml
	 * @return the user name
	 */
	protected String findUserName(String saml){
        
        //find invoker's username
        String userName = null;
        try{
            userName = accessControl.getUserFromUnverifiedSAML(saml);
        }
        catch(PGSecurityException ex){
            userName = "Unknown";
        }
        return userName;
    }
	
	// These checkPermission methods should probably be in a base class.

	/**
	 * Check permissions for a given action against the project and groups of the supplied record identifier.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByIdentifierToGroupLevel(String saml,String method,RBACAction action,String identifier) throws NotAuthorisedFault {

		String projectCode;
		String groupCode;
		try {
			projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
		} catch (InvalidIdentifierException ex) {
			throw new NotAuthorisedFault("The supplied identifier is invalid.", ex);
		}
		checkPermissionsByGroupSpecifiedByGroup(saml, method, action, projectCode, groupCode);
	}
	
	
	/**
	 * Check permissions for a given action within a given project and group.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByGroupSpecifiedByGroup(String saml,String method,RBACAction action,String projectCode,String groupCode) throws NotAuthorisedFault {

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);

			if (!accessControl.authoriseUser(saml, new AEFGroup(null,groupCode, null), action.toAEFAction(), 
												new AEFProject(null,projectCode,false))) {
				logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ method + "' for project '" + projectCode + "', group '"
						+ groupCode + "'");
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}

	
	/**
	 * Check permissions for a given action against the project and groups of the supplied record identifier.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByIdentifierToPharmacyLevel(String saml,String method,RBACAction action,String identifier) throws NotAuthorisedFault {

		String projectCode;
		String groupCode;
		try {
			projectCode = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			groupCode = IdentifierHelper.getGroupCodeFromIdentifier(identifier);
		} catch (InvalidIdentifierException ex) {
			throw new NotAuthorisedFault("The supplied identifier is invalid.", ex);
		}
		//checkPermissionsByGroup(saml, method, action, projectCode, groupCode);
	}
	
	
	/**
	 * Check permissions for a given action within a given project and group.
	 * Note that the pharmacy, not the group is passed in. A transform is then done to determine the group for that pharmacy.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 * @throws UnexpectedException 
	 */
	protected void checkPermissionsByGroupSpecifiedByPharmacy(String saml,String method,RBACAction action,String projectCode,String pharmacyCode) throws NotAuthorisedFault, UnexpectedException {

		Map<String, String> pharmacyToCentreMapping = null;
		try {
			pharmacyToCentreMapping = this.emailUtility.getAaqc().getPharmacyToCentreMapping(projectCode);
		} catch (RetrievePharmacyMappingException e) {
			throw new UnexpectedException("Exception when checking permissions by group", e);
		}
		String groupCode = pharmacyToCentreMapping.get(pharmacyCode);
		
		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);

			if (!accessControl.authoriseUser(saml, new AEFGroup(null,groupCode, null), action.toAEFAction(), 
												new AEFProject(null,projectCode,false))) {
				logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ method + "' for project '" + projectCode + "', group '"
						+ groupCode + "'");
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}

		
	protected boolean userIsAPharmacist(String projectCode, String user) throws UnexpectedException{
		String method = "getUserRole";
		try {
			List<RoleType> roles = this.emailUtility.getAaqc().getUserRolesInProject(new ProjectType(null, projectCode, null, null, false), user);
			boolean isAPharmacist = false;
			for(RoleType r : roles){
				if(r.getName().equals(RBACRole.Pharmacist.toString())){
					isAPharmacist = true;
					break;
				}
			}
			
			return isAPharmacist;
			
		} catch (NotAuthorisedFaultMessage e) {
			sLog.error(method + ": " + e.getClass().getSimpleName(), e);
			throw new NotAuthorisedFault(
					"Not authorised to retrieve the user's roles", e);
		} catch (ConnectException e) {
			sLog.error(method + ": " + e.getClass().getSimpleName(), e);
			throw new UnexpectedException("Connection Exception when trying to retrieve user role", e);
		} catch (PGSecuritySAMLVerificationException e) {
			sLog.error(method + ": " + e.getClass().getSimpleName(), e);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					e);
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecurityException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);

		}
		
		
	}
	
	/**
	 * Check permissions to ensure that the user has group inclusion for the stated group and has the right role.
	 * Furthhermore checks to make sure that that user also belongs to the stated pharmacy.
	 * @param saml
	 * @param method
	 * @param action
	 * @param projectCode
	 * @param pharmacyCode
	 * @throws UnexpectedException 
	 */
	protected void checkPermissionsByPharmacy(String saml, String method, RBACAction action, String projectCode, String pharmacyCode) throws UnexpectedException{
		
		checkPermissionsByGroupSpecifiedByPharmacy(saml, method, action, projectCode, pharmacyCode);
		
		Map<String, String> pharmacyToCentreMapping;
		try {
			pharmacyToCentreMapping = this.emailUtility.getAaqc().getPharmacyToCentreMapping(projectCode);
		} catch (RetrievePharmacyMappingException e1) {
			throw new UnexpectedException("Error when checking permissions by pharmacy", e1);
		}
		String groupCode = pharmacyToCentreMapping.get(pharmacyCode);
		
		try {
			String userName = this.accessControl.getUserFromUnverifiedSAML(saml);
			String firstName = LdapParser.getFirstName(userName);
			String surName = LdapParser.getSurname(userName);
			GroupAttributeType[] gatArray = emailUtility.getAaqc().getAaqc().getPort().getGroupAttributesForUserInGroup(projectCode, groupCode, firstName, surName);
			
			boolean pharmacyMatchFound = false;
			
			List<GroupAttributeType> gatList = Arrays.asList(gatArray);
			for(GroupAttributeType gat : gatList){
				if(gat.getDetail2().equals(pharmacyCode)){
					pharmacyMatchFound = true;
					break;
				}
			}
			
			if(!pharmacyMatchFound){
				throw new NotAuthorisedFault("User " + userName + " does not belong to pharmacy " + pharmacyCode);
			}
			
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
		} catch (PGSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Check permissions for a given action within a given project.
	 * Throw a NotAuthorisedFault exception if permission denied.
	 */
	protected void checkPermissionsByProject(String saml,String method,RBACAction action,String projectCode) throws NotAuthorisedFault {

		String userName = findUserName(saml);
		String callerIdentity = accessControl.getCallersIdentity();
		try {
			logHelper.logMethodCall(getComponentName(), method, userName,callerIdentity);
			if (!accessControl.authoriseUser(saml, new AEFGroup(),action.toAEFAction(),new AEFProject(null,projectCode,false))) {
				logHelper.logAccessDenied(getComponentName(), method, userName,callerIdentity);
				throw new NotAuthorisedFault("User '" + userName
						+ "' is not authorised to perform the action '"
						+ method + "' for project '" + projectCode + "'");
			}
		} catch (PGSecurityInvalidSAMLException ex) {
			sLog.error(method + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion has expired", ex);
		} catch (PGSecuritySAMLVerificationException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"The supplied SAML assertion does not come from a trusted issuer",
					ex);
		} catch (PGSecurityException ex) {
			sLog.error(action + ": " + ex.getClass().getSimpleName(), ex);
			throw new NotAuthorisedFault(
					"An error occurred during authorisation", ex);
		}
	}

	public ProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public MedicationPackageDao getMedsDao() {
		return medsDao;
	}

	public void setMedsDao(MedicationPackageDao medsDao) {
		this.medsDao = medsDao;
	}

	public MedsEventDao getEventDao() {
		return eventDao;
	}

	public void setEventDao(MedsEventDao eventDao) {
		this.eventDao = eventDao;
	}

	public MedsExportDao getMedsExportDao() {
		return medsExportDao;
	}

	public void setMedsExportDao(MedsExportDao medsExportDao) {
		this.medsExportDao = medsExportDao;
	}
	
	public EmailUtility getEmailUtility() {
		return emailUtility;
	}

	public void setEmailUtility(EmailUtility emailUtility) {
		this.emailUtility = emailUtility;
	}

}
