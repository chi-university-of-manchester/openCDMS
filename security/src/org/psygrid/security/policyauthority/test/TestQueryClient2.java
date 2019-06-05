package org.psygrid.security.policyauthority.test;

import org.opensaml.SAMLAssertion;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class TestQueryClient2 {

	static PAQueryClient paqc;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		*/
		SAMLAssertion sa;
		
		try {
			paqc = new PAQueryClient(
					"test.properties");
			AAQueryClient qc2 = new AAQueryClient(
					"test.properties");
			System.out.println("starting.....");
			
			//We need to get a SAML for a user who PA who IS a group member, and to get a SAML
			//for one who isn't a group member. The email action we're checking is ACTION_DR_INVESTIGATOR_REPORT.
			
			//The outcome is that the one in the group SHOULD have the privilege but the one who isn't in the group
			//Should not. We're checking for when the Group (or target) passed is all null properties.
			
			//'CN=PI One, OU=users, O=openCDMS Virtualized, C=UK' belongs to group 001001
			//'CN=PI Two, OU=users, O=openCDMS Virtualized, C=UK' belongs to group 002001
			
			//By the same token, confirmation that the bug has been recreated can be established if we revert back to
			//the security 1.7.8 (without the proposed fix) and see if both users return true.
			
			sa = qc2.getSAMLAssertion("CN=PI One, OU=users, O=openCDMS Virtualized, C=UK");
			System.out.println(sa);
			
			boolean answer = paqc.getPort().makePolicyDecision(new ProjectType(null, "NFSWTC", null, null, false), new TargetType(null, null), RBACAction.ACTION_DR_INVESTIGATOR_REPORT.toActionType(), sa.toString());
			System.out.println(answer ? "Privilege WAS granted" : "Privilege was NOT granted");
			
		}catch(Exception e){
			
		}
	}

}
