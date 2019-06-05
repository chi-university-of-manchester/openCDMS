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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentGroupDTO extends ComponentDTO {

    /**
     * The label for the group.
     * <p>
     * The label is intended to be used for displaying the number 
     * of the group within its dataset.
     */
    protected String label;
    
    /**
     * Record statuses that are permitted to fill in documents 
     * within this DocumentGroup
     */
    protected StatusDTO[] allowedRecordStatus = new StatusDTO[0];
    
    /**
     * DocumentGroups that are prerequisites of this one and so
     * should have been completed before starting documents in this
     * group
     */
    protected DocumentGroupDTO[] prerequisiteGroups = new DocumentGroupDTO[0]; 
    
    /**
     * The State a Record should be updated to once the Documents in 
     * this DocumentGroup have been completed
     */
    protected StatusDTO updateStatus;
    
    public DocumentGroupDTO() {
        super();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public StatusDTO[] getAllowedRecordStatus() {
		return allowedRecordStatus;
	}

	public void setAllowedRecordStatus(StatusDTO[] allowedRecordStatus) {
		this.allowedRecordStatus = allowedRecordStatus;
	}

	public DocumentGroupDTO[] getPrerequisiteGroups() {
		return prerequisiteGroups;
	}

	public void setPrerequisiteGroups(DocumentGroupDTO[] prerequisiteGroups) {
		this.prerequisiteGroups = prerequisiteGroups;
	}

	public StatusDTO getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(StatusDTO updateStatus) {
		this.updateStatus = updateStatus;
	}

	public org.psygrid.data.model.hibernate.DocumentGroup toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //document group in the map of references
        org.psygrid.data.model.hibernate.DocumentGroup hDG = null;
        if ( hRefs.containsKey(this)){
            hDG = (org.psygrid.data.model.hibernate.DocumentGroup)hRefs.get(this);
        }
        if ( null == hDG ){
            //an instance of the document group has not already
            //been created, so create it, and add it to the
            //map of references
            hDG = new org.psygrid.data.model.hibernate.DocumentGroup();
            hRefs.put(this, hDG);
            toHibernate(hDG, hRefs);
        }
        return hDG;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.DocumentGroup hDG, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hDG, hRefs);
        hDG.setLabel(this.label);
        
        if (null != updateStatus) {
        	hDG.setUpdateStatus(updateStatus.toHibernate(hRefs));
        }
        
        if (this.prerequisiteGroups != null) {
        	for (DocumentGroupDTO group: this.prerequisiteGroups) {
        		if (group != null) {
        			hDG.addPrerequisiteGroup(group.toHibernate(hRefs));
        		}
        	}
        }
        
        if (this.allowedRecordStatus != null) {
        	for (StatusDTO status: this.allowedRecordStatus) {
        		if (status != null) {
        			hDG.addAllowedRecordStatus(status.toHibernate(hRefs));
        		}
        	}
        }
    }
}
