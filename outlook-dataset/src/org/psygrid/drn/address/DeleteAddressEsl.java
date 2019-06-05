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


package org.psygrid.drn.address;

import org.opensaml.SAMLAssertion;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.services.client.EslClient;

/**
 * @author Rob Harper
 *
 */
public class DeleteAddressEsl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try{
			SAMLAssertion sa = InstallAddressEsl.login(args);
			String saml = sa.toString();

			EslClient client = new EslClient();
			IProject p = client.retrieveProjectByCode("ADD", saml);

			client.deleteProject(p.getId(), p.getProjectCode(), saml);

		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
