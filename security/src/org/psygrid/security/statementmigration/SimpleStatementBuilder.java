package org.psygrid.security.statementmigration;

import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.RBACTarget;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.OperatorType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;

public class SimpleStatementBuilder {

	public static StatementType createSimpleStatement(RBACTarget target, RBACAction action, RBACRole role){
		
		CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
				new ArgumentType(role.toPrivilegeType(), true) });
		StatementType simpleStatement = new StatementType(target.toTargetType(), action.toActionType(), rule);
		
		return simpleStatement;
	}
	
	public static StatementType createSimpleStatement(RBACTarget target, ActionType action, RoleType role){
		
		CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
				new ArgumentType(new PrivilegeType(role, null), true) });
		
		StatementType simpleStatement = new StatementType(target.toTargetType(), action, rule);
		return simpleStatement;
	}
	
	public static StatementType createSimpleStatement(TargetType target, ActionType action, RoleType role){
		
		CompositeRuleType rule = new CompositeRuleType(OperatorType.Or,
				null, new ArgumentType[] {
				new ArgumentType(new PrivilegeType(role, null), true) });
		
		StatementType simpleStatement = new StatementType(target, action, rule);
		return simpleStatement;
	}
	
}
