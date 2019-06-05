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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A response to a composite entry.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_comp_responses"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CompositeResponse extends Response {

	private List<CompositeRow> compositeRows = new ArrayList<CompositeRow>();

	private List<CompositeRow> deletedRows = new ArrayList<CompositeRow>();

	/**
	 * Get the collection of CompositeRow objects that are children 
	 * of the CompositeResponse.
	 * 
	 * @return The collection of child CompositeRow objects
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.CompositeRow"
	 * @hibernate.key column="c_comp_resp_id" 
	 *                not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<CompositeRow> getCompositeRows() {
		return compositeRows;
	}

	public void setCompositeRows(List<CompositeRow> compositeRows) {
		this.compositeRows = compositeRows;
	}

    /**
     * Retrieve the number of composite rows associated with
     * this composite response.
     * 
     * @return The number of composite rows.
     */
	public int numCompositeRows(){
		return compositeRows.size();
	}

    /**
     * Create a single composite row and add it to the composite
     * response's collection of composite rows.
     * 
     * @return The new composite row.
     */
	public CompositeRow createCompositeRow(){
		return createCompositeRow(null);
	}

    /**
     * Create a single composite row and add it to the composite
     * response's collection of composite rows.
     * 
     * @param reason Reason why row is being added, for storage 
     * in provenance.
     * @return The new composite row.
     */
	public CompositeRow createCompositeRow(String reason){
		CompositeRow row = new CompositeRow();
		row.record = this.record;
		row.setCompositeResponse(this);
		this.compositeRows.add(row);
		Provenance p = new Provenance(null, row);
		p.setComment(reason);
		this.provItems.add(p);
		propertyChangeSupport.firePropertyChange(null, null, null);
		return row;
	}

    /**
     * Retrieve a single existing composite row from the composite
     * responses collection of rows.
     * 
     * @param index The index of the composite row to retrieve.
     * @return The composite row at the specified index.
     * @throws ModelException if no composite row exists for
     * the specified index.
     */
	public CompositeRow getCompositeRow(int index) throws ModelException {
		try{
			return this.compositeRows.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No composite row exists for the the specified index of "+index);
		}
	}

    /**
     * Remove a single existing composite row from the composite
     * responses collection of rows.
     * 
     * @param index The index of the composite row to remove.
     * @throws ModelException if no composite row exists for
     * the specified index.
     */
	public void removeCompositeRow(int index) throws ModelException {
		removeCompositeRow(index, null);
	}

    /**
     * Remove a single existing composite row from the composite
     * responses colelction of rows.
     * 
     * @param index The index of the composite row to remove.
     * @param reason Reason why row is being removed, for storage 
     * in provenance.
     * @throws ModelException if no composite row exists for
     * the specified index.
     */
	public void removeCompositeRow(int index, String reason) throws ModelException {
		try{
			CompositeRow row = this.compositeRows.remove(index);
			this.deletedRows.add(row);
			Provenance p = new Provenance(row, null);
			p.setComment(reason);
			this.provItems.add(p);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No composite row exists for the the specified index of "+index);
		}
	}

	/**
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.CompositeRow"
	 * @hibernate.key column="c_del_cr_id" 
	 *                not-null="false"
	 * @hibernate.list-index column="c_del_index"
	 */
	public List<CompositeRow> getDeletedRows() {
		return deletedRows;
	}

	public void setDeletedRows(List<CompositeRow> deletedRows) {
		this.deletedRows = deletedRows;
	}

	public org.psygrid.data.model.dto.CompositeResponseDTO toDTO() {
		return toDTO(RetrieveDepth.DS_SUMMARY);
	}

	public org.psygrid.data.model.dto.CompositeResponseDTO toDTO(RetrieveDepth depth){
		//create list to hold references to objects in the responses's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its DTO equivalent once and once only
		Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		org.psygrid.data.model.dto.CompositeResponseDTO dtoR = toDTO(dtoRefs, depth);
		dtoRefs = null;
		return dtoR;
	}

	public org.psygrid.data.model.dto.CompositeResponseDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		org.psygrid.data.model.dto.CompositeResponseDTO dtoR = null;
		if ( dtoRefs.containsKey(this) ){
			dtoR = (org.psygrid.data.model.dto.CompositeResponseDTO)dtoRefs.get(this);
		}
		else{
			dtoR = new org.psygrid.data.model.dto.CompositeResponseDTO();
			dtoRefs.put(this, dtoR);
			toDTO(dtoR, dtoRefs, depth);
		}
		return dtoR;
	}

	public void toDTO(org.psygrid.data.model.dto.CompositeResponseDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoR, dtoRefs, depth);

		if ( RetrieveDepth.RS_SUMMARY != depth){
			org.psygrid.data.model.dto.CompositeRowDTO[] dtoRows = 
				new org.psygrid.data.model.dto.CompositeRowDTO[this.compositeRows.size()];
			Iterator it = this.compositeRows.iterator();
			int counter = 0;
			while ( it.hasNext() ){
				CompositeRow row = (CompositeRow)it.next();
				dtoRows[counter] = row.toDTO(dtoRefs, depth);
				counter++;
			}
			dtoR.setCompositeRows(dtoRows);

			org.psygrid.data.model.dto.CompositeRowDTO[] dtoDelRows = 
				new org.psygrid.data.model.dto.CompositeRowDTO[this.deletedRows.size()];
			it = this.deletedRows.iterator();
			counter = 0;
			while ( it.hasNext() ){
				CompositeRow row = (CompositeRow)it.next();
				dtoDelRows[counter] = row.toDTO(dtoRefs, depth);
				counter++;
			}
			dtoR.setDeletedRows(dtoDelRows);

		}
	}

	@Override
	public void attach(Entry ent) {
		super.attach(ent);

		for (CompositeRow row: compositeRows){
			row.attach(ent);
		}

		for (CompositeRow row: deletedRows){
			row.attach(ent);
		}

	}

	public void detach(){
		super.detach();

		for (CompositeRow row: compositeRows){
			row.detach();
		}

		for (CompositeRow row: deletedRows){
			row.detach();
		}        

	}

	@Override
	protected void addChildTasks(Record r) {
		for (CompositeRow row: compositeRows){
			row.setRecord(r);
			row.addChildTasks(r);
		}
		for (CompositeRow row: deletedRows){
			row.setRecord(r);
			row.addChildTasks(r);
		}
	}

	@Override
	public void recordCurrentState() {
		for ( CompositeRow row: compositeRows ){
			row.recordCurrentState();
		}
	}

	@Override
	public void checkForChanges(ChangeHistory change) {
		for ( CompositeRow row: compositeRows ){
			row.checkForChanges(change);
		}
		//there may be provenance that we need to link to this
		//change in the change history...
		for ( Provenance p: provItems ){
			if ( null == p.getParentChange() && null == p.getId() ){
				p.setParentChange(change);
			}
		}
	}

}
