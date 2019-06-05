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
import org.psygrid.security.policyauthority.model.IAuthority;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.AuthorityType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_authorities"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Authority extends Persistent implements IAuthority {
	private static Log sLog = LogFactory.getLog(Authority.class);


	/**
	 * Authority identity
	 */
	private String name;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Authority() {}

	/**
	 * Constructor that accepts the name of the authority
	 * 
	 * @param authority
	 *            The name of the authority.
	 */
	public Authority(String authority) {
		this.setName(authority);
	}
	
	/**
	 * Get the actionName
	 * 
	 * @return The actionName.
	 * @hibernate.property column = "c_action_name" lazy="false"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the actionName
	 * 
	 * @param actionName
	 *            The action name.
	 */
	public void setName(String authority) {
		this.name = authority;
	}

	public Element toDOM() {
		return null;
	}

	public static Authority fromAuthorityType(AuthorityType rt) {
		Authority r = new Authority();
		if (rt != null) {
			r.setName(rt.getName());
		}
		return r;
	}
	
	public AuthorityType toAuthorityType() {
		AuthorityType r = new AuthorityType(this.getName());
		return r;
	}
	
	public void print(){
		sLog.info("Authority: "+toString()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getName());
	}

	
	public boolean isEqual(Authority a) {
		boolean result = false;
		if ((a.getName() != null) && (getName() != null)) {
			if ((getName().equals(a.getName()) 
					&& (!a.getName().equals("")))) {
				result = true;
			}
		}
		return result;
	}
}

