package org.psygrid.meds.actions.notify;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.psygrid.meds.utils.LdapParser;
import org.psygrid.meds.utils.security.NotAuthorisedFault;
import org.psygrid.meds.utils.security.RetrievePharmacyMappingException;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * This class is just a wrapper around the AAQueryClient class to make
 * it easier to use with the Spring framework.
 * 
 * @author Bill Vance
 *
 */
public class AAQCWrapper {

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(AAQCWrapper.class);

	private AAQueryClient aaqc;
	
	public void setProperties(String propsFile){
		try{
			aaqc = new AAQueryClient(propsFile);
		}
		catch(PGSecurityException ex){
			sLog.error("Cannot instantiate Attribute Authority Query Client", ex);
		}
	}
	
	public Map<String, String> getPharmacyToCentreMapping(String projectCode) throws RetrievePharmacyMappingException{
	
			String method = "getPharmacyToCentreMapping";
			Map<String, String> pharmacyToCentreMap = new HashMap<String, String>();
		
			GroupType[] groups = null;
			try {
				groups = aaqc.getPort().getGroupsInProject(new ProjectType(null, projectCode, null, null, false));
				int numGroups = groups.length;
				for(int i = 0; i < numGroups; i++){
					GroupAttributeType[] gats;
					gats = aaqc.getPort().getGroupAttributesForGroup(projectCode, groups[i].getIdCode());
					
					int numGats = gats.length;
					for(int j = 0; j < numGats; j++){
						pharmacyToCentreMap.put(gats[j].getDetail2(), groups[i].getIdCode());
					}
				}
			} catch (ProcessingFaultMessage e) {
				sLog.error(method + " : " + e.getClass().getSimpleName(), e);
				throw new RetrievePharmacyMappingException(e);
			} catch (NotAuthorisedFaultMessage e) {
				sLog.error(method + " : " + e.getClass().getSimpleName(), e);
				throw new NotAuthorisedFault(e);
			} catch (InputFaultMessage e) {
				sLog.error(method + " : " + e.getClass().getSimpleName(), e);
				throw new RetrievePharmacyMappingException(e);
			} catch (RemoteException e) {
				sLog.error(method + " : " + e.getClass().getSimpleName(), e);
				throw new RetrievePharmacyMappingException(e);
			}
			
			return pharmacyToCentreMap;
		
	}
	
	public String getSAMLAssertion(String user) 
	throws  ConnectException, PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException, NotAuthorisedFaultMessage {
		SAMLAssertion saml = aaqc.getSAMLAssertion(user);
		String sa = saml.toString();
		return sa;
	}

	public List<InternetAddress> lookUpEmailAddress(ProjectType pt,
			GroupType gt, RoleType rt) throws ConnectException,
			PGSecurityException, NotAuthorisedFaultMessage {
		if ( null != aaqc ){
			return aaqc.lookUpEmailAddress(pt, gt, rt);
		}
		else{
			//aaqc has not been initialised so just return an empty list
			return new ArrayList<InternetAddress>();
		}
	}

	public InternetAddress lookUpEmailAddress(String user) 
	throws ConnectException, PGSecurityException, NotAuthorisedFaultMessage {
		if ( null != aaqc ){
			return aaqc.lookUpEmailAddress(user);
		}
		else{
			//aaqc has not been initialised so just return null
			return null;
		}
	}

	public String lookUpMobileNumber(String user) 
	throws ConnectException, PGSecurityException, NotAuthorisedFaultMessage {
		
		
		if ( null != aaqc ){
			//FIXME 
			return aaqc.lookUpMobileNumber(user);
		}
		else{
			//aaqc has not been initialised so just return null
			return null;
		}
	}

	
	
	public String[] getUserInProjectWithRoleGroupAndPharmacy(ProjectType pt, RoleType rt, GroupType gt, String pharmacyCode) throws ProcessingFaultMessage, NotAuthorisedFaultMessage, InputFaultMessage, RemoteException{
		
		List<String> matchingUsers = new ArrayList();
		
		String[] users = aaqc.getPort().getUsersInGroupInProjectWithRole(gt, rt, pt);
		
		//We'll need to parse the user names.
		
		
		int numUsers = users.length;
		for(int i = 0; i < numUsers; i++){
			
			boolean userIsInPharmacy = false;
			
			
			String firstName = LdapParser.getFirstName(users[i]);
			String lastName = LdapParser.getSurname(users[i]);
			
			GroupAttributeType[] gatArray = aaqc.getPort().getGroupAttributesForUserInGroup(pt.getIdCode(), gt.getIdCode(), firstName, lastName);
			
			int numAttributes = gatArray.length;
			
			for(int j = 0; j < numAttributes; j++){
				if(gatArray[j].getDetail2().equals(pharmacyCode)){
					userIsInPharmacy = true;
					break;
				}
			}
			
			if(userIsInPharmacy){
				matchingUsers.add(users[i]);
			}
			
			
		}
		
		String[] ar = new String[matchingUsers.size()];
		return matchingUsers.toArray(ar);
		
	}
	
	public List<RoleType> getUserRolesInProject(ProjectType pt, String user) throws NotAuthorisedFaultMessage, ConnectException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException, PGSecurityException{
		return aaqc.getUsersRolesInProject(user, pt);
	}

	public String[] getUsersInProjectWithRole(ProjectType pt, RoleType rt)
	throws ConnectException, PGSecurityException, NotAuthorisedFaultMessage {
		
		
		
		if ( null != aaqc ){
			return aaqc.getUsersInProjectWithRole(pt, rt);
		}
		else{
			//aaqc has not been initialised so just return null
			return null;
		}
	}

	public List<GroupType> getUsersGroupsInProject(String user,
			ProjectType project) throws ConnectException,
			PGSecuritySAMLVerificationException,
			PGSecurityInvalidSAMLException, PGSecurityException,
			NotAuthorisedFaultMessage {
		if ( null != aaqc ){
			return aaqc.getUsersGroupsInProject(user, project);
		}
		else{
			//aaqc has not been initialised so just return null
			return null;
		}
	}

	public AAQueryClient getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}
}
