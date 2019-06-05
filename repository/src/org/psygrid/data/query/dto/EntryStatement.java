/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query.dto;

import java.util.Map;

import org.psygrid.data.model.dto.DocumentOccurrenceDTO;
import org.psygrid.data.model.dto.EntryDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.query.QueryOperation;

/**
 * @author Rob Harper
 *
 */
public abstract class EntryStatement extends Statement {

	/**
	 * The operator 
	 */
	private String operator;
	
	/**
	 * The entry
	 */
	private EntryDTO entry;

	private DocumentOccurrenceDTO docOcc;
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public EntryDTO getEntry() {
		return entry;
	}

	public void setEntry(EntryDTO entry) {
		this.entry = entry;
	}

	public DocumentOccurrenceDTO getDocOcc() {
		return docOcc;
	}

	public void setDocOcc(DocumentOccurrenceDTO docOcc) {
		this.docOcc = docOcc;
	}

	@Override
    public abstract org.psygrid.data.query.hibernate.EntryStatement toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

    public void toHibernate(org.psygrid.data.query.hibernate.EntryStatement hES, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hES, hRefs);
    	if ( null != this.operator ){
    		hES.setOperator(QueryOperation.valueOf(this.operator));
    	}
    	hES.setEntry(this.entry.toHibernate(hRefs));
    	hES.setDocOcc(this.docOcc.toHibernate(hRefs));	 
    }

}
