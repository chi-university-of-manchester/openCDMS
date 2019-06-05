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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_grouplink"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class GroupLink extends Persistent {
	private static Log sLog = LogFactory.getLog(GroupLink.class);
	

	
	/**
	 *  Supported group in this attribute
	 */
	private Group group;
	
	/**
	 * List of group attributes with which the user is affiliated.
	 */
	private List<GroupAttributeLink> groupAttriubutes;
	

	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	
	protected GroupLink(){};
	
    /**
     * Constructor that accepts the attribute name and a list
     * of roles
     *  
     * @param project The project to which these privileges apply
     * @param group A list of group
     * @param roles A list of roles
     */
    public GroupLink(Group groups){
		this.setGroup(groups);     
	}
		

    
	public void print(){
		sLog.info("GroupLink: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: ");
		group.print();
	}

	/**
     * Get the group
     * 
     * @return A list containing the group.
     * @hibernate.many-to-one class="org.psygrid.security.attributeauthority.model.hibernate.Group"
     *                        column="c_group_id"
     *                        not-null="true"
     *                        cascade="none"
     */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group The group to set.
	 */
	protected void setGroup(Group groups) {
		this.group = groups;
	}

	/**
     * Get the groups
     * 
     * @return A list containing the group attributes.
     * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.GroupAttributeLink"
     * @hibernate.key column="c_grouplink_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
	public List<GroupAttributeLink> getGroupAttriubutes() {
		return groupAttriubutes;
	}

	public void setGroupAttriubutes(List<GroupAttributeLink> groupAttriubutes) {
		this.groupAttriubutes = groupAttriubutes;
	}
	
	public void addGroupAttribute(GroupAttribute groupAttribute){
		
		this.groupAttriubutes.add(new GroupAttributeLink(groupAttribute));
		
	}
}
