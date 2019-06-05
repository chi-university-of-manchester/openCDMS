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

package org.psygrid.edie;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

public class InstallEDIEProject {
	public static void main(String[] args) throws Exception {

		AAManagementClient mc = new AAManagementClient("test.properties");

		ProjectType project = new ProjectType("EDIE 2", "ED2", "EDIE-2",
				"2171", false);

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

		GroupType[] allgroups = ED2Groups.allGroups();

		GroupType[] nogroups = new GroupType[] {};

		try {
			RoleType[] roles = new RoleType[] { sa, ci, pa, pm, pi, crm, cro,
					sro, ta, rm, da, ni };
			ProjectDescriptionType pdt = new ProjectDescriptionType(project,
					allgroups, roles);
			ProjectDescriptionType[] pdta = new ProjectDescriptionType[] { pdt };
			mc.getPort().addProject(pdta);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Robert Harper, OU=Informatics, O=PsyGrid, C=UK", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Lucy Bridges, OU=Informatics, O=PsyGrid, C=UK", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Pauline Whelan, OU=Informatics, O=PsyGrid, C=UK", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=John Ainsworth, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Robert Harper, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Lucy Bridges, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { sa };
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Pauline Whelan, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { crm, ta, rm };
//			GroupType[] groups = allgroups;
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Suzanne Kaiser, OU=users, O=psygrid, C=uk", null,
//					"07906627549"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ci };
//			GroupType[] groups = allgroups;
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Tony Morrison, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.glasgow };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Andrew Gumley, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.birmingham };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Max Birchwood, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.eastAnglia };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=David Fowler, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.cambridge };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Peter Jones, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi, ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Paul French, OU=users, O=psygrid, C=uk", null,
//					"07768202276"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Graham Dunn, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Shon Lewis, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Richard Bental, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pi };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Linda Davies, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Hannah Taylor, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null, "CN=D R N, OU=users, O=psygrid, C=uk",
//					null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Melissa Wardle, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.birmingham };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Danielle Oliver, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.birmingham };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Sarah Wassall, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.eastAnglia };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Sarah Fish, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.eastAnglia };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Stephen Bradford, OU=users, O=psygrid, C=uk", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.cambridge };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Carolyn Crane, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.cambridge };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Jessica Nagar, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.glasgow };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Louise Jackson, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { cro };
//			GroupType[] groups = new GroupType[] { ED2Groups.glasgow };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Maria Gardani, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Aoiffe Kilcommons, OU=users, O=psygrid, C=uk", null,
//					"07768150389"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Sophie Parker, OU=users, O=psygrid, C=uk", null,
//					"07767755790"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.birmingham };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Katerine Brunet, OU=users, O=psygrid, C=uk", null,
//					"07985883444"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.eastAnglia };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Tony Reilly, OU=users, O=psygrid, C=uk", null,
//					"07917167209"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.eastAnglia };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Rebbeca Rollinson, OU=users, O=psygrid, C=uk", null,
//					null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.glasgow };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Katie Mackie, OU=users, O=psygrid, C=uk", null,
//					"07974433219"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.cambridge };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Jennie Conroy, OU=users, O=psygrid, C=uk", null,
//					"07834335044"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pm };
//			GroupType[] groups = new GroupType[] { ED2Groups.birmingham };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Paul Patterson, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { pm };
//			GroupType[] groups = new GroupType[] { ED2Groups.cambridge };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Graham Murray, OU=users, O=psygrid, C=uk", null, null),
//					new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { ta };
//			GroupType[] groups = new GroupType[] { ED2Groups.manchester };
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null,
//					null, null, null,
//					"CN=Clare McAlister, OU=users, O=psygrid, C=uk", null,
//					"07799072103"), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			RoleType[] roles = new RoleType[] { rm };
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(
//					new UserType(null, null, null, null,
//							"CN=Joanne Ashcroft, OU=users, O=psygrid, C=uk",
//							null, null), new AttributeType[] { pdt });
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
//			System.out.println(r);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
