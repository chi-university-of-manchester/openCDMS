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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jda
 *
 * @hibernate.joined-subclass table="t_password_record"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class PasswordRecord extends Persistent {
	private static Log sLog = LogFactory.getLog(PasswordRecord.class);
	
	private Date timeStamp;
	
	private String password;
	
		
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	protected PasswordRecord(){};
	   
    /**
     * Constructor that accepts 
     */
    public PasswordRecord(Date ts, String p){
    	this.timeStamp = ts;
    	this.password = p;
    	
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
		return new String("Date: "+this.timeStamp.toString()+"\n\tPassword: "+this.password+"\n\tVersion: "+getVersion());
	}
     
	/**
	 * @return 
	 * @hibernate.property column = "c_password"
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String p) {
		this.password = p;
	}
	
	/**
	 * @return Returns the timeStamp.
	 * @hibernate.property column = "c_time_stamp"
	 */
	public Date getTimeStamp() {
		return this.timeStamp;
	}

	/**
	 * @param ts The timeStamp to set.
	 */
	public void setTimeStamp(Date ts) {
		this.timeStamp = ts;
	}
	
}
