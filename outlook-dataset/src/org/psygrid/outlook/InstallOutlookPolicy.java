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

package org.psygrid.outlook;

import java.util.List;

import org.psygrid.security.DefaultPolicy;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 *
 */
public class InstallOutlookPolicy {

	public static void main(String[] args){


		GroupType[] groups = OLKGroups.allGroups();

		PolicyDescriptionType pdt = new PolicyDescriptionType();
		pdt.setActions(RBACAction.allActions());
		TargetType[] tta = new TargetType[OLKGroups.allAsTargets().length+RBACTarget.allAsTargets().length];
		for (int i = 0; i < OLKGroups.allAsTargets().length; i++) {
			tta[i] = OLKGroups.allAsTargets()[i];
		}
		int j=0;
		for (int i = OLKGroups.allAsTargets().length; i < OLKGroups
				.allAsTargets().length
				+ RBACTarget.allAsTargets().length; i++, j++) {
			tta[i] = RBACTarget.allAsTargets()[j];
		}
		pdt.setTargets(tta);

		PrivilegeType[] prta = new PrivilegeType[OLKGroups.allAsPrivileges().length
				+ RBACRole.allAsPrivileges().length];
		for (int i = 0; i < OLKGroups.allAsPrivileges().length; i++) {
			prta[i] = OLKGroups.allAsPrivileges()[i];
		}
		int k=0;
		for (int i = OLKGroups.allAsPrivileges().length; i < OLKGroups
				.allAsPrivileges().length
				+ RBACRole.allAsPrivileges().length; i++, k++) {
			prta[i] = RBACRole.allAsPrivileges()[k];
		}
		pdt.setPrivileges(prta);


		try {
			PAManagementClient mc = new PAManagementClient("test.properties");

			/*
			 * Uncomment to delete the policy first
			 */

			try {
				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
						null, new ArgumentType[] {
								new ArgumentType(RBACRole.SystemAdministrator.toPrivilegeType(), true) });
				StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
						RBACAction.ANY.toActionType(), rule);
				StatementType[] sta = new StatementType[] { st };
				PolicyType pt = new PolicyType("Outlook", "OLK", pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				mc.getPort().deletePolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			/*
			 * Be careful about who you make a sys admin!
			 */
			try {
				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
						null, new ArgumentType[] {
								new ArgumentType(RBACRole.SystemAdministrator.toPrivilegeType(), true) });
				StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
						RBACAction.ANY.toActionType(), rule);
				StatementType[] sta = new StatementType[] { st };
				PolicyType pt = new PolicyType("Outlook", "OLK", pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				//Must always use addPolicy first time
				mc.getPort().addPolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			List<StatementType> lst = DefaultPolicy.buildStatements(groups);

            try {
				System.out.println("Statements = " + lst.size());
				StatementType[] sta = new StatementType[lst.size()];
				int i = 0;
				for (StatementType st : lst) {
					sta[i] = st;
					i++;
				}
				PolicyType pt = new PolicyType("Outlook", "OLK", pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				mc.getPort().addStatementToPolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} catch (PGSecurityException pgse) {
			System.out.println(pgse.getMessage());
			pgse.printStackTrace();
		}
	}
}