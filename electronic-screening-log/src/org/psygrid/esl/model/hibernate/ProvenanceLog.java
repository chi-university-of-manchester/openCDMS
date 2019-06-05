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

package org.psygrid.esl.model.hibernate;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IProvenanceChange;
import org.psygrid.esl.model.IProvenanceLog;


/**
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_provenance_log"
 * 						proxy="org.psygrid.esl.model.hibernate.ProvenanceLog"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ProvenanceLog extends Persistent implements IProvenanceLog {

	private String createdBy;
	
	private Date created;
	
	/**
	 * List of all changes made during the history of an object
	 */
	private List<IProvenanceChange> provenanceChange = new ArrayList<IProvenanceChange>();
	
	
	/**
	 * A list of fields and values changed during this update
	 * 
	 * @return the changes
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.esl.model.hibernate.ProvenanceChange"
	 * @hibernate.key column="c_provenance_log_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<IProvenanceChange> getProvenanceChange() {
		return provenanceChange;
	}

	/**
	 * @param changes the changes to set
	 * 
	 * 
	 */
	protected void setProvenanceChange(List<IProvenanceChange> changes) {
		this.provenanceChange = changes;
	}

	public void addProvenanceChange(IProvenanceChange change) {
		provenanceChange.add(change);
	}
	
	/**
	 * @return the createdBy
	 * 
	 * @hibernate.property column="c_created_by"
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
	 * 
	 * @hibernate.property column="c_created"
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	public org.psygrid.esl.model.dto.ProvenanceLog toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.ProvenanceLog dtoLog = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoLog;
	}
	
	public org.psygrid.esl.model.dto.ProvenanceLog toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		//check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.esl.model.dto.ProvenanceLog dtoLog = null;
		if ( dtoRefs.containsKey(this)){
			dtoLog = (org.psygrid.esl.model.dto.ProvenanceLog)dtoRefs.get(this);
		}
		if ( null == dtoLog ){
			dtoLog = new org.psygrid.esl.model.dto.ProvenanceLog();
			dtoRefs.put(this, dtoLog);
			toDTO(dtoLog, dtoRefs);
		}
		return dtoLog;
	}

	public void toDTO(org.psygrid.esl.model.dto.ProvenanceLog dtoLog, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoLog, dtoRefs);

		if (this.provenanceChange != null) { 
			org.psygrid.esl.model.dto.ProvenanceChange[] dtoChanges = new org.psygrid.esl.model.dto.ProvenanceChange[this.provenanceChange.size()];
			for (int i=0; i<this.provenanceChange.size(); i++){
				IProvenanceChange c = provenanceChange.get(i);
				if (c != null) {
					dtoChanges[i] = ((ProvenanceChange)c).toDTO(dtoRefs);
				}
			}        
			dtoLog.setProvenanceChange(dtoChanges);            
		}
		
		dtoLog.setCreated(created);
		dtoLog.setCreatedBy(createdBy);

	}
	
}
