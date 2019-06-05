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

package org.psygrid.security.attributeauthority.model.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.attributeauthority.model.IRole;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_roles"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Role extends Persistent implements IRole {
	private static Log sLog = LogFactory.getLog(Role.class);
	
	/**
	 * Role name
	 */
	private String roleName;
	
	/**
	 * Role identity
	 */
	private String idCode;

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
	public Role(String roleName, String idCode) {
		this.setRoleName(roleName);
		this.setIdCode(idCode);
	}

	/**
	 * Get the roleName
	 * 
	 * @return The roleName.
	 * @hibernate.property column = "c_role_name"
	 */
	public String getRoleName() {
		return roleName;

	}
	
	/**
	 * Get the idCode
	 * 
	 * @return The roleName.
	 * @hibernate.property column = "c_id_code"
	 */
	public String getIdCode() {
		return roleName;

	}

	/**
	 * Set the roleName
	 * 
	 * @param roleName
	 *            The role name.
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Set the idCode
	 * 
	 * @param idCode
	 *            The ID code.
	 */
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	
	public Element toDOM() {
		return null;
	}

	public static Role fromRoleType(RoleType rt) {
		Role r = new Role();
		if (rt != null) {
			r.setRoleName(rt.getName());
			r.setIdCode(rt.getIdCode());
		}
		return r;
	}
	public void print(){
		sLog.info("Role: "+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getRoleName());
	}

	
	public RoleType toRoleType() {
		RoleType r = new RoleType(this.getRoleName(), this.getIdCode());
		return r;
	}
	
	public boolean identical(Role r){
		boolean result = false;
		if ((r.getRoleName() != null) && (getRoleName() != null)) {
			if ((getRoleName().equals(r.getRoleName()) 
					&& (!r.getRoleName().equals("")))) {
				result = true;
			}
		}
		if ((r.getIdCode() != null) && (getIdCode()!=null)) {
			if (getIdCode().equals(r.getIdCode())
					&& (!r.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}
}
