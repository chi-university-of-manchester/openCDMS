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


/**
 * Class to represent the dependency of an entry or an occurrence
 * of a section on the selection of a specific option.
 * 
 * @author Rob Harper
 *
 */
public class OptionDependentDTO extends PersistentDTO {

    /**
     * The dependent entry.
     */
    private EntryDTO myDependentEntry;
    
    /**
     * The dependent section occurrence.
     */
    private SectionOccurrenceDTO myDependentSecOcc;
    
    /**
     * The modified status for the dependent entry.
     */
    private String entryStatus;
    
    public OptionDependentDTO(){};
    
    public EntryDTO getMyDependentEntry() {
        return myDependentEntry;
    }

    /**
     * Set the dependent entry.
     * 
     * @param dependentEntry The dependent entry.
     */
    public void setMyDependentEntry(EntryDTO dependentEntry){
        this.myDependentEntry = dependentEntry;
    }
    
    public SectionOccurrenceDTO getMyDependentSecOcc() {
        return myDependentSecOcc;
    }

    public void setMyDependentSecOcc(SectionOccurrenceDTO dependentSecOcc) {
        this.myDependentSecOcc = dependentSecOcc;
    }

    public String getEntryStatus() {
        return this.entryStatus;
    }

    public void setEntryStatus(String entryStatus) {
        this.entryStatus = entryStatus;
    }

    public org.psygrid.data.model.hibernate.OptionDependent toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.OptionDependent hOD = new org.psygrid.data.model.hibernate.OptionDependent();
        toHibernate(hOD, hRefs);
        return hOD;
    }

    public void toHibernate(org.psygrid.data.model.hibernate.OptionDependent hOD, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hOD, hRefs);
        if ( null != this.entryStatus ){
            hOD.setEntryStatus(EntryStatus.valueOf(this.entryStatus));
        }
        if ( null != this.myDependentEntry ){
            hOD.setMyDependentEntry(this.myDependentEntry.toHibernate(hRefs));
        }
        if ( null != this.myDependentSecOcc ){
            hOD.setMyDependentSecOcc(this.myDependentSecOcc.toHibernate(hRefs));
        }
    }

}
