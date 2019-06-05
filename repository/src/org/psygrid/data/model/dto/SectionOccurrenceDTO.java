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

import java.util.Map;

import org.psygrid.data.model.hibernate.EntryStatus;

public class SectionOccurrenceDTO extends ComponentDTO {

    /**
     * The label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the section within its document.
     */
    protected String label;
    
    /**
     * The section that the section occurrence is associated with.
     */
    protected SectionDTO section;
    
    /**
     * Flag to mark whether entries in the section are optional or not.
     */
    protected String entryStatus;
    
    /**
     * Flag to mark whether multiple instances of this occurrence
     * may be created at runtime.
     */
    protected boolean multipleAllowed;
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SectionDTO getSection() {
        return section;
    }

    public void setSection(SectionDTO section) {
        this.section = section;
    }

    public String getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(String entryStatus) {
        this.entryStatus = entryStatus;
    }

    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    public void setMultipleAllowed(boolean multipleAllowed) {
        this.multipleAllowed = multipleAllowed;
    }

    public org.psygrid.data.model.hibernate.SectionOccurrence toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.SectionOccurrence hSO = null;
        if ( hRefs.containsKey(this)){
            hSO = (org.psygrid.data.model.hibernate.SectionOccurrence)hRefs.get(this);
        }
        else{
            //an instance of the section occurrence has not already
            //been created, so create it, and add it to the map 
            //of references
            hSO = new org.psygrid.data.model.hibernate.SectionOccurrence();
            hRefs.put(this, hSO);
            toHibernate(hSO, hRefs);
        }
        return hSO;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.SectionOccurrence hSO, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hSO, hRefs);
        hSO.setLabel(this.label);
        hSO.setMultipleAllowed(this.multipleAllowed);
        if ( null != this.section ){
            hSO.setSection(this.section.toHibernate(hRefs));
        }
        if ( null != this.entryStatus ){
            hSO.setEntryStatus(EntryStatus.valueOf(this.entryStatus));
        }
    }
}
