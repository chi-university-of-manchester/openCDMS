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


//Created on Nov 29, 2005 by John Ainsworth

package org.psygrid.security.policyauthority.model.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.www.xml.security.core.types.PrivilegeType;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_privileges"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Privilege extends Persistent {

	private static Log sLog = LogFactory.getLog(Privilege.class);


	/**
	 * identity
	 */
	private String name = null;

	private String idCode = null;

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Privilege() {
	}
	
	public Privilege(String name, String idCode){
		this.name = name;
		this.idCode = idCode;
	}

	public void print() {
		sLog.info("Privilege: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getName()+"\n\tIdCode: "+this.getIdCode());
	}

	/**
	 * Get the name
	 * 
	 * @return The name.
	 * @hibernate.property column = "c_name" lazy="false"
	 */
	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public boolean compare(Privilege p) {
		boolean result = false;
		if ((p.getName() != null) && (getName() != null)) {
			if ((getName().equals(p.getName()) && (!p.getName().equals("")))
					|| getName().equals(PGSecurityConstants.ANY)) {
				result = true;
			}
		}
		if ((p.getIdCode() != null) && (getIdCode()!=null)) {
			if ((getIdCode().equals(p.getIdCode())
					&& (!p.getIdCode().equals("")))
					|| getIdCode().equals(PGSecurityConstants.ANY)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * @return Returns the idCode.
	 * @hibernate.property column = "c_id_code"
	 */
	public String getIdCode() {
		return this.idCode;
	}

	/**
	 * @param idCode
	 *            The idCode to set.
	 */
	public void setIdCode(String codeNumber) {
		this.idCode = codeNumber;
	}
	public PrivilegeType toPrivilegeType(){
	    throw new RuntimeException("Subclass must override this method");
	}
	static public Privilege fromPrivilegeType(PrivilegeType pt){
		
		if(pt.getGroup()!=null && pt.getRole()!=null){
		    throw new RuntimeException("Privilege contains both role and group; only one can be set");				
		}
		if(pt.getGroup()!=null){
			return new Group(pt.getGroup().getName(), pt.getGroup().getIdCode());
		} else if (pt.getRole()!=null){
			return new Role(pt.getRole().getName(), pt.getRole().getIdCode());		
		}
	    throw new RuntimeException("Unknown PrivilegeType: "+pt.getClass());	
	}
	public boolean isEqual(Privilege p) {
		boolean result = false;
		if ((p.getName() != null) && (getName() != null)) {
			if ((getName().equals(p.getName()) && (!p.getName().equals("")))) {
				result = true;
			}
		}
		if ((p.getIdCode() != null) && (getIdCode() != null)) {
			if (getIdCode().equals(p.getIdCode())
					&& (!p.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}
}
