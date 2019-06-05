/*
Copyright (c) 2008, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
 */

package org.psygrid.del;

import java.util.List;

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
 * Install the policies for the data-element-library.
 * 
 * args[0] should be the name of the class implementing DELProject and
 * providing the groups for the project to be installed.
 * 
 * @author Lucy Bridges
 *
 */
public class InstallDELPolicy {

	public static void main(String[] args){

		DELProject project = null;
		
		try {
			String projectClass = args[0]; 
			project = (DELProject)Class.forName(projectClass).newInstance();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
			return;
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
			return;
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("You must supply the project class name");
			return;
		}
		
		GroupType[] groups = project.allGroups();

		PolicyDescriptionType pdt = new PolicyDescriptionType();
		pdt.setActions(RBACAction.allActions());
		TargetType[] tta = new TargetType[project.allAsTargets().length+RBACTarget.allAsTargets().length];
		for (int i = 0; i < project.allAsTargets().length; i++) {
			tta[i] = project.allAsTargets()[i];
		}
		int j=0;
		for (int i = project.allAsTargets().length; i < project
		.allAsTargets().length
		+ RBACTarget.allAsTargets().length; i++, j++) {
			tta[i] = RBACTarget.allAsTargets()[j];
		}
		pdt.setTargets(tta);

		PrivilegeType[] prta = new PrivilegeType[project.allAsPrivileges().length
		                                         + RBACRole.allAsPrivileges().length];
		for (int i = 0; i < project.allAsPrivileges().length; i++) {
			prta[i] = project.allAsPrivileges()[i];
		}
		int k=0;
		for (int i = project.allAsPrivileges().length; i < project
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
				PolicyType pt = new PolicyType(project.getProject(), project.getProjectID(), pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				mc.getPort().deletePolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
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
				PolicyType pt = new PolicyType(project.getProject(), project.getProjectID(), pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				//Must always use addPolicy first time
				mc.getPort().addPolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}	

			//Import the policies specific to the DEL
			List<StatementType> lst = DELPolicies.buildStatements(groups);

			try {
				System.out.println("Statements = " + lst.size());
				StatementType[] sta = new StatementType[lst.size()];
				int i = 0;
				for (StatementType st : lst) {
					sta[i] = st;
					i++;
				}
				PolicyType pt = new PolicyType(project.getProject(), project.getProjectID(), pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				mc.getPort().addStatementToPolicy(pta);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (PGSecurityException pgse) {
			System.out.println(pgse.getMessage());
		}
	}

}
