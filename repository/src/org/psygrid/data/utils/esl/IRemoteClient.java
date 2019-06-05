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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;

public interface IRemoteClient {

	/**
	 * Attribute authority query client
	 * 
	 * @return aaqc
	 */
	public AAQCWrapper getAaqc();

	/**
	 * Attribute authority query client
	 * 
	 * @param aaqc
	 */
	public void setAaqc(AAQCWrapper aaqc);
	
	/**
	 * Retrieve the UKCRN defined group identifier and name for
	 * the record identified by the given identifier.
	 * 
	 * @param identifier
	 * @return data
	 */
	//public String[] getGroupDetails(String identifier);
	
	/**
	 * Retrieve the UKCRN defined project identifier and acronym
	 * for a given project code
	 * 
	 * @param projectCode
	 * @return data
	 */
	public String[] getProjectDetails(String projectCode, String saml);
	
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
	public Date[] getSubjectRandomisationEvents(String projectCode, String subjectCode, String saml) throws EslException;
	
	/**
	 * Use the ESL to find out if a project uses randomisation.
	 * 
	 * @param projectCode
	 * @param saml
	 * @return isRandomised
	 * @throws EslException
	 */ 
	public boolean isProjectRandomised(String projectCode, String saml) throws EslException;
	
	/**
	 * Get the name of the user performing the specified role in the group
	 * 
	 * @param role
	 * @param groupCode
	 * @param projectCode
	 * @return username
	 */
	public String getUserInRoleForGroup(String role, String groupCode, String projectCode, String saml);

	/**
	 * Get the nhs numbers for the list of subject codes, in the specified project.
	 * 
	 * @param projectCode
	 * @param subjectCodes
	 * @return subjectCodes and nhs numbers
	 */
	public Map<String,String> getNhsNumbers(String projectCode, List<String> subjectCodes, String saml) throws EslException;

	/**
	 * Get the result of the randomisation event for the given subject and date.
	 * 
	 * @param projectCode
	 * @param subjectCode
	 * @param date
	 * @return randomisation result id and name
	 */
	public String[] getRandomisationResult(String projectCode, String subjectCode, Date date, String saml) throws EslException;
	
	/**
	 * Lock the subject.
	 * 
	 * @param identifier
	 * @param saml
	 * @throws EslException
	 */
	public void lockSubject(String identifier, String saml) throws EslException;
	
	/**
	 * Unlock the subject.
	 * 
	 * @param identifier
	 * @param saml
	 * @throws EslException
	 */
	public void unlockSubject(String identifier, String saml) throws EslException;
	
	/**
	 * Delete the subject.
	 * 
	 * @param identifier	The subject to be deleted
	 * @param saml			For authorisation checking
	 */
	public void deleteSubject(String identifier, String saml) throws EslException;
	
	/**
	 * Retrieve the value of a single ESL property for the subject
	 * with the specified identifier.
	 * 
	 * @param identifier The identifier of the subject.
	 * @param property The property to retrieve.
	 * @param saml 
	 * @return value of the property
	 */
	public String getEslProperty(String identifier, String property, String saml) throws EslException;
	
	/**
	 * Update the ESL record after consent has been withdrawn
	 * @param identifier	The record identifier
	 * @param saml			For authorisation checking
	 * @throws EslException
	 */
	public void handleConsentWithdrawn(String identifier, String saml) throws EslException;
	
	/**
	 * Check whether the ESL subject will be deleted when consent is withdrawn
	 * @param identifier	The record identifier
	 * @param saml			For authorisation checking
	 * @return				True if the subject will be deleted, false otherwise
	 * @throws EslException
	 */
	public boolean willSubjectBeDeletedWhenConsentIsWithdrawn(String identifier, String saml) throws EslException;
	
	/**
	 * Retrieve a project from its code
	 * @param projectCode
	 * @param saml
	 * @return
	 * @throws EslException
	 */
	public IProject retrieveProjectByCode(String projectCode, String saml) throws EslException;
	
	/**
	 * Retrieves a subject from the participant register
	 * @param project
	 * @param studyNumber
	 * @param saml
	 * @return
	 * @throws EslException
	 */
	public ISubject retrieveSubjectByStudyNumber(IProject project, String studyNumber, String saml) throws EslException;
	
	public List<ISubject> findSubjectByExample(IProject project, ISubject exampleSubject, String saml) throws EslException; 
}

