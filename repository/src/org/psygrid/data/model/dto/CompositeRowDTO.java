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
import java.util.Set;

public class CompositeRowDTO extends ElementInstanceDTO {

    private BasicResponseDTO[] basicResponses = new BasicResponseDTO[0];

    private CompositeResponseDTO compositeResponse = null;
    
    public BasicResponseDTO[] getBasicResponses() {
        return basicResponses;
    }

    public void setBasicResponses(BasicResponseDTO[] basicResponses) {
        this.basicResponses = basicResponses;
    }

    public CompositeResponseDTO getCompositeResponse() {
        return compositeResponse;
    }

    public void setCompositeResponse(CompositeResponseDTO compositeResponse) {
        this.compositeResponse = compositeResponse;
    }

    public org.psygrid.data.model.hibernate.CompositeRow toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.CompositeRow hR = null;
        if ( hRefs.containsKey(this) ){
            hR = (org.psygrid.data.model.hibernate.CompositeRow)hRefs.get(this);
        }
        else{
            hR = new org.psygrid.data.model.hibernate.CompositeRow();
            hRefs.put(this, hR);
            toHibernate(hR, hRefs);
        }
        return hR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.CompositeRow hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        Set<org.psygrid.data.model.hibernate.BasicResponse> hResponses = hR.getBasicResponses();
        for ( int i=0; i<this.basicResponses.length; i++ ){
            BasicResponseDTO resp = this.basicResponses[i];
            if ( null != resp ){
                hResponses.add(resp.toHibernate(hRefs));
            }
        }
        
        if ( null != this.compositeResponse ){
            hR.setCompositeResponse(this.compositeResponse.toHibernate(hRefs));
        }
    }
    
}
