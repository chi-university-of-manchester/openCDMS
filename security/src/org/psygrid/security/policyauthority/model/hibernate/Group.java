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
import org.psygrid.security.policyauthority.model.IGroup;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_groups"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Group extends Privilege implements IGroup {
	private static Log sLog = LogFactory.getLog(Group.class);

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Group() {}
	
	/**
	 * Constructor that accepts the name of the group
	 * 
	 * @param name
	 *            The name of the group.
	 */
	public Group(String groupName) {
		this.setName(groupName);
	}
	
	public Group(String groupName, String idCode) {
		this.setName(groupName);
		this.setIdCode(idCode);
	}

	public static Group fromGroupType(GroupType rt) {
		Group r = new Group();
		if (rt != null) {
			r.setName(rt.getName());
			r.setIdCode(rt.getIdCode());
		}
		return r;
	}
	
	public GroupType toGroupType() {
		GroupType g = new GroupType(getName(), getIdCode(), null);
		return g;
	}
	
	public PrivilegeType toPrivilegeType() {
		GroupType g = new GroupType(getName(), getIdCode(), null);
		PrivilegeType p = new PrivilegeType();
		p.setGroup(g);
		return p;
	}
	
	public void print(){
		sLog.info("Group: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getName()+"\n\tIdCode: "+this.getIdCode());
	}
	
	public Element toDOM() {
		return null;
	}

}
