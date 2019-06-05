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

package org.psygrid.web.helpers;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.web.beans.UserBean;
import org.psygrid.web.details.PsygridUserDetails;
import org.psygrid.web.repository.ReportingClient;
import org.psygrid.web.repository.RepositoryFault;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Helper methods for the Audit Log pages.
 * 
 * @author Rob Harper
 *
 */
public class AuditHelper {

	/**
	 * Create a list of all users in a project, with an additional list item
	 * for 'none'.
	 * 
	 * @param aaqc Attribute Authority Query Client
	 * @param project The project
	 * @return User list.
	 * @throws PGSecurityException
	 * @throws ConnectException
	 * @throws NotAuthorisedFaultMessage
	 */
	public static List<UserBean> createUserList(AAQueryClient aaqc, ProjectType project) 
			throws PGSecurityException, ConnectException, NotAuthorisedFaultMessage{
		String[] users = aaqc.getUsersInProject(project);
		List<UserBean> userList = new ArrayList<UserBean>();
		userList.add(new UserBean("--none--", "--none--"));
		for ( String user: users ){
			userList.add(new UserBean(user));
		}
		return userList;
	}
	
	/**
	 * Create a list of all identifiers for a project, with an additional
	 * list item for 'none'.
	 * 
	 * @param projectCode The project
	 * @return The list of identifiers.
	 */
	public static List<String> createIdentifierList(ProjectType project, PsygridUserDetails user) throws RepositoryFault {

		ReportingClient client = new ReportingClient();
		List<String> ids = client.getIdentifiers(project.getIdCode(), SamlHelper.getSaAsString(user.getSaml()));
		ids.add(0, "--none--");
		return ids;
	}

}
