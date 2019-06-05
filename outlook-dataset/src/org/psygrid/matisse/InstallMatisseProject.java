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

package org.psygrid.matisse;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;



public class InstallMatisseProject {
    public static void main (String[] args) throws Exception {
    	AAManagementClient mc = new AAManagementClient("test.properties");

    	ProjectType project = new ProjectType("Matisse", "MTS", "Matisse", "2269", false);

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

		GroupType[] allgroups = MatisseGroups.allGroups();
		GroupType[] nogroups = new GroupType[]{};

    	try{
    		RoleType[] roles = new RoleType[]{sa, ci, pa, pm, pi, crm, cro, sro, ta, rm, da, ni};
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
//			RoleType[] roles = new RoleType[]{da};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Ria", "Kalaitzaki", "erk@gprf.mrc.ac.uk", "RiaKalaitzaki", "CN=Ria Kalaitzaki, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Sakina", "Hossany", "s.hossany@imperial.ac.uk", "SakinaHossany", "CN=Sakina Hossany, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//
//
//		try{
//			RoleType[] roles = new RoleType[]{ci};
//			AttributeType pdt = new AttributeType(project, allgroups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Mike", "Crawford", "m.crawford@imperial.ac.uk", "MikeCrawford", "CN=Mike Crawford, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_London};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Sue", "Paterson", "sue.patterson@imperial.ac.uk", "SuePaterson", "CN=Sue Paterson, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_London};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Emese", "Csipke", "e.csipke@imperial.ac.uk", "EmeseCsipke", "CN=Emese Csipke, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_London};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Kofi", "Kramo", "k.kramo@imperial.ac.uk", "KofiKramo", "CN=Kofi Kramo, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.camden_islington};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Helen", "Killaspy", "h.killaspy@medsch.ucl.ac.uk", "HelenKillaspy", "CN=Helen Killaspy, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.camden_islington};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Angela", "Hoadley", "a.hoadley@medsch.ucl.ac.uk", "AngelaHoadely", "CN=Angela Hoadely, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.camden_islington};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Ben", "Reece", "b.reece@imperial.ac.uk", "BenReece", "CN=Ben Reece, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_England};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Tony", "Soteriou", "Tony.Soteriou@awp.nhs.uk", "TonySoteriou", "CN=Tony Soteriou, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_England};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Siobhan", "Floyd", "pssswf@bath.ac.uk", "SiobhanFloyd", "CN=Siobhan Floyd, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.west_England};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Elizabeth", "Steyert", "elizabeth.steyert@bristol.ac.uk", "ElizabethSteyert", "CN=Elizabeth Steyert, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.belfast};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("Tony", "ONeill", "tony.oneill@qub.ac.uk", "TonyONeill", "CN=Tony ONeill, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		try{
//			RoleType[] roles = new RoleType[]{crm, cro};
//			GroupType[] groups = new GroupType[]{MatisseGroups.belfast};
//			AttributeType pdt = new AttributeType(project, groups, roles);
//			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("John", "Dinsmore", "j.dinsmore@qub.ac.uk", "JohnDinsmore", "CN=John Dinsmore, OU=users, O=psygrid, C=uk", "173913", null),  new AttributeType[]{pdt});
//			boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//			System.out.println(r);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
    }
}

