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
 * Records all changes made to an object over its lifetime.
 * 
 * @author Lucy Bridges
 *
 */
public class ProvenanceLog extends Persistent {
	
	private String createdBy;
	
	private Date created;
	
	/**
	 * List of all changes made during the history of an object
	 */
	private ProvenanceChange[] provenanceChange = new ProvenanceChange[0];

	
	/**
	 * A list of fields and values changed during this update
	 * 
	 * @return ProvenanceChange[]
	 */
	public ProvenanceChange[] getProvenanceChange() {
		return provenanceChange;
	}

	/**
	 * @param changes the changes to set
	 */
	public void setProvenanceChange(ProvenanceChange[] changes) {
		this.provenanceChange = changes;
	}
	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	/**
	 * @return Date
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Set the time the Auditable object was created 
	 * 
	 * @param created 
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	
	public org.psygrid.esl.model.hibernate.ProvenanceLog toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.ProvenanceLog hLog = toHibernate(dtoRefs);
		dtoRefs = null;
		return hLog;
	}
	
    public org.psygrid.esl.model.hibernate.ProvenanceLog toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
    	//check for an already existing instance of a hibernate object for this 
        //instance in the map of references
		org.psygrid.esl.model.hibernate.ProvenanceLog hLog = null;
        if ( hRefs.containsKey(this)){
            hLog = (org.psygrid.esl.model.hibernate.ProvenanceLog)hRefs.get(this);
        }
        if ( null == hLog ){
            //an instance of the class has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hLog = new org.psygrid.esl.model.hibernate.ProvenanceLog();
        	hRefs.put(this, hLog);
        	toHibernate(hLog, hRefs);
        }
		return hLog;
    }
    
    public void toHibernate(org.psygrid.esl.model.hibernate.ProvenanceLog hLog, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
        super.toHibernate(hLog, hRefs);

        hLog.setCreated(created);
        hLog.setCreatedBy(createdBy);
        
		if (provenanceChange != null) {
			for (int i=0; i<this.provenanceChange.length; i++){
				ProvenanceChange c = provenanceChange[i];
				if ( null != c ){
					hLog.addProvenanceChange(c.toHibernate(hRefs));
				}
			} 
		}
		
    }
}
