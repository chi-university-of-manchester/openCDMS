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



package org.psygrid.security.policyauthority.test;

import org.psygrid.security.PGSecurityException;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.www.xml.security.core.types.PolicyType;

/**
 * @author jda
 *
 */
public class TestManagementClient {
	static String uidstring = "unique";
	static int uid=0;
	public static void main (String[] args) {

		try{
			
			PAManagementClient mc = new PAManagementClient("test.properties");
			try{
				mc.getPort().deletePolicy(new PolicyType[]{new PolicyType("Outlook", "OLK", null, null)});			
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
//			try{
//				for(int i=0; i<1;i++){
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null,  new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("AddRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("policy"+i, "po"+i, sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addPolicy(pta);
//				}
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("AddRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1", sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,null,  new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("CreateDataSet", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addPolicy(pta);
//				} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{	
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("SystemAdministrator", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("ReviewRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addPolicy(pta);				
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("SystemAdministrator", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("EditRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addStatementToPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule1 = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});			
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.Or, new CompositeRuleType[]{rule1}, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("RetrieveDataSet", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ANY", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("ModifyDataSet", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addStatementToPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ScientificResearchManager", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("ANY", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons",  "wa1", sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addStatementToPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, 
//						new CompositeRuleType[]{new CompositeRuleType(OperatorType.Or, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(null, new GroupType("subgroup1", "x25", null)), true)})},
//						new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("SystemAdministrator", null), null), true)});
//						
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("ApproveRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("waltons", "wa1",  sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().addStatementToPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				mc.getPort().deletePolicy(new DeletePolicyRequest(new PolicyType[]{new PolicyType("policy1", "po1", null), new PolicyType("policy2", "po2", null)}));			
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
////			try{
////				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});
////				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("EditRecord", null), rule);
////				StatementType[] sta = new StatementType[]{st};
////				PolicyType pt = new PolicyType("waltons",  "wa1", sta);
////				PolicyType[] pta = new PolicyType[]{pt};
////				mc.getPort().addArgumentToRuleToStatementToPolicy(pta);
////			} catch (Exception e){
////				System.out.println(e.getMessage());
////			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("SystemAdministrator", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("EditRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("policy1",  "po1", sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().deleteStatementFromPolicy(pta);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager", null), null), true), new ArgumentType(new PrivilegeType(new RoleType("DataAnalyst", null), null), true)});
//				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("AddRecord", null), rule);
//				StatementType[] sta = new StatementType[]{st};
//				PolicyType pt = new PolicyType("policy3",  "po3", sta);
//				PolicyType[] pta = new PolicyType[]{pt};
//				mc.getPort().deleteStatementFromPolicy(pta);		
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
////			try{
////				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager, null), true), new ArgumentType(new PrivilegeType(new RoleType("ClinicalResearchManager, null), true)});
////				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("AddRecord, rule);
////				StatementType[] sta = new StatementType[]{st};
////				PolicyType pt = new PolicyType("policy4", "po4",  sta);
////				PolicyType[] pta = new PolicyType[]{pt};
////				mc.getPort().deleteArgumentFromRuleFromStatementFromPolicy(pta);			
////			} catch (Exception e){
////				System.out.println(e.getMessage());
////			}
////			try{
////				CompositeRuleType rule = new CompositeRuleType(OperatorType.And, null, new ArgumentType[]{new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator, null), true), new ArgumentType(new PrivilegeType(new RoleType("ChiefInvestigator, null), true)});
////				StatementType st = new StatementType(new TargetType("target0", "x25"), new ActionType("AddRecord, rule);
////				StatementType[] sta = new StatementType[]{st};
////				PolicyType pt = new PolicyType("policy4", "po4",  sta);
////				PolicyType[] pta = new PolicyType[]{pt};
////				mc.getPort().deleteArgumentFromRuleFromStatementFromPolicy(pta);			
////			} catch (Exception e){
////				System.out.println(e.getMessage());
////			}
//			try{
//
//				String  c = mc.getPort().retrieveConfiguration(true);
//				System.out.println(c);
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
//			try{
//				PolicyType p = mc.getPort().getPolicyByProject(new PolicyType("waltons", "wa1", null));
//				Policy policy = Policy.fromPolicyType(p);
//				policy.print();
//			} catch (Exception e){
//				System.out.println(e.getMessage());
//			}
////			try{
////				
////				GetAllPoliciesResponseType0 p = mc.getPort().getAllPolicies("");
////				for(int i=0;i<p.getPolicies().length;i++){
////					Policy policy = Policy.fromPolicyType(p.getPolicies()[i]);
////					policy.print();
////				}
////			} catch (Exception e){
////				System.out.println(e.getMessage());
////			}
		} catch (PGSecurityException pgse){
			System.out.println(pgse.getMessage());
		}
	}
}