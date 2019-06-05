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


package org.psygrid.drn.address.test;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

/**
 * @author Rob Harper
 *
 */
public class InstallAddressProject {

    public static void main (String[] args) throws Exception {
    	AAManagementClient mc = new AAManagementClient("test.properties");

    	//TODO what is the aliasId??
    	ProjectType project = new ProjectType("ADDRESS TEST", "ADT", "ADDRESS TEST", "?", false);

    	//mc.getPort().deleteProject(new ProjectType[]{project});

    	RoleType sa = RBACRole.SystemAdministrator.toRoleType();
		RoleType ci = RBACRole.ChiefInvestigator.toRoleType();
		RoleType pm = RBACRole.ProjectManager.toRoleType();
		RoleType pi = RBACRole.PrincipalInvestigator.toRoleType();
		RoleType pa = RBACRole.ProjectAdministrator.toRoleType();
		RoleType crm = RBACRole.ClinicalResearchManager.toRoleType();
		RoleType cro = RBACRole.ClinicalResearchOfficer.toRoleType();
		RoleType sro = RBACRole.ScientificResearchOfficer.toRoleType();
		RoleType ta = RBACRole.TreatmentAdministrator.toRoleType();
		RoleType rm = RBACRole.RecruitmentManager.toRoleType();
		RoleType da = RBACRole.DataAnalyst.toRoleType();
		RoleType ni = RBACRole.NamedInvestigator.toRoleType();

		GroupType[] allgroups = ADTGroups.allGroups();
		GroupType[] nogroups = new GroupType[]{};

    	try{
    		RoleType[] roles = new RoleType[]{sa, ci, pa, pm, pi, crm, cro, sro, ta, rm, da, ni};
    		ProjectDescriptionType pdt = new ProjectDescriptionType(project, allgroups, roles);
    		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    		mc.getPort().addProject(pdta);
    	} catch (Exception e){
    		e.printStackTrace();
    	}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}


    }
}
