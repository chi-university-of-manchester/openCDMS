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
 * @hibernate.joined-subclass table="t_login_record"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class LoginRecord extends Persistent {
	private static Log sLog = LogFactory.getLog(LoginRecord.class);
	
	private Date timeStamp;
	
	private String ipAddress;
	
	private String credential;
	
	private boolean authenticated;
		
	/**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 * 
	 */
	protected LoginRecord(){};
	   
    /**
     * Constructor that accepts 
     */
    public LoginRecord(boolean auth, Date ts, String ip,
    		String cred){
    	this.authenticated = auth;
    	this.timeStamp = ts;
    	this.ipAddress = ip;
    	this.credential = cred;
    	
    }
		
	/**
     * Get the credential
     * 
     * @return The credential.
     * @hibernate.property column = "c_credential" type="text" length="3200"
     */
    public String getCredential(){
    		return this.credential;
    }

    /**
     * Set the credential
     * 
     * @param credential The credential.
     */
    public void setCredential(String credential){
    		this.credential = credential;
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
		return new String("Date: "+this.timeStamp.toString()+"\n\tIP: "+this.ipAddress+"\n\tVersion: "+getVersion()+"\n\tHostName: "+this.getCredential());
	}
     
	/**
	 * @return 
	 * @hibernate.property column = "c_ip_address"
	 */
	public String getIPAddress() {
		return this.ipAddress;
	}

	/**
	 * @param ipAddress The ipAddress to set.
	 */
	public void setIPAddress(String ip) {
		this.ipAddress = ip;
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
	
	/**
	 * @return Returns the authentication status.
	 * @hibernate.property column = "c_authenticated" not-null="true"
	 */
	public boolean getAuthenticated() {
		return this.authenticated;
	}

	/**
	 * @param tOrF The authentication status to set; true if the attempt was successfull.
	 */
	public void setAuthenticated(boolean tOrF) {
		this.authenticated = tOrF;
	}
	
}
