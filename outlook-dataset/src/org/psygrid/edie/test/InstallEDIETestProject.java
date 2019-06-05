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



package org.psygrid.edie.test;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;


public class InstallEDIETestProject {
    public static void main (String[] args) throws Exception {

    	AAManagementClient mc = new AAManagementClient("test.properties");
		ProjectType project = new ProjectType("EDIE Test", "EDT", "EDIE-2", "2171", false);
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

		GroupType[] allgroups = EDTGroups.allGroups();

		GroupType[] nogroups = new GroupType[]{};

//		try{
//			mc.getPort().deleteProject(new DeleteProjectRequest(new ProjectType[]{project}));
//		} catch (Exception e){
//			e.printStackTrace();
//		}

    	try{
    		RoleType[] roles = new RoleType[]{sa, ci, pa, pm, pi, crm, cro, sro, ta, rm, da, ni};
    		ProjectDescriptionType pdt = new ProjectDescriptionType(project, allgroups, roles);
    		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    		mc.getPort().addProject(pdta);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
  /*  	try {
			RoleType[] roles = new RoleType[] { sa };
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,
					"CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", null, null),
					new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			RoleType[] roles = new RoleType[] { sa };
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,
					"CN=Robert Harper, OU=Informatics, O=PsyGrid, C=UK", null, null),
					new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			RoleType[] roles = new RoleType[] { sa };
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,
					"CN=Lucy Bridges, OU=Informatics, O=PsyGrid, C=UK", null, null),
					new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			RoleType[] roles = new RoleType[] { sa };
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,
					"CN=Pauline Whelan, OU=Informatics, O=PsyGrid, C=UK", null, null),
					new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{sa};
			AttributeType pdt = new AttributeType(project, nogroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{crm, rm};
			GroupType[] groups = allgroups;
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CPM One, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ci};
			GroupType[] groups = allgroups;
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CI One, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{pi};
			GroupType[] groups = allgroups;
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=PI One, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{cro};
			GroupType[] groups = new GroupType[]{EDTGroups.manchester};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CRO One, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{cro};
			GroupType[] groups = new GroupType[]{EDTGroups.birmingham};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CRO Two, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{cro};
			GroupType[] groups = new GroupType[]{EDTGroups.cambridge};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CRO Three, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{cro};
			GroupType[] groups = new GroupType[]{EDTGroups.eastAnglia};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CRO Four, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{cro};
			GroupType[] groups = new GroupType[]{EDTGroups.glasgow};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=CRO Five, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ta};
			GroupType[] groups = new GroupType[]{EDTGroups.manchester};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=TA One, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ta};
			GroupType[] groups = new GroupType[]{EDTGroups.birmingham};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=TA Two, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ta};
			GroupType[] groups = new GroupType[]{EDTGroups.cambridge};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=TA Three, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ta};
			GroupType[] groups = new GroupType[]{EDTGroups.eastAnglia};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=TA Four, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}
		try{
			RoleType[] roles = new RoleType[]{ta};
			GroupType[] groups = new GroupType[]{EDTGroups.glasgow};
			AttributeType pdt = new AttributeType(project, groups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=TA Five, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
			System.out.println(r);
		} catch (Exception e){
			e.printStackTrace();
		}*/
    }
}

