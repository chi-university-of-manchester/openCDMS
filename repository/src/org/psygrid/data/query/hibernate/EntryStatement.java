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

package org.psygrid.data.query.hibernate;

import java.util.Map;

import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_entry_statements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class EntryStatement extends Statement implements IEntryStatement {

	/**
	 * The operator 
	 */
	private QueryOperation operator;
	
	/**
	 * The entry
	 */
	private Entry entry;

	/**
	 * The document occurrence - not actually needed for querying but stored
	 * for convenience.
	 */
	private DocumentOccurrence docOcc;
	
	public QueryOperation getOperator() {
		return operator;
	}

	public void setOperator(QueryOperation operator) {
		this.operator = operator;
	}

	/**
	 * @hibernate.property column="c_operator"
	 */
	protected String getEnumOperator() {
        if ( null == operator ){
            return null;
        }
        else{
            return operator.toString();
        }
	}

	protected void setEnumOperator(String enumOperator) {
        if ( null == enumOperator ){
            operator = null;
        }
        else{
        	operator = QueryOperation.valueOf(enumOperator);
        }
	}

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Entry"
     *                        column="c_entry_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
	public Entry getEntry() {
		return entry;
	}

	public void setEntry(Entry entry) {
		this.entry = (Entry)entry;
	}
	
	/**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DocumentOccurrence"
     *                        column="c_doc_occ_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
	 */
	public DocumentOccurrence getDocOcc() {
		return docOcc;
	}

	public void setDocOcc(DocumentOccurrence docOcc) {
		this.docOcc = (DocumentOccurrence)docOcc;
	}

	public abstract org.psygrid.data.query.dto.EntryStatement toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

	public void toDTO(org.psygrid.data.query.dto.EntryStatement dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);
		if ( null != this.operator ){
			dtoS.setOperator(this.operator.toString());
		}
		dtoS.setDocOcc(this.docOcc.toDTO(dtoRefs, depth));
		dtoS.setEntry(this.entry.toDTO(dtoRefs, depth));
	}
	
	/**
	 * Retrieve the value type that is associated with this type of statement
	 * @return
	 */
	public abstract Class<?> getAssociatedValueType();
}
