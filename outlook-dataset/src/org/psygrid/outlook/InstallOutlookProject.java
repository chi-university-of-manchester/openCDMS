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



package org.psygrid.outlook;

import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

public class InstallOutlookProject {
    public static void main (String[] args) throws Exception {

    	AAManagementClient mc = new AAManagementClient("test.properties");

    	ProjectType project = new ProjectType("Outlook", "OLK", "PsyGrid", "2146", false);

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

		GroupType[] allgroups = OLKGroups.allGroups();
		GroupType[] nogroups = new GroupType[]{};

    	try{
    		RoleType[] roles = new RoleType[]{sa, ci, pa, pm, pi, crm, cro, sro, ta, rm, da, ni};
    		ProjectDescriptionType pdt = new ProjectDescriptionType(project, allgroups, roles);
    		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    		mc.getPort().addProject(pdta);
    		//mc.getPort().deleteProject(new ProjectType[]{project});
    	} catch (Exception e){
    		e.printStackTrace();
    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=Informatics, O=PsyGrid, C=UK", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=Informatics, O=PsyGrid, C=UK", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lucy Bridges, OU=Informatics, O=PsyGrid, C=UK", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=Informatics, O=PsyGrid, C=UK", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
    	try{
    		RoleType[] roles = new RoleType[]{sa};
    		AttributeType pdt = new AttributeType(project, allgroups, roles);
    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType("John", "Ainsworth", null, null,"CN=John Ainsworth, OU=users, O=PsyGrid, C=UK", null, null),  new AttributeType[]{pdt});
    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
    		//boolean r = mc.getPort().deleteUser(new String[]{"CN=John Ainsworth, OU=users, O=psygrid, C=uk"});
    		System.out.println(r);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Pauline Whelan, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{ci, pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northWest_ManchesterMentalHealthandSocialCareTrust, OLKGroups.northWest_BoltonSalfordandTraffordMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Shon Lewis, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northWest_ManchesterMentalHealthandSocialCareTrust, OLKGroups.northWest_BoltonSalfordandTraffordMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Max Marshall, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust, OLKGroups.eastAnglia_CambridgeCAMEO, OLKGroups.eastAnglia_PeterbroughNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Peter Jones, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Max Birchwood, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
////  		RoleType[] roles = new RoleType[]{pi};
////  		GroupType[] groups = new GroupType[]{Groups.bristolAvon_AvonandWiltshireMentalHealthPartnership};
////  		AttributeType pdt = new AttributeType(project, groups, roles);
////  		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Glynn Harrison, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//
//    		boolean r = mc.getPort().deleteUser(new String[]{"CN=Glynn Harrison, OU=users, O=psygrid, C=uk"});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastMidlands_NottinghamshireHealthcareNHSTrust, OLKGroups.eastMidlands_LincolnshirePartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Peter Liddle, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastMidlands_NottinghamshireHealthcareNHSTrust, OLKGroups.eastMidlands_LincolnshirePartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Hugh Middleton, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_SouthWestLondonandStGeorgesTrust, OLKGroups.northLondon_CentralandWestLondonTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Tom Barnes, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.southLondon_SouthLondonandMaudselyTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Til Wykes, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.southLondon_SouthLondonandMaudselyTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robin Murray, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=John Ainsworth, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{sa};
//    		AttributeType pdt = new AttributeType(project, nogroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Robert Harper, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//
//    	try{
//    		RoleType[] roles = new RoleType[]{crm, rm};
//    		AttributeType pdt = new AttributeType(project, allgroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Helen Roberts, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{pm};
//    		AttributeType pdt = new AttributeType(project, allgroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Amanda Hughes, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.bristolAvon_AvonandWiltshireMentalHealthPartnership};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Alison Noble, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro, pi};
//    		GroupType[] groups = new GroupType[]{OLKGroups.bristolAvon_AvonandWiltshireMentalHealthPartnership};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sarah Sullivan, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.bristolAvon_AvonandWiltshireMentalHealthPartnership};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Clare Sandham, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Luise Poustka, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Luise Poustka, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Ursula Werners, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Ursula Werners, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Susannah Redhead, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Susannah Redhead, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sandi Secher, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sandi Secher, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO, OLKGroups.eastAnglia_PeterbroughNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jessica Nagar, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastMidlands_NottinghamshireHealthcareNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Florence Grindlay, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastMidlands_LincolnshirePartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Florence Grindlay, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northEast_NewcastleNorthumberlandandNorthTynesideMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Suzanne Humphrey, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northEast_NewcastleNorthumberlandandNorthTynesideMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Anna Massey, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northEast_NewcastleNorthumberlandandNorthTynesideMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sylvia Ruttledge, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_SouthWestLondonandStGeorgesTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sasha Gold, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_CentralandWestLondonTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sasha Gold, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_SouthWestLondonandStGeorgesTrust, OLKGroups.northLondon_CentralandWestLondonTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jayde Flynn, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northWest_ManchesterMentalHealthandSocialCareTrust, OLKGroups.northWest_BoltonSalfordandTraffordMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Anita Davies, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.southLondon_SouthLondonandMaudselyTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jana Advani, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.southLondon_SouthLondonandMaudselyTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Teuta Rexhepi, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Kate Harris, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Henna Hussain, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Kelly Panter, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lloyd McDonald, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Melissa Jeffery, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Simon Prangnell, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO, OLKGroups.eastAnglia_PeterbroughNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lindsay Weetman, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_CambridgeCAMEO, OLKGroups.eastAnglia_PeterbroughNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jane Addison, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Melanie Millward, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Annabel Ivins, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Stephen Bradford, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Lawrence Howells, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Freya Mellor, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Angela Brown, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	//////////////
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_PeterbroughNHSTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Katherine Lawrence, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_SouthWestLondonandStGeorgesTrust, OLKGroups.northLondon_CentralandWestLondonTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jo Hart, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Clare Hamlin, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Sarah Wassall, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.westMidlands_BirminghamandSolihullMentalHealthTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Charlotte Connor, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.bristolAvon_AvonandWiltshireMentalHealthPartnership};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Louisa Bolt, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northLondon_CentralandWestLondonTrust, OLKGroups.northLondon_SouthWestLondonandStGeorgesTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Jade Flynn, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{cro};
//    		GroupType[] groups = new GroupType[]{OLKGroups.eastAnglia_NorfolkandWaveneyMentalHealthPartnershipTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Angela Brown, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{da};
//    		GroupType[] groups = new GroupType[]{OLKGroups.northWest_ManchesterMentalHealthandSocialCareTrust};
//    		AttributeType pdt = new AttributeType(project, groups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null,"CN=Richard Jones, OU=users, O=psygrid, C=uk", null),  new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
//    	try{
//    		RoleType[] roles = new RoleType[]{rm};
//    		AttributeType pdt = new AttributeType(project, allgroups, roles);
//    		UserPrivilegesType urgt = new UserPrivilegesType(new UserType(null, null, null, null, "CN=Joanne Ashcroft, OU=users, O=psygrid, C=uk", null), new AttributeType[]{pdt});
//    		boolean r = mc.getPort().addUser(new UserPrivilegesType[]{urgt});
//    		System.out.println(r);
//    	} catch (Exception e){
//    		e.printStackTrace();
//    	}
	}
}

