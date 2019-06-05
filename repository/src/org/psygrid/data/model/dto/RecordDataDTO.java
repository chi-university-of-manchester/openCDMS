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


package org.psygrid.data.model.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rob Harper
 *
 */
public class RecordDataDTO extends ProvenanceableDTO {

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
    
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public Date getStudyEntryDate() {
		return studyEntryDate;
	}

	public void setStudyEntryDate(Date studyEntryDate) {
		this.studyEntryDate = studyEntryDate;
	}

	public org.psygrid.data.model.hibernate.RecordData toHibernate(){
		Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
		return toHibernate(hRefs);
	}
	
	@Override
	public org.psygrid.data.model.hibernate.RecordData toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //object in the map of references
        org.psygrid.data.model.hibernate.RecordData hRD = null;
        if ( hRefs.containsKey(this)){
        	hRD = (org.psygrid.data.model.hibernate.RecordData)hRefs.get(this);
        }
        if ( null == hRD ){
            //a dto instance of this object has not already
            //been created, so create it, and add it to the map 
            //of references
        	hRD = new org.psygrid.data.model.hibernate.RecordData();
            hRefs.put(this, hRD);
            toHibernate(hRD, hRefs);
        }

        return hRD;
    }

	public void toHibernate(org.psygrid.data.model.hibernate.RecordData hRD, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hRD, hRefs);
		hRD.setScheduleStartDate(this.scheduleStartDate);
		hRD.setStudyEntryDate(this.studyEntryDate);
		hRD.setNotes(this.notes);
	}

}
