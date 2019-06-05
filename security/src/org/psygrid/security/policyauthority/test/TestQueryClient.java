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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.opensaml.SAMLAssertion;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.InputFaultMessage;
import org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.ActionTargetType;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AllowedType;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
//import org.psygrid.www.xml.security.core.types.ProjectActionTargetType;
import org.psygrid.www.xml.security.core.types.ProjectActionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 * 
 */
public class TestQueryClient {
	static PAQueryClient paqc;
	public static void main(String[] args) {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		SAMLAssertion sa;
		
		try {
			paqc = new PAQueryClient(
					"test.properties");
			AAQueryClient qc2 = new AAQueryClient(
					"test.properties");
			System.out.println("starting.....");
			sa = qc2.getSAMLAssertion("CN=CRO One, OU=users, O=psygrid, C=uk");
			System.out.println(sa);
			
			
			/*
			CompositeRuleType[] crt = paqc.getPort().getMatchingRules(new ProjectActionTargetType(
					new ProjectType(null, "ED2", null, null, false),
					RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN.toActionType(), 
					RBACTarget.ANY.toTargetType()), 
					sa.toString());
			
			
			for(int i=0;i<crt.length;i++){
				String[] res = qc2.getUsersInProjectWithPermission(new ProjectType(null, "ED2", null, null, false), 
						crt[i]);
				for(int j=0; j<res.length; j++){
					System.out.println(res[j]);
				}
			}
			*/
					
			
//			try {
//				List<ActionType> lat = new ArrayList<ActionType>();
//				lat.add(new ActionType(RBACAction.ACTION_DR_GET_RECORD_SUMMARY.name(), null));
//				lat.add(new ActionType(RBACAction.ACTION_DR_GET_RECORDS_WITH_CONSENT_BY_GROUPS.name(), null));
//				lat.add(new ActionType(RBACAction.ACTION_ESL_EMERGENCY_BREAK_IN.name(), null));
//
//				Map<ProjectType, List<ActionType>> m = new HashMap<ProjectType, List<ActionType>>();
//				m.put(new ProjectType(null, "ED2", null, null, false), lat);
////				m.put(new ProjectType(null, "NED", null, null, false), lat);
////				m.put(new ProjectType(null, "EDT", null, null, false), lat);
//				Map<ProjectType, List<Map<ActionType, List<TargetType>>>> answer = checkAuthorisation(m ,sa.toString());
//				Set<Entry<ProjectType, List<Map<ActionType, List<TargetType>>>>> outer = answer.entrySet();
//				for(Entry<ProjectType, List<Map<ActionType, List<TargetType>>>> e : outer){
//					System.out.println(e.getKey().getIdCode());
//					for(Map<ActionType, List<TargetType>> x : e.getValue()){
//						Set<Entry<ActionType, List<TargetType>>> y = x.entrySet();
//						for(Entry<ActionType, List<TargetType>> e2 : y){
//							System.out.println(e2.getKey().getName());
//							for(TargetType tt : e2.getValue()){
//								System.out.println(tt.getName());
//							}
//						}
//					}
//					
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//			}
			
		} catch (Exception pgse) {
			pgse.printStackTrace();
			System.out.println(pgse.getMessage());
		}
		
//			try {
//				if (qc.getPort().makePolicyDecision(new PolicyType("policy0",  "po0",null),
//						new TargetType("target0", "x25"),
//						new ActionType("AddRecord", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("ChiefInvestigator", null), null), new PrivilegeType(new RoleType("DataAnalyst", null) , null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//
//				if (qc.getPort().makePolicyDecision(new PolicyType("waltons",  "wa1",null),
//						new TargetType("target0", "x25"),
//						new ActionType("ApproveRecord", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("SystemAdministrator", null), null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//
//				if (qc.getPort().makePolicyDecision(new PolicyType("waltons",  "wa1",null),
//						new TargetType("target0", "x25"),
//						new ActionType("RetrieveDataSet", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("ScientificResearchManager", null), null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//
//				if (qc.getPort().makePolicyDecision(new PolicyType("waltons",  "wa1", null),
//						new TargetType("target0", "x25"),
//						new ActionType("ModifyDataSet", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("PrincipalInvestigator", null), null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//				if (qc.getPort().makePolicyDecision(new PolicyType("policy0",  "po0", null),
//						new TargetType("target0", "x25"),
//						new ActionType("ViewRecord", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("ChiefInvestigator", null), null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//				if (qc.getPort().makePolicyDecision(new PolicyType("policy0",  "po0", null),
//						new TargetType("no-target", "nil"),
//						new ActionType("ViewRecord", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("ChiefInvestigator", null), null)})) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//			try {
//				if (qc.getPort().makePolicyDecision(new PolicyType("no-policy", "np0", null),
//						new TargetType("target0", "x25"),
//						new ActionType("ViewRecord", null),
//						new PrivilegeType[] { new PrivilegeType(new RoleType("ChiefInvestigator", null), null) })) {
//
//					System.out.println("come on");
//				} else {
//					System.out.println("denied");
//				}
//			} catch (InputFaultMessage e) {
//				System.out.println("I" + e.getFaultDiagnostic());
//			} catch (ProcessingFaultMessage e) {
//				System.out.println("p" + e.getMessage());
//			} catch (NotAuthorisedFaultMessage e) {
//				System.out.println("NA" + e.getMessage());
//			} catch (RemoteException e) {
//				System.out.println("RE" + e.getMessage());
//			}
//		} catch (PGSecurityException pgse) {
//			System.out.println(pgse.getMessage());
//		}
	}
	static public  Map<ProjectType, List<Map<ActionType, List<TargetType>>>> 
	checkAuthorisation(Map<ProjectType, List<ActionType>> request, String sa)
	throws PGSecurityException {
		
		org.psygrid.www.xml.security.core.types.AllowedType[] result = null;

		ProjectActionType[] pata = new ProjectActionType[request.size()];
		Set<Entry<ProjectType, List<ActionType>>> ws = request.entrySet();
		int i=0;
		for(Entry<ProjectType, List<ActionType>> e : ws){			
			pata[i] = new ProjectActionType();
			pata[i].setProject(e.getKey());
			List<ActionType> l = e.setValue(new ArrayList<ActionType>());
			pata[i].setAction(l.toArray(new ActionType[l.size()]));
			i++;
		}
		
		try {
			result = paqc.getPort().testPrivileges(pata, sa);
		} catch (InputFaultMessage e) {
			//sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (ProcessingFaultMessage e) {
			//sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (NotAuthorisedFaultMessage e) {
			//sLog.info(e.getFaultDiagnostic());
			throw new PGSecurityException(e.getFaultDiagnostic());
		} catch (RemoteException e) {
			//sLog.info(e.getMessage());
			throw new PGSecurityException(e.getMessage());
		}
		
		// process result
		Map<ProjectType, List<Map<ActionType, List<TargetType>>>> resultMap = new HashMap<ProjectType, List<Map<ActionType, List<TargetType>>>>();
		for(int j=0;j<result.length;j++){
			AllowedType at = result[j];
			List<Map<ActionType, List<TargetType>>> latttt = new ArrayList<Map<ActionType, List<TargetType>>>();
			for(int k=0;k<at.getActionTarget().length;k++){
				ActionTargetType att = at.getActionTarget()[k];
				List<TargetType> ltt = new ArrayList<TargetType>();
				for(int l=0;l<att.getTargets().length;l++){
					ltt.add(att.getTargets(l));
				}
				Map<ActionType, List<TargetType>> attmap = new HashMap<ActionType, List<TargetType>>();
				attmap.put(att.getAction(), ltt);
				latttt.add(attmap);
			}
			resultMap.put(result[j].getProject(), latttt);
		}
		return resultMap;
	}
}