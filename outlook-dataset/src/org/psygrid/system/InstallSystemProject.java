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


//Created on Oct 12, 2005 by John Ainsworth



package org.psygrid.system;

import org.psygrid.security.SystemProject;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

public class InstallSystemProject {
    public static void main (String[] args) throws Exception {

    	AAManagementClient mc = new AAManagementClient("test.properties");
    		try{
    			SystemProject.insert(mc);
    		} catch (Exception e){
    			e.printStackTrace();
    		}

		try{
			RoleType sa =  new RoleType("SystemAdministrator", null);
 			RoleType  pm =  new RoleType("ProjectManager", null);
			RoleType[] roles = new RoleType[]{sa, pm};
			ProjectType project = new ProjectType("SYSTEM", "-1",null, null, true);
			AttributeType pdt = new AttributeType(project, new GroupType[]{}, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("John", "Ainsworth", "support@psygrid.org", "JohnAinsworth", "CN=John Ainsworth, OU=users, O=PsyGrid, C=UK", "password", null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}

    }
}

