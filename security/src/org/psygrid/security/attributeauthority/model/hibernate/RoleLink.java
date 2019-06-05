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

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_rolelink"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RoleLink extends Persistent {
	private static Log sLog = LogFactory.getLog(RoleLink.class);
	
	/**
	 *  Supported role in this attribute
	 */
	private Role role;
	
	protected RoleLink(){};
	
    /**
     * Constructor that accepts the attribute name and a list
     * of role
     *  
     * @param project The project to which these privileges apply
     * @param groups A list of groups
     * @param role A list of role
     */
    public RoleLink(Role roles){
		this.setRole(roles); 
	}
    
	/**
     * Get the role
     * 
     * @return A list containing the role.
     * @hibernate.many-to-one class="org.psygrid.security.attributeauthority.model.hibernate.Role"
     *                        column="c_role_id"
     *                        not-null="true"
     *                        cascade="none"
     *                        
     */
    public Role getRole(){
    		return role;
    }

	/**
     * Set role
     * 
     * @param role A list containing the role.
     */
    protected void setRole(Role r){
    		this.role = r;
    }
    
	public void print(){
		sLog.info("Attribute: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: ");
		role.print();	
	}
}
