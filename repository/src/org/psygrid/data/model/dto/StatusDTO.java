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

import org.psygrid.data.model.hibernate.GenericState;

public class StatusDTO extends ProvenanceableDTO {

    /**
     * The numeric code of the status.
     */
    private int code;
    
    /**
     * The short name of the status.
     */
    private String shortName;
    
    /**
     * The long name of the status.
     */
    private String longName;
    
    /**
     * The collection of allowed status transitions.
     */
    private StatusDTO[] statusTransitions = new StatusDTO[0];
    
    /**
     * Boolean flag to indicate whether the status implies that the object
     * that it is applied to is inactive (True) or not.
     * <p>
     * Originally added to cater for Record statuses such as Withdrawn and
     * Deceased, which both imply that the Record is "inactive".
     */
    private boolean inactive;
    
    private String genericState;
    
    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String name) {
        this.shortName = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public StatusDTO[] getStatusTransitions() {
        return statusTransitions;
    }

    public void setStatusTransitions(StatusDTO[] statusTransitions) {
        this.statusTransitions = statusTransitions;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public String getGenericState() {
		return genericState;
	}

	public void setGenericState(String genericState) {
		this.genericState = genericState;
	}
    
    public org.psygrid.data.model.hibernate.Status toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object  
        //for this status in the map of references
        org.psygrid.data.model.hibernate.Status hS = null;
        if ( hRefs.containsKey(this)){
            hS = (org.psygrid.data.model.hibernate.Status)hRefs.get(this);
        }
        if ( null == hS ){
            //an instance of the status has not already
            //been created, so create it, and add it to the
            //map of references
            hS = new org.psygrid.data.model.hibernate.Status();
            hRefs.put(this, hS);
            toHibernate(hS, hRefs);
        }
        
        return hS;
    }

    public void toHibernate(org.psygrid.data.model.hibernate.Status hS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hS, hRefs);
        hS.setCode(this.code);
        hS.setShortName(this.shortName);
        hS.setLongName(this.longName);
        hS.setInactive(this.inactive);
        List<org.psygrid.data.model.hibernate.Status> hTransitions = hS.getStatusTransitions();
        for (int i=0; i<this.statusTransitions.length; i++){
            StatusDTO s = statusTransitions[i];
            hTransitions.add(s.toHibernate(hRefs));
        }
        
        if (null != genericState) {
        	hS.setGenericState(GenericState.valueOf(genericState));
        }
    }

}
