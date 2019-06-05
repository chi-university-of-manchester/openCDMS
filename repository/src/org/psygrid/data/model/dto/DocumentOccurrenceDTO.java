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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.TimeUnits;

public class DocumentOccurrenceDTO extends ComponentDTO {

    /**
     * The label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the entry within its dataset.
     */
    protected String label;
    
    private DocumentDTO document;
    
    private DocumentGroupDTO documentGroup;
    
    private ReminderDTO[] reminders = new ReminderDTO[0];
    
    private Integer scheduleTime;
    
    private String scheduleUnits;
    
    /**
     * If True, and the parent Dataset has the randomization required
     * flag set to True, the completion of an instance of this document
     * occurrence is the trigger for performing randomization via the
     * Electronic Screening Log.
     */
    private boolean randomizationTrigger;
    
    private boolean locked;
    
    private Long primaryOccIndex;
    
    private Long secondaryOccIndex;
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

    public DocumentGroupDTO getDocumentGroup() {
        return documentGroup;
    }

    public void setDocumentGroup(DocumentGroupDTO documentGroup) {
        this.documentGroup = documentGroup;
    }

    public ReminderDTO[] getReminders() {
        return reminders;
    }

    public void setReminders(ReminderDTO[] reminders) {
        this.reminders = reminders;
    }

    public Integer getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Integer scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleUnits() {
        return scheduleUnits;
    }

    public void setScheduleUnits(String scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
    }

    public boolean isRandomizationTrigger() {
        return randomizationTrigger;
    }

    public void setRandomizationTrigger(boolean randomizationTrigger) {
        this.randomizationTrigger = randomizationTrigger;
    }

    public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Long getPrimaryOccIndex() {
		return primaryOccIndex;
	}

	public void setPrimaryOccIndex(Long primaryOccIndex) {
		this.primaryOccIndex = primaryOccIndex;
	}

	public Long getSecondaryOccIndex() {
		return secondaryOccIndex;
	}

	public void setSecondaryOccIndex(Long secondaryOccIndex) {
		this.secondaryOccIndex = secondaryOccIndex;
	}

	public org.psygrid.data.model.hibernate.DocumentOccurrence toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //occurrence in the map of references
        org.psygrid.data.model.hibernate.DocumentOccurrence hO = null;
        if ( hRefs.containsKey(this)){
            hO = (org.psygrid.data.model.hibernate.DocumentOccurrence)hRefs.get(this);
        }
        if ( null == hO ){
            //an instance of the unit has not already
            //been created, so create it, and add it to the map 
            //of references
            hO = new org.psygrid.data.model.hibernate.DocumentOccurrence();
            hRefs.put(this, hO);
            toHibernate(hO, hRefs);
        }

        return hO;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.DocumentOccurrence hO, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hO, hRefs);
        hO.setLabel(this.label);
        hO.setRandomizationTrigger(this.randomizationTrigger);
        hO.setLocked(this.locked);
        hO.setPrimaryOccIndex(this.primaryOccIndex);
        hO.setSecondaryOccIndex(this.secondaryOccIndex);
        if ( null != this.document ){
            hO.setDocument(this.document.toHibernate(hRefs));
        }
        
        if ( null != this.documentGroup ){
            hO.setDocumentGroup(this.documentGroup.toHibernate(hRefs));
        }
        
        hO.setScheduleTime(this.scheduleTime);
        if ( null != this.scheduleUnits ){
            hO.setScheduleUnits(TimeUnits.valueOf(this.scheduleUnits));
        }
        
        List<org.psygrid.data.model.hibernate.Reminder> hReminders = hO.getReminders();
        for ( int i=0; i<this.reminders.length; i++ ){
            ReminderDTO r = this.reminders[i];
            if ( null != r ){
                hReminders.add(r.toHibernate(hRefs));
            }
        }
    }
        
}
