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
import java.util.List;
import java.util.Map;

public abstract class StatusedInstanceDTO extends ElementInstanceDTO {

    protected Long statusId;
    
    protected Date edited;
    
    protected ChangeHistoryDTO[] history = new ChangeHistoryDTO[0];
    
    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

	public Date getEdited() {
		return edited;
	}

	public void setEdited(Date edited) {
		this.edited = edited;
	}

	public ChangeHistoryDTO[] getHistory() {
		return history;
	}

	public void setHistory(ChangeHistoryDTO[] history) {
		this.history = history;
	}

	public abstract org.psygrid.data.model.hibernate.StatusedInstance toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.StatusedInstance hSI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hSI, hRefs);
        
        if ( null != this.statusId ){
            hSI.setStatusId(this.statusId);
        }
        
        hSI.setEdited(this.edited);
        
        List<org.psygrid.data.model.hibernate.ChangeHistory> hCH = 
            hSI.getHistory();
        for (int i=0; i<this.history.length; i++){
            ChangeHistoryDTO ch = history[i];
            if ( null != ch ){
                hCH.add(ch.toHibernate(hRefs));
            }
        }    

    }
    
}
