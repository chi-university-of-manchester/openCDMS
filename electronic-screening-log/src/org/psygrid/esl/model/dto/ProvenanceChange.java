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

package org.psygrid.esl.model.dto;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;


/**
 * Class records the changes made during one update instance of an Auditable object
 * 
 * @author Lucy Bridges
 *
 */
public class ProvenanceChange extends Persistent{


	
	/**
	 * The id of the user who is responsible for changing the state of an 
	 * object.
	 */
	private String user;
	
	/**
	 * The timestamp of when the state of an object was changed.
	 */
	private Date timestamp;

	/**
	 * The parent change log
	 */
	private ProvenanceLog provenanceLog = new ProvenanceLog();
	
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	
	public ProvenanceLog getProvenanceLog() {
		return provenanceLog;
	}

	public void setProvenanceLog(ProvenanceLog provenanceLog) {
		this.provenanceLog = provenanceLog;
	}
	

	
	public org.psygrid.esl.model.hibernate.ProvenanceChange toHibernate(){
		//create list to hold references to objects in the class'
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.ProvenanceChange hChange = toHibernate(dtoRefs);
		dtoRefs = null;
		return hChange;
	}
	
	public org.psygrid.esl.model.hibernate.ProvenanceChange toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
        //instance in the map of references
		org.psygrid.esl.model.hibernate.ProvenanceChange hC = null;
        if ( hRefs.containsKey(this)){
            hC = (org.psygrid.esl.model.hibernate.ProvenanceChange)hRefs.get(this);
        }
        if ( null == hC ){
            //an instance of the class has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hC = new org.psygrid.esl.model.hibernate.ProvenanceChange();
        	hRefs.put(this, hC);
        	toHibernate(hC, hRefs);
        }
		return hC;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.ProvenanceChange hP, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hP, hRefs);
		
		hP.setTimestamp(timestamp);
		hP.setUser(user);
		
		if (provenanceLog != null) {
			hP.setProvenanceLog(provenanceLog.toHibernate(hRefs));
		}

	}

}

