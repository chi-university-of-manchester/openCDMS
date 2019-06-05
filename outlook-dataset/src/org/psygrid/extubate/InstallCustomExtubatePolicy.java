package org.psygrid.extubate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.security.DefaultPolicy2;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.statementmigration.SimpleStatementBuilder;
import org.psygrid.security.utils.TargetAssessor;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class InstallCustomExtubatePolicy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PolicyDescriptionType pdt = new PolicyDescriptionType();
		pdt.setActions(RBACAction.allActions());		
		pdt.setTargets(RBACTarget.allAsTargets());
		
		int privilegeLength = RBACRole.allAsPrivileges().length + 1;
		List<PrivilegeType> ptl = Arrays.asList(RBACRole.allAsPrivileges());
		ArrayList<PrivilegeType> ptlVariableSize = new ArrayList<PrivilegeType>();
		ptlVariableSize.addAll(ptl);
		ptlVariableSize.add(ExtubateRBACRole.ExtubateLevel3.toPrivilegeType());
		
		PrivilegeType[] pta1 = new PrivilegeType[1];
		pdt.setPrivileges(ptlVariableSize.toArray(pta1));
	
		try {
			PAManagementClient mc = new PAManagementClient("test.properties");
			List<StatementType> lst = DefaultPolicy2.buildStatements();
		
			List<StatementType> croStatements = getMatchingStatements(lst, RBACRole.ClinicalResearchOfficer.toPrivilegeType(), null);
			List<StatementType> statementsToOmit = new ArrayList<StatementType>();
			for(StatementType st : croStatements){
				if(st.getAction().equals(RBACAction.ACTION_DR_EDIT_TREATMENT.toActionType()) || st.getAction().equals(RBACAction.ACTION_DR_EDIT_TREATMENT_INST.toActionType())){
					statementsToOmit.add(st);
				}
			}
			
			if(statementsToOmit.size() == 0){
				throw new Exception("The ACTION_DR_EDIT_TREATMENT and ACTION_DR_EDIT_TREATMENT_INST omission statements were not found.");
			}
			croStatements.removeAll(statementsToOmit);
			
			for(StatementType st: croStatements){
				ActionType action = st.getAction();
				TargetType target = st.getTarget();
				lst.add(SimpleStatementBuilder.createSimpleStatement(target, action, ExtubateRBACRole.ExtubateLevel3.toRoleType()));
			}
			
			List<StatementType> taGetRandResultsStatements = getMatchingStatements(lst, RBACRole.TreatmentAdministrator.toPrivilegeType(), RBACAction.ACTION_ESL_LOOKUP_RANDOMISATION_RESULT.toActionType());
			List<StatementType> taGetRandResultsStatements2= getMatchingStatements(lst, RBACRole.TreatmentAdministrator.toPrivilegeType(),RBACAction.ACTION_RS_GET_ALLOCATION.toActionType());
			//Grab all the statements for CRO and the statement from the TA that allows them to lookup
			//randomisation results.
			
			for(StatementType st : taGetRandResultsStatements){
				TargetType target = st.getTarget();
				ActionType action = st.getAction();
				lst.add(SimpleStatementBuilder.createSimpleStatement(target, action, ExtubateRBACRole.ExtubateLevel3.toRoleType()));
				lst.add(SimpleStatementBuilder.createSimpleStatement(target, action, RBACRole.ClinicalResearchOfficer.toRoleType()));
			}
			
			for(StatementType st : taGetRandResultsStatements2){
				TargetType target = st.getTarget();
				ActionType action = st.getAction();
				lst.add(SimpleStatementBuilder.createSimpleStatement(target, action, ExtubateRBACRole.ExtubateLevel3.toRoleType()));
				lst.add(SimpleStatementBuilder.createSimpleStatement(target, action, RBACRole.ClinicalResearchOfficer.toRoleType()));
			}

			
			
			
			//Then from these statements create new simple statements, all against the new extubate role.
			//Add these statements back to the array.
			
			try {
				StatementType[] sta = new StatementType[lst.size()];
				int i = 0;
				for (StatementType st : lst) {
					sta[i] = st;
					i++;
				}
	
				PolicyType pt = new PolicyType("Extubate", "EXT", pdt, sta);
				PolicyType[] pta = new PolicyType[] { pt };
				boolean r = mc.getPort().addPolicy(pta);
				String result = r == true ? "Success" : "Fail";
				System.out.println(result);
	
			} catch (Exception e) {
				e.printStackTrace();
			
			}
	
		} catch (Exception pgse) {
			pgse.printStackTrace();
		}
		
	}
	/*
	 * This returns the statements contained in the 'statements' list that match the specified filter.
	 * If both role and action are specified, then the filter is an 'and'
	 * If just role or just action is specified (i.e. one or the other is null) the filter is an 'or'
	 */
	public static List<StatementType> getMatchingStatements(List<StatementType> statements, PrivilegeType role, ActionType action){
		
		
		List<StatementType> matchingStatements = new ArrayList<StatementType>();
		
		if(role != null){
			if(action!= null){
				//filter by both
				for(StatementType st2 : statements){
					PrivilegeType stRole2 = st2.getRule().getPrivilege(0).getPrivilege();
					ActionType stAction = st2.getAction();
					if(stRole2.equals(role) && stAction.equals(action)){
						matchingStatements.add(st2);
					}
					
				}
			}else{
					for(StatementType st : statements){
						PrivilegeType stRole = st.getRule().getPrivilege(0).getPrivilege();
						if(stRole.equals(role)){
							matchingStatements.add(st);
						}//filter by role only
					}
			}
		}else if (action != null){
			for(StatementType st2 : statements){
				ActionType stAction = st2.getAction();
				if(stAction.equals(action)){
					matchingStatements.add(st2);
				}//filter by action only
			}
		}
		
		return matchingStatements;
	}

}
