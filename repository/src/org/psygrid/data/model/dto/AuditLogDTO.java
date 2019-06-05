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
package org.psygrid.data.model.dto;

import java.util.Map;

import org.psygrid.data.model.hibernate.AuditableChange;

/**
 * Audit log keeps track of all changes to the element
 * 
 * @author pwhelan
 */
public class AuditLogDTO extends PersistentDTO {

    /**
	 * Default no-arg constructor, as required by the Hibernate framework
	 * for all persistable classes.
	 */
	public AuditLogDTO(){};
	
	/**
	 * List of all changes made during the history of an object
	 */
	private AuditableChangeDTO[] auditableChanges = new AuditableChangeDTO[0];
	
	/**
	 * A list of fields and values changed during this update
	 * 
	 * @return ProvenanceChange[]
	 */
	public AuditableChangeDTO[] getAuditableChanges() {
		return auditableChanges;
	}
	
	/**
	 * @param changes the changes to set
	 */
	public void setAuditableChanges(AuditableChangeDTO[] changes) {
		this.auditableChanges = changes;
	}
	
    public org.psygrid.data.model.hibernate.AuditLog toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	//check for an already existing instance of a hibernate object for this 
        //instance in the map of references
		org.psygrid.data.model.hibernate.AuditLog hLog = null;
        if ( hRefs.containsKey(this)){
            hLog = (org.psygrid.data.model.hibernate.AuditLog)hRefs.get(this);
        }
        if ( null == hLog ){
            //an instance of the class has not already
            //been created, so create it, and add it to 
            //the map of references	
        	hLog = new org.psygrid.data.model.hibernate.AuditLog();
        	hRefs.put(this, hLog);
        	toHibernate(hLog, hRefs);
        }
		return hLog;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.AuditLog hLog, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hLog, hRefs);

		if (auditableChanges != null) {
			for (int i=0; i<this.auditableChanges.length; i++){
				AuditableChangeDTO c = auditableChanges[i];
				if ( null != c ) {
					hLog.addAuditableChange((AuditableChange)c.toHibernate(hRefs));
				}
			} 
		}
		
    }

	
	
	
}
