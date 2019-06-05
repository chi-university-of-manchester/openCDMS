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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.policyauthority.model.IArgument;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_arguments"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Argument extends Rule implements IArgument {
	private static Log sLog = LogFactory.getLog(Argument.class);
	/**
	 * Privilege
	 */
	private Privilege privilege;

	/**
	 * Assertion
	 */
	private boolean assertion;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Argument() {
	}

	/**
	 * Constructor that accepts the name of the operator
	 * 
	 * @param privilege
	 *            The privilege concerned.
	 * @param assertion
	 *            Is the privilege positively or negatively asserted?
	 */
	public Argument(Privilege p, boolean a) {
		privilege = p;
		assertion = a;
	}

	/**
	 * @return Returns the assertion.
	 * @hibernate.property column = "c_asserted" lazy="false"
	 */
	public boolean isAssertion() {
		return this.assertion;
	}

	/**
	 * @param assertion
	 *            The assertion to set.
	 */
	public void setAssertion(boolean assertion) {
		this.assertion = assertion;
	}

	/**
	 * @return Returns the privilege.
	 * @hibernate.many-to-one class="org.psygrid.security.policyauthority.model.hibernate.Privilege"
	 *                        column="c_privilege_id" not-null="false"
	 *                        cascade="none" lazy="false"
	 */
	public Privilege getPrivilege() {
		return this.privilege;
	}

	/**
	 * @param privilege
	 *            The privilege to set.
	 */
	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	public Element toDOM() {
		return null;
	}

	public void print() {
		sLog.info("Argument: " + toString() + "\n\tID: "
				+ this.getId() + "\n\tVersion: " + getVersion()
				+ "\n\tPrivilege: " + getPrivilege() + "\n\tAsserted: "
				+ this.isAssertion());
		getPrivilege().print();
	}

	public static Argument fromExternalType(ArgumentType rt) {
		Argument a = null;		
		if(rt instanceof ArgumentType){
			ArgumentType at = (ArgumentType)rt;	
			if (at.getPrivilege() != null) {
				if(at.getPrivilege().getRole()!=null){
					a = new Argument(Role
							.fromRoleType(at.getPrivilege().getRole()), at
							.getAssertion());
				} else if(at.getPrivilege().getGroup()!=null){
					a = new Argument(Group
							.fromGroupType(at.getPrivilege().getGroup()), at
							.getAssertion());
				}	
			} 
		}
		return a;
	}
	
	public ArgumentType toExternalType() {
		ArgumentType a = null;
		PrivilegeType pt = null;
		if (this.getPrivilege() != null) {
			if(getPrivilege() instanceof Role){
				pt = new PrivilegeType(((Role)getPrivilege()).toRoleType(), null);
			}
			if(getPrivilege() instanceof Group){
				pt = new PrivilegeType(null,((Group)getPrivilege()).toGroupType());
			}
			a = new ArgumentType(pt, this.isAssertion());
		} 
		return a;
	}

	protected boolean specifiesARole(){
		if(privilege instanceof Role){
			return true;
		}else{
			return false;
		}
	}
	
	protected boolean specifiesAndRelationshipBetweenRoles(){
		return false;
	}
	
	public boolean canBeTransformed(){
		return true;
	}
	
	public boolean isPermitted(List<Privilege> lr){
		boolean rc = true;		
		Iterator<Privilege> ir = lr.iterator();
		boolean ok = false;
		while(ir.hasNext()){
			Privilege priv = ir.next();
			if(this.isAssertion()){
				sLog.debug("assert true for "+ this.getPrivilege().getName());
				// must have
				ok = false;
				sLog.debug("comapare against " +priv.getName());
				if(this.getPrivilege().compare(priv)){
					ok = true;
					break;
				}
			} else {
				// must not have
				ok = true;
				if(this.getPrivilege().compare(priv)){
					ok = false;
					break;
				}						
			}
		}	
		if(!ok){
			rc=false;
		}
		
		if(rc && this.getAdditionalArgument() != null){
			rc = getAdditionalArgument().isPermitted(lr);
		}
		
		return rc;
	}
	/**
	 * Attach the rule to the persisted policy supplied
	 * 
	 * @param p The persisted policy to attach to
	 * @return False if the privilege point of attachment fails 
	 * 
	 * 
	 */
	public boolean attach(Policy p) {
		boolean found = false;
		for (Privilege priv : p.getPrivileges()) {
			if (priv.isEqual(privilege)) {
				found = true;
				privilege = priv;
				break;
			}
		}
		if(!found){
			sLog.info("Privilege not found: ");
			print();
		}
		return found;
	}
}
