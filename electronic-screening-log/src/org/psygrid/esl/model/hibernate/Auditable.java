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

import java.util.Map;
import java.util.HashMap;

//import org.psygrid.esl.model.IProvenance;
//import org.psygrid.esl.model.hibernate.Provenance;
import org.psygrid.esl.model.IAuditable;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.IProvenanceLog;


/**
 * Stores the log containing a history of all changes made to the Auditable object
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_auditable"
 * 							  proxy="org.psygrid.esl.model.hibernate.Auditable"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Auditable extends Provenanceable implements IAuditable {


	/**
	 * The list of changes made to fields during this update instance
	 */
	protected IProvenanceLog changes = new ProvenanceLog();


	public Auditable() {
	}

	/**
	 *  @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.ProvenanceLog"
	 *                        column="c_provenance_log_id"
	 *                        not-null="false"
	 *                        unique="true"
	 *                        cascade="all"
	 */
	public IProvenanceLog getLog() {
		return changes;
	}

	public void setLog(IProvenanceLog changes) {
		this.changes = changes;
	}    


	public org.psygrid.esl.model.dto.Auditable toDTO() {
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Auditable dtoElem = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoElem;
	}
	public org.psygrid.esl.model.dto.Auditable toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		org.psygrid.esl.model.dto.Auditable dtoE = null;
		if ( dtoRefs.containsKey(this)){
			dtoE = (org.psygrid.esl.model.dto.Auditable)dtoRefs.get(this);
		}
		if ( dtoE == null ){
			dtoE = new org.psygrid.esl.model.dto.Auditable();
			dtoRefs.put(this, dtoE);
			toDTO(dtoE, dtoRefs);
		}
		return dtoE;
	}

	public void toDTO(org.psygrid.esl.model.dto.Auditable dtoE, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoE, dtoRefs);
				
		if (changes !=  null) {
			dtoE.setLog(((ProvenanceLog)changes).toDTO(dtoRefs));
		}
	}

}
