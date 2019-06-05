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
import org.psygrid.security.attributeauthority.model.IGroup;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.TargetType;
import org.w3c.dom.Element;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_groups"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Group extends Persistent implements IGroup {
	
	
	private static final long serialVersionUID = 1L;

	private static Log sLog = LogFactory.getLog(Group.class);
	
	/**
	 *  Group Name
	 */
	private String groupName;
	
	private List<GroupAttribute> groupAttributes;
	
	
	/**
	 * Code Number
	 */
	private String idCode;
		
		
	/**
	 *  Parent Project - this should be set to the projectCode NOT the projectName.
	 */
	private String parentName;
	
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	public Group(){};
	
	/**
	 * Constructor that accepts the name of the group
	 *  
	 * @param groupName The name of the group.
	 */
	public Group(String groupName){
		this.setGroupName(groupName);
	}
	
    		
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_group_name"
     */
    public String getGroupName(){
    		return this.groupName;
    }

    /**
     * Set the groupName
     * 
     * @param groupName The group name.
     */
    public void setGroupName(String groupName){
    		this.groupName = groupName;
    }
    
    /* (non-Javadoc)
     * @see org.psygrid.security.attributeauthority.model.IGroup#toDOM()
     */
    public Element toDOM(){
    		return null;
    }
    
    /**
     * @param gt
     * @return
     */
    public static Group fromGroupType(GroupType gt) {
		Group g = new Group();
		if (gt != null) {
			g.setGroupName(gt.getName());
			g.setIdCode(gt.getIdCode());
			g.setParentName(gt.getParent());
		}
		return g;
	}
    
	/**
	 * 
	 */
	public void print(){
		sLog.info(toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return new String("Group: "+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getGroupName());
	}
     
	/**
	 * @return the projectCode of the owning project.
	 * 
	 * @hibernate.property column = "c_parent_name"
	 */
	public String getParentName() {
		return this.parentName;
	}

	/**
	 * This should be set the the projectCode of the owning project NOT the projectName.
	 * 
	 * @param parentName The parentName to set.
	 */
	public void setParentName(String parent) {
		this.parentName = parent;
	}

	/**
	 * @return The new GroupType
	 */
	public GroupType toGroupType() {
		GroupType r = new GroupType(getGroupName(), this.getIdCode(), this.getParentName());
		return r;
	}

	/**
	 * @return The new TargetType
	 */
	public TargetType toTargetType() {
		TargetType r = new TargetType(getGroupName(), this.getIdCode());
		return r;
	}
	
	/**
	 * @return Returns the idCode.
	 * @hibernate.property column = "c_id_code"
	 */
	public String getIdCode() {
		return this.idCode;
	}

	/**
	 * @param idCode The idCode to set.
	 */
	public void setIdCode(String codeNumber) {
		this.idCode = codeNumber;
	}
	
	public boolean identical(Group g){
		boolean result = false;
		if ((g.getGroupName() != null) && (getGroupName() != null)) {
			if ((getGroupName().equals(g.getGroupName()) 
					&& (!g.getGroupName().equals("")))) {
				result = true;
			}
		}
		if ((g.getIdCode() != null) && (getIdCode()!=null)) {
			if (getIdCode().equals(g.getIdCode())
					&& (!g.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}

	
	/**
	 * Get the attributes
	 * 
	 * @return A list containing the attributes.
	 * @hibernate.list cascade="all-delete-orphan" lazy="false" batch-size="100"
	 * @hibernate.one-to-many class="org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute"
	 * @hibernate.key column="c_group_id" not-null="true"
	 * @hibernate.list-index column="c_ga_index"
	 */
	public List<GroupAttribute> getGroupAttributes() {
		return groupAttributes;
	}

	public void setGroupAttributes(List<GroupAttribute> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}
	
	public void addGroupAttribute(GroupAttribute gA){
		//TODO:WRV - add checking here to make sure it's not a duplicate.
		groupAttributes.add(gA);
	}
}
