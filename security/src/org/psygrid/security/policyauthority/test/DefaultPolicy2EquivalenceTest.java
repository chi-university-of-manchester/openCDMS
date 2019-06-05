package org.psygrid.security.policyauthority.test;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import org.psygrid.security.DefaultPolicy;
import org.psygrid.security.DefaultPolicy2;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.model.hibernate.Argument;
import org.psygrid.security.policyauthority.model.hibernate.CompositeRule;
import org.psygrid.security.policyauthority.model.hibernate.Group;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.security.statementmigration.StatementTransformationException;
import org.psygrid.security.statementmigration.StatementTransformer;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.security.RBACRole;
import org.psygrid.security.policyauthority.model.hibernate.Privilege;

public class DefaultPolicy2EquivalenceTest extends TestCase {
	
	private final static GroupType tokenGroup = new GroupType("TOKEN", "001001", "project");

	public void testNewStatementEquivalence() throws StatementTransformationException{
		
		
		GroupType[] groups = new GroupType[]{tokenGroup};		
		List<StatementType> oldStatements = DefaultPolicy.buildStatements(groups);
		
		for(StatementType oldSt : oldStatements){
			
			List<StatementType> newEquivalentStatements = null;
			
			try {
				newEquivalentStatements = DefaultPolicy2.buildStatement(oldSt);
			} catch (StatementTransformationException e) {
				//No good - the test fails in this case.
				assert(false);
			}
			
			//Now from the old statement we want to extract all of the roles.
			Map<RoleType, Integer> rolesMap = StatementTransformer.getRolesFromCompositeRule(oldSt.getRule());
			StringBuffer groupId = new StringBuffer();
			boolean rulesGroupBased = StatementTransformer.ruleHasGroupClause(groupId, oldSt.getRule());
			
			//We also want to know whether the target is a group.
			
			//Based on what we have just found out above, we'll need to set up the test.
			//We want to test with every role.
			//If there's a group involved as well, we want to test both with and without group inclusion,
			//for every role
			
			List<List<Privilege>> testCases = createTestCases(rulesGroupBased ? groupId.toString() : null , rolesMap);
			
			for(List<Privilege> testCase : testCases){
				boolean success = evaluateTestCase(oldSt, newEquivalentStatements, testCase, rulesGroupBased ? groupId.toString() : null);
				if(success == false){
					//Print out the failed action and the test case under which the failure occurred.
					System.out.println("Equivalence test failed for the following action: " + oldSt.getAction().getName() + ".");
					System.out.println("The test case follows: ");
					for(Privilege arg : testCase){
						//Is this a role or a group
						System.out.println("Privilege name is: " + arg.getName() + " and privilege code is: " + arg.getIdCode() + ".");
					}
				}
				this.assertTrue(success);
			}
			
		}
		
		
	}
	
	private void augmentEquivalentStatements(List<Statement> newStatements, String groupId){
		
		for(Statement st : newStatements){
			if(st.getTarget().getTargetName().equals(RBACTarget.GROUP_INCLUSION.toString()) && groupId != null){
				Group gp = new Group(null, groupId);
				Argument arg = new Argument(gp, true);
				st.getRule().setAdditionalArgument(arg);
			}
		}
		
	}
	
	private boolean evaluateTestCase(StatementType old, List<StatementType> newEquivalents, List<Privilege> testCase, String groupId){
		
		//First, evaluate the old, original statement.
		boolean isPermittedByOld = false;
		Statement oldS = Statement.fromStatementType(old);
		isPermittedByOld = oldS.getRule().isPermitted(testCase);
		
		List<Statement> newEquivalentStatements = new ArrayList<Statement>();
		for(StatementType st : newEquivalents){
			newEquivalentStatements.add(Statement.fromStatementType(st));
		}
		
		if(groupId != null){
			this.augmentEquivalentStatements(newEquivalentStatements, groupId);
		}
		
		boolean isPermittedByNew = false;
		for(Statement s : newEquivalentStatements){
			isPermittedByNew = s.getRule().isPermitted(testCase);
			if(isPermittedByNew){
				break;
			}
		}
		
		return (isPermittedByNew == isPermittedByOld);
	}
	
	private List<List<Privilege>> createTestCases(String groupId, Map<RoleType, Integer> rolesMap){
		
		List<List<Privilege>> testCases = new ArrayList<List<Privilege>>();
		
		Set<RoleType> roles = rolesMap.keySet();
		for(RoleType r : roles){
			List<Privilege> testCase = new ArrayList<Privilege>();
			List<Privilege> testCase2 = null;
			Privilege role = new Privilege(r.getName(), r.getIdCode());
			Privilege group = null;
			if(groupId != null){
				
				testCase2 = new ArrayList<Privilege>();
				
				group = new Privilege(null, groupId);
				testCase.add(role);
				testCase.add(group);
				testCases.add(testCase);
				
				//Now create a separate test case with the role, but WITHOUT the group
				testCase2.add(role);
				testCases.add(testCase2);
			}else{
				testCase.add(role);
				testCases.add(testCase);
			}
		}
		
		return testCases;
	}
	
}
