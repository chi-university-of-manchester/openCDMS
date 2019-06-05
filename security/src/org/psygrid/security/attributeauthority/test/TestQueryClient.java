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



package org.psygrid.security.attributeauthority.test;

import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;

/**
 * @author jda
 *
 */
public class TestQueryClient {
    public static void main (String[] args) {
		SAMLAssertion sa;
		try{
			//System.setProperty("javax.net.debug", "ssl,handshake");
			AAQueryClient qc = new AAQueryClient("test.properties");
			System.out.println("starting.....");
			sa = qc.getSAMLAssertion(null, null);
			sa = qc.getSystemSAMLAssertion();
			sa = qc.getSAMLAssertion(null,null);
//			System.out.println(sa.toString());
//			sa = qc.getSAMLAssertion(new ProjectType(null,"OLK", null, null, true));
//			System.out.println(sa.toString());
			


			String[] lggt = qc.getUsersInGroupInProject(new ProjectType("Outlook", "OLK",  null, null,true), new GroupType(null,"007001", null));
			for(int i = 0; i<lggt.length;i++){
				System.out.println("getUsersInGroupInProject "+lggt[i]);
			}	
			String[] lggt2 = qc.getUsersInGroupInProjectWithRole(new ProjectType("Outlook", "OLK",  null, null,true), 
					new GroupType(null,"007001", null),
					new RoleType("ClinicalResearchOfficer", null));
			for(int i = 0; i<lggt2.length;i++){
				System.out.println("getUsersInGroupInProjectWithRole "+lggt2[i]);
			}	
			String[] lggt3 = qc.getUsersInProjectWithRole(new ProjectType("Outlook", "OLK",  null, null,true), 
					new RoleType("ClinicalResearchOfficer", null));
			for(int i = 0; i<lggt3.length;i++){
				System.out.println("getUserInProjectWithRole "+lggt3[i]);
			}
			String[] lggt4 = qc.getUsersInProject(new ProjectType("Outlook", "OLK",  null, null,true));
			for(int i = 0; i<lggt4.length;i++){
				System.out.println("getUserInProject "+lggt4[i]);
			}
			GroupType[] g = qc.getGroupsInProject(new ProjectType("Outlook", "OLK",  null, null,true));
			for(int i = 0; i<g.length;i++){
				System.out.println("getGroupsInProject "+g[i].getName());
			}
			RoleType[] r = qc.getRolesInProject(new ProjectType("Outlook", "OLK",  null, null,true));
			for(int i = 0; i<r.length;i++){
				System.out.println("getGroupsInProject "+r[i].getName());
			}
//			qc.getMyProjects();
//			qc.getMyRolesInProject(new ProjectType("Outlook", "OLK",  null, null,true));
//			qc.getMyGroupsInProject(new ProjectType("Outlook", "OLK",  null, null,true));
			//qc.postProcessLogin("xxx");
			qc.recordLoginAttempt("rob", true, new Date(), "0.0.0.0", null);
			
			List<InternetAddress> lia = qc.lookUpEmailAddress(new ProjectType("Outlook", "OLK",  null, null,true), null, null);
			for(InternetAddress a : lia){
				System.out.println(a.getAddress());
			}
			
			System.out.println(qc.lookUpEmailAddress("CN=John Ainsworth, OU=users, O=psygrid, C=uk").toString());
			System.out.println(qc.lookUpMobileNumber("CN=John Ainsworth, OU=users, O=psygrid, C=uk").toString());
		} catch (PGSecurityException pgse){
			pgse.printStackTrace();
			System.out.println(pgse.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}