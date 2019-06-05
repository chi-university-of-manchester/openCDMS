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


package org.psygrid.data.utils.esl;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.dto.Project;
import org.psygrid.esl.model.dto.Subject;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.ESLSubjectLockedFault;
import org.psygrid.esl.services.ESLSubjectNotFoundFault;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.RandomisationException;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;


/**
 * Class providing a connection to the org.psygrid.esl service.
 * 
 * Enables details to be retrieved about the number of  
 * randomisations performed, which is required for UKCRN 
 * reports.
 * 
 * @author Lucy Bridges
 *
 */
public class RemoteClient implements IRemoteClient {

	private String url;

	private EslClient client;

	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(RemoteClient.class);

	/**
	 * Default entry to be used for a blank or null result.
	 */
	private static final String ENTRY_DEFAULT = "Unknown";
	
	public RemoteClient () throws EslException {
		try {
			if (url != null) {
				client = new EslClient(new URL(url));
			}
			else {
				client = new EslClient();
			}
		}
		catch (Exception e) {
			sLog.info("Problem instantiating EslClient: "+e.getClass().getSimpleName(),e);
			throw new EslException("Problem connecting to the ESL service");
		}


	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	/**
	 * Use the ESL to get the dates (and therefore number) of randomisation
	 * events for a given subject.
	 * 
	 * @param projectCode
	 * @param subjectCode
	 * @param saml
	 * @return list of dates
	 * @throws EslException
	 */
	public Date[] getSubjectRandomisationEvents(String projectCode, String subjectCode, String saml) throws EslException {

		try {
			List<Calendar> cal = client.retrieveSubjectRandomisationEvents(projectCode, subjectCode, saml);
			if (cal == null) {
				return null;
			}

			Date[] dates = new Date[cal.size()];
			for (int i = 0; i < cal.size(); i++) {
				dates[i] = cal.get(i).getTime();
			}	
			return dates;
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (RandomisationException e) {	
			sLog.info("Randomisation exception occurred during EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Randomisation exception occurred calling the ESL service");
		}
	}


	/**
	 * Use the ESL to find out if a project uses randomisation.
	 * 
	 * @param projectCode
	 * @param saml
	 * @return isRandomised
	 * @throws EslException
	 */ 
	public boolean isProjectRandomised(String projectCode, String saml) throws EslException {

		try {
			return client.isProjectRandomised(projectCode, saml);
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
	}


	/**
	 * Retrieve the UKCRN defined project identifier and acronym
	 * for a given project code
	 * 
	 * @param projectCode
	 * @return data
	 */
	public String[] getProjectDetails(String projectCode, String saml) {

		String[] ukCRNData  = new String[2];		
		ProjectType project = null;

		try {
			if ( null == aaqc ){
				sLog.info("Attribute authority query client has not been initialised.");
			}
			else{
				project = aaqc.getProjectForCode(projectCode, getUser(saml));
				if (project != null) {
					ukCRNData[0] = project.getAliasName();
					ukCRNData[1] = project.getAliasId();
				}
			}
		}
		catch(PGSecurityException ex){
			sLog.info("Unable to look up data for project='"+project.getIdCode()+" "+ex.getMessage());
		}catch(PGSecurityInvalidSAMLException ex){
			sLog.info("Unable to look up data for project='"+project.getIdCode()+" "+ex.getMessage());
		}
		catch (PGSecuritySAMLVerificationException ex) {
			sLog.info("Unable to look up data for project='"+project.getIdCode()+"'"+" "+ex.getMessage());
		}	
		catch (ConnectException ex) {
			sLog.info("Unable to look up data for project='"+project.getIdCode()+"'"+" "+ex.getMessage());
		}
		catch(NotAuthorisedFaultMessage ex){
			sLog.info("Unable to look up data for project='"+project.getIdCode()+"'"+" "+ex.getMessage());
		}
		catch (Exception e) {
			sLog.info("Unknown problem retrieving UKCRN project details"+" "+e.getMessage());
		}

		if (ukCRNData[0] == null || ukCRNData[0].equals("")) {
			ukCRNData[0] = ENTRY_DEFAULT;
		}
		if (ukCRNData[1] == null || ukCRNData[1].equals("")) {
			ukCRNData[1] = ENTRY_DEFAULT;
		}

		return ukCRNData;
	}

	/**
	 * Retrieve the UKCRN defined group identifier and name for
	 * the record identified by the given identifier
	 * 
	 * Returns a String array with the following elements:
	 * data[0] = UKCRN approaved site ID
	 * data[1] = site name given to UKCRN
	 * data[2] = the group code for the identifier
	 * 
	 * @param identifier
	 * @return data
	 */
	/*public String[] getGroupDetails(String identifier) {

		String[] ukCRNData = new String[3];

		try {
			Record record = dao.getRecord(identifier, RetrieveDepth.RS_MINIMUM);
			Site site = record.toHibernate().getSite();

			ukCRNData[0] = site.getSiteId();
			ukCRNData[1] = site.getSiteName();
			ukCRNData[2] = record.getIdentifier().getGroupPrefix();
		}
		catch (DAOException e) {
			sLog.error("Problem retrieving site details for record "+identifier, e);
		}
		catch (NullPointerException npe) {
			//This can be thrown if no site has been specified,
			//it can be ignored as the default 'Unknown' will be given below.
			sLog.error("Null pointer exception occured when getting group details for "+identifier, npe);
		}
		catch (Exception e) {
			sLog.error("Unknown problem retrieving site details for record "+identifier, e);
		}

		if (ukCRNData[0] == null || ukCRNData[0].equals("")) {
			ukCRNData[0] = ENTRY_DEFAULT;
		}
		if (ukCRNData[1] == null || ukCRNData[1].equals("")) {
			ukCRNData[1] = ENTRY_DEFAULT;
		}
		return ukCRNData;
	}*/

	public String getUserInRoleForGroup(String role, String groupCode, String projectCode, String saml) {

		try {
			ProjectType projectType = aaqc.getProjectForCode(projectCode, getUser(saml));
			GroupType groupType = aaqc.getGroupForCode(projectType, groupCode);
			RoleType roleType = new RoleType(role, null); 
			String[] roles = aaqc.getUsersInGroupInProject(projectType, groupType, roleType);

			String user = roles[0];
			
			if (user == null || user.equals("")) {
				return ENTRY_DEFAULT;
			}
			
			return getPrettyName(user);
		} 
		catch(PGSecurityException ex){
			sLog.info("Unable to look up data for project='"+projectCode+" "+ex.getMessage());
		}catch(PGSecurityInvalidSAMLException ex){
			sLog.info("Unable to look up data for project='"+projectCode+"'"+" "+ex.getMessage());
		}
		catch (PGSecuritySAMLVerificationException ex) {
			sLog.info("Unable to look up data for project='"+projectCode+"'"+" "+ex.getMessage());
		}	
		catch (ConnectException ex) {
			sLog.info("Unable to look up data for project='"+projectCode+"'"+" "+ex.getMessage());
		}
		catch(NotAuthorisedFaultMessage ex){
			sLog.info("Unable to look up data for project='"+projectCode+"'"+" "+ex.getMessage());
		}
		catch (Exception e) {
			sLog.info("Unknown problem retrieving user for "+role+" role in group "+groupCode+" for user "+getUser(saml)+" "+e.getMessage());
		}
		return "";
	}
	/**
	 * Get the nhs numbers for the list of subject codes, in the specified project.
	 * 
	 * @param projectCode
	 * @param subjectCodes
	 * @return subjectCodes and nhs numbers
	 */
	public Map<String,String> getNhsNumbers(String projectCode, List<String> subjectCodes, String saml) 
	throws EslException {
		try {
			return client.retrieveNhsNumbers(projectCode, subjectCodes, saml);
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.getSubjectRandomisationEvents: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getSubjectRandomisationEvents: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
	}

	/**
	 * Get the result of the randomisation event for the given subject and date.
	 * 
	 * @param projectCode
	 * @param subjectCode
	 * @param date
	 * @return randomisationResult
	 */
	public String[] getRandomisationResult(String projectCode, String subjectCode, Date date, String saml) 
	throws EslException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String[] result = client.lookupRandomisationResultForDate(projectCode, subjectCode, cal, saml);
			
			return result;
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (RandomisationException e) {	
			sLog.info("Randomisation exception occurred during EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Randomisation exception occurred calling the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
	}

	
	public void lockSubject(String identifier, String saml) throws EslException {
		try{
			client.lockSubject(identifier, saml);
		}
		catch (ESLSubjectNotFoundFault e) {
			sLog.info("ESLSubjectNotFoundFault occurred calling EslClient.unlockSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslNoSubjectException("No subject found in the ESL for given identifier", e);
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
	}
	
	public void unlockSubject(String identifier, String saml) throws EslException {
		try{
			client.unlockSubject(identifier, saml);
		}
		catch (ESLSubjectNotFoundFault e) {
			sLog.info("ESLSubjectNotFoundFault occurred calling EslClient.unlockSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslNoSubjectException("No subject found in the ESL for given identifier", e);
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.unlockSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.unlockSubject: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.unlockSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service", e);
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.unlockSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
	}
	
	public void deleteSubject(String identifier, String saml) throws EslException {
		try {
			client.deleteSubject(identifier, saml);
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.deleteSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.deleteSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}		
	}
	
	public String getEslProperty(String identifier, String property, String saml) throws EslException{
		try{
			return client.getProperty(identifier, property, saml);
		}
		catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.getSubjectRandomisationEvents: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
		catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Not authorised fault occurred when connecting to the ESL service");
		}
		catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.getRandomisationResult: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		}
	}
	
	public void handleConsentWithdrawn(String identifier, String saml) throws EslException {
		try {
			client.handleConsentWithdrawn(identifier, saml);
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.handleConsentWithdrawn: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.handleConsentWithdrawn: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}		
	}
	
	public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(
			String identifier, String saml) throws EslException {
		try {
			return client.willSubjectBeDeletedWhenConsentIsWithdrawn(identifier, saml);
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.willSubjectBeDeletedWhenConsentIsWithdrawn: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.willSubjectBeDeletedWhenConsentIsWithdrawn: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
	}
	
	public IProject retrieveProjectByCode(String projectCode, String saml)
			throws EslException {
		try {
			return client.retrieveProjectByCode(projectCode, saml);
		} catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.retrieveProjectByCode: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.retrieveProjectByCode: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.retrieveProjectByCode: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.retrieveProjectByCode: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
	}
	
	public ISubject retrieveSubjectByStudyNumber(IProject project, String studyNumber, String saml)
			throws EslException {
		try {
			return client.retrieveSubjectByStudyNumber(project, studyNumber, saml);
		} catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.retrieveSubject: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (ESLSubjectLockedFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
	}
	
	public List<ISubject> findSubjectByExample(IProject project, ISubject exampleSubject, String saml) throws EslException {
		try {
			return client.findSubjectByExample(project, exampleSubject, saml);
		} catch (ConnectException e) {
			sLog.info("Connection exception occurred calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (SocketTimeoutException e) {
			sLog.info("Socket timeout exception occurred calling EslClient.retrieveSubject: "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service");
		} catch (ESLServiceFault e) {
			sLog.info("ESL Service Fault occurred calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		} catch (NotAuthorisedFault e) {
			sLog.info("Not authorised when calling EslClient.retrieveSubject: "+e.getClass().getSimpleName()+" "+e.getMessage());
			throw new EslException("Problem connecting to the ESL service", e);
		}
	}

	/**
	 * Retrieve the DN username for a given saml string
	 * 
	 * @param saml
	 * @return DN username
	 */
	private String getUser(String saml) {
		try {
			String[] list = saml.split("NameIdentifier>", 3);
			String dn = list[1].substring(0, list[1].length()-2);
			return dn;
		}
		catch (Exception e) {
			return saml; //do nothing
		}
	}
	
	/**
	 * Extract a name from the given CN string.
	 * e.g CN=CRO One, OU=users, O=psygrid, C=uk
	 * 
	 * @param cnName
	 * @return name
	 */
	private String getPrettyName(String cnName) {
		
		if (cnName == null || cnName.equals("")) {
			return cnName;
		}
		try {
			String[] a = cnName.split(",", 2);
			String[] b = a[0].split("=", 2);
			return b[1];
		}
		catch (Exception e) {
			return cnName;
		}
	}
}
