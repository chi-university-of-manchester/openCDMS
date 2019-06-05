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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_record_data"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RecordData extends Provenanceable {

    /**
     * Date to use as the zero-point for scheduling.
     */
    private Date scheduleStartDate;
    
    /**
     * Date of entry into the study - used for UKCRN reports.
     */
    private Date studyEntryDate;
    
    /**
     * General text notes on the record.
     */
    private String notes;
    
    /**
	 * Get the general text notes on the record.
	 * 
	 * @return The text notes.
     * @hibernate.property column="c_notes" type="text" length="4096"
     */
    public String getNotes() {
		return notes;
	}

    /**
     * Set the general text notes on the record.
	 * 
     * @param notes The text notes.
     */
	public void setNotes(String notes) {
		this.notes = notes;
	}

    /**
	 * Get the date to use as the zero-point for scheduling.
	 * 
	 * @return The date to use as the zero-point for scheduling.
     * @hibernate.property column="c_sch_st_date"
     */
	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	/**
	 * Set the date to use as the zero-point for scheduling.
	 * 
	 * @param scheduleStartDate The date to use as the zero-point for scheduling.
	 */
	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

    /**
	 * Get the date of entry into the study - used for UKCRN reports.
	 * 
	 * @return The date of entry into the study.
     * @hibernate.property column="c_stud_ent_date"
     */
	public Date getStudyEntryDate() {
		return studyEntryDate;
	}

	/**
	 * Set the date of entry into the study - used for UKCRN reports.
	 * 
	 * @param studyEntryDate The date of entry into the study
	 */
	public void setStudyEntryDate(Date studyEntryDate) {
		this.studyEntryDate = studyEntryDate;
	}
	
	public org.psygrid.data.model.dto.RecordDataDTO toDTO(){
		Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		return toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
	}

	@Override
	public org.psygrid.data.model.dto.RecordDataDTO toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //record data object in the map of references
        org.psygrid.data.model.dto.RecordDataDTO dtoRD = null;
        if ( dtoRefs.containsKey(this)){
        	dtoRD = (org.psygrid.data.model.dto.RecordDataDTO)dtoRefs.get(this);
        }
        else {
            //an instance of the record data object has not already
            //been created, so create it, and add it to the map 
            //of references
        	dtoRD = new org.psygrid.data.model.dto.RecordDataDTO();
            dtoRefs.put(this, dtoRD);
            toDTO(dtoRD, dtoRefs, depth);
        }
        
        return dtoRD;
	}


	public void toDTO(org.psygrid.data.model.dto.RecordDataDTO dtoRD, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoRD, dtoRefs, depth);
		dtoRD.setScheduleStartDate(this.scheduleStartDate);
		dtoRD.setStudyEntryDate(this.studyEntryDate);
		dtoRD.setNotes(this.notes);
	}

	public RecordData copy(){
		RecordData rd = new RecordData();
		rd.setScheduleStartDate(this.scheduleStartDate);
		rd.setStudyEntryDate(this.studyEntryDate);
		rd.setNotes(this.notes);
		return rd;
	}
	
}
