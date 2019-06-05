package org.psygrid.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.security.statementmigration.StatementTransformationException;
import org.psygrid.security.statementmigration.StatementTransformer;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;

public class ReadablePolicyGenerator {
	
	private final static GroupType tokenGroup = new GroupType("TOKEN", "001001", "project");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//convert conglomerate statements to a group of simple statements that are, collectively, equivalent.
		
		
		List<StatementType> originalDefaultStatements = DefaultPolicy.buildStatements(new GroupType[]{tokenGroup});
		List<StatementType> equivalentSimpleStatements = new ArrayList<StatementType>();
		
		
		for(StatementType original : originalDefaultStatements){
			try {
				List<StatementType> transformedStatements = StatementTransformer.transformStatement(original);
				equivalentSimpleStatements.addAll(transformedStatements);
			} catch (StatementTransformationException e) {
				System.out.println(e.getMessage() + " for Action: " + original.getAction().getName());
			}
		}
		
		Map<RBACRole, List<StatementType>[]> statementsMap = new HashMap<RBACRole, List<StatementType>[]>();
		
		//sort these statements by role, and by whether they are a group_inclusion statement.
		for(RBACRole r : RBACRole.values()){
			List<StatementType> statementsForRole = new ArrayList<StatementType>();
			List<StatementType> groupInclusionStatementsForRole = new ArrayList<StatementType>();
			for(StatementType s : equivalentSimpleStatements){
				RoleType roleType = s.getRule().getPrivilege()[0].getPrivilege().getRole();
				if(roleType.getName().equals(r.toString())){
					if(s.getTarget().getName().equals(RBACTarget.GROUP_INCLUSION.toString())){
						groupInclusionStatementsForRole.add(s);
					}else{
						statementsForRole.add(s);
					}
				}
			}
			
			List<StatementType>[] statementListsArray = new ArrayList[2];
			statementListsArray[0] = statementsForRole;
			statementListsArray[1] = groupInclusionStatementsForRole;
			
			statementsMap.put(r, statementListsArray);
			
		}
		
		
		//Start printing out the statements.
		
		Set<RBACRole> keySet = statementsMap.keySet();
		
		System.out.println("List<StatementType> additionalStatements = new ArrayList<StatementType>();");
		
		for(RBACRole r : keySet){
			List<StatementType>[] statementsForRole = statementsMap.get(r);
			System.out.println("////////////////////////////////////////////////////////");
			System.out.println("//Statements for " + r.toString());
			System.out.println("");
			
			List<StatementType> nonGroupStatements = statementsForRole[0];
			List<StatementType> groupStatements = statementsForRole[1];
			
			System.out.println("//Non-group statements for " + r.toString());
			System.out.println("");
			
			for(StatementType s : nonGroupStatements){
				String role = s.getRule().getPrivilege(0).getPrivilege().getRole().getName();
				String action = s.getAction().getName();
				String target = s.getTarget().getName();
				
				System.out.println("additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget." + target + ", RBACAction." + action + ", RBACRole." + role + "));");
				
			}
			
			
			System.out.println("");
			System.out.println("//Group statements for " + r.toString());
			System.out.println("");
			
			
			for(StatementType s: groupStatements){
				String role = s.getRule().getPrivilege(0).getPrivilege().getRole().getName();
				String action = s.getAction().getName();
				
				
				System.out.println("additionalStatements.add(SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, RBACAction." + action + ", RBACRole." + role + "));");
			}
			
			System.out.println("////////////////////////////////////////////////");
			System.out.println("");
			
		}
		
		

	}

}
