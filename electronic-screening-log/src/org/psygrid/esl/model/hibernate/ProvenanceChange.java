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

import org.psygrid.esl.model.IProvenanceChange;
import org.psygrid.esl.model.IProvenanceLog;
import org.psygrid.esl.model.IPersistent;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_provenance_change"
 * 					proxy="org.psygrid.esl.model.hibernate.ProvenanceChange"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ProvenanceChange extends Persistent implements IProvenanceChange {

	/**
	 * The id of the user who is responsible for changing the state of an 
	 * object.
	 */
	private String user;

	/**
	 * The timestamp of when the state of an object was changed.
	 */
	private Date timestamp;

	private IProvenanceLog provenanceLog = new ProvenanceLog();


	/**
	 * @hibernate.property column="c_timestamp"
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Set the timestamp of when the state of the response was changed.
	 * 
	 * @param timestamp The timestamp.
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @hibernate.property column="c_user"
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the id of the user who was responsible for changing the 
	 * state of the response.
	 * 
	 * @param user The id of the user.
	 */
	public void setUser(String user) {
		this.user = user;
	}


	/**
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.ProvenanceLog"
	 *                        column="c_provenance_log_id"
	 *                        not-null="false"
	 *                        insert="false"
	 *                        update="false"
	 */
	public IProvenanceLog getProvenanceLog() {
		return provenanceLog;
	}

	public void setProvenanceLog(IProvenanceLog provenanceLog) {
		this.provenanceLog = provenanceLog;
	}

	public org.psygrid.esl.model.dto.ProvenanceChange toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.ProvenanceChange dtoC = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoC;
	}
	
	public org.psygrid.esl.model.dto.ProvenanceChange toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){

		//check for an already existing instance of a dto object for this 
		//group in the set of references
		org.psygrid.esl.model.dto.ProvenanceChange dtoP = null;
		if ( dtoRefs.containsKey(this)){
			dtoP = (org.psygrid.esl.model.dto.ProvenanceChange)dtoRefs.get(this);
		}
		if ( null == dtoP ){
			dtoP = new org.psygrid.esl.model.dto.ProvenanceChange();
			dtoRefs.put(this, dtoP);
			toDTO(dtoP, dtoRefs);
		}
		return dtoP;
	}

	public void toDTO(org.psygrid.esl.model.dto.ProvenanceChange dtoP, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoP, dtoRefs);

		dtoP.setTimestamp(timestamp);
		dtoP.setUser(user);

		if (provenanceLog != null) {
			dtoP.setProvenanceLog(((ProvenanceLog)provenanceLog).toDTO(dtoRefs));
		}

	}
}
