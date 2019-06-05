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

//Created on May 6 2008 by John Ainsworth

package org.psygrid.security;

import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

//import org.psygrid.security.attributeauthority.types.GetAttributesForUserInProjectResponse;

public class SystemProject {
	public static void insert(AAManagementClient mc, String first, String last, String email, String uid, String dn, char[] password) throws Exception {

		try {
			RoleType sa = new RoleType("SystemAdministrator", null);
			RoleType pm = new RoleType("ProjectManager", null);
			RoleType sp = RBACRole.StudyPatcher.toRoleType();
			RoleType[] roles = new RoleType[] { sa, pm, sp };
			ProjectType project = new ProjectType("SYSTEM", "-1", null, null,
					true);
			GroupType[] groups = new GroupType[] {};
			ProjectDescriptionType pdt = new ProjectDescriptionType(project,
					groups, roles);
			ProjectDescriptionType[] pdta = new ProjectDescriptionType[] { pdt };
			boolean r = mc.getPort().addProject(pdta);

			AttributeType adt = new AttributeType(project, new GroupType[]{}, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(first, last, email, uid, dn, password.toString(), null),  new AttributeType[]{adt});
			r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void insert(AAManagementClient mc) throws Exception {

		try {
			RoleType sa = new RoleType("SystemAdministrator", null);
			RoleType pm = new RoleType("ProjectManager", null);
			RoleType sp = RBACRole.StudyPatcher.toRoleType();
			RoleType[] roles = new RoleType[] { sa, pm, sp };
			ProjectType project = new ProjectType("SYSTEM", "-1", null, null,
					true);
			GroupType[] groups = new GroupType[] {};
			ProjectDescriptionType pdt = new ProjectDescriptionType(project,
					groups, roles);
			ProjectDescriptionType[] pdta = new ProjectDescriptionType[] { pdt };
			boolean r = mc.getPort().addProject(pdta);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
