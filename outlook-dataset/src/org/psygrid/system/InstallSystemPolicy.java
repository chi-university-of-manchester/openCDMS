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


//Created on Nov 10, 2005 by John Ainsworth

package org.psygrid.system;

import org.psygrid.security.PGSecurityException;
import org.psygrid.security.SystemPolicy;
import org.psygrid.security.policyauthority.client.PAManagementClient;

/**
 * @author jda
 *
 */
public class InstallSystemPolicy {

	public static void main(String[] args) {

		try {
			PAManagementClient mc = new PAManagementClient("test.properties");
			SystemPolicy.insert(mc);
		} catch (PGSecurityException pgse) {
			System.out.println(pgse.getMessage());
		}
	}

}