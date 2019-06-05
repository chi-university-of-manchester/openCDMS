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


//Created on May 6 2008 by John Ainsworth

package org.psygrid.security;

import java.rmi.RemoteException;

import org.psygrid.security.PGSecurityException;

import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;

import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.StatementType;

/**
 * @author jda
 * 
 */
public class SystemPolicy {
	
	public static void insert(PAManagementClient mc){
		PolicyDescriptionType pdt = new PolicyDescriptionType();
		pdt.setActions(RBACAction.allActions());
		pdt.setTargets(RBACTarget.allAsTargets());
		PrivilegeType[] prta = new PrivilegeType[RBACRole.allAsPrivileges().length];
		for (int i = 0; i < RBACRole.allAsPrivileges().length; i++) {
			prta[i] = RBACRole.allAsPrivileges()[i];
		}
		pdt.setPrivileges(prta);
		
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_ADD_USER.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
			
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_ADD_PROJECT.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}

		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_RESET_PASSWORD.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_PA_ADD_POLICY.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addStatementToPolicy(pta);
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_GET_USERS.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
			
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_DELETE_USER.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
			
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.ProjectManager.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_AA_LDAP_QUERY.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
			
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
		
		try {
			CompositeRuleType rule = new CompositeRuleType(
					OperatorType.And, null,
					new ArgumentType[] { new ArgumentType(
							RBACRole.StudyPatcher.toPrivilegeType(), true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ACTION_DR_PATCH_DATASET.toActionType(),
					rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addPolicy(pta);
			
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}

		// Role=SysAdmin, Target=ANY, Action=ANY (catch all, do anything)
		try {
			CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
					null, new ArgumentType[] { new ArgumentType(
							RBACRole.SystemAdministrator.toPrivilegeType(),
							true) });
			StatementType st = new StatementType(RBACTarget.ANY.toTargetType(),
					RBACAction.ANY.toActionType(), rule);
			StatementType[] sta = new StatementType[] { st };
			PolicyType pt = new PolicyType("SYSTEM", "-1", pdt, sta);
			PolicyType[] pta = new PolicyType[] { pt };
			mc.getPort().addStatementToPolicy(pta);
		} catch (ProcessingFaultMessage e) {
			System.out.println("pfm "+e.getMessage());
		} catch (InputFaultMessage e) {
			System.out.println("ifm "+e.getMessage());
		} catch (NotAuthorisedFaultMessage e) {
			System.out.println("nafm "+e.getMessage());	
		} catch (RemoteException e) {
			System.out.println("re "+e.getMessage());
		}
	}
}