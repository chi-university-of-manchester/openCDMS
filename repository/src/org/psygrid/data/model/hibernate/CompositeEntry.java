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

import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;

/**
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_comp_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CompositeEntry extends Entry {

    /**
     * List of strings that act as labels for the rows in the 
     * composite entry.
     * <p>
     * If the list has non-zero size then it also acts to set the
     * number of rows in the rendered composite. If the list has 
     * zero size then assume that the composite starts with one row
     * and can grow to hold any number of rows.
     */
    private List<String> rowLabels = new ArrayList<String>();
    
    /**
     * Collection of entries that are contained by the composite
     * entry.
     */
    protected List<BasicEntry> entries = new ArrayList<BasicEntry>();

    public CompositeEntry() {
    }

    public CompositeEntry(String name, EntryStatus entryStatus) {
        super(name, entryStatus);
    }

    public CompositeEntry(String name, String displayText, EntryStatus entryStatus) {
        super(name, displayText, entryStatus);
    }

    public CompositeEntry(String name, String displayText) {
        super(name, displayText);
    }

    public CompositeEntry(String name) {
        super(name);
    }

    /**
     * Get the list of strings that act as labels for the rows in the 
     * composite entry.
     * <p>
     * If the list has non-zero size then it also acts to set the
     * number of rows in the rendered composite. If the list has 
     * zero size then assume that the composite starts with one row
     * and can grow to hold any number of rows.
     * 
     * @return The list of row labels.
     * 
     * @hibernate.list table="t_row_labels"
     *                 cascade="all" batch-size="100"
     * @hibernate.key column="c_comp_id"
     * @hibernate.list-index column="c_index"
     * @hibernate.element type="string"
     *                    column="c_row_label"
     *                    not-null="true"
     */
    public List<String> getRowLabels() {
        return rowLabels;
    }

    /**
     * Set the list of strings that act as labels for the rows in the 
     * composite entry.
     * <p>
     * If the list has non-zero size then it also acts to set the
     * number of rows in the rendered composite. If the list has 
     * zero size then assume that the composite starts with one row
     * and can grow to hold any number of rows.
     * 
     * @param rowLabels The list of row labels.
     */
    public void setRowLabels(List<String> rowLabels) {
        this.rowLabels = rowLabels;
    }

    /**
     * Retrieve the number of items in the list of row labels.
     * 
     * @return The number of row labels.
     */
    public int numRowLabels(){
        return rowLabels.size();
    }
    
    /**
     * Add a new row label to the list of row labels.
     * 
     * @param rowLabel The new row label to add.
     * @throws ModelException if the row label to add is <code>null</code>.
     */
    public void addRowLabel(String rowLabel) throws ModelException{
        if ( null == rowLabel ){
            throw new ModelException("Cannot add a null row label");
        }
        rowLabels.add(rowLabel);
    }
    
    /**
     * Retrieve a single row label from the list of row labels, from
     * the specified index in the list of row labels.
     * 
     * @param index The index to retrieve the row label from.
     * @return The row label.
     * @throws ModelException if no row label exists for the given index.
     */
    public String getRowLabel(int index) throws ModelException{
        try{
            return rowLabels.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No row label exists for index "+index, ex);
        }
    }
    
    /**
     * Get the collection of entries that are contained by the composite
     * entry.
     * 
     * @return The collection of entries.
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.BasicEntry"
     * @hibernate.key column="c_comp_ent_id" not-null="false"
     * @hibernate.list-index column="c_index"
     */
    public List<BasicEntry> getEntries() {
        return entries;
    }

    /**
     * Set the collection of entries that are contained by the composite
     * entry.
     * 
     * @param entries The collection of entries.
     */
    public void setEntries(List<BasicEntry> entries) {
        this.entries = entries;
    }

    /**
     * Method used to add a basic entry to the collection of basic 
     * entries associated with the composite entry.
     * 
     * @param entry The basic entry to add to the collection of entries.
     * 
     * @throws ModelException if the basic entry in the argument is <code>null</code>.
     */
    public void addEntry(BasicEntry entry) throws ModelException {
        if ( null == entry ){
            throw new ModelException("Cannot add a null basic entry");
        }
        BasicEntry e = (BasicEntry)entry;
        e.setSection(this.section);
        e.setMyDataSet(myDataSet);
        entries.add(e);
    }

    /**
     * Retrieve a single basic entry at the specified index in
     * the composite entry's collection of entries.
     * 
     * @param index The index in the collection of entries.
     * @return The basic entry at the given index.
     * @throws ModelException if there is no basic entry for the given index.
     */
    public BasicEntry getEntry(int index) throws ModelException {
        try{
            return entries.get(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No basic entry found for index "+index, ex);
        }
    }

    /**
     * Method used to insert a basic entry into the collection of entries
     * associated with the composite entry, at the specified index.
     * 
     * @param entry The basic entry to insert into the collection of entries.
     * @param index The index in the collection of entries to insert the
     * basic entry at.
     * @throws ModelException if the specified index is not valid; 
     * if the basic entry in the argument is <code>null</code>.
     */    
    public void insertEntry(BasicEntry entry, int index) throws ModelException {
        if ( null == entry ){
            throw new ModelException("Cannot insert a null basic entry");
        }
        BasicEntry e = (BasicEntry)entry;
        e.setMyDataSet(myDataSet);
        try{
            entries.add(index,e);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("Cannot insert basic entry at index "+index+" - invalid index", ex);            
        }
    }

    /**
     * Move a single basic entry to a new index in the composite entry's 
     * collection of entries.
     * 
     * @param currentPosition The current index of the basic entry to move.
     * @param newPosition The new index to move the basic entry to.
     * @throws ModelException if there is no basic entry for the given 
     * current index, or if the new index to move it to is not valid.
     */
    public void moveEntry(int currentIndex, int newIndex) throws ModelException {
        BasicEntry e = null;
        try{
            e = entries.remove(currentIndex);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No basic entry found for index "+currentIndex, ex);
        }
        try{
            entries.add(newIndex, e);
        }
        catch(IndexOutOfBoundsException ex){
            //roll back - re-insert child element to its old position
            entries.add(currentIndex, e);
            throw new ModelException("Cannot move basic entry to index "+newIndex+" - invalid index", ex);
        }
    }

    /**
     * Return the number of basic entries belonging to the composite
     * entry.
     * 
     * @return The number of entries.
     */
    public int numEntries() {
        return this.entries.size();
    }

    /**
     * Remove a single basic entry at the specified index from
     * the composite entry's collection of entries.
     * 
     * @param index The index in the collection of entries.
     * @throws ModelException if there is no basic entry for the given
     * index.
     */
    public void removeEntry(int index) throws ModelException {
        try{
            Persistent p = entries.remove(index);
            if ( null != p.getId() ){
                //the object being removed has previously been persisted
                //store it in the collection of deleted objects so that 
                //it may be manually deleted when the dataset is next saved
                getDataSet().getDeletedObjects().add(p);
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No basic entry found for index "+index, ex);
        }
    }

    @Override
    protected void addChildTasks(DataSet ds) {
        for ( BasicEntry e: entries ){
            e.setMyDataSet(ds);
        }
    }
    
    protected CompositeResponse generateInstance(){
        return new CompositeResponse();
    }
    
    /**
     * Generate a new instance of the composite entry - a composite response -
     * for the given section occurrence.
     * 
     * @return The new composite response.
     * @throws ModelException if the response cannot be created.
     */
    public CompositeResponse generateInstance(SectionOccurrence occurrence) throws ModelException {
        checkSectionOccurrence(occurrence);
        CompositeResponse cr = new CompositeResponse();
        cr.setEntry(this);
        cr.setSectionOccurrence((SectionOccurrence)occurrence);
        cr.setStatus(ResponseStatus.NORMAL);
        return cr;
    }

    /**
     * Generate a new response to the composite entry - a composite response -
     * for the given section occurrence instance.
     * <p>
     * Note that for an entry for which one or more statuses are defined
     * the status of the generated response will be set by default to the 
     * status at index 0 in the list, thus ensuring that the response 
     * has a status for the its entire lifetime.
     * 
     * @param secOccInst The section occurrence instance.
     * @return The new composite response.
     * @throws ModelException if the response cannot be created.
     */
    public CompositeResponse generateInstance(SecOccInstance secOccInst) throws ModelException {
        checkSecOccInstance(secOccInst);
        CompositeResponse cr = new CompositeResponse();
        cr.setEntry(this);
        cr.setSecOccInstance((SecOccInstance)secOccInst);
        cr.setStatus(ResponseStatus.NORMAL);
        return cr;
    }

    public void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode) {
    	if ( !entryStatus.equals(EntryStatus.DISABLED) ){
	    	CompositeResponse cr = null;
	    	if ( null != secOccInst ){
	    		cr = (CompositeResponse)docInst.getResponse(this, secOccInst);
	    		if ( null == cr ){
	    			cr = generateInstance(secOccInst);
	    			docInst.addResponse(cr);
	    		}
	    	}
	    	else {
	    		cr = (CompositeResponse)docInst.getResponse(this, secOcc);
	    		if ( null == cr ){
	    			cr = generateInstance(secOcc);
	    			docInst.addResponse(cr);
	    		}
	    	}
	    	
			if ( 0 == numRowLabels() ){
				if ( 0 == cr.numCompositeRows() ){
					CompositeRow row = cr.createCompositeRow();
					for ( int i=0, c=numEntries(); i<c; i++ ){
						BasicEntry be = getEntry(i);
						BasicResponse br = null;
						if ( null != secOccInst ){
							br = be.generateInstance(secOccInst);
						}
						else {
							br = be.generateInstance(secOcc);
						}
						row.addResponse(br);
						IValue v = be.generateValue();
						if ( !be.isDisableStandardCodes() ){
							v.setStandardCode(stdCode);
						}
						br.setValue(v);
					}
				}
				else{
					for ( int i=0, c=cr.numCompositeRows(); i<c; i++ ){
						if ( 0 == i){
							CompositeRow row = cr.getCompositeRow(i);
							for ( int j=0, d=numEntries(); j<d; j++ ){
								BasicEntry be = getEntry(j);
								BasicResponse br = row.getResponse(be);
								if ( null == br ){
									if ( null != secOccInst ){
										br = be.generateInstance(secOccInst);
									}
									else {
										br = be.generateInstance(secOcc);
									}
									row.addResponse(br);
								}
								IValue v = be.generateValue();
								if ( !be.isDisableStandardCodes() ){
									v.setStandardCode(stdCode);
								}
								br.setValue(v);
							}
						}
						else{
							cr.removeCompositeRow(i);
						}
					}
				}
			}
			else{
				if ( 0 == cr.numCompositeRows() ){
					for ( int i=0, c=numRowLabels(); i<c; i++ ){
						CompositeRow row = cr.createCompositeRow();
						for ( int j=0, d=numEntries(); j<d; j++ ){
							BasicEntry be = getEntry(j);
							BasicResponse br = null;
							if ( null != secOccInst ){
								br = be.generateInstance(secOccInst);
							}
							else {
								br = be.generateInstance(secOcc);
							}
							row.addResponse(br);
							IValue v = be.generateValue();
							if ( 0 == j ){
								//assume zeroth entry in a composite with row labels is a text entry
								ITextValue tv = (ITextValue)v;
								tv.setValue(getRowLabel(i));
								br.setValue(tv);
							}
							else{
								if ( !be.isDisableStandardCodes() ){
									v.setStandardCode(stdCode);
								}
								br.setValue(v);
							}
						}
					}
				}
				else{
					for ( int i=0, c=cr.numCompositeRows(); i<c; i++ ){
						CompositeRow row = cr.getCompositeRow(i);
						//note ignoring zeroth column as this is the row label
						for ( int j=1, d=numEntries(); j<d; j++ ){
							BasicEntry be = getEntry(j);
							BasicResponse br = row.getResponse(be);
							if ( null == br ){
								if ( null != secOccInst ){
									br = be.generateInstance(secOccInst);
								}
								else {
									br = be.generateInstance(secOcc);
								}
								row.addResponse(br);
							}
							IValue v = be.generateValue();
							if ( !be.isDisableStandardCodes() ){
								v.setStandardCode(stdCode);
							}
							br.setValue(v);
						}
					}
				}
			}
    	}
	}

	public org.psygrid.data.model.dto.CompositeEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //composite entry in the map of references
        org.psygrid.data.model.dto.CompositeEntryDTO dtoCE = null;
        if ( dtoRefs.containsKey(this)){
            dtoCE = (org.psygrid.data.model.dto.CompositeEntryDTO)dtoRefs.get(this);
        }
        else{
            //an instance of the composite entry has not already
            //been created, so create it, and add it to the
            //map of references
            dtoCE = new org.psygrid.data.model.dto.CompositeEntryDTO();
            dtoRefs.put(this, dtoCE);
            toDTO(dtoCE, dtoRefs, depth);
        }
        
        return dtoCE;
    }

    public void toDTO(org.psygrid.data.model.dto.CompositeEntryDTO dtoCE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoCE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            org.psygrid.data.model.dto.BasicEntryDTO[] dtoEntries = new org.psygrid.data.model.dto.BasicEntryDTO[entries.size()];
            for ( int i=0; i<this.entries.size(); i++ ){
                BasicEntry be = this.entries.get(i);
                if ( null != be ){
                    dtoEntries[i] = be.toDTO(dtoRefs, depth);
                }
            }
            dtoCE.setEntries(dtoEntries);
            
            String[] dtoRowLabels = new String[this.rowLabels.size()];
            for ( int i=0; i<this.rowLabels.size(); i++ ){
                String label = this.rowLabels.get(i);
                if ( null != label ){
                    dtoRowLabels[i] = label;
                }
            }
            dtoCE.setRowLabels(dtoRowLabels);
        }
    }

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.CompositeEntryDTO();
	}

	public List<QueryOperation> getQueryOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isQueryable() {
		return false;
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		// TODO: Currently, there is no CompositeStatement that can be created.
		return null;
	}
     
}
