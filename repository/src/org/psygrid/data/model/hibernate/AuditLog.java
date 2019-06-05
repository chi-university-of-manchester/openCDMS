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
package org.psygrid.data.model.hibernate;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * An audit log that is used to track all changes to elements in the repository.
 * 
 * @author pwhelan
 *
 * @hibernate.joined-subclass table="t_audit_log"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class AuditLog extends Persistent {
	
	/**
	 * List of all changes made during the history of an object
	 */
	private List<AuditableChange> auditableChanges = new ArrayList<AuditableChange>();
	
	 /**
     * Default no-arg constructor as required by Hibernate.
     */
    public AuditLog(){};
    
	
	/**
	 * Add an auditable change to the list of changes
	 *  
	 * @param change IAuditableChange
	 */
	public void addAuditableChange(AuditableChange change) {
		auditableChanges.add(change);
	}

	/**
	 * Setter for the auditable changes list 
	 * 
	 * @param auditableChanges list of auditable changes
	 * that should be set
	 */
	public void setAuditableChanges(List<AuditableChange> auditableChanges) {
		this.auditableChanges = auditableChanges;
	}
	
	/**
	 * A list of fields and values changed during this update
	 * 
	 * Retrieve the list of changes made to an Auditable object
	 * over its lifetime
	 * 
	 * @return the changes
	 * 
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.AuditableChange"
	 * @hibernate.key column="c_audit_log_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<AuditableChange> getAuditableChanges() {
		return auditableChanges;
	}

	@Override
	public org.psygrid.data.model.dto.AuditLogDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.data.model.dto.AuditLogDTO dtoLog = null;
		if ( dtoRefs.containsKey(this)){
			dtoLog = (org.psygrid.data.model.dto.AuditLogDTO)dtoRefs.get(this);
		}
		if ( null == dtoLog ){
			dtoLog = new org.psygrid.data.model.dto.AuditLogDTO();
			dtoRefs.put(this, dtoLog);
			toDTO(dtoLog, dtoRefs, depth);
		}
		return dtoLog;
	}

	public void toDTO(org.psygrid.data.model.dto.AuditLogDTO dtoLog, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoLog, dtoRefs, depth);
		
		if (this.auditableChanges != null) { 
			org.psygrid.data.model.dto.AuditableChangeDTO[] dtoChanges = new org.psygrid.data.model.dto.AuditableChangeDTO[this.auditableChanges.size()];
			for (int i=0; i<this.auditableChanges.size(); i++){
				AuditableChange c = auditableChanges.get(i);
				if (c != null) {
					dtoChanges[i] = ((AuditableChange)c).toDTO(dtoRefs, depth);
				}
			}        
			dtoLog.setAuditableChanges(dtoChanges);            
		}
	}
	
}
