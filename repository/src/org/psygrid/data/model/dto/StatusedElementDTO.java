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

public abstract class StatusedElementDTO extends ElementDTO {

    /**
     * Collection of statuses that instances of the element
     * may have.
     */
    protected StatusDTO[] statuses = new StatusDTO[0];
    
    public StatusDTO[] getStatuses() {
        return statuses;
    }

    public void setStatuses(StatusDTO[] statuses) {
        this.statuses = statuses;
    }

    public abstract org.psygrid.data.model.hibernate.StatusedElement toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.StatusedElement hE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hE, hRefs);
        
        List<org.psygrid.data.model.hibernate.Status> hStatuses = hE.getStatuses();
        for ( int i=0; i<this.statuses.length; i++ ){
            StatusDTO s = this.statuses[i];
            if ( null != s ){
                hStatuses.add(s.toHibernate(hRefs));
            }
        }
        
    }
     
}
