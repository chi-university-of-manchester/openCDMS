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
import org.psygrid.security.policyauthority.model.IRole;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_roles"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Role extends Privilege implements IRole{
	private static Log sLog = LogFactory.getLog(Role.class);

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Role() {}

	/**
	 * Constructor that accepts the name of the role
	 * 
	 * @param roleName
	 *            The name of the role.
	 */
	public Role(String roleName) {
		this.setName(roleName);
	}

	public Role(String roleName, String idCode) {
		this.setName(roleName);
		this.setIdCode(idCode);
	}
	
	public Element toDOM() {
		return null;
	}

	public static Role fromRoleType(RoleType rt) {
		Role r = new Role();
		if (rt != null) {
			r.setName(rt.getName());
		}
		return r;
	}
	public RoleType toRoleType() {
		RoleType r = new RoleType(this.getName(), null);
		return r;
	}
	public PrivilegeType toPrivilegeType() {
		RoleType r = new RoleType(getName(), getIdCode());
		PrivilegeType p = new PrivilegeType();
		p.setRole(r);
		return p;
	}
	public void print(){
		sLog.info("Role: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getName());
	}
}
