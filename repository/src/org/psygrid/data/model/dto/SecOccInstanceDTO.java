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

public class SecOccInstanceDTO extends ProvenanceableDTO {

    protected Long sectionOccurrenceId;
    
    protected boolean deleted;
    
    public Long getSectionOccurrenceId() {
        return sectionOccurrenceId;
    }

    public void setSectionOccurrenceId(Long sectionOccurrenceId) {
        this.sectionOccurrenceId = sectionOccurrenceId;
    }

    public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public org.psygrid.data.model.hibernate.SecOccInstance toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.SecOccInstance hSOI = null;
        if ( hRefs.containsKey(this)){
            hSOI = (org.psygrid.data.model.hibernate.SecOccInstance)hRefs.get(this);
        }
        else{
            //an instance of the section occurrence instance has not already
            //been created, so create it, and add it to the map 
            //of references
            hSOI = new org.psygrid.data.model.hibernate.SecOccInstance();
            hRefs.put(this, hSOI);
            toHibernate(hSOI, hRefs);
        }
        return hSOI;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.SecOccInstance hSOI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hSOI, hRefs);
        hSOI.setDeleted(deleted);
        if ( null != this.sectionOccurrenceId ){
            hSOI.setSectionOccurrenceId(this.sectionOccurrenceId);
        }
    }

}
