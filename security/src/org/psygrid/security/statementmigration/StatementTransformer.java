package org.psygrid.security.statementmigration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.utils.TargetAssessor;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class StatementTransformer {
	
	protected static TargetType originalTarget;
	protected static ActionType originalAction;
	
	public synchronized static List<StatementType> transformStatement(StatementType st) throws StatementTransformationException{
		
		originalTarget = st.getTarget();
		originalAction = st.getAction();
		
		CompositeRuleType compRule = st.getRule();		
		return transformRule(compRule);
		
	}
	
	public static boolean ruleHasGroupClause(StringBuffer groupId, CompositeRuleType r) throws StatementTransformationException{
		
		boolean ruleHasGroupClause = false;
		
		OperatorType operator = r.getOperator();
		
		if(operator.toString().equals("And")){
			
				if(r.getChildren() != null && r.getChildren().length != 0){
					if(r.getChildren().length > 1){
						throw new StatementTransformationException("Can't transform 'And' rule with > 1 nested composite.");
					}
					
					if(r.getChildren().length == 1){
						//Check that there's only one argument and that it is a group privilege
						//Check that the child is an 'Or' with only roles for arguments and no further nesting.
						
						OperatorType op = r.getChildren()[0].getOperator();
						if(!op.toString().equals("Or")){
							throw new StatementTransformationException("Can't transform 'And' rule containing a nested 'And'.");
						}else{
							if(isNested(r.getChildren(0))){
								throw new StatementTransformationException("Can't transform 'And rule containing an 'Or' rule that itself is further nested.");
							}
						}
						
						//Finally, check that there's only one argument and that it is a group privilege.
						if(r.getPrivilege().length != 1){
							throw new StatementTransformationException("Can't transform 'And' rule contating an 'Or' that doesn't have one and only one argument.");
						}
						
						ArgumentType arg = r.getPrivilege(0);
						PrivilegeType p = arg.getPrivilege();
						
						if(StatementTransformer.getPrivilegeType(p).equals("Role")){
							throw new StatementTransformationException("Can't transform 'And' rule with an 'Or' with a single 'Role' argument (must be group).");
						}
						
						//The root 'And' has one 'Or'. The 'Or' is not nested. The 'And' rule has only one argument, and this is a group.
						//The 'Or' arguments are where to find roles.
						
						if(groupId.length() == 0){
							groupId.append(r.getPrivilege(0).getPrivilege().getGroup().getIdCode());
						}
						ruleHasGroupClause = true;
						
				}
				
			}else{
				//There are no children.
				//Check that there are only two arguments - and one is a group and the other is a role.
				if(!(r.getPrivilege().length == 1 || r.getPrivilege().length == 2)){
					throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has either one or two arguments.");
				}
				
				if(r.getPrivilege().length ==2){
					PrivilegeType p1 = r.getPrivilege(0).getPrivilege();
					PrivilegeType p2 = r.getPrivilege(1).getPrivilege();
					
					if(StatementTransformer.getPrivilegeType(p1).equals(StatementTransformer.getPrivilegeType(p2))){
						throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has one role and one group argument.");
					}
					
					//Figure out which one is the group, and which one is the role, then create the new simple statement.
					ruleHasGroupClause = true;
					
					if(p1.getRole() != null){
						if(groupId.length() == 0){
							groupId.append(p2.getGroup().getIdCode());
						}
					}else{
						if(groupId.length() == 0){
							groupId.append(p1.getGroup().getIdCode());
						}
					}
					
				}else{
					//There's only 1 argument and no children.
					//Make sure the argument is a 'role' and not a group!
					ArgumentType arg = r.getPrivilege(0);
					if(StatementTransformer.getPrivilegeType(arg.getPrivilege()) != "Role"){
						throw new StatementTransformationException("Can't transform 'And' with 0 children and only one argument that is a group - the one argument must be a role.");
					}
				}
				

			}	
		}else if(operator.toString().equals("Or")){
			
			//We are relying on the default policy being....
			
			//Or statements might have EITHER children OR operators. 
			//We want to call each child, and add the stuff from the returned list(s) into the main list.
			//And we want to create a new statement for each argument. If the argument is of type group, then throw an exception.
			
			List<ArgumentType> privileges = Arrays.asList(r.getPrivilege());
			
			for(ArgumentType a : privileges){
				if(StatementTransformer.getPrivilegeType(a.getPrivilege()).equals("Group")){
					throw new StatementTransformationException("This statement has an 'Or' clause with a group as an argument - can't transform this.");
				}
			}
			
			//If we're here we know we have a straightforward 'Or' to deal with.
			if(r.getChildren() != null && r.getChildren().length > 0){
				List<CompositeRuleType> children = Arrays.asList(r.getChildren());
				for(CompositeRuleType child : children){
					ruleHasGroupClause = ruleHasGroupClause(groupId, child);
				}
			}
			
			for(ArgumentType a: privileges){
				if(a.getPrivilege().getGroup() != null){
					if(groupId.length() == 0){
						groupId.append(a.getPrivilege().getGroup().getIdCode());
					}
					ruleHasGroupClause = true;
					break;
				}
				
			}
		}
		
		return ruleHasGroupClause;
	}
	
	public static Map<RoleType, Integer> getRolesFromCompositeRule(CompositeRuleType r) throws StatementTransformationException{
		
		Map<RoleType, Integer> roleMap = new HashMap<RoleType, Integer>();
		
		getRolesFromCompositeRule(roleMap, r);
		
		return roleMap;
	}
	
	protected static void getRolesFromCompositeRule(Map<RoleType, Integer> map, CompositeRuleType r) throws StatementTransformationException{
		
		OperatorType operator = r.getOperator();
		
		if(operator.toString().equals("And")){
			
				if(r.getChildren() != null && r.getChildren().length != 0){
					if(r.getChildren().length > 1){
						throw new StatementTransformationException("Can't transform 'And' rule with > 1 nested composite.");
					}
					
					if(r.getChildren().length == 1){
						//Check that there's only one argument and that it is a group privilege
						//Check that the child is an 'Or' with only roles for arguments and no further nesting.
						
						OperatorType op = r.getChildren()[0].getOperator();
						if(!op.toString().equals("Or")){
							throw new StatementTransformationException("Can't transform 'And' rule containing a nested 'And'.");
						}else{
							if(isNested(r.getChildren(0))){
								throw new StatementTransformationException("Can't transform 'And rule containing an 'Or' rule that itself is further nested.");
							}
						}
						
						//Finally, check that there's only one argument and that it is a group privilege.
						if(r.getPrivilege().length != 1){
							throw new StatementTransformationException("Can't transform 'And' rule contating an 'Or' that doesn't have one and only one argument.");
						}
						
						ArgumentType arg = r.getPrivilege(0);
						PrivilegeType p = arg.getPrivilege();
						
						if(StatementTransformer.getPrivilegeType(p).equals("Role")){
							throw new StatementTransformationException("Can't transform 'And' rule with an 'Or' with a single 'Role' argument (must be group).");
						}
						
						//The root 'And' has one 'Or'. The 'Or' is not nested. The 'And' rule has only one argument, and this is a group.
						//The 'Or' arguments are where to find roles.
						
						CompositeRuleType r1 = r.getChildren(0);
						ArgumentType[] orArguments = r1.getPrivilege();
						int length = orArguments.length;
						
						for(int i = 0; i < length; i++){
							ArgumentType argu = orArguments[i];
							if(StatementTransformer.getPrivilegeType(argu.getPrivilege()).equals("Role")){
								map.put(argu.getPrivilege().getRole(), 1);
							}
						}
				}
				
			}else{
				//There are no children.
				//Check that there are only two arguments - and one is a group and the other is a role.
				if(!(r.getPrivilege().length == 1 || r.getPrivilege().length == 2)){
					throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has either one or two arguments.");
				}
				
				if(r.getPrivilege().length ==2){
					PrivilegeType p1 = r.getPrivilege(0).getPrivilege();
					PrivilegeType p2 = r.getPrivilege(1).getPrivilege();
					
					if(StatementTransformer.getPrivilegeType(p1).equals(StatementTransformer.getPrivilegeType(p2))){
						throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has one role and one group argument.");
					}
					
					//Figure out which one is the group, and which one is the role, then create the new simple statement.
					RoleType role = null;
					if(p1.getRole() != null){
						role = p1.getRole();
					}else{
						role = p2.getRole();
					}
					
					map.put(role, 1);
					
				}else{
					//There's only 1 argument and no children.
					//Make sure the argument is a 'role' and not a group!
					ArgumentType arg = r.getPrivilege(0);
					if(StatementTransformer.getPrivilegeType(arg.getPrivilege()) != "Role"){
						throw new StatementTransformationException("Can't transform 'And' with 0 children and only one argument that is a group - the one argument must be a role.");
					}
					
					map.put(r.getPrivilege(0).getPrivilege().getRole() , 1);
				}
				

			}	
		}else if(operator.toString().equals("Or")){
			
			//We are relying on the default policy being....
			
			//Or statements might have EITHER children OR operators. 
			//We want to call each child, and add the stuff from the returned list(s) into the main list.
			//And we want to create a new statement for each argument. If the argument is of type group, then throw an exception.
			
			List<ArgumentType> privileges = Arrays.asList(r.getPrivilege());
			
			for(ArgumentType a : privileges){
				if(StatementTransformer.getPrivilegeType(a.getPrivilege()).equals("Group")){
					throw new StatementTransformationException("This statement has an 'Or' clause with a group as an argument - can't transform this.");
				}
			}
			
			//If we're here we know we have a straightforward 'Or' to deal with.
			if(r.getChildren() != null && r.getChildren().length > 0){
				List<CompositeRuleType> children = Arrays.asList(r.getChildren());
				for(CompositeRuleType child : children){
					getRolesFromCompositeRule(map, child);
					//simpleStatements.addAll(subStatements);
				}
			}
			
			for(ArgumentType a: privileges){
				map.put(a.getPrivilege().getRole(), 1);
				
			}
		}
		
	}
	
	protected static List<StatementType> transformRule(CompositeRuleType r) throws StatementTransformationException{
			
		List<StatementType> simpleStatements = new ArrayList<StatementType>();
			
		//If this is an OR rule, then we just make an individual statement out of each operand.
			
		//However, if this is an AND rule, we check and see if we've got a (ROLE(s) && Group) rule. In which case, we combine this into a statement with a 
		//group-specific target.
			
		OperatorType operator = r.getOperator();
			
		if(operator.toString().equals("And")){
			
				if(r.getChildren() != null && r.getChildren().length != 0){
					if(r.getChildren().length > 1){
						throw new StatementTransformationException("Can't transform 'And' rule with > 1 nested composite.");
					}
					
					if(r.getChildren().length == 1){
						//Check that there's only one argument and that it is a group privilege
						//Check that the child is an 'Or' with only roles for arguments and no further nesting.
						
						OperatorType op = r.getChildren()[0].getOperator();
						if(!op.toString().equals("Or")){
							throw new StatementTransformationException("Can't transform 'And' rule containing a nested 'And'.");
						}else{
							if(isNested(r.getChildren(0))){
								throw new StatementTransformationException("Can't transform 'And rule containing an 'Or' rule that itself is further nested.");
							}
						}
						
						//Finally, check that there's only one argument and that it is a group privilege.
						if(r.getPrivilege().length != 1){
							throw new StatementTransformationException("Can't transform 'And' rule contating an 'Or' that doesn't have one and only one argument.");
						}
						
						ArgumentType arg = r.getPrivilege(0);
						PrivilegeType p = arg.getPrivilege();
						
						if(StatementTransformer.getPrivilegeType(p).equals("Role")){
							throw new StatementTransformationException("Can't transform 'And' rule with an 'Or' with a single 'Role' argument (must be group).");
						}
						
						//Now we can go get the statements from the embedded Composite.
						List<StatementType> subStatements = transformRule(r.getChildren(0));
						
						//Need to take these statements and modify them now - because their target should be RBACTarget.GROUP_INCLUSION
						for(StatementType s : subStatements){
							s.setTarget(RBACTarget.GROUP_INCLUSION.toTargetType());
							simpleStatements.add(s);
						}
				}
				
			}else{
				//There are no children.
				//Check that there are only two arguments - and one is a group and the other is a role.
				if(!(r.getPrivilege().length == 1 || r.getPrivilege().length == 2)){
					throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has either one or two arguments.");
				}
				
				if(r.getPrivilege().length ==2){
					PrivilegeType p1 = r.getPrivilege(0).getPrivilege();
					PrivilegeType p2 = r.getPrivilege(1).getPrivilege();
					
					if(StatementTransformer.getPrivilegeType(p1).equals(StatementTransformer.getPrivilegeType(p2))){
						throw new StatementTransformationException("Can't transform 'And' rule with no children unless it has one role and one group argument.");
					}
					
					//Figure out which one is the group, and which one is the role, then create the new simple statement.
					RoleType role = null;
					if(p1.getRole() != null){
						role = p1.getRole();
					}else{
						role = p2.getRole();
					}
					
					//We need to convert the RoleType to a role.
					
					//Everything checks out - so go ahead and create the statement.
					StatementType s = SimpleStatementBuilder.createSimpleStatement(RBACTarget.GROUP_INCLUSION, originalAction, role);
					simpleStatements.add(s);
				}else{
					//There's only 1 argument and no children.
					//Make sure the argument is a 'role' and not a group!
					ArgumentType arg = r.getPrivilege(0);
					if(StatementTransformer.getPrivilegeType(arg.getPrivilege()) != "Role"){
						throw new StatementTransformationException("Can't transform 'And' with 0 children and only one argument that is a group - the one argument must be a role.");
					}
					
					StatementType s = SimpleStatementBuilder.createSimpleStatement(originalTarget, originalAction, r.getPrivilege(0).getPrivilege().getRole());
					simpleStatements.add(s);
				}
				

			}	
		}else if(operator.toString().equals("Or")){
			
			//We are relying on the default policy being....
			
			//Or statements might have EITHER children OR operators. 
			//We want to call each child, and add the stuff from the returned list(s) into the main list.
			//And we want to create a new statement for each argument. If the argument is of type group, then throw an exception.
			
			List<ArgumentType> privileges = Arrays.asList(r.getPrivilege());
			
			for(ArgumentType a : privileges){
				if(StatementTransformer.getPrivilegeType(a.getPrivilege()).equals("Group")){
					throw new StatementTransformationException("This statement has an 'Or' clause with a group as an argument - can't transform this.");
				}
			}
			
			//If we're here we know we have a straightforward 'Or' to deal with.
			if(r.getChildren() != null && r.getChildren().length > 0){
				List<CompositeRuleType> children = Arrays.asList(r.getChildren());
				for(CompositeRuleType child : children){
					List<StatementType> subStatements = transformRule(child);
					simpleStatements.addAll(subStatements);
				}
			}
			
			for(ArgumentType a: privileges){
				//Here we have a problem because the original target MAY be a group.
				//If the target is a group, then we want to replace the original target with ANY. Otherwise, we want to leave the original target intact.
				
				TargetType target = originalTarget;
				
				if(TargetAssessor.targetIsCentre(originalTarget)){
					target = new TargetType(RBACTarget.ANY.toString(), String.valueOf(RBACTarget.ANY.ordinal()));
				}
				StatementType newStatement = SimpleStatementBuilder.createSimpleStatement(target, originalAction, a.getPrivilege().getRole());
				simpleStatements.add(newStatement);
			}
		}
			
		return simpleStatements;
	}
	
	protected static boolean isNested(CompositeRuleType cr){
		
		if(cr.getChildren() != null && cr.getChildren().length > 0)
			return true;
		else
			return false;
		
	}
	
	protected static String getPrivilegeType(PrivilegeType pt){
		if(pt.getRole() != null)
			return "Role";
		else
			return "Group";
	}

}
