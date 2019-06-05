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

package org.psygrid.security.attributeauthority.test;

import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

//import org.psygrid.security.attributeauthority.types.GetAttributesForUserInProjectResponse;

public class TestManagementClient {
	public static void main(String[] args) throws Exception {

		AAManagementClient mc = new AAManagementClient("test.properties");
		ProjectType project = new ProjectType("Outlook", "OLK", null, null,
				false);
		RoleType sa = new RoleType("SystemAdministrator", null);
		RoleType ci = new RoleType("ChiefInvestigator", null);
		RoleType pm = new RoleType("ProjectManager", null);
		RoleType pi = new RoleType("PrincipalInvestigator", null);
		RoleType pa = new RoleType("ProjectAdministrator", null);
		RoleType crm = new RoleType("ClinicalResearchManager", null);
		RoleType cro = new RoleType("ClinicalResearchOfficer", null);
		RoleType sro = new RoleType("ScientificResearchOfficer", null);
		RoleType ta = new RoleType("TreatmentAdministrator", null);
		GroupType gt1 = new GroupType(
				"Bristol Avon-Avon and Wiltshire Mental Health Partnership",
				"001001", "Outlook");
		GroupType gt2 = new GroupType(
				"East Anglia-Norfolk and Waveney Mental Health Partnership Trust",
				"002001", "Outlook");
		GroupType gt3 = new GroupType(
				"East Anglia-Cambridge and Peterbrough NHS Trust", "002002",
				"Outlook");
		GroupType gt4 = new GroupType(
				"East Midlands-Nottinghamshire Healthcare NHS Trust", "003001",
				"Outlook");
		GroupType gt5 = new GroupType(
				"East Midlands-Lincolnshire Partnership Trust", "003002",
				"Outlook");
		GroupType gt6 = new GroupType(
				"North East-Newcastle, Northumberland and North Tyneside Mental Health Trust",
				"004001", "Outlook");
		GroupType gt7 = new GroupType(
				"North London-South West London and St. Georges Trust",
				"005001", "Outlook");
		GroupType gt8 = new GroupType(
				"North London-Central and West London Trust", "005002",
				"Outlook");
		GroupType gt9 = new GroupType(
				"North West-Manchester Mental Health and Social Care Trust",
				"006001", "Outlook");
		GroupType gt10 = new GroupType(
				"South London-South London and Maudsely Trust", "007001",
				"Outlook");
		GroupType gt11 = new GroupType(
				"West Midlands-Birmingham and Solihull Mental Health Trust",
				"008001", "Outlook");
		GroupType gt12 = new GroupType(
				"Fictitious Hub-Made up Mental Health Trust", "009001",
				"Outlook");
		GroupType gt13 = new GroupType("Fictitious Hub-Fake Hospital",
				"009002", "Outlook");
		GroupType[] allgroups = new GroupType[] { gt1, gt2, gt3, gt4, gt5, gt6,
				gt7, gt8, gt9, gt10, gt11, gt12, gt13 };
		GroupType[] nogroups = new GroupType[] {};

		try {
			RoleType[] roles = new RoleType[] { sa, ci, pa, pm, pi, crm, cro,
					sro, ta };
			ProjectDescriptionType pdt = new ProjectDescriptionType(project,
					allgroups, roles);
			ProjectDescriptionType[] pdta = new ProjectDescriptionType[] { pdt };
			mc.getPort().addProject(pdta);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try {
			UserType ut = mc.getPort().getUserFromLDAP("CN=CRO Yes, OU=users, O=psygrid, C=uk");
			System.out.println(ut.getDistinguishedName()+" "+ut.getEmailAddress()+" "+ut.getUserId()
					+" "+ut.getLastName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "me@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().deleteUser(
					new String[] { "CN=CRO No, OU=users, O=psygrid, C=uk" });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"Y", "me@example.com", "CROY", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"Yes", "me@example.com", "CROYes", "CN=CRO Yes, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println(mc.getPort().getCNFromUID("CRONo"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		try {
			System.out.println(mc.getPort().getUIDFromCN(
					"CRO No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().userExists("CRO", "No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] s = mc.getPort().multiUserExists("CRO", "");
			for(String x :s){
				System.out.println(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "me@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().deleteProjectFromUser(
					new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println(mc.getPort().getCNFromUID("CRONo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().getUIDFromCN(
					"CN=CRO No, OU=users, O=psygrid, C=UK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().userExists("CRO", "No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] s = mc.getPort().multiUserExists("CRO", "");
			for(String x :s){
				System.out.println(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "me@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println(mc.getPort().getCNFromUID("CRONo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().getUIDFromCN(
					"CN=CRO No, OU=users, O=psygrid, C=UK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().userExists("CRO", "No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] s = mc.getPort().multiUserExists("CRO", "");
			for(String x :s){
				System.out.println(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "me@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().deleteUser(
					new String[] { "CN=CRO No, OU=users, O=psygrid, C=uk" });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println(mc.getPort().getCNFromUID("CRONo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().getUIDFromCN(
					"CN=CRO No, OU=users, O=psygrid, C=UK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().userExists("CRO", "No"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] s = mc.getPort().multiUserExists("CRO", "");
			for(String x :s){
				System.out.println(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.out.println(mc.getPort().getUsersAndPrivilegesInProject(
					project));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mc.getPort().getUser("CN=CRO No, OU=users, O=psygrid, C=uk");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "me@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			boolean r = mc.getPort().addUser(new UserPrivilegesType[] { urgt });
			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			mc.getPort().getUser("CN=CRO No, OU=users, O=psygrid, C=uk");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			RoleType[] roles = new RoleType[] { cro };
			AttributeType pdt = new AttributeType(project, allgroups, roles);
			UserPrivilegesType urgt = new UserPrivilegesType(new UserType("CRO",
					"No", "crono@example.com", "CRONo", "CN=CRO No, OU=users, O=psygrid, C=uk",
					"173913", null), new AttributeType[] { pdt });
			System.out.println(mc.getPort().updateUser(
					new UserPrivilegesType[] { urgt }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(mc.getPort().getUsersAndPrivilegesInProject(project));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mc.getPort().getVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=John Ainsworth,
		// OU=Informatics, O=PsyGrid, C=UK", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=David Nuttall,
		// OU=Informatics, O=PsyGrid, C=UK", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Robert Harper,
		// OU=Informatics, O=PsyGrid, C=UK", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project,allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Ismael Juma,
		// OU=Informatics, O=PsyGrid, C=UK", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{ci, pi};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Shon Lewis,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=John Ainsworth,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=David Nuttall,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Robert Harper,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{sa};
		// AttributeType pdt = new AttributeType(project,allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Ismael Juma,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Helen Roberts,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{pm};
		// AttributeType pdt = new AttributeType(project, allgroups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Amanda Hughes,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=Test User,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CRO One,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CRO Two,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CRO Three,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CRO Four,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{cro};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CRO Five,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CPM One,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CPM Two,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CPM Three,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CPM Four,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		// System.out.println(r);
		// } catch (Exception e){
		// e.printStackTrace();
		// }
		// try{
		// RoleType[] roles = new RoleType[]{crm};
		// GroupType[] groups = new GroupType[]{gt12, gt13};
		// AttributeType pdt = new AttributeType(project, groups, roles);
		// UserPrivilegesType urgt = new UserPrivilegesType("CN=CPM Five,
		// OU=users, O=psygrid, C=uk", new AttributeType[]{pdt});
		// boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
		//			System.out.println(r);	
		//		} catch (Exception e){
		//			e.printStackTrace();
		//		}
	}
}
