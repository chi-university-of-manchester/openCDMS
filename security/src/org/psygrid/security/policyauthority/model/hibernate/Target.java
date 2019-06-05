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
import org.psygrid.security.policyauthority.model.ITarget;
import org.psygrid.www.xml.security.core.types.TargetType;
import org.w3c.dom.Element;

/**
 * @author jda
 * @hibernate.joined-subclass table="t_targets"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Target extends Persistent implements ITarget {
	private static Log sLog = LogFactory.getLog(Target.class);

	/**
	 * Target identity
	 */
	private String targetName;
	
	
	private String idCode;

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
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

	/**
	 * Default no-arg constructor, as required by the Hibernate framework for
	 * all persistable classes.
	 * 
	 */
	protected Target() {
	};

	/**
	 * Constructor that accepts the name of the target
	 * 
	 * @param targetName
	 *            The name of the target.
	 */
	public Target(String targetName, String code) {
		this.setTargetName(targetName);
		this.setIdCode(code);
	}

	/**
	 * Get the targetName
	 * 
	 * @return The targetName.
	 * @hibernate.property column = "c_target_name" lazy="false"
	 */
	public String getTargetName() {
		return targetName;

	}

	/**
	 * Set the targetName
	 * 
	 * @param targetName
	 *            The target name.
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Element toDOM() {
		return null;
	}

	public void print(){
		sLog.info("Target: "+toString()+"\n\tID: "+this.getId()+"\n\tVersion: "+getVersion()+"\n\tName: "+this.getTargetName()+"\n\tIdCode: "+this.getIdCode());
	}
	
	public static Target fromTargetType(TargetType rt) {
		Target t = new Target();
		if (rt != null) {
			t.setTargetName(rt.getName());
			t.setIdCode(rt.getIdCode());
		}
		return t;
	}
	
	public TargetType toTargetType() {
		TargetType r = new TargetType(getTargetName(), getIdCode());
		return r;
	}
	
	public boolean isEqual(Target t) {
		boolean result = false;
		if ((t.getTargetName() != null) && (getTargetName() != null)) {
			if ((getTargetName().equals(t.getTargetName()) 
					&& (!t.getTargetName().equals("")))) {
				result = true;
			}
		}
		if ((t.getIdCode() != null) && (getIdCode()!=null)) {
			if (getIdCode().equals(t.getIdCode())
					&& (!t.getIdCode().equals(""))) {
				result = true;
			}
		}
		return result;
	}
}
