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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.model.IPolicy;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AuthorityType;
import org.psygrid.www.xml.security.core.types.PolicyDescriptionType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.StatementType;
import org.psygrid.www.xml.security.core.types.TargetType;
import org.w3c.dom.Element;

/**
 * @author jda
 * 
 * @hibernate.joined-subclass table="t_policies"
 * @hibernate.joined-subclass-key column="c_id"
 */

public class Policy extends Persistent implements IPolicy {
	private static Log sLog = LogFactory.getLog(Policy.class);
	/**
	 * Policy identitity, equivalent to project and dataSet
	 */
	private String policyName;
	
	
	private String idCode;
	
	/**
	 * Policy's Statements
	 */
	private List<Statement> statements = new ArrayList<Statement>();

	/**
	 * Policy's Actions
	 */
	private List<Action> actions = new ArrayList<Action>();
	
	/**
	 * Policy's Targets
	 */
	private List<Target> targets = new ArrayList<Target>();
	
	/**
	 * Policy's Privileges
	 */
	private List<Privilege> privileges = new ArrayList<Privilege>();

	
	/**
	 * Policy's Authorities
	 */
	private List<Authority> authorities = new ArrayList<Authority>();
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Policy() {
	};

	/**
	 * Constructor that accepts the name of the policy
	 * 
	 * @param policyName
	 *            The name of the policy.
	 */
	public Policy(String policyName) {
		this.setPolicyName(policyName);
	}
	
	/**
	 * Constructor that accepts the name of the policy
	 * 
	 * @param policyName
	 *            The name of the policy.
	 * @param policyId
	 * 			  The id of the policy
	 */
	public Policy(String policyName, String idCode) {
		this.setPolicyName(policyName);
		this.setIdCode(idCode);
	}

	/**
	 * Constructor that accepts the policy name and a list of actions
	 * 
	 * @param policyName
	 *            The name of the policy.
	 * @param idCode
	 * 			 The id of the policy
	 * @param statments
	 *            A list of statements
	 */
	public Policy(String policyName, String idCode, ArrayList<Statement> statements) {
		this.setPolicyName(policyName);
		this.setIdCode(idCode);
		this.setStatements(statements);
	}

	/**
	 * Get the policyName
	 * 
	 * @return The policyName.
	 * @hibernate.property column = "c_policy_name" lazy="false"
	 */
	public String getPolicyName() {
		return policyName;
	}

	/**
	 * Set the policyName
	 * 
	 * @param policyName
	 *            The policy name.
	 */
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	/**
	 * Get the actions
	 * 
	 * @return A list containing the statements.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Statement"
	 * @hibernate.key column="c_statement_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Statement> getStatements() {	
		return statements;	
	}

	/**
	 * Set actions
	 * 
	 * @param st
	 *            A list containing the statements.
	 */
	public void setStatements(List<Statement> st) {
		if(st == null){
			this.statements = new ArrayList<Statement>();
		}else{
			this.statements = st;
		}
	}


	/**
	 * @param a
	 * @param t
	 * @return boolean
	 */
	public boolean isSupported(Action a, Target t){
		boolean rc = false;
		ListIterator<Statement> it = statements.listIterator();
		while(it.hasNext()){
			Statement stemp = it.next();
			if((stemp.getAction().getActionName().equals(a.getActionName()))
					&& (stemp.getTarget().isEqual(t))){
				rc = true;
				break;
			}
		}
		return rc;		
	}
	
	/**
	 * @param t
	 * @param a
	 * @param r
	 *            List of the user's privileges
	 * @return
	 */
	public boolean isAllowedToPerform(Target t, Action a, List<Privilege> r) {
		boolean rc = false;
		boolean actionFound = false;
		boolean targetFound = false;
		sLog.info("start isAllowedToPerform "+ new Date().toString());
		ListIterator<Statement> it = statements.listIterator();
		while (it.hasNext() && rc != true) {
			Statement stemp = it.next();
			if ((stemp.getAction().getActionName().equals(a.getActionName()) || stemp
					.getAction().getActionName().equals(RBACAction.ANY.toString()))){
				actionFound = true;
				if((stemp.getTarget().isEqual(t) 
						|| stemp.getTarget().getTargetName().equals(RBACTarget.ANY.toString())
						|| stemp.getTarget().getIdCode().equals(RBACTarget.ANY.idAsString()))){
					targetFound = true;
					if(stemp.getRule().isPermitted(r)){
						rc = true;
						sLog.info("stop isAllowedToPerform "+ new Date().toString());
						break;
					}
				}			
			}
		}
		if(!rc){
			if(!actionFound){
				sLog.debug("Failed to find action " + a.getActionName());
			} else if(!targetFound){
				sLog.debug("Action "+a.getActionName()+" found. Failed to find target ID =" + t.getIdCode()+ " Name =" + t.getTargetName());		
			} else {
				sLog.debug("Action and target found. Privileges not sufficient");
				for(int i=0; i<r.size(); i++){
					sLog.debug("Name = "+r.get(i).getName()+" IDCode = "+r.get(i).getIdCode());
				}
			}
		}
		return rc;
	}
	
	
	/**
	 * @param t
	 * @param a
	 * @param r
	 * @return
	 */
	public boolean isAllowedToPerform(Target target, String a, String[] r){
		ArrayList<Privilege> alr = new ArrayList<Privilege>();
		for(int i=0; i<r.length; i++){
			alr.add(new Role(r[i]));			
		}
		return isAllowedToPerform(target, new Action(a), alr);
	}
	
	/**
	 * @param st
	 * @return
	 */
	public boolean addStatement(Statement st){
		boolean rc = false;
		
		/*
		if(isSupported(st.getAction(), st.getTarget())){
			rc = removeStatement(st);	
		}
		*/
		
		
		rc = statements.add(st);
		return rc;
	}
	
	/**
	 * @param s
	 * @return
	 */
	public boolean addStatement(StatementType[] s){
		for(int i=0; i<s.length;i++){
			Statement st = Statement.fromStatementType(s[i]);
			addStatement(st);
		}
		return true;		
	}
	
	
	/**
	 * @param a
	 * @return
	 */
	public boolean removeStatement(Statement a){
		boolean rc = false;
		ListIterator<Statement> it = statements.listIterator();
		while(it.hasNext()){
			Statement atemp = it.next();
			if(atemp.getAction().getActionName().equals(a.getAction().getActionName())&&
					atemp.getTarget().getTargetName().equals(a.getTarget().getTargetName())){
				it.remove();
				rc = true;
				break;
			}
		}
		return rc;	
	}
	
	/**
	 * @param st
	 * @return
	 */
	public boolean removeStatement(StatementType[] st){
		for(int i=0; i<st.length;i++){
			Statement a = Statement.fromStatementType(st[i]);
			removeStatement(a);
		}
		return true;
	}
	

	/**
	 * @param target
	 * @param action
	 * @return Statement
	 */
	public Statement getStatementByTargetAndAction(Target target, String action){
		Statement found = null;
		ListIterator<Statement> it = statements.listIterator();
		while(it.hasNext()){
			Statement temp = it.next();
			if(temp.getAction().getActionName().equals(action)
					&& temp.getTarget().isEqual(target)){
				found = temp;
				break;
			}
		}
		return found;
	}
	
	public Element toDOM() {
		//TODO
		return null;
	}

	public static Policy fromPolicyType(PolicyType p) {
		Policy policy = new Policy();
		if (p != null) {
			ArrayList<Statement> slist = new ArrayList<Statement>();
			if (p.getStatement() != null) {
				for (int i = 0; i < p.getStatement().length; i++) {
					slist.add(Statement.fromStatementType(p.getStatement()[i]));
				}
				policy.setStatements(slist);
			}
			PolicyDescriptionType pdt = null;
			if ((pdt=p.getDescription()) != null){
				if (pdt.getActions() != null) {
					ArrayList<Action> alist = new ArrayList<Action>();
					for (int i = 0; i < pdt.getActions().length; i++) {
						alist.add(Action.fromActionType(pdt.getActions()[i]));
					}
					policy.setActions(alist);
				}		
				if (pdt.getTargets() != null) {
					ArrayList<Target> tlist = new ArrayList<Target>();
					for (int i = 0; i < pdt.getTargets().length; i++) {
						tlist.add(Target.fromTargetType(pdt.getTargets()[i]));
					}
					policy.setTargets(tlist);
				}
				if (pdt.getPrivileges() != null) {
					ArrayList<Privilege> plist = new ArrayList<Privilege>();
					for (int i = 0; i < pdt.getPrivileges().length; i++) {
						plist.add(Privilege.fromPrivilegeType(pdt.getPrivileges()[i]));
					}
					policy.setPrivileges(plist);
				}
				if (pdt.getAuthorities() != null) {
					ArrayList<Authority> aulist = new ArrayList<Authority>();
					for (int i = 0; i < pdt.getAuthorities().length; i++) {
						aulist.add(Authority.fromAuthorityType(pdt.getAuthorities()[i]));
					}
					policy.setAuthorities(aulist);
				}
			}
			policy.setPolicyName(p.getName());
			policy.setIdCode(p.getIdCode());
		}
		return policy;
	}
	
	public PolicyType toPolicyType() {
		StatementType[] sta = new StatementType[this.getStatements().size()];
		for (int i = 0; i < getStatements().size(); i++) {
			sta[i] = getStatements().get(i).toStatementType();
		}
		PolicyDescriptionType pdt = new PolicyDescriptionType();
		ActionType[] ata = new ActionType[this.getActions().size()];
		for (int i = 0; i < getActions().size(); i++) {
			ata[i] = getActions().get(i).toActionType();
		}		
		pdt.setActions(ata);
		TargetType[] tta = new TargetType[this.getTargets().size()];
		for (int i = 0; i < getTargets().size(); i++) {
			tta[i] = getTargets().get(i).toTargetType();
		}		
		pdt.setTargets(tta);
		PrivilegeType[] pta = new PrivilegeType[this.getPrivileges().size()];
		for (int i = 0; i < getPrivileges().size(); i++) {
			pta[i] = getPrivileges().get(i).toPrivilegeType();
		}		
		pdt.setPrivileges(pta);
		AuthorityType[] auta = new AuthorityType[this.getAuthorities().size()];
		for (int i = 0; i < getAuthorities().size(); i++) {
			auta[i] = getAuthorities().get(i).toAuthorityType();
		}		
		pdt.setAuthorities(auta);
		return new PolicyType(this.getPolicyName(), this.getIdCode(), pdt, sta);
	}
	
	public void print(){
		sLog.info("Policy: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getPolicyName()+"\n\tIdCode: "+this.getIdCode());
		for(int i=0;i<statements.size();i++){
			statements.get(i).print();
		}
	}

	/**
	 * @return Returns the idCode.
	 * @hibernate.property column = "c_id_code" lazy="false"
	 */
	public String getIdCode() {
		return this.idCode;
	}

	/**
	 * @param idCode The idCode to set.
	 */
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

	/**
	 * @return Returns the actions.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Action"
	 * @hibernate.key column="c_policy_id"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Action> getActions() {
		return this.actions;
	}

	/**
	 * @param actions The actions to set.
	 */
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	/**
	 * @return Returns the privilege.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Privilege"
	 * @hibernate.key column="c_policy_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Privilege> getPrivileges() {
		return this.privileges;
	}
	
	/**
	 * @return Returns the privilege.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Authority"
	 * @hibernate.key column="c_policy_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Authority> getAuthorities() {
		return this.authorities;
	}

	/**
	 * @param List<Authority> The authorities to set.
	 */
	public void setAuthorities(List<Authority> la) {
		this.authorities = la;
	}
	
	/**
	 * @param privilege The privilege to set.
	 */
	public void setPrivileges(List<Privilege> privilege) {
		this.privileges = privilege;
	}

	/**
	 * @return Returns the targets.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false"
	 * @hibernate.one-to-many class="org.psygrid.security.policyauthority.model.hibernate.Target"
	 * @hibernate.key column="c_policy_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Target> getTargets() {
		return this.targets;
	}

	/**
	 * @param targets The targets to set.
	 */
	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}
	public void updateDescription(PolicyDescriptionType pdt){
		for(int i=0; i<pdt.getActions().length; i++){
			Action newAction = Action.fromActionType(pdt.getActions(i));
			boolean found = false;
			for(Action a : actions){
				if(a.isEqual(newAction)){
					found = true;
					break;
				}
			}
			if(!found){
				actions.add(newAction);
			}
		}
		for(int i=0; i<pdt.getTargets().length; i++){
			Target newTarget = Target.fromTargetType(pdt.getTargets(i));
			boolean found = false;
			for(Target t : targets){
				if(t.isEqual(newTarget)){
					found = true;
					break;
				}
			}
			if(!found){
				targets.add(newTarget);
			}
		}
		for(int i=0; i<pdt.getPrivileges().length; i++){
			Privilege newPrivilege = Privilege.fromPrivilegeType(pdt.getPrivileges(i));
			boolean found = false;
			for(Privilege p : privileges){
				if(p.isEqual(newPrivilege)){
					found = true;
					break;
				}
			}
			if(!found){
				privileges.add(newPrivilege);
			}
		}
		if(pdt.getAuthorities() != null){
			for(int i=0; i<pdt.getAuthorities().length; i++){
				Authority newAuthority = Authority.fromAuthorityType(pdt.getAuthorities(i));
				boolean found = false;
				for(Authority a: authorities){
					if(a.isEqual(newAuthority)){
						found = true;
						break;
					}
				}
				if(!found){
					authorities.add(newAuthority);
				}
			}
		}
	}
}
