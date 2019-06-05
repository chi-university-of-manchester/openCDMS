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


//Created on Oct 27, 2005 by John Ainsworth

package org.psygrid.security.policyauthority.model.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.policyauthority.model.IStatement;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.w3c.dom.Element;

/**
 * @author jda
 * 
 * @hibernate.joined-subclass table="t_statements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Statement extends Persistent implements IStatement {
	private static Log sLog = LogFactory.getLog(Statement.class);

	/**
	 * Target of the statement
	 */
	private Target target;

	/**
	 * Action of the statement
	 */
	private Action action;

	/**
	 * CompositeRule of the statement
	 */
	private Rule rule;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Statement() {}
	
	//boolean simplifiedStatement;

	/**
	 * Constructor that accepts the target, action and rule
	 * 
	 * @param t
	 *            The target of the statement.
	 * @param a
	 *            The action of the statement.
	 * @param r
	 *            The rule of the statement.
	 */
	public Statement(Target t, Action a, Rule r) {
		setTarget(t);
		setAction(a);
		setRule(r);
	}

	/**
	 * @return Returns the action.
     * @hibernate.many-to-one class="org.psygrid.security.policyauthority.model.hibernate.Action"
     *                        column="c_action_id"
     *                        not-null="false"
     *                        cascade="none"
     *                        lazy="false"
	 */
	public Action getAction() {
		return this.action;
	}

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return Returns the rule.
	 * @hibernate.many-to-one class="org.psygrid.security.policyauthority.model.hibernate.Rule"
	 * 						  column="c_rule_id"
     *                        not-null="false"
     *                        cascade="all"
     *                        lazy="false"
	 */
	public Rule getRule() {
		return this.rule;
	}

	/**
	 * @param rule
	 *            The rule to set.
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * @return Returns the target.
     * @hibernate.many-to-one class="org.psygrid.security.policyauthority.model.hibernate.Target"
     *                        column="c_target_id"
     *                        not-null="false"
     *                        cascade="none"
     *                        lazy="false"
	 */
	public Target getTarget() {
		return this.target;
	}

	/**
	 * @param target
	 *            The target to set.
	 */
	public void setTarget(Target target) {
		this.target = target;
	}
	


	public Element toDOM() {
		return null;
	}
	
	public static Statement fromStatementType(StatementType st) {
			return new Statement(Target.fromTargetType(st.getTarget()),
					Action.fromActionType(st.getAction()),
					CompositeRule.fromExternalType(st.getRule()));
		
	}

	public StatementType toStatementType() {
		return new StatementType(target.toTargetType(), action.toActionType(), (CompositeRuleType)rule.toExternalType());
	}

	public void print() {
		sLog.info("Statement: " + this.toString() + "\n\tID: "
				+ this.getId() + "\n\tVersion: " + getVersion() );
				rule.print();
				target.print();
				action.print();
	}
	/**
	 * Attach to the policy that is passed in.
	 * If any of the points of attachment is not found, 
	 * then the statement is not attached.
	 * 
	 * @param p
	 * 	The persisted policy to attach to
	 * 
	 */

	public void attach(Policy p){
		{
			boolean found = false;
			for (Action a : p.getActions()) {
				if (a.isEqual(action)) {
					found = true;
					action = a;
					break;
				}
			}
			if (!found) {
				sLog.info("Action not found: ");
				print();
				return;
			}
		}
		{
			boolean found = false;
			for (Target t : p.getTargets()) {
				if (t.isEqual(target)) {
					found = true;
					target = t;
					break;
				}
			}
			if (!found) {
				sLog.info("Target not found: ");
				print();
				return;
			}
		}
		if(!rule.attach(p)){
			return;
		}
		p.addStatement(this);
	}


}
