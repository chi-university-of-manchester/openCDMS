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



package org.psygrid.neden;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

//import org.psygrid.security.attributeauthority.types.GetAttributesForUserInProjectResponse;

public class InstallNEDENProject {
    public static void main (String[] args) throws Exception {

    	AAManagementClient mc = new AAManagementClient("test.properties");
		ProjectType project = new ProjectType("National EDEN", "NED", "National EDEN", "2158", false);
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

		GroupType[] allgroups = NEDGroups.allGroups();
		GroupType[] nogroups = new GroupType[]{};


		try{
			RoleType[] roles = new RoleType[]{sa, ci, pa, pm, pi, crm, cro, sro, ta, da, rm, ni};
			ProjectDescriptionType pdt = new ProjectDescriptionType(project, allgroups, roles);
			ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
			mc.getPort().addProject(pdta);
		} catch (Exception e){
			e.printStackTrace();
		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=Informatics, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{sa};
//			AttributeType pdt = new AttributeType(project, nogroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//
//		try{
//			RoleType[] roles = new RoleType[]{rm};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Joanne Ashcroft, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			boolean r = mc.getPort().deleteUser(new String[]{"CN=Natasha Posner, OU=users, O=psygrid, C=uk"});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Henna Hussain, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lloyd McDonald, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Melissa Jeffery, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Simon Prangnell, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{ci};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Helen Lester, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{ci, pi};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Max Birchwood, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Zannagh Hatton, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.lancashire_001_400, NEDGroups.lancashire_Blackpool_and_Morecambe};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//   			//mc.getPort().deleteUser(new DeleteUserRequest(new String[]{"CN=Natalie Bork, OU=users, O=psygrid, C=uk"}));
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Natalie Bork, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.lancashire_401_800, NEDGroups.lancashire_Blackpool_and_Morecambe};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//   			//mc.getPort().deleteUser(new DeleteUserRequest(new String[]{"CN=Gemma Tite, OU=users, O=psygrid, C=uk"}));
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Gemma Tite, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.norfolk};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Stephen Bradford, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.norfolk};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lawrence Howells, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.norfolk};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Freya Mellor, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sandi Secher, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Linda Benton, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jane Addison, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=James Plaistow, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Kelly Panter, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Susannah Redhead, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sara Clayton, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Maria Michail, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Laura Dawson, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.norfolk};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Melanie Millward, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.norfolk};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Annabel Ivins, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{rm};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Kelly (CRM) Panter, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.cambridge_CAMEO};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lindsay Weetman, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Kate Harris, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Alison Noble, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sarah Sullivan, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Clare Sandham, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Clare Hamlin, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sarah Wassall, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.heart_of_Birmingham_West_EIS, NEDGroups.heart_of_Birmingham_East_EIS, NEDGroups.east_PCT_Brimingham, NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Charlotte Connor, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			GroupType[] groups = new GroupType[]{NEDGroups.cornwall_001_500, NEDGroups.cornwall_501_1000};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Louisa Bolt, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lisa Sutton, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{da};
//			GroupType[] groups = new GroupType[]{NEDGroups.brimingham_South};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lesley Pearson, OU=users, O=psygrid, C=uk", null, null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{NEDGroups.lancashire_001_400, NEDGroups.lancashire_401_800, NEDGroups.lancashire_Blackpool_and_Morecambe};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null, "CN=Deela Monji, OU=users, O=psygrid, C=uk", null, null), new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
    }
}

