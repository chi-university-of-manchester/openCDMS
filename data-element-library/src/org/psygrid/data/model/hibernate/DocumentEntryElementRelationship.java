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

import java.util.Map;

/**
 * 
 * @author williamvance
 * This class captures the relationship between a document's entry and that
 * entry's relationship with a section. This relationship is unhierarchical, thus requiring
 * a separate class extension in order to handle it. 
 * 
 * @hibernate.joined-subclass table="t_document_entry_element_relationship"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DocumentEntryElementRelationship extends ElementRelationship {

	private int entrySectionIndex;
	private EntryStatus entryStatus;
	private String entryLabel;
	
	public DocumentEntryElementRelationship(){
		
	}

	public DocumentEntryElementRelationship(String lsid, String elementClass, String repopulateMethod, int index, int sectionIndex, EntryStatus entryStatus, String entryLabel) throws IllegalArgumentException {
		super(lsid, elementClass, repopulateMethod, index);
		this.entrySectionIndex = sectionIndex;
		this.entryStatus = entryStatus;
		this.entryLabel = entryLabel;
	}
	
	public DocumentEntryElementRelationship(DataElementContainer relatedElement, String elementClass, String repopulateMethod, int index, int sectionIndex, EntryStatus entryStatus, String entryLabel) throws IllegalArgumentException {
		super(relatedElement, elementClass, repopulateMethod, index);
		this.entrySectionIndex = sectionIndex;
		this.entryStatus = entryStatus;
		this.entryLabel = entryLabel;
	}

	/**
	 * @hibernate.property column="c_section_index"
	 */
	public int getEntrySectionIndex() {
		return entrySectionIndex;
	}

	protected void setEntrySectionIndex(int entrySectionIndex) {
		this.entrySectionIndex = entrySectionIndex;
	}
	
	@Override
	public void reconstituteNativeRelationship(DataElementContainer baseElement, DataElementContainer relatedElement) {
		org.psygrid.data.model.hibernate.Document doc = (org.psygrid.data.model.hibernate.Document) baseElement.getElement();
		org.psygrid.data.model.hibernate.Entry 	entry = (org.psygrid.data.model.hibernate.Entry) relatedElement.getElement();
		doc.addEntry(entry);
		
		Section section = doc.getSection(getEntrySectionIndex());
		entry.setSection(section);
		entry.setEnumEntryStatus(getEntryStatusEnum());
		entry.setLabel(getEntryLabel());
	}

	@Override
	public void toDTO(org.psygrid.data.model.dto.ElementRelationshipDTO elementRelationship, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(elementRelationship, dtoRefs, depth);
		org.psygrid.data.model.dto.DocumentEntryElementRelationship docEntryElementRelationship = (org.psygrid.data.model.dto.DocumentEntryElementRelationship)elementRelationship;
		docEntryElementRelationship.setEntrySectionIndex(getEntrySectionIndex());
		docEntryElementRelationship.setEntryStatus(getEntryStatusEnum());
		docEntryElementRelationship.setEntryLabel(getEntryLabel());
	}

	@Override
	public org.psygrid.data.model.dto.PersistentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.DocumentEntryElementRelationship elemRelationship = null;
		//check to see if we're already in the map.
       if ( dtoRefs.containsKey(this)){
           elemRelationship = (org.psygrid.data.model.dto.DocumentEntryElementRelationship)dtoRefs.get(this);
       }
       if( null == elemRelationship){
    	   elemRelationship = new org.psygrid.data.model.dto.DocumentEntryElementRelationship();
    	   dtoRefs.put(this, elemRelationship);
    	   toDTO(elemRelationship, dtoRefs, depth);
       }
		 
		return elemRelationship;
	}

	public EntryStatus getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(EntryStatus entryStatus) {
		this.entryStatus = entryStatus;
	}	
	
	public void setEntryStatusEnum(String entryStatus){
		if(entryStatus == null)
			this.entryStatus = null;
		else{
			this.entryStatus = EntryStatus.valueOf(entryStatus);
		}
	}
	
	/**
     * Get the string version of the entry status for this relationship.
     * 
     * @return the string version of the status. NOTE: it is possible for this method to return null.
     * 
     * @hibernate.property column = "c_entry_status"
     */
	public String getEntryStatusEnum(){
		if(this.entryStatus == null)
			return null;
		else
			return entryStatus.toString();
	}

	/**
	 * 
	 * @return the entry label string
	 * 
	 * @hibernate.property not-null="false" column = "c_entry_label"
	 */
	public String getEntryLabel() {
		return entryLabel;
	}

	public void setEntryLabel(String entryLabel) {
		this.entryLabel = entryLabel;
	}

}
