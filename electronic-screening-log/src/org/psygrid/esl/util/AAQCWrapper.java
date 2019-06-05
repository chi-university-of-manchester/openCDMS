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
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * This class is just a wrapper around the AAQueryClient class to make
 * it easier to use with the Spring framework.
 * 
 * @author Rob Harper
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
	
	public String getSystemSAMLAssertion() throws ConnectException, PGSecuritySAMLVerificationException,
	PGSecurityInvalidSAMLException, PGSecurityException,
	NotAuthorisedFaultMessage{
		if(null != aaqc){
			return aaqc.getSystemSAMLAssertion().toString();
		}
		
		return null;
	}
}
